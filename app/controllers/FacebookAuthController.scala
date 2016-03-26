package controllers

import javax.inject.Inject

import jp.t2v.lab.play2.auth.social.providers.facebook.{FacebookController, FacebookProviderUserSupport, FacebookUser}
import model.user.Role.User
import model.user.{Account, FacebookDAO, FacebookUser, UserDAO}
import play.api.Logger
import play.api.i18n.MessagesApi
import play.api.libs.ws.{WS, WSResponse}
import play.api.mvc.{RequestHeader, Result}
import play.api.Play.current

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

class FacebookAuthController @Inject()(val userDAO: UserDAO, val facebookDAO: FacebookDAO, val messagesApi: MessagesApi)
  extends FacebookController
  with AuthConfigImpl
  with FacebookProviderUserSupport {

  // native realization throw json exception
  private def readProviderUser(accessToken: String, response: WSResponse): ProviderUser = {
    val j = response.json
    jp.t2v.lab.play2.auth.social.providers.facebook.FacebookUser(
      (j \ "id").as[String],
      (j \ "name").as[String],
      (j \ "email").as[String],
      (j \ "picture" \ "data" \ "url").as[String],
      accessToken
    )
  }

  override def retrieveProviderUser(accessToken: AccessToken)(implicit ctx: ExecutionContext): Future[jp.t2v.lab.play2.auth.social.providers.facebook.FacebookUser] = {
    for {
      response <- WS.url("https://graph.facebook.com/me")
        .withQueryString("access_token" -> accessToken, "fields" -> "name,first_name,last_name,picture.type(large),email")
        .get()
    } yield {
      Logger(getClass).debug("Retrieving user info from provider API: " + response.body)
      readProviderUser(accessToken, response)
    }
  }

  override def onOAuthLinkSucceeded(token: AccessToken, consumerUser: User)(implicit request: RequestHeader, ctx: ExecutionContext): Future[Result] = {
    retrieveProviderUser(token).map { providerUser =>
      facebookDAO.save(consumerUser.id.get,
        new model.user.FacebookUser(0L, providerUser.id, providerUser.name, providerUser.coverUrl, providerUser.accessToken))
      Redirect(routes.Tasks.allTasks())
    }
  }

  override def onOAuthLoginSucceeded(token: AccessToken)(implicit request: RequestHeader, ctx: ExecutionContext): Future[Result] = {
    retrieveProviderUser(token).flatMap { providerUser =>

      facebookDAO.findById(providerUser.id).map {
        case None =>
          val id = Await.result(userDAO.createAccount(new Account(None, providerUser.email, "empty", User)), Duration.Inf)
          facebookDAO save(id,
            new model.user.FacebookUser(0L, providerUser.id, providerUser.name, providerUser.coverUrl, providerUser.accessToken))

          Await.result(gotoLoginSucceeded(id), Duration.Inf)
        case Some(fu) =>
          Await.result(gotoLoginSucceeded(fu.userId), Duration.Inf)
      }
    }
  }

}
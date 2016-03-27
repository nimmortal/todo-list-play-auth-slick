package controllers.auth.login

import javax.inject.Inject

import config.AuthConfiguration
import controllers.routes
import jp.t2v.lab.play2.auth.social.providers.facebook.{FacebookController, FacebookProviderUserSupport, FacebookUser}
import model.auth.LoginService
import play.api.Logger
import play.api.Play.current
import play.api.i18n.MessagesApi
import play.api.libs.ws.{WS, WSResponse}
import play.api.mvc.{RequestHeader, Result}
import model.user.dao.{FacebookDAO, UserDAO}

import scala.concurrent.{ExecutionContext, Future}

class FacebookAuthController @Inject()(val userDAO: UserDAO, val facebookDAO: FacebookDAO, loginService: LoginService, val messagesApi: MessagesApi)
  extends FacebookController
  with AuthConfiguration
  with FacebookProviderUserSupport {

  // native realization throw json exception
  private def readProviderUser(accessToken: String, response: WSResponse): ProviderUser = {
    val j = response.json
    FacebookUser(
      (j \ "id").as[String],
      (j \ "name").as[String],
      (j \ "email").as[String],
      (j \ "picture" \ "data" \ "url").as[String],
      accessToken
    )
  }

  override def retrieveProviderUser(accessToken: AccessToken)(implicit ctx: ExecutionContext): Future[FacebookUser] = {
    for {
      response <- WS.url("https://graph.facebook.com/me")
        .withQueryString("access_token" -> accessToken, "fields" -> "name,first_name,last_name,picture.type(large),email")
        .get()
    } yield {
      Logger(getClass).debug("Retrieving model.user info from provider API: " + response.body)
      readProviderUser(accessToken, response)
    }
  }

  override def onOAuthLinkSucceeded(token: AccessToken, consumerUser: User)(implicit request: RequestHeader, ctx: ExecutionContext): Future[Result] = {
    retrieveProviderUser(token).map { providerUser =>
//      facebookDAO.save(consumerUser.id,
//        new model.user.FacebookUser(0L, providerUser.id, providerUser.name, providerUser.coverUrl, providerUser.accessToken))
      Redirect(controllers.routes.Tasks.allTasks())
    }
  }

  override def onOAuthLoginSucceeded(token: AccessToken)(implicit request: RequestHeader, ctx: ExecutionContext): Future[Result] = {
    retrieveProviderUser(token).flatMap { providerUser =>
      val fu = new model.user.FacebookUser(0L, providerUser.id, providerUser.name, providerUser.email, providerUser.coverUrl, providerUser.accessToken)

      loginService.authenticateFacebook(fu).flatMap(id => gotoLoginSucceeded(id))
    }
  }

}
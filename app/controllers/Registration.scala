package controllers

import javax.inject.Inject

import play.api.i18n.{I18nSupport, MessagesApi}
import jp.t2v.lab.play2.auth.{AuthenticationElement, Login}
import model.user.Role.User
import model.user.{Account, UserDAO}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, Controller}
import views.html

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

case class LoginForm(login: String, email: String, password: String)

class Registration @Inject()(val userDAO: UserDAO, val messagesApi: MessagesApi)
  extends Controller with AuthenticationElement with Login with AuthConfigImpl with I18nSupport {

  val registrationForm = Form {
    mapping(
      "username" -> text.verifying("username.empty", s => !s.isEmpty),
      "email" -> email.verifying("email.empty", s => !s.isEmpty),
      "password" -> text.verifying("password.empty", s => !s.isEmpty)
    )(LoginForm.apply)(LoginForm.unapply)
  }

  def goToRegistration = Action { implicit request =>
    Ok(html.registration(registrationForm))
  }

  def registration = Action.async { implicit request =>
    registrationForm.bindFromRequest().fold(
      hasErrors => Future.successful(BadRequest(html.registration(hasErrors))),
      success =>  {

          userDAO.findUser(success.email).map { maybeUser =>
            if (maybeUser.isDefined) {
              BadRequest(html.registration(registrationForm.withGlobalError("error.exist")))
            } else {
              val acc = Account(None, success.email, success.password, User)
              val id = Await.result(userDAO.createAccount(acc), Duration.Inf)
              Await.result(gotoLoginSucceeded(id), Duration.Inf)

            }
          }

      }
    )
  }

}

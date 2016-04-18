package controllers.auth

import javax.inject._

import config.AuthConfiguration
import jp.t2v.lab.play2.auth.{AuthenticationElement, Login}
import model.auth.RegistrationService
import model.user.dao.UserDAO
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class RegForm(login: String, email: String, password: String, passwordCheck: String)

@Singleton
class RegistrationController @Inject()(val userDAO: UserDAO, val messagesApi: MessagesApi, registrationService: RegistrationService)
  extends Controller with AuthenticationElement with Login with AuthConfiguration with I18nSupport {

  val registrationForm = Form {
    mapping(
      "username" -> text.verifying("username.empty", s => !s.isEmpty),
      "email" -> email.verifying("email.empty", s => !s.isEmpty),
      "password" -> text.verifying("password.empty", s => !s.isEmpty),
      "password_confirm" -> text.verifying("password.empty", s => !s.isEmpty)
    )(RegForm.apply)(RegForm.unapply) verifying("password.not.match", f => f.password == f.passwordCheck)
  }

  def goToRegistration = Action { implicit request =>
    Ok(views.html.auth.registration(registrationForm))
  }

  def registration = Action.async { implicit request =>
    registrationForm.bindFromRequest().fold(
      hasErrors => Future.successful(BadRequest(views.html.auth.registration(hasErrors))),
      success => {
        Logger.debug("Registration form assembly")

        val userId = registrationService.registration(success.email, success.login, success.password)

        userId.flatMap {
          id => gotoLoginSucceeded(id)
        } recover {
          case _ => BadRequest(views.html.auth.registration(registrationForm.withGlobalError("error.exist")))
        }

      }
    )
  }

}

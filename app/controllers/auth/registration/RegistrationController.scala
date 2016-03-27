package controllers.auth.registration

import javax.inject.Inject

import config.AuthConfiguration
import jp.t2v.lab.play2.auth.{AuthenticationElement, Login}
import model.auth.RegistrationService
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}
import model.user.RegisteredUserDTO
import model.user.dao.UserDAO

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RegistrationController @Inject()(val userDAO: UserDAO, val messagesApi: MessagesApi, registrationService: RegistrationService)
  extends Controller with AuthenticationElement with Login with AuthConfiguration with I18nSupport {

  val registrationForm = Form {
    mapping(
      "username" -> text.verifying("username.empty", s => !s.isEmpty),
      "email" -> email.verifying("email.empty", s => !s.isEmpty),
      "password" -> text.verifying("password.empty", s => !s.isEmpty)
    )(LoginForm.apply)(LoginForm.unapply)
  }

  def goToRegistration = Action { implicit request =>
    Ok(views.html.auth.registration(registrationForm))
  }

  def registration = Action.async { implicit request =>
    registrationForm.bindFromRequest().fold(
      hasErrors => Future.successful(BadRequest(views.html.auth.registration(hasErrors))),
      success => {
        val userId = registrationService.registration(new RegisteredUserDTO(0L, success.email, success.login, success.password))

        userId.flatMap {
          id => gotoLoginSucceeded(id)
        } recover {
          case _ => BadRequest(views.html.auth.registration(registrationForm.withGlobalError("error.exist")))
        }

      }
    )
  }

}

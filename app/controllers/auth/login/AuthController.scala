package controllers.auth.login

import javax.inject.Inject

import config.AuthConfiguration
import jp.t2v.lab.play2.auth.{LoginLogout, OptionalAuthElement}
import model.auth.LoginService
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{Action, Controller}
import model.user.dao.UserDAO
import views.html

import scala.concurrent.Future

case class AuthData(login: String, password: String)

class AuthController @Inject()(userService: LoginService, val userDAO: UserDAO, val messagesApi: MessagesApi)
  extends Controller with OptionalAuthElement with LoginLogout with AuthConfiguration with I18nSupport {

  val index = Action { implicit request =>
    Redirect(controllers.routes.Tasks.allTasks())
  }

  val loginForm = Form {
    mapping(
      "login" -> email.verifying("email.empty", s => !s.isEmpty),
      "password" -> text.verifying("password.empty", s => !s.isEmpty)
    )(AuthData.apply)(AuthData.unapply)
  }

  def login = StackAction { implicit request =>
    if (loggedIn.isDefined)
      Redirect(controllers.routes.Tasks.allTasks())
    else
      Ok(html.auth.login(loginForm))
  }

  def logout = Action.async { implicit request =>
    gotoLogoutSucceeded.map(_.flashing(
      "success" -> "You've been logged out"
    ).removingFromSession("rememberme"))
  }

  def authenticate = Action.async { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(html.auth.login(formWithErrors))),
      form           =>  {
        userService.authenticate(form.login, form.password).flatMap { user =>
          user.map { u =>
            gotoLoginSucceeded(u.id)
          } getOrElse {
            Future.successful(BadRequest(html.auth.login(loginForm.withGlobalError("error.login"))))
          }
        }
      }
    )
  }

}
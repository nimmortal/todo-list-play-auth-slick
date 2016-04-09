package controllers.auth.login

import javax.inject._

import config.AuthConfiguration
import jp.t2v.lab.play2.auth.{LoginLogout, OptionalAuthElement}
import model.auth.LoginService
import model.user.UserService
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{Action, Controller}
import model.user.dao.UserDAO
import views.html

import scala.concurrent.Future

case class AuthForm(login: String, password: String)

@Singleton
class AuthController @Inject()(loginService: LoginService, userService: UserService, val userDAO: UserDAO, val messagesApi: MessagesApi)
  extends Controller with OptionalAuthElement with LoginLogout with AuthConfiguration with I18nSupport {


  val index = Action { implicit request =>
    Redirect(controllers.routes.Tasks.getTaskPage())
  }

  val loginForm = Form {
    mapping(
      "login" -> text.verifying("empty.login", s => !s.isEmpty),
      "password" -> text.verifying("empty.password", s => !s.isEmpty)
    )(AuthForm.apply)(AuthForm.unapply)
  }

  def login = StackAction { implicit request =>
    loggedIn.fold {
      Ok(html.auth.login(loginForm))
    } { _ =>
      Redirect(controllers.routes.Tasks.getTaskPage())
    }
  }

  def logout = AsyncStack { implicit request =>
    loggedIn.foreach(u => userService.removeUserView(u.id))

    gotoLogoutSucceeded.map(_.flashing(
      "success" -> "You've been logged out"
    ).removingFromSession("rememberme"))
  }

  def authenticate = Action.async { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(html.auth.login(formWithErrors))),
      form           =>  {
        loginService.authenticate(form.login, form.password).flatMap { user =>
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
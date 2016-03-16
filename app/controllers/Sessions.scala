package controllers

import javax.inject.Inject

import jp.t2v.lab.play2.auth.{OptionalAuthElement, LoginLogout}
import model.user.UserDAO
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{Action, Controller}
import views.html

import scala.concurrent.Future

class Sessions @Inject()(val userDAO: UserDAO, val messagesApi: MessagesApi)
  extends Controller with OptionalAuthElement with LoginLogout with AuthConfigImpl with I18nSupport {

  val index = Action { implicit request =>
    Redirect(routes.Tasks.allTasks())
  }

  val loginForm = Form {
    mapping(
      "login" -> email.verifying("email.empty", s => !s.isEmpty),
      "password" -> text.verifying("password.empty", s => !s.isEmpty)
    )(userDAO.authenticate)(_.map(u => (u.email, "")))
      .verifying("error.login", result => result.isDefined)
  }

  def login = StackAction { implicit request =>
    if (loggedIn.isDefined)
      Redirect(routes.Tasks.allTasks())
    else
      Ok(html.login(loginForm))
  }

  def logout = Action.async { implicit request =>
    gotoLogoutSucceeded.map(_.flashing(
      "success" -> "You've been logged out"
    ).removingFromSession("rememberme"))
  }

  def authenticate = Action.async { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(html.login(formWithErrors))),
      user           => gotoLoginSucceeded(user.get.id.get)
    )
  }

}
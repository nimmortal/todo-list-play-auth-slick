package controllers

import javax.inject.Inject

import jp.t2v.lab.play2.auth.{OptionalAuthElement, LoginLogout}
import model.user.UserDAO
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{Action, Controller}
import views.html
import play.api.i18n.Messages.Implicits._
import play.api.Play.current

import scala.concurrent.Future

class Sessions @Inject()(val userDAO: UserDAO)
  extends Controller with OptionalAuthElement with LoginLogout with AuthConfigImpl {

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

  def registrationForm = Action { implicit request =>
    Ok(html.registration(loginForm))
  }

  def registration = TODO

}
package controllers

import javax.inject.Inject

import play.api.i18n.{MessagesApi, I18nSupport}
import jp.t2v.lab.play2.auth.AuthenticationElement
import model.user.UserDAO
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, Controller}
import views.html


class Registration @Inject()(val userDAO: UserDAO, val messagesApi: MessagesApi)
  extends Controller with AuthenticationElement with AuthConfigImpl with I18nSupport {

  val registrationForm = Form {
    mapping(
      "login" -> email.verifying("email.empty", s => !s.isEmpty),
      "password" -> text.verifying("password.empty", s => !s.isEmpty)
    )(userDAO.authenticate)(_.map(u => (u.email, "")))
      .verifying("error.login", result => result.isDefined)
  }

  def goToRegistration = Action { implicit request =>
    Ok(html.registration(registrationForm))
  }

  def registration = TODO

}

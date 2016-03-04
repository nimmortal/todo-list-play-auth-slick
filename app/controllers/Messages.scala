package controllers

import controllers.stack.Pjax
import jp.t2v.lab.play2.auth.AuthElement
import model.user.Role
import model.user.Role.{Administrator, User}
import play.api.mvc.Controller
import views.html

trait Messages extends Controller with Pjax with AuthElement with AuthConfigImpl {

  // The `StackAction` method
  //    takes `(AuthorityKey, Authority)` as the first argument and
  //    a function signature `RequestWithAttributes[AnyContent] => Result` as the second argument and
  //    returns an `Action`

  // The `loggedIn` method
  //     returns current logged in user

  def main = StackAction(AuthorityKey -> User) { implicit request =>
    val title = "message main"
    val user = loggedIn
    Ok(html.message.main(title))
  }

  def list = StackAction(AuthorityKey -> User) { implicit request =>
    val title = "all messages"
    Ok(html.message.list(title))
  }

  def detail(id: Int) = StackAction(AuthorityKey -> User) { implicit request =>
    val title = "messages detail "
    Ok(html.message.detail(title + id))
  }

  def write = StackAction(AuthorityKey -> Administrator) { implicit request =>
    val title = "write message"
    Ok(html.message.write(title))
  }

  protected val fullTemplate: User => Template = html.fullTemplate.apply

}
object Messages extends Messages
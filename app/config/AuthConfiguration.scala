package config

import controllers.routes
import jp.t2v.lab.play2.auth._
import model.user.access.Role
import model.user.access.Role.{Administrator, BlockedUser, User}
import model.user.dao.UserDAO
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.{ClassTag, classTag}

trait AuthConfiguration extends AuthConfig {

  type Id = Long
  type User = model.user.User
  type Authority = Role

  val userDAO: UserDAO

  val idTag: ClassTag[Id] = classTag[Id]
  val sessionTimeoutInSeconds: Int = 3600
  def resolveUser(id: Id)(implicit ctx: ExecutionContext): Future[Option[User]] = userDAO.get(id)

  def loginSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] = {
    val uri = request.session.get("access_uri").getOrElse(routes.Tasks.getTaskPage().url.toString)
    Future.successful(Redirect(uri).withSession(request.session - "access_uri"))
  }

  def logoutSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] =
    Future.successful(Redirect(routes.Tasks.getTaskPage()))

  def authenticationFailed(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] =
    Future.successful(Redirect(controllers.auth.routes.AuthController.login()).withSession("access_uri" -> request.uri))

  override def authorizationFailed(request: RequestHeader, user: User, authority: Option[Authority])(implicit context: ExecutionContext): Future[Result] = {
    Future.successful(Forbidden("no permission"))
  }

  def authorize(user: User, authority: Authority)(implicit ctx: ExecutionContext): Future[Boolean] = Future.successful {
    (user.role, authority) match {
      case ( Administrator, _ ) => true
      case ( User, User ) => true
      case ( BlockedUser, BlockedUser ) => true
      case _              => false
    }
  }

  override lazy val tokenAccessor = new CookieTokenAccessor(
    cookieSecureOption = play.api.Play.isProd(play.api.Play.current),
    cookieMaxAge       = Some(sessionTimeoutInSeconds)
  )

}
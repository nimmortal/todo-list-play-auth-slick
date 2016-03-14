package controllers

import jp.t2v.lab.play2.auth._
import model.user.Role.{Administrator, User}
import model.user.{UserDAOTrait, Account, Role}
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.{ClassTag, classTag}

trait AuthConfigImpl extends AuthConfig {

  type Id = Long
  type User = Account
  type Authority = Role

  val userDAO: UserDAOTrait

  val idTag: ClassTag[Id] = classTag[Id]
  val sessionTimeoutInSeconds: Int = 3600
  def resolveUser(id: Id)(implicit ctx: ExecutionContext): Future[Option[User]] = userDAO.findUser(id)

  def loginSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] = {
    val uri = request.session.get("access_uri").getOrElse(routes.Tasks.allTasks().url.toString)
    Future.successful(Redirect(uri).withSession(request.session - "access_uri"))
  }

  def logoutSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] =
    Future.successful(Redirect(routes.Tasks.allTasks()))

  def authenticationFailed(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] =
    Future.successful(Redirect(routes.Sessions.login()).withSession("access_uri" -> request.uri))

  override def authorizationFailed(request: RequestHeader, user: User, authority: Option[Authority])(implicit context: ExecutionContext): Future[Result] = {
    Future.successful(Forbidden("no permission"))
  }

  def authorize(user: User, authority: Authority)(implicit ctx: ExecutionContext): Future[Boolean] = Future.successful {
    (user.role, authority) match {
      case ( Administrator, _ ) => true
      case ( User, User ) => true
      case _              => false
    }
  }

  override lazy val tokenAccessor = new CookieTokenAccessor(
    cookieSecureOption = play.api.Play.isProd(play.api.Play.current),
    cookieMaxAge       = Some(sessionTimeoutInSeconds)
  )

}
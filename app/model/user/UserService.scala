package model.user

import javax.inject.Inject

import model.user.dao.{FacebookDAO, RegisteredUserDAO, UserDAO}
import play.api.cache.CacheApi

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

class UserService @Inject()(userDAO: UserDAO,
                            registeredUserDAO: RegisteredUserDAO,
                            facebookDAO: FacebookDAO,
                            cache: CacheApi) {

  def getUserView(userId: Long) : UserView = {
    val maybeUser: Option[UserView] = cache.get[UserView](userId.toString)

    maybeUser.getOrElse {
      val registeredUser = registeredUserDAO.get(userId)
      val facebookUser = facebookDAO.findByUserId(userId)

      val userView = userDAO.get(userId).flatMap { maybeUser =>
        maybeUser.map { u =>
          registeredUser zip facebookUser map {
            case (Some(r), None) => new UserView(r.username, "", u.role)
            case (None, Some(f)) => new UserView(f.name, "", u.role)
            case _ => throw new Exception("user operations")
          }
        } getOrElse {
          Future.failed(throw new Exception("user operations"))
        }
      }

      val userViewResult = Await.result(userView, Duration.Inf)
      cache.set(userId.toString, userViewResult)
      userViewResult
    }

  }

  def removeUserView(userId: Long): Unit = {
    cache.remove(userId.toString)
  }

}

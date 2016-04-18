
package model.auth

import javax.inject.Inject

import model.user.access.Role
import model.user.dao.{FacebookDAO, RegisteredUserDAO, UserDAO}
import model.user.{FacebookUser, User}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LoginService @Inject()(userDAO: UserDAO, registeredUserDAO: RegisteredUserDAO, facebookDAO: FacebookDAO) {

  def authenticate(email: String, password: String): Future[Option[User]] = {
    registeredUserDAO.find(email, password).flatMap { regUser =>
      regUser.map(u => userDAO.get(u.userId)).getOrElse(Future.successful(None))
    }
  }

  def authenticateFacebook(user: FacebookUser): Future[Long] = {
    facebookDAO.findById(user.id).flatMap {
      case None => createFacebookAcc(user)
      case Some(fu) => Future.successful(fu.userId)
    }
  }

  private def createFacebookAcc(user: FacebookUser) : Future[Long] = {
    val userCreated = userDAO.create(new User(None, Role.User))

    userCreated.map { id =>
      val u = user.copy(userId = id)
      facebookDAO.save(u)
      u.userId
    }

  }

}

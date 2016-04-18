package model.auth

import javax.inject.Inject

import model.user.User
import model.user.access.Role
import model.user.dao.{RegisteredUserDAO, UserDAO}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RegistrationService @Inject()(userDAO: UserDAO, registeredUserDAO: RegisteredUserDAO) {

  def registration(email: String, username: String, password: String) : Future[Long] = {
    registeredUserDAO.find(email).flatMap { maybeUser =>
      if (maybeUser.isDefined) {
        throw new Exception("model.user.already.used")
      } else {
        val userCreated = userDAO.create(new User(None, Role.User))

        userCreated.map { id =>
          registeredUserDAO.save((id, email, username, password))
          id
        }

      }
    }
  }

}

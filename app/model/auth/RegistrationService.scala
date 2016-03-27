package model.auth

import javax.inject.Inject

import model.user.access.Role
import model.user.dao.{RegisteredUserDAO, UserDAO}
import model.user.{RegisteredUserDTO, User}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RegistrationService @Inject()(userDAO: UserDAO, registeredUserDAO: RegisteredUserDAO) {

  def registration(user: RegisteredUserDTO) : Future[Long] = {

    registeredUserDAO.find(user.email).flatMap { maybeUser =>
      if (maybeUser.isDefined) {
        throw new Exception("model.user.already.used")
      } else {
        val userCreated = userDAO.create(new User(0L, Role.User))

        userCreated.map { id =>
          user.userId = id
          registeredUserDAO.save(user)
          id
        }

      }
    }

  }

}

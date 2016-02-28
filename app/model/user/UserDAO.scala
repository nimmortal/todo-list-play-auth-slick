package model.user

import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

object UserDAO {

  def findUser(id: Long) : Future[Option[Account]] = Future {
    Some(new Account(Some(1L), "email", "pass", "name", "Administrator"))
  }

  def authenticate(email: String, password: String): Option[Account] = {
    Some(new Account(Some(1L), email, password, "name", "Administrator"))
  }


}

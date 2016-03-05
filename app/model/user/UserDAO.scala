package model.user

import model.user.table.AccountTable
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.H2Driver.api._

import slick.driver.JdbcProfile
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object UserDAO {

  val db = DatabaseConfigProvider.get[JdbcProfile](Play.current).db
  val accounts = TableQuery[AccountTable]

  def findUser(id: Long) : Future[Option[Account]] = db.run(accounts.filter(_.id === id).result.headOption)

  def authenticate(email: String, password: String): Option[Account] = {
//    Some(new Account(Some(2L), email, password, "name", Role.User))
    val findUser = accounts.filter { u =>
      u.email === email && u.password === password
    }

    Await.result(db.run(findUser.result.headOption), Duration.Inf)
  }


}

package model.user

import model.user.table.{UserTable, AccountTable}
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.H2Driver.api._
import scala.concurrent.ExecutionContext.Implicits.global

import slick.driver.JdbcProfile
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object UserDAO {

  val db = DatabaseConfigProvider.get[JdbcProfile](Play.current).db
  val accounts = TableQuery[AccountTable]
  val users = TableQuery[UserTable]

  def findUser(id: Long) : Future[Option[Account]] = db.run(accounts.filter(_.id === id).result.headOption)

  def getUser(acc: Account) : Future[Option[UserModel]] = {
    val innerJoin = for {
      (a, u) <- accounts join users on (_.email === _.email) if a.email === acc.email
    } yield (u, a)

    db.run(innerJoin.result.headOption.map {
      case o: Option[(User, Account)] => Some(new UserModel(o.get._1, o.get._2))
    })
  }

  def authenticate(email: String, password: String): Option[Account] = {
    val findUser = accounts.filter { u =>
      u.email === email && u.password === password
    }

    Await.result(db.run(findUser.result.headOption), Duration.Inf)
  }


}

package model.user

import javax.inject.{Inject, Singleton}
import com.google.inject.ImplementedBy
import model.user.table.{UserTable, AccountTable}
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import slick.driver.H2Driver.api._
import scala.concurrent.ExecutionContext.Implicits.global

import slick.driver.JdbcProfile
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

@ImplementedBy(classOf[UserDAO])
trait UserDAOTrait {
  def findUser(id: Long) : Future[Option[Account]]
  def authenticate(email: String, password: String): Option[Account]
}

@Singleton
class UserDAO @Inject()(val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] with UserDAOTrait {

  val accounts = TableQuery[AccountTable]
  val users = TableQuery[UserTable]

  def findUser(id: Long) : Future[Option[Account]] = db.run(accounts.filter(_.id === id).result.headOption)
  def findUser(email: String) : Future[Option[Account]] = db.run(accounts.filter(_.email === email).result.headOption)

  def createAccount(account: Account) : Future[Long] = {
    val accId = {
      (accounts returning accounts.map(_.id)) += account
    }

    val id = db.run(accId).map(id => id.get)
    db.run(users += new User(account.email, "a", "a", "a"))
    id
  }

  def getUser(acc: Account) : Future[Option[UserModel]] = {
    val innerJoin = for {
      (a, u) <- accounts join users on (_.email === _.email) if a.email === acc.email
    } yield (u, a)

    val res = db.run(users.result)
    val res1 = db.run(accounts.result)

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

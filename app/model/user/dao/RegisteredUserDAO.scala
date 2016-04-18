package model.user.dao

import javax.inject.Inject

import com.google.inject.ImplementedBy
import model.user.RegisteredUser
import model.user.RegisteredUser.RegisteredUserTable
import model.user.User.RegistrationTable
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.H2Driver.api._
import slick.driver.JdbcProfile

import scala.concurrent.Future

@ImplementedBy(classOf[RegisteredUserDAOImpl])
trait RegisteredUserDAO {
  def get(id: Long) : Future[Option[RegisteredUser]]
  def find(email: String) : Future[Option[RegisteredUser]]
  def find(login: String, pass: String) : Future[Option[RegisteredUser]]
  def save(registeredUser: RegisteredUser) : Future[Int]
  def save(user: (Long, String, String, String)): Future[Int]
}

class RegisteredUserDAOImpl @Inject()(val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] with RegisteredUserDAO {

  val users = TableQuery[RegisteredUserTable]

  override def get(id: Long): Future[Option[RegisteredUser]] = db.run(users.filter(_.userId === id).result.headOption)

  override def find(email: String): Future[Option[RegisteredUser]] = db.run(users.filter(_.email === email).result.headOption)

  override def find(login: String, pass: String): Future[Option[RegisteredUser]] = {
    val findUser = users.filter { u =>
      (u.email === login || u.username === login) && u.password === pass
    }

    db.run(findUser.result.headOption)
  }

  override def save(registeredUser: RegisteredUser): Future[Int] = {
    db.run(users += registeredUser)
  }

  val usersToRegistration = TableQuery[RegistrationTable]

  override def save(user: (Long, String, String, String)): Future[Int] = {
    db.run(usersToRegistration += user)
  }
}

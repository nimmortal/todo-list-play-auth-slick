package model.user.dao

import javax.inject.Inject

import com.google.inject.ImplementedBy
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.H2Driver.api._
import slick.driver.JdbcProfile
import model.user.{RegisteredUser, RegisteredUserDTO}
import model.user.table.{RegisteredUserDTOTableMapping, RegisteredUserTableMapping}

import scala.concurrent.Future

@ImplementedBy(classOf[RegisteredUserDAOImpl])
trait RegisteredUserDAO {
  def get(id: Long) : Future[Option[RegisteredUser]]
  def find(email: String) : Future[Option[RegisteredUser]]
  def find(login: String, pass: String) : Future[Option[RegisteredUser]]
  def save(registeredUser: RegisteredUser) : Future[Int]
  def save(user: RegisteredUserDTO): Future[Int]
}

class RegisteredUserDAOImpl @Inject()(val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] with RegisteredUserDAO {

  val users = TableQuery[RegisteredUserTableMapping]

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

  val usersToRegistration = TableQuery[RegisteredUserDTOTableMapping]

  override def save(user: RegisteredUserDTO): Future[Int] = {
    db.run(usersToRegistration += user)
  }
}

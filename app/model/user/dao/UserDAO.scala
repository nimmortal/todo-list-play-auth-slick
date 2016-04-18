package model.user.dao

import javax.inject.{Inject, Singleton}

import com.google.inject.ImplementedBy
import model.user.User
import model.user.User.UserTable
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.H2Driver.api._
import slick.driver.JdbcProfile
import slick.lifted.TableQuery

import scala.concurrent.Future

@ImplementedBy(classOf[UserDAOImpl])
trait UserDAO {
  def create(user: User) : Future[Long]
  def update(user: User) : Unit
  def get(id: Long) : Future[Option[User]]
}

@Singleton
class UserDAOImpl @Inject()(val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] with UserDAO {

  val users = TableQuery[UserTable]

  override def get(id: Long) : Future[Option[User]] = db.run(users.filter(_.id === id).result.headOption)

  override def create(user: User) : Future[Long] = db.run((users returning users.map(_.id)) += user)

  override def update(user: User): Unit = db.run(users.filter(_.id === user.id).update(user))

}
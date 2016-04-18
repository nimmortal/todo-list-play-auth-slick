package model.user.dao

import javax.inject.Inject

import com.google.inject.ImplementedBy
import model.user.FacebookUser
import model.user.FacebookUser.FacebookUserTable
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.H2Driver.api._
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@ImplementedBy(classOf[FacebookDAOImpl])
trait FacebookDAO {
  def findById(id: String): Future[Option[FacebookUser]]
  def findByUserId(userId: Long): Future[Option[FacebookUser]]
  def save(facebookUser: FacebookUser): FacebookUser
}

class FacebookDAOImpl @Inject()(val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] with FacebookDAO {

  val users = TableQuery[FacebookUserTable]

  override def findById(id: String): Future[Option[FacebookUser]] = {
    db.run(users.filter(_.id === id).result.headOption)
  }

  override def findByUserId(userId: Long): Future[Option[FacebookUser]] = {
    db.run(users.filter(_.userId === userId).result.headOption)
  }

  override def save(facebookUser: FacebookUser): FacebookUser = {
    db.run(users += facebookUser).onFailure {
      case e => Console.println("An error has occured: " + e)
    }

    facebookUser
  }

}

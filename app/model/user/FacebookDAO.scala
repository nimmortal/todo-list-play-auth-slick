package model.user

import javax.inject.Inject

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.H2Driver.api._
import slick.driver.JdbcProfile
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

case class FacebookUser( userId: Long,
                         id: String,
                         name: String,
                         coverUrl: String,
                         accessToken: String)

class FacebookUserTable(tag: Tag) extends Table[FacebookUser](tag, "facebook_users") {

  def userId = column[Long]("user_id")
  def id = column[String]("id", O.PrimaryKey)
  def name = column[String]("name")
  def coverUrl = column[String]("cover_url")
  def access_token = column[String]("access_token")

  def * = (userId, id, name, coverUrl, access_token) <> ((FacebookUser.apply _).tupled, FacebookUser.unapply)
}

class FacebookDAO @Inject()(val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  val facebookUsers = TableQuery[FacebookUserTable]

  def findById(id: String): Future[Option[FacebookUser]] = {
    db.run(facebookUsers.filter(_.id === id).result.headOption)
  }

  def findByUserId(userId: Long): Future[Option[FacebookUser]] = {
    db.run(facebookUsers.filter(_.userId === userId).result.headOption)
  }

  def save(userId: Long, facebookUser: FacebookUser): FacebookUser = {
    val user = FacebookUser(userId, facebookUser.id, facebookUser.name, facebookUser.coverUrl, facebookUser.accessToken)

    db.run(facebookUsers += user).onFailure {
      case e => Console.println(e)
    }
    user
  }

}

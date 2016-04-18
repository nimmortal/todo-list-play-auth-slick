package model.user

import slick.driver.H2Driver.api._

case class FacebookUser( userId: Long,
                         id: String,
                         name: String,
                         email: String,
                         avatar: String,
                         accessToken: String
                       )

object FacebookUser {

  class FacebookUserTable(tag: Tag) extends Table[FacebookUser](tag, "facebook_users") {
    def userId = column[Long]("user_id")
    def id = column[String]("id", O.PrimaryKey)
    def name = column[String]("email")
    def email = column[String]("name")
    def avatar = column[String]("avatar_url")
    def accessToken = column[String]("access_token")

    def * = (userId, id, name, email, avatar, accessToken) <> ((FacebookUser.apply _).tupled, FacebookUser.unapply)
  }

}
package model.user

import model.user.access.Role
import model.util.columns.RoleColumns
import slick.driver.H2Driver.api._

case class UserView(username: String, avatar: String, role: Role)

case class User(id: Option[Long], role: Role)

object User {

  class UserTable(tag: Tag) extends Table[User](tag, "users") with RoleColumns {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def role = column[Role]("role")
    def * = (id.?, role) <> ((User.apply _).tupled, User.unapply)
  }

  class RegistrationTable(tag: Tag) extends Table[(Long, String, String, String)](tag, "registered_users") {
    def userId = column[Long]("user_id")
    def email = column[String]("email")
    def username = column[String]("username")
    def password = column[String]("password")
    def * = (userId, email, username, password)
  }

}
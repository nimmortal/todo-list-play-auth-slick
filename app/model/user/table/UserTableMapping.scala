package model.user.table

import slick.driver.H2Driver.api._
import model.user.User
import model.user.access.Role
import model.util.columns.RoleColumns

class UserTableMapping(tag: Tag) extends Table[User](tag, "users") with RoleColumns {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def role = column[Role]("role")

  def * = (id, role) <> ((User.apply _).tupled, User.unapply)
}

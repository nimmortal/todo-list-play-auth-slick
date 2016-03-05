package model.user.table

import model.user.Account
import model.user.Role
import slick.driver.H2Driver.api._

class AccountTable(tag: Tag) extends Table[Account](tag, "users") {
  implicit val mappedRole = MappedColumnType.base[Role, String](
    { role => Role.toString(role) },
    { sRole => Role.valueOf(sRole) }
  )

  def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)
  def email = column[String]("email")
  def password = column[String]("password")
  def name = column[String]("name")
  def role = column[Role]("role")

  def * = (id, email, password, name, role) <> ((Account.apply _).tupled, Account.unapply)
}
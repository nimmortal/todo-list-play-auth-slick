package model.user.table

import model.user.User
import slick.driver.H2Driver.api._

class UserTable(tag: Tag) extends Table[User](tag, "users") {

  def email = column[String]("email")
  def name = column[String]("name")
  def surname = column[String]("surname")
  def address = column[String]("address")

  def * = (email, name, surname, address) <> ((User.apply _).tupled, User.unapply)
}

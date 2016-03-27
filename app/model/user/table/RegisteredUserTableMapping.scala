package model.user.table

import slick.driver.H2Driver.api._
import model.user.{RegisteredUser, RegisteredUserDTO}

class RegisteredUserTableMapping(tag: Tag) extends Table[RegisteredUser](tag, "registered_users") {

  def userId = column[Long]("user_id")
  def email = column[String]("email")
  def username = column[String]("username")
  def password = column[String]("password")
  def name = column[Option[String]]("name")
  def surname = column[Option[String]]("surname")
  def location = column[Option[String]]("location")
  def avatar = column[Option[String]]("avatar_url")

  def * = (userId, email, username, name, surname, location, avatar) <> ((RegisteredUser.apply _).tupled, RegisteredUser.unapply)

}

class RegisteredUserDTOTableMapping(tag: Tag) extends Table[RegisteredUserDTO](tag, "registered_users") {

  def userId = column[Long]("user_id")
  def email = column[String]("email")
  def username = column[String]("username")
  def password = column[String]("password")

  def * = (userId, email, username, password) <> ((RegisteredUserDTO.apply _).tupled, RegisteredUserDTO.unapply)

}

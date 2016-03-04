package model.user

sealed trait Role

object Role {

  case object Administrator extends Role
  case object User extends Role

  def valueOf(value: String): Role = value match {
    case "Administrator" => Administrator
    case "User"    => User
    case _ => throw new IllegalArgumentException()
  }
}

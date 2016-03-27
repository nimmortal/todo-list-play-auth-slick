package model.user.access

sealed trait Role

object Role {

  case object Administrator extends Role
  case object User extends Role
  case object BlockedUser extends Role

  def valueOf(value: String): Role = value match {
    case "Administrator" => Administrator
    case "BlockedUser" => BlockedUser
    case "User"    => User
    case _ => throw new IllegalArgumentException()
  }

  def toString(role: Role): String = role match {
    case Administrator => "Administrator"
    case BlockedUser => "BlockedUser"
    case User    => "User"
    case _ => throw new IllegalArgumentException()
  }
}

package model.user

case class Account(id: Option[Long] = None,
                   email: String,
                   password: String,
                   role: Role)

package model.user

case class RegisteredUserDTO( var userId: Long,
                              email: String,
                              username: String,
                              password: String )

case class RegisteredUser( userId: Long,
                           email: String,
                           username: String,
                           name: Option[String],
                           surname: Option[String],
                           location: Option[String],
                           avatar: Option[String]
                         )
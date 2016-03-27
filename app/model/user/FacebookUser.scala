package model.user

case class FacebookUser( var userId: Long,
                         id: String,
                         name: String,
                         email: String,
                         avatar: String,
                         accessToken: String
                       )
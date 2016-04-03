package model.user

import model.user.access.Role

case class User(id: Long, role: Role)

case class UserView(username: String, avatar: String, role: Role)
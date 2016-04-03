package model.util.columns

import model.user.access.Role

import slick.driver.H2Driver.api._

trait RoleColumns {

  implicit val roleColumnType = MappedColumnType.base[Role, String](
    r => Role.toString(r),
    r => Role.valueOf(r)
  )

}

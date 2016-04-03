package model.util.columns

import java.sql.{Time, Timestamp, Date}
import java.time.{LocalDate, LocalDateTime, LocalTime, ZoneOffset}

import slick.driver.H2Driver.api._

trait DateTimeColumns {

  implicit val localDateTimeColumnType = MappedColumnType.base[LocalDateTime, Timestamp](
    d => Timestamp.from(d.toInstant(ZoneOffset.ofHours(0))),
    d => d.toLocalDateTime
  )

/*
  implicit val dateColumnType = MappedColumnType.base[LocalDate, Date](
    d => Date.valueOf(d),
    d => d.toLocalDate
  )

  implicit val timeColumnType = MappedColumnType.base[LocalTime, Time](
    localTime => Time.valueOf(localTime),
    time => time.toLocalTime
  )
*/

}

package model.task.tabels

import java.sql.Timestamp
import java.time.{LocalDateTime, ZoneOffset}

import model.task.Task
import slick.driver.H2Driver.api._

class TasksTable(tag: Tag) extends Table[Task](tag, "task") {

  implicit val localDateTimeColumnType = MappedColumnType.base[LocalDateTime, Timestamp](
    d => Timestamp.from(d.toInstant(ZoneOffset.ofHours(0))),
    d => d.toLocalDateTime
  )

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def label = column[String]("label")
  def who = column[String]("who")
  def created = column[LocalDateTime]("created")
  def ready = column[Boolean]("ready")

  def * = (id, label, who, created, ready) <> ((Task.apply _).tupled, Task.unapply)
}
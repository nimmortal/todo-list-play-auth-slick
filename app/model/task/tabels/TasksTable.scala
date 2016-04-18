package model.task.tabels

import java.time.LocalDateTime

import model.task.Task
import model.util.columns.DateTimeColumns
import slick.driver.H2Driver.api._

class TasksTable(tag: Tag) extends Table[Task](tag, "task") with DateTimeColumns {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def label = column[String]("label")
  def who = column[String]("who")
  def created = column[LocalDateTime]("created")
  def ready = column[Boolean]("ready")

  def * = (id.?, label, who, created, ready) <> ((Task.apply _).tupled, Task.unapply)
}
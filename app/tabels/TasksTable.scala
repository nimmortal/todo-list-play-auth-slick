package tabels

import slick.driver.H2Driver.api._
import model.Task

class TasksTable(tag: Tag) extends Table[Task](tag, "task") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def label = column[String]("label")
  def who = column[String]("who")
  def myTime = column[String]("mytime")
  def ready = column[Boolean]("ready")

  def * = (id, label, who, myTime, ready) <> ((Task.apply _).tupled, Task.unapply)
}
package model.task

import javax.inject.{Inject, Singleton}
import model.task.tabels.TasksTable
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import slick.driver.H2Driver.api._
import slick.driver.JdbcProfile

import scala.concurrent.Future

@Singleton()
class TaskDAO @Inject()(val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  val tasks = TableQuery[TasksTable]

  def get(id: Long): Future[Option[Task]] = db.run(tasks.filter(_.id === id).result.headOption)

  def getAll: Future[Seq[Task]] = db.run(tasks.result)

  def create(label: String, owner: String, time: String): Future[Long] = {
    val taskId = (tasks returning tasks.map(_.id)) += new Task(0, label, owner, time, false)
    db.run(taskId)
  }

  def update(task: Task): Unit = {
    val query = tasks.filter(_.id === task.id)
    db.run(query.update(task))
  }

  def delete(task: Task): Unit = db.run(tasks.filter(_.id === task.id).delete)

  def complete(task: Task): Unit = {
    val q = for { t <- tasks if t.id === task.id } yield t.ready
    db.run(q.update(true))
  }

}




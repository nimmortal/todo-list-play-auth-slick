package model.task

import model.task.tabels.TasksTable
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.H2Driver.api._
import slick.driver.JdbcProfile

import scala.concurrent.Future

object TaskDAO {

  val db = DatabaseConfigProvider.get[JdbcProfile](Play.current).db
  val tasks = TableQuery[TasksTable]

  def get(id: Long): Future[Option[Task]] = db.run(tasks.filter(_.id === id).result.headOption)

  def getAll: Future[Seq[Task]] = db.run(tasks.result)

  def create(label: String, owner: String, time: String): Unit = db.run(tasks += new Task(0, label, owner, time, false))

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




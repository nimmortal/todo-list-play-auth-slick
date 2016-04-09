package model.task

import java.time.LocalDateTime
import javax.inject.{Inject, Singleton}

import model.task.tabels.TasksTable
import model.util.Page
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.H2Driver.api._
import slick.driver.JdbcProfile

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton()
class TaskDAO @Inject()(val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  val tasks = TableQuery[TasksTable]

  def get(id: Long): Future[Option[Task]] = db.run(tasks.filter(_.id === id).result.headOption)

  def getAll: Future[Seq[Task]] = db.run(tasks.result)

  def getTaskPage(page: Int = 1, limit: Int = 10): Future[Page[Task]] = {
    val offset = page * limit

    val q = tasks.drop(offset).take(limit).result
    db.run(q).map {
      case tasks: Seq[Task] => new Page[Task](tasks, limit, offset + 1, tasks.size)
    }
  }

  def getSize: Future[Int] = db.run(tasks.length.result)

  def create(label: String, owner: String, time: LocalDateTime): Future[Long] = {
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




package model.task

import java.time.LocalDateTime
import javax.inject.{Inject, Singleton}

import model.task.tabels.TasksTable
import model.util.Page
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.H2Driver.api._
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton()
class TaskDAO @Inject()(val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  val tasks = TableQuery[TasksTable]

  def filteredTasks(filter: String = "") = tasks.filter(_.label like ("%" ++ filter ++ "%"))

  def get(id: Long): Future[Option[Task]] = db.run(tasks.filter(_.id === id).result.headOption)

  def getAll: Future[Seq[Task]] = db.run(tasks.result)

  def getTaskPage(page: Int = 1, limit: Int = 10): Future[Page[Task]] = {
    val offset = page * limit
    val size = getSize()

    val q = tasks.drop(offset).take(limit).result
    db.run(q).flatMap {
      case tasks: Seq[Task] => size.map(s => new Page[Task](tasks, limit, offset + 1, s, None))
    }
  }

  def getTaskPageFiltered(page: Int = 1, limit: Int = 10, filter: String) : Future[Page[Task]] = {
    val offset = page * limit
    val size = getSize(filter)

    val q = filteredTasks(filter).drop(offset).take(limit).result

    db.run(q).flatMap {
      case tasks: Seq[Task] => size.map(s => new Page[Task](tasks, limit, offset + 1, s, Some(filter)))
    }
  }

  def getSize(filter: String = "%"): Future[Int] = db.run(filteredTasks(filter).length.result)

  def create(label: String, owner: String, time: LocalDateTime): Future[Long] = {
    val taskId = (tasks returning tasks.map(_.id)) += new Task(None, label, owner, time, false)
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




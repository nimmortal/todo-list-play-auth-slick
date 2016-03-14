package controllers

import java.text.SimpleDateFormat
import java.util.Calendar
import javax.inject.Inject

import jp.t2v.lab.play2.auth.AuthElement
import model.task.{Task, TaskDAO}
import model.user.{Account, UserDAO}
import model.user.Role.{Administrator, User}
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._

import scala.language.postfixOps

case class TaskForm(label: String, owner: String)

class Tasks @Inject()(taskDAO: TaskDAO, val userDAO: UserDAO) extends Controller with AuthElement with AuthConfigImpl {

  def allTasks = AsyncStack(AuthorityKey -> User) { implicit request =>
    val user: Account = loggedIn
    val tasks = taskDAO getAll
    val account = userDAO getUser user

    tasks zip account map {
      case (t, a) => Ok(views.html.index(t)(a))
    }
  }

  def addTask() = StackAction(AuthorityKey -> User) { implicit request =>
    Ok(views.html.task(None, taskForm))
  }

  def editTask(id: Long) = AsyncStack(AuthorityKey -> User) { implicit request =>
    val task = taskDAO get id
    task.map(t => Ok(views.html.task(Some(id), taskForm.fill(TaskForm(t.get.label, t.get.owner)))))
  }

  def newTask = StackAction(AuthorityKey -> User) { implicit request =>
    taskForm.bindFromRequest().fold(
      errors => BadRequest(views.html.task(None, errors)),
      data => {
        val today = Calendar.getInstance().getTime
        val timeFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
        val time = timeFormat.format(today)

        taskDAO.create(data.label, data.owner, time)
        Redirect(routes.Tasks.allTasks())
      }
    )
  }

  def updateTask(id: Long) = StackAction(AuthorityKey -> User) { implicit request =>
    taskForm.bindFromRequest().fold(
      errors => BadRequest(views.html.task(Some(id), errors)),
      data => {
        val oldTask = taskDAO get id

        oldTask.map(t => taskDAO.update(new Task(id, data.label, data.owner, t.get.myTime, t.get.ready)))
        Redirect(routes.Tasks.allTasks())
      }
    )
  }

  def deleteTask(id: Long) = StackAction(AuthorityKey -> Administrator) { implicit request =>
    val task = taskDAO.get(id)

    task.map(optTask => optTask.foreach(task => taskDAO.delete(task)))

    Redirect(routes.Tasks.allTasks())
  }

  def completeTask(id: Long) = StackAction(AuthorityKey -> User) { implicit request =>
    val task = taskDAO.get(id)

    task.map(optTask => optTask.foreach(task => taskDAO.complete(task)))

    Redirect(routes.Tasks.allTasks())
  }

  val taskForm = Form(
    mapping(
      "label" -> nonEmptyText,
      "owner" -> nonEmptyText
    )(TaskForm.apply)(TaskForm.unapply)
  )

//  protected implicit def template(implicit user: User): String => Html => Html = html.main(user)
}


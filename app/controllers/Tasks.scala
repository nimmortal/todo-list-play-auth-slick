package controllers

import java.text.SimpleDateFormat
import java.util.Calendar

import jp.t2v.lab.play2.auth.AuthElement
import model.task.{Task, TaskDAO}
import model.user.Role
import model.user.Role.{Administrator, User}
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import play.twirl.api.Html
import views.html

import scala.language.postfixOps

case class TaskForm(label: String, owner: String)

object Tasks extends Controller with AuthElement with AuthConfigImpl {

  def allTasks = StackAction(AuthorityKey -> User) { implicit request =>
    Ok(views.html.index(TaskDAO getAll))
  }

  def addTask() = StackAction(AuthorityKey -> User) { implicit request =>
    Ok(views.html.task(None, taskForm))
  }

  def editTask(id: Long) = StackAction(AuthorityKey -> User) { implicit request =>
    val task = TaskDAO get id
    Ok(views.html.task(Some(id), taskForm.fill(TaskForm(task.get.label, task.get.owner))))
  }

  def newTask = StackAction(AuthorityKey -> User) { implicit request =>
    taskForm.bindFromRequest().fold(
      errors => BadRequest(views.html.task(None, errors)),
      data => {
        val today = Calendar.getInstance().getTime
        val timeFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
        val time = timeFormat.format(today)

        TaskDAO.create(data.label, data.owner, time)
        Redirect(routes.Tasks.allTasks())
      }
    )
  }

  def updateTask(id: Long) = StackAction(AuthorityKey -> User) { implicit request =>
    taskForm.bindFromRequest().fold(
      errors => BadRequest(views.html.task(Some(id), errors)),
      data => {
        val oldTask = TaskDAO get id get

        TaskDAO.update(new Task(id, data.label, data.owner, oldTask.myTime, oldTask.ready))
        Redirect(routes.Tasks.allTasks())
      }
    )
  }

  def deleteTask(id: Long) = StackAction(AuthorityKey -> Administrator) { implicit request =>
    val task = TaskDAO.get(id)

    if (task.isDefined)
      TaskDAO.delete(task.get)

    Redirect(routes.Tasks.allTasks())
  }

  def completeTask(id: Long) = StackAction(AuthorityKey -> User) { implicit request =>
    val task = TaskDAO.get(id)

    if (task.isDefined)
      TaskDAO.complete(task.get)

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


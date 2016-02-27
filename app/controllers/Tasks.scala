package controllers

import java.text.SimpleDateFormat
import java.util.Calendar

import model.task.TaskDAO
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._

import scala.language.postfixOps

case class TaskForm(label: String, owner: String)

class Tasks extends Controller {

  def allTasks = Action {
    Ok(views.html.index(TaskDAO getAll))
  }

  def addTask() = Action {
    Ok(views.html.new_task(taskForm.fill(TaskForm("", "Author"))))
  }

  def newTask = Action { implicit request =>
    taskForm.bindFromRequest().fold(
      errors => BadRequest(views.html.index(TaskDAO getAll)),
      data => {
        val today = Calendar.getInstance().getTime
        val timeFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
        val time = timeFormat.format(today)
        //----------------------------
        TaskDAO.create(data.label, data.owner, time)
        Redirect(routes.Tasks.allTasks())
      }
    )

  }

  def deleteTask(id: Long) = Action {
    val task = TaskDAO.get(id)

    if (task.isDefined)
      TaskDAO.delete(task.get)

    Redirect(routes.Tasks.allTasks())
  }

  def completeTask(id: Long) = Action {
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

}


package controllers

import java.text.SimpleDateFormat
import java.util.Calendar

import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import model.Task

import scala.language.postfixOps

class Tasks extends Controller {

  def allTasks = Action {
    Ok(views.html.index(Task getAll))
  }

  def addTask() = Action {
    Ok(views.html.new_task(taskForm.fill(TaskFormData("", "Author"))))
  }

  def newTask = Action { implicit request =>
    taskForm.bindFromRequest().fold(
      errors => BadRequest(views.html.index(Task getAll)),
      data => {
        val today = Calendar.getInstance().getTime
        val timeFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
        val time = timeFormat.format(today)
        //----------------------------
        Task.create(data.label, data.owner, time)
        Redirect(routes.Tasks.allTasks())
      }
    )

  }

  def deleteTask(id: Long) = Action {
    val task = Task.get(id)

    if (task.isDefined)
      Task.delete(task.get)

    Redirect(routes.Tasks.allTasks())
  }

  def completeTask(id: Long) = Action {
    val task = Task.get(id)

    if (task.isDefined)
      Task.complete(task.get)

    Redirect(routes.Tasks.allTasks())
  }

  val taskForm = Form(
    mapping(
      "label" -> nonEmptyText,
      "owner" -> nonEmptyText
    )(TaskFormData.apply)(TaskFormData.unapply)
  )

}

case class TaskFormData(label: String, owner: String)

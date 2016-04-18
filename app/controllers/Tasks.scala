package controllers

import java.time.LocalDateTime
import javax.inject._

import config.AuthConfiguration
import jp.t2v.lab.play2.auth.AuthElement
import model.task.{Task, TaskDAO}
import model.user.access.Role.{Administrator, User}
import model.user.dao.{FacebookDAO, UserDAO}
import model.user.{UserService, UserView}
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

case class TaskForm(label: String, owner: Option[String])

@Singleton
class Tasks @Inject()(userService: UserService,
                      facebookDAO: FacebookDAO,
                      taskDAO: TaskDAO,
                      val userDAO: UserDAO,
                      val messagesApi: MessagesApi)
  extends Controller with AuthElement with AuthConfiguration with I18nSupport {

  val userView: (Long => UserView) = id => userService.getUserView(id)

  def getTaskPage(page: Int, filter: String) = AsyncStack(AuthorityKey -> User) { implicit request =>
    val taskPage = taskDAO.getTaskPageFiltered(page - 1, 5, filter)

    taskPage.map(p =>
      Ok(views.html.index(p)(userView(loggedIn.id.get)))
    )
  }

  def addTask() = StackAction(AuthorityKey -> User) { implicit request =>
      implicit val user = userView(loggedIn.id.get)

      Ok(views.html.task(None, taskForm))
  }

  def editTask(id: Long) = AsyncStack(AuthorityKey -> User) { implicit request =>
    implicit val user = userView(loggedIn.id.get)

    taskDAO.get(id).map(t => t.fold {
      BadRequest(views.html.task(Some(id), taskForm.withError("task", "task not exist")))
    } { task =>
      Ok(views.html.task(Some(id), taskForm.fill(TaskForm(task.label, Some(task.owner)))))
    })
  }

  def newTask = StackAction(AuthorityKey -> User) { implicit request =>
    implicit val user = userView(loggedIn.id.get)

    taskForm.bindFromRequest().fold(
      errors => BadRequest(views.html.task(None, errors)),
      data => {
        val owner = data.owner.getOrElse(loggedIn.id.get.toString)

        taskDAO.create(data.label, owner, LocalDateTime.now())
        Redirect(routes.Tasks.getTaskPage())
      }
    )
  }

  def updateTask(id: Long) = StackAction(AuthorityKey -> User) { implicit request =>
    implicit val user = userView(loggedIn.id.get)

    taskForm.bindFromRequest().fold(
      errors => BadRequest(views.html.task(Some(id), errors)),
      data => {
        val oldTask = taskDAO get id
        val owner = data.owner.getOrElse(loggedIn.id.get.toString)

        oldTask.map(t => taskDAO.update(new Task(Some(id), data.label, owner, t.get.created, t.get.ready)))
        Redirect(routes.Tasks.getTaskPage())
      }
    )
  }

  def deleteTask(id: Long) = StackAction(AuthorityKey -> Administrator) { implicit request =>
    val task = taskDAO.get(id)

    task.map(optTask => optTask.foreach(task => taskDAO.delete(task)))

    Redirect(routes.Tasks.getTaskPage())
  }

  def completeTask(id: Long) = StackAction(AuthorityKey -> User) { implicit request =>
    val task = taskDAO.get(id)

    task.map(optTask => optTask.foreach(task => taskDAO.complete(task)))

    Redirect(routes.Tasks.getTaskPage())
  }

  val taskForm = Form(
    mapping(
      "label" -> nonEmptyText,
      "owner" -> optional(text)
    )(TaskForm.apply)(TaskForm.unapply)
  )

}


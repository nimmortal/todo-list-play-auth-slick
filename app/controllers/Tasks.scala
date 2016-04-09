package controllers

import java.time.LocalDateTime
import javax.inject._

import config.AuthConfiguration
import jp.t2v.lab.play2.auth.AuthElement
import model.task.{Task, TaskDAO}
import model.user.{UserService, UserView}
import play.api.i18n.{I18nSupport, MessagesApi}

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import model.user.access.Role.{Administrator, User}
import model.user.dao.{FacebookDAO, UserDAO}
import model.util.Page

import scala.language.postfixOps

case class TaskForm(label: String, owner: String)

@Singleton
class Tasks @Inject()(userService: UserService, facebookDAO: FacebookDAO,taskDAO: TaskDAO, val userDAO: UserDAO, val messagesApi: MessagesApi)
  extends Controller with AuthElement with AuthConfiguration with I18nSupport {

  val userView: (Long => UserView) = id => userService.getUserView(id)

  def getTaskPage(page: Int) = AsyncStack(AuthorityKey -> User) { implicit request =>
    val taskPage = taskDAO.getTaskPage(page - 1, 5)

    taskPage.map(p => Ok(views.html.index(p)(userView(loggedIn.id))))
  }

  def addTask() = StackAction(AuthorityKey -> User) { implicit request =>
      implicit val user = userView(loggedIn.id)

      Ok(views.html.task(None, taskForm))
  }

  def editTask(id: Long) = AsyncStack(AuthorityKey -> User) { implicit request =>
    implicit val user = userView(loggedIn.id)

    val task = taskDAO get id
    task.map(t => Ok(views.html.task(Some(id), taskForm.fill(TaskForm(t.get.label, t.get.owner)))))
  }

  def newTask = StackAction(AuthorityKey -> User) { implicit request =>
    implicit val user = userView(loggedIn.id)

    taskForm.bindFromRequest().fold(
      errors => BadRequest(views.html.task(None, errors)),
      data => {
        taskDAO.create(data.label, data.owner, LocalDateTime.now())
        Redirect(routes.Tasks.getTaskPage())
      }
    )
  }

  def updateTask(id: Long) = StackAction(AuthorityKey -> User) { implicit request =>
    implicit val user = userView(loggedIn.id)

    taskForm.bindFromRequest().fold(
      errors => BadRequest(views.html.task(Some(id), errors)),
      data => {
        val oldTask = taskDAO get id

        oldTask.map(t => taskDAO.update(new Task(id, data.label, data.owner, t.get.created, t.get.ready)))
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
      "owner" -> nonEmptyText
    )(TaskForm.apply)(TaskForm.unapply)
  )

//  protected implicit def template(implicit model.user: User): String => Html => Html = html.main(model.user)
}


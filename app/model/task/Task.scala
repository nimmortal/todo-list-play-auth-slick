package model.task

import java.time.LocalDateTime

case class Task(id: Option[Long], label: String, owner: String, created: LocalDateTime, ready: Boolean)
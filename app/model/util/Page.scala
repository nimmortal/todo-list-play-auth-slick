package model.util

case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long, filter: Option[String]) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}

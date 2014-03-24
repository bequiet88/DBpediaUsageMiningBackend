package de.unimannheim.dws.models.postgre

import java.sql.Timestamp

import scala.slick.driver.PostgresDriver.simple._

case class UserSession(
  id: Long,
  ip: String,
  timeFrom: Timestamp,
  timeTo: Timestamp,
  createdAt: Timestamp) {
  def setId(id: Long) = this.copy(id = id)
}


class UserSessions(tag: Tag) extends Table[UserSession](tag, "user_sessions") {
  def id  = column[Long]("id")
  def ip = column[String]("ip")
  def timeFrom = column[Timestamp]("time_from")
  def timeTo = column[Timestamp]("time_to")
  def createdAt = column[Timestamp]("created_at")
  def * = (id, ip, timeFrom, timeTo, createdAt) <> (UserSession.tupled, UserSession.unapply _)
}
package de.unimannheim.dws.models.postgre

import java.sql.Timestamp
import scala.slick.driver.PostgresDriver.simple._
import de.unimannheim.dws.models.postgre.Tables.UserSessionsRow

object UserSessionTest extends App {
  dbConn.openConn withSession { implicit session =>
    val userTable = Tables.UserSessions
    
    
//    println(userTable.findBy("id" => 4));
    
    userTable foreach(r => println(r))

    

    val newUser = UserSessionsRow(id = 0L, ip = Some("2de7e8ad72e76d7534a905868d58de29"), timeFrom = None, timeTo = None)

    // Slick insert
    val newId = (userTable returning userTable.map(_.id)) += newUser

    println(newId)
  }
  // TODO create with autotimestamp -> Joda to SQL

}
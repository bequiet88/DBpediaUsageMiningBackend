package de.unimannheim.dws.models.postgre

import java.sql.Timestamp
import scala.slick.driver.PostgresDriver.simple._
import de.unimannheim.dws.models.postgre.Tables.UserSessionsRow

object DbConn {

  import scala.slick.driver.PostgresDriver.simple.{ Database, Session }
  val ds = new org.postgresql.ds.PGSimpleDataSource
  ds.setDatabaseName("usage_mining")
  ds.setUser("postgres")
  ds.setPassword("postgres")
  //  var db: Database = null
  def openConn = {
    Database.forDataSource(ds)
    //        db = Database.forURL("jdbc:postgres://postgres:postgres@localhost/usage_mining", driver = "org.postgresql.Driver")
  }
}

object UserSessionTest extends App {
  DbConn.openConn withSession { implicit session =>
    val userTable = Tables.UserSessions

    //    println(userTable.findBy("id" => 4));
    userTable foreach (r => println(r))

    val newUser = UserSessionsRow(id = 0L, ip = Some("2de7e8ad72e76d7534a905868d58de29"), timeFrom = None, timeTo = None)

    // Slick insert
    val newId = (userTable returning userTable.map(_.id)) += newUser

    println(newId)
  }
  // TODO create with autotimestamp -> Joda to SQL

}

//scala.slick.model.codegen.SourceCodeGenerator.main(
//  Array("scala.slick.driver.PostgresDriver", "org.postgresql.Driver", "jdbc:postgresql://localhost/usage_mining", "D:/data_server/eclipse/DBpediaUsageMining/src/main/scala/", "de.unimannheim.dws.models.postgre", "postgres", "postgres")
//)

// = Some(new Timestamp(System.currentTimeMillis()))
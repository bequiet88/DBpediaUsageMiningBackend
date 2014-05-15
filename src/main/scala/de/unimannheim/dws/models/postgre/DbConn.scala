package de.unimannheim.dws.models.postgre

import scala.slick.driver.PostgresDriver.simple._

import de.unimannheim.dws.models.postgre.Tables.UserSessionsRow
import scala.collection.JavaConversions._

object DbConn {

  import scala.slick.driver.PostgresDriver.simple.{ Database, Session }

  val ds = try {
    val ds = new org.postgresql.ds.PGSimpleDataSource
    ds.setDatabaseName("usage_mining")
    ds.setUser("postgres")
    ds.setPassword("postgres")
    ds.setServerName(scala.util.Properties.envOrElse("DB_PORT_5432_TCP_ADDR", "localhost"))
    ds.setPortNumber(Integer.parseInt(scala.util.Properties.envOrElse("DB_PORT_5432_TCP_PORT", "5432")))
    ds
  } catch {
    case e: Exception => {
      println("Exception creating Data Source " + e.printStackTrace())
      new org.postgresql.ds.PGSimpleDataSource
    }
  }

  //  var db: Database = null
  def openConn = {
    try {
      println("open db conn invoked");
      Database.forDataSource(ds)
    } catch {
      case e: Exception => {
        println("Failed to open DB connection " + e.printStackTrace());
        null
      }
    }
    //        db = Database.forURL("jdbc:postgres://postgres:postgres@localhost/usage_mining", driver = "org.postgresql.Driver")
  }
}
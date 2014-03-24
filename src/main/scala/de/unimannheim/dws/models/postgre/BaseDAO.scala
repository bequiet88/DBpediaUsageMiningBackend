package de.unimannheim.dws.models.postgre

import org.postgresql.ds.PGSimpleDataSource
import scala.slick.driver.PostgresDriver.simple.Database

class BaseDAO {

}


object UsageMining {
  def session = {
    val ds = new PGSimpleDataSource
    ds.setDatabaseName("usage_mining")
    ds.setUser("postgres")
    ds.setPassword("postgres")
    Database.forDataSource(ds).createSession
  }
}
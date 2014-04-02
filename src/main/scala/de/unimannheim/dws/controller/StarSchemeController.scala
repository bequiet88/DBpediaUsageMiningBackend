package de.unimannheim.dws.controller

import java.io.File
import scala.collection.JavaConverters._
import scala.io.BufferedSource
import scala.io.Source
import de.unimannheim.dws.models.postgre.DbConn
import de.unimannheim.dws.models.postgre.Tables._
import de.unimannheim.dws.preprocessing.DBpediaOntologyAccess
import de.unimannheim.dws.preprocessing.Util
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.{ GetResult, StaticQuery => Q }
import scala.slick.driver.JdbcDriver.backend.Database

object StarSchemeController extends App {
  DbConn.openConn withSession { implicit session =>

    createSubjEntityStarScheme
//    createObjEntityStarScheme

    /**
     * Method to create subject entity analytics on DB
     */
    def createSubjEntityStarScheme(implicit session: slick.driver.PostgresDriver.backend.Session) = {

      val listSubj = Q.queryNA[EntityAnalyticsRow]("select 0 as id, 0 as sessionId, b.id as queryId, c.id as tripleId, a.class_id as classId, UPPER(MD5(CONCAT(c.subj_prefix, c.subj_entity))) as entityId, localtimestamp as createdAt from entities_unique as a, sparql_queries as b, simple_triples as c where b.id = c.query_id and c.subj_type = 'uri' and c.subj_prefix = a.prefix and c.subj_entity = a.entity").list

      try {
        EntityAnalytics.insertAll(listSubj: _*)
        println("successful " + listSubj.size)
      } catch {
        case e: java.sql.BatchUpdateException => println(e.getNextException())
      }
    }

    /**
     * Method to create object entity analytics on DB
     */
    def createObjEntityStarScheme(implicit session: slick.driver.PostgresDriver.backend.Session) = {

      val listObj = Q.queryNA[EntityAnalyticsRow]("select 0 as id, 0 as sessionId, b.id as queryId, c.id as tripleId, a.class_id as classId, UPPER(MD5(CONCAT(c.obj_prefix, c.obj_entity))) as entityId, localtimestamp as createdAt from entities_unique as a, sparql_queries as b, simple_triples as c where b.id = c.query_id and c.obj_type = 'uri' and c.obj_prefix = a.prefix and c.obj_entity = a.entity").list

      try {
        EntityAnalytics.insertAll(listObj: _*)
        println("successful " + listObj.size)
      } catch {
        case e: java.sql.BatchUpdateException => println(e.getNextException())
      }
    }

  }
}
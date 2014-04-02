package de.unimannheim.dws.controller

import scala.collection.JavaConverters._
import scala.io.BufferedSource
import scala.slick.driver.PostgresDriver.simple._
//{ TableQuery, Column, queryToInsertInvoker }
import scala.slick.driver.JdbcDriver.backend.Database

import de.unimannheim.dws.models.postgre.DbConn
import de.unimannheim.dws.models.postgre.Tables._
import de.unimannheim.dws.preprocessing.Util

object StarSchemeController extends App {
  DbConn.openConn withSession { implicit session =>

    //    createSubjEntityStarScheme
    //    createObjEntityStarScheme
    createPropertyStarScheme

    /**
     * Method to create subject entity analytics on DB
     */
    def createSubjEntityStarScheme(implicit session: slick.driver.PostgresDriver.backend.Session) = {
      import scala.slick.jdbc.{ GetResult, StaticQuery => Q }

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
      import scala.slick.jdbc.{ GetResult, StaticQuery => Q }

      val listObj = Q.queryNA[EntityAnalyticsRow]("select 0 as id, 0 as sessionId, b.id as queryId, c.id as tripleId, a.class_id as classId, UPPER(MD5(CONCAT(c.obj_prefix, c.obj_entity))) as entityId, localtimestamp as createdAt from entities_unique as a, sparql_queries as b, simple_triples as c where b.id = c.query_id and c.obj_type = 'uri' and c.obj_prefix = a.prefix and c.obj_entity = a.entity").list

      try {
        EntityAnalytics.insertAll(listObj: _*)
        println("successful " + listObj.size)
      } catch {
        case e: java.sql.BatchUpdateException => println(e.getNextException())
      }
    }

    /**
     * Method to create property analytics on DB
     */
    def createPropertyStarScheme(implicit session: slick.driver.PostgresDriver.backend.Session) = {

      //      val listProp = Q.queryNA[PropertyAnalyticsRow]("select 0 as id, 0 as sessionId, a.id as queryId, b.id as tripleId, localtimestamp as createdAt, UPPER(MD5(CONCAT(b.pred_prefix, b.pred_prop))) as propertyId from sparql_queries as a, simple_triples as b where a.id = b.query_id and b.pred_type = 'uri'").list

      //      SimpleTriples.flatMap{t => // filter(t => t.predType === "uri").
      //        SparqlQueries.filter(q => q.id === t.queryId)
      //          .map(q => (c.name, s.name))}

      val listValues = for {
        (q, t) <- SparqlQueries innerJoin SimpleTriples on (_.id === _.queryId) if t.predType === "uri"
      } yield (q.id, t.id, t.predPrefix, t.predProp) //PropertyAnalyticsRow(id = 0L, sessionId = Some(0L), queryId = Some(q.id.toString.asInstanceOf[Long]), tripleId = Some(t.id.toString.asInstanceOf[Long]), propertyId = Some(Util.md5(t.predPrefix.toString+t.predProp.toString)))

      val listProp = listValues.list.map(v => {
        PropertyAnalyticsRow(id = 0L, sessionId = Some(0L), queryId = Some(v._1), tripleId = Some(v._2), propertyId = Some(Util.md5(v._3.get + v._4.get)))
      })

      val upperLimit = (Util.round(listProp.size / 10000D, 0) + 1).asInstanceOf[Int]

      for {
        i <- (1 to upperLimit)

      } yield {

        val skip = i * 10000
        
        val tempList = listProp.slice(skip-10000, skip)

        try {
          PropertyAnalytics.insertAll(tempList: _*)
          println("successful " + tempList.size + ", total " + skip)
        } catch {
          case e: java.sql.BatchUpdateException => println(e.getNextException())
        }
      }
    }
  }
}
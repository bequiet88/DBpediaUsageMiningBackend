package de.unimannheim.dws.controller

import scala.slick.driver.PostgresDriver.simple.queryToInsertInvoker

import com.hp.hpl.jena.query.Query
import com.hp.hpl.jena.query.QueryFactory
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.commons.conversions.scala.RegisterConversionHelpers
import com.mongodb.casbah.commons.conversions.scala.RegisterJodaTimeConversionHelpers

import de.unimannheim.dws.models.mongo.CommonLogFileDAO
import de.unimannheim.dws.models.postgre.DbConn
import de.unimannheim.dws.models.postgre.Tables._
import de.unimannheim.dws.preprocessing.ArqTripleExtractor
import de.unimannheim.dws.preprocessing.ManualTripleExtractor
import de.unimannheim.dws.preprocessing.Util

// http://notes.3kbo.com/scala
// http://joernhees.de/blog/2010/10/31/setting-up-a-local-dbpedia-mirror-with-virtuoso/

/*
 * Jena ARQ parser cannot parse all Virtuoso SPARQL queries correctly!!! 
 */

object TripleExtractorController extends App {
  DbConn.openConn withSession { implicit session =>
    /*
   * Converters for Joda Time
   */
    RegisterConversionHelpers()
    RegisterJodaTimeConversionHelpers()

    /*
   * Determine length of log files
   */
    val stepSize = 1000
    val logLength = CommonLogFileDAO.find(ref = MongoDBObject("httpStatus" -> "200")).count
    val upperLimit = (Util.round(logLength / stepSize.asInstanceOf[Double], 0) + 1).asInstanceOf[Int]

    /*
   * Invoke method to create SparqlQueries and SimpleTriples on PostgreSQL
   */
    createQueriesTriples(upperLimit, stepSize)

  }

  def createQueriesTriples(upperLimit: Int, stepSize: Int)(implicit session: slick.driver.PostgresDriver.backend.Session) = {

    for {
      i <- (0 to upperLimit)

    } yield {

      val skip = i * stepSize
      val rawCLFs = CommonLogFileDAO.find(ref = MongoDBObject("httpStatus" -> "200"))
        .sort(orderBy = MongoDBObject("_id" -> -1)) // sort by _id desc
        .limit(stepSize)
        .skip(skip)
        .toList

      val queriesTriples = for (log <- rawCLFs) yield {
        /*
     * Get the query string from the log entry
     */
        val queryString = log.request.get("query") match {
          case Some(query) => query
          case _ => ""
        }

        try {
          /*
       * Try to create valid SPARQL query
       */
          val query: Query = QueryFactory.create(queryString)

          //      val id = SparqlQueryDAO.insert(SparqlQuery(query = queryString, containsErrors = false))

          val sparqlQuery = SparqlQueriesRow(id = 0L, query = Some(queryString), containsErrors = Some("false"), sessionId = None)

          val newId = (SparqlQueries returning SparqlQueries.map(_.id)) += sparqlQuery

          val seqOfTriples = ArqTripleExtractor.extract(query, newId)

          seqOfTriples

        } catch {
          case e: Exception => {

            val seqOfTriples = ManualTripleExtractor.extract(queryString)

            /*
           * If query contains errors, the seqOfTriples is empty
           */
            if (seqOfTriples.size == 0) {
              val sparqlQuery = SparqlQueriesRow(id = 0L, query = Some(queryString), containsErrors = Some("true"), sessionId = None)
              val newId = (SparqlQueries returning SparqlQueries.map(_.id)) += sparqlQuery
              List()
            } else {
              /*
           * If not, insert manually parsed triples
           */
              val sparqlQuery = SparqlQueriesRow(id = 0L, query = Some(queryString), containsErrors = Some("false"), sessionId = None)
              val newId = (SparqlQueries returning SparqlQueries.map(_.id)) += sparqlQuery
              //        SparqlQueryDAO.insert(SparqlQuery(query = queryString, containsErrors = true))
              seqOfTriples.map(triple => triple.copy(queryId = Some(newId)))
            }

          }
        }

      }

      /*
   * Insert batch of triples into persistent store
   */
      val triples = queriesTriples.flatten
      SimpleTriples.insertAll(triples: _*)

      println("inserted " + skip + " queries, this batch had " + triples.size + " triples.")
      //  SimpleTripleDAO.insert(triples)
    }
  }
}
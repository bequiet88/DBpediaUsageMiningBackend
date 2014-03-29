package de.unimannheim.dws.controller

import org.bson.types.ObjectId
import com.hp.hpl.jena.query.Query
import com.hp.hpl.jena.query.QueryFactory
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.commons.conversions.scala.RegisterConversionHelpers
import com.mongodb.casbah.commons.conversions.scala.RegisterJodaTimeConversionHelpers
import de.unimannheim.dws.models.mongo.CommonLogFile
import de.unimannheim.dws.models.mongo.CommonLogFileDAO
import de.unimannheim.dws.models.mongo.SimpleTripleDAO
import de.unimannheim.dws.models.mongo.SparqlQuery
import de.unimannheim.dws.models.mongo.SparqlQueryDAO
import de.unimannheim.dws.models.postgre.Tables._
import de.unimannheim.dws.models.postgre.DbConn
import de.unimannheim.dws.preprocessing.ArqTripleExtractor
import de.unimannheim.dws.preprocessing.ManualTripleExtractor
import com.hp.hpl.jena.query.Syntax
import scala.slick.driver.PostgresDriver.simple._
import de.unimannheim.dws.models.postgre.DbConn

// http://notes.3kbo.com/scala
// http://joernhees.de/blog/2010/10/31/setting-up-a-local-dbpedia-mirror-with-virtuoso/

/*
 * Jena ARQ parser cannot parse all Virtuoso SPARQL queries correctly!!! 
 */

object TripleExtractorController extends App {

  /*
   * Converters for Joda Time
   */
  RegisterConversionHelpers()
  RegisterJodaTimeConversionHelpers()

  for {
    i <- (0 to 310)

  } yield {

     val skip = i * 1000
     val rawCLFs = CommonLogFileDAO.find(ref = MongoDBObject("httpStatus" -> "200"))
      .sort(orderBy = MongoDBObject("_id" -> -1)) // sort by _id desc
      .limit(1000)
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

        DbConn.openConn withSession { implicit session =>

          /*
       * Try to create valid SPARQL query
       */
          val query: Query = QueryFactory.create(queryString)

          //      val id = SparqlQueryDAO.insert(SparqlQuery(query = queryString, containsErrors = false))

          val sparqlQuery = SparqlQueriesRow(id = 0L, query = Some(queryString), containsErrors = Some("false"), sessionId = None)

          val newId = (SparqlQueries returning SparqlQueries.map(_.id)) += sparqlQuery

          val seqOfTriples = ArqTripleExtractor.extract(query, newId)

          seqOfTriples
        }
      } catch {
        case e: Exception => {
          DbConn.openConn withSession { implicit session =>

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

    }

    /*
   * Insert batch of triples into persistent store
   */
    val triples = queriesTriples.flatten
    DbConn.openConn withSession { implicit session =>
      SimpleTriples.insertAll(triples: _*)
    }
    
    println("inserted "+skip+" queries, this batch had "+ triples.size +" triples.")
    //  SimpleTripleDAO.insert(triples)
  }
}
package de.unimannheim.dws.controller

import de.unimannheim.dws.models.mongo.CommonLogFile
import org.bson.types.ObjectId
import org.joda.time.DateTime
import de.unimannheim.dws.models.mongo.CommonLogFileDAO
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.commons.conversions.scala.{ RegisterConversionHelpers, RegisterJodaTimeConversionHelpers }
import de.unimannheim.dws.preprocessing.LogFileParser
import scala.util.matching.Regex
import java.io.File
import java.io.FileInputStream
import de.unimannheim.dws.preprocessing.TripleExtractor
import de.unimannheim.dws.models.mongo.SimpleTripleDAO
import com.hp.hpl.jena.query.QueryFactory
import com.hp.hpl.jena.query.QueryParseException
import com.hp.hpl.jena.query.Query
import de.unimannheim.dws.models.mongo.SparqlQuery
import de.unimannheim.dws.models.mongo.SparqlQueryDAO

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

  val rawCLFs = CommonLogFileDAO.find(ref = MongoDBObject("httpStatus" -> "200"))
    .sort(orderBy = MongoDBObject("_id" -> -1)) // sort by _id desc
    .toList

  //  rawCLFs.map(o => println(o.toString))

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

      val sparqlQuery = SparqlQuery(query = queryString, containsErrors = false)
      
      val seqOfTriples = TripleExtractor.extract(query, sparqlQuery._id)

      (sparqlQuery, seqOfTriples)

    } catch {
      case e: Exception => {
//        SparqlQueryDAO.insert(SparqlQuery(query = queryString, containsErrors = true))
        (SparqlQuery(query = queryString, containsErrors = true), Seq())
      }
    }

  }
  
  /*
   * Insert queries and triples into doc store
   */
  val queries = queriesTriples.map(_._1)  
  SparqlQueryDAO.insert(queries)
  
  val triples = queriesTriples.map(_._2).flatten
  SimpleTripleDAO.insert(triples)
  
  
}
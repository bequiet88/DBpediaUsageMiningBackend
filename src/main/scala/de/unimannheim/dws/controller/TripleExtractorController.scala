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
import de.unimannheim.dws.preprocessing.ArqTripleExtractor
import de.unimannheim.dws.preprocessing.ManualTripleExtractor

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
      
      val seqOfTriples = ArqTripleExtractor.extract(query, sparqlQuery._id)

      (sparqlQuery, seqOfTriples)

    } catch {
      case e: Exception => {

        
       val sparqlQuery = SparqlQuery(query = queryString)
       
       val seqOfTriples = ManualTripleExtractor.extract(queryString)
       
       if(seqOfTriples.size == 0) {
         (sparqlQuery, seqOfTriples)
       }
       else {         
//        SparqlQueryDAO.insert(SparqlQuery(query = queryString, containsErrors = true))
        (sparqlQuery.copy(containsErrors = false), seqOfTriples)
       }
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
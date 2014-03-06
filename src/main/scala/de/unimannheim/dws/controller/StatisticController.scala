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

object StatisticController extends App {

  /*
   * Converters for Joda Time
   */
  RegisterConversionHelpers()
  RegisterJodaTimeConversionHelpers()

  val rawCLFs = CommonLogFileDAO.find(ref = MongoDBObject("httpStatus" -> "200"))
    .sort(orderBy = MongoDBObject("_id" -> -1)) // sort by _id desc
//    .skip(1)
//    .limit(613)
    .toList
 
  val formats = for {
    log <- rawCLFs
    format = log.request.get("format") match {
      case Some(format) => format
      case _ => ""
    }
  } yield format
    
  val formatDistribution = formats.groupBy(l => l).map(t => (t._1, t._2.length))
    
  println(formatDistribution.toString)
    

}
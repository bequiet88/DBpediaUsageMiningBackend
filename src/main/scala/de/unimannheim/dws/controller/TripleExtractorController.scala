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

object TripleExtractorController extends App {

  /*
   * Converters for Joda Time
   */
  RegisterConversionHelpers()
  RegisterJodaTimeConversionHelpers()

  val rawCLFs = CommonLogFileDAO.find(ref = MongoDBObject("httpStatus" -> "200"))
    .sort(orderBy = MongoDBObject("_id" -> -1)) // sort by _id desc
    .limit(5)
    .toList

  //  rawCLFs.map(o => println(o.toString))

  for (log <- rawCLFs) {
    val listOfTriples = TripleExtractor.extract(log)
    SimpleTripleDAO.insert(listOfTriples)
  }
}
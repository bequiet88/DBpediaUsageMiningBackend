package de.unimannheim.dws.controller

import de.unimannheim.dws.models.mongo.CommonLogFile
import org.bson.types.ObjectId
import org.joda.time.DateTime
import de.unimannheim.dws.models.mongo.CommonLogFileDAO
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.commons.conversions.scala.{RegisterConversionHelpers, RegisterJodaTimeConversionHelpers}
import de.unimannheim.dws.preprocessing.LogFileParser
import scala.util.matching.Regex
import java.io.File
import java.io.FileInputStream

object LogFileController extends App {
  
  /*
   * Converters for Joda Time
   */
  RegisterConversionHelpers() 
  RegisterJodaTimeConversionHelpers()
  
  val path = "D:/Download/Uni_Projekte/Master_Thesis/usewod2014-dataset/USEWOD2014/data/DBpedia/dbpedia3.8/http07082013.log/http07082013.log"
    
  val source = LogFileParser.readFile(path)
  
  
  /*
   * Structure of Log Case Class as Regex
   */
  val format = List(new Regex("([a-z0-9.]+) "), // remote_host, dotted quad or resolved
		  			new Regex("([a-z0-9_-]+) "), // rfc921 usually -
		  			new Regex("([a-z0-9_-]+) "), // username if identified, else -
		  			new Regex("\\[(\\d{1,2}/\\w{3}/\\d{4})"), // date part
		  			new Regex("(\\d{1,2}:\\d{1,2}:\\d{1,2}) "), // time part
		  			new Regex("(\\+\\d{4})\\] "), // timezone part
		  			new Regex("\"([A-Z]{3,5}) "), // request method
		  			new Regex("(/[^ ]+) "), // request uri
		  			new Regex("([A-Z]+/\\d\\.\\d)\" "), // request protocol
		  			new Regex("(\\d+) ")) // response status code
  
  
  for (line <- source.getLines()){
    val o = LogFileParser.parse(format, line)
    CommonLogFileDAO.insert(o)
  }
  
  
//  val o_* = CommonLogFileDAO.find(ref = MongoDBObject("ip" -> "00cf8964d7c730b8e6e4cf2efe5d0d11"))
//        .sort(orderBy = MongoDBObject("_id" -> -1)) // sort by _id desc
//        .skip(1)
//        .limit(1)
//        .toList
//        
//  o_*.map(o => println(o.toString))      
}
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
import de.unimannheim.dws.models.mongo.SimpleTripleDAO
import com.hp.hpl.jena.query.QueryFactory
import com.hp.hpl.jena.query.QueryParseException
import com.hp.hpl.jena.query.Query
import de.unimannheim.dws.models.mongo.SparqlQuery
import de.unimannheim.dws.models.mongo.SparqlQueryDAO
import org.apache.commons.csv.CSVPrinter
import java.io.BufferedWriter
import java.io.FileWriter
import org.apache.commons.csv.CSVFormat
import org.joda.time.format.DateTimeFormat

// http://notes.3kbo.com/scala

object StatisticController extends App {

  // generate CSVPrinter Object
  val dateTimeFormatter = DateTimeFormat.forPattern("ddMMyy_kkmmss");
  val path = "D:/ownCloud/Data/Studium/Master_Thesis/04_Data_Results/statistics/statistics_" + DateTime.now().toString(dateTimeFormatter) + ".csv"
  implicit val writer: BufferedWriter = new BufferedWriter(new FileWriter(
    new File(path)));
  implicit val csvPrinter: CSVPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
  csvPrinter.print("sep=,")
  csvPrinter.println()

  /*
   * Converters for Joda Time
   */
  RegisterConversionHelpers()
  RegisterJodaTimeConversionHelpers()

  /*
   * Data Set Size Statistics
   */
  val rawCLFs = CommonLogFileDAO.find(ref = MongoDBObject("httpStatus" -> "200"))
    .sort(orderBy = MongoDBObject("_id" -> -1)) // sort by _id desc
    //    .skip(1)
    //    .limit(613)
    .toList

  writeOutputToFile("", List(("Data Set Size", "", ""), ("" + rawCLFs.size, "", "")))

  /*
   * Content Type Statistics
   * http://wiki.opensemanticframework.org/index.php/SPARQL#Content_Returned
   */
  val formats = for {
    log <- rawCLFs
    format = log.request.get("format") match {
      case Some(format) => format
      case _ => ""
    }
  } yield format

  val formatDistribution = formats.groupBy(l => l).map(t => (t._1, t._2.length))
    .toList.sortBy({ _._2 }).map(f => (f._1, "" + f._2, "" + BigDecimal(f._2.toFloat / formats.size).setScale(2, BigDecimal.RoundingMode.HALF_UP))).reverse

  writeOutputToFile("Distribtion of Content Type of Access Log Entries", formatDistribution.+:(("Content Type", "Abs. Number", "Rel. Number")).slice(0, 11))

  /*
   * SPARQL Query-type break down
   */
  val successQueries = SparqlQueryDAO.find(ref = MongoDBObject("containsErrors" -> false))
    //    .sort(orderBy = MongoDBObject("_id" -> -1)) // sort by _id desc
    //    .skip(1)
    //    .limit(613)
    .toList

  val selectQueries = successQueries.filter(q => q.query.contains("SELECT"))
  val describeQueries = successQueries.filter(q => q.query.contains("DESCRIBE")).size
  val askQueries = successQueries.filter(q => q.query.contains("ASK")).size
  val constructQueries = successQueries.filter(q => q.query.contains("CONSTRUCT")).size
  val errorQueries = SparqlQueryDAO.find(ref = MongoDBObject("containsErrors" -> true)).count

  val queryBreakDown = List(
    ("Type", "Abs. Number", "Rel. Number"),
    ("SELECT", "" + selectQueries.size, "" + BigDecimal(selectQueries.size.toFloat / (errorQueries + successQueries.size)).setScale(2, BigDecimal.RoundingMode.HALF_UP)),
    ("DESCRIBE", "" + describeQueries, "" + BigDecimal(describeQueries.toFloat / (errorQueries + successQueries.size)).setScale(2, BigDecimal.RoundingMode.HALF_UP)),
    ("ASK", "" + askQueries, "" + BigDecimal(askQueries.toFloat / (errorQueries + successQueries.size)).setScale(2, BigDecimal.RoundingMode.HALF_UP) + "%"),
    ("error", "" + errorQueries, "" + BigDecimal(errorQueries.toFloat / (errorQueries + successQueries.size)).setScale(2, BigDecimal.RoundingMode.HALF_UP)))

  writeOutputToFile("SPARQL query break down", queryBreakDown)

  /*
   * SELECT queries with N triple pattern
   */

  val simpleTriples = //try {
    SimpleTripleDAO.find(ref = MongoDBObject()).toList
  //  } catch {
  //    case e: Exception => List()
  //  }

  val queryTripleOccurrences = for {
    query <- selectQueries
    triples = {
      simpleTriples.filter(_.queryId == query._id)
    }
    n = triples.size
  } yield (query, triples, n)

  val noOfTriples = queryTripleOccurrences.map(_._3)

  val tripleDistribution = noOfTriples.groupBy(l => l).map(t => (t._1, t._2.length))
    .toList.sortBy({ _._2 }).map(f => ("" + f._1, "" + f._2, "" + BigDecimal(f._2.toFloat / noOfTriples.size).setScale(2, BigDecimal.RoundingMode.HALF_UP))).reverse

  writeOutputToFile("SELECT queries with N triple pattern", tripleDistribution.+:(("N", "Abs. Number", "Rel. Number")).slice(0, 11))

  /*
   * Main Query-pattern types (1-pattern queries only)
   */
  val onePatternTriples = queryTripleOccurrences.filter(q => q._3 == 1).map(q => q._2)

  val patternTypes = for {
    query <- onePatternTriples.flatten
  } yield "(" + query.sub_type + "," + query.pred_type + "," + query.obj_type + ")"

  val patternDistribution = patternTypes.groupBy(l => l).map(t => (t._1, t._2.length))
    .toList.sortBy({ _._2 }).map(f => ("" + f._1, "" + f._2, "" + BigDecimal(f._2.toFloat / patternTypes.size).setScale(2, BigDecimal.RoundingMode.HALF_UP))).reverse

  writeOutputToFile("Main query pattern types", patternDistribution.+:(("Pattern", "Abs. Number", "Rel. Number")).slice(0, 11))

  /*
   * Predicates used in 1-pattern queries
   */
  val predicateTypes = for {
    query <- onePatternTriples.flatten
    cleanedquery = query if query.pred_type != "var" && query.pred_type != "blank" && query.pred_type != "-"
  } yield "<" + cleanedquery.pred_pref + cleanedquery.pred_prop + ">"

  val predicateDistribution = predicateTypes.groupBy(l => l).map(t => (t._1, t._2.length))
    .toList.sortBy({ _._2 }).map(f => ("" + f._1, "" + f._2, "" + BigDecimal(f._2.toFloat / patternTypes.size).setScale(2, BigDecimal.RoundingMode.HALF_UP))).reverse

  writeOutputToFile("Predicates used in 1-pattern queries", predicateDistribution.+:(("Predicate", "Abs. Number", "Rel. Number")).slice(0, 11))

  /*  
   *  Anonymous function to write data to the open csv file
   */
  def writeOutputToFile(title: String, data: List[(String, String, String)])(implicit writer: BufferedWriter, csvPrinter: CSVPrinter) = {
    csvPrinter.println
    csvPrinter.print(title)
    csvPrinter.println
    /*
	   * Row 1
	   */
    for (stat <- data) {
      csvPrinter.print(stat._1)
    }
    csvPrinter.println

    /*
	   * Row 2
	   */
    for (stat <- data) {
      csvPrinter.print(stat._2)
    }
    csvPrinter.println

    /*
	   * Row 3
	   */
    for (stat <- data) {
      csvPrinter.print(stat._3)
    }
    csvPrinter.println
    csvPrinter.flush

  }

  //		// generate CSVPrinter Object
  //		// FileOutputStream csvBAOS = new FileOutputStream(new File(path));
  //		BufferedWriter writer = new BufferedWriter(new FileWriter(
  //				new File(path)));
  //		// OutputStreamWriter csvWriter = new OutputStreamWriter(csvBAOS);
  //		CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
  //
  //		csvPrinter.print("sep=,");
  //		csvPrinter.println();
  //
  //		for (String key:data.keySet()) {
  //			csvPrinter.print(key);
  //			csvPrinter.print(data.get(key));
  //			csvPrinter.println();
  //		}
  //
  //		
  //		
  csvPrinter.close
}
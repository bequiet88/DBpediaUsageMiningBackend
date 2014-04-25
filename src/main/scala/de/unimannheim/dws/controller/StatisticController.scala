package de.unimannheim.dws.controller

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import com.hp.hpl.jena.query.Query
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.commons.conversions.scala.RegisterConversionHelpers
import com.mongodb.casbah.commons.conversions.scala.RegisterJodaTimeConversionHelpers

import de.unimannheim.dws.models.mongo.CommonLogFile
import de.unimannheim.dws.models.mongo.CommonLogFileDAO
import de.unimannheim.dws.models.mongo.SimpleTripleDAO
import de.unimannheim.dws.models.mongo.SparqlQueryDAO
import de.unimannheim.dws.models.postgre.DbConn
import de.unimannheim.dws.models.postgre.Tables._
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.driver.JdbcDriver.backend.Database
import scala.slick.jdbc.{ GetResult, StaticQuery => Q }

// http://notes.3kbo.com/scala

object StatisticController extends App {

  DbConn.openConn withSession { implicit session =>

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
    println("Data Set Statistics");
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
    println("Content Type Statistics");
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
    println("SPARQL Query-type break down");
    val successQueriesQ = SparqlQueries.filter(q => q.containsErrors === "false").length
    val successQueries = successQueriesQ.run
    //      SparqlQueryDAO.find(ref = MongoDBObject("containsErrors" -> false))
    //      //    .sort(orderBy = MongoDBObject("_id" -> -1)) // sort by _id desc
    //      //    .skip(1)
    //      //    .limit(613)
    //      .toList

    val selectQueriesQ = SparqlQueries.filter(q => (q.containsErrors === "false") && (q.query like ("%SELECT%"))).length
    val selectQueries = selectQueriesQ.run
    val describeQueries = SparqlQueries.filter(q => (q.containsErrors === "false") && (q.query like ("%DESCRIBE%"))).length.run
    val askQueries = SparqlQueries.filter(q => (q.containsErrors === "false") && (q.query like ("%ASK%"))).length.run
    val constructQueries = SparqlQueries.filter(q => (q.containsErrors === "false") && (q.query like ("%CONSTRUCT%"))).length.run
    val errorQueries = SparqlQueries.filter(q => (q.containsErrors === "true")).length.run

    val queryBreakDown = List(
      ("Type", "Abs. Number", "Rel. Number"),
      ("SELECT", "" + selectQueries, "" + BigDecimal(selectQueries.toFloat / (errorQueries + successQueries)).setScale(2, BigDecimal.RoundingMode.HALF_UP)),
      ("DESCRIBE", "" + describeQueries, "" + BigDecimal(describeQueries.toFloat / (errorQueries + successQueries)).setScale(2, BigDecimal.RoundingMode.HALF_UP)),
      ("CONSTRUCT", "" + constructQueries, "" + BigDecimal(constructQueries.toFloat / (errorQueries + successQueries)).setScale(2, BigDecimal.RoundingMode.HALF_UP)),
      ("ASK", "" + askQueries, "" + BigDecimal(askQueries.toFloat / (errorQueries + successQueries)).setScale(2, BigDecimal.RoundingMode.HALF_UP)),
      ("error", "" + errorQueries, "" + BigDecimal(errorQueries.toFloat / (errorQueries + successQueries)).setScale(2, BigDecimal.RoundingMode.HALF_UP)))

    writeOutputToFile("SPARQL query break down", queryBreakDown)

    /*
   * SELECT queries with N triple pattern
   */
    println("SELECT queries with N triple pattern");
        val simpleTriples = (for {
          q <- SparqlQueries if q.query like ("%SELECT%")
          t <- SimpleTriples if t.queryId === q.id
        } yield (q, t)).groupBy(_._1.id)
    
        val queryTripleOccurrences = simpleTriples.map {
          case (queryId, triples) =>
            (queryId, triples.length)
        }.list

//    val queryTripleOccurrencesQ = (for {
//      q <- SparqlQueries if q.query like ("%SELECT%") // inSetBind selectQueries.map(_.id)
//      t <- SimpleTriples if t.queryId === q.id
//    } yield (q.id, t))
//
//    println(queryTripleOccurrencesQ.selectStatement);
//
//    val queryTripleOccurrences = queryTripleOccurrencesQ.foldLeft(Map[(Long), (List[SimpleTriplesRow], Int)]())((i, row) => {
//
//      if (i.contains(row._1)) {
//        i + (((row._1), (i(row._1)._1 :+ row._2, i(row._1)._2 + 1)))
//      } else {
//        i + (((row._1), (List(row._2), 1)))
//      }
//    })

    //    val queryTripleOccurrences = {
    //      val groupedTriples = simpleTriples.groupBy(_._1).toList
    //      groupedTriples.map(gp => {
    //        (gp._1, gp._2.map(_._2), gp._2.size)
    //      })
    //    }

    //      
    //      for {
    //      // zipwithIndex returns no of current iteration
    //      (query, index) <- selectQueries.zipWithIndex
    //      triples = {
    //        simpleTriples.filter(_.queryId == query._id)
    //      }
    //      n = triples.size
    //      printer = println(index)
    //    } yield {
    //
    //      (query, triples, n)
    //    }

    val noOfTriples = queryTripleOccurrences.map(_._2)

    val tripleDistribution = noOfTriples.groupBy(l => l).map(t => (t._1, t._2.length))
      .toList.sortBy({ _._2 }).map(f => ("" + f._1, "" + f._2, "" + BigDecimal(f._2.toFloat / noOfTriples.size).setScale(2, BigDecimal.RoundingMode.HALF_UP))).reverse

    writeOutputToFile("SELECT queries with N triple patterns", tripleDistribution.+:(("N", "Abs. Number", "Rel. Number")).slice(0, 11))

    /*
   * Main Triple-pattern types
   */
    println("Main Triple-pattern types");
        
    val queryTripleTypes =  Q.queryNA[(String, String, String)]("select s57.subj_type, s57.pred_type, s57.obj_type from sparql_queries s55, simple_triples s57 where (s55.query like '%SELECT%') and s57.query_id = s55.id").list   
//    val allPatternTriples =  (for {
//      (key, value) <- queryTripleOccurrences
//    } yield { value._1 }).asInstanceOf[List[List[SimpleTriplesRow]]]
//
//    val patternTypes = (for {
//      triple <- allPatternTriples.flatten
//    } yield "(" + triple.subjType.get + "," + triple.predType.get + "," + triple.objType.get + ")")

    val patternDistribution = queryTripleTypes.groupBy(l => l).map(t => (t._1, t._2.length))
      .toList.sortBy({ _._2 }).map(f => ("" + f._1, "" + f._2, "" + BigDecimal(f._2.toFloat / queryTripleTypes.size).setScale(2, BigDecimal.RoundingMode.HALF_UP))).reverse

    writeOutputToFile("Main triple pattern types", patternDistribution.+:(("Pattern", "Abs. Number", "Rel. Number")).slice(0, 11))

    /*
   * Predicates used in 1-pattern queries
   */
    //    println("Predicates used in 1-pattern queries");
    //    val predicateTypes = for {
    //      query <- onePatternTriples.flatten
    //      cleanedquery = query if query.pred_type != "var" && query.pred_type != "blank" && query.pred_type != "-"
    //    } yield "<" + cleanedquery.pred_pref + cleanedquery.pred_prop + ">"
    //
    //    val predicateDistribution = predicateTypes.groupBy(l => l).map(t => (t._1, t._2.length))
    //      .toList.sortBy({ _._2 }).map(f => ("" + f._1, "" + f._2, "" + BigDecimal(f._2.toFloat / patternTypes.size).setScale(2, BigDecimal.RoundingMode.HALF_UP))).reverse
    //
    //    writeOutputToFile("Predicates used in 1-pattern queries", predicateDistribution.+:(("Predicate", "Abs. Number", "Rel. Number")).slice(0, 11))

    csvPrinter.close
  }

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
    println("Wrote to file.");
  }

}
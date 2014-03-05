package de.unimannheim.dws.preprocessing

import scala.io.BufferedSource
import scala.io.Source
import de.unimannheim.dws.models.mongo.CommonLogFile
import scala.util.matching.Regex
import org.joda.time.DateTime
import java.io.File

// http://www.tutorialspoint.com/scala/scala_regular_expressions.htm
// http://sujitpal.blogspot.de/2009/06/some-access-log-parsers.html

object LogFileParser {
  def readFile(path: String): BufferedSource = {
    Source.fromFile(new File(path))
  }

  def parse(format: List[Regex], line: String): CommonLogFile = {

    val result = parseRec(format, List(), line)

    CommonLogFile(ip = result(0).trim(),
      rfc921 = result(1).trim(),
      username = result(2).trim(),
      date = result(3).trim(),
      time = result(4).trim(),
      timezone = result(5).trim(),
      requestMethod = result(6).trim(),
      requestUrl = result(7).trim(),
      requestProtocol = result(8).trim(),
      httpStatus = result(9).trim()) //"2d0f7e6a4bd6ceebebddfa06b0d1da63", userIdentifier = "-", timestamp = DateTime.now, request = "GET /sparql?default-graph-uri=http%3A%2F%2Fdbpedia.org&query=SELECT+%3Fg+COUNT%28*%29+%7B+GRAPH+%3Fg+%7B%3Fs+%3Fp+%3Fo.%7D+%7D+GROUP+BY+%3Fg+ORDER+BY+DESC+2%3B%0D%0A&format=text%2Fhtml&timeout=30000&debug=on HTTP/1.1", httpStatus = "200", size = "0", referrer = "-", agent = "-")
  }

  def parseRec(format: List[Regex], result: List[String], line: String): List[String] = {

    format match {
      case regex :: tail => {
        regex.findFirstMatchIn(line) match {
          case Some(res) => {
            parseRec(tail, result :+ res.group(1), regex replaceFirstIn (line, ""))
          }
          case None => parseRec(tail, result :+ "", line)
        }
      }
      case Nil => {
        result
        //        regex.findFirstIn(line) match {
        //          case Some(res) =>  {
        //            result:+res
        //          }
        //          case None => result
        //        }        
      }
    }
  }
}
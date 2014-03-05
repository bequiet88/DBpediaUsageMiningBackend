package de.unimannheim.dws.preprocessing

import scala.io.BufferedSource

import de.unimannheim.dws.models.mongo.CommonLogFile
import de.unimannheim.dws.models.mongo.CommonLogFile
import de.unimannheim.dws.models.mongo.SimpleTriple

object TripleExtractor {
  def extract(log: CommonLogFile): List[SimpleTriple] = {

    val query = log.request.get("query") match {
      case Some(query) => query
      case _ => ""
    }

    /*
     * Generate the map holding all prefix - URL pairs
     */
    val prefixMap: Map[String, String] = query.length() match {
      case 0 => Map()
      case _ => try {
        generatePrefixMap(query.split("SELECT")(0))
      } catch {
        case aioob: ArrayIndexOutOfBoundsException => Map()
      }
    }

    /*
     * Generate a list of all triples in the query
     */
    val extractedRawTriples: List[(String, String, String)] = query.length() match {
      case 0 => List()
      case _ => try {
        findAllTriples(query.split("SELECT")(1))
      } catch {
        case aioob: ArrayIndexOutOfBoundsException => List()
      }
    }

    ???
  }

  def generatePrefixMap(prefixes: String): Map[String, String] = {

    // Sample Prefix: PREFIX owl: <http://www.w3.org/2002/07/owl#>\n

    val prefixElems = prefixes.split("\\n").map(prefix => {
      val withoutReturn = prefix.replaceAll("\\r", "")
      val withoutPREFIX = withoutReturn.replaceAll("PREFIX", "")
      val pair = withoutPREFIX.split(":", 2)
      pair.length match {
        case 2 => (pair(0).trim(), pair(1).trim())
        case _ => ("", "")
      }
    }).toList

    Map(prefixElems: _*)
  }

  def findAllTriples(actualQuery: String): List[(String, String, String)] = {

    /*
     *  ?subject ?lat ?long WHERE {\r\n?subject <http://purl.org/dc/terms/subject> <http://dbpedia.org/resource/Category:Football_venues_in_Portugal>.\r\n
     *  ?subject geo:lat ?lat.
     *  \r\n?subject geo:long ?long.\r\n} LIMIT 20
     */

    ???
  }

  def curlyBracesBalancer(actualQuery: String): List[String] = {

    def isBalanced(text: List[Char], stack: List[Int], res: List[String]): List[String] =
      if (text.isEmpty) res
      else if (text.head == '{') isBalanced(text.tail, stack :+ actualQuery.indexOf('{'), res)
      else if (text.head == '{' && !stack.isEmpty) isBalanced(text.tail, stack :+ actualQuery.indexOf("{", stack.last), res)
      else if (text.head == '}' && !stack.isEmpty) isBalanced(text.tail, stack.slice(0, stack.size-1), res :+ actualQuery.substring(stack.last+1, actualQuery.indexOf("}", stack.last)))
      else isBalanced(text.tail, stack, res)

    isBalanced(actualQuery.toList, List(), List())
  }

}
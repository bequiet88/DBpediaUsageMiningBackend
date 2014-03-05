package de.unimannheim.dws.preprocessing

import scala.io.BufferedSource
import de.unimannheim.dws.models.mongo.CommonLogFile
import de.unimannheim.dws.models.mongo.CommonLogFile
import de.unimannheim.dws.models.mongo.SimpleTriple
import com.hp.hpl.jena.query.Query
import com.hp.hpl.jena.query.QueryFactory

object TripleExtractor {
  def extract(log: CommonLogFile): List[SimpleTriple] = {

    val queryString = log.request.get("query") match {
      case Some(query) => query
      case _ => ""
    }
    
    val query: Query = QueryFactory.create(queryString);
    

    /*
     * Generate the map holding all prefix - URL pairs
     */
    val prefixMap: Map[String, String] = queryString.length() match {
      case 0 => Map()
      case _ => try {
        generatePrefixMap(queryString.split("SELECT")(0))
      } catch {
        case aioob: ArrayIndexOutOfBoundsException => Map()
      }
    }

    /*
     * Generate a list of all triples in the query
     */
    val extractedRawTriples: List[(String, String, String)] = queryString.length() match {
      case 0 => List()
      case _ => try {
        findAllTriples(queryString.split("SELECT")(1))
      } catch {
        case aioob: ArrayIndexOutOfBoundsException => List()
      }
    }

    List()
  }

  def generatePrefixMap(prefixes: String): Map[String, String] = {

    // Sample Prefix: PREFIX owl: <http://www.w3.org/2002/07/owl#>\n

    val prefixElems = prefixes.split("PREFIX").map(prefix => {
      val withoutReturn = prefix.replaceAll("\\r", "")
      val withoutBreak = withoutReturn.replaceAll("\\n", "")
      val pair = withoutBreak.split(":", 2)
      if(pair.length==2) (pair(0).trim(), pair(1).trim())    
      else ("","")
//      pair.length match {
//        case 2 => (pair(0).trim(), pair(1).trim())
//        case _ => DoNothing
//      }
    }).toList

    Map(prefixElems: _*)
  }

  def findAllTriples(actualQuery: String): List[(String, String, String)] = {

    /*
     *  ?subject ?lat ?long WHERE {\r\n?subject <http://purl.org/dc/terms/subject> <http://dbpedia.org/resource/Category:Football_venues_in_Portugal>.\r\n
     *  ?subject geo:lat ?lat.
     *  \r\n?subject geo:long ?long.\r\n} LIMIT 20
     */
    
    val curlyBracesBlocks = curlyBracesBalancer(actualQuery)

    List()
  }

  def curlyBracesBalancer(actualQuery: String): List[String] = {

    def isBalanced(text: List[Char], stack: List[Int], res: List[String], lastAdded: Int): List[String] =
      if (text.isEmpty) res
//      else if (text.head == '{' && !stack.isEmpty && lastAdded == 0) {
//        isBalanced(text.tail, stack :+ actualQuery.indexOf('{', stack.last+1), res, lastAdded)
//      }    
      else if (text.head == '{' && !stack.isEmpty) {
        isBalanced(text.tail, stack :+ actualQuery.indexOf('{', lastAdded), res, actualQuery.indexOf('{', lastAdded)+1)
      }    
      else if (text.head == '{') {
        isBalanced(text.tail, stack :+ actualQuery.indexOf('{'), res, actualQuery.indexOf('{')+1)
      }
      else if (text.head == '}' && !stack.isEmpty) {
        isBalanced(text.tail, stack.slice(0, stack.size-1), res :+ actualQuery.substring(stack.last+1, actualQuery.indexOf('}', lastAdded)+1), lastAdded)
      }
      else isBalanced(text.tail, stack, res, lastAdded)

    isBalanced(actualQuery.toList, List(), List(), 0)
  }

}
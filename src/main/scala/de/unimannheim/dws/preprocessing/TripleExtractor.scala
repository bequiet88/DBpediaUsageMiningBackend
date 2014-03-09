package de.unimannheim.dws.preprocessing

import scala.io.BufferedSource
import de.unimannheim.dws.models.mongo.CommonLogFile
import de.unimannheim.dws.models.mongo.CommonLogFile
import de.unimannheim.dws.models.mongo.SimpleTriple
import com.hp.hpl.jena.query.Query
import com.hp.hpl.jena.query.QueryFactory
import com.hp.hpl.jena.query.QueryParseException
import com.hp.hpl.jena.sparql.syntax.Element
import com.hp.hpl.jena.sparql.syntax.ElementGroup
import scala.collection.JavaConverters._
import com.hp.hpl.jena.sparql.core.TriplePath
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock
import com.hp.hpl.jena.sparql.syntax.ElementUnion
import de.unimannheim.dws.models.mongo.SimpleTriple
import com.hp.hpl.jena.graph.Node
import org.bson.types.ObjectId

// http://jena.apache.org/documentation/javadoc/jena/index.html
// http://jena.apache.org/documentation/javadoc/arq/index.html

object TripleExtractor {
  def extract(query: Query, id: ObjectId): Seq[SimpleTriple] = {

    /*
     * Anonymous method to generate information from a node
     */
    def getNodeInfo(node: Node) = {

      if (node.isURI() && node.getNameSpace() != null && node.getLocalName() != null) {
        (node.getNameSpace(), node.getLocalName(), "uri")
      } else if (node.isLiteral() && node.getLiteralLexicalForm() != null && node.getLiteralLanguage()!= null) {
        ("-", node.getLiteralLexicalForm() + node.getLiteralLanguage(), "literal")
      } else if (node.isVariable() && node.getName() != null) {
        ("-", "?" + node.getName(), "var")
      } else if (node.isBlank() && node.getBlankNodeLabel() != null) {
        ("-", node.getBlankNodeLabel(), "blank")
      } else ("-", "-", "-")
    }

    val elems: List[Element] = query.getQueryPattern() match {
      case x: ElementGroup => x.getElements().asScala.toList
      case _ => List()
    }

    val triples = extractTriplePaths(elems, List[List[TriplePath]]())

    /*
	   * Return sequence of Triples
	   */
    val filteredTriples = triples.flatten.filter(_.getPredicate() != null)
    
    
    filteredTriples.map(triple => {

      /*
       * Process Triple to Tuples of NameSpace - Uri/Label/variable
       */
      val subj = triple.getSubject()
      val pred = triple.getPredicate() 
      val obj =  triple.getObject()

      val subjInfo = getNodeInfo(subj)
      val predInfo = getNodeInfo(pred)
      val objInfo = getNodeInfo(obj)
      
      println(objInfo)

      SimpleTriple(
        sub_pref = subjInfo._1,
        sub_ent = subjInfo._2,
        sub_type = subjInfo._3,
        pred_pref = predInfo._1,
        pred_prop = predInfo._2,
        pred_type = predInfo._3,
        obj_pref = objInfo._1,
        obj_ent = objInfo._2,
        obj_type = objInfo._3,
        queryId = id)
    })

    //    /*
    //     * Generate the map holding all prefix - URL pairs
    //     */
    //    val prefixMap: Map[String, String] = queryString.length() match {
    //      case 0 => Map()
    //      case _ => try {
    //        generatePrefixMap(queryString.split("SELECT")(0))
    //      } catch {
    //        case aioob: ArrayIndexOutOfBoundsException => Map()
    //      }
    //    }

    //    /*
    //     * Generate a list of all triples in the query
    //     */
    //    val extractedRawTriples: List[(String, String, String)] = queryString.length() match {
    //      case 0 => List()
    //      case _ => try {
    //        findAllTriples(queryString.split("SELECT")(1))
    //      } catch {
    //        case aioob: ArrayIndexOutOfBoundsException => List()
    //      }
    //    }
  }

  def extractTriplePaths(elems: List[Element], res: List[List[TriplePath]]): List[List[TriplePath]] = {

    elems match {
      case head :: tail => {
        head match {
          case head: ElementPathBlock => extractTriplePaths(tail, res :+ head.getPattern().getList().asScala.toList)
          case head: ElementGroup => extractTriplePaths(tail, res ++ extractTriplePaths(head.getElements().asScala.toList, res))
          case head: ElementUnion => extractTriplePaths(tail, res ++ extractTriplePaths(head.getElements().asScala.toList, res))
          case _ => extractTriplePaths(tail, res)
        }
      }
      case Nil => res
    }

    //        if (text.isEmpty) res
    //        //      else if (text.head == '{' && !stack.isEmpty && lastAdded == 0) {
    //        //        isBalanced(text.tail, stack :+ actualQuery.indexOf('{', stack.last+1), res, lastAdded)
    //        //      }    
    //        else if (text.head == '{' && !stack.isEmpty) {
    //          isBalanced(text.tail, stack :+ actualQuery.indexOf('{', lastAdded), res, actualQuery.indexOf('{', lastAdded) + 1)
    //        } else if (text.head == '{') {
    //          isBalanced(text.tail, stack :+ actualQuery.indexOf('{'), res, actualQuery.indexOf('{') + 1)
    //        } else if (text.head == '}' && !stack.isEmpty) {
    //          isBalanced(text.tail, stack.slice(0, stack.size - 1), res :+ actualQuery.substring(stack.last + 1, actualQuery.indexOf('}', lastAdded) + 1), lastAdded)
    //        } else isBalanced(text.tail, stack, res, lastAdded)

  }

  //  def generatePrefixMap(prefixes: String): Map[String, String] = {
  //
  //    // Sample Prefix: PREFIX owl: <http://www.w3.org/2002/07/owl#>\n
  //
  //    val prefixElems = prefixes.split("PREFIX").map(prefix => {
  //      val withoutReturn = prefix.replaceAll("\\r", "")
  //      val withoutBreak = withoutReturn.replaceAll("\\n", "")
  //      val pair = withoutBreak.split(":", 2)
  //      if(pair.length==2) (pair(0).trim(), pair(1).trim())    
  //      else ("","")
  ////      pair.length match {
  ////        case 2 => (pair(0).trim(), pair(1).trim())
  ////        case _ => DoNothing
  ////      }
  //    }).toList
  //
  //    Map(prefixElems: _*)
  //  }

  //  def findAllTriples(actualQuery: String): List[(String, String, String)] = {
  //
  //    /*
  //     *  ?subject ?lat ?long WHERE {\r\n?subject <http://purl.org/dc/terms/subject> <http://dbpedia.org/resource/Category:Football_venues_in_Portugal>.\r\n
  //     *  ?subject geo:lat ?lat.
  //     *  \r\n?subject geo:long ?long.\r\n} LIMIT 20
  //     */
  //    
  //    val curlyBracesBlocks = curlyBracesBalancer(actualQuery)
  //
  //    List()
  //  }

  //  def curlyBracesBalancer(actualQuery: String): List[String] = {
  //
  //    def isBalanced(text: List[Char], stack: List[Int], res: List[String], lastAdded: Int): List[String] =
  //      if (text.isEmpty) res
  ////      else if (text.head == '{' && !stack.isEmpty && lastAdded == 0) {
  ////        isBalanced(text.tail, stack :+ actualQuery.indexOf('{', stack.last+1), res, lastAdded)
  ////      }    
  //      else if (text.head == '{' && !stack.isEmpty) {
  //        isBalanced(text.tail, stack :+ actualQuery.indexOf('{', lastAdded), res, actualQuery.indexOf('{', lastAdded)+1)
  //      }    
  //      else if (text.head == '{') {
  //        isBalanced(text.tail, stack :+ actualQuery.indexOf('{'), res, actualQuery.indexOf('{')+1)
  //      }
  //      else if (text.head == '}' && !stack.isEmpty) {
  //        isBalanced(text.tail, stack.slice(0, stack.size-1), res :+ actualQuery.substring(stack.last+1, actualQuery.indexOf('}', lastAdded)+1), lastAdded)
  //      }
  //      else isBalanced(text.tail, stack, res, lastAdded)
  //
  //    isBalanced(actualQuery.toList, List(), List(), 0)
  //  }

}
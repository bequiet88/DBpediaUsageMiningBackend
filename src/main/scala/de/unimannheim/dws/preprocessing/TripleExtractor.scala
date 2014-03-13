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

// TODO: Write general trait for triple extractors

/*
 * Triple Extractor using the Jena Arq Parser
 */
object ArqTripleExtractor {

  /*
   * Main method for ARQ extraction
   */
  def extract(query: Query, id: ObjectId): Seq[SimpleTriple] = {

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
      val obj = triple.getObject()

      val subjInfo = getArqNodeInfo(subj)
      val predInfo = getArqNodeInfo(pred)
      val objInfo = getArqNodeInfo(obj)

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

  }

  /*
   * Method to generate information from a node
   */
  private def getArqNodeInfo(node: Node) = {

    if (node.isURI() && node.getNameSpace() != null && node.getLocalName() != null) {
      (node.getNameSpace(), node.getLocalName(), "uri")
    } else if (node.isLiteral() && node.getLiteralLexicalForm() != null && node.getLiteralLanguage() != null) {
      ("-", node.getLiteralLexicalForm() + node.getLiteralLanguage(), "literal")
    } else if (node.isVariable() && node.getName() != null) {
      ("-", "?" + node.getName(), "var")
    } else if (node.isBlank() && node.getBlankNodeLabel() != null) {
      ("-", node.getBlankNodeLabel(), "blank")
    } else ("-", "-", "-")
  }

  /*
   * Use Apache Jena Arq Element Interface for SPARQL query parsing
   */
  private def extractTriplePaths(elems: List[Element], res: List[List[TriplePath]]): List[List[TriplePath]] = {

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
  }

}

/*
 * Manual Triple Extractor, invoked in case Arq SPARQL parser does not work
 */
object ManualTripleExtractor {

  /*
   * Main method for Manual extraction
   */
  def extract(queryString: String): Seq[SimpleTriple] = {
    /*
         * Generate the map holding all prefix - URL pairs
         */
    implicit val prefixMap: Map[String, String] = queryString.length() match {
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
    val filteredTriples: Seq[(TriplePatternElement, TriplePatternElement, TriplePatternElement)] = queryString.length() match {
      case 0 => Seq()
      case _ => try {
        findAllTriples(queryString.split("SELECT")(1))
      } catch {
        case aioob: ArrayIndexOutOfBoundsException => List()
      }
    }

    filteredTriples.map(triple => {

      val subjInfo = getManualNodeInfo(triple._1)
      val predInfo = getManualNodeInfo(triple._2)
      val objInfo = getManualNodeInfo(triple._3)

      SimpleTriple(
        sub_pref = subjInfo._1,
        sub_ent = subjInfo._2,
        sub_type = subjInfo._3,
        pred_pref = predInfo._1,
        pred_prop = predInfo._2,
        pred_type = predInfo._3,
        obj_pref = objInfo._1,
        obj_ent = objInfo._2,
        obj_type = objInfo._3)
    })

    Seq()
  }

  /*
   *   Method to generate information from a TriplePatternElement
   */
  def getManualNodeInfo(node: TriplePatternElement)(implicit prefixMap: Map[String, String]): (String, String, String) = {

    node match {
      case n: Variable => ("-", n.getValue, "var")
      case n: Literal => ("-", n.getValue, "literal")
      case n: Uri => {
        /*
         * Uri does not uses a shortage for the resolutino URI
         */
        if (n.getValue.startsWith("<")) {
          val withouthBrackets = n.getValue.substring(1, n.getValue.length - 1)
          if(withouthBrackets.count(_ == ':') > 1) {
            val posLastColon = withouthBrackets.reverse.indexOf(":")            
            (withouthBrackets.reverse.substring(posLastColon, withouthBrackets.length()).reverse, withouthBrackets.reverse.substring(0, posLastColon).reverse, "uri")
          }
          else if (withouthBrackets.contains("#")) {
            val posAsterix = withouthBrackets.reverse.indexOf("#")            
            (withouthBrackets.reverse.substring(posAsterix , withouthBrackets.length()).reverse, withouthBrackets.reverse.substring(0, posAsterix).reverse, "uri")
          }
          else {
            val posLastSlash = withouthBrackets.reverse.indexOf("/")
            (withouthBrackets.reverse.substring(posLastSlash , withouthBrackets.length()).reverse, withouthBrackets.reverse.substring(0, posLastSlash).reverse, "uri")
          }
          
        /*  
         *  Uri does use a Prefix -> look it up in the prefix map
         */
        } else {
          try {
            val nodeElements = n.getValue.split(":", 2)
            (prefixMap.get(nodeElements(0)).get, nodeElements(1), "uri")
          } catch {
            case aioob: ArrayIndexOutOfBoundsException => ("", "", "")
          }
        }
      }
      case _ => ("", "", "")
    }
  }

  /*
   * Extracts all Prefixes and their resolver to a map
   */
  def generatePrefixMap(prefixes: String): Map[String, String] = {

    // Sample Prefix: PREFIX owl: <http://www.w3.org/2002/07/owl#>\n

    val prefixElems = prefixes.split("PREFIX").map(prefix => {
      val withoutReturn = prefix.replaceAll("\\r", "")
      val withoutBreak = withoutReturn.replaceAll("\\n", "")
      val pair = withoutBreak.split(":", 2)
      if (pair.length == 2) (pair(0).trim(), pair(1).replaceAll("<", "").replaceAll(">", "").trim())
      else ("", "")
      //      pair.length match {
      //        case 2 => (pair(0).trim(), pair(1).trim())
      //        case _ => DoNothing
      //      }
    }).toList

    Map(prefixElems: _*)
  }

  /*
   * Finds patterns of triples in the query
   */
  def findAllTriples(actualQuery: String): Seq[(TriplePatternElement, TriplePatternElement, TriplePatternElement)] = {

    /*
       *  ?subject ?lat ?long WHERE {\r\n?subject <http://purl.org/dc/terms/subject> <http://dbpedia.org/resource/Category:Football_venues_in_Portugal>.\r\n
       *  ?subject geo:lat ?lat.
       *  \r\n?subject geo:long ?long.\r\n} LIMIT 20
       */

    /*
     * Remove all unnecessary stuff like selected variables, break, returns, whitespaces
     */
    val withoutWhere = actualQuery.split("WHERE", 2)(1)
    val withoutReturn = withoutWhere.replaceAll("\\r", " ")
    val withoutBreak = withoutReturn.replaceAll("\\n", " ")
    val cleanedActualQuery = withoutBreak.trim().replaceAll(" +", " ")

    if (balanced(cleanedActualQuery.toList) == false) Seq()

    //    val curlyBracesBlocks = curlyBracesBalancer(actualQuery)

    val elements = tokenize(cleanedActualQuery)

    recursiveTripleFinder(elements)
  }

  /*
   * Tokenizes the query string and allocates the suitable class to the token
   */
  def tokenize(query: String): List[SparqlParserElement] = {

    val tokens = query.split(" ").toList

    tokens.map(token => {
      token.charAt(0) match {
        case '?' => new Variable(token)
        case '"' => new Literal(token)
        case '<' => new Uri(token)
        case '.' => new EndOfTriple(token)
        case ';' => new EndOfTriple(token)
        case '}' => new EndOfTriple(token)
        case _ => {
          if (token.contains(':')) new Uri(token)
          else new Noise(token)
        }
      }
    })

  }

  /*
   * Recursively traverses the list to find the patterns of elements that are needed for a triple
   */
  def recursiveTripleFinder(elements: List[SparqlParserElement]): List[(TriplePatternElement, TriplePatternElement, TriplePatternElement)] = {

    def finder(elements: List[SparqlParserElement], res: List[(TriplePatternElement, TriplePatternElement, TriplePatternElement)]): List[(TriplePatternElement, TriplePatternElement, TriplePatternElement)] = {

      if (elements.isEmpty) res
      else {
        elements match {
          case List(s: TriplePatternElement, p: TriplePatternElement, o: TriplePatternElement, x: EndOfTriple, _*) => finder(elements.slice(4, elements.size - 1), res :+ (s, p, o))
          //          case List(s: Uri, p: Uri, o: Literal, x: EndOfTriple, _*) => finder(elements.slice(4, elements.size - 1), res :+ (s.getValue, p.getValue, o.getValue))
          //          case List(s: Uri, p: Uri, o: Variable, x: EndOfTriple, _*) => finder(elements.slice(4, elements.size - 1), res :+ (s.getValue, p.getValue, o.getValue))
          //          case List(s: Uri, p: Literal, o: Uri, x: EndOfTriple, _*) => finder(elements.slice(4, elements.size - 1), res :+ (s.getValue, p.getValue, o.getValue))
          //          case List(s: Uri, p: Literal, o: Variable, x: EndOfTriple, _*) => finder(elements.slice(4, elements.size - 1), res :+ (s.getValue, p.getValue, o.getValue))
          //          case List(s: Uri, p: Variable, o: Uri, x: EndOfTriple, _*) => finder(elements.slice(4, elements.size - 1), res :+ (s.getValue, p.getValue, o.getValue))
          //          case List(s: Uri, p: Variable, o: Variable, x: EndOfTriple, _*) => finder(elements.slice(4, elements.size - 1), res :+ (s.getValue, p.getValue, o.getValue))
          //          case List(s: Literal, p: Uri, o: Uri, x: EndOfTriple, _*) => finder(elements.slice(4, elements.size - 1), res :+ (s.getValue, p.getValue, o.getValue))
          //          case List(s: Literal, p: Uri, o: Uri, x: EndOfTriple, _*) => finder(elements.slice(4, elements.size - 1), res :+ (s.getValue, p.getValue, o.getValue))
          case List(p: TriplePatternElement, o: TriplePatternElement, x: Semicolon, _*) => finder(elements.slice(3, elements.size - 1), res :+ (res.last._1, p, o))
          case _ => finder(elements.tail, res)
        }

      }
    }

    finder(elements, List())
  }

  //  def curlyBracesBalancer(actualQuery: String): List[String] = {
  //
  //    def isBalanced(text: List[Char], stack: List[Int], res: List[String], lastAdded: Int): List[String] =
  //      if (text.isEmpty) res
  //      //      else if (text.head == '{' && !stack.isEmpty && lastAdded == 0) {
  //      //        isBalanced(text.tail, stack :+ actualQuery.indexOf('{', stack.last+1), res, lastAdded)
  //      //      }    
  //      else if (text.head == '{' && !stack.isEmpty) {
  //        isBalanced(text.tail, stack :+ actualQuery.indexOf('{', lastAdded), res, actualQuery.indexOf('{', lastAdded) + 1)
  //      } else if (text.head == '{') {
  //        isBalanced(text.tail, stack :+ actualQuery.indexOf('{'), res, actualQuery.indexOf('{') + 1)
  //      } else if (text.head == '}' && !stack.isEmpty) {
  //        isBalanced(text.tail, stack.slice(0, stack.size - 1), res :+ actualQuery.substring(stack.last + 1, actualQuery.indexOf('}', lastAdded) + 1), lastAdded)
  //      } else isBalanced(text.tail, stack, res, lastAdded)
  //
  //    isBalanced(actualQuery.toList, List(), List(), 0)
  //  }

  def balanced(chars: List[Char]): Boolean = {

    def isBalanced(text: List[Char], stack: List[Char]): Boolean =
      if (text.isEmpty) stack.isEmpty
      else if (text.head == '{') isBalanced(text.tail, stack :+ text.head)
      else if (text.head == '}') (!stack.isEmpty && isBalanced(text.tail, stack.reverse.tail.reverse))
      else isBalanced(text.tail, stack)

    isBalanced(chars, List())
  }

}
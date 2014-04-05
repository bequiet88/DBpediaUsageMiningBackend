import org.joda.time.format.DateTimeFormatter

object Tests {

  import org.joda.time.DateTime
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet

  val test = "1kfjsdf:lkfdslfkL"                  //> test  : String = 1kfjsdf:lkfdslfkL
  test.charAt(test.length() - 1)                  //> res0: Char = L
  test.count(_ == ':')                            //> res1: Int = 1
  val testIndex = test.indexOf(":")               //> testIndex  : Int = 7
  val testSubString = test.substring(0, test.length() - 1)
                                                  //> testSubString  : String = 1kfjsdf:lkfdslfk

 val mySet: Set[Int] = Set(1,2,3,4)               //> mySet  : Set[Int] = Set(1, 2, 3, 4)
mySet contains(5)                                 //> res2: Boolean = false

val myList = mySet.toList:+1                      //> myList  : List[Int] = List(1, 2, 3, 4, 1)


val pairs = for(x <- myList; y <- myList) yield (x, y)
                                                  //> pairs  : List[(Int, Int)] = List((1,1), (1,2), (1,3), (1,4), (1,1), (2,1), (
                                                  //| 2,2), (2,3), (2,4), (2,1), (3,1), (3,2), (3,3), (3,4), (3,1), (4,1), (4,2), 
                                                  //| (4,3), (4,4), (4,1), (1,1), (1,2), (1,3), (1,4), (1,1))
import de.unimannheim.dws.preprocessing.DBpediaOntologyAccess
import scala.collection.JavaConverters._
val node = DBpediaOntologyAccess.getOntClass("http://www.w3.org/2002/07/owl#Thing")
                                                  //> log4j:WARN No appenders could be found for logger (org.apache.jena.riot.stre
                                                  //| am.JenaIOEnvironment).
                                                  //| log4j:WARN Please initialize the log4j system properly.
                                                  //| log4j:WARN See http://logging.apache.org/log4j/1.2/faq.html#noconfig for mor
                                                  //| e info.
                                                  //| node  : com.hp.hpl.jena.ontology.OntClass = http://www.w3.org/2002/07/owl#Th
                                                  //| ing
println(node.hasSuperClass(node, true))           //> true
val superNodes = node.listSuperClasses().asScala.toList
                                                  //> superNodes  : List[com.hp.hpl.jena.ontology.OntClass] = List()
println(superNodes)                               //> List()
}
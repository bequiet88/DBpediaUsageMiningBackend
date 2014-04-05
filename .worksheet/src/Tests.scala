import org.joda.time.format.DateTimeFormatter

object Tests {

  import org.joda.time.DateTime;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(140); 
  println("Welcome to the Scala worksheet");$skip(35); 

  val test = "1kfjsdf:lkfdslfkL";System.out.println("""test  : String = """ + $show(test ));$skip(33); val res$0 = 
  test.charAt(test.length() - 1);System.out.println("""res0: Char = """ + $show(res$0));$skip(23); val res$1 = 
  test.count(_ == ':');System.out.println("""res1: Int = """ + $show(res$1));$skip(36); 
  val testIndex = test.indexOf(":");System.out.println("""testIndex  : Int = """ + $show(testIndex ));$skip(59); 
  val testSubString = test.substring(0, test.length() - 1);System.out.println("""testSubString  : String = """ + $show(testSubString ));$skip(37); 

 val mySet: Set[Int] = Set(1,2,3,4);System.out.println("""mySet  : Set[Int] = """ + $show(mySet ));$skip(18); val res$2 = 
mySet contains(5);System.out.println("""res2: Boolean = """ + $show(res$2));$skip(30); 

val myList = mySet.toList:+1;System.out.println("""myList  : List[Int] = """ + $show(myList ));$skip(57); 


val pairs = for(x <- myList; y <- myList) yield (x, y)
import de.unimannheim.dws.preprocessing.DBpediaOntologyAccess
import scala.collection.JavaConverters._;System.out.println("""pairs  : List[(Int, Int)] = """ + $show(pairs ));$skip(187); 
val node = DBpediaOntologyAccess.getOntClass("http://www.w3.org/2002/07/owl#Thing");System.out.println("""node  : com.hp.hpl.jena.ontology.OntClass = """ + $show(node ));$skip(40); 
println(node.hasSuperClass(node, true));$skip(56); 
val superNodes = node.listSuperClasses().asScala.toList;System.out.println("""superNodes  : List[com.hp.hpl.jena.ontology.OntClass] = """ + $show(superNodes ));$skip(20); 
println(superNodes)}
}

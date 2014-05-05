import org.joda.time.format.DateTimeFormatter

object Tests {

  import org.joda.time.DateTime;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(140); 
  println("Welcome to the Scala worksheet");$skip(35); 

  val test = "1kfjsdf:lkfdslfkL";System.out.println("""test  : String = """ + $show(test ));$skip(33); val res$0 = 
  test.charAt(test.length() - 1);System.out.println("""res0: Char = """ + $show(res$0));$skip(23); val res$1 = 
  test.count(_ == ':');System.out.println("""res1: Int = """ + $show(res$1));$skip(36); 
  val testIndex = test.indexOf(":");System.out.println("""testIndex  : Int = """ + $show(testIndex ));$skip(59); 
  val testSubString = test.substring(0, test.length() - 1);System.out.println("""testSubString  : String = """ + $show(testSubString ));$skip(38); 

 val mySet: Set[Int] = Set(1,2,3,4);System.out.println("""mySet  : Set[Int] = """ + $show(mySet ));$skip(27); val res$2 = 
 mySet.slice(1,mySet.size)
 
 
import de.unimannheim.dws.models.postgre.Tables._;System.out.println("""res2: scala.collection.immutable.Set[Int] = """ + $show(res$2));$skip(127); 
 
 val _1 = PairCounterRow(prop1Id = "aa", prop2Id="aa", count= Some(0));System.out.println("""_1  : de.unimannheim.dws.models.postgre.Tables.PairCounterRow = """ + $show(_1 ));$skip(71); 
 val _2 = PairCounterRow(prop1Id = "aa", prop2Id="aa", count= Some(0));System.out.println("""_2  : de.unimannheim.dws.models.postgre.Tables.PairCounterRow = """ + $show(_2 ));$skip(29); val res$3 = 
 
 _1.equalsByReverseIds(_2);System.out.println("""res3: Boolean = """ + $show(res$3));$skip(16); 
 
 val d = 25/3;System.out.println("""d  : Int = """ + $show(d ))}
}

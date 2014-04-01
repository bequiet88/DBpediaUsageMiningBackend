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
mySet contains(5)


import sun.security.provider.MD5
import java.security.MessageDigest;System.out.println("""res2: Boolean = """ + $show(res$2));$skip(133); 

    val md5: MessageDigest = MessageDigest.getInstance("MD5");System.out.println("""md5  : java.security.MessageDigest = """ + $show(md5 ));$skip(36); 
    md5.update("hallo".getBytes());$skip(57); ;
    val id = md5.digest().map("%02X".format(_)).mkString
 import de.unimannheim.dws.preprocessing.Util;System.out.println("""id  : String = """ + $show(id ));$skip(64); 
val d1:Double = 3;System.out.println("""d1  : Double = """ + $show(d1 ));$skip(36); 
val d2:Double = 4353453.34487567635;System.out.println("""d2  : Double = """ + $show(d2 ));$skip(26); 
val res = (d1/d2).toFloat;System.out.println("""res  : Float = """ + $show(res ));$skip(31); 
val res2 = Util.round(res, 10);System.out.println("""res2  : Double = """ + $show(res2 ));$skip(65); 


val md51 = Util.md5("http://dbpedia.org/ontology/abstract@fr");System.out.println("""md51  : String = """ + $show(md51 ));$skip(61); 
val md52 = Util.md5("http://dbpedia.org/ontology/birthDate");System.out.println("""md52  : String = """ + $show(md52 ))}

}

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


import sun.security.provider.MD5
import java.security.MessageDigest

    val md5: MessageDigest = MessageDigest.getInstance("MD5")
                                                  //> md5  : java.security.MessageDigest = MD5 Message Digest from SUN, <initializ
                                                  //| ed>
                                                  //| 
    md5.update("hallo".getBytes());
    val id = md5.digest().map("%02X".format(_)).mkString
                                                  //> id  : String = 598D4C200461B81522A3328565C25F7C
 import de.unimannheim.dws.preprocessing.Util
val d1:Double = 3                                 //> d1  : Double = 3.0
val d2:Double = 4353453.34487567635               //> d2  : Double = 4353453.344875677
val res = (d1/d2).toFloat                         //> res  : Float = 6.891081E-7
val res2 = Util.round(res, 10)                    //> res2  : Double = 6.891E-7


val md51 = Util.md5("http://dbpedia.org/ontology/abstract@fr")
                                                  //> md51  : String = 54366E57972BCE3240E6587CDA2ABB5D
val md52 = Util.md5("http://dbpedia.org/ontology/birthDate")
                                                  //> md52  : String = C212D09402AF52A91979F9E91B5287AF

}
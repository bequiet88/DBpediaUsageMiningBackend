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
 mySet.slice(1,mySet.size)                        //> res2: scala.collection.immutable.Set[Int] = Set(2, 3, 4)
 
 
import de.unimannheim.dws.models.postgre.Tables._
 
 val _1 = PairCounterRow(prop1Id = "aa", prop2Id="aa", count= Some(0))
                                                  //> _1  : de.unimannheim.dws.models.postgre.Tables.PairCounterRow = PairCounterR
                                                  //| ow(aa,aa,Some(0))
 val _2 = PairCounterRow(prop1Id = "aa", prop2Id="aa", count= Some(0))
                                                  //> _2  : de.unimannheim.dws.models.postgre.Tables.PairCounterRow = PairCounterR
                                                  //| ow(aa,aa,Some(0))
 
 _1.equalsByReverseIds(_2)                        //> res3: Boolean = true
}
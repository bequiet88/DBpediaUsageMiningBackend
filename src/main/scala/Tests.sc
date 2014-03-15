import org.joda.time.format.DateTimeFormatter

object Tests {

import org.joda.time.DateTime
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  
val test = "1kfjsdf:lkfdslfkL"                    //> test  : String = 1kfjsdf:lkfdslfkL
test.charAt(test.length()-1)                      //> res0: Char = L
test.count(_ == ':')                              //> res1: Int = 1
val testIndex = test.indexOf(":")                 //> testIndex  : Int = 7
val testSubString = test.substring(0,test.length()-1)
                                                  //> testSubString  : String = 1kfjsdf:lkfdslfk



}
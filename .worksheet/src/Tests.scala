import org.joda.time.format.DateTimeFormatter

object Tests {

import org.joda.time.DateTime;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(138); 
  println("Welcome to the Scala worksheet");$skip(35); 
  
val test = "1kfjsdf:lkfdslfkL";System.out.println("""test  : String = """ + $show(test ));$skip(29); val res$0 = 
test.charAt(test.length()-1);System.out.println("""res0: Char = """ + $show(res$0));$skip(21); val res$1 = 
test.count(_ == ':');System.out.println("""res1: Int = """ + $show(res$1));$skip(34); 
val testIndex = test.indexOf(":");System.out.println("""testIndex  : Int = """ + $show(testIndex ));$skip(54); 
val testSubString = test.substring(0,test.length()-1);System.out.println("""testSubString  : String = """ + $show(testSubString ));$skip(325); 

scala.slick.model.codegen.SourceCodeGenerator.main(
  Array("scala.slick.driver.PostgresDriver", "org.postgresql.Driver", "jdbc:postgresql://localhost/usage_mining", "D:/data_server/eclipse/DBpediaUsageMining/src/main/scala/de/unimannheim/dws/models/postgre", "de.unimannheim.dws.models.postgre", "postgres", "postgres")
)}

}

import org.joda.time.format.DateTimeFormatter

object Tests {

import org.joda.time.DateTime;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(138); 
  println("Welcome to the Scala worksheet")

import com.hp.hpl.jena.query.Query
import com.hp.hpl.jena.query.QueryFactory;$skip(934); 
val queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
"PREFIX dbo: <http://dbpedia.org/ontology/>"+
"SELECT ?pic ?abstract WHERE {"+
"{"+
    "?s rdfs:label \"jkj\"@ru ."+
    "{"+
        "?s dbo:thumbnail ?pic;"+
           "dbo:abstract  ?abstract"+
    "}"+
    "UNION"+
    "{"+
        "?s dbo:wikiPageDisambiguates ?actualResource ."+
        "?actualResource rdfs:label ?redirectsTo ;"+
                        "dbo:thumbnail ?pic;"+
                        "dbo:abstract ?abstract."+
        "FILTER(lang(?redirectsTo) = \"ru\")"+
    "}"+
    "UNION"+
    "{"+
        "?s dbo:wikiPageRedirects ?actualResource ."+
        "?actualResource rdfs:label ?redirectsTo ;"+
                        "dbo:thumbnail ?pic;"+
        "FILTER(lang(?redirectsTo) = \"ru\")"+
    "}"+
"}"+
"FILTER (lang(?abstract) = \"ru\")"+
"} LIMIT 1";System.out.println("""queryString  : String = """ + $show(queryString ));$skip(52); 
val query: Query = QueryFactory.create(queryString);System.out.println("""query  : com.hp.hpl.jena.query.Query = """ + $show(query ));$skip(182); 

    def compressRecursive[A](ls: List[A]): List[A] = ls match {
      case Nil => Nil
      case h :: tail => h :: compressRecursive(tail.dropWhile(s => s == " " && s == h))
    };System.out.println("""compressRecursive: [A](ls: List[A])List[A]""");$skip(99); 
    
    val cleanedActualQuery:String = "dfj    dfdf dfdf   dfd< ".trim().replaceAll(" +", " ");System.out.println("""cleanedActualQuery  : String = """ + $show(cleanedActualQuery ));$skip(31); ;
val test = "1kfjsdf:lkfdslfkL";System.out.println("""test  : String = """ + $show(test ));$skip(21); val res$0 = 
test.count(_ == ':');System.out.println("""res0: Int = """ + $show(res$0));$skip(34); 
val testIndex = test.indexOf(":");System.out.println("""testIndex  : Int = """ + $show(testIndex ));$skip(62); 
val testSubString = test.substring(testIndex+1,test.length());System.out.println("""testSubString  : String = """ + $show(testSubString ))}



}

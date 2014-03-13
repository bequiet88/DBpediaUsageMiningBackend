import org.joda.time.format.DateTimeFormatter

object Tests {

import org.joda.time.DateTime
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet

import com.hp.hpl.jena.query.Query
import com.hp.hpl.jena.query.QueryFactory
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
"} LIMIT 1"                                       //> queryString  : String = PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#
                                                  //| >PREFIX dbo: <http://dbpedia.org/ontology/>SELECT ?pic ?abstract WHERE {{?s
                                                  //|  rdfs:label "jkj"@ru .{?s dbo:thumbnail ?pic;dbo:abstract  ?abstract}UNION{
                                                  //| ?s dbo:wikiPageDisambiguates ?actualResource .?actualResource rdfs:label ?r
                                                  //| edirectsTo ;dbo:thumbnail ?pic;dbo:abstract ?abstract.FILTER(lang(?redirect
                                                  //| sTo) = "ru")}UNION{?s dbo:wikiPageRedirects ?actualResource .?actualResourc
                                                  //| e rdfs:label ?redirectsTo ;dbo:thumbnail ?pic;FILTER(lang(?redirectsTo) = "
                                                  //| ru")}}FILTER (lang(?abstract) = "ru")} LIMIT 1
val query: Query = QueryFactory.create(queryString)
                                                  //> log4j:WARN No appenders could be found for logger (org.apache.jena.riot.str
                                                  //| eam.JenaIOEnvironment).
                                                  //| log4j:WARN Please initialize the log4j system properly.
                                                  //| log4j:WARN See http://logging.apache.org/log4j/1.2/faq.html#noconfig for mo
                                                  //| re info.
                                                  //| query  : com.hp.hpl.jena.query.Query = PREFIX  rdfs: <http://www.w3.org/200
                                                  //| 0/01/rdf-schema#>
                                                  //| PREFIX  dbo:  <http://dbpedia.org/ontology/>
                                                  //| 
                                                  //| SELECT  ?pic ?abstract
                                                  //| WHERE
                                                  //|   { { ?s rdfs:label "jkj"@ru
                                                  //|         { ?s dbo:thumbnail ?pic .
                                                  //|           ?s dbo:abstract ?abstract
                                                  //|         }
                                                  //|       UNION
                                                  //|         { ?s dbo:wikiPageDisambiguates ?actualResource .
                                                  //|           ?actualResource rdfs:label ?redirectsTo .
                                                  //|           ?actualResource dbo:thumbnail ?pic .
                                                  //|           ?actualResource dbo:abstract ?abstract
                                                  //|           FILTER ( lang(?redirectsTo) = "ru" )
                                                  //|         }
                                                  //|       UNION
                                                  //|         { ?s dbo:wikiPageRedirects ?actualResource .
                                                  //|           ?act
                                                  //| Output exceeds cutoff limit.

    def compressRecursive[A](ls: List[A]): List[A] = ls match {
      case Nil => Nil
      case h :: tail => h :: compressRecursive(tail.dropWhile(s => s == " " && s == h))
    }                                             //> compressRecursive: [A](ls: List[A])List[A]
    
    val cleanedActualQuery:String = "dfj    dfdf dfdf   dfd< ".trim().replaceAll(" +", " ");
                                                  //> cleanedActualQuery  : String = dfj dfdf dfdf dfd<
val test = "1kfjsdf:lkfdslfkL"                    //> test  : String = 1kfjsdf:lkfdslfkL
test.count(_ == ':')                              //> res0: Int = 1
val testIndex = test.indexOf(":")                 //> testIndex  : Int = 7
val testSubString = test.substring(testIndex+1,test.length())
                                                  //> testSubString  : String = lkfdslfkL



}
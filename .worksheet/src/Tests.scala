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
val query: Query = QueryFactory.create(queryString);System.out.println("""query  : com.hp.hpl.jena.query.Query = """ + $show(query ))}

}

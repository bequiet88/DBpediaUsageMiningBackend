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

}
package de.unimannheim.dws.controller

import de.unimannheim.dws.preprocessing.DBpediaOntologyAccess

object MainController {

  def main(args: Array[String]): Unit = {
    
    DBpediaOntologyAccess.getOntClass("dummyOnly")
    println("Scala called. Ontology loaded");
    
  }

}
package de.unimannheim.dws.controller

import de.unimannheim.dws.preprocessing.DBpediaOntologyAccess
import scala.collection.JavaConverters._

object MasterDataController extends App {

  val ontClass = DBpediaOntologyAccess.getOntClass("http://dbpedia.org/ontology/Biomolecule")

  if (ontClass.hasSubClass()) {

    for {
      subClass <- ontClass.listSubClasses().toList.asScala.toList
      printer = println(subClass.toString())
    } yield { subClass }

  }

  println()

}
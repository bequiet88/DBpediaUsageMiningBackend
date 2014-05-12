package de.unimannheim.dws.preprocessing

import com.hp.hpl.jena.ontology.OntDocumentManager
import com.hp.hpl.jena.ontology.OntModelSpec
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.ontology.OntModel
import com.hp.hpl.jena.ontology.OntClass
import scala.collection.JavaConverters._
import com.hp.hpl.jena.util.FileManager

object DBpediaOntologyAccess {

  private lazy val dm: OntDocumentManager = new OntDocumentManager()
  private lazy val modelSpec: OntModelSpec = OntModelSpec.OWL_MEM
  modelSpec.setDocumentManager(dm)
  private lazy val base: OntModel = ModelFactory.createOntologyModel( modelSpec )
  base.read(getClass().getResourceAsStream("dbpedia_3_9.owl"), "RDF/XML") // FileManager.get.open("assets/dbpedia_3_9.owl")

  private lazy val dbpediaModel = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_TRANS_INF, base );
  
  def getOntClass(classLabel: String): OntClass = {
    dbpediaModel.getOntClass(classLabel)
  }
//  
//  private lazy val artefact: OntClass = dbpediaModel.getOntClass("http://dbpedia.org/ontology/Enzyme")
//  
//  println(artefact.getSuperClass().getURI())
//  
//  
//  private lazy val superClasses = for {
//    superClass <- artefact.listSuperClasses(true).toList.asScala.toList
//    printer = println(superClass.toString())
//  } yield {superClass}
//  
//  println(superClasses.size)
//  
  
       
//        val ontClass = DBpediaOntologyAccess.getOntClass("http://dbpedia.org/ontology/Biomolecule")
//    
//        if (ontClass.hasSubClass()) {
//    
//          for {
//            subClass <- ontClass.listSubClasses().toList.asScala.toList
//            printer = println(subClass.toString())
//          } yield { subClass }
//    
//        }
  
}
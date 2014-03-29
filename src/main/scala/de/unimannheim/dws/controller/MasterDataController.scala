package de.unimannheim.dws.controller

import java.io.File
import scala.collection.JavaConverters._
import scala.io.BufferedSource
import scala.io.Source
import de.unimannheim.dws.models.postgre.DbConn
import de.unimannheim.dws.models.postgre.Tables._
import de.unimannheim.dws.preprocessing.DBpediaOntologyAccess
import de.unimannheim.dws.preprocessing.Util
import scala.slick.driver.PostgresDriver.simple._

object MasterDataController extends App {
  DbConn.openConn withSession { implicit session =>
    /*
   * Read all classes of ontology
   */

    val superClass = DBpediaOntologyAccess.getOntClass("http://www.w3.org/2002/07/owl#Thing")
    val subClasses = superClass.listSubClasses().asScala.toList

    val levelList: List[(String, Int)] = subClasses map { c =>
      (c.toString, c.listSuperClasses().toList().size)
    }

    val completeList = levelList :+ ("http://www.w3.org/2002/07/owl#Thing", -1)

    /*
   * Write class master data to DB
   */
    //    val classes = completeList.map { c =>
    //      ClassesUniqueRow(id = Util.md5(c._1), label = Some(c._1))
    //    }
    //
    //    ClassesUnique.insertAll(classes: _*)
    //    
    //    println("Number of inserted classes: "+classes.size)

    /*
   * Generate Level Map for look up purposes
   */
    val levelMap = completeList.toMap

    println(levelMap)

    /*
   * Read entity file
   */
    val buffer: Iterator[String] = Source.fromFile(new File("G:/instance_types_en/instance_types_en.nt")).getLines //new File("D:/ownCloud/Data/Studium/Master_Thesis/03_Libs_Docu/instance_types_en.nt/sample.txt")).getLines

    def recursiveEntityReader(entityList: List[EntitiesUniqueRow], buffer: Iterator[String], tempEnt: String, tempClasses: List[String]): List[EntitiesUniqueRow] = {

      // Flush to database after 1k entities
      if (entityList.size > 1000) {
        try {
          EntitiesUnique.insertAll(entityList: _*)
          println("Added 1k entities")
           recursiveEntityReader(List(), buffer, tempEnt, tempClasses)
        } catch {
          case e: Exception => {
            println("ERROR: Lost 1k entities")
            recursiveEntityReader(List(), buffer, tempEnt, tempClasses)
          }
        }
        
       
      } // iterate through the file
      else if (buffer.hasNext) {

        val next = buffer.next.toString
        val entityClass = {
          val split = next.split(" ").toList
          if (split.size == 4) {
            (split(0).trim.substring(1, split(0).length - 1), split(2).trim.substring(1, split(2).length() - 1))
          } else ("", "http://www.w3.org/2002/07/owl#Thing")
        }

        // next iteration round
        if (tempEnt.length() == 0 || tempEnt.equals(entityClass._1)) {
          if (entityClass._2.contains("dbpedia") || entityClass._2.contains("Thing")) {
            recursiveEntityReader(entityList, buffer, entityClass._1, tempClasses :+ entityClass._2)
          } else {
            recursiveEntityReader(entityList, buffer, entityClass._1, tempClasses)
          }
          // add a row to entity list 
        } else {

          val prefixEnt = {
            val posLastSlash = tempEnt.reverse.indexOf("/")
            (tempEnt.reverse.substring(posLastSlash, tempEnt.length()).reverse, tempEnt.reverse.substring(0, posLastSlash).reverse)
          }

          val classLabel = {
            val depthList = tempClasses.map(c => levelMap.get(c).getOrElse(0))
            val label = tempClasses(depthList.zipWithIndex.maxBy(_._1)._2)
            if (label.length() > 0) label
            else "http://www.w3.org/2002/07/owl#Thing"
          }

          val row: EntitiesUniqueRow = EntitiesUniqueRow(id = Util.md5(tempEnt), prefix = Some(prefixEnt._1), entity = Some(prefixEnt._2), classId = Some(Util.md5(classLabel)))

          // Check whether this entity already exists in current entitylist
          if (entityList.filter(e => e.id == row.id).length > 0) {
            if (entityClass._2.contains("dbpedia") || entityClass._2.contains("Thing")) {
              recursiveEntityReader(entityList, buffer, entityClass._1, List() :+ entityClass._2)
            } else {
              recursiveEntityReader(entityList, buffer, entityClass._1, List())
            }
          } else {
            if (entityClass._2.contains("dbpedia") || entityClass._2.contains("Thing")) {
              recursiveEntityReader(entityList :+ row, buffer, entityClass._1, List() :+ entityClass._2)
            } else {
              recursiveEntityReader(entityList :+ row, buffer, entityClass._1, List())
            }
          }
        }

        // Flush remaining entities to DB
      } else {
        val prefixEnt = {
          val posLastSlash = tempEnt.reverse.indexOf("/")
          (tempEnt.reverse.substring(posLastSlash, tempEnt.length()).reverse, tempEnt.reverse.substring(0, posLastSlash).reverse)
        }

        val classLabel = {
          val depthList = tempClasses.map(c => levelMap.get(c).getOrElse(0))
          val label = tempClasses(depthList.zipWithIndex.maxBy(_._1)._2)
          if (label.length() > 0) label
          else "http://www.w3.org/2002/07/owl#Thing"
        }

        val row: EntitiesUniqueRow = EntitiesUniqueRow(id = Util.md5(tempEnt), prefix = Some(prefixEnt._1), entity = Some(prefixEnt._2), classId = Some(Util.md5(classLabel)))

        EntitiesUnique.insertAll(entityList :+ row: _*)
        return entityList :+ row
      }
    }

    val lines = recursiveEntityReader(List(), buffer, "", List())

    lines.map(l => println(l))

  }
}
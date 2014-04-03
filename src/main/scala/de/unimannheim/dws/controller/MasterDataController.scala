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
import scala.slick.jdbc.{ GetResult, StaticQuery => Q }
import scala.slick.driver.JdbcDriver.backend.Database

object MasterDataController extends App {
  DbConn.openConn withSession { implicit session =>

    /*
   * Read all classes of ontology
   */
    lazy val superClass = DBpediaOntologyAccess.getOntClass("http://www.w3.org/2002/07/owl#Thing")
    lazy val subClasses = superClass.listSubClasses().asScala.toList

    lazy val levelList: List[(String, Int)] = subClasses map { c =>
      (c.toString, c.listSuperClasses().toList().size)
    }

    lazy val completeList = levelList :+ ("http://www.w3.org/2002/07/owl#Thing", -1)

    /*
     * Write Classes Master Data
     */
    createClassesUnique(completeList)

    /*
     * Write Entity Master Data
     */
    val file: File = new File("G:/instance_types_en/instance_types_en.nt")
    //new File("D:/ownCloud/Data/Studium/Master_Thesis/03_Libs_Docu/instance_types_en.nt/sample.txt")).getLines
    createEntitiesUnique(completeList, file)

    /*
     * Write Property Master Data
     */
    createPropertiesUnique()

  }

  /**
   * Method to create unique classes on DB
   */
  def createClassesUnique(completeList: List[(String, Int)])(implicit session: slick.driver.PostgresDriver.backend.Session) = {

    /*
   * Write class master data to DB
   */
    val classes = completeList.map { c =>
      ClassesUniqueRow(id = Util.md5(c._1), label = Some(c._1))
    }

    ClassesUnique.insertAll(classes: _*)

    println("Number of inserted classes: " + classes.size)
  }

  /**
   * Method to create unique entities on DB
   */
  def createEntitiesUnique(completeList: List[(String, Int)], file: File)(implicit session: slick.driver.PostgresDriver.backend.Session) = {

    /*
   * Generate Level Map for look up purposes
   */
    val levelMap = completeList.toMap

    /*
   * Read entity file
   */
    val buffer: Iterator[String] = Source.fromFile(file).getLines

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

    /*
     * Invoke Recursion
     */
    val lines = recursiveEntityReader(List(), buffer, "", List())

  }

  /**
   * Method to create Property Master Data
   */
  def createPropertiesUnique()(implicit session: slick.driver.PostgresDriver.backend.Session) = {

    val list = Q.queryNA[PropertiesUniqueRow]("SELECT DISTINCT '0' as md5, a.pred_prefix, a.pred_prop, COUNT(*) as count FROM simple_triples as a WHERE a.pred_type = 'uri' GROUP BY a.pred_prefix, a.pred_prop").list

    val totalCount = list.foldLeft(0D)((i, row) => i + row.support.getOrElse(0D))

    //    //    val filteredList = list.filter( row => !row.prefix.get.contains(" "))
    //
    //     = list.map { row =>
    //      row.copy(id = Util.md5(row.prefix.get + row.property.get), support = Some(Util.round(row.support.get / totalCount, 10)))
    //    }
    //    
    //    val distinctPropSet = (SimpleTriples filter (_.predType === "uri") map (s => (s.predPrefix, s.predProp))).buildColl[Set]

    /*
     * Filter out duplicate properties originated from wrong prefix / property allocation
     */
    val finalList = list.groupBy(l => l.prefix.get + l.property.get).map(t => {
      val row = t._2(0)
      row.copy(id = Util.md5(row.prefix.get + row.property.get), support = Some(Util.round((t._2.foldLeft(0D)((i, r) => i + r.support.getOrElse(0D))) / totalCount, 10)))
    }).toList

    /*
     * Dump to PostgreSQL
     */
    try {
      PropertiesUnique.insertAll(finalList: _*)
      println("successful " + finalList.size)
    } catch {
      case e: java.sql.BatchUpdateException => println(e.getNextException())
    }

  }
}
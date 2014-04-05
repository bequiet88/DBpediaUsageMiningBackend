package de.unimannheim.dws.algorithms

import scala.slick.driver.JdbcDriver.backend.Database
import scala.slick.driver.PostgresDriver.simple._
import de.unimannheim.dws.models.postgre.Tables._
import de.unimannheim.dws.preprocessing.Util
import de.unimannheim.dws.preprocessing.DBpediaOntologyAccess
import scala.collection.JavaConverters._
import com.hp.hpl.jena.ontology.OntClass
import scala.util.control._

object ClusterGrouper extends RankingAlgorithm[PairCounterRow, (String, Double)] {

  /**
   * Method to generate property pairs with their number of hits
   * possible improvements:
   * 1. use index on query_id
   */
  def generate()(implicit session: slick.driver.PostgresDriver.backend.Session) = {

    /*
     * If duplicate properties on query level should apply, use List[String], otherwise Set[String]
     */
    val mapQueryProp = (for {
      p <- PropertyAnalytics
    } yield (p.queryId.get, p.propertyId.get)).foldLeft(Map[(Long), Set[String]]())((i, row) => {
      if (i.contains((row._1))) {
        i + (((row._1), i(row._1) + row._2))
      } else {
        i + (((row._1), Set(row._2)))
      }
    })

    /*
     * big set of all property pairs 
     * -> leaks memory if duplicate values are allowed
     */
    val setPropPairs = mapQueryProp.map(sp => {
      val pairs = for (x <- sp._2; y <- sp._2) yield (x, y)
      pairs
    }).toList.flatten

    println("Property Pairs extracted from DB: " + setPropPairs.size)

    /*
     * property pair (key), count (value) map
     */
    val mapPropPairCount = setPropPairs.foldLeft(Map[(String, String), Int]())((i, row) => {

      if (i.contains((row._1, row._2))) {
        i + (((row._1, row._2), i(row._1, row._2) + 1))
      } else {
        if (i.size % 1000 == 0) {
          println("Size of Map: " + i.size)
        }
        i + (((row._1, row._2), 1))
      }
    })

    println("Distinct Property Pairs extracted from DB: " + mapPropPairCount.size)

    val resList = mapPropPairCount.map(res => {
      PairCounterRow(prop1Id = res._1._1, prop2Id = res._1._2, count = Some(res._2))
    })

    resList.toList
  }

  /**
   * Method to retrieve a sorted map of properties
   * possible improvements
   * 1. use index
   */
  def retrieve(triples: List[(String, String, String)])(implicit session: slick.driver.PostgresDriver.backend.Session) = {
    ???
    //      if (triples.size > 0) {
    //  
    //        val entityId = Util.md5(triples.head._1)
    //  
    //        val propertyIds = triples.map(t => {
    //          Util.md5(t._2)
    //        })
    //  
    //        /*
    //         * Read class label for this entity from DB
    //         */
    //        val classLabel = (for {
    //          //        e <- EntitiesUnique if e.id === entityId
    //          //        c <- ClassesUnique if (c.id === e.classId)
    //          (c, e) <- ClassesUnique innerJoin EntitiesUnique on (_.id === _.classId) if (e.id === entityId)
    //        } yield (c.id, c.label)).first
    //  
    //        /*
    //         * Get all properties requested together with this entity's class
    //         */
    //        val properties = (for {
    //          p <- PropertiesUnique
    //          cp <- ClassPropertyCounter
    //          if cp.classId === classLabel._1
    //          if cp.propertyId === p.id
    //          if cp.propertyId inSetBind propertyIds
    //        } yield (p.id, p.prefix, p.property, cp.count)).list
    //  
    //        /*
    //         * check whether props from DB present in this triple list's predicates
    //         */
    //        val resMapPropIds = properties.foldLeft((Map[String, Double](), propertyIds))((i, prop) => {
    //          val propLabel = prop._2.get + prop._3.get
    //          if (i._2.contains(prop._1)) {
    //            (i._1.+((propLabel, prop._4.get)), i._2 diff List(prop._1))
    //          } else {
    //            (i._1, i._2)
    //          }
    //        })
    //  
    //        /*
    //         * In case all elements are full, return a sorted list, otherwise go into the recursion
    //         */
    //        if (resMapPropIds._2.size > 0) {
    //          recursiveRetrieval(resMapPropIds._2, classLabel, resMapPropIds._1, 0.5D).toList.sortBy({ _._2 }).reverse
    //        } else resMapPropIds._1.toList.sortBy({ _._2 }).reverse
    //      } else List()
  }

  //  private def recursiveRetrieval(propertyIds: List[String], classLabel: (String, Option[String]), resMap: Map[String, Double], weight: Double)(implicit session: slick.driver.PostgresDriver.backend.Session): Map[String, Double] = {
  //
  //    lazy val ontClass = DBpediaOntologyAccess.getOntClass(classLabel._2.get)
  //    val superClass = ontClass.getSuperClass()
  //    val subClasses = superClass.listSubClasses(true).asScala.toList diff List(ontClass)
  //
  //    /*
  //     * Find all leaf classes on this level of the ontology
  //     */
  //    val leafClasses = subClasses.foldLeft(List[OntClass]())((i, ontClass) => {
  //      /*
  //       * Subclasses that are directly leaf classes
  //       */
  //      if (ontClass.hasSubClass() == false) {
  //        i :+ ontClass
  //      } /*
  //       * In case subclasses are NOT directly leaf classes, resolve their leaf sub classes
  //       */ else {
  //        val currentSubClasses = ontClass.listSubClasses().asScala.toList
  //        i ++ currentSubClasses.foldLeft(List[OntClass]())((j, ontSubClass) => {
  //          if (ontSubClass.hasSubClass() == false) {
  //            j :+ ontSubClass
  //          } else j
  //        })
  //      }
  //    })
  //
  //    val resMapPropIds: (Map[String, Double], List[String]) = leafClasses.removeDuplicates.foldLeft((resMap, propertyIds))((i, classLabel) => {
  //
  //      if (i._2.size > 0) {
  //        /*
  //       * Get all properties requested together with this entity's class
  //       */
  //        val properties = (for {
  //          p <- PropertiesUnique
  //          cp <- ClassPropertyCounter
  //          if cp.classId === Util.md5(classLabel.getLabel("EN"))
  //          if cp.propertyId === p.id
  //          if cp.propertyId inSetBind propertyIds
  //        } yield (p.id, p.prefix, p.property, cp.count)).list
  //
  //        /*
  //       * check whether props from DB present in this triple list's predicates
  //       */
  //        properties.foldLeft((i._1, i._2))((j, prop) => {
  //          val propLabel = prop._2.get + prop._3.get
  //          if (j._2.contains(prop._1)) {
  //            (j._1.+((propLabel, prop._4.get * weight)), j._2 diff List(prop._1))
  //          } else {
  //            (j._1, i._2)
  //          }
  //        })
  //      } else (i._1, i._2)
  //    })
  //
  //    if (resMapPropIds._2.size > 0 && superClass.hasSuperClass()) {
  //      recursiveRetrieval(resMapPropIds._2, (Util.md5(superClass.getLabel("EN")), Some(superClass.getLabel("EN"))), resMapPropIds._1, weight / 2)
  //    } else {
  //      val remainingPropIds = resMapPropIds._2.map(p => (p, 0D)).toMap
  //      resMapPropIds._1.++(remainingPropIds)
  //    }
  //  }
  //
}
package de.unimannheim.dws.algorithms

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

import scala.collection.JavaConverters._
import scala.slick.driver.JdbcDriver.backend.Database
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.GetResult
import scala.slick.jdbc.{ StaticQuery => Q }
import scala.collection.mutable.StringBuilder

import de.unimannheim.dws.models.postgre.Tables._
import de.unimannheim.dws.preprocessing.Util

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

    val properties = triples.map(_._2).removeDuplicates

    println("Unique Properties generated from TripleList: " + properties.size)

    // Generate temporary Maps mapping 1.) Integer Index to URI 2.) MD5 to Integer Index
    val propertyIdMaps = properties.zipWithIndex.foldLeft((Map[Int, String](), Map[String, Int]())) { (i, t) =>
      {
        val id = Util.md5(t._1)
        (i._1 + ((t._2, t._1)), i._2 + ((id, t._2)))
      }
    }

    // sorted sequence of integers
    val propertyIntIdsList = for (i <- 0 to propertyIdMaps._1.size-1) yield i

    // all pairs from the properties MD5 hashes
    val pairs = for (x <- propertyIdMaps._2.keySet; y <- propertyIdMaps._2.keySet) yield (x, y)

    // string with all pairs for DB Select Statement
    val pairString = pairs.foldLeft(new StringBuilder())((i, row) => {
      if (i.length() == 0) i.append("'" + row._1 + row._2 + "'")
      else i.append(",'" + row._1 + row._2 + "'")
    })

    println("Pairs generated from TripleList: " + pairs.size)

    // List of Pairs found on DB with a count assigned to them
    val propPairWeightList = Q.queryNA[PairCounterRow]("select prop_1_id, prop_2_id, count from pair_counter where concat(prop_1_id, prop_2_id) in (" + pairString.toString + ")")
      .list

    println("Pairs read from DB: " + propPairWeightList.size)

    //    val propPairWeightMapQ = (for {
    //      pc <- PairCounter
    //      if pc.prop1Id inSetBind pairs.map(_._1)
    //      if pc.prop2Id inSetBind pairs.map(_._2)
    //    } yield (pc.prop1Id, pc.prop2Id, pc.count.get))

    //    println(propPairWeightMapQ.selectStatement)

    // Two lists: 1.) 
    val propPairWeightUniqueLists = propPairWeightList.foldLeft((Map[(Int, Int), Double](), propPairWeightList))((i, row) => {

      def getIds(a: ((Int, Int), Double)): (Int, Int) = {
        if (a._1._1 < a._1._2) (a._1._1, a._1._2)
        else (a._1._2, a._1._1)
      }

      val sameElem = (i._2 diff List(row)).filter(_.equalsByReverseIds(row)).headOption
      val rowTransformed = ((propertyIdMaps._2.get(row.prop1Id).get, propertyIdMaps._2.get(row.prop2Id).get), row.count.get.asInstanceOf[Double])
      val ids = getIds(rowTransformed)

      sameElem match {
        case x: Some[PairCounterRow] => {
          println("same pair found")
          (i._1 + (((ids._1, ids._2), (row.count.get + x.get.count.get).asInstanceOf[Double])), i._2 diff List(row, sameElem.get))
        }
        case None => {
          if (i._1.contains(ids._1, ids._2) == true) {

            (i._1, i._2 diff List(row))

          } else {

            (i._1 + (((ids._1, ids._2), (row.count.get).asInstanceOf[Double])), i._2 diff List(row))

          }
        }
      }
    })
    
    println(""+propPairWeightUniqueLists._2.size);
 

    println("Unique pairs read from DB: " + propPairWeightUniqueLists._1.size)
    propPairWeightUniqueLists._1.foreach(p => println(propertyIdMaps._1.get(p._1._1) + " " + propertyIdMaps._1.get(p._1._2) + " " + p._2))
    propPairWeightUniqueLists._1.foreach(p => println(p._1._1 + " " + p._1._2 + " " + p._2))

    val propertyMatrix = propertyIntIdsList.foldLeft(List[(Int, Int, Double)]())((i, id) => {

      // inner loop to iterate only over all pairs (ab/ba) once
      val listInnerLoop = propertyIntIdsList.slice(id, propertyIntIdsList.size)

      val tempPropMatrix = listInnerLoop.map(subId => {
        //        if (id == subId) {
        //          (id, subId, 0D)
        //        } else {
        val count = propPairWeightUniqueLists._1.getOrElse((id, subId), 0D)
        if (count == 0D) {
          (id, subId, 10D)
          //          } else if (count == 0D) {
          //            (id, subId, 0D)
        } else {
          (id, subId, 1 / count)
        }
        //        }
      })
      i ++ tempPropMatrix
    })

    println("Pairs about to be written to file: " + propertyMatrix.size)

        val file: File = new File("C:/Temp/distance_matrix.ascii");
        file.getParentFile().mkdirs();
    
        val out: BufferedWriter = new BufferedWriter(new FileWriter(file));
    
        val last = propertyMatrix.reverse.head
    
        propertyMatrix.map(p => {
          out.write(p._1 + " " + p._2 + " " + p._3)
          if (!p.eq(last)) out.newLine()
        })
        out.flush()
        out.close()

    List()
  }
}
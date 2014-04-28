package de.unimannheim.dws.algorithms

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.HashMap

import scala.collection.JavaConverters._
import scala.collection.mutable.StringBuilder
import scala.slick.driver.JdbcDriver.backend.Database
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.GetResult
import scala.slick.jdbc.{ StaticQuery => Q }

import de.unimannheim.dws.models.postgre.Tables._
import de.unimannheim.dws.preprocessing.Util

import weka.core.Attribute
import weka.core.FastVector
import weka.core.Instance
import weka.core.Instances
import weka.clusterers.DBSCAN
import weka.clusterers.ClusterEvaluation

object ClusterGrouper extends RankingAlgorithm[PairCounterRow, (String, String, Double)] {

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

    // Calculate the distance matrix of given triples and Integer-URL-Resolver
    val distanceMatrix = calculateDistanceMatrix(triples)

    // Pushes the Distance Matrix to the Interoperability Class
    DistanceMatrix.setDistanceMatrix(convertDistanceMatrixToWeka(distanceMatrix._1))

    /*
     * Instantiate WEKA, see 
     * - http://weka.wikispaces.com/Creating+an+ARFF+file
     * - http://weka.wikispaces.com/Use+Weka+in+your+Java+code#Clustering
     */
    // 1. set up attributes
    var atts: FastVector = new FastVector()
    // - string
    atts.addElement(new Attribute("att1"))
    // 2. create Instances object
    var data: Instances = new Instances("clusterGrouper", atts, 0);

    // 3. fill with data
    for {
      i <- 0 to distanceMatrix._2.size
    } yield {
      var vals: Array[Double] = new Array[Double](data.numAttributes())
      vals(0) = i
      data.add(new Instance(1.0, vals))
    }

    // 4. instantiate clusterer
    var options: Array[String] = new Array[String](6)
    options(0) = "-E"; // epsilon
    options(1) = "0.2"
    options(2) = "-D"; // distance function
    options(3) = "de.unimannheim.dws.algorithms.CustomDBSCANDataObject"
    options(4) = "-M"; // epsilon
    options(5) = "10"
    var clusterer: DBSCAN = new DBSCAN() // new instance of clusterer
    clusterer.setOptions(options) // set the options
    clusterer.buildClusterer(data) // build the clusterer
    
    clusterer.getOptions().foreach(p => println(p))

    // 5. iterate with instances over cluster
    val resList = (for {
      i <- 0 to data.numInstances()
    } yield {
      var cluster = "noise"
      try {
        cluster = clusterer.clusterInstance(data.instance(i)).toString
      } catch {
        case t: Exception => // todo: handle error
      }
      val propLabel = distanceMatrix._2.get(i).getOrElse("")
      val support = {
        val filteredProp = distanceMatrix._3.filter(p => (p.prefix.get + p.property.get).equals(propLabel))
        filteredProp.headOption match {
          case p: Some[PropertiesUniqueRow] => p.get.support.get
          case _ => 0D
        }
      }
      (propLabel, cluster, support)
    }).toList

    resList.filter(r => !r._1.equals("")).sortBy(r => (r._2, r._3))
  }

  def calculateDistanceMatrix(triples: List[(String, String, String)])(implicit session: slick.driver.PostgresDriver.backend.Session): (List[(Int, Int, Double)], Map[Int, String], List[PropertiesUniqueRow]) = {

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
    val propertyIntIdsList = for (i <- 0 to propertyIdMaps._1.size - 1) yield i

    // string with all pairs for DB Select Statement
    val pairString = propertyIdMaps._2.keySet.foldLeft(new StringBuilder())((i, row) => {
      if (i.length() == 0) i.append("'" + row + "'")
      else i.append(",'" + row + "'")
    })

    // List of Pairs found on DB with a count assigned to them
    val propPairWeightList = Q.queryNA[PairCounterRow]("select prop_1_id, prop_2_id, count from pair_counter where prop_1_id in (" + pairString.toString + ") and prop_2_id in (" + pairString.toString + ")")
      .list

    println("Pairs read from DB: " + propPairWeightList.size)

    // List of Support of single properties from Triple List
    val propertyList = Q.queryNA[PropertiesUniqueRow]("select * from properties_unique where id in (" + pairString.toString + ")").list

    // Two collections: 1.) Map with two integer indexes as key and the count as value. 2.) List of unprocessed prop pairs 
    val propPairWeightUniqueLists = propPairWeightList.foldLeft((Map[(Int, Int), Double](), propPairWeightList))((i, row) => {

      // Ensures that the smaller ID is in first place as required by ELKI
      def getIds(a: ((Int, Int), Double)): (Int, Int) = {
        if (a._1._1 < a._1._2) (a._1._1, a._1._2)
        else (a._1._2, a._1._1)
      }

      // Checks whether there's another same element in the remaining prop pairs 
      val sameElem = (i._2 diff List(row)).filter(_.equalsByReverseIds(row)).headOption
      val rowTransformed = ((propertyIdMaps._2.get(row.prop1Id).get, propertyIdMaps._2.get(row.prop2Id).get), row.count.get.asInstanceOf[Double])
      val ids = getIds(rowTransformed)

      // Append the current element and either delete only the current elem or also the same elem 
      sameElem match {
        case x: Some[PairCounterRow] => {
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

    println("Unique pairs read from DB: " + propPairWeightUniqueLists._1.size)
    propPairWeightUniqueLists._1.foreach(p => println(propertyIdMaps._1.get(p._1._1) + " " + propertyIdMaps._1.get(p._1._2) + " " + p._2))

    // Generate the Distance matrix of all prop pairs with a distance assigned to them
    val propertyMatrix = propertyIntIdsList.foldLeft(List[(Int, Int, Double)]())((i, id) => {

      // inner loop to iterate only over all pairs (ab/ba) once
      val listInnerLoop = propertyIntIdsList.slice(id, propertyIntIdsList.size)

      val tempPropMatrix = listInnerLoop.map(subId => {
        val count = propPairWeightUniqueLists._1.getOrElse((id, subId), 0D)
        if (count == 0D) {
          (id, subId, 10D)
        } else {
          (id, subId, 1 / count)
        }
      })
      i ++ tempPropMatrix
    })
    (propertyMatrix, propertyIdMaps._1, propertyList)
  }

  def printDistanceMatrix(propertyMatrix: List[(Int, Int, Double)]) = {

    println("Pairs about to be written to file: " + propertyMatrix.size)
    // Print the Distance Matrix as required by ELKI 
    val file: File = new File("D:/ownCloud/Data/Studium/Master_Thesis/04_Data_Results/distance_matrices/distance_matrix.ascii");
    file.getParentFile().mkdirs();

    val out: BufferedWriter = new BufferedWriter(new FileWriter(file));

    val last = propertyMatrix.reverse.head

    propertyMatrix.map(p => {
      out.write(p._1 + " " + p._2 + " " + p._3)
      if (!p.eq(last)) out.newLine()
    })
    out.flush()
    out.close()
  }

  def convertDistanceMatrixToWeka(distancesList: List[(Int, Int, Double)]): Map[(Integer, Integer), java.math.BigDecimal] = {

    distancesList.foldLeft(Map[(Integer, Integer), java.math.BigDecimal]())((i, row) => {

      i + (((row._1, row._2), new java.math.BigDecimal(row._3)))

      //      if(i.containsKey(row._1)) {
      //        i.get(row._1).put(row._2, new java.math.BigDecimal(row._3))
      //        i        
      //      }
      //      else {
      //        val newMap: HashMap[Integer, java.math.BigDecimal] = new HashMap()
      //        newMap.put(row._2, new java.math.BigDecimal(row._3))
      //        i.put(row._1, newMap)
      //        i
      //      }
    })

  }

}
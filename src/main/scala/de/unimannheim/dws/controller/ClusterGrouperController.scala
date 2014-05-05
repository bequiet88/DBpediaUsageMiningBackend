package de.unimannheim.dws.controller

import de.unimannheim.dws.models.postgre.DbConn
import de.unimannheim.dws.preprocessing.Util
import de.unimannheim.dws.models.postgre.Tables._
import scala.slick.driver.PostgresDriver.simple._
import de.unimannheim.dws.models.mongo.ClassPropertyCounter
import de.unimannheim.dws.models.mongo.ClassPropertyCounterDAO
import de.unimannheim.dws.algorithms.ClusterGrouper
import java.io.File
import scala.io.Source
import de.unimannheim.dws.algorithms.RankingAlgorithm

object ClusterGrouperController extends App {
  DbConn.openConn withSession { implicit session =>

    //    createPropertyPairs(10000)
    val file: File = new File("D:/ownCloud/Data/Studium/Master_Thesis/04_Data_Results/berlin_test_triples.txt")
    readPropertyPairsCluster(file)

  }

  def createPropertyPairs(stepSize: Int)(implicit session: slick.driver.PostgresDriver.backend.Session) = {

    val listPropPairs = ClusterGrouper.generate

    val upperLimit = (Util.round(listPropPairs.size / stepSize.asInstanceOf[Double], 0) + 1).asInstanceOf[Int]

    for {
      i <- (1 to upperLimit)

    } yield {

      val skip = i * stepSize

      val tempList = listPropPairs.slice(skip - stepSize, skip)

      /*
         * PostgreSQL insert
         */
      try {
        de.unimannheim.dws.models.postgre.Tables.PairCounter.insertAll(tempList: _*)
        println("PostgreSQL successful " + tempList.size + ", total " + skip)
      } catch {
        case e: java.sql.BatchUpdateException => println(e.getNextException())
      }

      //      /*
      //         * mongoDB insert
      //         */
      //      val mongoList = tempList.map(c => {
      //        val classLabel = ClassesUnique.filter(_.id === c.classId)
      //        val propertyLabel = PropertiesUnique.filter(_.id === c.propertyId)
      //        de.unimannheim.dws.models.mongo.ClassPropertyCounter(classLabel = classLabel.first.label.get, propertyLabel = propertyLabel.first.prefix.get + propertyLabel.first.property.get, count = c.count.get)
      //      })
      //      ClassPropertyCounterDAO.insert(mongoList)
      //      println("Mongo successful " + tempList.size + ", total " + skip)

    }
  }

  def readPropertyPairsCluster(file: File)(implicit session: slick.driver.PostgresDriver.backend.Session) = {

    val listLines: List[String] = Source.fromFile(file, "UTF-8").getLines.toList

    val listTriples = listLines.map(l => {
      val split = l.split(" ").toList
      if (split.size == 3) {
        (split(0), split(1), split(2))
      } else if (split.size > 3) {
        val objectLiteral = split.slice(2, split.size).foldLeft(new StringBuilder())((i, row) => {
          i.append(row + " ")
        })
        (split(0), split(1), objectLiteral.toString)
      } else {
        ("", "", "")
      }
    })

    val resList = ClusterGrouper.getRankedTriples(listTriples, ClusterGrouper.retrieve(listTriples, Array[String]()))

    resList.map(r => println(r))

  }

}
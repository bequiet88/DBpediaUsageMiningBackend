package de.unimannheim.dws.controller

import de.unimannheim.dws.models.postgre.DbConn
import de.unimannheim.dws.algorithms.SimpleCounter
import de.unimannheim.dws.preprocessing.Util
import de.unimannheim.dws.models.postgre.Tables._
import scala.slick.driver.PostgresDriver.simple._
import de.unimannheim.dws.models.mongo.ClassPropertyCounter
import de.unimannheim.dws.models.mongo.ClassPropertyCounterDAO

object SimpleCountController extends App {
  DbConn.openConn withSession { implicit session =>

    createClassPropertyPairs

  }

  def createClassPropertyPairs()(implicit session: slick.driver.PostgresDriver.backend.Session) = {

    val listClassProp = SimpleCounter.generate

    val upperLimit = (Util.round(listClassProp.size / 1000D, 0) + 1).asInstanceOf[Int]

    for {
      i <- (1 to upperLimit)

    } yield {

      val skip = i * 1000

      val tempList = listClassProp.slice(skip - 1000, skip)

      /*
         * PostgreSQL insert
         */
      try {
        de.unimannheim.dws.models.postgre.Tables.ClassPropertyCounter.insertAll(tempList: _*)
        println("PostgreSQL successful " + tempList.size + ", total " + skip)
      } catch {
        case e: java.sql.BatchUpdateException => println(e.getNextException())
      }

      /*
         * mongoDB insert
         */
      val mongoList = tempList.map(c => {
        val classLabel = ClassesUnique.filter(_.id === c.classId)
        val propertyLabel = PropertiesUnique.filter(_.id === c.propertyId)
        de.unimannheim.dws.models.mongo.ClassPropertyCounter(classLabel = classLabel.first.label.get, propertyLabel = propertyLabel.first.prefix.get + propertyLabel.first.property.get, count = c.count.get)
      })
      ClassPropertyCounterDAO.insert(mongoList)
      println("Mongo successful " + tempList.size + ", total " + skip)

    }
  }
}
package de.unimannheim.dws.algorithms

import scala.collection.JavaConverters._
import java.util.HashMap
import de.unimannheim.dws.preprocessing.Util
import scala.slick.jdbc.{ StaticQuery => Q }
import de.unimannheim.dws.models.postgre.Tables._

/**
 * Singleton Object for Interaction with Java Class
 * http://twitter.github.io/scala_school/java.html
 * http://www.codecommit.com/blog/java/interop-between-java-and-scala
 */
object DistanceMatrix {

  var distanceMatrix: Map[(Integer, Integer), java.math.BigDecimal] = Map()

  def setDistanceMatrix(in: Map[(Integer, Integer), java.math.BigDecimal]) = {
    distanceMatrix = in
  }

  def getDistance(key1:Integer, key2:Integer): java.math.BigDecimal = {
    distanceMatrix.getOrElse((key1, key2), new java.math.BigDecimal(10D))
  }
}
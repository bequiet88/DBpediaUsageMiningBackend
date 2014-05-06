package de.unimannheim.dws.preprocessing

import java.security.MessageDigest
import scala.math.BigDecimal._
object Util {

  def md5(str: String): String = {

    val md5: MessageDigest = MessageDigest.getInstance("MD5")
    md5.update(str.getBytes());
    md5.digest().map("%02X".format(_)).mkString

  }

  def round(value: Double, places: Int): Double = {
    if (places < 0) throw new IllegalArgumentException();
    val bd: BigDecimal = BigDecimal(value);
    return bd.setScale(places, RoundingMode.HALF_UP).doubleValue
  }

  def getPropertiesToRemove(): List[String] = {

    List("http://www.w3.org/2000/01/rdf-schema#label",
      "http://dbpedia.org/ontology/thumbnail",
      "http://dbpedia.org/ontology/abstract",
      "http://dbpedia.org/ontology/wikiPageRedirects",
      "http://dbpedia.org/ontology/wikiPageDisambiguates",
      "http://www.w3.org/1999/02/22-rdf-syntax-ns#type",
      "http://www.w3.org/2002/07/owl#sameAs")

  }

}
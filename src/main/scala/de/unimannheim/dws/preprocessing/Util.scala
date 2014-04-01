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

}
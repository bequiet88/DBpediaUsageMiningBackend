package de.unimannheim.dws.preprocessing

import java.security.MessageDigest

object Util {

  def md5(str: String): String = {

    val md5: MessageDigest = MessageDigest.getInstance("MD5")
    md5.update(str.getBytes());
    md5.digest().map("%02X".format(_)).mkString

  }

}
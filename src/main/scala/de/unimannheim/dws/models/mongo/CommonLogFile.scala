package de.unimannheim.dws.models.mongo

import com.novus.salat._
import com.novus.salat.global._
import com.mongodb.casbah.MongoConnection
import com.novus.salat.dao.SalatDAO
import org.bson.types.ObjectId
import org.joda.time.DateTime
import com.novus.salat.annotations.raw.Key

// https://github.com/novus/salat/wiki/SalatDAO

case class CommonLogFile(
    @Key("_id") _id: ObjectId = new ObjectId, 
    ip: String, 
    rfc921: String,
    username: String,
    date: String, // strftime format %d/%b/%Y:%H:%M:%S %z.
    time: String,
    timezone: String,
    requestMethod: String,
    requestUrl: String,
    requestProtocol: String,
    httpStatus: String)
//    size: String,
//    referrer: String,
//    agent: String)
    
object CommonLogFileDAO extends SalatDAO[CommonLogFile, ObjectId](collection = MongoConnection()("usage_mining")("logs"))    
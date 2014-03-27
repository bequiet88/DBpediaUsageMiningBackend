package de.unimannheim.dws.models.mongo

import com.novus.salat._
import com.novus.salat.global._
import com.mongodb.casbah.MongoConnection
import com.novus.salat.dao.SalatDAO
import org.bson.types.ObjectId
import org.joda.time.DateTime
import com.novus.salat.annotations.raw.Key

// https://github.com/novus/salat/wiki/SalatDAO

case class PairCounter(
    @Key("propertyId1") propertyId1: String, 
    @Key("propertyId2") propertyId2: String,  
    count: Integer)
    
object PairCounterDAO extends SalatDAO[PairCounter, ObjectId](collection = MongoConnection()("usage_mining")("pair_counter"))    
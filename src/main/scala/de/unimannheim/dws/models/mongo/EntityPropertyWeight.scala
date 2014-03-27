package de.unimannheim.dws.models.mongo

import com.novus.salat._
import com.novus.salat.global._
import com.mongodb.casbah.MongoConnection
import com.novus.salat.dao.SalatDAO
import org.bson.types.ObjectId
import org.joda.time.DateTime
import com.novus.salat.annotations.raw.Key

// https://github.com/novus/salat/wiki/SalatDAO

case class EntityPropertyWeight(
    @Key("propertyId") propertyId: String, 
    @Key("entityId") entityId: String, 
    weight: Double)
    
object EntityPropertyWeightDAO extends SalatDAO[EntityPropertyWeight, ObjectId](collection = MongoConnection()("usage_mining")("entity_property_weight"))    
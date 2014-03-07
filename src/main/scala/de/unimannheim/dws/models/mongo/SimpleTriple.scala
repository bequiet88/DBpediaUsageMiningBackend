package de.unimannheim.dws.models.mongo

import com.novus.salat._
import com.novus.salat.global._
import com.mongodb.casbah.MongoConnection
import com.novus.salat.dao.SalatDAO
import org.bson.types.ObjectId
import org.joda.time.DateTime
import com.novus.salat.annotations.raw.Key

// https://github.com/novus/salat/wiki

case class SimpleTriple(
    @Key("_id") _id: ObjectId = new ObjectId,
    sub_type: String,
    sub_pref: String, 
    sub_ent: String,
    pred_type: String,
    pred_pref: String,
    pred_prop: String, // strftime format %d/%b/%Y:%H:%M:%S %z.
    obj_type: String,
    obj_pref: String,
    obj_ent: String,
    queryId: ObjectId)
    
object SimpleTripleDAO extends SalatDAO[SimpleTriple, ObjectId](collection = MongoConnection()("usage_mining")("simple_triples"))    
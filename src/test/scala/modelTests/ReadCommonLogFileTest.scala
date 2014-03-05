package modelTests

import org.specs2.mutable._
import de.unimannheim.dws.models.mongo.CommonLogFileDAO
import com.mongodb.casbah.commons.MongoDBObject

// http://etorreborre.github.io/specs2/

class ReadCommonLogFileTest extends Specification {

  "The mongo query for CLF logs" should {
    "have at least 1 entry for httpStatus = 200" in {

      val res = CommonLogFileDAO.find(ref = MongoDBObject("httpStatus" -> "200"))
        .sort(orderBy = MongoDBObject("_id" -> -1)) // sort by _id desc
        .skip(1)
        .limit(1)
        .toList

      res must have size (1)
    }
  }

  "The mongo query for CLF logs" should {
    "have 6134 entries for httpStatus = 200" in {

      val res = CommonLogFileDAO.find(ref = MongoDBObject("httpStatus" -> "200"))
        .sort(orderBy = MongoDBObject("_id" -> -1)) // sort by _id desc
        .toList

      res must have size (6134)
    }
  }

}
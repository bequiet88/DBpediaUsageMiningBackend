package de.unimannheim.dws.models.postgre


object DetailedErrorCode extends Enumeration {
  type DetailedErrorCodeType = Integer
  val Unspecific = -1
}

class ModelException(detailedErrorCode: DetailedErrorCode.DetailedErrorCodeType, msg: String) extends RuntimeException(msg) {

}

object ModelException {
  import DetailedErrorCode._
  def apply(msg: String) = new ModelException(Unspecific, msg)

}
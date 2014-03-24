//package de.unimannheim.dws.models.postgre
//
//import scala.slick.lifted.MappedTypeMapper
//import slick.driver.PostgresDriver.simple._
//import slick.lifted.Query
//import java.sql.Timestamp
//import java.util.Date
//import java.util.UUID
//import de.unimannheim.dws.models.postgre.ModelException
//
//object Util {
//  def currentTimestamp = new Timestamp(new Date().getTime())
//}
//
//object app {
//    var db: Database = null
//}
//
//abstract class Model {
//  def mType = getClass.getSimpleName.toLowerCase
//}
//
//abstract class BaseModel extends Model {
//  val id: Option[Long]
//  def setId(id: Long): BaseModel
//}
//
//trait TypeMappers {
//  implicit val boolTypeMapper = MappedTypeMapper.base[java.util.Date, java.sql.Timestamp](
//    date => new Timestamp(date.getTime()),
//    timestamp => new Date(timestamp.getTime()))
//
//  implicit val jsObjectTypeMapper = MappedTypeMapper.base[JsValue, String](
//    jsVal => Json.stringify(jsVal),
//    str => Json.parse(str))
//
//  implicit val uuidTypeMapper = MappedTypeMapper.base[UUID, String](
//    uuid => uuid.toString,
//    str => UUID.fromString(str))
//}
//
//abstract class BaseTable[M <: BaseModel](override val tableName: String) extends Table[M](tableName) with TypeMappers {
//
//  def id = column[Long]("id")
//
//}
//
//abstract class BaseModelDAO[T <: BaseTable[M], M <: BaseModel](queryy: Query[T, M], table: BaseTable[M]) {
//  import app.db
//
//  protected val query = queryy
//
//  def doInsert(o: M): Option[Long]
//  def doUpdate(o: M)(implicit q: Query[T, M]): Int
//
//  def create(o: M): Option[Long] = db withTransaction {
//    doInsert(o)
//  }
//
//  /**
//   * Only to be used within a db withTransaction {} method
//   */
//  def createWithoutCommit(o: M): Option[Long] = {
//    create(o: M)
//  }
//
//  def update(o: M, id: Long = -1)(implicit q: Query[T, M] = query) = db withTransaction {
//    updateWithoutCommit(o, id)
//  }
//
//  /**
//   * Only to be used within a db withTransaction {} method
//   */
//  def updateWithoutCommit(o: M, id: Long = -1)(implicit q: Query[T, M] = query) = {
//    var _o = idCheck(o, id)
//    doUpdate(_o)(q.filter(_.id === _o.id))
//  }
//
//  private def idCheck(o: M, id: Long) = {
//    o.id match {
//      case None if id <= 0 => throw ModelException("Id not specified. Instance attribute is None and id parameter has default value }")
//      case None if id > 0 => o.setId(id).asInstanceOf[M]
//      case Some(oid) if id <= 0 => o
//      case Some(oid) if oid == id => o
//      case Some(oid) if oid != id => throw ModelException("Ambiguous Ids: param ${id}; instance ${o.id.get} ")
//      case _ => throw ModelException("Unspecified Id exception")
//    }
//  }
//
//  def delete(o: M)(implicit q: Query[T, M] = query): Unit = deleteById(id = o.id.get)
//  def deleteById(id: Long)(implicit q: Query[T, M] = query): Unit = db withSession { q.where(_.id === id).mutate(_.delete) }
//
//  def findById(id: Long)(implicit q: Query[T, M] = query): Option[M] = db withSession {
//    q.where(_.id === id).take(1).firstOption
//  }
//
//  def exists(id: Long)(implicit q: Query[T, M] = query): Boolean =
//    Query(q.where(_.id === id).exists).first
//
//  def where(f: (T => Column[Boolean]))(implicit q: Query[T, M] = query) = db withSession { q.where(f).list }
//
//  def all(implicit q: Query[T, M] = query): List[M] = db withSession { q.list }
//}

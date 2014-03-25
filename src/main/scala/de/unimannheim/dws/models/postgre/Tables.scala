package de.unimannheim.dws.models.postgre

import java.sql.Timestamp

//scala.slick.model.codegen.SourceCodeGenerator.main(
//  Array("scala.slick.driver.PostgresDriver", "org.postgresql.Driver", "jdbc:postgresql://localhost/usage_mining", "D:/data_server/eclipse/DBpediaUsageMining/src/main/scala/", "de.unimannheim.dws.models.postgre", "postgres", "postgres")
//)

// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = scala.slick.driver.PostgresDriver
} with Tables

object dbConn {

  import scala.slick.driver.PostgresDriver.simple.{ Database, Session }
  val ds = new org.postgresql.ds.PGSimpleDataSource
  ds.setDatabaseName("usage_mining")
  ds.setUser("postgres")
  ds.setPassword("postgres")
//  var db: Database = null
  def openConn = {
    Database.forDataSource(ds)
    //        db = Database.forURL("jdbc:postgres://postgres:postgres@localhost/usage_mining", driver = "org.postgresql.Driver")
  }
}

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: scala.slick.driver.JdbcProfile
  import profile.simple._
  import scala.slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import scala.slick.jdbc.{ GetResult => GR }

  /** DDL for all tables. Call .create to execute. */
  lazy val ddl = EntityAnalytics.ddl ++ PropertyAnalytics.ddl ++ SimpleTriples.ddl ++ SparqlQueries.ddl ++ UserSessions.ddl

  /**
   * Entity class storing rows of table EntityAnalytics
   *  @param id Database column id AutoInc, PrimaryKey
   *  @param sessionid Database column sessionId
   *  @param queryid Database column queryId
   *  @param tripleid Database column tripleId
   *  @param classid Database column classId
   *  @param entityid Database column entityId
   *  @param createdat Database column createdAt
   */
  case class EntityAnalyticsRow(id: Long, sessionid: Option[Long], queryid: Option[Long], tripleid: Option[Long], classid: Option[String], entityid: Option[String], createdat: Option[java.sql.Timestamp] = Some(new Timestamp(System.currentTimeMillis()/1000)))
  /** GetResult implicit for fetching EntityAnalyticsRow objects using plain SQL queries */
  implicit def GetResultEntityAnalyticsRow(implicit e0: GR[Long], e1: GR[Option[Long]], e2: GR[Option[String]], e3: GR[Option[java.sql.Timestamp]]): GR[EntityAnalyticsRow] = GR {
    prs =>
      import prs._
      EntityAnalyticsRow.tupled((<<[Long], <<?[Long], <<?[Long], <<?[Long], <<?[String], <<?[String], <<?[java.sql.Timestamp]))
  }
  /** Table description of table entity_analytics. Objects of this class serve as prototypes for rows in queries. */
  class EntityAnalytics(tag: Tag) extends Table[EntityAnalyticsRow](tag, "entity_analytics") {
    def * = (id, sessionid, queryid, tripleid, classid, entityid, createdat) <> (EntityAnalyticsRow.tupled, EntityAnalyticsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, sessionid, queryid, tripleid, classid, entityid, createdat).shaped.<>({ r => import r._; _1.map(_ => EntityAnalyticsRow.tupled((_1.get, _2, _3, _4, _5, _6, _7))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column id AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column sessionId  */
    val sessionid: Column[Option[Long]] = column[Option[Long]]("sessionId")
    /** Database column queryId  */
    val queryid: Column[Option[Long]] = column[Option[Long]]("queryId")
    /** Database column tripleId  */
    val tripleid: Column[Option[Long]] = column[Option[Long]]("tripleId")
    /** Database column classId  */
    val classid: Column[Option[String]] = column[Option[String]]("classId")
    /** Database column entityId  */
    val entityid: Column[Option[String]] = column[Option[String]]("entityId")
    /** Database column createdAt  */
    val createdat: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("createdAt")
  }
  /** Collection-like TableQuery object for table EntityAnalytics */
  lazy val EntityAnalytics = new TableQuery(tag => new EntityAnalytics(tag))

  /**
   * Entity class storing rows of table PropertyAnalytics
   *  @param id Database column id AutoInc, PrimaryKey
   *  @param sessionid Database column sessionId
   *  @param queryid Database column queryId
   *  @param tripleid Database column tripleId
   *  @param createdat Database column createdAt
   *  @param propertyid Database column propertyId
   */
  case class PropertyAnalyticsRow(id: Long, sessionid: Option[Long], queryid: Option[Long], tripleid: Option[Long], createdat: Option[java.sql.Timestamp]  = Some(new Timestamp(System.currentTimeMillis()/1000)), propertyid: Option[String])
  /** GetResult implicit for fetching PropertyAnalyticsRow objects using plain SQL queries */
  implicit def GetResultPropertyAnalyticsRow(implicit e0: GR[Long], e1: GR[Option[Long]], e2: GR[Option[java.sql.Timestamp]], e3: GR[Option[String]]): GR[PropertyAnalyticsRow] = GR {
    prs =>
      import prs._
      PropertyAnalyticsRow.tupled((<<[Long], <<?[Long], <<?[Long], <<?[Long], <<?[java.sql.Timestamp], <<?[String]))
  }
  /** Table description of table property_analytics. Objects of this class serve as prototypes for rows in queries. */
  class PropertyAnalytics(tag: Tag) extends Table[PropertyAnalyticsRow](tag, "property_analytics") {
    def * = (id, sessionid, queryid, tripleid, createdat, propertyid) <> (PropertyAnalyticsRow.tupled, PropertyAnalyticsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, sessionid, queryid, tripleid, createdat, propertyid).shaped.<>({ r => import r._; _1.map(_ => PropertyAnalyticsRow.tupled((_1.get, _2, _3, _4, _5, _6))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column id AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column sessionId  */
    val sessionid: Column[Option[Long]] = column[Option[Long]]("sessionId")
    /** Database column queryId  */
    val queryid: Column[Option[Long]] = column[Option[Long]]("queryId")
    /** Database column tripleId  */
    val tripleid: Column[Option[Long]] = column[Option[Long]]("tripleId")
    /** Database column createdAt  */
    val createdat: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("createdAt")
    /** Database column propertyId  */
    val propertyid: Column[Option[String]] = column[Option[String]]("propertyId")
  }
  /** Collection-like TableQuery object for table PropertyAnalytics */
  lazy val PropertyAnalytics = new TableQuery(tag => new PropertyAnalytics(tag))

  /**
   * Entity class storing rows of table SimpleTriples
   *  @param id Database column id AutoInc, PrimaryKey
   *  @param subjType Database column subj_type
   *  @param subjPrefix Database column subj_prefix
   *  @param subjEntity Database column subj_entity
   *  @param predType Database column pred_type
   *  @param predPrefix Database column pred_prefix
   *  @param predProp Database column pred_prop
   *  @param objType Database column obj_type
   *  @param objPrefix Database column obj_prefix
   *  @param objEntity Database column obj_entity
   *  @param queryid Database column queryId
   *  @param createdat Database column createdAt
   */
  case class SimpleTriplesRow(id: Long, subjType: Option[String], subjPrefix: Option[String], subjEntity: Option[String], predType: Option[String], predPrefix: Option[String], predProp: Option[String], objType: Option[String], objPrefix: Option[String], objEntity: Option[String], queryid: Option[Long], createdat: Option[java.sql.Timestamp] = Some(new Timestamp(System.currentTimeMillis()/1000)))
  /** GetResult implicit for fetching SimpleTriplesRow objects using plain SQL queries */
  implicit def GetResultSimpleTriplesRow(implicit e0: GR[Long], e1: GR[Option[String]], e2: GR[Option[Long]], e3: GR[Option[java.sql.Timestamp]]): GR[SimpleTriplesRow] = GR {
    prs =>
      import prs._
      SimpleTriplesRow.tupled((<<[Long], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[Long], <<?[java.sql.Timestamp]))
  }
  /** Table description of table simple_triples. Objects of this class serve as prototypes for rows in queries. */
  class SimpleTriples(tag: Tag) extends Table[SimpleTriplesRow](tag, "simple_triples") {
    def * = (id, subjType, subjPrefix, subjEntity, predType, predPrefix, predProp, objType, objPrefix, objEntity, queryid, createdat) <> (SimpleTriplesRow.tupled, SimpleTriplesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, subjType, subjPrefix, subjEntity, predType, predPrefix, predProp, objType, objPrefix, objEntity, queryid, createdat).shaped.<>({ r => import r._; _1.map(_ => SimpleTriplesRow.tupled((_1.get, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column id AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column subj_type  */
    val subjType: Column[Option[String]] = column[Option[String]]("subj_type")
    /** Database column subj_prefix  */
    val subjPrefix: Column[Option[String]] = column[Option[String]]("subj_prefix")
    /** Database column subj_entity  */
    val subjEntity: Column[Option[String]] = column[Option[String]]("subj_entity")
    /** Database column pred_type  */
    val predType: Column[Option[String]] = column[Option[String]]("pred_type")
    /** Database column pred_prefix  */
    val predPrefix: Column[Option[String]] = column[Option[String]]("pred_prefix")
    /** Database column pred_prop  */
    val predProp: Column[Option[String]] = column[Option[String]]("pred_prop")
    /** Database column obj_type  */
    val objType: Column[Option[String]] = column[Option[String]]("obj_type")
    /** Database column obj_prefix  */
    val objPrefix: Column[Option[String]] = column[Option[String]]("obj_prefix")
    /** Database column obj_entity  */
    val objEntity: Column[Option[String]] = column[Option[String]]("obj_entity")
    /** Database column queryId  */
    val queryid: Column[Option[Long]] = column[Option[Long]]("queryId")
    /** Database column createdAt  */
    val createdat: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("createdAt")
  }
  /** Collection-like TableQuery object for table SimpleTriples */
  lazy val SimpleTriples = new TableQuery(tag => new SimpleTriples(tag))

  /**
   * Entity class storing rows of table SparqlQueries
   *  @param id Database column id AutoInc, PrimaryKey
   *  @param query Database column query
   *  @param containserrors Database column containsErrors
   *  @param sessionid Database column sessionId
   *  @param createdat Database column createdAt
   */
  case class SparqlQueriesRow(id: Long, query: Option[String], containserrors: Option[String], sessionid: Option[Long], createdat: Option[java.sql.Timestamp] = Some(new Timestamp(System.currentTimeMillis()/1000)))
  /** GetResult implicit for fetching SparqlQueriesRow objects using plain SQL queries */
  implicit def GetResultSparqlQueriesRow(implicit e0: GR[Long], e1: GR[Option[String]], e2: GR[Option[Long]], e3: GR[Option[java.sql.Timestamp]]): GR[SparqlQueriesRow] = GR {
    prs =>
      import prs._
      SparqlQueriesRow.tupled((<<[Long], <<?[String], <<?[String], <<?[Long], <<?[java.sql.Timestamp]))
  }
  /** Table description of table sparql_queries. Objects of this class serve as prototypes for rows in queries. */
  class SparqlQueries(tag: Tag) extends Table[SparqlQueriesRow](tag, "sparql_queries") {
    def * = (id, query, containserrors, sessionid, createdat) <> (SparqlQueriesRow.tupled, SparqlQueriesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, query, containserrors, sessionid, createdat).shaped.<>({ r => import r._; _1.map(_ => SparqlQueriesRow.tupled((_1.get, _2, _3, _4, _5))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column id AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column query  */
    val query: Column[Option[String]] = column[Option[String]]("query")
    /** Database column containsErrors  */
    val containserrors: Column[Option[String]] = column[Option[String]]("containsErrors")
    /** Database column sessionId  */
    val sessionid: Column[Option[Long]] = column[Option[Long]]("sessionId")
    /** Database column createdAt  */
    val createdat: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("createdAt")
  }
  /** Collection-like TableQuery object for table SparqlQueries */
  lazy val SparqlQueries = new TableQuery(tag => new SparqlQueries(tag))

  /**
   * Entity class storing rows of table UserSessions
   *  @param id Database column id AutoInc, PrimaryKey
   *  @param ip Database column ip
   *  @param timeFrom Database column time_from
   *  @param timeTo Database column time_to
   *  @param createdAt Database column created_at
   */
  case class UserSessionsRow(id: Long, ip: Option[String], timeFrom: Option[java.sql.Timestamp], timeTo: Option[java.sql.Timestamp], createdAt: Option[java.sql.Timestamp] = Some(new Timestamp(System.currentTimeMillis()/1000)))
  /** GetResult implicit for fetching UserSessionsRow objects using plain SQL queries */
  implicit def GetResultUserSessionsRow(implicit e0: GR[Long], e1: GR[Option[String]], e2: GR[Option[java.sql.Timestamp]], e3: GR[Option[java.sql.Timestamp]]): GR[UserSessionsRow] = GR {
    prs =>
      import prs._
      UserSessionsRow.tupled((<<[Long], <<?[String], <<?[java.sql.Timestamp], <<?[java.sql.Timestamp], <<?[java.sql.Timestamp]))
  }
  /** Table description of table user_sessions. Objects of this class serve as prototypes for rows in queries. */
  class UserSessions(tag: Tag) extends Table[UserSessionsRow](tag, "user_sessions") {
    def * = (id, ip, timeFrom, timeTo, createdAt) <> (UserSessionsRow.tupled, UserSessionsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, ip, timeFrom, timeTo, createdAt).shaped.<>({ r => import r._; _1.map(_ => UserSessionsRow.tupled((_1.get, _2, _3, _4, _5))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column id AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column ip  */
    val ip: Column[Option[String]] = column[Option[String]]("ip")
    /** Database column time_from  */
    val timeFrom: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("time_from")
    /** Database column time_to  */
    val timeTo: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("time_to")
    /** Database column created_at  */
    val createdAt: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("created_at")
  }
  /** Collection-like TableQuery object for table UserSessions */
  lazy val UserSessions = new TableQuery(tag => new UserSessions(tag))
}
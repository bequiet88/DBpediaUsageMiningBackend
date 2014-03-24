package de.unimannheim.dws.models.postgre
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = scala.slick.driver.PostgresDriver
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: scala.slick.driver.JdbcProfile
  import profile.simple._
  import scala.slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import scala.slick.jdbc.{GetResult => GR}
  
  /** DDL for all tables. Call .create to execute. */
  lazy val ddl = SimpleTriples.ddl ++ SparqlQueries.ddl ++ UserSessions.ddl
  
  /** Entity class storing rows of table SimpleTriples
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
   *  @param queryid Database column queryId  */
  case class SimpleTriplesRow(id: Long, subjType: Option[String], subjPrefix: Option[String], subjEntity: Option[String], predType: Option[String], predPrefix: Option[String], predProp: Option[String], objType: Option[String], objPrefix: Option[String], objEntity: Option[String], queryid: Option[Long])
  /** GetResult implicit for fetching SimpleTriplesRow objects using plain SQL queries */
  implicit def GetResultSimpleTriplesRow(implicit e0: GR[Long], e1: GR[Option[String]], e2: GR[Option[Long]]): GR[SimpleTriplesRow] = GR{
    prs => import prs._
    SimpleTriplesRow.tupled((<<[Long], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[Long]))
  }
  /** Table description of table simple_triples. Objects of this class serve as prototypes for rows in queries. */
  class SimpleTriples(tag: Tag) extends Table[SimpleTriplesRow](tag, "simple_triples") {
    def * = (id, subjType, subjPrefix, subjEntity, predType, predPrefix, predProp, objType, objPrefix, objEntity, queryid) <> (SimpleTriplesRow.tupled, SimpleTriplesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, subjType, subjPrefix, subjEntity, predType, predPrefix, predProp, objType, objPrefix, objEntity, queryid).shaped.<>({r=>import r._; _1.map(_=> SimpleTriplesRow.tupled((_1.get, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
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
  }
  /** Collection-like TableQuery object for table SimpleTriples */
  lazy val SimpleTriples = new TableQuery(tag => new SimpleTriples(tag))
  
  /** Entity class storing rows of table SparqlQueries
   *  @param id Database column id AutoInc, PrimaryKey
   *  @param query Database column query 
   *  @param containserrors Database column containsErrors 
   *  @param sessionid Database column sessionId  */
  case class SparqlQueriesRow(id: Long, query: Option[String], containserrors: Option[String], sessionid: Option[Long])
  /** GetResult implicit for fetching SparqlQueriesRow objects using plain SQL queries */
  implicit def GetResultSparqlQueriesRow(implicit e0: GR[Long], e1: GR[Option[String]], e2: GR[Option[Long]]): GR[SparqlQueriesRow] = GR{
    prs => import prs._
    SparqlQueriesRow.tupled((<<[Long], <<?[String], <<?[String], <<?[Long]))
  }
  /** Table description of table sparql_queries. Objects of this class serve as prototypes for rows in queries. */
  class SparqlQueries(tag: Tag) extends Table[SparqlQueriesRow](tag, "sparql_queries") {
    def * = (id, query, containserrors, sessionid) <> (SparqlQueriesRow.tupled, SparqlQueriesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, query, containserrors, sessionid).shaped.<>({r=>import r._; _1.map(_=> SparqlQueriesRow.tupled((_1.get, _2, _3, _4)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column query  */
    val query: Column[Option[String]] = column[Option[String]]("query")
    /** Database column containsErrors  */
    val containserrors: Column[Option[String]] = column[Option[String]]("containsErrors")
    /** Database column sessionId  */
    val sessionid: Column[Option[Long]] = column[Option[Long]]("sessionId")
  }
  /** Collection-like TableQuery object for table SparqlQueries */
  lazy val SparqlQueries = new TableQuery(tag => new SparqlQueries(tag))
  
  /** Entity class storing rows of table UserSessions
   *  @param id Database column id AutoInc, PrimaryKey
   *  @param ip Database column ip 
   *  @param timeFrom Database column time_from 
   *  @param timeTo Database column time_to 
   *  @param createdAt Database column created_at  */
  case class UserSessionsRow(id: Long, ip: Option[String], timeFrom: Option[java.sql.Timestamp], timeTo: Option[java.sql.Timestamp], createdAt: Option[java.sql.Time])
  /** GetResult implicit for fetching UserSessionsRow objects using plain SQL queries */
  implicit def GetResultUserSessionsRow(implicit e0: GR[Long], e1: GR[Option[String]], e2: GR[Option[java.sql.Timestamp]], e3: GR[Option[java.sql.Time]]): GR[UserSessionsRow] = GR{
    prs => import prs._
    UserSessionsRow.tupled((<<[Long], <<?[String], <<?[java.sql.Timestamp], <<?[java.sql.Timestamp], <<?[java.sql.Time]))
  }
  /** Table description of table user_sessions. Objects of this class serve as prototypes for rows in queries. */
  class UserSessions(tag: Tag) extends Table[UserSessionsRow](tag, "user_sessions") {
    def * = (id, ip, timeFrom, timeTo, createdAt) <> (UserSessionsRow.tupled, UserSessionsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, ip, timeFrom, timeTo, createdAt).shaped.<>({r=>import r._; _1.map(_=> UserSessionsRow.tupled((_1.get, _2, _3, _4, _5)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column ip  */
    val ip: Column[Option[String]] = column[Option[String]]("ip")
    /** Database column time_from  */
    val timeFrom: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("time_from")
    /** Database column time_to  */
    val timeTo: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("time_to")
    /** Database column created_at  */
    val createdAt: Column[Option[java.sql.Time]] = column[Option[java.sql.Time]]("created_at")
  }
  /** Collection-like TableQuery object for table UserSessions */
  lazy val UserSessions = new TableQuery(tag => new UserSessions(tag))
}
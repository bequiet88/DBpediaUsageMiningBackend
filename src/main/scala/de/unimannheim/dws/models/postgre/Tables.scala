package de.unimannheim.dws.models.postgre

import java.sql.Timestamp
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
  lazy val ddl = ClassesUnique.ddl ++ ClassPropertyCounter.ddl ++ EntitiesUnique.ddl ++ EntityAnalytics.ddl ++ EntityPropertyWeight.ddl ++ PairCounter.ddl ++ PropertiesUnique.ddl ++ PropertyAnalytics.ddl ++ SimpleTriples.ddl ++ SparqlQueries.ddl ++ UserSessions.ddl
  
  /** Entity class storing rows of table ClassesUnique
   *  @param id Database column id 
   *  @param label Database column label  */
  case class ClassesUniqueRow(id: String, label: Option[String])
  /** GetResult implicit for fetching ClassesUniqueRow objects using plain SQL queries */
  implicit def GetResultClassesUniqueRow(implicit e0: GR[String], e1: GR[Option[String]]): GR[ClassesUniqueRow] = GR{
    prs => import prs._
    ClassesUniqueRow.tupled((<<[String], <<?[String]))
  }
  /** Table description of table classes_unique. Objects of this class serve as prototypes for rows in queries. */
  class ClassesUnique(tag: Tag) extends Table[ClassesUniqueRow](tag, "classes_unique") {
    def * = (id, label) <> (ClassesUniqueRow.tupled, ClassesUniqueRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, label).shaped.<>({r=>import r._; _1.map(_=> ClassesUniqueRow.tupled((_1.get, _2)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id  */
    val id: Column[String] = column[String]("id")
    /** Database column label  */
    val label: Column[Option[String]] = column[Option[String]]("label")
  }
  /** Collection-like TableQuery object for table ClassesUnique */
  lazy val ClassesUnique = new TableQuery(tag => new ClassesUnique(tag))
  
  /** Entity class storing rows of table ClassPropertyCounter
   *  @param classId Database column class_id 
   *  @param propertyId Database column property_id 
   *  @param count Database column count  */
  case class ClassPropertyCounterRow(classId: String, propertyId: String, count: Option[Int])
  /** GetResult implicit for fetching ClassPropertyCounterRow objects using plain SQL queries */
  implicit def GetResultClassPropertyCounterRow(implicit e0: GR[String], e1: GR[Option[Int]]): GR[ClassPropertyCounterRow] = GR{
    prs => import prs._
    ClassPropertyCounterRow.tupled((<<[String], <<[String], <<?[Int]))
  }
  /** Table description of table class_property_counter. Objects of this class serve as prototypes for rows in queries. */
  class ClassPropertyCounter(tag: Tag) extends Table[ClassPropertyCounterRow](tag, "class_property_counter") {
    def * = (classId, propertyId, count) <> (ClassPropertyCounterRow.tupled, ClassPropertyCounterRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (classId.?, propertyId.?, count).shaped.<>({r=>import r._; _1.map(_=> ClassPropertyCounterRow.tupled((_1.get, _2.get, _3)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column class_id  */
    val classId: Column[String] = column[String]("class_id")
    /** Database column property_id  */
    val propertyId: Column[String] = column[String]("property_id")
    /** Database column count  */
    val count: Column[Option[Int]] = column[Option[Int]]("count")
  }
  /** Collection-like TableQuery object for table ClassPropertyCounter */
  lazy val ClassPropertyCounter = new TableQuery(tag => new ClassPropertyCounter(tag))
  
  /** Entity class storing rows of table EntitiesUnique
   *  @param id Database column id 
   *  @param prefix Database column prefix 
   *  @param entity Database column entity 
   *  @param classId Database column class_id  */
  case class EntitiesUniqueRow(id: String, prefix: Option[String], entity: Option[String], classId: Option[String])
  /** GetResult implicit for fetching EntitiesUniqueRow objects using plain SQL queries */
  implicit def GetResultEntitiesUniqueRow(implicit e0: GR[String], e1: GR[Option[String]]): GR[EntitiesUniqueRow] = GR{
    prs => import prs._
    EntitiesUniqueRow.tupled((<<[String], <<?[String], <<?[String], <<?[String]))
  }
  /** Table description of table entities_unique. Objects of this class serve as prototypes for rows in queries. */
  class EntitiesUnique(tag: Tag) extends Table[EntitiesUniqueRow](tag, "entities_unique") {
    def * = (id, prefix, entity, classId) <> (EntitiesUniqueRow.tupled, EntitiesUniqueRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, prefix, entity, classId).shaped.<>({r=>import r._; _1.map(_=> EntitiesUniqueRow.tupled((_1.get, _2, _3, _4)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id  */
    val id: Column[String] = column[String]("id")
    /** Database column prefix  */
    val prefix: Column[Option[String]] = column[Option[String]]("prefix")
    /** Database column entity  */
    val entity: Column[Option[String]] = column[Option[String]]("entity")
    /** Database column class_id  */
    val classId: Column[Option[String]] = column[Option[String]]("class_id")
  }
  /** Collection-like TableQuery object for table EntitiesUnique */
  lazy val EntitiesUnique = new TableQuery(tag => new EntitiesUnique(tag))
  
  /** Entity class storing rows of table EntityAnalytics
   *  @param id Database column id AutoInc
   *  @param sessionId Database column session_id 
   *  @param queryId Database column query_id 
   *  @param tripleId Database column triple_id 
   *  @param classId Database column class_id 
   *  @param entityId Database column entity_id 
   *  @param createdAt Database column created_at  */
  case class EntityAnalyticsRow(id: Long, sessionId: Option[Long], queryId: Option[Long], tripleId: Option[Long], classId: Option[String], entityId: Option[String], createdAt: Option[java.sql.Timestamp] = Some(new Timestamp(System.currentTimeMillis())))
  /** GetResult implicit for fetching EntityAnalyticsRow objects using plain SQL queries */
  implicit def GetResultEntityAnalyticsRow(implicit e0: GR[Long], e1: GR[Option[Long]], e2: GR[Option[String]], e3: GR[Option[java.sql.Timestamp]]): GR[EntityAnalyticsRow] = GR{
    prs => import prs._
    EntityAnalyticsRow.tupled((<<[Long], <<?[Long], <<?[Long], <<?[Long], <<?[String], <<?[String], <<?[java.sql.Timestamp]))
  }
  /** Table description of table entity_analytics. Objects of this class serve as prototypes for rows in queries. */
  class EntityAnalytics(tag: Tag) extends Table[EntityAnalyticsRow](tag, "entity_analytics") {
    def * = (id, sessionId, queryId, tripleId, classId, entityId, createdAt) <> (EntityAnalyticsRow.tupled, EntityAnalyticsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, sessionId, queryId, tripleId, classId, entityId, createdAt).shaped.<>({r=>import r._; _1.map(_=> EntityAnalyticsRow.tupled((_1.get, _2, _3, _4, _5, _6, _7)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id AutoInc */
    val id: Column[Long] = column[Long]("id", O.AutoInc)
    /** Database column session_id  */
    val sessionId: Column[Option[Long]] = column[Option[Long]]("session_id")
    /** Database column query_id  */
    val queryId: Column[Option[Long]] = column[Option[Long]]("query_id")
    /** Database column triple_id  */
    val tripleId: Column[Option[Long]] = column[Option[Long]]("triple_id")
    /** Database column class_id  */
    val classId: Column[Option[String]] = column[Option[String]]("class_id")
    /** Database column entity_id  */
    val entityId: Column[Option[String]] = column[Option[String]]("entity_id")
    /** Database column created_at  */
    val createdAt: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("created_at")
  }
  /** Collection-like TableQuery object for table EntityAnalytics */
  lazy val EntityAnalytics = new TableQuery(tag => new EntityAnalytics(tag))
  
  /** Entity class storing rows of table EntityPropertyWeight
   *  @param entityId Database column entity_id 
   *  @param propertyId Database column property_id 
   *  @param weight Database column weight  */
  case class EntityPropertyWeightRow(entityId: String, propertyId: String, weight: Option[Double])
  /** GetResult implicit for fetching EntityPropertyWeightRow objects using plain SQL queries */
  implicit def GetResultEntityPropertyWeightRow(implicit e0: GR[String], e1: GR[Option[Double]]): GR[EntityPropertyWeightRow] = GR{
    prs => import prs._
    EntityPropertyWeightRow.tupled((<<[String], <<[String], <<?[Double]))
  }
  /** Table description of table entity_property_weight. Objects of this class serve as prototypes for rows in queries. */
  class EntityPropertyWeight(tag: Tag) extends Table[EntityPropertyWeightRow](tag, "entity_property_weight") {
    def * = (entityId, propertyId, weight) <> (EntityPropertyWeightRow.tupled, EntityPropertyWeightRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (entityId.?, propertyId.?, weight).shaped.<>({r=>import r._; _1.map(_=> EntityPropertyWeightRow.tupled((_1.get, _2.get, _3)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column entity_id  */
    val entityId: Column[String] = column[String]("entity_id")
    /** Database column property_id  */
    val propertyId: Column[String] = column[String]("property_id")
    /** Database column weight  */
    val weight: Column[Option[Double]] = column[Option[Double]]("weight")
  }
  /** Collection-like TableQuery object for table EntityPropertyWeight */
  lazy val EntityPropertyWeight = new TableQuery(tag => new EntityPropertyWeight(tag))
  
  /** Entity class storing rows of table PairCounter
   *  @param prop1Id Database column prop_1_id 
   *  @param prop2Id Database column prop_2_id 
   *  @param count Database column count  */
  case class PairCounterRow(prop1Id: String, prop2Id: String, count: Option[Int])
  /** GetResult implicit for fetching PairCounterRow objects using plain SQL queries */
  implicit def GetResultPairCounterRow(implicit e0: GR[String], e1: GR[Option[Int]]): GR[PairCounterRow] = GR{
    prs => import prs._
    PairCounterRow.tupled((<<[String], <<[String], <<?[Int]))
  }
  /** Table description of table pair_counter. Objects of this class serve as prototypes for rows in queries. */
  class PairCounter(tag: Tag) extends Table[PairCounterRow](tag, "pair_counter") {
    def * = (prop1Id, prop2Id, count) <> (PairCounterRow.tupled, PairCounterRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (prop1Id.?, prop2Id.?, count).shaped.<>({r=>import r._; _1.map(_=> PairCounterRow.tupled((_1.get, _2.get, _3)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column prop_1_id  */
    val prop1Id: Column[String] = column[String]("prop_1_id")
    /** Database column prop_2_id  */
    val prop2Id: Column[String] = column[String]("prop_2_id")
    /** Database column count  */
    val count: Column[Option[Int]] = column[Option[Int]]("count")
  }
  /** Collection-like TableQuery object for table PairCounter */
  lazy val PairCounter = new TableQuery(tag => new PairCounter(tag))
  
  /** Entity class storing rows of table PropertiesUnique
   *  @param id Database column id 
   *  @param prefix Database column prefix 
   *  @param property Database column property 
   *  @param support Database column support  */
  case class PropertiesUniqueRow(id: String, prefix: Option[String], property: Option[String], support: Option[Double])
  /** GetResult implicit for fetching PropertiesUniqueRow objects using plain SQL queries */
  implicit def GetResultPropertiesUniqueRow(implicit e0: GR[String], e1: GR[Option[String]], e2: GR[Option[Double]]): GR[PropertiesUniqueRow] = GR{
    prs => import prs._
    PropertiesUniqueRow.tupled((<<[String], <<?[String], <<?[String], <<?[Double]))
  }
  /** Table description of table properties_unique. Objects of this class serve as prototypes for rows in queries. */
  class PropertiesUnique(tag: Tag) extends Table[PropertiesUniqueRow](tag, "properties_unique") {
    def * = (id, prefix, property, support) <> (PropertiesUniqueRow.tupled, PropertiesUniqueRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, prefix, property, support).shaped.<>({r=>import r._; _1.map(_=> PropertiesUniqueRow.tupled((_1.get, _2, _3, _4)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id  */
    val id: Column[String] = column[String]("id")
    /** Database column prefix  */
    val prefix: Column[Option[String]] = column[Option[String]]("prefix")
    /** Database column property  */
    val property: Column[Option[String]] = column[Option[String]]("property")
    /** Database column support  */
    val support: Column[Option[Double]] = column[Option[Double]]("support")
  }
  /** Collection-like TableQuery object for table PropertiesUnique */
  lazy val PropertiesUnique = new TableQuery(tag => new PropertiesUnique(tag))
  
  /** Entity class storing rows of table PropertyAnalytics
   *  @param id Database column id AutoInc
   *  @param sessionId Database column session_id 
   *  @param queryId Database column query_id 
   *  @param tripleId Database column triple_id 
   *  @param createdAt Database column created_at 
   *  @param propertyId Database column property_id  */
  case class PropertyAnalyticsRow(id: Long, sessionId: Option[Long], queryId: Option[Long], tripleId: Option[Long], createdAt: Option[java.sql.Timestamp] = Some(new Timestamp(System.currentTimeMillis())), propertyId: Option[String])
  /** GetResult implicit for fetching PropertyAnalyticsRow objects using plain SQL queries */
  implicit def GetResultPropertyAnalyticsRow(implicit e0: GR[Long], e1: GR[Option[Long]], e2: GR[Option[java.sql.Timestamp]], e3: GR[Option[String]]): GR[PropertyAnalyticsRow] = GR{
    prs => import prs._
    PropertyAnalyticsRow.tupled((<<[Long], <<?[Long], <<?[Long], <<?[Long], <<?[java.sql.Timestamp], <<?[String]))
  }
  /** Table description of table property_analytics. Objects of this class serve as prototypes for rows in queries. */
  class PropertyAnalytics(tag: Tag) extends Table[PropertyAnalyticsRow](tag, "property_analytics") {
    def * = (id, sessionId, queryId, tripleId, createdAt, propertyId) <> (PropertyAnalyticsRow.tupled, PropertyAnalyticsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, sessionId, queryId, tripleId, createdAt, propertyId).shaped.<>({r=>import r._; _1.map(_=> PropertyAnalyticsRow.tupled((_1.get, _2, _3, _4, _5, _6)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id AutoInc */
    val id: Column[Long] = column[Long]("id", O.AutoInc)
    /** Database column session_id  */
    val sessionId: Column[Option[Long]] = column[Option[Long]]("session_id")
    /** Database column query_id  */
    val queryId: Column[Option[Long]] = column[Option[Long]]("query_id")
    /** Database column triple_id  */
    val tripleId: Column[Option[Long]] = column[Option[Long]]("triple_id")
    /** Database column created_at  */
    val createdAt: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("created_at")
    /** Database column property_id  */
    val propertyId: Column[Option[String]] = column[Option[String]]("property_id")
  }
  /** Collection-like TableQuery object for table PropertyAnalytics */
  lazy val PropertyAnalytics = new TableQuery(tag => new PropertyAnalytics(tag))
  
  /** Entity class storing rows of table SimpleTriples
   *  @param id Database column id AutoInc
   *  @param subjType Database column subj_type 
   *  @param subjPrefix Database column subj_prefix 
   *  @param subjEntity Database column subj_entity 
   *  @param predType Database column pred_type 
   *  @param predPrefix Database column pred_prefix 
   *  @param predProp Database column pred_prop 
   *  @param objType Database column obj_type 
   *  @param objPrefix Database column obj_prefix 
   *  @param objEntity Database column obj_entity 
   *  @param queryId Database column query_id 
   *  @param createdAt Database column created_at  */
  case class SimpleTriplesRow(id: Long, subjType: Option[String], subjPrefix: Option[String], subjEntity: Option[String], predType: Option[String], predPrefix: Option[String], predProp: Option[String], objType: Option[String], objPrefix: Option[String], objEntity: Option[String], queryId: Option[Long], createdAt: Option[java.sql.Timestamp] = Some(new Timestamp(System.currentTimeMillis())))
  /** GetResult implicit for fetching SimpleTriplesRow objects using plain SQL queries */
  implicit def GetResultSimpleTriplesRow(implicit e0: GR[Long], e1: GR[Option[String]], e2: GR[Option[Long]], e3: GR[Option[java.sql.Timestamp]]): GR[SimpleTriplesRow] = GR{
    prs => import prs._
    SimpleTriplesRow.tupled((<<[Long], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[Long], <<?[java.sql.Timestamp]))
  }
  /** Table description of table simple_triples. Objects of this class serve as prototypes for rows in queries. */
  class SimpleTriples(tag: Tag) extends Table[SimpleTriplesRow](tag, "simple_triples") {
    def * = (id, subjType, subjPrefix, subjEntity, predType, predPrefix, predProp, objType, objPrefix, objEntity, queryId, createdAt) <> (SimpleTriplesRow.tupled, SimpleTriplesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, subjType, subjPrefix, subjEntity, predType, predPrefix, predProp, objType, objPrefix, objEntity, queryId, createdAt).shaped.<>({r=>import r._; _1.map(_=> SimpleTriplesRow.tupled((_1.get, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id AutoInc */
    val id: Column[Long] = column[Long]("id", O.AutoInc)
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
    /** Database column query_id  */
    val queryId: Column[Option[Long]] = column[Option[Long]]("query_id")
    /** Database column created_at  */
    val createdAt: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("created_at")
  }
  /** Collection-like TableQuery object for table SimpleTriples */
  lazy val SimpleTriples = new TableQuery(tag => new SimpleTriples(tag))
  
  /** Entity class storing rows of table SparqlQueries
   *  @param id Database column id AutoInc
   *  @param query Database column query 
   *  @param containsErrors Database column contains_errors 
   *  @param sessionId Database column session_id 
   *  @param createdAt Database column created_at  */
  case class SparqlQueriesRow(id: Long, query: Option[String], containsErrors: Option[String], sessionId: Option[Long], createdAt: Option[java.sql.Timestamp] = Some(new Timestamp(System.currentTimeMillis())))
  /** GetResult implicit for fetching SparqlQueriesRow objects using plain SQL queries */
  implicit def GetResultSparqlQueriesRow(implicit e0: GR[Long], e1: GR[Option[String]], e2: GR[Option[Long]], e3: GR[Option[java.sql.Timestamp]]): GR[SparqlQueriesRow] = GR{
    prs => import prs._
    SparqlQueriesRow.tupled((<<[Long], <<?[String], <<?[String], <<?[Long], <<?[java.sql.Timestamp]))
  }
  /** Table description of table sparql_queries. Objects of this class serve as prototypes for rows in queries. */
  class SparqlQueries(tag: Tag) extends Table[SparqlQueriesRow](tag, "sparql_queries") {
    def * = (id, query, containsErrors, sessionId, createdAt) <> (SparqlQueriesRow.tupled, SparqlQueriesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, query, containsErrors, sessionId, createdAt).shaped.<>({r=>import r._; _1.map(_=> SparqlQueriesRow.tupled((_1.get, _2, _3, _4, _5)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id AutoInc */
    val id: Column[Long] = column[Long]("id", O.AutoInc)
    /** Database column query  */
    val query: Column[Option[String]] = column[Option[String]]("query")
    /** Database column contains_errors  */
    val containsErrors: Column[Option[String]] = column[Option[String]]("contains_errors")
    /** Database column session_id  */
    val sessionId: Column[Option[Long]] = column[Option[Long]]("session_id")
    /** Database column created_at  */
    val createdAt: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("created_at")
  }
  /** Collection-like TableQuery object for table SparqlQueries */
  lazy val SparqlQueries = new TableQuery(tag => new SparqlQueries(tag))
  
  /** Entity class storing rows of table UserSessions
   *  @param id Database column id AutoInc, PrimaryKey
   *  @param ip Database column ip 
   *  @param timeFrom Database column time_from 
   *  @param timeTo Database column time_to 
   *  @param createdAt Database column created_at  */
  case class UserSessionsRow(id: Long, ip: Option[String], timeFrom: Option[java.sql.Timestamp], timeTo: Option[java.sql.Timestamp], createdAt: Option[java.sql.Timestamp] = Some(new Timestamp(System.currentTimeMillis())))
  /** GetResult implicit for fetching UserSessionsRow objects using plain SQL queries */
  implicit def GetResultUserSessionsRow(implicit e0: GR[Long], e1: GR[Option[String]], e2: GR[Option[java.sql.Timestamp]]): GR[UserSessionsRow] = GR{
    prs => import prs._
    UserSessionsRow.tupled((<<[Long], <<?[String], <<?[java.sql.Timestamp], <<?[java.sql.Timestamp], <<?[java.sql.Timestamp]))
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
    val createdAt: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("created_at")
  }
  /** Collection-like TableQuery object for table UserSessions */
  lazy val UserSessions = new TableQuery(tag => new UserSessions(tag))
}
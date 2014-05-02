package de.unimannheim.dws.algorithms

abstract class RankingAlgorithm[T,S] {
  
   def generate()(implicit session: slick.driver.PostgresDriver.backend.Session): List[T]

   def retrieve(triples: List[(String, String, String)], options: Array[String])(implicit session: slick.driver.PostgresDriver.backend.Session): List[S] 
   

}
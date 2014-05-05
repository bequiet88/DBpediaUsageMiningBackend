package de.unimannheim.dws.algorithms

abstract class RankingAlgorithm[T] {

  def generate()(implicit session: slick.driver.PostgresDriver.backend.Session): List[T]

  def retrieve(triples: List[(String, String, String)], options: Array[String])(implicit session: slick.driver.PostgresDriver.backend.Session): List[(String, String, Double)]

  def getRankedTriples(triples: List[(String, String, String)], list: List[(String, String, Double)]): List[((String, String, String), String)] = {

    val groupedTriples = triples.groupBy(_._2)

    val resultCols = list.foldLeft((List[((String, String, String), String)](), groupedTriples)) { (i, row) =>
      {
        val values = i._2.get(row._1).getOrElse(List()).map(t => (t, row._2))

        (i._1 ++ ((values)), i._2.filterNot(r => r._1 == row._1))
      }
    }

    resultCols._2.foldLeft(resultCols._1) { (i, row) =>
      {
    	  val values = row._2.map(t => (t, "noise"))
    	  i++values
      }
    }
  }

}
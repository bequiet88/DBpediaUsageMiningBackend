package de.unimannheim.dws.algorithms

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

abstract class RankingAlgorithm[T, S] {

  /**
   * Generate the aggregates
   */
  def generate()(implicit session: slick.driver.PostgresDriver.backend.Session): List[T]

  /**
   * Retrieve data from ranking algorithm
   */
  def retrieve(triples: List[(String, String, String)], options: Array[String])(implicit session: slick.driver.PostgresDriver.backend.Session): S

  /**
   * Sort the triples
   */
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
        i ++ values
      }
    }
  }

  /**
   * Print the result to file
   */
  def printResults(triples: List[((String, String, String), String)], options: Array[String], clusterInfo: String): Any = {

    val label = triples.head._1._1.split("/").last

    val fileName = options.foldLeft(new StringBuilder())((i, row) => {
      if (i.length() == 0) {
        if (clusterInfo.length() == 0) i.append(label + "_counter_" + row)
        else i.append(label + "_cluster_" + row)
      } else i.append("_" + row)
    })

    val file: File = new File("D:/ownCloud/Data/Studium/Master_Thesis/04_Data_Results/rankings/" + fileName + ".txt");
    file.getParentFile().mkdirs();

    val out: BufferedWriter = new BufferedWriter(new FileWriter(file));

    // clusterInfo
    if (clusterInfo.length() > 0) {
      out.write(clusterInfo)
      out.newLine()
    }

    // Ranked triples
    triples.foldLeft("")((i, row) => {
      if (row._2.equals(i)) {
        out.write(row._2 + " " + row._1._1 + " " + row._1._2 + " " + row._1._3)
        out.newLine()
        row._2
      } else {
        out.newLine()
        out.write("Group " + row._2)
        out.write(row._2 + " " + row._1._1 + " " + row._1._2 + " " + row._1._3)
        out.newLine()
        row._2
      }

    })

    out.flush()
    out.close()

  }

  def printResults(triples: List[((String, String, String), String)], options: Array[String]): Any = {
    printResults(triples, options, "")
  }

}
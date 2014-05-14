import sbt._
import Process._
import Keys._

name := "DBpediaUsageMining"

version := "0.1.0"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  "org.apache.jena" 		% "jena-core" 	 				 % "2.11.0" withSources,
  "com.typesafe.slick" 		%% "slick" 		 				 % "2.0.1" withSources,
  "postgresql"              %  "postgresql"                  % "9.1-901-1.jdbc4" withSources,
  "nz.ac.waikato.cms.weka" 	% "weka-stable" 				 % "3.6.11"
)

scalacOptions in Test ++= Seq("-Yrangepos")

//resolvers ++= Seq(
//  Resolver.url("apache.snapshots.https", new URL("https://repository.apache.org/content/repositories/snapshots/"))
//)

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)
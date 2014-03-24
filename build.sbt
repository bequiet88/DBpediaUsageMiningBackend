import sbt._
import Process._
import Keys._

name := "DBpediaUsageMining"

version := "0.1.0"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  "org.mongodb" 			%% "casbah" 					 % "2.6.5",
  "com.novus" 				%% "salat" 						 % "1.9.5",
  "org.specs2" 				%% "specs2" 					 % "2.3.8" % "test",
  "org.apache.jena" 		% "jena-core" 	 				 % "2.11.0" withSources,
  "org.apache.jena" 		% "jena-arq" 	 				 % "2.11.0" withSources,
  "org.apache.jena" 		% "jena-iri" 	 				 % "1.0.0" withSources,
  "org.apache.jena" 		% "jena-tdb" 	 				 % "1.0.0" withSources,
  "com.typesafe.slick" 		%% "slick" 		 				 % "2.0.1" withSources,
  "postgresql"              %  "postgresql"                  % "9.1-901-1.jdbc4" withSources
)

scalacOptions in Test ++= Seq("-Yrangepos")

//resolvers ++= Seq(
//  Resolver.url("apache.snapshots.https", new URL("https://repository.apache.org/content/repositories/snapshots/"))
//)

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)
import sbt._
import Process._
import Keys._

name := "DBpediaUsageMining"

version := "0.1.0"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  "com.typesafe.slick"      %% "slick"                       % "1.0.1" withSources, 
  "org.mongodb" 			%% "casbah" 					 % "2.6.5",
  "com.novus" 				%% "salat" 						 % "1.9.5",
  "org.specs2" 				%% "specs2" 					 % "2.3.8" % "test"
)

scalacOptions in Test ++= Seq("-Yrangepos")

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)
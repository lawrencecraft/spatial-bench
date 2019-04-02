import Dependencies._

ThisBuild / scalaVersion := "2.12.8"
ThisBuild / version := "1.0.0"
ThisBuild / organization := "spatialbench"
ThisBuild / organizationName := "spatialbench"

lazy val root = (project in file("."))
  .settings(
    name := "Spatial Bench",
    libraryDependencies ++= Seq(kafka, scalaTest % Test)
      ++ cats
      ++ storm
      ++ spatials,
    scalacOptions += "-Ypartial-unification",
    assemblyJarName in assembly := "spatial-bench.jar",
    resolvers ++=
      Seq(
        "clojars" at "https://clojars.org/repo",
        "osgeo" at "http://download.osgeo.org/webdav/geotools/"
      ),
    assemblyMergeStrategy in assembly := {
      case PathList("tec", "uom", "se", "format", "messages.properties") =>
        MergeStrategy.discard
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    }
  )

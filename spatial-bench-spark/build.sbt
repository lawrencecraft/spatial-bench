import Dependencies._

ThisBuild / scalaVersion := "2.11.11"
ThisBuild / version := "1.0.0"
ThisBuild / organization := "spatialbench-spark"
ThisBuild / organizationName := "spatialbench"

lazy val root = (project in file("."))
  .settings(
    name := "Spatial Bench Spark",
    libraryDependencies ++= spark,
    scalacOptions += "-Ypartial-unification",
    assemblyJarName in assembly := "spatial-bench-spark.jar",
    resolvers ++=
      Seq(
        "clojars" at "https://clojars.org/repo",
        "locationtech-releases" at "https://repo.locationtech.org/content/groups/releases",
        "boundlessgeo" at "https://repo.boundlessgeo.com/main",
        "osgeo" at "http://download.osgeo.org/webdav/geotools",
        "conjars.org" at "http://conjars.org/repo",
        "media.javax" at "http://maven.geotoolkit.org"
      ),
    assemblyMergeStrategy in assembly := {
      case PathList(ps @ _*) if ps.last endsWith "jaiext" =>
        MergeStrategy.discard
      case PathList(ps @ _*) if ps.last endsWith "jai" =>
        MergeStrategy.discard
      case PathList(ps @ _*) if ps.last endsWith "properties" =>
        MergeStrategy.discard
      case PathList(ps @ _*) if ps.last == "JaiI18N.class" =>
        MergeStrategy.discard
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    }
  )

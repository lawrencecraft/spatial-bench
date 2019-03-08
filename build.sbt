import Dependencies._

ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "spatialbench"
ThisBuild / organizationName := "spatialbench"

lazy val root = (project in file("."))
  .settings(
    name := "Spatial Bench",
    libraryDependencies ++= Seq(kafka, scalaTest % Test) ++ cats ++ storm ++ spatials,
    scalacOptions += "-Ypartial-unification",
    assemblyJarName in assembly := "spatial-bench.jar",
    resolvers ++=
      Seq(
          "clojars" at "https://clojars.org/repo",
          "osgeo" at "http://download.osgeo.org/webdav/geotools/"
      )

  )


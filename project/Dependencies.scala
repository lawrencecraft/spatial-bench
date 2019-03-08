import sbt._

object Dependencies {
  lazy val scalaTest: ModuleID = "org.scalatest" %% "scalatest" % "3.0.5"
  lazy val kafka: ModuleID = "org.apache.kafka" % "kafka-clients" % "2.1.1"
  lazy val cats: Seq[ModuleID] = Seq("org.typelevel" %% "cats-core" % "1.6.0", "org.typelevel" %% "cats-effect" % "1.2.0")
  lazy val storm: Seq[ModuleID] = Seq(
    "org.apache.storm" % "storm-core" % "1.2.2" % "provided",
    "org.apache.storm" % "storm-kafka-client" % "1.2.2"
  )

  lazy val spatials: Seq[ModuleID] = Seq(
    "com.spatial4j" % "spatial4j" % "0.5",
    "net.sf.geographiclib" % "GeographicLib-Java" % "1.49",
    "javax.media" % "jai_core" % "1.1.3",
    "org.geotools" % "gt-shapefile" % "21.0"

  )
}

import sbt._

object Dependencies {
  lazy val scalaTest: ModuleID = "org.scalatest" %% "scalatest" % "3.0.5"
  lazy val kafka: ModuleID = "org.apache.kafka" % "kafka-clients" % "2.1.1"
  lazy val cats: Seq[ModuleID] = Seq("org.typelevel" %% "cats-core" % "1.6.0", "org.typelevel" %% "cats-effect" % "1.2.0")

  lazy val spark: Seq[ModuleID] = Seq(
    "org.apache.spark" %% "spark-core" % "2.4.1" % "provided",
    "org.apache.spark" %% "spark-sql" % "2.4.1" % "provided",
    "org.locationtech.geomesa" %% "geomesa-spark-sql" % "2.3.0" exclude("com.googlecode.efficient-java-matrix-library", "core") exclude("com.googlecode.efficient-java-matrix-library", "ejml")
  )
}

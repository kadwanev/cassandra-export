import sbt._
import Keys._

object CassandraExportBuild extends Build {

  val cassandraVersion = SettingKey[String]("cassandra-version", "The version of Cassandra used for building.")

  override lazy val settings = super.settings ++
    Seq(cassandraVersion := "A: in Build.settings in Build.scala", resolvers := Seq())

  lazy val root = Project(id = "cassandra-export",
    base = file("."),
    settings = Project.defaultSettings)
}
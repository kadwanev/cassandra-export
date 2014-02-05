name := "cassandra-export"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.2"

mainClass in (Compile, run) := Some("com.twitter.scalding.Tool")

resolvers += "Default Maven Repo" at "http://repo1.maven.org/maven2"

resolvers += "Concurrent Maven Repo" at "http://conjars.org/repo"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

cassandraVersion := "1.2.6"

//    sbt "set cassandraVersion := \"1.0.6\"" +assembly \
//        "set cassandraVersion := \"1.1.12\"" +assembly \
//        "set cassandraVersion := \"1.2.8\"" +assembly \
//        "set cassandraVersion := \"2.0.2\"" +assembly

// Base
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.2.2",
  "org.scalatest" %% "scalatest" % "2.0.RC2" % "test",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.2.3",
//  "com.fasterxml" % "jackson-module-scala" % "1.9.3",
  "org.slf4j" % "slf4j-log4j12" % "1.7.2",  // Listing specifically for run requires it
  "org.apache.hadoop" % "hadoop-core" % "1.0.3" % "provided"
)

// Scalding
libraryDependencies ++= Seq(
  "cascading" % "cascading-core" % "2.2.0",
  "cascading" % "cascading-local" % "2.2.0",
  "cascading" % "cascading-hadoop" % "2.2.0",
  "com.twitter" %% "scalding-core" % "0.8.11",
  "com.twitter" %% "scalding-args" % "0.8.11",
  "com.twitter" %% "scalding-date" % "0.8.11"
)

// Cassandra
version <<= cassandraVersion { cv => "cassandra" + cv }

libraryDependencies <++= (cassandraVersion) { (el) => Seq(
  "org.apache.cassandra" % "cassandra-all" % el,
  "net.java.dev.jna" % "jna" % "4.0.0",
  "net.java.dev.jna" % "jna-platform" % "4.0.0",
  "org.xerial.snappy" % "snappy-java" % "1.0.5"
)}


classpathConfiguration in Runtime := Configurations.CompileInternal // add provided dependencies for runtime

scalacOptions ++= Seq("-unchecked", "-deprecation")

//net.virtualvoid.sbt.graph.Plugin.graphSettings

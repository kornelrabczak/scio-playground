import Dependencies._

ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

val scioVersion = "0.8.0-beta1"
val beamVersion = "2.15.0"
val kafkaVersion = "2.3.0"

lazy val root = (project in file("."))
  .settings(
    name := "scio-playground",
    libraryDependencies += scalaTest % Test,
    libraryDependencies ++= Seq(
      "com.spotify" %% "scio-core" % scioVersion,
      "org.apache.beam" % "beam-runners-direct-java" % beamVersion,
      "org.apache.beam" % "beam-sdks-java-io-kafka" % beamVersion,
      "org.apache.kafka" % "kafka-clients" % kafkaVersion,  
      "org.slf4j" % "slf4j-simple" % "1.7.25",
      "com.spotify" %% "scio-test" % scioVersion % Test
    )
  )

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)

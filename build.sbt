ThisBuild / version := "1.0"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "Auth-Project"
  )


scalacOptions += "-Ymacro-annotations"

val AkkaVersion = "2.8.5"
val AkkaHttpVersion = "10.5.2"
val AkkaHttpCors = "1.2.0"
val SLF4JVersion = "2.0.9"
val SlickVersion = "3.4.1"
val PostgresSQLVersion = "42.6.0"
val MongoDBVersion = "4.10.0"
val CirceVersion = "0.14.5"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
  "ch.megard" %% "akka-http-cors" % AkkaHttpCors,
  "org.slf4j" % "slf4j-api" % SLF4JVersion,
  "org.slf4j" % "slf4j-simple" % SLF4JVersion,
  "org.mongodb.scala" %% "mongo-scala-driver" % MongoDBVersion,
  "io.circe" %% "circe-core" % CirceVersion,
  "io.circe" %% "circe-generic" % CirceVersion,
  "io.circe" %% "circe-parser" % CirceVersion,
  "com.typesafe.slick" %% "slick" % SlickVersion,
  "com.typesafe.slick" %% "slick-hikaricp" % SlickVersion,
  "org.postgresql" % "postgresql" % PostgresSQLVersion,
)

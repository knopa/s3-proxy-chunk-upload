name := """S3ProxyUpload"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.7"
routesGenerator := StaticRoutesGenerator

libraryDependencies ++= Seq(
  javaJdbc,
  "org.postgresql" % "postgresql" % "9.3-1100-jdbc4",
  "com.amazonaws" % "aws-java-sdk" % "1.8.11",
  "org.apache.directory.api" % "api-all" % "1.0.0-M14",
  cache,
  javaWs
)

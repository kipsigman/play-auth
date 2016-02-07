import Dependencies._

name := """play-auth"""

organization := "kipsigman"

version := "0.1.0"

scalaVersion := "2.11.7"

// Change this to another test framework if you prefer
libraryDependencies ++= Seq(
  "kipsigman" %% "scala-domain-model" % "0.1.0",
  "com.mohiva" %% "play-silhouette" % silhouetteVersion,
  "net.ceedubs" %% "ficus" % "1.1.2",
  "org.scalatest" %% "scalatest" % "2.2.5" % Test,
  "org.mockito" % "mockito-core" % "1.10.19" % Test,
  "com.mohiva" %% "play-silhouette-testkit" % silhouetteVersion % Test
)
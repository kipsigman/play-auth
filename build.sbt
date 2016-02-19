import Dependencies._

name := """play-auth"""
organization := "kipsigman"

scalaVersion := "2.11.7"

// Change this to another test framework if you prefer
libraryDependencies ++= Seq(
  "kipsigman" %% "scala-domain-model" % "0.1.4",
  "com.mohiva" %% "play-silhouette" % silhouetteVersion,
  "net.ceedubs" %% "ficus" % "1.1.2",
  "org.scalatest" %% "scalatest" % "2.2.5" % Test,
  "org.mockito" % "mockito-core" % "1.10.19" % Test,
  "com.mohiva" %% "play-silhouette-testkit" % silhouetteVersion % Test
)

licenses += ("Apache-2.0", url("https://github.com/kipsigman/play-auth/blob/master/LICENSE"))
homepage := Some(url("https://github.com/kipsigman/play-auth"))
scmInfo := Some(ScmInfo(url("https://github.com/kipsigman/play-auth"), "scm:git:git://github.com:kipsigman/play-auth.git"))
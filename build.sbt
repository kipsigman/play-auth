import Dependencies._

name := """play-auth"""
organization := "kipsigman"

scalaVersion := "2.11.8"

resolvers += Resolver.bintrayRepo("kipsigman", "maven")

libraryDependencies ++= Seq(
  "kipsigman" %% "scala-domain-model" % "0.3.3",
  "kipsigman" %% "play-extensions" % "0.3.3",
  "com.mohiva" %% "play-silhouette" % silhouetteVersion,
  "net.ceedubs" %% "ficus" % "1.1.2",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.mockito" % "mockito-core" % "2.3.11" % "test",
  "com.mohiva" %% "play-silhouette-testkit" % silhouetteVersion % Test
)

licenses += ("Apache-2.0", url("https://github.com/kipsigman/play-auth/blob/master/LICENSE"))
homepage := Some(url("https://github.com/kipsigman/play-auth"))
scmInfo := Some(ScmInfo(url("https://github.com/kipsigman/play-auth"), "scm:git:git://github.com:kipsigman/play-auth.git"))
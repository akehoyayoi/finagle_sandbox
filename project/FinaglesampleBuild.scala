import sbt._
import sbt.Keys._

object FinaglesampleBuild extends Build {

  lazy val finaglesample = Project(
    id = "finagle-sample",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "finagle-sample",
      organization := "jp.akehoyayoi.finagle.sample",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.10.0",
      // add other settings here
      resolvers ++= Seq("Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases/"),
      libraryDependencies ++= Seq(
        "com.twitter" %% "finagle-core" % "6.6.2",
        "com.twitter" %% "finagle-http" % "6.6.2",
        "com.twitter" %% "finagle-memcached" % "6.6.2",
        "com.twitter" %% "finagle-mysql" % "6.6.2"
      )
    )
  )
}

scalaVersion := "2.13.13"
organization := "com.github.taylor-peterson"
version := "0.1"

lazy val root = (project in file("."))
  .settings(
    name := "000-core",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.19" % Test,
    )
  )

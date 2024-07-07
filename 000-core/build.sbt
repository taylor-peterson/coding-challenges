scalaVersion := "2.13.14"
organization := "com.github.taylor-peterson"
version := "0.1"

lazy val root = (project in file("."))
  .settings(
    name := "000-core",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "os-lib" % "0.10.2",
      "org.scalatest" %% "scalatest" % "3.2.19", // TODO only for test?
    )
  )

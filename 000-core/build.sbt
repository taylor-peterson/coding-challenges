scalaVersion := "2.13.14"
organization := "com.github.taylor-peterson"
version := "0.4"

ThisBuild / versionScheme := Some("early-semver")
ThisBuild / githubOwner := "taylor-peterson"
ThisBuild / githubRepository := "coding-challenges"

val CompileOnly = config("compile-only").hide

lazy val root = (project in file("."))
  .settings(
    name := "000-core",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "os-lib" % "0.10.2",
      "org.scalatest" %% "scalatest" % "3.2.19" % CompileOnly,
    ),
    ivyConfigurations += CompileOnly,
    Compile / unmanagedClasspath ++= update.value.select(configurationFilter(CompileOnly.name))
  )

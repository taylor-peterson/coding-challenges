import sbtassembly.AssemblyPlugin.defaultShellScript

ThisBuild / scalaVersion := "2.13.13"
ThisBuild / assemblyPrependShellScript := Some(defaultShellScript)

ThisBuild / resolvers += Resolver.githubPackages("taylor-peterson")

lazy val ccwc = (project in file("."))
  .settings(
    name := "ccwc",
    libraryDependencies ++= Seq(
      "com.github.scopt" %% "scopt" % "4.1.0",
      "com.lihaoyi" %% "os-lib" % "0.10.2",
      "com.github.taylor-peterson" %% "000-core" % "0.4",
      "org.scalatest" %% "scalatest" % "3.2.19" % Test,
    ),

    assembly / test := (Test / test).value,
    assembly / assemblyJarName := s"${name.value}",
  )

lazy val IntegrationTest = (project in file("integration"))
  .settings(
    publish / skip := true,
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.19" % Test,
      "com.github.taylor-peterson" %% "000-core" % "0.4" % Test,
    ),
    Test / testOptions += Tests.Argument(
      TestFrameworks.ScalaTest, s"-Dccwc=${(ccwc / assembly / target).value}/ccwc"
    ),
    Test / test := ((Test / test) dependsOn (ccwc / assembly)).value
  )

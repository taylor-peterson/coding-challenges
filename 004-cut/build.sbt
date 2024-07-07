import sbtassembly.AssemblyPlugin.defaultShellScript

ThisBuild / scalaVersion := "2.13.13"

ThisBuild / resolvers += Resolver.githubPackages("taylor-peterson")

ThisBuild / assemblyPrependShellScript := Some(defaultShellScript)
lazy val cut = (project in file("."))
  .settings(
    name := "cut",
    libraryDependencies ++= Seq(
      "com.github.scopt" %% "scopt" % "4.1.0",
      "com.lihaoyi" %% "os-lib" % "0.10.2",
      "com.github.taylor-peterson" %% "000-core" % "0.4",
      "org.scalatest" %% "scalatest" % "3.2.19" % Test,
    ),

    assembly / test := (Test / test).value,
    assembly / assemblyJarName := name.value,
  )

lazy val IntegrationTest = (project in file("integration"))
  .settings(
    publish / skip := true,
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.19" % Test,
      "com.github.taylor-peterson" %% "000-core" % "0.4" % Test
    ),
    Test / testOptions += Tests.Argument(
      TestFrameworks.ScalaTest, s"-Dcut=${(cut / assembly / target).value}/cut"
    ),
    Test / test := ((Test / test) dependsOn (cut / assembly)).value
  )

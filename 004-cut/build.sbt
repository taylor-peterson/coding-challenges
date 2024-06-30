import sbtassembly.AssemblyPlugin.defaultShellScript

ThisBuild / scalaVersion := "2.13.13"

ThisBuild / assemblyPrependShellScript := Some(defaultShellScript)
// TODO extract common settings/config
lazy val cut = (project in file("."))
  .settings(
    name := "cut",
    libraryDependencies ++= Seq(
      "com.github.scopt" %% "scopt" % "4.1.0",
      "com.lihaoyi" %% "os-lib" % "0.10.2",
      "org.scalatest" %% "scalatest" % "3.2.19" % "test",
    ),

    assembly / test := (Test / test).value,
    assembly / assemblyJarName := name.value,
  )

lazy val IntegrationTest = (project in file("integration"))
  .settings(
    publish / skip := true,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % Test,
    Test / testOptions += Tests.Argument(
      TestFrameworks.ScalaTest, s"-Dcut=${(cut / assembly / target).value}/cut"
    ),
    Test / test := ((Test / test) dependsOn (cut / assembly)).value
  )

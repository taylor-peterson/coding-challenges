ThisBuild / scalaVersion := "2.13.13"

// TODO extract common settings/config
lazy val cut = (project in file("."))
  .enablePlugins(NativeImagePlugin)
  .settings(
    name := "cut",
    libraryDependencies ++= Seq(
      "com.github.scopt" %% "scopt" % "4.1.0",
      "com.lihaoyi" %% "os-lib" % "0.10.2",
      "org.scalatest" %% "scalatest" % "3.2.18" % "test",
    ),

    nativeImageOptions += "--no-fallback",
    nativeImageVersion := "22.1.0",
    Global / excludeLintKeys += nativeImageVersion,
  )

lazy val IntegrationTest = (project in file("integration"))
  .settings(
    publish / skip := true,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % Test,
    Test / testOptions += Tests.Argument(
      TestFrameworks.ScalaTest, s"-DtargetDir=${(cut / target).value}"
    ),
    Test / test := ((Test / test) dependsOn (cut / nativeImage)).value
  )

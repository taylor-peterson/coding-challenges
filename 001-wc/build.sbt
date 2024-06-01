ThisBuild / scalaVersion := "2.13.13"

lazy val ccwc = (project in file("."))
  .enablePlugins(NativeImagePlugin)
  .settings(
    name := "ccwc",
    libraryDependencies ++= Seq(
      "com.github.scopt" %% "scopt" % "4.1.0",
      "com.lihaoyi" %% "os-lib" % "0.10.1",
      "org.scalatest" %% "scalatest" % "3.2.18" % "test",
    ),
    Compile / mainClass := Some("Main"),

    // https://typelevel.org/cats-effect/docs/core/native-image
    nativeImageOptions += "--no-fallback",
    nativeImageVersion := "22.1.0",
    Global / excludeLintKeys += nativeImageVersion // Suppress lintUnused check
  )

lazy val IntegrationTest = (project in file("integration"))
  .settings(
    publish / skip := true,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % Test,
    Test / testOptions += Tests.Argument(
      TestFrameworks.ScalaTest, s"-DtargetDir=${(ccwc / target).value}"
    ),
    Test / test := ((Test / test) dependsOn (ccwc / nativeImage)).value
  )

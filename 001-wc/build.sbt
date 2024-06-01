ThisBuild / scalaVersion := "2.13.13"

lazy val ccwc = (project in file("."))
  .enablePlugins(NativeImagePlugin)
  .configs(IntegrationTest)
  .settings(
    name := "ccwc",
    libraryDependencies ++= Seq(
      "com.github.scopt" %% "scopt" % "4.1.0",
      "com.lihaoyi" %% "os-lib" % "0.10.1",
      "org.scalatest" %% "scalatest" % "3.2.18" % "it,test",
    ),
    Compile / mainClass := Some("Main"),

    // https://typelevel.org/cats-effect/docs/core/native-image
    nativeImageOptions += "--no-fallback",
    nativeImageVersion := "22.1.0",

    Defaults.itSettings,
    IntegrationTest / testOptions += Tests.Argument(
      TestFrameworks.ScalaTest, s"-DtargetDir=${target.value}"
    ),
    (IntegrationTest / test) := ((IntegrationTest / test) dependsOn nativeImage).value
  )

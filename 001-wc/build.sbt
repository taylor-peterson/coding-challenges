ThisBuild / scalaVersion := "2.13.13"

lazy val ccwc = (project in file("."))
  .enablePlugins(NativeImagePlugin)
  .settings(
    name := "ccwc",
    libraryDependencies ++= Seq(
      "com.github.scopt" %% "scopt" % "4.1.0",
      "com.lihaoyi" %% "os-lib" % "0.9.1",
    ),
    Compile / mainClass := Some("Main"),

    // https://typelevel.org/cats-effect/docs/core/native-image
    nativeImageOptions += "--no-fallback",
    nativeImageVersion := "22.1.0"
  )

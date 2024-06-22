ThisBuild / scalaVersion := "2.13.13"

lazy val root = (project in file("."))
  .settings(
    name := "000-core",
  )

lazy val IntegrationTest = (project in file("integration"))
  .settings(
    publish / skip := true,
    libraryDependencies ++= Seq("org.scalatest" %% "scalatest" % "3.2.18" % "test"),
    Test / testOptions += Tests.Argument(
      Some(TestFrameworks.ScalaTest),
      List(
        s"-Dcut=/Users/taylorpeterson/src/coding-challenges/004-cut/target/native-image/cut",
        s"-Dccwc=/Users/taylorpeterson/src/coding-challenges/001-wc/target/native-image/ccwc",
        s"-DcsvFile=/Users/taylorpeterson/src/coding-challenges/004-cut/integration/src/test/resources/fourchords.csv")
    ),
  )

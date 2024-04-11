import Dependencies._

val scala3Version = "3.4.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "scala-task",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      Libs.catsCore,
      Libs.catsEffect,
      Libs.fs2,
    ),

  testFrameworks += new TestFramework("munit.Framework")
  )

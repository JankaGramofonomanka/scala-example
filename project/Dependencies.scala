import sbt._

object Dependencies {

  object V {

    val catsCore    = "2.8.0"
    val catsEffect  = "3.3.14"
    val fs2         = "3.2.12"
    
    val scalatest   = "3.2.13"

  }

  object Libs {

    val catsCore            = "org.typelevel"       %% "cats-core"            % V.catsCore
    val catsEffect          = "org.typelevel"       %% "cats-effect"          % V.catsEffect
    
    val fs2                 = "co.fs2"              %% "fs2-core"             % V.fs2

    // Test
    val scalatest           = "org.scalatest"       %% "scalatest"            % V.scalatest % Test

    val munit               = "org.scalameta"       %% "munit"                % "0.7.29"    % Test
    val munitCatsEffect3    = "org.typelevel"       %% "munit-cats-effect-3"  % "1.0.7"     % Test
  }
}

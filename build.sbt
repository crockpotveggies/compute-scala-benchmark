import sbt.Keys.scalacOptions

lazy val root = (project in file(".")).
  enablePlugins(JmhPlugin).
  settings(
    inThisBuild(List(
      organization := "org.deeplearning4j",
      scalaVersion := "2.12.4",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "compute-scala-benchmark",
    libraryDependencies ++= Seq(
      "com.thoughtworks.compute" %% "gpu" % "latest.release"
    ),
    libraryDependencies += ("org.lwjgl" % "lwjgl" % "latest.release").jar().classifier {
      import scala.util.Properties._
      if (isMac) {
        "natives-macos"
      } else if (isLinux) {
        "natives-linux"
      } else if (isWin) {
        "natives-windows"
      } else {
        throw new MessageOnlyException(s"lwjgl does not support $osName")
      }
    },
    scalacOptions += "-Ypartial-unification"
  )

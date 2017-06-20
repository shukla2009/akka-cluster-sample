import sbt._
import Keys._
import sbtassembly.AssemblyPlugin.autoImport._


object Settings {

  lazy val settings = Seq(
    organization := "coma.avalia",
    version := "0.0.1" + sys.props.getOrElse("buildNumber", default="0-SNAPSHOT"),
    scalaVersion := "2.12.0",
    publishMavenStyle := true,
    publishArtifact in Test := false
  )
  lazy val sigarSettings = Seq(
    unmanagedSourceDirectories in (Compile,run) += baseDirectory.value.getParentFile / "sigar",
    javaOptions in run ++= {
      System.setProperty("java.library.path", file("./sigar").getAbsolutePath)
      Seq("-Xms128m", "-Xmx1024m")
    })
  lazy val testSettings = Seq(
    fork in Test := false,
    parallelExecution in Test := false
  )

  lazy val itSettings = Defaults.itSettings ++ Seq(
    logBuffered in IntegrationTest := false,
    fork in IntegrationTest := true
  )

  lazy val akkaSettings = Seq(
    assemblyJarName in assembly := "akka-" + version.value + ".jar",
    test in assembly := {},
    target in assembly := file(baseDirectory.value + "/../bin/"),
    assemblyOption in assembly := (assemblyOption in assembly).value.copy(
      includeScala = false,
      includeDependency=true),
    assemblyMergeStrategy in assembly := {
      case PathList("META-INF", xs@_*) => MergeStrategy.discard
      case n if n.startsWith("reference.conf") => MergeStrategy.concat
      case _ => MergeStrategy.first
    }
  )


  lazy val nodeSettings = Seq()

}

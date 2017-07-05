import sbt._
import Keys._

object Dependencies {

  lazy val version = new {
    val scalaTest = "3.0.0"
    val scalaCheck = "1.13.4"
    val akka = "2.5.3"
  }

  lazy val library = new {
    val test = "org.scalatest" %% "scalatest" % version.scalaTest % Test
    val check = "org.scalacheck" %% "scalacheck" % version.scalaCheck % Test
    val akkaActor = "com.typesafe.akka" %% "akka-actor" % version.akka
    val akkaCluster = "com.typesafe.akka" %% "akka-cluster" % version.akka
    val akkaClusterMetrics = "com.typesafe.akka" %% "akka-cluster-metrics" % version.akka
    val akkaTool = "com.typesafe.akka" %% "akka-cluster-tools" % version.akka
    val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % version.akka % "test"
    //val sigar = "org.fusesource" % "sigar" % "1.6.4"
    val sigar = "io.kamon" % "sigar-loader" % "1.6.6" % "test"
  }

  val clusterDependencies: Seq[ModuleID] = Seq(
    library.test,
    library.check,
    library.akkaActor,
    library.akkaCluster,
    library.akkaClusterMetrics,
    library.akkaTestkit,
    library.sigar,
    library.akkaTool
  )

  val frontendDependencies: Seq[ModuleID] = Seq(
    library.test,
    library.check
  )

  val akkaDependencies: Seq[ModuleID] = Seq(
    library.test,
    library.check
  )

}

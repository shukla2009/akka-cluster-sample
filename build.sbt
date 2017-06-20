import Dependencies._
import Settings._

lazy val cluster = (project in file("cluster")).
  settings(Settings.settings: _*).
  //settings(Settings.sigarSettings: _*).
  settings(libraryDependencies ++= clusterDependencies)

lazy val node_1 = (project in file("node_1")).
  settings(Settings.settings: _*)
  .settings(Settings.nodeSettings: _*)
  .dependsOn(cluster)

lazy val node_2 = (project in file("node_2")).
  settings(Settings.settings: _*)
  .settings(Settings.nodeSettings: _*)
  .dependsOn(cluster)


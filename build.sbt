import Dependencies._
import Settings._
import sbt.Attributed.data
import sbt.KeyRanks.APlusTask

//lazy val runAll = InputKey[Unit]("runAll", "Runs a main class, passing along arguments provided on the command line.", APlusTask)
//runAll := runAllTask
//
//def runAllTask: Def.Initialize[InputTask[Unit]] = {
//  val projects: Seq[ProjectRef] = allNodeProjects.value
//  projects.foreach(p => println(p))
//}
//
////def runTask(classpath: Initialize[Task[Classpath]], mainClassTask: Initialize[Task[Option[String]]], scalaRun: Initialize[Task[ScalaRun]]): Initialize[InputTask[Unit]] = {
////  import Def.parserToInput
////  val parser = Def.spaceDelimited()
////  Def.inputTask {
////    val mainClass = mainClassTask.value getOrElse sys.error("No main class detected.")
////    scalaRun.value.run(mainClass, data(classpath.value), parser.parsed, streams.value.log) foreach sys.error
////  }
////}
///** Projects that have the Microservice plugin enabled. */
//private lazy val allNodeProjects: Def.Initialize[Task[Seq[ProjectRef]]] = Def.task {
//  val structure = buildStructure.value
//  val projects = structure.allProjectRefs
//  for {
//    projRef <- projects
//    proj <- Project.getProject(projRef, structure).toList if proj.id.startsWith("node")
//  } yield projRef
//}

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


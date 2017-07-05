package com.avalia.cluster

import java.net.URL

import com.typesafe.config.{Config, ConfigFactory}

import scala.collection.JavaConverters._

/**
  * Created by synerzip on 16/6/17.
  */
class AkkaClusterHelperClassLoader(loader: ClassLoader) extends ClassLoader(loader) {
  override def getResources(name: String): java.util.Enumeration[URL] = {
    val resources: List[URL] = super.getResources(name).asScala.toList
    val finalResources = resources.filter(p => p.getPath.contains("akka-cluster-sample")) ++
      resources.filter(p => !p.getPath.contains("akka-cluster-sample"))
    java.util.Collections.enumeration(finalResources.asJava)
  }

}

object ConfigHelper {

  def getConfig(roles: List[String], host: String, port: Int): Config = {
    ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
      .withFallback(ConfigFactory.parseString(s"akka.remote.netty.tcp.hostname=$host"))
      .withFallback(ConfigFactory.parseString(s"akka.cluster.roles = [${roles.mkString(",")}]"))
      .withFallback(ConfigFactory.load(new AkkaClusterHelperClassLoader(getClass.getClassLoader)))
  }

  def getConfig(roles: List[String], port: Int): Config = {
    ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
      .withFallback(ConfigFactory.parseString(s"akka.cluster.roles = [${roles.mkString(",")}]"))
      .withFallback(ConfigFactory.load(new AkkaClusterHelperClassLoader(getClass.getClassLoader)))
  }

  def getConfig(roles: List[String]): Config = {
    ConfigFactory.parseString(s"akka.cluster.roles = [${roles.mkString(",")}]")
      .withFallback(ConfigFactory.load(new AkkaClusterHelperClassLoader(getClass.getClassLoader)))
  }

  def getConfig(): Config = {
    ConfigFactory.load(new AkkaClusterHelperClassLoader(getClass.getClassLoader))
  }
}

package com.avalia.cluster



import akka.actor.{Actor, Address}
import com.typesafe.config.{Config, ConfigFactory}

/** A `NodeGuardian` manages the worker actors at the root of each
  * deployed application, where any special application logic is handled in the
  * implementer here, but the cluster work, node lifecycle and supervision events
  * are handled in [[ClusterAwareNodeGuardian]].
  *
  */
class BaseActor extends ClusterAwareNodeGuardian {

  override def preStart(): Unit = {
    //val port = 2552
    //val host = "127.0.0.1"
    //cluster.joinSeedNodes(Vector(Address("akka.tcp", "ClusterSystem", host, port)))
    super.preStart()
  }


  override def initialize(): Unit = {
    super.initialize()
    context become initialized
  }

  def initialized: Actor.Receive = {
    case "shutdown" => gracefulShutdown(sender())
  }

}

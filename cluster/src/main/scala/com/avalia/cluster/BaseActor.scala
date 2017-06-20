package com.avalia.cluster

/**
  * Created by Rahul Shukla on 3/3/17.
  */

import akka.actor.Actor
import com.typesafe.config.{Config, ConfigFactory}

/** A `NodeGuardian` manages the worker actors at the root of each
  * deployed application, where any special application logic is handled in the
  * implementer here, but the cluster work, node lifecycle and supervision events
  * are handled in [[ClusterAwareNodeGuardian]].
  *
  */
class BaseActor extends ClusterAwareNodeGuardian {

  override def preStart(): Unit = {
    super.preStart()
    cluster.joinSeedNodes(Vector(cluster.selfAddress))
  }

  override def initialize(): Unit = {
    super.initialize()
    context become initialized
  }

  def initialized: Actor.Receive = {
    case "shutdown" => gracefulShutdown(sender())
  }

}

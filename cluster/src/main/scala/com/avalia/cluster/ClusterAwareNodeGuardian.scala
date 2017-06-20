package com.avalia.cluster

import java.util.concurrent.TimeoutException

import akka.actor._
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.control.NonFatal




/** A `NodeGuardian` is the root of each KillrWeather deployed application, where
  * any special application logic is handled in its implementers, but the cluster
  * work and node lifecycle and supervision events are handled here.
  *
  * It extends [[ClusterAware]] which handles creation of the [[akka.cluster.Cluster]],
  * but does the cluster.join and cluster.leave itself.
  *
  * `NodeGuardianLike` also handles graceful shutdown of the node and all child actors. */
abstract class ClusterAwareNodeGuardian extends ClusterAware {

  import SupervisorStrategy._
  import akka.pattern.gracefulStop
  import context.dispatcher

  // customize
  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1.minute) {
      case _: ActorInitializationException => Stop
      case _: IllegalArgumentException => Stop
      case _: IllegalStateException => Restart
      case _: TimeoutException => Escalate
      case _: Exception => Escalate
    }

  override def preStart(): Unit = {
    super.preStart()
    log.info("Starting at {}", cluster.selfAddress)
  }

  override def postStop(): Unit = {
    super.postStop()
    log.info("Node {} shutting down.", cluster.selfAddress)
  }

  /** On startup, actor is in an [[uninitialized]] state. */
  override def receive = uninitialized orElse initialized orElse super.receive

  def uninitialized: Actor.Receive = {
    case "initialize" => initialize()
  }

  def initialize(): Unit = {
    log.info(s"Node is transitioning from 'uninitialized' to 'initialized'")
  }

  /** Must be implemented by an Actor. */
  def initialized: Actor.Receive

  protected def gracefulShutdown(listener: ActorRef): Unit = {
    implicit val timeout = Timeout(5.seconds)
    val status = Future.sequence(context.children.map(shutdown))
    listener ! status
    log.info(s"Graceful shutdown completed.")
  }

  /** Executes [[akka.pattern.gracefulStop( )]] on `child`. */
  private def shutdown(child: ActorRef)(implicit t: Timeout): Future[Boolean] =
    try gracefulStop(child, t.duration + 1.seconds) catch {
      case NonFatal(e) =>
        log.error("Error shutting down {}, cause {}", child.path, e.toString)
        Future(false)
    }
}

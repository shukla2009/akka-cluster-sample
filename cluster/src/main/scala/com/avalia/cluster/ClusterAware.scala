package com.avalia.cluster

import akka.actor.{Actor, ActorLogging}
import akka.cluster.ClusterEvent._
import akka.cluster.metrics.{ClusterMetricsChanged, Metric}
import akka.cluster.{Cluster, Member}

/**
  * Creates the [[Cluster]] and does any cluster lifecycle handling
  * aside from join and leave so that the implementing applications can
  * customize when this is done.
  *
  * Implemented by [[ClusterAwareNodeGuardian]].
  */
abstract class ClusterAware extends Actor with ActorLogging {

  val cluster = Cluster(context.system)

  /** subscribe to cluster changes, re-subscribe when restart. */
  override def preStart(): Unit = cluster.subscribe(self, classOf[ClusterDomainEvent])

  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive: Actor.Receive = {
    case MemberUp(member) => watch(member)
    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) =>
      log.info(s"Member is Removed: ${member.address} after $previousStatus")
    case ClusterMetricsChanged(forNode) =>
      forNode collectFirst { case m if m.address == cluster.selfAddress =>
        log.debug(s"${}", filter(m.metrics))
      }
    case _: MemberEvent =>
  }

  /** Initiated when node receives a [[akka.cluster.ClusterEvent.MemberUp]]. */
  private def watch(member: Member): Unit = {
    log.info("Member [{}] joined cluster.", member.address)
  }

  def filter(nodeMetrics: Set[Metric]): String = {
    val filtered = nodeMetrics collect { case v if v.name != "processors" => s"${v.name}:${v.value}" }
    s"NodeMetrics[${filtered.mkString(",")}]"
  }
}
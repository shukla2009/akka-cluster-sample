package com.avalia.node1

import akka.actor.{ActorSystem, Props}
import akka.cluster.Cluster
import akka.event.LoggingReceive
import com.avalia.cluster.{BaseActor, ConfigHelper}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Put, Subscribe, SubscribeAck}

/**
  * Created by synerzip on 15/6/17.
  */
class TestCaseActor extends BaseActor {

  val topic = "datasource-lookup"
  val mediator = DistributedPubSub(context.system).mediator
  mediator ! Subscribe(topic, self)

  //mediator ! Put(self)
  override def initialized: Receive = first orElse super.initialized

  def first: Receive = LoggingReceive {
    case s: String => log.info(s"################## Node 1 receive $s #########################")
      sender() ! ("Welcome","Boss")
    case SubscribeAck(Subscribe(topic, None, `self`)) ⇒ log.error(s"################## Subscribed to topic $topic ######")
  }
}

object Main extends App {

  private val system = ActorSystem("ClusterSystem", config = ConfigHelper.getConfig())
  //Cluster(system).join(Cluster(system).selfAddress)
  private val actorOf = system.actorOf(Props[TestCaseActor], "node")
  //  actorOf ! "FIRST"
  //  actorOf ! "SECOND"
  //  actorOf ! "FIRST"
  //  actorOf ! "SECOND"
}

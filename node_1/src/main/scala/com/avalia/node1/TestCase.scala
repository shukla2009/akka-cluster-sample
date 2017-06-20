package com.avalia.node1

import akka.actor.{ActorSystem, Props}
import akka.event.LoggingReceive
import com.avalia.cluster.{BaseActor, ConfigHelper}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Subscribe, SubscribeAck}

/**
  * Created by synerzip on 15/6/17.
  */
class TestCaseActor extends BaseActor {

  val mediator = DistributedPubSub(context.system).mediator
  val topic = "datasource-lookup"
  mediator ! Subscribe(topic, self)

  override def initialized: Receive = first orElse super.initialized

  def first: Receive = LoggingReceive {
    case s: String => log.info(s"################## Node 1 receive $s #########################")
    case SubscribeAck(Subscribe(topic, None, `self`)) ⇒ log.error(s"################## Subscribed to topic $topic ######")
  }
}

object Main extends App {

  private val system = ActorSystem("ClusterSystem", config = ConfigHelper.getConfig(List("NODE"), "127.0.0.1", 0))
  private val actorOf = system.actorOf(Props[TestCaseActor], "node")
  //  actorOf ! "FIRST"
  //  actorOf ! "SECOND"
  //  actorOf ! "FIRST"
  //  actorOf ! "SECOND"
}

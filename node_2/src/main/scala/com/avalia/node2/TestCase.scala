package com.avalia.node2

import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Subscribe, SubscribeAck}
import akka.event.LoggingReceive
import com.avalia.cluster.BaseActor

/**
  * Created by synerzip on 15/6/17.
  */
class TestCaseActor1 extends BaseActor {

  val topic = "datasource-lookup"

  val mediator = DistributedPubSub(context.system).mediator
  mediator ! Subscribe(topic, self)


  override def initialized: Receive = first orElse super.initialized orElse second

  def first: Receive = LoggingReceive {
    case SubscribeAck(Subscribe(topic, None, `self`)) ⇒ log.error(s"################## Subscribed to topic $topic ######")
  }

  def second: Receive = LoggingReceive {
    case s: String => log.error(s"######################## Received message $s from ${sender.path} in ${self.path} #################")
  }
}


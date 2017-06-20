package com.avalia.node2

import akka.actor.{ActorSystem, Props}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe, SubscribeAck}
import akka.event.LoggingReceive
import com.avalia.cluster.{BaseActor, ConfigHelper}

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by synerzip on 15/6/17.
  */
class TestCaseActor extends BaseActor {

  val topic = "datasource-lookup"

  val mediator = DistributedPubSub(context.system).mediator
  mediator ! Subscribe(topic, self)

  override def initialized: Receive = first orElse super.initialized orElse second

  def first: Receive = LoggingReceive {
    case "FIRST" => {
      log.error("################## First Message Received #########################")

      mediator ! Publish(topic, "Message Form Node 2")

      context.system.scheduler.schedule(0 seconds, 5 seconds, mediator, Publish(topic, "Message Form Node 2"))
    }
    case SubscribeAck(Subscribe(topic, None, `self`)) ⇒ log.error(s"################## Subscribed to topic $topic ######")
  }

  def second: Receive = LoggingReceive {
    case s: String => log.error(s"######################## Received in Node 2 $s #################")
    case x: Any => log.error(s"Why the hell here $x")
  }
}

object Main extends App {

  private val system = ActorSystem("ClusterSystem", config = ConfigHelper.getConfig(List("NODE"), "127.0.0.1", 0))

  system.actorOf(Props[TestCaseActor], "node") ! "FIRST"

}

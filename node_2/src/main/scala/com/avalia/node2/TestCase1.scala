package com.avalia.node2

import akka.actor.{ActorRef, ActorSystem, Address, Props}
import akka.cluster.Cluster
import akka.cluster.client.ClusterClientReceptionist
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe, SubscribeAck}
import akka.event.LoggingReceive
import com.avalia.cluster.{BaseActor, ConfigHelper}
import org.reactivestreams.Subscriber

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by synerzip on 15/6/17.
  */
class TestCaseActor extends BaseActor {

  val topic = "datasource-lookup"

  val mediator = DistributedPubSub(context.system).mediator
  //mediator ! Subscribe(topic, self)

  override def initialized: Receive = first orElse super.initialized

  var i = 0

  def getMessage: String = {
    i = i + 1
    s"Message $i"
  }

  def first: Receive = LoggingReceive {
    case "FIRST" => {
      context.system.scheduler.schedule(0 seconds, 5 seconds, mediator, Publish(topic, getMessage))
    }
    case (str1: String, str2: String) => log.error(s"################## ACK $str1 $str2 ######")
    case SubscribeAck(Subscribe(topic, None, `self`)) ⇒ log.error(s"################## Subscribed to topic $topic ######")
  }
}

object Main extends App {

  private val system = ActorSystem("ClusterSystem", config = ConfigHelper.getConfig())
  private val system1 = ActorSystem("ClusterSystem", config = ConfigHelper.getConfig())
  //Address("akka.tcp","ClusterSystem","127.0.0.1",2552)
  //private val selfAddress1: Address = Cluster(system).selfAddress
  //private val selfAddress = selfAddress1
  //Cluster(system).join(Address("akka.tcp","ClusterSystem","127.0.0.1",2552))
  system.actorOf(Props[TestCaseActor], "node") ! "FIRST"
  system1.actorOf(Props[TestCaseActor1], "node1")
}

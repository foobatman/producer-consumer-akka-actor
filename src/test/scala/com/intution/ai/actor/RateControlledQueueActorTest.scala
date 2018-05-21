package com.intution.ai.actor

import akka.actor.{ActorPath, ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.intution.ai.actor.Messages._
import com.intution.ai.data.Item
import org.scalatest.{BeforeAndAfterAll, FunSpecLike, Matchers}

import scala.concurrent.duration._
import scala.collection.immutable.Queue

class RateControlledQueueActorTest() extends TestKit(ActorSystem("RateControlledQueueActorTest")) with ImplicitSender
  with FunSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  describe("RateControlledQueueActor#preStart") {

    it("should start by signalling producer to send next Item ") {
      val queue = system.actorOf(Props(new RateControlledQueueActor(testActor, 10)), "rate-controlled-queue-actor-1")
      expectMsg(NextItem)
    }

  }
  describe("RateControlledQueueActor#receive") {

    describe("NextItem") {
      it("should dequeue the next item from non-consumed item queue if available and give to consumer") {
        val producer = system.actorOf(Props[Producer], "test-producer-1")
        val queueActor = system.actorOf(Props(new RateControlledQueueActor(producer, 10)), "rate-controlled-queue-actor-2")
        Thread.sleep(200)
        queueActor ! NextItem
        expectMsg(200.millis, Item(1))
      }

      it("should enqueue the consumer into waiting consumer queue if any non-consumed item not available") {
        val producer = producerWithNothingToProduce
        val queueActor = system.actorOf(Props(new RateControlledQueueActor(producer, 10)), "rate-controlled-queue-actor-3")
        Thread.sleep(200)
        queueActor ! NextItem

        queueActor ! WaitingConsumerQuery
        expectMsg(200.millis, WaitingConsumerQueryResult(Queue(ActorPath.fromString("akka://RateControlledQueueActorTest/system/testActor-1"))))
      }
    }

    describe("Item") {
      it("should enqueue item to non-consumed item queue if waiting consumer queue is empty") {
        val producer = system.actorOf(Props[Producer], "test-producer-3")
        val queueActor = system.actorOf(Props(new RateControlledQueueActor(producer, 10)), "rate-controlled-queue-actor-4")
        Thread.sleep(200)

        queueActor ! NonConsumedItemQueueQuery
        expectMsg(NonConsumedItemQueueQueryResult[Int](Queue(Item(1))))
      }
    }

  }

  def producerWithNothingToProduce: ActorRef = system.actorOf(Props(new Producer() {
    override def receive: Receive = {
      case NextItem => // No item to return
    }
  }), "test-producer-2")


}

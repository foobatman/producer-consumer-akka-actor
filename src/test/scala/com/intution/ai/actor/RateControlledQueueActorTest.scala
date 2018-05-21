package com.intution.ai.actor

import akka.actor.{ActorPath, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
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
      val queue = system.actorOf(Props(new RateControlledQueueActor(testActor, 10)), "rate-controlled-queue-actor")
      expectMsg(NextItem)
    }

  }
  describe("RateControlledQueueActor#receive") {

    describe("NextItem") {
      it("should dequeue the next item from item queue if available and give to consumer") {
        val producer = system.actorOf(Props[Producer], "test-producer")
        val queueActor = system.actorOf(Props(new RateControlledQueueActor(producer, 10)), "rate-controlled-queue-actor")
        Thread.sleep(200)
        queueActor ! NextItem
        expectMsg(200.millis, Item(1))
      }

      it("should enqueue the consumer into consumer queue if next item not available") {
        val producer = system.actorOf(Props[Producer], "test-producer")
        val queueActor = system.actorOf(Props(new RateControlledQueueActor(producer, 10)), "rate-controlled-queue-actor")
        Thread.sleep(200)
        queueActor ! NextItem
        expectMsg(200.millis, Item(1))
        Thread.sleep(200)
        queueActor ! NextItem

        queueActor ! WaitingConsumerQuery
        expectMsg(200.millis, WaitingConsumerQueryResult(Queue(ActorPath.fromString("akka://RateControlledQueueActorTest/system/testActor-1"))))
      }

    }

    describe("Item") {
      it("should enqueue item to itemQueue if consumerQueue is empty") {
        val producer = system.actorOf(Props[Producer], "test-producer")
        val queueActor = system.actorOf(Props(new RateControlledQueueActor(producer, 10)), "rate-controlled-queue-actor")
        queueActor ! QueueQuery
        Thread.sleep(200)
        expectMsg(QueueQueryResult[Int](Queue(Item(1))))
      }
    }

  }

}

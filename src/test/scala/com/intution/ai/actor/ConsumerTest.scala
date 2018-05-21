package com.intution.ai.actor

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.intution.ai.actor.Messages.{LastConsumedItem, NextItem}
import com.intution.ai.data.Item
import org.scalatest.{BeforeAndAfterAll, FunSpecLike, Matchers}

class ConsumerTest() extends TestKit(ActorSystem("ConsumerActorTest")) with ImplicitSender
  with FunSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  describe("Consumer#preStart") {
    it("should send message NextItem to queue actor") {
      val consumer = system.actorOf(Props(new Consumer(testActor)))
      expectMsg(NextItem)
    }
  }

  describe("Consumer#Receive") {

    describe("Item") {
      it("should consume the Item and signal NextItem to queue actor") {
        val producer = system.actorOf(Props[Producer], "test-producer")
        val queueActor = system.actorOf(Props(new RateControlledQueueActor(producer, 10)), "rate-controlled-queue-actor")
        val consumer = system.actorOf(Props(new Consumer(queueActor)), "test-consumer")
        Thread.sleep(200)

        consumer ! LastConsumedItem

        expectMsgAllOf(Item(10))
      }
    }
  }

}

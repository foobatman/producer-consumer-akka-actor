package com.intution.ai.actor

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.intution.ai.actor.Messages.NextItem
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
}

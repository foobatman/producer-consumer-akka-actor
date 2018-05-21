package com.intution.ai.actor

import akka.actor.{Actor, ActorRef}
import com.intution.ai.actor.Messages.{LastConsumedItem, NextItem}
import com.intution.ai.data.Item

class Consumer(queueActor: ActorRef) extends Actor {
  var lastConsumedItem: Item[Int] = Item[Int](-1)

  override def preStart(): Unit = {
    super.preStart()

    queueActor ! NextItem
  }


  override def receive: Receive = {
    case item: Item[Int] => {
      val queueActor = sender()
      consume(item)
      lastConsumedItem = item
      queueActor ! NextItem
    }
    case LastConsumedItem => sender ! lastConsumedItem
  }

  def consume(item: Item[Int]): Unit = {
    // potentially slower / faster consume method
  }
}

package com.intution.ai.actor

import akka.actor.{Actor, ActorRef}
import com.intution.ai.actor.Messages.NextItem

class Consumer(queueActor: ActorRef) extends Actor{

  override def preStart(): Unit = {
    super.preStart()

    queueActor ! NextItem
  }

  override def receive: Receive = {
    case _ =>
  }
}

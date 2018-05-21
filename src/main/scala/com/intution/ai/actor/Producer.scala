package com.intution.ai.actor

import akka.actor.{Actor, ActorRef}
import com.intution.ai.actor.Messages.NextItem
import com.intution.ai.data.Item

class Producer extends Actor {

  override def receive: Receive = {
    case NextItem => {
      val queueActor = sender
      produceNextItem() match {
        case Some(item) => queueActor ! item
        case None => // If no data available, don't do anything
      }
    }
  }

  def produceNextItem(): Option[Item[Int]] = if (dataSource.hasNext) Some(Item(dataSource.next())) else None

  private lazy val dataSource: Iterator[Int] = {
    1 to 10
  }.toIterator
}

package com.intution.ai.actor

import akka.actor.{Actor, ActorRef}
import com.intution.ai.actor.Messages.NextItem
import com.intution.ai.data.Item

class Producer extends Actor {

  override def receive: Receive = {
    case NextItem => sender ! produceNextItem()
  }

  def produceNextItem(): Item[Int] = Item(dataSource.next())

  private lazy val dataSource: Iterator[Int] = {
    1 to 10
  }.toIterator
}

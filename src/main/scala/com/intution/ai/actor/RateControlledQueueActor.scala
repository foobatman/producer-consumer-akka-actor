package com.intution.ai.actor

import akka.actor.{Actor, ActorRef}
import com.intution.ai.actor.Messages._
import com.intution.ai.data.Item

import scala.collection.immutable.Queue

class RateControlledQueueActor(producer: ActorRef, size: Int) extends Actor {
  var itemQueue: Queue[Item[Int]] = Queue.empty[Item[Int]]
  var consumerQueue: Queue[ActorRef] = Queue.empty[ActorRef]

  override def preStart(): Unit = {
    super.preStart()
    producer ! NextItem
  }

  override def receive: Receive = {

    case NextItem =>
      val consumer = sender()
      if (itemQueue.nonEmpty) {
        val (item, modifiedItemQueue) = itemQueue.dequeue
        itemQueue = modifiedItemQueue
        consumer ! item
      } else {
        val modifiedConsumerQueue = consumerQueue.enqueue(consumer)
        consumerQueue = modifiedConsumerQueue
      }

    case item: Item[Int] =>
      if (consumerQueue.nonEmpty) {
        val (consumer, modifiedConsumerQueue) = consumerQueue.dequeue
        consumerQueue = modifiedConsumerQueue
        consumer ! item
        producer ! NextItem
      } else {
        val modifiedItemQueue = itemQueue.enqueue(item)
        itemQueue = modifiedItemQueue
      }

    case QueueQuery => sender ! {
      QueueQueryResult[Int](itemQueue)
    }

    case WaitingConsumerQuery => sender ! WaitingConsumerQueryResult(consumerQueue.map(_.path))

  }


}

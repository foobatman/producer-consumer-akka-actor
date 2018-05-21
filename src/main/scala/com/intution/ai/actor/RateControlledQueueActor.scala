package com.intution.ai.actor

import akka.actor.{Actor, ActorRef}
import com.intution.ai.actor.Messages._
import com.intution.ai.data.Item

import scala.collection.immutable.Queue

class RateControlledQueueActor(producer: ActorRef, size: Int) extends Actor {
  var nonConsumedItemQueue: Queue[Item[Int]] = Queue.empty[Item[Int]]
  var waitingConsumerQueue: Queue[ActorRef] = Queue.empty[ActorRef]

  override def preStart(): Unit = {
    super.preStart()
    producer ! NextItem
  }

  override def receive: Receive = {

    case NextItem =>
      val consumer = sender()
      if (nonConsumedItemQueue.nonEmpty) {
        val (item, remainingNonConsumedItemQueue) = nonConsumedItemQueue.dequeue
        nonConsumedItemQueue = remainingNonConsumedItemQueue
        consumer ! item
      } else {
        val newWaitingConsumerQueue = waitingConsumerQueue.enqueue(consumer)
        waitingConsumerQueue = newWaitingConsumerQueue
      }

    case item: Item[Int] =>
      if (waitingConsumerQueue.nonEmpty) {
        val (consumer, remainingWaitingConsumerQueue) = waitingConsumerQueue.dequeue
        waitingConsumerQueue = remainingWaitingConsumerQueue
        consumer ! item
        producer ! NextItem
      } else {
        val modifiedItemQueue = nonConsumedItemQueue.enqueue(item)
        nonConsumedItemQueue = modifiedItemQueue
      }

    case QueueQuery => sender ! {
      QueueQueryResult[Int](nonConsumedItemQueue)
    }

    case WaitingConsumerQuery => sender ! WaitingConsumerQueryResult(waitingConsumerQueue.map(_.path))

  }


}

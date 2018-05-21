package com.intution.ai.actor

import akka.actor.ActorPath
import com.intution.ai.data.Item

import scala.collection.immutable.Queue

object Messages {
  case object NextItem
  case object NonConsumedItemQueueQuery
  case class NonConsumedItemQueueQueryResult[T](queue: Queue[Item[T]])
  case object WaitingConsumerQuery
  case class WaitingConsumerQueryResult(consumers: Queue[ActorPath])
  case object LastConsumedItem
}

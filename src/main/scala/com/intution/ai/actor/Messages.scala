package com.intution.ai.actor

import com.intution.ai.data.Item

import scala.collection.immutable.Queue

object Messages {
  case object NextItem
  case object QueueQuery
  case class QueueQueryResult[T](queue: Queue[Item[T]])
}

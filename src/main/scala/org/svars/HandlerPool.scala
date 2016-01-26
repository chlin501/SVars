package org.svars

import collection.mutable.{ Queue, ArrayBuffer }

import concurrent.{ Future, ExecutionContext }

import java.util.concurrent.atomic.AtomicInteger

case class HandlerPool[D, T](lattice: Lattice[D, T])(implicit executionContext: ExecutionContext) {

  val QuiesceSleepingMilliseconds = 50

  val runningTasks = new AtomicInteger()

  val handlerQueue = new Queue[Tuple2[Seq[D], Function1[T, Unit]]]()

  def quiesce = while (runningTasks.get != 0) Thread.sleep(QuiesceSleepingMilliseconds)

  def addHandler(xs: Set[D], cb: T => Unit): Unit = handlerQueue.synchronized {
    handlerQueue += ((xs, cb))
  }

  def handleElement(store: D, element: T) = handlerQueue.foreach { case(limits, function) =>
    limits.foreach { limit =>
      if (lattice.<(limit, store)) {
        Future {
          runningTasks.incrementAndGet
          function(element) // ????
          runningTasks.decrementAndGet
        }
      }
    }
  }

}

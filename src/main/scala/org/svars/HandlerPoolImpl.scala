package org.svars

import collection.mutable.{ Queue, ArrayBuffer }

import concurrent.{ Future, ExecutionContext, blocking }

import java.util.concurrent.atomic.AtomicInteger

case class HandlerPoolImpl[D, T](lattice: Lattice[D, T])(implicit executionContext: ExecutionContext) extends HandlerPool[D, T] {

  val runningFutures = new AtomicInteger()

  val handlerQueue = new Queue[Tuple2[Set[D], Function1[D, Unit]]]()

  override def quiesce(function: => D): Future[D] = Future {
    blocking {
      while (runningFutures.get != 0) { }
      function
    }
  }

  override def addHandler(xs: Set[D], cb: D => Unit): Unit = handlerQueue.synchronized {
    handlerQueue += ((xs, cb))
  }

  override def handleElement(store: D) = handlerQueue.foreach { case(limits, function) =>
    limits.foreach { limit =>
      if (lattice < (limit, store)) postFuture(() => function(limit))
    }
  }

  override def postFuture(function: => Unit) = Future {
    try {
      runningFutures.incrementAndGet
      function
    } finally {
      runningFutures.decrementAndGet
    }
  }

}

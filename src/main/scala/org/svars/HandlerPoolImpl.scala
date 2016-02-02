package org.svars

import concurrent.{ Future, ExecutionContext, blocking }

import java.util.concurrent.atomic.AtomicInteger

case class HandlerPoolImpl[D, T](lattice: Lattice[D, T])(implicit executionContext: ExecutionContext) extends HandlerPool[D, T] {

  val runningPuts = new AtomicInteger()

  override def doPut(function: => Unit): Unit = Future {
    runningPuts.incrementAndGet
    try {
      function
    } finally {
      runningPuts.decrementAndGet
    }
  }.onFailure { case t => throw t }

  override def doFuture(function: => Unit): Unit = Future {
    function
  }

  override def quiesce(function: => D): Future[D] = Future {
    blocking { while (runningPuts.get != 0) { } }
    function
  }

}

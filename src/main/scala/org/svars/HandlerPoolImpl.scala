package org.svars

import concurrent.{ Future, ExecutionContext, blocking }

import java.util.concurrent.atomic.{ AtomicInteger, AtomicBoolean }

case class HandlerPoolImpl[D, T](lattice: Lattice[D, T])(implicit executionContext: ExecutionContext) extends HandlerPool[D, T] {

  private val runningPuts = new AtomicInteger()

  private val hasHadInvalidPut = new AtomicBoolean()

  override def doPut(function: => Unit): Future[Unit] = Future {
    runningPuts.incrementAndGet

    try {
      function
    } catch {
      case e: LVarFrozenException[D, T] =>
        hasHadInvalidPut.set(true)
        throw e
    } finally {
      runningPuts.decrementAndGet
    }
  }

  override def doFuture(function: => Unit): Future[Unit] = Future {
    function
  }

  override def quiesce(function: => D): Future[D] = Future {
    if (hasHadInvalidPut.get) throw new IllegalStateException()
    blocking { while (runningPuts.get != 0) { } }
    function
  }

}

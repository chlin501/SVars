package org.svars

import collection.mutable.{ Queue, ArrayBuffer }

import concurrent.{ Future, ExecutionContext, blocking }

import java.util.concurrent.atomic.AtomicInteger

trait HandlerPool[D, T] {

  def quiesce(function: => D): Future[D]

  def addHandler(xs: Set[D], cb: D => Unit, history: Set[D]): Unit

  def processNewElement(store: D): Unit

  def postFuture(function: => Unit): Unit

}

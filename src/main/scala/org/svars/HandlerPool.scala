package org.svars

import collection.mutable.{ Queue, ArrayBuffer }

import concurrent.{ Future, ExecutionContext, blocking }

import java.util.concurrent.atomic.AtomicInteger

trait HandlerPool[D, T] {

  def quiesce(function: => D): Future[D]

  def addHandler(xs: Set[D], cb: D => Unit): Unit

  def processElement(store: D): Unit

  def doPut(function: => Unit): Unit

}

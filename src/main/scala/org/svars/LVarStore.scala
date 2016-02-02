package org.svars

import java.util.concurrent.atomic.AtomicReference

class LVarStore[T] {

  private val set = new AtomicReference[Set[T]](Set.empty)

  def add(element: T): Unit = {
    var success = false

    do {
      val setReference = set.get

      val newSet = setReference + element

      success = newSet.size == setReference.size || set.compareAndSet(setReference, newSet)
    } while (!success)
  }

  def addHandler(callback: T => Unit): Unit = ???

}

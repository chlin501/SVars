package org.svars

import java.util.concurrent.atomic.AtomicReference

class CallbackHandler[D, T](val thresholdSet: Set[D], val lattice: Lattice[D, T], val callback: D => Unit) {

  private val threshold = new AtomicReference(thresholdSet.toList.sortWith((a, b) => lattice < (a, b)))

  def tick(state: D): Unit = {
    var stop = false

    while (!stop) {
      val list = threshold.get

      list match {
        case head :: tail if lattice < (head, state) && threshold.compareAndSet(list, tail) =>
          callback(head)
        case _ =>
          stop = true
      }
    }
  }
}

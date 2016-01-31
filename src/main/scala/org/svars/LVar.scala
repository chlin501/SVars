package org.svars

import java.util.concurrent.atomic.{ AtomicReference, AtomicBoolean }

import concurrent.Future

class LVar[D, T](val lattice: Lattice[D, T], val handlerPool: HandlerPool[D, T]) {

  private val data = new AtomicReference(lattice.empty)

  private val store = new LVarStore[D]()

  // LVish paper gives a quite complicated explanation
  // about races between the put calls, get calls and freeze calls.
  // However since no get call is going to be implemented, all
  // complications belonging to races are removed.
  def put(element: T): this.type = {

    handlerPool.doPut {
      var success = false
      var alreadyAdded = false
      var joined: D = lattice.empty

      do {
        val dataReference = data.get

        joined = lattice.add(dataReference, element)
        alreadyAdded = dataReference == joined

        success = alreadyAdded || data.compareAndSet(dataReference, joined)

      } while (!success)

      if (!alreadyAdded) {
        store.add(joined)
        handlerPool.processElement(joined)
      }
    }

    this
  }

  // ??? weird
  def addHandler(xs: Set[D])(cb: D => Unit): this.type = {
    handlerPool.addHandler(xs, cb)

    this
  }

  // Races between this call and new calls to the method put is
  // why the LVar is quasi-deterministic and not deterministic.
  // This is how the LVish paper solves it. Check page 7 of here:
  // http://www.cs.indiana.edu/l/www/ftp/techreports/TR710.pdf
  def freeze(): Future[D] = handlerPool.quiesce {
    data.get
  }

}

object LVar {

  def apply[D, T](lattice: Lattice[D, T]): LVar[D, T] = new LVar[D, T](lattice, ???)

}

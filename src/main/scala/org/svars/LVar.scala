package org.svars

import java.util.concurrent.atomic.{ AtomicReference, AtomicBoolean }

import concurrent.Future

class LVar[D, T](val lattice: Lattice[D, T], val handlerPool: HandlerPool[D, T]) {

  private val frozen = new AtomicBoolean()

  private val data = new AtomicReference(lattice.empty)

  private val store = new LVarStore[D]()

  def put(element: T): this.type = {

    handlerPool.doPut {
      var success = false

      do {
        val dataReference = data.get

        val joined = lattice.add(dataReference, element)
        val alreadyAdded = dataReference == joined

        success = alreadyAdded || data.compareAndSet(dataReference, joined)

        if (success) {
          if (frozen.get) throw LVarFrozenException()

          if (!alreadyAdded) {
            store.add(joined)
          }
        }

      } while (!success)
    }

    this
  }

  def addHandler(xs: Set[D])(cb: D => Unit): this.type = {

    val callback = (s: D) => {
      xs.foreach { x =>
        if (lattice < (x, s)) handlerPool.doFuture { cb(x) }
      }
    }

    this
  }

  def freeze(): Future[D] = handlerPool.quiesce {
    frozen.set(true)
    data.get
  }

}

object LVar {

  def apply[D, T](lattice: Lattice[D, T]): LVar[D, T] = new LVar[D, T](lattice, ???)

}

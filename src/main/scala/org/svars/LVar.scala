package org.svars

import java.util.concurrent.atomic.AtomicReference

import concurrent.Future

class LVar[D, T](val lattice: Lattice[D, T], val handlerPool: HandlerPool[D, T]) {

  @volatile private var frozen = false

  @volatile private var data = new AtomicReference(lattice.empty)

  def put(v: T): this.type = {
    if (frozen) throw LVarFrozenException(this)

    handlerPool.postFuture { internalPut(v) }

    this
  }

  private def internalPut(v: T): Unit = {
    var success = false
    var alreadyAdded = false
    var joined: D = lattice.empty

    do {
      val fetchedData = data.get

      joined = lattice.add(fetchedData, v)
      alreadyAdded = fetchedData == joined

      success = alreadyAdded || data.compareAndSet(fetchedData, joined)

    } while (!success)

    if (!alreadyAdded) handlerPool.processNewElement(joined)
  }

  def addHandler(xs: Set[D])(cb: D => Unit): this.type = {
    if (frozen) throw LVarFrozenException(this)

    handlerPool.addHandler(xs, cb, history)
  }

  // Races between this call and new calls to the method put is
  // why the LVar is quasi-deterministic and not deterministic.
  // This is how the LVish paper solves it. Check page 7 of here:
  // http://www.cs.indiana.edu/l/www/ftp/techreports/TR710.pdf
  def freeze(): Future[D] = handlerPool.quiesce {
    frozen = true
    data.get
  }

}

object LVar {

  def apply[D, T](lattice: Lattice[D, T]): LVar[D, T] = new LVar[D, T](lattice, ???)

}

package org.svars

import java.util.concurrent.atomic.AtomicReference

import concurrent.Future

class LVar[D, T](val lattice: Lattice[D, T]) {

  @volatile private var frozen = false

  @volatile private var data = new AtomicReference(lattice.empty)

  def put(v: T): this.type = {
    if (frozen) throw LVarFrozenException(this)

    var success = false
    var alreadyAdded = false

    do {
      val fetchedData = data.get

      val joined = lattice.add(fetchedData, v)
      alreadyAdded = fetchedData == joined

      success = alreadyAdded || data.compareAndSet(fetchedData, joined)

    } while (!success)

    this
  }

  def addHandler(xs: Set[D])(cb: T => Unit): this.type = ???

  def freeze(): Future[D] = {
    frozen = true
    ???
  }

}

object LVar {

  def apply[D, T](lattice: Lattice[D, T]): LVar[D, T] = new LVar[D, T](lattice)

}

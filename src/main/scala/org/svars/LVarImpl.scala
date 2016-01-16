package org.svars

final private[svars] class LVarImpl[D, T](val lattice: Lattice[D, T])
    extends LVar[D, T] {

  @volatile private var frozen = false

  @volatile def put(v: T): this.type = {
    if (frozen) throw LVarFrozenException(this)

    lattice += v

    this
  }

  def addHandler(threshold: D)(cb: T => Unit): this.type = ???

  def freeze(): Lattice[D, T] = {
    frozen = true
    lattice
  }

}

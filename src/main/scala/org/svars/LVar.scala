package org.svars

import org.svars

trait LVar[D, T] {

  def put(x: T): this.type

  def addHandler(threshold: D)(cb: T => Unit): this.type

  def freeze(): Lattice[D, T]
}

object LVar {

  def apply[D, T](lattice: Lattice[D, T]): LVar[D, T] = new LVarImpl[D, T](lattice)

}

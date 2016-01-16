package org.svars.lattices

import org.svars.Lattice

class SetLattice[T] extends Lattice[Set[T], T] {

  private var set = Set[T]().empty

  override def get: Set[T] = set

  override def += (v: T): Unit = synchronized {
    set = set + v
  }

  override def < (v: Set[T]): Boolean = set.size < v.size
}

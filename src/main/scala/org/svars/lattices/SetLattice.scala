package org.svars.lattices

import org.svars.Lattice

class SetLattice[T] extends Lattice[Set[T], T] {

  override def add(store: Set[T], element: T): Set[T] = store + element

  override def <(lhs: Set[T], rhs: Set[T]): Boolean = lhs.size < rhs.size

  override val empty: Set[T] = Set.empty

}

package org.svars.lattices

import org.svars.Lattice

class CountLattice extends Lattice[Int, Int] {

  override def add(store: Int, element: Int): Int = store + 1

  override def <(lhs: Int, rhs: Int): Boolean = lhs < rhs

  override val empty: Int = 0

}

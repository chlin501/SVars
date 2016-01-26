package org.svars.lattices

import org.svars._

class SingleLattice[T] extends Lattice[Option[T], T] {

  override def add(store: Option[T], element: T): Option[T] = store match {
    case Some(v) if element != v => throw LVarLatticeViolationException(store, element)
    case _ => Some(element)
  }

  override def <(lhs: Option[T], rhs: Option[T]): Boolean = lhs.isEmpty && !rhs.isEmpty

  override val empty: Option[T] = None

}

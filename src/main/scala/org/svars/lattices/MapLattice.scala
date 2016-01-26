package org.svars.lattices

import org.svars._

class MapLattice[K, V] extends Lattice[Map[K, V], Tuple2[K, V]] {

  override def add(store: Map[K, V], element: Tuple2[K, V]): Map[K, V] =
    store(element._1) match {
      case Some(v) if element._2 != v => throw LVarLatticeViolationException(store, element)
      case _ => store + element
    }

  override def <(lhs: Map[K, V], rhs: Map[K, V]): Boolean = lhs.size < rhs.size

  override val empty: Map[K, V] = Map.empty

}

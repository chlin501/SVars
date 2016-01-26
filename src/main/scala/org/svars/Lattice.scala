package org.svars

trait Lattice[D, T] {

  def add(store: D, element: T): D

  def <(lhs: D, rhs: D): Boolean

  val empty: D

}

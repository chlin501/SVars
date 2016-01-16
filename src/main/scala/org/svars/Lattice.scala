package org.svars

trait Lattice[D, T] {

  def get: D

  def += (v: T): Unit

  def < (v: D): Boolean

}

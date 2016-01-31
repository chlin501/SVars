package org.svars

final case class LVarFrozenException[D, T]() extends IllegalStateException(
  s"LVar frozen!"
)

final case class LVarLatticeViolationException[D, T](store: D, element: T) extends IllegalStateException(
  s"The lattice with store $store and element $element has been violated!"
)

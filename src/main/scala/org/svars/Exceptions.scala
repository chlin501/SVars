package org.svars

final case class LVarFrozenException[D, T](lvar: LVar[D, T]) extends IllegalStateException(
  s"LVar $lvar has already been frozen!"
)

final case class LVarLatticeViolationException[D, T](store: D, element: T) extends IllegalStateException(
  s"The lattice with store $store and element $element has been violated!"
)

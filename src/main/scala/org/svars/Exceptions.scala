package org.svars

final case class LVarFrozenException[D, T](lvar: LVarImpl[D, T]) extends RuntimeException(
  s"LVar $lvar has already been frozen!"
)

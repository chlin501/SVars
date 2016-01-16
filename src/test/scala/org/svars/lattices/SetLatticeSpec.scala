package org.svars.lattices

import org.svars._

import org.scalatest._

class SetLatticeSpec extends SVarsSpec {

  "A LVar of a Set of Integers" when {
    "inserted one element" should {
      "freeze and give back that element" in {
        val lvar = LVar(new SetLattice[Int])

        lvar.put(1)

        val set = lvar.freeze.get

        set should be (Set(1))
      }
    }
  }
}

package org.svars.lattices

import org.svars._

import concurrent.Await
import concurrent.duration._

import java.util.concurrent.atomic.AtomicInteger

import org.scalatest._

class SetLatticeSpec extends LVarSpec {

  "A LVar of a Set of Integers" when {
    "inserted one element" should {
      "return the correct set when frozen" in {
        val lvar = LVar.withDefaultHandlerPool(new SetLattice[Int])

        lvar.put(1)

        lvar.freeze.futureValue should be (Set(1))
      }

      "call the callbacks added" in {
        val lvar = LVar.withDefaultHandlerPool(new SetLattice[Int])

        val c = new AtomicInteger()

        val lambda: Set[Int] => Unit = (s: Set[Int]) => {
          s should be (Set())
          c.getAndIncrement
        }

        lvar.addHandler(Set(Set(), Set(1, 2)))(lambda)
        lvar.addHandler(Set(Set(), Set(1, 2)))(lambda)

        lvar.put(1)

        lvar.addHandler(Set(Set(), Set(1, 2)))(lambda)
        lvar.addHandler(Set(Set(), Set(1, 2)))(lambda)

        while (c.get != 4) { }
      }
    }

    "inserted multiple elements" should {
      "return the correct set when frozen" in {
        val lvar = LVar.withDefaultHandlerPool(new SetLattice[Int])

        var res = Set[Int]().empty

        for (i <- 0 until 5000) {
          res = res + i
          lvar.put(i)
        }

        lvar.freeze.futureValue should be (res)
      }

      "throw an exception if frozen and then put is called" in {
        val lvar = LVar.withDefaultHandlerPool(new SetLattice[Int])

        lvar.put(1)
        lvar.put(2)

        whenReady(lvar.freeze) { result =>
          result should be (Set(1, 2))

          whenReady(lvar.put(2).failed) { e =>
            e shouldBe a [LVarFrozenException[_, _]]

            whenReady(lvar.freeze.failed) { e =>
              e shouldBe a [IllegalStateException]
            }
          }
        }
      }
    }
  }
}

## SVars - A LVar implementation in Scala

### Introduction
LVars originates from [this](https://www.cs.indiana.edu/~lkuper/papers/lvars-fhpc13.pdf) paper and have since then been [extended](https://www.cs.indiana.edu/~lkuper/papers/lvish-popl14.pdf). LVars are lattice-based quasi-deterministic data structures and here I've implemented a subset of supported operations for them. [Here](https://hackage.haskell.org/package/lvish) is the original Haskell package.

### Features
The package `org.svars.lattices` contains various supported lattices. It is also possible to construct custom lattices if you extend `org.svars.Lattice[D, T]`. Currently `Set`, `Map`, `Count` and `Single` lattices are supported. The `D` type represents the lattice, e.g. a `Set[T]`, and the `T` type represents an element inside the lattice.

The extendable LVar class can be found at `org.svars.LVar[D, T]`. It currently only supports `put`, `addHandler` and `freeze`. It takes two constructor arguments, the lattice and a handler pool.

The handler pool interface can be found at `org.svars.HandlerPool[D, T]` and has three methods that needs to be implemented. Take a look at `org.svars.HandlerPoolImpl[D, T]` for an example implementation. A major benefit of implementing your own handler pool would be to better detect quiescence.

### Examples
Here are a few examples:

```scala
val lvar = LVar.withDefaultHandlerPool(new SetLattice[Int])

// Takes the lattice from the empty set to Set(1)
lvar.put(1)

// No effect
lvar.put(1)

// Takes the lattice from Set(1) to Set(1, 2)
lvar.put(2)

val lambda: Set[Int] => Unit = (s: Set[Int]) => { println(s) }

// Adds an event handler. First argument are the thresholds, second the lambda.
// Whenever a threshold is traversed, the lambda is run with the threshold as
// argument. This will print "Set()" and then "Set(1)".
lvar.addHandler(Set(Set(), Set(1)))(lambda)

// Freezes the lvar, f is a future with the value Set(1, 2).
// The freeze method waits for quiescence.
val f = lvar.freeze

// Now, when the future is finished, you can't call put, it will throw
// an exception (inside the future it returns).
```

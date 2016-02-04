package org.svars

import java.util.concurrent.atomic.{ AtomicReference, AtomicBoolean }

import concurrent.Future

class LVar[D, T](val lattice: Lattice[D, T], val handlerPool: HandlerPool[D, T]) {

  private val frozen = new AtomicBoolean()

  private val data = new AtomicReference(lattice.empty)

  private val callbacks = new AtomicReference(Set[CallbackHandler[D, T]]().empty)

  def put(element: T): Future[Unit] = handlerPool.doPut {
    var success = false

    do {
      val dataReference = data.get

      val joined = lattice.add(dataReference, element)
      val alreadyAdded = dataReference == joined

      success = alreadyAdded || data.compareAndSet(dataReference, joined)

      if (success) {
        if (frozen.get) throw LVarFrozenException()

        if (!alreadyAdded) callbacks.get.foreach { cb =>
          handlerPool.doFuture { cb.tick(joined) }
        }
      }

    } while (!success)
  }

  def addHandler(xs: Set[D])(cb: D => Unit): this.type = {

    val callbackHandler = new CallbackHandler(xs, lattice, cb)

    var success = false

    do {
      val cbs = callbacks.get
      success = callbacks.compareAndSet(cbs, cbs + callbackHandler)
    } while (!success)

    callbackHandler.tick(data.get)

    this
  }

  def freeze(): Future[D] = handlerPool.quiesce {
    frozen.set(true)
    data.get
  }

}

object LVar {

  def apply[D, T](lattice: Lattice[D, T], handlerPool: HandlerPool[D, T]): LVar[D, T] =
    new LVar[D, T](lattice, handlerPool)

  def withDefaultHandlerPool[D, T](lattice: Lattice[D, T]): LVar[D, T] = {
    implicit val ec = concurrent.ExecutionContext.Implicits.global
    LVar(lattice, new HandlerPoolImpl(lattice))
  }

}

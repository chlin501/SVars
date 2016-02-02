package org.svars

class CallbackHandler[D](val store: LVarStore[D], val callback: D => Unit) {

  private var myPosition = 0

  def tick(limit: Int): Unit = {

  }
}

package org.svars

import java.util.concurrent.atomic.{ AtomicReference, AtomicInteger }

class Node[T](val value: T) {
  @volatile var next: Option[Node[T]] = None
}

class LVarStore[T] {

  private val start = new AtomicReference[Node[T]]()

  private val current = new AtomicReference[Node[T]]()

  private val size = new AtomicInteger()

  def add(element: T): Unit = {
    val newNode = new Node[T](element)

    if (start.compareAndSet(null, newNode)) current.set(newNode)
    else {
      var currentNode: Node[T] = null
      var success = false

      do {
        currentNode = current.get
        if (currentNode != null) {
          currentNode.next = Some(newNode)
          success = current.compareAndSet(currentNode, newNode)
        }
      } while (!success)
    }
  }

  def getStartNode: Option[Node[T]] = {
    val startNode = start.get

    if (startNode == null) None
    else Some(startNode)
  }
}

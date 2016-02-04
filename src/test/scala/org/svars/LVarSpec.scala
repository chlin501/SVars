package org.svars

import concurrent.ExecutionContext.Implicits.global

import org.scalatest._
import org.scalatest.concurrent._

abstract class LVarSpec extends WordSpec with Matchers with ScalaFutures

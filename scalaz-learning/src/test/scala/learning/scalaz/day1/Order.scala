package learning.scalaz.day1

import org.scalatest.{FreeSpec, Matchers}
import scalaz._
import Scalaz._

/**
  * Created by williame on 4/29/17.
  */
class Order extends FreeSpec with Matchers {

  "Order type class" in {
    1 > 2.0 should be(false)
    1 gt 2 should be(false)

    1.0 ?|? 2.0 should be(Ordering.LT)
    1.0 max 2.0 should be(2.0)

    1.0 lt 2.0 should be(true)
  }
}

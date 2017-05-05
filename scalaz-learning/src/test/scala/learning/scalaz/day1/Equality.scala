package learning.scalaz.day1

import org.scalatest.{FreeSpec, Matchers}
import scalaz._

/**
  * Created by williame on 4/29/17.
  */
class Equality extends FreeSpec with Matchers {

  "Scalaz equality" in {
    1 === 1 should be(true)
    "test" === "hello" should be(false)
  }
}

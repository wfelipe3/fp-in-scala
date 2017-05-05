package learning.scalaz.day1

import org.scalatest.{FreeSpec, Matchers}
import scalaz._
import Scalaz._

/**
  * Created by williame on 4/29/17.
  */
class EnumTypeClass extends FreeSpec with Matchers {

  "Enum type class" in {
    ('a' to 'e').toList should be(List('a', 'b', 'c', 'd', 'e'))
    ('a' |-> 'e') should be(List('a', 'b', 'c', 'd', 'e'))
    (3 |=> 5).toList should be(List(3, 4, 5))
    10.succ should be(11)
  }

  "Bounded type class with Enum" in {
    implicitly[Enum[Int]].min should be(Some(Int.MinValue))
    implicitly[Enum[Int]].max should be(Some(Int.MaxValue))
  }
}

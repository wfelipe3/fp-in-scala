package learning.scalaz.day1

import org.scalatest.{FreeSpec, Matchers}

import scalaz._
import Scalaz._
import scalaz.syntax.ShowOps

/**
  * Created by williame on 4/29/17.
  */
class ShowTypeClass extends FreeSpec with Matchers {

  def testShowTypeClassUsage[A](value: ShowOps[A]) = s"this is a test with value ${value.shows.replaceAll("\"", "")}"

  "Show type class" in {
    4.shows should be("4")
    println(3.show)
    testShowTypeClassUsage(4) should be("this is a test with value 4")
    testShowTypeClassUsage("nice") should be("this is a test with value nice")
    val values = 1 :: 2 :: 3 :: Nil |> (_.map(i => s"the value is $i")) |> (_.mkString(", "))
    val values2 = (1 :: 2 :: 3 :: Nil)
      .map(i => s"the value is $i")
      .mkString(", ")
    println(values)
  }
}

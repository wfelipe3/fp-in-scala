package chap02

import org.scalatest.{FreeSpec, Matchers}

/**
  * Created by dev-williame on 4/25/17.
  */
class GettingStartedWithFunctions extends FreeSpec with Matchers {

  "Get fibonacci number" in {
    def fib(n: Int): Int = {
      if (n == 0 || n == 1)
        n
      else
        fib(n - 1) + fib(n - 2)
    }

    fib(0) should be(0)
    fib(1) should be(1)
    fib(2) should be(1)
    fib(3) should be(2)
    fib(4) should be(3)
    fib(5) should be(5)
    fib(6) should be(8)
    println(fib(1000))
  }
}

package chap03

import org.scalatest.{FreeSpec, Matchers}

import scala.annotation.tailrec

/**
  * Created by williame on 6/9/17.
  */
class FunctionalDataStructuresTest extends FreeSpec with Matchers {

  sealed trait List[+A]

  case object Nil extends List[Nothing]

  case class Cons[+A](head: A, tail: List[A]) extends List[A]

  object List {
    def sum(ints: List[Int]): Int = ints match {
      case Nil => 0
      case Cons(head, tail) => head + sum(tail)
    }

    def product(ds: List[Double]): Double = ds match {
      case Nil => 1
      case Cons(0, _) => 0
      case Cons(head, tail) => head * product(tail)
    }

    def apply[A](as: A*): List[A] = {
      if (as.isEmpty) Nil
      else Cons(as.head, apply(as.tail: _*))
    }

    def tail[A](ds: List[A]): List[A] = ds match {
      case Nil => Nil
      case Cons(_, tail) => tail
    }


    def setHead[A](ds: List[A], h: A): List[A] = Cons(h, tail(ds))
    @tailrec
    def drop[A](l: List[A], n: Int): List[A] = {
      if (n <= 0) l
      else drop(tail(l), n - 1)
    }

    @tailrec
    def dropWhile[A](l: List[A], f: A => Boolean): List[A] = l match {
      case Nil => Nil
      case Cons(x, xs) => if (f(x)) dropWhile(xs, f) else l
    }

    def append[A](a1: List[A], a2: List[A]): List[A] =
      a1 match {
        case Nil => a2
        case Cons(h, t) => Cons(h, append(t, a2))
      }

    def init[A](l: List[A]): List[A] = l match {
      case Nil => Nil
      case Cons(_, Nil) => Nil
      case Cons(h, t) => Cons(h, init(t))
    }

    def foldRight[A, B](l: List[A], z: B)(f: (A, B) => B): B = l match {
      case Nil => z
      case Cons(h, t) => f(h, foldRight(t, z)(f))
    }

    def sum2(l: List[Int]) = foldRight(l, 0)(_ + _)
    def product2(l: List[Int]) = foldRight(l, 1)(_ * _)

    def length[A](as: List[A]): Int = foldRight(as, 0)((_, b)=> 1 + b)
  }


  "Exercise 3.1" in {
    import List._

    val x = List(1, 2, 3, 4, 5) match {
      case Cons(x, Cons(2, Cons(4, _))) => x
      case Nil => 42
      case Cons(x, Cons(y, Cons(3, Cons(4, _)))) => x + y
      case Cons(h, t) => h + sum(t)
      case _ => 101
    }

    x should be(3)
  }

  "Exercise 3.2" in {
    import List._

    tail(List(1, 2, 3, 4)) should be(List(2, 3, 4))
    tail(Nil) should be(Nil)
    tail(List(1)) should be(Nil)
  }

  "Exercise 3.3" in {
    import List._
    setHead(List(1, 2, 3), 4) should be(List(4, 2, 3))
  }

  "Exercise 3.4" in {
    import List._
    drop(List(1, 2, 3, 4), 2) should be(List(3, 4))
  }

  "Exercise 3.5" in {
    import List._
    dropWhile(List(1, 2, 3, 4), (x: Int) => x < 3) should be(List(3, 4))
  }

  "Exercise 3.6" in {
    import List._
    init(List(1, 2, 3, 4)) should be(List(1, 2, 3))
    init(List(1)) should be(Nil)
  }

  "Exercise 3.7" in {
    import List._
    //it is not possible
    foldRight(List(1, 2, 3, 4), 1)(_ * _)
  }

  "Exercise 3.8" in {
    import List._

    //Cons(1, Cons(2, Cons(3, Cons(4, Nil))))
    foldRight(List(1, 2, 3, 4), Nil: List[Int])(Cons(_, _)) should be(List(1,2,3,4))
  }

  "Exercise 3.9" in {
    import List._

    List.length(List(1,2,3,4,5)) should be (5)
    List.length(List()) should be (0)
  }

}

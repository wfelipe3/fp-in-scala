package learning.scalaz.day0

import org.scalatest.{FreeSpec, Matchers}

/**
  * Created by williame on 4/28/17.
  */
class Day0Test extends FreeSpec with Matchers {

  "head is  Parametric polymorphic function that works for any A type" in {
    def head[A](xs: List[A]): A = xs.head

    case class Car(make: String)
    head(1 :: 2 :: Nil) should be(1)
    head(Car("Civic") :: Car("CR-V") :: Nil) should be(Car("Civic"))
  }

  "adhoc polymorphism" in {
    trait Plus[A] {
      def plus(a1: A, a2: A): A
    }
    def plus[A: Plus](a1: A, a2: A): A = implicitly[Plus[A]].plus(a1, a2)

    implicit val intPlus: Plus[Int] = (a1, a2) => a1 + a2
    plus(1, 2)(intPlus) should be(3)
  }

  "sum function" in {
    def sum(xs: List[Int]): Int = xs.sum

    sum(1 :: 2 :: 3 :: Nil) should be(1 + 2 + 3)
  }

  "Int monoid" in {
    object IntMonoid {
      def mappend(a: Int, b: Int): Int = a + b
      def mzero: Int = 0
    }

    def sum(x: List[Int]): Int = x.foldLeft(IntMonoid.mzero)(IntMonoid.mappend)

    sum(1 :: 2 :: 3 :: Nil) should be(1 + 2 + 3)
  }

  trait Monoid[A] {
    def mzero: A
    def mappend(a1: A, a2: A): A
  }

  object Monoid {
    implicit val intMonoid = new Monoid[Int] {
      def mappend(a: Int, b: Int): Int = a + b
      def mzero: Int = 0
    }

    implicit val stringMonoid = new Monoid[String] {
      override def mzero: String = ""
      override def mappend(a1: String, a2: String): String = a1 + a2
    }
  }

  "monoid trait" in {

    implicit object IntMonoid extends Monoid[Int] {
      def mappend(a: Int, b: Int): Int = a + b
      def mzero: Int = 0
    }

    def sum(x: List[Int], monoid: Monoid[Int]): Int = x.foldLeft(monoid.mzero)(monoid.mappend)
    sum(1 :: 2 :: 3 :: Nil, IntMonoid) should be(1 + 2 + 3)

    def sum2(xs: List[Int])(implicit m: Monoid[Int]) = xs.foldLeft(m.mzero)(m.mappend)
    sum2(1 :: 2 :: 3 :: Nil) should be(1 + 2 + 3)

    def sum3[A: Monoid](xs: List[A]) = {
      val m = implicitly[Monoid[A]]
      xs.foldLeft(m.mzero)(m.mappend)
    }
    sum3(1 :: 2 :: 3 :: Nil) should be(1 + 2 + 3)
    sum3("this" :: "is" :: "a" :: "test" :: Nil) should be("thisisatest")
  }

  "Fold left" in {
    object FoldLeftList {
      def foldLeft[A, B](xs: List[A], b: B, f: (B, A) => B) = xs.foldLeft(b)(f)
    }

    def sum[A: Monoid](xs: List[A]) = {
      val m = implicitly[Monoid[A]]
      FoldLeftList.foldLeft(xs, m.mzero, m.mappend)
    }

    sum(1 :: 2 :: 3 :: Nil) should be(1 + 2 + 3)
    sum("this" :: "is" :: "a" :: "test" :: Nil) should be("thisisatest")
  }

  trait FoldLeft[F[_]] {
    def foldLeft[A, B](xs: F[A], b: B, f: (B, A) => B): B
  }

  object FoldLeft {
    implicit val listFoldLeft = new FoldLeft[List] {
      override def foldLeft[A, B](xs: List[A], b: B, f: (B, A) => B): B = xs.foldLeft(b)(f)
    }
  }

  "Sum with Fold polymorphic fold left" in {
    def sum[M[_] : FoldLeft, A: Monoid](xs: M[A]): A = {
      val f = implicitly[FoldLeft[M]]
      val m = implicitly[Monoid[A]]
      f.foldLeft(xs, m.mzero, m.mappend)
    }

    sum(1 :: 2 :: 3 :: Nil) should be(1 + 2 + 3)
  }

  "Create operator" in {
    def plus[A: Monoid](a1: A, a2: A): A = implicitly[Monoid[A]].mappend(a1, a2)
    plus(1, 2) should be(1 + 2)

    trait MonoidOp[A] {
      val F: Monoid[A]
      val value: A
      def |+|(a2: A) = F.mappend(value, a2)
    }

    implicit def toMonoidOp[A: Monoid](a: A): MonoidOp[A] = new MonoidOp[A] {
      val F = implicitly[Monoid[A]]
      val value = a
    }

    3 |+| 4 should be(3 + 4)
    "felipe" |+| "rojas" should be("feliperojas")
  }

}

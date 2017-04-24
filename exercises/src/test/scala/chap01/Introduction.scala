package chap01

import org.scalatest.{FreeSpec, Matchers}

/**
  * Created by dev-williame on 4/24/17.
  */
class Introduction extends FreeSpec with Matchers {

  "program with side effects" in {
    // This program has side effects given the price is charged to the credit card in the buy coffee method
    // this makes harder to compose into a larger program
    class cafe {
      class CreditCard {
        def charge(price: Int) = println("charge to credit card")
      }

      case class Coffee(price: Int = 2)

      def buyCoffee(cc: CreditCard): Coffee = {
        val cup = Coffee()
        cc.charge(cup.price)
        cup
      }
    }
  }

  "Second implementation for buying coffee" in {
    //This implementation is cleaner but it steel has side effects in the buy method
    class Cafe {
      case class CreditCard(number: Int)
      case class Coffee(price: Int = 2)

      class Payments {
        def charge(price: Int, creditCard: CreditCard) = println("charge to credit card")
      }

      def buyCoffee(cc: CreditCard, p: Payments): Coffee = {
        val cup = Coffee()
        p.charge(cup.price, cc)
        cup
      }
    }
  }

  "Buying coffee without side effects" in {
    //This implementation does not have side effects, now we return a charge
    //and this allows us to compose and combine more easily
    case class CreditCard(number: Int)
    case class Coffee(price: Int = 2)

    case class Charge(cc: CreditCard, amount: Int) {
      def combine(other: Charge): Either[String, Charge] = {
        if (cc == other.cc)
          Right(Charge(cc, amount + other.amount))
        else
          Left("Credit cards are different")
      }
    }

    class Cafe {
      def buyCoffee(cc: CreditCard): (Coffee, Charge) = {
        val cup = Coffee()
        (cup, Charge(cc, cup.price))
      }

      def buyCoffees(cc: CreditCard, n: Int): (List[Coffee], Charge) = {
        val purchase = List.fill(n)(buyCoffee(cc))
        val (coffees, charges) = purchase.unzip
        (coffees, charges.reduce((c1, c2) => c1.combine(c2).right.get))
      }

      def coalesce(charges: List[Charge]): List[Charge] =
        charges.groupBy(_.cc).values.map(_.reduce((c1, c2) => c1.combine(c2).right.get)).toList
    }
  }
}

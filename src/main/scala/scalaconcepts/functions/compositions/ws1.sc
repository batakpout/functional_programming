val add: Int => Int = _ + 1
val mul: Int => Int = x => x * 2
val sub: Int => Int = (x: Int) => x - 1

add(mul(2))
(add compose mul) (2)

mul(add(2))
(add andThen mul) (2)

//  (f compose g)(x) = f(g(x))
//  (f andThen g)(x) = g(f(x))

/**
 * Donut Bill, apply tax first then discount
 */

val totalCost = 100
val applyDiscount: Double => Double = (amount: Double) => {
  val discount = 20
  amount - discount
}

val applyTax: Double => Double = (amount: Double) => {
  val tax = 4 // fetch tax from database
  amount + tax
}

val f: Double = applyDiscount(applyTax(totalCost))
val g: Double => Double = applyDiscount andThen applyTax
(applyTax andThen applyDiscount)(totalCost)
(applyDiscount compose applyTax)(totalCost)

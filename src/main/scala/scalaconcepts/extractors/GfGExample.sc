object GfG {
  def apply(x: Double):Double = x / 5

  def unapply(z: Double): Option[Double] = {
    if (z % 5 == 0) Some(z / 5) else None
  }
}

val g: Double = GfG(391)

// Applying pattern matching
 g match {
//unapply method takes g
  // unapply method is called
  case GfG(y) => println("The value is: " + y)
  //y is value returned from unapply not g
  case _      => println("Can't be evaluated")

}

/**
 * case _ works only if you have None in unapply method,
 * if you don't have None in unapply method and you only
 * return Some then this case _ won't get executed,
 * also if we have None in unapply method and no case _ here
 * then we will get MatchError
 */


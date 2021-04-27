package cats

import cats.implicits._

object Semigroups {

  /** Simple example
   */
  val catsCombined1 = Semigroup.combine(Option(1), Option(2))
  val catsCombined2 = Option(1) combine Option(2)
  val catsCombined3 = Option(1) |+| Option(2)

  val scalaCombined1 = for (i <- Option(1); j <- Option(2)) yield i + j

  /** Complex example
   */
  val catsCombined4 =
    Map("foo" -> Map("bar" -> Option(3))) |+| Map("foo" -> Map("bar" -> Option(2))) |+| Map("kek" -> Map.empty)

}

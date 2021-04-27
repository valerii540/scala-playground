package cats

import cats.data._

object DataTypes extends App {

  /**
    Chain - like List but appending with O(1)
   **/
  val chain: Chain[Int] = Chain(1, 2, 4, 3)
  val nonEmptyChain     = NonEmptyChain(1)

}

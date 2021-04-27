package algorithms.math

import scala.annotation.tailrec

object Factorial extends App {

  def factorial(depth: Int): Int = {

    @tailrec
    def recursion(acc: Int, index: Int): Int =
      if (index >= depth)
        acc
      else {
        val i = index + 1
        recursion(acc * i, i)
      }

    recursion(1, 1)
  }
}

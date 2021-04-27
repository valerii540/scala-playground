package algorithms.math

import scala.annotation.tailrec

object Fibonacci extends App {

  def fibonacci(depth: Int): Int = {

    @tailrec
    def recursion(acc: Int)(d: Int, prev: Int): Int =
      if (d <= 0)
        acc
      else
        recursion(acc + prev)(d - 1, acc)


    recursion(0)(depth, 1)
  }
}

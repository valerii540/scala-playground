package std

import scala.collection.parallel.CollectionConverters._
import scala.util.Random

object ConcurrencyBench extends App {
  def withMeasure[T](f: => T)(implicit name: String = "[unnamed]"): T = {
    val start  = System.nanoTime()
    val result = f
    val finish = System.nanoTime()
    println(s"==> Execution time of $name: ${(finish - start) / 1000000}ms")
    result
  }

  val size = 100000000

  val array =
    withMeasure {
      IndexedSeq.fill(size)(Random.nextDouble() / 3.0)
    }("creation")

  println("=> Single thread")
  withMeasure {
    array.map(_ * 3.0)
  }("mapping")

  println("=> Multi-threaded")
  val parArray = array.par
  withMeasure {
    parArray.map(_ * 3.0)
  }("mapping")
}

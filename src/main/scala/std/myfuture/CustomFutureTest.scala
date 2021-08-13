package std.myfuture

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext
import scala.util.Success

object CustomFutureTest extends App {

  import scala.concurrent.ExecutionContext.Implicits.global
  val singleThreadPool = ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor())


  val myFuture1 = MyFuture {
    println(s"${Thread.currentThread()}: FUTURE 1: hello world!")
    2
  }

  myFuture1
    .onSuccess(num => println(s"${Thread.currentThread()}: callback 1, result: $num"))

  myFuture1
    .onSuccess(num => println(s"${Thread.currentThread()}: callback 2, result: $num"))

  myFuture1
    .onSuccess(num => println(s"${Thread.currentThread()}: callback 3, result: $num"))(ExecutionContext.parasitic)

  myFuture1
    .onSuccess(num => println(s"${Thread.currentThread()}: callback 4, result: $num"))(singleThreadPool)

  val result1 = myFuture1.await()
  println(result1)
  assert(result1 == Success(2))

  println("\n------------------- map/flatMap test -------------------")
  val myFuture2: MyFuture[Double] =
    for {
      intResult    <- MyFuture(1 + 1)
      _             = println(s"${Thread.currentThread()}: result from first future: $intResult")
      doubleResult <- MyFuture(intResult * 2.0)
      _             = println(s"${Thread.currentThread()}: result from third future: $doubleResult")
    } yield doubleResult

  val result2 = myFuture2.await()
  println(result2)
  assert(result2 == Success(4.0))
}

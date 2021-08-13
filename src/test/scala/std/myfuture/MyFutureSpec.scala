package std.myfuture

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext
import scala.util.Success

final class MyFutureSpec extends AnyWordSpecLike with Matchers {
  val fixedPool: ExecutionContext         = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(4))
  val singleThreadPool1: ExecutionContext = ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor())
  val singleThreadPool2: ExecutionContext = ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor())

  "MyFuture" should {
    "be executable" in {
      val f: MyFuture[Int] =
        MyFuture {
          1 + 1
        }(fixedPool)

      f.await() shouldBe Success(2)
    }

    "make callback" in {
      implicit val ec: ExecutionContext = fixedPool

      val f: MyFuture[Int] = MyFuture(1 + 1)

      var callbackResult = 0
      f.onSuccess(r => callbackResult = r * 2)

      Thread.sleep(1000)
      callbackResult shouldBe 4
    }

    "make callbacks" in {
      val f: MyFuture[Int] = MyFuture(1 + 1)(fixedPool)

      var callbackResult1 = 0
      f.onSuccess { r =>
        info(s"${Thread.currentThread()}: callback 1")
        callbackResult1 = r + 1
      }(fixedPool)

      var callbackResult2 = 0
      f.onSuccess { r =>
        info(s"${Thread.currentThread()}: callback 2")
        callbackResult2 = r + 2
      }(singleThreadPool1)

      var callbackResult3 = 0
      f.onSuccess { r =>
        info(s"${Thread.currentThread()}: callback 3")
        callbackResult3 = r + 3
      }(singleThreadPool2)

      Thread.sleep(1000)
      callbackResult1 shouldBe 3
      callbackResult2 shouldBe 4
      callbackResult3 shouldBe 5
    }

    "have working flatMap" in {
      implicit val ec: ExecutionContext = fixedPool

      val f =
        for {
          i      <- MyFuture(2 + 2)
          result <- MyFuture(i * 2)
        } yield result

      f.await() shouldBe Success(8)
    }

    "have working map" in {
      implicit val ec: ExecutionContext = fixedPool

      val f = MyFuture(2 + 2).map(_ * 2)

      f.await() shouldBe Success(8)
    }

    "handle both flatMap and map" in {
      implicit val ec: ExecutionContext = fixedPool

      val f: MyFuture[Double] =
        for {
          i      <- MyFuture(1 + 1)
          _      = info(s"${Thread.currentThread()}: result from first future: $i")
          result <- MyFuture(i * 2.0)
          _      = info(s"${Thread.currentThread()}: result from third future: $result")
        } yield result

      f.await() shouldBe Success(4.0)
    }
  }
}

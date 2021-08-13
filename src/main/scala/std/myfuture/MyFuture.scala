package std.myfuture

import scala.concurrent.ExecutionContext
import scala.util.Try

sealed trait MyFuture[T] {
  def map[B](v: T => B)(implicit ex: ExecutionContext): MyFuture[B]
  def flatMap[B](v: T => MyFuture[B])(implicit ec: ExecutionContext): MyFuture[B]
  def onSuccess[U](than: PartialFunction[T, U])(implicit ec: ExecutionContext): Unit
  def onError[U](recover: PartialFunction[Throwable, U]): Unit
  def value: Option[Try[T]]
  def await(): Try[T]
}

final class MyFutureImpl[T](promise: MyPromise[T]) extends MyFuture[T] {
  override def map[B](f: T => B)(implicit ec: ExecutionContext): MyFuture[B] = flatMap(t => MyFuture(f(t)))

  override def flatMap[B](f: T => MyFuture[B])(implicit ec: ExecutionContext): MyFuture[B] = {
    val newPromise = MyPromise[B]()
    promise.setOnSuccess(f(_).onSuccess(newPromise.success(_)))
    newPromise.future
  }

  override def onSuccess[U](than: PartialFunction[T, U])(implicit ec: ExecutionContext): Unit = promise.setOnSuccess(than)

  override def onError[U](recover: PartialFunction[Throwable, U]): Unit = promise.setOnFailure(recover)

  override def value: Option[Try[T]] = promise.get

  override def await(): Try[T] = {
    while (value.isEmpty) Thread.sleep(10)
    value.get
  }
}

object MyFuture {
  def apply[T](f: => T)(implicit ec: ExecutionContext): MyFuture[T] = {
    val promise = MyPromise[T]()
    ec.execute(() =>
      try {
        val result = f
        promise.success(result)
      } catch {
        case throwable: Throwable => promise.failure(throwable)
      }
    )
    new MyFutureImpl[T](promise)
  }
}

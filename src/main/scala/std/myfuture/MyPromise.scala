package std.myfuture

import java.util.concurrent.atomic.AtomicReference
import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

sealed trait MyPromise[T] {
  def success(value: T): Unit
  def failure(value: Throwable): Unit
  def setOnSuccess[U](than: T => U)(implicit ec: ExecutionContext): Unit
  def setOnFailure[U](recover: Throwable => U): Unit
  def get: Option[Try[T]]
  def future: MyFuture[T]
}

final class MyPromiseImpl[T] extends MyPromise[T] {
  private[this] var result: Option[Try[T]] = None
  private[this] val successCallbacks: AtomicReference[mutable.Queue[(ExecutionContext, T => Any)]] =
    new AtomicReference(mutable.Queue.empty)

  private[this] var failureCallback: Throwable => Any = error => throw error

  override def success(value: T): Unit = {
    result = Some(Success(value))
    executeSuccessCallbacks(value)
  }

  override def failure(th: Throwable): Unit = {
    result = Some(Failure(th))
    failureCallback(th)
  }

  override def get: Option[Try[T]] = result

  override def setOnSuccess[U](f: T => U)(implicit ec: ExecutionContext): Unit = {
    successCallbacks.updateAndGet(_ += ec -> f)
    if (result.isDefined) result.get.foreach(result => executeSuccessCallbacks(result))
  }

  override def setOnFailure[U](f: Throwable => U): Unit = failureCallback = f

  private[this] def executeSuccessCallbacks(result: T): Unit =
    while (successCallbacks.get().nonEmpty) {
      val (ec, f) = successCallbacks.get().dequeue()
      ec.execute(() => f(result))
    }

  override def future: MyFuture[T] = new MyFutureImpl[T](this)
}

object MyPromise {
  def apply[T](): MyPromiseImpl[T] = new MyPromiseImpl[T]
}

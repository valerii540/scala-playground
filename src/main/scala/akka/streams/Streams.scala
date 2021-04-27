package akka.streams

import akka.NotUsed
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.stream.ClosedShape
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source}

import scala.concurrent.Future

object Streams extends App {

  implicit val system = ActorSystem[Nothing](Behaviors.empty, "system")
  implicit val ec = system.executionContext

  val in    = Source(1 to 100)
  val seq = Sink.seq[Int]
  val ignore = Sink.ignore

  def result(mat: NotUsed, out: Future[Seq[Int]], done: Future[Any]) = out

  val r: Future[Seq[Int]] =
    RunnableGraph
      .fromGraph(GraphDSL.create(in, seq, ignore)(result) {
        implicit builder => (in, seqOut, ignore) =>
        import GraphDSL.Implicits._

        val bcast = builder.add(Broadcast[Int](2))
        val odds  = Flow[Int].filter(_ % 2 != 0)
        val evens = Flow[Int].filter(_ % 2 == 0)

        in ~> bcast ~> odds  ~> ignore
              bcast ~> evens ~> seqOut

        ClosedShape
      })
      .run()


  system.terminate()
}

package other

object CakePattern extends App {

  final class A(b: => B) { override def toString: String = s"A($b)"}
  final class B(a: => A) { override def toString: String = s"B($a)"}
  final class C(a: => A, b: => B) { override def toString: String = s"C($a, $b)"}

  trait Module1 {
    self: Module2 =>

    lazy val a: A = new A(b)
  }

  trait Module2 {
    self: Module1 =>

    lazy val b: B = new B(a)
  }

  trait Module3 {
    self: Module1 with Module2 =>

    lazy val c: C = new C(a, b)
  }

  trait MainModule extends Module1 with Module2 with Module3

  class Entry extends MainModule {
    println(a)
    println(b)
    println(c)
  }

//  new Entry
  class E {
    println("E created")
    def call(): Unit = println("E called")
  }

  class EE(e: => E) {
    println("EE created")
    e.call()
    e.call()
  }

  lazy val e = new E
  new EE(e)
}


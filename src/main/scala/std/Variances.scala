package std

object Invariant extends App {

  trait User

  case class FreeUser() extends User

  case class PaidUser() extends User

  case class Container[T](value: T)

//  val c1: Container[User] = Container[FreeUser](new FreeUser)

  val c2: Container[FreeUser] = Container[FreeUser](new FreeUser)
}

object Covariant extends App {
  List(1, 2, 3).length
}



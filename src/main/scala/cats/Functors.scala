package cats

object Functors {
  // ============== Functors =================
  val source = List("hello", "1", "3", "3d")

  /**  Mapping
    */
  val catsMapping = Functor[List].map(source)(_.length)

  val scalaMapping = source.map(_.length)

  /** Product
    */
  val catsProduct = Functor[List].fproduct(source)(_.length)

  val scalaProduct = source.map(e => e -> e.length)

  /** Composing
    */
  val listOpt      = Functor[List] compose Functor[Option]
  val catsComposed = listOpt.map(List(Some(1), None, Some(2)))(_ * Math.PI)

  val scalaComposed = List(Some(1), None, Some(2)).map(o => o.map(_ * Math.PI))
}

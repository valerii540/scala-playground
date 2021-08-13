package std.performance.collections

import org.scalameter.KeyValue
import org.scalameter.api._

import scala.collection.immutable.{ListSet, TreeSet}

class ListLengthPerformanceTest extends Bench.LocalTime {
  override lazy val measurer = new Measurer.IgnoringGC

  val sizes: Gen[Int] = Gen.exponential("size")(100, 100_000_000, 10)
  val arrays: Gen[List[Int]] = for (sz <- sizes) yield (0 until sz).toList

  performance of "List" in {
    measure method "length" in {
      using(arrays)
        .config(KeyValue(exec.independentSamples -> 1), KeyValue(exec.benchRuns -> 10))
        .in { xs =>
          xs.length
        }
    }
  }
}

class ArrayLengthPerformanceTest extends Bench.LocalTime {
  override lazy val measurer = new Measurer.IgnoringGC

  val sizes: Gen[Int] = Gen.exponential("size")(100, 100_000_000, 10)
  val arrays: Gen[Array[Int]] = for (sz <- sizes) yield (0 until sz).toArray

  performance of "Array" in {
    measure method "length" in {
      using(arrays)
        .config(KeyValue(exec.independentSamples -> 1), KeyValue(exec.benchRuns -> 10))
        .in { xs =>
          xs.length
        }
    }
  }
}

class IndexedSeqLengthPerformanceTest extends Bench.LocalTime {
  override lazy val measurer = new Measurer.IgnoringGC

  val sizes: Gen[Int] = Gen.exponential("size")(100, 100_000_000, 10)
  val arrays: Gen[IndexedSeq[Int]] = for (sz <- sizes) yield 0 until sz

  performance of "IndexedSeq" in {
    measure method "length" in {
      using(arrays)
        .config(KeyValue(exec.independentSamples -> 1), KeyValue(exec.benchRuns -> 10))
        .in { xs =>
          xs.length
        }
    }
  }
}

class SetLengthPerformanceTest extends Bench.LocalTime {
  override lazy val measurer = new Measurer.IgnoringGC

  val sizes: Gen[Int] = Gen.exponential("size")(100, 100_000_000, 10)
  val arrays: Gen[Set[Int]] = for (sz <- sizes) yield (0 until sz).toSet

  performance of "Set" in {
    measure method "length" in {
      using(arrays)
        .config(KeyValue(exec.independentSamples -> 1), KeyValue(exec.benchRuns -> 10))
        .in { xs =>
          xs.size
        }
    }
  }
}

class ListSetLengthPerformanceTest extends Bench.LocalTime {
  override lazy val measurer = new Measurer.IgnoringGC

  val sizes: Gen[Int] = Gen.exponential("size")(100, 100_000, 10)
  val arrays: Gen[ListSet[Int]] = for (sz <- sizes) yield (0 until sz).to(ListSet)

  performance of "ListSet" in {
    measure method "length" in {
      using(arrays)
        .config(KeyValue(exec.independentSamples -> 1), KeyValue(exec.benchRuns -> 10))
        .in { xs =>
          xs.size
        }
    }
  }
}

class TreeSetLengthPerformanceTest extends Bench.LocalTime {
  override lazy val measurer = new Measurer.IgnoringGC

  val sizes: Gen[Int] = Gen.exponential("size")(100, 100_000_000, 10)
  val arrays: Gen[TreeSet[Int]] = for (sz <- sizes) yield (0 until sz).to(TreeSet)

  performance of "TreeSet" in {
    measure method "length" in {
      using(arrays)
        .config(KeyValue(exec.independentSamples -> 1), KeyValue(exec.benchRuns -> 10))
        .in { xs =>
          xs.size
        }
    }
  }
}

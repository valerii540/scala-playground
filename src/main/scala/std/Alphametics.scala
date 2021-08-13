package std

import java.util.UUID
import scala.collection.mutable
import scala.util.Random

object Alphametics extends App {
  //  Random.setSeed(1)

  private val fixedTests = Array(
    Array("SEND + MORE = MONEY", "9567 + 1085 = 10652"),
    Array("ZEROES + ONES = BINARY", "698392 + 3192 = 701584"),
    Array("COUPLE + COUPLE = QUARTET", "653924 + 653924 = 1307848"),
    Array("DO + YOU + FEEL = LUCKY", "57 + 870 + 9441 = 10368"),
    Array("ELEVEN + NINE + FIVE + FIVE = THIRTY", "797275 + 5057 + 4027 + 4027 = 810386")
  )

  class Alphametics(s: String) {
    case class Individual(name: String, chromosomes: Map[Char, Int]) {
      val fitness: Int = {
        val numerical       = tokens.map(_.map(chromosomes(_)).mkString).map(_.toInt)
        val (summands, sum) = (numerical.init, numerical.last)

        math.abs(summands.sum - sum)
      }

      def mutate(newGenes: Seq[(Char, Int)]): Individual = Individual(name, chromosomes ++ newGenes)

      override def toString: String = s"($name, ${chromosomes.values.mkString("|")}, $fitness)"
    }

    private val numbers         = (0 to 9).toSet
    private val tokens          = "(\\w+)".r.findAllIn(s).toIndexedSeq
    private val genes           = tokens.flatten.toSet
    private val leadingGenes    = tokens.map(_.head).toSet
    private val nonLeadingGenes = genes -- leadingGenes

    def solve(): String = {
      var pop = initialPopulation(15)

      var counter = 0
      while (!checkConditions(pop)) {
        if (pop.isEmpty)
          throw new Exception(s"Population extincted")

//        if (pop.forall(_.fitness == pop.head.fitness))
//          throw new Exception(s"Population stagnation begins. Fitness - ${pop.head.fitness}")

        println(
          s"Generation $counter, " +
            s"${pop.size} individuals, " +
            s"${pop.foldLeft(0)((acc, i) => acc + i.fitness) / pop.size} average fitness, ${pop.minBy(_.fitness).fitness} minimal fitness"
          //            s"\n" + pop.mkString("\n")
        )

        val selected = tournamentSelection(pop, 3)
        val newPop   = intelligentCrossOver(selected, counter, genes.size / 2)

        pop = intelligentMutation(newPop)

        counter += 1
        Thread.sleep(300)
      }
      //      println("Initial population:\n" + initial.mkString("\n"))
      //      println("\nSelected population:\n" + selected.mkString("\n"))

      ""
    }

    def initialPopulation(n: Int): IndexedSeq[Individual] =
      for (i <- 0 until n) yield Individual(i.toString, randomChromosome())

    def tournamentSelection(pop: IndexedSeq[Individual], k: Int): IndexedSeq[Individual] =
      (for (_ <- pop.indices) yield {
        (for (_ <- 0 until k) yield pop(Random.nextInt(pop.length))).minBy(_.fitness)
      }).distinctBy(_.name)
        .map { ind =>
          if (ind.chromosomes.values.size != ind.chromosomes.values.toSet.size) throw new Exception(s"Bad individual found: $ind")
          ind
        }

    def crossOver(pop: IndexedSeq[Individual], generation: Int): IndexedSeq[Individual] = {
      val parents: IndexedSeq[(Individual, Individual)] =
        pop.combinations(2).take(pop.size).toIndexedSeq.map(s => (s(0), s(1)))

      parents
        .map { case (p1, p2) =>
          val chosenGenes = {
            val leftBoundary = Random.nextInt(genes.size - 1)
            genes.slice(leftBoundary, Random.between(leftBoundary, genes.size))
          }

          val p1c = p1.chromosomes.to(mutable.Map)
          val p2c = p2.chromosomes.to(mutable.Map)
          chosenGenes.foreach { c =>
            val temp = p1c(c)
            p1c(c) = p2c(c)
            p2c(c) = temp
          }
          (
            Individual(s"F$generation ${UUID.randomUUID()}", p1c.toMap),
            Individual(s"S$generation ${UUID.randomUUID()}", p2c.toMap)
          )
        }
        .foldLeft(Seq.empty[Individual])((acc, offspring) => acc ++ Seq(offspring._1, offspring._2))
        .toIndexedSeq
    }

    def intelligentCrossOver(pop: IndexedSeq[Individual], generation: Int, genesN: Int): IndexedSeq[Individual] = {
      val parents: IndexedSeq[(Individual, Individual)] =
        pop.sortBy(_.fitness).combinations(2).take(pop.size).toIndexedSeq.map(s => (s(0), s(1)))

      parents
        .map { case (p1, p2) =>
          val chosenGenes = Random.shuffle(p1.chromosomes.keys.toSeq).take(genesN)

          val p1c = p1.chromosomes
          val p2c = p2.chromosomes

          val p1update = chosenGenes.map(c => c -> p2c(c)).toMap
          val p2update = chosenGenes.map(c => c -> p1c(c)).toMap

          val p1conflict = p1c.collect { case (c, i) if p1update.exists(_._2 == i) && !chosenGenes.contains(c) => c }
          val p2conflict = p2c.collect { case (c, i) if p2update.exists(_._2 == i) && !chosenGenes.contains(c) => c }

          val completeChromosome1 = {
            val badChromosome = p1c ++ p1update
            val pool          = (numbers -- badChromosome.values).to(mutable.Stack)

            val patch = p1conflict.map { gene =>
              gene -> (if (leadingGenes.contains(gene)) {
                val v = pool.collectFirst { case n if n != 0 => n }
                pool -= v.get
                v.get
              } else pool.pop())
            }.toMap

            badChromosome ++ patch
          }

          val completeChromosome2 = {
            val badChromosome = p2c ++ p2update
            val pool          = (numbers -- badChromosome.values).to(mutable.Stack)

            val patch = p2conflict.map { gene =>
              gene -> (if (leadingGenes.contains(gene)) {
                val v = pool.collectFirst { case n if n != 0 => n }
                pool -= v.get
                v.get
              } else pool.pop())
            }.toMap

            badChromosome ++ patch
          }

          (
            Individual(s"F$generation ${UUID.randomUUID()}", completeChromosome1),
            Individual(s"S$generation ${UUID.randomUUID()}", completeChromosome2)
          )
        }
        .foldLeft(Seq.empty[Individual])((acc, offspring) => acc ++ Seq(offspring._1, offspring._2))
        .toIndexedSeq
    }

    def mutation(pop: IndexedSeq[Individual], prob: Int = 25): IndexedSeq[Individual] =
      pop.map { ind =>
        val mGenes = mutable.ArrayBuffer.empty[(Char, Int)]
        ind.chromosomes.keys.foreach { g =>
          if (Random.nextInt(100) < prob) {
            val newVal =
              if (leadingGenes(g)) (numbers - 0).toIndexedSeq(Random.nextInt(numbers.size - 1))
              else numbers.toIndexedSeq(Random.nextInt(10))
            mGenes += (g -> newVal)
          }
        }
        ind mutate mGenes.toSeq
      }

    def intelligentMutation(pop: IndexedSeq[Individual], prob: Int = 10): IndexedSeq[Individual] = {
      pop.map { ind =>
        val m =
          if (Random.nextInt(100) < prob) {
            val chosenGene    = genes.toIndexedSeq(Random.nextInt(genes.size))
            val geneWithValue = chosenGene -> (if (leadingGenes(chosenGene)) Random.between(1, 10) else Random.nextInt(10))
            val conflict      = ind.chromosomes.find(_._2 == geneWithValue._2)
            if (conflict.isDefined) {
              Seq(
                conflict.get._1 -> (if (leadingGenes(conflict.get._1)) (numbers -- ind.chromosomes.values - 0).head
                else (numbers -- ind.chromosomes.values).head)
              )
            } else Nil
          } else Nil

        ind mutate m
      }
    }

    def checkConditions(pop: IndexedSeq[Individual]): Boolean = {
      val winner = pop.find(_.fitness == 0)
      winner.foreach { w =>
        val numerical       = tokens.map(_.map(w.chromosomes(_)).mkString).map(_.toInt)
        val (summands, sum) = (numerical.init, numerical.last)
        println(s"Winner found: ${w.name}, ${w.chromosomes}, ${w.fitness}")
        println(tokens.init.mkString(" + ") + " = " + tokens.last)
        println(summands.mkString(" + ") + " = " + sum)
        assert(summands.sum == sum, "Wrong expression")
      }
      winner.isDefined
    }

    def randomChromosome(): Map[Char, Int] = {
      val others     = nonLeadingGenes.zip(Random.shuffle(numbers.toSeq))
      val othersVals = others.map(_._2)

      val leading = leadingGenes.zip(Random.shuffle(numbers -- othersVals))
      (leading ++ others).toMap
    }
  }

  new Alphametics(fixedTests(1)(0)).solve()
}

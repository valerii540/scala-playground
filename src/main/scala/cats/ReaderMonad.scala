package cats

import cats.data.Reader

object ReaderMonad extends App {
  trait Repo {
    def createUser: Long
  }

  def createUser(name: String, age: Long): Reader[Repo, Long] =
    Reader { repo =>
      repo.createUser
    }


  val repo = new Repo {
    override def createUser: Long = 42
  }

  createUser("tom", 23).run(repo)
}

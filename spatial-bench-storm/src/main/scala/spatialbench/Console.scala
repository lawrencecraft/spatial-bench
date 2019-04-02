package spatialbench

import cats.effect.IO

object Console {

  def output(s: String): IO[Unit] = IO(println(s))
}

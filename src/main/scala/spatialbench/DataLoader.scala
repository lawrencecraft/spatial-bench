package spatialbench

import java.io.File
import java.util.concurrent.Executors

import cats.effect._
import cats.implicits._
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import scala.concurrent.ExecutionContext
import scala.language.higherKinds


object DataLoader {
  private[this] val singleExecution = Resource.make(IO(Executors.newFixedThreadPool(2)))(ec => IO(ec.shutdown()))

  import Console._

  val recordsPerSecond = 1000

  def constructKafkaBroker(options: LoaderOptions): IO[KafkaProducer[String, String]] = IO {
    new KafkaProducer[String, String](Kafka.properties(options))
  }

  def listPathsFromDirectory(path: String): IO[List[File]] =
    IO(new File(path)).flatMap {
      p =>
        println(s"$p")
        IO(p.listFiles).map(_.filter(_.isFile).toList)
    }

  def lines(path: File): IO[Iterator[String]] = IO(scala.io.Source.fromFile(path).getLines)

  def openAllPaths(path: String): IO[Iterator[String]] =
    for {
      files <- listPathsFromDirectory(path)
      allLines <- files.traverse(lines)
    } yield allLines.reduceLeftOption(_ ++ _).getOrElse(Nil.toIterator)


  def loadAll(options: LoaderOptions, lines: Iterator[String], producer: KafkaProducer[String, String]): IO[Unit] = IO {
    var count = 0
    var start = System.currentTimeMillis()
    var nextEnd = start + 1000
    lines.map(addTimestamp).foreach { line =>
      count += 1
      if (count > recordsPerSecond) {
        val sleepDuration = nextEnd - System.currentTimeMillis()
        println(s"Sleeping for $sleepDuration")
        Thread.sleep(sleepDuration)
        count = 0
        start = System.currentTimeMillis()
        nextEnd = start + 1000
      }
      producer.send(new ProducerRecord(options.writeTopic, line))

    }
  }


  def executeOnContext[T](f: IO[T])(implicit cs: ContextShift[IO]): IO[T] =
    singleExecution.use {
      ex =>
        val ec = ExecutionContext.fromExecutor(ex)
        cs.evalOn(ec)(f)
    }

  def addTimestamp(s: String): String = s + "," + System.currentTimeMillis()

  def loadFromFile(options: LoaderOptions)(implicit cs: ContextShift[IO]): IO[Unit] =
    for {
      producer <- constructKafkaBroker(options)
      lines <- openAllPaths(options.sourcePath)
      _ <- output("Beginning to load data")
      _ <- executeOnContext(loadAll(options, lines, producer))
      _ <- output("Benchmark data loaded!")
    } yield ()

}

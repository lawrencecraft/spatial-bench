package spatialbench

import java.io.{BufferedWriter, File, FileWriter, OutputStream}
import java.util.Collections

import cats.effect._
import org.apache.kafka.clients.consumer.{ConsumerRecord, ConsumerRecords, KafkaConsumer}

import scala.concurrent.duration._
import scala.collection.JavaConverters._
import scala.language.postfixOps

object DataReaderWriter {

  import Console._

  def openKafkaConnection(options: LoaderOptions): IO[KafkaConsumer[String, String]] = IO {
    val settings = Kafka.consumerProperties(options)
    val kafka = new KafkaConsumer[String, String](settings)
    kafka.subscribe(Collections.singleton(options.readTopic))
    kafka
  }

  def openFile(options: LoaderOptions): Resource[IO, BufferedWriter] =
    Resource.make(IO {
      val file = new File(options.destFile)
      new BufferedWriter(new FileWriter(file))
    })(b => IO(b.close()))

  def poll(consumer: KafkaConsumer[String, String]): IO[ConsumerRecords[String, String]] = {
    IO(consumer.poll(java.time.Duration.ofMillis(2)))
  }

  //  def now: IO[Long] = IO(System.currentTimeMillis())

  def write(options: LoaderOptions, writer: BufferedWriter, records: Iterable[ConsumerRecord[String, String]]): IO[(Long, Option[Long])] =
    IO {
      var produced = 0
      var time: Option[Long] = None
      records.foreach {
        r =>
          val now = System.currentTimeMillis()
          val row = r.value()
          val producedTime = row.split(",").last.toLong
          val latency = r.timestamp() - producedTime
          writer.write(latency.toString + "\n")
          time = Some(now)
          produced += 1
      }
      (produced, time)
    }

  def writeLoop(options: LoaderOptions, writer: BufferedWriter, consumer: KafkaConsumer[String, String], timeout: Duration, lastSeen: Long)(implicit t: Timer[IO]): IO[Unit] =
    for {
      records <- poll(consumer).map(_.records(options.readTopic)).map(_.asScala.toList)
      ts <- write(options, writer, records)
      _ <- ts match {
        case (count, Some(time)) => writeLoop(options, writer, consumer, timeout, time)
        case (_, None) if (System.currentTimeMillis() - lastSeen) > timeout.toMillis => output("Done")
        case (_, None) => writeLoop(options, writer, consumer, timeout, lastSeen)
      }
    } yield ()

  def doWrite(options: LoaderOptions, writer: BufferedWriter, consumer: KafkaConsumer[String, String], timeout: Duration)(implicit t: Timer[IO]): IO[Unit] = {
    writeLoop(options, writer, consumer, timeout, System.currentTimeMillis())
  }

  def writeOutLatencies(options: LoaderOptions)(implicit cs: ContextShift[IO], t: Timer[IO]): IO[Fiber[IO, Unit]] =
    for {
      _ <- output("Beginning read")
      consumer <- openKafkaConnection(options)
      fib <- openFile(options).use(doWrite(options, _, consumer, 1 minutes)).start
    } yield fib
}

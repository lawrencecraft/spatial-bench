package spatialbench

import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp {
  val options = LoaderOptions(
    sourcePath = "/tmp/spatial-bench3",
    destFile = "test.csv",
    kafkaBroker = "kafka-zk:9092",
    writeTopic = "input_data",
    readTopic = "output_data"
  )

  override def run(args: List[String]): IO[ExitCode] =
    for {
      receivingFiber <- DataReaderWriter.writeOutLatencies(options)
      dataLoadingFiber <- DataLoader.loadFromFile(options).start
      _ <- dataLoadingFiber.join
      _ <- receivingFiber.join
    } yield ExitCode.Success
}

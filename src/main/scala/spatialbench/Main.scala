package spatialbench

import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp {
  val options = LoaderOptions(
    sourcePath = "/root/spatial-bench3",
    destFile = "test.csv",
    kafkaBroker = "kafkab1:9092",
    writeTopic = "spatial-bench-2",
    readTopic = "spatial-bench-output-2"
  )

  override def run(args: List[String]): IO[ExitCode] =
    for {
      receivingFiber <- DataReaderWriter.writeOutLatencies(options)
      dataLoadingFiber <- DataLoader.loadFromFile(options).start
      _ <- dataLoadingFiber.join
      _ <- receivingFiber.join
    } yield ExitCode.Success
}

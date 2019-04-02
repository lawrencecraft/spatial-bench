package spatialbench

case class LoaderOptions(sourcePath: String, destFile: String, kafkaBroker: String, writeTopic: String, readTopic: String)

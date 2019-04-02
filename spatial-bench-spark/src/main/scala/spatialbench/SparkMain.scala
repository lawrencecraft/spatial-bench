package spatialbench

import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.{OutputMode, Trigger}
import org.apache.spark.sql.{SQLTypes, SparkSession}
import org.locationtech.geomesa.spark.jts._


object SparkMain {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .appName("spatial-bench")
      .getOrCreate()

    SQLTypes.init(spark.sqlContext)

    val df = spark
      .read
      .option("header", "true")
      .csv("/tmp/nb.csv")
      .select("PRI_NEIGH", "the_geom")
    println("Done!")
    println(df.count())
    println(df.schema)

    df.show()
    df.createOrReplaceTempView("neighborhoods")

    val loccedUp = spark.sql("select PRI_NEIGH, st_geomFromWKT(the_geom) as poly from neighborhoods").cache()

    loccedUp.show()

    spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "kafka-zk:9092")
      .option("subscribe", "input_data")
      .load()
      .selectExpr("cast(split(value, ',')[7] as double) as lat", "cast(split(value, ',')[8] as double) as lon", "split(value, ',')[9] as timestamp")
      .withColumn("point_loc", st_point(col("lon"), col("lat")))
      .join(broadcast(loccedUp), st_intersects(col("point_loc"), col("poly")))
      .withColumnRenamed("PRI_NEIGH", "neighborhood")
      .selectExpr("CONCAT(neighborhood, ',', timestamp) as value")
      .writeStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "kafka-zk:9092")
      .option("topic", "output_data")
      .option("checkpointLocation", "/tmp/chkpnt")
      .outputMode(OutputMode.Append())
      .trigger(Trigger.ProcessingTime("1 second"))
      .start()
      .awaitTermination()
  }
}

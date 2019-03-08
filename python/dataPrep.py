import argparse
import pyspark
import os
from pyspark.sql import SparkSession

spark = SparkSession.builder.master("local[*]").appName("dprep").getOrCreate()

ROOT = "/home/lawrencecraft/Downloads/chicago/chicago-complete.daily.2019-03-04/"

nodesDf = spark.read.option("header", "true").csv(ROOT + "nodes.csv")
dataDf = spark.read.option("header", "true").csv(ROOT + "data.csv")

joined_df = dataDf.join(nodesDf, dataDf.node_id == nodesDf.node_id).select([dataDf[c] for c in dataDf.columns] + [nodesDf["lat"], nodesDf["lon"]])

joined_df.write.option("header", "true").csv("/home/lawrencecraft/spatial-bench3")
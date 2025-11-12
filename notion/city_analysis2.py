from datetime import datetime

from pyspark.sql import SparkSession
from pyspark.sql.dataframe import DataFrame


spark = SparkSession.Builder().getOrCreate()

# Define schema

city_schema = Str


def load_raw_data():
	raw_data_df = spark.read.json("path/to/file.json")
	raw_data_df.show()
	return raw_data_df
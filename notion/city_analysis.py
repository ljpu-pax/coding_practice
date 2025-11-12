import json
from pyspark.sql import SparkSession
from pyspark.sql.types import StructType, StructField, StringType, IntegerType, DoubleType, ArrayType, TimestampType
from pyspark.sql.functions import col, year

# Initialize Spark
spark = SparkSession.builder.appName("CityDataAnalysis").getOrCreate()

# Define schema
city_schema = StructType([
    StructField("name", StringType(), True),
    StructField("country", StringType(), True),
    StructField("population", IntegerType(), True),
    StructField("area", IntegerType(), True),
    StructField("elevation", IntegerType(), True),
    StructField("timezone", StringType(), True),
    StructField("gps_coordinates", ArrayType(DoubleType()), True),
    StructField("average_temperature", DoubleType(), True),
    StructField("transit_trips_per_capita", IntegerType(), True),
    StructField("car_ownership_rate", DoubleType(), True),
    StructField("founded", TimestampType(), True),
])

# Load data
df = spark.read.schema(city_schema).json("sample_data.json")

# Show sample
print("== Sample Data ==")
df.show(5, truncate=False)

# Add population density
df = df.withColumn("population_density", col("population") / col("area"))

# Filter southern hemisphere cities
southern_cities = df.filter(col("gps_coordinates")[0] < 0)
print("\n== Southern Hemisphere Cities ==")
southern_cities.select("name", "gps_coordinates").show()

# Filter cities with population > 10M and founded before 1500
old_large_cities = df.filter((col("population") > 10000000) & (year("founded") < 1500))
print("\n== Large & Old Cities ==")
old_large_cities.select("name", "population", "founded").show()

# Average car ownership by country
print("\n== Average Car Ownership Rate by Country ==")
df.groupBy("country").avg("car_ownership_rate").orderBy("avg(car_ownership_rate)", ascending=False).show()

# Newest city
newest = df.orderBy(col("founded").desc()).select("name", "founded").first()
print(f"\n== Newest Founded City ==\n{newest['name']} founded on {newest['founded']}")

# Write to parquet
output_path = "output/cities_by_country"
df.write.mode("overwrite").partitionBy("country").parquet(output_path)
print(f"\n== Data written to Parquet at {output_path} ==")

# Stop Spark
spark.stop()

## 1. 概述

[Apache Spark](https://www.baeldung.com/apache-spark)是一个快速的分布式数据处理系统。它执行内存中数据处理并使用内存中缓存和优化执行，从而实现快速性能。它为 Scala、Python、Java 和 R 等流行编程语言提供高级 API。

在本快速教程中，我们将介绍 Spark 的三个基本概念：数据帧、数据集和 RDD。

## 2.数据框

自 Spark 1.3 以来，Spark SQL 引入了一种称为 DataFrame 的表格数据抽象。从那时起，它就成为了 Spark 中最重要的特性之一。当我们想要处理结构化和半结构化的分布式数据时，这个 API 很有用。

在第 3 节中，我们将讨论弹性分布式数据集 (RDD)。 DataFrames 以比 RDD 更有效的方式存储数据，这是因为它们使用 RDD 的不可变、内存中、弹性、分布式和并行功能，但它们也将模式应用于数据。DataFrames 还将 SQL 代码转换为优化的低级 RDD 操作。

我们可以通过三种方式创建 DataFrame：

-   转换现有的 RDD
-   运行 SQL 查询
-   加载外部数据

Spark 团队在 2.0 版本中引入了SparkSession，它统一了所有不同的上下文，确保开发人员无需担心创建不同的上下文：

```java
SparkSession session = SparkSession.builder()
  .appName("TouristDataFrameExample")
  .master("local[]")
  .getOrCreate();

DataFrameReader dataFrameReader = session.read();
```

我们将分析Tourist.csv文件：

```java
Dataset<Row> data = dataFrameReader.option("header", "true")
  .csv("data/Tourist.csv");
```

由于 Spark 2.0 DataFrame 成为Row类型的数据集，因此我们可以使用 DataFrame 作为Dataset<Row>的别名。

我们可以选择我们感兴趣的特定列。我们还可以按给定列进行过滤和分组：

```java
data.select(col("country"), col("year"), col("value"))
  .show();

data.filter(col("country").equalTo("Mexico"))
  .show();

data.groupBy(col("country"))
  .count()
  .show();
```

## 3.数据集

数据集是一组强类型的结构化数据。它们提供了熟悉的面向对象的编程风格以及类型安全的好处，因为数据集可以在编译时检查语法并捕获错误。

Dataset是 DataFrame 的扩展，因此我们可以将 DataFrame 视为数据集的无类型视图。

Spark 团队在 Spark 1.6 中发布了Dataset API，正如他们所说：“Spark Datasets 的目标是提供一个 API，使用户可以轻松地表达对象域上的转换，同时还提供 Spark SQL 执行的性能和健壮性优势引擎”。

首先，我们需要创建一个TouristData类型的类：

```java
public class TouristData {
    private String region;
    private String country;
    private String year;
    private String series;
    private Double value;
    private String footnotes;
    private String source;
    // ... getters and setters
}
```

要将我们的每个记录映射到指定的类型，我们需要使用编码器。编码器在Java对象和 Spark 的内部二进制格式之间进行转换：

```java
// SparkSession initialization and data load
Dataset<Row> responseWithSelectedColumns = data.select(col("region"), 
  col("country"), col("year"), col("series"), col("value").cast("double"), 
  col("footnotes"), col("source"));

Dataset<TouristData> typedDataset = responseWithSelectedColumns
  .as(Encoders.bean(TouristData.class));
```

与 DataFrame 一样，我们可以按特定列进行过滤和分组：

```java
typedDataset.filter((FilterFunction) record -> record.getCountry()
  .equals("Norway"))
  .show();

typedDataset.groupBy(typedDataset.col("country"))
  .count()
  .show();
```

我们还可以通过匹配特定范围的列进行过滤或计算特定列的总和等操作，以获得它的总值：

```java
typedDataset.filter((FilterFunction) record -> record.getYear() != null 
  && (Long.valueOf(record.getYear()) > 2010 
  && Long.valueOf(record.getYear()) < 2017)).show();

typedDataset.filter((FilterFunction) record -> record.getValue() != null 
  && record.getSeries()
    .contains("expenditure"))
    .groupBy("country")
    .agg(sum("value"))
    .show();
```

## 4. RDDs

弹性分布式数据集或 RDD 是 Spark 的主要编程抽象。它代表了一组元素：不可变的、有弹性的和分布式的。

RDD 封装了一个大型数据集，Spark 会自动将 RDD 中包含的数据分布到我们的集群中，并并行化我们对它们执行的操作。

我们只能通过对稳定存储中数据的操作或对其他RDD的操作来创建RDD。

当我们处理大量数据并且数据分布在集群机器上时，容错是必不可少的。由于 Spark 的内置故障恢复机制，RDD 具有弹性。Spark 依赖于 RDD 记住它们是如何创建的这一事实，以便我们可以轻松地追溯沿袭以恢复分区。

我们可以对 RDD 执行两种类型的操作：转换和操作。

### 4.1. 转换

我们可以将转换应用于 RDD 以操作其数据。执行此操作后，我们将获得一个全新的 RDD，因为 RDD 是不可变对象。

我们将检查如何实现 Map 和 Filter 这两个最常见的转换。

首先，我们需要创建一个JavaSparkContext并将数据作为 RDD 从Tourist.csv文件加载：

```java
SparkConf conf = new SparkConf().setAppName("uppercaseCountries")
  .setMaster("local[]");
JavaSparkContext sc = new JavaSparkContext(conf);

JavaRDD<String> tourists = sc.textFile("data/Tourist.csv");
```

接下来，让我们应用 map 函数从每条记录中获取国家名称并将名称转换为大写。我们可以将这个新生成的数据集保存为磁盘上的文本文件：

```java
JavaRDD<String> upperCaseCountries = tourists.map(line -> {
    String[] columns = line.split(COMMA_DELIMITER);
    return columns[1].toUpperCase();
}).distinct();

upperCaseCountries.saveAsTextFile("data/output/uppercase.txt");
```

如果我们只想选择一个特定的国家，我们可以在我们原来的游客 RDD 上应用过滤器功能：

```java
JavaRDD<String> touristsInMexico = tourists
  .filter(line -> line.split(COMMA_DELIMITER)[1].equals("Mexico"));

touristsInMexico.saveAsTextFile("data/output/touristInMexico.txt");
```

### 4.2. 动作

在对数据进行一些计算后，操作将返回最终值或将结果保存到磁盘。

Spark 中经常使用的两个操作是 Count 和 Reduce。

让我们计算 CSV 文件中的国家总数：

```java
// Spark Context initialization and data load
JavaRDD<String> countries = tourists.map(line -> {
    String[] columns = line.split(COMMA_DELIMITER);
    return columns[1];
}).distinct();

Long numberOfCountries = countries.count();
```

现在，我们将按国家/地区计算总支出。我们需要过滤描述中包含支出的记录。

我们将不使用 JavaRDD，而是使用JavaPairRDD。一对RDD是一种可以存储键值对的RDD。接下来我们检查一下：

```java
JavaRDD<String> touristsExpenditure = tourists
  .filter(line -> line.split(COMMA_DELIMITER)[3].contains("expenditure"));

JavaPairRDD<String, Double> expenditurePairRdd = touristsExpenditure
  .mapToPair(line -> {
      String[] columns = line.split(COMMA_DELIMITER);
      return new Tuple2<>(columns[1], Double.valueOf(columns[6]));
});

List<Tuple2<String, Double>> totalByCountry = expenditurePairRdd
  .reduceByKey((x, y) -> x + y)
  .collect();
```

## 5.总结

综上所述，当我们需要特定领域的 API 时，我们应该使用 DataFrames 或 Datasets，我们需要聚合、求和或 SQL 查询等高级表达式。或者当我们在编译时想要类型安全时。

另一方面，当数据是非结构化的并且我们不需要实现特定模式或者当我们需要低级转换和操作时，我们应该使用 RDD。
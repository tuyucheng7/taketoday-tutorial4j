## 1. 概述

[Apache Spark](https://www.baeldung.com/apache-spark)是一种开源分布式分析和处理系统，可实现大规模数据工程和数据科学。它通过为数据传输、大规模转换和分发提供统一的 API，简化了面向分析的应用程序的开发。

DataFrame是 Spark API 的重要组成部分。在本教程中，我们将使用一个简单的客户数据示例来研究一些 Spark DataFrame API。

## 2. Spark中的DataFrame

从逻辑上讲，DataFrame是一组组织成命名列的不可变记录。它与 RDBMS 中的表或Java中的结果集有相似之处。

作为 API，DataFrame提供对多个 Spark 库的统一访问，包括[Spark SQL、Spark Streaming、MLib 和 GraphX](https://www.baeldung.com/apache-spark)。

在Java中，我们使用Dataset<Row>来表示一个DataFrame。

本质上，Row使用称为Tungsten的高效存储，与其[前身](https://www.baeldung.com/java-spark-dataframe-dataset-rdd)相比，它高度优化了 Spark 操作。

## 3.Maven依赖

让我们首先将[spark-core](https://search.maven.org/artifact/org.apache.spark/spark-core_2.11)和[spark-sql](https://search.maven.org/artifact/org.apache.spark/spark-sql_2.11)依赖项添加到我们的pom.xml中：

```java
<dependency>
    <groupId>org.apache.spark</groupId>
    <artifactId>spark-core_2.11</artifactId>
    <version>2.4.8</version>
</dependency>

<dependency>
    <groupId>org.apache.spark</groupId>
    <artifactId>spark-sql_2.11</artifactId>
    <version>2.4.8</version>
</dependency>

```

## 4. DataFrame和 Schema

本质上，DataFrame是具有模式的[RDD](https://www.baeldung.com/scala/apache-spark-rdd)。模式可以被推断或定义为StructType。

StructType是 Spark SQL 中内置的数据类型，我们用它来表示StructField对象的集合。

让我们定义一个示例Customer模式StructType：

```java
public static StructType minimumCustomerDataSchema() {
    return DataTypes.createStructType(new StructField[] {
      DataTypes.createStructField("id", DataTypes.StringType, true),
      DataTypes.createStructField("name", DataTypes.StringType, true),
      DataTypes.createStructField("gender", DataTypes.StringType, true),
      DataTypes.createStructField("transaction_amount", DataTypes.IntegerType, true) }
    );
}
```

在这里，每个StructField都有一个名称，代表DataFrame列名称、类型和布尔值，代表它是否可以为空。

## 5. 构建数据框

每个Spark应用的第一个操作就是通过master获取一个SparkSession 。

它为我们提供了一个访问DataFrames的入口点。让我们从创建SparkSession开始：

```java
public static SparkSession getSparkSession() {
    return SparkSession.builder()
      .appName("Customer Aggregation pipeline")
      .master("local")
      .getOrCreate();
}
```

请注意，我们使用本地主机连接到 Spark。如果我们要连接到集群，我们将改为提供集群地址。

一旦我们有了SparkSession ，我们就可以使用各种方法创建一个DataFrame 。让我们简要地看一下其中的一些。

### 5.1. 来自List<POJO>的DataFrame

让我们先建立一个List<Customer>： 

```java
List<Customer> customers = Arrays.asList(
  aCustomerWith("01", "jo", "Female", 2000), 
  aCustomerWith("02", "jack", "Male", 1200)
);
```

接下来，让我们使用createDataFrame从List<Customer>构建DataFrame：

```java
Dataset<Row> df = SPARK_SESSION
  .createDataFrame(customerList, Customer.class);
```

### 5.2. 来自数据集的数据框

如果我们有一个Dataset，我们可以通过在Dataset上调用toDF轻松地将它转换为DataFrame。

让我们首先使用createDataset创建一个Dataset<Customer>，它采用 org.apache.spark.sql.Encoders：

```java
Dataset<Customer> customerPOJODataSet = SPARK_SESSION
  .createDataset(CUSTOMERS, Encoders.bean(Customer.class));
```

接下来，让我们将其转换为DataFrame：

```java
Dataset<Row> df = customerPOJODataSet.toDF();
```

### 5.3. 使用RowFactory来自 POJO 的行

由于DataFrame本质上是Dataset<Row>，让我们看看如何从Customer POJO创建行。

基本上，通过实现 MapFunction<Customer, Row>并覆盖调用 方法，我们可以使用RowFactory.create将每个Customer映射到 一个 Row ：

```java
public class CustomerToRowMapper implements MapFunction<Customer, Row> {
    
    @Override
    public Row call(Customer customer) throws Exception {
        Row row = RowFactory.create(
          customer.getId(),
          customer.getName().toUpperCase(),
          StringUtils.substring(customer.getGender(),0, 1),
          customer.getTransaction_amount()
        );
        return row;
    }
}
```

我们应该注意，我们可以在将Customer数据转换为Row之前对其进行操作。

### 5.4. 来自List<Row>的DataFrame

我们还可以从Row对象列表创建一个DataFrame ：

```java
List<Row> rows = customer.stream()
  .map(c -> new CustomerToRowMapper().call(c))
  .collect(Collectors.toList());
```

现在，让我们将此List<Row>与StructType架构一起提供给SparkSession：

```java
Dataset<Row> df = SparkDriver.getSparkSession()
  .createDataFrame(rows, SchemaFactory.minimumCustomerDataSchema());
```

请注意，List<Row>将根据模式定义转换为DataFrame。模式中不存在的任何字段都不会成为 DataFrame 的一部分。

### 5.5. 来自结构化文件和数据库的DataFrame

DataFrames可以存储列信息，如 CSV 文件，以及嵌套字段和数组，如 JSON 文件。

无论我们使用的是 CSV 文件、JSON 文件还是其他格式以及数据库，DataFrame API 都保持不变。

让我们从多行 JSON 数据创建DataFrame ：

```java
Dataset<Row> df = SparkDriver.getSparkSession()
  .read()
  .format("org.apache.spark.sql.execution.datasources.json.JsonFileFormat")
  .option("multiline", true)
  .load("data/minCustomerData.json");
```

同样，在从数据库读取的情况下，我们将有：

```java
Dataset<Row> df = SparkDriver.getSparkSession()
  .read()
  .option("url", "jdbc:postgresql://localhost:5432/customerdb")
  .option("dbtable", "customer")
  .option("user", "user")
  .option("password", "password")
  .option("serverTimezone", "EST")
  .format("jdbc")
  .load();
```

## 6.将DataFrame转换为Dataset

现在，让我们看看如何将DataFrame 转换为Dataset。如果我们想要操作现有的 POJO 和仅适用于DataFrame的扩展 API，则此转换很有用。

我们将继续使用上一节中从 JSON 创建的DataFrame 。

让我们调用一个映射器函数，它获取Dataset<Row>的每一行并将其转换为Customer对象：

```java
Dataset<Customer> ds = df.map(
  new CustomerMapper(),
  Encoders.bean(Customer.class)
);
```

在这里，CustomerMapper实现了 MapFunction<Row, Customer>：

```java
public class CustomerMapper implements MapFunction<Row, Customer> {

    @Override
    public Customer call(Row row) {
        Customer customer = new Customer();
        customer.setId(row.getAs("id"));
        customer.setName(row.getAs("name"));
        customer.setGender(row.getAs("gender"));
        customer.setTransaction_amount(Math.toIntExact(row.getAs("transaction_amount")));
        return customer;
    }
}
```

我们应该注意到MapFunction <Row, Customer> 只被实例化一次，无论我们必须处理多少记录。

## 7. DataFrame操作和转换

现在，让我们使用客户数据示例构建一个简单的管道。我们希望从两个不同的文件源中提取客户数据作为数据帧 ，对其进行规范化，然后对数据执行一些转换。

最后，我们将转换后的数据写入数据库。

这些转换的目的是找出按性别和来源排序的年度支出。

### 7.1. 摄取数据

首先，让我们使用从 JSON 数据开始的SparkSession的读取方法从几个来源获取数据：

```java
Dataset<Row> jsonDataToDF = SPARK_SESSION.read()
  .format("org.apache.spark.sql.execution.datasources.json.JsonFileFormat")
  .option("multiline", true)
  .load("data/customerData.json");

```

现在，让我们对 CSV 源做同样的事情：

```java
Dataset<Row> csvDataToDF = SPARK_SESSION.read()
  .format("csv")
  .option("header", "true")
  .schema(SchemaFactory.customerSchema())
  .option("dateFormat", "m/d/YYYY")
  .load("data/customerData.csv"); 

csvDataToDF.show(); 
csvDataToDF.printSchema(); 
return csvData;
```

重要的是，为了读取此 CSV 数据，我们提供了一个StructType架构来确定列数据类型。

一旦我们摄取了数据，我们就可以使用 show方法检查DataFrame的内容。

此外，我们还可以通过在 show方法中提供大小来限制行数。而且，我们可以使用 printSchema来检查新创建的DataFrame 的模式。

我们会注意到这两个模式有一些差异。因此，我们需要先规范化模式，然后才能进行任何转换。

### 7.2. 规范化数据帧

接下来，我们将规范化表示 CSV 和 JSON 数据的原始数据帧。

在这里，让我们看看执行的一些转换：

```java
private Dataset<Row> normalizeCustomerDataFromEbay(Dataset<Row> rawDataset) {
    Dataset<Row> transformedDF = rawDataset
      .withColumn("id", concat(rawDataset.col("zoneId"),lit("-"), rawDataset.col("customerId")))
      .drop(column("customerId"))
      .withColumn("source", lit("ebay"))
      .withColumn("city", rawDataset.col("contact.customer_city"))
      .drop(column("contact"))
      .drop(column("zoneId"))
      .withColumn("year", functions.year(col("transaction_date")))
      .drop("transaction_date")
      .withColumn("firstName", functions.split(column("name"), " ")
        .getItem(0))
      .withColumn("lastName", functions.split(column("name"), " ")
        .getItem(1))
      .drop(column("name"));

    return transformedDF; 
}
```

上例中对DataFrame的一些重要操作是：

-   c oncat 连接来自多个列和文字的数据以创建一个新的 id列
-   lit 静态函数返回具有文字值的列
-   功能。year 从transactionDate中提取年份
-   function.split将姓名 拆分为名字和姓氏列
-   drop 方法删除数据框中的一列
-   col方法根据名称返回数据集的列
-   withColumnRenamed 返回具有重命名值的列

重要的是，我们可以看到DataFrame是不可变的。 因此，无论何时需要更改，我们都必须创建一个新的DataFrame。

最终，两个数据框都被规范化为相同的模式，如下所示：

```java
root
 |-- gender: string (nullable = true)
 |-- transaction_amount: long (nullable = true)
 |-- id: string (nullable = true)
 |-- source: string (nullable = false)
 |-- city: string (nullable = true)
 |-- year: integer (nullable = true)
 |-- firstName: string (nullable = true)
 |-- lastName: string (nullable = true)
```

### 7.3. 组合数据框

接下来让我们组合规范化的DataFrames：

```java
Dataset<Row> combineDataframes(Dataset<Row> df1, Dataset<Row> df2) {
    return df1.unionByName(df2); 
}
```

重要的是，我们应该注意：

-   如果我们在合并两个DataFrame时关心列名，我们应该使用unionByName
-   如果我们在合并两个DataFrame时不关心列名，我们应该使用union

### 7.4. 聚合数据帧

接下来，让我们对合并后的DataFrame进行分组，以按年份、来源和性别找出年度支出。

然后，我们将按年升序列和按降序排列的年度支出对聚合 数据进行排序：

```sql
Dataset<Row> aggDF = dataset
  .groupBy(column("year"), column("source"), column("gender"))
  .sum("transactionAmount")
  .withColumnRenamed("sum(transaction_amount)", "yearly spent")
  .orderBy(col("year").asc(), col("yearly spent").desc());
```

上例中对DataFrame的一些重要操作是：

-   groupBy 用于将相同的数据在DataFrame上进行分组，然后执行类似于SQL“GROUP BY”子句的聚合功能
-   sum 在分组后对列 transactionAmount应用聚合函数
-   orderBy 按一列或多列对DataFrame进行排序
-   Column类的asc和 desc函数可用于指定排序顺序

最后我们用show方法看看数据框转换后的样子：

```bash
+----+------+------+---------------+
|year|source|gender|annual_spending|
+----+------+------+---------------+
|2018|amazon|  Male|          10600|
|2018|amazon|Female|           6200|
|2018|  ebay|  Male|           5500|
|2021|  ebay|Female|          16000|
|2021|  ebay|  Male|          13500|
|2021|amazon|  Male|           4000|
|2021|amazon|Female|           2000|
+----+------+------+---------------+
```

因此，最终转换后的模式应该是：

```java
root
 |-- source: string (nullable = false)
 |-- gender: string (nullable = true)
 |-- year: integer (nullable = true)
 |-- yearly spent: long (nullable = true)

```

### 7.5. 从DataFrame写入关系数据库

最后，让我们通过将转换后的DataFrame写入关系数据库中的表来结束：

```java
Properties dbProps = new Properties();

dbProps.setProperty("connectionURL", "jdbc:postgresql://localhost:5432/customerdb");
dbProps.setProperty("driver", "org.postgresql.Driver");
dbProps.setProperty("user", "postgres");
dbProps.setProperty("password", "postgres");
```

接下来，我们可以使用 Spark 会话写入数据库：

```java
String connectionURL = dbProperties.getProperty("connectionURL");

dataset.write()
  .mode(SaveMode.Overwrite)
  .jdbc(connectionURL, "customer", dbProperties);
```

## 8. 测试

现在，我们可以使用两个摄取源以及postgres 和pgAdmin Docker 镜像来端到端地测试管道：

```java
@Test
void givenCSVAndJSON_whenRun_thenStoresAggregatedDataFrameInDB() throws Exception {
    Properties dbProps = new Properties();
    dbProps.setProperty("connectionURL", "jdbc:postgresql://localhost:5432/customerdb");
    dbProps.setProperty("driver", "org.postgresql.Driver");
    dbProps.setProperty("user", "postgres");
    dbProps.setProperty("password", "postgres");

    pipeline = new CustomerDataAggregationPipeline(dbProps);
    pipeline.run();

    String allCustomersSql = "Select count() from customer";

    Statement statement = conn.createStatement();
    ResultSet resultSet = statement.executeQuery(allCustomersSql);
    resultSet.next();
    int count = resultSet.getInt(1);
    assertEquals(7, count);
}
```

运行此命令后，我们可以验证是否存在一个表，其中的列和行与DataFrame对应。最后，我们还可以通过pgAdmin4客户端观察这个输出：

[![截图 2022-04-23-at-01.11.37](https://www.baeldung.com/wp-content/uploads/2022/05/Screenshot-2022-04-23-at-01.11.37.png)](https://www.baeldung.com/wp-content/uploads/2022/05/Screenshot-2022-04-23-at-01.11.37.png)

我们应该在这里注意几个要点：

-   作为写操作的结果，客户表是自动创建的。
-   使用的模式是SaveMode.Overwrite。 因此，这将覆盖表中已经存在的任何内容。其他可用选项有Append、Ignore和ErrorIfExists。

此外，我们还可以使用write 将DataFrame数据导出为CSV、JSON或parquet等格式。

## 9.总结

在本教程中，我们了解了如何使用DataFrames在 Apache Spark 中执行数据操作和聚合。

首先，我们从各种输入源创建了DataFrame 。然后我们使用一些 API 方法对数据进行规范化、合并，然后聚合。

最后，我们将DataFrame导出为关系数据库中的表。
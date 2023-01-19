## 1. 简介

[Apache Spark](https://spark.apache.org/)是一个开源集群计算框架。它为 Scala、Java、Python 和 R 提供优雅的开发 API，允许开发人员跨不同数据源(包括 HDFS、Cassandra、HBase、S3 等)执行各种数据密集型工作负载。

从历史上看，Hadoop 的 MapReduce 被证明对于某些迭代和交互式计算作业效率低下，这最终导致了 Spark 的发展。使用 Spark，我们可以比内存中的 Hadoop 快两个数量级，或者在磁盘上快一个数量级。

## 2.星火架构

[如下图](https://spark.apache.org/docs/latest/cluster-overview.html)所示，Spark 应用程序在集群上作为独立的进程集运行：

[![集群概览](https://www.baeldung.com/wp-content/uploads/2017/10/cluster-overview-300x144.png)](https://www.baeldung.com/wp-content/uploads/2017/10/cluster-overview.png)

 

这些进程集由主程序(称为驱动程序)中的SparkContext对象协调。SparkContext连接到多种类型的集群管理器(Spark 自己的独立集群管理器、Mesos 或 YARN)，这些集群管理器跨应用程序分配资源。

连接后，Spark 会在集群中的节点上获取执行程序，这些执行程序是为你的应用程序运行计算和存储数据的进程。

接下来，它将你的应用程序代码(由传递给SparkContext的 JAR 或 Python 文件定义)发送给执行程序。最后，SparkContext将任务发送给执行器运行。

## 3. 核心组件

下[图](https://intellipaat.com/tutorial/spark-tutorial/apache-spark-components/)清晰地展示了 Spark 的不同组件：

[![星火的组件](https://www.baeldung.com/wp-content/uploads/2017/10/Components-of-Spark-300x143.jpg)](https://www.baeldung.com/wp-content/uploads/2017/10/Components-of-Spark.jpg)

 

### 3.1. 星火核心

Spark Core 组件负责所有基本的 I/O 功能、调度和监控 spark 集群上的作业、任务调度、与不同存储系统的网络、故障恢复和高效的内存管理。

与 Hadoop 不同，Spark 通过使用称为 RDD(弹性分布式数据集)的特殊数据结构避免共享数据存储在 Amazon S3 或 HDFS 等中间存储中。

弹性分布式数据集是不可变的，是一个分区的记录集合，可以并行操作并允许容错“内存中”计算。

RDD 支持两种操作：

-   转换——Spark RDD 转换是一种从现有 RDD 生成新 RDD 的函数。Transformer 以 RDD 作为输入，产生一个或多个 RDD 作为输出。转换本质上是惰性的，即，当我们调用一个动作时它们就会执行

-   动作——转换从彼此创建 RDD，但是当我们想要使用实际数据集时，此时会执行动作。因此，操作是提供非 RDD 值的 Spark RDD 操作。动作的值存储到驱动程序或外部存储系统

动作是从执行器向驱动程序发送数据的方式之一。

执行者是负责执行任务的代理。而驱动程序是一个 JVM 进程，用于协调工作人员和任务的执行。Spark 的一些动作是计数和收集。

### 3.2. 星火SQL

Spark SQL 是一个用于结构化数据处理的 Spark 模块。它主要用于执行 SQL 查询。DataFrame构成了 Spark SQL 的主要抽象。在 Spark中，分布式数据集合被命名为列，称为DataFrame。

Spark SQL 支持从 Hive、Avro、Parquet、ORC、JSON 和 JDBC 等不同来源获取数据。它还使用 Spark 引擎扩展到数千个节点和多小时查询——它提供了完整的中间查询容错。

### 3.3. 火花流

Spark Streaming 是核心 Spark API 的扩展，支持对实时数据流进行可扩展、高吞吐量、容错的流处理。数据可以从许多来源获取，例如 Kafka、Flume、Kinesis 或 TCP 套接字。

最后，可以将处理后的数据推送到文件系统、数据库和实时仪表板。

### 3.4. 火花机

MLlib 是 Spark 的机器学习 (ML) 库。它的目标是使实用的机器学习具有可扩展性和简单性。在高层次上，它提供了以下工具：

-   机器学习算法——常见的学习算法，如分类、回归、聚类和协同过滤
-   特征化——特征提取、变换、降维和选择
-   管道——用于构建、评估和调整 ML 管道的工具
-   持久性——保存和加载算法、模型和管道
-   实用工具——线性代数、统计、数据处理等。

### 3.5. 星火图X

GraphX 是用于图和图并行计算的组件。在高层次上，GraphX 通过引入新的图抽象来扩展 Spark RDD：一个有向多图，其属性附加到每个顶点和边。

为了支持图形计算，GraphX 公开了一组基本运算符(例如，subgraph、joinVertices和aggregateMessages)。

此外，GraphX 包括越来越多的图形算法和构建器集合，以简化图形分析任务。

## 4. Spark 中的“Hello World”

现在我们了解了核心组件，我们可以继续进行简单的基于 Maven 的 Spark 项目——用于计算字数。

我们将演示在本地模式下运行的 Spark，其中所有组件都在同一台机器上本地运行，其中它是主节点、执行节点或 Spark 的独立集群管理器。

### 4.1. Maven 设置

让我们在pom.xml文件中设置一个带有[Spark 相关依赖项的JavaMaven 项目：](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.spark" AND a%3A"spark-core_2.10")

```xml
<dependencies>
    <dependency>
        <groupId>org.apache.spark</groupId>
	<artifactId>spark-core_2.10</artifactId>
	<version>1.6.0</version>
    </dependency>
</dependencies>
```

### 4.2. 字数统计 – Spark 作业

现在让我们编写 Spark 作业来处理包含句子的文件并输出不同的单词及其在文件中的计数：

```java
public static void main(String[] args) throws Exception {
    if (args.length < 1) {
        System.err.println("Usage: JavaWordCount <file>");
        System.exit(1);
    }
    SparkConf sparkConf = new SparkConf().setAppName("JavaWordCount");
    JavaSparkContext ctx = new JavaSparkContext(sparkConf);
    JavaRDD<String> lines = ctx.textFile(args[0], 1);

    JavaRDD<String> words 
      = lines.flatMap(s -> Arrays.asList(SPACE.split(s)).iterator());
    JavaPairRDD<String, Integer> ones 
      = words.mapToPair(word -> new Tuple2<>(word, 1));
    JavaPairRDD<String, Integer> counts 
      = ones.reduceByKey((Integer i1, Integer i2) -> i1 + i2);

    List<Tuple2<String, Integer>> output = counts.collect();
    for (Tuple2<?, ?> tuple : output) {
        System.out.println(tuple._1() + ": " + tuple._2());
    }
    ctx.stop();
}
```

请注意，我们将本地文本文件的路径作为参数传递给 Spark 作业。

SparkContext对象是Spark的主要入口点，表示与已运行的 Spark 集群的连接。它使用SparkConf对象来描述应用程序配置。SparkContext用于读取内存中的文本文件作为JavaRDD对象。

接下来，我们使用flatmap方法将lines JavaRDD对象转换为words JavaRDD对象，首先将每行转换为空格分隔的单词，然后将每行处理的输出展平。

我们再次应用变换操作mapToPair，它基本上将单词的每次出现映射到单词元组和 1 的计数。

然后，我们应用reduceByKey操作将多次出现的计数为 1 的任何单词分组到一个单词元组中，并对计数求和。

最后，我们执行 c ollect RDD action 得到最终的结果。

### 4.3. 执行 – Spark 作业

现在让我们使用 Maven 构建项目以在目标文件夹中生成apache-spark-1.0-SNAPSHOT.jar 。

接下来，我们需要将这个 WordCount 作业提交给 Spark：

```shell
${spark-install-dir}/bin/spark-submit --class cn.tuyucheng.taketoday.WordCount 
  --master local ${WordCount-MavenProject}/target/apache-spark-1.0-SNAPSHOT.jar
  ${WordCount-MavenProject}/src/main/resources/spark_example.txt
```

在运行上述命令之前，需要更新 Spark 安装目录和 WordCount Maven 项目目录。

提交后，幕后发生了几个步骤：

1.  从驱动程序代码，SparkContext连接到集群管理器(在我们的例子中是在本地运行的 spark 独立集群管理器)
2.  集群管理器跨其他应用程序分配资源
3.  Spark 在集群中的节点上获取执行器。在这里，我们的字数统计应用程序将获得自己的执行进程
4.  应用程序代码(jar 文件)被发送到执行程序
5.  任务由SparkContext发送给执行者。

最后，spark job 的结果返回给驱动程序，我们将看到文件中的单词数作为输出：

```shell
Hello 1
from 2
Baledung 2
Keep 1
Learning 1
Spark 1
Bye 1
```

## 5.总结

在本文中，我们讨论了 Apache Spark 的架构和不同组件。我们还演示了一个 Spark 作业的工作示例，该作业提供文件中的字数统计。
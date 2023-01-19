## 1. 简介

图形处理对于从社交网络到广告的许多应用程序都很有用。在大数据场景中，我们需要一个工具来分配处理负载。

在本教程中，我们将使用Java中的[Apache Spark加载和探索图的可能性。](https://www.baeldung.com/apache-spark)为避免复杂的结构，我们将使用简单且高级的 Apache Spark 图形 API：GraphFrames API。

## 2. 图表

首先，让我们定义一个图形及其组件。图是具有边和顶点的数据结构。边携带表示顶点之间关系的信息。

顶点是n维空间中的点，边根据它们的关系连接顶点：

[![图形示例 1](https://www.baeldung.com/wp-content/uploads/2019/12/Graph-Example-1.png)](https://www.baeldung.com/wp-content/uploads/2019/12/Graph-Example-1.png)

在上图中，我们有一个社交网络示例。我们可以看到字母代表的顶点和顶点之间承载着什么样的关系的边。

## 3. 行家设置

现在，让我们通过设置 Maven 配置来启动项目。

让我们添加[spark-graphx 2.11](https://search.maven.org/search?q=g:org.apache.spark AND a:spark-graphx_2.11)、 [graphframes](https://mvnrepository.com/artifact/graphframes/graphframes?repo=spark-packages)和[spark-sql 2.11](https://search.maven.org/search?q=g:org.apache.spark AND a:spark-sql_2.11)：

```xml
<dependency>
    <groupId>org.apache.spark</groupId>
    <artifactId>spark-graphx_2.11</artifactId>
    <version>2.4.4</version>
</dependency>
<dependency>
   <groupId>graphframes</groupId>
   <artifactId>graphframes</artifactId>
   <version>0.7.0-spark2.4-s_2.11</version>
</dependency>
<dependency>
   <groupId>org.apache.spark</groupId>
   <artifactId>spark-sql_2.11</artifactId>
   <version>2.4.4</version>
</dependency>
```

这些工件版本支持 Scala 2.11。

此外，碰巧 GraphFrames 不在 Maven Central 中。因此，让我们也添加所需的 Maven 存储库：

```xml
<repositories>
     <repository>
          <id>SparkPackagesRepo</id>
          <url>http://dl.bintray.com/spark-packages/maven</url>
     </repository>
</repositories>
```

## 4.星火配置

为了使用 GraphFrames，我们需要下载[Hadoop](https://hadoop.apache.org/releases.html)并定义HADOOP_HOME环境变量。

在 Windows 作为操作系统的情况下，我们还将适当的[winutils.exe](https://github.com/steveloughran/winutils/blob/master/hadoop-3.0.0/bin/winutils.exe)下载到HADOOP_HOME/bin文件夹。

接下来，让我们通过创建基本配置来开始我们的代码：

```java
SparkConf sparkConf = new SparkConf()
  .setAppName("SparkGraphFrames")
  .setMaster("local[]");
JavaSparkContext javaSparkContext = new JavaSparkContext(sparkConf);
```

我们还需要创建一个SparkSession：

```java
SparkSession session = SparkSession.builder()
  .appName("SparkGraphFrameSample")
  .config("spark.sql.warehouse.dir", "/file:C:/temp")
  .sparkContext(javaSparkContext.sc())
  .master("local[]")
  .getOrCreate();
```

## 5. 图的构建

现在，我们都准备好从我们的主要代码开始了。因此，让我们为顶点和边定义实体，并创建GraphFrame实例。

我们将处理来自一个假设的社交网络的用户之间的关系。

### 5.1. 数据

首先，对于此示例，我们将两个实体都定义为User和Relationship：

```java
public class User {
    private Long id;
    private String name;
    // constructor, getters and setters
}
 
public class Relationship implements Serializable {
    private String type;
    private String src;
    private String dst;
    private UUID id;

    public Relationship(String type, String src, String dst) {
        this.type = type;
        this.src = src;
        this.dst = dst;
        this.id = UUID.randomUUID();
    }
    // getters and setters
}
```

接下来，让我们定义一些User和Relationship实例：

```java
List<User> users = new ArrayList<>();
users.add(new User(1L, "John"));
users.add(new User(2L, "Martin"));
users.add(new User(3L, "Peter"));
users.add(new User(4L, "Alicia"));

List<Relationship> relationships = new ArrayList<>();
relationships.add(new Relationship("Friend", "1", "2"));
relationships.add(new Relationship("Following", "1", "4"));
relationships.add(new Relationship("Friend", "2", "4"));
relationships.add(new Relationship("Relative", "3", "1"));
relationships.add(new Relationship("Relative", "3", "4"));
```

### 5.2. GraphFrame实例

现在，为了创建和操作我们的关系图，我们将创建一个GraphFrame实例。GraphFrame构造函数需要两个Dataset<Row>实例，第一个代表顶点，第二个代表边：

```java
Dataset<Row> userDataset = session.createDataFrame(users, User.class);
Dataset<Row> relationshipDataset = session.createDataFrame(relationships, Relation.class);

GraphFrame graph = new GraphFrame(userDataframe, relationshipDataframe);
```

最后，我们将在控制台中记录我们的顶点和边以查看它的外观：

```java
graph.vertices().show();
graph.edges().show();
+---+------+
| id|  name|
+---+------+
|  1|  John|
|  2|Martin|
|  3| Peter|
|  4|Alicia|
+---+------+

+---+--------------------+---+---------+
|dst|                  id|src|     type|
+---+--------------------+---+---------+
|  2|622da83f-fb18-484...|  1|   Friend|
|  4|c6dde409-c89d-490...|  1|Following|
|  4|360d06e1-4e9b-4ec...|  2|   Friend|
|  1|de5e738e-c958-4e0...|  3| Relative|
|  4|d96b045a-6320-4a6...|  3| Relative|
+---+--------------------+---+---------+
```

## 6. 图运算符

现在我们有了一个GraphFrame实例，让我们看看我们可以用它做什么。

### 6.1. 筛选

GraphFrames 允许我们通过查询过滤边和顶点。

接下来，让我们通过 User的name 属性 过滤顶点：

```java
graph.vertices().filter("name = 'Martin'").show();
```

在控制台，我们可以看到结果：

```shell
+---+------+
| id|  name|
+---+------+
|  2|Martin|
+---+------+
```

此外，我们可以通过调用filterEdges或filterVertices直接在图上进行过滤：

```java
graph.filterEdges("type = 'Friend'")
  .dropIsolatedVertices().vertices().show();
```

现在，由于我们过滤了边，我们可能仍然有一些孤立的顶点。因此，我们将调用dropIsolatedVertices()。 

结果，我们有一个子图，仍然是一个GraphFrame实例，只有具有“朋友”状态的关系：

```shell
+---+------+
| id|  name|
+---+------+
|  1|  John|
|  2|Martin|
|  4|Alicia|
+---+------+
```

### 6.2. 学位

另一个有趣的特征集是操作的度集。[这些操作返回入射](https://www.baeldung.com/cs/graphs-incident-edge)到每个顶点的边数。

度运算仅返回每个顶点的所有边的计数。另一方面，inDegrees仅计算传入边，outDegrees仅计算传出边。

让我们计算图中所有顶点的传入度数：

```java
graph.inDegrees().show();
```

结果，我们有一个GraphFrame显示每个顶点的传入边数，不包括没有的边：

```shell
+---+--------+
| id|inDegree|
+---+--------+
|  1|       1|
|  4|       3|
|  2|       1|
+---+--------+
```

## 7. 图算法

GraphFrames 还提供随时可用的流行算法——让我们来看看其中的一些算法。

### 7.1. 网页排名

Page Rank 算法对顶点的传入边进行加权并将其转换为分数。

这个想法是每个传入边代表一个背书，并使顶点在给定图中更相关。

例如，在社交网络中，如果一个人被不同的人关注，他或她的排名就会很高。

运行页面排名算法非常简单：

```java
graph.pageRank()
  .maxIter(20)
  .resetProbability(0.15)
  .run()
  .vertices()
  .show();
```

要配置此算法，我们只需要提供：

-   maxIter – 要运行的页面排名的迭代次数 – 建议为 20，太少会降低质量，太多会降低性能
-   resetProbability – 随机重置概率 (alpha) – 它越低，赢家和输家之间的分数差距就越大 – 有效范围是从 0 到 1。通常，0.15 是一个不错的分数

响应是一个类似的GraphFrame，不过这次我们看到了一个额外的列，给出了每个顶点的页面排名：

```shell
+---+------+------------------+
| id|  name|          pagerank|
+---+------+------------------+
|  4|Alicia|1.9393230468864597|
|  3| Peter|0.4848822786454427|
|  1|  John|0.7272991738542318|
|  2|Martin| 0.848495500613866|
+---+------+------------------+
```

在我们的图中，Alicia 是最相关的顶点，其次是 Martin 和 John。

### 7.2. 连通分量

连通分量算法找到孤立的簇或孤立的子图。这些簇是图中连接的顶点的集合，其中每个顶点都可以从同一集合中的任何其他顶点到达。

我们可以通过connectedComponents()方法调用不带任何参数的算法：

```java
graph.connectedComponents().run().show();
```

该算法返回一个GraphFrame，其中包含每个顶点和每个顶点所连接的组件：

```shell
+---+------+------------+
| id|  name|   component|
+---+------+------------+
|  1|  John|154618822656|
|  2|Martin|154618822656|
|  3| Peter|154618822656|
|  4|Alicia|154618822656|
+---+------+------------+
```

我们的图只有一个组成部分——这意味着我们没有孤立的子图。该组件有一个自动生成的 ID，在我们的例子中是 154618822656。

尽管我们在这里多了一列——组件 ID——我们的图表仍然是一样的。

### 7.3. 三角计数

三角形计数通常用作社交网络图中的社区检测和计数。三角形是一组三个顶点，其中每个顶点与三角形中的其他两个顶点有关系。

在社交网络社区中，很容易发现相当数量的三角形相互连接。

我们可以直接从我们的GraphFrame实例轻松地执行三角形计数：

```java
graph.triangleCount().run().show();
```

该算法还返回一个GraphFrame，其中包含通过每个顶点的三角形数。

```shell
+-----+---+------+
|count| id|  name|
+-----+---+------+
|    1|  3| Peter|
|    2|  1|  John|
|    2|  4|Alicia|
|    1|  2|Martin|
+-----+---+------+
```

## 八. 总结

Apache Spark 是一个以优化和分布式方式计算相关数据量的好工具。而且，GraphFrames 库使我们能够轻松地在 Spark 上分发图形操作。
## 1. 概述

[InfluxDB](https://www.influxdata.com/)是一种用于时间序列数据的高性能存储。它支持通过类似SQL的查询语言插入和实时查询数据。

在这篇介绍性文章中，我们将演示如何连接到 InfluxDb 服务器、创建数据库、写入时间序列信息，然后查询数据库。

## 2.设置

要连接到数据库，我们需要在pom.xml文件中添加一个条目：

```plaintext
<dependency>
    <groupId>org.influxdb</groupId>
    <artifactId>influxdb-java</artifactId>
    <version>2.8</version>
</dependency>

```

可以在[Maven Central](https://search.maven.org/classic/#search|gav|1|g%3A"org.influxdb" AND a%3A"influxdb-java")上找到此依赖项的最新版本。

我们还需要一个 InfluxDB 实例。可以在[InfluxData](https://www.influxdata.com/)网站上找到有关[下载和安装数据库](https://docs.influxdata.com/influxdb/v1.4/introduction/installation/)的说明。

## 3.连接到服务器

### 3.1. 创建连接

创建数据库连接需要将 URL字符串和用户凭据传递给连接工厂：

```java
InfluxDB influxDB = InfluxDBFactory.connect(databaseURL, userName, password);
```

### 3.2. 验证连接

与数据库的通信是通过 RESTful API 执行的，因此它们不是持久的。

API 提供专用的“ping”服务来确认连接是否正常。如果连接良好，则响应包含数据库版本。如果不是，它包含“未知”。

因此，在创建连接后，我们可以通过以下方式验证它：

```java
Pong response = this.influxDB.ping();
if (response.getVersion().equalsIgnoreCase("unknown")) {
    log.error("Error pinging server.");
    return;
} 

```

### 3.3. 创建数据库

创建 InfluxDB 数据库类似于在大多数平台上创建数据库。但是我们需要在使用之前至少创建一个保留策略。

保留策略告诉数据库一条数据应该存储多长时间。时间序列，例如 CPU 或内存统计信息，往往会在大型数据集中累积。

控制时间序列数据库大小的典型策略是下采样。“原始”数据以高速率存储、汇总，然后在短时间后删除。

保留策略通过将一段数据与过期时间相关联来简化这一过程。InfluxData在他们的网站上有[深入的解释。](https://docs.influxdata.com/influxdb/v1.4/guides/downsampling_and_retention/)

创建数据库后，我们将添加一个名为defaultPolicy 的策略。它只会将数据保留 30 天：

```java
influxDB.createDatabase("baeldung");
influxDB.createRetentionPolicy(
  "defaultPolicy", "baeldung", "30d", 1, true);
```

要创建保留策略，我们需要一个名称、数据库、一个时间间隔、一个因子(对于单实例数据库应该为 1)和一个表示它是默认策略的布尔值。

### 3.4. 设置日志记录级别

在内部，InfluxDB API 使用[Retrofit](https://www.baeldung.com/retrofit)并通过[日志拦截器向 Retrofit 的日志工具公开一个接口。](https://www.baeldung.com/retrofit#logging)

因此，我们可以使用以下方法设置日志记录级别：

```java
influxDB.setLogLevel(InfluxDB.LogLevel.BASIC);

```

现在我们可以在打开连接并对其执行 ping 操作时看到消息：

```plaintext
Dec 20, 2017 5:38:10 PM okhttp3.internal.platform.Platform log
INFO: --> GET http://127.0.0.1:8086/ping
```

可用级别为BASIC、FULL、HEADERS和NONE 。

## 4. 添加和检索数据

### 4.1. 积分

所以现在我们准备开始插入和检索数据。

InfluxDB中信息的基本单位是Point，本质上是时间戳和key-value map。

让我们看一下保存内存利用率数据的点：

```java
Point point = Point.measurement("memory")
  .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
  .addField("name", "server1")
  .addField("free", 4743656L)
  .addField("used", 1015096L)
  .addField("buffer", 1010467L)
  .build();

```

我们创建了一个条目，其中包含三个Long作为内存统计信息、一个主机名和一个时间戳。

让我们看看如何将它添加到数据库中。

### 4.2. 写批次

时间序列数据往往由许多小点组成，一次写入这些记录将非常低效。首选方法是将记录收集到批次中。

InfluxDB API 提供了一个BatchPoint对象：

```java
BatchPoints batchPoints = BatchPoints
  .database(dbName)
  .retentionPolicy("defaultPolicy")
  .build();

Point point1 = Point.measurement("memory")
  .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
  .addField("name", "server1") 
  .addField("free", 4743656L)
  .addField("used", 1015096L) 
  .addField("buffer", 1010467L)
  .build();

Point point2 = Point.measurement("memory")
  .time(System.currentTimeMillis() - 100, TimeUnit.MILLISECONDS)
  .addField("name", "server1")
  .addField("free", 4743696L)
  .addField("used", 1016096L)
  .addField("buffer", 1008467L)
  .build();

batchPoints.point(point1);
batchPoints.point(point2);
influxDB.write(batchPoints);
```

我们创建一个BatchPoint，然后向其添加Points。我们将第二个条目的时间戳设置为过去 100 毫秒，因为时间戳是主索引。如果我们发送两个具有相同时间戳的点，则只会保留一个。

请注意，我们必须将 BatchPoints与数据库和保留策略相关联。

### 4.3. 一次写一个

对于某些用例，批处理可能不切实际。

让我们通过一次调用 InfluxDB 连接来启用批处理模式：

```java
influxDB.enableBatch(100, 200, TimeUnit.MILLISECONDS);

```

我们启用了 100 的批处理以插入服务器或每 200 毫秒发送一次。

启用批处理模式后，我们仍然可以一次编写一个。但是，需要一些额外的设置：

```java
influxDB.setRetentionPolicy("defaultPolicy");
influxDB.setDatabase(dbName);

```

此外，现在我们可以写个人积分，它们由后台线程批量收集：

```java
influxDB.write(point);

```

在我们对单个点进行入队之前，我们需要设置一个数据库(类似于 SQL 中的use命令)并设置一个默认的保留策略。因此，如果我们希望利用具有多个保留策略的下采样，创建批次是可行的方法。

批处理模式使用一个单独的线程池。因此，在不再需要时禁用它是个好主意：

```java
influxDB.disableBatch();

```

关闭连接也会关闭线程池：

```java
influxDB.close();
```

### 4.4. 映射查询结果

查询返回一个QueryResult，我们可以将其映射到 POJO。

在我们查看查询语法之前，让我们创建一个类来保存我们的内存统计信息：

```java
@Measurement(name = "memory")
public class MemoryPoint {

    @Column(name = "time")
    private Instant time;

    @Column(name = "name")
    private String name;

    @Column(name = "free")
    private Long free;

    @Column(name = "used")
    private Long used;

    @Column(name = "buffer")
    private Long buffer;
}

```

该类用@Measurement(name = “memory”)注解，对应于我们用来创建Points的Point.measurement(“memory”)。

对于QueryResult中的每个字段，我们添加带有相应字段名称的@Column(name = “XXX”)注解。

QueryResults使用InfluxDBResultMapper 映射到 POJO。

### 4.5. 查询 InfluxDB

因此，让我们将 POJO 与我们在两点批处理中添加到数据库中的点一起使用：

```java
QueryResult queryResult = connection
  .performQuery("Select  from memory", "baeldung");

InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
List<MemoryPoint> memoryPointList = resultMapper
  .toPOJO(queryResult, MemoryPoint.class);

assertEquals(2, memoryPointList.size());
assertTrue(4743696L == memoryPointList.get(0).getFree());

```

该查询说明了我们的名为memory的测量如何存储为我们可以从中选择的点表。

InfluxDBResultMapper通过QueryResult接受对MemoryPoint.class的引用并返回一个点列表。

映射结果后，我们通过检查从查询中收到的列表的长度来验证我们收到了两个。然后我们查看列表中的第一个条目，并查看我们插入的第二个点的可用内存大小。来自 InfluxDB 的查询结果的默认排序是按时间戳升序。

让我们改变一下：

```java
queryResult = connection.performQuery(
  "Select  from memory order by time desc", "baeldung");
memoryPointList = resultMapper
  .toPOJO(queryResult, MemoryPoint.class);

assertEquals(2, memoryPointList.size());
assertTrue(4743656L == memoryPointList.get(0).getFree());

```

按时间 desc添加顺序会颠倒我们结果的顺序。

InfluxDB 查询看起来与 SQL 非常相似。[他们的网站上](https://docs.influxdata.com/influxdb/v1.4/guides/querying_data/)有一份详尽的参考指南。

## 5.总结

我们已经连接到 InfluxDB 服务器，创建了一个具有保留策略的数据库，然后从服务器插入和检索数据。
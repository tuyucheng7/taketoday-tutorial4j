## 1. 概述

[Apache Cassandra](https://www.baeldung.com/cassandra-with-java)的 DataStax Distribution是一个生产就绪的分布式数据库，与开源 Cassandra 兼容。它增加了一些开源发行版中没有的功能，包括监控、改进的批处理和流数据处理。

DataStax 还为其 Apache Cassandra 发行版提供了一个Java客户端。该驱动程序高度可调，可以利用 DataStax 发行版中的所有额外功能，但它也与开源版本完全兼容。

在本教程中，我们将了解如何使用[适用于 Apache Cassandra 的 DataStaxJava驱动程序](https://github.com/datastax/java-driver) 连接到 Cassandra 数据库并执行基本数据操作。

## 延伸阅读：

## [使用 Cassandra、Astra 和 Stargate 构建仪表板](https://www.baeldung.com/cassandra-astra-stargate-dashboard)

了解如何使用 DataStax Astra 构建仪表板，DataStax Astra 是一种由 Apache Cassandra 和 Stargate API 提供支持的数据库即服务。

[阅读更多](https://www.baeldung.com/cassandra-astra-stargate-dashboard)→

## [使用 Cassandra、Astra、REST 和 GraphQL 构建仪表板——记录状态更新](https://www.baeldung.com/cassandra-astra-rest-dashboard-updates)

使用 Cassandra 存储时间序列数据的示例。

[阅读更多](https://www.baeldung.com/cassandra-astra-rest-dashboard-updates)→

## [使用 Cassandra、Astra 和 CQL 构建仪表板——映射事件数据](https://www.baeldung.com/cassandra-astra-rest-dashboard-map)

了解如何根据存储在 Astra 数据库中的数据在交互式地图上显示事件。

[阅读更多](https://www.baeldung.com/cassandra-astra-rest-dashboard-map)→



## 2.Maven依赖

为了使用 Apache Cassandra 的 DataStaxJava驱动程序，我们需要将它包含在我们的类路径中。

使用 Maven，我们只需将[java-driver-core依赖](https://search.maven.org/search?q=g:com.datastax.oss a:java-driver-core)项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.datastax.oss</groupId>
    <artifactId>java-driver-core</artifactId>
    <version>4.1.0</version>
</dependency>

<dependency>
    <groupId>com.datastax.oss</groupId>
    <artifactId>java-driver-query-builder</artifactId>
    <version>4.1.0</version>
</dependency>
```

## 3. 使用 DataStax 驱动程序

现在我们已经有了驱动程序，让我们看看我们可以用它做什么。

### 3.1. 连接到数据库

为了连接到数据库，我们将创建一个CqlSession：

```java
CqlSession session = CqlSession.builder().build();
```

如果我们没有明确定义任何联系点，构建器将默认为127.0.0.1:9042。

让我们创建一个带有一些可配置参数的连接器类来构建CqlSession：

```java
public class CassandraConnector {

    private CqlSession session;

    public void connect(String node, Integer port, String dataCenter) {
        CqlSessionBuilder builder = CqlSession.builder();
        builder.addContactPoint(new InetSocketAddress(node, port));
        builder.withLocalDatacenter(dataCenter);

        session = builder.build();
    }

    public CqlSession getSession() {
        return this.session;
    }

    public void close() {
        session.close();
    }
}
```

### 3.2. 创建键空间

现在我们已经连接到数据库，我们需要创建我们的键空间。让我们首先编写一个简单的存储库类来处理我们的键空间。

对于本教程，我们将使用SimpleStrategy策略并将副本数设置为 1：

```java
public class KeyspaceRepository {

    public void createKeyspace(String keyspaceName, int numberOfReplicas) {
        CreateKeyspace createKeyspace = SchemaBuilder.createKeyspace(keyspaceName)
          .ifNotExists()
          .withSimpleStrategy(numberOfReplicas);

        session.execute(createKeyspace.build());
    }

    // ...
}
```

此外，我们可以在当前会话中开始使用键空间：

```java
public class KeyspaceRepository {

    //...
 
    public void useKeyspace(String keyspace) {
        session.execute("USE " + CqlIdentifier.fromCql(keyspace));
    }
}
```

### 3.3. 创建表

驱动程序提供语句来配置和执行数据库中的查询。例如，我们可以将键空间设置为在每个语句中单独使用。

我们将定义视频模型并创建一个表来表示它：

```java
public class Video {
    private UUID id;
    private String title;
    private Instant creationDate;

    // standard getters and setters
}
```

让我们创建我们的表，可以定义我们要在其中执行查询的键空间。我们将编写一个简单的VideoRepository类来处理我们的视频数据：

```java
public class VideoRepository {
    private static final String TABLE_NAME = "videos";

    public void createTable() {
        createTable(null);
    }

    public void createTable(String keyspace) {
        CreateTable createTable = SchemaBuilder.createTable(TABLE_NAME)
          .withPartitionKey("video_id", DataTypes.UUID)
          .withColumn("title", DataTypes.TEXT)
          .withColumn("creation_date", DataTypes.TIMESTAMP);

        executeStatement(createTable.build(), keyspace);
    }

    private ResultSet executeStatement(SimpleStatement statement, String keyspace) {
        if (keyspace != null) {
            statement.setKeyspace(CqlIdentifier.fromCql(keyspace));
        }

        return session.execute(statement);
    }

    // ...
}
```

请注意，我们正在重载方法createTable。

重载此方法背后的想法是为表创建提供两个选项：

-   在特定的键空间中创建表，将键空间名称作为参数发送，与当前使用的会话的键空间无关
-   在会话中开始使用键空间，并使用不带任何参数的表创建方法——在这种情况下，表将在会话当前使用的键空间中创建

### 3.4. 插入数据

此外，驱动程序还提供准备好的和有界的语句。

PreparedStatement 通常用于经常执行的查询，仅更改值。

我们可以用我们需要的值填充PreparedStatement 。之后，我们将创建一个BoundStatement并执行它。

让我们编写一个将一些数据插入数据库的方法：

```java
public class VideoRepository {

    //...
 
    public UUID insertVideo(Video video, String keyspace) {
        UUID videoId = UUID.randomUUID();

        video.setId(videoId);

        RegularInsert insertInto = QueryBuilder.insertInto(TABLE_NAME)
          .value("video_id", QueryBuilder.bindMarker())
          .value("title", QueryBuilder.bindMarker())
          .value("creation_date", QueryBuilder.bindMarker());

        SimpleStatement insertStatement = insertInto.build();

        if (keyspace != null) {
            insertStatement = insertStatement.setKeyspace(keyspace);
        }

        PreparedStatement preparedStatement = session.prepare(insertStatement);

        BoundStatement statement = preparedStatement.bind()
          .setUuid(0, video.getId())
          .setString(1, video.getTitle())
          .setInstant(2, video.getCreationDate());

        session.execute(statement);

        return videoId;
    }

    // ...
}
```

### 3.5. 查询数据

现在，让我们添加一个方法来创建一个简单的查询来获取我们存储在数据库中的数据：

```java
public class VideoRepository {
 
    // ...
 
    public List<Video> selectAll(String keyspace) {
        Select select = QueryBuilder.selectFrom(TABLE_NAME).all();

        ResultSet resultSet = executeStatement(select.build(), keyspace);

        List<Video> result = new ArrayList<>();

        resultSet.forEach(x -> result.add(
            new Video(x.getUuid("video_id"), x.getString("title"), x.getInstant("creation_date"))
        ));

        return result;
    }

    // ...
}
```

### 3.6. 把它们放在一起

最后，让我们看一个使用本教程中涵盖的每个部分的示例：

```java
public class Application {
 
    public void run() {
        CassandraConnector connector = new CassandraConnector();
        connector.connect("127.0.0.1", 9042, "datacenter1");
        CqlSession session = connector.getSession();

        KeyspaceRepository keyspaceRepository = new KeyspaceRepository(session);

        keyspaceRepository.createKeyspace("testKeyspace", 1);
        keyspaceRepository.useKeyspace("testKeyspace");

        VideoRepository videoRepository = new VideoRepository(session);

        videoRepository.createTable();

        videoRepository.insertVideo(new Video("Video Title 1", Instant.now()));
        videoRepository.insertVideo(new Video("Video Title 2",
          Instant.now().minus(1, ChronoUnit.DAYS)));

        List<Video> videos = videoRepository.selectAll();

        videos.forEach(x -> LOG.info(x.toString()));

        connector.close();
    }
}
```

执行我们的示例后，结果，我们可以在日志中看到数据已正确存储在数据库中：

```bash
INFO com.baeldung.datastax.cassandra.Application - [id:733249eb-914c-4153-8698-4f58992c4ad4, title:Video Title 1, creationDate: 2019-07-10T19:43:35.112Z]
INFO com.baeldung.datastax.cassandra.Application - [id:a6568236-77d7-42f2-a35a-b4c79afabccf, title:Video Title 2, creationDate: 2019-07-09T19:43:35.181Z]
```

## 4. 总结

在本教程中，我们介绍了适用于 Apache Cassandra 的 DataStaxJava驱动程序的基本概念。我们连接到数据库并创建了一个键空间和表。此外，我们将数据插入表中并运行查询来检索它。
## 一、概述

在本教程中，我们将探索[ScyllaDB——](https://www.scylladb.com/)一种快速且可扩展的 NoSQL 数据库。我们将看到它的功能以及如何与之交互。

## 2.什么是ScyllaDB？

**ScyllaDB 是一个开源的分布式 NoSQL 数据库。**[它支持与Cassandra](https://www.baeldung.com/cassandra-storage-engine)相同的协议，具有更高的吞吐量和更低的延迟。它是使用C++语言开发的。

ScyllaDB 具有三个变体：

-   ScyllaDB Open Source：这是一个免费的开源版本。我们将拥有完全所有权，需要自己进行维护
-   ScyllaDB Enterprise：这是一个付费版本，我们将在其中获得一些高级功能和 24/7 支持。我们需要使用我们自己的基础设施来安装这个变体
-   ScyllaDB Cloud：这是 ScyllaDB 提供的基于云的服务，我们不需要拥有自己的基础设施或进行任何安装和维护

### 2.1. 安装

我们将使用开源版本并使用以下命令在[Docker容器上运行它：](https://www.baeldung.com/ops/docker-guide)

```bash
$ docker run --name scylla -p 9042:9042 -d scylladb/scylla复制
```

我们在这里公开端口号 9042。我们将使用此端口连接到数据库。

现在，让我们连接到数据库，创建一个表并插入一些数据。我们将编写 Java 代码来获取这些数据。

让我们执行以下命令连接到数据库：

```bash
$ docker exec -it scylla cqlsh复制
```

现在让我们使用因子 3 的简单复制策略创建一个命名空间：

```sql
CREATE KEYSPACE IF NOT EXISTS baeldung WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 3};复制
```

让我们执行以下查询来创建表并插入数据：

```sql
CREATE COLUMNFAMILY IF NOT EXISTS baeldung.User (id bigint PRIMARY KEY, name text);
INSERT INTO baeldung.User (id, name) values (1, 'john doe');复制
```

## 3.Java代码实现

我们将编写一个简单的 Java 程序，该程序将连接到我们本地部署的 Scylla 数据库并执行查询。

### 3.1. Maven 依赖

让我们在*pom.xml*文件中添加[Scylla 核心库](https://search.maven.org/artifact/com.scylladb/java-driver-core)依赖项：

```java
<dependency>
    <groupId>com.scylladb</groupId>
    <artifactId>java-driver-core</artifactId>
    <version>4.14.1.0</version>
</dependency>复制
```

### 3.2. Java代码

让我们首先将连接 URL 添加到*application.yml*文件中：

```yaml
datastax-java-driver:
  basic:
    contact-points: 127.0.0.1:9042复制
```

我们可以参考[此文档](https://java-driver.docs.scylladb.com/stable/manual/core/configuration/reference/README.html)以获取有关所有可配置值的更多详细信息。

现在让我们获取之前添加的用户名：

```java
try (CqlSession session = CqlSession.builder().build()) {
    ResultSet rs = session.execute("select * from baeldung.User");
    Row row = rs.one();
    return row.getString("name");
}复制
```

我们还可以使用 *[查询生成器](https://java-driver.docs.scylladb.com/stable/manual/query_builder/)*来插入和获取数据。我们首先需要将 [*java-driver-query-builder* Maven 依赖](https://search.maven.org/artifact/com.scylladb/java-driver-query-builder)项添加到*pom.xml*文件中：

```java
<dependency>
    <groupId>com.scylladb</groupId>
    <artifactId>java-driver-query-builder</artifactId>
    <version>4.14.1.0</version>
</dependency>复制
```

现在我们将在代码中编写 SELECT 和 INSERT 构建器语句来获取和插入数据：

```java
try (CqlSession session = CqlSession.builder().build()) {
    InsertInto insert = insertInto("baeldung", "User");
    SimpleStatement statement = insert.value("id", literal(2))
      .value("name", literal("dev user"))
      .build();
    ResultSet rs = session.execute(statement);
}复制
```

它将在命名空间*baeldung*的表*User中插入一个**id = 2 和 name = “dev user”*的新用户。现在让我们创建一个 SELECT 语句来按名称查找该用户：

```java
try (CqlSession session = CqlSession.builder().build()) {
    Select query = selectFrom("baeldung", "User").all()
      .whereColumn("name").isEqualTo(literal("dev user"))
      .allowFiltering();;
    SimpleStatement statement = query.build();
    ResultSet rs = session.execute(statement);
    Row row = rs.one();
    assertEquals(2, row.getLong("id"));
    assertEquals("dev user", row.getString("name");
}复制
```

我们可以看到它会返回我们用*id = 2*插入的数据。

## 4。结论

在这里，我们看到了对 ScyllaDB 的快速介绍，学习了如何安装、连接和执行查询，以及我们如何从我们的应用程序中与之交互。
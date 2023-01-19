## 1. 简介

今天的应用程序有时需要一个副本数据库、一个搜索索引来执行搜索操作、一个缓存存储来加速数据读取，以及一个数据仓库来对数据进行复杂的分析。

支持不同数据模型和数据访问模式的需求提出了一个大多数软件 Web 开发人员需要解决的常见问题，而这正是变更数据捕获 (CDC) 来拯救的时候！

在本文中，我们将从 CDC 的简要概述开始，我们将重点介绍Debezium，这是 CDC 常用的平台。

## 2. 什么是疾控中心？

在本节中，我们将了解什么是 CDC、使用它的主要好处以及一些常见用例。

### 2.1. 更改数据捕获

变更数据捕获 (CDC) 是一种技术和设计模式。我们经常使用它在数据库之间实时数据。

我们还可以跟踪写入源数据库的数据更改并自动同步目标数据库。CDC 支持增量加载并消除了批量加载更新的需要。

### 2.2. 中华网的优势

今天，大多数公司仍在使用批处理来同步其系统之间的数据。使用批处理：

-   数据不会立即同步
-   更多分配的资源用于同步数据库
-   数据仅在指定的批处理期间发生

但是，更改数据捕获具有一些优势：

-   持续跟踪源数据库中的变化
-   即时更新目标数据库
-   使用流处理来保证即时更改

有了 CDC，不同的数据库不断同步，批量选择成为过去。此外， 由于 CDC 仅传输增量更改，因此降低了传输数据的成本。

### 2.3. CDC 常见用例

CDC 可以帮助我们解决各种用例，例如通过保持不同数据源同步进行数据、更新或使缓存失效、更新搜索索引、微服务中的数据同步等等。

现在我们对 CDC 的功能有了一些了解，让我们看看它是如何在一个著名的开源工具中实现的。

## 3. Debezium 平台

在本节中，我们将介绍[Debezium](https://debezium.io/)，详细了解其架构，并了解部署它的不同方式。

### 3.1. 什么是 Debezium？

[Debezium 是建立在Apache Kafka](https://kafka.apache.org/)之上的 CDC 开源平台。它的主要用途是在事务日志中记录提交给每个源数据库表的所有行级更改。每个侦听这些事件的应用程序都可以根据增量数据更改执行所需的操作。

Debezium 提供了一个连接器库，支持多种数据库，如 MySQL、MongoDB、PostgreSQL 等。

这些连接器可以监视和记录数据库更改并将它们发布到像 Kafka 这样的流媒体服务。

此外，即使我们的应用程序出现故障，Debezium 也会进行监控。重新启动后，它会从中断处开始处理事件，因此不会遗漏任何内容。

### 3.2. Debezium架构

部署 Debezium 取决于我们拥有的基础设施，但更常见的是，我们经常使用 Apache Kafka Connect。

Kafka Connect 是一个框架，与 Kafka 代理一起作为单独的服务运行。我们用它在 Apache Kafka 和其他系统之间传输数据。

我们还可以定义连接器来将数据传入和传出 Kafka。

下图显示了基于 Debezium 的变更数据捕获管道的不同部分：

[![Debezium 平台架构](https://www.baeldung.com/wp-content/uploads/2021/04/simple-app-debezium-embedded-arch-1.png)](https://www.baeldung.com/wp-content/uploads/2021/04/simple-app-debezium-embedded-arch-1.png)

首先，在左侧，我们有一个 MySQL 源数据库，我们要其数据并在目标数据库(如 PostgreSQL 或任何分析数据库)中使用。

其次，[Kafka Connect 连接器](https://www.baeldung.com/kafka-connectors-guide)解析和解释事务日志并将其写入 Kafka 主题。

接下来，Kafka 充当消息代理，将变更集可靠地传输到目标系统。

然后，在右边，我们有 Kafka 连接器轮询 Kafka 并将更改推送到目标数据库。

Debezium 在其架构中使用了 Kafka，但它也提供了其他部署方法来满足我们的基础架构需求。

我们可以将它作为一个独立的服务器与 Debezium 服务器一起使用，或者我们可以将它作为一个库嵌入到我们的应用程序代码中。

我们将在以下部分中看到这些方法。

### 3.3. Debezium 服务器

Debezium 提供了一个独立的服务器来捕获源数据库的变化。它被配置为使用 Debezium 源连接器之一。

此外，这些连接器将更改事件发送到各种消息传递基础设施，如 Amazon Kinesis 或 Google Cloud Pub/Sub。

### 3.4. 嵌入式Debezium

Kafka Connect 在用于部署 Debezium 时提供容错和可扩展性。然而，有时我们的应用程序不需要那种级别的可靠性，而我们希望将基础架构的成本降至最低。

值得庆幸的是，我们可以通过在我们的应用程序中嵌入 Debezium 引擎来做到这一点。这样做之后，我们必须配置连接器。

## 4.设置

在本节中，我们首先从应用程序的架构开始。然后，我们将了解如何设置我们的环境并遵循一些基本步骤来集成 Debezium。

让我们首先介绍我们的应用程序。

### 4.1. 示例应用程序的架构

为了使我们的应用程序简单，我们将创建一个用于客户管理的Spring Boot应用程序。

我们的客户模型有ID、全名和电子邮件 字段。对于数据访问层，我们将使用[Spring Data JPA](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)。

最重要的是，我们的应用程序将运行 Debezium 的嵌入式版本。让我们想象一下这个应用程序架构：

[![Springboot Debezium 嵌入式集成](https://www.baeldung.com/wp-content/uploads/2021/04/simple-app-debezium-embedded-arch-1.png)](https://www.baeldung.com/wp-content/uploads/2021/04/simple-app-debezium-embedded-arch-1.png)

首先，Debezium 引擎将在源 MySQL 数据库(来自另一个系统或应用程序)上跟踪客户表的事务日志。

其次，每当我们对客户表执行插入/更新/删除等数据库操作时，Debezium 连接器都会调用服务方法。

最后，基于这些事件，该方法会将客户表的数据同步到目标 MySQL 数据库(我们应用程序的主数据库)。

### 4.2. Maven 依赖项

让我们开始吧，首先将[所需的依赖](https://search.maven.org/classic/#search|ga|1|g%3A"io.debezium" AND (a%3A"debezium-api" OR a%3A"debezium-embedded"))项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>io.debezium</groupId>
    <artifactId>debezium-api</artifactId>
    <version>1.4.2.Final</version>
</dependency>
<dependency>
    <groupId>io.debezium</groupId>
    <artifactId>debezium-embedded</artifactId>
    <version>1.4.2.Final</version>
</dependency>
```

同样，我们为我们的应用程序将使用的每个 Debezium 连接器添加依赖项。

在我们的例子中，我们将使用[MySQL 连接器](https://search.maven.org/classic/#search|ga|1|g%3A"io.debezium"  AND a%3A"debezium-connector-mysql")：

```xml
<dependency>
    <groupId>io.debezium</groupId>
    <artifactId>debezium-connector-mysql</artifactId>
    <version>1.4.2.Final</version>
</dependency>
```

### 4.3. 安装数据库

我们可以手动安装和配置我们的数据库。但是，为了加快速度，我们将使用[docker-compose](https://www.baeldung.com/docker-compose)文件：

```yaml
version: "3.9"
services:
  # Install Source MySQL DB and setup the Customer database
  mysql-1:
    container_name: source-database
    image: mysql
    ports:
      - 3305:3306
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_USER: user
      MYSQL_PASSWORD: password
      MYSQL_DATABASE: customerdb

  # Install Target MySQL DB and setup the Customer database
  mysql-2:
    container_name: target-database
    image: mysql
    ports:
      - 3306:3306
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_USER: user
      MYSQL_PASSWORD: password
      MYSQL_DATABASE: customerdb
```

该文件将在不同端口上运行两个数据库实例。

我们可以使用命令docker-compose up -d运行这个文件。

现在，让我们通过运行 SQL 脚本来创建 c ustomer表：

```sql
CREATE TABLE customer
(
    id integer NOT NULL,
    fullname character varying(255),
    email character varying(255),
    CONSTRAINT customer_pkey PRIMARY KEY (id)
);
```

## 5.配置

在本节中，我们将配置 Debezium MySQL 连接器并了解如何运行嵌入式 Debezium 引擎。

### 5.1. 配置 Debezium 连接器

要配置我们的 Debezium MySQL 连接器，我们将创建一个 Debezium 配置 bean：

```java
@Bean
public io.debezium.config.Configuration customerConnector() {
    return io.debezium.config.Configuration.create()
        .with("name", "customer-mysql-connector")
        .with("connector.class", "io.debezium.connector.mysql.MySqlConnector")
        .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
        .with("offset.storage.file.filename", "/tmp/offsets.dat")
        .with("offset.flush.interval.ms", "60000")
        .with("database.hostname", customerDbHost)
        .with("database.port", customerDbPort)
        .with("database.user", customerDbUsername)
        .with("database.password", customerDbPassword)
        .with("database.dbname", customerDbName)
        .with("database.include.list", customerDbName)
        .with("include.schema.changes", "false")
        .with("database.server.id", "10181")
        .with("database.server.name", "customer-mysql-db-server")
        .with("database.history", "io.debezium.relational.history.FileDatabaseHistory")
        .with("database.history.file.filename", "/tmp/dbhistory.dat")
        .build();
}
```

让我们更详细地研究一下这个配置。

此 bean 中的 create 方法使用构建器来创建Properties对象。

无论首选连接器如何，此构建器都会设置[引擎所需的](https://debezium.io/documentation/reference/1.4/development/engine.html#engine-properties)几个 属性。为了跟踪源 MySQL 数据库，我们使用类MySqlConnector。

当这个连接器运行时，它开始跟踪来自源的变化并记录“偏移量”以确定 它从事务日志中处理了多少数据。

有几种方法可以保存这些偏移量，但在本例中，我们将使用类 FileOffsetBackingStore将偏移量存储在我们的本地文件系统上。

连接器的最后几个参数是 MySQL 数据库属性。

现在我们有了配置，我们可以创建我们的引擎了。

### 5.2. 运行 Debezium 引擎

DebeziumEngine 充当 我们 MySQL 连接器的包装器。让我们使用连接器配置创建引擎：

```java
private DebeziumEngine<RecordChangeEvent<SourceRecord>> debeziumEngine;

public DebeziumListener(Configuration customerConnectorConfiguration, CustomerService customerService) {

    this.debeziumEngine = DebeziumEngine.create(ChangeEventFormat.of(Connect.class))
      .using(customerConnectorConfiguration.asProperties())
      .notifying(this::handleEvent)
      .build();

    this.customerService = customerService;
}
```

更重要的是，引擎将为每次数据更改调用一个方法——在我们的示例中为 handleChangeEvent。

在这个方法中，首先，我们将根据调用 create() 时指定的格式解析每个事件。

然后，我们找到我们有哪些操作并调用 CustomerService以在我们的目标数据库上执行创建/更新/删除功能：

```dart
private void handleChangeEvent(RecordChangeEvent<SourceRecord> sourceRecordRecordChangeEvent) {
    SourceRecord sourceRecord = sourceRecordRecordChangeEvent.record();
    Struct sourceRecordChangeValue= (Struct) sourceRecord.value();

    if (sourceRecordChangeValue != null) {
        Operation operation = Operation.forCode((String) sourceRecordChangeValue.get(OPERATION));

        if(operation != Operation.READ) {
            String record = operation == Operation.DELETE ? BEFORE : AFTER;
            Struct struct = (Struct) sourceRecordChangeValue.get(record);
            Map<String, Object> payload = struct.schema().fields().stream()
              .map(Field::name)
              .filter(fieldName -> struct.get(fieldName) != null)
              .map(fieldName -> Pair.of(fieldName, struct.get(fieldName)))
              .collect(toMap(Pair::getKey, Pair::getValue));

            this.customerService.replicateData(payload, operation);
        }
    }
}
```

现在我们已经配置了一个DebeziumEngine对象，让我们使用服务执行器异步启动它：

```java
private final Executor executor = Executors.newSingleThreadExecutor();

@PostConstruct
private void start() {
    this.executor.execute(debeziumEngine);
}

@PreDestroy
private void stop() throws IOException {
    if (this.debeziumEngine != null) {
        this.debeziumEngine.close();
    }
}
```

## 6. Debezium 实战

要查看代码的运行情况，让我们对源数据库的客户表进行一些数据更改。

### 6.1. 插入记录

要向 客户表添加新记录，我们将转到 MySQL shell 并运行：

```sql
INSERT INTO customerdb.customer (id, fullname, email) VALUES (1, 'John Doe', 'jd@example.com')
```

运行此查询后，我们将看到应用程序的相应输出：

```shell
23:57:57.897 [pool-1-thread-1] INFO  c.b.l.d.listener.DebeziumListener - Key = 'Struct{id=1}' value = 'Struct{after=Struct{id=1,fullname=John Doe,email=jd@example.com},source=Struct{version=1.4.2.Final,connector=mysql,name=customer-mysql-db-server,ts_ms=1617746277000,db=customerdb,table=customer,server_id=1,file=binlog.000007,pos=703,row=0,thread=19},op=c,ts_ms=1617746277422}'
Hibernate: insert into customer (email, fullname, id) values (?, ?, ?)
23:57:58.095 [pool-1-thread-1] INFO  c.b.l.d.listener.DebeziumListener - Updated Data: {fullname=John Doe, id=1, email=jd@example.com} with Operation: CREATE
```

最后，我们检查一条新记录是否插入到我们的目标数据库中：

```shell
id  fullname   email
1  John Doe   jd@example.com
```

### 6.2. 更新记录

现在，让我们尝试更新我们最后插入的客户并检查会发生什么：

```sql
UPDATE customerdb.customer t SET t.email = 'john.doe@example.com' WHERE t.id = 1
```

之后，我们将获得与插入相同的输出，除了操作类型更改为“更新”，当然，Hibernate 使用的查询是“更新”查询：

```shell
00:08:57.893 [pool-1-thread-1] INFO  c.b.l.d.listener.DebeziumListener - Key = 'Struct{id=1}' value = 'Struct{before=Struct{id=1,fullname=John Doe,email=jd@example.com},after=Struct{id=1,fullname=John Doe,email=john.doe@example.com},source=Struct{version=1.4.2.Final,connector=mysql,name=customer-mysql-db-server,ts_ms=1617746937000,db=customerdb,table=customer,server_id=1,file=binlog.000007,pos=1040,row=0,thread=19},op=u,ts_ms=1617746937703}'
Hibernate: update customer set email=?, fullname=? where id=?
00:08:57.938 [pool-1-thread-1] INFO  c.b.l.d.listener.DebeziumListener - Updated Data: {fullname=John Doe, id=1, email=john.doe@example.com} with Operation: UPDATE
```

我们可以验证 John 的电子邮件已在我们的目标数据库中更改：

```shell
id  fullname   email
1  John Doe   john.doe@example.com
```

### 6.3. 删除记录

现在，我们可以 通过执行以下命令删除客户表中的条目：

```sql
DELETE FROM customerdb.customer WHERE id = 1
```

同样，这里我们换个操作，再次查询：

```shell
00:12:16.892 [pool-1-thread-1] INFO  c.b.l.d.listener.DebeziumListener - Key = 'Struct{id=1}' value = 'Struct{before=Struct{id=1,fullname=John Doe,email=john.doe@example.com},source=Struct{version=1.4.2.Final,connector=mysql,name=customer-mysql-db-server,ts_ms=1617747136000,db=customerdb,table=customer,server_id=1,file=binlog.000007,pos=1406,row=0,thread=19},op=d,ts_ms=1617747136640}'
Hibernate: delete from customer where id=?
00:12:16.951 [pool-1-thread-1] INFO  c.b.l.d.listener.DebeziumListener - Updated Data: {fullname=John Doe, id=1, email=john.doe@example.com} with Operation: DELETE
```

我们可以验证目标数据库上的数据是否已被删除：

```shell
select  from customerdb.customer where id= 1
0 rows retrieved
```

## 七. 总结

在本文中，我们看到了 CDC 的好处以及它可以解决哪些问题。我们还了解到，如果没有它，我们将只能批量加载数据，这既费时又费钱。

我们还看到了 Debezium，一个优秀的开源平台，可以帮助我们轻松解决 CDC 用例。
## 1. 概述

在使用[Spring Data MongoDB](https://www.baeldung.com/spring-data-mongodb-tutorial)时，我们可能需要记录到比默认级别更高的级别。通常，我们可能需要查看一些附加信息，例如语句执行或查询参数。

在这个简短的教程中，我们将了解如何修改查询的MongoDB日志记录级别。

## 2. 配置MongoDB查询日志记录

[MongoDB Support](https://docs.spring.io/spring-data/mongodb/docs/3.3.1/reference/html/#introduction)提供了[MongoOperations](https://docs.spring.io/spring-data/mongodb/docs/current/api/org/springframework/data/mongodb/core/MongoOperations.html)接口或其主要的[MongoTemplate](https://docs.spring.io/spring-data/mongodb/docs/current/api/org/springframework/data/mongodb/core/MongoTemplate.html)实现来访问数据，因此我们只需要为MongoTemplate类配置debug级别。

**与任何Spring或Java应用程序一样，我们可以使用[日志库](https://www.baeldung.com/logback)并为MongoTemplate定义日志记录级别**。

通常，我们可以在配置文件中写入如下内容：

```xml
<logger name="org.springframework.data.mongodb.core.MongoTemplate" level="DEBUG" />
```

**但是，如果我们运行的是[Spring Boot](https://www.baeldung.com/spring-boot)应用程序，那么我们可以在application.properties文件中进行配置**：

```properties
logging.level.org.springframework.data.mongodb.core.MongoTemplate=DEBUG
```

同样，我们可以使用YAML语法：

```yaml
logging:
    level:
        org:
            springframework:
                data:
                    mongodb:
                        core:
                            MongoTemplate: DEBUG
```

## 3. 日志测试类

首先，让我们创建一个Book类：

```java
@Document(collection = "book")
public class Book {

    @MongoId
    private ObjectId id;
    private String bookName;
    private String authorName;

    // getters and setters
}
```

我们希望创建一个简单的测试类并检查日志。

**为了演示这一点，我们使用[嵌入式MongoDB](https://www.baeldung.com/spring-boot-embedded-mongodb)**：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
<dependency>
    <groupId>de.flapdoodle.embed</groupId>
    <artifactId>de.flapdoodle.embed.mongo</artifactId>
    <version>${embed.mongo.version}</version>
    <scope>test</scope>
</dependency>
```

最后，让我们使用[Spring Boot Test](https://www.baeldung.com/spring-boot-testing)定义我们的测试类：

```java
@SpringBootTest
@TestPropertySource(properties = {"logging.level.org.springframework.data.mongodb.core.MongoTemplate=DEBUG"})
class LoggingUnitTest {

    private static final String CONNECTION_STRING = "mongodb://%s:%d";

    private MongodExecutable mongodExecutable;
    private MongoTemplate mongoTemplate;

    @AfterEach
    void clean() {
        mongodExecutable.stop();
    }

    @BeforeEach
    void setup() throws Exception {
        String ip = "localhost";
        int port = 27017;

        ImmutableMongodConfig mongodbConfig = MongodConfig.builder()
              .version(Version.Main.PRODUCTION)
              .net(new Net(ip, port, Network.localhostIsIPv6()))
              .build();

        MongodStarter starter = MongodStarter.getDefaultInstance();
        mongodExecutable = starter.prepare(mongodbConfig);
        mongodExecutable.start();
        mongoTemplate = new MongoTemplate(MongoClients.create(String.format(CONNECTION_STRING, ip, port)), "test");
    }

    // tests
}
```

## 4. 日志示例

在本节中，我们将定义一些简单的测试用例并显示相关日志以测试最常见的场景，例如查找、插入、更新或聚合Document。

### 4.1 插入

首先，让我们从插入单个文档开始：

```java
Book book = new Book();
book.setBookName("Book");
book.setAuthorName("Author");

mongoTemplate.insert(book);
```

日志显示我们要插入的集合。查找Document时，还会记录id：

```shell
[mongod output] 14:48:22.477 [main] DEBUG [o.s.data.mongodb.core.MongoTemplate] >>> Inserting Document containing fields: [bookName, authorName, _class] in collection: book
...
[mongod output] 14:48:22.477 [main] DEBUG [o.s.data.mongodb.core.MongoTemplate] >>> findOne using query: { "id" : { "$oid" : "638af1368934bd0c0eabefb6"}} fields: Document{{}} for class: class cn.tuyucheng.taketoday.logging.model.Book in collection: book 
14:48:22.482 [main] DEBUG [o.s.data.mongodb.core.MongoTemplate] >>> findOne using query: { "_id" : { "$oid" : "638af1368934bd0c0eabefb6"}} fields: {} in db.collection: test.book 
```

### 4.2 更新

同样，在更新文档时：

```java
Book book = new Book();
book.setBookName("Book");
book.setAuthorName("Author");

mongoTemplate.insert(book);

String authorNameUpdate = "AuthorNameUpdate";

book.setAuthorName(authorNameUpdate);
mongoTemplate.updateFirst(query(where("bookName").is("Book")), update("authorName", authorNameUpdate), Book.class);
```

我们可以在日志中看到实际更新的Document字段：

```shell
[mongod output] 14:52:32.906 [main] DEBUG [o.s.data.mongodb.core.MongoTemplate] >>> Calling update using query: { "bookName" : "Book"} and update: { "$set" : { "authorName" : "AuthorNameUpdate"}} in collection: book 
```

### 4.3 批量插入

让我们添加一个批量插入的示例：

```java
Book book = new Book();
book.setBookName("Book");
book.setAuthorName("Author");

Book book1 = new Book();
book1.setBookName("Book1");
book1.setAuthorName("Author1");

mongoTemplate.insert(Arrays.asList(book, book1), Book.class);
```

我们可以在日志中看到插入的Document的数量：

```shell
[mongod output] 14:53:56.904 [main] DEBUG [o.s.data.mongodb.core.MongoTemplate] >>> Inserting list of Documents containing 2 items
```

### 4.4 删除

另外，我们添加一个删除示例：

```java
Book book = new Book();
book.setBookName("Book");
book.setAuthorName("Author");

mongoTemplate.insert(book);

mongoTemplate.remove(book);
```

我们可以在日志中看到，在这种情况下，是已删除文档的ID：

```shell
[mongod output] 14:55:12.249 [main] DEBUG [o.s.data.mongodb.core.MongoTemplate] >>> Remove using query: { "_id" : { "$oid" : "638af2d0c5ed4725b189ee7b"}} in collection: book.
```

### 4.5 聚合

让我们看一个[聚合](https://www.baeldung.com/spring-data-mongodb-projections-aggregations)的例子。在这种情况下，我们需要定义一个结果类。例如，我们将按作者姓名进行聚合：

```java
public class GroupByAuthor {

    @Id
    private String authorName;
    private int authCount;

    // getters and setters
}
```

接下来，让我们定义一个用于分组的测试用例：

```java
Book book = new Book();
book.setBookName("Book");
book.setAuthorName("Author");

Book book1 = new Book();
book1.setBookName("Book1");
book1.setAuthorName("Author");

Book book2 = new Book();
book2.setBookName("Book2");
book2.setAuthorName("Author");

mongoTemplate.insert(Arrays.asList(book, book1, book2), Book.class);

GroupOperation groupByAuthor = group("authorName")
        .count()
        .as("authCount");

Aggregation aggregation = newAggregation(groupByAuthor);

AggregationResults<GroupByAuthor> aggregationResults = mongoTemplate.aggregate(aggregation, "book", GroupByAuthor.class);
```

我们可以在日志中看到我们聚合了哪些字段以及什么样的聚合管道：

```shell
[mongod output] 14:57:34.611 [main] DEBUG [o.s.data.mongodb.core.MongoTemplate] >>> Executing aggregation: [{ "$group" : { "_id" : "$authorName", "authCount" : { "$sum" : 1}}}] in collection book
```

## 5. 总结

在本文中，我们研究了如何为Spring Data MongoDB启用debug日志记录级别。

我们定义了一些常见的查询方案，并通过运行一些实时测试查看了它们的相关日志。
## 一、简介

在本教程中，我们将讨论如何通过将可尾游标与[Spring Data MongoDB](https://www.baeldung.com/spring-data-mongodb-tutorial)一起使用，将 MongoDB 用作无限数据流。

## 2. 可尾游标

当我们执行查询时，数据库驱动程序会打开一个游标来提供匹配的文档。默认情况下，当客户端读取所有结果时，MongoDB 会自动关闭游标。因此，转向导致有限的数据流。

然而，我们可以使用带尾游标的上限集合，该游标保持打开状态，即使在客户端消耗了所有最初返回的数据之后——形成无限数据流。这种方法对于处理事件流（如聊天消息或股票更新）的应用程序很有用。

Spring Data MongoDB 项目帮助我们利用反应式数据库功能，包括可尾游标。

## 3.设置

为了演示上述功能，我们将实现一个简单的日志计数器应用程序。让我们假设有一些日志聚合器收集所有日志并将其保存到一个中心位置——我们的 MongoDB 上限集合。

首先，我们将使用简单的Log实体：

```java
@Document
public class Log {
    private @Id String id;
    private String service;
    private LogLevel level;
    private String message;
}
```

其次，我们会将日志存储在我们的 MongoDB 上限集合中。[上限集合](https://docs.mongodb.com/manual/core/capped-collections/)是固定大小的集合，它根据插入顺序插入和检索文档。我们可以使用 MongoOperations.createCollection创建它们：

```java
db.createCollection(COLLECTION_NAME, new CreateCollectionOptions()
  .capped(true)
  .sizeInBytes(1024)
  .maxDocuments(5));
```

对于上限集合，我们必须定义 sizeInBytes属性。此外， maxDocuments指定集合可以拥有的最大文档数。一旦达到，旧文档将从集合中删除。

第三，我们将使用适当的[Spring Boot 启动器依赖项](https://search.maven.org/search?q=a:spring-boot-starter-data-mongodb-reactive)：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb-reactive</artifactId>
    <versionId>2.2.2.RELEASE</versionId>
</dependency>
```

## 4. 响应式 Tailable 游标

我们可以使用[命令式](https://www.baeldung.com/spring-data-mongodb-tailable-cursors#messagelistener)和反应式 MongoDB API 使用可尾游标。强烈建议使用反应变体。

让我们使用反应式方法实现WARN级别的日志计数器。我们能够使用ReactiveMongoOperations.tail 方法创建无限流查询。

当新文档到达上限集合并匹配[过滤器查询](https://www.baeldung.com/queries-in-spring-data-mongodb)时，tailable 游标保持打开状态并发出数据——实体的Flux：

```java
private Disposable subscription;

public WarnLogsCounter(ReactiveMongoOperations template) {
    Flux<Log> stream = template.tail(
      query(where("level").is(LogLevel.WARN)), 
      Log.class);
    subscription = stream.subscribe(logEntity -> 
      counter.incrementAndGet()
    );
}
```

一旦具有WARN日志级别的新文档在集合中持久化，订阅者（lambda 表达式）将递增计数器。

最后，我们应该处理订阅以关闭流：

```java
public void close() {
    this.subscription.dispose();
}
```

另外，请注意，如果查询最初没有返回匹配项，tailable 游标可能会失效或无效。换句话说，即使新的持久文档匹配过滤查询，订阅者也无法接收它们。这是 MongoDB 可尾游标的已知限制。在创建 tailable 游标之前，我们必须确保 capped 集合中有匹配的文档。

## 5. 带有响应式存储库的可尾游标

Spring Data 项目为不同的数据存储提供存储库抽象，包括反应版本。

MongoDB 也不例外。 有关详细信息，请查看 [Spring Data Reactive Repositories with MongoDB一文。](https://www.baeldung.com/spring-data-mongodb-reactive)

此外， MongoDB 反应式存储库通过使用@Tailable注释查询方法来支持无限流。我们可以注释任何返回Flux或其他能够发出多个元素的反应类型的存储库方法：

```java
public interface LogsRepository extends ReactiveCrudRepository<Log, String> {
    @Tailable
    Flux<Log> findByLevel(LogLevel level);
}
```

让我们使用这种可跟踪的存储库方法来计算INFO日志：

```java
private Disposable subscription;

public InfoLogsCounter(LogsRepository repository) {
    Flux<Log> stream = repository.findByLevel(LogLevel.INFO);
    this.subscription = stream.subscribe(logEntity -> 
      counter.incrementAndGet()
    );
}
```

同样，对于WarnLogsCounter，我们应该处理订阅以关闭流：

```java
public void close() {
    this.subscription.dispose();
}
```

## 6. 带有MessageListener的 Tailable Cursors

尽管如此，如果我们不能使用响应式 API，我们可以利用 Spring 的消息传递概念。

首先，我们需要创建一个MessageListenerContainer来处理发送的SubscriptionRequest对象。同步 MongoDB 驱动程序创建一个长时间运行的阻塞任务，用于侦听上限集合中的新文档。

Spring Data MongoDB 附带一个默认实现，能够为TailableCursorRequest创建和执行Task实例：

```java
private String collectionName;
private MessageListenerContainer container;
private AtomicInteger counter = new AtomicInteger();

public ErrorLogsCounter(MongoTemplate mongoTemplate,
  String collectionName) {
    this.collectionName = collectionName;
    this.container = new DefaultMessageListenerContainer(mongoTemplate);

    container.start();
    TailableCursorRequest<Log> request = getTailableCursorRequest();
    container.register(request, Log.class);
}

private TailableCursorRequest<Log> getTailableCursorRequest() {
    MessageListener<Document, Log> listener = message -> 
      counter.incrementAndGet();

    return TailableCursorRequest.builder()
      .collection(collectionName)
      .filter(query(where("level").is(LogLevel.ERROR)))
      .publishTo(listener)
      .build();
}
```

TailableCursorRequest创建一个仅过滤ERROR级别日志的查询。每个匹配的文档都将发布到 MessageListener，这将使计数器递增。

请注意，我们仍然需要确保初始查询返回一些结果。否则，tailable 游标将立即关闭。

此外，一旦我们不再需要它，我们不应该忘记停止容器：

```java
public void close() {
    container.stop();
}
```

## 七、结论

带有可尾游标的 MongoDB 上限集合帮助我们以连续的方式从数据库接收信息。我们可以运行一个查询，该查询将一直提供结果，直到明确关闭。Spring Data MongoDB 为我们提供了使用可尾游标的阻塞和反应方式。
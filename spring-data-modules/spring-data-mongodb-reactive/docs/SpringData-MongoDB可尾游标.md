## 1. 简介

在本教程中，我们将讨论如何通过将可尾游标与[Spring Data MongoDB](https://www.baeldung.com/spring-data-mongodb-tutorial)一起使用，将MongoDB用作无限数据流。

## 2. 可尾游标

当我们执行查询时，数据库驱动程序会打开一个游标来提供匹配的文档。默认情况下，当客户端读取所有结果时，MongoDB会自动关闭游标。因此，转换会产生有限的数据流。

但是，**我们可以使用带有可尾游标的上限集合，即使在客户端消费了所有初始返回的数据之后，该游标仍保持打开状态-形成无限数据流**。这种方法对于处理事件流(如聊天消息或股票更新)的应用程序非常有用。

Spring Data MongoDB项目可帮助我们利用响应式数据库功能，包括可尾游标。

## 3. 设置

为了演示上述功能，我们将实现一个简单的日志计数器应用程序。让我们假设有一些日志聚合器收集所有日志并将其保存到一个中心位置-我们的MongoDB上限集合。

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

其次，我们会将日志存储在我们的MongoDB上限集合中。[上限集合](https://docs.mongodb.com/manual/core/capped-collections/)是固定大小的集合，它根据插入顺序插入和检索文档。我们可以使用MongoOperations.createCollection创建它们：

```java
db.createCollection(COLLECTION_NAME, new CreateCollectionOptions()
    .capped(true)
    .sizeInBytes(1024)
    .maxDocuments(5));
```

对于上限集合，我们必须定义sizeInBytes属性。此外，maxDocuments指定集合可以拥有的最大文档数。一旦达到该数量，旧文档将从集合中删除。

第三，我们将使用适当的[Spring Boot启动器依赖项](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-data-mongodb-reactive/3.0.4)：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb-reactive</artifactId>
    <versionId>2.2.2.RELEASE</versionId>
</dependency>
```

## 4. 响应式可尾游标

我们可以使用[命令式](https://www.baeldung.com/spring-data-mongodb-tailable-cursors#messagelistener)和响应式MongoDB API使用可尾游标。强烈建议**使用响应变体**。

让我们使用响应式方法实现WARN级别的日志计数器。**我们能够使用ReactiveMongoOperations.tail方法创建无限流查询**。

当新文档到达上限集合并匹配[过滤器查询](https://www.baeldung.com/queries-in-spring-data-mongodb)时，可尾游标保持打开状态并发出数据-实体的Flux：

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

一旦具有WARN日志级别的新文档在集合中持久化，订阅者(lambda表达式)将递增计数器。

最后，我们应该处理订阅以关闭流：

```java
public void close() {
    this.subscription.dispose();
}
```

另外，**请注意，如果查询最初没有返回匹配项，则可尾游标可能会变为死状态或无效**。换句话说，即使新的持久文档匹配过滤查询，订阅者也无法接收它们。这是MongoDB可尾游标的已知限制。在创建可尾游标之前，我们必须确上限集合中有匹配的文档。

## 5. 使用响应式Repository的可尾游标

Spring Data项目为不同的数据存储提供Repository抽象，包括响应式版本。

MongoDB也不例外。有关详细信息，请查看[Spring Data Reactive Repository与MongoDB](https://www.baeldung.com/spring-data-mongodb-reactive)一文。

此外，**MongoDB响应式Repository通过使用@Tailable标注查询方法来支持无限流**。我们可以标注任何返回Flux或其他能够发出多个元素的响应类型的Repository方法：

```java
public interface LogsRepository extends ReactiveCrudRepository<Log, String> {
    @Tailable
    Flux<Log> findByLevel(LogLevel level);
}
```

让我们使用以下可跟踪的Repository方法来统计INFO日志：

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

## 6. 使用MessageListener的可尾游标

尽管如此，如果我们不能使用响应式API，我们可以利用Spring的消息传递概念。

首先，我们需要创建一个MessageListenerContainer来处理发送的SubscriptionRequest对象。同步MongoDB驱动程序创建一个长时间运行的阻塞任务，该任务用于监听上限集合中的新文档。

**Spring Data MongoDB附带了一个默认实现，能够为TailableCursorRequest创建和执行Task实例**：

```java
private String collectionName;
private MessageListenerContainer container;
private AtomicInteger counter = new AtomicInteger();

public ErrorLogsCounter(MongoTemplate mongoTemplate, String collectionName) {
    this.collectionName = collectionName;
    this.container = new DefaultMessageListenerContainer(mongoTemplate);

    container.start();
    TailableCursorRequest<Log> request = getTailableCursorRequest();
    container.register(request, Log.class);
}

private TailableCursorRequest<Log> getTailableCursorRequest() {
    MessageListener<Document, Log> listener = message -> counter.incrementAndGet();

    return TailableCursorRequest.builder()
        .collection(collectionName)
        .filter(query(where("level").is(LogLevel.ERROR)))
        .publishTo(listener)
        .build();
}
```

TailableCursorRequest创建一个仅过滤ERROR级别日志的查询。每个匹配的文档都将发布到将递增计数器的MessageListener。

请注意，我们仍然需要确保初始查询返回一些结果。否则，可尾游标将立即关闭。

此外，一旦我们不再需要它，我们应该记住停止容器：

```java
public void close() {
    container.stop();
}
```

## 7. 总结

带有可尾游标的MongoDB上限集合帮助我们以连续的方式从数据库接收信息。我们可以运行一个查询，该查询将不断给出结果，直到明确关闭。Spring Data MongoDB为我们提供了使用可尾游标的阻塞和响应方式。
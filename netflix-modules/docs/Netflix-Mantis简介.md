## 1. 概述

在本文中，我们将了解 Netflix 开发的 Mantis 平台。

我们将通过创建、运行和研究流处理作业来探索 Mantis 的主要概念。

## 2. 什么是螳螂？

Mantis 是一个用于构建流处理应用程序(作业)的平台。它提供了一种简单的方法来管理作业的部署和生命周期。此外，它促进了这些作业之间的资源分配、发现和通信。

因此，开发人员可以专注于实际的业务逻辑，同时拥有强大且可扩展的平台的支持来运行他们的高容量、低延迟、无阻塞的应用程序。

Mantis 作业由三个不同的部分组成：

-   source，负责从外部源检索数据
-   一个或多个阶段，负责处理传入的事件流
-   和一个收集处理过的数据的接收器

现在让我们来探索它们中的每一个。

## 3.设置和依赖

让我们从添加[mantis-runtime](https://search.maven.org/classic/#search|ga|1|g%3A"io.mantisrx" a%3A"mantis-runtime")和[jackson-databind](https://search.maven.org/classic/#search|ga|1|g%3A"com.fasterxml.jackson.core" a%3A"jackson-databind")依赖项开始：

```xml
<dependency>
    <groupId>io.mantisrx</groupId>
    <artifactId>mantis-runtime</artifactId>
</dependency>

<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>
```

现在，为了设置作业的数据源，让我们实现 Mantis Source接口：

```java
public class RandomLogSource implements Source<String> {

    @Override
    public Observable<Observable<String>> call(Context context, Index index) {
        return Observable.just(
          Observable
            .interval(250, TimeUnit.MILLISECONDS)
            .map(this::createRandomLogEvent));
    }

    private String createRandomLogEvent(Long tick) {
        // generate a random log entry string
        ...
    }

}
```

正如我们所见，它只是每秒多次生成随机日志条目。

## 4. 我们的第一份工作

现在让我们创建一个 Mantis 作业，它只从我们的RandomLogSource收集日志事件。稍后，我们将添加组和聚合转换以获得更复杂和有趣的结果。

首先，让我们创建一个LogEvent实体：

```java
public class LogEvent implements JsonType {
    private Long index;
    private String level;
    private String message;

    // ...
}
```

然后，让我们添加我们的TransformLogStage。

这是一个实现 ScalarComputation 接口并拆分日志条目以构建LogEvent的简单阶段。此外，它还会过滤掉任何格式错误的字符串：

```java
public class TransformLogStage implements ScalarComputation<String, LogEvent> {

    @Override
    public Observable<LogEvent> call(Context context, Observable<String> logEntry) {
        return logEntry
          .map(log -> log.split("#"))
          .filter(parts -> parts.length == 3)
          .map(LogEvent::new);
    }

}
```

### 4.1. 运行作业

在这一点上，我们有足够的构建块来组装我们的 Mantis 作业：

```java
public class LogCollectingJob extends MantisJobProvider<LogEvent> {

    @Override
    public Job<LogEvent> getJobInstance() {
        return MantisJob
          .source(new RandomLogSource())
          .stage(new TransformLogStage(), new ScalarToScalar.Config<>())
          .sink(Sinks.eagerSubscribe(Sinks.sse(LogEvent::toJsonString)))
          .metadata(new Metadata.Builder().build())
          .create();
    }

}
```

让我们仔细看看我们的工作。

如我们所见，它扩展了 MantisJobProvider。首先，它从我们的RandomLogSource获取数据并将TransformLogStage应用于获取的数据。最后，它将处理后的数据发送到内置的接收器，该接收器通过[SSE](https://en.wikipedia.org/wiki/Server-sent_events)热切地订阅和传递数据。

现在，让我们将作业配置为在启动时在本地执行：

```java
@SpringBootApplication
public class MantisApplication implements CommandLineRunner {

    // ...
 
    @Override
    public void run(String... args) {
        LocalJobExecutorNetworked.execute(new LogCollectingJob().getJobInstance());
    }
}
```

让我们运行应用程序。我们会看到如下日志消息：

```bash
...
Serving modern HTTP SSE server sink on port: 86XX
```

现在让我们使用curl连接到接收器：

```bash
$ curl localhost:86XX
data: {"index":86,"level":"WARN","message":"login attempt"}
data: {"index":87,"level":"ERROR","message":"user created"}
data: {"index":88,"level":"INFO","message":"user created"}
data: {"index":89,"level":"INFO","message":"login attempt"}
data: {"index":90,"level":"INFO","message":"user created"}
data: {"index":91,"level":"ERROR","message":"user created"}
data: {"index":92,"level":"WARN","message":"login attempt"}
data: {"index":93,"level":"INFO","message":"user created"}
...
```

### 4.2. 配置接收器

到目前为止，我们已经使用内置接收器来收集我们处理过的数据。让我们看看是否可以通过提供自定义接收器来为我们的方案增加更多的灵活性。

例如，如果我们想按消息过滤日志怎么办？

让我们创建一个实现Sink<LogEvent>接口的LogSink ：

```java
public class LogSink implements Sink<LogEvent> {
    @Override
    public void call(Context context, PortRequest portRequest, Observable<LogEvent> logEventObservable) {
        SelfDocumentingSink<LogEvent> sink = new ServerSentEventsSink.Builder<LogEvent>()
          .withEncoder(LogEvent::toJsonString)
          .withPredicate(filterByLogMessage())
          .build();
        logEventObservable.subscribe();
        sink.call(context, portRequest, logEventObservable);
    }
    private Predicate<LogEvent> filterByLogMessage() {
        return new Predicate<>("filter by message",
          parameters -> {
            if (parameters != null && parameters.containsKey("filter")) {
                return logEvent -> logEvent.getMessage().contains(parameters.get("filter").get(0));
            }
            return logEvent -> true;
        });
    }
}
```

在此接收器实现中，我们配置了一个谓词，该谓词使用过滤器参数仅检索包含过滤器参数中设置的文本的日志：

```bash
$ curl localhost:8874?filter=login
data: {"index":93,"level":"ERROR","message":"login attempt"}
data: {"index":95,"level":"INFO","message":"login attempt"}
data: {"index":97,"level":"ERROR","message":"login attempt"}
...
```

注意 Mantis 还提供了一种强大的查询语言[MQL](https://netflix.github.io/mantis/reference/mql/)，可用于以 SQL 方式查询、转换和分析流数据。

## 5.阶段链接

现在假设我们有兴趣了解在给定时间间隔内我们有多少ERROR、WARN或INFO日志条目。为此，我们将在我们的工作中再添加两个阶段并将它们链接在一起。

### 5.1. 分组

首先，让我们创建一个GroupLogStage。

此阶段是一个ToGroupComputation实现，它从现有的TransformLogStage接收LogEvent流数据。之后，它按日志级别对条目进行分组并将它们发送到下一阶段：

```java
public class GroupLogStage implements ToGroupComputation<LogEvent, String, LogEvent> {

    @Override
    public Observable<MantisGroup<String, LogEvent>> call(Context context, Observable<LogEvent> logEvent) {
        return logEvent.map(log -> new MantisGroup<>(log.getLevel(), log));
    }

    public static ScalarToGroup.Config<LogEvent, String, LogEvent> config(){
        return new ScalarToGroup.Config<LogEvent, String, LogEvent>()
          .description("Group event data by level")
          .codec(JacksonCodecs.pojo(LogEvent.class))
          .concurrentInput();
    }
    
}
```

我们还通过提供描述、用于序列化输出的编解码器创建了自定义阶段配置，并允许该阶段的调用方法通过使用 concurrentInput() 并发运行。

需要注意的一件事是，这个阶段是水平可扩展的。这意味着我们可以根据需要运行这个阶段的任意多个实例。另外值得一提的是，当部署在 Mantis 集群中时，这个阶段会将数据发送到下一阶段，这样属于特定组的所有事件都会落在下一阶段的同一个 worker 上。

### 5.2. 聚合

在我们继续创建下一阶段之前，让我们先添加一个LogAggregate实体：

```java
public class LogAggregate implements JsonType {

    private final Integer count;
    private final String level;

}
```

现在，让我们创建链中的最后一个阶段。

此阶段实施GroupToScalarComputation并将日志组流转换为标量LogAggregate。它通过计算每种类型的日志在流中出现的次数来实现。另外，它还有一个LogAggregationDuration参数，可以用来控制聚合窗口的大小：

```java
public class CountLogStage implements GroupToScalarComputation<String, LogEvent, LogAggregate> {

    private int duration;

    @Override
    public void init(Context context) {
        duration = (int)context.getParameters().get("LogAggregationDuration", 1000);
    }

    @Override
    public Observable<LogAggregate> call(Context context, Observable<MantisGroup<String, LogEvent>> mantisGroup) {
        return mantisGroup
          .window(duration, TimeUnit.MILLISECONDS)
          .flatMap(o -> o.groupBy(MantisGroup::getKeyValue)
            .flatMap(group -> group.reduce(0, (count, value) ->  count = count + 1)
              .map((count) -> new LogAggregate(count, group.getKey()))
            ));
    }

    public static GroupToScalar.Config<String, LogEvent, LogAggregate> config(){
        return new GroupToScalar.Config<String, LogEvent, LogAggregate>()
          .description("sum events for a log level")
          .codec(JacksonCodecs.pojo(LogAggregate.class))
          .withParameters(getParameters());
    }

    public static List<ParameterDefinition<?>> getParameters() {
        List<ParameterDefinition<?>> params = new ArrayList<>();

        params.add(new IntParameter()
          .name("LogAggregationDuration")
          .description("window size for aggregation in milliseconds")
          .validator(Validators.range(100, 10000))
          .defaultValue(5000)
          .build());

        return params;
    }
    
}
```

### 5.3. 配置和运行作业

现在唯一要做的就是配置我们的工作：

```java
public class LogAggregationJob extends MantisJobProvider<LogAggregate> {

    @Override
    public Job<LogAggregate> getJobInstance() {

        return MantisJob
          .source(new RandomLogSource())
          .stage(new TransformLogStage(), TransformLogStage.stageConfig())
          .stage(new GroupLogStage(), GroupLogStage.config())
          .stage(new CountLogStage(), CountLogStage.config())
          .sink(Sinks.eagerSubscribe(Sinks.sse(LogAggregate::toJsonString)))
          .metadata(new Metadata.Builder().build())
          .create();
    }
}
```

一旦我们运行应用程序并执行我们的新作业，我们就可以看到每隔几秒检索一次日志计数：

```bash
$ curl localhost:8133
data: {"count":3,"level":"ERROR"}
data: {"count":13,"level":"INFO"}
data: {"count":4,"level":"WARN"}

data: {"count":8,"level":"ERROR"}
data: {"count":5,"level":"INFO"}
data: {"count":7,"level":"WARN"}
...
```

## 六. 总结

总而言之，在本文中，我们了解了 Netflix Mantis 是什么以及它的用途。此外，我们研究了主要概念，使用它们来构建作业，并探索了不同场景的自定义配置。
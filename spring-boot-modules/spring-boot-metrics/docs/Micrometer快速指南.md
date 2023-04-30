## 1. 简介

[Micrometer](https://github.com/micrometer-metrics/micrometer-docs)为许多流行的监控系统提供了一个简单的仪表客户端。目前支持以下监控系统：Atlas、Datadog、Graphite、Ganglia、Influx、JMX、Prometheus。

在本教程中，我们将介绍 Micrometer 的基本用法及其与 Spring 的集成。

为了简单起见，我们将以 Micrometer Atlas 为例来演示我们的大部分用例。

## 2.Maven依赖

首先，让我们将以下依赖项添加到pom.xml：

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-atlas</artifactId>
    <version>1.7.1</version>
</dependency>
```

最新版本可以在[这里](https://search.maven.org/classic/#search|ga|1|a%3A"micrometer-registry-atlas" AND g%3A "io.micrometer")找到。

## 3.MeterRegistry _

在 Micrometer 中，[MeterRegistry](https://github.com/micrometer-metrics/micrometer/blob/master/micrometer-core/src/main/java/io/micrometer/core/instrument/MeterRegistry.java#L41)是用于注册仪表的核心组件。我们可以遍历注册表并进一步遍历每个仪表的指标，以在后端生成一个时间序列，其中包含指标及其维度值的组合。

注册表的最简单形式是[SimpleMeterRegistry](https://github.com/micrometer-metrics/micrometer/blob/master/micrometer-core/src/main/java/io/micrometer/core/instrument/simple/SimpleMeterRegistry.java#L31)。但是，在大多数情况下，我们应该使用明确为我们的监控系统设计的[MeterRegistry ；](https://github.com/micrometer-metrics/micrometer/blob/master/micrometer-core/src/main/java/io/micrometer/core/instrument/MeterRegistry.java#L41)对于 Atlas，它是[AtlasMeterRegistry](https://github.com/micrometer-metrics/micrometer/blob/master/implementations/micrometer-registry-atlas/src/main/java/io/micrometer/atlas/AtlasMeterRegistry.java#L36)。

[CompositeMeterRegistry](https://github.com/micrometer-metrics/micrometer/blob/master/micrometer-core/src/main/java/io/micrometer/core/instrument/composite/CompositeMeterRegistry.java#L37)允许添加多个注册表。它提供了一种将应用程序指标同时发布到各种受支持的监控系统的解决方案。

我们可以添加将数据上传到多个平台所需的任何MeterRegistry ：

```java
CompositeMeterRegistry compositeRegistry = new CompositeMeterRegistry();
SimpleMeterRegistry oneSimpleMeter = new SimpleMeterRegistry();
AtlasMeterRegistry atlasMeterRegistry 
  = new AtlasMeterRegistry(atlasConfig, Clock.SYSTEM);

compositeRegistry.add(oneSimpleMeter);
compositeRegistry.add(atlasMeterRegistry);
```

[Micrometer Metrics.globalRegistry](https://github.com/micrometer-metrics/micrometer/blob/master/micrometer-core/src/main/java/io/micrometer/core/instrument/Metrics.java#L31)中有静态全局注册表支持。此外，还提供了一组基于此全局注册表的静态构建器，用于在[Metrics](https://github.com/micrometer-metrics/micrometer/blob/master/micrometer-core/src/main/java/io/micrometer/core/instrument/Metrics.java#L30)中生成计量表：

```java
@Test
public void givenGlobalRegistry_whenIncrementAnywhere_thenCounted() {
    class CountedObject {
        private CountedObject() {
            Metrics.counter("objects.instance").increment(1.0);
        }
    }
    Metrics.addRegistry(new SimpleMeterRegistry());

    Metrics.counter("objects.instance").increment();
    new CountedObject();

    Optional<Counter> counterOptional = Optional.ofNullable(Metrics.globalRegistry
      .find("objects.instance").counter());
    assertTrue(counterOptional.isPresent());
    assertTrue(counterOptional.get().count() == 2.0);
}
```

## 4.标签和仪表

### 4.1. 标签

[Meter](https://github.com/micrometer-metrics/micrometer/blob/master/micrometer-core/src/main/java/io/micrometer/core/instrument/Meter.java#L24)的标识符由名称和标签组成。我们应该遵循用点分隔单词的命名约定，以帮助保证指标名称在多个监控系统之间的可移植性。

```java
Counter counter = registry.counter("page.visitors", "age", "20s");
```

[标签](https://github.com/micrometer-metrics/micrometer/blob/master/micrometer-core/src/main/java/io/micrometer/core/instrument/Tag.java#L23)可用于对度量进行切片以对值进行推理。在上面的代码中， page.visitors是计量器的名称， age=20s作为它的标签。在这种情况下，计数器计算的是年龄在 20 到 30 岁之间的页面访问者。

对于大型系统，我们可以将公共标签附加到注册表中。例如，假设指标来自特定区域：

```java
registry.config().commonTags("region", "ua-east");
```

### 4.2. 柜台

[计数器](https://github.com/micrometer-metrics/micrometer/blob/master/micrometer-core/src/main/java/io/micrometer/core/instrument/Counter.java#L25)仅报告对应用程序指定属性的计数。我们可以使用 fluent builder 或任何MetricRegistry的辅助方法来构建自定义计数器：

```java
Counter counter = Counter
  .builder("instance")
  .description("indicates instance count of the object")
  .tags("dev", "performance")
  .register(registry);

counter.increment(2.0);
 
assertTrue(counter.count() == 2);
 
counter.increment(-1);
 
assertTrue(counter.count() == 1);
```

如上面的代码片段所示，我们试图将计数器减一，但我们只能将计数器单调递增一个固定的正数。

### 4.3. 计时器

为了测量我们系统中事件的延迟或频率，我们可以使用[Timers](https://github.com/micrometer-metrics/micrometer/blob/master/micrometer-core/src/main/java/io/micrometer/core/instrument/Timer.java#L34)。计时器将至少报告特定时间序列的总时间和事件计数。

例如，我们可以记录一个可能持续数秒的应用程序事件：

```java
SimpleMeterRegistry registry = new SimpleMeterRegistry();
Timer timer = registry.timer("app.event");
timer.record(() -> {
    try {
        TimeUnit.MILLISECONDS.sleep(15);
    } catch (InterruptedException ignored) {
    }
    });

timer.record(30, TimeUnit.MILLISECONDS);

assertTrue(2 == timer.count());
assertThat(timer.totalTime(TimeUnit.MILLISECONDS)).isBetween(40.0, 55.0);
```

为了记录长时间运行的事件，我们使用[LongTaskTimer](https://github.com/micrometer-metrics/micrometer/blob/master/micrometer-core/src/main/java/io/micrometer/core/instrument/LongTaskTimer.java#L26)：

```java
SimpleMeterRegistry registry = new SimpleMeterRegistry();
LongTaskTimer longTaskTimer = LongTaskTimer
  .builder("3rdPartyService")
  .register(registry);

LongTaskTimer.Sample currentTaskId = longTaskTimer.start();
try {
    TimeUnit.MILLISECONDS.sleep(2);
} catch (InterruptedException ignored) { }
long timeElapsed = currentTaskId.stop();
 
 assertEquals(2L, timeElapsed/((int) 1e6),1L);
```

### 4.4. 测量

仪表显示仪表的当前值。

与其他仪表不同，[仪表](https://github.com/micrometer-metrics/micrometer/blob/master/micrometer-core/src/main/java/io/micrometer/core/instrument/Gauge.java#L23)只应在观察时报告数据。在监视缓存或集合的统计信息时，仪表很有用：

```java
SimpleMeterRegistry registry = new SimpleMeterRegistry();
List<String> list = new ArrayList<>(4);

Gauge gauge = Gauge
  .builder("cache.size", list, List::size)
  .register(registry);

assertTrue(gauge.value() == 0.0);
 
list.add("1");
 
assertTrue(gauge.value() == 1.0);
```

### 4.5. 分布概要

DistributionSummary 提供事件分布和简单[摘要](https://github.com/micrometer-metrics/micrometer/blob/master/micrometer-core/src/main/java/io/micrometer/core/instrument/DistributionSummary.java#L29)：

```java
SimpleMeterRegistry registry = new SimpleMeterRegistry();
DistributionSummary distributionSummary = DistributionSummary
  .builder("request.size")
  .baseUnit("bytes")
  .register(registry);

distributionSummary.record(3);
distributionSummary.record(4);
distributionSummary.record(5);

assertTrue(3 == distributionSummary.count());
assertTrue(12 == distributionSummary.totalAmount());
```

此外，DistributionSummary和Timers可以通过百分位数来丰富：

```java
SimpleMeterRegistry registry = new SimpleMeterRegistry();
Timer timer = Timer
  .builder("test.timer")
  .publishPercentiles(0.3, 0.5, 0.95)
  .publishPercentileHistogram()
  .register(registry);
```

现在，在上面的代码片段中，三个带有标签percentile=0.3、percentile =0.5和percentile =0.95的仪表 将在注册表中可用，分别指示 95%、50% 和 30% 的观测值低于该值.

因此，要查看这些百分位数的实际情况，让我们添加一些记录：

```java
timer.record(2, TimeUnit.SECONDS);
timer.record(2, TimeUnit.SECONDS);
timer.record(3, TimeUnit.SECONDS);
timer.record(4, TimeUnit.SECONDS);
timer.record(8, TimeUnit.SECONDS);
timer.record(13, TimeUnit.SECONDS);
```

然后我们可以通过提取这三个百分位数Gauges中的值来验证：

```java
Map<Double, Double> actualMicrometer = new TreeMap<>();
ValueAtPercentile[] percentiles = timer.takeSnapshot().percentileValues();
for (ValueAtPercentile percentile : percentiles) {
    actualMicrometer.put(percentile.percentile(), percentile.value(TimeUnit.MILLISECONDS));
}

Map<Double, Double> expectedMicrometer = new TreeMap<>();
expectedMicrometer.put(0.3, 1946.157056);
expectedMicrometer.put(0.5, 3019.89888);
expectedMicrometer.put(0.95, 13354.663936);

assertEquals(expectedMicrometer, actualMicrometer);
```

此外，Micrometer 还支持[服务级别目标](https://en.wikipedia.org/wiki/Service-level_objective)(直方图)：

```java
DistributionSummary hist = DistributionSummary
  .builder("summary")
  .serviceLevelObjectives(1, 10, 5)
  .register(registry);
```

与百分位数类似，在追加几条记录后，我们可以看到直方图可以很好地处理计算：

```java
Map<Integer, Double> actualMicrometer = new TreeMap<>();
HistogramSnapshot snapshot = hist.takeSnapshot();
Arrays.stream(snapshot.histogramCounts()).forEach(p -> {
  actualMicrometer.put((Integer.valueOf((int) p.bucket())), p.count());
});

Map<Integer, Double> expectedMicrometer = new TreeMap<>();
expectedMicrometer.put(1,0D);
expectedMicrometer.put(10,2D);
expectedMicrometer.put(5,1D);

assertEquals(expectedMicrometer, actualMicrometer);

```

通常，直方图可以帮助说明不同桶中的直接比较。直方图还可以按时间缩放，这对于分析后端服务响应时间非常有用：

```java
Duration[] durations = {Duration.ofMillis(25), Duration.ofMillis(300), Duration.ofMillis(600)};
Timer timer = Timer
  .builder("timer")
  .sla(durations)
  .publishPercentileHistogram()
  .register(registry);
```

## 5.粘合剂

Micrometer 有多个内置的绑定器来监控 JVM、缓存、ExecutorService和日志服务。

在 JVM 和系统监控方面，我们可以监控类加载器指标 ( [ClassLoaderMetrics](https://github.com/micrometer-metrics/micrometer/blob/main/micrometer-core/src/main/java/io/micrometer/core/instrument/binder/jvm/ClassLoaderMetrics.java) )、JVM 内存池 ( [JvmMemoryMetrics](https://github.com/micrometer-metrics/micrometer/blob/main/micrometer-core/src/main/java/io/micrometer/core/instrument/binder/jvm/JvmMemoryMetrics.java) ) 和 GC 指标 ( [JvmGcMetrics](https://github.com/micrometer-metrics/micrometer/blob/main/micrometer-core/src/main/java/io/micrometer/core/instrument/binder/jvm/JvmGcMetrics.java) )，以及线程和 CPU 利用率([JvmThreadMetrics](https://github.com/micrometer-metrics/micrometer/blob/main/micrometer-core/src/main/java/io/micrometer/core/instrument/binder/jvm/JvmThreadMetrics.java)、[ProcessorMetrics](https://github.com/micrometer-metrics/micrometer/blob/main/micrometer-binders/src/main/java/io/micrometer/binder/system/ProcessorMetrics.java))。

[通过使用GuavaCacheMetrics](https://github.com/micrometer-metrics/micrometer/blob/main/micrometer-core/src/main/java/io/micrometer/core/instrument/binder/cache/GuavaCacheMetrics.java)、[EhCache2Metrics](https://github.com/micrometer-metrics/micrometer/blob/main/micrometer-core/src/main/java/io/micrometer/core/instrument/binder/cache/EhCache2Metrics.java)、[HazelcastCacheMetrics](https://github.com/micrometer-metrics/micrometer/blob/main/micrometer-core/src/main/java/io/micrometer/core/instrument/binder/cache/HazelcastCacheMetrics.java)和CaffeineCacheMetrics 进行检测支持缓存监控(目前仅支持 Guava、EhCache、Hazelcast 和Caffeine [)](https://github.com/micrometer-metrics/micrometer/blob/main/micrometer-core/src/main/java/io/micrometer/core/instrument/binder/cache/CaffeineCacheMetrics.java)。为了监控日志回传服务，我们可以将[LogbackMetrics](https://github.com/micrometer-metrics/micrometer/blob/main/micrometer-core/src/main/java/io/micrometer/core/instrument/binder/logging/LogbackMetrics.java)绑定到任何有效的注册表：

```java
new LogbackMetrics().bind(registry);
```

上述binders的使用与LogbackMetrics非常相似，都比较简单，这里不再赘述。

## 6. 弹簧集成

Spring Boot Actuator 为 Micrometer 提供依赖管理和自动配置。现在它在Spring Boot2.0/1.x 和 Spring Framework 5.0/4.x 中受支持。

我们需要以下依赖项(最新版本可以在[这里](https://search.maven.org/classic/#search|gav|1|g%3A"io.micrometer" AND a%3A"micrometer-spring-legacy")找到)：

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-spring-legacy</artifactId>
    <version>1.3.20</version>
</dependency>
```

在不对现有代码进行任何进一步更改的情况下，我们已经启用了 Micrometer 的 Spring 支持。我们的 Spring 应用程序的 JVM 内存指标将自动注册到全局注册表中，并发布到默认的 atlas 端点：http://localhost:7101/api/v1/publish。

有几个可配置的属性可用于控制指标导出行为，从spring.metrics.atlas.开始。检查[AtlasConfig](https://github.com/Netflix/spectator/blob/master/spectator-reg-atlas/src/main/java/com/netflix/spectator/atlas/AtlasConfig.java)以查看 Atlas 发布的配置属性的完整列表。

如果我们需要绑定更多指标，只需将它们作为@Bean添加到应用程序上下文中即可。

假设我们需要JvmThreadMetrics：

```java
@Bean
JvmThreadMetrics threadMetrics(){
    return new JvmThreadMetrics();
}
```

至于网络监控，它是为我们应用程序中的每个端点自动配置的，但可以通过配置属性spring.metrics.web.autoTimeServerRequests 进行管理。

默认实现为端点提供四个维度的指标：HTTP 请求方法、HTTP 响应代码、端点 URI 和异常信息。

当请求得到响应时，与请求方法(GET、POST等)相关的指标将在 Atlas 中发布。

使用[Atlas Graph API](https://github.com/Netflix/atlas/wiki/Graph)，我们可以生成一个图表来比较不同方法的响应时间：

[![方法](https://www.baeldung.com/wp-content/uploads/2017/10/methods.png)](https://www.baeldung.com/wp-content/uploads/2017/10/methods.png)

默认情况下，还会报告20x、30x、40x、50x的响应代码：

[![地位](https://www.baeldung.com/wp-content/uploads/2017/10/status.png)](https://www.baeldung.com/wp-content/uploads/2017/10/status.png)

我们还可以比较不同的 URI：

[![类型](https://www.baeldung.com/wp-content/uploads/2017/10/uri.png)](https://www.baeldung.com/wp-content/uploads/2017/10/uri.png)

或者检查异常指标：

[![例外](https://www.baeldung.com/wp-content/uploads/2017/10/exception.png)](https://www.baeldung.com/wp-content/uploads/2017/10/exception.png)

请注意，我们还可以在控制器类或特定端点方法上使用[@Timed](https://github.com/micrometer-metrics/micrometer/blob/master/micrometer-core/src/main/java/io/micrometer/core/annotation/Timed.java#L24)来自定义指标的标签、长任务、分位数和百分位数：

```java
@RestController
@Timed("people")
public class PeopleController {

    @GetMapping("/people")
    @Timed(value = "people.all", longTask = true)
    public List<String> listPeople() {
        //...
    }

}
```

根据上面的代码，我们可以通过查看 Atlas 端点http://localhost:7101/api/v1/tags/name看到以下标签：

```plaintext
["people", "people.all", "jvmBufferCount", ... ]
```

Micrometer 也适用于Spring Boot2.0 中引入的函数 Web 框架。我们可以通过过滤RouterFunction来启用指标：

```java
RouterFunctionMetrics metrics = new RouterFunctionMetrics(registry);
RouterFunctions.route(...)
  .filter(metrics.timer("server.requests"));
```

我们还可以从数据源和计划任务中收集指标。查看[官方文档](https://micrometer.io/docs/registry/atlas#_timers)以获取更多详细信息。

## 七. 总结

在本文中，我们介绍了度量门面 Micrometer。通过在通用语义下抽象和支持多个监控系统，该工具使不同监控平台之间的切换变得非常容易。
## 1. 概述

[Netflix Servo](https://github.com/Netflix/servo)是Java应用程序的度量工具。Servo 类似于[Dropwizard Metrics](http://metrics.dropwizard.io/)，但更简单。它仅利用 JMX 来提供用于公开和发布应用程序指标的简单接口。

在本文中，我们将介绍 Servo 提供的功能以及我们如何使用它来收集和发布应用程序指标。

## 2.Maven依赖

在我们深入实际实现之前，让我们将[Servo](https://search.maven.org/classic/#search|ga|1|a%3A"servo-core")依赖项添加到pom.xml文件中：

```xml
<dependency>
    <groupId>com.netflix.servo</groupId>
    <artifactId>servo-core</artifactId>
    <version>0.12.16</version>
</dependency>
```

此外，还有很多可用的扩展，例如[Servo-Apache](https://search.maven.org/classic/#search|ga|1|servo-apache)、[Servo-AWS](https://search.maven.org/classic/#search|ga|1|servo-aws)等，我们以后可能会需要它们。这些扩展的最新版本也可以在[Maven Central](https://search.maven.org/classic/#search|ga|1|g%3A"com.netflix.servo")上找到。

## 3.收集指标

首先，让我们看看如何从我们的应用程序中收集指标。

Servo 提供四种主要的指标类型：[Counter](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/monitor/Counter.html)、[Gauge](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/monitor/Gauge.html)、[Timer](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/monitor/Timer.html)和[Informational](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/monitor/Informational.html)。

### 3.1. 指标类型 –计数器

[计数器](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/monitor/Counter.html)用于记录增量。常用的实现是[BasicCounter](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/monitor/BasicCounter.html)、[ StepCounter](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/monitor/StepCounter.html)和[PeakRateCounter](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/monitor/PeakRateCounter.html)。

BasicCounter做了计数器应该做的事情，简单明了：

```java
Counter counter = new BasicCounter(MonitorConfig.builder("test").build());
assertEquals("counter should start with 0", 0, counter.getValue().intValue());

counter.increment();
 
assertEquals("counter should have increased by 1", 1, counter.getValue().intValue());

counter.increment(-1);
 
assertEquals("counter should have decreased by 1", 0, counter.getValue().intValue());
```

PeakRateCounter返回轮询间隔期间给定秒的最大计数：

```java
Counter counter = new PeakRateCounter(MonitorConfig.builder("test").build());
assertEquals(
  "counter should start with 0", 
  0, counter.getValue().intValue());

counter.increment();
SECONDS.sleep(1);

counter.increment();
counter.increment();

assertEquals("peak rate should have be 2", 2, counter.getValue().intValue());
```

与其他计数器不同，StepCounter记录上一次轮询间隔的每秒速率：

```java
System.setProperty("servo.pollers", "1000");
Counter counter = new StepCounter(MonitorConfig.builder("test").build());
 
assertEquals("counter should start with rate 0.0", 0.0, counter.getValue());

counter.increment();
SECONDS.sleep(1);

assertEquals(
  "counter rate should have increased to 1.0", 
  1.0, counter.getValue());
```

请注意，我们在上面的代码中将servo.pollers设置为1000。那就是将轮询间隔设置为1秒，而不是默认的60秒和10秒的间隔。稍后我们将对此进行更多介绍。

### 3.2. 指标类型 –仪表

[Gauge](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/monitor/Gauge.html)是一个返回当前值的简单监视器。[提供了BasicGauge](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/monitor/BasicGauge.html)、[ MinGauge](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/monitor/MinGauge.html)、[ MaxGauge](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/monitor/MaxGauge.html)和[NumberGauges](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/monitor/NumberGauge.html)。

BasicGauge调用Callable来获取当前值。我们可以获得集合的大小、BlockingQueue的最新值或任何需要少量计算的值。

```java
Gauge<Double> gauge = new BasicGauge<>(MonitorConfig.builder("test")
  .build(), () -> 2.32);
 
assertEquals(2.32, gauge.getValue(), 0.01);
```

MaxGauge和MinGauge分别用于跟踪最大值和最小值：

```java
MaxGauge gauge = new MaxGauge(MonitorConfig.builder("test").build());
assertEquals(0, gauge.getValue().intValue());

gauge.update(4);
assertEquals(4, gauge.getCurrentValue(0));

gauge.update(1);
assertEquals(4, gauge.getCurrentValue(0));
```

NumberGauge ( [LongGauge](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/monitor/LongGauge.html) , [DoubleGauge](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/monitor/DoubleGauge.html) ) 包装提供的Number ( Long , Double )。要使用这些量规收集指标，我们必须确保Number是线程安全的。

### 3.3. 指标类型 –计时器

[计时器](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/monitor/Timer.html)有助于测量特定事件的持续时间。默认实现是[BasicTimer](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/monitor/BasicTimer.html)、[ StatsTimer](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/monitor/StatsTimer.html)和[BucketTimer](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/monitor/BucketTimer.html)。

BasicTimer记录总时间、计数等简单统计信息：

```java
BasicTimer timer = new BasicTimer(MonitorConfig.builder("test").build(), SECONDS);
Stopwatch stopwatch = timer.start();

SECONDS.sleep(1);
timer.record(2, SECONDS);
stopwatch.stop();

assertEquals("timer should count 1 second", 1, timer.getValue().intValue());
assertEquals("timer should count 3 seconds in total", 
  3.0, timer.getTotalTime(), 0.01);
assertEquals("timer should record 2 updates", 2, timer.getCount().intValue());
assertEquals("timer should have max 2", 2, timer.getMax(), 0.01);
```

StatsTimer通过在轮询间隔之间进行采样来提供更丰富的统计信息：

```java
System.setProperty("netflix.servo", "1000");
StatsTimer timer = new StatsTimer(MonitorConfig
  .builder("test")
  .build(), new StatsConfig.Builder()
  .withComputeFrequencyMillis(2000)
  .withPercentiles(new double[] { 99.0, 95.0, 90.0 })
  .withPublishMax(true)
  .withPublishMin(true)
  .withPublishCount(true)
  .withPublishMean(true)
  .withPublishStdDev(true)
  .withPublishVariance(true)
  .build(), SECONDS);
Stopwatch stopwatch = timer.start();

SECONDS.sleep(1);
timer.record(3, SECONDS);
stopwatch.stop();

stopwatch = timer.start();
timer.record(6, SECONDS);
SECONDS.sleep(2);
stopwatch.stop();

assertEquals("timer should count 12 seconds in total", 
  12, timer.getTotalTime());
assertEquals("timer should count 12 seconds in total", 
  12, timer.getTotalMeasurement());
assertEquals("timer should record 4 updates", 4, timer.getCount());
assertEquals("stats timer value time-cost/update should be 2", 
  3, timer.getValue().intValue());

final Map<String, Number> metricMap = timer.getMonitors().stream()
  .collect(toMap(monitor -> getMonitorTagValue(monitor, "statistic"),
    monitor -> (Number) monitor.getValue()));
 
assertThat(metricMap.keySet(), containsInAnyOrder(
  "count", "totalTime", "max", "min", "variance", "stdDev", "avg", 
  "percentile_99", "percentile_95", "percentile_90"));
```

BucketTimer提供了一种通过分桶值范围来获取样本分布的方法：

```java
BucketTimer timer = new BucketTimer(MonitorConfig
  .builder("test")
  .build(), new BucketConfig.Builder()
  .withBuckets(new long[] { 2L, 5L })
  .withTimeUnit(SECONDS)
  .build(), SECONDS);

timer.record(3);
timer.record(6);

assertEquals(
  "timer should count 9 seconds in total",
  9, timer.getTotalTime().intValue());
 
Map<String, Long> metricMap = timer.getMonitors().stream()
  .filter(monitor -> monitor.getConfig().getTags().containsKey("servo.bucket"))
  .collect(toMap(
    m -> getMonitorTagValue(m, "servo.bucket"),
    m -> (Long) m.getValue()));

assertThat(metricMap, allOf(hasEntry("bucket=2s", 0L), hasEntry("bucket=5s", 1L),
  hasEntry("bucket=overflow", 1L)));
```

要跟踪可能持续数小时的长时间操作，我们可以使用复合监视器[DurationTimer](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/monitor/DurationTimer.html)。

### 3.4. 指标类型 -信息

此外，我们还可以利用[Informational](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/monitor/Informational.html)监视器记录描述性信息，以帮助调试和诊断。唯一的实现是[BasicInformational](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/monitor/BasicInformational.html)，它的用法再简单不过了：

```java
BasicInformational informational = new BasicInformational(
  MonitorConfig.builder("test").build());
informational.setValue("information collected");
```

### 3.5. 监控注册表

度量类型都是[Monitor](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/monitor/Monitor.html)类型，它是Servo的基础。我们现在知道各种工具收集原始指标，但要报告数据，我们需要注册这些监视器。

请注意，每个配置的监视器都应注册一次且仅注册一次以确保指标的正确性。所以我们可以使用单例模式注册监视器。

大多数时候，我们可以使用[DefaultMonitorRegistry](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/DefaultMonitorRegistry.html)来注册监视器：

```java
Gauge<Double> gauge = new BasicGauge<>(MonitorConfig.builder("test")
  .build(), () -> 2.32);
DefaultMonitorRegistry.getInstance().register(gauge);
```

如果我们想动态注册一个监视器，可以使用[DynamicTimer](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/monitor/DynamicTimer.html)和[DynamicCounter ：](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/monitor/DynamicCounter.html)

```java
DynamicCounter.increment("monitor-name", "tag-key", "tag-value");
```

请注意，每次更新值时，动态注册都会导致昂贵的查找操作。

Servo 还提供了几种辅助方法来注册在对象中声明的监视器：

```java
Monitors.registerObject("testObject", this);
assertTrue(Monitors.isObjectRegistered("testObject", this));
```

方法[registerObject](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/monitor/Monitors.html#registerObject(java.lang.String, java.lang.Object))将使用反射来添加由注解[@Monitor](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/annotations/Monitor.html)声明的所有监视器实例，并添加由[@MonitorTags](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/annotations/MonitorTags.html)声明的标签：

```java
@Monitor(
  name = "integerCounter",
  type = DataSourceType.COUNTER,
  description = "Total number of update operations.")
private AtomicInteger updateCount = new AtomicInteger(0);

@MonitorTags
private TagList tags = new BasicTagList(
  newArrayList(new BasicTag("tag-key", "tag-value")));

@Test
public void givenAnnotatedMonitor_whenUpdated_thenDataCollected() throws Exception {
    System.setProperty("servo.pollers", "1000");
    Monitors.registerObject("testObject", this);
    assertTrue(Monitors.isObjectRegistered("testObject", this));

    updateCount.incrementAndGet();
    updateCount.incrementAndGet();
    SECONDS.sleep(1);

    List<List<Metric>> metrics = observer.getObservations();
 
    assertThat(metrics, hasSize(greaterThanOrEqualTo(1)));
 
    Iterator<List<Metric>> metricIterator = metrics.iterator();
    metricIterator.next(); //skip first empty observation
 
    while (metricIterator.hasNext()) {
        assertThat(metricIterator.next(), hasItem(
          hasProperty("config", 
          hasProperty("name", is("integerCounter")))));
    }
}
```

## 4. 发布指标

通过收集到的指标，我们可以将其发布为任何格式，例如在各种数据可视化平台上渲染时间序列图。要发布指标，我们需要定期从监视器观察中轮询数据。

### 4.1. 度量轮询器

[MetricPoller](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/publish/MetricPoller.html)用作指标获取器。我们可以获取[MonitorRegistries](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/publish/MonitorRegistryMetricPoller.html)、[ JVM](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/publish/JvmMetricPoller.html)、[ JMX](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/publish/JmxMetricPoller.html)的指标。在扩展的帮助下，我们可以轮询[Apache 服务器状态](https://github.com/Netflix/servo/tree/master/servo-apache)和[Tomcat 指标等指标](https://github.com/Netflix/servo/tree/master/servo-tomcat)。

```java
MemoryMetricObserver observer = new MemoryMetricObserver();
PollRunnable pollRunnable = new PollRunnable(new JvmMetricPoller(),
  new BasicMetricFilter(true), observer);
PollScheduler.getInstance().start();
PollScheduler.getInstance().addPoller(pollRunnable, 1, SECONDS);

SECONDS.sleep(1);
PollScheduler.getInstance().stop();
List<List<Metric>> metrics = observer.getObservations();

assertThat(metrics, hasSize(greaterThanOrEqualTo(1)));
List<String> keys = extractKeys(metrics);
 
assertThat(keys, hasItems("loadedClassCount", "initUsage", "maxUsage", "threadCount"));
```

这里我们创建了一个[JvmMetricPoller](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/publish/JvmMetricPoller.html)来轮询 JVM 的指标。将轮询器添加到调度程序时，我们让轮询任务每秒运行一次。系统默认轮询器配置在[Pollers](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/monitor/Pollers.html)中定义，但我们可以指定轮询器与系统属性servo.pollers一起使用。

### 4.2. 指标观察者

当轮询指标时，注册的[MetricObservers](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/publish/MetricObserver.html)的观察将被更新。

默认提供的MetricObservers是[MemoryMetricObserver](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/publish/MemoryMetricObserver.html)、[FileMetricObserver](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/publish/FileMetricObserver.html)和[AsyncMetricObserver](https://netflix.github.io/servo/current/servo-core/docs/javadoc/com/netflix/servo/publish/AsyncMetricObserver.html)。我们已经在前面的代码示例中展示了如何使用MemoryMetricObserver 。

目前，有几个有用的扩展可用：

-   [AtlasMetricObserver](https://github.com/Netflix/servo/tree/master/servo-atlas)：将指标发布到[Netflix Atlas](https://github.com/Netflix/atlas)以在内存中生成时间序列数据以供分析
-   [CloudWatchMetricObserver](https://github.com/Netflix/servo/tree/master/servo-aws)：将指标推送到[Amazon CloudWatch](https://aws.amazon.com/cloudwatch/)以进行指标监控和跟踪
-   [GraphiteObserver](https://github.com/Netflix/servo/tree/master/servo-graphite)：将指标发布到[Graphite](http://graphiteapp.org/)以存储和绘制图表

我们可以实现自定义的MetricObserver以将应用程序指标发布到我们认为合适的位置。唯一需要关心的是处理更新的指标：

```java
public class CustomObserver extends BaseMetricObserver {

    //...

    @Override
    public void updateImpl(List<Metric> metrics) {
        //TODO
    }
}
```

### 4.3. 发布到 Netflix Atlas

[Atlas](https://github.com/Netflix/atlas)是 Netflix 的另一个与指标相关的工具。它是用于管理维度时间序列数据的工具，是发布我们收集的指标的理想场所。

现在，我们将演示如何将指标发布到 Netflix Atlas。

首先，让我们将[servo-atlas](https://search.maven.org/classic/#search|ga|1|g%3A"com.netflix.servo" AND a%3A"servo-atlas")依赖项附加到pom.xml：

```xml
<dependency>
      <groupId>com.netflix.servo</groupId>
      <artifactId>servo-atlas</artifactId>
      <version>${netflix.servo.ver}</version>
</dependency>

<properties>
    <netflix.servo.ver>0.12.17</netflix.servo.ver>
</properties>
```

这个依赖包括一个AtlasMetricObserver来帮助我们发布指标到Atlas。

然后，我们将设置一个 Atlas 服务器：

```bash
$ curl -LO 'https://github.com/Netflix/atlas/releases/download/v1.4.4/atlas-1.4.4-standalone.jar'
$ curl -LO 'https://raw.githubusercontent.com/Netflix/atlas/v1.4.x/conf/memory.conf'
$ java -jar atlas-1.4.4-standalone.jar memory.conf
```

为了节省我们的测试时间，让我们在memory.conf 中将步长设置为 1 秒，这样我们就可以生成一个包含足够详细指标的时间序列图。

AtlasMetricObserver需要一个简单的配置和一个标签列表。给定标签的指标将被推送到 Atlas：

```java
System.setProperty("servo.pollers", "1000");
System.setProperty("servo.atlas.batchSize", "1");
System.setProperty("servo.atlas.uri", "http://localhost:7101/api/v1/publish");
AtlasMetricObserver observer = new AtlasMetricObserver(
  new BasicAtlasConfig(), BasicTagList.of("servo", "counter"));

PollRunnable task = new PollRunnable(
  new MonitorRegistryMetricPoller(), new BasicMetricFilter(true), observer);
```

使用PollRunnable任务启动PollScheduler后，我们可以自动将指标发布到 Atlas：

```java
Counter counter = new BasicCounter(MonitorConfig
  .builder("test")
  .withTag("servo", "counter")
  .build());
DefaultMonitorRegistry
  .getInstance()
  .register(counter);
assertThat(atlasValuesOfTag("servo"), not(containsString("counter")));

for (int i = 0; i < 3; i++) {
    counter.increment(RandomUtils.nextInt(10));
    SECONDS.sleep(1);
    counter.increment(-1  RandomUtils.nextInt(10));
    SECONDS.sleep(1);
}

assertThat(atlasValuesOfTag("servo"), containsString("counter"));
```

基于指标，我们可以使用Atlas 的[图形 API生成折线图：](https://github.com/Netflix/atlas/wiki/Graph)

[![图形](https://www.baeldung.com/wp-content/uploads/2017/06/graph-300x161.png)](https://www.baeldung.com/wp-content/uploads/2017/06/graph.png)

## 5.总结

在本文中，我们介绍了如何使用 Netflix Servo 收集和发布应用指标。

如果还没有阅读我们对 Dropwizard Metrics 的介绍，请在[此处](https://www.baeldung.com/dropwizard-metrics)查看以与 Servo 进行快速比较。
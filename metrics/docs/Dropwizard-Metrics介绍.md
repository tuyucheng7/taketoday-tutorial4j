## 1. 简介

[Metrics](http://metrics.dropwizard.io/3.1.0/)是一个Java库，它为Java应用程序提供测量工具。

它有几个模块，在本文中，我们将详细阐述metrics-core模块、metrics-healthchecks模块、metrics-servlets模块和metrics-servlet模块，并勾勒出其余模块，供大家参考。

## 2.模块指标-核心

### 2.1. Maven 依赖项

要使用metrics-core模块，只需要将一个依赖项添加到pom.xml文件中：

```xml
<dependency>
    <groupId>io.dropwizard.metrics</groupId>
    <artifactId>metrics-core</artifactId>
    <version>3.1.2</version>
</dependency>

```

你可以[在这里](https://search.maven.org/classic/#search|gav|1|g%3A"io.dropwizard.metrics" AND a%3A"metrics-core")找到它的最新版本。

### 2.2. 度量注册表

简而言之，我们将使用MetricRegistry类来注册一个或多个指标。

我们可以对所有指标使用一个指标注册表，但如果我们想对不同的指标使用不同的报告方法，我们也可以将指标分组，并为每个组使用不同的指标注册表。

让我们现在创建一个MetricRegistry：

```java
MetricRegistry metricRegistry = new MetricRegistry();
```

然后我们可以用这个MetricRegistry注册一些指标：

```java
Meter meter1 = new Meter();
metricRegistry.register("meter1", meter1);

Meter meter2 = metricRegistry.meter("meter2");

```

有两种创建新指标的基本方法：自己实例化一个或从指标注册表中获取一个。如所见，我们在上面的示例中同时使用了它们，我们正在实例化Meter对象“meter1”，我们正在获取另一个由metricRegistry创建的Meter对象“meter2” 。

在指标注册表中，每个指标都有一个唯一的名称，因为我们在上面使用“meter1”和“meter2”作为指标名称。MetricRegistry还提供了一组静态辅助方法来帮助我们创建合适的指标名称：

```java
String name1 = MetricRegistry.name(Filter.class, "request", "count");
String name2 = MetricRegistry.name("CustomFilter", "response", "count");

```

如果我们需要管理一组指标注册表，我们可以使用SharedMetricRegistries类，它是单例且线程安全的。我们可以向其中添加一个度量寄存器，从中检索这个度量寄存器，然后将其删除：

```java
SharedMetricRegistries.add("default", metricRegistry);
MetricRegistry retrievedMetricRegistry = SharedMetricRegistries.getOrCreate("default");
SharedMetricRegistries.remove("default");

```

## 3.指标概念

metrics-core 模块提供了几种常用的指标类型：Meter、Gauge、Counter、Histogram和Timer，以及Reporter来输出指标的值。

### 3.1. 仪表

仪表测量事件发生次数和发生率：

```java
Meter meter = new Meter();
long initCount = meter.getCount();
assertThat(initCount, equalTo(0L));

meter.mark();
assertThat(meter.getCount(), equalTo(1L));

meter.mark(20);
assertThat(meter.getCount(), equalTo(21L));

double meanRate = meter.getMeanRate();
double oneMinRate = meter.getOneMinuteRate();
double fiveMinRate = meter.getFiveMinuteRate();
double fifteenMinRate = meter.getFifteenMinuteRate(); 

```

getCount()方法返回事件发生次数，mark()方法将事件发生次数加1 或 n。Meter对象提供四种速率，分别代表整个Meter生命周期、最近一分钟、最近五分钟和最近一个季度的平均速率。

### 3.2. 测量

Gauge是一个接口，仅用于返回特定值。metrics-core 模块提供了它的几种实现：[RatioGauge](http://metrics.dropwizard.io/3.1.0/apidocs/com/codahale/metrics/RatioGauge.html)、[CachedGauge](http://metrics.dropwizard.io/3.1.0/apidocs/com/codahale/metrics/annotation/CachedGauge.html)、[DerivativeGauge](http://metrics.dropwizard.io/3.1.0/apidocs/com/codahale/metrics/DerivativeGauge.html)和[JmxAttributeGauge](http://metrics.dropwizard.io/3.1.0/apidocs/com/codahale/metrics/JmxAttributeGauge.html)。

RatioGauge是一个抽象类，它测量一个值与另一个值的比率。

让我们看看如何使用它。首先，我们实现一个AttendanceRatioGauge类：

```java
public class AttendanceRatioGauge extends RatioGauge {
    private int attendanceCount;
    private int courseCount;

    @Override
    protected Ratio getRatio() {
        return Ratio.of(attendanceCount, courseCount);
    }
    
    // standard constructors
}

```

然后我们测试它：

```java
RatioGauge ratioGauge = new AttendanceRatioGauge(15, 20);

assertThat(ratioGauge.getValue(), equalTo(0.75));

```

CachedGauge是另一个可以缓存值的抽象类，因此，当值的计算成本很高时，它非常有用。要使用它，我们需要实现一个类ActiveUsersGauge：

```java
public class ActiveUsersGauge extends CachedGauge<List<Long>> {
    
    @Override
    protected List<Long> loadValue() {
        return getActiveUserCount();
    }
 
    private List<Long> getActiveUserCount() {
        List<Long> result = new ArrayList<Long>();
        result.add(12L);
        return result;
    }

    // standard constructors
}
```

然后我们对其进行测试，看看它是否按预期工作：

```java
Gauge<List<Long>> activeUsersGauge = new ActiveUsersGauge(15, TimeUnit.MINUTES);
List<Long> expected = new ArrayList<>();
expected.add(12L);

assertThat(activeUsersGauge.getValue(), equalTo(expected));

```

我们在实例化ActiveUsersGauge时将缓存的过期时间设置为 15 分钟。

DerivativeGauge也是一个抽象类，它允许从其他Gauge派生一个值作为它的值。

让我们看一个例子：

```java
public class ActiveUserCountGauge extends DerivativeGauge<List<Long>, Integer> {
    
    @Override
    protected Integer transform(List<Long> value) {
        return value.size();
    }

    // standard constructors
}
```

这个Gauge从ActiveUsersGauge派生它的值，所以我们期望它是来自基本列表大小的值：

```java
Gauge<List<Long>> activeUsersGauge = new ActiveUsersGauge(15, TimeUnit.MINUTES);
Gauge<Integer> activeUserCountGauge = new ActiveUserCountGauge(activeUsersGauge);

assertThat(activeUserCountGauge.getValue(), equalTo(1));

```

当我们需要访问通过 JMX 公开的其他库的指标时，使用JmxAttributeGauge 。

### 3.3. 柜台

Counter用于记录递增和递减：

```java
Counter counter = new Counter();
long initCount = counter.getCount();
assertThat(initCount, equalTo(0L));

counter.inc();
assertThat(counter.getCount(), equalTo(1L));

counter.inc(11);
assertThat(counter.getCount(), equalTo(12L));

counter.dec();
assertThat(counter.getCount(), equalTo(11L));

counter.dec(6);
assertThat(counter.getCount(), equalTo(5L));
```

### 3.4. 直方图

直方图用于跟踪Long值流，并分析它们的统计特征，例如最大值、最小值、平均值、中值、标准差、第 75 个百分位等：

```java
Histogram histogram = new Histogram(new UniformReservoir());
histogram.update(5);
long count1 = histogram.getCount();
assertThat(count1, equalTo(1L));

Snapshot snapshot1 = histogram.getSnapshot();
assertThat(snapshot1.getValues().length, equalTo(1));
assertThat(snapshot1.getValues()[0], equalTo(5L));

histogram.update(20);
long count2 = histogram.getCount();
assertThat(count2, equalTo(2L));

Snapshot snapshot2 = histogram.getSnapshot();
assertThat(snapshot2.getValues().length, equalTo(2));
assertThat(snapshot2.getValues()[1], equalTo(20L));
assertThat(snapshot2.getMax(), equalTo(20L));
assertThat(snapshot2.getMean(), equalTo(12.5));
assertEquals(10.6, snapshot2.getStdDev(), 0.1);
assertThat(snapshot2.get75thPercentile(), equalTo(20.0));
assertThat(snapshot2.get999thPercentile(), equalTo(20.0));

```

Histogram通过使用 reservoir 采样对数据进行采样，当我们实例化一个Histogram对象时，我们需要显式地设置它的 reservoir。

Reservoir是一个接口，metrics-core 提供了它们的四种实现：[ExponentiallyDecayingReservoir](http://metrics.dropwizard.io/3.1.0/apidocs/com/codahale/metrics/ExponentiallyDecayingReservoir.html)、[UniformReservoir](http://metrics.dropwizard.io/3.1.0/apidocs/com/codahale/metrics/UniformReservoir.html)、[SlidingTimeWindowReservoir](http://metrics.dropwizard.io/3.1.0/apidocs/com/codahale/metrics/SlidingTimeWindowReservoir.html)、[SlidingWindowReservoir](http://metrics.dropwizard.io/3.1.0/apidocs/com/codahale/metrics/SlidingWindowReservoir.html)。

在上一节中，我们提到了除了使用构造函数之外，还可以通过 MetricRegistry创建指标。当我们使用metricRegistry.histogram()时，它会返回一个具有ExponentiallyDecayingReservoir实现的Histogram实例。

### 3.5. 定时器

Timer用于跟踪由Context对象表示的多个计时持续时间，它还提供它们的统计数据：

```java
Timer timer = new Timer();
Timer.Context context1 = timer.time();
TimeUnit.SECONDS.sleep(5);
long elapsed1 = context1.stop();

assertEquals(5000000000L, elapsed1, 1000000);
assertThat(timer.getCount(), equalTo(1L));
assertEquals(0.2, timer.getMeanRate(), 0.1);

Timer.Context context2 = timer.time();
TimeUnit.SECONDS.sleep(2);
context2.close();

assertThat(timer.getCount(), equalTo(2L));
assertEquals(0.3, timer.getMeanRate(), 0.1);

```

### 3.6. 记者

当我们需要输出我们的测量值时，我们可以使用Reporter。这是一个接口，metrics-core模块提供了它的几种实现，比如[ConsoleReporter](http://metrics.dropwizard.io/3.1.0/apidocs/com/codahale/metrics/ConsoleReporter.html)、[CsvReporter](http://metrics.dropwizard.io/3.1.0/apidocs/com/codahale/metrics/CsvReporter.html)、[Slf4jReporter](http://metrics.dropwizard.io/3.1.0/apidocs/com/codahale/metrics/Slf4jReporter.html)、[JmxReporter](http://metrics.dropwizard.io/3.1.0/apidocs/com/codahale/metrics/JmxReporter.html)等。

这里我们以ConsoleReporter为例：

```java
MetricRegistry metricRegistry = new MetricRegistry();

Meter meter = metricRegistry.meter("meter");
meter.mark();
meter.mark(200);
Histogram histogram = metricRegistry.histogram("histogram");
histogram.update(12);
histogram.update(17);
Counter counter = metricRegistry.counter("counter");
counter.inc();
counter.dec();

ConsoleReporter reporter = ConsoleReporter.forRegistry(metricRegistry).build();
reporter.start(5, TimeUnit.MICROSECONDS);
reporter.report();

```

这是ConsoleReporter 的示例输出：

```java
-- Histograms ------------------------------------------------------------------
histogram
count = 2
min = 12
max = 17
mean = 14.50
stddev = 2.50
median = 17.00
75% <= 17.00
95% <= 17.00
98% <= 17.00
99% <= 17.00
99.9% <= 17.00

-- Meters ----------------------------------------------------------------------
meter
count = 201
mean rate = 1756.87 events/second
1-minute rate = 0.00 events/second
5-minute rate = 0.00 events/second
15-minute rate = 0.00 events/second

```

## 4.模块metrics-healthchecks

Metrics 有一个扩展 metrics-healthchecks 模块，用于处理健康检查。

### 4.1. Maven 依赖项

要使用 metrics-healthchecks 模块，我们需要将此依赖项添加到pom.xml文件中：

```xml
<dependency>
    <groupId>io.dropwizard.metrics</groupId>
    <artifactId>metrics-healthchecks</artifactId>
    <version>3.1.2</version>
</dependency>
```

你可以[在这里](https://search.maven.org/classic/#search|gav|1|g%3A"io.dropwizard.metrics" AND a%3A"metrics-healthchecks")找到它的最新版本。

### 4.2. 用法

首先，我们需要几个类负责具体的健康检查操作，这些类必须实现HealthCheck。

例如，我们使用DatabaseHealthCheck和UserCenterHealthCheck：

```java
public class DatabaseHealthCheck extends HealthCheck {
 
    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}

public class UserCenterHealthCheck extends HealthCheck {
 
    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}

```

然后，我们需要一个HealthCheckRegistry(就像MetricRegistry一样)，并用它注册DatabaseHealthCheck和UserCenterHealthCheck：

```java
HealthCheckRegistry healthCheckRegistry = new HealthCheckRegistry();
healthCheckRegistry.register("db", new DatabaseHealthCheck());
healthCheckRegistry.register("uc", new UserCenterHealthCheck());

assertThat(healthCheckRegistry.getNames().size(), equalTo(2));

```

我们也可以注销HealthCheck：

```java
healthCheckRegistry.unregister("uc");
 
assertThat(healthCheckRegistry.getNames().size(), equalTo(1));

```

我们可以运行所有HealthCheck实例：

```java
Map<String, HealthCheck.Result> results = healthCheckRegistry.runHealthChecks();
for (Map.Entry<String, HealthCheck.Result> entry : results.entrySet()) {
    assertThat(entry.getValue().isHealthy(), equalTo(true));
}

```

最后，我们可以运行一个特定的HealthCheck实例：

```java
healthCheckRegistry.runHealthCheck("db");

```

## 5. 模块指标-servlets

Metrics 为我们提供了一些有用的 servlet，它们允许我们通过 HTTP 请求访问与指标相关的数据。

### 5.1. Maven 依赖项

要使用 metrics-servlets 模块，我们需要将此依赖项添加到pom.xml文件中：

```xml
<dependency>
    <groupId>io.dropwizard.metrics</groupId>
    <artifactId>metrics-servlets</artifactId>
    <version>3.1.2</version>
</dependency>
```

你可以[在这里](https://search.maven.org/classic/#search|gav|1|g%3A"io.dropwizard.metrics" AND a%3A"metrics-servlets")找到它的最新版本。

### 5.2. HealthCheckServlet用法

HealthCheckServlet提供健康检查结果。首先，我们需要创建一个ServletContextListener来暴露我们的HealthCheckRegistry：

```java
public class MyHealthCheckServletContextListener
  extends HealthCheckServlet.ContextListener {
 
    public static HealthCheckRegistry HEALTH_CHECK_REGISTRY
      = new HealthCheckRegistry();

    static {
        HEALTH_CHECK_REGISTRY.register("db", new DatabaseHealthCheck());
    }

    @Override
    protected HealthCheckRegistry getHealthCheckRegistry() {
        return HEALTH_CHECK_REGISTRY;
    }
}

```

然后，我们将这个监听器和HealthCheckServlet添加到web.xml文件中：

```xml
<listener>
    <listener-class>com.baeldung.metrics.servlets.MyHealthCheckServletContextListener</listener-class>
</listener>
<servlet>
    <servlet-name>healthCheck</servlet-name>
    <servlet-class>com.codahale.metrics.servlets.HealthCheckServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>healthCheck</servlet-name>
    <url-pattern>/healthcheck</url-pattern>
</servlet-mapping>
```

现在我们可以启动 Web 应用程序，并向“http://localhost:8080/healthcheck”发送 GET 请求以获取健康检查结果。它的响应应该是这样的：

```plaintext
{
  "db": {
    "healthy": true
  }
}
```

### 5.3. ThreadDumpServlet用法

ThreadDumpServlet提供有关 JVM 中所有活动线程的信息、它们的状态、堆栈跟踪以及它们可能正在等待的任何锁的状态。
如果我们想使用它，我们只需要将这些添加到web.xml文件中：

```xml
<servlet>
    <servlet-name>threadDump</servlet-name>
    <servlet-class>com.codahale.metrics.servlets.ThreadDumpServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>threadDump</servlet-name>
    <url-pattern>/threaddump</url-pattern>
</servlet-mapping>
```

线程转储数据将在“http://localhost:8080/threaddump”提供。

### 5.4. PingServlet用法

PingServlet可用于测试应用程序是否正在运行。我们将这些添加到web.xml文件中：

```xml
<servlet>
    <servlet-name>ping</servlet-name>
    <servlet-class>com.codahale.metrics.servlets.PingServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>ping</servlet-name>
    <url-pattern>/ping</url-pattern>
</servlet-mapping>
```

然后向“http://localhost:8080/ping”发送 GET 请求。响应的状态码是200，内容是“pong”。

### 5.5. MetricsServlet使用情况

MetricsServlet提供指标数据。首先，我们需要创建一个ServletContextListener来公开我们的MetricRegistry：

```java
public class MyMetricsServletContextListener
  extends MetricsServlet.ContextListener {
    private static MetricRegistry METRIC_REGISTRY
     = new MetricRegistry();

    static {
        Counter counter = METRIC_REGISTRY.counter("m01-counter");
        counter.inc();

        Histogram histogram = METRIC_REGISTRY.histogram("m02-histogram");
        histogram.update(5);
        histogram.update(20);
        histogram.update(100);
    }

    @Override
    protected MetricRegistry getMetricRegistry() {
        return METRIC_REGISTRY;
    }
}

```

这个监听器和MetricsServlet 都需要添加到web.xml中：

```xml
<listener>
    <listener-class>com.codahale.metrics.servlets.MyMetricsServletContextListener</listener-class>
</listener>
<servlet>
    <servlet-name>metrics</servlet-name>
    <servlet-class>com.codahale.metrics.servlets.MetricsServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>metrics</servlet-name>
    <url-pattern>/metrics</url-pattern>
</servlet-mapping>
```

这将在我们的 Web 应用程序“http://localhost:8080/metrics”中公开。它的响应应包含各种指标数据：

```plaintext
{
  "version": "3.0.0",
  "gauges": {},
  "counters": {
    "m01-counter": {
      "count": 1
    }
  },
  "histograms": {
    "m02-histogram": {
      "count": 3,
      "max": 100,
      "mean": 41.66666666666666,
      "min": 5,
      "p50": 20,
      "p75": 100,
      "p95": 100,
      "p98": 100,
      "p99": 100,
      "p999": 100,
      "stddev": 41.69998667732268
    }
  },
  "meters": {},
  "timers": {}
}

```

### 5.6. AdminServlet用法

AdminServlet聚合了HealthCheckServlet、ThreadDumpServlet、MetricsServlet和PingServlet。

让我们将这些添加到web.xml中：

```xml
<servlet>
    <servlet-name>admin</servlet-name>
    <servlet-class>com.codahale.metrics.servlets.AdminServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>admin</servlet-name>
    <url-pattern>/admin/</url-pattern>
</servlet-mapping>
```

现在可以通过“http://localhost:8080/admin”访问它。我们将获得一个包含四个链接的页面，每个链接对应这四个 servlet。

请注意，如果我们要进行健康检查并访问指标数据，仍然需要这两个侦听器。

## 6.模块指标-servlet

metrics-servlet模块提供了一个具有多个指标的过滤器：状态代码的计量器、活动请求数的计数器和请求持续时间的计时器。

### 6.1. Maven 依赖项

要使用这个模块，让我们首先将依赖添加到pom.xml中：

```xml
<dependency>
    <groupId>io.dropwizard.metrics</groupId>
    <artifactId>metrics-servlet</artifactId>
    <version>3.1.2</version>
</dependency>
```

你可以[在这里](https://search.maven.org/classic/#search|gav|1|g%3A"io.dropwizard.metrics" AND a%3A"metrics-servlet")找到它的最新版本。

### 6.2. 用法

要使用它，我们需要创建一个ServletContextListener，它将我们的MetricRegistry暴露给InstrumentedFilter：

```java
public class MyInstrumentedFilterContextListener
  extends InstrumentedFilterContextListener {
 
    public static MetricRegistry REGISTRY = new MetricRegistry();

    @Override
    protected MetricRegistry getMetricRegistry() {
        return REGISTRY;
    }
}

```

然后，我们将这些添加到web.xml中：

```xml
<listener>
     <listener-class>
         com.baeldung.metrics.servlet.MyInstrumentedFilterContextListener
     </listener-class>
</listener>

<filter>
    <filter-name>instrumentFilter</filter-name>
    <filter-class>
        com.codahale.metrics.servlet.InstrumentedFilter
    </filter-class>
</filter>
<filter-mapping>
    <filter-name>instrumentFilter</filter-name>
    <url-pattern>/</url-pattern>
</filter-mapping>
```

现在InstrumentedFilter可以工作了。如果我们想访问它的指标数据，我们可以通过它的MetricRegistry REGISTRY来实现。

## 七、其他模块

除了我们上面介绍的模块外，Metrics还有一些其他的模块用于不同的目的：

-   metrics-jvm：提供了几个有用的指标来检测 JVM 内部
-   metrics-ehcache : 提供InstrumentedEhcache，一个 Ehcache 缓存的装饰器
-   metrics-httpclient：提供用于检测 Apache HttpClient(4.x 版本)的类
-   metrics-log4j：提供InstrumentedAppender，这是 log4j 1.x 的 Log4j Appender实现，它按日志记录级别记录记录事件的速率
-   metrics-log4j2：类似于 metrics-log4j，仅适用于 log4j 2.x
-   metrics-logback：提供InstrumentedAppender，一个 Logback Appender实现，它按日志记录级别记录记录事件的速率
-   metrics-json :为 Jackson提供HealthCheckModule和MetricsModule

更重要的是，除了这些主要的项目模块，其他一些[第三方库](http://metrics.dropwizard.io/3.1.0/manual/third-party/)提供与其他库和框架的集成。

## 八. 总结

Instrumenting 应用程序是一个普遍的需求，因此在本文中，我们介绍了 Metrics，希望它可以帮助解决问题。
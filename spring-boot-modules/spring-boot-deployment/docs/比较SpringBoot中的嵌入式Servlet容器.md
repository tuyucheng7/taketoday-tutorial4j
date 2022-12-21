## 1. 简介

云原生应用程序和微服务的日益普及导致了对嵌入式Servlet容器的需求增加，Spring Boot允许开发人员使用3个最成熟的可用容器轻松构建应用程序或服务：Tomcat、Undertow和Jetty。

在本教程中，我们将演示一种使用在启动时和在某些负载下获得的指标来快速比较容器实现的方法。

## 2. 依赖关系

我们对每个可用容器实现的设置将始终要求我们在pom.xml中声明对spring-boot-starter-web的依赖。

通常，我们希望将父级指定为spring-boot-starter-parent，然后包含我们想要的Starter：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.4.0</version>
    <relativePath/>
</parent>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```

### 2.1 Tomcat

使用Tomcat时不需要进一步的依赖项，因为在使用spring-boot-starter-web时默认包含它。

### 2.2 Jetty

为了使用Jetty，我们首先需要从spring-boot-starter-web中排除spring-boot-starter-tomcat。

然后，我们简单地声明对spring-boot-starter-jetty的依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jetty</artifactId>
</dependency>

```

### 2.3 Undertow

Undertow的设置与Jetty相同，除了我们使用spring-boot-starter-undertow作为我们的依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-undertow</artifactId>
</dependency>
```

### 2.4 Actuator

我们将使用Spring Boot的Actuator作为对系统施加压力和查询指标的便捷方式。

查看[这篇文章]()以了解有关Actuator的详细信息，我们只需在我们的pom中添加一个依赖项即可使其可用：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

###  2.5 Apache Bench

[Apache Bench](https://httpd.apache.org/docs/2.2/programs/ab.html)是一个开源负载测试实用程序，与Apache Web服务器捆绑在一起。

Windows用户可以从[此处](https://httpd.apache.org/docs/current/platform/windows.html#down)链接的第三方供应商之一下载Apache，如果Apache已经安装在你的Windows机器上，你应该能够在你的apache/bin目录中找到ab.exe。

如果是Linux机器上，可以使用apt-get安装ab：

```bash
$ apt-get install apache2-utils
```

## 3. 启动指标

### 3.1 收集

为了收集我们的启动指标，我们将注册一个事件处理程序以在Spring Boot的ApplicationReadyEvent上触发。

我们将通过直接使用Actuator组件使用的MeterRegistry，以编程方式提取我们感兴趣的指标：

```java
@Component
public class StartupEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(StartupEventHandler.class);

    public StartupEventHandler(MeterRegistry registry) {
        this.meterRegistry = registry;
    }

    private final String[] METRICS = {
          "jvm.memory.used",
          "jvm.classes.loaded",
          "jvm.threads.live"};

    private String METRIC_MSG_FORMAT = "Startup Metric >> {}={}";

    private final MeterRegistry meterRegistry;

    @EventListener
    public void getAndLogStartupMetrics(ApplicationReadyEvent event) {
        Arrays.asList(METRICS)
              .forEach(this::getAndLogActuatorMetric);
    }

    private void getAndLogActuatorMetric(String metric) {
        Meter meter = meterRegistry.find(metric).meter();
        Map<Statistic, Double> stats = getSamples(meter);

        logger.info(METRIC_MSG_FORMAT, metric, stats.get(Statistic.VALUE).longValue());
    }

    private Map<Statistic, Double> getSamples(Meter meter) {
        Map<Statistic, Double> samples = new LinkedHashMap<>();
        mergeMeasurements(samples, meter);
        return samples;
    }

    private void mergeMeasurements(Map<Statistic, Double> samples, Meter meter) {
        meter.measure().forEach(measurement ->
              samples.merge(measurement.getStatistic(), measurement.getValue(), mergeFunction(measurement.getStatistic())));
    }

    private BiFunction<Double, Double, Double> mergeFunction(Statistic statistic) {
        return Statistic.MAX.equals(statistic) ? Double::max : Double::sum;
    }
}
```

通过在事件处理程序中记录启动时有趣的指标，我们避免了手动查询Actuator REST端点或运行独立JMX控制台的需要。

### 3.2 选择

Actuator提供了大量开箱即用的指标，我们选择了3个指标，这些指标有助于在服务器启动后获得关键运行时特征的高级概述：

-   jvm.memory.used：JVM自启动以来使用的总内存
-   jvm.classes.loaded：加载的类总数
-   jvm.threads.live：活动线程的总数，在我们的测试中，这个值可以被视为“静止”的线程计数

## 4. 运行时指标

### 4.1 收集

除了提供启动指标外，我们还将在运行Apache Bench时使用Actuator公开的/metrics端点作为目标URL，以便使应用程序处于负载之下。

为了在负载下测试真实的应用程序，我们可能会改用应用程序提供的端点。

服务器启动后，我们打开命令提示符并执行ab：

```bash
ab -n 10000 -c 10 http://localhost:8080/actuator/metrics
```

在上面的命令中，我们使用10个并发线程指定了总共10,000个请求。

### 4.2 选择

Apache Bench能够非常快速地为我们提供一些有用的信息，包括连接时间和在特定时间内服务的请求百分比。出于我们的目的，**我们专注于每秒请求数和每次请求时间(平均值)**。

## 5. 结果

在启动时，我们发现**Tomcat、Jetty和Undertow的内存占用量相当**，Undertow需要的内存比其他两个略多，而Jetty需要的内存最少。

对于我们的基准测试，我们发现**Tomcat、Jetty和Undertow的性能相当，但Undertow显然是最快的，而Jetty只是稍逊一筹**。 

|          指标          | Tomcat | Jetty | Undertow |
|:--------------------:|:------:|:-----:|:--------:|
| jvm.memory.used (MB) |  168   |  155  |   164    |
|  jvm.classes.loaded  |  9869  | 9784  |   9787   |
|   jvm.threads.live   |   25   |  17   |    19    |
|        每秒请求数         |  1542  | 1627  |   1650   |
|    每个请求的平均时间(毫秒)     | 6.483  | 6.148 |  6.059   |

请注意，这些指标自然地代表了准系统项目；你自己的应用程序的指标肯定会有所不同。

## 6. 基准讨论

开发适当的基准测试以对服务器实现进行全面的比较可能会变得复杂，为了提取最相关的信息，**清楚地了解什么对所讨论的用例很重要是至关重要的**。

请务必注意，此示例中收集的基准测试度量是使用非常特定的工作负载进行的，该工作负载由对Actuator端点的HTTP GET请求组成。

**预计不同的工作负载可能会导致容器实现之间的相对度量不同**，如果需要更稳健或更精确的测量，那么设置一个更接近生产用例的测试计划将是一个很好的主意。

此外，更复杂的基准测试解决方案(如[JMeter]()或[Gatling]())可能会产生更有价值的见解。

## 7. 选择容器

**选择正确的容器实施可能应该基于许多因素，这些因素不能仅用少数几个指标来巧妙地概括。舒适度、功能、可用配置选项和策略通常同样重要，甚至更重要**。

## 8. 总结

在本文中，我们研究了Tomcat、Jetty和Undertow嵌入式Servlet容器实现，通过查看Actuator组件公开的指标来检查每个容器在启动时使用默认配置的运行时特征。我们针对正在运行的系统执行了人为的工作负载，然后使用Apache Bench测量了性能。 
最后，我们讨论了该策略的优点，并提到了在比较实施基准时要记住的几件事。
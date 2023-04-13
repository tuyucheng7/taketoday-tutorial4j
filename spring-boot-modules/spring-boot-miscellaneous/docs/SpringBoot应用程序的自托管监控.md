## 1. 概述

Spring Boot的众多强大功能之一是一组内置[Actuator](https://www.baeldung.com/spring-boot-actuators)。这些Actuator提供了一种简单的方法来监视和控制Spring Boot应用程序的几乎每个方面。

在本教程中，我们将了解如何使用[指标Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-metrics.html)为Spring Boot应用程序创建自托管监控解决方案。

## 2. 指标数据库

**监控Spring Boot应用程序的第一部分是选择一个指标数据库**。默认情况下，Spring Boot会在每个应用程序中配置一个[Micrometer](https://micrometer.io/)指标注册表。

此默认实现收集一组预定义的应用程序指标，例如内存和CPU使用率、HTTP请求以及其他一些指标。但是**这些指标只存储在内存中**，这意味着它们会在应用程序重新启动时丢失。

要创建自托管监控解决方案，我们应该首先选择一个位于Spring Boot应用程序之外的指标数据库。以下部分将仅讨论几个可用的自托管选项。

请注意，任何时候Spring Boot在类路径上检测到另一个指标数据库时，它都会自动禁用内存中的注册表。

### 2.1 Influx数据库

[InfluxDB](https://www.influxdata.com/products/influxdb-overview/)是一个开源的时间序列数据库。开始使用InfluxDB的最快方法是将其作为Docker容器在本地运行：

```shell
docker run -p 8086:8086 -v /tmp:/var/lib/influxdb influxdb
```

请注意，这会将指标存储在本地/tmp分区中。这对于开发和测试来说很好，但对于生产环境来说不是一个好的选择。

InfluxDB运行后，**我们可以通过添加适当的[Micrometer依赖项](https://central.sonatype.com/artifact/io.micrometer/micrometer-registry-influx/1.10.5)来配置我们的Spring Boot应用程序以向其发布指标**：

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-influx</artifactId>
</dependency>
```

此外，我们需要向application.properties文件添加一个新条目：

```properties
management.metrics.export.influx.uri=http://localhost:8086
```

开箱即用，数据库名称设置为mydb，而用户名和密码保持未设置。

但是，我们可以使用专用属性覆盖默认值：

```properties
management.metrics.export.influx.db=customDbName
management.metrics.export.influx.user-name=myUserName
management.metrics.export.influx.password=mySecret
```

**InfluxDB不提供原生的可视化工具**。但是，它提供了一个名为[Chronograph](https://docs.influxdata.com/chronograf/)的单独工具，可以很好地可视化InfluxDB数据。

### 2.2 Prometheus

[Prometheus](https://prometheus.io/)是一个开源的监控和警报工具包，最初由SoundCloud构建。它的工作方式与InfluxDB略有不同。

我们不是将应用程序配置为将指标发布到Prometheus，而是**将Prometheus配置为定期轮询我们的应用程序**。

首先，我们配置Spring Boot应用程序以公开一个新的Prometheus Actuator端点。我们通过包含[micrometer-registry-prometheus](https://central.sonatype.com/artifact/io.micrometer/micrometer-registry-prometheus/1.10.5)依赖项来做到这一点：

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

**这将创建一个新的Actuator端点，以Prometheus理解的特殊格式生成指标数据**。

接下来，我们必须通过将所需配置添加到prometheus.yml文件中来配置Prometheus以轮询我们的应用程序。

以下配置指示Prometheus使用新的Actuator端点每5秒轮询一次我们的应用程序：

```yaml
scrape_configs:
    -   job_name: 'spring-actuator'
        metrics_path: '/actuator/prometheus'
        scrape_interval: 5s
        static_configs:
            -   targets: [ '127.0.0.1:8080' ]
```

最后，我们可以使用Docker启动本地Prometheus服务器。这假设我们的自定义配置文件位于本地文件/etc/prometheus/prometheus.yml中：

```shell
docker run -d \
--name=prometheus \
-p 9090:9090 \
-v /etc/prometheus/prometheus.yml:/etc/prometheus/prometheus.yml \
prom/prometheus \
--config.file=/etc/prometheus/prometheus.yml
```

Prometheus提供了自己的可视化工具，用于查看收集到的指标。可以通过URL [http://localhost:9090/](http://localhost:9090/)访问它。

<img src="../assets/img_4.png">

### 2.3 Graphite

[Graphite](https://graphiteapp.org/)是另一个开源的时间序列数据库。它的架构比我们看过的其他数据库稍微复杂一些，但是使用Docker，在本地运行实例非常简单：

```shell
docker run -d \
 --name graphite \
 --restart=always \
 -p 80:80 \
 -p 2003-2004:2003-2004 \
 -p 2023-2024:2023-2024 \
 -p 8125:8125/udp \
 -p 8126:8126 \
 graphiteapp/graphite-statsd
```

然后我们可以配置Spring Boot通过添加[micrometer-registry-graphite](https://central.sonatype.com/artifact/io.micrometer/micrometer-registry-graphite/1.10.5)依赖项将指标发布到我们的实例：

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-graphite</artifactId>
</dependency>
```

以及将配置属性添加到application.properties：

```properties
management.metrics.export.graphite.host=127.0.0.1
management.metrics.export.graphite.port=2004
```

与Prometheus一样，Graphite包含自己的可视化仪表板。它位于URL [http://localhost/](http://localhost/)。

<img src="../assets/img_5.png">

## 3. 可视化工具

一旦我们有了在Spring Boot应用程序之外存储指标的解决方案，**下一个决定就是我们希望如何可视化数据**。

前面提到的一些指标数据库包括它们自己的可视化工具。对于我们的自托管监控解决方案，有一个值得关注的独立可视化工具。

### 3.1 Grafana

[Grafana](https://grafana.com/)是一个开源分析和监控工具。**它可以连接到前面提到的所有数据库以及许多其他数据库**。

与大多数内置可视化工具相比，Grafana通常提供更好的配置和更高级的警报。它可以很容易地使用插件进行扩展，并且有许多可以导入的预构建仪表板来快速创建我们自己的可视化。

要在本地运行Grafana，我们可以使用Docker启动它：

```shell
docker run -d -p 3000:3000 grafana/grafana
```

现在我们可以通过URL [http://localhost:3000/](http://localhost:3000/)访问Grafana主页。

**此时，我们需要配置一个或多个数据源**。这可以是前面讨论的任何指标数据库或各种其他受支持的工具。

配置数据源后，我们可以构建一个新的仪表板或导入一个执行我们想要的操作的仪表板。

<img src="../assets/img_6.png">

## 4. 总结

在本文中，我们研究了如何为Spring Boot应用程序创建自托管监控解决方案。

我们查看了Spring Boot轻松支持的三个指标数据库，并了解了如何在本地运行它们。

我们还简要介绍了Grafana，这是一个强大的可视化工具，可以显示来自各种来源的指标数据。
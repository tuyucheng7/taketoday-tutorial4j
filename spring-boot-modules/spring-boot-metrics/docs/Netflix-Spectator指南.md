## 1. 概述

[Spectator](https://github.com/Netflix/spectator)是一个用于为维度时间序列后端系统检测代码和收集数据的库。Spectator 起源于 Netflix，用于各种指标收集，与之配套使用的后端系统主要是[Atlas](https://github.com/Netflix/atlas)。

在本教程中，我们将了解 Spectator 提供的内容以及我们如何使用它来收集指标。

## 2.Maven依赖

在深入研究实际实现之前，让我们将[Spectator](https://search.maven.org/artifact/com.netflix.spectator/spectator-api)依赖项添加到 pom.xml 文件中：

```xml
<dependency>
    <groupId>com.netflix.spectator</groupId>
    <artifactId>spectator-api</artifactId>
    <version>1.0.11</version>
</dependency>
```

spectator-api是核心旁观者库。

## 3. Registry、Meter和 Metrics基础知识

在我们开始深入研究这个库之前，我们应该首先了解Registry、Meter和 Metrics的基础知识。

-   [注册表](https://www.javadoc.io/doc/com.netflix.spectator/spectator-api/0.70.2/com/netflix/spectator/api/Registry.html)是我们维护一组仪表的地方
-   [仪表](https://www.javadoc.io/static/com.netflix.spectator/spectator-api/0.38.1/com/netflix/spectator/api/Meter.html)用于收集有关我们应用程序的一组测量值，例如，计数器、计时器、仪表等。
-   指标是我们在Meter 上显示的单独测量值，例如，计数、持续时间、最大值、平均值等。

让我们进一步探索这些并了解它们是如何在 Spectator 库中使用的。

## 4.注册表

Spectator 库以Registry作为接口，带有一些内置实现，例如DefaultRegistry 和 NoopRegistry。我们还可以根据我们的要求创建自定义注册表实现。

注册表实现可以如下使用：

```java
Registry registry = new DefaultRegistry();
```

## 5.仪表

仪表主要有两种类型，即有源和无源。

### 5.1. 有源电表

这些仪表用于测量某些事件的发生。我们有三种类型的此类仪表：

-   [柜台](https://www.javadoc.io/static/com.netflix.spectator/spectator-api/0.38.1/com/netflix/spectator/api/Counter.html)
-   定时器
-   [分布概要](https://www.javadoc.io/static/com.netflix.spectator/spectator-api/0.38.1/com/netflix/spectator/api/DistributionSummary.html)

### 5.2. 无源电表

这些仪表用于在需要时获取指标的值。例如，正在运行的线程数可能是我们要衡量的指标。我们有一种这样的仪表，Gauge。

接下来，让我们详细探讨这些不同类型的仪表。

## 6.柜台

这些仪表测量事件发生的速率。例如，假设我们要测量从列表中插入或删除元素的速率。

让我们首先在初始化时向Registry对象注册一个计数器：

```java
insertCounter = registry.counter("list.insert.count");
removeCounter = registry.counter("list.remove.count");
```

在这里，我们可以允许用户使用依赖注入的任何注册表实现。

现在，我们可以增加或减少Counter meter 以分别添加到列表或从列表中删除：

```java
requestList.add(element);
insertCounter.increment();

requestList.remove(0);
removeCounter.increment();
```

这样我们就可以生成两个meter，后面我们可以把metrics推送到Atlas中进行可视化。

## 7. 定时器

这些仪表测量在某些事件上花费的时间。Spectator 支持两种类型的计时器：

-   [定时器](https://www.javadoc.io/static/com.netflix.spectator/spectator-api/0.38.1/com/netflix/spectator/api/Timer.html)
-   [长任务定时器](https://www.javadoc.io/static/com.netflix.spectator/spectator-api/0.38.1/com/netflix/spectator/api/LongTaskTimer.html)

### 7.1. 定时器

这些定时器主要用于测量持续时间短的事件。因此，他们通常衡量事件完成后花费的时间。

首先，我们需要在注册表中注册这个仪表：

```java
requestLatency = registry.timer("app.request.latency");
```

接下来，我们可以调用Timer的record()方法来测量处理请求所花费的时间：

```java
requestLatency.record(() -> handleRequest(input));
```

### 7.2. 长任务定时器

这些计时器主要用于测量长时间运行任务的持续时间。因此，即使事件正在处理中，我们也可以查询这些计时器。这也是Gauge 的一种。 当事件正在进行时，我们可以看到 持续时间 和activeTasks等指标。

同样，作为第一步，我们需要注册这个仪表：

```java
refreshDuration = LongTaskTimer.get(registry, registry.createId("metadata.refreshDuration"));
```

接下来，我们可以使用LongTaskTimer来启动和停止围绕长时间运行的任务的测量：

```java
long taskId = refreshDuration.start();
try {
    Thread.sleep(input);
    return "Done";
} catch (InterruptedException e) {
    e.printStackTrace();
    throw e;
} finally {
    refreshDuration.stop(taskId);
}
```

## 8. 仪表

正如我们之前讨论的，仪表是无源仪表。因此，这些给出了在任何时间点为正在运行的任务采样的值。因此，例如，如果我们想知道 JVM 中正在运行的线程数或任何时间点的堆内存使用情况，我们就会使用它。

我们有两种类型的仪表：

-   轮询仪表
-   有源仪表

### 8.1. 轮询仪表

这种类型的仪表在后台轮询正在运行的任务的值。它在它监视的任务上创建一个挂钩。因此，无需更新此仪表中的值。

现在，让我们看看如何使用这个量规来监控List的大小：

```java
PolledMeter.using(registry)
  .withName("list.size")
  .monitorValue(listSize);
```

在这里， PolledMeter 是允许 使用monitorValue()方法对listSize进行后台轮询的类。此外，listSize 是跟踪样本列表大小的变量。

### 8.2. 有源仪表

这种类型的仪表需要定期手动更新与监控任务更新相关的值。以下是使用主动仪表的示例：

```java
gauge = registry.gauge("list.size");
```

我们首先在注册表中注册这个仪表。然后，我们在列表中添加或删除元素时手动更新它：

```java
list.add(element);
gauge.set(listSize);
list.remove(0);
gauge.set(listSize);
```

## 9.分布总结

现在，我们将研究另一个称为DistributionSummary 的仪表。它跟踪事件的分布。该仪表可以测量请求负载的大小。例如，我们将使用DistributionSummary来衡量请求的大小。

首先，一如既往，我们在注册表中注册这个仪表：

```java
distributionSummary = registry.distributionSummary("app.request.size");
```

现在，我们可以使用这个类似于计时器的计量器来记录请求的大小：

```java
distributionSummary.record((long) input.length());
handleRequest();
```

## 10. 观察者 vs. 伺服 vs. 千分尺

[Servo](https://www.baeldung.com/netflix-servo)也是一个衡量不同代码指标的库。Spectator 是 Servo 的继任者，由 Netflix 打造。Spectator 最初是为Java8 推出的，从未来支持的角度来看，它是一个更好的选择。

这些 Netflix 库是市场上可用于衡量不同指标的各种选项之一。我们总是可以单独使用它们，或者我们可以选择像[Micrometer](https://www.baeldung.com/micrometer)这样的外观。Micrometer 让用户可以轻松地在不同的度量测量库之间切换。因此，它还允许选择不同的后端监控系统。

## 11.总结

在本文中，我们介绍了 Spectator，这是 Netflix 的一个用于度量指标的库。此外，我们还研究了其各种有源和无源仪表的使用情况。我们可以将检测数据推送并发布到时间序列数据库Atlas。
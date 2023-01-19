## 1. 概述

在本文中，我们将了解如何在Java中测量运行时间。虽然这听起来很容易，但我们必须注意一些陷阱。

我们将探索标准Java类和提供测量经过时间功能的外部包。

## 2. 简单测量

### 2.1. 当前时间毫秒()

当我们在Java中遇到测量经过时间的需求时，我们可能会尝试这样做：

```java
long start = System.currentTimeMillis();
// ...
long finish = System.currentTimeMillis();
long timeElapsed = finish - start;
```

如果我们看一下代码，它就非常有意义。我们在开始时得到一个时间戳，当代码完成时我们得到另一个时间戳。经过的时间是这两个值之间的差值。

但是，结果可能并且将会不准确，因为System.currentTimeMillis()测量的是挂钟时间。挂钟时间可能会因多种原因而改变，例如，改变系统时间会影响结果，或者闰秒会破坏结果。

### 2.2. 纳米时间()

java.lang.System类中的另一个方法 是nanoTime()。如果我们查看[Java 文档](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/System.html#nanoTime())，我们会发现以下语句：

“这种方法只能用于测量经过的时间，与任何其他系统或挂钟时间概念无关。”

让我们使用它：

```java
long start = System.nanoTime();
// ...
long finish = System.nanoTime();
long timeElapsed = finish - start;
```

代码和之前基本一样。唯一的区别是用于获取时间戳的方法—— nanoTime()而不是currentTimeMillis()。

我们还要注意nanoTime()显然以纳秒为单位返回时间。因此，如果用不同的时间单位测量经过的时间，我们必须相应地进行转换。

例如，要转换为毫秒，我们必须将以纳秒为单位的结果除以 1.000.000。

nanoTime()的另一个缺陷是，即使它提供纳秒级精度，它也不能保证纳秒级分辨率(即值更新的频率)。

但是，它确实保证分辨率至少与 currentTimeMillis()的一样好。

## 3.Java 8

如果我们使用Java8——我们可以尝试新的[java.time.Instant](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/Instant.html) 和[java.time.Duration](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/Duration.html)类。两者都是不可变的、线程安全的并且使用它们自己的时间尺度， 即Java 时间尺度，新的java.time API中的所有类也是如此。

### 3.1.Java时标

传统的时间计量方式是把一天分成 24 小时 60 分 60 秒，这样一天就有 86.400 秒。然而，太阳日并不总是一样长。

UTC 时标实际上允许一天有 86.399 或 86.401 SI 秒。SI 秒是科学的“标准国际秒”，由铯 133 原子的辐射周期定义。这是保持一天与太阳对齐所必需的。

Java Time-Scale 将每个日历日精确划分为 86.400 个细分，称为秒。没有闰秒。

### 3.2. 即时课堂

Instant类表示时间轴上的一个瞬间。基本上，它是自标准Java纪元1970-01-01T00:00:00Z以来的数字时间戳。

为了获取当前时间戳，我们可以使用 Instant.now()静态方法。此方法允许传入可选的[Clock](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/Clock.html) 参数。如果省略，它将使用默认时区的系统时钟。

我们可以将开始和结束时间存储在两个变量中，如前面的示例所示。接下来，我们可以计算两个时刻之间经过的时间。

我们还可以使用 Duration类及其 between()方法来获取两个Instant对象之间的持续时间。最后，我们需要将Duration转换为毫秒：

```java
Instant start = Instant.now();
// CODE HERE        
Instant finish = Instant.now();
long timeElapsed = Duration.between(start, finish).toMillis();
```

## 4.秒表

转到库，Apache Commons Lang 提供了 可用于测量经过时间的StopWatch类。

### 4.1. Maven 依赖

我们可以通过更新 pom.xml 来获取最新版本：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

[可以在此处](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.commons" AND a%3A"commons-lang3")检查最新版本的依赖项 。

### 4.2. 用秒表测量经过的时间

首先，我们需要获取类的一个实例，然后我们可以简单地测量经过的时间：

```java
StopWatch watch = new StopWatch();
watch.start();
```

一旦我们的手表开始运行，我们就可以执行我们想要进行基准测试的代码，然后在最后，我们只需调用 stop ()方法。最后，为了获得实际结果，我们调用getTime()：

```java
watch.stop();
System.out.println("Time Elapsed: " + watch.getTime()); // Prints: Time Elapsed: 2501
```

StopWatch 有一些额外的辅助方法，我们可以使用它们来暂停或恢复我们的测量。如果我们需要使我们的基准测试更复杂，这可能会有所帮助。

最后，请注意该类不是线程安全的。

## 5.总结

在Java中有多种测量时间的方法。我们通过使用currentTimeMillis()介绍了一种非常“传统”(且不准确)的方法。此外，我们检查了 Apache Common 的StopWatch并查看了Java8 中可用的新类。

总的来说，对于经过时间的简单和正确的测量， nanoTime()方法就足够了。它的输入也比currentTimeMillis()更短。

但是，请注意，为了进行适当的基准测试，我们可以使用JavaMicrobenchmark Harness (JMH) 等框架，而不是手动测量时间。这个主题超出了本文的范围，但我们 [在这里](https://www.baeldung.com/java-microbenchmark-harness)进行了探讨。
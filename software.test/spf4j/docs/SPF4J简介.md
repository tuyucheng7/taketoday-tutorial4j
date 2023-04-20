## 1. 概述

性能测试是一项经常被推到软件开发周期最后阶段的活动。我们通常依靠[Java 分析器](https://www.baeldung.com/java-profilers)来帮助解决性能问题。

在本教程中，我们将介绍Java简单性能框架 (SPF4J)。它为我们提供了可以添加到我们代码中的 API。因此，我们可以使性能监控成为我们组件的一个组成部分。

## 2. Metrics 捕获和可视化的基本概念

在我们开始之前，让我们尝试使用一个简单的示例来理解指标捕获和可视化的概念。

假设我们有兴趣监控应用商店中新推出的应用的下载情况。为了学习，让我们考虑手动做这个实验。

### 2.1. 捕获指标

首先，我们需要决定需要测量什么。我们感兴趣的指标是下载/分钟。因此，我们将测量下载次数。

第二，我们需要多久进行一次测量？让我们决定“每分钟一次”。

最后，我们应该监测多长时间？让我们决定“一小时”。

有了这些规则，我们就可以进行实验了。实验结束后，我们可以看到结果：

```bash
Time	Cumulative Downloads	Downloads/min
----------------------------------------------
T       497                     0  
T+1     624                     127
T+2     676                     52
...     
T+14    19347                   17390
T+15    19427                   80
...  
T+22    27195                   7350
...  
T+41    41321                   11885
...   
T+60    43395                   40
```

前两列——时间和累计下载——是我们观察到的直接值。第三列，downloads/min ，是一个派生值，计算为当前和以前累积下载值之间的差异。这为我们提供了该时间段内的实际下载次数。

### 2.2. 可视化指标

让我们绘制一个时间与下载/分钟的简单线性图。

[![每分钟下载量图表](https://www.baeldung.com/wp-content/uploads/2019/06/graph1.png)](https://www.baeldung.com/wp-content/uploads/2019/06/graph1.png)

我们可以看到有一些峰值表明在少数情况下发生了大量下载。由于用于下载轴的线性刻度，较低的值显示为一条直线。

让我们更改下载轴以使用对数刻度(以 10 为底)并绘制对数/线性图。

[![图2](https://www.baeldung.com/wp-content/uploads/2019/06/graph2.png)](https://www.baeldung.com/wp-content/uploads/2019/06/graph2.png)

现在我们实际上开始看到较低的值。他们更接近 100 (+/-)。请注意，线性图显示平均值为703，因为它还包括峰值。

如果我们要将峰作为畸变排除在外，我们可以使用对数/线性图从我们的实验中得出总结：

-   平均下载量/分钟大约为 100 秒

## 3. 函数调用的性能监控

理解了如何捕获一个简单的指标并从前面的示例中对其进行分析后，现在让我们将其应用于一个简单的Java方法 — isPrimeNumber：

```java
private static boolean isPrimeNumber(long number) {
    for (long i = 2; i <= number / 2; i++) {
        if (number % i == 0)
            return false;
    }
    return true;
}
```

使用 SPF4J，有两种方法可以捕获指标。让我们在下一节中探讨它们。

## 4. 设置和配置

### 4.1. Maven 设置

SPF4J 为我们提供了许多用于不同目的的不同库，但对于我们的简单示例，我们只需要几个。

核心库是[spf4j-core](https://search.maven.org/search?q=g:org.spf4j AND a:spf4j-core)，它为我们提供了大部分必要的功能。

让我们将其添加为 Maven 依赖项：

```xml
<dependency>
    <groupId>org.spf4j</groupId>
    <artifactId>spf4j-core</artifactId>
    <version>8.6.10</version>
</dependency>

```

有一个更适合性能监控的库 — [spf4j-aspects](https://search.maven.org/search?q=g:org.spf4j AND a:spf4j-aspects)，它使用AspectJ。

我们将在我们的示例中探讨这一点，所以让我们也添加它：

```xml
<dependency>
    <groupId>org.spf4j</groupId>
    <artifactId>spf4j-aspects</artifactId>
    <version>8.6.10</version>
</dependency>

```

最后，SPF4J 还附带了一个简单的 UI，这对数据可视化非常有用，所以让我们也添加[spf4j-ui](https://search.maven.org/search?q=g:org.spf4j AND a:spf4j-ui)：

```xml
<dependency>
    <groupId>org.spf4j</groupId>
    <artifactId>spf4j-ui</artifactId>
    <version>8.6.10</version>
</dependency>
```

### 4.2. 输出文件的配置

SPF4J 框架将数据写入时间序列数据库 (TSDB)，也可以选择写入文本文件。

让我们配置它们并设置系统属性spf4j.perf.ms.config：

```java
public static void initialize() {
  String tsDbFile = System.getProperty("user.dir") + File.separator + "spf4j-performance-monitoring.tsdb2";
  String tsTextFile = System.getProperty("user.dir") + File.separator + "spf4j-performance-monitoring.txt";
  LOGGER.info("nTime Series DB (TSDB) : {}nTime Series text file : {}", tsDbFile, tsTextFile);
  System.setProperty("spf4j.perf.ms.config", "TSDB@" + tsDbFile + "," + "TSDB_TXT@" + tsTextFile);
}
```

### 4.3. 记录器和源

SPF4J 框架的核心能力是记录、聚合和保存指标，因此在分析时不需要进行后处理。它通过使用MeasurementRecorder和MeasurementRecorderSource类来实现。

这两个类提供了两种不同的方式来记录指标。关键区别在于MeasurementRecorder可以从任何地方调用，而MeasurementRecorderSource仅与注解一起使用。

该框架为我们提供了一个RecorderFactory类来为不同类型的聚合创建记录器和记录器源类的实例：

-   createScalableQuantizedRecorder()和createScalableQuantizedRecorderSource()
-   createScalableCountingRecorder()和createScalableCountingRecorderSource()
    
-   createScalableMinMaxAvgRecorder()和createScalableMinMaxAvgRecorderSource()
-   createDirectRecorder()和createDirectRecorderSource()

对于我们的示例，让我们选择可扩展的量化聚合。

### 4.4. 创建记录器

首先，让我们创建一个辅助方法来创建MeasurementRecorder的实例：

```java
public static MeasurementRecorder getMeasurementRecorder(Object forWhat) {
    String unitOfMeasurement = "ms";
    int sampleTimeMillis = 1_000;
    int factor = 10;
    int lowerMagnitude = 0;
    int higherMagnitude = 4;
    int quantasPerMagnitude = 10;

    return RecorderFactory.createScalableQuantizedRecorder(
      forWhat, unitOfMeasurement, sampleTimeMillis, factor, lowerMagnitude, 
      higherMagnitude, quantasPerMagnitude);
}
```

让我们看看不同的设置：

-   unitOfMeasurement – 被测量的单位值 – 对于性能监控场景，一般是时间单位
-   sampleTimeMillis – 进行测量的时间段 – 或者换句话说，进行测量的频率
-   factor – 用于绘制测量值的对数刻度的底数
-   lowerMagnitude——对数标度上的最小值——对于以 10 为底的对数，lowerMagnitude = 0 表示 10 的 0 次幂 = 1
-   higherMagnitude – 对数标度上的最大值 – 对于以 10 为底的对数，higherMagnitude = 4 表示 10 的 4 次幂 = 10,000
-   quantasPerMagnitude – 一个幅度内的部分数量 – 如果一个幅度在 1,000 到 10,000 之间，则quantasPerMagnitude = 10 意味着该范围将被分为 10 个子范围

我们可以看到这些值可以根据我们的需要进行更改。因此，为不同的测量创建单独的MeasurementRecorder实例可能是个好主意。

### 4.5. 创建源

接下来，让我们使用另一个辅助方法创建MeasurementRecorderSource的实例：

```java
public static final class RecorderSourceForIsPrimeNumber extends RecorderSourceInstance {
    public static final MeasurementRecorderSource INSTANCE;
    static {
        Object forWhat = App.class + " isPrimeNumber";
        String unitOfMeasurement = "ms";
        int sampleTimeMillis = 1_000;
        int factor = 10;
        int lowerMagnitude = 0;
        int higherMagnitude = 4;
        int quantasPerMagnitude = 10;
        INSTANCE = RecorderFactory.createScalableQuantizedRecorderSource(
          forWhat, unitOfMeasurement, sampleTimeMillis, factor, 
          lowerMagnitude, higherMagnitude, quantasPerMagnitude);
    }
}
```

请注意，我们使用了与之前相同的设置值。

### 4.6. 创建配置类

现在让我们创建一个方便的Spf4jConfig类并将上述所有方法放入其中：

```java
public class Spf4jConfig {
    public static void initialize() {
        //...
    }

    public static MeasurementRecorder getMeasurementRecorder(Object forWhat) {
        //...
    }

    public static final class RecorderSourceForIsPrimeNumber extends RecorderSourceInstance {
        //...
    }
}
```

### 4.7. 配置aop.xml

SPF4J 为我们提供了注解方法的选项，在这些方法上进行性能测量和监控。它使用[AspectJ](https://www.baeldung.com/aspectj)库，该库允许将性能监控所需的额外行为添加到现有代码中，而无需修改代码本身。

让我们使用加载时编织器编织我们的类和方面，并将aop.xml放在META-INF文件夹下：

```xml
<aspectj>
    <aspects>
        <aspect name="org.spf4j.perf.aspects.PerformanceMonitorAspect" />
    </aspects>
    <weaver options="-verbose">
        <include within="com.." />
        <include within="org.spf4j.perf.aspects.PerformanceMonitorAspect" />
    </weaver>
</aspectj>
```

## 5.使用MeasurementRecorder

现在让我们看看如何使用MeasurementRecorder来记录我们测试函数的性能指标。

### 5.1. 记录指标

让我们生成 100 个随机数并在循环中调用质数检查方法。在此之前，让我们调用我们的Spf4jConfig类来进行初始化并创建 MeasureRecorder 类的实例。使用此实例，让我们调用record()方法来保存 100次 isPrimeNumber()调用所花费的个人时间：

```java
Spf4jConfig.initialize();
MeasurementRecorder measurementRecorder = Spf4jConfig
  .getMeasurementRecorder(App.class + " isPrimeNumber");
Random random = new Random();
for (int i = 0; i < 100; i++) {
    long numberToCheck = random.nextInt(999_999_999 - 100_000_000 + 1) + 100_000_000;
    long startTime = System.currentTimeMillis();
    boolean isPrime = isPrimeNumber(numberToCheck);
    measurementRecorder.record(System.currentTimeMillis() - startTime);
    LOGGER.info("{}. {} is prime? {}", i + 1, numberToCheck, isPrime);
}
```

### 5.2. 运行代码

我们现在准备测试简单函数isPrimeNumber () 的性能。

让我们运行代码并查看结果：

```bash
Time Series DB (TSDB) : E:Projectsspf4j-core-appspf4j-performance-monitoring.tsdb2
Time Series text file : E:Projectsspf4j-core-appspf4j-performance-monitoring.txt
1. 406704834 is prime? false
...
9. 507639059 is prime? true
...
20. 557385397 is prime? true
...
26. 152042771 is prime? true
...
100. 841159884 is prime? false
```

### 5.3. 查看结果

让我们通过从项目文件夹运行命令来启动 SPF4J UI：

```bash
java -jar target/dependency-jars/spf4j-ui-8.6.9.jar
```

这将调出一个桌面 UI 应用程序。然后，从菜单中选择File > Open。之后，让我们使用浏览窗口找到spf4j-performance-monitoring.tsdb2文件并将其打开。

我们现在可以看到一个新窗口打开，其中包含一个树视图，其中包含我们的文件名和一个子项。让我们单击子项，然后单击其上方的Plot按钮。

这将生成一系列图表。

第一张图，测量分布，是我们之前看到的对数线性图的变体。此图还显示了基于计数的热图。

[![图3](https://www.baeldung.com/wp-content/uploads/2019/06/graph3.png)](https://www.baeldung.com/wp-content/uploads/2019/06/graph3.png)

第二张图显示了聚合数据，例如最小值、最大值和平均值：

[![图4](https://www.baeldung.com/wp-content/uploads/2019/06/graph4.png)](https://www.baeldung.com/wp-content/uploads/2019/06/graph4.png)

最后一张图显示了测量次数与时间的关系：

[![图5](https://www.baeldung.com/wp-content/uploads/2019/06/graph5.png)](https://www.baeldung.com/wp-content/uploads/2019/06/graph5.png)

## 6.使用MeasurementRecorderSource

在上一节中，我们不得不围绕我们的功能编写额外的代码来记录测量值。在本节中，让我们使用另一种方法来避免这种情况。

### 6.1. 记录指标

首先，我们将删除为捕获和记录指标添加的额外代码：

```java
Spf4jConfig.initialize();
Random random = new Random();
for (int i = 0; i < 50; i++) {
    long numberToCheck = random.nextInt(999_999_999 - 100_000_000 + 1) + 100_000_000;
    isPrimeNumber(numberToCheck);
}
```

接下来，让我们使用@PerformanceMonitor注解isPrimeNumber()方法，而不是所有这些样板文件：

```java
@PerformanceMonitor(
  warnThresholdMillis = 1,
  errorThresholdMillis = 100, 
  recorderSource = Spf4jConfig.RecorderSourceForIsPrimeNumber.class)
private static boolean isPrimeNumber(long number) {
    //...
}
```

让我们看看不同的设置：

-   warnThresholdMillis – 允许方法在没有警告消息的情况下运行的最长时间
-   errorThresholdMillis – 允许方法在没有错误消息的情况下运行的最长时间
-   recorderSource —— MeasurementRecorderSource的一个实例

### 6.2. 运行代码

让我们先进行 Maven 构建，然后通过传递Java代理来执行代码：

```bash
java -javaagent:target/dependency-jars/aspectjweaver-1.8.13.jar -jar target/spf4j-aspects-app.jar
```

我们看到结果：

```bash
Time Series DB (TSDB) : E:Projectsspf4j-aspects-appspf4j-performance-monitoring.tsdb2
Time Series text file : E:Projectsspf4j-aspects-appspf4j-performance-monitoring.txt

[DEBUG] Execution time 0 ms for execution(App.isPrimeNumber(..)), arguments [555031768]
...
[ERROR] Execution time  2826 ms for execution(App.isPrimeNumber(..)) exceeds error threshold of 100 ms, arguments [464032213]
...
```

我们可以看到 SPF4J 框架记录了每个方法调用所花费的时间。并且只要它超过errorThresholdMillis值 100 毫秒，它就会将其记录为错误。传递给该方法的参数也被记录下来。

### 6.3. 查看结果

我们可以使用与之前使用 SPF4J UI 相同的方式查看结果，因此我们可以参考上一节。

## 七. 总结

在本文中，我们讨论了捕获和可视化指标的基本概念。

然后我们通过一个简单的例子了解了SPF4J框架的性能监控能力。我们还使用内置的 UI 工具来可视化数据。
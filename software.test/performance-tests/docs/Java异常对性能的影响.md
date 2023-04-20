## 1. 概述

在Java中，异常通常被认为是昂贵的，不应该用于流控制。本教程将**证明这种看法是正确的，并查明导致性能问题的原因**。

## 2. 搭建环境

在编写代码评估性能成本之前，我们需要搭建一个基准测试环境。

### 2.1 Java Microbenchmark Harness

测量异常开销并不像在简单循环中执行方法并记录总时间那么容易。

原因是即时编译器会阻碍并优化代码。这种优化可能会使代码的性能比在生产环境中的实际性能更好。换句话说，**它可能会产生假阳性结果**。

为了创建可以减轻JVM优化的受控环境，我们将使用[Java Microbenchmark Harness](https://openjdk.java.net/projects/code-tools/jmh/)，或简称JMH。

以下小节将介绍如何设置基准测试环境，而不会深入介绍JMH的细节。有关此工具的更多信息，请查看我们的[Java微基准测试](https://www.baeldung.com/java-microbenchmark-harness)教程。

### 2.2 获取JMH工件

要获取JMH工件，请将以下两个依赖项添加到POM：

```xml
<dependency>
    <groupId>org.openjdk.jmh</groupId>
    <artifactId>jmh-core</artifactId>
    <version>1.35</version>
</dependency>
<dependency>
    <groupId>org.openjdk.jmh</groupId>
    <artifactId>jmh-generator-annprocess</artifactId>
    <version>1.35</version>
</dependency>
```

[JMH Core](https://central.sonatype.com/artifact/org.openjdk.jmh/jmh-core/1.36)和[JMH Annotation Processor](https://central.sonatype.com/artifact/org.openjdk.jmh/jmh-generator-annprocess/1.36)的最新版本请参考Maven Central。

### 2.3 基准类

我们需要一个类来保存基准：

```java
@Fork(1)
@Warmup(iterations = 2)
@Measurement(iterations = 10)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ExceptionBenchmark {
    private static final int LIMIT = 10_000;
    // benchmarks go here
}
```

让我们看一下上面显示的JMH注解：

-   @Fork：指定JMH必须生成新进程以运行基准测试的次数。我们将它的值设置为1，只生成一个进程，避免等待太长时间才能看到结果
-   @Warmup：携带预热参数。iterations元素为2表示在计算结果时忽略前两次运行
-   @Measurement：携带测量参数。迭代值为10表示JMH将每个方法执行10次
-   @BenchmarkMode：这是JHM收集执行结果的方式。AverageTime值要求JMH计算方法完成其操作所需的平均时间
-   @OutputTimeUnit：表示输出时间单位，本例为毫秒

此外，类体内有一个静态字段，即LIMIT。这是每个方法主体中的迭代次数。

### 2.4 执行基准测试

要执行基准测试，我们需要一个main方法：

```java
public class MappingFrameworksPerformance {
    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }
}
```

我们可以将项目打包成JAR文件，在命令行运行。当然，现在这样做会产生一个空输出，因为我们还没有添加任何基准测试方法。

为了方便，我们可以在POM中加入maven-jar-plugin。这个插件允许我们在IDE中执行main方法：

```xml
<plugin>org.apache.maven.plugins</plugin>
    <artifactId>maven-jar-plugin</artifactId>
    <version>3.2.0</version>
    <configuration>
        <archive>
            <manifest>
                <mainClass>cn.tuyucheng.taketoday.performancetests.MappingFrameworksPerformance</mainClass>
            </manifest>
        </archive>
    </configuration>
</plugin>
```

最新版本的maven-jar-plugin可以在[这里](https://central.sonatype.com/artifact/org.apache.maven.plugins/maven-jar-plugin/3.3.0)找到。

## 3. 性能衡量

是时候采用一些基准测试方法来衡量性能了。这些方法中的每一个都必须带有@Benchmark注解。

### 3.1 方法正常返回

让我们从一个正常返回的方法开始；也就是说，一个不抛出异常的方法：

```java
@Benchmark
public void doNotThrowException(Blackhole blackhole) {
    for (int i = 0; i < LIMIT; i++) {
        blackhole.consume(new Object());
    }
}
```

blackhole参数引用Blackhole的一个实例。这是一个有助于防止死代码消除的JMH类，这是一种即时编译器可能执行的优化。

在这种情况下，基准测试不会抛出任何异常。事实上，**我们将使用它作为评估那些抛出异常的性能的参考**。

执行main方法会给我们一个报告：

```shell
Benchmark                               Mode  Cnt  Score   Error  Units
ExceptionBenchmark.doNotThrowException  avgt   10  0.049 ± 0.006  ms/op
```

这个结果没有什么特别的。基准测试的平均执行时间为0.049毫秒，这本身毫无意义。

### 3.2 创建和抛出异常

这是另一个抛出和捕获异常的基准：

```java
@Benchmark
public void throwAndCatchException(Blackhole blackhole) {
    for (int i = 0; i < LIMIT; i++) {
        try {
            throw new Exception();
        } catch (Exception e) {
            blackhole.consume(e);
        }
    }
}
```

让我们看一下输出：

```shell
Benchmark                                  Mode  Cnt   Score   Error  Units
ExceptionBenchmark.doNotThrowException     avgt   10   0.048 ± 0.003  ms/op
ExceptionBenchmark.throwAndCatchException  avgt   10  17.942 ± 0.846  ms/op
```

doNotThrowException方法执行时间的微小变化并不重要。这只是底层操作系统和JVM状态的波动。关键要点是**抛出异常会使方法运行速度慢数百倍**。

接下来的几小节将找出究竟是什么导致了如此巨大的差异。

### 3.3 创建异常而不抛出它

我们只是创建它，而不是创建、抛出和捕获异常：

```java
@Benchmark
public void createExceptionWithoutThrowingIt(Blackhole blackhole) {
    for (int i = 0; i < LIMIT; i++) {
        blackhole.consume(new Exception());
    }
}
```

现在，让我们执行我们声明的三个基准测试：

```shell
Benchmark                                            Mode  Cnt   Score   Error  Units
ExceptionBenchmark.createExceptionWithoutThrowingIt  avgt   10  17.601 ± 3.152  ms/op
ExceptionBenchmark.doNotThrowException               avgt   10   0.054 ± 0.014  ms/op
ExceptionBenchmark.throwAndCatchException            avgt   10  17.174 ± 0.474  ms/op
```

结果可能会出乎意料：第一种方法和第三种方法的执行时间几乎相同，而第二种方法的执行时间要短得多。

在这一点上，**很明显throw和catch语句本身是相当廉价的。另一方面，异常的产生会产生高昂的开销**。

### 3.4 在不添加堆栈跟踪的情况下抛出异常

让我们弄清楚为什么构造异常比构造普通对象要昂贵得多：

```java
@Benchmark
@Fork(value = 1, jvmArgs = "-XX:-StackTraceInThrowable")
public void throwExceptionWithoutAddingStackTrace(Blackhole blackhole) {
    for (int i = 0; i < LIMIT; i++) {
        try {
            throw new Exception();
        } catch (Exception e) {
            blackhole.consume(e);
        }
    }
}
```

此方法与3.2小节中的方法之间的唯一区别是jvmArgs元素。它的值-XX:-StackTraceInThrowable是一个JVM选项，可防止堆栈跟踪被添加到异常中。

让我们再次运行基准测试：

```shell
Benchmark                                                 Mode  Cnt   Score   Error  Units
ExceptionBenchmark.createExceptionWithoutThrowingIt       avgt   10  17.874 ± 3.199  ms/op
ExceptionBenchmark.doNotThrowException                    avgt   10   0.046 ± 0.003  ms/op
ExceptionBenchmark.throwAndCatchException                 avgt   10  16.268 ± 0.239  ms/op
ExceptionBenchmark.throwExceptionWithoutAddingStackTrace  avgt   10   1.174 ± 0.014  ms/op
```

通过不使用堆栈跟踪填充异常，我们将执行时间缩短了100多倍。显然，**遍历堆栈并将其帧添加到异常会导致我们所看到的迟缓**。

### 3.5 抛出异常并展开其堆栈跟踪

最后，让我们看看如果我们抛出异常并在捕获它时展开堆栈跟踪会发生什么：

```java
@Benchmark
public void throwExceptionAndUnwindStackTrace(Blackhole blackhole) {
    for (int i = 0; i < LIMIT; i++) {
        try {
            throw new Exception();
        } catch (Exception e) {
            blackhole.consume(e.getStackTrace());
        }
    }
}
```

结果如下：

```shell
Benchmark                                                 Mode  Cnt    Score   Error  Units
ExceptionBenchmark.createExceptionWithoutThrowingIt       avgt   10   16.605 ± 0.988  ms/op
ExceptionBenchmark.doNotThrowException                    avgt   10    0.047 ± 0.006  ms/op
ExceptionBenchmark.throwAndCatchException                 avgt   10   16.449 ± 0.304  ms/op
ExceptionBenchmark.throwExceptionAndUnwindStackTrace      avgt   10  326.560 ± 4.991  ms/op
ExceptionBenchmark.throwExceptionWithoutAddingStackTrace  avgt   10    1.185 ± 0.015  ms/op
```

仅仅通过展开堆栈跟踪，我们就看到执行持续时间增加了大约20倍。换句话说，**如果我们除了抛出异常之外还从异常中提取堆栈跟踪，性能会更差**。

## 4. 总结

在本教程中，我们分析了异常对性能的影响。具体来说，它发现性能成本主要在于将堆栈跟踪添加到异常中。如果此堆栈跟踪随后展开，开销将变得更大。

由于抛出和处理异常的代价很高，因此我们不应该将它用于正常的程序流程。相反，正如其名称所暗示的那样，异常应该只用于特殊情况。
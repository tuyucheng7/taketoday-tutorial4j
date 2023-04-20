## 1. 简介

这篇快速文章重点介绍JMH(Java Microbenchmark Harness)。首先，我们熟悉API并学习其基础知识。然后我们会看到一些在编写微基准测试时应该考虑的最佳实践。

简而言之，JMH负责JVM预热和代码优化路径之类的事情，使基准测试尽可能简单。

## 2. 入门

首先，我们实际上可以继续使用Java 8并简单地定义依赖项：

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

最新版本的[JMH Core](https://central.sonatype.com/artifact/org.openjdk.jmh/jmh-core/1.36)和[JMH Annotation Processor](https://central.sonatype.com/artifact/org.openjdk.jmh/jmh-generator-annprocess/1.36)可以在Maven Central中找到。

接下来，使用@Benchmark注解创建一个简单的基准(在任何公共类中)：

```java
@Benchmark
public void init() {
    // Do nothing
}
```

然后我们添加启动基准测试过程的主类：

```java
public class BenchmarkRunner {
    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }
}
```

现在运行BenchmarkRunner将执行我们可以说有点无用的基准测试。运行完成后，将显示一个汇总表：

```shell
# Run complete. Total time: 00:06:45
Benchmark      Mode  Cnt Score            Error        Units
BenchMark.init thrpt 200 3099210741.962 ± 17510507.589 ops/s
```

## 3. 基准类型

JMH支持一些可能的基准：Throughput、AverageTime、SampleTime和SingleShotTime。这些可以通过@BenchmarkMode注解进行配置：

```java
@Benchmark
@BenchmarkMode(Mode.AverageTime)
public void init() {
    // Do nothing
}
```

生成的表将具有平均时间指标(而不是吞吐量)：

```shell
# Run complete. Total time: 00:00:40
Benchmark Mode Cnt  Score Error Units
BenchMark.init avgt 20 ≈ 10⁻⁹ s/op
```

## 4. 配置预热和执行

通过使用@Fork注解，我们可以设置基准测试的执行方式：value参数控制基准测试执行的次数，warmup参数控制基准测试在收集结果之前试运行的次数，例如:

```java
@Benchmark
@Fork(value = 1, warmups = 2)
@BenchmarkMode(Mode.Throughput)
public void init() {
    // Do nothing
}
```

这指示JMH在进行实时基准测试之前运行两个预热fork并丢弃结果。

此外，@Warmup注解可用于控制预热迭代次数。例如，@Warmup(iterations = 5)告诉JMH五次预热迭代就足够了，而不是默认的20次。

## 5. 状态

现在让我们来看看如何利用State来执行一个不太琐碎但更具指示性的哈希算法基准测试任务。假设我们决定通过对密码进行数百次哈希处理来增加对密码数据库的字典攻击的额外保护。

我们可以使用State对象来探索性能影响：

```java
@State(Scope.Benchmark)
public class ExecutionPlan {

    @Param({ "100", "200", "300", "500", "1000" })
    public int iterations;

    public Hasher murmur3;

    public String password = "4v3rys3kur3p455w0rd";

    @Setup(Level.Invocation)
    public void setUp() {
        murmur3 = Hashing.murmur3_128().newHasher();
    }
}
```

然后，我们的基准测试方法将如下所示：

```java
@Fork(value = 1, warmups = 1)
@Benchmark
@BenchmarkMode(Mode.Throughput)
public void benchMurmur3_128(ExecutionPlan plan) {
    for (int i = plan.iterations; i > 0; i--) {
        plan.murmur3.putString(plan.password, Charset.defaultCharset());
    }

    plan.murmur3.hash();
}
```

在这里，当JMH将其传递给基准方法时，字段iterations将使用来自@Param注解的适当值进行填充。@Setup注解方法在每次调用基准测试之前被调用，并创建一个新的Hasher确保隔离。

执行完成后，我们会得到类似于下面的结果：

```shell
# Run complete. Total time: 00:06:47

Benchmark                   (iterations)   Mode  Cnt      Score      Error  Units
BenchMark.benchMurmur3_128           100  thrpt   20  92463.622 ± 1672.227  ops/s
BenchMark.benchMurmur3_128           200  thrpt   20  39737.532 ± 5294.200  ops/s
BenchMark.benchMurmur3_128           300  thrpt   20  30381.144 ±  614.500  ops/s
BenchMark.benchMurmur3_128           500  thrpt   20  18315.211 ±  222.534  ops/s
BenchMark.benchMurmur3_128          1000  thrpt   20   8960.008 ±  658.524  ops/s
```

## 6. 死代码消除

**运行微基准测试时，了解优化非常重要**。否则，它们可能会以非常误导的方式影响基准测试结果。

为了使事情更具体一点，让我们考虑一个例子：

```java
@Benchmark
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public void doNothing() {
}

@Benchmark
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public void objectCreation() {
    new Object();
}
```

我们预计对象分配的成本比什么都不做要高。但是，如果我们运行基准测试：

```shell
Benchmark                 Mode  Cnt  Score   Error  Units
BenchMark.doNothing       avgt   40  0.609 ± 0.006  ns/op
BenchMark.objectCreation  avgt   40  0.613 ± 0.007  ns/op
```

显然在[TLAB](https://alidg.me/blog/2019/6/21/tlab-jvm)中找到一个位置，创建和初始化一个对象几乎是免费的！仅仅通过查看这些数字，我们就应该知道这里有些东西并没有完全加起来。

**在这里，我们是死代码消除的受害者**。编译器非常擅长优化冗余代码。事实上，这正是JIT编译器在这里所做的。

**为了防止这种优化，我们应该以某种方式欺骗编译器，让它认为代码被其他组件使用**。实现此目的的一种方法是返回创建的对象：

```java
@Benchmark
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public Object pillarsOfCreation() {
    return new Object();
}
```

另外，我们可以让[Blackhole](http://javadox.com/org.openjdk.jmh/jmh-core/1.6.3/org/openjdk/jmh/infra/Blackhole.html)消耗它：

```java
@Benchmark
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public void blackHole(Blackhole blackhole) {
    blackhole.consume(new Object());
}
```

**让Blackhole消耗对象是说服JIT编译器不应用死代码消除优化的一种方式**。无论如何，如果我们再次运行这些基准测试，这些数字会更有意义：

```bash
Benchmark                    Mode  Cnt  Score   Error  Units
BenchMark.blackHole          avgt   20  4.126 ± 0.173  ns/op
BenchMark.doNothing          avgt   20  0.639 ± 0.012  ns/op
BenchMark.objectCreation     avgt   20  0.635 ± 0.011  ns/op
BenchMark.pillarsOfCreation  avgt   20  4.061 ± 0.037  ns/op
```

## 7. 常量折叠

让我们考虑另一个例子：

```java
@Benchmark
public double foldedLog() {
    int x = 8;

    return Math.log(x);
}
```

**基于常量的计算可能会返回完全相同的输出，而不管执行次数如何**。因此，JIT编译器很有可能会用其结果替换对数函数调用：

```java
@Benchmark
public double foldedLog() {
    return 2.0794415416798357;
}
```

**这种形式的部分评估称为常量折叠**。在这种情况下，常量折叠完全避免了Math.log调用，这是基准测试的重点。

为了防止常量折叠，我们可以将常量状态封装在一个状态对象中：

```java
@State(Scope.Benchmark)
public static class Log {
    public int x = 8;
}

@Benchmark
public double log(Log input) {
     return Math.log(input.x);
}
```

如果我们相互运行这些基准测试：

```shell
Benchmark             Mode  Cnt          Score          Error  Units
BenchMark.foldedLog  thrpt   20  449313097.433 ± 11850214.900  ops/s
BenchMark.log        thrpt   20   35317997.064 ±   604370.461  ops/s
```

显然，与foldedLog相比，log基准测试正在做一些严肃的工作，这是明智的。

## 8. 总结

本教程重点介绍并展示了Java的微基准测试工具。
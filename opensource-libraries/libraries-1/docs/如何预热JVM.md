## 1. 概述

JVM 是有史以来最古老但功能最强大的虚拟机之一。

在本文中，我们快速了解了预热 JVM 的含义以及如何进行预热。

## 2. JVM 架构基础

[每当一个新的 JVM 进程启动时，所有需要的类都会被ClassLoader](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/ClassLoader.html)的一个实例加载到内存中。这个过程分三个步骤进行：

1.  Bootstrap 类加载： “ Bootstrap 类加载器”将Java代码和基本的Java类(如java.lang.Object )加载到内存中。这些加载的类位于JRElibrt.jar中。
2.  扩展类加载：ExtClassLoader 负责加载位于java.ext.dirs路径的所有 JAR 文件。在非 Maven 或非基于 Gradle 的应用程序中，开发人员手动添加 JAR，所有这些类都在此阶段加载。
3.  应用程序类加载：AppClassLoader 加载位于应用程序类路径中的所有类。

此初始化过程基于延迟加载方案。

## 3. 什么是 JVM 预热

一旦类加载完成，所有重要的类(在进程启动时使用)都会被推送到[JVM 缓存(本机代码)](https://www.ibm.com/support/knowledgecenter/en/SSAW57_8.5.5/com.ibm.websphere.nd.doc/ae/rdyn_tunediskcache.html)中——这使得它们在运行时可以更快地访问。其他类是根据每个请求加载的。

对JavaWeb 应用程序发出的第一个请求通常比进程生命周期内的平均响应时间慢得多。这个预热期通常可以归因于延迟类加载和即时编译。

牢记这一点，对于低延迟应用程序，我们需要预先缓存所有类——以便在运行时访问它们时立即可用。

这个调整 JVM 的过程称为预热。

## 4.分层编译

由于 JVM 的良好架构，在应用程序生命周期中，经常使用的方法被加载到本机缓存中。

我们可以利用此属性在应用程序启动时将关键方法强制加载到缓存中。为此，我们需要设置一个名为Tiered Compilation的 VM 参数：

```bash
-XX:CompileThreshold -XX:TieredCompilation
```

通常，VM 使用解释器来收集有关提供给编译器的方法的分析信息。在分层方案中，除了解释器之外，客户端编译器还用于生成收集有关自身分析信息的方法的编译版本。

由于编译代码比解释代码快得多，因此程序在分析阶段执行时具有更好的性能。

[由于记录在案的错误](https://issues.jboss.org/browse/JBEAP-26)，在启用了此 VM 参数的 JBoss 和 JDK 版本 7 上运行的应用程序往往会在一段时间后崩溃。该问题已在 JDK 版本 8 中修复。

这里要注意的另一点是，为了强制加载，我们必须确保所有(或大多数)将要执行的类都需要被访问。它类似于在单元测试期间确定代码覆盖率。覆盖的代码越多，性能就会越好。

下一节将演示如何实现这一点。

## 5. 手动实现

我们可以实施替代技术来预热 JVM。在这种情况下，简单的手动预热可能包括在应用程序启动后立即重复创建不同的类数千次。

首先，我们需要用普通方法创建一个虚拟类：

```java
public class Dummy {
    public void m() {
    }
}
```

接下来，我们需要创建一个具有静态方法的类，该方法将在应用程序启动后立即执行至少 100000 次，并且每次执行时，它都会创建我们之前创建的上述虚拟类的新实例：

```java
public class ManualClassLoader {
    protected static void load() {
        for (int i = 0; i < 100000; i++) {
            Dummy dummy = new Dummy();
            dummy.m();
        }
    }
}
```

现在，为了衡量性能增益，我们需要创建一个主类。此类包含一个静态块，其中包含对ManualClassLoader 的 load()方法的直接调用。

在 main 函数中，我们再次调用ManualClassLoader 的 load()方法，并在函数调用前后以纳秒为单位捕获系统时间。最后，我们减去这些时间得到实际执行时间。

我们必须运行应用程序两次；一次在静态块中调用load()方法，一次不调用此方法：

```java
public class MainApplication {
    static {
        long start = System.nanoTime();
        ManualClassLoader.load();
        long end = System.nanoTime();
        System.out.println("Warm Up time : " + (end - start));
    }
    public static void main(String[] args) {
        long start = System.nanoTime();
        ManualClassLoader.load();
        long end = System.nanoTime();
        System.out.println("Total time taken : " + (end - start));
    }
}
```

下面的结果以纳秒为单位重现：

| 热身 | 没有热身 | 区别(％) |
| -------- | ------------ | -------------- |
| 1220056  | 8903640      | 730            |
| 1083797  | 13609530     | 1256           |
| 1026025  | 9283837      | 905            |
| 1024047  | 7234871      | 706            |
| 868782   | 9146180      | 1053           |

正如预期的那样，预热方法显示出比普通方法更好的性能。

当然，这是一个非常简单的基准测试，仅提供了对该技术影响的一些表面层面的洞察。此外，重要的是要了解，对于真实世界的应用程序，我们需要使用系统中的典型代码路径进行热身。

## 6.工具

我们还可以使用多种工具来预热 JVM。最著名的工具之一是JavaMicrobenchmark Harness，[JMH](https://openjdk.java.net/projects/code-tools/jmh/)。它通常用于微基准测试。加载后，它会反复点击代码片段并监视预热迭代周期。

要使用它，我们需要向pom.xml添加另一个依赖项：

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

[我们可以在Central Maven Repository](https://search.maven.org/classic/#search|ga|1|org.openjdk.jmh)中查看 JMH 的最新版本。

或者，我们可以使用 JMH 的 maven 插件来生成示例项目：

```shell
mvn archetype:generate 
    -DinteractiveMode=false 
    -DarchetypeGroupId=org.openjdk.jmh 
    -DarchetypeArtifactId=jmh-java-benchmark-archetype 
    -DgroupId=com.baeldung 
    -DartifactId=test 
    -Dversion=1.0
```

接下来，让我们创建一个主要方法：

```java
public static void main(String[] args) 
  throws RunnerException, IOException {
    Main.main(args);
}
```

现在，我们需要创建一个方法并使用 JMH 的@Benchmark注解对其进行注解：

```java
@Benchmark
public void init() {
    //code snippet	
}
```

在这个init方法里面，我们需要写一些需要反复执行的代码来预热。

## 7.性能基准

在过去的 20 年里，对Java的大部分贡献都与 GC(垃圾收集器)和 JIT(即时编译器)有关。在网上找到的几乎所有性能基准测试都是在已经运行了一段时间的 JVM 上完成的。然而，

但是，[北航](http://www.eecg.toronto.edu/~yuan/papers/osdi16-hottub.pdf)已经发布了一份基准报告，其中考虑了 JVM 预热时间。他们使用基于 Hadoop 和 Spark 的系统来处理海量数据：

[![虚拟机](https://www.baeldung.com/wp-content/uploads/2017/06/jvm-300x222.png)](https://www.baeldung.com/wp-content/uploads/2017/06/jvm.png)

这里的 HotTub 指定了 JVM 预热的环境。

如所见，速度提升可能非常显着，尤其是对于相对较小的读取操作——这就是值得考虑此数据的原因。

## 八. 总结

在这篇快速文章中，我们展示了 JVM 如何在应用程序启动时加载类，以及我们如何预热 JVM 以获得性能提升。

如果想继续阅读，[本书将介绍有关该主题的更多信息和指南。](https://www.safaribooksonline.com/library/view/java-performance-the/9781449363512/)
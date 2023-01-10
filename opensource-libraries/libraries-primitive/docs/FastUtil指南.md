## 1. 简介

在本教程中，我们将研究[FastUtil](http://fastutil.di.unimi.it/) 库。

首先，我们将编写其特定类型集合的一些示例。

然后，我们将分析赋予FastUtil 名称的性能。

最后，让我们看一下FastUtil的 BigArray 实用程序。

## 2.特点

FastUtilJava库旨在扩展Java集合框架。它提供特定于类型的映射、集合、列表和队列，具有更小的内存占用和快速访问和插入。FastUtil 还提供了一组实用程序，用于处理和操作大型(64 位)数组、集合和列表。

该库还包括大量用于二进制和文本文件的实用输入/输出类。

其最新版本FastUtil 8还发布了一系列[特定于类型的函数](http://fastutil.di.unimi.it/docs/it/unimi/dsi/fastutil/Function.html)，扩展了 JDK 的[函数式接口](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/function/package-summary.html)。

### 2.1. 速度

在许多情况下， FastUtil 实现是最快的。作者甚至提供了他们自己的深入[基准报告](http://java-performance.info/hashmap-overview-jdk-fastutil-goldman-sachs-hppc-koloboke-trove-january-2015/)，将其与类似的库(包括HPPC 和 Trove)进行了比较。

在本教程中，我们将使用[Java Microbench Harness (JMH)](https://www.baeldung.com/java-microbenchmark-harness)定义我们自己的基准。

## 3.全尺寸依赖

在通常的JUnit依赖项之上，我们将 在本教程中使用[FastUtils](https://search.maven.org/search?q=g:it.unimi.dsi AND a:fastutil)和[JMH](https://search.maven.org/search?q=a:jmh-core OR a:jmh-generator-annprocess)[依赖项。](https://search.maven.org/search?q=a:jmh-core OR a:jmh-generator-annprocess)

我们的 pom.xml 文件中需要以下依赖项：

```java
<dependency>
    <groupId>it.unimi.dsi</groupId>
    <artifactId>fastutil</artifactId>
    <version>8.2.2</version>
</dependency>
<dependency>
    <groupId>org.openjdk.jmh</groupId>
    <artifactId>jmh-core</artifactId>
    <version>1.35</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.openjdk.jmh</groupId>
    <artifactId>jmh-generator-annprocess</artifactId>
    <version>1.35</version>
    <scope>test</scope>
</dependency>
```

或者对于 Gradle 用户：

```java
testCompile group: 'org.openjdk.jmh', name: 'jmh-core', version: '1.19'
testCompile group: 'org.openjdk.jmh', name: 'jmh-generator-annprocess', version: '1.19'
compile group: 'it.unimi.dsi', name: 'fastutil', version: '8.2.2'
```

### 3.1. 定制的 Jar 文件

由于缺少泛型， FastUtils 生成了大量类型特定的类。不幸的是，这会导致生成一个巨大的 jar 文件。

然而，对我们来说幸运的是，FastUtils 包含一个find-deps.sh 脚本，它允许生成更小、更集中的 jar，其中仅包含我们想在应用程序中使用的类。

## 4. 特定类型的集合

在开始之前，让我们快速浏览一下[实例化特定类型集合的简单过程](https://www.baeldung.com/java-map-primitives)。让我们选择一个使用双精度存储键和值的HashMap 。 

为此， FastUtils 提供了一个[Double2DoubleMap](http://fastutil.di.unimi.it/docs/it/unimi/dsi/fastutil/doubles/Double2DoubleMap.html) 接口和一个 [Double2DoubleOpenHashMap](http://fastutil.di.unimi.it/docs/it/unimi/dsi/fastutil/doubles/Double2DoubleOpenHashMap.html) 实现：

```java
Double2DoubleMap d2dMap = new Double2DoubleOpenHashMap();
```

现在我们已经实例化了我们的类，我们可以像使用JavaCollections API 中的任何Map 一样简单地填充数据：

```java
d2dMap.put(2.0, 5.5);
d2dMap.put(3.0, 6.6);
```

最后，我们可以检查数据是否已正确添加：

```java
assertEquals(5.5, d2dMap.get(2.0));
```

### 4.1. 表现

FastUtils专注于其高性能实现。在本节中，我们将使用 JMH 来验证这一事实。让我们将JavaCollections HashSet<Integer>实现与 FastUtil 的 [IntOpenHashSet](http://fastutil.di.unimi.it/docs/it/unimi/dsi/fastutil/ints/IntOpenHashSet.html)进行比较。

首先，让我们看看如何实现 IntOpenHashSet：

```java
@Param({"100", "1000", "10000", "100000"})
public int setSize;

@Benchmark
public IntSet givenFastUtilsIntSetWithInitialSizeSet_whenPopulated_checkTimeTaken() {
    IntSet intSet = new IntOpenHashSet(setSize);
    for(int i = 0; i < setSize; i++) {
        intSet.add(i);
    }
    return intSet; 
}
```

上面，我们只是声明了IntSet 接口的IntOpenHashSet实现 。我们还 使用 @Param 注解声明了初始大小setSize 。

简而言之，这些数字被输入 JMH 以生成一系列具有不同集合大小的基准测试。

接下来，让我们使用JavaCollections 实现做同样的事情：

```java
@Benchmark
public Set<Integer> givenCollectionsHashSetWithInitialSizeSet_whenPopulated_checkTimeTaken() {
    Set<Integer> intSet = new HashSet<>(setSize);
    for(int i = 0; i < setSize; i++) {
        intSet.add(i);
    }
    return intSet;
}
```

最后，让我们运行基准测试并比较两个实现：

```java
Benchmark                                     (setSize)  Mode  Cnt     Score   Units
givenCollectionsHashSetWithInitialSizeSet...        100  avgt    2     1.460   us/op
givenCollectionsHashSetWithInitialSizeSet...       1000  avgt    2    12.740   us/op
givenCollectionsHashSetWithInitialSizeSet...      10000  avgt    2   109.803   us/op
givenCollectionsHashSetWithInitialSizeSet...     100000  avgt    2  1870.696   us/op
givenFastUtilsIntSetWithInitialSizeSet...           100  avgt    2     0.369   us/op
givenFastUtilsIntSetWithInitialSizeSet...          1000  avgt    2     2.351   us/op
givenFastUtilsIntSetWithInitialSizeSet...         10000  avgt    2    37.789   us/op
givenFastUtilsIntSetWithInitialSizeSet...        100000  avgt    2   896.467   us/op
```

这些结果清楚地表明FastUtils 实现比JavaCollections 替代方案性能更高。 

## 5. 大收藏

Fa stUtils 的另一个重要特性 是能够使用 64 位数组。默认情况下，Java 中的数组限制为 32 位。

首先，让我们看一下Integer类型的BigArrays类。[IntBigArrays](http://fastutil.di.unimi.it/docs/it/unimi/dsi/fastutil/ints/IntBigArrays.html)提供了用于处理二维整数数组的静态方法。通过使用这些提供的方法，我们基本上可以将我们的数组包装成一个更加用户友好的一维数组。

让我们来看看这是如何工作的。

首先，我们首先初始化一个一维数组，然后使用IntBigArray 的 wrap 方法将其转换为二维数组：

```java
int[] oneDArray = new int[] { 2, 1, 5, 2, 1, 7 };
int[][] twoDArray = IntBigArrays.wrap(oneDArray.clone());
```

我们应该确保使用克隆方法来确保数组的深拷贝。

现在，就像我们处理List 或 Map一样，我们可以使用get 方法访问元素 ：

```java
int firstIndex = IntBigArrays.get(twoDArray, 0);
int lastIndex = IntBigArrays.get(twoDArray, IntBigArrays.length(twoDArray)-1);
```

最后，让我们添加一些检查以确保我们的IntBigArray 返回正确的值：

```java
assertEquals(2, firstIndex);
assertEquals(7, lastIndex);
```

## 六. 总结

在本文中，我们深入 探讨了FastUtils 的核心功能。

在尝试一些BigCollections之前，我们查看了FastUtil提供 的一些特定于类型的集合。
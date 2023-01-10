##  1. 概述

在本教程中，我们将了解如何使用[Chronicle Map](https://github.com/OpenHFT/Chronicle-Map/blob/master/docs/CM_Tutorial.adoc)来存储键值对。我们还将创建简短示例来演示其行为和用法。

## 2. 什么是编年史地图？

在文档之后，“Chronicle Map 是一种超快、内存中、非阻塞、键值存储，专为低延迟和/或多进程应用程序而设计”。

简而言之，它是一个堆外键值存储。该地图不需要大量 RAM 即可正常运行。它可以根据可用磁盘容量增长。此外，它还支持在多主服务器设置中数据。

现在让我们看看如何设置和使用它。

## 3.Maven依赖

首先，我们需要将[chronicle-map 依赖](https://search.maven.org/search?q=g:net.openhft AND a:chronicle-map)项添加到我们的项目中：

```xml
<dependency>
    <groupId>net.openhft</groupId>
    <artifactId>chronicle-map</artifactId>
    <version>3.17.2</version>
</dependency>
```

## 4.编年史地图的种类

我们可以通过两种方式创建地图：作为内存地图或持久地图。

让我们详细看看这两个。

### 4.1. 内存映射

内存中的编年史地图是在服务器的物理内存中创建的地图存储。这意味着它只能在创建地图存储的 JVM 进程中访问。

让我们看一个简单的例子：

```java
ChronicleMap<LongValue, CharSequence> inMemoryCountryMap = ChronicleMap
  .of(LongValue.class, CharSequence.class)
  .name("country-map")
  .entries(50)
  .averageValue("America")
  .create();
```

为了简单起见，我们正在创建一个存储 50 个国家 ID 及其名称的地图。正如我们在代码片段中看到的，除了averageValue()配置之外，创建非常简单。这告诉映射配置映射条目值占用的平均字节数。

换句话说，在创建地图时，编年史地图确定序列化形式的值占用的平均字节数。它通过使用配置的值编组器序列化给定的平均值来做到这一点。然后它将为每个映射条目的值分配确定的字节数。

关于内存映射，我们必须注意的一件事是只有当 JVM 进程处于活动状态时才能访问数据。当进程终止时，库将清除数据。

### 4.2. 持久映射

与内存中的地图不同，该实现会将持久化的地图保存到磁盘。现在让我们看看如何创建持久化地图：

```java
ChronicleMap<LongValue, CharSequence> persistedCountryMap = ChronicleMap
  .of(LongValue.class, CharSequence.class)
  .name("country-map")
  .entries(50)
  .averageValue("America")
  .createPersistedTo(new File(System.getProperty("user.home") + "/country-details.dat"));
```

这将在指定的文件夹中创建一个名为country-details.dat的文件。如果此文件已在指定路径中可用，那么构建器实现将从该 JVM 进程打开一个指向现有数据存储的链接。

我们可以在需要的情况下使用持久化地图：

-   在创造者进程之外生存；例如，支持热应用重新部署
-   使其在服务器中全局化；例如，支持多个并发进程访问
-   充当我们将保存到磁盘的数据存储

## 五、尺寸配置

在创建 Chronicle Map 时必须配置平均值和平均键，除非我们的键/值类型是盒装基元或值接口。在我们的示例中，我们没有配置平均键，因为键类型LongValue是一个[值接口](https://github.com/OpenHFT/Chronicle-Values)。

现在，让我们看看配置平均键/值字节数的选项是什么：

-   averageValue() – 确定为映射条目的值分配的平均字节数的值
-   averageValueSize() – 为映射条目的值分配的平均字节数
-   constantValueSizeBySample() – 当值的大小始终相同时，为映射条目的值分配的字节数
-   averageKey() – 从中确定要为映射条目的键分配的平均字节数的键
-   averageKeySize() – 为映射条目的键分配的平均字节数
-   constantKeySizeBySample() – 当键的大小始终相同时，为映射条目的键分配的字节数

## 6.键值类型

在创建 Chronicle Map 时，我们需要遵循某些标准，尤其是在定义键和值时。当我们使用推荐的类型创建键和值时，映射效果最佳。

以下是一些推荐的类型：

-   值接口
-   [从Chronicle Bytes实现](https://github.com/OpenHFT/Chronicle-Bytes)Byteable接口的任何类
-   任何从 Chronicle Bytes实现BytesMarshallable接口的类；实现类应该有一个公共的无参数构造函数
-   byte[]和ByteBuffer
-   CharSequence、String和StringBuilder
-   Integer、Long和Double
-   任何实现java.io.Externalizable的类；实现类应该有一个公共的无参数构造函数
-   任何实现java.io.Serializable的类型，包括盒装基本类型(上面列出的除外)和数组类型
-   任何其他类型，如果提供了自定义序列化程序

## 7.查询编年史地图

Chronicle Map 支持单键查询，也支持多键查询。

### 7.1. 单键查询

单键查询是处理单个键的操作。ChronicleMap支持JavaMap接口和ConcurrentMap接口的所有操作：

```java
LongValue qatarKey = Values.newHeapInstance(LongValue.class);
qatarKey.setValue(1);
inMemoryCountryMap.put(qatarKey, "Qatar");

//...

CharSequence country = inMemoryCountryMap.get(key);
```

除了正常的 get 和 put 操作之外，ChronicleMap还添加了一个特殊的操作getUsing()，它可以减少检索和处理条目时的内存占用。让我们看看实际效果：

```java
LongValue key = Values.newHeapInstance(LongValue.class);
StringBuilder country = new StringBuilder();
key.setValue(1);
persistedCountryMap.getUsing(key, country);
assertThat(country.toString(), is(equalTo("Romania")));

key.setValue(2);
persistedCountryMap.getUsing(key, country);
assertThat(country.toString(), is(equalTo("India")));
```

在这里，我们使用相同的StringBuilder对象通过将其传递给getUsing()方法来检索不同键的值。它基本上重用相同的对象来检索不同的条目。在我们的例子中，getUsing()方法等效于：

```java
country.setLength(0);
country.append(persistedCountryMap.get(key));
```

### 7.2. 多键查询

可能存在我们需要同时处理多个键的用例。为此，我们可以使用queryContext()功能。queryContext ()方法将创建用于处理地图条目的上下文。

让我们首先创建一个多图并向其添加一些值：

```java
Set<Integer> averageValue = IntStream.of(1, 2).boxed().collect(Collectors.toSet());
ChronicleMap<Integer, Set<Integer>> multiMap = ChronicleMap
  .of(Integer.class, (Class<Set<Integer>>) (Class) Set.class)
  .name("multi-map")
  .entries(50)
  .averageValue(averageValue)
  .create();

Set<Integer> set1 = new HashSet<>();
set1.add(1);
set1.add(2);
multiMap.put(1, set1);

Set<Integer> set2 = new HashSet<>();
set2.add(3);
multiMap.put(2, set2);
```

要处理多个条目，我们必须锁定这些条目以防止由于并发更新而可能发生的不一致：

```java
try (ExternalMapQueryContext<Integer, Set<Integer>, ?> fistContext = multiMap.queryContext(1)) {
    try (ExternalMapQueryContext<Integer, Set<Integer>, ?> secondContext = multiMap.queryContext(2)) {
        fistContext.updateLock().lock();
        secondContext.updateLock().lock();

        MapEntry<Integer, Set<Integer>> firstEntry = fistContext.entry();
        Set<Integer> firstSet = firstEntry.value().get();
        firstSet.remove(2);

        MapEntry<Integer, Set<Integer>> secondEntry = secondContext.entry();
        Set<Integer> secondSet = secondEntry.value().get();
        secondSet.add(4);

        firstEntry.doReplaceValue(fistContext.wrapValueAsData(firstSet));
        secondEntry.doReplaceValue(secondContext.wrapValueAsData(secondSet));
    }
} finally {
    assertThat(multiMap.get(1).size(), is(equalTo(1)));
    assertThat(multiMap.get(2).size(), is(equalTo(2)));
}
```

## 8. 关闭编年史地图

现在我们已经完成了对地图的处理，让我们在地图对象上调用close()方法来释放堆外内存和与之关联的资源：

```java
persistedCountryMap.close();
inMemoryCountryMap.close();
multiMap.close();
```

这里要记住的一件事是所有地图操作必须在关闭地图之前完成。否则，JVM 可能会意外崩溃。

## 9.总结

在本教程中，我们学习了如何使用 Chronicle Map 来存储和检索键值对。尽管社区版本提供了大部分核心功能，但商业版本具有一些高级功能，例如跨多个服务器的数据和远程调用。
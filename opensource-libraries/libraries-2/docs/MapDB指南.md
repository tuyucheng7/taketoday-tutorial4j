## 1. 简介

在本文中，我们将了解[MapDB](http://www.mapdb.org/)库——一种通过类似集合的 API 访问的嵌入式数据库引擎。

我们首先探索帮助配置、打开和管理我们的数据库的核心类DB 和 DBMaker 。然后，我们将深入研究存储和检索数据的 MapDB 数据结构的一些示例。

最后，在将 MapDB 与传统数据库和Java集合进行比较之前，我们将了解一些内存模式。

## 2.在MapDB中存储数据

首先，让我们介绍一下我们将在整个教程中不断使用的两个类——DB 和 DBMaker。 DB类代表一个开放的数据库。它的方法调用用于创建和关闭存储集合的操作以处理数据库记录，以及处理事务事件。

DBMaster处理数据库配置、创建和打开。 作为配置的一部分，我们可以选择在内存中或文件系统上托管我们的数据库。

### 2.1. 一个简单的HashMap示例

为了理解这是如何工作的，让我们在内存中实例化一个新的数据库。

首先，让我们使用DBMaster 类创建一个新的内存数据库：

```java
DB db = DBMaker.memoryDB().make();
```

一旦我们的 DB 对象启动并运行，我们就可以使用它来构建一个HTreeMap 来处理我们的数据库记录：

```java
String welcomeMessageKey = "Welcome Message";
String welcomeMessageString = "Hello Baeldung!";

HTreeMap myMap = db.hashMap("myMap").createOrOpen();
myMap.put(welcomeMessageKey, welcomeMessageString);
```

HTreeMap 是 MapDB 的HashMap 实现。所以，现在我们的数据库中有数据，我们可以使用get 方法检索它：

```java
String welcomeMessageFromDB = (String) myMap.get(welcomeMessageKey);
assertEquals(welcomeMessageString, welcomeMessageFromDB);
```

最后，既然我们已经完成了数据库，我们应该关闭它以避免进一步的突变：

```java
db.close();
```

要将我们的数据存储在文件中，而不是内存中，我们需要做的就是更改实例化DB对象的方式：

```java
DB db = DBMaker.fileDB("file.db").make();
```

我们上面的例子没有使用类型参数。因此，我们坚持将我们的结果转换为适用于特定类型。在我们的下一个示例中，我们将引入 序列化 程序来消除强制转换的需要。

### 2.2. 收藏品

MapDB 包括不同的集合类型。为了演示，让我们使用 NavigableSet从我们的数据库中添加和检索一些数据，它的工作方式与对JavaSet的预期一样：

让我们从DB 对象的简单实例化开始：

```java
DB db = DBMaker.memoryDB().make();
```

接下来，让我们创建我们的 NavigableSet：

```java
NavigableSet<String> set = db
  .treeSet("mySet")
  .serializer(Serializer.STRING)
  .createOrOpen();
```

在这里，序列化程序确保来自我们数据库的输入数据使用String对象进行序列化和反序列化。

接下来，让我们添加一些数据：

```java
set.add("Baeldung");
set.add("is awesome");
```

现在，让我们检查我们的两个不同的值是否已正确添加到数据库中：

```java
assertEquals(2, set.size());
```

最后，由于这是一个集合，让我们添加一个重复的字符串并验证我们的数据库仍然只包含两个值：

```java
set.add("Baeldung");

assertEquals(2, set.size());
```

### 2.3. 交易

与传统数据库非常相似， DB 类提供了 提交 和 回滚 我们添加到数据库中的数据的方法。

要启用此功能，我们需要使用 transactionEnable 方法初始化我们的数据库 ：

```java
DB db = DBMaker.memoryDB().transactionEnable().make();
```

接下来，让我们创建一个简单的集合，添加一些数据，并将其提交到数据库：

```java
NavigableSet<String> set = db
  .treeSet("mySet")
  .serializer(Serializer.STRING)
  .createOrOpen();

set.add("One");
set.add("Two");

db.commit();

assertEquals(2, set.size());
```

现在，让我们向数据库中添加第三个未提交的字符串：

```java
set.add("Three");

assertEquals(3, set.size());
```

如果我们对我们的数据不满意，我们可以使用DB 的回滚方法回滚数据：

```java
db.rollback();

assertEquals(2, set.size());
```

### 2.4. 序列化程序

MapDB 提供了种类繁多的[序列化程序，它们处理集合中的数据](https://jankotek.gitbooks.io/mapdb/content/htreemap/#serializers)。最重要的构造参数是名称，它标识DB 对象中的单个集合：

```java
HTreeMap<String, Long> map = db.hashMap("indentification_name")
  .keySerializer(Serializer.STRING)
  .valueSerializer(Serializer.LONG)
  .create();
```

虽然建议序列化，但它是可选的，可以跳过。但是，值得注意的是，这将导致通用序列化过程变慢。

## 3. H树图

MapDB 的 HTreeMap 提供了HashMap 和 HashSet 集合来处理我们的数据库。HTreeMap是一个分段的哈希树，没有使用固定大小的哈希表。相反，它使用自动扩展的索引树，并且不会随着表的增长而重新散列所有数据。最重要的是，HTreeMap 是线程安全的并且支持使用多个段的并行写入。

首先，让我们实例化一个简单的 HashMap ，它使用String作为键和值：

```java
DB db = DBMaker.memoryDB().make();

HTreeMap<String, String> hTreeMap = db
  .hashMap("myTreeMap")
  .keySerializer(Serializer.STRING)
  .valueSerializer(Serializer.STRING)
  .create();
```

上面，我们为键和值定义了单独的序列化 程序。现在我们的 HashMap 已创建，让我们使用put 方法添加数据：

```java
hTreeMap.put("key1", "value1");
hTreeMap.put("key2", "value2");

assertEquals(2, hTreeMap.size());
```

由于 HashMap 作用于Object 的 hashCode 方法，使用相同键添加数据会导致值被覆盖：

```java
hTreeMap.put("key1", "value3");

assertEquals(2, hTreeMap.size());
assertEquals("value3", hTreeMap.get("key1"));
```

## 4.SortedTableMap _

MapDB 的 SortedTableMap 将键存储在固定大小的表中，并使用二进制搜索进行检索。值得注意的是，一旦准备好，地图就是只读的。

让我们来看看创建和查询SortedTableMap 的过程。 我们将首先创建一个内存映射卷来保存数据，以及一个接收器来添加数据。在第一次调用我们的卷时，我们将只读标志设置为false，确保我们可以写入卷：

```java
String VOLUME_LOCATION = "sortedTableMapVol.db";

Volume vol = MappedFileVol.FACTORY.makeVolume(VOLUME_LOCATION, false);

SortedTableMap.Sink<Integer, String> sink =
  SortedTableMap.create(
    vol,
    Serializer.INTEGER,
    Serializer.STRING)
    .createFromSink();
```

接下来，我们将添加我们的数据并调用接收器上的 创建 方法来创建我们的地图：

```java
for(int i = 0; i < 100; i++){
  sink.put(i, "Value " + Integer.toString(i));
}

sink.create();
```

现在我们的地图已经存在，我们可以定义一个只读卷并使用SortedTableMap 的 open方法打开我们的地图：

```java
Volume openVol = MappedFileVol.FACTORY.makeVolume(VOLUME_LOCATION, true);

SortedTableMap<Integer, String> sortedTableMap = SortedTableMap
  .open(
    openVol,
    Serializer.INTEGER,
    Serializer.STRING);

assertEquals(100, sortedTableMap.size());
```

### 4.1. 二进制搜索

在我们继续之前，让我们更详细地了解 SortedTableMap 如何利用二进制搜索。

SortedTableMap将存储拆分为页面，每个页面包含多个由键和值组成的节点。在这些节点中是我们在Java代码中定义的键值对。

SortedTableMap执行三个二进制搜索来检索正确的值：

1.  每个页面的键都存储在数组中的堆上。SortedTableMap执行二进制搜索以找到正确的页面。
2.  接下来，对节点中的每个键进行解压缩。二进制搜索根据键建立正确的节点。
3.  最后，SortedTableMap搜索节点内的键以找到正确的值。

## 5. 内存模式

MapDB 提供三种类型的内存存储。让我们快速浏览一下每种模式，了解其工作原理并研究其优势。

### 5.1. 堆上

堆上模式将对象存储在一个简单的JavaCollection Map中。它不使用序列化，并且对于小型数据集可以非常快。 

但是，由于数据存储在堆上，因此数据集由垃圾回收 (GC) 管理。GC 的持续时间随着数据集的大小而增加，导致性能下降。

让我们看一个指定堆上模式的例子：

```java
DB db = DBMaker.heapDB().make();
```

### 5.2. 字节[]

第二种存储类型基于字节数组。在这种模式下，数据被序列化并存储到最大 1MB 的数组中。虽然在技术上是堆上的，但这种方法对于垃圾收集更有效。

这是默认推荐的，并在我们的“ [Hello Baeldung”示例](https://www.baeldung.com/mapdb#db)中使用：

```java
DB db = DBMaker.memoryDB().make();
```

### 5.3. 直接字节缓冲区

最终存储基于 DirectByteBuffer。Java 1.4 中引入的直接内存允许将数据直接传递到本机内存而不是Java堆。结果，数据将完全存储在堆外。

我们可以调用这种类型的商店：

```java
DB db = DBMaker.memoryDirectDB().make();
```

## 6. 为什么选择 MapDB？

那么，为什么要使用 MapDB？

### 6.1. MapDB 与传统数据库

MapDB 提供了大量的数据库功能，只需几行Java代码即可配置。当我们使用 MapDB 时，我们可以避免通常耗时的设置各种服务和连接来使我们的程序正常工作。

除此之外，MapDB 允许我们通过熟悉的Java集合来访问数据库的复杂性。使用 MapDB，我们不需要 SQL，我们可以通过简单的get 方法调用来访问记录。

### 6.2. MapDB 与简单Java集合

一旦停止执行，Java Collections 将不会保留我们应用程序的数据。MapDB 提供了一种简单、灵活、可插入的服务，使我们能够在保持Java集合类型的实用性的同时，快速轻松地在我们的应用程序中持久化数据。

## 七. 总结

在本文中，我们深入探讨了 MapDB 的嵌入式数据库引擎和集合框架。

我们首先查看核心类 DB 和 DBMaster ，以配置、打开和管理我们的数据库。然后，我们介绍了 MapDB 提供的一些数据结构示例，用于处理我们的记录。最后，我们研究了 MapDB 相对于传统数据库或JavaCollection 的优势。
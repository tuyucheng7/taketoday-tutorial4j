## 1. 概述

有时，我们需要刚刚插入[MongoDB](https://www.baeldung.com/java-mongodb)数据库的文档的 ID。例如，我们可能希望将 ID 作为对调用者的响应发送回或记录创建的对象以进行调试。

在本教程中，我们将了解如何在 MongoDB 中实现 ID，以及如何通过Java程序检索我们刚刚插入集合中的文档的 ID。

## 2. MongoDB文档的ID是什么？

与在每个数据存储系统中一样，MongoDB 需要为集合中存储的每个文档分配一个唯一标识符。这个标识符相当于关系数据库中的主键。

在 MongoDB 中，这个 ID 由 12 个字节组成：

-   一个 4 字节的时间戳值表示自 Unix 纪元以来的秒数
-   每个进程生成一次的 5 字节随机值。这个随机值对于机器和过程是唯一的。
-   一个 3 字节的递增计数器

ID 存储在名为_id的字段 中，由客户端生成。这意味着必须在将文档发送到数据库之前生成 ID。在客户端，我们可以使用驱动程序生成的 ID 或生成自定义 ID。

我们可以看到，同一个客户端在同一秒内创建的文档的前 9 个字节是相同的。因此，在这种情况下，ID 的唯一性依赖于计数器。该计数器可让客户在同一秒内创建超过 1600 万份文档。

虽然它以时间戳开头，但我们应该注意不要将标识符用作排序标准。这是因为不能保证在同一秒内创建的文档按创建日期排序，因为计数器不能保证是单调的。此外，不同的客户端可能有不同的系统时钟。

Java 驱动程序为计数器使用随机数生成器，它不是单调的。这就是为什么我们不应该使用驱动程序生成的 ID 按创建日期排序。

## 3.ObjectId类_

唯一标识符存储在ObjectId类中，该类提供了方便的方法来获取存储在 ID 中的数据，而无需手动解析。

例如，我们可以通过以下方式获取 ID 的创建日期：

```java
Date creationDate = objectId.getDate();
```

同样，我们可以以秒为单位检索 ID 的时间戳：

```java
int timestamp = objectId.getTimestamp();
```

ObjectId类还提供了获取计数器、机器标识符或进程标识符的方法，但它们都已被弃用。

## 4.取回ID

要记住的主要事情是，在 MongoDB 中，客户端在将文档发送到集群之前生成文档的唯一标识符。这与关系数据库中的序列形成对比。这使得检索此 ID 变得非常容易。

### 4.1. 司机生成的 ID

生成文档唯一 ID 的标准且简单的方法是让驱动程序完成这项工作。当我们向Collection中插入一个新的Document时，如果Document中不存在_id 字段，则驱动程序会在向集群发送插入命令之前生成一个新的ObjectId 。

我们将新文档插入你的集合的代码可能如下所示：

```java
Document document = new Document();
document.put("name", "Shubham");
document.put("company", "Baeldung");
collection.insertOne(document);
```

我们可以看到，我们从未指明 ID 必须如何生成。

当insertOne()方法返回时，我们可以从Document中获取生成的ObjectId：

```java
ObjectId objectId = document.getObjectId("_id");
```

我们还可以像Document的标准字段一样检索ObjectId ，然后将其转换为ObjectId：

```java
ObjectId oId = (ObjectId) document.get("_id");
```

### 4.2. 自定义编号

另一种检索 ID 的方法是在我们的代码中生成它，然后像任何其他字段一样将其放入文档中。如果我们将带有_id字段的Document发送给驱动程序，它不会生成新的。

在某些情况下，我们可能需要这样做，在将 Document 插入 Collection 之前，我们需要MongoDB Document的ID。

我们可以通过创建类的新实例来生成新的ObjectId ：

```java
ObjectId generatedId = new ObjectId();
```

或者，我们也可以调用ObjectId类的静态get()方法：

```java
ObjectId generatedId = ObjectId.get();
```

然后，我们只需要创建我们的文档并使用生成的 ID。为此，我们可以在Document构造函数中提供它：

```java
Document document = new Document("_id", generatedId);

```

或者，我们可以使用put()方法：

```java
document.put("_id", generatedId);
```

当使用用户生成的 ID 时，我们必须谨慎地在每次插入之前生成一个新的ObjectId，因为重复的 ID 是被禁止的。重复的 ID 将导致带有重复键消息的MongoWriteException 。

ObjectId类提供了其他几个构造函数，允许我们设置标识符的某些部分：

```java
public ObjectId(final Date date)
public ObjectId(final Date date, final int counter)
public ObjectId(final int timestamp, final int counter)
public ObjectId(final String hexString)
public ObjectId(final byte[] bytes)
public ObjectId(final ByteBuffer buffer)
```

但是，当我们使用这些构造函数时应该非常小心，因为提供给驱动程序的 ID 的唯一性完全依赖于我们的代码。在这些特殊情况下，我们可能会遇到重复键错误：

-   如果我们多次使用相同的日期(或时间戳)和计数器组合
-   如果我们多次使用相同的十六进制String、字节数组或ByteBuffer

## 5.总结

在本文中，我们了解了文档的 MongoDB 唯一标识符是什么以及它是如何工作的。然后，我们看到了如何在将Document插入Collection之后以及插入之前检索它。
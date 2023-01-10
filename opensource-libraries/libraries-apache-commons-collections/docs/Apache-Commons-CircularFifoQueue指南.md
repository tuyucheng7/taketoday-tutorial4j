## 1. 概述

在本快速教程中，我们将了解Apache Commons Collections 库的collections4.queue包中提供的CircularFifoQueue数据结构。

CircularFifoQueue<E>实现了Queue<E>接口，是一个固定大小的非阻塞队列——当将一个元素添加到已满的队列时，最旧的元素将被移除以为新元素腾出空间。

## 2.Maven依赖

对于 Maven 项目，我们需要添加所需的依赖项：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-collections4</artifactId>
    <version>4.1</version>
</dependency>
```

[可以在Maven Central](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.commons" AND a%3A"commons-collections4")上找到该库的最新版本。

## 3.构造函数

要创建CircularFifoQueue对象，我们可以使用默认构造函数，它创建一个默认大小为 32 的队列：

```java
CircularFifoQueue<String> bits = new CircularFifoQueue();
```

如果我们知道队列所需的最大大小，我们可以使用以int作为参数的构造函数来指定大小：

```java
CircularFifoQueue<String> colors = new CircularFifoQueue<>(5);
```

还有一个选项可以通过为构造函数提供一个集合作为参数来创建CircularFifoQueue对象。

在这种情况下，队列将充满集合的元素，其大小将与集合的大小相同：

```java
CircularFifoQueue<String> daysOfWeek = new CircularFifoQueue<>(days);
```

注意：由于这个队列在构造时已经满了，任何添加都会导致第一个创建的元素被丢弃。

## 4.添加元素

与任何Queue实现一样，我们可以使用add和offer方法添加元素。[Queue JavaDoc](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Queue.html)指定要在使用容量受限的队列操作时使用offer方法。

但是，由于CircularFifoQueue是非阻塞的，因此插入不会失败。因此，它的add和offer方法表现出相同的行为。

让我们看看如何使用add方法将元素添加到我们的颜色队列：

```java
colors.add("Red");
colors.add("Blue");
colors.add("Green");
```

让我们使用offer方法添加一些元素：

```java
colors.offer("White");
colors.offer("Black");
```

## 5.删除和检索元素

CircularFifoQueue类为我们提供了一些方法，当我们需要操作队列的元素时，这些方法很有用。有些方法用于从队列中获取元素，有些方法用于删除元素，还有一些方法用于同时执行这两种操作。

### 5.1. 窥视法

peek方法是非破坏性的并返回队列的头部。

只要在两次调用之间队列中的元素没有发生任何变化，此方法将始终返回相同的元素。如果队列为空，peek将返回null：

```java
String colorsHead = colors.peek();
```

### 5.2. 元法

element方法类似于peek — 它返回队列的当前头部。

但是，如果队列为空， element方法会抛出异常：

```java
colorsHead = colors.element();
```

### 5.3. 获取方法

当我们需要从队列中获取某个元素时，可以使用get方法。此方法将所需元素的索引作为参数。队列的索引是从零开始的。

让我们从我们之前填充元素的颜色队列中获取一个元素：

```java
String color = colors.get(1);
```

这将返回“蓝色”。

现在让我们向队列中添加三个元素并再次检查此结果：

```java
colors.add("Orange");
colors.add("Violet");
colors.add("Pink");
		
color = colors.get(1);
```

这一次，get方法返回“ Black ”。这是因为我们创建的队列的大小有限，前三个元素(“红色”、“蓝色”、“绿色”)随着新元素的添加而被移除。

### 5.4. 轮询方法

poll方法删除队列的头部元素并返回该元素。如果队列没有元素，则poll方法返回null：

```java
colorsHead = colors.poll();
```

### 5.5. 删除方法

remove方法的 操作与poll方法非常相似——它返回队列的头部并删除返回的元素。但是，如果队列为空，remove将抛出异常：

```java
colorsHead = colors.remove();
```

### 5.6. 清除方法

当我们想清空我们的队列时，我们可以使用clear方法：

```java
colors.clear();
```

## 六、检查方法

在了解了我们如何添加、删除和检索队列元素之后，让我们看看该类在检查其大小和容量方面必须提供什么。我们将在示例中使用前面部分中创建的队列。

通常，我们有两种方法来检查队列的大小——一种用于获取对象的最大大小，另一种用于检查其当前元素数。

maxSize方法将返回队列最大大小的整数值：

```java
int maxSize = bits.maxSize();
```

这将返回32，因为位队列是使用默认构造函数创建的。

size方法将返回当前存储在队列中的元素数量：

```java
int size = colors.size();
```

要检查队列对象的容量，我们可以使用isEmpty和isAtFullCapacity方法。

isEmpty方法将返回一个布尔值，指示队列是否为空：

```java
boolean isEmpty = bits.isEmpty();
```

要检查我们的队列是否已满，我们可以使用isAtFullCapacity方法。仅当已达到队列中元素的最大大小时，此方法才返回true：

```java
boolean isFull = daysOfWeek.isAtFullCapacity();
```

应该注意到，此方法从 4.1 版开始可用。

我们可以用来检查队列是否已满的Queue接口的另一个方法是isFull方法。对于CircularFifoQueue，isFull方法将始终返回false，因为队列始终可以接受新元素：

```java
boolean isFull = daysOfWeek.isFull();
```

## 七. 总结

在本文中，我们了解了如何使用 Apache Commons CircularFifoQueue。我们看到了一些示例，这些示例说明了如何实例化队列对象、如何填充它、如何清空它、如何从中获取和删除元素以及如何检查其大小和容量。
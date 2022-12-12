## 1. 概述

在本文中，我们将研究[java.util.Stream](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/Stream.html) API，我们将了解如何使用该构造对无限数据/元素流进行操作。

处理无限元素序列的可能性是基于这样一个事实，即流被构建为惰性。

这种惰性是通过分离两种可以在流上执行的操作来实现的：中间操作和终端操作。

## 2. 中间和终端操作

所有Stream操作分为中间操作和终端操作，并组合起来形成流管道。

流管道由源(例如Collection、数组、生成器函数、I/O通道或无限序列生成器)组成；随后是零个或多个中间操作和一个终端操作。

### 2.1 中间操作

在调用某些终端操作之前，不会执行中间操作。中间操作组成了Stream执行的管道，可以通过以下方法将中间操作添加到Stream管道中：

-   filter()
-   map()
-   flatMap()
-   distinct()
-   sorted()
-   peek()
-   limit()
-   skip()

所有中间操作都是惰性的，因此在实际需要处理结果之前不会执行它们。

基本上，中间操作返回一个新流。执行中间操作实际上并不执行任何操作，而是创建一个新流，在遍历时，该流包含与给定谓词匹配的初始流的元素。

因此，在执行管道的终端操作之前，不会开始遍历Stream。

这是非常重要的属性，对于无限流尤其重要，因为它允许我们创建仅在调用终端操作时才会实际调用的流。

### 2.2 终端操作

终端操作可能会遍历流以产生结果或副作用。

执行完终端操作后，Stream pipeline就被认为消耗掉了，不能再使用了。几乎在所有情况下，终端操作都是急切的，在返回之前完成对数据源的遍历和管道的处理。

终端操作的急切性对于无限流很重要，因为在处理时我们需要仔细考虑我们的Stream是否被适当地限制，例如limit()转换。终端操作包括：

-   forEach()
-   forEachOrdered()
-   toArray()
-   reduce()
-   collect()
-   min()
-   max()
-   count()
-   anyMatch()
-   allMatch()
-   noneMatch()
-   findFirst()
-   findAny()

这些操作中的每一个都会触发所有中间操作的执行。

## 3. 无限流

现在我们了解了中间操作和终端操作这两个概念，我们能够编写一个利用Streams惰性的无限流。

比方说，我们想要创建一个无限的元素流，从0开始递增2。然后我们需要在调用终端操作之前限制该序列。

在执行作为终端操作的collect( )方法之前使用limit()方法至关重要，否则我们的程序将无限期运行：

```java
// given
Stream<Integer> infiniteStream = Stream.iterate(0, i -> i + 2);

// when
List<Integer> collect = infiniteStream
  .limit(10)
  .collect(Collectors.toList());

// then
assertEquals(collect, Arrays.asList(0, 2, 4, 6, 8, 10, 12, 14, 16, 18));
```

我们使用iterate()方法创建了一个无限流，然后我们调用了limit()转换和collect()终端操作。然后在我们的结果集合中，由于Stream的惰性，我们得到无限序列的前10个元素。

## 4. 自定义元素类型的无限流

假设我们想要创建一个无限的随机UUID流。

使用Stream API实现此目的的第一步是创建这些随机值的Supplier：

```java
Supplier<UUID> randomUUIDSupplier = UUID::randomUUID;
```

当我们定义Supplier时，我们可以使用generate()方法创建无限流：

```java
Stream<UUID> infiniteStreamOfRandomUUID = Stream.generate(randomUUIDSupplier);
```

然后我们可以从该流中获取几个元素。如果我们希望我们的程序在有限时间内完成，我们需要记住使用limit()方法：

```java
List<UUID> randomInts = infiniteStreamOfRandomUUID
  .skip(10)
  .limit(10)
  .collect(Collectors.toList());
```

我们使用skip()转换来丢弃前10个结果并获取接下来的10个元素。我们可以通过将Supplier接口的函数传递给Stream上的generate()方法来创建任何自定义类型元素的无限流。

## 5. Do-While

假设我们的代码中有一个简单的do..while循环：

```java
int i = 0;
while (i < 10) {
    System.out.println(i);
    i++;
}
```

这里打印i十次，我们可以预期可以使用Stream API轻松编写此类构造，理想情况下，我们将在流上有一个doWhile()方法。

不幸的是，流上没有这样的方法，当我们想要实现类似于标准do-while循环的功能时，我们需要使用limit()方法：

```java
Stream<Integer> integers = Stream
  .iterate(0, i -> i + 1);
integers
  .limit(10)
  .forEach(System.out::println);
```

我们用更少的代码实现了与命令式while循环相同的功能，但是对limit()函数的调用不像我们在Stream对象上使用doWhile()方法那样具有描述性。

## 6. 总结

本文解释了我们如何使用Stream API来创建无限流。当与limit()等转换一起使用时，可以使某些场景更容易理解和实现。
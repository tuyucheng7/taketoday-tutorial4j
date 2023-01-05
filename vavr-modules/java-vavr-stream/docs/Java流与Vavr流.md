## 1. 简介

在本文中，我们将研究Stream实现在Java和Vavr中有何不同。

本文假设你熟悉[Java Stream API](https://www.baeldung.com/java-8-streams-introduction)和[Vavr库](https://www.baeldung.com/vavr)的基础知识。

## 2. 比较

两种实现都代表了惰性序列的相同概念，但在细节上有所不同。

Java Streams在构建时考虑到了强大的并行性，为并行化提供了简单的支持。另一方面，Vavr实现有利于方便地处理数据序列，并且不提供对并行性的本地支持(但可以通过将实例转换为Java实现来实现)。

这就是Java Streams由[Spliterator](https://www.baeldung.com/java-spliterator)实例支持的原因——升级到更旧的Iterator和Vavr的实现由上述Iterator支持(至少在最新的实现之一中)。

这两种实现都松散地与其支持数据结构相关联，并且本质上是流遍历的数据源之上的外观，但由于Vavr的实现是基于迭代器的，因此它不能容忍源集合的并发修改。

Java对流源的处理使得在执行终端流操作之前修改[行为良好的流源成为可能](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/package-summary.html#NonInterference)。

尽管存在基本的设计差异，Vavr提供了一个非常强大的API，可以将其流(和其他数据结构)转换为Java实现。

## 3. 附加功能

处理流及其元素的方法导致我们在Java和Vavr中使用它们的方式发生有趣的差异

### 3.1 随机元素访问

为元素提供方便的API和访问方法是Vavr真正超越Java API的领域之一。例如，Vavr有一些提供随机元素访问的方法：

-   [get()](https://static.javadoc.io/io.vavr/vavr/0.9.2/io/vavr/collection/Stream.html#get-int-)提供对流元素的基于索引的访问。
-   [indexOf()](https://static.javadoc.io/io.vavr/vavr/0.9.2/io/vavr/collection/Stream.html#indexOf-T-int-)提供与标准JavaList中相同的索引定位功能 
-   [insert()](https://static.javadoc.io/io.vavr/vavr/0.9.2/io/vavr/collection/Stream.html#insert-int-T-)提供了将元素添加到流中指定位置的能力。
-   [intersperse()](https://static.javadoc.io/io.vavr/vavr/0.9.2/io/vavr/collection/Stream.html#intersperse-T-)将在流的所有元素之间插入提供的参数。
-   [find()](https://static.javadoc.io/io.vavr/vavr/0.9.2/io/vavr/collection/Traversable.html#find-java.util.function.Predicate-)将从流中定位并返回一个项目，Java提供了[noneMatched](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/Stream.html#noneMatch(java.util.function.Predicate))，它只检查元素是否存在。
-   [update()](https://static.javadoc.io/io.vavr/vavr/0.9.2/io/vavr/collection/Stream.html#update-int-java.util.function.Function-)将替换给定索引处的元素，这也接收一个函数来计算替换。
-   [search ()](https://static.javadoc.io/io.vavr/vavr/0.9.2/io/vavr/collection/LinearSeq.html#search-T-)将在排序流中定位一个项目(未排序的流将产生未定义的结果)

重要的是我们要记住，此功能仍然由具有线性搜索性能的数据结构支持。

### 3.2 并行性和并发修改

虽然Vavr的Streams不像Java的parallel()方法那样原生支持并行性，但有[toJavaParallelStream](https://static.javadoc.io/io.vavr/vavr/0.9.0/io/vavr/Value.html#toJavaParallelStream--)方法提供源Vavr流的基于Java的并行副本。

Vavr 流的一个相对薄弱的领域[是非干扰原则](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/package-summary.html#NonInterference)。

简而言之，Java流允许我们在调用终端操作之前修改底层数据源。只要没有在给定的Java流上调用终端操作，该流就可以获取对基础数据源的任何更改：

```java
List<Integer> intList = new ArrayList<>();
intList.add(1);
intList.add(2);
intList.add(3);
Stream<Integer> intStream = intList.stream(); //form the stream
intList.add(5); //modify underlying list
intStream.forEach(i -> System.out.println("In aJavastream: " + i));
```

我们会发现最后一次添加反映在流的输出中。无论修改是流管道的内部还是外部，此行为都是一致的：

```bash
in aJavastream: 1
in aJavastream: 2
in aJavastream: 3
in aJavastream: 5
```

我们发现Vavr流不会容忍这种情况：

```java
Stream<Integer> vavrStream = Stream.ofAll(intList);
intList.add(5)
vavrStream.forEach(i -> System.out.println("in aVavrStream: " + i));
```

我们得到的：

```bash
Exception in thread "main" java.util.ConcurrentModificationException
  at java.util.ArrayList$Itr.checkForComodification(ArrayList.java:901)
  at java.util.ArrayList$Itr.next(ArrayList.java:851)
  at io.vavr.collection.StreamModule$StreamFactory.create(Stream.java:2078)
```

根据Java标准，Vavr流不是“行为良好”的。Vavr使用原始支持数据结构表现更好：

```java
int[] aStream = new int[]{1, 2, 4};
Stream<Integer> wrapped = Stream.ofAll(aStream);

aStream[2] = 5;
wrapped.forEach(i -> System.out.println("Vavr looped " + i));
```

给我们：

```bash
Vavr looped 1
Vavr looped 2
Vavr looped 5
```

### 3.3 短路操作和flatMap()

与map操作一样，flatMap是流处理中的中间操作——两种实现都遵循[中间流操作的约定](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/package-summary.html#StreamOps)-在调用终端操作之前，不应从底层数据结构进行处理。

然而，JDK 8和9有一个[错误](https://bugs.java.com/bugdatabase/view_bug.do?bug_id=8075939)，导致flatMap实现打破这个契约，并在与短路中间操作(如findFirst或limit)结合时急切地求值。

一个简单的例子：

```java
Stream.of(42)
  	.flatMap(i -> Stream.generate(() -> { 
      	System.out.println("nested call"); 
      	return 42; 
  	}))
  	.findAny();
```

在上面的代码片段中，我们永远不会从findAny得到结果，因为flatMap会被急切地评估，而不是简单地从嵌套的Stream中获取单个元素。

Java 10中提供了针对此错误的修复。

Vavr的flatMap没有同样的问题，功能相似的操作在O(1)中完成：

```java
Stream.of(42)
  	.flatMap(i -> Stream.continually(() -> { 
      	System.out.println("nested call"); 
      	return 42; 
  	}))
  	.get(0);
```

### 3.4 核心Vavr功能

在某些领域，Java和Vavr之间并没有一对一的比较；Vavr通过Java无法匹敌的功能增强了流媒体体验(或者至少需要大量的手动工作)：

-   [zip()](https://static.javadoc.io/io.vavr/vavr/0.9.2/io/vavr/collection/Stream.html#zip-java.lang.Iterable-)将流中的项目与提供的Iterable中的项目配对 此操作过去在JDK-8中受支持，但在[build-93之后已被删除](https://stackoverflow.com/questions/17640754/zipping-streams-using-jdk8-with-lambda-java-util-stream-streams-zip)。
-   [partition()](https://static.javadoc.io/io.vavr/vavr/0.9.2/io/vavr/collection/Stream.html#partition-java.util.function.Predicate-)给定谓词，会将流的内容分成两个流。
-   [permutation()](https://static.javadoc.io/io.vavr/vavr/0.9.2/io/vavr/collection/Stream.html#permutations--)顾名思义，将计算流元素的排列(所有可能的唯一顺序)。
-   [combinations()](https://static.javadoc.io/io.vavr/vavr/0.9.2/io/vavr/collection/Stream.html#combinations-int-)给出流的组合(即可能的项目选择)。
-   [groupBy](https://static.javadoc.io/io.vavr/vavr/0.9.2/io/vavr/collection/Stream.html#groupBy-java.util.function.Function-)将返回包含原始流中元素的流映射，由提供的分类器分类。
-   [distinct](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/Stream.html#distinct())方法通过提供接收compareTo lambda表达式的变体改进了Java版本。

虽然对高级功能的支持在Java SE流中有些平淡，但[表达式语言3.0](https://github.com/javaee/el-spec/blob/master/spec/pdf/EL3.0.PFD.pdf)奇怪地提供了比标准JDK流更多的功能支持。

## 4. 流操作

Vavr允许直接操作流的内容：

-   插入现有的Vavr流

```java
Stream<String> vavredStream = Stream.of("foo", "bar", "baz");
vavredStream.forEach(item -> System.out.println("List items: " + item));
Stream<String> vavredStream2 = vavredStream.insert(2, "buzz");
vavredStream2.forEach(item -> System.out.println("List items: " + item));
```

-   从流中删除项目

```java
Stream<String> removed = inserted.remove("buzz");
```

-   基于队列的操作 

通过由队列支持的Vavr流，它提供了恒定时间的prepend和append操作。

但是，对Vavr流所做的更改不会传播回创建该流的数据源。

## 5. 总结

Vavr和Java都有自己的优势，我们已经展示了每个库对其设计目标的承诺-Java实现廉价的并行性，而Vavr实现方便的流操作。

由于Vavr支持在其自己的流和Java流之间来回转换，因此可以在同一项目中获得这两个库的好处，而无需大量开销。
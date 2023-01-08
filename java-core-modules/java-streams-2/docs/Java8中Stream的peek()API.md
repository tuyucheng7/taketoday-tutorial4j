## 1. 概述

[Java Stream API](https://www.baeldung.com/java-8-streams)向我们介绍了处理数据的强大替代方案。

在这个简短的教程中，我们将重点关注peek()，这是一种经常被误解的方法。

## 2. 快速示例

让我们动手尝试使用peek()。我们有一个名称流，我们想将它们打印到控制台。

由于peek()期望Consumer<T>作为它唯一的参数，它看起来很合适，所以让我们试一试：

```java
Stream<String> nameStream = Stream.of("Alice", "Bob", "Chuck");
nameStream.peek(System.out::println);
```

但是，上面的代码片段没有产生任何输出。为了理解原因，让我们快速回顾一下流生命周期的各个方面。

## 3. 中间操作与终端操作

回想一下，流具有三个部分：数据源、零个或多个中间操作以及零个或一个终端操作。

源向管道提供元素。

中间操作逐个获取元素并进行处理。所有中间操作都是惰性的，因此，在管道开始工作之前，任何操作都不会产生任何影响。

终端操作意味着流生命周期的结束。对于我们的场景最重要的是，他们 启动了管道中的工作。

## 4.peek ()用法

peek()在我们的第一个示例中不起作用的原因是它是一个中间操作，我们没有将终端操作应用于管道。或者，我们可以使用具有相同参数的forEach()来获得所需的行为：

```java
Stream<String> nameStream = Stream.of("Alice", "Bob", "Chuck");
nameStream.forEach(System.out::println);
```

peek()的[Javadoc 页面](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/Stream.html#peek(java.util.function.Consumer))说：“此方法的存在主要是为了支持调试，你希望在元素流过管道中的某个点时查看元素”。

让我们考虑来自同一个 Javadoc 页面的这个片段：

```java
Stream.of("one", "two", "three", "four")
  .filter(e -> e.length() > 3)
  .peek(e -> System.out.println("Filtered value: " + e))
  .map(String::toUpperCase)
  .peek(e -> System.out.println("Mapped value: " + e))
  .collect(Collectors.toList());
```

它演示了我们如何观察通过每个操作的元素。

最重要的是，peek()在另一种情况下很有用：当我们想要改变元素的内部状态时。例如，假设我们想在打印之前将所有用户名转换为小写：

```java
Stream<User> userStream = Stream.of(new User("Alice"), new User("Bob"), new User("Chuck"));
userStream.peek(u -> u.setName(u.getName().toLowerCase()))
  .forEach(System.out::println);
```

或者，我们可以使用 map()，但peek()更方便，因为我们不想替换元素。

## 5.总结

在这个简短的教程中，我们看到了流生命周期的摘要，以了解peek()的工作原理。当使用peek()是最直接的选项时，我们还看到了两个日常用例。
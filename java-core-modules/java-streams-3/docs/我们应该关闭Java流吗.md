## 1. 概述

随着Java8 中 lambda 表达式的引入，可以以更简洁和更实用的方式编写代码。[流](https://www.baeldung.com/java-streams)和[功能接口](https://www.baeldung.com/java-8-functional-interfaces)是Java平台这一革命性变化的核心。

在本快速教程中，我们将了解是否应该通过从资源角度查看Java8 流来显式关闭它们。

## 2.关闭流

Java 8[流](https://github.com/openjdk/jdk/blob/6bab0f539fba8fb441697846347597b4a0ade428/src/java.base/share/classes/java/util/stream/BaseStream.java#L64)实现了AutoCloseable 接口：

```java
public interface Stream<T> extends BaseStream<...> {
    // omitted
}
public interface BaseStream<...> extends AutoCloseable {
    // omitted
}
```

简而言之，我们应该将流视为可以借用并在用完后归还的资源。 与大多数资源相反，我们不必总是关闭流。

起初这听起来可能违反直觉，所以让我们看看何时应该以及何时不应该关闭Java8 流。

### 2.1. 集合、数组和生成器

大多数时候，我们 从Java集合、数组或生成器函数创建Stream实例。例如，在这里，我们通过 Stream API对String集合进行操作：

```java
List<String> colors = List.of("Red", "Blue", "Green")
  .stream()
  .filter(c -> c.length() > 4)
  .map(String::toUpperCase)
  .collect(Collectors.toList());
```

有时，我们正在生成有限或无限的顺序流：

```java
Random random = new Random();
random.ints().takeWhile(i -> i < 1000).forEach(System.out::println);
```

此外，我们还可以使用基于数组的流：

```java
String[] colors = {"Red", "Blue", "Green"};
Arrays.stream(colors).map(String::toUpperCase).toArray()
```

在处理这些类型的流时，我们不应该明确地关闭它们。 与这些流关联的唯一有价值的资源是内存，[垃圾收集](https://www.baeldung.com/jvm-garbage-collectors)(GC) 会自动处理。

### 2.2. IO 资源

但是，某些流由 IO 资源(如文件或套接字)支持。例如，[Files.lines()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#lines(java.nio.file.Path))方法流式传输给定文件的所有行：

```java
Files.lines(Paths.get("/path/to/file"))
  .flatMap(line -> Arrays.stream(line.split(",")))
  // omitted
```

在幕后，此方法打开一个 [FileChannel](https://github.com/openjdk/jdk/blob/6bab0f539fba8fb441697846347597b4a0ade428/src/java.base/share/classes/java/nio/file/Files.java#L4033) 实例，然后在流关闭时将其关闭。因此， 如果我们忘记关闭流，底层通道将保持打开状态，然后我们将以[资源泄漏](https://en.wikipedia.org/wiki/Resource_leak)告终。

为防止此类资源泄漏，强烈建议使用[try-with-resources](https://www.baeldung.com/java-try-with-resources) 习惯用法来关闭基于 IO 的流：

```java
try (Stream<String> lines = Files.lines(Paths.get("/path/to/file"))) {
    lines.flatMap(line -> Arrays.stream(line.split(","))) // omitted
}
```

这样，编译器会自动关闭通道。这里的关键要点是关闭所有基于 IO 的流。

请注意，关闭一个已经关闭的流会抛出IllegalStateException。

## 3.总结

在这个简短的教程中，我们看到了简单流和 IO 密集流之间的区别。我们还了解了这些差异如何影响我们决定是否关闭Java8 流。
## 1. 概述

我们经常想要将Java Stream转换为集合，这通常会产生一个可变集合，但我们可以自定义它。

在这个简短的教程中，我们介绍**如何将Java流收集到不可变集合中**：首先使用纯Java，然后使用Guava库。

## 2. 使用标准Java

### 2.1 toUnmodifiableList

从Java 10开始，我们可以使用Java Collectors类中的toUnmodifiableList方法：

```java
List<String> givenList = Arrays.asList("a", "b", "c");
List<String> result = givenList
    .stream()
    .collect(toUnmodifiableList());
```

通过使用这种方法，我们得到的是一个来自ImmutableCollections的不支持空值的List实现：

```java
class java.util.ImmutableCollections$ListN
```

### 2.2 collectAndThen

Collectors类中的collectAndThen方法接收一个Collector和一个finisher函数(Function)，此完成器应用于从收集器返回的结果：

```java
List<String> givenList = Arrays.asList("a", "b", "c");
List<String> result = givenList
    .stream()
    .collect(collectingAndThen(toList(), ImmutableList::copyOf));

System.out.println(result.getClass());
```

使用这种方法，**由于我们不能直接使用toCollection收集器，所以我们需要将元素收集到一个临时集合中**。然后，我们从中构造一个不可变集合。

### 2.3 Stream.toList()

Java 16在[Stream API](https://www.baeldung.com/java-8-streams)上引入了一个名为toList()的新方法，这个工具方法返回一个**包含流元素的不可修改的集合**：

```java
@Test
void whenUsingStreamToList_thenReturnImmutableList() {
    List<String> immutableList = Stream.of("a", "b", "c", "d").toList();
	
    assertThrows(UnsupportedOperationException.class, () -> {
        immutableList.add("e");
    });
}
```

从上面的单元测试中看到，Stream.toList()返回一个不可变集合。因此，尝试向集合中添加新元素就会导致[UnsupportedOperationException](https://www.baeldung.com/java-list-unsupported-operation-exception)。

记住一点，新的Stream.toList()方法与现有的[Collectors.toList()](https://www.baeldung.com/java-8-collectors#1-collectorstolist)方法略有不同，因为它返回一个不可修改的集合。

## 3. 构建自定义收集器

我们还可以选择[实现自定义收集器](https://www.baeldung.com/java-8-collectors#Custom)。

### 3.1 一个基本的不可变收集器

为此，我们可以使用静态Collector.of方法：

```java
public static <T> Collector<T, List<T>, List<T>> toImmutableList() {
    return Collector.of(ArrayList::new, List::add, 
        (left, right) -> {
            left.addAll(right);
            return left;
        }, Collections::unmodifiableList);
}
```

我们可以像使用任何内置的Collector一样使用此函数：

```java
List<String> givenList = Arrays.asList("a", "b", "c", "d");
List<String> result = givenList
    .stream()
    .collect(MyImmutableListCollector.toImmutableList());
```

最后，我们观察一下输出类型：

```java
class java.util.Collections$UnmodifiableRandomAccessList
```

### 3.2 使MyImmutableListCollector泛型

我们的实现有一个限制，它总是返回一个由ArrayList支持的不可变实例，但只要稍微改进一下，我们可以让这个收集器返回一个用户指定的类型：

```java
public static <T, A extends List<T>> Collector<T, A, List<T>> toImmutableList(Supplier<A> supplier) {
    return Collector.of(
        supplier,
        List::add, (left, right) -> {
            left.addAll(right);
            return left;
        }, Collections::unmodifiableList);
}
```

因此，我们不是在方法实现中确定Supplier，而是向用户请求Supplier：

```java
List<String> givenList = Arrays.asList("a", "b", "c", "d");
List<String> result = givenList
    .stream()
    .collect(MyImmutableListCollector.toImmutableList(LinkedList::new));
```

在上面，我们使用的是LinkedList而不是ArrayList。

```java
class java.util.Collections$UnmodifiableList
```

这一次，我们得到的是UnmodifiableList而不是UnmodifiableRandomAccessList。

## 4. 使用Guava的Collectors

在本节中，我们将使用[Google Guava](https://search.maven.org/search?q=a:guava)库来驱动我们的一些示例：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

从Guava 21开始，每个不可变类都附带了一个收集器，它与Java的标准收集器一样很容易使用：

```java
List<Integer> list = IntStream.range(0, 9)
    .boxed()
    .collect(ImmutableList.toImmutableList());
```

返回的result实例是RegularImmutableList：

```java
class com.google.common.collect.RegularImmutableList
```

## 5. 总结

在这篇简短的文章中，我们了解了将Stream收集到不可变集合的各种方法。

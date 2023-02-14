## 1. 概述

在本文中，我们将快速了解Java 8添加的主要新功能之一-Stream。

我们将解释什么是流，并通过简单示例展示流的创建和基本操作。

## 2. Stream接口

Java 8的主要新特性之一是引入了流功能[java.util.stream](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/package-summary.html)-它包含用于处理元素序列的类。

核心API类是[Stream](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/Stream.html)。以下部分将演示如何使用现有数据提供程序源创建流。

### 2.1 流创建

可以在stream()和of()方法的帮助下从不同的元素源创建流，例如集合或数组：

```java
String[] arr = new String[]{"a", "b", "c"};
Stream<String> stream = Arrays.stream(arr);
stream = Stream.of("a", "b", "c");
```

stream()默认方法被添加到Collection接口并允许使用任何集合作为元素源创建Stream<T\>：

```java
Stream<String> stream = list.stream();
```

### 2.2 流与多线程

Stream API还通过提供parallelStream()方法来简化多线程处理，该方法以并行模式对流的元素运行操作。

下面的代码允许为流的每个元素并行运行方法doWork()：

```java
list.parallelStream().forEach(element -> doWork(element));
```

在下一节中，我们将介绍一些基本的Stream API操作。

## 3. 流操作

可以对流执行许多有用的操作。

它们分为**中间操作**(返回Stream<T\>)和**终端操作**(返回确定类型的结果)。中间操作允许链接。

还值得注意的是，对流的操作不会更改源。

这是一个简单的例子：

```java
long count = list.stream().distinct().count();
```

因此，distinct()方法代表一个中间操作，它创建一个新的流，其中包含前一个流的唯一元素。而count()方法是一个终端操作，它返回流的大小。

### 3.1 迭代

Stream API有助于替代for-each和while循环。它允许专注于操作的逻辑，而不是对元素序列的迭代。例如：

```java
for (String string : list) {
    if (string.contains("a")) {
        return true;
    }
}
```

只需一行Java 8代码即可更改此代码：

```java
boolean isExist = list.stream().anyMatch(element -> element.contains("a"));
```

### 3.2 过滤

filter()方法允许我们选择满足谓词的元素流。

例如，考虑以下列表：

```java
ArrayList<String> list = new ArrayList<>();
list.add("One");
list.add("OneAndOnly");
list.add("Derek");
list.add("Change");
list.add("factory");
list.add("justBefore");
list.add("Italy");
list.add("Italy");
list.add("Thursday");
list.add("");
list.add("");
```

下面的代码创建了一个List<String\>的Stream<String\>，找到这个流中包含字符“d”的所有元素，并创建一个只包含过滤元素的新流：

```java
Stream<String> stream = list.stream().filter(element -> element.contains("d"));
```

### 3.3 映射

要通过对Stream应用特殊函数来转换元素并将这些新元素收集到Stream中，我们可以使用map()方法：

```java
List<String> uris = new ArrayList<>();
uris.add("C:\\My.txt");
Stream<Path> stream = uris.stream().map(uri -> Paths.get(uri));
```

因此，上面的代码通过将特定的lambda表达式应用于初始Stream的每个元素，将Stream<String\>转换为Stream<Path\>。

如果你有一个流，其中每个元素都包含自己的元素序列，并且你想创建这些内部元素的流，你应该使用flatMap()方法：

```java
List<Detail> details = new ArrayList<>();
details.add(new Detail());
Stream<String> stream = details.stream().flatMap(detail -> detail.getParts().stream());
```

在此示例中，我们有一个Detail类型的元素列表。Detail类包含一个字段PARTS，它是一个List<String\>。在flatMap()方法的帮助下，字段PARTS中的每个元素都将被提取并添加到新的结果流中。之后，初始的Stream<Detail\>将丢失。

### 3.4 匹配

Stream API提供了一组方便的工具来根据某些谓词验证序列的元素。为此，可以使用以下方法之一：anyMatch()、allMatch()、noneMatch()。他们的名字是不言自明的。这些是返回布尔值的终端操作：

```java
boolean isValid = list.stream().anyMatch(element -> element.contains("h")); // true
boolean isValidOne = list.stream().allMatch(element -> element.contains("h")); // false
boolean isValidTwo = list.stream().noneMatch(element -> element.contains("h")); // false
```

对于空流，带有任何给定谓词的allMatch()方法将返回true：

```java
Stream.empty().allMatch(Objects::nonNull); // true
```

这是一个合理的默认值，因为我们找不到任何不满足谓词的元素。

同样，对于空流，anyMatch()方法总是返回false：

```java
Stream.empty().anyMatch(Objects::nonNull); // false
```

同样，这是合理的，因为我们找不到满足此条件的元素。

### 3.5 归约

Stream API允许在Stream类型的reduce()方法的帮助下，根据指定函数将元素序列归约为某个值。此方法有两个参数：第一个是起始值，第二个是累加器函数。

假设你有一个List<Integer\并且你想要所有这些元素和一些初始Integer(在本例中为23)的总和。因此，你可以运行以下代码，结果将为26(23 + 1 + 1 + 1)。

```java
List<Integer> integers = Arrays.asList(1, 1, 1);
Integer reduced = integers.stream().reduce(23, (a, b) -> a + b);
```

### 3.6 收集

归约也可以由Stream类型的collect()方法提供。在将流转换为Collection或Map并以单个字符串的形式表示流的情况下，此操作非常方便。有一个实用程序类Collectors，它为几乎所有典型的收集操作提供了解决方案。对于一些非常重要的任务，可以创建自定义收集器。

```java
List<String> resultList = list.stream().map(element -> element.toUpperCase()).collect(Collectors.toList());
```

此代码使用终端collect()操作将Stream<String\>归约为List<String\>。

## 4. 总结

在本文中，我们简要介绍了Java流-这绝对是Java 8最有趣的特性之一。

有许多使用Stream的高级示例；这篇文章的目的只是提供一个快速实用的介绍，介绍你可以开始使用该功能做什么，并作为探索和进一步学习的起点。
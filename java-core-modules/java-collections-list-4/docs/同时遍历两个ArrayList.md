## 1. 概述

有时，我们需要将多个列表中的数据连接在一起，将第一个列表中的第一项与第二个列表中的对应项连接起来，依此类推。

在本教程中，我们将学习几种同时循环访问两个[ArrayList](https://www.baeldung.com/java-arraylist)集合的方法。我们将研究循环、迭代器、流和第三方实用程序来解决问题。

## 2. 问题陈述和用例

让我们举一个例子，我们有两个列表，一个包含国家名称，第二个包含国家的电话代码。并假设在第二个列表中，任何给定索引处的电话代码对应于第一个列表中同一索引处的国家/地区名称。

我们希望将第二个列表中的正确国家代码与第一个列表中相应的国家/地区名称相关联。

Java 中没有针对此用例的现成现成解决方案。但是，有几种方法可以实现这一目标。

让我们首先创建两个我们将用于处理的List对象：

```java
List<String> countryName = List.of("USA", "UK", "Germany", "India");
List<String> countryCode = List.of("+1", "+44", "+49", "+91");
```

在这里，我们有两个相同大小的列表，因为每个国家/地区都应该有一个代码。如果列表的大小不同，我们的解决方案可能不起作用。

我们要处理这两个列表并为每个国家/地区获取正确的代码。我们将测试每个解决方案的预期输出：

```java
assertThat(processedList)
  .containsExactly("USA: +1", "UK: +44", "Germany: +49", "India: +91");
```

## 3. 使用for循环迭代

让我们从使用for循环迭代两个列表的最简单方法开始：

```java
for (int i = 0; i < countryName.size(); i++) {
    String processedData = String.format("%s: %s", countryName.get(i), countryCode.get(i));
    processedList.add(processedData);
}
```

在这里，我们对具有相同索引的两个列表中的每一个都使用了get() - i - 来配对我们的项目。在循环结束时， processedList将包含正确的结果。

## 4. 使用集合迭代器进行 迭代

我们还可以使用[Collection](https://www.baeldung.com/java-collections)接口的iterator()方法来获取[Iterator](https://www.baeldung.com/java-iterator)实例。我们将首先获取两个列表的Iterator实例：

```java
Iterator<String> nameIterator = countryName.iterator();
Iterator<String> codeIterator = countryCode.iterator();
```

我们将使用while循环来一起管理两个迭代器：

```java
while (nameIterator.hasNext() && codeIterator.hasNext()) {
    String processedData = String.format("%s: %s", nameIterator.next(), codeIterator.next());
    processedList.add(processedData);
}
```

while循环调用 hasNext()以 确保两个迭代器仍然有值，并且在循环内，我们使用next()选择下一对值。

## 5. 使用StreamUtils zip()方法处理

我们可以说我们的目标是将每个列表中的数据相互附加，或者一起处理这对数据。这也称为压缩一对集合。各种库提供了这个过程的实现，我们可以开箱即用。

让我们使用Spring 的[StreamUtils](https://www.baeldung.com/spring-stream-utils)库的zip()方法并提供一个[Lambda](https://www.baeldung.com/java-8-lambda-expressions-tips)来创建我们的组合值。

### 5.1. 依赖性

首先，我们应该在pom.xml文件中添加[Spring Data 依赖项：](https://search.maven.org/artifact/org.springframework.data/spring-data-commons)

```java
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-commons</artifactId>
    <version>2.7.5</version>
</dependency>
```

### 5.2. 执行

我们会将列表流和 lambda 函数传递给zip()方法。lambda 将具有处理逻辑，我们将使用collect()方法获取列表中的所有已处理数据。

```java
List<String> processedList = StreamUtils.zip(
  countryName.stream(), 
  countryCode.stream(),
  (name, code) -> String.format("%s: %s", name, code))
  .collect(Collectors.toList());
```

zip()的输出 是我们收集的Stream 。在输入列表的两个Stream之后，我们作为第三个参数提供的 BiFunction用于创建Stream的元素，然后我们可以在末尾将这些元素收集到包含正确配对的单个列表中。

我们应该注意到，此方法具有JavaStreams 的所有优点，包括过滤和映射输入数据、过滤输出数据以及尽可能少地保留在内存中。

## 六，总结

在本教程中，我们学习了同时遍历两个ArrayList的不同方法。
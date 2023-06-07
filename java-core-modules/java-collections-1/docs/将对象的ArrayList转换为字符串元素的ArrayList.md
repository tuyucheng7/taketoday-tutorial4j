---
layout: post
title:  使用Selenium处理浏览器选项卡
category: java-collection
copyright: java-collection
excerpt: Java Collection
---

## 1. 概述

在本文中，我们将研究将给定的ArrayList<Object\>转换为ArrayList<String\>的不同方法。

## 2. 问题陈述

让我们在这里理解问题陈述。假设我们有一个ArrayList<Object\>，其中的对象可以是任何类型，从Integer、Float或Boolean等自动装箱的基本类型到String、ArrayList、HashMap甚至自定义的非基本引用类型-定义的类。我们必须编写代码将上述列表转换为ArrayList<String\>。让我们看几个例子：

```plaintext
Example 1: [1, 2, 3, 4, 5]
Output 1: ["1", "2", "3", "4", "5"]

Example 2: ["Hello", 4.9837, -19837338737, true]
Output 2: ["Hello", "4.9837", "-19837338737", "true"]

Example 3: [new Node(1,4), new Double(7699.05), new User("John Doe")]
Output 3: ["Node (x=1, y=4)", "7699.05", "User (full name=John Doe)"]
```

我们可以在输入列表中提供大量不同的对象，包括自定义类的对象，如下所示的User和Node。假定这些类具有重写的toString()方法。如果未定义该方法，将调用Object类的toString()，这将生成如下输出：

```java
Node@f6d9f0, User@u8g0f9
```

上面的示例包含自定义类的实例，例如User和Node：

```java
public class User {
    private final String fullName;

    // getters and setters

   @Override
    public String toString() {
        return "User (" + "full name='" + fullName + ')';
    }
}
public class Node {
    private final int x;
    private final int y;

    // getters and setters

    @Override
    public String toString() {
        return "Node (" + "x=" + x + ", y=" + y + ')';
    }
}
```

最后，我们假设在其余部分中，变量inputList和expectedStringList包含对我们所需的输入和输出列表的引用：

```java
List<Object> inputList = List.of(
                        1,
                        true,
                        "hello",
                        Double.valueOf(273773.98),
                        new Node(2, 4),
                        new User("John Doe")
                    );
List<String> expectedStringList = List.of(
                        "1",
                        "true",
                        "hello",
                        Double.toString(273773.98),
                        new Node(2, 4).toString(),
                        new User("John Doe").toString()
                    );
```

## 3. 使用Java集合For-Each循环进行转换

让我们尝试使用Java Collection来解决我们的问题。这个想法是遍历列表的元素并将每个元素转换为String。一旦完成，我们就有了一个Stringobjects的列表。在下面的代码中，我们使用for-each循环遍历给定的列表，并通过调用toString()将每个对象显式转换为字符串：

```java
List<String> outputList = new ArrayList<>(inputList.size());
for(Object obj : inputList){
    outputList.add(obj.toString());
}
Assert.assertEquals(expectedStringList, outputList);
```

此解决方案适用于输入列表中的所有对象组合，适用于Java 5以上的所有Java版本。但是，上述解决方案无法避免输入中的空对象，并且会在遇到null时抛出[NullPointerException](https://www.baeldung.com/java-exceptions)。一个简单的增强功能是利用Java 7中引入的Objects实用程序类提供的toString()方法，它是空安全的：

```java
List<String> outputList = new ArrayList<>(inputList.size());
for(Object obj : inputList){
    outputList.add(Objects.toString(obj, null));
}
Assert.assertEquals(expectedStringList, outputList);
```

## 4. 使用Java流进行转换

我们也可以利用[Java Stream API](https://www.baeldung.com/java-8-streams)来解决我们的问题。我们首先通过应用stream()方法将数据源inputList转换为流。一旦我们有了一个Object类型的元素流，我们就需要一个中间操作，在我们的例子中是进行对象到字符串的转换，最后是一个终端操作，即将结果收集到另一个String类型的列表。

在我们的例子中，中间操作是一个采用lambda表达式的map()操作：

```java
(obj) -> Objects.toString(obj, null)
```

最后，我们的流需要一个终端操作来编译并返回所需的列表。在随后的小节中，我们将讨论可供我们使用的不同终端操作。

### 4.1 Collectors.toList()作为终端操作

在这种方法中，我们使用Collectors.toList()将中间操作生成的流收集到输出列表中：

```java
List<String> outputList;
outputList = inputList
    .stream()
    .map((obj) -> Objects.toString(obj, null))
    .collect(Collectors.toList());
Assert.assertEquals(expectedStringList, outputList);
```

这种方法适用于Java 8及更高版本，因为Stream API是在Java 8中引入的。此处作为输出生成的列表是一个可变列表，这意味着我们可以向其中添加元素。输出列表也可以包含空值。

### 4.2 Collectors.toUnmodifableList()作为终端操作-Java 10兼容方法

如果我们想要生成不可修改的String对象的输出列表，我们可以利用Java 10中引入的Collectors.toUnmodifableList()实现：

```java
List<String> outputList;
outputList = inputList
    .stream()
    .filter(Objects::nonNull)
    .map((obj) -> Objects.toString(obj, null))
    .collect(Collectors.toUnmodifiableList());
Assert.assertEquals(expectedStringListWithoutNull, outputList);
```

这里的一个重要警告是列表不能包含null值，因此，如果inputList包含null，则此代码会产生NullPointerException。这就是为什么我们添加一个过滤器以在应用我们的操作之前仅从流中选择非空元素。outputList是不可变的，如果稍后尝试修改它，将产生UnsupportedOperationException。

### 4.3 toList()作为终端操作-Java 16兼容方法

如果我们想直接从输入Stream创建一个不可修改的列表，但我们希望在结果列表中允许空值，我们可以使用在Java 16的Stream接口中引入的toList()方法：

```java
List<String> outputList;
outputList = inputList
    .stream()
    .map((obj) -> Objects.toString(obj, null))
    .toList();
Assert.assertEquals(expectedStringList, outputList);
```

## 5. 使用Guava进行转换

我们可以使用Google[Guava库](https://www.baeldung.com/guava-lists)将对象的输入列表转换为新的String列表。

### 5.1 Maven配置

要使用Google Guava库，我们需要在pom.xml中包含相应的Maven依赖项：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.1-jre</version>
    <scope>test</scope>
</dependency>
```

可以从[Maven Central](https://search.maven.org/classic/#search|gav|1|g%3A"com.google.guava"ANDa%3A"guava")获取最新版本的依赖项。

### 5.2使用Lists.transform()

我们可以使用Google Guava Lists类中的transform()方法。它接收inputList和前面提到的lambda表达式来生成String对象的outputList：

```java
List<String> outputList;
outputList = Lists.transform(inputList, obj -> Objects.toString(obj, null));
Assert.assertEquals(expectedStringList, outputList);
```

使用此方法，输出列表可以包含空值。

## 6.  总结

在本文中，我们研究了几种不同的方法，可以将Object元素的ArrayList转换为String的ArrayList。我们探讨了从基于for-each循环的方法到基于Java Stream的方法的不同实现。我们还研究了特定于不同Java版本的不同实现，以及来自Guava的实现。

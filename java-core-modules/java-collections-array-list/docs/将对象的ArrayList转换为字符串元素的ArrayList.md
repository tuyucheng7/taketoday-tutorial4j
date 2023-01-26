## 一、简介

在本文中，我们将研究将给定的*ArrayList<Object>* 转换为*ArrayList<String>*的不同方法。

## 2.问题陈述

让我们在这里理解问题陈述。假设我们有一个*ArrayList<Object>*，其中的对象可以是任何类型，从*Integer*、*Float*或*Boolean等自动装箱的基本类型到**String*、*ArrayList*、*HashMap*甚至自定义的非基本引用类型-定义的类。**我们必须编写代码将上述列表转换为\*ArrayList<String>\***。让我们看几个例子：

```plaintext
Example 1: [1, 2, 3, 4, 5]
Output 1: ["1", "2", "3", "4", "5"]

Example 2: ["Hello", 4.9837, -19837338737, true]
Output 2: ["Hello", "4.9837", "-19837338737", "true"]

Example 3: [new Node(1,4), new Double(7699.05), new User("John Doe")]
Output 3: ["Node (x=1, y=4)", "7699.05", "User (full name=John Doe)"]复制
```

我们可以在输入列表中提供大量不同的对象，包括自定义类的对象，如下所示的*User* 和 *Node 。***假定这些类具有重写的 \*toString()\*方法**。如果未定义该方法，将调用*Object*类的*toString()*，这将生成如下输出：

```java
Node@f6d9f0, User@u8g0f9复制
```

上面的示例包含自定义类的实例，例如*User*和*Node*：

```java
public class User {
    private final String fullName;

    // getters and setters

   @Override
    public String toString() {
        return "User (" + "full name='" + fullName + ')';
    }
}复制
public class Node {
    private final int x;
    private final int y;

    // getters and setters

    @Override
    public String toString() {
        return "Node (" + "x=" + x + ", y=" + y + ')';
    }
}复制
```

最后，我们假设在其余部分中，变量*inputList*和*expectedStringList*包含对我们所需的输入和输出列表的引用：

```java
List<Object> inputList = List.of(
                        1,
                        true,
                        "hello",
                        Double.valueOf(273773.98),
                        new Node(2, 4),
                        new User("John Doe")
                    );复制
List<String> expectedStringList = List.of(
                        "1",
                        "true",
                        "hello",
                        Double.toString(273773.98),
                        new Node(2, 4).toString(),
                        new User("John Doe").toString()
                    );复制
```

## 3. 使用 Java 集合 For-Each 循环进行转换

让我们尝试使用 Java Collections 来解决我们的问题。这个想法是遍历列表的元素并将每个元素转换为*String*。一旦完成，我们就有了一个*String* objects的列表*。*在下面的代码中，我们使用 for-each 循环遍历给定的列表，并 通过调用*toString()将每个对象显式转换为**字符串*：

```java
List<String> outputList = new ArrayList<>(inputList.size());
for(Object obj : inputList){
    outputList.add(obj.toString());
}
Assert.assertEquals(expectedStringList, outputList);复制
```

此解决方案适用于输入列表中的所有对象组合，适用于 Java 5 以上的所有 Java 版本。**但是，上述解决方案无法避免输入中的\*空\*对象，并且会 在遇到\*null时抛出\**[NullPointerException](https://www.baeldung.com/java-exceptions)\***。一个简单的增强功能是利用Java 7 中引入的*Objects 实用程序类提供的**toString()*方法，它是空安全的：

```java
List<String> outputList = new ArrayList<>(inputList.size());
for(Object obj : inputList){
    outputList.add(Objects.toString(obj, null));
}
Assert.assertEquals(expectedStringList, outputList);复制
```

## 4. 使用 Java 流进行转换

我们也可以利用[Java Streams API](https://www.baeldung.com/java-8-streams)来解决我们的问题。我们首先通过应用*stream()*方法将数据源*inputList转换为流。****一旦我们有了一个Object\*****类型的元素流****，我们就需要一个中间操作，在我们的例子中是进行对象到字符串的转换，最后是一个终端操作，即将结果收集到另一个*****String\*****类型的列表****。**

在我们的例子中，中间操作是一个采用 lambda 表达式的*map()*操作：

```java
(obj) -> Objects.toString(obj, null)复制
```

最后，我们的流需要一个终端操作来编译并返回所需的列表。在随后的小节中，我们将讨论可供我们使用的不同终端操作。

### **4.1. \*Collectors.toList()\* 作为终端操作**

在这种方法中，我们使用 *Collectors.toList()* 将中间操作生成的流收集到输出列表中：

```java
List<String> outputList;
outputList = inputList
    .stream()
    .map((obj) -> Objects.toString(obj, null))
    .collect(Collectors.toList());
Assert.assertEquals(expectedStringList, outputList);复制
```

这种方法适用于 Java 8 及更高版本，因为*Streams* API 是在 Java 8 中引入的。此处作为输出生成的列表是一个可变列表，这意味着我们可以向其中添加元素。输出列表也可以包含空值。

### **4.2. \*Collectors.toUnmodifableList()\* 作为终端操作——Java 10 兼容方法**

如果我们想要生成不可修改的*String*对象的输出列表，我们可以利用Java 10 中引入的*Collectors.toUnmodifableList()实现：*

```java
List<String> outputList;
outputList = inputList
    .stream()
    .filter(Objects::nonNull)
    .map((obj) -> Objects.toString(obj, null))
    .collect(Collectors.toUnmodifiableList());
Assert.assertEquals(expectedStringListWithoutNull, outputList);复制
```

这里的一个重要警告是列表不能包含*null*值，因此，如果*inputList*包含*null*，则此代码会产生*NullPointerException*。这就是为什么我们添加一个过滤器以在应用我们的操作之前*仅从流中选择非空* 元素。outputList是不可变的，如果*稍后*尝试修改它，将产生*UnsupportedOperationException 。*

### **4.3. \*toList()\* 作为终端操作——Java 16 兼容方法**

如果我们想直接从输入*Stream*创建一个不可修改的列表，但我们希望在结果列表中允许*空*值，我们可以使用在 Java 16 的*Stream*接口中引入的*toList()方法：*

```java
List<String> outputList;
outputList = inputList
    .stream()
    .map((obj) -> Objects.toString(obj, null))
    .toList();
Assert.assertEquals(expectedStringList, outputList);复制
```

## 5. 使用 Guava 进行转换

我们可以使用 Google [Guava 库](https://www.baeldung.com/guava-lists)将对象的输入列表转换为新的*String*列表。

### 5.1. Maven 配置

要使用 Google Guava 库，我们需要在*pom.xml*中包含相应的 Maven 依赖项：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.1-jre</version>
    <scope>test</scope>
</dependency>复制
```

可以从[Maven Central](https://search.maven.org/classic/#search|gav|1|g%3A"com.google.guava" AND a%3A"guava")获取最新版本的依赖项。

### 5.2 使用*Lists.transform()*

我们可以使用Google Guava *Lists*类中的*transform() 方法。*它接受*inputList*和前面提到的 lambda 表达式来生成*String*对象的*outputList ：*

```java
List<String> outputList;
outputList = Lists.transform(inputList, obj -> Objects.toString(obj, null));
Assert.assertEquals(expectedStringList, outputList);复制
```

使用此方法，输出列表可以包含*空*值。

## 六，结论

在本文中，我们研究了几种不同的方法，可以将*Object元素的**ArrayList* 转换为String*的**ArrayList*。我们探索了从基于 for-each 循环的方法到基于 Java Streams 的方法的不同实现。我们还研究了特定于不同 Java 版本的不同实现，以及来自 Guava 的实现。
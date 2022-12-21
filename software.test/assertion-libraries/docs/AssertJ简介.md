## 1. 概述

在本文中，我们介绍[AssertJ](https://joel-costigliola.github.io/assertj/)，这是一个开源社区驱动的库，用于在Java测试中编写流畅和丰富的断言。

本文重点介绍名为AssertJ-core的基本AssertJ模块中可用的工具。

## 2. Maven依赖

为了使用AssertJ，你需要在pom.xml文件中包含以下部分：

```xml
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <version>3.4.1</version>
    <scope>test</scope>
</dependency>
```

此依赖项仅涵盖基本的Java断言，如果要使用更多高级断言，则需要单独添加其他模块。

请注意，对于Java 7和更早版本，你应该使用AssertJ核心版本2.x.x。

## 3. 简介

AssertJ提供了一组类和工具方法，使我们能够轻松地编写流畅而漂亮的断言：

-   标准Java
-   Java 8
-   Guava
-   Joda Time
-   Neo4J
-   Swing组件

[项目网站](https://joel-costigliola.github.io/assertj)上提供了所有模块的详细集合。

首先我们从AssertJ文档中的几个示例开始：

```java
assertThat(frodo)
    .isNotEqualTo(sauron)
    .isIn(fellowshipOfTheRing);

assertThat(frodo.getName())
    .startsWith("Fro")
    .endsWith("do")
    .isEqualToIgnoringCase("frodo");

assertThat(fellowshipOfTheRing)
    .hasSize(9)
    .contains(frodo, sam)
    .doesNotContain(sauron);
```

上面的例子只是冰山一角，但让我们大致了解如何使用这个库编写断言。

## 4. AssertJ实践

### 4.1 入门

要使用AssertJ提供的断言方法，我们可以静态导入以下类：

```java
import static org.assertj.core.api.Assertions.;
```

### 4.2 写断言

为了编写一个断言，你总是需要首先将对象传递给Assertions.assertThat()方法，然后再执行实际的断言。

重要的是要记住，与其他一些库不同，下面的代码实际上并没有断言任何东西，并且永远不会失败测试：

```java
assertThat(anyRefenceOrValue);
```

如果你利用IDE的代码完成功能，编写AssertJ断言变得非常容易，因为它的方法非常具有描述性。这是IntelliJ IDEA 2022.2中的样子：

[![IDE 的代码完成功能](https://www.baeldung.com/wp-content/uploads/2016/06/ide-e1466629641205-300x230.png)](https://www.baeldung.com/wp-content/uploads/2016/06/ide.png)

如你所见，你有许多上下文方法可供选择，并且这些方法仅适用于String类型。让我们详细探讨一下这个API并看看一些具体的断言。

### 4.3 对象断言

可以通过各种方式比较对象以确定两个对象的相等性或检查对象的字段。

让我们来看两种比较两个对象相等性的方法，给定以下两个Dog对象fido和fidosClone：

```java
public class Dog { 
    private String name; 
    private Float weight;
    
    // standard getters and setters
}

Dog fido = new Dog("Fido", 5.25);

Dog fidosClone = new Dog("Fido", 5.25);
```

我们可以使用以下断言进行对象的相等性比较：

```java
assertThat(fido).isEqualTo(fidosClone);
```

上面的断言将失败，因为isEqualTo()比较的是对象引用。如果我们想比较它们的内容，我们可以使用isEqualToComparingFieldByFieldRecursively()：

```java
assertThat(fido).isEqualToComparingFieldByFieldRecursively(fidosClone);
```

在逐个字段进行递归比较时，fido和fidosClone是相等的，因为一个对象的每个字段都与另一个对象中的字段进行比较。

还有许多其他断言方法提供了不同的方法来比较和收缩对象，以及检查和断言它们的字段。你可以参阅官方AbstractObjectAssert[文档](https://joel-costigliola.github.io/assertj/core-8/api/org/assertj/core/api/AbstractObjectAssert.html)。

### 4.4 布尔断言

有一些简单的方法可用于真值测试：

-   isTrue()
-   isFalse()

下面是具体的例子：

```java
assertThat("".isEmpty()).isTrue();
```

### 4.5 Iterable/数组断言

对于Iterable或数组，有多种方法可以断言它们的内容存在。最常见的断言之一是检查Iterable或数组是否包含给定元素：

```java
List<String> list = Arrays.asList("1", "2", "3");

assertThat(list).contains("1");
```

或者是否List不为空：

```java
assertThat(list).isNotEmpty();
```

或者是否集合以给定字符开头，例如“1”：

```java
assertThat(list).startsWith("1");
```

记住，如果你想为同一个对象创建多个断言，可以轻松地将它们链接在一起。

以下是一个断言示例，它检查提供的集合是否不为空、是否包含元素“1”、是否不包含任何空值以及是否包含元素序列“2”、“3”：

```java
assertThat(list)
    .isNotEmpty()
    .contains("1")
    .doesNotContainNull()
    .containsSequence("2", "3");
```

当然，这些类型存在更多可能的断言，你可以参阅官方AbstractIterableAssert[文档](https://joel-costigliola.github.io/assertj/core-8/api/org/assertj/core/api/AbstractIterableAssert.html)。

### 4.6 字符断言

字符类型的断言主要涉及比较，甚至检查给定字符是否来自Unicode表。

下面是一个断言示例，它检查提供的字符是否不是“a”、是否在Unicode表中、是否大于“b”并且是否为小写：

```java
assertThat(someCharacter)
    .isNotEqualTo('a')
    .inUnicode()
    .isGreaterThanOrEqualTo('b')
    .isLowerCase();
```

有关所有字符类型断言的详细信息，请参阅AbstractCharacterAssert[文档](https://joel-costigliola.github.io/assertj/core-8/api/org/assertj/core/api/AbstractCharacterAssert.html)。

### 4.7 Class断言

Class类型的断言主要是关于检查其字段、Class类型、注解的存在和类的最终性。

如果你想断言Runnable类是一个接口，你只需要写：

```java
assertThat(Runnable.class).isInterface();
```

或者如果你想检查一个类是否可以从另一个类分配：

```java
assertThat(Exception.class).isAssignableFrom(NoSuchElementException.class);
```

所有可能的Class断言都可以在AbstractClassAssert[文档](https://joel-costigliola.github.io/assertj/core-8/api/org/assertj/core/api/AbstractClassAssert.html)中找到。

### 4.8 文件断言

文件断言都是关于检查给定的File实例是否存在、是否是目录或文件、是否具有某些内容、是否可读或是否具有给定的扩展名。

下面的示例检查给定文件是否存在、是文件而不是目录、是否可读和可写：

```java
 assertThat(someFile)
     .exists()
     .isFile()
     .canRead()
     .canWrite();
```

所有可能的文件断言都可以在AbstractFileAssert[文档](https://joel-costigliola.github.io/assertj/core-8/api/org/assertj/core/api/AbstractFileAssert.html)中找到。

### 4.9 Double/Float/Integer断言

**Double/Float/Integer和其他数字类型**断言都是关于比较给定偏移量之内或之外的数值。例如，如果你想根据给定的精度检查两个值是否相等，我们可以执行以下操作：

```java
assertThat(5.1).isEqualTo(5, withPrecision(1d));
```

请注意，我们使用已经导入的withPrecision(Double offset)工具方法来生成Offset对象。

有关更多断言，请查阅AbstractDoubleAssert[文档](https://joel-costigliola.github.io/assertj/core-8/api/org/assertj/core/api/AbstractDoubleAssert.html)。

### 4.10 输入流断言

只有一个特定于InputStream的断言可用：

-   hasSameContentAs(InputStream expected)

以下是一个例子：

```java
assertThat(given).hasSameContentAs(expected);
```

### 4.11 Map断言

Map断言允许你检查Map是否分别包含某些Entry、Entry集或键/值。

下面的例子检查给定的Map是否不为空，包含数字键“2”，不包含数字键“10”并且包含key2，值为“a”的键值对：

```java
assertThat(map)
    .isNotEmpty()
    .containsKey(2)
    .doesNotContainKeys(10)
    .contains(entry(2, "a"));
```

有关更多断言，请查阅AbstractMapAssert[文档](https://joel-costigliola.github.io/assertj/core-8/api/org/assertj/core/api/AbstractMapAssert.html)。

### 4.12 Throwable断言

Throwable断言允许检查异常的消息、堆栈跟踪、原因检查或验证是否已经抛出异常。

下面的例子检查是否抛出了一个给定的异常并且有一个以“c”结尾的消息：

```java
assertThat(ex).hasNoCause().hasMessageEndingWith("c");
```

有关更多断言，请查阅AbstractThrowableAssert[文档](https://joel-costigliola.github.io/assertj/core-8/api/org/assertj/core/api/AbstractThrowableAssert.html)。

## 5. 描述断言

为了实现更高的详细程度，你可以为断言创建动态生成的自定义描述，做到这一点的关键是as(String description, Object... args)方法。

如果你这样定义断言：

```java
assertThat(person.getAge())
    .as("%s's age should be equal to 100", person.getName())
    .isEqualTo(100);
```

这是运行测试时会得到的结果：

```java
[Alex's age should be equal to 100] expected:<100> but was:<34>
```

## 6. Java 8

AssertJ充分利用了Java 8的函数式编程特性，我们可以通过一个案例来对比Java 7和Java 8之中断言的编写方式，下面是使用Java 7的例子：

```java
assertThat(fellowshipOfTheRing)
    .filteredOn("race", HOBBIT)
    .containsOnly(sam, frodo, pippin, merry);
```

这里我们过滤了race为HOBBIT的集合，在Java 8中我们可以这样做：

```java
assertThat(fellowshipOfTheRing)
    .filteredOn(character -> character.getRace().equals(HOBBIT))
    .containsOnly(sam, frodo, pippin, merry);
```

我们会在本系列的后续文章中探讨AssertJ的Java 8功能，以上示例取自AssertJ的[官网](https://joel-costigliola.github.io/assertj/)。

## 7. 总结

在本文中，我们简要探讨了AssertJ为我们提供的可用方法以及最常见的核心Java类型断言。
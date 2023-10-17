## 1. 概述

[Vavr](http://www.vavr.io/vavr-docs/)是一个强大的Java 8+库，构建在Java lambda表达式之上。受Scala语言的启发，Vavr将函数式编程结构添加到Java语言中，例如模式匹配、控制结构、数据类型、持久和不可变集合等。

在这篇简短的文章中，我们将展示如何使用一些工厂方法来创建Vavr集合。如果你是Vavr的新手，你可以从这个[介绍性教程](https://www.baeldung.com/vavr-tutorial)开始，该教程又引用了其他有用的文章。

## 2. Maven依赖

要将Vavr库添加到你的Maven项目，请编辑你的pom.xml文件以包含以下依赖项：

```xml
<dependency>
    <groupId>io.vavr</groupId>
    <artifactId>vavr</artifactId>
    <version>0.9.1</version>
</dependency>
```

你可以在[Maven Central](https://search.maven.org/search?q=a:vavr)中找到该库的最新版本。

## 3. 静态工厂方法

使用静态导入：

```java
static import io.vavr.API.*;
```

我们可以使用构造函数List(...)创建一个列表：

```java
List numbers = List(1,2,3);
```

而不是使用静态工厂方法(...)：

```java
List numbers = List.of(1,2,3);
```

或者还有：

```java
Tuple t = Tuple('a', 3);
```

代替：

```java
Tuple t = Tuple.of('a', 3);
```

这种语法糖类似于Scala/Kotlin中的结构。从现在开始，我们将在本文中使用这些缩写。

## 4. Option元素的创建

Option元素不是集合，但它们可以是Vavr库的非常有用的构造。这是一种允许我们保存对象或None元素(相当于空对象)的类型：

```java
Option<Integer> none = None();
Option<Integer> some = Some(1);
```

## 5. Vavr元组

同样，Java没有元组，如有序对、三元组等。在Vavr中，我们可以定义一个元组，它最多可以容纳八个不同类型的对象。这是一个包含Character、String和Integer对象的示例：

```java
Tuple3<Character, String, Integer> tuple = Tuple('a', "chain", 2);
```

## 6. Try类型

Try类型可用于对可能引发或不引发异常的计算建模：

```java
Try<Integer> integer = Success(55);
Try<Integer> failure = Failure(new Exception("Exception X encapsulated here"));
```

在这种情况下，如果我们评估integer.get()，我们将获得整数对象55。如果我们评估failure.get()，将抛出异常。

## 7. Vavr系列

我们可以用许多不同的方式创建集合。对于List，我们可以使用List.of()、List.fill()、List.tabulate()等。如前所述，默认工厂方法是List.of()，可以使用Scala风格的构造函数进行缩写:

```java
List<Integer> list = List(1, 2, 3, 4, 5);
```

我们还可以创建一个空列表(在Vavr中称为Nil对象)：

```java
List()
```

以类似的方式，我们可以创建其他类型的Collection：

```java
Array arr = Array(1, 2, 3, 4, 5);
Stream stm = Stream(1, 2, 3, 4, 5);
Vector vec = Vector(1, 2, 3, 4, 5);
```

## 8. 总结

我们已经看到了Vavr类型和集合的最常见的构造函数，[第3节](https://www.baeldung.com/vavr-collection-factory-methods#sec3)中提到的静态导入提供的语法糖使得在库中创建所有类型变得容易。
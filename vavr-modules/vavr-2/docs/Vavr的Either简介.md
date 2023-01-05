## 1. 概述

[Vavr](http://www.vavr.io/)是Java 8+的开源对象函数式语言扩展库。它有助于减少代码量并提高健壮性。

在本文中，我们将了解Vavr的名为Either的工具。如果你想了解有关Vavr库的更多信息，请查看[这篇文章](https://www.baeldung.com/vavr)。

## 2. Either是什么？

在函数式编程世界中，函数值或对象无法修改(即[正常形式](https://en.wikipedia.org/wiki/Normal_form_(abstract_rewriting)))；在Java术语中，它被称为[不可变](https://en.wikipedia.org/wiki/Immutable_object)变量。

[Either](https://static.javadoc.io/io.vavr/vavr/0.9.0/io/vavr/control/Either.html)表示两种可能的数据类型的值。Either要么是[Left](https://static.javadoc.io/io.vavr/vavr/0.9.0/io/vavr/control/Either.Left.html)要么是[Right](https://static.javadoc.io/io.vavr/vavr/0.9.0/io/vavr/control/Either.Right.html)。按照惯例，Left表示失败案例结果，Right表示成功。

## 3. Maven依赖

我们需要在pom.xml中添加以下依赖项：

```xml
<dependency>
    <groupId>io.vavr</groupId>
    <artifactId>vavr</artifactId>
    <version>0.9.0</version>
</dependency>
```

最新版本的Vavr在[Maven Central](https://search.maven.org/search?q=a:vavr)中可用。

## 4. 用例

让我们考虑一个用例，我们需要创建一个接受输入的方法，并根据输入返回一个String或一个Integer。

### 4.1 纯Java

我们可以通过两种方式实现这一点。我们的方法可以返回一个映射，其中的键表示成功/失败结果，或者它可以返回一个固定大小的列表/数组，其中位置表示结果类型。

这就是它的样子：

```java
public static Map<String, Object> computeWithoutEitherUsingMap(int marks) {
    Map<String, Object> results = new HashMap<>();
    if (marks < 85) {
        results.put("FAILURE", "Marks not acceptable");
    } else {
        results.put("SUCCESS", marks);
    }
    return results;
}

public static void main(String[] args) {
    Map<String, Object> results = computeWithoutEitherUsingMap(8);

    String error = (String) results.get("FAILURE");
    int marks = (int) results.get("SUCCESS");
}
```

对于第二种方法，我们可以使用以下代码：

```java
public static Object[] computeWithoutEitherUsingArray(int marks) {
    Object[] results = new Object[2];
    if (marks < 85) {
        results[0] = "Marks not acceptable";
    } else {
        results[1] = marks;
    }
    return results;
}
```

正如我们所看到的，这两种方式都需要相当多的工作，最终的结果在美学上不是很吸引人，使用起来也不安全。

### 4.2 使用Either

现在让我们看看如何利用Vavr的Either实用程序来获得相同的结果：

```java
private static Either<String, Integer> computeWithEither(int marks) {
    if (marks < 85) {
        return Either.left("Marks not acceptable");
    } else {
        return Either.right(marks);
    }
}
```

不，需要显式类型转换、空检查或未使用的对象创建。

此外，Either提供了一个非常方便的类monadic API来处理这两种情况：

```java
computeWithEither(80)
    .right()
    .filter(...)
    .map(...)
    // ...
```

按照惯例，Either的Left属性代表失败案例，Right属性代表成功案例。然而，根据我们的需要，我们可以使用投影来改变这一点-Vavr中的Either不偏向左或右。

如果我们向右投影，如果Either为左，则filter()、map()等操作将无效。

例如，让我们创建Right投影并在其上定义一些操作：

```java
computeWithEither(90).right()
    .filter(...)
    .map(...)
    .getOrElse(Collections::emptyList);
```

如果事实证明我们将Left投影到Right，我们将立即得到一个空列表。

我们可以用类似的方式与左投影交互：

```java
computeWithEither(9).left()
    .map(FetchError::getMsg)
    .forEach(System.out::println);
```

### 4.3 附加的功能 

有很多可用的Either实用程序；让我们来看看其中的一些。

我们可以使用isLeft和isRight方法检查Either是否仅包含Left或Right：

```java
result.isLeft();
result.isRight();
```

我们可以检查Either是否包含给定的Right值：

```java
result.contains(100)
```

我们可以将Left和Right折叠成一种常见的类型：

```java
Either<String, Integer> either = Either.right(42);
String result = either.fold(i -> i, Object::toString);
```

或者...甚至交换双方：

```java
Either<String, Integer> either = Either.right(42);
Either<Integer, String> swap = either.swap();
```

## 5. 总结

在本快速教程中，我们了解了如何使用Vavr框架的Either实用程序。可以在[此处](https://slides.com/pivovarit/to-try-or-not-to-try-there-is-no-throws#/32)找到有关Either的更多详细信息。
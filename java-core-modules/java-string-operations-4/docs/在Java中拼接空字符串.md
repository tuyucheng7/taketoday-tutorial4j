## 1. 概述

Java 提供了多种用于连接 String的方法和类。 但是，如果我们不注意空对象，结果String可能包含一些不需要的值。

在本教程中，我们将看到一些在连接String时避免空 String对象的方法。

## 2.问题陈述

假设我们想要连接一个String数组的元素，其中任何元素都可以为null。

我们可以简单地使用 + 运算符来做到这一点：

```java
String[] values = { "Java ", null, "", "is ", "great!" };
String result = "";

for (String value : values) {
    result = result + value;
}
```

这会将所有元素连接到结果String中，如下所示：

```java
Java nullis great!
```

但是，我们可能不想在输出中显示或附加此类“空”值。

同样，如果我们的应用程序运行在Java8 或更高版本上，我们使用[String.join()静态方法得到相同的输出：](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#join(java.lang.CharSequence,java.lang.CharSequence...))

```java
String result = String.join("", values);
```

我们也无法避免在使用String.join()方法时连接 null 元素。

让我们看看一些方法来避免这些空元素被连接起来并得到我们期望的结果：“Java 很棒！”。

## 3. 使用 + 运算符

加法 (+) 运算符被重载以连接Java 中的String。在使用 + 运算符连接时，我们可以检查String 是否为null，并将null String替换为空(“”)String：

```java
for (String value : values) {
    result = result + (value == null ? "" : value);
}

assertEquals("Java is great!", result);
```

或者，我们可以将检查null String的代码提取到接受String对象并返回非null String对象的辅助方法中：

```java
for (String value : values) {
    result = result + getNonNullString(value);
}
```

在这里，getNonNullString() 方法是我们的辅助方法。它只是检查输入字符串对象的空引用。如果输入对象是null，它返回一个空的(“”)String，否则，它返回相同的String：

```java
return value == null ? "" : value;
```

然而，正如我们所知，String对象在Java中是不可变的。这意味着，每次我们使用 + 运算符连接String对象时，它都会在内存中创建一个新的String 。因此，事实证明，使用 + 运算符进行连接的[成本很高](https://www.baeldung.com/java-string-performance)。

此外，我们可以使用这种创建辅助方法的方法来检查各种其他连接支持操作中的空 字符串对象。让我们来看看其中的一些。

## 4. 使用String.concat()方法

当我们想要连接String对象时[，String.conca ](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#concat(java.lang.String))[t ( )方法是一个不错的选择。](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#concat(java.lang.String))

在这里，我们可以使用我们的getNonNullString()方法来检查空对象并返回一个空字符串：

```java
for (String value : values) {
    result = result.concat(getNonNullString(value));
}
```

getNonNullString()方法返回的空字符串被连接到结果，从而忽略空对象。

## 5. 使用StringBuilder类

[StringBuilder](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/StringBuilder.html) 提供了一堆有用且方便的String构建方法。其中之一是[append()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/StringBuilder.html#append(java.lang.String)) 方法。

在这里，我们也可以在使用append()方法时使用相同的getNonNullString()方法来避免空对象：

```java
for (String value : values) {
    result = result.append(getNonNullString(value));
}
```

## 6. 使用StringJoiner类 (Java 8+)

[StringJoiner](https://www.baeldung.com/java-string-joiner)类提供String.join()的所有功能以及以给定前缀开头和以给定后缀结尾的选项。我们可以使用它的add()方法来连接String s。

和以前一样，我们可以使用我们的辅助方法getNonNullString() 来避免空 字符串值被连接起来：

```java
StringJoiner result = new StringJoiner("");

for (String value : values) {
    result = result.add(getNonNullString(value));
}
```

String.join() 和StringJoiner之间的一个区别是，与String.join()不同，我们必须遍历集合(数组、列表等)以连接所有元素。

## 7. 使用Streams.filter (Java 8+)

[Stream](https://www.baeldung.com/java-8-streams-introduction) API 提供了大量的顺序和并行聚合操作。一个这样的中间流操作是过滤器 ，它接受一个[Predicate](https://www.baeldung.com/java-8-functional-interfaces#Predicates) 作为输入，并根据给定的 Predicate 将Stream转换为另一个Stream 。

因此，我们可以定义一个Predicate 来检查String的空值 并将此Predicate 传递给filter()方法。因此，过滤器将从原始Stream 中过滤掉那些空值。

最后，我们可以使用Collectors.joining()连接所有这些非null String值，最后，将结果Stream收集到一个String变量中：

```java
result = Stream.of(values).filter(value -> null != value).collect(Collectors.joining(""));

```

## 八、总结

在本文中，我们说明了避免空 字符串对象串联的各种方法。总会有不止一种正确的方法来满足我们的要求。因此，我们必须确定哪种方法最适合给定的地方。

我们必须记住，连接String 本身可能是一项昂贵的操作，尤其是在循环中。因此，始终建议考虑JavaString API 的性能方面。
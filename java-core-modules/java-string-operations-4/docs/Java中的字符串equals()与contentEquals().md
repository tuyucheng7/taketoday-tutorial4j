## 1. 概述

[Java中String](https://www.baeldung.com/java-string)[类](https://www.baeldung.com/java-string)的equals()和contentEquals()方法用于进行String比较。但是，这两种方法的功能之间存在特定差异。

在本教程中，我们将通过实际示例快速了解这两种方法之间的区别。

## 2. equals()方法

[equals()](https://www.baeldung.com/java-compare-strings#equals-string)方法是Java String类的公共方法。它覆盖了Object类中的原始equals()方法。这个方法的签名是：

```java
public boolean equals(Object anObject)
```

该方法通过检查两个不同的字符串中的单个字符来比较两个不同的字符串。但是，该方法不仅检查内容，还检查对象是否是String的实例。因此，该方法仅在满足所有这些条件时才返回true ：

-   参数对象不为空
-   它是一个字符串对象
-   字符序列相同

## 3. contentEquals()方法

与equals()方法类似，[contentEquals()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#contentEquals(java.lang.CharSequence))方法也用于比较String 的内容。但是，与equals()方法不同，contentEquals()将CharSequence接口的任何实现作为参数。这意味着可以比较String、StringBuffer、StringBuilder、CharBuffer或Segment。

这个方法的签名是：

```java
public boolean contentEquals(StringBuffer sb)
public boolean contentEquals(CharSequence cs)
```

因此，contentEquals()方法只关心字符串的内容。如果参数是String对象，则调用equals()方法进行比较。另一方面，如果提供了通用字符序列，则该方法比较相似位置的各个字符。

如果给定参数中的字符序列与原始String匹配，则该方法返回true。与equals()方法不同，如果将null参数传递给contentEquals()方法，它会抛出NullPointerException。

## 4.例子

让我们通过编写简单的测试用例来了解这两种方法的实际应用。为了简单起见，让我们在代码中使用“Baeldung”一词。

首先，我们将获取两个相同的String对象并检查它们。在这种情况下，两种方法都将返回一个真值：

```java
String actualString = "baeldung";
String identicalString = "baeldung";

assertTrue(actualString.equals(identicalString));
assertTrue(actualString.contentEquals(identicalString));
```

接下来，我们采用具有相同内容的CharSequence的两个不同实现。对于第一个实现，我们将使用String实例化CharSequence。在这种情况下，两种方法都应返回true，因为内容和类型是相同的：

```java
CharSequence identicalStringInstance = "baeldung";

assertTrue(actualString.equals(identicalStringInstance));
assertTrue(actualString.contentEquals(identicalStringInstance));
```

对于下一个示例，我们将采用StringBuffer实现。由于contentEquals()方法仅检查内容，因此它应该返回true。但是，equals()方法应该为false：

```java
CharSequence identicalStringBufferInstance = new StringBuffer("baeldung");

assertFalse(actualString.equals(identicalStringBufferInstance));
assertTrue(actualString.contentEquals(identicalStringBufferInstance));
```

## 5.总结

在本文中，我们快速浏览了String类的两个方法。equals()方法只比较String的实例，而contentEquals()方法可以比较CharSequence的任何实现。

总而言之，当我们只关心对象的内容时，我们应该使用contentEquals() 。另一方面，有时检查对象的类型可能很重要。在这种情况下，我们应该使用equals()方法，它为我们提供了更严格的检查条件。
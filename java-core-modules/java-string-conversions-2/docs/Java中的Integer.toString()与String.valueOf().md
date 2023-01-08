## 1. 概述

众所周知，从int到String 的转换是Java中非常常见的操作。

在这个简短的教程中，我们将介绍两个非常流行的 方法， [Integer](https://docs.oracle.com/en/java/javase/18/docs/api/java.base/java/lang/Integer.html#toString())类[的](https://docs.oracle.com/en/java/javase/18/docs/api/java.base/java/lang/Integer.html#toString())[toString()和](https://docs.oracle.com/en/java/javase/18/docs/api/java.base/java/lang/Integer.html#toString())[String](https://docs.oracle.com/en/java/javase/18/docs/api/java.base/java/lang/String.html#valueOf(int))[类的 ](https://docs.oracle.com/en/java/javase/18/docs/api/java.base/java/lang/String.html#valueOf(int))[valueOf()](https://docs.oracle.com/en/java/javase/18/docs/api/java.base/java/lang/String.html#valueOf(int))，它们可以帮助我们进行这种转换。此外，我们还会使用这两种方法查看一些要点和示例，以便更好地理解它。

## 2. Integer.toString()方法

此方法接受原始数据类型int 的整数作为参数，并返回表示指定整数的String对象。

让我们看看它的签名：

```java
public static String toString(int i)
```

现在，我们将看到一些示例，我们将有符号/无符号整数作为参数传递给它，以了解整数到字符串的转换：

```java
@Test
public void whenValidIntIsPassed_thenShouldConvertToString() {
    assertEquals("11", Integer.toString(11)); 
    assertEquals("11", Integer.toString(+11)); 
    assertEquals("-11", Integer.toString(-11));
}
```

## 3. String.valueOf()方法

该方法也接受一个原始数据类型int 的整数作为参数，并返回一个String对象。有趣的是，返回的字符串表示与Integer.toString(int i)方法返回的完全相同。这是因为在内部，它使用Integer.toString()方法。

让我们看一下它在java.lang.String类中给出的内部实现：

```java
/
  Returns the string representation of the {@code int} argument.
  <p>
  The representation is exactly the one returned by the
  {@code Integer.toString} method of one argument.
 
  @param   i   an {@code int}.
  @return  a string representation of the {@code int} argument.
  @see     java.lang.Integer#toString(int, int)
 /
public static String valueOf(int i) {
    return Integer.toString(i);
}
```

为了更好地理解它，我们将看到一些示例，其中我们将有符号/无符号整数作为参数传递给它以了解整数到字符串的转换发生：

```java
@Test
public void whenValidIntIsPassed_thenShouldConvertToValidString() {
    assertEquals("11", String.valueOf(11)); 
    assertEquals("11", String.valueOf(+11));
    assertEquals("-11", String.valueOf(-11));
}
```

## 4.整数之间的差异。toString()和字符串。的价值()

综上所述，这两种方法并没有实际的区别，但我们应该了解以下几点，以免混淆。

当我们使用String.valueOf()方法时，堆栈跟踪中有一个额外的调用，因为它在内部使用相同的Integer.toString()方法。

将null对象传递给 valueOf() 方法时可能会有一些混淆，因为 当将原始int 传递给valueOf()方法时，它看起来是一样的，但实际方法调用转到不同的重载方法。 

如果给定的Integer对象为null ， Integer.toString()可能会抛出NullPointerException。String.valueOf()不会抛出异常，因为它会转到String.valueOf(Object obj)方法并返回null。请注意， 传递给String.valueOf(int i)的原始 int永远不能为空，但由于还有另一种方法String.valueOf(Object obj)，我们可能会混淆这两个重载方法。

让我们通过以下示例来理解最后一点：

```java
@Test(expected = NullPointerException.class)
public void whenNullIntegerObjectIsPassed_thenShouldThrowException() {
    Integer i = null; 
    System.out.println(String.valueOf(i)); 
    System.out.println(i.toString());
}
```

请注意原始int永远不能为null，我们正在检查它以防它下面的方法抛出异常。

## 5. JVM方法内联对String.valueOf()方法的影响

正如我们之前讨论的，String.valueOf()方法涉及额外的调用。但是，JVM 甚至可以通过方法内联消除堆栈跟踪中的额外调用。

然而，这完全取决于 JVM 是否选择内联该方法。有关更详细的描述，请访问[我们关于 JVM 中的方法内联的文章。](https://www.baeldung.com/jvm-method-inlining)

## 六，总结

在本文中，我们了解了整数。toString()和字符串。valueOf()方法。我们还研究了几个应该集中注意的地方，以避免在编程时出现混淆。
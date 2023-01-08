## 1. 概述

在本文中，我们将简要说明字符串 转换和执行toString()方法之间的区别 。我们将简要回顾这两种语法，并通过一个示例来解释使用它们的目的。最后，我们将看看哪种方法更好。

## 2.字符串类型转换和toString()方法

让我们首先快速回顾一下。使用(String) 语法与[Java](https://www.baeldung.com/java-type-casting)中的类型转换严格相关。简而言之，使用此语法的主要任务是将源变量转换为String：

```java
String str = (String) object;

```

正如我们所知，Java 中的每个类都是Object类的直接或间接扩展，后者实现[了toString ()方法](https://www.baeldung.com/java-tostring)。我们用它来获取 任何Object的String表示形式：

```java
String str = object.toString();
```

现在我们已经做了一个简短的回顾，让我们通过一些示例来帮助理解何时使用每种方法。

## 3. (字符串)与 toString()

假设我们有一个Object变量，我们想要获得一个String。我们应该使用哪种语法？

在继续之前，我们应该强调以下实用方法仅用于帮助解释我们的主题。实际上，我们不会使用这样的实用方法。

首先，我们让我们介绍一个简单的实用方法来将Object转换为String：

```java
public static String castToString(Object object) {
    if (object instanceof String) {
        return (String) object;
    }
    return null;
}
```

如我们所见，在强制转换之前，我们必须检查我们的对象变量是否是String的实例。如果我们不这样做，它可能会失败并生成ClassCastException：

```java
@Test(expected = ClassCastException.class)
public void givenIntegerObject_whenCastToObjectAndString_thenCastClassException() {
    Integer input = 1234;

    Object obj = input;
    String str = (String) obj;
}
```

但是，此操作是空安全的。在非实例化变量上使用它，即使之前没有将它应用于String变量，也会成功：

```java
@Test
public void givenNullInteger_whenCastToObjectAndString_thenSameAndNoException() {
    Integer input = null;

    Object obj = input;
    String str = (String) obj;

    assertEquals(obj, str);
    assertEquals(str, input);
    assertSame(input, str);
}
```

现在，是时候在请求的对象上实现另一个调用toString()的实用函数了：

```java
public static String getStringRepresentation(Object object) {
    if (object != null) {
        return object.toString();
    }
    return null;
}
```

在这种情况下，我们不需要知道对象的类型，无需类型转换就可以在对象上成功执行。我们只需要添加一个简单的空检查。如果我们不添加此检查，则在将非实例化变量传递给方法时可能会出现NullPointerException ：

```java
@Test(expected = NullPointerException.class)
public void givenNullInteger_whenToString_thenNullPointerException() {
    Integer input = null;

    String str = input.toString();
}
```

此外，由于核心String实现，对String变量执行toString()方法会返回相同的对象：

```java
@Test
public void givenString_whenToString_thenSame() {
    String str = "baeldung";

    assertEquals("baeldung", str.toString());
    assertSame(str, str.toString());
}
```

让我们回到我们的问题——我们应该在我们的对象变量上使用哪种语法？正如我们在上面看到的，如果我们知道我们的变量是一个 String实例，我们应该使用类型转换：

```java
@Test
public void givenString_whenCastToObject_thenCastToStringReturnsSame() {
    String input = "baeldung";
    
    Object obj = input;
    
    assertSame(input, StringCastUtils.castToString(obj));
}
```

这种方法通常更有效、更快速，因为我们不需要执行额外的函数调用。但是，请记住，我们永远不应该将String作为Object传递。这暗示我们有代码味道。

当我们传递任何其他对象类型时，我们需要显式调用toString()方法。 重要的是要记住它根据实现返回一个字符串值：

```java
@Test
public void givenIntegerNotNull_whenCastToObject_thenGetToStringReturnsString() {
    Integer input = 1234;

    Object obj = input;

    assertEquals("1234", StringCastUtils.getStringRepresentation(obj));
    assertNotSame("1234", StringCastUtils.getStringRepresentation(obj));
}
```

## 4。总结

在这个简短的教程中，我们比较了两种方法：字符串类型转换和使用toString()方法获取字符串表示形式。通过示例，我们解释了差异并探讨了何时使用 ( String)或toString()。
## 1. 概述

在本教程中，我们将讨论Java的[Number](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Number.html)类。首先，我们将了解 Number类的作用及其包含的方法。然后，我们将深入研究这个抽象类的各种实现。

## 2.号码类

Number是java.lang包中的一个抽象类。各种子类扩展了Number类。最常用的是：

-   字节
-   短的
-   整数
-   长
-   双倍的
-   漂浮

此类的主要目的是提供将相关数值转换为各种基本类型(如byte、short、int、long、double和float )的方法。

有四种抽象方法可用于帮助完成任务：

-   整数值()
-   长值()
-   双值()
-   浮动值()

Number还有两个具体方法byteValue()和shortValue()，分别返回指定数字的字节值和短值。要了解有关Number类的不同实现的更多信息，请参阅我们关于[包装类](https://www.baeldung.com/java-wrapper-classes)的文章。

在接下来的部分中，我们将详细了解这些方法及其用法。

## 三、具体方法

下面我们一一讨论具体的方法。

### 3.1. 短值()

顾名思义，此方法将指定的Number对象转换为原始短值。

默认实现将int值转换为short并返回它。但是，子类有自己的实现，它们将各自的值转换为short，然后返回。

以下是如何将Double值转换为short基本类型：

```java
@Test
public void givenDoubleValue_whenShortValueUsed_thenShortValueReturned() {
    Double doubleValue = Double.valueOf(9999.999);
    assertEquals(9999, doubleValue.shortValue());
}
```

### 3.2. 字节值()

此方法以字节值形式返回指定Number对象的值。然而，Number类的子类有它们自己的实现。

以下是如何将Float值转换为字节值：

```java
@Test
public void givenFloatValue_whenByteValueUsed_thenByteValueReturned() {
    Float floatValue = Float.valueOf(101.99F);
    assertEquals(101, floatValue.byteValue());
}
```

## 4.抽象方法

此外，Number类还有一些抽象方法和几个实现它们的子类。

在本节中，让我们快速浏览一下这些方法的使用方法。

### 4.1. 整数值()

此方法返回上下文中数字的int表示形式。

让我们看看如何将Long值更改为int：

```java
@Test
public void givenLongValue_whenInitValueUsed_thenInitValueReturned() {
    Long longValue = Long.valueOf(1000L);
    assertEquals(1000, longValue.intValue());
}
```

当然，编译器在这里通过将long值转换为int来执行[缩小](https://www.baeldung.com/java-primitive-conversions#narrowing-primitive-conversion)操作。

### 4.2. 长值()

此方法将返回指定为long的N 数字的值。

在这个例子中，我们看到了如何通过Integer类将Integer值转换为long：

```java
@Test
public void givenIntegerValue_whenLongValueUsed_thenLongValueReturned() {
    Integer integerValue = Integer.valueOf(100);
    assertEquals(100, integerValue.longValue());
}
```

与intValue()方法相反，longValue()在[扩展](https://www.baeldung.com/java-primitive-conversions#widening-primitive-conversions)原始转换后返回long值。

### 4.3. 浮动值()

我们可以使用此方法以浮点数形式返回指定 N 数的值。让我们看一下如何将Short值转换为float值：

```java
@Test
public void givenShortValue_whenFloatValueUsed_thenFloatValueReturned() {
    Short shortValue = Short.valueOf(127);
    assertEquals(127.0F, shortValue.floatValue(), 0);
}
```

同样，longValue()和 floatValue()也执行扩展原始转换。

### 4.4. 双值()

最后，此方法将给定Number类的值转换为double基本数据类型并返回它。

下面是使用此方法将Byte转换为double的示例：

```java
@Test
public void givenByteValue_whenDoubleValueUsed_thenDoubleValueReturned() {
    Byte byteValue = Byte.valueOf(120);
    assertEquals(120.0, byteValue.doubleValue(), 0);
}
```

## 5.总结

在本快速教程中，我们了解了Number类中一些最重要的方法。

最后，我们演示了如何在各种[Wrapper类](https://www.baeldung.com/java-wrapper-classes)中使用这些方法。
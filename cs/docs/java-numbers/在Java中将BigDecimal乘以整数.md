## 一、概述

*Integer*和[*BigDecimal*](https://www.baeldung.com/java-bigdecimal-biginteger)是我们日常 Java 编程中常用的两种数字类型。

在本快速教程中，我们将探讨如何将*BigDecimal*数乘以*整数*。

## 二、问题介绍

举个例子可以很快说明问题。假设我们有一个*BigDecimal*数字和一个整数：

```java
final BigDecimal BIG = new BigDecimal("42.42");
final int INT = 10;复制
```

然后我们要计算*42.42 x 10*的结果作为另一个*BigDecimal*数：

```java
final BigDecimal EXPECTED = new BigDecimal("424.2");复制
```

*BigDecimal*提供了一套数学计算方法，如*add()*、*divide()*、*subtract() 、* *multiply()*等。这些方法使我们能够方便地进行标准的算术计算。但是，我们应该注意，**这些方法只能在两个\*BigDecimal\*对象**之间进行数学运算。换句话说，它们只接受*BigDecimal*类型的参数。

因此，**我们不能将\*BigDecimal\*直接乘以\*Integer\****。*

接下来，让我们看看如何进行计算。为简单起见，我们将使用单元测试断言来验证解决方案是否产生了预期的结果。

## 3. 将*整数*转换为*BigDecimal*实例

现在我们明白*BigDecimal.multiply()*只接受*BigDecimal*数字作为参数。所以，如果我们可以将*Integer*对象转换为*BigDecimal*对象，我们就可以进行乘法计算。

***BigDecimal\*类具有\*valueOf()方法，它允许我们从\**Integer\*中获取\*BigDecimal\*数字：**

```java
BigDecimal result = BIG.multiply(BigDecimal.valueOf(INT));
                                                          
assertEquals(0, EXPECTED.compareTo(result));复制
```

如果我们试一试，测试就会通过。所以我们得到了预期的结果。

值得一提的是，除了*BigDecimal.valueOf(INT)*方法之外，我们还可以使用构造函数*new BigDecimal(INT)从**Integer中获取**BigDecimal*数字。

但是，首选**使用 \*valueOf()\*方法**。这是因为*BigDecimal*类预定义了十一个常用实例：零到十：

```java
ZERO_THROUGH_TEN = new BigDecimal[]{new BigDecimal(BigInteger.ZERO, 0L, 0, 1),
  new BigDecimal(BigInteger.ONE, 1L, 0, 1), 
  new BigDecimal(BigInteger.TWO, 2L, 0, 1), 
  new BigDecimal(BigInteger.valueOf(3L), 3L, 0, 1), 
  ...
  new BigDecimal(BigInteger.TEN, 10L, 0, 2)};
复制
```

***valueOf()\*方法检查给定的 整数是否在“零到十”范围内，并尝试重用预定义的实例。**另一方面，调用构造函数总是会创建一个新的*BigDecimal*实例。

## 4.关于断言的一句话

我们编写了一个测试，它验证了我们的解决方案是否有效。然而，好奇的眼睛可能会看到这个断言看起来有点尴尬：

```java
assertEquals(0, EXPECTED.compareTo(result));复制
```

我们中的一些人可能想简化它以提高可读性：

```java
assertEquals(EXPECTED, result);复制
```

但是，如果我们使用上面的断言运行测试，测试将失败：

```bash
org.opentest4j.AssertionFailedError: 
Expected :424.2
Actual   :424.20复制
```

这是因为***BigDecimal\*的\*equals()\*方法不仅比较两个\*BigDecimal\*数的值，它还检查两个\*BigDecimal\*数的\*小数\*位数：**

```java
public boolean equals(Object x) {
    if (x instanceof BigDecimal xDec) {
        if (x == this) {
            return true;
        } else if (this.scale != xDec.scale) {
            return false;
        } else {
        ...
}复制
```

在我们的例子中，我们只对*BigDecimal*数字的值感兴趣。因此，我们需要调用 *compareTo()*方法。

或者，我们可以使用[AssertJ](https://www.baeldung.com/introduction-to-assertj)的*isEqualByComparingTo()*方法使代码更易于阅读：

```java
assertThat(result).isEqualByComparingTo(EXPECTED);复制
```

## 5.结论

在本文中，我们学习了如何将*BigDecimal*乘以*Integer。*由于*BigDecimal.multiply()* 只接受*BigDecimal*对象作为参数，我们需要在调用*multiply()*方法之前将*Integer*对象转换为*BigDecimal*实例。
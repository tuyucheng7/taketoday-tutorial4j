## 1. 概述

在Java中，当我们使用Integer、Long、Float和Double等类型时，我们经常想检查数字是正数还是负数。这是一个基本且常见的数字运算。

在本快速教程中，我们将讨论如何检查给定数字是正数还是负数。

## 二、问题简介

检查一个数字是正数还是负数是一个非常简单的问题。然而，在我们开始研究实现之前，让我们先了解一下积极和消极的定义。

给定[实数](https://en.wikipedia.org/wiki/Real_number) n，如果 n大于零，则为正数。否则，如果 n小于零，则为负。所以，我们还有一个特殊情况：零。[零既不是正数也不是负数](https://en.wikipedia.org/wiki/0)。

因此，我们可以创建一个[枚举](https://www.baeldung.com/a-guide-to-java-enums)来涵盖这三种可能性：

```java
enum Result {
    POSITIVE, NEGATIVE, ZERO
}

```

在本教程中，我们将介绍两种不同的方法来检查数字是正数、负数还是零。为简单起见，我们将使用单元测试断言来验证结果。

那么接下来，让我们看看他们的行动。

## 3. 使用“ < ”和“ > ”运算符

根据定义，一个数是正数还是负数取决于与零的比较结果。因此，我们可以使用Java的“大于(>)”和“小于(<)”[运算符](https://www.baeldung.com/java-operators)来解决这个问题。

下面我们以Integer类型为例，创建一个方法来做校验：

```java
static Result byOperator(Integer integer) {
    if (integer > 0) {
        return POSITIVE;
    } else if (integer < 0) {
        return NEGATIVE;
    }
    return ZERO;
}

```

上面的代码解释得很清楚。根据与零比较的结果，我们确定结果是正数、负数还是零。

让我们创建一个测试来验证我们的方法：

```java
assertEquals(POSITIVE, PositiveOrNegative.byOperator(42));
assertEquals(ZERO, PositiveOrNegative.byOperator(0));
assertEquals(NEGATIVE, PositiveOrNegative.byOperator(-700));
```

毫不奇怪，如果我们执行测试，测试就会通过。

当然，如果我们可以将Integer参数更改为Long、Float或Double ，同样的逻辑也适用。

## 4. 使用signum()方法

我们已经了解了如何使用 < 和 > 运算符检查数字是正数还是负数。或者，我们可以使用signum()方法来获取给定数字的符号。

对于整数和长整数，我们可以调用[Integer.signum()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Integer.html#signum(int))和[Long.signum()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Long.html#signum(long))方法。

当n 为负数、零或正数 时 ， signum(n)方法返回-1、0和1 。

下面以一个Integer为例，创建一个 check 方法：

```java
static Result bySignum(Integer integer) {
    int result = Integer.signum(integer);
    if (result == 1) {
        return Result.POSITIVE;
    } else if (result == -1) {
        return Result.NEGATIVE;
    }
    return Result.ZERO;
}
```

下面的测试验证了我们的方法是否按预期工作：

```java
assertEquals(POSITIVE, PositiveOrNegative.bySignum(42));
assertEquals(ZERO, PositiveOrNegative.bySignum(0));
assertEquals(NEGATIVE, PositiveOrNegative.bySignum(-700));
```

与Integer和Long不同，Float和Double类不提供signum()方法。但是，Math.signum [()](https://www.baeldung.com/java-lang-math#signum)方法接受Float和Double数字作为参数，例如：

```java
static Result bySignum(Float floatNumber) {
    float result = Math.signum(floatNumber);
   
    if (result.compareTo(1.0f) == 0) {
        return Result.POSITIVE;
    } else if (result.compareTo(-1.0f) == 0) {
        return Result.NEGATIVE;
    }
    return Result.ZERO;
}
```

最后，让我们创建一个测试来验证该方法是否可以检查浮点数是正数还是负数：

```java
assertEquals(POSITIVE, PositiveOrNegative.bySignum(4.2f));
assertEquals(ZERO, PositiveOrNegative.bySignum(0f));
assertEquals(NEGATIVE, PositiveOrNegative.bySignum(-7.7f));
```

如果我们试一试，测试就会通过。

## 5.总结

在本文中，我们学习了两种确定给定数字是正数、负数还是零的方法。
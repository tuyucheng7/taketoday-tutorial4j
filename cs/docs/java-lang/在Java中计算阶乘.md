## 1. 概述

给定一个非负整数n，阶乘是所有小于或等于n的正整数的乘积。

在本快速教程中，我们将探讨在Java中计算给定数字的阶乘的不同方法。

## 2. 20以内的阶乘

### 2.1. 使用for循环的阶乘

让我们看一个使用for循环的基本阶乘算法：

```java
public long factorialUsingForLoop(int n) {
    long fact = 1;
    for (int i = 2; i <= n; i++) {
        fact = fact  i;
    }
    return fact;
}
```

上述解决方案适用于最多 20 的数字。但是，如果我们尝试大于 20 的值，那么它将失败，因为结果太大而无法放入 long中，从而导致溢出。

让我们再看看几个，注意其中每一个都只适用于少数。

### 2.2. 使用Java8 流的阶乘

我们还可以使用[Java 8 Stream API](https://www.baeldung.com/java-8-streams-introduction)轻松计算阶乘：

```java
public long factorialUsingStreams(int n) {
    return LongStream.rangeClosed(1, n)
        .reduce(1, (long x, long y) -> x  y);
}
```

在这个程序中，我们首先使用 LongStream遍历 1 和n之间的数字。然后我们使用reduce()，它使用身份值和累加器函数来减少步骤。

### 2.3. 使用递归的阶乘

让我们看另一个阶乘程序的例子，这次使用递归：

```java
public long factorialUsingRecursion(int n) {
    if (n <= 2) {
        return n;
    }
    return n  factorialUsingRecursion(n - 1);
}
```

### 2.4. 使用 Apache Commons Math 进行阶乘

[Apache Commons Math](https://www.baeldung.com/apache-commons-math)有一个带有静态阶乘方法的CombinatoricsUtils类，我们可以用它来计算阶乘。

为了包含 Apache Commons Math，我们将[commons -math3依赖](https://search.maven.org/search?q=a:commons-math3)项添加到我们的 pom中：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-math3</artifactId>
    <version>3.6.1</version>
</dependency>
```

让我们看一个使用 CombinatoricsUtils 类的示例：

```java
public long factorialUsingApacheCommons(int n) {
    return CombinatoricsUtils.factorial(n);
}
```

请注意，它的返回类型是long，就像我们自己开发的解决方案一样。

这意味着如果计算值超过Long.MAX_VALUE，则会抛出MathArithmeticException。

为了变得更大，我们将需要一个不同的返回类型。

## 3. 大于 20 的数的阶乘

### 3.1. 使用BigInteger进行阶乘

如前所述，long数据类型只能用于n <= 20的阶乘。

对于更大的n值，我们可以使用java.math 包中的 BigInteger 类，它最多可以保存2 ^ Integer.MAX_VALUE的值：

```java
public BigInteger factorialHavingLargeResult(int n) {
    BigInteger result = BigInteger.ONE;
    for (int i = 2; i <= n; i++)
        result = result.multiply(BigInteger.valueOf(i));
    return result;
}
```

### 3.2. 使用番石榴的阶乘

Google 的[Guava](https://www.baeldung.com/category/guava/)库还提供了一种用于计算较大数字的阶乘的实用方法。

要包含该库，我们可以将其[guava依赖 项](https://search.maven.org/search?q=guava)添加到我们的 pom中：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

现在，我们可以使用BigIntegerMath类中的静态阶乘方法 来计算给定数字的阶乘：

```java
public BigInteger factorialUsingGuava(int n) {
    return BigIntegerMath.factorial(n);
}
```

## 4。总结

在本文中，我们看到了几种使用核心Java以及几个外部库计算阶乘的方法。

我们首先看到了使用 long数据类型计算20以内数字的阶乘的解决方案。然后，我们看到了几种将 BigInteger 用于大于 20 的数字的方法。
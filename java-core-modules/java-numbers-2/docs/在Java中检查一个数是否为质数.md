## 1. 概述

首先，让我们回顾一些基本理论。

简而言之，如果一个数只能被 1 和该数本身整除，则它就是素数。非质数称为合数。第一既不是质数也不是合数。

在本文中，我们将了解在Java中检查数字素数的不同方法。

## 2.自定义实现

通过这种方法，我们可以检查 2 和(数字的平方根)之间的数字是否可以准确地除以该数字。

如果数字是质数，则以下逻辑将返回true ：

```java
public boolean isPrime(int number) {
    return number > 1 
      && IntStream.rangeClosed(2, (int) Math.sqrt(number))
      .noneMatch(n -> (number % n == 0));
}
```

## 3.使用大整数

BigInteger类通常用于存储大整数，即大于 64 位的整数。它提供了一些有用的 API 来处理int和long值。

其中一个 API 是isProbablePrime。如果数字绝对是合数，则此 API 返回false ，如果它有一定概率为素数，则返回true 。它在处理大整数时很有用，因为要完全验证这些整数可能需要大量的计算。

快速旁注– isProbablePrime API 使用所谓的“Miller – Rabin 和 Lucas – Lehmer”素数测试来检查数字是否可能是素数。在数字小于 100 位的情况下，仅使用“Miller-Rabin”测试，否则，两个测试都用于检查数字的素数。

[“Miller-Rabin”测试](https://www.baeldung.com/cs/miller-rabin-method)迭代固定次数以确定数字的素数，此迭代计数由简单检查确定，该检查涉及数字的位长度和传递给 API 的确定性值：

```java
public boolean isPrime(int number) {
    BigInteger bigInt = BigInteger.valueOf(number);
    return bigInt.isProbablePrime(100);
}
```

## 4. 使用 Apache Commons 数学

Apache Commons Math API 提供了一个名为org.apache.commons.math3.primes.Primes 的方法，我们将使用它来检查数字的素数。

首先，我们需要通过在pom.xml中添加以下依赖项来导入 Apache Commons Math 库：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-math3</artifactId>
    <version>3.6.1</version>
</dependency>
```

可以在[此处](https://search.maven.org/classic/#search|ga|1|a%3A"commons-math3")找到最新版本的 commons-math3 。

我们可以通过调用方法来进行检查：

```java
Primes.isPrime(number);
```

## 5.总结

在这篇简短的文章中，我们看到了三种检查数字素数的方法。
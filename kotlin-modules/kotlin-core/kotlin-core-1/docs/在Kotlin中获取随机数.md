## 1. 简介

这个简短的教程将演示如何使用Kotlin生成随机数。

## 2. 使用java.lang.Math

在Kotlin中生成随机数的最简单方法是使用java.lang.Math，下面的示例将生成一个介于0和1之间的随机double数。

```kotlin
@Test
fun whenRandomNumberWithJavaUtilMath_thenResultIsBetween0And1() {
    val randomNumber = Math.random()
    assertTrue { randomNumber >= 0 }
    assertTrue { randomNumber < 1 }
}
```

## 3. 使用ThreadLocalRandom

我们还可以使用java.util.concurrent.ThreadLocalRandom来生成一个随机的double、int或long值，**以这种方式生成的int和long值可以是正数也可以是负数**。

**ThreadLocalRandom是线程安全的，并在多线程环境中提供更好的性能**，因为它为每个线程提供了一个单独的Random对象，从而减少了线程之间的争用：

```kotlin
@Test
fun whenRandomNumberWithJavaThreadLocalRandom_thenResultsInDefaultRanges() {
    val randomDouble = ThreadLocalRandom.current().nextDouble()
    val randomInteger = ThreadLocalRandom.current().nextInt()
    val randomLong = ThreadLocalRandom.current().nextLong()
    assertTrue { randomDouble >= 0 }
    assertTrue { randomDouble < 1 }
    assertTrue { randomInteger >= Integer.MIN_VALUE }
    assertTrue { randomInteger < Integer.MAX_VALUE }
    assertTrue { randomLong >= Long.MIN_VALUE }
    assertTrue { randomLong < Long.MAX_VALUE }
}
```

## 4. 使用Kotlin.js的随机数

我们还可以**使用kotlin.js库中的Math类**生成随机double数。

```kotlin
@Test
fun whenRandomNumberWithKotlinJSMath_thenResultIsBetween0And1() {
    val randomDouble = Math.random()
    assertTrue { randomDouble >=0 }
    assertTrue { randomDouble < 1 }
}
```

## 5. 使用纯Kotlin在给定范围内随机数

使用纯Kotlin，我们可以**创建一个数字集合，将其打乱顺序，然后从该集合中取出第一个元素**：

```kotlin
@Test
fun whenRandomNumberWithKotlinNumberRange_thenResultInGivenRange() {
    val randomInteger = (1..12).shuffled().first()
    assertTrue { randomInteger >= 1 }
    assertTrue { randomInteger <= 12 }
}
```

## 6. 使用ThreadLocalRandom在给定范围内随机数

第3节中介绍的ThreadLocalRandom也可用于生成给定范围内的随机数：

```kotlin
@Test
fun whenRandomNumberWithJavaThreadLocalRandom_thenResultsInGivenRanges() {
    val randomDouble = ThreadLocalRandom.current().nextDouble(1.0, 10.0)
    val randomInteger = ThreadLocalRandom.current().nextInt(1, 10)
    val randomLong = ThreadLocalRandom.current().nextLong(1, 10)
    assertTrue { randomDouble >= 1 }
    assertTrue { randomDouble < 10 }
    assertTrue { randomInteger >= 1 }
    assertTrue { randomInteger < 10 }
    assertTrue { randomLong >= 1 }
    assertTrue { randomLong < 10 }
}
```

## 7. 伪随机数生成器与安全随机数生成器

java.util.Random的标准JDK实现使用[线性同余生成器](https://en.wikipedia.org/wiki/Linear_congruential_generator)来提供随机数，这种算法的问题在于它的加密强度不高，并且它的输出可以被攻击者预测。

为了克服这个问题，我们应该在需要良好安全性的地方使用java.security.SecureRandom：

```kotlin
fun whenRandomNumberWithJavaSecureRandom_thenResultsInGivenRanges() {
    val secureRandom = SecureRandom()
    secureRandom.nextInt(100)
    assertTrue { randomLong >= 0 }
    assertTrue { randomLong < 100 }
}
```

SecureRandom使用加密性强的伪随机数生成器([CSPRNG](https://en.wikipedia.org/wiki/Cryptographically_secure_pseudorandom_number_generator))生成加密性强的随机值。

应用程序不应使用其他方式在与安全性相关的位置生成安全随机数。

## 8. 总结

在本文中，我们学习了几种在Kotlin中生成随机数的方法。
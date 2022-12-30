## 1. 概述

在本教程中，我们将讨论如何使用三种不同的方法在Kotlin中生成随机字母数字字符串：Java Random、Kotlin Random和来自Apache Commons Lang库的RandomStringUtils。

## 2. 依赖关系

在我们深入学习本教程之前，让我们将[Apache Commons Lang](https://search.maven.org/search?q=a:commons-lang3)依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

此外，我们可以设置一个常量来定义我们将生成的随机字符串的长度：

```kotlin
const val STRING_LENGTH = 10
```

## 3. 关于测试

在本教程中，我们将使用单元测试断言来验证每种方法是否给出了预期的结果。然而，[随机性测试](https://www.baeldung.com/cs/randomness)本身是一个复杂的话题，因此，为简单起见，我们将每个解决方案执行10000次，并期望生成的10000个字符串完全不同。此外，每个字符串的长度应为10并匹配“^[0-9a-zA-Z]+$”正则表达式。

我们将每个解决方案都设为一个Kotlin函数，该函数不接收任何参数并返回随机字符串。

因此，让我们首先创建[一个高阶函数](https://www.baeldung.com/kotlin/interfaces-higher-order-functions)来验证函数：

```kotlin
private fun verifyTheSolution(randomFunc: () -> String) {
    val randoms = List(10_000) { randomFunc() }

    assertTrue { randoms.all { it.matches(Regex("^[0-9a-zA-Z]+$") } }
    assertTrue { randoms.none { it.length != STRING_LENGTH } }
    assertTrue { randoms.size == randoms.toSet().size } // no duplicates
}
```

如上面的代码所示，verifyTheSolution()函数接收一个[lambda表达式](https://www.baeldung.com/kotlin/lambda-expressions)，该表达式将是要测试的解决方案函数。

接下来，我们调用randomFunc()函数10000次并将结果存储在randoms列表中。然后，我们可以应用前面提到的验证。

## 4. Java Random

首先，让我们看看如何使用Java Random生成一个随机字符串。

在此示例中，我们将使用ThreadLocalRandom，它为每个线程提供一个Random实例并防止争用：

```kotlin
val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

fun randomStringByJavaRandom() = ThreadLocalRandom.current()
    .ints(STRING_LENGTH.toLong(), 0, charPool.size)
    .asSequence()
    .map(charPool::get)
    .joinToString("")
```

在这里，我们通过生成索引从charPool中获取10个随机字母数字字符，然后将它们拼接在一起以创建随机字符串。

ThreadLocalRandom从JDK 7开始可用，我们可以改用java.util.Random，但是如果多个线程使用同一个Random实例，则同一个种子会被多个线程共享，从而导致线程争用。

但是，ThreadLocalRandom和Random都不是加密安全的，因为可以猜测从生成器返回的下一个值，不过Java也确实提供了明显较慢的java.security.SecureRandom来安全地生成随机值。

接下来，让我们看看这个函数是否可以通过我们的verifyTheSolution()测试：

```kotlin
@Test
fun givenAStringLength_whenUsingJava_thenReturnAlphanumericString() {
    verifyTheSolution { randomStringByJavaRandom() }
}
```

如果我们运行上面的测试，它就会通过。

## 5. Kotlin Random

从Kotlin 1.3开始，kotlin.random.Random作为多平台功能提供，它在JDK 6和7中使用java.util.Random，在JDK 8+中使用ThreadLocalRandom，在Javascript中使用Math.random。

我们可以用同样的方法得到一个随机字符串：

```kotlin
fun randomStringByKotlinRandom() = (1..STRING_LENGTH)
    .map { Random.nextInt(0, charPool.size).let { charPool[it] } }
    .joinToString("")
```

此外，它通过了我们的测试：

```kotlin
@Test
fun givenAStringLength_whenUsingKotlin_thenReturnAlphanumericString() {
    verifyTheSolution { randomStringByKotlinRandom() }
}
```

值得一提的是，Kotlin的Collection提供了random()函数，它允许我们从集合中随机检索一个元素。因此，我们可以使用random()简化我们的解决方案：

```kotlin
fun randomStringByKotlinCollectionRandom() = List(STRING_LENGTH) { charPool.random() }.joinToString("")
```

正如我们所看到的，charPool.random()使该函数更易于阅读和理解。

毫不奇怪，这个解决方案也通过了我们的测试：

```kotlin
@Test
fun givenAStringLength_whenUsingKotlinCollectionRandom_thenReturnAlphanumericString() {
    verifyTheSolution { randomStringByKotlinCollectionRandom() }
}
```

## 6. Apache Common Lang 

最后，在Kotlin中，我们仍然可以使用Apache Common Lang库来生成随机字符串：

```kotlin
fun randomStringByApacheCommons() = RandomStringUtils.randomAlphanumeric(STRING_LENGTH)
```

在这个例子中，我们只需调用RandomStringUtils.randomAlphanumeric()来获取具有预定义长度的字符串。

我们应该注意到RandomStringUtils通过使用java.util.Random生成随机值，正如我们上面讨论的那样，它在密码学上是不安全的。因此，为了生成安全的令牌或值，我们可以使用Apache Commons Crypto中的CryptoRandom或Java的SecureRandom。

我们有一个关于如何[在Java中生成随机字符串](https://www.baeldung.com/java-random-string)的教程，以更详细地介绍这个主题。

如果我们用verifyTheSolution()函数测试这个函数，它也会通过：

```kotlin
@Test
fun givenAStringLength_whenUsingKotlinCollectionRandom_thenReturnAlphanumericString() {
    verifyTheSolution { randomStringByKotlinCollectionRandom() }
}
```

## 7. 总结

在本文中，我们介绍了三种在Kotlin中生成随机字母数字字符串的方法，并探讨了每种方法的细微差别。
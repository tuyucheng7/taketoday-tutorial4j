## 1. 概述

在本教程中，我们将探讨在Java中生成随机数的不同方法。

## 2. 使用Java API

Java API 为我们提供了几种方法来实现我们的目的。让我们看看其中的一些。

### 2.1. java.lang.数学

Math类的随机方法将返回0.0(含)到 1.0(不含)范围内的双精度值。让我们看看我们如何使用它来获取由min和max定义的给定范围内的随机数：

```java
int randomWithMathRandom = (int) ((Math.random()  (max - min)) + min);
```

### 2.2. java.util.随机

在Java1.7 之前，最流行的生成随机数的方法是使用nextInt。有两种使用此方法的方式，带参数和不带参数。无参数调用以大致相等的概率返回任何int值。所以，我们很可能会得到负数：

```java
Random random = new Random();
int randomWithNextInt = random.nextInt();
```

如果我们使用带有绑定参数的netxInt调用，我们将得到一个范围内的数字：

```java
int randomWintNextIntWithinARange = random.nextInt(max - min) + min;
```

这将为我们提供一个介于 0(含)和参数(不含)之间的数字。因此，绑定参数必须大于 0。否则，我们将得到java.lang.IllegalArgumentException。

Java 8 引入了返回java.util.stream.IntStream的新ints方法。 让我们看看如何使用它们。

不带参数的ints方法返回无限的int值流：

```java
IntStream unlimitedIntStream = random.ints();
```

我们也可以传入一个参数来限制流的大小：

```java
IntStream limitedIntStream = random.ints(streamSize);
```

当然，我们可以为生成的范围设置最大值和最小值：

```java
IntStream limitedIntStreamWithinARange = random.ints(streamSize, min, max);
```

### 2.3. java.util.concurrent.ThreadLocalRandom

Java 1.7 版本为我们带来了一种通过ThreadLocalRandom类生成随机数的更有效的新方法。这与Random类有三个重要区别：

-   我们不需要显式启动ThreadLocalRandom的新实例。这有助于我们避免创建大量无用实例和浪费垃圾收集器时间的错误
-   我们不能为ThreadLocalRandom设置种子，这会导致一个真正的问题。如果我们需要设置种子，那么我们应该避免这种生成随机数的方式
-   随机类[在多线程环境中](https://www.baeldung.com/java-thread-local-random)表现不佳

现在，让我们看看它是如何工作的：

```java
int randomWithThreadLocalRandomInARange = ThreadLocalRandom.current().nextInt(min, max);
```

使用Java8 或更高版本，我们有了新的可能性。首先，我们有两个nextInt方法的变体：

```java
int randomWithThreadLocalRandom = ThreadLocalRandom.current().nextInt();
int randomWithThreadLocalRandomFromZero = ThreadLocalRandom.current().nextInt(max);
```

其次，更重要的是，我们可以使用ints方法：

```java
IntStream streamWithThreadLocalRandom = ThreadLocalRandom.current().ints();
```

### 2.4. java.util.SplittableRandom

Java 8 还为我们带来了一个非常快速的生成器[——SplittableRandom](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/SplittableRandom.html)类。

正如我们在 JavaDoc 中看到的，这是一个用于并行计算的生成器。重要的是要知道实例不是线程安全的。所以，我们在使用这个类的时候要小心。

我们有可用的nextInt和ints方法。使用nextInt我们可以使用两个参数调用直接设置顶部和底部范围：

```java
SplittableRandom splittableRandom = new SplittableRandom();
int randomWithSplittableRandom = splittableRandom.nextInt(min, max);
```

这种使用方式检查max参数是否大于min。否则，我们将得到一个IllegalArgumentException。但是，它不会检查我们使用的是正数还是负数。因此，任何参数都可以为负数。此外，我们还有可用的单参数和零参数调用。这些工作方式与我们之前描述的相同。

我们也有可用的ints方法。这意味着我们可以很容易地得到一个int值流。澄清一下，我们可以选择有限流或无限流。对于有限的流，我们可以为号码生成范围设置top和bottom：

```java
IntStream limitedIntStreamWithinARangeWithSplittableRandom = splittableRandom.ints(streamSize, min, max);
```

### 2.5. java.security.SecureRandom

如果我们有对安全敏感的应用程序，我们应该考虑使用[SecureRandom](https://www.baeldung.com/java-secure-random)。这是一个密码强大的生成器。默认构造的实例不使用加密随机种子。所以，我们应该：

-   设置种子——因此，种子将不可预测
-   将java.util.secureRandomSeed系统属性设置为true

此类继承自java.util.Random。因此，我们可以使用上面看到的所有方法。例如，如果我们需要获取任何int值，那么我们将调用不带参数的nextInt ：

```java
SecureRandom secureRandom = new SecureRandom();
int randomWithSecureRandom = secureRandom.nextInt();
```

另一方面，如果我们需要设置范围，我们可以使用绑定参数调用它：

```java
int randomWithSecureRandomWithinARange = secureRandom.nextInt(max - min) + min;
```

我们必须记住，如果参数不大于零，这种使用方式会抛出IllegalArgumentException 。

## 3. 使用第三方API

正如我们所看到的，Java 为我们提供了很多用于生成随机数的类和方法。但是，也有用于此目的的第三方 API。

我们将看看其中的一些。

### 3.1. org.apache.commons.math3.random.RandomDataGenerator

Apache Commons 项目的公共数学库中有很多生成器。最简单，也可能是最有用的是RandomDataGenerator。它使用[Well19937c](https://en.wikipedia.org/wiki/Well_equidistributed_long-period_linear)算法进行随机生成。但是，我们可以提供我们的算法实现。

让我们看看如何使用它。首先，我们必须添加依赖：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-math3</artifactId>
    <version>3.6.1</version>
</dependency>
```

最新版本的commons-math3可以在[Maven Central](https://search.maven.org/search?q=g:commons-math AND a:commons-math)上找到。

然后我们就可以开始使用它了：

```java
RandomDataGenerator randomDataGenerator = new RandomDataGenerator();
int randomWithRandomDataGenerator = randomDataGenerator.nextInt(min, max);
```

### 3.2. it.unimi.dsi.util.XoRoShiRo128PlusRandom

当然，这是最快的随机数生成器实现之一。它是由米兰大学信息科学系开发的。

该库也可在[Maven 中央](https://search.maven.org/search?q=g:it.unimi.dsi AND a:dsiutils)存储库中获得。所以，让我们添加依赖项：

```xml
<dependency>
    <groupId>it.unimi.dsi</groupId>
    <artifactId>dsiutils</artifactId>
    <version>2.6.0</version>
</dependency>
```

这个生成器继承自java.util.Random。然而，如果我们看一下[JavaDoc](http://dsiutils.di.unimi.it/docs/it/unimi/dsi/util/XoRoShiRo128PlusRandom.html)，我们就会意识到只有一种使用方法——通过nextInt方法。最重要的是，此方法仅适用于零参数和单参数调用。任何其他调用都将直接使用java.util.Random方法。

例如，如果我们想得到一个范围内的随机数，我们会这样写：

```java
XoRoShiRo128PlusRandom xoroRandom = new XoRoShiRo128PlusRandom();
int randomWithXoRoShiRo128PlusRandom = xoroRandom.nextInt(max - min) + min;
```

## 4。总结

有几种方法可以实现随机数生成。但是，没有最好的方法。因此，我们应该选择最适合我们需要的那个。
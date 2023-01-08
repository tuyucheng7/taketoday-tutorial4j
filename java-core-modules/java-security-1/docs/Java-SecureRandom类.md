## 1. 概述

在这个简短的教程中，我们将了解java.security.SecureRandom，这是一个提供强大的加密随机数生成器的类。

## 2.与java.util.Random的比较

java.util.Random的标准 JDK 实现 使用[线性同余生成器](https://en.wikipedia.org/wiki/Linear_congruential_generator)(LCG) 算法来提供随机数。该算法的问题在于它的加密强度不高。换句话说，生成的值更可预测，因此攻击者可以使用它来破坏我们的系统。

为了克服这个问题，我们应该在任何安全决策中使用java.security.SecureRandom。它通过使用加密强度高的伪随机数生成器( [CSPRNG](https://en.wikipedia.org/wiki/Cryptographically_secure_pseudorandom_number_generator) ) 来生成加密强度高的随机值。

为了更好地理解 LCG 和 CSPRNG 之间的区别，请查看下图，其中显示了两种算法的值分布：

[![安全随机算法](https://www.baeldung.com/wp-content/uploads/2019/07/secure_random_algorithms.png)](https://www.baeldung.com/wp-content/uploads/2019/07/secure_random_algorithms.png)

 

## 3. 生成随机值

使用SecureRandom最常见的方法是生成int、long、float、double或boolean值：

```java
int randomInt = secureRandom.nextInt();
long randomLong = secureRandom.nextLong();
float randomFloat = secureRandom.nextFloat();
double randomDouble = secureRandom.nextDouble();
boolean randomBoolean = secureRandom.nextBoolean();
```

为了生成int值，我们可以将上限作为参数传递：

```java
int randomInt = secureRandom.nextInt(upperBound);
```

此外，我们可以为int、double和long生成一个值流：

```java
IntStream randomIntStream = secureRandom.ints();
LongStream randomLongStream = secureRandom.longs();
DoubleStream randomDoubleStream = secureRandom.doubles();
```

对于所有流，我们可以显式设置流大小：

```java
IntStream intStream = secureRandom.ints(streamSize);
```

以及原点(包括)和绑定(不包括)值：

```java
IntStream intStream = secureRandom.ints(streamSize, originValue, boundValue);
```

我们还可以生成一个随机字节序列。nextBytes ()函数接受用户提供的字节数组并用随机字节填充它：

```java
byte[] values = new byte[124];
secureRandom.nextBytes(values);
```

## 4. 选择算法

默认情况下，SecureRandom使用 SHA1PRNG 算法生成随机值。我们可以通过调用getInstance()方法明确地让它使用另一种算法：

```java
SecureRandom secureRandom = SecureRandom.getInstance("NativePRNG");
```

使用新运算符创建SecureRandom等同于SecureRandom.getInstance(“SHA1PRNG”)。

[Java 中可用的所有随机数生成器都可以在官方文档页面上](https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html#securerandom-number-generation-algorithms)找到。

## 5. 种子

SecureRandom的每个实例都是使用初始种子创建的。它作为提供随机值的基础，每次我们生成新值时都会发生变化。

使用new运算符或调用SecureRandom.getInstance()将从[/dev/urandom](https://tersesystems.com/blog/2015/12/17/the-right-way-to-use-securerandom/)[获取默认种子](https://tersesystems.com/blog/2015/12/17/the-right-way-to-use-securerandom/)。

我们可以通过将种子作为构造函数参数传递来更改种子：

```java
byte[] seed = getSecureRandomSeed();
SecureRandom secureRandom = new SecureRandom(seed);
```

或者通过在已经创建的对象上调用 setter 方法：

```java
byte[] seed = getSecureRandomSeed();
secureRandom.setSeed(seed);
```

请记住，如果我们使用相同的种子创建两个SecureRandom实例，并且对每个实例进行相同的方法调用序列，它们将生成并返回相同的数字序列。

## 六，总结

在本教程中，我们了解了SecureRandom 的工作原理以及如何使用它来生成随机值。
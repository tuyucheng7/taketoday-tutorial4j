## 1. 概述

Java 提供了一些原语，例如 int 或long来执行整数运算。但有时，我们需要存储数字，这会超出这些数据类型的可用限制。

在本教程中，我们将更深入地研究BigInteger类。我们将通过查看源代码来检查它的结构并回答问题——如何在可用[原始数据类型](https://www.baeldung.com/java-primitives)的限制之外存储大量数字？

## 2.大整数类

正如我们所知，[BigInteger类](https://www.baeldung.com/java-bigdecimal-biginteger#biginteger)用于涉及比原始long类型更大的非常大的整数计算的数学运算。它表示不可变的任意精度整数。

在继续之前，让我们记住，在Java中，所有字节都使用[大端表示法](https://www.baeldung.com/cs/most-significant-bit)在[二进制补码系统](https://www.baeldung.com/cs/two-complement)中表示。它将一个字的最高有效字节存储在最小的内存地址(最低索引)处。此外，字节的第一位也是符号位。让我们检查示例字节值：

-   1000 0000代表-128
-   0111 1111代表127
-   1111 1111代表 -1

所以现在，让我们检查[源代码](https://hg.openjdk.java.net/jdk8/jdk8/jdk/file/tip/src/share/classes/java/math/BigInteger.java) 并解释它如何存储超过可用基元限制的给定数字。

### 2.1. 符号

signum属性确定BigInteger的符号。三个整数值表示值的符号：-1表示负数，0表示零，1 表示正数：

```java
assertEquals(1, BigInteger.TEN.signum());
assertEquals(-1, BigInteger.TEN.negate().signum());
assertEquals(0, BigInteger.ZERO.signum());
```

请注意， 由于幅度数组， BigInteger.ZERO的 符号必须为0 。该值确保每个BigInteger值只有一种表示形式。

### 2.2. 整数 [] 杂志

BigInteger类的所有魔力都始于mag属性。它使用二进制表示将给定值存储在数组中，这允许省略原始数据类型限制。

此外， BigInteger 将它们分组为 32 位部分——一组四个字节。因此，类定义中的 magnitude 被声明为int数组：

```
int[] mag;
```

该数组以大端表示法保存给定值的大小。该数组的第零个元素是幅度的最重要整数。让我们使用 [BigInteger(byte[] bytes)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/math/BigInteger.html#(byte[]))检查它：

```java
assertEquals(new BigInteger("1"), new BigInteger(new byte[]{0b1}))
assertEquals(new BigInteger("2"), new BigInteger(new byte[]{0b10}))
assertEquals(new BigInteger("4"), new BigInteger(new byte[]{0b100}))
```

此构造函数将包含二进制补码表示的给定字节数组转换为值。

由于有一个符号大小变量 ( signum )，我们不使用第一位作为 value 的符号位。让我们快速检查一下：

```java
byte[] bytes = { -128 }; // 1000 0000
assertEquals(new BigInteger("128"), new BigInteger(1, bytes));
assertEquals(new BigInteger("-128"), new BigInteger(-1, bytes));
```

[我们使用BigInteger(int signum, byte[] magnitude)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/math/BigInteger.html#(int,byte[]))构造函数创建了两个不同的值。它将符号幅度表示转换为BigInteger。我们重复使用了相同的字节数组，只改变了一个符号值。

我们还可以使用[toString(int radix)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/math/BigInteger.html#toString(int))方法打印大小：

```java
assertEquals("10000000", new BigInteger(1, bytes));
assertEquals("-10000000", new BigInteger(-1, bytes));
```

请注意，对于负值，添加了减号。

最后，幅度的最重要整数必须是非零的。这意味着BigInteger.ZERO 有一个零长度的 mag 数组：

```scss
assertEquals(0, BigInteger.ZERO.bitCount()); 
assertEquals(BigInteger.ZERO, new BigInteger(0, new byte[]{}));
```

现在，我们将跳过检查其他属性。由于冗余，它们被标记为已弃用，仅用作内部缓存。

现在让我们直接进入更复杂的示例，并检查BigInteger 如何在原始数据类型上存储数字。

## 3. BigInteger大于 Long.MAX_VALUE。

正如我们已经知道的 ， long数据类型是一个 64 位二进制补码整数。signed long 的最小值为 -2 63 (1000 0000 … 0000)，最大值为 2 63 -1 (0111 1111 … 1111)。 要创建超过这些限制的数字，我们需要使用BigInteger类。

现在让我们创建一个比Long.MAX_VALUE大 1 的值，等于 2 63。根据上一章的资料，它需要有：

-   标志属性设置为 1 ，
-   一个mag数组，总共有 64 位，其中只设置了最高有效位(1000 0000 … 0000)。

首先，让我们 使用[setBit(int n)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/math/BigInteger.html#setBit(int))函数创建一个BigInteger ：

```java
BigInteger bi1 = BigInteger.ZERO.setBit(63);
String str = bi1.toString(2);
assertEquals(64, bi1.bitLength());
assertEquals(1, bi1.signum());
assertEquals("9223372036854775808", bi1.toString());
assertEquals(BigInteger.ONE, bi1.substract(BigInteger.valueOf(Long.MAX_VALUE)));

assertEquals(64, str.length());
assertTrue(str.matches("^10{63}$")); // 1000 0000 ... 0000
```

请记住，在二进制表示系统中，位从右到左排序，从 0 开始。虽然BigInteger.ZERO有一个空的幅度数组，但设置第 63 位使其同时成为最重要的 - 第零个元素64 长度数组。signum 自动设置为 1。

另一方面，相同的位序列由Long.MIN_VALUE表示。让我们将此常量转换为byte[]数组并创建构造BigInteger：

```java
byte[] bytes = ByteBuffer.allocate(Long.BYTES).putLong(Long.MIN_VALUE).array();
BigInteger bi2 = new BigInteger(1, bytes);
assertEquals(bi1, bi2);
...
```

如我们所见，两个值相等，因此应用相同的断言包。

最后，我们可以检查内部int[] mag变量。目前，Java 不提供 API 来获取这个值，但我们可以通过调试器中的评估工具来实现：

[![贝尔 4920 1](https://www.baeldung.com/wp-content/uploads/2021/07/bael_4920_1.png)](https://www.baeldung.com/wp-content/uploads/2021/07/bael_4920_1.png)

我们使用两个整数将我们的值存储在数组中，两个 32 位包。第零个元素等于Integer.MIN_VALUE，另一个为零。

## 4。总结

在本快速教程中，我们重点介绍了BigInteger类的实现细节。我们首先提醒一些有关数字、基元和二进制表示规则的信息。

然后我们检查了 BigInteger 的源代码。我们检查了signum和mag属性。我们还了解了BigInteger如何存储给定值，从而允许我们提供比可用原始数据类型更大的数字。
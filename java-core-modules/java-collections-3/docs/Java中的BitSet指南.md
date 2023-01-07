## 1. 概述

在本教程中，我们将了解如何使用 [BitSet](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/BitSet.html)来表示位向量[。](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/BitSet.html)

首先，我们将从不使用boolean[]背后的基本原理开始 。然后在熟悉了 BitSet 的内部结构之后，我们将仔细研究它的 API。

## 2. 位数组

要存储和操作位数组，有人可能会争辩说我们应该使用 boolean[] 作为我们的数据结构。乍一看，这似乎是一个合理的建议。

但是， boolean[] 中的每个boolean 成员 通常[占用一个字节而不是一个位](https://www.baeldung.com/jvm-boolean-memory-layout)。因此，当我们对内存有严格要求时，或者我们只是想减少内存占用时，boolean[] 远非理想。

为了使事情更具体，让我们看看具有 1024 个元素的boolean[] 占用了多少空间 ：

```java
boolean[] bits = new boolean[1024];
System.out.println(ClassLayout.parseInstance(bits).toPrintable());
```

理想情况下，我们期望此数组占用 1024 位内存。然而，[Java 对象布局 (JOL)](https://search.maven.org/artifact/org.openjdk.jol/jol-core)揭示了一个完全不同的现实：

```plaintext
[Z object internals:
 OFFSET  SIZE      TYPE DESCRIPTION            VALUE
      0     4           (object header)        01 00 00 00 (00000001 00000000 00000000 00000000) (1)
      4     4           (object header)        00 00 00 00 (00000000 00000000 00000000 00000000) (0)
      8     4           (object header)        7b 12 07 00 (01111011 00010010 00000111 00000000) (463483)
     12     4           (object header)        00 04 00 00 (00000000 00000100 00000000 00000000) (1024)
     16  1024   boolean [Z.                    N/A
Instance size: 1040 bytes
```

如果我们忽略对象头的开销，数组元素将消耗 1024 字节，而不是预期的 1024 位。这比我们预期的多 700% 的内存。

可 [寻址问题和字撕裂是](https://www.baeldung.com/jvm-boolean-memory-layout#2-word-tearing)布尔值不仅仅是一位的主要原因。

为了解决这个问题，我们可以结合使用数字数据类型(例如long)和按位运算。这就是BitSet 的用武之地。

## 3. BitSet 的 工作原理

正如我们之前提到的，为了实现每个标志一位的内存使用， BitSet API 使用了基本数字数据类型和按位操作的组合。

为了简单起见，假设我们要用一个字节表示八个标志。首先，我们用零初始化这个单字节的所有位：

[![初始字节](https://www.baeldung.com/wp-content/uploads/2020/07/initial-bytes.png)](https://www.baeldung.com/wp-content/uploads/2020/07/initial-bytes.png)

现在，如果我们想将位置 3 的位设置为 true，我们应该首先将数字 1 左移三位：

[![左移](https://www.baeldung.com/wp-content/uploads/2020/07/left-shift.png)](https://www.baeldung.com/wp-content/uploads/2020/07/left-shift.png)

然后 或其 结果与当前字节值：

[![最后或](https://www.baeldung.com/wp-content/uploads/2020/07/final-or.png)](https://www.baeldung.com/wp-content/uploads/2020/07/final-or.png)

如果决定将该位设置为索引 7，则会发生相同的过程：

[![另一套](https://www.baeldung.com/wp-content/uploads/2020/07/another-set.png)](https://www.baeldung.com/wp-content/uploads/2020/07/another-set.png)

如上所示，我们执行左移七位，并使用or 运算符将结果与前一个字节值 组合。

### 3.1. 获取位索引

要检查特定位索引是否设置为 真 ，我们将使用 and 运算符。例如，下面是我们检查索引三是否已设置的方法：

1.  将值 1 左移三位
2.  将 结果与当前字节值相加
3.  如果结果大于零，那么我们找到了匹配项，并且该位索引实际上已设置。否则，请求的索引为 clear 或等于 false

[![准备好](https://www.baeldung.com/wp-content/uploads/2020/07/get-set.png)](https://www.baeldung.com/wp-content/uploads/2020/07/get-set.png)

上图展示了索引三的get操作步骤。但是，如果我们查询一个明确的索引，结果会有所不同：

[![弄清楚](https://www.baeldung.com/wp-content/uploads/2020/07/get-clear.png)](https://www.baeldung.com/wp-content/uploads/2020/07/get-clear.png)

由于 and 结果等于零，索引四是明确的。

### 3.2. 增加存储空间

目前，我们只能存储一个 8 位的向量。要超越这个限制，我们只需要使用byte数组 ，而不是单个 byte，就是这样！

现在，每次我们需要设置、获取或清除特定索引时，我们应该首先找到相应的数组元素。例如，假设我们要设置索引 14：

[![数组集](https://www.baeldung.com/wp-content/uploads/2020/07/array-set-1.png)](https://www.baeldung.com/wp-content/uploads/2020/07/array-set-1.png)

如上图所示，找到正确的数组元素后，我们确实设置了合适的索引。

另外，如果我们想在这里设置一个超过 15 的索引， BitSet 将首先扩展它的内部数组。只有在扩展数组并元素后，它才会设置请求的位。这有点类似于[ArrayList](https://www.baeldung.com/java-arraylist-linkedlist) 内部的工作方式。

到目前为止， 为了简单起见，我们使用了字节 数据类型。但是， BitSet API 在内部使用长 值数组 。

## 4. BitSet API

现在我们已经对理论有了足够的了解，是时候看看 BitSet API 长什么样了。

对于初学者，让我们将1024 位 的BitSet 实例的内存占用 与我们之前看到的boolean[] 进行比较：

```java
BitSet bitSet = new BitSet(1024);

System.out.println(GraphLayout.parseInstance(bitSet).toPrintable());
```

这将打印 BitSet 实例的浅尺寸及其内部数组的尺寸：

```plaintext
java.util.BitSet@75412c2fd object externals:
          ADDRESS       SIZE TYPE             PATH         VALUE
        70f97d208         24 java.util.BitSet              (object)
        70f97d220        144 [J               .words       [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
```

如上所示，它 内部使用了一个有 16 个元素(16  64 位 = 1024 位)的long[] 。无论如何，这个实例总共使用了 168 个字节，而 boolean[] 使用了 1024 个字节。

我们拥有的位数越多，足迹差异就越大。例如，要存储 1024  1024 位，boolean[] 消耗 1 MB，而 BitSet 实例消耗大约 130 KB。

### 4.1. 构造BitSet _

创建 BitSet 实例的最简单方法是使用[无参数构造函数](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/BitSet.html#())：

```java
BitSet bitSet = new BitSet();
```

这将创建一个 长度为 1 的long[] 的BitSet 实例 。当然，如果需要，它可以自动增长这个数组。

也可以创建一个 具有[初始位数的](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/BitSet.html#(int))BitSet ：

```java
BitSet bitSet = new BitSet(100_000);
```

在这里，内部数组将有足够的元素来容纳 100,000 位。当我们已经对要存储的位数有了合理的估计时，这个构造函数就派上用场了。在此类用例中，它可以防止或减少在增长数组元素时对数组元素进行不必要的。

甚至可以从现有的 long[]、 byte[]、 [LongBuffer](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/LongBuffer.html)和 [ByteBuffer](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/ByteBuffer.html)创建 BitSet 。例如，这里我们 从给定的 long[]创建一个BitSet 实例：

```java
BitSet bitSet = BitSet.valueOf(new long[] { 42, 12 });
```

[valueOf()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/BitSet.html#valueOf(byte[])) 静态工厂方法还有另外三个重载版本 来支持其他提到的类型。

### 4.2. 设置位

我们可以使用 [set(index)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/BitSet.html#set(int))方法将特定索引的值设置为 true ： 

```java
BitSet bitSet = new BitSet();

bitSet.set(10);
assertThat(bitSet.get(10)).isTrue();
```

像往常一样，指数是从零开始的。甚至可以 使用 [set(fromInclusive, toExclusive)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/BitSet.html#set(int,int))方法将一系列位设置为真 ：

```java
bitSet.set(20, 30);
for (int i = 20; i <= 29; i++) {
    assertThat(bitSet.get(i)).isTrue();
}
assertThat(bitSet.get(30)).isFalse();
```

从方法签名可以明显看出，开始索引是包含性的，结束索引是排他性的。

当我们说设置索引时，通常是指将其设置为 true。尽管有这个术语，我们可以使用 [set(index, boolean)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/BitSet.html#set(int,boolean))方法将特定位索引设置为 false ： 

```java
bitSet.set(10, false);
assertThat(bitSet.get(10)).isFalse();
```

此版本还支持设置值范围：

```java
bitSet.set(20, 30, false);
for (int i = 20; i <= 29; i++) {
    assertThat(bitSet.get(i)).isFalse();
}
```

### 4.3. 清除位

我们可以简单地使用 [clear(index)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/BitSet.html#clear(int,int))方法清除它，而不是将特定位索引设置为 false ： 

```java
bitSet.set(42);
assertThat(bitSet.get(42)).isTrue();
        
bitSet.clear(42);
assertThat(bitSet.get(42)).isFalse();
```

[此外，我们还可以使用clear(fromInclusive, toExclusive)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/BitSet.html#clear(int,int)) 重载版本清除一系列位 ：

```java
bitSet.set(10, 20);
for (int i = 10; i < 20; i++) {
    assertThat(bitSet.get(i)).isTrue();
}

bitSet.clear(10, 20);
for (int i = 10; i < 20; i++) {
    assertThat(bitSet.get(i)).isFalse();
}
```

有趣的是，如果我们在不传递任何参数的情况下调用此方法，它将清除所有设置位：

```java
bitSet.set(10, 20);
bitSet.clear();
for (int i = 0; i < 100; i++) { 
    assertThat(bitSet.get(i)).isFalse();
}
```

如上所示，调用 [clear()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/BitSet.html#clear()) 方法后，所有位都设置为零。

### 4.4. 获取位

到目前为止，我们 相当广泛地使用了[get(index)方法。](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/BitSet.html#get(int)) 设置请求的位索引后，此方法将返回 true。否则，它将返回 false：

```java
bitSet.set(42);

assertThat(bitSet.get(42)).isTrue();
assertThat(bitSet.get(43)).isFalse();
```

与 set 和 clear类似，我们可以使用[get(fromInclusive, toExclusive)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/BitSet.html#get(int,int)) 方法获取一系列位索引 ：

```java
bitSet.set(10, 20);
BitSet newBitSet = bitSet.get(10, 20);
for (int i = 0; i < 10; i++) {
    assertThat(newBitSet.get(i)).isTrue();
}
```

如上所示，该方法返回当前 位集的 [20, 30) 范围内的另一个位位集。也就是说，bitSet 变量的索引 20相当于 newBitSet 变量的索引 0 。

### 4.5. 翻转位

要取反当前位索引值，我们可以使用 [flip(index)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/BitSet.html#flip(int)) 方法。也就是说，它将把 真 值变成 假 值，反之亦然：

```java
bitSet.set(42);
bitSet.flip(42);
assertThat(bitSet.get(42)).isFalse();

bitSet.flip(12);
assertThat(bitSet.get(12)).isTrue();
```

[类似地，我们可以使用flip(fromInclusive, toExclusive)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/BitSet.html#flip(int,int)) 方法对一系列值实现相同的 效果：

```java
bitSet.flip(30, 40);
for (int i = 30; i < 40; i++) {
    assertThat(bitSet.get(i)).isTrue();
}
```

### 4.6. 长度

BitSet有三种类似长度的方法 。[size()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/BitSet.html#size())方法返回内部数组可以表示的 位数 。例如，由于无参数构造函数分配了一个 只有一个元素的long[] 数组，因此 size() 将为其返回 64：

```java
BitSet defaultBitSet = new BitSet();
assertThat(defaultBitSet.size()).isEqualTo(64);
```

一个 64 位的数，我们只能表示 64 位。当然，如果我们明确传递位数，这将会改变：

```java
BitSet bitSet = new BitSet(1024);
assertThat(bitSet.size()).isEqualTo(1024);
```

此外， [cardinality()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/BitSet.html#cardinality()) 方法表示 BitSet中设置位的数量：

```java
assertThat(bitSet.cardinality()).isEqualTo(0);
bitSet.set(10, 30);
assertThat(bitSet.cardinality()).isEqualTo(30 - 10);
```

起初，此方法返回零，因为所有位都是 false。将 [10, 30) 范围设置为 true后， cardinality() 方法调用返回 20。

此外， [length()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/BitSet.html#length()) 方法返回最后一个设置位的索引之后的一个索引：

```java
assertThat(bitSet.length()).isEqualTo(30);
bitSet.set(100);
assertThat(bitSet.length()).isEqualTo(101);
```

起初，最后设置的索引是 29，所以此方法返回 30。当我们将索引 100 设置为 true 时， length() 方法返回 101。另外值得一提的是，如果所有位都清除，此方法将返回零。

最后， 当BitSet中至少有一个设置位时 ， [isEmpty()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/BitSet.html#isEmpty()) 方法返回 false 。否则，它将返回 true：

```java
assertThat(bitSet.isEmpty()).isFalse();
bitSet.clear();
assertThat(bitSet.isEmpty()).isTrue();
```

### 4.7. 与其他 BitSet组合

[intersects(BitSet)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/BitSet.html#intersects(java.util.BitSet)) 方法采用 另一个 BitSet 并在两个 BitSet有共同点时返回 true 。也就是说，它们在同一索引中至少有一个设置位：

```java
BitSet first = new BitSet();
first.set(5, 10);

BitSet second = new BitSet();
second.set(7, 15);

assertThat(first.intersects(second)).isTrue();
```

[7, 9] 范围在两个 BitSet中都设置了，因此此方法返回 true。

也可以 对两个BitSet执行逻辑与 操作 ：

```java
first.and(second);
assertThat(first.get(7)).isTrue();
assertThat(first.get(8)).isTrue();
assertThat(first.get(9)).isTrue();
assertThat(first.get(10)).isFalse();
```

这将在两个BitSet之间执行逻辑 与 ，并用结果修改第 一个 变量。同样，我们也可以对两个 BitSet执行逻辑 异 或：

```java
first.clear();
first.set(5, 10);

first.xor(second);
for (int i = 5; i < 7; i++) {
    assertThat(first.get(i)).isTrue();
}
for (int i = 10; i < 15; i++) {
    assertThat(first.get(i)).isTrue();
}
```

还有其他方法，例如 [andNot(BitSet)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/BitSet.html#andNot(java.util.BitSet))或 [or(BitSet)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/BitSet.html#or(java.util.BitSet))，它们可以对两个BitSet 执行其他逻辑操作 。

### 4.8. 各种各样的

从Java8 开始，有一个 [stream()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/BitSet.html#stream()) 方法可以流式传输BitSet的所有设置位 。例如：

```java
BitSet bitSet = new BitSet();
bitSet.set(15, 25);

bitSet.stream().forEach(System.out::println);
```

这会将所有设置位打印到控制台。由于这将返回一个 [IntStream](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/IntStream.html)，我们可以执行常见的数值运算，例如求和、平均、计数等。例如，这里我们计算设置位的数量：

```java
assertThat(bitSet.stream().count()).isEqualTo(10);
```

此外，nextSetBit [(fromIndex)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/BitSet.html#nextSetBit(int)) 方法将返回从 fromIndex开始的下一个设置位索引：

```java
assertThat(bitSet.nextSetBit(13)).isEqualTo(15);
```

fromIndex 本身包含在此计算中。 当BitSet中没有任何真 位时 ，它将返回 -1：

```java
assertThat(bitSet.nextSetBit(25)).isEqualTo(-1);
```

同样，nextClearBit [(fromIndex)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/BitSet.html#nextClearBit(int)) 返回从 fromIndex开始的下一个清除索引：

```java
assertThat(bitSet.nextClearBit(23)).isEqualTo(25);
```

另一方面， [previousClearBit(fromIndex)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/BitSet.html#previousClearBit(int)) 返回相反方向最近的清除索引的索引：

```java
assertThat(bitSet.previousClearBit(24)).isEqualTo(14);
```

[previousSetBit(fromIndex)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/BitSet.html#previousSetBit(int))也是如此：[ ](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/BitSet.html#previousSetBit(int))

```java
assertThat(bitSet.previousSetBit(29)).isEqualTo(24);
assertThat(bitSet.previousSetBit(14)).isEqualTo(-1);
```

此外，我们可以 分别使用 [toByteArray()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/BitSet.html#toByteArray())或 [toLongArray()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/BitSet.html#toLongArray())方法将BitSet 转换为 byte[] 或 long[] ： 

```java
byte[] bytes = bitSet.toByteArray();
long[] longs = bitSet.toLongArray();
```

## 5.总结

在本教程中，我们了解了如何使用 BitSet来表示位向量。

起初，我们熟悉了不使用 boolean[]来表示位向量背后的基本原理。然后我们看到了 BitSet 的内部工作原理以及它的 API 是什么样子的。
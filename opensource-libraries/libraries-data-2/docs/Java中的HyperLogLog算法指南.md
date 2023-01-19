## 1. 概述

HyperLogLog (HLL)数据结构是一种概率数据结构，用于估计数据集的基数。

假设我们有数百万用户，我们想要计算网页的不同访问次数。一个简单的实现是将每个唯一的用户 ID 存储在一个集合中，然后集合的大小就是我们的基数。

当我们处理非常大量的数据时，这样计算基数会非常低效，因为数据集会占用大量内存。

但是，如果我们可以接受百分之几以内的估计并且不需要唯一访问的确切数量，那么我们可以使用HLL，因为它正是为这样的用例而设计的——估计数百万甚至数十亿的数量不同的价值观。

## 2.Maven依赖

首先，我们需要为[hll](https://search.maven.org/classic/#search|gav|1|g%3A"net.agkn" AND a%3A"hll")库添加 Maven 依赖项：

```xml
<dependency>
    <groupId>net.agkn</groupId>
    <artifactId>hll</artifactId>
    <version>1.6.0</version>
</dependency>
```

## 3. 使用HLL估计基数

直接进入——HLL构造函数有两个参数，我们可以根据需要进行调整：

-   log2m(log base 2)——这是HLL内部使用的寄存器数量(注意：我们指定的是m)
-   regwidth——这是每个寄存器使用的位数

如果我们想要更高的精度，我们需要将它们设置为更高的值。这样的配置会有额外的开销，因为我们的HLL会占用更多的内存。如果我们可以接受较低的精度，我们可以降低这些参数，我们的HLL将占用更少的内存。

让我们创建一个HLL来计算具有 1 亿个条目的数据集的不同值。我们将把log2m参数设置为14，将 regwidth设置为5——对于这种大小的数据集来说是合理的值。

当每个新元素被插入到HLL中时，需要预先对其进行哈希处理。我们将使用来自 Guava 库(包含在hll依赖项中)的 Hashing.murmur3_128 ( )，因为它既准确又快速。

```java
HashFunction hashFunction = Hashing.murmur3_128();
long numberOfElements = 100_000_000;
long toleratedDifference = 1_000_000;
HLL hll = new HLL(14, 5);
```

选择这些参数应该使我们的错误率低于百分之一(1,000,000 个元素)。我们稍后会对此进行测试。

接下来，让我们插入 1 亿个元素：

```java
LongStream.range(0, numberOfElements).forEach(element -> {
    long hashedValue = hashFunction.newHasher().putLong(element).hash().asLong();
    hll.addRaw(hashedValue);
  }
);
```

最后，我们可以测试HLL返回的基数是否在我们期望的错误阈值内：

```java
long cardinality = hll.cardinality();
assertThat(cardinality)
  .isCloseTo(numberOfElements, Offset.offset(toleratedDifference));
```

## 4. HLL的内存大小

我们可以使用以下公式计算上一节中的HLL将占用多少内存： numberOfBits = 2 ^ log2m  regwidth。

在我们的示例中，它将是2 ^ 14  5位(大约81000位或8100字节)。因此使用HLL估计 1 亿成员集的基数只占用 8100 字节的内存。

让我们将其与朴素的集合实现进行比较。在这样的实现中，我们需要有一组1 亿个Long值，这将占用100,000,000  8 bytes = 800,000,000 bytes 。

我们可以看到差异高得惊人。使用HLL，我们只需要 8100 字节，而使用简单的Set实现我们大约需要 800 兆字节。

当我们考虑更大的数据集时， HLL和朴素的Set实现之间的差异变得更大。

## 5. 两个HLL的联合

HLL在执行联合时有一个有益的特性。当我们采用从不同数据集创建的两个HLL的并集并测量其基数时，我们将得到与使用单个HLL并计算两个数据的所有元素的哈希值时得到的并集相同的错误阈值从头开始设置。

请注意，当我们合并两个 HLL 时，两者应该具有相同的log2m和regwidth参数以产生正确的结果。

让我们通过创建两个HLL来测试该属性——一个填充了 0 到 1 亿的值，第二个填充了 1 亿到 2 亿的值：

```java
HashFunction hashFunction = Hashing.murmur3_128();
long numberOfElements = 100_000_000;
long toleratedDifference = 1_000_000;
HLL firstHll = new HLL(15, 5);
HLL secondHLL = new HLL(15, 5);

LongStream.range(0, numberOfElements).forEach(element -> {
    long hashedValue = hashFunction.newHasher()
      .putLong(element)
      .hash()
      .asLong();
    firstHll.addRaw(hashedValue);
    }
);

LongStream.range(numberOfElements, numberOfElements  2).forEach(element -> {
    long hashedValue = hashFunction.newHasher()
      .putLong(element)
      .hash()
      .asLong();
    secondHLL.addRaw(hashedValue);
    }
);
```

请注意，我们调整了HLL的配置参数，将log2m参数从上一节中看到的 14 增加到本例中的 15，因为生成的HLL联合将包含两倍的元素。

接下来，让我们使用union()方法合并 firstHll和secondHll 。如所见，估计的基数在错误阈值内，就好像我们从一个具有 2 亿个元素的HLL中获取基数一样：

```java
firstHll.union(secondHLL);
long cardinality = firstHll.cardinality();
assertThat(cardinality)
  .isCloseTo(numberOfElements  2, Offset.offset(toleratedDifference  2));

```

## 六. 总结

在本教程中，我们了解了HyperLogLog算法。

我们看到了如何使用HLL来估计集合的基数。我们还看到，与原始解决方案相比， HLL非常节省空间。我们对两个HLL执行联合操作，并验证联合的行为方式与单个HLL相同。
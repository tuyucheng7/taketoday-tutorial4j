## 1. 概述

HashMap是一种功能强大的数据结构，具有广泛的应用，尤其是在需要快速查找时间时。然而，如果我们不注意细节，它可能会变得次优。

在本教程中，我们将了解如何尽可能快地制作[HashMap 。](https://www.baeldung.com/java-hashmap)

## 2. HashMap的瓶颈

HashMap的元素检索的乐观常数时间 ( O(1) ) 来自散列的强大功能。对于每个元素， HashMap计算哈希码并将该元素放入与该哈希码关联的桶中。因为不相等的对象可以具有相同的哈希码(一种称为哈希码冲突的现象)，所以桶的大小可以增加。

桶其实就是一个简单的链表。在链表中查找元素不是很快(O(n))，但如果列表很小，这不是问题。当我们有很多哈希码冲突时，问题就开始了，所以我们没有大量的小桶，而是有少量的大桶。

在最坏的情况下，我们将所有内容都放在一个桶中，我们的HashMap会降级为链表。因此，我们得到的不是O(1)的查找时间，而是非常不令人满意的O(n)。

## 3. 树而不是链表

从Java8 开始，HashMap内置了[一种优化](https://hg.openjdk.java.net/jdk8u/jdk8u/jdk/file/a006fa0a9e8f/src/share/classes/java/util/HashMap.java#l143)： 当桶变得太大时，它们会被转换为树，而不是链表。这使O(n)的悲观时间变为O(log(n))，这要好得多。为此，HashMap的键需要实现[Comparable](https://www.baeldung.com/java-comparator-comparable)接口。

这是一个很好的自动解决方案，但它并不完美。O(log(n)) 仍然比期望的恒定时间差，并且转换和存储树需要额外的功率和内存。

## 4.最佳哈希码实现

在选择哈希函数时，我们需要考虑两个因素：生成的哈希码的质量和速度。

### 4.1. 衡量哈希码 质量

哈希码存储在int变量中，因此可能的哈希数受限于int类型的容量。必须如此，因为散列用于计算带桶的数组的索引。这意味着我们可以将有限数量的键存储在HashMap中而不会发生哈希冲突。

为了尽可能避免冲突，我们希望尽可能均匀地分布散列。换句话说，我们要实现均匀分布。这意味着每个散列码值与其他任何散列码值都有相同的出现机会。

同样，一个糟糕的hashCode方法会有一个非常不平衡的分布。在最坏的情况下，它总是会返回相同的数字。

### 4.2. 默认对象的哈希码

一般来说，我们不应该使用默认的 Object 的 hashCode方法，因为我们[不想](https://www.baeldung.com/java-map-key-byte-array#4-meaningful-equality)在equals方法中使用对象标识。但是，在我们真正想为HashMap中的键使用对象标识的极不可能的情况下，默认的hashCode函数可以正常工作。否则，我们将需要一个自定义实现。

### 4.3. 自定义哈希码

通常，我们要[覆盖equals方法，然后我们还需要覆盖hashCode](https://www.baeldung.com/java-equals-hashcode-contracts)。有时，我们可以利用类的特定标识，轻松制作一个非常快速的[hashCode方法](https://www.baeldung.com/java-hashcode)。

假设我们的对象的身份完全基于它的整数id。然后，我们可以将这个id用作哈希函数：

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    MemberWithId that = (MemberWithId) o;

    return id.equals(that.id);
}

@Override
public int hashCode() {
    return id;
}
```

它将非常快并且不会产生任何碰撞。我们的HashMap的行为就像它有一个整数键而不是一个复杂的对象。

如果我们需要考虑的字段更多，情况将变得更加复杂。假设我们想要基于id和name的相等性：

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    MemberWithIdAndName that = (MemberWithIdAndName) o;

    if (!id.equals(that.id)) return false;
    return name != null ? name.equals(that.name) : that.name == null;
}
```

现在，我们需要以某种方式组合id和name的哈希值。

首先，我们将获得与之前相同的id的哈希值。然后，我们将它乘以一些精心选择的数字并添加name的哈希值：

```java
@Override
public int hashCode() {
    int result = id.hashCode();
    result = PRIME  result + (name != null ? name.hashCode() : 0);
    return result;
}
```

如何选择这个数字并不是一个容易回答的问题。从历史上看，最流行的数字是31。 它是素数，分布良好，很小，乘以它可以使用移位操作进行优化：

```java
31  i == (i << 5) - i
```

但是，既然我们不需要为每个 CPU 周期而战，就可以使用一些更大的素数。比如524287 也可以优化：

```java
524287  i == i << 19 - i
```

而且，它可以提供质量更好的散列，从而减少冲突的可能性。请注意，这些移位优化是由 JVM 自动完成的，因此我们不需要用它们来混淆我们的代码。

### 4.4. 对象实用程序类

我们刚刚实现的算法已经很好地建立起来了，我们通常不需要每次都手动重新创建它。相反，我们可以使用Objects类提供的辅助方法：

```java
@Override
public int hashCode() {
    return Objects.hash(id, name);
}
```

在底层，它完全使用前面描述的以数字31作为乘数的算法。

### 4.5. 其他哈希函数

有许多散列函数提供比前面描述的更小的冲突机会。问题是它们的计算量更大，因此无法提供我们寻求的速度增益。

如果出于某种原因我们真的需要质量并且不太关心速度，我们可以看看[Guava](https://www.baeldung.com/guava-guide)库中的Hashing类：

```java
@Override
public int hashCode() {
    HashFunction hashFunction = Hashing.murmur3_32();
    return hashFunction.newHasher()
      .putInt(id)
      .putString(name, Charsets.UTF_8)
      .hash().hashCode();
}
```

选择 32 位函数很重要，因为无论如何我们都无法存储更长的哈希值。

## 5.总结

现代Java的HashMap 是一种功能强大且优化良好的数据结构。然而，它的性能可能会因设计不当的hashCode方法而恶化。在本教程中，我们研究了使散列快速有效的可能方法。
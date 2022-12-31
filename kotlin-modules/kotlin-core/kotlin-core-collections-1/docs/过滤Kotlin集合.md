## 1. 概述

Kotlin集合是强大的数据结构，具有许多超越Java集合的有益方法。

我们将详细介绍一些可用的过滤方法，以便能够利用我们未在本文中明确介绍的所有其他方法。

**所有这些方法都返回一个新集合，而原始集合保持不变**。

我们将使用lambda表达式来执行一些过滤器，要阅读有关lambda的更多信息，请在此处查看我们的Kotlin Lambda文章。

## 2. 移除

我们将从修剪集合的基本方法开始，删除允许我们获取集合的一部分并返回一个新列表，该列表缺少数字中列出的元素数量：

```kotlin
@Test
fun whenDroppingFirstTwoItemsOfArray_thenTwoLess() {
    val array = arrayOf(1, 2, 3, 4)
    val result = array.drop(2)
    val expected = listOf(3, 4)

    assertIterableEquals(expected, result)
}
```

另一方面，**如果我们想删除最后的n个元素，我们调用dropLast**：

```kotlin
@Test
fun givenArray_whenDroppingLastElement_thenReturnListWithoutLastElement() {
    val array = arrayOf("1", "2", "3", "4")
    val result = array.dropLast(1)
    val expected = listOf("1", "2", "3")

    assertIterableEquals(expected, result)
}
```

现在我们将查看包含谓词的第一个过滤条件。

此函数将获取我们的代码并在列表中向后工作，直到我们到达不满足条件的元素：

```kotlin
@Test
fun whenDroppingLastUntilPredicateIsFalse_thenReturnSubsetListOfFloats() {
    val array = arrayOf(1f, 1f, 1f, 1f, 1f, 2f, 1f, 1f, 1f)
    val result = array.dropLastWhile { it == 1f }
    val expected = listOf(1f, 1f, 1f, 1f, 1f, 2f)

    assertIterableEquals(expected, result)
}
```

dropLastWhile从列表中删除最后三个1f，因为该方法循环遍历每个项目，直到数组元素不等于1f的第一个实例。

**一旦元素不满足谓词的条件，该方法就会停止删除元素**。

dropWhile是另一个采用谓词的过滤器，但dropWhile从索引0 -> n开始工作，而dropLastWhile从索引n -> 0开始工作。

**如果我们尝试删除比集合包含的元素更多的元素，我们只会留下一个空列表**。

## 3. Take

与drop非常相似，take将使元素保持在给定索引或谓词之前：

```kotlin
@Test
fun `when predicating on 'is String', then produce list of array up until predicate is false`() {
    val originalArray = arrayOf("val1", 2, "val3", 4, "val5", 6)
    val actualList = originalArray.takeWhile { it is String }
    val expectedList = listOf("val1")

    assertIterableEquals(expectedList, actualList)
}
```

**drop和take的区别在于drop删除项目，而take保留项目**。

尝试获取比集合中可用的更多的项目-只会返回一个与原始集合大小相同的列表

这里需要注意的是takeIf不是一个集合方法，takeIf使用谓词来确定是否返回空值(想想Optional#filter)。

尽管将所有与谓词匹配的项目放入返回的List中似乎符合函数名称模式，但我们使用过滤器来执行该操作。

## 4. 过滤

过滤器根据提供的谓词创建一个新列表：

```kotlin
@Test
fun givenAscendingValueMap_whenFilteringOnValue_ThenReturnSubsetOfMap() {
    val originalMap = mapOf("key1" to 1, "key2" to 2, "key3" to 3)
    val filteredMap = originalMap.filter { it.value < 2 }
    val expectedMap = mapOf("key1" to 1)

    assertTrue { expectedMap == filteredMap }
}
```

过滤时，我们有一个函数可以让我们累积不同数组的过滤器结果，它称为filterTo，并将可变列表副本到给定的可变数组。

**这允许我们获取多个集合并将它们过滤成一个单一的、累积的集合**。

这个例子需要；数组、序列和列表。

然后它将相同的谓词应用于所有三个以过滤每个集合中包含的素数：

```kotlin
@Test
fun whenFilteringToAccumulativeList_thenListContainsAllContents() {
    val array1 = arrayOf(90, 92, 93, 94, 92, 95, 93)
    val array2 = sequenceOf(51, 31, 83, 674_506_111, 256_203_161, 15_485_863)
    val list1 = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
    val primes = mutableListOf<Int>()
    
    val expected = listOf(2, 3, 5, 7, 31, 83, 15_485_863, 256_203_161, 674_506_111)

    val primeCheck = { num: Int -> Primes.isPrime(num) }

    array1.filterTo(primes, primeCheck)
    list1.filterTo(primes, primeCheck)
    array2.filterTo(primes, primeCheck)

    primes.sort()

    assertIterableEquals(expected, primes)
}
```

带或不带谓词的过滤器也适用于Maps：

```kotlin
val originalMap = mapOf("key1" to 1, "key2" to 2, "key3" to 3)
val filteredMap = originalMap.filter { it.value < 2 }
```

一对非常有用的过滤方法是filterNotNull和filterNotNullTo，**它们只会过滤掉所有空元素**。

最后，如果我们需要使用集合项的索引，**filterIndexed和filterIndexed提供了对元素及其位置索引使用谓词lambda的能力**。

## 5. 切片

我们也可以使用范围来执行切片，要执行切片，我们只需定义切片要提取的范围：

```kotlin
@Test
fun whenSlicingAnArrayWithDotRange_ThenListEqualsTheSlice() {
    val original = arrayOf(1, 2, 3, 2, 1)
    val actual = original.slice(3 downTo 1)
    val expected = listOf(2, 3, 2)

    assertIterableEquals(expected, actual)
}
```

切片可以向上或向下。

**使用Ranges时，我们还可以设置范围步长**。

使用没有步骤的范围和超出集合边界的切片，我们将在我们的结果列表中创建许多空对象。

但是，使用**Range with steps**超出集合的边界可能会触发ArrayIndexOutOfBoundsException：

```kotlin
@Test
fun whenSlicingBeyondRangeOfArrayWithStep_thenOutOfBoundsException() {
    assertThrows(ArrayIndexOutOfBoundsException::class.java) {
        val original = arrayOf(12, 3, 34, 4)
        original.slice(3..8 step 2)
    }
}
```

## 6. 去重

我们将在本文中看到的另一个过滤器是distinct，**我们可以使用此方法从我们的列表中收集唯一对象**：

```kotlin
@Test
fun whenApplyingDistinct_thenReturnListOfNoDuplicateValues() {
    val array = arrayOf(1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 5, 6, 7, 8, 9)
    val result = array.distinct()
    val expected = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)

    assertIterableEquals(expected, result)
}
```

我们还可以选择使用选择器功能，**选择器返回我们要评估唯一性的值**。

我们将实现一个名为SmallClass的小型数据类，以探索如何使用选择器中的对象：

```kotlin
data class SmallClass(val key: String, val num: Int)
```

使用SmallClass数组：

```kotlin
val original = arrayOf(
  SmallClass("key1", 1),
  SmallClass("key2", 2),
  SmallClass("key3", 3),
  SmallClass("key4", 3),
  SmallClass("er", 9),
  SmallClass("er", 10),
  SmallClass("er", 11))
```

我们可以在distinctBy中使用各种字段：

```kotlin
val actual = original.distinctBy { it.key }
val expected = listOf(
  SmallClass("key1", 1),
  SmallClass("key2", 2),
  SmallClass("key3", 3),
  SmallClass("key4", 3),
  SmallClass("er", 9))
```

函数不需要直接返回变量属性，**我们也可以通过计算来确定我们的不同值**。

例如，对于每10个范围(0 – 9、10 – 19、20 - 29等)的数字，我们可以向下舍入到最接近的10，这就是我们的选择器的值：

```kotlin
val actual = array.distinctBy { Math.floor(it.num / 10.0) }
```

## 7. 分块

Kotlin 1.2的一个有趣特性是chunked，分块采用单个Iterable集合并创建一个新的与定义大小匹配的块列表。**这不适用于数组；只有Iterables**。

我们可以简单地使用要提取的块的大小来分块：

```kotlin
@Test
fun givenDNAFragmentString_whenChunking_thenProduceListOfChunks() {
    val dnaFragment = "ATTCGCGGCCGCCAA"

    val fragments = dnaFragment.chunked(3)

    assertIterableEquals(listOf("ATT", "CGC", "GGC", "CGC", "CAA"), fragments)
}
```

或者size和transformer：

```kotlin
@Test
fun givenDNAString_whenChunkingWithTransformer_thenProduceTransformedList() {
    val codonTable = mapOf(
      "ATT" to "Isoleucine", 
      "CAA" to "Glutamine", 
      "CGC" to "Arginine", 
      "GGC" to "Glycine")
    val dnaFragment = "ATTCGCGGCCGCCAA"

    val proteins = dnaFragment.chunked(3) { codon ->
        codonTable[codon.toString()] ?: error("Unknown codon")
    }

    assertIterableEquals(listOf(
      "Isoleucine", "Arginine", 
      "Glycine", "Arginine", "Glutamine"), proteins)
}
```

上面的DNA片段选择器示例是从Kotlin文档中提取的，[此处](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/chunked.html)提供了分块。

**当传递chunked的大小不是我们集合大小的除数时，在这些情况下，我们的块列表中的最后一个元素将只是一个较小的列表**。

注意不要假设每个块都是完整大小而遇到ArrayIndexOutOfBoundsException！

## 8. 总结

所有Kotlin过滤器都允许我们应用lambda来确定是否应该过滤某个项目，**并非所有这些函数都可以在Maps上使用**，但是，所有适用于Maps的过滤器函数都适用于Arrays。

Kotlin集合文档为我们提供了有关是否可以仅对数组或两者使用过滤器函数的信息，文档可以在[这里](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/index.html)找到。
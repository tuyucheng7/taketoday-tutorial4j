## 1. 概述

在本教程中，我们将讨论在Kotlin中使用列表。

## 2. Kotlin中的列表

列表是可以包含重复值的一般有序元素集合，并且，**Kotlin中的List是一个扩展了Collection接口的接口**。

**该接口中的所有方法都支持对列表的只读访问**，让我们看看List接口是如何声明的：

```kotlin
public interface List<out E> : Collection<E>
```

**此外，Kotlin有一个MutableList接口来修改list的元素**，MutableList接口扩展了MutableCollection接口，该接口中的方法允许我们在列表中添加和删除元素。

现在让我们看看如何声明可变列表接口：

```kotlin
public interface MutableList<E> : List<E>, MutableCollection<E>
```

List和MutableList接口都有帮助我们在Kotlin中处理列表的方法。

## 3. 创建一个列表

我们可以使用listOf()方法在Kotlin中创建一个只读列表：

```kotlin
val countries = listOf("Germany", "India", "Japan", "Brazil", "Australia")
```

而且，我们可以使用mutableListOf()方法在Kotlin中创建一个可变列表：

```kotlin
val cities = mutableListOf("Berlin", "Calcutta", "Seoul", "Sao Paulo", "Sydney")
```

## 4. 遍历列表

我们可以通过迭代访问列表的元素，有几种方法可以做到这一点。

### 4.1 循环

我们可以使用各种类型的for循环来迭代Kotlin中的列表。

首先，for循环按元素遍历列表。对于每个循环，变量country指向列表中的下一个元素：

```kotlin
for (country in countries) {
    country.length
    // ...
}
```

还有一个for循环的替代方法，它使用列表的大小来遍历元素：

```kotlin
for (i in 0 until countries.size) {
    countries[i].length
    // ...
}
```

我们还有一些方法可以为我们进行迭代。

例如，forEach()方法将谓词函数作为参数并对列表中的每个元素执行操作：

```kotlin
countries.forEach { it ->
    it.length
    // ...
}
```

forEachIndexed()方法对每个元素执行一个操作，同时为该元素提供顺序索引。它以一个谓词函数作为参数，该函数包括元素的索引和元素本身：

```kotlin
countries.forEachIndexed { i, e ->
    e.length
    // ...
}
```

### 4.2 列表迭代器

我们可以使用listIterator()方法在向前和向后的方向上迭代列表。

迭代器也可用于使用listIterator(index: Int)方法从指定索引开始迭代列表。**此外，列表迭代器可以使用nextIndex()和previousIndex()方法提供有关元素索引的信息**：

```kotlin
fun iterateUsingListIterator() {
    val iterator = countries.listIterator()
    while (iterator.hasNext()) {
        val country = iterator.next()
        // ...
    }

    while (iterator.hasPrevious()) {
        val country = iterator.previousIndex();
        // ...
    }
}
```

### 4.3 可变列表迭代器

我们可以使用MutableCollection接口提供的iterator()方法。除了迭代列表之外，此迭代器还允许使用remove()方法删除元素：

```kotlin
fun iterateUsingIterator() {
    val iterator = cities.iterator()
    iterator.next()
    iterator.remove()
    // ...
}
```

我们还可以使用MutableList接口提供的listIterator()方法。**这个迭代器允许我们在遍历列表时添加、替换和删除元素**：

```kotlin
fun iterateUsingMutableListIterator() {
    val iterator = cities.listIterator(1)
    iterator.next()
    iterator.add("London")
    iterator.next()
    iterator.set("Milan")
    // ...
}
```

## 5. 检索列表中的元素

我们可以使用get()方法来检索列表中的指定元素。此外，我们还可以使用索引运算符[]来使用数组样式访问元素。

**索引运算符[]比get()方法噪音小并且更方便**：

```kotlin
val element = countries[2]
val element = countries.get(3)
```

**我们可以分别使用first()和last()方法检索列表的第一个和最后一个元素**。此外，这两种方法还可用于在集合中搜索与给定谓词匹配的元素。

对于没有元素可能与谓词匹配的情况，我们可以使用firstOrNull()和lastOrNull()方法来避免抛出异常。

让我们看一下以下示例以检索第一个和最后一个元素：

```kotlin
countries.first()
countries.last()
countries.first { it.length > 7 }
countries.last { it.startsWith("J") }
countries.firstOrNull { it.length > 8 }
```

## 6. 检索列表的一部分

我们可以使用subList()方法来检索列表的一部分，该方法的参数用于定义列表在fromIndex(包括)和toIndex(不包括)之间的指定范围。

**subList()方法返回原始列表的视图并将随之更改**。因此，原始列表中的任何结构更改都会使视图的行为未定义。

让我们看一个创建子列表的例子：

```kotlin
val subList = countries.subList(1, 4)

assertEquals(3, subList.size)
```

此外，Collection接口提供了另一种方法来检索列表的各个部分。

我们可以使用slice()方法根据索引检索列表的一部分，所有指数都包括在内。**与subList()不同，此方法将创建一个包含元素子集的新列表**。

让我们看看slice()方法是如何工作的：

```kotlin
val sliceListUsingIndices = countries.slice(1..4)

assertEquals(4, sliceListUsingIndices.size)
val sliceListUsingCollection = countries.slice(listOf(1, 4))

assertEquals(2, sliceListUsingCollection.size)
```

## 7. 计算列表中的元素

我们可以使用count()方法或size属性来查找列表中元素的数量，**count()方法还允许我们提供谓词函数作为参数**，之后，它返回与给定谓词匹配的元素数。

让我们看看如何计算列表中的元素：

```kotlin
val count = countries.count()
val count = countries.count { it.length > 5 }
val size = countries.size
```

## 8. 列表中的写操作

我们可以使用add()和addAll()方法将元素添加到可变列表中。

此外，我们可以通过提供元素插入位置作为附加参数来将元素添加到列表中的特定位置：

```kotlin
cities.add("Barcelona")
cities.add(3, "London")
cities.addAll(listOf("Singapore", "Moscow"))
cities.addAll(2, listOf("Prague", "Amsterdam"))
```

我们可以使用remove()和removeAll()方法从可变列表中删除元素。此外，我们还可以使用removeAt()方法从列表中的指定位置删除元素。

让我们看一个从列表中删除元素的示例：

```kotlin
cities.remove("Seoul")
cities.removeAt(1)
```

我们可以使用set()方法替换列表中指定位置的元素，此外，我们还可以使用索引运算符[]来替换元素。

fill()方法可用于将列表中的所有元素替换为指定值。

让我们尝试使用set()、[]和fill来修改我们的列表：

```kotlin
cities.set(3, "Prague")
cities[4] = "Moscow"
cities.fill("Barcelona")
```

## 9. 列表中的排序操作

我们可以使用sort()和sortDescending()方法分别对Kotlin中的列表进行升序和降序排序：

```kotlin
val sortCitiesAscending = cities.sort()
val sortCitiesDescending = cities.sortDescending()
```

**这些方法将使用元素的自然顺序并将就地排序。因此，该集合必须是一个可变列表**。

**如果我们想在排序后返回一个列表，我们可以使用sorted()和sortedDescending()方法**：

```kotlin
val sortedCountriesAscending = countries.sorted()
val sortedCountriesDescending = countries.sortedDescending()
```

**我们可以使用sortBy()和sortByDescending()方法根据给定对象的特定属性对列表进行排序**。该集合必须是一个可变列表，因为方法将使用元素的自然顺序并将就地排序。如果我们想在排序后返回一个列表，我们可以使用sortedBy()和sortedByDescending()方法。

**我们可以使用sortWith()和sortedWith()来使用Comparator对象作为参数对列表进行排序**。

要更深入地了解在Kotlin中使用列表时的排序操作，请参阅我们的[Kotlin排序指南](Kotlin排序指南.md)文章。

## 10. 检查列表中的元素

我们可以使用contains()方法或in运算符检查列表中的指定元素。**后者更流畅，在Kotlin中更受欢迎**。

我们可以使用containsAll()方法检查列表中的多个元素，**contains()和containsAll()方法都返回一个布尔值**。

让我们用contains()和containsAll()测试我们的列表：

```kotlin
assertTrue(countries.contains("Germany"))

assertFalse("Spain" in countries)

assertTrue(cities.containsAll(listOf("Calcutta", "Sao Paulo", "Sydney")))
```

## 11. 总结

在本教程中，我们看到了在Kotlin中使用列表的各种操作，List和MutableList接口都提供了几种方法来处理列表中的元素。
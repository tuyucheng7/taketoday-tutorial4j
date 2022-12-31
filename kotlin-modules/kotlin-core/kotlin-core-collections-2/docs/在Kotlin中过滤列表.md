## 1. 概述

在本教程中，我们将讨论过滤[List](https://www.baeldung.com/kotlin/lists)的不同方法。

## 2. 过滤列表

标准Kotlin库提供了许多有用的函数来过滤List，**这些函数返回一个新的List并且可用于只读和可变List**。

**我们使用谓词来定义过滤条件，谓词是简单的**[lambda表达式](https://www.baeldung.com/kotlin/lambda-expressions)，**这些表达式在提供的元素与谓词匹配时返回true，在不匹配时返回false**。

### 2.1 filter()和filterTo()

**我们可以使用filter()函数返回匹配给定谓词的元素列表**：

```kotlin
val countries = listOf("Germany", "India", "Japan", "Brazil", "Australia")
val filterList = countries.filter { it.length > 5 }

assertEquals(3, list.size)
assertTrue(list.containsAll(listOf("Germany","Brazil","Australia")))
```

在此示例中，我们使用listOf()函数创建一个只读列表，然后我们根据大于5的元素的长度过滤列表。

**随后，我们可以使用filterTo()函数将与给定谓词匹配的元素列表附加到目标列表**：

```kotlin
var list = mutableListOf("United States", "Canada")
countries.filterTo(list, { it.length > 5 })

assertEquals(5, list.size)
assertTrue(list.containsAll(listOf("United States","Canada","Germany","Brazil","Australia")))
```

在这里，我们使用mutableListOf()函数创建了一个MutableList，然后我们将此列表作为目标列表传递给filterTo()函数调用的第一个参数。

### 2.2 filterNot()和filterNotTo()

**要返回不匹配给定谓词的元素列表，我们可以使用filterNot()函数**：

```kotlin
val filterList = countries.filterNot { it.length > 5 }

assertEquals(2, filterList.size)
assertTrue(list.containsAll(listOf("India","Japan")))
```

在上面的示例中，filterNot()函数返回谓词产生假的国家列表中的两个元素。

**同样，我们可以使用filterNotTo()函数将不匹配给定谓词的元素列表附加到目标列表**：

```kotlin
var list = mutableListOf("United States", "Canada")
countries.filterNotTo(list, { it.length > 5 })

assertEquals(4, list.size)
assertTrue(list.containsAll(listOf("United States","Canada","India","Japan")))
```

在这种情况下，filterNotTo()函数将与谓词不匹配的两个元素附加到mutableList。

### 2.3 filterIndexed()和 filterIndexedTo()

**我们可以使用filterIndexed()函数来利用过滤器中的元素位置**，此函数的谓词使用索引和元素作为参数，然后它返回与给定谓词匹配的元素列表：

```kotlin
val filterList = countries.filterIndexed { index, it -> index != 3 && it.length > 5 }

assertEquals(2, filterList.size)
assertTrue(list.containsAll(listOf("Germany","Australia")))
```

在此示例中，filterIndexed()函数删除索引位置为3的元素以及长度小于5的元素。

**同样，filterIndexedTo()函数使用索引和元素作为谓词的参数**，然后它将匹配给定谓词的元素列表附加到目标列表：

```kotlin
var list = mutableListOf("United States", "Canada")
return countries.filterIndexedTo(list, { index, it -> index != 3 && it.length > 5 })

assertEquals(4, list.size)
assertTrue(list.containsAll(listOf("United States","Canada","Germany","Australia")))
```

在此示例中，filterIndexedTo()函数将与谓词匹配的结果两个元素附加到mutableList。

### 2.4 filterIsInstance()和 filterIsInstanceTo()

**filterIsInstance()函数可用于返回指定类型的元素列表**：

```kotlin
val countryCode = listOf("Germany", 49, null, "India", 91, "Japan", 81, "Brazil", null, "Australia", 61)
val filterList = countryCode.filterIsInstance<Int>()

assertEquals(4, filterList.size)
assertTrue(list.containsAll(listOf(49,91,81,61)))
```

在这种情况下，我们使用filterIsInstance()函数返回一个整数类型的元素列表。

**随后，我们可以使用filterIsInstanceTo()函数将指定类型的元素列表附加到目标列表**：

```kotlin
val countryCode = listOf("Germany", 49, null, "India", 91, "Japan", 81, "Brazil", null, "Australia", 61)
var list = mutableListOf(1, 24)
countryCode.filterIsInstanceTo(list)

assertEquals(6, list.size)
assertTrue(list.containsAll(listOf(1,24,49,91,81,61)))
```

在此示例中，我们将filterIsInstanceTo()函数返回的Integer类型的元素列表附加到mutableList。

### 2.5 filterNotNull()和filterNotNullTo()

**要返回一个非空元素列表，我们可以使用filterNotNull()函数**：

```kotlin
val countries = listOf("Germany", "India", null, "Japan", "Brazil", null, "Australia")
val filterList = countries.filterNotNull()

assertEquals(5, filterList.size)
assertTrue(list.containsAll(listOf("Germany","India","Japan","Brazil","Australia")))
```

在上述情况下，结果列表仅包含由filterNotNull()函数返回的非空元素。

**类似地，我们可以使用filterNotNullTo()函数将非空元素列表附加到目标列表**：

```kotlin
val countries = listOf("Germany", "India", null, "Japan", "Brazil", null, "Australia")
var list = mutableListOf("United States", "Canada") 
countries.filterNotNullTo(list)

assertEquals(7, list.size)
assertTrue(list.containsAll(listOf("United States","Canada","Germany","India","Japan","Brazil","Australia")))
```

在此示例中，国家列表中的非空元素附加到mutableList中。

## 3. 就地过滤列表

**就地过滤List意味着修改原始列表而不重新创建新的List。为此，项目应该是**[MutableList](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/)**类型**。

### 3.1 remove()

一种通过使用迭代器遍历列表来就地过滤可变列表的方法，**然后我们可以使用Iterator的remove()函数删除元素**：

```kotlin
val countries = mutableListOf("Germany", "India", "Japan", "Brazil", "Australia")
val iterator = countries.iterator()
while (iterator.hasNext())
{
    val current = iterator.next()
    if (current.length > 5) {
        iterator.remove()
    }
}

assertEquals(2, countries.size)
assertTrue(list.containsAll(listOf("India","Japan")))
```

在上面的示例中，我们使用迭代器来迭代可变的国家列表并删除元素。

### 3.2 removeAll()

**或者，我们可以使用removeAll()函数删除与给定谓词匹配的可变列表的元素**：

```kotlin
val countries = mutableListOf("Germany", "India", "Japan", "Brazil", "Australia")
countries.removeAll { it.length > 5 }

assertEquals(2, countries.size)
assertTrue(list.containsAll(listOf("India","Japan")))
```

在本例中，我们使用removeAll()函数删除所有长度超过5的元素。

### 3.3 retainAll()

**最后，我们可以使用retainAll()函数来保留与给定谓词匹配的可变列表的元素**：

```kotlin
val countries = mutableListOf("Germany", "India", "Japan", "Brazil", "Australia")
countries.retainAll { it.length > 5 }

assertEquals(3, countries.size)
assertTrue(list.containsAll(listOf("Germany","Brazil","Australia")))
```

在这里，我们使用retainAll()函数来保留所有长度大于5的元素。

## 4. 总结

在本文中，我们讨论了用于过滤List的各种函数。
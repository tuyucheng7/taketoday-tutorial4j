## 1. 概述

在本教程中，我们将学习如何删除Kotlin集合中的重复元素。

## 2. distinct()函数

**为了从任何集合(或**[Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/)**，更具体地说)中删除重复元素，我们可以使用**[distinct()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/distinct.html)**扩展函数**：

```kotlin
val protocols = listOf("tcp", "http", "tcp", "udp", "udp")
val distinct = protocols.distinct()
assertThat(distinct).hasSize(3)
assertThat(distinct).containsExactlyInAnyOrder("tcp", "http", "udp")
```

如上所示，此函数删除给定字符串的所有额外出现，并在结果[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/)中仅保留每个字符串中的一个。

### 2.1 实现

在底层，distinct()扩展函数使用[equals()](https://www.baeldung.com/kotlin/equality-operators)方法比较元素，为了证明这一说法，我们可以看一下函数[实现](https://github.com/JetBrains/kotlin/blob/80cce1dc5280eb9135390270c8644a7b8d198071/libraries/stdlib/common/src/generated/_Collections.kt#L1639)：

```kotlin
public fun <T> Iterable<T>.distinct(): List<T> {
    return this.toMutableSet().toList()
}
```

如上所示，它将接收的Iterable转换为Set，因此，**equals()方法判断元素是否相等**。

在撰写本文时，底层的Set实现是[LinkedHashSet](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/LinkedHashSet.html)，这意味着**equals()实现也应该与**[hashCode()实现](https://www.baeldung.com/java-equals-hashcode-contracts)**兼容**；否则，我们会看到意想不到的结果。

## 3. distinctBy()函数

然而，**有时我们可能需要使用自定义条件来删除重复元素**。例如，假设我们要将URL封装为值的组合：

```kotlin
data class Url(val protocol: String, val host: String, val port: Int, val path: String)
```

鉴于此，我们不能简单地使用distinct()函数并删除重复的主机名。

但是，还有另一个名为[distinctBy()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/distinct-by.html)的扩展函数，它通过lambda接收其自定义条件。

为了更好地理解这一点，让我们考虑一组URL：

```kotlin
val urls = listOf(
    Url("https", "tuyucheng", 443, "/authors"),
    Url("https", "tuyucheng", 443, "/authors"),
    Url("http", "tuyucheng", 80, "/authors"),
    Url("https", "tuyucheng", 443, "/kotlin/distinct"),
    Url("https", "google", 443, "/"),
    Url("http", "google", 80, "/search"),
    Url("tcp", "docker", 2376, "/"),
)
```

所以现在，为了删除重复的主机名，我们可以像这样使用distinctBy()：

```kotlin
val uniqueHosts = urls.distinctBy { it.host }
assertThat(uniqueHosts).hasSize(3)
```

在这里，我们告诉distinctBy{}在比较每个Url实例时使用主机名(it.host部分)；显然，在上面的示例中我们只有三个不同的主机：“tuyucheng”、“google”和“docker”。

作为另一个例子，在这里，我们要删除重复的完整URL：

```kotlin
val uniqueUrls = urls.distinctBy { "${it.protocol}://${it.host}:${it.port}/" }
assertThat(uniqueUrls).hasSize(5)
```

在上面的示例中，如果两个URL共享相同的协议、主机名和端口值，则它们是重复的。

### 3.1 实现

让我们看看distinctBy()的[实现](https://github.com/JetBrains/kotlin/blob/80cce1dc5280eb9135390270c8644a7b8d198071/libraries/stdlib/common/src/generated/_Collections.kt#L1652)：

```kotlin
public inline fun <T, K> Iterable<T>.distinctBy(selector: (T) -> K): List<T> {
    val set = HashSet<K>()
    val list = ArrayList<T>()
    for (e in this) {
        val key = selector(e)
        if (set.add(key))
            list.add(e)
    }
    return list
}
```

基本上，它迭代接收的Iterable<T\>一次，对于每个元素，它使用给定的lambda选择器计算一个键，如果这个键是重复的，那么它不会将当前元素添加到最终的List中。在本教程中，我们只使用了字符串键，但是，也可以在lambda中返回任何其他类型。

## 4. 总结

在本教程中，我们学习了两种从集合或数组中删除重复元素的方法。当我们要使用对象相等性来比较元素时，distinct()函数很有用。另一方面，为了更个性化的比较，我们可以使用更灵活的distinctBy()函数。
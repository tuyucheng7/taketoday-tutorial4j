##  一、简介

在本教程中，我们将说明如何迭代[JSONArray](https://stleary.github.io/JSON-java/org/json/JSONArray.html)。

首先，我们将尝试使用for循环来完成它。然后，我们将研究如何制作扩展功能并使其更加简单。

## 2. 遍历JSONArray

[JSONArray](https://stleary.github.io/JSON-java/org/json/JSONArray.html) 类未实现迭代[器](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/) 运算符。因此，我们不能使用典型的 for-each 模式来迭代 JSONArray。

让我们看看我们可以使用的其他一些方法。

### 2.1. 通过 for循环迭代

我们可以使用for循环遍历JSONArray：

```kotlin
val booksJSONArray = JSONArray(BOOKS_STRING)
for (i in 0 until booksJSONArray.length()) {
    val book = booksJSONArray.getJSONObject(i)
    println("${book.get("book_name")} by ${book.get("author")}")
}
```

我们还可以使用for-each循环：

```kotlin
val booksJSONArray = JSONArray(BOOKS_STRING)
(0 until booksJSONArray.length()).forEach {
    val book = booksJSONArray.getJSONObject(it)
    println("${book.get("book_name")} by ${book.get("author")}")
}
```

这使事情更具可读性。但是，它仍然不如使用迭代器直观。

### 2.2. 添加方便的扩展功能

典型的迭代模式如下所示：

```java
val numbers = arrayListOf(1,2)
for (number in numbers){
    ...
}
```

在这里，我们可以使用这种模式，因为[ArrayList](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-array-list/) 实现 了[Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/)。遗憾的是，JSONArray没有实现它。

但是，我们可以使用扩展函数来完成这项工作：

```kotlin
operator fun <T> JSONArray.iterator(): Iterator<T> =
  (0 until this.length()).asSequence().map { this.get(it) as T }.iterator()
```

在这里，我们将迭代器 运算符添加到JSONArray。最后，这让我们像往常一样循环它：

```kotlin
for (book in booksJSONArray) {
    println("${(book as JSONObject).get("book_name")} by ${(book as JSONObject).get("author")}")
}
```

## 3.总结

总而言之，JSONArray不公开 迭代器。如上所示，一种选择是使用 for 循环。此外，我们可以使用扩展函数用迭代器对其进行改造。
## 1. 概述

当我们使用[Arrays](https://www.baeldung.com/kotlin/arrays)时，我们经常想检查数组是否包含给定值。

在本教程中，让我们探讨如何检查数组是否包含Kotlin中的给定元素。

## 2. 问题简介

如果我们仔细考虑这个问题，我们可以意识到这个问题可以有两种变体：检查一个数组是否包含：

-   一个给定值
-   一组值中的任何值

在本教程中，我们将介绍涵盖这两种情况的各种方法。

此外，为简单起见，我们将在单元测试中创建断言以验证我们的方法是否按预期工作。

现在，让我们创建一个字符串数组作为例子来验证我们的解决方案：

```kotlin
val myArray = arrayOf("Ruby", "Java", "Kotlin", "Go", "Python", "Perl")
```

如上面的代码所示，在Kotlin中，我们可以使用arrayOf函数轻松地[初始化一个数组](https://www.baeldung.com/kotlin/initialize-array)，我们的myArray包含一些编程语言名称。

接下来，让我们看看如何检查数组是否包含特定值。

## 3. 检查一个数组是否包含一个单一的值

首先，让我们检查数组是否包含一个值。

### 3.1 使用any函数

Kotlin在Array类上预定义了[any()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/any.html)[扩展函数](https://www.baeldung.com/kotlin/extension-methods)。

**我们可以将谓词函数传递给any()函数，any()函数遍历数组中的元素并在元素与给定谓词匹配时返回true**。

如果我们比较数组元素和谓词函数中的给定值，any()函数可以解决我们的问题：

```kotlin
assertThat(myArray.any { it == "Kotlin" }).isTrue
assertThat(myArray.any { it == "Php" }).isFalse
```

### 3.2 使用in运算符

在Kotlin中，Array类具有[contains](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/contains.html)函数来检查数组是否包含给定值，这正是我们要找的。此外，当我们调用contains函数时，我们可以使用[in运算符](https://www.baeldung.com/kotlin/operator-overloading#7-in-convention)使代码更易于阅读：

```kotlin
assertThat("Kotlin" in myArray).isTrue
assertThat("Php" in myArray).isFalse
```

因此，**如果我们要检查一个数组是否包含一个给定值，使用“value in array”将是最直接和惯用的方法**。

接下来，让我们看看在第二种情况下如何进行检查。

## 4. 检查数组是否包含一组值中的任何值

有时，我们会得到多个值，我们想知道一个数组是否至少包含其中的一个值，有几种方法可以实现这一目标。接下来，让我们看看其中一些的实际应用。

### 4.1 使用Java的disjoint方法

**解决该问题的一个想法是首先将数组和要检查的值都转换为Collection类型，例如**[Set](https://www.baeldung.com/kotlin/collections-api#2-set)，**然后，我们可以检查这两个集合是否有共同的元素**，如果我们找到共同元素，则意味着该数组至少包含给定值中的一个元素。

Java的[Collections.disjoint](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Collections.html#disjoint(java.util.Collection,java.util.Collection))方法就是做这个工作的：

```kotlin
val toBeChecked1 = setOf("Scala", "Php", "whatever", "Kotlin")
val toBeChecked2 = setOf("Scala", "Php", "Javascript", "whatever")

val mySet = myArray.toSet()

assertThat(!Collections.disjoint(mySet, toBeChecked1)).isTrue
assertThat(!Collections.disjoint(mySet, toBeChecked2)).isFalse
```

如上面的代码所示，我们可以使用Array的toSet扩展函数快速将数组转换为Set。当我们执行测试时，不出所料，它通过了。

### 4.2 使用Kotlin的intersect函数

Iterable类在Kotlin的标准库中定义了[intersect函数](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/intersect.html)，此函数将以Set的形式返回两个集合中包含的元素。**此外，intersect函数是一个**[中缀函数](https://www.baeldung.com/kotlin/infix-functions)。

同样，我们可以先将数组和要检查的值都转换为两个Set，然后将这两个set传递给intersect函数。最后，我们验证返回的Set，如果它不为空，我们知道我们的数组至少包含一个给定值。

接下来，让我们创建一些测试，看看它是否按预期工作：

```kotlin
val mySet = myArray.toSet()

assertThat((toBeChecked1 intersect mySet).isNotEmpty()).isTrue
assertThat((toBeChecked2 intersect mySet).isNotEmpty()).isFalse
```

如果我们运行，测试就会通过。

### 4.3 组合any函数和in运算符

我们已经了解到，我们可以使用any函数或in运算符检查数组是否包含单个给定值。

现在，我们有多个候选值要检查，我们可以结合any函数和in运算符来解决问题：

```kotlin
assertThat(myArray.any { it in toBeChecked1 }).isTrue
assertThat(myArray.any { it in toBeChecked2 }).isFalse
```

正如我们在上面的实现中看到的，我们在any函数的谓词函数中使用了in运算符，**与intersect和disjoint方法不同，一旦在数组和候选值集合中检测到共同值，any函数返回true并终止进一步检查**。

当然，我们可以交换上面实现中的数组和候选值集合，它遵循相同的逻辑：

```kotlin
assertThat(toBeChecked1.any { it in myArray }).isTrue
assertThat(toBeChecked2.any { it in myArray }).isFalse
```

值得一提的是，除了将lambda传递给any函数外，**我们还可以传递contains函数的引用**：

```kotlin
assertThat(myArray.any(toBeChecked1::contains)).isTrue
assertThat(myArray.any(toBeChecked2::contains)).isFalse
```

### 4.4 创建一个惯用的中缀扩展函数

现在，让我们在Array类上创建我们自己的扩展函数“containsAnyFrom”来解决这个问题：

```kotlin
infix fun <T> Array<T>.containsAnyFrom(elements: Collection<T>): Boolean {
    return any(elements.toSet()::contains)
}
```

如上面的代码所示，**由于我们的函数只有一个参数，我们可以在函数定义中添加中缀，使函数调用更易于阅读**：

```kotlin
myArray containsAnyFrom toBeChecked1
myArray containsAnyFrom toBeChecked2
```

## 5. 总结

在本文中，我们探讨了检查数组是否包含给定值或多个值中的任何值的不同解决方案。此外，我们已经展示了哪些是惯用的Kotlin解决方案。
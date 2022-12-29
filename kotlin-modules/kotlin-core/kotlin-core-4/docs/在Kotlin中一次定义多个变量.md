## 1. 概述

正如我们所知，我们可以在Java中的一行中声明和分配多个变量。

在本教程中，我们将探讨如何在Kotlin中执行相同的操作。

## 2. 问题简介

和Java一样，我们可以在一行中声明和赋值多个变量，在本教程中，我们将介绍实现此目的的方法：

-   用分号分隔声明
-   使用[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/)对象分配两个变量
-   使用[数组](https://www.baeldung.com/kotlin/arrays)或[List](https://www.baeldung.com/kotlin/lists)分配多个变量
-   使用[解构声明](https://www.baeldung.com/kotlin/destructuring-declarations)

为简单起见，我们将使用单元测试来验证变量在分配后是否具有预期值。那么接下来，让我们首先创建三个变量来保存预期值：

```kotlin
private val expectedString = "Kotlin is awesome!"
private val expectedLong = 42L
private val expectedList = listOf("I", "am", "a", "list", ".")
```

接下来，让我们看看实际的方法。

## 3. 用分号分隔声明

在Java中，我们一次只能在同一个类型中声明和赋值多个变量，例如：

```java
//Javacode
String str1 = "Hello World!", str2 = "Hi there", str3 = "See you later";
```

如上例所示，我们在一行中声明了三个String变量，并用逗号分隔。

在Kotlin中，我们可以类似地使用分号作为分隔符来实现它，先看一个测试方法：

```kotlin
 val aLong = 42L; val aString = "Kotlin is awesome!"; val aList = listOf("I", "am", "a", "list", ".")
 assertThat(aLong).isEqualTo(expectedLong)
 assertThat(aString).isEqualTo(expectedString)
 assertThat(aList).isEqualTo(expectedList)
```

如果我们运行测试，它就会通过。因此，变量赋值按预期工作。

眼尖的人可能已经注意到，**与Java不同的是，在Kotlin中，我们可以在一行中声明和赋值多个不同类型的变量**。

## 4. 使用Pair分配两个变量

或者，**使用Pair对象，我们也可以一次分配两个不同类型的变量**：

```kotlin
val/var (var1:Type1, var2:Type2) = Pair(value1,value2) 
or
val/var (var1:Type1, var2:Type2) = value1 to value2
```

我们通过一个测试方法来快速了解一下：

```kotlin
val (aLong, aString) = 42L to "Kotlin is awesome!"
assertThat(aLong).isEqualTo(expectedLong)
assertThat(aString).isEqualTo(expectedString)
```

在上面的示例中，**我们没有显式声明两个变量的类型(Long和String)，这是因为Kotlin非常智能，它可以从值中推断类型(42L -> Long和“Kotlin is ...” -> String)**。

但是，如果我们想一次分配两个以上的变量怎么办？接下来，让我们弄清楚。

## 5. 使用数组或List分配多个变量

首先，让我们看一个使用Array对象分配三个不同类型变量的示例：

```kotlin
val (aLong, aString, aList) = arrayOf(42L, "Kotlin is awesome!", listOf("I", "am", "a", "list", "."))
assertThat(aLong).isEqualTo(expectedLong)
assertThat(aString).isEqualTo(expectedString)
assertThat(aList).isEqualTo(expectedList)
```

正如我们在上面的示例中看到的，语法与Pair方法非常相似。不同之处在于，我们在左侧创建了三个变量，而在右侧创建了一个Array对象。

类似地，如果我们用List实例替换数组对象，赋值也会起作用：

```kotlin
val (aLong, aString, aList) = listOf(42L, "Kotlin is awesome!", listOf("I", "am", "a", "list", "."))
assertThat(aLong).isEqualTo(expectedLong)
assertThat(aString).isEqualTo(expectedString)
assertThat(aList).isEqualTo(expectedList)
```

值得一提的是，当我们使用数组或列表为多个变量赋值时，**我们应该确保左侧变量的顺序与数组或列表中元素的顺序一致**。

## 6. 使用解构声明

最后，让我们看看Kotlin的解构声明如何声明和分配多个变量的，在开始之前，让我们创建一个非常简单的[数据类](https://www.baeldung.com/kotlin/data-classes)Article：

```kotlin
data class Article(val title: String, val author: String, val words: Long, val published: Boolean)
```

如上面的类所示，Article类有四个属性。反过来，它们是两个字符串(title和author)、一个长字符串(words)和一个布尔值(published)。

**Kotlin的解构声明允许我们将一个对象解构为多个变量**。换句话说，如果我们有一个Article对象，我们可以将该对象解构为两个String变量、一个Long变量和一个Boolean变量。让我们看一个例子：

```kotlin
val anArticle = Article("Define multiple variables at once in Kotlin", "Kai", 4200L, false)
val (title, author, noOfWords, publishedAlready) = anArticle
assertThat(title).isEqualTo(anArticle.title)
assertThat(author).isEqualTo(anArticle.author)
assertThat(noOfWords).isEqualTo(anArticle.words)
assertThat(publishedAlready).isFalse
```

在这里，我们应该注意，**左边声明的变量顺序val(title, author, noOfWords, publishedAlready)应该和Article类中声明的属性的顺序一致**。

在我们的例子中，Article类有四个属性，但是，我们可能不需要将它们全部解构为变量。例如，我们可能只想保存一个Article的title: String和published: Boolean两个变量。所以，**如果我们在解构声明中不需要变量，我们可以在左侧声明中放置一个下划线**：

```kotlin
val anArticle = Article("Define multiple variables at once in Kotlin", "Kai", 4200L, false)
val (title, _, _, publishedAlready) = anArticle
assertThat(title).isEqualTo(anArticle.title)
assertThat(publishedAlready).isFalse
```

## 7. 总结

在本文中，我们了解了Kotlin在一行中声明和分配多个变量的方式，我们可以通过几种方法来做到这一点，例如分号分隔的声明、通过List、使用解构声明等。

与Java相比的主要区别在于，在Kotlin中，我们可以在一行中声明和分配多个不同类型的变量。
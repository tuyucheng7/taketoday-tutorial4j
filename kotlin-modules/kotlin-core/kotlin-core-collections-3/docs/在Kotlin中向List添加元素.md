## 1. 概述

[List](https://www.baeldung.com/kotlin/lists)是Kotlin中非常常用的数据类型，在本教程中，我们将探讨如何将元素添加到Kotlin List中。

## 2. 问题简介

与Java相比，**除了**[ArrayList](https://www.baeldung.com/kotlin/arraylist-vs-mutablelistof#1-arraylistlttgt)**之外，Kotlin还新增了一些List类型，例如List和**[MutableList](https://www.baeldung.com/kotlin/arraylist-vs-mutablelistof#2-mutablelistoflttgt)。

如果我们来自Java世界，这些List可能会让我们感到困惑。此外，当我们尝试在Kotlin中向List添加元素时，并不总是像在Java中调用add()方法那样简单。例如，有时，list.add()行可能无法编译。

下面简单介绍一下Kotlin中常见的三种List类型：ArrayList、MutableList和List。此外，我们将展示如何通过示例向这些List添加元素。

## 3. ArrayList、MutableList和List

首先，让我们看一下**集合的三种不同的可变性类型**：

-   可变的：我们可以自由地改变集合，例如，添加或删除元素。
-   只读：我们不能更改集合的内容，例如添加或删除元素；但是，我们可以修改底层数据对象。
-   不可变：我们无法更改集合的任何内容。

接下来我们看看Kotlin中常见的三种List类型：

-   ArrayList：可变的，类似于Java的ArrayList
-   MutableList：也是可变的，我们经常使用方便的mutableListOf()函数来初始化MutableList，在这种情况下，我们将获得一个ArrayList实例
-   List：只读，add()和remove()函数不可用

现在我们已经了解了三种KotlinList类型之间的差异，让我们看看如何向它们中的每一种添加元素。

为简单起见，我们将使用断言来验证“添加”操作是否按预期工作。

接下来，让我们看看它们的实际效果。

## 4. 将元素添加到ArrayList

由于Kotlin的ArrayList和Java端的ArrayList几乎一样，所以在Kotlin ArrayList中添加一个元素不是问题，我们可以**调用add()方法**来做到这一点：

```kotlin
val myArrayList = ArrayList<String>()
myArrayList.add("Tom Hanks")
assertThat(myArrayList).containsExactly("Tom Hanks")
```

如果我们运行测试，不出所料，它会通过。

值得一提的是，Kotlin支持[运算符重载](https://www.baeldung.com/kotlin/operator-overloading)，此外，**Kotlin标准库附带“+=”运算符以对MutableCollection执行“plusAssign”操作**，即，将元素添加到可变集合中。

**+=运算符可以使代码更易于阅读**，例如，我们可以使用“+=”向myArrayList添加一个新元素：

```kotlin
myArrayList += "Brad Pitt"
assertThat(myArrayList).containsExactly("Tom Hanks", "Brad Pitt")
```

## 5. 向MutableList添加一个元素

我们了解到，我们可以通过调用mutableListOf()方法轻松初始化KotlinMutableList，该函数返回一个ArrayList对象。因此，向其添加新元素与我们对ArrayList所做的相同：

```kotlin
val myMutable = mutableListOf("Tom Hanks", "Brad Pitt")
myMutable.add("Tom Cruise")
assertThat(myMutable).containsExactly("Tom Hanks", "Brad Pitt", "Tom Cruise")
myMutable += "Will Smith"
assertThat(myMutable).containsExactly("Tom Hanks", "Brad Pitt", "Tom Cruise", "Will Smith")
```

如上面的测试所示，我们用两个元素初始化了一个MutableList，然后，我们使用相同的方法使用add()方法和+=运算符将两个元素添加到List中。

如果我们运行，测试就会通过。

## 6. 向只读List添加元素

最后，让我们看看Kotlin中的List类型，**由于List是只读的，我们无法向其添加元素**，但是让我们创建一个测试来验证它是否为真：

```kotlin
var myList = listOf("Tom Hanks", "Brad Pitt")
myList.add("Tom Cruise")
```

如果我们编译上面的代码，编译器会报错：“Kotlin: Unresolved reference: add.” 这是意料之中的，因为List接口没有add()方法。

接下来，让我们测试+=运算符：

```kotlin
val myList = listOf("Tom Hanks", "Brad Pitt")
myList += "Tom Cruise"
```

正如我们所料，代码也无法编译，但是，这次的错误信息有所不同：“Kotlin: Val cannot be reassigned.”

现在，让我们仔细看看这个编译错误。首先，由于List接口及其超类型没有plusAssign()函数，因此myList += “Tom Cruise”与myList = myList + “Tom Cruise”相同，因此，它解释了重新分配是如何发生的。

更进一步，让我们看一下List的'+'(加号操作)运算符的实现：

```kotlin
public operator fun <T> Collection<T>.plus(element: T): List<T> {
    val result = ArrayList<T>(size + 1)
    result.addAll(this)
    result.add(element)
    return result
}
```

如上面的代码所示，每次我们调用“List + element”时，**Kotlin都会创建一个包含原始List中所有元素和新元素的新List**。换句话说，'+'操作将返回一个新对象。接下来我们通过测试来验证一下：

```kotlin
var myList = listOf("Tom Hanks", "Brad Pitt")
val originalList = myList

myList += "Tom Cruise"
assertThat(myList).containsExactly("Tom Hanks", "Brad Pitt", "Tom Cruise")
assertThat(myList).isNotSameAs(originalList)
```

如我们所见，**我们使用了var关键字来声明myList变量以使其可变**，此外，我们要确保在执行myList += “...”之后，myList变量将引用一个新对象。

如果我们执行它，测试就会通过。

## 7. 总结

在本文中，我们探讨了如何向Kotlin的三种常见List类型添加元素。
## 1. 概述

众所周知，Kotlin有用于保存数据的[数据类](../../kotlin-core-lang-oop-1/docs/Kotlin中的数据类.md)。

在本教程中，我们将通过几个常见的陷阱来讨论数据类的equals()方法。

## 2. Kotlin的数据类

Kotlin的数据类是为保存数据而设计的，例如：

```kotlin
data class Person(val firstname: String, val lastname: String)
```

**数据类必须有一个非空的主构造函数，此外，数据类不能被继承**。

Kotlin的**数据类预先实现了一组常用的方法，例如getters/setters、copy()、toString()、hashcode()和equals()**，这些方法使我们能够非常轻松地操作数据。

但是，如果我们不太了解数据类的equals()方法，我们可能会遇到问题。

接下来，让我们通过两个常见的陷阱来更好地理解equals()方法。

为简单起见，我们将使用单元测试断言来验证本教程中的结果。

## 3. 数据类equals陷阱#1

首先，让我们稍微扩展一下Person类，假设我们要引入一个新的dateOfBirth属性并将Person类变成PersonV1类：

```kotlin
data class PersonV1(val firstname: String, val lastname: String) {
    lateinit var dateOfBirth: LocalDate
}
```

下面通过一个例子来介绍一下第一个陷阱。

### 3.1 陷阱简介

一个例子可以快速说明问题，假设我们创建了两个PersonV1的实例：

```kotlin
val p1 = PersonV1("Amanda", "Smith").also { it.dateOfBirth = LocalDate.of(1992, 8, 8) }
val p2 = PersonV1("Amanda", "Smith").also { it.dateOfBirth = LocalDate.of(1976, 11, 18) }
```

正如我们所了解的，该数据类已经提供了hashcode()和equals()方法。如果我们使用equals()方法比较p1和p2，虽然它们的firstname和lastname相同，但我们会认为它们显然不相等，因为它们的dateOfBirth值不同。

但是，如果我们运行下面的测试，它会通过：

```kotlin
assertTrue { p1 == p2 }
```

所以，这个结果告诉我们p1和p2实际上是相等的。我们知道，与Java不同，**Kotlin的**[“==”运算符](https://www.baeldung.com/kotlin/equality-operators)**检查结构相等性，在这种情况下，它调用数据类的equals()方法并执行值比较。**

如果我们认为p1和p2不相等，可能会导致一些问题。例如，我们可以将它们用作键来将一些数据存储在HashMap中，并期望有两个条目。但是，由于p1 == p2的事实，后一个条目将覆盖现有的条目。

接下来，让我们理解为什么Kotlin告诉我们p1 == p2，尽管它们的dateOfBirth属性具有不同的值。

### 3.2 理解数据类的equals()方法并解决问题

这就是p1 == p2的原因：**数据类仅在主构造函数中声明的属性上自动生成那些方便的方法，例如equals()、hashcode()、toString()和copy()**。

换句话说，当我们检查p1 == p2时，只会比较主构造函数中的属性，dateOfBirth属性不在主构造函数中；因此，它根本没有比较。

既然我们了解了数据类的默认equals()是如何工作的，那么解决这个问题对我们来说就不是一项具有挑战性的工作了。

所以，有两种方法可以选择。第一个选项(也是推荐的方法)是**重构相关代码并将dateOfBirth属性移动到主构造函数**。这样，所有三个属性都将参与equals()检查。

或者，我们可以覆盖hashcode()和equals()方法，这样当我们对该数据类调用equals()方法时，我们自己实现的equals()方法将被调用。

那么接下来，让我们创建一个新类PersonV2来覆盖hashcode()和equals()：

```kotlin
data class PersonV2(val firstname: String, val lastname: String) {
    lateinit var dateOfBirth: LocalDate

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PersonV2) return false

        if (firstname != other.firstname) return false
        if (lastname != other.lastname) return false
        if (dateOfBirth != other.dateOfBirth) return false

        return true
    }

    override fun hashCode(): Int {
        var result = firstname.hashCode()
        result = 31 * result + lastname.hashCode()
        result = 31 * result + dateOfBirth.hashCode()
        return result
    }
}
```

接下来，让我们测试equals()方法是否按预期工作：

```kotlin
val p1 = PersonV2("Amanda", "Smith").also { it.dateOfBirth = LocalDate.of(1992, 8, 8) }
val p2 = PersonV2("Amanda", "Smith").also { it.dateOfBirth = LocalDate.of(1976, 11, 18) }
assertFalse { p1 == p2 }
```

在测试中，我们创建了两个firstname和lastname相同的PersonV2实例，但它们的dateOfBirth值不同；所以这一次，我们预计它们不相等。

## 4. 数据类equals陷阱#2

我们已经了解到，Kotlin的数据类仅在执行equals()检查时检查在其主构造函数中声明的属性，那么让我们看另一个数据类示例：

```kotlin
data class TuyuchengString(
    val value: String,
    val chars: CharArray,
)
```

如上面的代码所示，这次所有属性都在TuyuchengString的主构造函数中声明。所以，接下来，让我们创建一些实例，看看equals()检查是否给了我们预期的结果。

### 4.1 陷阱简介

同样，让我们创建两个TuyuchengString实例：

```kotlin
val s1 = TuyuchengString("Amanda", charArrayOf('A', 'm', 'a', 'n', 'd', 'a'))
val s2 = TuyuchengString("Amanda", charArrayOf('A', 'm', 'a', 'n', 'd', 'a'))
```

正如我们所看到的，尽管s1和s2是不同的对象，但它们在value和chars属性中具有相同的值。因此，我们可能认为s1 == s2应该返回true。

令人惊讶的是，如果我们执行下面的测试，它会通过。也就是说，**s1不等于s2**。

```kotlin
assertFalse { s1 == s2 }
```

接下来，让我们了解为什么会得到这个结果并解决问题。

### 4.2 了解Kotlin中的Array.equals()并解决问题

当我们调用数据类的equal()方法时，Kotlin会比较主构造函数中声明的属性。此外，当Kotlin比较属性时，它会调用属性类型的equals()方法。

例如，对于s1 == s2，Kotlin检查s1.value == s2.value和s1.chars == s2.chars，正如我们所提到的，==运算符检查结构是否相等。

但为什么s1 == s2返回false？这是因为，**与List不同**，[array1 == array2](https://www.baeldung.com/kotlin/comparing-arrays#equality)**在Kotlin中比较它们的引用**。 

让我们创建一个测试来查看差异：

```kotlin
val list1 = listOf("one", "two", "three", "four")
val list2 = listOf("one", "two", "three", "four")

val array1 = arrayOf("one", "two", "three", "four")
val array2 = arrayOf("one", "two", "three", "four")

assertTrue { list1 == list2 }
assertFalse { array1 == array2 }
```

如果我们运行上面的测试，它就会通过。

因此，==不是比较两个数组值的正确方法，**如果我们想检查数组结构是否相等，我们应该使用数组的**[contentEquals()](https://www.baeldung.com/kotlin/comparing-arrays#contentEquals)**方法**：

```kotlin
assertTrue { array1 contentEquals array2 }
```

上述断言通过。

现在，让我们解决这个问题。

由于List的“==”运算符检查值，最直接的解决方案是用集合替换数据类中的数组。此外，一般来说，**我们应该**[更喜欢List而不是数据](https://www.baeldung.com/kotlin/convert-list-to-array#preferring-lists-over-arrays)。

如果出于某种原因，我们必须在数据类中使用数组类型，我们可以通过覆盖equals()和hashcode()方法来解决问题。但是我们应该注意，**在equals()方法中，我们应该使用contentEquals()方法而不是'=='来比较数组的值**。

让我们创建TuyuchengStringV2并覆盖这两个方法：

```kotlin
data class TuyuchengStringV2(val value: String, val chars: CharArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TuyuchengStringV2) return false

        if (value != other.value) return false
        if (!chars.contentEquals(other.chars)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + chars.contentHashCode()
        return result
    }
}
```

最后，让我们创建两个具有相同值的TuyuchengStringV2对象并测试是否得到预期结果：

```kotlin
val s1 = TuyuchengStringV2("Amanda", charArrayOf('A', 'm', 'a', 'n', 'd', 'a'))
val s2 = TuyuchengStringV2("Amanda", charArrayOf('A', 'm', 'a', 'n', 'd', 'a'))

assertTrue { s1 == s2 }
```

当我们执行它时，测试通过了。这样，问题就解决了。

## 5. 总结

在本文中，我们通过几个常见的陷阱讨论了Kotlin数据类的equals()方法。
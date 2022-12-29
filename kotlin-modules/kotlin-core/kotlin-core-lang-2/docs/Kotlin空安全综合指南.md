## 1. 概述

在本文中，我们将研究Kotlin语言中内置的空安全功能。Kotlin提供全面的、原生的可空字段处理-不需要额外的库。

## 2. Maven依赖

首先，你需要将kotlin-stdlib Maven依赖项添加到你的pom.xml：

```xml
<dependency>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-stdlib</artifactId>
    <version>1.1.1</version>
</dependency>
```

你可以在[Maven Central](https://search.maven.org/search?q=a:kotlin-stdlib)上找到最新版本。

## 3. 可为空和不可为空的引用类型

**Kotlin有两种类型的引用**，它们由编译器解释以在编译时为程序员提供有关程序正确性的信息-可为空的和不可为空的。

**默认情况下，Kotlin假定value不能为null**：

```kotlin
var a: String = "value"

assertEquals(a.length, 5)
```

我们不能将null分配给引用a，如果你尝试这样做，它会导致编译器错误。

**如果我们想创建一个可为空的引用，我们需要创建追加问号(?)到类型定义**：

```kotlin
var b: String? = "value"
```

之后，我们可以为其分配null：

```kotlin
b = null
```

**当我们想要访问b引用时，我们必须显式处理null情况以避免编译错误**，因为Kotlin知道这个变量可以保存null：

```kotlin
if (b != null) {
    println(b.length)
} else {
    assertNull(b)
}
```

## 4. 安全调用

以这种方式处理每个可为空的引用可能很麻烦，幸运的是，Kotlin有一种“安全调用”的语法，**这种语法允许程序员仅在特定引用持有非空值时才执行操作**。

让我们定义两个数据类来说明此功能：

```kotlin
data class Person(val country: Country?)

data class Country(val code: String?)
```

请注意，country和code字段是可为空的引用类型。

为了以流畅的方式访问这些字段，我们可以使用安全调用语法：

```kotlin
val p: Person? = Person(Country("ENG"))

val res = p?.country?.code

assertEquals(res, "ENG")
```

如果变量p为null，安全调用语法将返回null结果：

```kotlin
val p: Person? = Person(Country(null))

val res = p?.country?.code

assertNull(res)
```

### 4.1 let()方法

**要仅在引用包含非空值时执行操作，我们可以使用let运算符**。

假设我们有一个值列表，并且该列表中还有一个空值：

```kotlin
val firstName = "Tom"
val secondName = "Michael"
val names: List<String?> = listOf(firstName, null, secondName)
```

接下来，我们可以使用let函数对名names集合的每个不可为空的元素执行操作：

```kotlin
var res = listOf<String?>()
for (item in names) {
    item?.let { res = res.plus(it) }
}

assertEquals(2, res.size)
assertTrue { res.contains(firstName) }
assertTrue { res.contains(secondName) }
```

### 4.2 also()方法

**如果我们想应用一些额外的操作，例如记录每个非空值，我们可以使用also()方法并将它与let()链接起来**：

```kotlin
var res = listOf<String?>()
for (item in names) {
    item?.let { res = res.plus(it); it }
        ?.also{it -> println("non nullable value: $it")}
}
```

它将打印出每个不为空的元素：

```bash
non nullable value: Tom
non nullable value: Michael
```

### 4.3 run()方法

Kotlin有一个run()方法来对可为空的引用执行一些操作，它与let()非常相似，但在函数体内部，Run()方法**在此引用而不是函数参数上运行**：

```kotlin
var res = listOf<String?>()
for (item in names) {
    item?.run { res = res.plus(this) }
}
```

## 5. elvis运算符(?:)

有时，当我们有一个引用时，如果引用持有null，我们希望从操作中返回一些默认值。为了实现这一点，我们可以使用elvis运算符(?:)，这相当于Java Optional类中的orElse/orElseGet：

```kotlin
val value: String? = null

val res = value?.length ?: -1

assertEquals(res, -1)
```

当value引用持有一个不可为空的值时，方法length将被调用：

```kotlin
val value: String? = "name"

val res = value?.length ?: -1

assertEquals(res, 4)
```

## 6. 可为空的不安全获取

**Kotlin还有一个不安全的运算符，可以在不显式处理缺失逻辑的情况下获取可为空字段的值，但应该非常小心地使用它**。

双感叹号运算符(!!)从可为null的引用中获取一个值，如果它为null，则抛出NullPointerException，这等效于Optional.get()操作：

```kotlin
var b: String? = "value"
b = null

assertFailsWith<NullPointerException> {
    b!!
}
```

如果可为null的引用包含一个不可为null的值，则对该值的操作将成功执行：

```kotlin
val b: String? = "value"

assertEquals(b!!.length, 5)
```

## 7. 从列表中过滤空值

Kotlin中的List类有一个实用方法filterNotNull()，它只从包含可为空引用的列表中返回不可为空的值：

```kotlin
val list: List<String?> = listOf("a", null, "b")

val res = list.filterNotNull()

assertEquals(res.size, 2)
assertTrue { res.contains("a") }
assertTrue { res.contains("b") }
```

这是一个非常有用的结构，它封装了我们原本需要自己实现的逻辑。

## 8. 总结

在本文中，我们深入探讨了Kotlin的空安全特性，我们看到了可以保存空值和不能保存空值的引用类型。我们通过使用“安全调用”功能和elvis运算符实现了流畅的空处理逻辑。
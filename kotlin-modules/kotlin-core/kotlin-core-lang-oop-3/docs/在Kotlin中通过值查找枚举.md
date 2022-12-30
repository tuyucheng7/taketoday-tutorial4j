## 1. 概述

[枚举](https://www.baeldung.com/kotlin/enum)允许我们以类型安全、不易出错和自我记录的方式处理常量。此外，由于枚举也是类，因此它们可以具有属性。

在本教程中，我们将探索如何通过给定的属性值找到对应的枚举对象。

## 2. 问题简介

像往常一样，让我们通过一个例子来理解这个问题：

```kotlin
enum class Number(val value: Int) {
    ONE(1), TWO(2), THREE(3),
}
```

如上例所示，我们有一个非常简单的枚举类Number，它有三个实例。此外，Number枚举具有Int属性值。

现在，**假设我们得到了一个值v，我们想要找到满足e.value == v的枚举实例e**。例如，对于值3，我们期望得到THREE。

当然，没有枚举实例可以匹配给定的值v，例如v=42。在现实世界中，我们可能会根据需要以不同方式处理这种情况，例如，通过抛出异常或创建额外的UNKNOWN(x)枚举实例。在本教程中，我们将简单地为未找到的情况返回null。

我们将介绍四种解决问题的方法，为了便于理解，我们将为每种方法以不同的方式命名枚举类，例如NumberV1、NumberV2等。

为简单起见，我们将使用单元测试断言来验证我们的解决方案是否按预期工作。

## 3. 方法#1：通过Enum.values()检查

解决该问题的一个直接想法是**遍历枚举实例并找到其属性等于给定值的实例**。

我们可以使用[values()](https://www.baeldung.com/kotlin/enum#2-iterating-through-enum-constants)函数遍历枚举实例，因此，我们可以在[伴生对象](https://www.baeldung.com/kotlin/companion-object)块中创建一个函数来执行此检查，它的工作方式与Java的静态方法非常相似：

```kotlin
enum class NumberV1(val value: Int) {
    ONE(1), TWO(2), THREE(3);

    companion object {
        infix fun from(value: Int): NumberV1? = NumberV1.values().firstOrNull { it.value == value }
    }
}
```

因此，我们在伴生对象中创建了from()函数，正如我们之前提到的，**firstOrNull()函数负责返回找到的枚举实例，如果没有找到匹配的实例，则返回null**。

此外，我们已将from()声明为一个[中缀函数](https://www.baeldung.com/kotlin/infix-functions)，因此，我们可以这样调用它：“NumberV1 from someValue”。正如我们所看到的，**中缀函数调用的代码看起来像自然语言并且易于阅读**。

接下来，让我们创建一个测试，看看它是否正常工作：

```kotlin
val searchOne = NumberV1 from 1
assertEquals(NumberV1.ONE, searchOne)

val searchTwo = NumberV1 from 2
assertEquals(NumberV1.TWO, searchTwo)

val shouldBeNull = NumberV1.from(42)
assertNull(shouldBeNull)
```

如果我们运行它，测试就会通过，所以我们的from()函数解决了这个问题。

## 4. 方法#2：创建一个值 -> 枚举实例Map

我们中的一些人可能已经注意到，每次我们调用方法#1的from()函数时，它都会遍历枚举实例(O(N))。此外，我们知道HashMap的get()函数非常高效(O(1))。

虽然枚举类在实践中通常不会承载太多实例，但我们可以构建一个HashMap来保存值 -> 枚举实例关系。因此，如果我们想通过给定的值搜索枚举实例，我们可以直接从HashMap对象中获取它。

### 4.1 创建Map

那么接下来，让我们在NumberV2中实现这个想法：

```kotlin
enum class NumberV2(val value: Int) {
    ONE(1), TWO(2), THREE(3);

    companion object {
        private val map = NumberV2.values().associateBy { it.value }
        infix fun from(value: Int) = map[value]
    }
}
```

如上面的代码所示，**我们首先使用**[associateBy()](https://www.baeldung.com/kotlin/list-to-map#mapping-using-associate-methods)**函数将Array转换为Map**。然后，from()函数是从Map中获取匹配的枚举实例。

现在，让我们测试它是否按预期工作：

```kotlin
val searchOne = NumberV2 from 1
assertEquals(NumberV2.ONE, searchOne)

val searchTwo = NumberV2 from 2
assertEquals(NumberV2.TWO, searchTwo)

val shouldBeNull = NumberV2 from 42
assertNull(shouldBeNull)
```

如果我们执行它，测试就会通过。

### 4.2 运算符重载

我们已经了解到，中缀函数可以增强代码的可读性。或者，我们可以通过Kotlin中的[运算符重载](https://www.baeldung.com/kotlin/operator-overloading)来简化函数调用。

例如，**如果我们重载NumberV2的get()操作，我们可以通过“NumberV2\[theValue\]”找到对应的枚举实例**：

```kotlin
companion object {
    private val map = NumberV2.values().associateBy { it.value }
    operator fun get(value: Int) = map[value]
}
```

接下来，让我们看看该函数在测试中是如何被调用的：

```kotlin
val searchOneAgain = NumberV2[1]
assertEquals(NumberV2.ONE, searchOneAgain)

val searchTwoAgain = NumberV2[2]
assertEquals(NumberV2.TWO, searchTwoAgain)

val shouldBeNullAgain = NumberV2[42]
assertNull(shouldBeNullAgain)
```

毫不奇怪，如果我们运行，测试也会通过。

## 5. 方法#3：创建一个EnumFinder

方法#2基于HashMap，因此，我们首先将实例数组转化为具有值->枚举实例关系的Map，然后，from()函数负责接收输入值并从Map中获取枚举实例。

如果我们需要在许多枚举类上使用这种“按值查找”功能，我们可以根据方法#2进行一些改进，以构建更符合习惯的解决方案。

首先，让我们创建一个泛型抽象类EnumFinder：

```kotlin
abstract class EnumFinder<V, E>(private val valueMap: Map<V, E>) {
    infix fun from(value: V) = valueMap[value]
}
```

如上面的代码所示，EnumFinder在其主构造函数中需要一个Map对象，**该Map包含V -> E关系，即值 -> 枚举实例关系**。

此外，EnumFinder定义了from()函数以从valueMap中获取相应的实例。因此，**在枚举类中，我们只需要创建一个**[命名的伴随对象](https://www.baeldung.com/kotlin/companion-object#named-companion-object)**并使其**[继承自EnumFinder类](https://www.baeldung.com/kotlin/companion-object#inheritance-and-companion-object)：

```kotlin
enum class NumberV3(val value: Int) {
    ONE(1), TWO(2), THREE(3);

    companion object : EnumFinder<Int, NumberV3>(NumberV3.values().associateBy { it.value })
}
```

在这种方法中，枚举类的伴生对象的任务只是建立映射关系。

接下来，让我们测试这种方法是否正确工作：

```kotlin
val searchOne = NumberV3 from 1
assertEquals(NumberV3.ONE, searchOne)

val searchTwo = NumberV3 from 2
assertEquals(NumberV3.TWO, searchTwo)

val shouldBeNull = NumberV3 from 42
assertNull(shouldBeNull)
```

当我们执行测试时，它通过了。

## 6. 方法#4：为任何具有值的枚举创建一个findBy()函数

到目前为止，我们已经看到了三种解决问题的方法。但是，这三种解决方案都需要向枚举类添加一些函数，如果我们的项目有很多需要这个“findBy”函数的枚举类，我们必须多次重复实现。

在本节中，**我们将创建一个适用于任何具有属性的枚举的通用findBy()函数。此外，不需要对枚举进行任何更改**。

### 6.1 创建findBy()函数

为了实现我们的目标，findBy()函数需要了解以下信息：

-   具体枚举类型
-   给定枚举类型的所有实例
-   每个枚举实例的属性值

如果我们的findBy()函数适用于任何枚举，它应该是一个[泛型](https://www.baeldung.com/kotlin/generics)函数。因此，我们可以为枚举类型定义一个类型参数，所以，第一个要求不是问题。

第二个要求看起来有点困难，因为我们不能调用枚举的values()函数来从类型参数(例如T.values()或Enum<T\>.values())获取所有实例，**Kotlin提供了**[具体化的enumValues](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/enum-values.html)[函数](https://www.baeldung.com/kotlin/reified-functions)**来获取Enum<T\>的所有实例**：

```kotlin
fun <reified T : Enum<T>> enumValues(): Array<T>
```

第三个要求也有点挑战性，这是因为每个枚举可能会以不同的方式命名属性：

```kotlin
enum class NumberV4(val value: Int) {
    ONE(1), TWO(2), THREE(3),
}

enum class OS(val input: String) {
    Linux("linux"), MacOs("mac"),
}
```

如果我们将findBy()函数作为工具函数调用，我们可以通过泛型类型参数解析属性类型，例如findBy<NumberV4, Int>(1)。但是，由于我们不知道它的名字，因此获取它的值并不容易。因此，**我们不能使findBy()成为一个工具函数**。

接下来，让我们先看看findBy()的实现，然后了解它是如何工作的：

```kotlin
infix inline fun <reified E : Enum<E>, V> ((E) -> V).findBy(value: V): E? {
    return enumValues<E>().firstOrNull { this(it) == value }
}
```

正如我们在上面的代码中看到的，E是枚举类型，V是属性类型。简单地说，findBy()是一个函数的函数，**findBy()是函数(E) -> V的**[扩展函数](https://www.baeldung.com/kotlin/extension-methods)，**它是从枚举实例获取其属性值的函数。我们可以使用Kotlin的**[方法引用](https://www.baeldung.com/kotlin/lambda-expressions#4-method-references)**作为(E) -> V函数**，例如NumberV4::value和OS::input。 

当然，(E) -> V函数是可调用的，调用它将获得给定枚举实例的属性值。因此，this(it)将返回给定枚举实例的属性值。这里，this引用了(E) -> V函数，it表示当前的枚举实例。

### 6.2 测试findBy()函数

正如我们之前看到的，NumberV4和OS枚举仅包含常量实例，没有其他函数。接下来，让我们在这两个枚举上测试我们的findBy()函数，看看它是否正常工作：

```kotlin
val searchOne = NumberV4::value findBy 1
assertEquals(NumberV4.ONE, searchOne)
val searchTwo = NumberV4::value findBy 2
assertEquals(NumberV4.TWO, searchTwo)

val shouldBeNull = NumberV4::value findBy 42
assertNull(shouldBeNull)

val linux = OS::input findBy "linux"
assertEquals(OS.Linux, linux)

val windows = OS::input findBy "windows"
assertNull(windows)
```

当我们执行它时，测试通过了。因此，我们的findBy()函数在不更改枚举类的情况下解决了这个问题。

## 7. 总结

在本文中，我们学习了四种通过给定值查找枚举实例的方法。
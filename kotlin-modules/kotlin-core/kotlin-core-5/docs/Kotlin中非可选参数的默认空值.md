## 1. 简介

Kotlin为我们提供了在类构造函数中提供默认值的选项。然而，在某些情况下，此功能无法帮助我们实现所需的目标，这方面的一个例子是当我们想在传递空值时对非可选参数使用默认值时。

在本文中，我们将探讨以轻松的方式解决此问题的不同方法。

## 2. 非可选空参数

首先，让我们探讨一下简介中描述的问题，假设我们有以下实体：

```kotlin
data class Person(val name: String, val age: Int)
```

通常，当我们想要对默认值做出一些决定时，我们可以使用Kotlin内置的默认参数语法来实现：

```kotlin
data class PersonWithDefaults(val name: String = "John", val age: Int = 30)
```

当我们有一些可以为null的传入值时，我们想要在本文中回答的问题就来了。然后，我们需要记住内联正确的默认值：

```kotlin
val nullableName: String? = "John"
val nullableAge: Int? = null

Person(
    name = nullableName ?: "John", // Ouch
    age = nullableAge ?: 0 // Ouch
)
```

这就引出了我们在本文中要回答的问题：**有没有更好的方法让非可选参数在传递null时有一个默认值**？

## 3. 默认空值

由于Kotlin的许多功能，有很多方法可以解决这个问题。

### 3.1 类属性

一种选择是使用类本身的属性：

```kotlin
class PersonClassSolution(nullableName: String?, nullableAge: Int?) {
    val name: String = nullableName ?: "John"
    val age: Int = nullableAge ?: 30
}
```

我们利用了这样一个事实，即我们通过构造函数传递的内容和我们从类中公开的内容可以是两种不同的东西；或者，**可以使用数据类实现相同的目的**：

```kotlin
data class PersonDataClassSolution(private val nullableName: String?, private val nullableAge: Int?) {
    val name: String = nullableName ?: "John"
    val age: Int = nullableAge ?: 30
}
```

通过对构造函数变量使用private，我们强制用户使用我们定义的非空字段。让我们看看我们可以使用哪些其他Kotlin功能来实现相同的结果。

### 3.2 辅助构造函数

Kotlin的[数据类](https://www.baeldung.com/kotlin/data-classes)需要在主构造函数中定义字段，考虑到这个约束，如果我们想要将非空变量作为主要变量，我们需要使用另一种机制来解决这个问题，解决方案之一是使用辅助构造函数：

```kotlin
data class PersonWithAdditionalConstructor(val name: String, val age: Int) {
    constructor(name: String?, age: Int?) : this(name ?: "John", age ?: 0)
}
```

这种方法可以正常工作，但它公开了没有默认值的构造函数。**如果我们不想暴露它，那么我们可以尝试使用Kotlin的getter特性**。

### 3.3 Getter

Kotlin允许自定义[getter和setter](https://www.baeldung.com/kotlin/getters-setters)来调整它们的功能，我们可以利用此功能来获得与上一个示例中的结果非常相似的结果：

```kotlin
data class PersonWithGetter(private val nullableName: String?, private val nullableAge: Int?) {
    val name: String
        get() = nullableName ?: ""

    val age: Int
        get() = nullableAge ?: 0
}
```

感谢getter，我们再次**限制对可空变量的访问并公开不可空变量**，生成的代码看起来与3.1节中描述的“类方式”非常相似。

### 3.4 invoke运算符

我们可以使用的最终解决方案是将主构造函数设为私有并使用运算符重载来实现我们需要的结果，在我们进入代码之前，更好地了解[运算符重载在Kotlin中的工作方式](https://www.baeldung.com/kotlin/operator-overloading#10-invoke)可能会很有用。

让我们看一个利用**invoke运算符**的示例：

```kotlin
data class PersonWithInvoke private constructor(val name: String, val age: Int) {
    companion object {
        operator fun invoke(name: String?, age: Int?) = PersonWithInvoke(name ?: "John", age ?: 0)
    }
}
```

多亏了invoke运算符，我们创建了一个带有空值默认值的干净实体，同时保留了非空值的使用质量。

## 4. 总结

在本文中，我们了解了为非可选参数默认空值的不同方法，我们发现，由于Kotlin的许多功能，有很多可能的方法。
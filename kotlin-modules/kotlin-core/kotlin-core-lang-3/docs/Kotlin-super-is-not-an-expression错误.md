## 1. 简介

在使用Kotlin时，我们经常会遇到以下警告/错误：

```bash
'super' is not an expression, it can only be used on the left-hand side of a dot ('.')
```

尝试调用基类的构造函数时可能发生此错误：

```kotlin
open class Vehicle()
class Car() : Vehicle() {
    init {
        super() // compilation error
    }
}
```

这是调用Java中基类的主构造函数的完全有效的表达式；但是在Kotlin中，这个表达式是无效的。

让我们看看如何在Kotlin中正确调用基类构造函数，稍后，我们还将了解super关键字在Kotlin中的其他用法。

## 2. 调用基类构造函数

有趣的是，可以[在Kotlin中隐式调用](https://www.baeldung.com/kotlin/constructors)主要的基础构造函数，我们根本不需要使用super关键字：

```kotlin
open class Vehicle()
class Car() : Vehicle() {}
```

如我们所见，对Vehicle基类的主构造函数的调用是类头本身的一部分。

这会导致从派生类的构造函数**隐式调用基类构造函数**，这意味着我们不需要使用super关键字来显式调用它。

## 3. Kotlin中的super关键字

尽管为了委托给基类主构造函数不需要super关键字，但它实际上是Kotlin中的有效关键字，它还有多种其他用途，让我们来看看它们。

### 3.1 使用super的辅助构造函数委托

首先，我们可以使用super关键字来委托构造函数调用，具体来说，**从派生类的二级构造函数调用基类构造函数**：

```kotlin
open class Vehicle() {
    constructor(name: String) : this() {}
}
class Motorbike : Vehicle {
    constructor(name: String) : super(name) {}
}
```

请注意，**这仅在某些情况下才有可能**。首先，派生类不能声明主构造函数；其次，基类必须声明一个非默认构造函数(主构造函数或辅助构造函数)。

### 3.2 使用super重写函数调用

此外，我们可以使用super关键字来调用基类中声明的方法：

```kotlin
open class Vehicle() {
    open fun start() {}
}

class Motorbike : Vehicle() {
    override fun start() {
        super.start()
    }
}
```

这在派生类覆盖了基类中的方法的情况下特别有用，我们可以使用super关键字指定该方法的基类版本的调用。

值得注意的是，Kotlin中super关键字的这种特殊行为**与它在Java中的行为非常相似**。

## 4. 总结

在本文中，我们讨论了在Kotlin语言中使用super关键字的各种方式，**我们可以用它来委托构造函数调用**。

此外，**我们可以使用super调用基类中被覆盖的函数**，类似于在Java中的做法。
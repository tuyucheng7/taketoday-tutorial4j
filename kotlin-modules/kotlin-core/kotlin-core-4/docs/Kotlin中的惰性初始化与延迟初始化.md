## 1. 简介

Kotlin中的惰性初始化很容易与延迟初始化混淆。毕竟，在这两种情况下，类字段都没有被初始化，而是在稍后的某个时间被赋予它的实际值。然而，事情真的这么简单吗？

在本文中，我们将仔细研究这两种初始化类型到底是什么，以及它们之间是否存在任何差异。

## 2. 惰性初始化

通常，**Kotlin中的惰性初始化意味着**[委托函数](https://kotlinlang.org/docs/delegated-properties.html)**lazy{}的使用**，有几个开箱即用的委托提供程序函数，lazy就是其中之一。声明委托属性的常用方法是通过以下方式使用by关键字：

```kotlin
val specialValue by SomeDelegate(actualValue = "I am Groot")
```

**编写一个线程安全的惰性初始化是相当困难的**，错误可能代价高昂，尤其是当领域是财务或硬件控制时。**惰性委托允许我们重新使用久经考验的原语并回避所有可能的问题**：

```kotlin
val lazyValue by lazy {
    println("Only now the field is initialized")
    18
}
```

默认的惰性委托是kotlin.SynchronizedLazyImpl，它将采用同步锁，确保在第一次读取期间只有一个实例化。其他所有线程都将等待，直到持有锁的线程完成。在随后的读取中，不会有阻塞。

值得注意的是，**只有不可变的val字段可以使用标准库lazy primitive**。生成的Lazy<T\>委托缺少setter，因此无法更改。

## 3. 延迟初始化

另一方面，延迟初始化是一个特殊的语言关键字：

```kotlin
lateinit var lateValue: ValueType
```

它**只能出现在var修饰符之前**，并且**只能修饰非原始类型的变量**。事后看来，原因很明显，如果它是原始类型且未初始化，则它将具有该类型的默认值。如果它是一个val，那么它以后就不能改变了。

**lateinit关键字只不过是对编译器的承诺**，即此引用肯定会在任何人访问它之前获得一个值，**如果我们违背了这个承诺，代码将抛出UninitializedPropertyAccessException**：

```kotlin
class LateinitSample {
    lateinit var lateValue: ValueType
}

val sample = LateinitSample()
sample.lateValue // This line throws UninitializedPropertyAccessException
```

lateinit变量的正确用法是在访问之前对其进行初始化：

```kotlin
class LateinitSample {
    lateinit var lateValue: ValueType

    fun initBasedOnEnvironment(env: Map<String, String>) {
        lateValue = ValueType(env.toString())
    }
}

val sample = LateinitSample().apply { 
    initBasedOnEnvironment(mapOf("key" to "value"))
}
sample.lateValue // Doesn't throw
```

然而，这使得LateinitSample类的使用变得很麻烦，在极少数情况下，lateinit关键字是合理的，在大多数情况下，**最好避免使用它**。

## 4. 比较

那么，惰性初始化和延迟初始化有什么相似之处吗？

惰性初始化是属性Delegate-s之一，而延迟初始化需要使用语言关键字。惰性初始化仅适用于val，延迟初始化仅适用于var字段。我们可以有一个原始类型的惰性字段，但lateinit仅适用于引用类型。

最重要的是，当我们将一个字段实现为惰性委托时，我们实际上是在给它一个排序值。我们放了一个函数来计算它，而不是一个实际值，当我们需要它的时候。另一方面，当我们将一个字段声明为lateinit时，我们只是关闭了一个编译器检查，以确保程序在接收值之前不访问任何变量。相反，我们承诺自己进行检查。

因此，可以说**惰性初始化与延迟初始化完全不同**。

## 5. 总结

在本教程中，我们并排比较了延迟初始化和惰性初始化，发现它们非常不同。在惰性初始化期间，我们为字段提供了一种方法，以便在我们稍后访问它时获取它的值。当我们使用延迟初始化时，我们会将字段保留为未初始化状态，直到以后使用，这可能会给我们带来麻烦。
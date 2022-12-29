## 1. 简介

Kotlin从其他语言中借鉴了很多想法；其中一个构造是对象。

在这篇简短的文章中，我们将了解对象是什么以及如何使用。

## 2. Kotlin中的对象

在Kotlin中，就像在几乎所有JVM语言中一样，类的概念是面向对象编程模型的核心，**Kotlin在此基础上引入了对象的概念**。

类描述了可以在需要时实例化的结构，并允许根据需要创建尽可能多的实例，**而对象则表示单个静态实例**，并且永远不能超过或少于此一个实例。

这对于各种技术都很有用，包括单例对象和简单的封装函数打包：

```kotlin
object SimpleSingleton {
    val answer = 42
    fun greet(name: String) = "Hello, $name!"
}

assertEquals(42, SimpleSingleton.answer)
assertEquals("Hello, world!", SimpleSingleton.greet("world"))
```

**对象还提供对可见性修饰符的完全支持**，允许像任何其他类一样进行数据隐藏和封装：

```kotlin
object Counter {
    private var count: Int = 0

    fun currentCount() = count

    fun increment() {
        ++count
    }
}
Counter.increment()
println(Counter.currentCount())
println(Counter.count) // this will fail to compile
```

此外，**对象可以扩展类和实现接口**，在这样做时，它们实际上是父类的单例实例，正如预期的那样。

这对于我们有一个无状态实现并且不需要每次都创建一个新实例的情况非常有用，例如Comparator：

```kotlin
object ReverseStringComparator : Comparator<String> {
    override fun compare(o1: String, o2: String) = o1.reversed().compareTo(o2.reversed())
}

val strings = listOf("Hello", "World")
val sortedStrings = strings.sortedWith(ReverseStringComparator)
```

## 3. 什么是伴生对象？

**伴生对象本质上与标准对象定义相同，只是增加了一些使开发更容易的附加功能**。

伴生对象总是在另一个类中声明，**虽然它可以有一个名字，但它不需要有一个**，在这种情况下它会自动具有名称Companion：

```kotlin
class OuterClass {
    companion object { // Equivalent to "companion object Companion"
    }
}
```

**伴生对象允许在不指定名称的情况下从伴生类内部访问其成员**。

同时，当以类名为前缀时，可以从类外部访问可见成员：

```kotlin
class OuterClass {
    companion object {
        private val secret = "You can't see me"
        val public = "You can see me"
    }

    fun getSecretValue() = secret
}

assertEquals("You can see me", OuterClass.public)
assertEquals("You can't see me", OuterClass.secret) // Cannot access 'secret'
```

## 4. 静态字段

**伴生对象的主要用途是替换Java中已知的静态字段/方法**，但是，这些字段不会在生成的类文件中自动生成。

如果我们需要生成它们，我们需要在字段上使用@JvmStatic注解，然后将按预期生成字节码：

```kotlin
class StaticClass {
    companion object {
        @JvmStatic
        val staticField = 42
    }
}
```

如果不这样做，静态字段staticField就无法从Java代码中访问。

**添加此注解会完全按照标准静态字段的需要生成字段，从而在必要时允许与Java实现完全互操作性**。

这意味着上面在StaticClass类上生成了一个静态方法getStaticField()。

## 5. 总结

Kotlin中的对象添加了一个我们可以使用的额外层，进一步简化了我们的代码并使其更易于开发。伴生对象则更进一步，允许更清晰的代码，更易于维护和使用。
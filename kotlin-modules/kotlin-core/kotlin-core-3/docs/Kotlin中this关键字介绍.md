## 1. 简介

在Kotlin中，“this”关键字允许我们引用我们恰好正在运行其函数的类的实例。此外，还有其他方式可以让“this”表达式派上用场。

让我们来看看。

## 2. 访问一个类的成员

我们可以将this用作属性引用或函数调用的前缀：

```kotlin
class Counter {
    var count = 0

    fun incrementCount() {
        this.count += 2
    }
}
```

使用this作为前缀，我们可以引用类的属性，我们可以使用它来解决名称相似的局部变量的歧义。

同样，我们也可以使用this调用成员函数。

## 3. 使用this访问类实例

**我们可以使用独立的this来表示对象的实例**：

```kotlin
class Foo {
    var count = 0

    fun incrementCount() {
        incrementFoo(this)
    }
}

private fun incrementFoo(foo: Foo) {
    foo.count += 2
}

fun main() {
    val foo = Foo()
    foo.incrementCount()
    println("Final count = ${foo.count}")
}
```

在这里，this表示类本身的实例。例如，我们可以将封闭类实例作为参数传递给函数调用。

此外，我们可以使用this将实例分配给局部变量。

## 4. 辅助构造函数的委托

在Kotlin中，辅助构造函数必须[委托](https://www.baeldung.com/kotlin/constructors)**给主构造函数**，我们可以通过this方式进行委派：

```kotlin
class Car(val id: String, val type: String) {
    constructor(id: String) : this(id, "unknown")
}
```

Car的辅助构造函数委托给主构造函数。事实上，我们可以在类体中定义任意数量的额外构造函数。

但是，所有辅助构造函数都必须使用this委托给主构造函数或另一个辅助构造函数。

## 5. 引用外部实例

当单独使用时，this指最里面的封闭范围。但是，如果我们想访问该范围之外的实例怎么办？我们可以通过限定以下内容来做到这一点：

```kotlin
class Outside {
    inner class Inside {
        fun innerInstance() = this
        fun outerInstance() = this@Outside
    }
}
```

这里，this指的是内部类实例。然而，为了引用外部类的实例，我们**用this@Outside限定它**。同样，我们可以从内部扩展函数或带有接收者的函数文字中引用外部实例。

## 6. 总结

总而言之，我们可以在各种情况下使用this。简而言之，它帮助我们处理歧义并使我们能够使我们的代码更有意向性。
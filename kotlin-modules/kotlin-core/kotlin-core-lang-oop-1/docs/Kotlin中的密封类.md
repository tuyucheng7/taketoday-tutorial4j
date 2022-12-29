## 1. 简介

简而言之，Kotlin语言借鉴了其他函数式语言的许多概念，以帮助编写更安全、更具可读性的代码，密封层次结构是这些概念之一。

## 2. 什么是密封类？

**密封类允许我们修复类型层次结构并禁止开发人员创建新的子类**。

当我们有一个非常严格的继承层次结构时，它们很有用，只有一组特定的可能的子类，而没有其他子类。[从Kotlin 1.5开始](https://kotlinlang.org/docs/whatsnew15.html#package-wide-sealed-class-hierarchies)，**密封类可以在同一编译单元和同一包的所有文件中拥有子类**。

密封类也是隐式抽象的，在我们的代码的其余部分中，它们应该被这样对待，除了没有其他东西能够实现它们。

密封类可以在其中定义字段和方法，包括抽象函数和实现函数。这意味着我们可以有一个类的基本表示，然后对其进行调整以适应子类。

### 2.1 密封接口

[从Kotlin 1.5](https://kotlinlang.org/docs/whatsnew15.html#sealed-interfaces)**开始，接口也可以具有sealed修饰符**，它作用于接口的方式与作用于类的方式相同：密封接口的所有实现都应该在编译时已知。

与密封类相比，密封接口的优点之一是能够从多个密封接口继承，这对于密封类来说是不可能的，因为Kotlin中缺少多重继承。

## 3. 什么时候使用密封类？

**密封类的设计目的是在存在一组非常具体的值可能选项时使用**，并且这些选项中的每一个在功能上都是不同的-只是[代数数据类型](https://en.wikipedia.org/wiki/Algebraic_data_type)。

常见的用例可能包括实现[状态机](https://en.wikipedia.org/wiki/Finite-state_machine)或在[Monadic编程](https://en.wikipedia.org/wiki/Monad_(functional_programming))中，随着函数式编程概念的出现，它变得越来越流行。

任何时候我们有多个选项并且它们仅在数据的含义上有所不同，我们最好使用[枚举类](https://kotlinlang.org/docs/reference/enum-classes.html)来代替。

任何时候我们有未知数量的选项时，我们都不能使用密封类，因为这会阻止我们在原始源文件之外添加选项。

## 4. 编写密封类

让我们从编写我们自己的密封类开始，这种密封层次结构的一个很好的例子是Java 8中的Optional-它可以是Some或None。

在实现这一点时，限制创建新实现的可能性很有意义，提供的两个实现是详尽无遗的，没有人应该添加自己的实现。

因此，我们可以实现这一点：

```kotlin
sealed class Optional<out V> {
    // ...
    abstract fun isPresent(): Boolean
}

data class Some<out V>(val value: V) : Optional<V>() {
    // ...
    override fun isPresent(): Boolean = true
}

class None<out V> : Optional<V>() {
    // ...
    override fun isPresent(): Boolean = false
}
```

现在可以保证，任何时候我们有一个Optional<V\>的实例，我们实际上有一个Some<V\>或一个None<V\>。

**在Java 8中，由于没有密封类，实际实现看起来有所不同**。

然后我们可以在计算中使用它：

```kotlin
val result: Optional<String> = divide(1, 0)
println(result.isPresent())
if (result is Some) {
    println(result.value)
}
```

第一行将返回Some或None，然后我们输出是否得到结果。

## 5. 与when使用

Kotlin支持在其when构造中使用密封类，因为总是存在一组确切的可能子类，所以**如果任何分支未被处理，编译器能够以与枚举完全相同的方式警告我们**。

这意味着在这种情况下，通常不需要一个包罗万象的处理程序，这反过来意味着添加一个新的子类是自动安全的-如果我们没有处理它，编译器会立即警告我们，我们将需要在继续之前修复此类错误。

上面的示例可以扩展为根据返回的类型输出错误结果：

```kotlin
val message = when (result) {
    is Some -> "Answer: ${result.value}"
    is None -> "No result"
}
println(message)
```

如果缺少两个分支中的任何一个，则不会编译，而是导致以下错误：

```bash
'when' expression must be exhaustive, add necessary 'else' branch
```

## 6. 总结

密封类可以成为我们API设计工具箱的宝贵工具，允许一个众所周知的、结构化的类层次结构，它只能是一组预期的类中的一个，这有助于从我们的代码中消除一整套潜在的错误条件，同时仍然使事情易于阅读和维护。
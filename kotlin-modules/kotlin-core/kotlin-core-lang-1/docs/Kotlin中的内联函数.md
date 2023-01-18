## 1. 概述

在Kotlin中，函数是[一等公民](https://en.wikipedia.org/wiki/First-class_function)，因此我们可以像其他普通类型一样传递函数或返回它们。但是，这些函数在运行时的表示形式有时可能会导致一些限制或性能复杂化。

在本教程中，首先我们将列举关于lambda和泛型的两个看似无关的问题，然后在介绍内联函数之后，我们将了解它们如何解决这两个问题。

## 2. 天堂里的麻烦

### 2.1 Kotlin中Lambda的开销

在Kotlin中，函数作为一等公民的好处之一是我们可以将一段行为传递给其他函数，将函数作为lambda传递可以让我们以更简洁优雅的方式表达我们的意图，但这只是故事的一部分。

为了探索lambda的阴暗面，让我们通过声明一个扩展函数来过滤集合来重新发明轮子：

```kotlin
fun <T> Collection<T>.filter(predicate: (T) -> Boolean): Collection<T> = // Omitted
```

现在，让我们看看上面的函数是如何编译成Java的，密切关注作为参数传递的predicate函数：

```java
public static final <T> Collection<T> filter(Collection<T>, kotlin.jvm.functions.Function1<T, Boolean>);
```

注意到了如何使用Function1接口处理谓词了吗？

现在，如果我们在Kotlin中调用它：

```kotlin
sampleCollection.filter { it == 1 }
```

将生成类似于以下内容的内容来包装lambda代码：

```java
filter(sampleCollection, new Function1<Integer, Boolean>() {
    @Override
    public Boolean invoke(Integer param) {
        return param == 1;
    }
});
```

**每次我们声明一个**[高阶函数](https://kotlinlang.org/docs/reference/lambdas.html#higher-order-functions)**时，都会创建这些特殊Function*类型的至少一个实例**。

为什么Kotlin这样做，而不是像Java 8那样使用[invokedynamic来处理lambdas](http://wiki.jvmlangsummit.com/images/1/1e/2011_Goetz_Lambda.pdf)？**简单来说，Kotlin是为了兼容Java 6，而invokedynamic直到Java 7才可用**。

但这还没有结束，正如我们可能猜到的那样，仅仅创建一个类型的实例是不够的。

为了实际执行封装在Kotlin lambda中的操作，高阶函数(在本例中为filter)将需要在新实例上调用名为invoke的特殊方法，**结果是由于额外的调用而导致的开销增加**。

所以，回顾一下，当我们将lambda传递给函数时，底层会发生以下情况：

1.  至少创建一个特殊类型的实例并将其存储在堆中
2.  总是会发生额外的方法调用

**多一次实例分配和多一次虚拟方法调用似乎并没有那么糟糕，对吧**？

### 2.2 闭包

正如我们之前看到的，当我们将lambda传递给函数时，将创建一个Function类型的实例，类似于Java中的匿名内部类。

就像后者一样，**lambda表达式可以访问其闭包，即在外部作用域中声明的变量**。当lambda从其闭包中捕获变量时，Kotlin会将该变量与捕获的lambda代码一起存储。

**当lambda捕获变量时，额外的内存分配会变得更糟：JVM在每次调用时都会创建一个Function类型实例**。对于非捕获lambda，这些Function类型只有一个实例，即单例。

我们怎么这么确定呢？让我们通过声明一个函数来重新发明另一个轮子以在每个集合元素上应用一个函数：

```kotlin
fun <T> Collection<T>.each(block: (T) -> Unit) {
    for (e in this) block(e)
}
```

尽管听起来很愚蠢，但在这里我们将每个集合元素乘以一个随机数：

```kotlin
fun main() {
    val numbers = listOf(1, 2, 3, 4, 5)
    val random = random()

    numbers.each { println(random * it) } // capturing the random variable
}
```

如果我们使用javap查看字节码：

```bash
>> javap -c MainKt
public final class MainKt {
  public static final void main();
    Code:
      // Omitted
      51: new           #29                 // class MainKt$main$1
      54: dup
      55: fload_1
      56: invokespecial #33                 // Method MainKt$main$1."<init>":(F)V
      59: checkcast     #35                 // class kotlin/jvm/functions/Function1
      62: invokestatic  #41                 // Method CollectionsKt.each:(Ljava/util/Collection;Lkotlin/jvm/functions/Function1;)V
      65: return
```

然后我们可以从索引51中发现JVM为每次调用创建一个新的MainKt$main$1内部类实例。此外，索引56显示了Kotlin如何捕获random变量，**这意味着每个捕获的变量都将作为构造函数参数传递，从而产生内存开销**。

### 2.3 类型擦除

当谈到JVM上的泛型时，它从来就不是天堂！无论如何，Kotlin会在运行时擦除泛型类型信息。也就是说，**泛型类的实例在运行时不保留其类型参数**。

例如，当声明一些像List<Int\>或List<String\>这样的集合时，我们在运行时拥有的只是原始List。正如所承诺的那样，这似乎与前面的问题无关，但我们将看到内联函数如何成为这两个问题的通用解决方案。

## 3. 内联函数

### 3.1 消除Lambda的开销

使用lambda时，额外的内存分配和额外的虚拟方法调用会引入一些运行时开销。因此，如果我们直接执行相同的代码，而不是使用lambda，我们的实现会更有效率。

**我们必须在抽象和效率之间做出选择吗**？

**事实证明，有了Kotlin中的内联函数，我们可以同时拥有这两者**！我们可以编写漂亮而优雅的lambda，**编译器会为我们生成内联和直接代码**，我们所要做的就是在其上放置一个inline：

```kotlin
inline fun <T> Collection<T>.each(block: (T) -> Unit) {
    for (e in this) block(e)
}
```

**使用内联函数时，编译器会内联函数体。也就是说，它将函数体直接替换到函数被调用的地方**。默认情况下，编译器会内联函数本身和传递给它的lambda的代码。

例如，编译器翻译：

```kotlin
val numbers = listOf(1, 2, 3, 4, 5)
numbers.each { println(it) }
```

类似于：

```kotlin
val numbers = listOf(1, 2, 3, 4, 5)
for (number in numbers)
    println(number)
```

**使用内联函数时，没有额外的对象分配，也没有额外的虚拟方法调用**。

但是，我们不应该过度使用内联函数，尤其是对于长函数，因为内联可能会导致生成的代码增长很多。

### 3.2 非内联

默认情况下，传递给内联函数的所有lambda也将是内联的。但是，我们可以使用**noinline**关键字标记一些lambda，以将它们从内联中排除：

```kotlin
inline fun foo(inlined: () -> Unit, noinline notInlined: () -> Unit) { ... }
```

### 3.3 内联具体化

正如我们之前看到的，Kotlin在运行时会擦除泛型类型信息，但对于内联函数，我们可以避免这种限制。**也就是说，编译器可以具体化内联函数的泛型类型信息**。

我们所要做的就是用reified关键字标记类型参数：

```kotlin
inline fun <reified T> Any.isA(): Boolean = this is T
```

如果没有inline和reified，isA函数将无法编译，正如我们在[Kotlin泛型](https://www.baeldung.com/kotlin-generics)文章中详尽解释的那样。

### 3.4 非局部return

在Kotlin中，**我们只能使用return表达式(也称为非限定的return)从命名函数或匿名函数中退出**：

```kotlin
fun namedFunction(): Int {
    return 42
}

fun anonymous(): () -> Int {
    // anonymous function
    return fun(): Int {
        return 42
    }
}
```

在这两个示例中，return表达式都是有效的，因为函数是命名的或匿名的。

但是，**我们不能使用非限定的return表达式从lambda表达式中退出**，为了更好地理解这一点，让我们重新发明另一个轮子：

```kotlin
fun <T> List<T>.eachIndexed(f: (Int, T) -> Unit) {
    for (i in indices) {
        f(i, this[i])
    }
}
```

此函数对每个元素执行给定的代码块(函数f)，为元素提供顺序索引。让我们使用这个函数来编写另一个函数：

```kotlin
fun <T> List<T>.indexOf(x: T): Int {
    eachIndexed { index, value ->
        if (value == x) {
            return index
        }
    }

    return -1
}
```

该函数应该在接收集合中搜索给定元素并返回找到的元素的索引或-1，然而，**由于我们无法从带有非限定return表达式的lambda中退出，该函数甚至无法编译**：

```bash
Kotlin: 'return' is not allowed here
```

**作为此限制的解决方法，我们可以内联eachIndexed函数**：

```kotlin
inline fun <T> List<T>.eachIndexed(f: (Int, T) -> Unit) {
    for (i in indices) {
        f(i, this[i])
    }
}
```

然后我们就可以实际使用indexOf函数了：

```kotlin
val found = numbers.indexOf(5)
```

内联函数仅仅是源代码的产物，不会在运行时表现出来。因此，**从内联lambda返回等同于从封闭函数返回**。 

## 4. 限制

通常，**只有在直接调用lambda或将lambda传递给另一个内联函数时，我们才能使用lambda参数内联函数**。否则，编译器会阻止内联并出现编译器错误。

例如，让我们看一下Kotlin标准库中的[replace函数](https://github.com/JetBrains/kotlin/blob/master/libraries/stdlib/src/kotlin/text/Strings.kt#L690)：

```kotlin
inline fun CharSequence.replace(regex: Regex, noinline transform: (MatchResult) -> CharSequence): String =
    regex.replace(this, transform) // passing to a normal function
```

上面的代码片段将lambda transform传递给一个普通函数replace，因此是noinline。

## 5. 总结

在本文中，我们深入探讨了Kotlin中的lambda性能和类型擦除问题。然后，在引入内联函数之后，我们看到了它们如何解决这两个问题。

然而，我们应该尽量不要过度使用这些类型的函数，尤其是当函数体太大时，因为生成的字节码大小可能会增加，并且我们也可能会在此过程中失去一些JVM优化。
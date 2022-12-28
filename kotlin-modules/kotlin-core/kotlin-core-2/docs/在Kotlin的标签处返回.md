## 1. 概述

在Kotlin中，函数是[一等公民](https://en.wikipedia.org/wiki/First-class_citizen)，可以将局部函数或函数文本声明为[lambda表达式](https://www.baeldung.com/kotlin-lambda-expressions)的一部分。因此，在许多情况下，我们最终可能会在其他函数中使用函数。

在这个快速教程中，我们将了解如何从此类嵌套函数结构中的特定函数返回。

## 2. 返回标签

**默认情况下，Kotlin中的return表达式从最近的封闭函数返回**，例如：

```kotlin
fun <T> List<T>.findOne(x: T): Int {
    forEachIndexed { i, v ->
        if (v == x) {
            return i
        }
    }

    return -1
}
```

在上面的例子中，return i表达式将返回给findOne函数的调用者。

然而，有时我们可能需要从lambda表达式返回，而不是封闭函数返回。为此，**我们可以使用return@label语法**。默认情况下，标签是接收lambda的函数的名称-在本例中为forEachIndexed：

```kotlin
fun <T> List<T>.printIndexOf(x: T) {
    forEachIndexed { i, v ->
        if (v == x) {
            print("Found $v at $i")
            return@forEachIndexed // return out of the lambda
        }
    }
}
```

**这种方法被称为合格返回、返回标签，甚至是return@**。“return@forEachIndexed”不会返回到printIndexOf()调用者，而是只会从lambda表达式返回并继续执行封闭函数。

也可以使用自定义标签而不是函数名称：

```kotlin
fun <T> List<T>.printIndexOf2(x: T) {
    forEachIndexed loop@{ i, v ->
        if (v == x) {
            print("Found $v at $i")
            return@loop
        }
    }
}
```

如上所示，我们将默认标签重命名为loop。为此，**我们必须在lambda表达式的左括号前放置一个label@**。

有趣的是，即使我们有多层嵌套的lambda函数，这种方法也能奏效：

```kotlin
fun <T> List<List<T>>.nestedFind(x: T) {
    forEach {
        it.forEachIndexed { i, v ->
            if (v == x) {
                println("Found $v at $i")
                return@forEach
            }
        }
    }
}
```

如上所示，return@forEach将从外部forEach函数返回。

最后一点，可以通过将lambda表达式替换为[匿名函数](https://kotlinlang.org/docs/reference/lambdas.html#anonymous-functions)来使用标准返回表达式：

```kotlin
fun <T> List<T>.printIndexOfAnonymous(x: T) {
    forEachIndexed(fun(i: Int, v: T) {
        if (v == x) {
            print("Found $v at $i")
            return
        }
    })
}
```

这样，最近的封闭函数将是匿名函数，因此不需要限定的return。

## 3. 总结

在这个简短的教程中，我们了解了如何在Kotlin中控制嵌套函数结构中的return表达式行为。
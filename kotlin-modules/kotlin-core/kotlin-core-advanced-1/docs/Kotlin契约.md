## 1. 概述

在本教程中，我们将讨论[Kotlin契约](https://kotlinlang.org/docs/reference/whatsnew13.html#contracts)，它们的语法还不稳定，但二进制实现是稳定的，并且Kotlin stdlib本身已经在使用它们。

**基本上，Kotlin契约是一种通知编译器函数行为的方法**。

## 2. Maven设置

这个特性是在Kotlin 1.3中引入的，所以我们需要使用这个版本或者更新的版本，对于本教程，我们将使用可用的[最新版本](https://search.maven.org/search?q=a:kotlin-stdlib)–1.8.0。

有关设置的更多详细信息，请参阅我们对[Kotlin的介绍](https://www.baeldung.com/kotlin)。

## 3. 契约动机

**尽管编译器很聪明，但它并不总是能得出最好的总结**。

考虑下面的例子：

```kotlin
data class Request(val arg: String)

class Service {

    fun process(request: Request?) {
        validate(request)
        println(request.arg) // Doesn't compile because request might be null
    }
}

private fun validate(request: Request?) {
    if (request == null) {
        throw IllegalArgumentException("Undefined request")
    }
    if (request.arg.isBlank()) {
        throw IllegalArgumentException("No argument is provided")
    }
}
```

任何程序员都可以阅读这段代码，并且知道如果对validate的调用没有抛出异常，则请求不为空。**换句话说，我们的println指令不可能抛出NullPointerException**。

不幸的是，编译器并不知道这一点，也不允许我们引用request.arg。

然而，我们可以通过一个契约来增强验证，它定义了如果函数成功返回(也就是说，它没有抛出异常)那么给定的参数就不是null：

```kotlin
@ExperimentalContracts
class Service {

    fun process(request: Request?) {
        validate(request)
        println(request.arg) // Compiles fine now
    }
}

@ExperimentalContracts
private fun validate(request: Request?) {
    contract {
        returns() implies (request != null)
    }
    if (request == null) {
        throw IllegalArgumentException("Undefined request")
    }
    if (request.arg.isBlank()) {
        throw IllegalArgumentException("No argument is provided")
    }
}
```

接下来，让我们更详细地了解一下此功能。

## 4. 契约API

一般契约形式为：

```kotlin
function {
    contract {
        Effect
    }
}
```

**我们可以将其理解为“调用函数产生效果”**。

在接下来的部分中，让我们看看该语言现在支持的效果类型。

### 4.1 根据返回值做出保证

**这里我们指定如果目标函数返回，则满足目标条件，我们在动机部分使用了它**。

我们还可以在返回值中指定一个值，这将指示Kotlin编译器只有在返回目标值时才满足条件：

```kotlin
data class MyEvent(val message: String)

@ExperimentalContracts
fun processEvent(event: Any?) {
    if (isInterested(event)) {
        println(event.message) 
    }
}

@ExperimentalContracts
fun isInterested(event: Any?): Boolean {
    contract { 
        returns(true) implies (event is MyEvent)
    }
    return event is MyEvent
}
```

这有助于编译器在processEvent函数中进行智能转换。

**请注意，目前，返回契约仅允许implies右侧的true、false和null**。 

即使implies采用布尔参数，也只接收有效Kotlin表达式的子集：即空值检查(== null、!= null)、实例检查(is、!is)、逻辑运算符(&&、||、！)。

还有一个针对任何非null返回值的变体：

```kotlin
contract {
    returnsNotNull() implies (event is MyEvent)
}
```

### 4.2 保证函数的使用

callsInPlace契约表达了以下保证：

-   所有者函数完成后不会调用可调用对象
-   没有契约它也不会被传递给另一个函数

这有助于我们解决以下情况：

```kotlin
inline fun <R> myRun(block: () -> R): R {
    return block()
}

fun callsInPlace() {
    val i: Int
    myRun {
        i = 1 // Is forbidden due to possible re-assignment
    }
    println(i) // Is forbidden because the variable might be uninitialized
}
```

**我们可以通过帮助编译器确保给定的块保证被调用并且只调用一次来修复错误**：

```kotlin
@ExperimentalContracts
inline fun <R> myRun(block: () -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block()
}
```

标准的Kotlin实用函数run、with、apply等已经定义了这样的契约。

这里我们使用了InvocationKind.EXACTLY_ONCE，**其他选项是AT_LEAST_ONCE、AT_MOST_ONCE和UNKNOWN**。

## 5. 契约的限制

**虽然Kotlin契约看起来很有前途，但目前的语法不稳定，未来可能会完全改变**。

此外，它们有一些限制：

-   我们只能在有主体的顶级函数上应用契约，即我们不能在字段和类函数上使用它们。
-   契约调用必须是函数体中的第一条语句。
-   编译器无条件地信任契约；这意味着程序员有责任编写正确和合理的契约，**未来的版本可能会实现验证**。

最后，契约描述只允许引用参数；例如，下面的代码无法编译：

```kotlin
data class Request(val arg: String?)

@ExperimentalContracts
private fun validate(request: Request?) {
    contract {
        // We can't reference request.arg here
        returns() implies (request != null && request.arg != null)
    }
    if (request == null) {
        throw IllegalArgumentException("Undefined request")
    }
    if (request.arg.isBlank()) {
        throw IllegalArgumentException("No argument is provided")
    }
}
```

## 6. 总结

该功能看起来相当有趣，即使它的语法处于原型阶段，二进制表示也足够稳定并且已经是stdlib的一部分，如果没有优雅的迁移周期，它就不会改变，这意味着我们可以依赖带有契约的二进制工件(例如stdlib)来获得所有通常的兼容性保证。

**这就是为什么我们的建议是即使现在也值得使用契约**，如果他们的DSL发生变化，更改契约声明不会太难。
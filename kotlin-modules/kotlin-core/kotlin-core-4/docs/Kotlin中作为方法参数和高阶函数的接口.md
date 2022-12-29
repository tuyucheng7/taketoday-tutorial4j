## 1. 简介

Kotlin努力成为一种函数式语言，或者更确切地说，是一种允许函数式范式与命令式范式并存的语言。函数式范式规定我们应该将程序视为函数的马赛克，接收参数并产生结果。拥有函数类型和函数变量是范式的重要组成部分，当然，Kotlin[也支持它](https://www.baeldung.com/kotlin/sam-conversions)。

在本教程中，我们将讨论接口作为Kotlin中的方法参数和高阶函数。

## 2. 声明和调用高阶函数

在Kotlin中声明高阶函数非常简单，**最明显的方法是接收lambda**：

```kotlin
fun performAction(action: () -> Unit) {
    // Do things before the action
    action()
    // Do things after the action
}

// Invocation: 
performAction { println("Hello, world!") }
```

这个例子不是很实用，因为唯一可以作为参数传递的函数类型是不接收参数且不产生任何结果的类型，这几乎将它缩小到各种副作用。

让我们通过返回一个特定的结果让它变得更有趣，**如果我们在多个高阶函数中使用相同的参数类型，那么键入lambda的完整类型定义也非常乏味。相反，我们将使用类型别名来缩短内容**：

```kotlin
typealias MySpecialSupplier = () -> SuppliedType

fun supplyWithTiming(supplier: MySpecialSupplier) =
    measureTimedValue { supplier() }
        .let {
            println("Invocation took ${it.duration}ms") // Or there can be a Metrics call here
            it.value // Returning the actual result
        }
```

最后，当有许多潜在的实现时，我们可能会考虑声明一个函数接口。或者，高阶函数的作者和用户可能是不同的人群，例如库的作者和库的用户。然后我们声明一个接口，只有一个函数和前面的fun关键字：

```kotlin
fun interface Mapper {
    fun toSupply(name: String, weight: Int): SuppliedType
}

fun verifiedSupplier(name: String, weight: Int, mapper: Mapper): SuppliedType {
    println("Some verification")
    return mapper.toSupply(name, weight)
}

// Possible invocation:
verifiedSupplier("Sugar", 1) { name, weight -> SuppliedType(name, weight) }
```

**如你所见，由于Kotlin SAM转换，我们仍然能够传递一个简单的lambda作为参数**。

当然，有时我们的高阶函数足够通用，不需要关心lambda参数或它们的返回类型。在这种情况下，我们会使用泛型类型，就像在这个Kotlin标准库函数中一样：

```kotlin
public inline fun <T, R, C : MutableCollection<in R>> Iterable<T>
        .mapTo(destination: C, transform: (T) -> R): C {
    for (item in this)
        destination.add(transform(item))
    return destination
}
```

**我们还可以进行柯里化或部分应用**：

```kotlin
fun <T> curry(a: T, bifunction: (T, T) -> T): (T) -> T = { b -> bifunction(a, b) }

val plusFour = curry(4) { a, b -> a + b }
assert(plusFour(2) == 6)
```

当调用函数的代码不知道它的所有参数来自何处时，这可以用于更精细地分离关注点。

## 3. 高阶函数的应用

有时我们出于技术或基础设施原因不得不中断业务逻辑，例如指标检测、日志记录或资源使用。生成的代码可读性不是很好，它也更脆弱，因为我们可能会在更改代码的非业务方面时不小心更改业务逻辑。

让我们考虑一段代码，该代码必须在超时时间内从服务器下载图片并处理可能出现的异常：

```kotlin
val imageResponse = try {
    clientExecutor.submit(Callable { client.get(imageUrl) })
        .get(10L, TimeUnit.SECONDS)
} catch (ex: Exception) {
    logger.error("Failed to get image: $imageUrl")
    return
}
```

发生了太多事情！我们将任务提交给执行程序，以便能够对其设置超时。我们捕获可能的异常并进行日志记录。此外，此超时代码可能会在我们的整个服务中重复出现，**我们必须在这段代码中识别出更小的模式并将它们分开**。

让我们从梳理超时函数开始：

```kotlin
fun <T> timeout(timeoutSec: Long, supplier: Callable<T>): T =
    clientExecutor.submit(supplier).get(timeoutSec, TimeUnit.SECONDS)
```

现在，如果我们想在任务上设置超时，我们可以使用这个方法。接下来，让我们处理异常日志记录方面：

```kotlin
fun <T> successOrLogException(exceptionMsg: String, supplier: () -> T): T? = try {
    supplier()
} catch (ex: Exception) {
    logger.error(exceptionMsg)
    null
}
```

诚然，在函数式编程中，有更优雅的方法来处理错误的输出，但这只是为了说明使用高阶函数进行关注分离的概念。让我们将创建的两个函数应用到代码中：

```kotlin
val imageResponse = successOrLogException("Failed to get image: $imageUrl") {
    timeout(10L) { client.get(imageUrl) }
} ?: return
```

逻辑是相同的，但现在我们只是在陈述我们的意图，而不是煞费苦心地列出实现这些目标所需的所有基本步骤。

## 4. 总结

在本文中，我们研究了如何创建将函数作为参数并将其作为结果返回的函数，这些函数对于将代码编写为一系列声明性语句而不是基本指令列表很有用。声明性语句揭示了代码背后的原因，这种方法有助于使代码更具可读性。
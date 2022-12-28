## 1. 概述

调用具有大量参数的函数可能会导致代码难以阅读或输入被意外切换的错误，当我们增加一个函数的可选输入的数量时，通过函数重载提供默认值也会变得很麻烦。

我们可以通过使用Kotlin的两个语言特性来解决所有这些问题。

在本教程中，**我们将讨论Kotlin对命名参数和默认参数的语言支持**。

## 2. 位置参数

在我们研究Kotlin的命名参数之前，让我们提醒自己注意位置参数的挑战。

我们一直使用位置参数，**位置参数是必须按声明顺序传递的方法参数**，它们有点像具有严格类型的严格顺序的固定大小输入数组。

### 2.1 位置参数的问题

当调用具有多个位置参数的函数时，调用代码并不总是清楚地解释哪个参数是哪个。

让我们看一个例子：

```kotlin
fun resizePane(newSize: Int, forceResize: Boolean, noAnimation: Boolean) {
    println("The parameters are newSize = $newSize, forceResize = $forceResize, noAnimation = $noAnimation")
}
```

我们将使用位置参数来调用它：

```java
resizePane(10, true, false)
```

在没有IDE支持或不查看resizePane函数声明的情况下，很难理解resizePane调用中的参数代表什么。我们可能会认为数值是一个大小，但是这两个布尔值可能意味着任何东西，而且我们很容易将它们的顺序弄错。

随着参数数量的增加，或者如果我们尝试允许输入空值，问题会变得更糟。而Kotlin的命名参数为这个问题提供了一个巧妙的解决方案。

## 3. Kotlin的命名参数

在调用Kotlin函数时，**我们可以命名一个或多个参数**，用于参数的名称必须与函数声明中指定的参数名称相匹配。

### 3.1 使用命名参数调用

让我们使用命名参数重写resizePane的调用：

```kotlin
resizePane(newSize = 10, forceResize = true, noAnimation = false)
```

这个版本的代码更加自文档化，并且更易于仔细检查。

### 3.2 命名参数顺序

正如我们之前看到的，位置参数的顺序不能改变，那么命名参数的顺序呢？

Kotlin的**命名参数可以按任何顺序传递**，所以让我们交换两个布尔值：

```kotlin
resizePane(newSize = 11, noAnimation = false, forceResize = true)
```

由于所有参数都已命名，我们可以按照我们喜欢的任何顺序传递它们：

```kotlin
resizePane(forceResize = true, newSize = 12, noAnimation = false)
```

### 3.3 混合命名参数和位置参数

尽管命名参数具有可读性优势，但它们也会向代码中添加更多文本。为了简洁起见，能够使用位置参数是很有用的，但为了清楚起见，混合使用命名参数会有所帮助。

幸运的是，我们可以在一次调用中混合使用命名参数和位置参数。但是，我们应该注意，在Kotlin 1.3中，所有位置参数都必须放在命名参数之前，而Kotlin 1.4不再施加此限制。

**在Kotlin 1.4中，我们可以混合使用命名参数和位置参数，前提是要保持顺序**。

接下来我们尝试这种语法，Kotlin 1.3将允许我们仅在位置参数之后命名参数：

```kotlin
resizePane(20, true, noAnimation = false)
```

在更新的版本中，我们可以在命名参数的中间使用位置参数：

```kotlin
resizePane(newSize = 20, true, noAnimation = false)
```

同样，我们可以只传递最后一个参数作为位置参数：

```kotlin
resizePane(newSize = 30, forceResize = true, false)
```

另一方面，我们可以在位置参数中间使用命名参数：

```kotlin
resizePane(40, forceResize = true, false)
```

这些中的每一个都具有相同的效果。

当命名参数与默认值结合使用时，命名参数的功能要强大得多，它允许我们只指定我们希望的输入，而不管它们在函数定义中的什么位置。

## 4. 默认参数

有时，函数可能希望将其某些参数视为可选参数，并在未提供值时为它们假定默认值。许多语言(包括Java)不支持此功能，在这种情况下，我们必须编写代码，在未提供值的情况下为参数分配默认值。

此外，我们可以使用[函数重载](https://en.wikipedia.org/wiki/Function_overloading)来提供方法的不同版本，其中每个版本都允许调用者跳过一个或多个可选参数。

然而，随着参数数量的增加，方法重载会迅速失控，导致更难以识别参数的含义。

**Kotlin为默认参数提供语言支持**，因此，可以减少函数重载。

### 4.1 Kotlin中的默认参数

默认参数值在函数声明中的参数类型后使用=符号指定：

```kotlin
fun connect(url: String, connectTimeout: Int = 1000, enableRetry: Boolean = true) {
    println("The parameters are url = $url, connectTimeout = $connectTimeout, enableRetry = $enableRetry")
}
```

在这里，我们为connectTimeout和enableRetry参数指定了默认值。

接下来，让我们看看如何使用默认参数调用函数。

### 4.2 使用默认参数调用函数

由于connectTimeout和enableRetry参数是使用默认值声明的，因此我们可以在函数调用中排除它们：

```kotlin
connect("http://www.tuyucheng.com")
```

这将对两个跳过的参数都使用默认值。

接下来，让我们只跳过enableRetry参数：

```kotlin
connect("http://www.tuyucheng.com", 5000)
```

这将使用enableRetry参数的默认值。

### 4.3 跳过中间参数

接下来，让我们只跳过中间参数connectTimeout：

```kotlin
connect("http://www.tuyucheng.com", false)
```

我们得到一个编译错误：

```shell
The boolean literal does not conform to the expected type Int
```

什么地方出了错？我们跳过了第二个参数connectTimeout并在其位置传递了布尔参数enableRetry。但是，编译器期望Int值作为第二个参数，**一旦跳过具有默认值的参数，所有后续参数都必须作为命名参数传递**。

因为我们跳过了connectTimeout参数，所以我们必须将enableRetry作为命名参数传递：

```kotlin
connect("http://www.tuyucheng.com", enableRetry = false)
```

这就可以修复错误。这是命名参数如何补充默认参数的一个很好的例子，如果没有命名参数，我们无法使用位置参数获得默认值的好处。

## 5. 覆盖函数和默认参数

到目前为止，我们已经了解了普通函数中的默认参数，现在让我们了解重写函数上下文中的默认参数。

### 5.1 继承默认参数值

**重写使用在基类函数中声明的默认参数值**。

让我们通过声明一个名为AbstractConnector的抽象类来尝试一下：

```kotlin
open class AbstractConnector {
    open fun connect(url: String = "localhost") {
        // function implementation 
    }
}
```

AbstractConnector类中的connect函数有一个带有默认值的url参数。

接下来，让我们扩展这个类并重写connect函数：

```kotlin
class RealConnector : AbstractConnector() {
    override fun connect(url: String) {
        println("The parameter is url = $url")
    }
}
```

正如我们所料，我们可以通过传入url参数的值来调用重写的connect方法：

```kotlin
val realConnector = RealConnector()
realConnector.connect("www.tuyucheng.com")
```

但是，重写的connect函数中的url参数也使用基类函数中声明的默认值。

因此，我们可以在没有url参数的情况下调用重写的connect：

```kotlin
realConnector.connect()
```

这可以用[里氏替换原则](https://www.baeldung.com/cs/liskov-substitution-principle)来理解，所有被重写的函数都应该被认为是从声明它们的超类型中调用的。那么，如果我们想更改默认值怎么办？

### 5.2 覆盖默认值

让我们尝试为子类RealConnector中的url参数指定一个默认值：

```kotlin
class RealConnector : AbstractConnector() {
    override fun connect(url: String = "www.tuyucheng.com") { // Compiler Error 
        // function implementation
    }
}
```

这是不允许的，并会导致编译错误，**重写函数无法为其参数指定默认值**，Kotlin要求默认值只能在基类函数中指定。

## 6. 总结

在本文中，我们了解了Kotlin对命名参数和默认参数值的支持。首先，我们研究了位置参数是如何难以阅读的，然后我们研究了Kotlin对命名参数的原生支持，以及这如何帮助提高大量参数的可读性。

最后，我们看到Kotlin对默认参数的支持使开发人员无需编写代码来实现默认值，并有助于避免过多的函数重载。
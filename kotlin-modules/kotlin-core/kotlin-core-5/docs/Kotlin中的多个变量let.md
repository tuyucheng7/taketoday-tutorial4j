## 1. 概述

在Kotlin中，[let](https://www.baeldung.com/kotlin/scope-functions#let)()是一个非常方便的[作用域函数](https://www.baeldung.com/kotlin/scope-functions)，它允许我们将给定变量转换为另一种类型的值。

在本教程中，我们将探讨如何对多个变量应用类似let的操作。

## 2. 问题介绍

首先，让我们看一个简单的let例子：

```kotlin
val str: String? = "hello"
val lengthReport = str?.let { "The length of the string [$it] is: ${it.length}" }
println(lengthReport)
// will print: The length of the string [hello] is: 5
```

在上面的示例中，let()函数生成给定字符串的长度报告，代码非常简单。但是，值得一提的是，str变量是可以为空的字符串类型(String?)；此外，**我们只想在str变量不为空时调用let()函数**。因此，我们使用了空安全的let()调用：str?.let{ ... }，这也是我们处理可空类型时常用的技巧。

有时，我们希望将空安全的let-like操作应用于多个变量，但是**标准的let()函数只能处理单个变量**。

在本教程中，我们将首先看一下“两个变量的let”案例，然后我们将看看是否可以构建一个函数来对更多变量执行类似let的操作。

为简单起见，我们将使用单元测试断言来验证我们的函数是否按预期工作。

接下来，让我们看看它们的实际效果。

##  3. 两个变量的空安全let

现在，我们将介绍几种在两个变量上实现空安全let调用的方法。

### 3.1 嵌套两个let调用

使空安全的let()处理两个可为空的变量的最直接方法是编写两个let()调用，接下来我们通过一个例子来理解它：

```kotlin
val theName: String? = "Kai"
val theNumber: Int? = 7
val result = theName?.let { name ->
    theNumber?.let { num -> "Hi $name, $num squared is ${num * num}" }
}
assertThat(result).isEqualTo("Hi Kai, 7 squared is 49")
```

如上面的代码所示，我们有两个嵌套的空安全let()调用，如果我们运行测试，它就会通过。所以，它按预期工作。

但是，嵌套结构不容易阅读。那么，接下来，让我们看看能不能找到更好的实现方式。

### 3.2 创建我们自己的let2()函数

节省一些输入并使代码更易于理解的常用方法是将逻辑包装在函数或方法中。

现在，让我们创建一个函数来模拟let()以便我们可以对两个变量执行[lambda表达式](https://www.baeldung.com/kotlin/lambda-expressions)：

```kotlin
inline fun <T1 : Any, T2 : Any, R : Any> let2(p1: T1?, p2: T2?, block: (T1, T2) -> R?): R? {
    return if (p1 != null && p2 != null) block(p1, p2) else null
}
```

接下来，让我们快速浏览一下函数实现并了解它的作用。

函数let2()具有三个参数：p1、p2和block；p1和p2是两个可为空的参数，block是一个函数，它接收两个非空参数并返回一个可为空的R实例。

正如我们在上面的代码中看到的，let2()是一个泛型函数，因此两个参数(p1和p2)的类型可以不同，**函数block决定let2()函数的返回类型**。

仅当p1和p2都不为空时，我们才执行block(p1, p2)函数；否则，let2()将返回null。

在Kotlin中，如果函数的最后一个参数是函数，我们可以将lambda表达式传递给它并将其放在(...)之外。例如，我们可以这样调用我们的let2()：

```kotlin
let2(v1, v2) { a, b ->  ... (lambda) }
```

此外，**我们将let2()函数声明为**[内联函数](https://www.baeldung.com/kotlin/inline-functions)**以获得更好的性能**。

接下来，让我们创建一个测试来查看let2()是否按预期工作。首先，让我们声明一个可为空的Int类型(Int?)来保存空值，以便Kotlin知道空值的具体类型：

```kotlin
val nullNum: Int? = null
```

然后，让我们向message传递一个字符串和一个整数来测试let2()函数：

```kotlin
assertThat(let2("Kai", 7) { name, num -> "Hi $name, $num squared is ${num * num}" }).isEqualTo("Hi Kai, 7 squared is 49")

assertThat(let2(nullNum, 7) { name, num -> "Hi $name, $num squared is ${num * num}" }).isNull()
assertThat(let2(7, nullNum) { name, num -> "Hi $name, $num squared is ${num * num}" }).isNull()
assertThat(let2(nullNum, nullNum) { name, num -> "Hi $name, $num squared is ${num * num}" }).isNull()
```

如果我们执行测试，它就会通过。所以我们的let2()函数可以对两个可为空的变量执行类似let的空安全操作。

接下来，让我们看看是否可以对两个以上的变量执行let()。

##  4. 多个变量的空安全let

为简单起见，我们将在本节中介绍如何对三个变量执行类似let的操作。

### 4.1 扩展let2()函数

由于let2()函数按预期工作，我们可以轻松地将其扩展为接收三个变量，所以这对我们来说不是挑战：

```kotlin
inline fun <T1 : Any, T2 : Any, T3 : Any, R : Any> let3(p1: T1?, p2: T2?, p3: T3?, block: (T1, T2, T3) -> R?): R? {
    return if (p1 != null && p2 != null && p3 != null) block(p1, p2, p3) else null
}
```

基本上，我们只是通过向函数添加一个新参数p3来将let2()扩展到let3()。

接下来，让我们测试我们的 let3()函数。这一次，为了简单起见，我们将使用三个可为空的整数(Int?)变量：

```kotlin
assertThat(let3(5, 6, 7) { n1, n2, n3 -> "$n1 + $n2 + $n3 is ${n1 + n2 + n3}" }).isEqualTo("5 + 6 + 7 is 18")

assertThat(let3(nullNum, 7, 6) { n1, n2, n3 -> "$n1 + $n2 + $n3 is ${n1 + n2 + n3}" }).isNull()
assertThat(let3(nullNum, nullNum, 6) { n1, n2, n3 -> "$n1 + $n2 + $n3 is ${n1 + n2 + n3}" }).isNull()
assertThat(let3(nullNum, nullNum, nullNum) { n1, n2, n3 -> "$n1 + $n2 + $n3 is ${n1 + n2 + n3}" }).isNull()
```

毫不奇怪，如果我们执行测试，测试就会通过，所以let3()函数起作用了。但是，如果我们回顾一下我们为let2()和let3()创建的函数，参数的数量总是固定的。当然，如果需要，我们仍然可以创建let4()、let5()等来支持更多的变量，但是最好有一个函数来处理可变数量的参数。

那么接下来，让我们看看如何实现这一目标。

### 4.2 处理动态数量的变量

**在Kotlin中**，[可变参数](https://www.baeldung.com/kotlin/varargs-spread-operator)**允许我们将动态数量的参数传递给函数**。因此，我们可以创建一个带有vararg参数的新内联函数：

```kotlin
inline fun <T : Any, R : Any> letIfAllNotNull(vararg arguments: T?, block: (List<T>) -> R): R? {
    return if (arguments.all { it != null }) {
        block(arguments.filterNotNull())
    } else null
}
```

正如我们在上面的letIfAllNotNull()函数中看到的，可变参数arguments可以有不同数量的参数，block函数现在接收具有非空T类型的元素列表。

与let2()和let3()函数类似，我们首先使用arguments.all { it != null }检查所有参数是否为非空值；然后，**arguments.filterNotNull()将可为空的可变参数(数组)转换为非空值列表**。

接下来，让我们创建一个测试来查看如何调用此函数并验证它是否按预期工作：

```kotlin
assertThat(letIfAllNotNull(5, 6, 7, 8) { "${it.joinToString(separator = " + ") { num -> "$num" }} is ${it.sum()}" }).isEqualTo("5 + 6 + 7 + 8 is 26")
assertThat(letIfAllNotNull(5, 6, 7) { "${it.joinToString(separator = " + ") { num -> "$num" }} is ${it.sum()}" }).isEqualTo("5 + 6 + 7 is 18")
assertThat(letIfAllNotNull(5, 7) { "${it.joinToString(separator = " + ") { num -> "$num" }} is ${it.sum()}" }).isEqualTo("5 + 7 is 12")

assertThat(letIfAllNotNull(nullNum, 7, 6) { "${it.joinToString(separator = " + ") { num -> "$num" }} is ${it.sum()}" }).isNull()
assertThat(letIfAllNotNull(nullNum, null, 6) { "${it.joinToString(separator = " + ") { num -> "$num" }} is ${it.sum()}" }).isNull()
assertThat(letIfAllNotNull(nullNum, nullNum, nullNum) { "${it.joinToString(separator = " + ") { num -> "$num" }} is ${it.sum()}" }).isNull()
```

正如我们在测试代码中看到的那样，**由于block()函数在我们编写lambda表达式时接收一个List对象作为参数，因此it变量是一个包含我们传递给letIfAllNotNull()的所有变量的List**。

如果我们运行测试，它就会通过，所以letIfAllNotNull()函数允许我们对动态数量的可空变量执行类似let的空安全操作。

### 4.3 非空变量let

到目前为止，我们已经创建了函数来对多个变量应用空安全的类似let的操作，这些解决方案中的一个共同要求是，仅当所有变量都不为空时才调用block函数。

然而，在实践中，**我们可能希望首先过滤掉空值并将所有非空变量传递给我们的block函数或lambda表达式**。

最后，我们在letIfAllNotNull()函数的基础上做一些改动，创建letIfAnyNotNull()函数：

```kotlin
inline fun <T : Any, R : Any> letIfAnyNotNull(vararg arguments: T?, block: (List<T>) -> R?): R? {
    return if (arguments.any { it != null }) {
        block(arguments.filterNotNull())
    } else null
}
```

如上面的代码所示，**我们检查arguments.any { it != null }而不是arguments.all { it != null }以便在参数包含任何非空变量时调用block()函数**。

接下来，让我们创建一个测试和一些输入数据，看看letIfAnyNotNull()是否可以产生预期的结果：

```kotlin
assertThat(letIfAnyNotNull(5, 6, 7) { "${it.joinToString(separator = " + ") { num -> "$num" }} is ${it.sum()}" }).isEqualTo("5 + 6 + 7 is 18")
assertThat(letIfAnyNotNull(nullNum, 6, 7) { "${it.joinToString(separator = " + ") { num -> "$num" }} is ${it.sum()}" }).isEqualTo("6 + 7 is 13")
assertThat(letIfAnyNotNull(nullNum, nullNum, 7) { "${it.joinToString(separator = " + ") { num -> "$num" }} is ${it.sum()}" }).isEqualTo("7 is 7")
assertThat(letIfAnyNotNull(nullNum, nullNum, nullNum) { "${it.joinToString(separator = " + ") { num -> "$num" }} is ${it.sum()}" }).isNull()
```

正如断言所示，letIfAnyNotNull()函数会将所有非空值打包到一个List中，并将该List传递给我们的lambda表达式。

如果我们运行，测试就会通过。

## 5. 总结

在本文中，我们学习了在Kotlin中对多个变量执行空安全let -like操作的不同方法。
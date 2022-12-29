## 1. 概述

在本教程中，我们将通过一些简单的用例重点了解**Kotlin中的yield函数**。

## 2. 基础知识

关于yield()函数，首先要了解的是它是一个在Kotlin[协程](https://www.baeldung.com/kotlin/coroutines)上下文中使用的挂起函数。**当调用yield()时，如果可能，它会将当前协程调度程序的线程(或线程池)提供给其他协程运行**。

其次，我们应该理解**yield()和yield(value: T)是两个不同的函数**，后者仅在序列的上下文中使用，我们将在下一节中探讨。

## 3. 构建序列

我们可以使用yield()的最常见用例之一是构建序列。

### 3.1 有限序列

假设我们想要构建一个有限的元音序列，对于如此短的序列，我们可以使用多个语句来产生单个元音值，让我们在Yield类中定义vowels()函数：

```kotlin
fun vowels() = sequence {
    yield("a")
    yield("e")
    yield("i")
    yield("o")
    yield("u")
}
```

现在，让我们使用vowelIterator对此序列进行迭代：

```kotlin
val client = Yield()
val vowelIterator = client.vowels().iterator()
while (vowelIterator.hasNext()) {
    println(vowelIterator.next())
}
```

一旦我们运行了main函数，我们就应该能够验证迭代的结果。在这个简单的场景中，**关于有限序列需要注意的一件重要事情是，在调用vowelIterator的next()方法之前，我们应该始终使用hasNext()方法来检查序列中是否有下一个可用项**。

### 3.2 无限序列

使用yield()的一个更实际的用例是构建无限序列，因此，让我们用它来生成斐波那契数列的项。

为了构建这样一个序列，让我们编写一个fibonacci()函数，它使用一个[无限循环](https://www.baeldung.com/kotlin/loops#while-loop)，在每次迭代中产生一个单项：

```kotlin
fun fibonacci() = sequence {
    var terms = Pair(0, 1)
    while (true) {
        yield(terms.first)
        terms = Pair(terms.second, terms.first + terms.second)
    }
}
```

现在，让我们通过对该序列使用迭代器来验证序列的前5项：

```kotlin
val client = Yield()
val fibonacciIterator = client.fibonacci().iterator()
var count = 5
while (count > 0) {
    println(fibonacciIterator.next())
    count--
}
```

**对于无限序列，我们可以放宽对迭代器的hasNext()方法的调用，因为我们可以保证获得序列的下一个元素**。

## 4. 协作式多任务处理

在协作式多任务系统中，一个任务会自愿放弃以允许另一个作业执行。在本节中，我们将使用yield()函数在一个简单的系统中实现协作式多任务处理。

### 4.1 数字打印机编排器

假设我们要打印低于特定阈值的所有数字。

首先，让我们使用AtomicInteger将当前值定义为0，并将阈值定义为常量整数值：

```kotlin
val current = AtomicInteger(0)
val threshold = 10
```

现在，让我们定义一个numberPrinter()函数来协调数字的打印。

我们打算**通过定义两个不同的作业来分而治之，一个用于打印偶数，另一个用于打印奇数**。因此，为了确保我们一直等到所有低于阈值的数字都被打印出来，我们将使用runBlocking CoroutineScope来定义它：

```kotlin
fun numberPrinter() = runBlocking {
}
```

### 4.2 奇偶数打印机

现在，让我们专注于将打印数字的任务委托给两个不同作业的部分，即evenNumberPrinter和oddNumberPrinter。

首先，让我们看看如何启动evenNumberPrinter作业：

```kotlin
val evenNumberPrinter = launch {
    while (current.get() < threshold) {
        if (current.get() % 2 == 0) {
            println("$current is even")
            current.incrementAndGet()
        }
        yield()
    }
}
```

这个想法很简单；它仅在偶数时才打印当前值。否则，**它明白它需要与另一个可以打印奇数值的任务合作，因此它自愿让步**。

接下来，让我们看一下oddNumberPrinter作业，除了只打印奇数的[情况](Kotlin中的If-Else表达式.md)外，它本质上是相同的：

```kotlin
val oddNumberPrinter = launch {
    while (current.get() < threshold) {
        if (current.get() % 2 != 0) {
            println("$current is odd")
            current.incrementAndGet()
        }
        yield()
    }
}
```

最后，让我们调用numberPrinter()业务流程协调器来打印数字：

```kotlin
client.numberPrinter()
```

正如预期的那样，我们能够看到所有低于阈值的数字：

```shell
0 is even
1 is odd
2 is even
3 is odd
4 is even
5 is odd
6 is even
7 is odd
8 is even
9 is odd
```

## 5. 总结

在本教程中，我们通过构建序列和实现作业的协作式多任务处理，采用动手实践的方法来理解Kotlin中的yield函数。
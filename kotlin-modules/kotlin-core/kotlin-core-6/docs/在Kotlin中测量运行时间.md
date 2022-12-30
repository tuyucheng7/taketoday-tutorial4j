## 1. 概述

在本教程中，我们将熟悉在Kotlin中计算经过时间的几种不同方法。

首先，我们将从定义现代计算中测量时间的不同方式开始。然后，我们将评估在Kotlin中计算持续时间的几种不同方法。

## 2. 时钟的定义

在现代计算中，至少有两种类型的时钟，一种这样的时钟被称为一天中的时间或挂钟时间，**时间时钟根据日历报告实际日期和时间**。

例如，[System.currentTimeMillis()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/System.html#currentTimeMillis())返回自纪元时间以来的秒数，所以它是Java中的时间时钟。

时间时钟不能保证总是向前移动，甚至不能保证合理地向前移动。起初这听起来可能违反直觉，但是，由于各种原因，例如从NTP服务器漂移、NTP服务器配置错误、突然重置、VM暂停、闰秒等，此类时钟可能不准确。**由于这些奇怪的现象，机器可能会在时间上突然向前或向后跳跃**。

因此，**这些时钟不是测量经过时间或持续时间的好工具，**[因为可能会出现负的经过时间](https://blog.cloudflare.com/how-and-why-the-leap-second-affected-cloudflare-dns/)。

单调时钟是现代计算机中可用的第二种时钟，**这样的时钟通常测量自计算机启动或任何其他任意事件以来的纳秒数**，正因为如此，它们可以保证始终向前移动，因此有利于测量持续时间。Java中的[System.nanoTime()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/System.html#nanoTime())是一个单调时钟。

在本教程的过程中，我们将根据这两个时钟之一对每个解决方案进行分类。

## 3. measureTimeMillis函数

要以毫秒为单位测量经过的时间，我们可以使用[measureTimeMillis(block: () -> Unit)](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.system/measure-time-millis.html)函数，我们所要做的就是传递一个代码块：

```kotlin
val elapsed = measureTimeMillis {
    Thread.sleep(100)
    println("Measuring time via measureTimeMillis")
}

assertThat(elapsed).isGreaterThanOrEqualTo(100)
```

**measureTimeMillis()函数将执行传递的lambda表达式并返回经过的时间(以毫秒为单位)**，使用这种方法，lambda本身无法向外部上下文返回任何值。

值得一提的是，measureTimeMillis()函数在底层使用System.currentTimeMillis()方法来计算经过的持续时间。因此，**它基于时间时钟，不建议计算持续时间，即使我们可以**！

## 4. measureNanoTime函数

为了以纳秒为单位测量经过的时间，我们可以使用[measureNanoTime(block: () -> Unit)](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.system/measure-nano-time.html)函数：

```kotlin
val elapsed = measureNanoTime {
    Thread.sleep(100)
    println("Measuring time via measureNanoTime")
}

assertThat(elapsed).isGreaterThanOrEqualTo(100 * 1_000_000)
```

因为每毫秒是1,000,000纳秒，因此我们在这里执行乘法。与measureTimeMillis()类似，此函数仅返回经过的时间，因此lambda本身无法向封闭范围返回任何内容。

**此外，measureNanoTime()在内部使用System.nanoTime()方法，因此，它基于单调时钟**。

## 5. TimeSource API

**Kotlin 1.3引入了**[TimeSource](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-time-source/)**和**[Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/) **API来测量时间间隔**。

例如，下面是我们如何使用这个新API测量经过的时间：

```kotlin
@Test
@ExperimentalTime
fun `Given a block of code, When using measureTime, Then reports the elapsed time as a Duration`() {
    val elapsed: Duration = measureTime {
        Thread.sleep(100)
        println("Measuring time via measureTime")
    }

    assertThat(elapsed).isGreaterThanOrEqualTo(100.milliseconds)
    assertThat(elapsed).isGreaterThanOrEqualTo(100.toDuration(DurationUnit.MILLISECONDS))
}
```

由于这仍然是一个实验性API，我们应该选择使用[kotlin.time.ExperimentalTime](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-experimental-time/)注解，[kotlin.time.measureTime(block: () -> Unit)](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/measure-time.html)函数接收一个代码块作为lambda表达式，并在执行它时计算经过的时间。

与measureTimeMillis()和measureNanoTime()相反，**此函数返回一个kotlin.time.Duration实例而不是一个原始数字**。此外，为了创建Duration实例，我们使用了两种方法：

-   所有数字数据类型的[milliseconds扩展属性](https://github.com/JetBrains/kotlin/blob/0c13a7f89a166015fcc281da745a5fc0719f73c4/libraries/stdlib/src/kotlin/time/Duration.kt#L400)；除了毫秒之外，每个时间单位还有一个扩展属性，例如[seconds](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/seconds.html)、[microseconds](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/microseconds.html)等
-   [Int.toDuration(kotlin.time.DurationUnit)](https://github.com/JetBrains/kotlin/blob/0c13a7f89a166015fcc281da745a5fc0719f73c4/libraries/stdlib/src/kotlin/time/Duration.kt#L352)扩展函数，它将接收的Int转换为Duration

measureTime()在底层使用[TimeSource.Monotonic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-time-source/-monotonic/)实现，**因此它基于单调时钟**。此外，此函数仅返回经过的时间，因此lambda本身无法向封闭上下文返回任何内容。

与measureTime()函数相反，[measureTimedValue(block: () -> T)](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/measure-timed-value.html)**实验函数除了返回经过的持续时间外，还可以返回一个值**：

```kotlin
val (value, elapsed) = measureTimedValue {
    Thread.sleep(100)
    42
}

assertThat(value).isEqualTo(42)
assertThat(elapsed).isGreaterThanOrEqualTo(100.milliseconds)
```

如上所示，measureTimedValue()函数向外部上下文返回一个值，在这里，我们使用了析构模式将返回的[TimedValue](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-timed-value/)实例分解为一个(value, elapsed)对。当然，我们也可以在不破坏结果的情况下使用TimedValue本身作为返回类型：

```kotlin
val timedValue: TimedValue = measureTimedValue {
    Thread.sleep(100)
    42
}

assertThat(timedValue.value).isEqualTo(42)
assertThat(timedValue.duration).isGreaterThanOrEqualTo(100.milliseconds)
```

## 6. 经典Java

Kotlin对Java的System工具类提供了许多漂亮而简洁的抽象来测量持续时间，但是，仍然可以使用纯Java方法来完成此任务。

例如，以下是我们如何使用System.nanoTime()实现相同的目的：

```kotlin
val start = System.nanoTime()
Thread.sleep(100)

assertThat(System.nanoTime() - start).isGreaterThanOrEqualTo(100 * 1_000_000)
```

我们也可以使用System.currentTimeMillis()静态方法实现相同的目的，但是，**在计算经过时间时，使用单调时钟始终是首选方法**。

## 7. 总结

在本教程中，我们看到了大多数现代计算机中可用的不同类型的时钟。然后，我们评估了在Kotlin中计算经过时间的不同方法，包括当前稳定的API、实验性API和普通的旧Java API。
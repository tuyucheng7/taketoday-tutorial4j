## 1. 概述

with()是Kotlin中的[作用域函数](https://www.baeldung.com/kotlin/scope-functions)之一。

在本教程中，我们将仔细研究with()函数。

## 2. with()函数介绍

作为Kotlin作用域函数的成员，**with()函数引用一个上下文对象并在该上下文中执行一段代码**。

为了更好地理解with()函数的用法，让我们先来看看它是如何定义的：

```kotlin
public inline fun <T, R> with(receiver: T, block: T.() -> R): R
```

让我们分解并理解它：

-   第一个参数：类型为T的receiver，即所谓的上下文对象
-   第二个参数：block，它是一个lambda 表达式，包含我们要在上面的上下文中执行的逻辑
-   with()函数返回lambda表达式的返回值

接下来，让我们通过实例学习如何使用with()函数。为简单起见，我们将使用单元测试断言来验证with()的返回值是否符合预期。

## 3. with()函数的使用

下面我们通过一个例子来了解如何使用with()函数，假设我们有一个[数据类](../../kotlin-core-lang-oop-1/docs/Kotlin中的数据类.md)Player：

```kotlin
data class Player(val firstname: String, val lastname: String, val totalPlayed: Int, val numOfWin: Int)
```

我们想获得一个Player实例的描述字符串，格式如下：“firstname lastname's win-rate is rate%”。

那么接下来，让我们使用with()编写一个测试：

```kotlin
val tomHanks = Player(firstname = "Tom", lastname = "Hanks", totalPlayed = 100, numOfWin = 77)
val expectedDescription = "Tom Hanks's win-rate is 77%"
val result = with(tomHanks) {
    "$firstname $lastname's win-rate is ${numOfWin * 100 / totalPlayed}%"
}
assertEquals(expectedDescription, result)
```

如上面的代码所示，**tomHanks对象是with()函数的上下文对象**，在with{...}块中，代码位于tomHanks的上下文中。因此，我们可以直接访问上下文对象的属性，例如“ $firstname $lastname ”。在这里，我们使用了Kotlin的[字符串模板](https://www.baeldung.com/kotlin/string-templates)。

如果我们运行测试，它就会通过。因此，**with{ ... }返回lambda结果**，在本例中，它是描述字符串。

当我们想在with{...}块中引用上下文对象时，我们可以使用this关键字：

```kotlin
val tomHanks = Player(firstname = "Tom", lastname = "Hanks", totalPlayed = 100, numOfWin = 77)
val result = with(tomHanks) {
    "$this"
}
assertEquals("$tomHanks", result)
```

在上面的测试中，我们在with{...}块中返回tomHanks的toString()结果。同样，我们使用了字符串模板，在这里，**$this与this.toString()相同**。

因此，当我们使用with()时，我们应该记住几件事：

-   with{...}块中的所有代码都在接收者对象的上下文中，如果我们想引用那个对象，我们使用this
-   with{...}返回lambda结果

一些作用域函数使用it引用接收者对象，例如let()和also()。但是，一些作用域函数使用this，它与with()相同，例如apply()和run()。接下来，让我们比较一下它们，看看apply()/run()和with()之间的区别。

## 4. with()与apply()

with()和apply()都使用this关键字引用上下文对象，with()和apply()之间的显著区别是**with()返回lambda结果；另一方面，apply()返回接收者对象**。因此，apply()通常用于对象配置。

例如，假设我们有一个Player2类：

```kotlin
class Player2(val id: Int) {
    var firstname: String = ""
    var lastname: String = ""
    var totalPlayed: Int = 0
    var numOfWin: Int = 0

    // hashcode and equals() methods omitted. 
    // Both methods check all properties in the class
}
```

现在，让我们看看apply()是如何用于对象配置的：

```kotlin
val tomHanks = Player2(7)
tomHanks.firstname = "Tom"
tomHanks.lastname = "Hanks"
tomHanks.totalPlayed = 100
tomHanks.numOfWin = 77

val result = Player2(7).apply {
    firstname = "Tom"
    lastname = "Hanks"
    totalPlayed = 100
    numOfWin = 77
}
assertEquals(tomHanks, result)
```

如我们所见，apply()函数返回上下文对象本身，在此示例中，它是Player2(7)。在apply{...}块中，我们设置了接收者对象的不同属性。

如果我们运行，测试就会通过。但是，如果我们在上面的示例中将apply()更改为with()，则结果不是Player2实例。相反，它将是[Unit](https://www.baeldung.com/kotlin/void-type)类型(类似于Java的void)，因为lambda不返回任何值：

```kotlin
val result = with(Player2(7)) {
    firstname = "Tom"
    lastname = "Hanks"
    totalPlayed = 100
    numOfWin = 77
}
assertTrue { result is Unit }
```

眼尖的人可能已经注意到**with()和apply()的调用方式也不一样了**：

-   someObject.apply { ... }
-   with(someObject) { ... }

这是因为**apply()是所有类型的**[扩展函数](https://www.baeldung.com/kotlin/extension-methods)，**但with()不是**：

```kotlin
public inline fun <T> T.apply(block: T.() -> Unit): T {..}
```

## 5. with()与run()

run()函数是另一个使用this来引用上下文对象的作用域函数；此外，与with()一样，run()函数也返回lambda结果。

run()和with()之间的唯一区别是run()是所有类型的扩展函数：

```kotlin
public inline fun <T, R> T.run(block: T.() -> R): R = block()
```

因此，我们可以这样使用run()：someObject.run { ... }。因此，我们可以看到**with()和run()除了调用之外非常相似**。

但是，**如果接收者对象可以为空，则run()方法更易于阅读**，让我们看一个例子：

```kotlin
fun giveMeAPlayer(): Player? {
    return Player(firstname = "Tom", lastname = "Hanks", totalPlayed = 100, numOfWin = 77)
}
```

如上面的代码所示，我们创建了一个函数giveMeAPlayer()来返回一个可为空的Player实例。当然，为了简单起见，我们让函数始终返回同一个对象。

接下来，让我们看看with()和run()是如何处理可空对象的：

```kotlin
val expectedDescription = "Tom Hanks's win-rate is 77%"

// using run
val runResult = giveMeAPlayer()?.run { "$firstname $lastname's win-rate is ${numOfWin * 100 / totalPlayed}%" }
assertEquals(expectedDescription, runResult)

// using with                                                                                                              
val withResult = with(giveMeAPlayer()) {
    if (this != null) "$firstname $lastname's win-rate is ${numOfWin * 100 / totalPlayed}%" else null
}
assertEquals(expectedDescription, withResult)
```

我们在测试中可以看到，由于run()是一个扩展函数，**我们可以使用**[空安全调用](https://www.baeldung.com/kotlin/null-safety#safe-calls)**someNullable?.run { ... }让运行块只在someNullable不为空时执行**。但是，在with()方面，我们不能使用空安全特性，因此我们需要在块中做一个空检查。

如果我们运行测试，它就会通过。因此，这两种方法给出相同的结果。

## 6. 总结

在本文中，我们学习了如何使用with()函数，此外，我们还讨论了with()、run()和apply()之间的区别。
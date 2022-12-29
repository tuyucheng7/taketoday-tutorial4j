## 1. 概述

在本教程中，我们将讨论Kotlin中结构跳转表达式的用法。

简单来说，**Kotlin有三个结构跳转表达式：return、break、continue**。在接下来的部分中，我们将介绍带标签和不带标签的功能。

## 2. Kotlin中的标签

Kotlin中的任何表达式都可以用标签标记。

**我们使用标识符后跟“@”符号来创建标签**，例如，abc@、loop@是有效的标签。

要标记一个表达式，我们只需在它前面添加标签：

```kotlin
loop@ for (i in 1..10) {
    // some code 
}
```

## 3. break语句

**如果没有标签，break将终止最近的封闭循环**。

让我们看一个例子：

```kotlin
@Test
fun givenLoop_whenBreak_thenComplete() {
    var value = ""
    for (i in "hello_world") {
        if (i == '_') break
        value += i.toString()
    }
    assertEquals("hello", value)
}
```

或者，我们可以使用**带有标签的break来终止标有该标签的循环**：

```kotlin
@Test
fun givenLoop_whenBreakWithLabel_thenComplete() {
    var value = ""
    outer_loop@ for (i in 'a'..'d') {
        for (j in 1..3) {
            value += "" + i + j
            if (i == 'b' && j == 1)
                break@outer_loop
        }
    }
    assertEquals("a1a2a3b1", value)
}
```

在这种情况下，当i和j变量分别等于“b”和“1”时，外循环终止。

## 4. continue语句

接下来，让我们看一下continue关键字，我们也可以在有或没有标签的情况下使用它。

**如果没有标签，continue将继续进行封闭循环的下一次迭代**：

```kotlin
@Test
fun givenLoop_whenContinue_thenComplete() {
    var result = ""
    for (i in "hello_world") {
        if (i == '_') continue
        result += i
    }
    assertEquals("helloworld", result)
}
```

另一方面，**当我们使用带有标记循环的标签的continue时，它将继续进行该循环的下一次迭代**：

```kotlin
@Test
fun givenLoop_whenContinueWithLabel_thenComplete() {
    var result = ""
    outer_loop@ for (i in 'a'..'c') {
        for (j in 1..3) {
            if (i == 'b') continue@outer_loop
            result += "" + i + j
        }
    }
    assertEquals("a1a2a3c1c2c3", result)
}
```

在此示例中，我们使用了continue跳过标记为outer_loop的循环的一次迭代。

## 5. return语句

如果没有标签，它将**返回到最近的封闭函数或匿名函数**：

```kotlin
@Test
fun givenLambda_whenReturn_thenComplete() {
    var result = returnInLambda()
    assertEquals("hello", result)
}

private fun returnInLambda(): String {
    var result = ""
    "hello_world".forEach {
        if (it == '_') return result
        result += it.toString()
    }
    // this line won't be reached
    return result
}
```

当我们想**在匿名函数上应用continue逻辑**时，return也很有用：

```kotlin
@Test
fun givenAnonymousFunction_return_thenComplete() {
    var result = ""
    "hello_world".forEach(fun(element) {
        if (element == '_') return
        result += element.toString()
    })
    assertEquals("helloworld", result)
}
```

在这个例子中，return语句将返回到匿名fun的调用者，即forEach循环。

**对于lambda表达式，我们也可以使用带有标签的return来实现类似的结果**：

```kotlin
@Test
fun givenLambda_whenReturnWithExplicitLabel_thenComplete() {
    var result = ""
    "hello_world".forEach lit@{
        if (it == '_') {
            return@lit
        }
        result += it.toString()
    }
    assertEquals("helloworld", result)
}
```

或者，我们也可以**使用隐式标签返回**：

```kotlin
@Test
fun givenLambda_whenReturnWithImplicitLabel_thenComplete() {
    var result = ""
    "hello_world".forEach {
        if (it == '_') {
            // local return to the caller of the lambda, i.e. the forEach loop
            return@forEach
        }
        result += it.toString()
    }
    assertEquals("helloworld", result)
}
```

在上面的例子中，return语句也会返回到lambda的调用者-forEach循环。

**最后，return可以与标签一起使用，通过返回到外部的标签来将中断逻辑应用于lambda表达式**：

```kotlin
@Test
fun givenAnonymousFunction_returnToLabel_thenComplete() {
    var result = ""
    run loop@{
        "hello_world".forEach {
            if (it == '_') return@loop
            result += it.toString()
        }
    }
    assertEquals("hello", result)
}
```

## 6. 总结

在本文中，我们介绍了Kotlin中return、break、continue的用例。
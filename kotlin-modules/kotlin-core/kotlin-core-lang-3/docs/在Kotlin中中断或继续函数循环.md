## 1. 简介

**在Kotlin中使用break和continue关键字跳出函数循环是不可能的**，至少传统方式不行。

例如，我们无法通过简单地编写break语句来停止forEach函数循环的执行。

**但是，我们可以使用一些技术来模仿这种行为**，让我们看看其中的一些。

## 2. 使用标签

正如我们所知，我们可以使用标签来限定返回语句，我们可以使用它来模拟函数循环中传统的“continue”和“break”行为。

### 2.1 模拟continue

让我们看一个例子：

```kotlin
val list = listOf(3, 4, 3, 4, 3)
var sum = 0
list.forEach loop@{ number ->
    if (number % 2 == 0) { // skip all even numbers
        return@loop
    }
    sum += number
}
assertTrue { sum == 9 }
```

在这里，**我们将forEach循环标记为'loop'，稍后，执行跳转到此标签以模拟“continue”行为**。

我们需要我们的函数循环来跳过特定迭代的执行，为此，我们将“loop”标签与return@loop语句一起使用。

### 2.2 模拟break

同样，我们也可以使用标签和返回语句来模拟'break'：

```kotlin
run outer@{
    list.forEach inner@{ number ->
        if (number % 2 == 0) { // 'break' at the first even number
            return@outer
        }
        sum += number
    }
}
assertTrue { sum == 3 }
```

与前面的示例不同，我们需要一些额外的样板来模拟'break'，我们需要将循环包围在另一个作用域内，这是必需的，以便我们稍后可以返回到外部作用域。

**我们为外部和内部作用域分配不同的标签，随后从内部作用域中的循环返回到外部作用域**，这就是我们模拟“break”行为的方式。

为了澄清，我们创建外部作用域是因为返回到(inner)循环标签，我们只跳过当前迭代，我们不会停止循环本身的执行。

我们也可以使用不带标签的返回语句，在这种情况下，我们最终会返回函数本身。这可能会导致在我们的循环之后跳过函数中的其他语句，因此需要一个额外的外部作用域。

值得注意的是，我们还可以在这里使用其他[作用域函数](https://kotlinlang.org/docs/scope-functions.html)来代替run。

## 3. 使用filter和takeWhile

另一种模仿“continue”和“break”行为的方法是将它们与filter和takeWhile等函数链接起来。

### 3.1 使用filter模拟continue

在常规for循环中使用continue，我们可以跳过迭代我们不想处理的元素，我们可以通过使用filter通过函数循环来做到这一点：

```kotlin
val list = listOf(3, 4, 3, 4,
    var sum = 0
list.filter { it % 2 != 0 } // skip all even numbers
    .forEach { number ->
        sum += number
    }
assertTrue { sum == 9 }
```

在这里，**我们使用filter操作过滤我们想要预先跳过的元素**，然后我们遍历过滤后的列表，这有效地实现了与在常规for循环中使用continue相同的结果。

### 3.2 使用takeWhile模拟break

同样，我们可以通过在函数循环中调用[takeWhile](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/take-while.html)来模拟函数循环中的break行为：

```kotlin
list.takeWhile { it % 2 != 0 } // 'break' at the first even number
    .forEach { number ->
        sum += number
    }
assertTrue { sum == 3 }
```

正如我们所知，**takeWhile过滤列表，在到达第一个不满足给定谓词的元素后删除所有元素**。通过迭代结果列表，我们获得了与break相同的结果。

## 4. 总结

Kotlin中的函数式循环不支持break和continue跳转，但是，我们可以使用一些技术来模拟这一点。

看起来带标签的return语句更有效，这是由于函数链接的开销，这可能会导致创建中间集合。

另一方面，使用filter和takeWhile似乎需要最少的样板代码。
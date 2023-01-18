## 1. 概述

在本文中，我们将探索Kotlin语言中的Lambda。请记住，lambda并不是Kotlin独有的，并且在许多其他语言中已经存在了很多年。

**Lambdas表达式本质上是匿名函数，我们可以将其视为值**，例如，我们可以将它们作为参数传递给方法、返回它们，或者做我们可以用普通对象做的任何其他事情。

## 2. 定义Lambda

正如我们将看到的，Kotlin Lambda与Java Lambda非常相似，你可以在[此处](https://www.baeldung.com/java-8-lambda-expressions-tips)找到有关如何使用Java Lambdas和一些最佳实践的更多信息。

要定义lambda，我们需要遵循以下语法：

```kotlin
val lambdaName: Type = { argumentList -> codeBody }
```

**lambda中唯一不是可选的部分是代码体**。

当定义最多一个参数时可以跳过参数列表，并且Kotlin编译器通常可以推断出类型，**我们也并不总是需要一个变量，lambda可以直接作为方法参数传递**。

lambda块中最后一个命令的类型是返回的类型。

### 2.1 类型推断

Kotlin的类型推断允许编译器评估lambda的类型。

编写一个生成数字平方的lambda可以写成：

```kotlin
val square = { number: Int -> number * number }
val nine = square(3)
```

Kotlin会将上面的示例评估为一个接收一个Int并返回一个Int的函数：(Int) -> Int。

如果我们想创建一个lambda，将其单个参数数字乘以100，然后将该值作为字符串返回：

```kotlin
val magnitude100String = { input: Int ->
    val magnitude = input * 100
    magnitude.toString()
}
```

Kotlin会理解这个lambda的类型是(Int) -> String。

### 2.2 类型声明

有时Kotlin无法推断出我们的类型，我们必须显式声明lambda的类型；就像我们可以处理任何其他类型一样。

模式是input -> output，但是，如果代码不返回任何值，我们使用Unit类型：

```kotlin
val that: Int -> Int = { three -> three }
```

```kotlin
val more: (String, Int) -> String = { str, int -> str + int }
```

```kotlin
val noReturn: Int -> Unit = { num -> println(num) }
```

**我们可以使用lambda作为类扩展**：

```kotlin
val another: String.(Int) -> String = { this + it }
```

我们在这里使用的模式与我们定义的其他lambda略有不同，我们的括号仍然包含我们的参数，但在我们的括号之前，我们有我们要将此lambda附加到的类型。

要从字符串中使用此模式，我们调用Type.lambdaName(arguments)以调用我们的“another”示例：

```kotlin
fun extendString(arg: String, num: Int): String {
    val another: String.(Int) -> String = { this + it }

    return arg.another(num)
}
```

### 2.3 从Lambda返回

最后一个表达式是执行lambda后将返回的值：

```kotlin
val calculateGrade = { grade : Int ->
    when(grade) {
        in 0..40 -> "Fail"
        in 41..70 -> "Pass"
        in 71..100 -> "Distinction"
        else -> false
    }
}
```

最后一种方法是利用匿名函数定义-我们必须显式定义参数和返回类型，并且可以像使用任何方法一样使用return语句：

```kotlin
val calculateGrade = fun(grade: Int): String {
    if (grade < 0 || grade > 100) {
        return "Error"
    } else if (grade < 40) {
        return "Fail"
    } else if (grade < 70) {
        return "Pass"
    }

    return "Distinction"
}
```

## 3. it

单个参数lambda的简写是使用关键字'it'，**该值代表我们传递给lambda函数的任何单独的参数**。

我们将对以下Ints数组执行相同的forEach方法：

```kotlin
val array = arrayOf(1, 2, 3, 4, 5, 6)
```

我们将首先看一下lambda函数的长写形式，然后是相同代码的简写形式，其中“it”将代表以下数组中的每个元素。

长写：

```kotlin
array.forEach { item -> println(item * 4) }
```

短写：

```kotlin
array.forEach { println(it * 4) }
```

## 4. 实现Lambda

我们将非常简要地介绍如何调用作用域内的lambda以及如何将lambda作为参数传递。

一旦lambda对象在作用域内，就可以像调用任何其他作用域内方法一样调用它，使用其名称后跟括号和任何参数：

```kotlin
fun invokeLambda(lambda: (Double) -> Boolean) : Boolean {
    return lambda(4.329)
}
```

如果我们需要将lambda作为参数传递给高阶方法，我们有五个选择。

### 4.1 Lambda对象变量

使用第2节中声明的现有lambda对象，我们将该对象传递给方法，就像我们使用任何其他参数一样：

```kotlin
@Test
fun whenPassingALambdaObject_thenCallTriggerLambda() {
    val lambda = { arg: Double ->
        arg == 4.329
    }

    val result = invokeLambda(lambda)

    assertTrue(result)
}
```

### 4.2 Lambda字面量

我们可以将文本直接传递给方法调用，而不是将lambda赋值给变量：

```kotlin
Test
fun whenPassingALambdaLiteral_thenCallTriggerLambda() {
    val result = invokeLambda({
        true
    })

    assertTrue(result)
}
```

### 4.3 括号外的Lambda本文

JetBrains鼓励的lambda文本的另一种模式是将lambda作为最后一个参数传递给方法，并将lambda放在方法调用之外：

```kotlin
@Test
fun whenPassingALambdaLiteralOutsideBrackets_thenCallTriggerLambda() {
    val result = invokeLambda { arg -> arg.isNaN() }

    assertFalse(result)
}
```

### 4.4 方法引用

最后，我们可以选择使用方法引用，这些是对现有方法的引用。

在下面的示例中，我们采用Double::isFinite。然后该函数具有与lambda相同的结构，但是，它的类型为KFunction1<Double, Boolean>因为它有一个参数，接收一个Double并返回一个Boolean：

```kotlin
@Test
fun whenPassingAFunctionReference_thenCallTriggerLambda() {
    val reference = Double::isFinite
    val result = invokeLambda(reference)

    assertTrue(result)
}
```

## 5. Java中的Kotlin Lambda

Kotlin使用生成的函数接口与Java互操作，它们存在于[此处](https://github.com/JetBrains/kotlin/blob/master/libraries/stdlib/jvm/runtime/kotlin/jvm/functions/Functions.kt)的Kotlin源代码中。

我们对这些生成的类可以传递的参数数量有限制，当前限制为22；由接口Function22表示。

Function接口泛型的结构是数字和表示lambda的参数数量，那么类的个数就是参数类型的顺序。

最后一个泛型参数是返回类型：

```kotlin
import kotlin.jvm.functions.*

public interface Function1<in P1, out R> : Function<R> {
    public operator fun invoke(p1: P1): R
}
```

**当Kotlin代码中没有定义返回类型时，lambda将返回一个Kotlin Unit**，Java代码必须从kotlin包中导入类并返回null。

下面是一个从部分Kotlin和部分Java的项目调用Kotlin Lambda的示例：

```java
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
...
new Function1<Customer, Unit>() {
    @Override
    public Unit invoke(Customer c) {

        AnalyticsManager.trackFacebookLogin(c.getCreated());

        return null;
    }
}
```

使用Java 8时，我们使用Java lambda而不是Function匿名类：

```java
@Test
void givenJava8_whenUsingLambda_thenReturnLambdaResult() {
    assertTrue(LambdaKt.takeLambda(c -> c >= 0));
}
```

## 6. 匿名内部类

Kotlin有两种有趣的方式来处理匿名内部类。

### 6.1 对象表达式

当调用Kotlin匿名内部类或由多个方法组成的Java匿名类时，我们必须实现一个对象表达式。

为了证明这一点，我们将采用一个简单的接口和一个类，该类接收该接口的实现并调用依赖于布尔参数的方法：

```kotlin
class Processor {
    interface ActionCallback {
        fun success() : String
        fun failure() : String
    }

    fun performEvent(decision: Boolean, callback : ActionCallback) : String {
        return if(decision) {
            callback.success()
        } else {
            callback.failure()
        }
    }
}
```

现在要提供一个匿名内部类，我们需要使用“object”语法：

```kotlin
@Test
fun givenMultipleMethods_whenCallingAnonymousFunction_thenTriggerSuccess() {
    val result = Processor().performEvent(true, object : Processor.ActionCallback {
        override fun success() = "Success"

        override fun failure() = "Failure"
    })

    assertEquals("Success", result)
}
```

### 6.2 Lambda表达式

另一方面，我们也可以选择使用lambda代替，**使用lambda代替匿名内部类有一定的条件**：

1.  该类是Java接口的实现(不是Kotlin接口)
2.  接口必须有一个最大值

如果同时满足这两个条件，我们可以改用lambda表达式。

**lambda本身将接收与接口的单个方法一样多的参数**。

一个常见的例子是使用lambda而不是标准的Java Consumer：

```kotlin
val list = ArrayList<Int>(2)

list.stream()
    .forEach({ i -> println(i) })
```

## 7. 总结

虽然在语法上相似，但Kotlin和Java lambda是完全不同的特性，当面向Java 6时，Kotlin必须将其lambda转换为可在JVM 1.6中使用的结构。

尽管如此，Java 8 lambda的最佳实践仍然适用。有关lambda最佳实践的更多信息，请单击[此处](https://www.baeldung.com/java-8-lambda-expressions-tips)。
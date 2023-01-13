## 1. 概述

Arrow 是一个从 [KΛTEGORY](http://kategory.io/) 和 [funKTiale](https://github.com/MarioAriasC/funKTionale)合并而来的库。

在本教程中，我们将了解 Arrow 的基础知识，以及它如何帮助我们利用 Kotlin 中函数式编程的强大功能。

我们将讨论核心包中的数据类型，并研究有关错误处理的用例。

## 2.Maven依赖

[要在我们的项目中](https://search.maven.org/search?q=g:io.arrow-kt AND a:arrow-core&core=gav)包含 Arrow，我们必须添加[arrow-core](https://search.maven.org/search?q=g:io.arrow-kt AND a:arrow-core&core=gav)[依赖](https://search.maven.org/search?q=g:io.arrow-kt AND a:arrow-core&core=gav)项：

```xml
<dependency>
    <groupId>io.arrow-kt</groupId>
    <artifactId>arrow-core</artifactId>
    <version>0.7.3</version>
</dependency>

```

## 3. 函数式数据类型

让我们从研究核心模块中的数据类型开始。

### 3.1. 单子介绍

这里讨论的一些数据类型是 Monad。基本上，Monad 具有以下属性：

-   它们是一种特殊的数据类型，基本上是一个或多个原始值的包装器
-   他们有三个公共方法：
    -   包装值的工厂方法
    -   地图
    -   平面地图
-   这些方法效果很好，即它们没有副作用。

在 Java 世界中，数组和流是 Monad，但 [Optional 不是](https://www.sitepoint.com/how-optional-breaks-the-monad-laws-and-why-it-matters/)。有关 Monads 的更多信息，[一袋花生可能会有所帮助](https://medium.com/beingprofessional/understanding-functor-and-monad-with-a-bag-of-peanuts-8fa702b3f69e)。

现在让我们看看arrow-core模块中的第一个数据类型。

### 3.2. ID

Id是 Arrow 中最简单的包装器。

我们可以使用构造函数或工厂方法创建它：

```java
val id = Id("foo")
val justId = Id.just("foo");

```

而且，它有一个extract方法来检索包装的值：

```java
Assert.assertEquals("foo", id.extract())
Assert.assertEquals(justId, id)
```

Id类满足 Monad 模式的要求。

### 3.3. 选项

Option是一种数据类型，用于模拟可能不存在的值，类似于 Java 的 Optional。

虽然从技术上讲它不是 Monad，但它仍然非常有用。

它可以包含两种类型：值周围的 Some 包装器或没有值时的None。

我们有几种不同的方法来创建Option：

```java
val factory = Option.just(42)
val constructor = Option(42)
val emptyOptional = Option.empty<Integer>()
val fromNullable = Option.fromNullable(null)

Assert.assertEquals(42, factory.getOrElse { -1 })
Assert.assertEquals(factory, constructor)
Assert.assertEquals(emptyOptional, fromNullable)
```

现在，这里有一个棘手的问题，那就是工厂方法和构造函数对null的行为不同：

```java
val constructor : Option<String?> = Option(null)
val fromNullable : Option<String?> = Option.fromNullable(null)
Assert.assertNotEquals(constructor, fromNullable)
```

我们更喜欢第二种，因为它没有KotlinNullPointerException 风险：

```java
try {
    constructor.map { s -> s!!.length }
} catch (e : KotlinNullPointerException) {
    fromNullable.map { s -> s!!.length }
}
```

### 3.3. 任何一个

正如我们之前看到的，Option可以没有值 ( None ) 或一些值 ( Some ) 。

在这条道路上走得更远，可以有两个值之一。对于两个值的类型， Either有两个通用参数，分别表示为右和左：

```java
val rightOnly : Either<String,Int> = Either.right(42)
val leftOnly : Either<String,Int> = Either.left("foo")
```

此类被设计为右偏。因此，正确的分支应该包含业务价值，比如某些计算的结果。左分支可以包含错误消息甚至异常。

因此，值提取器方法 ( getOrElse ) 被设计为向右：

```java
Assert.assertTrue(rightOnly.isRight())
Assert.assertTrue(leftOnly.isLeft())
Assert.assertEquals(42, rightOnly.getOrElse { -1 })
Assert.assertEquals(-1, leftOnly.getOrElse { -1 })
```

甚至map和flatMap 方法也被设计为使用右侧并跳过左侧：

```java
Assert.assertEquals(0, rightOnly.map { it % 2 }.getOrElse { -1 })
Assert.assertEquals(-1, leftOnly.map { it % 2 }.getOrElse { -1 })
Assert.assertTrue(rightOnly.flatMap { Either.Right(it % 2) }.isRight())
Assert.assertTrue(leftOnly.flatMap { Either.Right(it % 2) }.isLeft())
```

我们将在第 4 部分研究如何使用Either进行错误处理。

### 3.4. 评价

Eval是一个 Monad，旨在控制操作的评估。它内置了对 memoization 和 eager 和 lazy evaluation 的支持。

使用now工厂方法，我们可以从已经计算的值创建一个Eval实例：

```java
val now = Eval.now(1)
```

map和flatMap操作将延迟执行：

```java
var counter : Int = 0
val map = now.map { x -> counter++; x+1 }
Assert.assertEquals(0, counter)

val extract = map.value()
Assert.assertEquals(2, extract)
Assert.assertEquals(1, counter)
```

正如我们所见，计数器仅在调用value 方法后发生变化。

后面 的工厂方法将从一个函数创建一个Eval实例。评估将推迟到调用value时，结果将被记忆：

```java
var counter : Int = 0
val later = Eval.later { counter++; counter }
Assert.assertEquals(0, counter)

val firstValue = later.value()
Assert.assertEquals(1, firstValue)
Assert.assertEquals(1, counter)

val secondValue = later.value()
Assert.assertEquals(1, secondValue)
Assert.assertEquals(1, counter)
```

三厂始终如一。它创建一个Eval实例，每次调用该值时都会重新计算给定的函数：

```java
var counter : Int = 0
val later = Eval.always { counter++; counter }
Assert.assertEquals(0, counter)

val firstValue = later.value()
Assert.assertEquals(1, firstValue)
Assert.assertEquals(1, counter)

val secondValue = later.value()
Assert.assertEquals(2, secondValue)
Assert.assertEquals(2, counter)
```

## 4. 函数式数据类型的错误处理模式

通过抛出异常来处理错误有几个缺点。

对于经常和可预测地失败的方法，比如将用户输入解析为数字，抛出异常代价高昂且没有必要。成本的最大部分来自fillInStackTrace方法。事实上，在现代框架中，堆栈跟踪可能会长得离谱，而有关业务逻辑的信息却少得惊人。

此外，处理已检查的异常很容易使客户端代码不必要地复杂化。另一方面，对于运行时异常，调用者没有关于异常可能性的信息。

接下来，我们将实现一个解决方案，以确定偶数输入数的最大约数是否为平方数。用户输入将作为字符串到达。连同这个例子，我们将研究 Arrow 的数据类型如何帮助处理错误

### 4.1. 使用选项进行错误处理

首先，我们将输入字符串解析为整数。

幸运的是，Kotlin 有一个方便的、异常安全的方法：

```java
fun parseInput(s : String) : Option<Int> = Option.fromNullable(s.toIntOrNull())
```

我们将解析结果包装到Option中。然后，我们将使用一些自定义逻辑来转换这个初始值：

```java
fun isEven(x : Int) : Boolean // ...
fun biggestDivisor(x: Int) : Int // ...
fun isSquareNumber(x : Int) : Boolean // ...
```

由于Option的设计，我们的业务逻辑不会被异常处理和 if-else 分支弄得一团糟：

```java
fun computeWithOption(input : String) : Option<Boolean> {
    return parseInput(input)
      .filter(::isEven)
      .map(::biggestDivisor)
      .map(::isSquareNumber)
}
```

如我们所见，它是纯业务代码，没有技术细节的负担。

让我们看看客户端如何处理结果：

```java
fun computeWithOptionClient(input : String) : String {
    val computeOption = computeWithOption(input)
    return when(computeOption) {
        is None -> "Not an even number!"
        is Some -> "The greatest divisor is square number: ${computeOption.t}"
    }
}
```

这很好，但是客户端没有关于输入错误的详细信息。

现在，让我们看看如何使用Either提供错误情况的更详细描述。

### 4.2. 错误处理

我们有几个选项可以使用Either返回有关错误情况的信息。在左侧，我们可以包含字符串消息、错误代码，甚至是异常。

现在，我们为此创建了一个密封类：

```java
sealed class ComputeProblem {
    object OddNumber : ComputeProblem()
    object NotANumber : ComputeProblem()
}
```

我们将此类包含在返回的Either中。在 parse 方法中，我们将使用cond工厂函数：

```java
Either.cond( /Condition/, /Right-side provider/, /Left-side provider/)
```

因此，我们将在 parseInput方法中使用Either 而不是Option ：

```java
fun parseInput(s : String) : Either<ComputeProblem, Int> =
  Either.cond(s.toIntOrNull() != null, { -> s.toInt() }, { -> ComputeProblem.NotANumber } )
```

这意味着Either将填充 数字或 错误对象。

所有其他功能将与以前相同。但是，Either的过滤方法不同。它不仅需要谓词，还需要谓词假分支左侧的提供者：

```java
fun computeWithEither(input : String) : Either<ComputeProblem, Boolean> {
    return parseInput(input)
      .filterOrElse(::isEven) { -> ComputeProblem.OddNumber }
      .map (::biggestDivisor)
      .map (::isSquareNumber)
}
```

这是因为，我们需要提供 Either的另一端，以防我们的过滤器返回 false。

现在客户将确切地知道他们的输入出了什么问题：

```java
fun computeWithEitherClient(input : String) {
    val computeWithEither = computeWithEither(input)
    when(computeWithEither) {
        is Either.Right -> "The greatest divisor is square number: ${computeWithEither.b}"
        is Either.Left -> when(computeWithEither.a) {
            is ComputeProblem.NotANumber -> "Wrong input! Not a number!"
            is ComputeProblem.OddNumber -> "It is an odd number!"
        }
    }
}
```

## 5.总结

Arrow 库的创建是为了支持 Kotlin 中的功能特性。我们调查了 arrow-core 包中提供的数据类型。然后我们使用Optional和Either进行函数式错误处理。
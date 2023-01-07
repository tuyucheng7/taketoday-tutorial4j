## 1. 概述

在本教程中，我们将了解函数式编程范式的核心原则以及如何在Java编程语言中实践它们。

我们还将介绍一些高级函数式编程技术。

这将帮助我们评估我们从函数式编程中获得的好处，尤其是在Java中。

## 2. 什么是函数式编程？

基本上，[函数式编程](https://www.baeldung.com/cs/functional-programming)是一种编写计算机程序的风格，将计算视为评估数学函数。

在数学中，函数是将输入集与输出集相关联的表达式。

重要的是，函数的输出仅取决于其输入。更有趣的是，我们可以将两个或多个函数组合在一起以获得一个新函数。

### 2.1. λ微积分

要理解为什么数学函数的这些定义和属性在编程中很重要，我们必须稍微回顾一下过去。

在 1930 年代，数学家 Alonzo Church 开发了一个形式化系统来表达基于函数抽象的计算。这种通用的计算模型后来被称为[lambda 演算](https://plato.stanford.edu/entries/lambda-calculus/)。

Lambda 演算对开发编程语言理论产生了巨大影响，尤其是函数式编程语言。通常，函数式编程语言实现 lambda 演算。

由于 lambda 演算侧重于函数组合，函数式编程语言提供了在函数组合中组合软件的表达方式。

### 2.2. 编程范式的分类

当然，函数式编程并不是实践中唯一的编程风格。从广义上讲，编程风格可以分为命令式和声明式编程范式。

命令式方法将程序定义为一系列语句，这些语句会更改程序的状态，直到达到最终状态。

过程式编程是一种命令式编程，我们使用过程或子例程构建程序。称为[面向对象编程 (OOP) 的](https://www.baeldung.com/cs/oop-vs-functional)流行编程范例之一扩展了过程编程概念。

相比之下，声明式方法表达了计算的逻辑，而不是根据一系列语句来描述其控制流。

简而言之，声明式方法的重点是定义程序必须实现什么，而不是它应该如何实现。函数式编程是声明式编程语言的一个子集。

这些类别有更多的子类别，分类法变得相当复杂，但我们不会在本教程中深入探讨。

### 2.3. 编程语言的分类

现在我们将尝试了解编程语言是如何根据它们对函数式编程的支持来划分的。

纯函数式语言，例如 Haskell，只允许纯函数式程序。

其他语言允许函数式和过程式程序，被认为是不纯的函数式语言。许多语言都属于这一类，包括 Scala、Kotlin 和 Java。

重要的是要了解当今大多数流行的编程语言都是通用语言，因此它们倾向于支持多种编程范例。

## 3. 基本原则和概念

本节将介绍函数式编程的一些基本原则以及如何在Java中采用它们。

请注意，我们将使用的许多功能并不总是Java的一部分，建议使用Java8 或更高版本以有效地练习函数式编程。

### 3.1. 一等函数和高阶函数

如果一种编程语言将函数视为一等公民，则称它具有一等函数。

这意味着允许功能支持通常对其他实体可用的所有操作。这些包括将函数分配给变量，将它们作为参数传递给其他函数以及将它们作为其他函数的值返回。

这个属性使得在函数式编程中定义高阶函数成为可能。高阶函数能够接收函数作为参数并返回一个函数作为结果。这进一步启用了函数式编程中的多种技术，例如函数组合和柯里化。

传统上，只能使用函数接口或匿名内部类等构造在Java中传递函数。功能接口只有一个抽象方法，也称为单一抽象方法 (SAM) 接口。

假设我们必须为Collections.sort方法提供一个自定义比较器：

```java
Collections.sort(numbers, new Comparator<Integer>() {
    @Override
    public int compare(Integer n1, Integer n2) {
        return n1.compareTo(n2);
    }
});
```

正如我们所看到的，这是一种乏味和冗长的技术——当然不是鼓励开发人员采用函数式编程的东西。

幸运的是，Java 8 带来了许多新特性来简化这个过程，例如 lambda 表达式、方法引用和预定义的函数接口。

让我们看看 lambda 表达式如何帮助我们完成相同的任务：

```java
Collections.sort(numbers, (n1, n2) -> n1.compareTo(n2));
```

这样肯定更简洁易懂。

但是，请注意，虽然这可能会给我们一种在Java中将函数用作一等公民的印象，但事实并非如此。

在 lambda 表达式的语法糖的背后，Java 仍然将它们包装到函数接口中。因此，Java 将 lambda 表达式视为Object，这是Java中真正的一等公民。

### 3.2. 纯函数

纯函数的定义强调纯函数应该只根据参数返回一个值，并且应该没有副作用。

这听起来与Java中的所有最佳实践完全相反。

作为面向对象的语言，Java 建议将封装作为核心编程实践。它鼓励隐藏对象的内部状态，只公开访问和修改它所必需的方法。所以，这些方法不是严格意义上的纯函数。

当然，封装等面向对象的原则只是建议，在Java中不具有约束力。

事实上，开发人员最近开始意识到定义无副作用的不可变状态和方法的价值。

假设我们想要找到刚刚排序的所有数字的总和：

```java
Integer sum(List<Integer> numbers) {
    return numbers.stream().collect(Collectors.summingInt(Integer::intValue));
}
```

此方法仅取决于它接收到的参数，因此它是确定性的。此外，它不会产生任何副作用。

副作用可以是方法预期行为之外的任何事物。例如，副作用可以像更新本地或全局状态或在返回值之前保存到数据库一样简单。(纯粹主义者也将日志记录视为副作用。)

那么，让我们看看我们如何处理合法的副作用。例如，出于真正的原因，我们可能需要将结果保存在数据库中。函数式编程中有一些技术可以在保留纯函数的同时处理副作用。

我们将在后面的部分讨论其中的一些。

### 3.3. 不变性

不变性是函数式编程的核心原则之一，它指的是一个实体在被实例化后不能被修改的属性。

在函数式编程语言中，这是由语言级别的设计支持的。但是在Java中，我们必须自己决定创建不可变数据结构。

请注意，Java 本身提供了几种内置的不可变类型，例如String。这主要是出于安全原因，因为我们在类加载中大量使用String并将其用作基于哈希的数据结构中的键。还有其他几种内置的不可变类型，例如原始包装器和数学类型。

但是我们在Java中创建的数据结构呢？当然，默认情况下它们不是不可变的，我们必须进行一些更改才能实现不可变。

使用final关键字就是其中之一，但还不止于此：

```java
public class ImmutableData {
    private final String someData;
    private final AnotherImmutableData anotherImmutableData;
    public ImmutableData(final String someData, final AnotherImmutableData anotherImmutableData) {
        this.someData = someData;
        this.anotherImmutableData = anotherImmutableData;
    }
    public String getSomeData() {
        return someData;
    }
    public AnotherImmutableData getAnotherImmutableData() {
        return anotherImmutableData;
    }
}

public class AnotherImmutableData {
    private final Integer someOtherData;
    public AnotherImmutableData(final Integer someData) {
        this.someOtherData = someData;
    }
    public Integer getSomeOtherData() {
        return someOtherData;
    }
}
```

请注意，我们必须努力遵守一些规则：

-   不可变数据结构的所有字段都必须是不可变的。
-   这也必须适用于所有嵌套类型和集合(包括它们包含的内容)。
-   根据需要应该有一个或多个构造函数用于初始化。
-   应该只有访问器方法，可能没有副作用。

每次都完全正确并不容易，尤其是当数据结构开始变得复杂时。

但是，一些外部库可以使在Java中处理不可变数据变得更加容易。例如，[Immutables](https://www.baeldung.com/immutables)和[Project Lombok](https://www.baeldung.com/intro-to-project-lombok)提供了现成的框架，用于在Java中定义不可变数据结构。

### 3.4. 参考透明度

引用透明性可能是函数式编程中较难理解的原则之一，但这个概念非常简单。

如果用相应的值替换它对程序的行为没有影响，我们称该表达式是引用透明的。

这在函数式编程中启用了一些强大的技术，例如高阶函数和惰性求值。

为了更好地理解这一点，让我们举个例子：

```java
public class SimpleData {
    private Logger logger = Logger.getGlobal();
    private String data;
    public String getData() {
        logger.log(Level.INFO, "Get data called for SimpleData");
        return data;
    }
    public SimpleData setData(String data) {
        logger.log(Level.INFO, "Set data called for SimpleData");
        this.data = data;
        return this;
    }
}
```

这是Java中典型的 POJO 类，但我们有兴趣了解它是否提供了引用透明性。

让我们观察以下语句：

```java
String data = new SimpleData().setData("Baeldung").getData();
logger.log(Level.INFO, new SimpleData().setData("Baeldung").getData());
logger.log(Level.INFO, data);
logger.log(Level.INFO, "Baeldung");
```

对logger的三个调用在语义上是等价的，但不是引用透明的。

第一次调用不是引用透明的，因为它会产生副作用。如果我们像在第三次调用中一样用它的值替换此调用，我们将错过日志。

第二次调用也不是引用透明的，因为SimpleData是可变的。在程序中的任何地方调用data.setData都会使它很难被替换为它的值。

因此，为了引用透明性，我们需要我们的函数是纯粹的和不可变的。这是我们之前讨论的两个先决条件。

作为引用透明性的一个有趣结果，我们生成了上下文无关代码。换句话说，我们可以以任何顺序和上下文运行它们，这会导致不同的优化可能性。

## 4. 函数式编程技巧

我们之前讨论的函数式编程原则使我们能够使用多种技术从函数式编程中获益。

在本节中，我们将介绍其中的一些流行技术，并了解如何在Java中实现它们。

### 4.1. 功能组合

函数组合是指通过组合更简单的函数来组合复杂的函数。

这主要是在Java中使用函数接口实现的，函数接口是 lambda 表达式和方法引用的目标类型。

通常，任何具有单个抽象方法的接口都可以用作功能接口。因此，我们可以很容易地定义一个功能接口。

然而，[Java 8在包](https://www.baeldung.com/java-8-functional-interfaces)java.util.function下默认为我们提供了很多功能接口用于不同的用例。

这些函数式接口中的许多都根据默认方法和静态方法提供对函数组合的支持。让我们选择Function接口来更好地理解这一点。

函数是一种简单而通用的函数式接口，它接受一个参数并产生一个结果。

它还提供了两个默认方法，compose和andThen，这将帮助我们进行函数组合：

```java
Function<Double, Double> log = (value) -> Math.log(value);
Function<Double, Double> sqrt = (value) -> Math.sqrt(value);
Function<Double, Double> logThenSqrt = sqrt.compose(log);
logger.log(Level.INFO, String.valueOf(logThenSqrt.apply(3.14)));
// Output: 1.06
Function<Double, Double> sqrtThenLog = sqrt.andThen(log);
logger.log(Level.INFO, String.valueOf(sqrtThenLog.apply(3.14)));
// Output: 0.57
```

这两种方法都允许我们将多个函数组合成一个函数，但提供不同的语义。虽然compose首先应用参数中传递的函数，然后应用调用它的函数，andThen以相反的方式执行相同的操作。

其他几个函数式接口在函数组合中使用了有趣的方法，例如Predicate接口中的默认方法and、or 和negate 。虽然这些功能接口接受单个参数，但有[两个特殊化](https://www.baeldung.com/java-8-functional-interfaces#Specializations)，例如BiFunction和BiPredicate。

### 4.2. 单子

许多函数式编程概念源自[范畴论](https://plato.stanford.edu/entries/category-theory/)，这是数学中函数的一般理论。它提出了几个类别的概念，例如函子和自然变换。

对我们来说，重要的是要知道这是在函数式编程中使用 monad 的基础。

形式上，monad 是一种允许通用地构造程序的抽象。因此，monad允许我们包装一个值，应用一组转换，并在应用所有转换后取回值。

当然，任何 monad 都需要遵循三个法则——左恒等式、右恒等式和结合律——但我们不会在这里详述。

在Java中，有一些我们经常使用的 monad，例如Optional和Stream：

```java
Optional.of(2).flatMap(f -> Optional.of(3).flatMap(s -> Optional.of(f + s)))
```

为什么我们称Optional为 monad？

这里Optional允许我们使用 的方法包装一个值并应用一系列转换。我们正在使用flatMap方法应用添加另一个包装值的转换。

我们可以证明Optional遵循单子的三个定律。然而，在某些情况下， Optional确实违反了 monad 法则。但对于大多数实际情况，它对我们来说应该足够好了。

如果我们了解 monads 的基础知识，我们很快就会意识到Java中还有许多其他示例，例如Stream 和CompletableFuture。它们帮助我们实现不同的目标，但它们都有一个标准的组合，在这个组合中处理上下文操作或转换。

当然，我们可以在Java中定义自己的 monad 类型来实现不同的目标，例如 log monad、report monad 或 audit monad。例如，monad 是一种函数式编程技术，用于处理函数式编程中的副作用。

### 4.3. 柯里化

[柯里化](https://www.baeldung.com/java-currying)是一种数学技术，可将采用多个参数的函数转换为一系列采用单个参数的函数。

在函数式编程中，它为我们提供了一种强大的组合技术，我们无需使用所有参数调用函数。

此外，柯里化函数在接收到所有参数之前不会实现其效果。

在 Haskell 等纯函数式编程语言中，柯里化得到了很好的支持。事实上，默认情况下所有函数都是柯里化的。

然而，在Java中它并不是那么简单：

```java
Function<Double, Function<Double, Double>> weight = mass -> gravity -> mass  gravity;

Function<Double, Double> weightOnEarth = weight.apply(9.81);
logger.log(Level.INFO, "My weight on Earth: " + weightOnEarth.apply(60.0));

Function<Double, Double> weightOnMars = weight.apply(3.75);
logger.log(Level.INFO, "My weight on Mars: " + weightOnMars.apply(60.0));
```

这里我们定义了一个函数来计算我们在行星上的重量。虽然我们的质量保持不变，但重力因我们所在的星球而异。

我们 可以通过仅传递重力来部分应用该函数来为特定行星定义函数。此外，我们可以将这个部分应用的函数作为任意组合的参数或返回值传递。

柯里化依赖于语言来提供两个基本特性：lambda 表达式和闭包。Lambda 表达式是匿名函数，可帮助我们将代码视为数据。我们之前已经看到了如何使用函数式接口来实现它们。

lambda 表达式可以关闭其词法范围，我们将其定义为闭包。

让我们看一个例子：

```java
private static Function<Double, Double> weightOnEarth() {	
    final double gravity = 9.81;	
    return mass -> mass  gravity;
}
```

请注意我们在上面的方法中返回的 lambda 表达式如何依赖于封闭变量，我们称之为闭包。与其他函数式编程语言不同，Java 有一个限制，即封闭范围必须是[final 或有效的 final](https://www.baeldung.com/java-effectively-final)。

作为一个有趣的结果，柯里化还允许我们在Java中创建一个任意数量的函数式接口。

### 4.4. 递归

[递归](https://www.baeldung.com/java-recursion)是函数式编程中的另一种强大技术，它允许我们将问题分解成更小的部分。递归的主要好处是它可以帮助我们消除副作用，这是任何命令式循环的典型特征。

让我们看看如何使用递归计算数字的阶乘：

```java
Integer factorial(Integer number) {
    return (number == 1) ? 1 : number  factorial(number - 1);
}
```

在这里，我们递归地调用相同的函数，直到我们到达基本情况，然后开始计算我们的结果。

请注意，我们在计算每个步骤的结果之前或在计算开头的单词中进行递归调用。因此，这种递归风格也称为头递归。

这种递归的缺点是每一步都必须保持所有先前步骤的状态，直到我们到达基本情况。这对于小数来说并不是真正的问题，但是对于大数保持状态可能是低效的。

解决方案是递归的一种略有不同的实现，称为尾递归。这里我们确保递归调用是函数进行的最后一次调用。

让我们看看如何重写上述函数以使用尾递归：

```java
Integer factorial(Integer number, Integer result) {
    return (number == 1) ? result : factorial(number - 1, result  number);
}
```

注意函数中累加器的使用，消除了在递归的每一步都保持状态的需要。这种风格的真正好处是利用编译器优化，编译器可以决定放弃当前函数的堆栈帧，这种技术称为尾调用消除。

虽然 Scala 等许多语言都支持尾调用消除，但Java仍然不支持这一点。这是Java积压工作的一部分，可能会作为[Project Loom](http://cr.openjdk.java.net/~rpressler/loom/Loom-Proposal.html)下提议的更大更改的一部分以某种形式出现。

## 5. 为什么函数式编程很重要

到现在为止，我们可能想知道我们为什么要付出这么多努力。对于具有Java背景的人来说，函数式编程要求的转变并非微不足道。因此，在Java中采用函数式编程应该有一些真正有前途的优势。

在包括Java在内的任何语言中采用函数式编程的最大优势是纯函数和不可变状态。如果我们回想起来，大多数编程挑战都以某种方式植根于副作用和可变状态。简单地摆脱它们可以使我们的程序更易于阅读、推理、测试和维护。

声明式编程导致非常简洁和可读的程序。作为声明式编程的一个子集，函数式编程提供了多种结构，例如高阶函数、函数组合和函数链。想一想 Stream API 在处理数据操作方面为Java8 带来的好处。

但除非完全准备好，否则不要试图切换。请注意，函数式编程不是我们可以立即使用并从中受益的简单设计模式。

函数式编程更多地改变了我们如何推理问题及其解决方案以及如何构建算法。

所以，在我们开始使用函数式编程之前，我们必须训练自己从函数的角度来思考我们的程序。

## 6.Java适合吗？

很难否认函数式编程的好处，但Java是合适的选择吗？

从历史上看，Java 演变为一种更适合面向对象编程的通用编程语言。甚至考虑使用Java8 之前的函数式编程也是乏味的！但在Java8 之后，情况肯定发生了变化。

Java 中没有真正的函数类型这一事实违背了函数式编程的基本原则。伪装成 lambda 表达式的功能接口在很大程度上弥补了这一点，至少在语法上是这样。

然后，Java中的类型本质上是可变的，我们必须编写如此多的样板来创建不可变类型，这无济于事。

我们期望从函数式编程语言中获得Java中缺失或困难的其他东西。例如，Java 中参数的默认评估策略是 eager。但是懒惰评估是函数式编程中更有效和推荐的方式。

我们仍然可以使用运算符短路和功能接口在Java中实现惰性求值，但它涉及更多。

该列表当然不完整，可能包括带有类型擦除的泛型支持、缺少对尾部调用优化的支持和其他内容。但是，我们有一个广泛的想法。

Java绝对不适合在函数式编程中从头开始一个程序。

但是如果我们已经有一个用Java编写的现有程序，可能是面向对象的编程呢？没有什么能阻止我们获得函数式编程的一些好处，尤其是在Java8 中。

这就是函数式编程对Java开发人员的大部分好处所在。面向对象编程与函数式编程优势的结合可以走很长的路。

## 七、总结

在本文中，我们了解了函数式编程的基础知识。我们介绍了基本原则以及如何在Java中采用它们。

此外，我们还通过Java示例讨论了函数式编程中的一些流行技术。

最后，我们介绍了采用函数式编程的一些好处，并回答了Java是否适用于此。
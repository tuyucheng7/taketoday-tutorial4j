##  1. 概述

使用final关键字的性能优势是Java开发人员中非常流行的争论话题。根据我们应用它的位置， final关键字可以有不同的目的和不同的性能影响。

在本教程中，我们将探索在我们的代码中使用final关键字是否有任何性能优势。我们将研究在变量、方法和类级别上使用final的性能影响。

除了性能，我们还将提到使用final关键字的设计方面。最后，我们将建议我们是否应该使用它以及出于什么原因应该使用它。

## 2.局部变量

当[final](https://www.baeldung.com/java-final) 应用于局部变量时，它的值必须恰好赋值一次。

我们可以在最终变量声明或类构造函数中赋值。如果我们稍后尝试更改最终变量值，编译器将抛出错误。

### 2.1. 性能测试

让我们看看将final关键字应用于我们的局部变量是否可以提高性能。

我们将使用[JMH 工具](https://www.baeldung.com/java-microbenchmark-harness)来测量基准方法的平均执行时间。在我们的基准测试方法中，我们将对非最终局部变量进行简单的字符串连接：

```java
@Benchmark
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public static String concatNonFinalStrings() {
    String x = "x";
    String y = "y";
    return x + y;
}
```

接下来，我们将重复相同的性能测试，但这次使用最终局部变量：

```java
@Benchmark
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public static String concatFinalStrings() {
    final String x = "x";
    final String y = "y";
    return x + y;
}
```

JMH 将负责运行预热迭代，以便让[JIT 编译器](https://www.baeldung.com/ahead-of-time-compilation)优化启动。最后，让我们看一下以纳秒为单位的测量平均性能：

```bash
Benchmark                              Mode  Cnt  Score   Error  Units
BenchmarkRunner.concatFinalStrings     avgt  200  2,976 ± 0,035  ns/op
BenchmarkRunner.concatNonFinalStrings  avgt  200  7,375 ± 0,119  ns/op
```

在我们的示例中，使用 final 局部变量可以使执行速度提高 2.5 倍。

### 2.2. 静态代码优化

字符串连接示例演示了final关键字如何帮助编译器静态优化代码。

使用非最终局部变量，编译器生成以下字节码来连接两个字符串：

```java
NEW java/lang/StringBuilder
DUP
INVOKESPECIAL java/lang/StringBuilder.<init> ()V
ALOAD 0
INVOKEVIRTUAL java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
ALOAD 1
INVOKEVIRTUAL java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
INVOKEVIRTUAL java/lang/StringBuilder.toString ()Ljava/lang/String;
ARETURN
```

通过添加final关键字，我们帮助编译器得出总结，字符串连接结果实际上永远不会改变。因此，编译器能够完全避免字符串连接并静态优化生成的字节码：

```java
LDC "xy"
ARETURN
```

我们应该注意到，在大多数情况下，将final添加到我们的局部变量不会像本例中那样带来显着的性能优势。

## 3.实例和类变量

我们可以将final关键字应用于实例或类变量。这样，我们确保他们的价值分配只能完成一次。我们可以在实例初始化块或构造函数中在最终实例变量声明时赋值。

通过在类的成员变量中添加[static](https://www.baeldung.com/java-static)关键字来声明类变量。此外，通过将final关键字应用于类变量，我们定义了一个常量。我们可以在常量声明或静态初始化块中赋值：

```java
static final boolean doX = false;
static final boolean doY = true;
```

让我们用使用这些布尔常量的条件编写一个简单的方法：

```java
Console console = System.console();
if (doX) {
    console.writer().println("x");
} else if (doY) {
    console.writer().println("y");
}
```

接下来，让我们从布尔类变量中删除final关键字并比较类生成的字节码：

-   使用非最终类变量的示例——76 行字节码
-   使用最终类变量(常量)的示例——39 行字节码

通过将final关键字添加到类变量中，我们再次帮助编译器执行静态代码优化。编译器将简单地用它们的实际值替换最终类变量的所有引用。

但是，我们应该注意到，像这样的例子很少会用在现实生活中的Java应用程序中。将变量声明为final 只会对实际应用程序的性能产生较小的积极影响。

## 4. 最终有效

术语[effectively final](https://www.baeldung.com/java-effectively-final) variable 是在Java8 中引入的。如果一个变量没有显式声明为 final，但它的值在初始化后永远不会改变，那么它就是 effectively final。

effectively final 变量的主要[目的](https://www.baeldung.com/java-lambda-effectively-final-local-variables) 是使 lambda 表达式能够使用未明确声明为 final 的局部变量。但是，Java编译器不会像对 final 变量那样对有效的 final 变量执行静态代码优化。

## 5.类和方法

final关键字在应用于类和方法时具有不同的用途。当我们将final关键字应用于一个类时，该类就不能被子类化。当我们将它应用于一个方法时，该方法就不能被覆盖。

没有报告将final应用于类和方法的性能优势。此外，final 类和方法可能会给开发人员带来极大的不便，因为它们限制了我们重用现有代码的选择。因此，鲁莽地使用final会损害良好的面向对象设计原则。

创建最终类或方法有一些正当理由，例如强制不变性。但是，性能优势并不是在类和方法级别使用final的充分理由。

## 6.性能与简洁设计

除了性能，我们可能会考虑使用final的其他原因。final关键字有助于提高代码的可读性和可理解性。让我们看看final如何传达设计选择的几个例子：

-   最终类是防止扩展的设计决策——这可能是通往[不可变对象的途径](https://www.baeldung.com/java-immutable-object)
-   方法被声明为 final 以防止子类不兼容
-   方法参数被声明为 final 以防止副作用
-   final 变量在设计上是只读的

因此，我们应该使用final来与其他开发人员交流设计选择。此外，应用于变量的final关键字可以作为编译器执行较小性能优化的有用提示。

## 七、总结

在本文中，我们研究了使用 final关键字的性能优势。在示例中，我们展示了将final关键字应用于变量会对性能产生轻微的积极影响。然而，将final关键字应用于类和方法不会带来任何性能优势。

我们证明，与 final 变量不同，编译器实际上不会使用 final 变量来执行静态代码优化。最后，除了性能之外，我们还研究了在不同级别应用final关键字的设计含义。
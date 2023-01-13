## 1. 概述

在本文中，我们介绍一个名为[Lambda Behave](https://github.com/RichardWarburton/lambda-behave)的基于Java的测试框架。

顾名思义，这个测试框架旨在与Java 8 Lambdas一起使用。此外，在本文中，我们会介绍规范并查看每个规范的示例。

下面是我们需要使用到的依赖：

```xml
<dependency>           
    <groupId>com.insightfullogic</groupId>
    <artifactId>lambda-behave</artifactId>
    <version>0.4</version>
</dependency>
```

## 2. 基础知识

该框架的目标之一是实现出色的可读性，语法鼓励我们使用完整的句子而不是几个单词来描述测试用例。

我们可以利用参数化测试，当我们不想将测试用例绑定到某些预定义值时，我们可以生成随机参数。

## 3. Lambda Behave测试实现

每个规范套件都以Suite.describe开头。此时，我们有几个内置方法来声明我们的规范。所以，一个Suite就像一个JUnit测试类，规范就像JUnit中用@Test标注的方法。

为了描述一个测试，我们使用should()。同样，如果我们将期望lambda参数命名为“expect”，我们可以通过expect.that()说出我们期望从测试中得到什么结果。

如果我们想在规范之前和之后设置或删除任何数据，我们可以使用it.isSetupWith()和it.isConcludedWith()。同样，为了在Suite之前和之后做一些事情，我们可以使用it.initiatizesWith()和it.completesWith()。

让我们看一个Calculator类的简单测试规范示例：

```java
public class Calculator {

    public int add() {
        return this.x + this.y;
    }

    public int divide(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException();
        }
        return a / b;
    }
}
```

我们从Suite.describe开始，然后添加代码来初始化Calculator。

接下来，我们通过编写规范来测试add()方法：

```java
{
    Suite.describe("Lambda behave example tests", it -> {
        it.isSetupWith(() -> {
            calculator = new Calculator(1, 2);
        });
 
        it.should("Add the given numbers", expect -> {
            expect.that(calculator.add()).is(3);
        });
}
```

在这里，我们将变量命名为“it”和“expect”以提高可读性。由于这些是lambda参数名称，我们可以将它们替换为我们选择的任何名称。

should()的第一个参数使用简单的英语描述了该测试应该检查的内容。第二个参数是一个lambda，表示我们期望add()方法应该返回3。

下面我们添加另一个除以0的测试用例，并验证我们是否得到异常：

```java
it.should("Throw an exception if divide by 0", expect -> {
    expect.exception(ArithmeticException.class, () -> {
        calculator.divide(1, 0);
    });
});
```

在这种情况下，我们期待一个异常，所以我们声明expect.exception()并在其中编写应该抛出异常的代码。**请注意，每个规范的文本描述必须是唯一的**。

## 4. 数据驱动的规范

该框架允许在规范级别进行测试参数化。作为演示，我们向Calculator类添加一个方法：

```java
public int add(int a, int b) {
    return a + b;
}
```

然后我们为该方法编写一个数据驱动的测试：

```java
it.uses(2, 3, 5)
    .and(23, 10, 33)
    .toShow("%d + %d = %d", (expect, a, b, c) -> {
        expect.that(calculator.add(a, b)).is(c);
});
```

uses()方法用于指定不同列数的输入数据。前两个参数是传递给add()函数的参数，第三个参数是add()函数执行的预期结果。这些参数也可以在测试中显示的描述中使用。

toShow()用于使用参数描述测试，输出如下：

```java
0: 2 + 3 = 5 (seed: 42562700892554)(Lambda behave example tests)
1: 23 + 10 = 33 (seed: 42562700892554)(Lambda behave example tests)
```

## 5. 生成的规范-基于属性的测试

通常，当我们编写单元测试时，我们希望关注适用于我们系统的更广泛的属性。例如，当我们测试一个String反转函数时，我们可能会检查如果我们将一个特定的String反转两次，我们最终会得到原始字符串。

**基于属性的测试侧重于通用属性，无需对特定测试参数进行硬编码**。我们可以通过使用随机生成的测试用例来实现这一点。这种策略类似于使用数据驱动规范，但我们不是指定数据表，而是指定要生成的测试用例的数量。

因此，基于字符串反转属性的测试将如下所示：

```java
it.requires(2)
    .example(Generator.asciiStrings())
    .toShow("Reversing a String twice returns the original String", 
            (expect, str) -> {
                String same = new StringBuilder(str)
                    .reverse().reverse().toString();
            expect.that(same).isEqualTo(str);
            });
```

我们使用requires()方法指示了所需测试用例的数量，并使用example()子句来说明我们需要什么类型的对象以及如何使用。

此规范的输出为：

```java
0: Reversing a String twice returns the original String(ljL+qz2) 
  (seed: 42562700892554)(Lambda behave example tests)
1: Reversing a String twice returns the original String(g) 
  (seed: 42562700892554)(Lambda behave example tests)
```

### 5.1 确定性测试用例生成

当我们使用自动生成的测试用例时，隔离测试失败变得相当困难。**例如，如果我们的功能每1000次失败1次，那么自动生成仅10个案例的规范将不得不一遍又一遍地运行以观察错误**。因此，我们需要能够确定性地重新运行测试，包括以前失败的情况。

Lambda Behave能够处理这个问题。如上一个测试用例的输出所示，它打印出用于生成随机测试用例集的种子。因此，如果有任何失败，我们可以使用种子重新创建以前生成的测试用例。

我们可以查看测试用例的输出并确定种子：(seed:42562700892554)。现在，要再次生成同一组测试，我们可以使用SourceGenerator。SourceGenerator包含deterministicNumbers()方法，该方法仅将种子作为参数：

```java
it.requires(2)
    .withSource(SourceGenerator.deterministicNumbers(42562700892554L))
    .example(Generator.asciiStrings())
    .toShow("Reversing a String twice returns the original String", 
            (expect, str) -> {
                String same = new StringBuilder(str).reverse()
                    .reverse()
                    .toString();
                expect.that(same).isEqualTo(str);
            });
```

运行此测试时，我们会得到与之前看到的相同的输出。

## 6. 总结

在本文中，我们了解了如何使用Java 8 lambda表达式与一个名为Lambda Behave的流式测试框架编写单元测试。
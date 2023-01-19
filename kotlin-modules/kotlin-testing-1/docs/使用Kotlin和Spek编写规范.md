## 一、简介

规范测试框架是对单元测试框架的补充，用于测试我们的应用程序。

在本教程中，我们将介绍[Spek 框架](http://spekframework.org/)——一种用于 Java 和 Kotlin 的规范测试框架。

## 2. 什么是规范测试？

简而言之，在规范测试中，我们从规范开始，描述软件的意图，而不是它的机制。

这通常在行为驱动开发中使用，因为目的是根据我们应用程序的预定义规范验证系统。

众所周知的规范测试框架包括[Spock](http://spockframework.org/)、 [Cucumber](https://cucumber.io/)、[Jasmine](https://jasmine.github.io/)和[RSpec](http://rspec.info/)。

### 2.1. 什么是 Spek？

Spek 是一个基于 Kotlin 的 JVM 规范测试框架。它旨在用作[JUnit 5 测试引擎](https://www.baeldung.com/junit-5-kotlin)。这意味着我们可以轻松地将它插入任何已经使用 JUnit 5 的项目，以与我们可能拥有的任何其他测试一起运行。

如果需要，还可以通过使用 JUnit Platform Runner 依赖项，使用较旧的 JUnit 4 框架运行测试。

### 2.2. Maven 依赖项

要使用 Spek，我们需要将所需的依赖项添加到我们的 Maven 构建中：

```xml
<dependency>
    <groupId>org.jetbrains.spek</groupId>
    <artifactId>spek-api</artifactId>
    <version>1.1.5</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.jetbrains.spek</groupId>
    <artifactId>spek-junit-platform-engine</artifactId>
    <version>1.1.5</version>
    <scope>test</scope>
</dependency>
```

spek -api依赖项是用于测试框架的实际 API。它定义了我们的测试将使用的所有内容。spek -junit-platform-engine依赖项就是执行我们的测试所需的 JUnit 5 测试引擎。

请注意，所有 Spek 依赖项都需要彼此具有相同的版本。最新版本可在[此处](https://search.maven.org/classic/#search|gav|1|g%3A"org.jetbrains.spek" AND a%3A"spek-api")找到。

### 2.3. 第一次测试

一旦设置了 Spek，编写测试就是在正确的结构中编写正确的类的简单案例。这有点不寻常，以使其更具可读性。

Spek 要求我们的测试都继承自适当的超类——通常是Spek—— 并且我们通过将一个块传递给此类的构造函数来实现我们的测试：

```groovy
class FirstSpec : Spek({
    // Implement the test here
})
```

## 3. 测试风格

规范测试强调以尽可能可读的方式编写测试。例如，Cucumber 以人类可读的语言编写整个测试，然后将其与步骤联系起来，以便代码保持独立。

Spek 通过使用充当可读字符串的特殊方法来工作，每个方法都被赋予一个块以在适当时执行。根据我们希望测试阅读的方式，我们使用的函数有一些变化。

### 3.1. 给予/在/它

我们可以编写测试的一种方式是“给定/在/它”风格。

这使用嵌套在该结构中的名为 given、 on和 it的方法来编写我们的测试：

-   给定——设置测试的初始条件
-   on – 执行测试动作
-   it – 断言测试操作正确执行

我们可以根据需要拥有任意数量的每个块，但必须按以下顺序嵌套它们：

```groovy
class CalculatorTest : Spek({
    given("A calculator") {
        val calculator = Calculator()
        on("Adding 3 and 5") {
            val result = calculator.add(3, 5)
            it("Produces 8") {
                assertEquals(8, result)
            }
        }
    }
})
```

这个测试读起来很容易。关注测试步骤，我们可以将其理解为“给定一个计算器，将 3 和 5 相加得到 8”。

### 3.2. 形容它

我们可以编写测试的另一种方式是“描述/它”风格。相反，它对所有嵌套使用方法describe ，并继续将其用于我们的断言。

在这种情况下，我们可以根据需要编写测试来嵌套 describe方法：

```groovy
class CalculatorTest : Spek({
    describe("A calculator") {
        val calculator = Calculator()
        describe("Addition") {
            val result = calculator.add(3, 5)
            it("Produces the correct answer") {
                assertEquals(8, result)
            }
        }
    }
})
```

使用这种风格对测试强制执行的结构较少，这意味着我们在编写测试的方式上有更大的灵活性。

不幸的是，这样做的缺点是测试不像我们使用“given/on/it”时那样自然。

### 3.3. 附加样式

Spek 不强制执行这些样式，它允许关键字根据需要进行交换。唯一的要求是所有断言都存在于其中，并且在该级别找不到其他块。

可用的嵌套关键字的完整列表是：

-   给予
-   在
-   描述
-   语境

我们可以使用这些来为我们的测试提供我们想要编写它们的最佳结构。

### 3.4. 数据驱动测试

用于定义测试的机制只不过是简单的函数调用。这意味着我们可以用它们做其他事情，就像任何普通代码一样。特别是，如果我们愿意，我们可以以数据驱动的方式调用它们。

最简单的方法是遍历我们要使用的数据，并从此循环中调用适当的块：

```groovy
class DataDrivenTest : Spek({
    describe("A data driven test") {
        mapOf(
          "hello" to "HELLO",
          "world" to "WORLD"
        ).forEach { input, expected ->
            describe("Capitalising $input") {
                it("Correctly returns $expected") {
                    assertEquals(expected, input.toUpperCase())
                }
            }
        }
    }
})
```

如果需要，我们可以做各种各样的事情，但这可能是最有用的。

## 4.断言

Spek 没有规定任何使用断言的特定方式。相反，它允许我们使用我们最熟悉的任何断言框架。

显而易见的选择是org.junit.jupiter.api.Assertions类，因为我们已经在使用 JUnit 5 框架作为我们的测试运行器。

然而，我们也可以使用我们想要的任何其他断言库，如果它能使我们的测试更好——例如，[Kluent](https://github.com/MarkusAmshove/Kluent)、[Expekt](https://github.com/winterbe/expekt)或[HamKrest](https://github.com/npryce/hamkrest)。

使用这些库而不是标准的 JUnit 5 断言类的好处在于测试的可读性。

例如，上面使用 Kluent 重写的测试如下：

```groovy
class CalculatorTest : Spek({
    describe("A calculator") {
        val calculator = Calculator()
        describe("Addition") {
            val result = calculator.add(3, 5)
            it("Produces the correct answer") {
                result shouldEqual 8
            }
        }
    }
})
```

## 5. 前/后处理程序

与大多数测试框架一样，Spek 也可以在测试之前/之后执行逻辑。

顾名思义，这些是在测试本身之前或之后执行的块。

这里的选项是：

-   前组
-   后组
-   在每个测试之前
-   每次测试后

这些可以放置在任何嵌套关键字中，并将应用于该组内的所有内容。

Spek 的工作方式是，任何嵌套关键字内的所有代码都会在测试开始时立即执行，但控制块会以其块为中心的特定顺序执行。

从外到内工作，Spek 将 在嵌套在同一组中的每个 it 块之前立即执行每个 beforeEachTest 块， 并 在每个 it块 之后立即执行每个afterEachTest块。同样，Spek 将 在当前嵌套中的每个组之前立即执行每个beforeGroup 块，并在每个组之后立即执行每个 afterGroup块。

这很复杂，最好用一个例子来解释：

```groovy
class GroupTest5 : Spek({
    describe("Outer group") {
        beforeEachTest {
            System.out.println("BeforeEachTest 0")
        }
        beforeGroup {
            System.out.println("BeforeGroup 0")
        }
        afterEachTest {
            System.out.println("AfterEachTest 0")
        }
        afterGroup {
            System.out.println("AfterGroup 0")
        }
        
        describe("Inner group 1") {
            beforeEachTest {
                System.out.println("BeforeEachTest 1")
            }
            beforeGroup {
                System.out.println("BeforeGroup 1")
            }
            afterEachTest {
                System.out.println("AfterEachTest 1")
            }
            afterGroup {
                System.out.println("AfterGroup 1")
            }
            it("Test 1") {
                System.out.println("Test 1")
            }
        }
    }
})
```

运行上面的输出是：

```plaintext
BeforeGroup 0
BeforeGroup 1
BeforeEachTest 0
BeforeEachTest 1
Test 1
AfterEachTest 1
AfterEachTest 0
AfterGroup 1
AfterGroup 0
```

我们可以立即看到外部的 beforeGroup/afterGroup块围绕整个测试集，而内部的 beforeGroup/afterGroup块仅围绕同一上下文中的测试。

我们还可以看到所有 beforeGroup块都在任何 beforeEachTest 块之前执行，而 afterGroup/afterEachTest则相反。

[在 GitHub 上](https://github.com/Baeldung/kotlin-tutorials/tree/master/core-kotlin-modules/core-kotlin)可以看到一个更大的例子，显示多个组中多个测试之间的交互 。

## 6. 测试对象

很多时候，我们将为单个 Test Subject 编写单个 Spec。Spek 提供了一种方便的方式来编写它，这样它就可以自动为我们管理 Subject Under Test。为此，我们使用 SubjectSpek基类而不是 Spek类。

我们在使用这个的时候，需要在最外层声明调用 subject 块。这定义了测试对象。然后我们可以从我们的任何测试代码中引用它作为 主题。

我们可以使用它来重写我们之前的计算器测试，如下所示：

```groovy
class CalculatorTest : SubjectSpek<Calculator>({
    subject { Calculator() }
    describe("A calculator") {
        describe("Addition") {
            val result = subject.add(3, 5)
            it("Produces the correct answer") {
                assertEquals(8, result)
            }
        }
    }
})
```

它可能看起来不多，但这有助于使测试更具可读性，尤其是当需要考虑大量测试用例时。

### 6.1. Maven 依赖项

要使用主题扩展，我们需要向我们的 Maven 构建添加依赖项：

```xml
<dependency>
    <groupId>org.jetbrains.spek</groupId>
    <artifactId>spek-subject-extension</artifactId>
    <version>1.1.5</version>
    <scope>test</scope>
</dependency>
```

## 七、总结

Spek 是一个强大的框架，允许进行一些非常可读的测试，这反过来意味着组织的所有部分都可以阅读它们。

这对于允许所有同事为测试整个应用程序做出贡献非常重要。
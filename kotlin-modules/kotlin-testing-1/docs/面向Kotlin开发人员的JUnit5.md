## 一、简介

新发布的[JUnit 5](http://junit.org/junit5/)是著名的 Java 测试框架的下一个版本。此版本包含许多专门针对 Java 8 中引入的功能的特性——它主要围绕 lambda 表达式的使用构建。

在这篇简短的文章中，我们将展示同一工具如何与 Kotlin 语言一起工作。

## 2. 简单的 JUnit 5 测试

在最简单的情况下，用 Kotlin 编写的 JUnit 5 测试完全符合预期。我们编写一个测试类，使用@Test注解来注解我们的测试方法，编写我们的代码，并执行断言：

```scala
class CalculatorTest {
    private val calculator = Calculator()

    @Test
    fun whenAdding1and3_thenAnswerIs4() {
        Assertions.assertEquals(4, calculator.add(1, 3))
    }
}
```

这里的一切都是开箱即用的。我们可以使用标准的@Test、@BeforeAll、@BeforeEach、@AfterEach和@AfterAll注解。我们还可以与测试类中的字段进行交互，就像在 Java 中一样。

请注意，所需的导入是不同的，我们 使用Assertions类而不是Assert类进行断言。这是 JUnit 5 的标准更改，并非特定于 Kotlin。

在继续之前，让我们更改测试名称并在 Kotlin 中使用b acktick 标识符：

```java
@Test
fun `Adding 1 and 3 should be equal to 4`() {
    Assertions.assertEquals(4, calculator.add(1, 3))
}
```

现在它更具可读性！在 Kotlin 中，我们可以使用反引号声明所有变量和函数，但不建议在正常用例中这样做。

## 3.高级断言

JUnit 5 添加了一些用于使用 lambda的高级断言。这些在 Kotlin 中的工作方式与在 Java 中相同，但由于语言的工作方式，需要以略有不同的方式表达。

### 3.1. 断言异常

JUnit 5 添加了一个断言，用于判断调用何时会抛出异常。我们可以测试一个特定的调用——而不是方法中的任何调用——是否抛出预期的异常。我们甚至可以断言异常本身。

在 Java 中，我们会将 lambda 传递给对Assertions.assertThrows的调用。我们在 Kotlin 中做同样的事情，但我们可以通过在断言调用的末尾附加一个块来使代码更具可读性：

```scala
@Test
fun `Dividing by zero should throw the DivideByZeroException`() {
    val exception = Assertions.assertThrows(DivideByZeroException::class.java) {
        calculator.divide(5, 0)
    }

    Assertions.assertEquals(5, exception.numerator)
}
```

此代码的工作方式与 Java 等效代码完全相同，但更易于阅读 ，因为我们不需要在调用assertThrows函数的括号内传递 lambda。

### 3.2. 多重断言

JUnit 5 添加了同时执行多个断言的能力，它将评估所有断言并报告所有失败。

这使我们能够在单次测试运行中收集更多信息，而不是被迫修复一个错误以解决下一个错误。为此，我们调用Assertions.assertAll，传入任意数量的 lambda。

在 Kotlin中，我们需要稍微不同地处理这个问题。该函数实际上采用Executable类型的可变参数参数。

目前，不支持自动将 lambda 转换为函数式接口，因此我们需要手动完成：

```scala
fun `The square of a number should be equal to that number multiplied in itself`() {
    Assertions.assertAll(
        Executable { Assertions.assertEquals(1, calculator.square(1)) },
        Executable { Assertions.assertEquals(4, calculator.square(2)) },
        Executable { Assertions.assertEquals(9, calculator.square(3)) }
    )
}
```

### 3.3. 真假测试的供应商

有时，我们想要测试某些调用返回真值还是假值。从历史上看，我们会计算此值并根据需要调用assertTrue或assertFalse。JUnit 5 允许提供 lambda 而不是返回被检查的值。

Kotlin 允许我们以与上面看到的测试异常相同的方式传入 lambda。我们也可以传入方法引用。这在测试某些现有对象的返回值时特别有用，就像我们在这里使用List.isEmpty 所做的那样：

```scala
@Test
fun `isEmpty should return true for empty lists`() {
    val list = listOf<String>()
    Assertions.assertTrue(list::isEmpty)
}
```

### 3.4. 失败消息的供应商

在某些情况下，我们希望提供自己的错误消息以在断言失败时显示而不是默认错误消息。

通常这些都是简单的字符串，但有时我们可能希望使用计算成本高的字符串。在 JUnit 5 中，我们可以提供一个 lambda 来计算这个字符串，它只在失败时调用而不是预先计算。

这有助于使测试运行得更快并减少构建时间。这与我们之前看到的完全一样：

```scala
@Test
fun `3 is equal to 4`() {
    Assertions.assertEquals(3, 4) {
        "Three does not equal four"
    }
}
```

## 4. 数据驱动测试

JUnit 5 中的一项重大改进是对数据驱动测试的原生支持。这些在 Kotlin 中同样有效，并且在集合上使用函数映射可以使我们的测试更易于阅读和维护。

### 4.1. 测试工厂方法

处理数据驱动测试的最简单方法是使用@TestFactory注解。这替换了@Test注解，并且该方法返回一些DynamicNode实例的集合——通常通过调用DynamicTest.dynamicTest创建。

这在 Kotlin 中的工作方式完全相同，我们可以再次以更简洁的方式传递 lambda，正如我们之前看到的：

```scala
@TestFactory
fun testSquares() = listOf(
    DynamicTest.dynamicTest("when I calculate 1^2 then I get 1") { Assertions.assertEquals(1,calculator.square(1))},
    DynamicTest.dynamicTest("when I calculate 2^2 then I get 4") { Assertions.assertEquals(4,calculator.square(2))},
    DynamicTest.dynamicTest("when I calculate 3^2 then I get 9") { Assertions.assertEquals(9,calculator.square(3))}
)
```

不过，我们可以做得更好。我们可以通过对简单的输入数据列表执行一些功能映射来轻松构建我们的列表：

```scala
@TestFactory
fun testSquares() = listOf(
    1 to 1,
    2 to 4,
    3 to 9,
    4 to 16,
    5 to 25)
    .map { (input, expected) ->
        DynamicTest.dynamicTest("when I calculate $input^2 then I get $expected") {
            Assertions.assertEquals(expected, calculator.square(input))
        }
    }
```

马上，我们可以轻松地向输入列表添加更多测试用例，它会自动添加测试。

我们还可以将输入列表创建为类字段并在多个测试之间共享：

```scala
private val squaresTestData = listOf(
    1 to 1,
    2 to 4,
    3 to 9,
    4 to 16,
    5 to 25)


@TestFactory
fun testSquares() = squaresTestData
    .map { (input, expected) ->
        DynamicTest.dynamicTest("when I calculate $input^2 then I get $expected") {
            Assertions.assertEquals(expected, calculator.square(input))
        }
    }
@TestFactory
fun testSquareRoots() = squaresTestData
    .map { (expected, input) ->
        DynamicTest.dynamicTest("when I calculate the square root of $input then I get $expected") {
            Assertions.assertEquals(expected, calculator.squareRoot(input))
        }
    }
```

### 4.2. 参数化测试

JUnit 5有一些实验性扩展，可以更轻松地编写参数化测试。这些是使用org.junit.jupiter:junit-jupiter-params依赖项中的@ParameterizedTest注解完成的：

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-params</artifactId>
    <version>5.0.0</version>
</dependency>
```

最新版本可以在[Maven Central](https://search.maven.org/classic/#search|ga|1|g%3A"org.junit.jupiter" AND a%3A"junit-jupiter-params")上找到。

@MethodSource注解允许我们通过调用与测试位于同一类中的静态函数来生成测试参数。这是可能的，但在 Kotlin 中并不明显。我们必须在伴随对象中使用@JvmStatic注解：

```scala
@ParameterizedTest
@MethodSource("squares")
fun testSquares(input: Int, expected: Int) {
    Assertions.assertEquals(expected, input  input)
}

companion object {
    @JvmStatic
    fun squares() = listOf(
        Arguments.of(1, 1),
        Arguments.of(2, 4)
    )
}
```

这也意味着用于生成参数的方法必须全部放在一起，因为每个类只能有一个伴生对象。

使用参数化测试的所有其他方式在 Kotlin 中的工作方式与在 Java 中的工作方式完全相同。@CsvSource在这里需要特别注意，因为我们可以在大多数情况下使用它代替@MethodSource来获取简单的测试数据，从而使我们的测试更具可读性：

```scala
@ParameterizedTest
@CsvSource(
    "1, 1",
    "2, 4",
    "3, 9"
)
fun testSquares(input: Int, expected: Int) {
    Assertions.assertEquals(expected, input  input)
}
```

## 5. 标记测试

Kotlin 语言目前不允许对类和方法进行重复注解。这使得标签的使用稍微更加冗长，因为我们需要将它们包装在@Tags注解中：

```scala
@Tags(
    Tag("slow"),
    Tag("logarithms")
)
@Test
fun `Log to base 2 of 8 should be equal to 3`() {
    Assertions.assertEquals(3.0, calculator.log(2, 8))
}
```

这在 Java 7 中也是必需的，并且已经被 JUnit 5 完全支持。

## 6.总结

JUnit 5 添加了一些我们可以使用的强大的测试工具。这些几乎都适用于 Kotlin 语言，尽管在某些情况下它们的语法与我们在 Java 等价物中看到的略有不同。

不过，在使用 Kotlin 时，这些语法更改通常更易于阅读和使用。
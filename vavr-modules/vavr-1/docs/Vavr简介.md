## 1. 概述

在本文中，我们将探讨[Vavr](https://www.vavr.io/)到底是什么、我们为什么需要它以及如何在我们的项目中使用它。

Vavr是Java 8+的功能库，提供不可变的数据类型和功能控制结构。

### 1.1 Maven依赖

为了使用Vavr，你需要添加依赖项：

```xml
<dependency>
    <groupId>io.vavr</groupId>
    <artifactId>vavr</artifactId>
    <version>0.9.0</version>
</dependency>
```

建议始终使用最新版本。你可以通过[此链接](https://search.maven.org/search?q=a:vavr)获得它。

## 2. Option

Option的主要目标是通过利用Java类型系统消除我们代码中的空检查。

Option是Vavr中的一个对象容器，其最终目标类似于Java 8中的[Optional](https://www.baeldung.com/java-optional)。Vavr的Option实现了Serializable、Iterable，并且具有更丰富的API。

由于Java中的任何对象引用都可以有空值，因此我们通常必须在使用它之前使用if语句检查是否为空。这些检查使代码健壮和稳定：

```java
@Test
public void givenValue_whenNullCheckNeeded_thenCorrect() {
    Object possibleNullObj = null;
    if (possibleNullObj == null) {
        possibleNullObj = "someDefaultValue";
    }
    assertNotNull(possibleNullObj);
}
```

如果没有检查，应用程序可能会由于简单的NPE而崩溃：

```java
@Test(expected = NullPointerException.class)
public void givenValue_whenNullCheckNeeded_thenCorrect2() {
    Object possibleNullObj = null;
    assertEquals("somevalue", possibleNullObj.toString());
}
```

然而，这些检查使代码冗长且不那么可读，尤其是当if语句最终被嵌套多次时。

Option通过完全消除空值并为每个可能的场景用有效的对象引用替换它们来解决这个问题。

使用Option，空值将评估为None的实例，而非空值将评估为Some的实例：

```java
@Test
public void givenValue_whenCreatesOption_thenCorrect() {
    Option<Object> noneOption = Option.of(null);
    Option<Object> someOption = Option.of("val");

    assertEquals("None", noneOption.toString());
    assertEquals("Some(val)", someOption.toString());
}
```

因此，与其直接使用对象值，不如将它们包装在Option实例中，如上所示。

请注意，我们不必在调用toString之前进行检查，但我们也不必像以前那样处理NullPointerException。Option的toString在每次调用中为我们返回有意义的值。

在本节的第二个片段中，我们需要一个空检查，在尝试使用它之前，我们将在其中为变量分配一个默认值。选项可以在一行中处理这个，即使有一个空值：

```java
@Test
public void givenNull_whenCreatesOption_thenCorrect() {
    String name = null;
    Option<String> nameOption = Option.of(name);
   
    assertEquals("tuyucheng", nameOption.getOrElse("tuyucheng"));
}
```

或非空：

```java
@Test
public void givenNonNull_whenCreatesOption_thenCorrect() {
    String name = "tuyucheng";
    Option<String> nameOption = Option.of(name);

    assertEquals("tuyucheng", nameOption.getOrElse("nottuyucheng"));
}
```

请注意，在没有空检查的情况下，我们如何在一行中获取一个值或返回一个默认值。

## 3. 元组

Java中没有元组数据结构的直接等价物。元组是[函数式编程](https://www.baeldung.com/cs/functional-programming)语言中的一个常见概念。元组是不可变的，可以以类型安全的方式保存多个不同类型的对象。

Vavr将元组引入Java 8。元组的类型为Tuple1、Tuple2到Tuple8，具体取决于它们要采用的元素数量。

目前有八个元素的上限。我们像tuple._n一样访问元组的元素，其中n类似于数组中索引的概念：

```java
public void whenCreatesTuple_thenCorrect1() {
    Tuple2<String, Integer> java8 = Tuple.of("Java", 8);
    String element1 = java8._1;
    int element2 = java8._2();

    assertEquals("Java", element1);
    assertEquals(8, element2);
}
```

请注意，第一个元素是使用n==1检索的。所以元组不像数组那样使用零基。将存储在元组中的元素的类型必须在其类型声明中声明，如下所示：

```java
@Test
public void whenCreatesTuple_thenCorrect2() {
    Tuple3<String, Integer, Double> java8 = Tuple.of("Java", 8, 1.8);
    String element1 = java8._1;
    int element2 = java8._2();
    double element3 = java8._3();
        
    assertEquals("Java", element1);
    assertEquals(8, element2);
    assertEquals(1.8, element3, 0.1);
}
```

元组的作用是存储一组固定的任何类型的对象，这些对象最好作为一个单元处理并且可以传递。一个更明显的用例是从Java中的函数或方法返回多个对象。

## 4. Try

在Vavr中，Try是计算可能导致异常的容器。

由于Option包装了一个可为null的对象，因此我们不必通过if检查显式处理空值，Try包装了一个计算，因此我们不必使用try-catch块显式处理异常。

以下面的代码为例：

```java
@Test(expected = ArithmeticException.class)
public void givenBadCode_whenThrowsException_thenCorrect() {
    int i = 1 / 0;
}
```

如果没有try-catch块，应用程序就会崩溃。为了避免这种情况，你需要将语句包装在try-catch块中。使用Vavr，我们可以将相同的代码包装在Try实例中并获得结果：

```java
@Test
public void givenBadCode_whenTryHandles_thenCorrect() {
    Try<Integer> result = Try.of(() -> 1 / 0);

    assertTrue(result.isFailure());
}
```

然后可以在代码中的任何位置通过选择来检查计算是否成功。

在上面的代码片段中，我们选择简单地检查成功或失败。我们也可以选择返回一个默认值：

```java
@Test
public void givenBadCode_whenTryHandles_thenCorrect2() {
    Try<Integer> computation = Try.of(() -> 1 / 0);
    int errorSentinel = result.getOrElse(-1);

    assertEquals(-1, errorSentinel);
}
```

或者甚至明确抛出我们选择的异常：

```java
@Test(expected = ArithmeticException.class)
public void givenBadCode_whenTryHandles_thenCorrect3() {
    Try<Integer> result = Try.of(() -> 1 / 0);
    result.getOrElseThrow(ArithmeticException::new);
}
```

在上述所有情况下，我们都可以控制计算后发生的事情，这要感谢Vavr的Try。

## 5. 函数式接口

随着Java 8的到来，[函数式接口](https://www.baeldung.com/java-8-functional-interfaces)是内置的并且更易于使用，尤其是与lambda结合使用时。

然而，Java 8只提供了两个基本函数，一个只接受一个参数并产生一个结果：

```java
@Test
public void givenJava8Function_whenWorks_thenCorrect() {
    Function<Integer, Integer> square = (num) -> num * num;
    int result = square.apply(2);

    assertEquals(4, result);
}
```

第二个只接受两个参数并产生一个结果：

```java
@Test
public void givenJava8BiFunction_whenWorks_thenCorrect() {
    BiFunction<Integer, Integer, Integer> sum = (num1, num2) -> num1 + num2;
    int result = sum.apply(5, 7);

    assertEquals(12, result);
}
```

另一方面，Vavr进一步扩展了Java中函数式接口的概念，支持最多八个参数，并使用记忆、组合和柯里化方法丰富了API。

就像元组一样，这些函数式接口根据它们采用的参数数量命名：Function0、Function1、Function2等。使用Vavr，我们可以这样编写上述两个函数：

```java
@Test
public void givenVavrFunction_whenWorks_thenCorrect() {
    Function1<Integer, Integer> square = (num) -> num * num;
    int result = square.apply(2);

    assertEquals(4, result);
}
```

和这个：

```java
@Test
public void givenVavrBiFunction_whenWorks_thenCorrect() {
    Function2<Integer, Integer, Integer> sum = (num1, num2) -> num1 + num2;
    int result = sum.apply(5, 7);

    assertEquals(12, result);
}
```

当没有参数但我们仍然需要输出时，在Java 8中我们需要使用Supplier类型，在Vavr Function0中可以提供帮助：

```java
@Test
public void whenCreatesFunction_thenCorrect0() {
    Function0<String> getClazzName = () -> this.getClass().getName();
    String clazzName = getClazzName.apply();

    assertEquals("cn.tuyucheng.taketoday.vavr.VavrTest", clazzName);
}
```

五参数函数怎么样，这只是使用Function5的问题：

```java
@Test
public void whenCreatesFunction_thenCorrect5() {
    Function5<String, String, String, String, String, String> concat = (a, b, c, d, e) -> a + b + c + d + e;
    String finalString = concat.apply("Hello ", "world", "! ", "Learn ", "Vavr");

    assertEquals("Hello world! Learn Vavr", finalString);
}
```

我们还可以将静态工厂方法FunctionN.of与任何函数结合起来，以从方法引用创建Vavr函数。就像我们有以下sum方法一样：

```java
public int sum(int a, int b) {
    return a + b;
}
```

我们可以像这样从中创建一个函数：

```java
@Test
public void whenCreatesFunctionFromMethodRef_thenCorrect() {
    Function2<Integer, Integer, Integer> sum = Function2.of(this::sum);
    int summed = sum.apply(5, 6);

    assertEquals(11, summed);
}
```

## 6. 集合

Vavr团队在设计满足函数式编程要求(即持久性、不变性)的新集合API方面投入了大量精力。

Java 集合是可变的，这使它们成为程序失败的重要来源，尤其是在并发的情况下。Collection接口提供如下方法：

```java
interface Collection<E> {
    void clear();
}
```

此方法删除集合中的所有元素(产生副作用)并且不返回任何内容。创建诸如ConcurrentHashMap之类的类是为了处理已经产生的问题。

这样的类不仅增加了零边际收益，而且还降低了它试图填补其漏洞的类的性能。

有了不变性，我们就可以免费获得线程安全：无需编写新类来处理一开始就不应该出现的问题。

在Java中为集合添加不可变性的其他现有策略仍然会产生更多问题，即异常：

```java
@Test(expected = UnsupportedOperationException.class)
public void whenImmutableCollectionThrows_thenCorrect() {
    java.util.List<String> wordList = Arrays.asList("abracadabra");
    java.util.List<String> list = Collections.unmodifiableList(wordList);
    list.add("boom");
}
```

以上所有问题在Vavr集合中都不存在。

要在Vavr中创建列表：

```java
@Test
public void whenCreatesVavrList_thenCorrect() {
    List<Integer> intList = List.of(1, 2, 3);

    assertEquals(3, intList.length());
    assertEquals(new Integer(1), intList.get(0));
    assertEquals(new Integer(2), intList.get(1));
    assertEquals(new Integer(3), intList.get(2));
}
```

API也可用于在适当的位置对列表执行计算：

```java
@Test
public void whenSumsVavrList_thenCorrect() {
    int sum = List.of(1, 2, 3).sum().intValue();

    assertEquals(6, sum);
}
```

Vavr集合提供了Java集合框架中的大部分常见类，实际上，所有功能都已实现。

要点是不变性，去除void返回类型和产生副作用的API，一组更丰富的函数来操作底层元素，与Java的集合操作相比，代码非常短、健壮和紧凑。

Vavr集合的全面介绍超出了本文的范围。

## 7. 验证

Vavr将Applicative Functor的概念从函数式编程世界引入Java。用最简单的术语来说，Applicative Functor使我们能够在累积结果的同时执行一系列操作。

vavr.control.Validation类有助于错误的累积。请记住，通常，一旦遇到错误，程序就会终止。

然而，Validation继续处理和累积程序的错误以作为批处理它们。

考虑到我们正在按姓名和年龄注册用户，我们希望首先获取所有输入并决定是创建一个Person实例还是返回一个错误列表。这是我们的Person类：

```java
public class Person {
    private String name;
    private int age;

    // standard constructors, setters and getters, toString
}
```

接下来，我们创建一个名为PersonValidator的类。每个字段将通过一种方法进行验证，另一种方法可用于将所有结果组合到一个Validation实例中：

```java
class PersonValidator {
	String NAME_ERR = "Invalid characters in name: ";
	String AGE_ERR = "Age must be at least 0";

	public Validation<Seq<String>, Person> validatePerson(
		String name, int age) {
		return Validation.combine(validateName(name), validateAge(age)).ap(Person::new);
	}

	private Validation<String, String> validateName(String name) {
		String invalidChars = name.replaceAll("[a-zA-Z ]", "");
		return invalidChars.isEmpty() ? Validation.valid(name) : Validation.invalid(NAME_ERR + invalidChars);
	}

	private Validation<String, Integer> validateAge(int age) {
		return age < 0 ? Validation.invalid(AGE_ERR) : Validation.valid(age);
	}
}
```

age的规则是它应该是一个大于0的整数，name的规则是它不应该包含特殊字符：

```java
@Test
public void whenValidationWorks_thenCorrect() {
    PersonValidator personValidator = new PersonValidator();

    Validation<List<String>, Person> valid = personValidator.validatePerson("John Doe", 30);

    Validation<List<String>, Person> invalid = personValidator.validatePerson("John? Doe!4", -1);

    assertEquals("Valid(Person [name=John Doe, age=30])", valid.toString());

    assertEquals("Invalid(List(Invalid characters in name: ?!4, Age must be at least 0))", invalid.toString());
}
```

有效值包含在Validation.Valid实例中，验证错误列表包含在Validation.Invalid实例中。所以任何验证方法都必须返回两者之一。

Validation.Valid内部是Person的实例，而Validation.Invalid内部是错误列表。

## 8. Lazy

Lazy是一个容器，表示延迟计算的值，即计算被推迟到需要结果时。此外，评估值被缓存或记忆并在每次需要时一次又一次地返回，而无需重复计算：

```java
@Test
public void givenFunction_whenEvaluatesWithLazy_thenCorrect() {
    Lazy<Double> lazy = Lazy.of(Math::random);
    assertFalse(lazy.isEvaluated());
        
    double val1 = lazy.get();
    assertTrue(lazy.isEvaluated());
        
    double val2 = lazy.get();
    assertEquals(val1, val2, 0.1);
}
```

在上面的例子中，我们评估的函数是Math.random。请注意，在第二行中，我们检查了值并意识到该函数尚未执行。这是因为我们还没有表现出对返回值的兴趣。

在第三行代码中，我们通过调用Lazy.get来显示对计算值的兴趣。此时，函数执行并且Lazy.evaluated返回true。

我们还继续尝试再次获取值，以确认Lazy的记忆位。如果再次执行我们提供的函数，我们肯定会收到不同的随机数。

然而，当最终断言确认时，Lazy再次延迟返回最初计算的值。

## 9. 模式匹配

模式匹配是几乎所有函数式编程语言中的原生概念。目前Java中没有这样的东西。

相反，每当我们想要根据收到的输入执行计算或返回值时，我们都会使用多个if语句来解析要执行的正确代码：

```java
@Test
public void whenIfWorksAsMatcher_thenCorrect() {
    int input = 3;
    String output;
    if (input == 0) {
        output = "zero";
    }
    if (input == 1) {
        output = "one";
    }
    if (input == 2) {
        output = "two";
    }
    if (input == 3) {
        output = "three";
    }
    else {
        output = "unknown";
    }

    assertEquals("three", output);
}
```

我们可以在检查三种情况时突然看到代码跨越多行。每个检查占用三行代码。如果我们必须检查多达100个案例怎么办，那将是大约300行，这不是很好！

另一种选择是使用switch语句：

```java
@Test
public void whenSwitchWorksAsMatcher_thenCorrect() {
    int input = 2;
    String output;
    switch (input) {
    case 0:
        output = "zero";
        break;
    case 1:
        output = "one";
        break;
    case 2:
        output = "two";
        break;
    case 3:
        output = "three";
        break;
    default:
        output = "unknown";
        break;
    }

    assertEquals("two", output);
}
```

没有任何更好的。我们仍然平均每张支票3行。很多混乱和潜在的错误。忘记break子句在编译时不是问题，但会导致以后难以检测的错误。

在Vavr中，我们用Match方法替换了整个开关块。每个case或if语句都被Case方法调用所取代。

最后，像$()这样的原子模式替换了随后计算表达式或值的条件。我们还将其作为第二个参数提供给Case：

```java
@Test
public void whenMatchworks_thenCorrect() {
    int input = 2;
    String output = Match(input).of(
      Case($(1), "one"), 
      Case($(2), "two"), 
      Case($(3), "three"),
      Case($(), "?"));
 
    assertEquals("two", output);
}
```

注意代码是多么紧凑，平均每次检查只有一行。模式匹配API比这更强大，可以做更复杂的事情。

例如，我们可以用谓词替换原子表达式。想象一下，我们正在解析一个控制台命令以获取帮助和版本标志：

```java
Match(arg).of(
    Case($(isIn("-h", "--help")), o -> run(this::displayHelp)),
    Case($(isIn("-v", "--version")), o -> run(this::displayVersion)),
    Case($(), o -> run(() -> {
        throw new IllegalArgumentException(arg);
    }))
);
```

一些用户可能更熟悉简写版本(-v)，而其他用户可能更熟悉完整版本(-version)。一个好的设计师必须考虑所有这些情况。

不需要几个if语句，我们已经处理了多个条件。我们将在另一篇文章中详细了解模式匹配中的谓词、多重条件和副作用。

## 10. 总结

在本文中，我们介绍了流行的Java 8函数式编程库Vavr。我们已经解决了可以快速适应以改进代码的主要功能。
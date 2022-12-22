## 1. 概述

在本教程中，我们将了解如何使用JUnit库(JUnit 4与JUnit 5)测试是否引发了异常。

## 2. Junit 5

**JUnit 5 Jupiter Assertions API引入了assertThrows方法来断言异常**。

该方法需要一个预期异常的类型和一个Executable函数式接口作为参数，我们可以在其中通过lambda表达式传递被测代码：

```java
class ExceptionAssertionUnitTest {

    @Test
    void whenExceptionThrown_thenAssertionSucceeds() {
        Exception exception = assertThrows(NumberFormatException.class, () -> Integer.parseInt("1a"));

        String expectedMessage = "For input string";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
```

**如果抛出了预期的异常，assertThrows()方法会返回该异常，这使我们也可以对异常消息进行断言**。

此外，重要的是要注意，当包含的代码抛出NumberFormatException类型或其任何子类型的异常时，此断言就会满足。

这意味着如果我们将Exception作为预期的异常类型传递，任何抛出的异常都会使断言成功，因为Exception是所有异常的父类。

如果我们将上面测试的预期异常更改为RuntimeException，测试同样通过：

```java
class ExceptionAssertionUnitTest {

    @Test
    void whenDerivedExceptionThrown_thenAssertionSucceeds() {
        Exception exception = assertThrows(RuntimeException.class, () -> Integer.parseInt("1a"));

        String expectedMessage = "For input string";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
```

assertThrows()方法可以对异常断言逻辑进行更细粒度的控制，因为我们可以在代码的特定部分使用它。

## 3. Junit 4

在使用JUnit 4时，我们可以使用@Test注解的expected属性来声明我们期望在带注解的测试方法中的任何地方抛出异常。

因此，在运行测试时，如果没有抛出指定的异常，它将失败，反之则会通过：

```java
public class ExceptionAssertionUnitTest {

    @Test(expected = NullPointerException.class)
    public void whenExceptionThrown_thenExpectationSatisfied() {
        String test = null;
        test.length();
    }
}
```

在这个例子中，我们期望测试代码抛出NullPointerException。

**当我们需要验证异常的一些其他属性时，可以使用ExpectedException Rule。**

让我们看一个验证异常的message属性的示例：

```java
public class ExceptionAssertionUnitTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void whenExceptionThrown_thenRuleIsApplied() {
        exceptionRule.expect(NumberFormatException.class);
        exceptionRule.expectMessage("For input string");
        Integer.parseInt("1a");
    }
}
```

在上面的示例中，我们首先声明ExpectedException Rule。
然后在我们的测试中，我们断言尝试解析Integer值的代码将导致NumberFormatException，并且异常消息为“For input string”。

## 4. 总结

在本文中，我们介绍了JUnit 4和JUnit 5中的异常断言。
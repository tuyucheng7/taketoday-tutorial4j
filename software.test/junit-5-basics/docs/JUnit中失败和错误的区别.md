## 1. 概述

在本教程中，我们将探讨JUnit测试中failure和error之间的区别。

简而言之，failure是未实现的断言，而errors是由于异常的测试执行造成的。

## 2. 示例代码

让我们考虑一个非常简单的例子，即一个SimpleCalculator类，它有一个方法可以将两个double值相除：

```java
public class SimpleCalculator {

    public static double divideNumbers(double dividend, double divisor) {
        if (divisor == 0)
            throw new ArithmeticException("Division by zero!");
        return dividend / divisor;
    }
}
```

**请注意，对于double值，Java实际上并没有为除数为0抛出ArithmeticException - 它返回Infinity或NaN**。

## 3. Failure例子

使用JUnit编写单元测试时，可能会出现测试失败的情况。一种可能性是我们的代码不符合其测试标准，这意味着一个或多个测试用例由于断言不满足而失败。

在下面的示例中，断言将失败，因为除法的结果是2而不是15。我们的断言和实际结果根本不匹配：

```java
class SimpleCalculatorUnitTest {

    @Test
    @Disabled("test is expected to fail, disabled so that CI build still goes through")
    void whenDivideNumbers_thenExpectWrongResult() {
        double result = SimpleCalculator.divideNumbers(6, 3);
        assertEquals(15, result);
    }
}
```

## 4. Error例子

**另一种可能是我们在测试执行过程中遇到了意外情况，很可能是异常导致的**；例如，访问null对象将引发RuntimeException。

让我们看一个示例，其中测试将因错误而中止，因为我们试图除以0，我们通过在SimpleCalculator代码中抛出异常来明确防止这一点：

```java
class SimpleCalculatorUnitTest {

    @Test
    @Disabled("test is expected to raise an error, disabled so that CI still goes through")
    void whenDivideByZero_thenThrowsException() {
        SimpleCalculator.divideNumbers(10, 0);
    }
}
```

现在，我们可以通过简单地将异常作为我们的断言来解决这个问题。

```java
public class SimpleCalculatorUnitTest {

    @Test
    void whenDivideByZero_thenAssertException() {
        assertThrows(ArithmeticException.class, () -> SimpleCalculator.divideNumbers(10, 0));
    }
}
```

如果抛出异常，则测试通过.但如果不通过，就会导致测试失败，即failure。

## 5. 总结

JUnit测试中的failure和error都表示不希望出现的情况，但它们的语义不同。
**failure意为无效的测试结果，而error表示意外的测试执行**。
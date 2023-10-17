## 1. 概述

默认情况下，Java中的浮点计算是平台相关的。因此，浮点计算结果的精度取决于所使用的硬件。

在本教程中，我们将学习如何**在Java中使用strictfp来确保与平台无关的浮点计算**。

## 2. strictfp用法

我们可以使用strictfp关键字作为类、非抽象方法或接口的非访问修饰符：

```java
public strictfp class ScientificCalculator {
    // ...
    
    public double sum(double value1, double value2) {
        return value1 + value2;
    }

    public double diff(double value1, double value2) { 
        return value1 - value2; 
    }
}

public strictfp void calculateMarksPercentage() {
    // ...
}

public strictfp interface Circle {
    double computeArea(double radius);
}
```

当我们使用strictfp声明一个接口或类时，它的所有成员方法和其他嵌套类型都会继承它的行为。

但是，请注意**我们不允许在变量、构造函数或抽象方法上使用strictfp关键字**。

此外，对于标有它的超类的情况，它不会使我们的子类继承该行为。

## 3. 什么时候使用？

每当我们非常关心所有浮点计算的确定性行为时，Java strictfp关键字就会派上用场：

```java
@Test
public void whenMethodOfstrictfpClassInvoked_thenIdenticalResultOnAllPlatforms() {
    ScientificCalculator calculator = new ScientificCalculator();
    double result = calculator.sum(23e10, 98e17);
    assertThat(result, is(9.800000230000001E18));

    result = calculator.diff(Double.MAX_VALUE, 1.56);
    assertThat(result, is(1.7976931348623157E308));
}
```

由于ScientificCalculator类使用了此关键字，因此上述测试用例将在所有硬件平台上通过。请注意，**如果我们不使用它，JVM可以自由使用目标平台硬件上可用的任何额外精度**。

它的一个流行的现实世界用例是执行高度敏感的医学计算的系统。

## 4. 总结

在本快速教程中，我们讨论了何时以及如何在Java中使用strictfp关键字。
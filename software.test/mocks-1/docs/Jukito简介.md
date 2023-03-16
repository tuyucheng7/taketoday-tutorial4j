## 1. 概述

[Jukito](https://github.com/ArcBees/Jukito)是[JUnit](http://junit.org/junit5/)、[Guice](https://github.com/google/guice)和[Mockito](https://github.com/mockito/mockito)的组合功能-用于简化同一接口的多个实现的测试。

在本文中，我们将了解Jukito如何设法结合这三个库来帮助我们减少大量样板代码，从而使我们的测试变得灵活和简单。

## 2. 设置

首先，我们将以下依赖项添加到我们的项目中：

```xml
<dependency>
    <groupId>org.jukito</groupId>
    <artifactId>jukito</artifactId>
    <version>1.5</version>
    <scope>test</scope>
</dependency>
```

我们可以在[Maven Central](https://central.sonatype.com/artifact/org.jukito/jukito/1.5)找到最新版本。

## 3. 接口的不同实现

要开始了解Jukito的强大功能，我们将使用add方法定义一个简单的Calculator接口：

```java
public interface Calculator {
    double add(double a, double b);
}
```

我们将实现以下接口：

```java
public class SimpleCalculator implements Calculator {

    @Override
    public double add(double a, double b) {
        return a + b;
    }
}
```

我们还需要另一个实现：

```java
public class ScientificCalculator extends SimpleCalculator {
}
```

现在，让我们使用Jukito来测试我们的两个实现：

```java
@RunWith(JukitoRunner.class)
public class CalculatorTest {

    public static class Module extends JukitoModule {

        @Override
        protected void configureTest() {
            bindMany(Calculator.class, SimpleCalculator.class, ScientificCalculator.class);
        }
    }

    @Test
    public void givenTwoNumbers_WhenAdd_ThenSumBoth(@All Calculator calc) {
        double result = calc.add(1, 1);

        assertEquals(2, result, .1);
    }
}
```

在这个例子中，我们可以看到一个JukitoModule，它连接了所有指定的实现。

@All注解采用JukitoModule对同一接口的所有绑定，并使用**在运行时注入的所有不同实现来运行测试**。

如果我们运行测试，我们可以看到确实运行了两个测试而不是一个：

```shell
Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
```

## 4. 笛卡尔积

现在让我们为add方法添加一个简单的嵌套类，用于不同的测试组合：

```java
public static class AdditionTest {
    int a;
    int b;
    int expected;

    // standard constructors/getters
}
```

这将扩展我们可以运行的测试数量，但首先，我们需要在configureTest方法中添加额外的绑定：

```java
bindManyInstances(AdditionTest.class, 
    new AdditionTest(1, 1, 2), 
    new AdditionTest(10, 10, 20), 
    new AdditionTest(18, 24, 42));
```

最后我们在套件中添加另一个测试：

```java
@Test
public void givenTwoNumbers_WhenAdd_ThenSumBoth(@All Calculator calc, @All AdditionTest addTest) {
    double result = calc.add(addTest.a, addTest.b);
 
    assertEquals(addTest.expected, result, .1);
}
```

现在，**@All注解将生成Calculator接口的不同实现和AdditionTest实例之间的不同组合的笛卡尔积**。

我们可以看看它现在产生的测试数量增加了：

```shell
Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
```

**我们需要记住，笛卡尔积的测试执行次数急剧增加**。

所有测试的执行时间将随着执行次数线性增长。即：具有三个参数的测试方法，带有@All注解，每个参数有四个绑定，将执行4 x 4 x 4 = 64次。

同一个测试方法具有五个绑定将导致5 x 5 x 5 = 125次执行。

## 5. 按名称分组

我们将讨论的最后一个功能是按名称分组：

```java
bindManyNamedInstances(Integer.class, "even", 2, 4, 6);
bindManyNamedInstances(Integer.class, "odd", 1, 3, 5);
```

在这里，我们将Integer类的一些命名实例添加到我们的configureTest方法中，以展示可以使用这些组做什么。

现在让我们再添加一些测试：

```java
@Test
public void givenEvenNumbers_whenPrint_thenOutput(@All("even") Integer i) {
    System.out.println("even " + i);
}

@Test
public void givenOddNumbers_whenPrint_thenOutput(@All("odd") Integer i) {
    System.out.println("odd " + i);
}
```

上面的示例将打印六个字符串“even 2”、“even 4”、“even 6”、“odd 1”、“odd 3”、“odd 5”。

请记住，在运行时不能保证这些的顺序。

## 6. 总结

在这个快速教程中，我们了解了Jukito如何通过提供足够多的测试用例组合来允许使用整个测试套件。
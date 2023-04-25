## 1. 概述

单元测试是软件设计和实现的关键步骤。

它不仅提高了代码的效率和有效性，而且使代码更加健壮，减少了未来开发和维护中的回归。

在本教程中，，我们将讨论一些在Java中进行单元测试的最佳实践。

## 2. 什么是单元测试？

单元测试是一种测试源代码是否适合在生产中使用的方法。

我们通过创建各种测试用例来开始编写单元测试，以验证单个源代码单元的行为。

然后**执行完整的测试套件以捕获回归，无论是在实施阶段还是在为部署的各个阶段(如暂存和生产)构建包时**。

让我们来看一个简单的场景。

首先，让我们创建Circle类并在其中实现calculateArea方法：

```java
public class Circle {

    public static double calculateArea(double radius) {
        return Math.PI * radius * radius;
    }
}
```

然后我们将为Circle类创建单元测试以确保calculateArea方法按预期工作。

让我们在src/main/test目录中创建CalculatorTest类：

```java
public class CircleTest {

    @Test
    public void testCalculateArea() {
        //...
    }
}
```

在这种情况下，我们使用[JUnit的@Test注解](https://www.baeldung.com/junit-5-test-annotation)以及[Maven](https://www.baeldung.com/maven-run-single-test)或[Gradle](https://www.baeldung.com/junit-5-gradle)等构建工具来运行测试。

## 3. 最佳实践

### 3.1 源代码

将测试类与主源代码分开是个好主意。因此，**它们是独立于生产代码开发、执行和维护的**。

此外，它避免了在生产环境中运行测试代码的任何可能性。

我们可以**按照Maven和Gradle等构建工具的步骤寻找src/main/test目录进行测试实现**。

### 3.2 包命名约定

我们应该在src/main/test目录下为测试类创建一个类似的包结构，这样可以提高测试代码的可读性和可维护性。

简单地说，**测试类的包应该与它要测试的源代码单元的源类的包相匹配**。

例如，如果我们的Circle类存在于cn.tuyucheng.taketoday.math包中，那么CircleTest类也应该存在于src/main/test目录结构下的cn.tuyucheng.taketoday.math包中。

### 3.3 测试用例命名约定

**测试名称应该是有洞察力的**，用户应该只看一眼名称本身就可以理解测试的行为和期望。

例如，我们的单元测试的名称是testCalculateArea，它在关于测试场景和期望的任何有意义的信息上都是模糊的。

因此，我们应该使用操作和期望来命名一个测试，例如testCalculateAreaWithGeneralDoubleValueRadiusThatReturnsAreaInDouble、testCalculateAreaWithLargeDoubleValueRadiusThatReturnsAreaAsInfinity。

但是，我们仍然可以改进名称以提高可读性。

**在given_when_then中命名测试用例通常有助于详细说明单元测试的目的**：

```java
public class CircleTest {

    //...

    @Test
    public void givenRadius_whenCalculateArea_thenReturnArea() {
        //...
    }

    @Test
    public void givenDoubleMaxValueAsRadius_whenCalculateArea_thenReturnAreaAsInfinity() {
        //...
    }
}
```

我们还应该**以[Given、When和Then](https://www.baeldung.com/cs/bdd-guide)格式描述代码块。此外，它有助于将测试分为三个部分：输入、操作和输出**。

首先，对应于given部分的代码块创建测试对象，模拟数据并安排输入。

接下来，when部分的代码块表示特定的操作或测试场景。

同样，then部分指出代码的输出，该输出使用断言根据预期结果进行验证。

### 3.4 预期与实际

**测试用例应该在预期值和实际值之间有一个断言**。

为了证实期望值与实际值的概念，我们可以查看[JUnit的Assert类的assertEquals](https://www.baeldung.com/junit-assertions#junit4-assertequals)方法的定义：

```java
public static void assertEquals(Object expected, Object actual)
```

让我们在其中一个测试用例中使用该断言：

```java
@Test 
public void givenRadius_whenCalculateArea_thenReturnArea() {
    double actualArea = Circle.calculateArea(1d);
    double expectedArea = 3.141592653589793;
    Assert.assertEquals(expectedArea, actualArea); 
}
```

建议在变量名前面加上actual和expected关键字，以提高测试代码的可读性。

### 3.5 倾向简单的测试用例

在前面的测试用例中，我们可以看到期望值是硬编码的。这样做是为了避免重写或重用测试用例中的实际代码实现以获得预期值。

不鼓励计算圆的面积以匹配calculateArea方法的返回值：

```java
@Test 
public void givenRadius_whenCalculateArea_thenReturnArea() {
    double actualArea = Circle.calculateArea(2d);
    double expectedArea = 3.141592653589793 * 2 * 2;
    Assert.assertEquals(expectedArea, actualArea); 
}
```

在此断言中，我们使用相似的逻辑计算预期值和实际值，从而永远产生相似的结果。因此，我们的测试用例不会为代码的单元测试增加任何价值。

因此，**我们应该创建一个简单的测试用例，将硬编码的预期值与实际值进行断言**。

虽然有时需要在测试用例中编写逻辑，但我们不应该过度编写。此外，正如通常看到的那样，**我们永远不应该在测试用例中实现生产逻辑来传递断言**。

### 3.6 适当的断言

始终使用正确的断言来验证预期结果与实际结果。我们应该使用[JUnit](https://www.baeldung.com/junit)的Assert类或类似框架(如[AssertJ)](https://www.baeldung.com/introduction-to-assertj)中可用的各种方法。

例如，我们已经使用Assert.assertEquals方法进行值断言。类似地，我们可以使用assertNotEquals来检查预期值和实际值是否不相等。

其他方法，如assertNotNull、assertTrue 和assertNotSame在不同的断言中是有益的。

### 3.7 特定单元测试

我们应该创建单独的测试用例，而不是将多个断言添加到同一个单元测试中。

当然，有时在同一个测试中验证多个场景很诱人，但最好将它们分开。然后，在测试失败的情况下，将更容易确定是哪个特定场景失败，同样，也更容易修复代码。

因此，请**始终编写单元测试来测试单个特定场景**。

单元测试不会变得太复杂而难以理解。而且，以后调试和维护单元测试会更容易。

### 3.8 测试生产场景

**当我们在考虑真实场景的情况下编写测试时，单元测试会更有价值**。

主要是，它有助于使单元测试更具相关性。此外，事实证明，它对于理解某些生产案例中的代码行为至关重要。

### 3.9 Mock外部服务

虽然单元测试集中在特定的和较小的代码片段上，但代码有可能依赖于某些逻辑的外部服务。

因此，**我们应该mock外部服务，只针对不同的场景测试代码的逻辑和执行**。

我们可以使用各种框架，如[Mockito](https://www.baeldung.com/mockito-series)、[EasyMock](https://www.baeldung.com/easymock)和[JMockit](https://www.baeldung.com/jmockit-101)来mock外部服务。

### 3.10 避免代码冗余

**创建越来越多的辅助函数来生成常用对象并mock数据或外部服务以进行类似的单元测试**。

与其他建议一样，这增强了测试代码的可读性和可维护性。

### 3.11 注解

通常，测试框架会为各种目的提供注解，例如，在运行测试之前执行设置、执行代码和在运行测试之后拆除。

JUnit的[@Before、@BeforeClass和@After](https://www.baeldung.com/junit-before-beforeclass-beforeeach-beforeall)以及来自其他测试框架(例如[TestNG)](https://www.baeldung.com/testng)的各种注解可供我们使用。

我们应该**利用注解来为测试准备系统**，方法是在每次测试后创建数据、排列对象并删除所有这些数据，以保持测试用例彼此隔离。

### 3.12 80%的测试覆盖率

更多的[源代码测试覆盖率](https://www.baeldung.com/cs/code-coverage)总是有益的。然而，这并不是要实现的唯一目标。我们应该做出明智的决定，并选择一个对我们的实施、截止日期和团队都有效的更好的权衡。

根据经验，我们应该尝试**通过单元测试覆盖80%的代码**。

此外，我们可以使用[JaCoCo](https://www.baeldung.com/jacoco)和[Cobertura](https://www.baeldung.com/cobertura)等工具以及Maven或Gradle来生成代码覆盖率报告。

### 3.13 TDD方法

[测试驱动开发(TDD)](https://www.baeldung.com/java-test-driven-list)是我们在实施之前和实施过程中创建测试用例的方法。该方法与设计和实现源代码的过程相结合。

好处包括**从一开始就可测试生产代码、易于重构的可靠实现和更少的回归**。

### 3.14 自动化

我们可以通过**在创建新版本时自动执行整个测试套件来提高代码的可靠性**。

首先，这有助于避免在各种发布环境中发生不幸的回归。它还确保在发布损坏的代码之前快速反馈。

因此，**单元测试执行应该是[CI-CD管道](https://www.baeldung.com/ops/jenkins-pipelines)的一部分**，并在出现故障时提醒利益相关者。

## 4. 总结

在本文中，我们探讨了Java单元测试的一些最佳实践。遵循最佳实践可以在软件开发的许多方面有所帮助。
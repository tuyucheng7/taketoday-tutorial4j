## 1. 简介

通过本文，我们将开始一个以mock工具包[JMockit](https://jmockit.github.io/)为中心的新系列。

在第一部分中，我们将讨论JMockit是什么、它的特性以及如何创建和使用mock。

后面的文章将重点关注并深入探讨其功能。

## 2. JMockit

### 2.1 概述

首先，让我们谈谈JMockit是什么：一个用于在测试中mock对象的Java框架(你可以将其用于[JUnit](http://junit.org/junit4/)和[TestNG](http://testng.org/doc/index.html))。

它使用Java的检测API在运行时修改类的字节码，以动态改变它们的行为。它的一些优点是它的可表达性和开箱即用的mock静态和私有方法的能力。

也许你是JMockit的新手，但这绝对不是因为它是新的库。JMockit的开发始于2006年6月，其第一个稳定版本的发布日期为2012年12月，因此它已经存在了一段时间。

### 2.2 Maven依赖

首先，我们需要将[jmockit](https://central.sonatype.com/artifact/org.jmockit/jmockit/1.49)依赖项添加到我们的项目中：

```xml
<dependency> 
    <groupId>org.jmockit</groupId> 
    <artifactId>jmockit</artifactId> 
    <version>1.41</version>
</dependency>
```

### 2.3 JMockit的可表达性

如前所述，JMockit的一个优势点是它的可表达性。为了创建mock并定义它们的行为，你只需要直接定义它们，而不是从mock API调用方法。

这意味着你不会也不需要执行以下操作：

```java
API.expect(mockInstance.method()).andThenReturn(value).times(2);
```

相反，使用以下内容代替：

```java
new Expectation() {
    mockInstance.method(); 
    result = value; 
    times = 2;
}
```

看起来它的代码虽然更多，但你可以简单地将所有三行放在一行上。真正重要的部分是，你最终不会得到一大串链式方法调用。相反，你最终会定义你希望mock在调用时的行为方式。

如果你考虑到在result = value部分你可以返回任何内容(固定值、动态生成的值、异常等)，那么JMockit的表现力会变得更加明显。

### 2.4 记录-重播-验证模型

使用JMockit的测试分为三个不同的阶段：记录、重播和验证。

1.  在**记录**阶段，在测试准备期间和调用我们要执行的方法之前，我们将为下一阶段要使用的所有测试定义预期行为。
2.  **重播**阶段是执行被测代码的阶段。现在将重播先前在上一阶段记录的mock方法/构造函数的调用。
3.  最后，在**验证**阶段，我们将断言测试的结果是我们预期的结果(并且mock的行为和使用是根据记录阶段定义的)。

通过代码示例，测试的骨架如下所示：

```java
@Test
public void testWireframe() {
    // preparation code not specific to JMockit, if any

    new Expectations() {{ 
        // define expected behaviour for mocks
    }};

    // execute code-under-test

    new Verifications() {{ 
        // verify mocks
    }};

    // assertions
}
```

## 3. 创建Mock

### 3.1 JMockit的注解

使用JMockit时，使用mock的最简单方法是使用注解。有三种用于创建mock(@Mocked、@Injectable和@Capturing)和一种用于指定被测类(@Tested)的注解。

在字段上使用@Mocked注解时，它将创建该特定类的每个新对象的mock实例。

另一方面，使用@Injectable注解，只会创建一个mock实例。

最后一个注解@Capturing的行为类似于@Mocked，但会将其范围扩展到扩展或实现带注解字段类型的每个子类。

### 3.2 将参数传递给测试

使用JMockit时，可以将mock作为测试参数传递。这对于专门为该测试创建一个mock非常有用，例如一些需要针对单个测试的特定行为的复杂模型对象。它会是这样的：

```java
@RunWith(JMockit.class)
public class TestPassingArguments {

    @Injectable
    private Foo mockForEveryTest;

    @Tested
    private Bar bar;

    @Test
    public void testExample(@Mocked Xyz mockForJustThisTest) {
        new Expectations() {{
            mockForEveryTest.someMethod("foo");
            mockForJustThisTest.someOtherMethod();
        }};

        bar.codeUnderTest();
    }
}
```

这种通过将mock作为参数传递来创建mock的方式，而不是必须调用一些API方法，再次向我们展示了我们从一开始就在谈论的可表达性。

### 3.3 完整示例

在本文结束时，我们将包含一个使用JMockit进行测试的完整示例。

在此示例中，我们将测试在其perform()方法中使用Collaborator的Performer类。这个perform()方法接收一个Model对象作为参数，它将使用它的getInfo()返回一个String，这个String将被传递给Collaborator的collaborate()方法，该方法将为这个特定的测试返回true，并且该值将传递给来自Collaborator的receive()方法。

因此，测试的类将如下所示：

```java
public class Model {
    public String getInfo() {
        return "info";
    }
}

public class Collaborator {
    public boolean collaborate(String string) {
        return false;
    }

    public void receive(boolean bool) {
        // NOOP
    }
}

public class Performer {
    private Collaborator collaborator;

    public void perform(Model model) {
        boolean value = collaborator.collaborate(model.getInfo());
        collaborator.receive(value);
    }
}
```

测试的代码最终将如下所示：

```java
@RunWith(JMockit.class)
public class PerformerTest {

    @Injectable
    private Collaborator collaborator;

    @Tested
    private Performer performer;

    @Test
    public void testThePerformMethod(@Mocked Model model) {
        new Expectations() {{
            model.getInfo();result = "bar";
            collaborator.collaborate("bar"); result = true;
        }};
        performer.perform(model);
        new Verifications() {{
            collaborator.receive(true);
        }};
    }
}
```

## 4 总结

至此，我们将结束对JMockit的实用介绍。如果你想了解更多关于JMockit的信息，敬请期待后续文章。

### 4.1 系列文章

该系列的所有文章：

-   [JMockit101](https://www.baeldung.com/jmockit-101)
-   [JMockit指南–期望](https://www.baeldung.com/jmockit-expectations)
-   [JMockit高级用法](https://www.baeldung.com/jmockit-advanced-usage)
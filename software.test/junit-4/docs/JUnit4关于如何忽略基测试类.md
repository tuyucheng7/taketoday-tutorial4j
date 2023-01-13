## 1. 概述

本教程介绍在JUnit 4中跳过从基测试类运行测试的可能解决方案。出于本教程的目的，基类中只有工具方法，而子类将扩展它并运行实际测试。

## 2. 绕过基测试类

假设我们有一个包含一些工具方法的BaseUnitTest类：

```java
public class BaseUnitTest {
    public void helperMethod() {
        // ...
    }
}
```

现在，我们通过一个包含测试方法的类来扩展它：

```java
public class ExtendedBaseUnitTest extends BaseUnitTest {
    @Test
    public void whenDoTest_thenAssert() {
        // ...        
    }
}
```

如果我们使用IDE或Maven构建运行测试，我们可能会得到一个错误，告诉我们BaseUnitTest中没有可运行的测试方法。我们不想在此类中运行测试，所以我们需要寻找一种方法来避免这个错误。

如果使用IDE运行测试，我们可能会得到不同的结果，具体取决于我们的IDE插件以及我们如何配置它来运行JUnit测试。

### 2.1 重命名类

我们可以将类重命名为构建约定将排除在作为测试运行之外的名称。例如，如果我们使用Maven，我们可以检查[Maven Surefire插件](https://maven.apache.org/surefire/maven-surefire-plugin/examples/inclusion-exclusion.html)的默认值。

**我们可以将名称从BaseUnitTest更改为BaseUnitTestHelper或类似名称**：

```java
public class BaseUnitTestHelper {
    public void helperMethod() {
        // ...
    }
}
```

### 2.2 Ignore

**第二种选择是使用JUnit @Ignore注解暂时禁用测试，我们可以在类级别添加它来禁用类中的所有测试**：

```java
@Ignore("Class not ready for tests")
public class IgnoreClassUnitTest {
    @Test
    public void whenDoTest_thenAssert() {
        // ...
    }
}
```

同样，**我们可以在方法级别应用它**，以防我们只需要排除该类中的某些测试方法：

```java
public class IgnoreMethodTest {
    @Ignore("This method not ready yet")
    @Test
    public void whenMethodIsIgnored_thenTestsDoNotRun() {
        // ...
    }
}
```

如果使用Maven运行，我们会看到如下输出：

```java
Tests run: 1, Failures: 0, Errors: 0, Skipped: 1, Time elapsed: 0.041 s - in com.baeldung.IgnoreMethodTest
```

**从JUnit 5起，@Disabled注解取代了@Ignore**。

### 2.3 使基类抽象

**最好的方法可能是使基测试类抽象化**。抽象需要一个具体的类来扩展它。这就是为什么JUnit在任何情况下都不会将其视为测试实例的原因。

下面我们将BaseUnitTest类抽象化：

```java
public abstract class BaseUnitTest {
    public void helperMethod() {
        // ...
    }
}
```

## 3. 总结

在本文中，我们演示了一些在JUnit 4中从运行测试中排除基测试类的例子，其中最好的方法是创建抽象类。JUnit的@Ignore注解也被广泛使用，但被认为是一种不好的做法。**如果忽略测试，我们可能会忘记它们以及忽略它们的原因**。

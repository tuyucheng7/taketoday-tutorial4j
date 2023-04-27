## 1. 简介

[JUnit](https://www.baeldung.com/junit-5)是Java应用程序最流行的测试框架之一，具有强大而灵活的方式来创建自动化单元测试。它的功能之一是能够**创建测试套件，这使我们能够对多个测试进行分组**。

在本教程中，我们将探讨如何使用JUnit创建测试套件。首先，我们将实现并运行一个简单的测试套件。之后，我们将探索一些配置以包含或排除某些测试。

## 2. 创建测试套件

正如我们所知，**测试套件是组合在一起并作为一个单元运行的测试的集合**。我们使用它们将测试组织到逻辑组中，例如针对特定组件或应用程序功能的测试。我们还可以轻松地按特定顺序执行测试或根据特定标准运行测试的子集。

JUnit 5提供了多种创建测试套件的方法。在开始之前，我们需要确保包含[junit-platform-suite](https://mvnrepository.com/artifact/org.junit.platform/junit-platform-suite)依赖项：

```xml
<dependency>
    <groupId>org.junit.platform</groupId>
    <artifactId>junit-platform-suite</artifactId>
    <version>1.9.2</version>
    <scope>test</scope>
</dependency>
```

[JUnit Platform Suite Engine](https://junit.org/junit5/docs/current/user-guide/#junit-platform-suite-engine)负责使用JUnit中的一个或多个测试引擎运行自定义测试套件。它还为我们带来了额外的API，我们将使用这些API来创建测试套件。

### 2.1 使用@Suite注解

**实现测试套件最简单的方法是使用@Suite类级注解**，这也是最推荐的解决方案。从JUnit Platform的1.8.0版本开始，这个注解就可用了。

让我们准备一个JUnitTestSuite类：

```java
@Suite
@SuiteDisplayName("My Test Suite")
public class JUnitTestSuite {
}
```

在此示例中，我们使用@Suite注解告诉JUnit将此类标记为可在单个单元中执行。此外，我们添加了一个可选的@SuiteDisplayName注解并指定了一个自定义标题。

从现在开始，我们可以使用此类通过使用我们的IDE或[maven-surefire-plugin](https://www.baeldung.com/maven-surefire-plugin)在单次运行中执行此套件中配置的所有测试。请注意，目前此套件不包含任何测试。

### 2.2 使用@RunWith注解

或者，我们可以使用JUnit 4 Runners模型的[@RunWith](https://junit.org/junit5/docs/current/user-guide/#running-tests-junit-platform-runner-test-suite)注解重写我们的测试套件：

```java
@RunWith(JUnitPlatform.class)
@SuiteDisplayName("My Test Suite")
public class JUnitTestSuite {
}
```

如果我们缺少JUnitPlatform类，我们需要包含一个[额外的依赖项](https://mvnrepository.com/artifact/org.junit.platform/junit-platform-runner)：

```xml
<dependency>
    <groupId>org.junit.platform</groupId>
    <artifactId>junit-platform-runner</artifactId>
    <version>1.9.2</version>
    <scope>test</scope>
</dependency>
```

因此，这个测试套件的工作方式与我们之前通过@Suite注解创建的测试套件类似。**仅建议使用早期版本的JUnit的开发人员使用此解决方案**。此外，[JUnit Platform Runner自1.8.0以来已被弃用](https://junit.org/junit5/docs/5.8.0/user-guide/index.html#running-tests-junit-platform-runner)，取而代之的是@Suite支持，并将在未来的版本中删除。

## 3. 包括和排除测试

目前，我们的测试套件不包含任何测试。**JUnit Platform Suite Engine提供了[多个注解](https://junit.org/junit5/docs/current/api/org.junit.platform.suite.api/org/junit/platform/suite/api/package-summary.html)以在我们的测试套件中包含或排除测**试。

我们可以区分两组主要的注解：@Select和@Include/@Exclude。

**@Select注解指定JUnit应在其中查找测试的资源。同时，@Include/@Exclude注解指定了包含或排除先前发现的测试的其他条件**。

**如果没有@Select注解，这两个注解将无法工作**。我们可以混合所有注解来确定我们想要运行哪些测试。

现在让我们通过配置我们的测试套件来了解其中的一些。

### 3.1 @SelectClasses

为我们的测试套件选择测试的最常见方法是**使用@SelectClasses注解指定测试类**：

```java
@Suite
@SelectClasses({ClassOneUnitTest.class, ClassTwoUnitTest.class})
public class JUnitTestSuite {
}
```

测试套件现在执行来自两个类的所有@Test标记的方法。

### 3.2 @SelectPackages

我们可以**使用@SelectPackages来提供用于测试扫描的包**，而不是指定类列表：

```java
@Suite
@SelectPackages({"cn.tuyucheng.taketoday.testsuite", "cn.tuyucheng.taketoday.testsuitetwo"})
public class JUnitTestSuite {
}
```

值得注意的是，**此注解还执行子包中的所有类**。

### 3.3 @IncludePackages和@ExcludePackages

现在，我们知道如何包含包中的所有类。**要进一步指定是否包含或排除包，我们可以分别使用@IncludePackages和@ExcludePackage的注解**：

```java
@Suite
@SelectPackages({"cn.tuyucheng.taketoday.testsuite")
@IncludePackages("cn.tuyucheng.taketoday.testsuite.subpackage")
public class JUnitTestSuite {
}
```

上面的配置只执行cn.tuyucheng.taketoday.testsuite.subpackage包中的所有测试，忽略其他发现。

让我们看看如何排除单个包：

```java
@Suite
@SelectPackages("cn.tuyucheng.taketoday.testsuite")
@ExcludeEngines("cn.tuyucheng.taketoday.testsuite.subpackage")
public class JUnitTestSuite {
}
```

现在，JUnit执行cn.tuyucheng.taketoday.testsuite包及其子包中的所有测试，仅忽略cn.tuyucheng.taketoday.testsuite.subpackage中的类。

### 3.4 @IncludeClassNamePatterns和@ExcludeClassNamePatterns

如果我们不想使用包指定包含规则，**我们可以使用@IncludeClassNamePatterns和@ExcludeClassNamePatterns注解并对类名进行正则表达式检查**：

```java
@Suite
@SelectPackages("cn.tuyucheng.taketoday.testsuite")
@IncludeClassNamePatterns("cn.tuyucheng.taketoday.testsuite.Class.*UnitTest")
@ExcludeClassNamePatterns("cn.tuyucheng.taketoday.testsuite.ClassTwoUnitTest")
public class JUnitTestSuite {
}
```

此示例包括在cn.tuyucheng.taketoday.testsuite包中找到的所有测试，而不包括其子包。类名必须匹配Class.*UnitTest正则表达式模式，例如ClassOneUnitTest和ClassThreeUnitTest。

此外，我们还严格排除了满足第一个条件的ClassTwoUnitTest名称。我们知道，在Java中，完整的类名还包括它的包。在定义模式时也应考虑到这一点。

### 3.5 @IncludeTags和@ExcludeTags

正如我们所知，在JUnit中，我们可以通过[@Tag](https://junit.org/junit5/docs/5.8.0/user-guide/index.html#writing-tests-tagging-and-filtering)注解来标记类和方法。这是使用简单值过滤测试的简单方法。我们可以在定义测试用例时使用相同的机制，**使用@IncludeTags和@ExcludeTags来运行带有指定@Tag的测试**：

```java
@Suite
@SelectPackages("cn.tuyucheng.taketoday.testsuite")
@IncludeTags("slow")
public class JUnitTestSuite {
}
```

该测试套件将扫描cn.tuyucheng.taketoday.testsuite包和所有子包，**仅运行带有@Tag("slow")注解的测试**。

现在让我们反转配置：

```java
@Suite
@SelectPackages("cn.tuyucheng.taketoday.testsuite")
@ExcludeTags("slow")
public class JUnitTestSuite {
}
```

在此示例中，**JUnit运行所有没有@Tag("slow")注解的测试，包括未标记的测试**。

值得注意的是，在JUnit中，我们还可以在测试类中标记单个方法。**使用@IncludeTags和@ExcludeTags还允许我们包含类中的单个方法**，这与之前的注解不同。

## 4. 总结

JUnit提供了一种创建自动化测试的简单方法，包括创建测试套件的能力。我们可以使用测试套件将测试组织成逻辑组，以特定顺序运行一组测试，或基于特定标准运行测试子集。

在本文中，我们讨论了通过@Suite和@RunWith API实现测试套件的两种方法。然后我们探索了一些额外的注解并检查了如何为我们的测试套件选择测试。最后，我们修改了所选测试的列表，根据不同的条件包括和排除它们。
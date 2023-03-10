## 1. 概述

在进行单元测试时，我们可能希望定期处理测试方法执行的结果。在这个快速教程中，**我们将了解如何使用[JUnit](http://junit.org/junit5/)提供的TestWatcher API来实现这一点**。

有关使用JUnit进行测试的深入指南，请查看我们出色的[JUnit 5指南](https://www.baeldung.com/junit-5)。

## 2. TestWatcher API

**简而言之，[TestWatcher](https://junit.org/junit5/docs/5.5.1/api/org/junit/jupiter/api/extension/TestWatcher.html)接口为希望处理测试结果的Extension定义了API**，我们可以想到此API的一种方式是提供用于获取单个测试用例状态的钩子。

但是，在我们深入研究一些实际示例之前，**让我们退后一步，简要总结一下TestWatcher接口中的方法**：

```java
testAborted(ExtensionContext context, Throwable cause)
```

要处理中止测试的结果，我们可以重写testAborted()方法。顾名思义，此方法在测试中止后调用。

```java
testDisabled(ExtensionContext context, Optional reason)
```

当我们想要处理禁用的测试方法的结果时，我们可以重写testDisabled()方法。此方法还可能包括测试被禁用的原因。

```java
testFailed(ExtensionContext context, Throwable cause)
```

如果我们想在测试失败后做一些额外的处理，我们可以简单地在testFailed方法中实现该功能。该方法可能包括测试失败的原因。

```java
testSuccessful(ExtensionContext context)
```

最后但同样重要的是，当我们希望处理成功测试的结果时，我们只需重写testSuccessful()方法即可。

我们应该注意到上述所有方法都包含ExtensionContext作为参数，这封装了当前测试执行的上下文。

## 3. Maven依赖

首先，让我们添加示例所需的项目依赖项。

除了主要的JUnit 5库junit-jupiter-engine之外，我们还需要junit-jupiter-api库：

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <version>5.9.2</version>
    <scope>test</scope>
</dependency>
```

与往常一样，我们可以从[Maven Central](https://central.sonatype.com/artifact/org.junit.jupiter/junit-jupiter-api/5.9.2)获取最新版本。

## 4. TestResultLoggerExtension示例

现在我们对TestWatcher API有了基本的了解，接下来我们将介绍一个实际的例子。

**让我们首先创建一个简单的扩展来记录结果并提供我们的测试摘要**。在这种情况下，要创建扩展，我们需要定义一个实现TestWatcher接口的类：

```java
public class TestResultLoggerExtension implements TestWatcher, AfterAllCallback {

    private static final Logger LOG = LoggerFactory.getLogger(TestResultLoggerExtension.class);
    private final List<TestResultStatus> testResultsStatus = new ArrayList<>();

    private enum TestResultStatus {
        SUCCESSFUL, ABORTED, FAILED, DISABLED
    }
}
```

**与所有扩展接口一样，TestWatcher也继承了主Extension接口，它只是一个标记接口**。在这个例子中，我们还实现了AfterAllCallback接口。

在我们的扩展中，我们有一个TestResultStatus列表，这是一个简单的枚举，我们将使用它来表示测试结果的状态。

### 4.1 处理测试结果

现在，让我们看看如何处理单个单元测试方法的结果：

```java
@Override
public void testDisabled(ExtensionContext context, Optional<String> reason) {
    LOG.info("Test Disabled for test {}: with reason :- {}", context.getDisplayName(), reason.orElse("No reason"));
    testResultsStatus.add(TestResultStatus.DISABLED);
}

@Override
public void testSuccessful(ExtensionContext context) {
    LOG.info("Test Successful for test {}: ", context.getDisplayName());
    testResultsStatus.add(TestResultStatus.SUCCESSFUL);
}
```

我们首先填充扩展的主体并**覆盖testDisabled()和testSuccessful()方法**。

在我们的简单示例中，我们输出测试的名称并将测试的状态添加到testResultsStatus列表中。

**对于其他两个方法 - testAborted()和testFailed()**：

```java
@Override
public void testAborted(ExtensionContext context, Throwable cause) {
    LOG.info("Test Aborted for test {}: ", context.getDisplayName());
    testResultsStatus.add(TestResultStatus.ABORTED);
}

@Override
public void testFailed(ExtensionContext context, Throwable cause) {
    LOG.info("Test Failed for test {}: ", context.getDisplayName());
    testResultsStatus.add(TestResultStatus.FAILED);
}
```

### 4.2 统计测试结果

在示例的最后一部分，**我们将重写afterAll()方法**：

```java
@Override
public void afterAll(ExtensionContext context) {
    Map<TestResultStatus, Long> summary = testResultsStatus.stream()
        .collect(groupingBy(Function.identity(), Collectors.counting()));

    LOG.info("Test result summary for {} {}", context.getDisplayName(), summary.toString());
}
```

快速回顾一下，afterAll()方法在所有测试方法运行后执行。我们使用这个方法对testResultsStatus中的不同TestResultStatus进行分组，然后输出一个基本的统计。

有关生命周期回调的深入指南，请查看我们出色的[JUnit 5 Extension指南](https://www.baeldung.com/junit-5-extensions)。

## 5. 运行测试

**在倒数第二部分中，我们将使用简单的日志扩展查看测试的输出**。

现在我们已经定义了我们的扩展，我们将首先使用标准的@ExtendWith注解注册它：

```java
@ExtendWith(TestResultLoggerExtension.class)
class TestWatcherAPIUnitTest {

    @Test
    void givenFalseIsTrue_whenTestAbortedThenCaptureResult() {
        assumeTrue(true);
    }

    @Disabled
    @Test
    void givenTrueIsTrue_whenTestDisabledThenCaptureResult() {
        assertTrue(true);
    }

    @Test
    void givenTrueIsTrue_whenTestAbortedThenCaptureResult() {
        assumeTrue(true);
    }

    @Disabled("This test is disabled")
    @Test
    void givenFailure_whenTestDisabledWithReason_ThenCaptureResult() {
        fail("Not yet implemented");
    }
}
```

**接下来，我们用单元测试填充我们的测试类，添加禁用、中止和成功测试的混合**。

### 5.1 查看输出

当我们运行单元测试时，我们应该看到每个测试的输出：

```shell
23:32:17.904 ...  [c.t.t.e.t.TestResultLoggerExtension] >>> Test Disabled for test givenTrueIsTrue_whenTestDisabledThenCaptureResult(): with reason :- void cn.tuyucheng.taketoday.extensions.testwatcher.TestWatcherAPIUnitTest.givenTrueIsTrue_whenTestDisabledThenCaptureResult() is @Disabled 
23:32:17.904 ...  [c.t.t.e.t.TestResultLoggerExtension] >>> Test Disabled for test givenFailure_whenTestDisabledWithReason_ThenCaptureResult(): with reason :- This test is disabled 
23:32:17.914 ...  [c.t.t.e.t.TestResultLoggerExtension] >>> Test Aborted for test givenFalseIsTrue_whenTestAbortedThenCaptureResult:  
23:32:17.914 ...  [c.t.t.e.t.TestResultLoggerExtension] >>> Test Successful for test givenTrueIsTrue_whenTestAbortedThenCaptureResult:  

23:32:17.928 ...  [c.t.t.e.t.TestResultLoggerExtension] >>> Test result summary for TestWatcherAPIUnitTest {DISABLED=2, SUCCESSFUL=1, ABORTED=1} 
```

**当然，当所有测试方法完成时，我们也会看到打印的统计**。

## 6. 注意点

在最后一节中，让我们回顾一下在使用TestWatcher接口时应该注意的一些微妙之处：

+ TestWatcher扩展不允许影响测试的执行；**这意味着如果从TestWatcher抛出异常，它将不会传播到正在运行的测试**
+ 目前，该API仅用于报告@Test方法和@TestTemplate方法的测试结果
+ 默认情况下，如果没有为带有@Disabled注解的测试方法提供测试被禁用的原因，那么它将包含测试方法的全限定名称，后跟“is @Disabled”

## 7. 总结

总而言之，在本教程中，我们展示了如何使用JUnit 5 TestWatcher API来处理测试方法执行的结果。
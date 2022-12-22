## 1. 概述

在进行单元测试时，我们可能希望定期处理测试方法执行的结果。在本教程中，我们将了解如何使用JUnit提供的TestWatcher API来实现这一点。

## 2. TestWatcher API

**简而言之，TestWatcher接口为希望处理测试结果的Extension定义了API**，我们可以想到这个API的一种方法是提供钩子来获取单个测试用例的状态。

但是，在我们深入研究一些实际示例之前，让我们简要总结一下TestWatcher接口中的方法：

```text
testAborted(ExtensionContext context, Throwable cause)
```

为了处理中止测试的结果，我们可以重写testAborted()方法。顾名思义，这个方法是在测试中止后调用的。

```text
testDisabled(ExtensionContext context, Optional reason)
```

当我们想要处理禁用的测试方法的结果时，我们可以重写testDisabled()方法。此方法还可能包括测试被禁用的原因。

```text
testFailed(ExtensionContext context, Throwable cause)
```

如果我们想在测试失败后做一些额外的处理，我们可以简单地实现testFailed()。该方法可能包括测试失败的原因。

```text
testSuccessful(ExtensionContext context)
```

最后一个，当我们希望处理成功测试的结果时，我们只需重写testSuccessful()方法。

上述所有方法都包含一个ExtensionContext对象作为参数，该对象封装了执行当前测试的上下文。

## 3. maven依赖

除了junit-jupiter-engine的依赖，我们还需要以下的依赖：

```xml

<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <version>5.8.1</version>
    <scope>test</scope>
</dependency>
```

## 4. TestResultLoggerExtension例子

现在我们对TestWatcher接口有了基本的了解，接下来我们将介绍一个实际的例子。

**首先创建一个简单的Extension来记录结果，并提供测试的详细摘要**。
在这种情况下，为了创建Extension，我们需要定义一个实现TestWatcher接口的类：

```java
public class TestResultLoggerExtension implements TestWatcher, AfterAllCallback {

    private static final Logger LOG = LoggerFactory.getLogger(TestResultLoggerExtension.class);
    private final List<TestResultStatus> testResultsStatus = new ArrayList<>();

    private enum TestResultStatus {
        SUCCESSFUL, ABORTED, FAILED, DISABLED
    }
}
```

**与所有Extension接口一样，TestWatcher也继承了主要的Extension接口，它只是一个标记接口**。在本例中，我们还实现了AfterAllCallback接口。

在我们的Extension中，我们有一个TestResultStatus对象的集合，这是一个简单的枚举，我们使用它来表示测试结果的状态。

### 4.1 处理测试结果

现在，让我们看看如何处理单个单元测试方法的结果：

```java
public class TestResultLoggerExtension implements TestWatcher, AfterAllCallback {

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
}
```

我们首先重写testDisabled()、testSuccessful()方法。

在我们的代码中，我们输出测试的名称，并将测试的状态添加到testResultsStatus集合中。

然后重写testAborted()和testFailed()方法：

```java
public class TestResultLoggerExtension implements TestWatcher, AfterAllCallback {

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
}
```

### 4.2 统计测试结果

在上面例子的最后一部分中，我们将重写afterAll()方法：

```java
public class TestResultLoggerExtension implements TestWatcher, AfterAllCallback {

    @Override
    public void afterAll(ExtensionContext context) {
        Map<TestResultStatus, Long> summary = testResultsStatus.stream()
                .collect(groupingBy(Function.identity(), Collectors.counting()));

        LOG.info("Test result summary for {} {}", context.getDisplayName(), summary.toString());
    }
}
```

afterAll()方法是在所有测试方法运行结束之后执行的，我们使用这个方法对testResultsStatus中的不同TestResultStatus进行分组，然后输出一个基本的统计。

## 5. 运行测试

现在我们已经定义了我们的Extension，我们首先需要使用标准的@ExtendWith注解注册它：

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

**然后，我们添加几个单元测试方法。分别添加了被禁用，中止和成功的测试**。

当我们运行单元测试时，我们应该看到每个测试的输出：

```text
23:32:17.904 ...  [c.t.t.e.t.TestResultLoggerExtension] >>> Test Disabled for test givenTrueIsTrue_whenTestDisabledThenCaptureResult(): with reason :- void cn.tuyucheng.taketoday.extensions.testwatcher.TestWatcherAPIUnitTest.givenTrueIsTrue_whenTestDisabledThenCaptureResult() is @Disabled 
23:32:17.904 ...  [c.t.t.e.t.TestResultLoggerExtension] >>> Test Disabled for test givenFailure_whenTestDisabledWithReason_ThenCaptureResult(): with reason :- This test is disabled 
23:32:17.914 ...  [c.t.t.e.t.TestResultLoggerExtension] >>> Test Aborted for test givenFalseIsTrue_whenTestAbortedThenCaptureResult:  
23:32:17.914 ...  [c.t.t.e.t.TestResultLoggerExtension] >>> Test Successful for test givenTrueIsTrue_whenTestAbortedThenCaptureResult:  

23:32:17.928 ...  [c.t.t.e.t.TestResultLoggerExtension] >>> Test result summary for TestWatcherAPIUnitTest {DISABLED=2, SUCCESSFUL=1, ABORTED=1} 
```

当然，当所有测试方法都完成后，我们还会看到打印的测试统计。

## 6. 注意点

让我们回顾一下使用TestWatcher接口时应该注意的几个细节：

+ TestWatcher Extension不允许影响测试的执行；**这意味着如果从TestWatcher抛出异常，它将不会传播到正在运行的测试**。
+ 目前，此API仅可用于报告@Test方法和@TestTemplate方法的测试结果。
+ 默认情况下，如果没有为带有@Disabled注解的测试方法提供测试被禁用的原因，
  则它将包含测试方法的全限定名称，后跟“is @Disabled”。

## 7. 总结

在本教程中，我们演示了如何使用JUnit 5 TestWatcher API来处理我们测试方法执行的结果。
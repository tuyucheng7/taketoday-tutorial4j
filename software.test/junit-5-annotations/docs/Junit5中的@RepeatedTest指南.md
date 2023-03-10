## 1. 概述

在这篇简短的文章中，我们将了解**JUnit 5中引入的@RepeatedTest**注解。它为我们提供了一种强大的功能来编写我们想要重复多次运行的任何测试。

如果你想了解有关JUnit 5的更多信息，请查看我们解释[JUnit 5](https://www.baeldung.com/junit-5)基础知识和[指南](https://www.baeldung.com/junit-5-preview)的其他文章。

## 2. Maven依赖

首先要注意的是JUnit 5需要Java 8才能运行。话虽如此，让我们看一下Maven依赖项：

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-engine</artifactId>
    <version>5.9.2</version>
    <scope>test</scope>
</dependency>
```

这是我们需要添加以编写测试的主要JUnit 5依赖项。在[此处](https://central.sonatype.com/artifact/org.junit.jupiter/junit-jupiter-engine/5.9.2)查看最新版本的工件。

## 3. @RepeatedTest例子

创建可重复执行的测试很简单-只需在测试方法上添加@RepeatedTest注解：

```java
@RepeatedTest(3)
void repeatedTest(TestInfo testInfo) {
    LOGGER.info("Executing repeated test");
    assertEquals(2, Math.addExact(1, 1), "1 + 1 should equal 2");
}
```

注意，我们使用@RepeatedTest标注单元测试方法，而不是标准的@Test注解。**上述测试方法将被执行3次**，就好像同一个测试被编写了3次一样。

测试报告(报告文件或IDE的JUnit选项卡中的结果)将显示所有执行：

<img src="../assets/img.png">

## 4. @RepeatedTest的生命周期支持

**@RepeatedTest的每次执行与常规的@Test一样，支持标准的测试生命周期**。这意味着，在每次执行期间，都会调用@BeforeEach和@AfterEach方法。为了演示这一点，只需在测试类中添加对应的生命周期方法：

```java
@BeforeEach
void beforeEachTest() {
    LOGGER.info("Before Each Test");
}

@AfterEach
void afterEachTest() {
    LOGGER.info("After Each Test");
    LOGGER.info("=======================");
}
```

如果我们运行之前的测试，结果将显示在控制台上：

```shell
19:49:33.089 [main] DEBUG cn.tuyucheng.taketoday.junit5.RepeatedTestAnnotationUnitTest - Before Each Test
19:49:33.094 [main] DEBUG cn.tuyucheng.taketoday.junit5.RepeatedTestAnnotationUnitTest - Executing repeated test
19:49:33.101 [main] DEBUG cn.tuyucheng.taketoday.junit5.RepeatedTestAnnotationUnitTest - After Each Test
19:49:33.101 [main] DEBUG cn.tuyucheng.taketoday.junit5.RepeatedTestAnnotationUnitTest - =====================
19:49:33.115 [main] DEBUG cn.tuyucheng.taketoday.junit5.RepeatedTestAnnotationUnitTest - Before Each Test
19:49:33.115 [main] DEBUG cn.tuyucheng.taketoday.junit5.RepeatedTestAnnotationUnitTest - Executing repeated test
19:49:33.115 [main] DEBUG cn.tuyucheng.taketoday.junit5.RepeatedTestAnnotationUnitTest - After Each Test
19:49:33.115 [main] DEBUG cn.tuyucheng.taketoday.junit5.RepeatedTestAnnotationUnitTest - =====================
19:49:33.117 [main] DEBUG cn.tuyucheng.taketoday.junit5.RepeatedTestAnnotationUnitTest - Before Each Test
19:49:33.117 [main] DEBUG cn.tuyucheng.taketoday.junit5.RepeatedTestAnnotationUnitTest - Executing repeated test
19:49:33.117 [main] DEBUG cn.tuyucheng.taketoday.junit5.RepeatedTestAnnotationUnitTest - After Each Test
19:49:33.117 [main] DEBUG cn.tuyucheng.taketoday.junit5.RepeatedTestAnnotationUnitTest - =====================
```

我们可以看到，**在每次执行时都会调用@BeforeEach和@AfterEach方法**。

## 5. 配置测试名称

在第一个示例中，我们观察到测试报告的输出不包含任何标识符。这可以使用@RepeatedTest注解的”name“属性进一步配置：

```java
@RepeatedTest(value = 3, name = RepeatedTest.LONG_DISPLAY_NAME)
void repeatedTestWithLongName() {
    LOGGER.info("Executing repeated test with long name");
    assertEquals(2, Math.addExact(1, 1), "1 + 1 should equal 2");
}
```

输出现在将包含方法名称和重复索引：

<img src="../assets/img_1.png">

另一种方式是使用RepeatedTest.SHORT_DISPLAY_NAME，这将生成测试的短名称：

```shell
repetition 1 of 3(repeatedTestWithShortName())
repetition 2 of 3(repeatedTestWithShortName())
repetition 3 of 3(repeatedTestWithShortName())
```

我们也可以使用我们自定义的名称：

```java
@RepeatedTest(value = 3, name = "Custom name {currentRepetition}/{totalRepetitions}")
void repeatedTestWithCustomDisplayName() {
    assertEquals(2, Math.addExact(1, 1), "1 + 1 should equal 2");
}
```

**{CurrentRepetition}和{TotalRepetitions}是当前测试和测试总数的占位符。这些值由JUnit在运行时自动提供，不需要额外的配置**。输出几乎符合我们的预期：

<img src="../assets/img_2.png">

## 6. RepetitionInfo

除了name属性外，JUnit还提供了对我们测试代码中重复元数据的访问。这是通过在我们的测试方法中添加一个RepetitionInfo参数来实现的：

```java
@RepeatedTest(3)
void repeatedTestWithRepetitionInfo(RepetitionInfo repetitionInfo) {
    LOGGER.info("Repetition # {}", repetitionInfo.getCurrentRepetition());
    assertEquals(3, repetitionInfo.getTotalRepetitions());
}
```

输出将包含每次执行的当前重复索引：

<img src="../assets/img_3.png">

RepetitionInfo由RepetitionInfoParameterResolver提供，并且由JUnit自动注入到我们的测试方法参数中，仅在@RepeatedTest的上下文中可用。

## 7. 总结

在这个快速教程中，我们探讨了JUnit提供的@RepeatedTest注解并学习了配置它的不同方法。
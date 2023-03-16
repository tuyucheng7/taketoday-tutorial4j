## 1. 概述

在本文中，我们将讨论使用TestNG生成自定义日志记录和报告。

TestNG提供了自己的报告功能-以HTML/XML格式生成报告。如果使用maven-surefire-plugin运行测试，则报告将采用插件定义的默认形式。除了内置报告外，它还提供了一种轻松自定义记录信息和生成报告的机制。

如果你想从TestNG基础开始，请查看[这篇文章](https://www.baeldung.com/testng)。

## 2. 自定义日志

在我们实现自定义日志记录之前，让我们通过执行mvn test命令来查看默认日志：

```shell
Tests run: 11, Failures: 1, Errors: 0, Skipped: 0, Time elapsed: 1.21 sec <<< FAILURE! - in TestSuite whenCalledFromSuite_thanOK(cn.tuyucheng.taketoday.RegistrationTest)  
Time elapsed: 0.01 sec  <<< FAILURE!
java.lang.AssertionError: Test Failed due to some reason at cn.tuyucheng.taketoday.RegistrationTest.whenCalledFromSuite_thanOK(RegistrationTest.java:15)

Results :

Failed tests: RegistrationTest.whenCalledFromSuite_thanOK:15 Test Failed due to some reason

Tests run: 11, Failures: 1, Errors: 0, Skipped: 0

[ERROR] There are test failures.
```

这些日志不会为我们提供有关执行顺序或特定测试何时开始/完成等的任何信息。

如果我们想知道每次运行的结果以及一些自定义数据，我们可以实现自己的日志和报告。TestNG提供了一种实现自定义报告和日志记录的方法。

**简单地说，我们可以实现用于日志记录的org.testng.ITestListener接口或用于报告的org.testng.IReporter接口**。这些实现的类会收到有关测试和套件的开始、结束、失败等事件的通知。

让我们继续实现一些简单的自定义日志记录：

```java
public class CustomisedListener implements ITestListener {

    // ...
    @Override
    public void onFinish(ITestContext testContext) {
        LOGGER.info("PASSED TEST CASES");
        testContext.getPassedTests().getAllResults().forEach(result -> {LOGGER.info(result.getName());});

        LOGGER.info("FAILED TEST CASES");
        testContext.getFailedTests().getAllResults().forEach(result -> {LOGGER.info(result.getName());});

        LOGGER.info("Test completed on: " + testContext.getEndDate().toString());
    }

    // ...
}
```

请注意我们是如何覆盖onFinish()方法的，该方法将在所有测试执行完成且所有配置完成时被调用。同样，我们可以覆盖其他方法-例如onTestStart()、onTestFailure()等([此处](http://javadox.com/org.testng/testng/6.8/org/testng/ITestListener.html)可以找到有关这些其他方法的详细信息)。

现在，让我们将这个监听器包含在XML配置中：

```xml
<suite name="My test suite">
    <listeners>
        <listener class-name="cn.tuyucheng.taketoday.reports.CustomisedListener"/>
    </listeners>
    <test name="numbersXML">
        <parameter name="value" value="1"/>
        <parameter name="isEven" value="false"/>
        <classes>
            <class name="cn.tuyucheng.taketoday.ParametrizedTests"/>
        </classes>
    </test>
</suite>
```

一旦执行，监听器将在每个事件上被调用，并在我们的实现中记录信息。这对于调试我们的测试执行可能很有用。

输出日志：

```shell
...
INFO CUSTOM_LOGS - Started testing on: Sat Apr 22 14:39:43 IST 2017
INFO CUSTOM_LOGS - Testing: givenNumberFromDataProvider_ifEvenCheckOK_thenCorrect
INFO CUSTOM_LOGS - Tested: givenNumberFromDataProvider_ifEvenCheckOK_thenCorrect Time taken:6 ms
INFO CUSTOM_LOGS - Testing: givenNumberObjectFromDataProvider_ifEvenCheckOK_thenCorrect
INFO CUSTOM_LOGS - Failed : givenNumberObjectFromDataProvider_ifEvenCheckOK_thenCorrect
INFO CUSTOM_LOGS - PASSED TEST CASES
INFO CUSTOM_LOGS - givenNumberFromDataProvider_ifEvenCheckOK_thenCorrect
INFO CUSTOM_LOGS - FAILED TEST CASES
INFO CUSTOM_LOGS - givenNumberObjectFromDataProvider_ifEvenCheckOK_thenCorrect
INFO CUSTOM_LOGS - Test completed on: Sat Apr 22 14:39:43 IST 2017
...
```

自定义日志为我们提供了默认日志中缺少的信息。

## 3. 自定义报告

当我们使用插件运行测试时，它会在target/surefire-reports目录中生成HTML/XML格式的报告：

<img src="../assets/img.png">

如果我们想使用TestNG XML文件运行一个特定的测试套件，我们需要在surefire插件的configuration标签中列出它：

```xml
<configuration>
    <suiteXmlFiles>
        <suiteXmlFile>
            src\test\resources\parametrized_testng.xml
        </suiteXmlFile>
    </suiteXmlFiles>
</configuration>
```

在自定义日志记录之后，现在让我们尝试创建一些自定义报告，我们在其中实现org.testng.IReporter接口并覆盖generateReport()方法：

```java
public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
    String reportTemplate = initReportTemplate();

    String body = suites
        .stream()
        .flatMap(suiteToResults())
        .collect(Collectors.joining());

    String report = reportTemplate.replaceFirst("</tbody>", String.format("%s</tbody>", body));
    saveReportTemplate(outputDirectory, report);
}
```

被重写的方法接收三个参数：

-   xmlSuite：包含XML文件中提到的所有套件的列表
-   suites：一个列表对象，包含有关测试执行的所有信息
-   outputDirectory：生成报告的目录路径

我们使用initReportTemplate()方法加载一个HTML模板，suiteToResults()函数调用resultsToRow()函数来处理生成报告的内部机制：

```java
private Function<ISuite, Stream<? extends String>> suiteToResults() {
    return suite -> suite.getResults().entrySet()
        .stream()
        .flatMap(resultsToRows(suite));
}

private Function<Map.Entry<String, ISuiteResult>, Stream<? extends String>> resultsToRows(ISuite suite) {
    return e -> {
        ITestContext testContext = e.getValue().getTestContext();

        Set<ITestResult> failedTests = testContext.getFailedTests().getAllResults();
        Set<ITestResult> passedTests = testContext.getPassedTests().getAllResults();
        Set<ITestResult> skippedTests = testContext.getSkippedTests().getAllResults();

        String suiteName = suite.getName();

        return Stream
            .of(failedTests, passedTests, skippedTests)
            .flatMap(results -> generateReportRows(e.getKey(), suiteName, results).stream());
    };
}
```

saveReportTemplate()用于保存完整的结果。

在XML配置文件中包含报告器：

```xml
<suite name="suite">
    <listeners>
        <listener class-name="cn.tuyucheng.taketoday.reports.CustomisedReports"/>
    </listeners>
    <test name="test suite">
        <classes>
            <class name="cn.tuyucheng.taketoday.RegistrationTest"/>
            <class name="cn.tuyucheng.taketoday.SignInTest"/>
        </classes>
    </test>
</suite>
```

这是我们报告的输出：

<img src="../assets/img_1.png">

与默认的surefire HTML报告相比，此报告在单个表格中提供了清晰明了的结果图片。哪个更方便，更易于阅读。

## 4. 总结

在这个快速教程中，我们学习了如何使用Surefire Maven插件生成测试报告。我们还研究了使用TestNG自定义日志和生成自定义报告。有关TestNG的更多详细信息，例如如何编写测试用例、套件，请务必从我们的[介绍性文章](https://www.baeldung.com/testng)开始。
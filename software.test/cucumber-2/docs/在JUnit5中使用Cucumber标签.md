## 1. 概述

在本教程中，我们将介绍如何使用Cucumber标记表达式来操纵测试的执行及其相关设置。

我们将研究如何分离我们的API和UI测试并控制我们为每个测试运行的配置步骤。

## 2. 带有UI和API组件的应用程序

我们的应用程序有一个简单的UI，用于生成给定范围内的随机数：

<img src="../assets/img.png">

我们还有一个/status Rest端点返回一个HTTP状态代码。我们将使用[Cucumber](https://www.baeldung.com/cucumber-rest-api-testing)和 [Junit 5](https://www.baeldung.com/junit-5)通过验收测试涵盖这两个功能。

为了让Cucumber与Junit 5一起工作，我们必须在我们的pom中声明[cucumber–junit-platform-engine](https://search.maven.org/artifact/io.cucumber/cucumber-junit-platform-engine)作为它的依赖项：

```xml
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-junit-platform-engine</artifactId>
    <version>6.10.3</version>
</dependency>
```

## 3. Cucumber标签和条件钩子

Cucumber标签可以帮助我们将场景分组在一起，假设我们对测试UI和API有不同的要求。例如，我们需要启动一个浏览器来测试UI组件，但这不是调用/status端点所必需的。我们需要的是一种方法来确定运行哪些步骤以及何时运行，Cucumber标签可以帮助我们解决这个问题。

## 4. UI测试

首先，让我们通过标签将我们的功能或场景分组在一起。在这里，我们用@ui标签标记我们的UI Feature：

```gherkin
@ui
Feature: UI - Random Number Generator

    Scenario: Successfully generate a random number
        Given we are expecting a random number between min and max
        And I am on random-number-generator page
        When I enter min 1
        And I enter max 10
        And I press Generate button
        Then I should receive a random number between 1 and 10
```

然后，基于这些标签，我们可以通过使用条件钩子来操纵我们为这组Feature运行的内容。我们使用单独的@Before和@After方法执行此操作，并在ScenarioHooks中使用相关标签进行标注：

```java
@Before("@ui")
public void setupForUI() {
    uiContext.getWebDriver();
}
```

```java
@After("@ui")
public void tearDownForUi(Scenario scenario) throws IOException {
    uiContext.getReport().write(scenario);
    uiContext.getReport().captureScreenShot(scenario, uiContext.getWebDriver());
    uiContext.getWebDriver().quit();
}
```

## 5. API测试

与我们的UI测试类似，我们可以使用@api标签标记我们的API功能：

```gherkin
@api
Feature: Health check

    Scenario: Should hava a working health check
        When I make a GET call on /status
        Then I should receive 200 response status code
        And should receive a non-empty body
```

我们也有带有@api标签的@Before和@After方法：

```java
@Before("@api")
public void setupForApi() {
    RestAssuredMockMvc.mockMvc(mvc);
    RestAssuredMockMvc.config = RestAssuredMockMvc.config()
      .logConfig(new LogConfig(apiContext.getReport().getRestLogPrintStream(), true));
}

@After("@api")
public void tearDownForApi(Scenario scenario) throws IOException {
    apiContext.getReport().write(scenario);
}
```

当我们运行AcceptanceTestRunnerIT时，我们可以看到我们正在为相关测试执行适当的设置和拆卸步骤。

## 6. 总结

在本文中，我们演示了如何使用Cucumber标签和条件Hooks来控制不同测试集及其设置/拆卸指令的执行。
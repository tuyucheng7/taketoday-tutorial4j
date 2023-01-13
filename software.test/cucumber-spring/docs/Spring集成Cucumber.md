## 一、概述

Cucumber 是一个用 Ruby 编程语言编写的非常强大的测试框架，它遵循 BDD（行为驱动开发）方法论。它使开发人员能够以纯文本形式编写可由非技术利益相关者验证的高级用例，并将它们转化为可执行测试，并以一种称为 Gherkin 的语言编写。

我们已经在[另一篇文章](https://www.baeldung.com/cucumber-rest-api-testing)中讨论过这些。

[Cucumber-Spring Integration](https://spring.io/blog/2013/08/04/webinar-replay-spring-with-cucumber-for-automation)旨在简化测试自动化。一旦我们将 Cucumber 测试与 Spring 集成，我们应该能够将它们与 Maven 构建一起执行。

## 2.Maven依赖

让我们通过定义 Maven 依赖项开始使用 Cucumber-Spring 集成——从 Cucumber-JVM 依赖项开始：

```xml
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-java</artifactId>
    <version>6.8.0</version>
    <scope>test</scope>
</dependency>
```

[我们可以在这里](https://mvnrepository.com/artifact/io.cucumber/cucumber-jvm)找到最新版本的 Cucumber JVM 。

接下来，我们将添加 JUnit 和 Cucumber 测试依赖项：

```xml
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-junit</artifactId>
    <version>6.8.0</version>
    <scope>test</scope>
</dependency>
```

[可以在此处](https://mvnrepository.com/artifact/io.cucumber/cucumber-junit)找到最新版本的 Cucumber JUnit 。

最后，Spring 和 Cucumber 依赖项：

```xml
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-spring</artifactId>
    <version>6.8.0</version>
    <scope>test</scope>
</dependency>
```

[同样，我们可以在此处](https://mvnrepository.com/artifact/io.cucumber/cucumber-spring)查看最新版本的 Cucumber Spring 。

## 三、配置

我们现在将看看如何将 Cucumber 集成到 Spring 应用程序中。

首先，我们将创建一个 Spring Boot 应用程序——为此我们将遵循[Spring-Boot 应用程序一文](https://www.baeldung.com/spring-boot-application-configuration)。然后，我们将创建一个 Spring REST 服务并为其编写 Cucumber 测试。

### 3.1. 休息控制器

首先，让我们创建一个简单的控制器：

```java
@RestController
public class VersionController {
    @GetMapping("/version")
    public String getVersion() {
        return "1.0";
    }
}
```

### 3.2. 黄瓜步骤定义

使用 JUnit 运行 Cucumber 测试所需要做的就是创建一个带有注释@RunWith(Cucumber.class)的空类：

```java
@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources")
public class CucumberIntegrationTest {
}
```

我们可以看到注释@CucumberOptions，我们在其中指定 Gherkin 文件的位置，也称为特征文件。此时，Cucumber 识别出 Gherkin 语言；你可以在介绍中提到的文章中阅读更多关于 Gherkin 的信息。

那么现在，让我们创建一个 Cucumber 特征文件：

```ruby
Feature: the version can be retrieved
  Scenario: client makes call to GET /version
    When the client calls /version
    Then the client receives status code of 200
    And the client receives server version 1.0
```

场景是对 REST 服务 url /version进行 GET 调用并验证响应。

接下来，我们需要创建一个所谓的胶水代码。这些是将单个 Gherkin 步骤与 Java 代码链接起来的方法。

我们必须在这里选择——我们可以在注释中使用[Cucumber 表达式或正则表达式。](https://cucumber.io/docs/cucumber/cucumber-expressions/)在我们的例子中，我们将坚持使用正则表达式：

```java
@When("^the client calls /version$")
public void the_client_issues_GET_version() throws Throwable{
    executeGet("http://localhost:8080/version");
}

@Then("^the client receives status code of (\\d+)$")
public void the_client_receives_status_code_of(int statusCode) throws Throwable {
    HttpStatus currentStatusCode = latestResponse.getTheResponse().getStatusCode();
    assertThat("status code is incorrect : "+ 
    latestResponse.getBody(), currentStatusCode.value(), is(statusCode));
}

@And("^the client receives server version (.+)$")
public void the_client_receives_server_version_body(String version) throws Throwable {
    assertThat(latestResponse.getBody(), is(version));
}
```

所以现在让我们将 Cucumber 测试与 Spring Application Context 集成。为此，我们将创建一个新类并使用@SpringBootTest和@CucumberContextConfiguration对其进行注释：

```java
@CucumberContextConfiguration
@SpringBootTest
public class SpringIntegrationTest {
    // executeGet implementation
}
```

现在，所有 Cucumber 定义都可以进入一个单独的 Java 类，该类扩展了 SpringIntegrationTest：

```java
public class StepDefs extends SpringIntegrationTest {
   
    @When("^the client calls /version$")
    public void the_client_issues_GET_version() throws Throwable {
        executeGet("http://localhost:8080/version");
    }
}
```

我们现在都准备好进行试运行了。

最后，我们可以通过命令行快速运行，只需运行mvn clean install -Pintegration-lite-first – Maven 将执行集成测试并在控制台中显示结果。

```bash
3 Scenarios ([32m3 passed[0m)
9 Steps ([32m9 passed[0m)
0m1.054s

Tests run: 12, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 9.283 sec - in
  com.baeldung.CucumberTest
2016-07-30 06:28:20.142  INFO 732 --- [Thread-2] AnnotationConfigEmbeddedWebApplicationContext :
  Closing org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext:
  startup date [Sat Jul 30 06:28:12 CDT 2016]; root of context hierarchy

Results :

Tests run: 12, Failures: 0, Errors: 0, Skipped: 0

```

## 4。结论

使用 Spring 配置 Cucumber 后，在 BDD 测试中使用 Spring 配置的组件会很方便。这是将 Cucumber 测试集成到 Spring-Boot 应用程序中的简单指南。
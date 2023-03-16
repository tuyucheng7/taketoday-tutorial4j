## 1. 概述

Cucumber是一个用Ruby编程语言编写的非常强大的测试框架，它遵循BDD(行为驱动开发)方法论。它使开发人员能够以纯文本形式编写可由非技术利益相关者验证的高级用例，并将它们转化为可执行测试，以一种称为Gherkin的语言编写。

我们已经在[另一篇文章](https://www.baeldung.com/cucumber-rest-api-testing)中讨论过这些。

[Cucumber-Spring集成](https://spring.io/blog/2013/08/04/webinar-replay-spring-with-cucumber-for-automation)旨在简化测试自动化。一旦我们将Cucumber测试与Spring集成在一起，我们应该能够将它们与Maven构建一起执行。

## 2. Maven依赖

让我们通过定义Maven依赖项开始使用Cucumber-Spring集成-从Cucumber-JVM依赖项开始：

```xml
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-java</artifactId>
    <version>6.8.0</version>
    <scope>test</scope>
</dependency>
```

Cucumber JUnit的最新版本可以在[这里](https://central.sonatype.com/artifact/io.cucumber/cucumber-java/7.11.1)找到。

接下来，我们将添加JUnit和Cucumber测试依赖项：

```xml
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-junit</artifactId>
    <version>6.8.0</version>
    <scope>test</scope>
</dependency>
```

可以在[此处](https://central.sonatype.com/artifact/io.cucumber/cucumber-junit/7.11.1)找到最新版本的Cucumber JUnit。

最后，Spring和Cucumber依赖项：

```xml
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-spring</artifactId>
    <version>6.8.0</version>
    <scope>test</scope>
</dependency>
```

同样，我们可以在[此处](https://central.sonatype.com/artifact/io.cucumber/cucumber-spring/7.11.1)查看最新版本的Cucumber Spring。

## 3. 配置

我们现在将看看如何将Cucumber集成到Spring应用程序中。

首先，我们将创建一个Spring Boot应用程序-为此我们将遵循[Spring Boot应用程序](https://www.baeldung.com/spring-boot-application-configuration)一文。然后，我们将创建一个Spring REST服务并为其编写Cucumber测试。

### 3.1 REST服务

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

### 3.2 Cucumber步骤定义

使用JUnit运行Cucumber测试所需要做的就是创建一个带有注解@RunWith(Cucumber.class)的空类：

```java
@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources")
public class CucumberIntegrationTest {
}
```

我们可以看到注解@CucumberOptions，其中我们指定了Gherkin文件(也称为Feature文件)的位置。此时，Cucumber识别出Gherkin语言；你可以在介绍中提到的文章中阅读更多关于Gherkin的信息。

那么现在，让我们创建一个Cucumber功能文件：

```gherkin
Feature: the version can be retrieved
    Scenario: client makes call to GET /version
        When the client calls /version
        Then the client receives status code of 200
        And the client receives server version 1.0
```

Scenario是对REST服务url/version进行GET调用并验证响应。

接下来，我们需要创建一个所谓的步骤定义代码。这些是将单个Gherkin步骤与Java代码链接起来的方法。

我们必须在这里选择-我们可以在注解中使用[Cucumber表达式](https://cucumber.io/docs/cucumber/cucumber-expressions/)或正则表达式。在我们的例子中，我们将坚持使用正则表达式：

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

所以现在让我们将Cucumber测试与Spring ApplicationContext集成。为此，我们将创建一个新类并使用@SpringBootTest和@CucumberContextConfiguration对其进行标注：

```java
@CucumberContextConfiguration
@SpringBootTest
public class SpringIntegrationTest {
    // executeGet implementation
}
```

现在，所有Cucumber定义都可以放入一个单独的Java类，该类扩展了SpringIntegrationTest：

```java
public class StepDefs extends SpringIntegrationTest {

    @When("^the client calls /version$")
    public void the_client_issues_GET_version() throws Throwable {
        executeGet("http://localhost:8080/version");
    }
}
```

最后，我们可以通过命令行快速运行，只需运行**mvn clean install -Pintegration-lite-first**，Maven将执行集成测试并在控制台中显示结果。

```shell
3 Scenarios ([32m3 passed[0m)
9 Steps ([32m9 passed[0m)
0m1.054s

Tests run: 12, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 9.283 sec - in
  cn.tuyucheng.taketoday.CucumberTest
2022-07-30 06:28:20.142  INFO 732 --- [Thread-2] AnnotationConfigEmbeddedWebApplicationContext :
  Closing org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext:
  startup date [Sat Jul 30 06:28:12 CDT 2016]; root of context hierarchy

Results :

Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
```

## 4. 总结

使用Spring配置Cucumber后，在BDD测试中使用Spring配置的组件会很方便。这是将Cucumber测试集成到Spring Boot应用程序中的简单指南。
## 1. 概述

本教程介绍了[Cucumber](https://cucumber.io/)(一种用于用户验收测试的常用工具)以及如何在REST API测试中使用它。

此外，为了使文章自包含并独立于任何外部REST服务，我们将使用WireMock，这是一个stub和mock Web服务库。如果你想了解更多关于这个库的信息，请参考[WireMock简介](WireMock简介.md)。

## 2. Gherkin-Cucumber使用的语言

Cucumber是一个支持[行为驱动开发(BDD)](https://www.baeldung.com/cs/bdd-guide)的测试框架，允许用户以纯文本形式定义应用程序操作。它基于[Gherkin领域特定语言](https://github.com/cucumber/cucumber/wiki/Gherkin)(DSL)工作。Gherkin的这种简单而强大的语法允许开发人员和测试人员可以编写复杂的测试，同时让非技术用户也能理解它。

### 2.1 Gherkin简介

Gherkin是一种面向行的语言，使用行尾、缩进和关键字来定义文档。每个非空行通常以Gherkin的关键字开头，后跟任意文本，通常是对关键字的描述。

整个结构必须写入具有feature扩展名的文件中，才能被Cucumber识别。

这是一个简单的Gherkin文档示例：

```gherkin
Feature: A short description of the desired functionality

	Scenario: A business situation
		Given a precondition
		And another precondition
		When an event happens
		And another event happens too
		Then a testable outcome is achieved
		And something else is also completed
```

在接下来的部分中，我们将描述Gherkin结构中的几个最重要的元素。

### 2.2 Feature

我们使用Gherkin文件来描述需要测试的应用程序功能。该文件的开头包含Feature关键字，随后是在同一行上的Feature名称以及下面可能跨多行的可选说明。

Cucumber解析器会跳过除Feature关键字之外的所有文本，并仅出于文档目的包含它。

### 2.3 场景和步骤

Gherkin结构可能包含一个或多个场景，由Scenario关键字识别。场景基本上是允许用户验证应用程序功能的测试。它应该描述初始背景、可能发生的事件以及这些事件产生的预期结果。

这些事情是使用步骤完成的，这些步骤由五个关键字之一标识：Given、When、Then、And和But。

-   Given：此步骤是在用户开始与应用程序交互之前将系统置于明确定义的状态。可以将Given子句视为测试用例的先决条件。
-   When：When步骤用于描述应用程序发生的事件。这可以是用户执行的操作，也可以是另一个系统触发的事件。
-   Then：此步骤是指定测试的预期结果。结果应该与被测试功能的业务价值相关。
-   And和But：当有多个相同类型的步骤时，可以使用这些关键字替换上述步骤关键字。

Cucumber实际上并没有区分这些关键字，但是它们仍然存在以使该功能更具可读性并与BDD结构保持一致。

## 3. Cucumber-JVM实现

Cucumber最初是用Ruby编写的，并且已经通过Cucumber-JVM实现移植到Java中，这是本节的主题。

### 3.1 Maven依赖项

为了在Maven项目中使用Cucumber-JVM，POM中需要包含以下依赖项：

```xml
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-java</artifactId>
    <version>6.8.0</version>
    <scope>test</scope>
</dependency>
```

为了方便使用Cucumber进行JUnit测试，我们需要添加另外一个依赖项：

```xml
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-junit</artifactId>
    <version>6.8.0</version>
</dependency>
```

或者，我们可以使用另一个依赖cucumber-java8来利用Java 8中的lambda表达式，这不会在本教程中介绍。

### 3.2 步骤定义

如果不将Gherkin场景转化为行动，它们将毫无用处，这就是步骤定义发挥作用的地方。基本上，步骤定义是带有附加模式的带注解的Java方法，其工作是将纯文本格式的Gherkin步骤转换为可执行代码。解析功能文档后，Cucumber将搜索与要执行的预定义Gherkin步骤匹配的步骤定义。

为了更清楚，让我们看一下以下步骤：

```gherkin
Given I have registered a course in Tuyucheng
```

和对应的步骤定义：

```java
@Given("I have registered a course in Tuyucheng")
public void verifyAccount() {
    // method implementation
}
```

当Cucumber读取给定的步骤时，它将寻找其注解模式与Gherkin文本匹配的步骤定义。

## 4. 创建和运行测试

### 4.1 编写Feature文件

让我们首先在以.feature扩展名结尾的文件中声明场景和步骤：

```gherkin
Feature: Testing a REST API
	Users should be able to submit GET and POST requests to a web service,
	represented by WireMock

	Scenario: Data Upload to a web service
		When users upload data on a project
		Then the server should handle it and return a success status

	Scenario: Data retrieval from a web service
		When users want to get information on the 'Cucumber' project
		Then the requested data is returned
```

我们现在将此文件保存在名为Feature的目录中，条件是该目录将在运行时加载到类路径中，例如src/main/resources。

### 4.2 配置JUnit以使用Cucumber

为了让JUnit在运行时识别Cucumber并读取Feature文件，必须将Cucumber类声明为Runner。我们还需要告诉JUnit在哪里搜索Feature文件和步骤定义。

```java
@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:feature")
public class CucumberIntegrationTest {
}
```

如你所见，@CucumberOption的features属性定位到之前创建的feature文件。另一个重要的属性称为glue，它提供步骤定义的路径。但是，如果测试用例和步骤定义位于同一包中(与本教程一样)，则可以不指定该属性。

### 4.3 编写步骤定义

当Cucumber解析步骤时，它会搜索带有Gherkin关键字注解的方法来定位匹配的步骤定义。

步骤定义的表达式可以是正则表达式或Cucumber表达式。在本教程中，我们将使用Cucumber表达式。

以下是完全匹配一个Gherkin步骤的方法。该方法用于将数据发布到REST Web服务：

```java
@When("users upload data on a project")
public void usersUploadDataOnAProject() throws IOException {
    
}
```

下面是一个匹配Gherkin步骤并从文本中获取参数的方法，该参数将用于从REST Web服务获取信息：

```java
@When("users want to get information on the {string} project")
public void usersGetInformationOnAProject(String projectName) throws IOException {
    
}
```

如你所见，usersGetInformationOnAProject方法接收一个String参数，即projectName。此参数由注解中的{string}声明，在这里它对应于步骤文本中的“Cucumber”。

或者，我们可以使用正则表达式：

```java
@When("^users want to get information on the '(.+)' project$")
public void usersGetInformationOnAProject(String projectName) throws IOException {
    
}
```

请注意，'^'和'$'相应地指示正则表达式的开始和结束。而'(.+)'对应于String参数。

我们将在下一节中提供上述两种方法的工作代码。

### 4.4 创建和运行测试

首先，我们将从一个JSON结构开始，来说明通过POST请求上传到服务器并使用GET下载到客户端的数据。该结构保存在jsonString字段中，如下所示：

```json
{
    "testing-framework": "cucumber",
    "supported-language": [
        "Ruby",
        "Java",
        "Javascript",
        "PHP",
        "Python",
        "C++"
    ],
    "website": "cucumber.io"
}
```

为了演示REST API，我们使用WireMock服务器：

```java
WireMockServer wireMockServer = new WireMockServer(options().dynamicPort());
```

此外，我们将使用Apache HttpClient API来表示用于连接到服务器的客户端：

```java
CloseableHttpClient httpClient = HttpClients.createDefault();
```

现在，让我们继续在步骤定义中编写测试代码。首先我们为usersUploadDataOnAProject方法执行此操作。

服务器应该在客户端连接到它之前运行：

```java
wireMockServer.start();
```

使用WireMock API stub REST服务：

```java
configureFor("localhost", wireMockServer.port());
stubFor(post(urlEqualTo("/create"))
    .withHeader("content-type", equalTo(APPLICATION_JSON))
    .withRequestBody(containing("testing-framework"))
    .willReturn(aResponse().withStatus(200)));
```

现在，向服务器发送一个POST请求，其中包含从上面声明的jsonString字段中获取的内容：

```java
HttpPost request = new HttpPost("http://localhost:" + wireMockServer.port() + "/create");
StringEntity entity = new StringEntity(jsonString);
request.addHeader("content-type", "application/json");
request.setEntity(entity);
HttpResponse response = httpClient.execute(request);
```

以下代码断言已成功接收和处理POST请求：

```java
assertEquals(200, response.getStatusLine().getStatusCode());
verify(postRequestedFor(urlEqualTo("create"))
    .withHeader("content-type", equalTo("application/json")));
```

服务器应在使用后停止：

```java
wireMockServer.stop();
```

我们将在这里实现的第二种方法是usersGetInformationOnAProject(String projectName)。与第一个测试类似，我们需要启动服务器，然后stub REST服务：

```java
wireMockServer.start();

configureFor("localhost", wireMockServer.port());
stubFor(get(urlEqualTo("/projects/cucumber"))
    .withHeader("accept", equalTo("application/json"))
    .willReturn(aResponse().withBody(jsonString)));
```

提交GET请求并接收响应：

```java
HttpGet request = new HttpGet("http://localhost:" + wireMockServer.port() + "/projects/" + projectName.toLowerCase());
request.addHeader("accept", "application/json");
HttpResponse httpResponse = httpClient.execute(request);
```

我们使用工具方法将httpResponse变量转换为字符串：

```java
String responseString = convertResponseToString(httpResponse);
```

下面是该转换工具方法的实现：

```java
private String convertResponseToString(HttpResponse response) throws IOException {
	InputStream responseStream = response.getEntity().getContent();
	Scanner scanner = new Scanner(responseStream, "UTF-8");
	String responseString = scanner.useDelimiter("\\Z").next();
	scanner.close();
	return responseString;
}
```

下面验证整个过程：

```java
assertThat(responseString, containsString("\"testing-framework\": \"cucumber\""));
assertThat(responseString, containsString("\"website\": \"cucumber.io\""));
verify(getRequestedFor(urlEqualTo("/projects/cucumber")).withHeader("accept", equalTo("application/json")));
```

最后，如前所述停止服务器。

## 5. 并行运行Feature

Cucumber-JVM原生支持跨多个线程的并行测试执行。我们将使用JUnit和Maven Failsafe插件来执行Runner。或者，我们可以使用Maven Surefire。

JUnit并行运行Feature文件而不是场景，这意味着**Feature文件中的所有场景都将由同一个线程执行**。

现在让我们添加插件配置：

```xml
<plugin>
    <artifactId>maven-failsafe-plugin</artifactId>
    <version>${maven-failsafe-plugin.version}</version>
    <configuration>
        <includes>
            <include>CucumberIntegrationTest.java</include>
        </includes>
        <parallel>methods</parallel>
        <threadCount>2</threadCount>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>integration-test</goal>
                <goal>verify</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

注意：

-   parallel：可以是classes、methods或两者都有-在我们的例子中，classes将使每个测试类在单独的线程中运行
-   threadCount：表示应该为此执行分配多少个线程

这就是我们并行运行Cucumber测试所需要指定的所有配置。

## 6. 总结

在本教程中，我们介绍了Cucumber的基础知识，以及该框架如何使用Gherkin领域特定语言来测试REST API。
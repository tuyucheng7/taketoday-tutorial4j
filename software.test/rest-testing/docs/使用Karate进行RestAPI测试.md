## 1. 概述

在本文中，我们将介绍[Karate](https://github.com/intuit/karate)，这是一个用于Java的行为驱动开发(BDD)测试框架。

## 2. Karate和BDD

**Karate建立在另一个[BDD测试](https://www.baeldung.com/cs/bdd-guide)框架Cucumber之上**，并共享一些相同的概念。其中之一是**使用Gherkin文件，该文件描述了测试的功能**。但是，与Cucumber不同的是，测试不是用Java编写的，而是在Gherkin文件中进行了完整的描述。

Gherkin文件以“.feature”扩展名保存。它以Feature关键字开头，后跟同一行上的功能名称。它还包含不同的测试场景，每个场景都以关键字Scenario开头，并由关键字Given、When、Then、And和But的多个步骤组成。

更多关于Cucumber和Gherkin结构的信息可以在[这里](使用Cucumber进行RestAPI测试.md)找到。

## 3. Maven依赖

要在Maven项目中使用Karate，我们需要在pom.xml中添加[karate-apache](https://central.sonatype.com/artifact/com.intuit.karate/karate-apache/0.9.6)依赖项：

```xml
<dependency>
    <groupId>com.intuit.karate</groupId>
    <artifactId>karate-apache</artifactId>
    <version>0.6.0</version>
</dependency>
```

我们还需要[karate-junit4](https://central.sonatype.com/artifact/com.intuit.karate/karate-junit4/1.4.0.RC3)依赖项来集成JUnit测试：

```xml
<dependency>
    <groupId>com.intuit.karate</groupId>
    <artifactId>karate-junit4</artifactId>
    <version>0.6.0</version>
</dependency>
```

## 4. 创建测试

我们将首先在Gherkin功能文件中为一些常见场景编写测试。

### 4.1 测试状态码

让我们编写一个场景来测试GET端点并检查它是否返回200(OK) HTTP状态代码：

```gherkin
Scenario: Testing valid GET endpoint
Given url 'http://localhost:8097/user/get'
When method GET
Then status 200
```

这显然适用于所有可能的HTTP状态码。

### 4.2 测试响应

让我们编写另一个场景来测试REST端点是否返回特定响应：

```gherkin
Scenario: Testing the exact response of a GET endpoint
Given url 'http://localhost:8097/user/get'
When method GET
Then status 200
And match $ == {id:"1234",name:"John Smith"}
```

**match操作用于验证，其中“$”表示响应**。因此，上述场景检查响应是否与'{id："1234"，name："John Smith"}'完全匹配。

我们还可以专门检查id字段的值：

```gherkin
And match $.id == "1234"
```

**match操作也可用于检查响应是否包含某些字段**。当只需要检查某些字段或并非所有响应字段都已知时，这很有用：

```gherkin
Scenario: Testing that GET response contains specific field
Given url 'http://localhost:8097/user/get'
When method GET
Then status 200
And match $ contains {id:"1234"}
```

### 4.3 使用标记验证响应值

在我们不知道返回的确切值的情况下，我们仍然可以使用标记(响应中匹配字段的占位符)来验证该值。

例如，我们可以使用标记来指示我们是否期望空值：

-   #null
-   #notnull

或者我们可以使用标记来匹配字段中某种类型的值：

-   #boolean
-   #number
-   #string

当我们期望字段包含JSON对象或数组时，可以使用其他标记：

-   #array
-   #object

还有一些用于匹配某种格式或正则表达式的标记，以及用于评估布尔表达式的标记：

-   #uuid：值符合UUID格式
-   #regex STR：值与正则表达式STR匹配
-   #? EXPR：断言JavaScript表达式EXPR的计算结果为true

最后，如果我们不想对某个字段进行任何类型的检查，我们可以使用#ignore标记。

让我们重写上面的场景来检查id字段是否不为null：

```gherkin
Scenario: Test GET request exact response
Given url 'http://localhost:8097/user/get'
When method GET
Then status 200
And match $ == {id:"#notnull",name:"John Smith"}
```

### 4.4 使用请求正文测试POST端点

下面是一个测试POST端点并获取请求正文的最终场景：

```gherkin
Scenario: Testing a POST endpoint with request body
Given url 'http://localhost:8097/user/create'
And request { id: '1234' , name: 'John Smith'}
When method POST
Then status 200
And match $ contains {id:"#notnull"}
```

## 5. 运行测试

现在测试场景已经编写完成，我们可以通过将Karate与JUnit集成来运行我们的测试。

我们将使用@CucumberOptions注解来指定Feature文件的确切位置：

```java
@RunWith(Karate.class)
@CucumberOptions(features = "classpath:karate")
public class KarateIntegrationTest {
    // ...
}
```

为了演示REST API，我们将使用[WireMock服务器](WireMock简介.md)。

对于这个例子，我们在使用@BeforeClass标注的方法中mock了测试的所有端点，并且在使用@AfterClass标注的方法中关闭WireMock服务器：

```java
private static final int PORT_NUMBER = 8097;

private static final WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.options().port(PORT_NUMBER));

@BeforeClass
public static void setUp() throws Exception {
	wireMockServer.start();
    
	configureFor("localhost", PORT_NUMBER);
	stubFor(get(urlEqualTo("/user/get"))
	    .willReturn(aResponse()
	        .withStatus(200)
	        .withHeader("Content-Type", "application/json")
	        .withBody("{ \"id\": \"1234\", name: \"John Smith\" }")));
	stubFor(post(urlEqualTo("/user/create"))
	    .withHeader("content-type", equalTo("application/json"))
	    .withRequestBody(containing("id"))
	    .willReturn(aResponse()
	        .withStatus(200)
	        .withHeader("Content-Type", "application/json")
	        .withBody("{ \"id\": \"1234\", name: \"John Smith\" }")));
}

@AfterClass
public static void tearDown() throws Exception {
	wireMockServer.stop();
}
```

当我们运行KarateIntegrationTest类时，REST端点由WireMock服务器创建，并运行指定Feature文件中的所有场景。

## 6. 总结

在本教程中，我们介绍了如何使用Karate测试框架测试REST API。
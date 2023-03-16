## 1. 概述

在本文中，我们将介绍[Hoverfly](https://hoverfly.readthedocs.io/en/latest/#)Java库-它提供了一种创建真实API存根/模拟的简单方法。

## 2. Maven依赖

要使用Hoverfly，我们需要添加一个Maven依赖项：

```xml
<dependency>
    <groupId>io.specto</groupId>
    <artifactId>hoverfly-java</artifactId>
    <version>0.8.1</version>
</dependency>
```

最新版本可以在[这里](https://central.sonatype.com/artifact/io.specto/hoverfly-java/0.14.4)找到。

## 3. 模拟API

首先，我们将Hoverfly配置为在模拟模式下运行。**定义模拟的最简单方法是使用DSL**。

让我们从一个简单的例子开始，通过实例化HoverflyRule实例：

```java
public static final HoverflyRule rule = HoverflyRule.inSimulationMode(dsl(
    service("http://www.tuyucheng.com")
        .get("/api/courses/1")
        .willReturn(success().body(jsonWithSingleQuotes("{'id':'1','name':'HCI'}"))));
```

SimulationSource类提供了一个用于启动API定义的dsl方法。此外，Hoverfly DSL的service方法允许我们定义端点和相关的请求路径。

然后我们调用willReturn来说明我们想要得到哪个响应作为回报。我们还使用了ResponseBuilder的success方法来设置响应状态和正文。

## 4. 使用JUnit进行测试

使用JUnit可以轻松测试Stubbed API。

让我们创建一个发送HTTP请求的简单测试，看看它是否到达端点：

```java
responseEntity<String> courseResponse = restTemplate.getForEntity("http://www.baeldung.com/api/courses/1", String.class);
 
assertEquals(HttpStatus.OK, courseResponse.getStatusCode());
assertEquals("{\"id\":\"1\",\"name\":\"HCI\"}", courseResponse.getBody());
```

我们使用Spring Web模块的RestTemplate类实例来发送HTTP请求。

## 5. 添加延迟

**可以为特定的HTTP方法或特定的API调用全局添加延迟**。

以下是使用POST方法设置请求延迟的示例代码：

```java
SimulationSource.dsl(service("http://www.tuyucheng.com")
    .post("/api/courses")
    .willReturn(success())
    .andDelay(3, TimeUnit.SECONDS)
    .forMethod("POST")
)
```

## 6. 请求匹配器

HoverflyMatchers工厂类提供了多个匹配器，包括用于URL的exactMatch和globMatch。

对于HTTP主体，它提供JSON/XML精确匹配和JSONPath/XPath匹配。

默认情况下，exactMatch匹配器用于URL和正文匹配。

以下是使用不同匹配器的示例：

```java
SimulationSource.dsl(service(matches("www.*dung.com"))
    .get(startsWith("/api/student")) 
    .queryParam("page", any()) 
    .willReturn(success())
 
    .post(equalsTo("/api/student"))
    .body(equalsToJson(jsonWithSingleQuotes("{'id':'1','name':'Joe'}")))
    .willReturn(success())
 
    .put("/api/student/1")
    .body(matchesJsonPath("$.name")) 
    .willReturn(success())
 
    .post("/api/student")
    .body(equalsToXml("<student><id>2</id><name>John</name></student>"))
    .willReturn(success())
 
    .put("/api/student/2")
    .body(matchesXPath("/student/name")) 
    .willReturn(success()));
)
```

在此示例中，matches方法使用允许通配符搜索的globMatch检查URL。

然后startsWith检查请求路径是否以“/api/student”开头。我们使用任何匹配器来允许页面查询参数中的所有可能值。

equalsToJson匹配器确保正文有效负载与此处给出的确切JSON匹配。matchesJsonPath方法用于检查特定JSON路径中的元素的是否存在。

同样，equalsToXml将请求正文中给出的XML与此处给出的XML匹配。matchesXPath将正文与XPath表达式匹配。

## 7. 总结

在这个快速教程中，我们讨论了Hoverfly Java库的使用。我们研究了模拟HTTP服务、用于配置端点的DSL、添加延迟和使用请求匹配器。我们还研究了使用JUnit测试这些服务。
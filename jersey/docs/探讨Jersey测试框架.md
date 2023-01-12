## 1. 概述

在本教程中，我们将了解 Jersey 测试框架并了解如何使用它来快速编写集成测试。

正如我们在之前的文章中已经看到的，[Jersey](https://jersey.github.io/)是一个用于开发 RESTful Web 服务的开源框架。我们可以在介绍使用 Jersey 和 Spring 创建 API 的文章中了解更多关于 Jersey 的信息 – [此处](https://www.baeldung.com/jersey-rest-api-with-spring)。

## 2. 应用设置

Jersey 测试框架是一个帮助我们验证服务器端组件正确实现的工具。正如我们稍后将看到的，它提供了一种快速且无忧无虑的方式来编写集成测试，并且可以很好地处理与我们的 HTTP API 的通信。

同样，它几乎是开箱即用的，并且很容易与我们基于 Maven 的项目集成。该框架主要基于 JUnit，尽管它也可以与 TestNG 一起使用，这使得它几乎可以在所有环境中使用。

在下一节中，我们将了解需要将哪些依赖项添加到我们的应用程序中才能使用该框架。

### 2.1. Maven 依赖项

首先，让我们将 Jersey Test Framework 核心依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.glassfish.jersey.test-framework</groupId>
    <artifactId>jersey-test-framework-core</artifactId>
    <version>2.27</version>
    <scope>test</scope>
</dependency>
```

与往常一样，我们可以从[Maven Central](https://search.maven.org/classic/#search|ga|1|g%3A"org.glassfish.jersey.test-framework")获取最新版本。

几乎所有 Jersey 测试都使用事实上的 Grizzly 测试容器工厂，我们还需要添加它：

```xml
<dependency>
    <groupId>org.glassfish.jersey.test-framework.providers</groupId>
    <artifactId>jersey-test-framework-provider-grizzly2</artifactId>
    <version>2.27</version>
    <scope>test</scope>
</dependency>
```

[同样，我们可以在Maven Central](https://search.maven.org/classic/#search|ga|1|jersey-test-framework-provider-grizzly2)中找到最新版本。

## 3. 开始

在下一节中，我们将介绍编写简单测试所需的基本步骤。

我们将从在我们的服务器上测试简单的Greetings资源开始：

```java
@Path("/greetings")
public class Greetings {

    @GET
    @Path("/hi")
    public String getHiGreeting() {
        return "hi";
    }
}

```

### 3.1. 配置测试

现在让我们定义我们的测试类：

```java
public class GreetingsResourceIntegrationTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(Greetings.class);
    }
    //...
}

```

我们可以在上面的示例中看到，要使用 Jersey 测试框架开发测试，我们的测试需要子类化 JerseyTest。

接下来，我们重写configure 方法，该方法为我们的测试返回一个自定义资源配置，并且只包含Greetings 资源。当然，这是我们希望测试的资源。

### 3.2. 编写我们的第一个测试

让我们从我们的问候语 API 测试一个简单的 GET 请求开始：

```java
@Test
public void givenGetHiGreeting_whenCorrectRequest_thenResponseIsOkAndContainsHi() {
    Response response = target("/greetings/hi").request()
        .get();

    assertEquals("Http Response should be 200: ", Status.OK.getStatusCode(), response.getStatus());
    assertEquals("Http Content-Type should be: ", MediaType.TEXT_HTML, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

    String content = response.readEntity(String.class);
    assertEquals("Content of ressponse is: ", "hi", content);
}

```

请注意，我们可以完全访问 HTTP 响应——因此我们可以执行检查状态代码等操作以确保操作确实成功，或者使用响应的实际主体。

让我们更详细地解释一下我们在上面的例子中做了什么：

1.  向“/greetings/hi”发送 HTTP GET 请求
2.  检查 HTTP 状态代码和内容类型响应标头
3.  测试响应内容是否包含字符串“hi”

## 4. 测试 GET 以检索资源

现在，我们已经了解了创建测试所涉及的基本步骤。让我们测试我们在优秀的[Jersey MVC 支持文章](https://www.baeldung.com/jersey-mvc)中介绍的简单 Fruit API 。

### 4.1. 获取纯 JSON

在下面的示例中，我们将响应主体作为标准 JSON 字符串处理：

```java
@Test
public void givenFruitExists_whenSearching_thenResponseContainsFruit() {
    final String json = target("fruit/search/strawberry").request()
        .get(String.class);
    assertThat(json, containsString("{"name":"strawberry","weight":20}"));
}
```

### 4.2. 获取实体而不是 JSON

我们还可以将响应直接映射到资源实体类——例如：

```java
   @Test
    public void givenFruitExists_whenSearching_thenResponseContainsFruitEntity() {
        final Fruit entity = target("fruit/search/strawberry").request()
            .get(Fruit.class);

        assertEquals("Fruit name: ", "strawberry", entity.getName());
        assertEquals("Fruit weight: ", Integer.valueOf(20), entity.getWeight());
    }
```

这一次，我们在get方法中指定响应实体将转换为的Java类型——一个Fruit对象。

## 5.测试POST创建资源

为了在我们的 API 中创建一个新资源——我们将充分利用 POST 请求。在下一节中，我们将了解如何测试 API 的这一部分。

### 5.1. 发布纯 JSON

让我们首先发布一个普通的 JSON 字符串来测试新水果资源的创建：

```java
@Test
public void givenCreateFruit_whenJsonIsCorrect_thenResponseCodeIsCreated() {
    Response response = target("fruit/created").request()
        .post(Entity.json("{"name":"strawberry","weight":20}"));

    assertEquals("Http Response should be 201 ", Status.CREATED.getStatusCode(), response.getStatus());
    assertThat(response.readEntity(String.class), containsString("Fruit saved : Fruit [name: strawberry colour: null]"));
}
```

在上面的示例中，我们使用了带有Entity对象参数的post方法。我们使用方便的json 方法从相应的 JSON 字符串创建实体。

### 5.2. 发布实体而不是 JSON

正如我们已经看到的 get 请求，我们也可以直接发布一个 Resource 实体类——例如：

```java
@Test
public void givenCreateFruit_whenFruitIsInvalid_thenResponseCodeIsBadRequest() {
    Fruit fruit = new Fruit("Blueberry", "purple");
    fruit.setWeight(1);

    Response response = target("fruit/create").request(MediaType.APPLICATION_JSON_TYPE)
        .post(Entity.entity(fruit, MediaType.APPLICATION_JSON_TYPE));

    assertEquals("Http Response should be 400 ", 400, response.getStatus());
    assertThat(response.readEntity(String.class), containsString("Fruit weight must be 10 or greater"));
}
```

这次我们使用实体方法来发布我们的 Fruit 实体，并将媒体类型指定为 JSON。

### 5.3. 使用 POST 提交表单

在我们最后的帖子示例中，我们将看到如何通过帖子请求测试表单提交：

```java
@Test
public void givenCreateFruit_whenFormContainsNullParam_thenResponseCodeIsBadRequest() {
    Form form = new Form();
    form.param("name", "apple");
    form.param("colour", null);
    
    Response response = target("fruit/create").request(MediaType.APPLICATION_FORM_URLENCODED)
        .post(Entity.form(form));

    assertEquals("Http Response should be 400 ", 400, response.getStatus());
    assertThat(response.readEntity(String.class), containsString("Fruit colour must not be null"));
 }
```

同样，我们使用Entity类，但这次将包含许多参数的表单传递给我们的发布请求。

## 6. 测试其他 HTTP 动词

有时我们需要测试其他 HTTP 端点，例如 PUT 和 DELETE。这当然完全可以使用 Jersey 测试框架。

让我们看一个简单的 PUT 示例：

```java
@Test
public void givenUpdateFruit_whenFormContainsBadSerialParam_thenResponseCodeIsBadRequest() {
    Form form = new Form();
    form.param("serial", "2345-2345");

    Response response = target("fruit/update").request(MediaType.APPLICATION_FORM_URLENCODED)
        .put(Entity.form(form));

    assertEquals("Http Response should be 400 ", 400, response.getStatus());
    assertThat(response.readEntity(String.class), containsString("Fruit serial number is not valid"));
}
```

一旦我们调用了请求方法，我们就可以在当前请求对象上调用任何 HTTP 方法。

## 7. 附加功能

Jersey 测试框架包含许多额外的配置属性，可以帮助调试和测试。

在下一个示例中，我们将看到如何以编程方式启用具有给定名称的功能：

```java
public class FruitResourceIntegrationTest extends JerseyTest {

    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        //...
```

当我们创建和配置被测 Jersey 应用程序时。我们还可以启用其他属性。在这种情况下，我们启用两个日志记录属性——LOG_TRAFFIC和DUMP_ENTITY ，它们将在测试运行期间提供有用的额外日志记录和调试信息。

## 8. 支持的容器

正如我们已经提到的，使用 Jersey 测试框架编写测试时使用的事实上的容器是 Grizzly。但是，支持许多其他容器：

-   内存容器
-   来自 Oracle JDK 的 HttpServer
-   简单容器 (org.simpleframework.http
-   码头容器 (org.eclipse.jetty)

有关如何配置这些容器的更多信息，请参阅[此处](https://eclipse-ee4j.github.io/jersey.github.io/documentation/latest/test-framework.html#d0e17501)的文档。

## 9.总结

总而言之，在本教程中，我们探索了 Jersey 测试框架。首先，我们首先介绍了如何配置 Jersey 测试框架，然后我们了解了如何为一个非常简单的 API 编写测试。

在下一节中，我们了解了如何为各种 GET 和 POST API 端点编写测试。最后，我们查看了 Jersey 测试框架支持的一些附加功能和容器。
## 1. 概述

为了简化 REST Web 服务及其客户端在Java中的开发，JAX-RS API 的标准和可移植实现被设计出来，称为 Jersey。

Jersey 是一个用于开发 REST Web 服务的开源框架，它为JAX-RS API 提供支持并作为JAX-RS 参考实现。

在本教程中，我们将了解如何设置具有不同媒体类型的Jersey响应主体。

## 2.Maven依赖

首先，我们需要在pom.xml文件中包含以下依赖项：

```xml
<dependency>
    <groupId>org.glassfish.jersey.bundles</groupId>
    <artifactId>jaxrs-ri</artifactId>
    <version>2.26</version>
</dependency>
<dependency>
    <groupId>org.glassfish.jersey.core</groupId>
    <artifactId>jersey-server</artifactId>
    <version>2.26</version>
</dependency>
```

最新版本的JAX-RS可以在[jaxrs-ri](https://search.maven.org/search?q=g:org.glassfish.jersey.bundles AND a:jaxrs-ri&core=gav)找到， Jersey服务器可以在[jersey-server找到](https://search.maven.org/search?q=g:org.glassfish.jersey.core AND a:jersey-server&core=gav)

## 3. 泽西岛的回应

自然地，有不同的方法可以使用Jersey构建响应，我们将在下面研究如何构建它们。

这里的所有示例都是 HTTP GET 请求，我们将使用 curl命令来测试资源。

### 3.1. 好的文本响应

此处显示的端点是一个简单示例，说明如何将纯文本作为 Jersey 响应返回：

```java
@GET
@Path("/ok")
public Response getOkResponse() {

    String message = "This is a text response";

    return Response
      .status(Response.Status.OK)
      .entity(message)
      .build();
}
```

我们可以使用curl执行 HTTP GET来验证响应：

```bash
curl -XGET http://localhost:8080/jersey/response/ok
```

该端点将发回如下响应：

```plaintext
This is a text response
```

当未指定媒体类型时，Jersey 将默认为文本/纯文本。

### 3.2. 错误响应

错误也可以作为 Jersey 响应发回：

```java
@GET
@Path("/not_ok")
public Response getNOkTextResponse() {

    String message = "There was an internal server error";

    return Response
      .status(Response.Status.INTERNAL_SERVER_ERROR)
      .entity(message)
      .build();
}
```

要验证响应，我们可以使用curl执行 HTTP GET 请求 ：

```bash
curl -XGET http://localhost:8080/jersey/response/not_ok
```

错误消息将在响应中发回：

```plaintext
There was an internal server error
```

### 3.3. 纯文本响应

我们还可以返回 简单的纯文本响应：

```java
@GET
@Path("/text_plain")
public Response getTextResponseTypeDefined() {

    String message = "This is a plain text response";

    return Response
      .status(Response.Status.OK)
      .entity(message)
      .type(MediaType.TEXT_PLAIN)
      .build();
}
```

同样，我们可以使用curl执行 HTTP GET来验证响应：

```bash
curl -XGET http://localhost:8080/jersey/response/text_plain
```

响应如下：

```plaintext
This is a plain text response
```

同样的结果也可以通过 Produces 注解来实现，而不是在Response中使用type() 方法：

```java
@GET
@Path("/text_plain_annotation")
@Produces({ MediaType.TEXT_PLAIN })
public Response getTextResponseTypeAnnotated() {

    String message = "This is a plain text response via annotation";

    return Response
      .status(Response.Status.OK)
      .entity(message)
      .build();
}
```

我们可以使用curl进行响应验证：

```bash
curl -XGET http://localhost:8080/jersey/response/text_plain_annotation
```

这是回应：

```plaintext
This is a plain text response via annotation
```

### 3.4. 使用 POJO 的 JSON 响应

也可以使用简单的 普通旧Java对象 (POJO) 构建 Jersey 响应。

我们有一个非常简单的Person POJO，如下所示，我们将使用它来构建响应：

```java
public class Person {
    String name;
    String address;

    // standard constructor
    // standard getters and setters
}
```

Person POJO现在 可用于返回 JSON 作为 Response body：

```java
@GET
@Path("/pojo")
public Response getPojoResponse() {

    Person person = new Person("Abhinayak", "Nepal");

    return Response
      .status(Response.Status.OK)
      .entity(person)
      .build();
}
```

可以通过以下curl命令验证此 GET 端点的工作情况：

```bash
curl -XGET http://localhost:8080/jersey/response/pojo
```

Person POJO 将被转换为 JSON 并作为响应发回：

```java
{"address":"Nepal","name":"Abhinayak"}
```

### 3.5. 使用简单字符串的 JSON 响应

我们可以使用预先格式化的字符串来创建一个响应，而且可以很简单地完成。

以下端点是如何将表示为字符串的 JSON 作为 Jersey 响应中的 JSON 发回的示例：

```java
@GET
@Path("/json")
public Response getJsonResponse() {

    String message = "{"hello": "This is a JSON response"}";

    return Response
      .status(Response.Status.OK)
      .entity(message)
      .type(MediaType.APPLICATION_JSON)
      .build();
}
```

这可以通过使用curl执行 HTTP GET来验证响应来验证：

```bash
curl -XGET http://localhost:8080/jersey/response/json
```

调用此资源将返回一个 JSON：

```java
{"hello":"This is a JSON response"}
```

相同的模式适用于其他常见的媒体类型，如 XML 或 HTML。我们只需要使用MediaType.TEXT_XML或MediaType.TEXT_HTML通知 Jersey 它是 XML 或 HTML  ，Jersey 将处理其余部分。


## 4. 总结

在这篇简短的文章中，我们为各种媒体类型构建了 Jersey (JAX-RS) 响应。
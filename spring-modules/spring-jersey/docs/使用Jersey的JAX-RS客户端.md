## 1. 概述

Jersey 是一个用于开发 RESTFul Web 服务的开源框架。它还具有强大的内置客户端功能。

在本快速教程中，我们将探索使用[Jersey 2](https://jersey.java.net/)创建 JAX-RS 客户端。

有关使用 Jersey 创建 RESTful Web 服务的讨论，请参阅[本文](https://www.baeldung.com/jersey-rest-api-with-spring)。

## 延伸阅读：

## [带有 Jersey 和 Spring 的 REST API](https://www.baeldung.com/jersey-rest-api-with-spring)

使用 Jersey 2 和 Spring 构建 Restful Web 服务。

[阅读更多](https://www.baeldung.com/jersey-rest-api-with-spring)→

## [JAX-RS 中的 CORS](https://www.baeldung.com/cors-in-jax-rs)

了解如何在基于 JAX-RS 的应用程序中实现跨源资源共享 (CORS) 机制。

[阅读更多](https://www.baeldung.com/cors-in-jax-rs)→

## [泽西岛过滤器和拦截器](https://www.baeldung.com/jersey-filters-interceptors)

看看过滤器和拦截器如何在 Jersey 框架中工作。

[阅读更多](https://www.baeldung.com/jersey-filters-interceptors)→

## 2.Maven依赖

让我们首先在pom.xml中添加所需的依赖项(对于 Jersey JAX-RS 客户端)：

```xml
<dependency>
    <groupId>org.glassfish.jersey.core</groupId>
    <artifactId>jersey-client</artifactId>
    <version>2.25.1</version>
</dependency>
```

使用 Jackson 2.x 作为 JSON 提供者：

```xml
<dependency>
    <groupId>org.glassfish.jersey.media</groupId>
    <artifactId>jersey-media-json-jackson</artifactId>
    <version>2.25.1</version>
</dependency>
```

这些依赖项的最新版本可以在[jersey-client](https://search.maven.org/classic/#search|gav|1|g%3A"org.glassfish.jersey.core" AND a%3A"jersey-client")和[jersey-media-json-jackson](https://search.maven.org/classic/#search|gav|1|g%3A"org.glassfish.jersey.media" AND a%3A"jersey-media-json-jackson")找到。

## 3. 泽西岛的 RESTFul 客户端

我们将开发一个 JAX-RS 客户端来使用我们[在这里](https://www.baeldung.com/jersey-rest-api-with-spring)开发的 JSON 和 XML REST API (我们需要确保服务已部署且 URL 可访问)。

### 3.1. 资源表示类

让我们看一下资源表示类：

```java
@XmlRootElement
public class Employee {
    private int id;
    private String firstName;

    // standard getters and setters
}
```

仅当需要 XML 支持时才需要像@XmlRootElement这样的JAXB 注解。

### 3.2. 创建客户端实例

我们首先需要的是一个Client实例：

```java
Client client = ClientBuilder.newClient();
```

### 3.3. 创建WebTarget

一旦我们有了Client实例，我们就可以使用目标 Web 资源的 URI创建一个WebTarget ：

```java
WebTarget webTarget 
  = client.target("http://localhost:8082/spring-jersey");
```

使用WebTarget，我们可以定义特定资源的路径：

```java
WebTarget employeeWebTarget 
  = webTarget.path("resources/employees");
```

### 3.4. 构建 HTTP 请求调用

调用构建器实例是通过WebTarget.request()方法之一创建的：

```java
Invocation.Builder invocationBuilder 
  = employeeWebTarget.request(MediaType.APPLICATION_JSON);
```

对于 XML 格式，可以使用MediaType.APPLICATION_XML 。

### 3.5. 调用 HTTP 请求

调用 HTTP GET：

```java
Response response 
  = invocationBuilder.get(Employee.class);
```

调用 HTTP POST：

```java
Response response 
  = invocationBuilder
  .post(Entity.entity(employee, MediaType.APPLICATION_JSON);
```

### 3.6. 示例 REST 客户端

让我们开始编写一个简单的 REST 客户端。getJsonEmployee ()方法根据员工ID检索Employee对象。[REST Web 服务](https://www.baeldung.com/jersey-rest-api-with-spring)返回的 JSON在返回之前被反序列化为Employee对象。

流畅地使用 JAX-RS API 创建 Web 目标、调用构建器和调用 GET HTTP 请求：

```java
public class RestClient {
 
    private static final String REST_URI 
      = "http://localhost:8082/spring-jersey/resources/employees";
 
    private Client client = ClientBuilder.newClient();

    public Employee getJsonEmployee(int id) {
        return client
          .target(REST_URI)
          .path(String.valueOf(id))
          .request(MediaType.APPLICATION_JSON)
          .get(Employee.class);
    }
    //...
}
```

现在让我们为 POST HTTP 请求添加一个方法。createJsonEmployee ()方法通过调用用于创建员工的[REST Web 服务](https://www.baeldung.com/jersey-rest-api-with-spring)来创建员工。在调用 HTTP POST 方法之前，客户端 API 在内部将Employee对象序列化为 JSON：

```java
public Response createJsonEmployee(Employee emp) {
    return client
      .target(REST_URI)
      .request(MediaType.APPLICATION_JSON)
      .post(Entity.entity(emp, MediaType.APPLICATION_JSON));
}
```

### 4.测试客户端

让我们用 JUnit 测试我们的客户端：

```java
public class JerseyClientLiveTest {
 
    public static final int HTTP_CREATED = 201;
    private RestClient client = new RestClient();

    @Test
    public void givenCorrectObject_whenCorrectJsonRequest_thenResponseCodeCreated() {
        Employee emp = new Employee(6, "Johny");

        Response response = client.createJsonEmployee(emp);

        assertEquals(response.getStatus(), HTTP_CREATED);
    }
}
```

## 5.总结

在本文中，我们介绍了使用 Jersey 2 的 JAX-RS 客户端，并开发了一个简单的 RESTFulJava客户端。
## 1. 概述

Jersey是一个用于开发RESTFul Web服务的开源框架。它还具有强大的内置客户端功能。

在本快速教程中，我们将探讨如何使用[Jersey 2](https://jersey.java.net/)创建JAX-RS客户端。

有关使用Jersey创建RESTful Web服务的讨论，请参阅[本文](https://www.baeldung.com/jersey-rest-api-with-spring)。

### 延伸阅读

### [使用Jersey和Spring的REST API](https://www.baeldung.com/jersey-rest-api-with-spring)

使用Jersey 2和Spring构建RESTful Web服务。

[阅读更多](https://www.baeldung.com/jersey-rest-api-with-spring)→

### [JAX-RS中的CORS](https://www.baeldung.com/cors-in-jax-rs)

了解如何在基于JAX-RS的应用程序中实现跨源资源共享(CORS)机制。

[阅读更多](https://www.baeldung.com/cors-in-jax-rs)→

### [Jersey过滤器和拦截器](https://www.baeldung.com/jersey-filters-interceptors)

看看过滤器和拦截器如何在Jersey框架中工作。

[阅读更多](https://www.baeldung.com/jersey-filters-interceptors)→

## 2. Maven依赖

让我们首先在pom.xml中添加所需的依赖项(对于Jersey JAX-RS客户端)：

```xml
<dependency>
    <groupId>org.glassfish.jersey.core</groupId>
    <artifactId>jersey-client</artifactId>
    <version>2.25.1</version>
</dependency>
```

使用Jackson 2.x作为JSON提供者：

```xml
<dependency>
    <groupId>org.glassfish.jersey.media</groupId>
    <artifactId>jersey-media-json-jackson</artifactId>
    <version>2.25.1</version>
</dependency>
```

这些依赖项的最新版本可以在[jersey-client](https://central.sonatype.com/artifact/org.glassfish.jersey.core/jersey-client/3.1.1)和[jersey-media-json-jackson](https://central.sonatype.com/artifact/org.glassfish.jersey.media/jersey-media-json-jackson/3.1.1)找到。

## 3. Jersey的RESTFul客户端

我们将开发一个JAX-RS客户端来使用我们在[这里](https://www.baeldung.com/jersey-rest-api-with-spring)开发的JSON和XML REST API(我们需要确保服务已部署并且URL可访问)。

### 3.1 资源表示类

让我们看一下资源表示类：

```java
@XmlRootElement
public class Employee {
    private int id;
    private String firstName;

    // standard getters and setters
}
```

仅当需要XML支持时才需要像@XmlRootElement这样的JAXB注解。

### 3.2 创建Client实例

我们首先需要的是一个Client实例：

```java
Client client = ClientBuilder.newClient();
```

### 3.3 创建WebTarget

一旦我们有了Client实例，我们就可以使用目标Web资源的URI创建一个WebTarget：

```java
WebTarget webTarget = client.target("http://localhost:8082/spring-jersey");
```

使用WebTarget，我们可以定义特定资源的路径：

```java
WebTarget employeeWebTarget = webTarget.path("resources/employees");
```

### 3.4 构建HTTP请求调用

调用构建器实例是通过WebTarget.request()方法之一创建的：

```java
Invocation.Builder invocationBuilder = employeeWebTarget.request(MediaType.APPLICATION_JSON);
```

对于XML格式，可以使用MediaType.APPLICATION_XML。

### 3.5 调用HTTP请求

调用HTTP GET：

```java
Response response = invocationBuilder.get(Employee.class);
```

调用HTTP POST：

```java
Response response = invocationBuilder
    .post(Entity.entity(employee, MediaType.APPLICATION_JSON);
```

### 3.6 示例REST客户端

让我们开始编写一个简单的REST客户端。getJsonEmployee()方法根据员工ID检索Employee对象。[REST Web Service](https://www.baeldung.com/jersey-rest-api-with-spring)返回的JSON在返回之前被反序列化为Employee对象。

流式地使用JAX-RS API创建WebTarget、调用构建器和调用GET HTTP请求：

```java
public class RestClient {

    private static final String REST_URI = "http://localhost:8082/spring-jersey/resources/employees";

    private Client client = ClientBuilder.newClient();

    public Employee getJsonEmployee(int id) {
        return client
              .target(REST_URI)
              .path(String.valueOf(id))
              .request(MediaType.APPLICATION_JSON)
              .get(Employee.class);
    }
    // ...
}
```

现在让我们为POST HTTP请求添加一个方法。createJsonEmployee()方法通过调用用于创建员工的[REST Web Service](https://www.baeldung.com/jersey-rest-api-with-spring)来创建员工。在调用HTTP POST方法之前，客户端API在内部将Employee对象序列化为JSON：

```java
public Response createJsonEmployee(Employee emp) {
    return client
        .target(REST_URI)
        .request(MediaType.APPLICATION_JSON)
        .post(Entity.entity(emp, MediaType.APPLICATION_JSON));
}
```

## 4. 测试客户端

让我们用JUnit测试我们的客户端：

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

## 5. 总结

在本文中，我们介绍了使用Jersey 2的JAX-RS客户端，并开发了一个简单的RESTFul Java客户端。
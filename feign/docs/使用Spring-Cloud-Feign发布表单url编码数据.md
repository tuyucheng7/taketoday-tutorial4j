## 一、概述

在本教程中，我们将学习如何使用[Feign](https://www.baeldung.com/intro-to-feign)客户端在请求正文中使用*表单 url 编码*数据发出 POST API 请求。

## *2. POST form-url-encoded*数据的方法

我们可以通过两种不同的方式制作 POST*表单 url 编码*数据。我们首先需要创建一个自定义编码器并为我们的 Feign 客户端配置它：

```java
class FormFeignEncoderConfig {
    @Bean
    public Encoder encoder(ObjectFactory<HttpMessageConverters> converters) {
        return new SpringFormEncoder(new SpringEncoder(converters));
    }
}复制
```

我们将在我们的 Feign 客户端配置中使用这个自定义类：

```java
@FeignClient(name = "form-client", url = "http://localhost:8085/api",
  configuration = FormFeignEncoderConfig.class)
public interface FormClient {
    // request methods
}复制
```

我们现在完成了 Feign 和 bean 配置。现在让我们看看请求方法。

### 2.1. 使用 POJO

我们将创建一个 Java POJO 类，其中包含所有表单参数作为成员：

```java
public class FormData {
    int id;
    String name;
    // constructors, getters and setters
}复制
```

我们将在 POST 请求中将此对象作为请求主体传递。

```java
@PostMapping(value = "/form", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
void postFormData(@RequestBody FormData data);复制
```

让我们验证我们的代码；请求正文应将*id*和*名称*作为*form-url-encoded*数据：

```java
@Test
public void givenFormData_whenPostFormDataCalled_thenReturnSuccess() {
    FormData formData = new FormData(1, "baeldung");
    stubFor(WireMock.post(urlEqualTo("/api/form"))
      .willReturn(aResponse().withStatus(HttpStatus.OK.value())));

    formClient.postFormData(formData);
    wireMockServer.verify(postRequestedFor(urlPathEqualTo("/api/form"))
      .withHeader("Content-Type", equalTo("application/x-www-form-urlencoded; charset=UTF-8"))
      .withRequestBody(equalTo("name=baeldung&id=1")));
}复制
```

### 2.2. 使用地图

我们还可以使用[*Map*](https://www.baeldung.com/java-hashmap)而不是 POJO 类来发送POST 请求正文中的*表单 url 编码数据。*

```java
@PostMapping(value = "/form/map", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
void postFormMapData(Map<String, ?> data);复制
```

**请注意，Map 的值应为“?”。**

让我们验证我们的代码：

```java
@Test
public void givenFormMap_whenPostFormMapDataCalled_thenReturnSuccess() {
    Map<String, String> mapData = new HashMap<>();
    mapData.put("name", "baeldung");
    mapData.put("id", "1");
    stubFor(WireMock.post(urlEqualTo("/api/form/map"))
      .willReturn(aResponse().withStatus(HttpStatus.OK.value())));

    formClient.postFormMapData(mapData);
    wireMockServer.verify(postRequestedFor(urlPathEqualTo("/api/form/map"))
      .withHeader("Content-Type", equalTo("application/x-www-form-urlencoded; charset=UTF-8"))
      .withRequestBody(equalTo("name=baeldung&id=1")));
}复制
```

## 3.结论

在本文中，我们了解了如何在请求正文中使用*表单 url 编码数据发出 POST API 请求。*
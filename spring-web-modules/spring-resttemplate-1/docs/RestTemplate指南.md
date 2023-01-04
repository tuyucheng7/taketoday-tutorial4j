## 1. 概述

在本教程中，我们将说明可以使用并很好地使用Spring REST客户端RestTemplate的广泛操作。

对于所有示例的API端，我们将从[此处](https://github.com/eugenp/tutorials/tree/master/spring-web-modules/spring-resttemplate)运行RESTful服务。

## 2. 弃用通知

从Spring Framework 5开始，除了WebFlux堆栈，Spring 还引入了一个名为[WebClient](https://www.baeldung.com/spring-5-webclient)的新HTTP客户端。

WebClient是[RestTemplate](https://www.baeldung.com/spring-webclient-resttemplate)的现代替代HTTP客户端。它不仅提供传统的同步API，而且还支持高效的非阻塞和异步方法。

也就是说，如果我们正在开发新应用程序或迁移旧应用程序，使用WebClient是个好主意。展望未来，RestTemplate将在未来的版本中被弃用。

## 3. 使用GET获取资源

### 3.1 获取纯JSON

让我们从简单开始并讨论GET请求，使用getForEntity() API的快速示例：

```java
RestTemplate restTemplate = new RestTemplate();
String fooResourceUrl = "http://localhost:8080/spring-rest/foos";
ResponseEntity<String> response = restTemplate.getForEntity(fooResourceUrl + "/1", String.class);
Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);

```

请注意，我们可以完全访问HTTP响应，因此我们可以执行诸如检查状态代码以确保操作成功或使用响应的实际主体之类的操作：

```java
ObjectMapper mapper = new ObjectMapper();
JsonNode root = mapper.readTree(response.getBody());
JsonNode name = root.path("name");
Assertions.assertNotNull(name.asText());
```

我们在这里将响应主体作为标准字符串使用，并使用Jackson(以及Jackson提供的JSON节点结构)来验证一些细节。

### 3.2 检索POJO而不是JSON

我们还可以将响应直接映射到资源DTO：

```java
public class Foo implements Serializable {
    private long id;

    private String name;
    // standard getters and setters
}
```

现在我们可以简单地在模板中使用getForObject API：

```java
Foo foo = restTemplate.getForObject(fooResourceUrl + "/1", Foo.class);
Assertions.assertNotNull(foo.getName());
Assertions.assertEquals(foo.getId(), 1L);
```

## 4. 使用HEAD检索标头

在继续使用更常用的方法之前，让我们快速了解一下HEAD的使用。

我们将在这里使用headForHeaders() API：

```java
HttpHeaders httpHeaders = restTemplate.headForHeaders(fooResourceUrl);
Assertions.assertTrue(httpHeaders.getContentType().includes(MediaType.APPLICATION_JSON));
```

## 5. 使用POST创建资源

为了在API中创建新资源，我们可以充分利用postForLocation()、postForObject()或postForEntity() API。

第一个返回新创建的资源的URI，而第二个返回资源本身。

### 5.1 postForObject() API 

```java
RestTemplate restTemplate = new RestTemplate();

HttpEntity<Foo> request = new HttpEntity<>(new Foo("bar"));
Foo foo = restTemplate.postForObject(fooResourceUrl, request, Foo.class);
Assertions.assertNotNull(foo);
Assertions.assertEquals(foo.getName(), "bar");
```

### 5.2 postForLocation() API

同样，让我们看一下不返回完整资源而只返回新创建资源的位置的操作：

```java
HttpEntity<Foo> request = new HttpEntity<>(new Foo("bar"));
URI location = restTemplate.postForLocation(fooResourceUrl, request);
Assertions.assertNotNull(location);
```

### 5.3 exchange()API 

让我们看看如何使用更通用的交换API执行POST：

```java
RestTemplate restTemplate = new RestTemplate();
HttpEntity<Foo> request = new HttpEntity<>(new Foo("bar"));
ResponseEntity<Foo> response = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, request, Foo.class);
 
Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
 
Foo foo = response.getBody();
 
Assertions.assertNotNull(foo);
Assertions.assertEquals(foo.getName(), "bar");
```

### 5.4 提交表单数据

接下来，让我们看看如何使用POST方法提交表单。

首先，我们需要将Content-Type标头设置为application/x-www-form-urlencoded。

这确保可以将大查询字符串发送到服务器，其中包含由&分隔的名称/值对：

```java
HttpHeaders headers = new HttpHeaders();
headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
```

我们可以将表单变量包装到[LinkedMultiValueMap](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/util/LinkedMultiValueMap.html)中：

```java
MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
map.add("id", "1");
```

接下来，我们使用[HttpEntity](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/HttpEntity.html)实例构建请求：

```java
HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
```

最后，我们可以通过在端点/foos/form上调用[restTemplate.postForEntity()](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html#postForEntity-java.net.URI-java.lang.Object-java.lang.Class-)来连接到REST服务

```java
ResponseEntity<String> response = restTemplate.postForEntity(fooResourceUrl+"/form", request , String.class);
Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
```

## 6. 使用OPTIONS获取允许的操作

接下来，我们将快速浏览一下使用OPTIONS请求并探索使用这种请求对特定URI允许的操作；API是optionsForAllow：

```java
Set<HttpMethod> optionsForAllow = restTemplate.optionsForAllow(fooResourceUrl);
HttpMethod[] supportedMethods = {HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE};
Assertions.assertTrue(optionsForAllow.containsAll(Arrays.asList(supportedMethods)));
```

## 7. 使用PUT更新资源

接下来，我们将开始查看PUT，更具体地说是此操作的exchange() API，因为template.put API非常简单。

### 7.1 带exchange()的简单PUT

我们将从针对API的简单PUT操作开始——请记住，该操作不会将正文返回给客户端：

```java
Foo updatedInstance = new Foo("newName");
updatedInstance.setId(createResponse.getBody().getId());
String resourceUrl = fooResourceUrl + '/' + createResponse.getBody().getId();
HttpEntity<Foo> requestUpdate = new HttpEntity<>(updatedInstance, headers);
template.exchange(resourceUrl, HttpMethod.PUT, requestUpdate, Void.class);
```

### 7.2 PUT与exchange()和请求回调

接下来，我们将使用请求回调来发出PUT。

让我们确保准备好回调，我们可以在其中设置我们需要的所有标头以及请求正文：

```java
RequestCallback requestCallback(final Foo updatedInstance) {
    return clientHttpRequest -> {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(clientHttpRequest.getBody(), updatedInstance);
        clientHttpRequest.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        clientHttpRequest.getHeaders().add(HttpHeaders.AUTHORIZATION, "Basic " + getBase64EncodedLogPass());
    };
}
```

接下来，我们使用POST请求创建资源：

```java
ResponseEntity<Foo> response = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, request, Foo.class);
Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
```

然后我们更新资源：

```java
Foo updatedInstance = new Foo("newName");
updatedInstance.setId(response.getBody().getId());
String resourceUrl =fooResourceUrl + '/' + response.getBody().getId();
restTemplate.execute(
    resourceUrl, 
    HttpMethod.PUT, 
    requestCallback(updatedInstance), 
    clientHttpResponse -> null);
```

## 8. 使用DELETE删除资源

要删除现有资源，我们将快速使用delete() API：

```java
String entityUrl = fooResourceUrl + "/" + existingResource.getId();
restTemplate.delete(entityUrl);
```

## 9. 配置超时

我们可以通过简单地使用ClientHttpRequestFactory将RestTemplate配置为超时：

```java
RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());

private ClientHttpRequestFactory getClientHttpRequestFactory() {
    int timeout = 5000;
    HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
    clientHttpRequestFactory.setConnectTimeout(timeout);
    return clientHttpRequestFactory;
}
```

我们可以使用HttpClient进行进一步的配置选项：

```java
private ClientHttpRequestFactory getClientHttpRequestFactory() {
    int timeout = 5000;
    RequestConfig config = RequestConfig.custom()
        .setConnectTimeout(timeout)
        .setConnectionRequestTimeout(timeout)
        .setSocketTimeout(timeout)
        .build();
    CloseableHttpClient client = HttpClientBuilder
        .create()
        .setDefaultRequestConfig(config)
        .build();
    return new HttpComponentsClientHttpRequestFactory(client);
}
```

## 10. 总结

在本文中，我们回顾了主要的HTTP动词，使用RestTemplate来编排使用所有这些的请求。

如果你想深入了解如何使用模板进行身份验证，请查看我们关于[使用RestTemplate进行基本身份验证](https://www.baeldung.com/how-to-use-resttemplate-with-basic-authentication-in-spring)的文章。
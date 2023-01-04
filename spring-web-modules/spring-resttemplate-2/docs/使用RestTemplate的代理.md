## 1. 概述

在这个简短的教程中，我们将了解如何使用[RestTemplate](https://www.baeldung.com/rest-template)[向代理](https://www.baeldung.com/java-connect-via-proxy-server)发送请求。

## 2. 依赖关系

首先，RestTemplateCustomizer使用HttpClient类连接到代理。

要使用该类，我们需要将[Apache的httpcore](https://search.maven.org/search?q=a:httpcore)依赖项添加到我们的Maven pom.xml文件中：

```xml
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpcore</artifactId>
    <version>4.4.13</version>
</dependency>
```

或者到我们的Gradle build.gradle文件：

```groovy
compile 'org.apache.httpcomponents:httpcore:4.4.13'
```

## 3. 使用SimpleClientHttpRequestFactory

使用RestTemplate向代理发送请求非常简单。我们需要做的就是在构建RestTemplate对象之前从SimpleClientHttpRequestFactory调用[setProxy(java.net.Proxy)](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/client/SimpleClientHttpRequestFactory.html#setProxy-java.net.Proxy-)。

首先，我们从配置SimpleClientHttpRequestFactory开始：

```java
Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress(PROXY_SERVER_HOST, PROXY_SERVER_PORT));
SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
requestFactory.setProxy(proxy);
```

然后，我们继续将请求工厂实例传递给RestTemplate构造函数：

```java
RestTemplate restTemplate = new RestTemplate(requestFactory);
```

最后，一旦我们构建了RestTemplate，我们就可以使用它来发出代理请求：

```java
ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://httpbin.org/get", String.class);

assertThat(responseEntity.getStatusCode(), is(equalTo(HttpStatus.OK)));
```

## 4. 使用RestTemplateCustomizer

另一种方法是将RestTemplateCustomizer与RestTemplateBuilder结合使用来构建自定义的RestTemplate。

让我们开始定义一个ProxyCustomizer：

```java
class ProxyCustomizer implements RestTemplateCustomizer {

    @Override
    public void customize(RestTemplate restTemplate) {
        HttpHost proxy = new HttpHost(PROXY_SERVER_HOST, PROXY_SERVER_PORT);
        HttpClient httpClient = HttpClientBuilder.create()
              .setRoutePlanner(new DefaultProxyRoutePlanner(proxy) {
                  @Override
                  public HttpHost determineProxy(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
                      return super.determineProxy(target, request, context);
                  }
              })
              .build();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
    }
}
```

之后，我们构建自定义的RestTemplate：

```java
RestTemplate restTemplate = new RestTemplateBuilder(new ProxyCustomizer()).build();
```

最后，我们使用RestTemplate发出首先通过代理传递的请求：

```java
ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://httpbin.org/get", String.class);
assertThat(responseEntity.getStatusCode(), is(equalTo(HttpStatus.OK)));
```

## 5. 总结

在这个简短的教程中，我们探索了两种使用RestTemplate向代理发送请求的不同方法。

首先，我们了解了如何通过使用SimpleClientHttpRequestFactory构建的RestTemplate发送请求。然后我们学习了如何使用RestTemplateCustomizer来做同样的事情，根据[文档](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto-http-clients-proxy-configuration)，这是推荐的方法。
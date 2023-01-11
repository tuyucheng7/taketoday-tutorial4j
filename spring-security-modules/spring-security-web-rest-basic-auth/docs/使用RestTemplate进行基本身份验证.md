## 1. 概述

在本教程中，我们介绍如何使用Spring的RestTemplate来**使用由基本身份验证保护的RESTful服务**。

一旦我们为RestTemplate设置了基本身份验证，每个请求都将被抢先发送，其中包含执行身份验证过程所需的完整凭据。凭据将根据基本身份验证方案的规范进行编码，并使用Authorization HTTP头，如下所示：

```bash
Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==
```

## 2.设置RestTemplate

我们可以简单地通过为RestTemplate声明一个bean来将RestTemplate引导到Spring上下文中；但是，使用基本身份验证设置RestTemplate需要手动干预，因此我们将使用Spring FactoryBean来实现更大的灵活性，而不是直接声明bean。这个FactoryBean将在初始化时创建和配置RestTemplate：

```java
@Component
public class RestTemplateFactory implements FactoryBean<RestTemplate>, InitializingBean {
	private RestTemplate restTemplate;

	public RestTemplateFactory() {
		super();
	}

	@Override
	public RestTemplate getObject() {
		return restTemplate;
	}

	@Override
	public Class<RestTemplate> getObjectType() {
		return RestTemplate.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void afterPropertiesSet() {
		HttpHost host = new HttpHost("localhost", 8082, "http");
		final ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactoryBasicAuth(host);
	}
}
```

主机和端口值应取决于环境，允许客户端灵活地定义一组值用于集成测试，另一组值用于生产使用。这些值可以由Spring对属性文件的第一类支持来管理。

## 3. Authorization HTTP Header的手动管理

为基本身份验证创建Authorization标头对我们来说相当简单，因此我们可以通过几行代码手动完成：

```java
HttpHeaders createHeaders(String username, String password){
   return new HttpHeaders() {{
         String auth = username + ":" + password;
         byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")) );
         String authHeader = "Basic " + new String( encodedAuth );
         set( "Authorization", authHeader );
      }};
}
```

此外，发送请求同样简单：

```java
restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<T>(createHeaders(username, password)), clazz);
```

## 4. Authorization HTTP Header的自动管理

Spring 3.0和3.1，以及现在的4.x，对Apache HTTP库有很好的支持：

-   在Spring 3.0中，CommonsClientHttpRequestFactory与现在已经过时的HttpClient 3.x集成。
-   Spring 3.1通过HttpComponentsClientHttpRequestFactory引入了对当前HttpClient 4.x的支持(在JIRA[SPR-6180](https://jira.springsource.org/browse/SPR-6180)中添加了支持)。
-   Spring 4.0通过HttpComponentsAsyncClientHttpRequestFactory引入了异步支持。

让我们开始使用HttpClient 4和Spring 4进行设置。

RestTemplate需要一个支持基本身份验证的HTTP请求工厂，然而，直接使用现有的HttpComponentsClientHttpRequestFactory将被证明是困难的，因为RestTemplate的体系结构在设计时没有对HttpContext提供良好的支持，而HttpContext是难题的一个重要组成部分。因此，我们需要继承HttpComponentsClientHttpRequestFactory并重写createHttpContext方法：

```java
public class HttpComponentsClientHttpRequestFactoryBasicAuth extends HttpComponentsClientHttpRequestFactory {

	HttpHost host;

	public HttpComponentsClientHttpRequestFactoryBasicAuth(HttpHost host) {
		super();
		this.host = host;
	}

	protected HttpContext createHttpContext(HttpMethod httpMethod, URI uri) {
		return createHttpContext();
	}

	private HttpContext createHttpContext() {

		AuthCache authCache = new BasicAuthCache();

		BasicScheme basicAuth = new BasicScheme();
		authCache.put(host, basicAuth);

		BasicHttpContext localcontext = new BasicHttpContext();
		localcontext.setAttribute(HttpClientContext.AUTH_CACHE, authCache);
		return localcontext;
	}
}
```

在HttpContext的创建中，我们在这里构建了基本的身份验证支持。正如我们所见，使用 HttpClient 4.x进行抢占式基本身份验证对我们来说有点负担。身份验证信息是缓存的，我们设置这个认证身份验证是非常手动和不直观的。

现在一切就绪，RestTemplate只需添加一个BasicAuthorizationInterceptor即可支持基本身份验证方案：

```java
restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor("username", "password"));
```

然后请求：

```java
restTemplate.exchange("http://localhost:8082/spring-security-rest-basic-auth/api/foos/1", HttpMethod.GET, null, Foo.class);
```

## 5. Maven依赖

对于RestTemplate本身和HttpClient库，我们需要以下Maven依赖项：

```xml
<dependency>
   <groupId>org.springframework</groupId>
   <artifactId>spring-webmvc</artifactId>
   <version>5.3.13</version>
</dependency>

<dependency>
   <groupId>org.apache.httpcomponents</groupId>
   <artifactId>httpclient</artifactId>
   <version>4.5.13</version>
</dependency>
```

或者，如果我们手动构建HTTP Authorization标头，那么我们需要一个额外的库来支持编码：

```xml
<dependency>
   <groupId>commons-codec</groupId>
   <artifactId>commons-codec</artifactId>
   <version>1.10</version>
</dependency>
```

## 6. 总结

在RestTemplate和安全性上可以找到的许多信息仍然不能解释当前的HttpClient 4.x版本，即使3.x分支已经过时，Spring对该版本的支持也被完全弃用。在本文中，我们试图通过详细的、逐步的讨论来改变这一点，讨论如何使用RestTemplate设置基本身份验证并使用它来使用安全的REST API。

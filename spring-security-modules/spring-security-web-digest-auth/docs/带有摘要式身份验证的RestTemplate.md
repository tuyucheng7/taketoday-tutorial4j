## 1. 概述

本文将展示如何配置 Spring RestTemplate以使用受 Digest Authentication 保护的服务。

与基本身份验证类似，一旦在模板中设置了摘要身份验证，客户端将能够通过必要的安全步骤并获取授权标头所需的信息：

```bash
Authorization: Digest 
    username="user1",
    realm="Custom Realm Name",
    nonce="MTM3NTYwOTA5NjU3OTo5YmIyMjgwNTFlMjdhMTA1MWM3OTMyMWYyNDY2MGFlZA==",
    uri="/spring-security-rest-digest-auth/api/foos/1", 
    ....
```

有了这些数据，服务器就可以正确地验证请求并返回 200 OK 响应。

## 2. 设置RestTemplate

RestTemplate需要在 Spring 上下文中声明为bean——这在 XML 或纯Java中都非常简单，使用@Bean注解：

```java
import org.apache.http.HttpHost;
import com.baeldung.client.HttpComponentsClientHttpRequestFactoryDigestAuth;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ClientConfig {

    @Bean
    public RestTemplate restTemplate() {
        HttpHost host = new HttpHost("localhost", 8080, "http");
        CloseableHttpClient client = HttpClientBuilder.create().
          setDefaultCredentialsProvider(provider()).useSystemProperties().build();
        HttpComponentsClientHttpRequestFactory requestFactory = 
          new HttpComponentsClientHttpRequestFactoryDigestAuth(host, client);

        return new RestTemplate(requestFactory);;
    }
    
    private CredentialsProvider provider() {
        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = 
          new UsernamePasswordCredentials("user1", "user1Pass");
        provider.setCredentials(AuthScope.ANY, credentials);
        return provider;
    }
}
```

摘要访问机制的大部分配置都是在注入到模板中的客户端http请求工厂的自定义实现—— HttpComponentsClientHttpRequestFactoryDigestAuth中完成的。

请注意，我们现在正在使用有权访问安全 API 的凭据预配置模板。

## 3.配置摘要认证

我们将利用 Spring 3.1 中引入的对当前 HttpClient 4.x 的支持——即HttpComponentsClientHttpRequestFactory——通过扩展和配置它。

我们主要将配置HttpContext并连接我们的自定义逻辑以进行摘要身份验证：

```java
import java.net.URI;
import org.apache.http.HttpHost;
import org.apache.http.client.AuthCache;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

public class HttpComponentsClientHttpRequestFactoryDigestAuth 
  extends HttpComponentsClientHttpRequestFactory {

    HttpHost host;

    public HttpComponentsClientHttpRequestFactoryDigestAuth(HttpHost host, HttpClient httpClient) {
        super(httpClient);
        this.host = host;
    }

    @Override
    protected HttpContext createHttpContext(HttpMethod httpMethod, URI uri) {
        return createHttpContext();
    }

    private HttpContext createHttpContext() {
        // Create AuthCache instance
        AuthCache authCache = new BasicAuthCache();
        // Generate DIGEST scheme object, initialize it and add it to the local auth cache
        DigestScheme digestAuth = new DigestScheme();
        // If we already know the realm name
        digestAuth.overrideParamter("realm", "Custom Realm Name");
        authCache.put(host, digestAuth);

        // Add AuthCache to the execution context
        BasicHttpContext localcontext = new BasicHttpContext();
        localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);
        return localcontext;
    }
}
```

现在，可以简单地注入RestTemplate并在测试中使用：

```java
@Test
public void whenSecuredRestApiIsConsumed_then200OK() {
    String uri = "http://localhost:8080/spring-security-rest-digest-auth/api/foos/1";
    ResponseEntity<Foo> entity = restTemplate.exchange(uri, HttpMethod.GET, null, Foo.class);
    System.out.println(entity.getStatusCode());
}
```

为了说明完整的配置过程，此测试还设置了用户凭据 - user1和user1Pass。当然，这部分应该只在测试本身之外完成一次。

## 4. Maven依赖

RestTemplate和 HttpClient 库所需的 Maven 依赖项是：

```xml
<dependency>
   <groupId>org.springframework</groupId>
   <artifactId>spring-webmvc</artifactId>
   <version>5.2.8.RELEASE</version>
</dependency>

<dependency>
   <groupId>org.apache.httpcomponents</groupId>
   <artifactId>httpclient</artifactId>
   <version>4.3.5</version>
</dependency>
```

## 5. 总结

本教程展示了如何设置和配置 Rest Template，以便它可以使用通过 Digest 身份验证保护的应用程序。REST API 本身需要[配置摘要安全](https://www.baeldung.com/spring-security-digest-authentication)机制。
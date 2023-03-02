## 1. 概述

Spring Cloud Security模块提供与Spring Boot应用程序中基于令牌的安全性相关的功能。

具体来说，它使基于OAuth2的SSO更容易-支持在资源服务器之间中继令牌，以及使用嵌入式Zuul代理配置下游身份验证。

在这篇简短的文章中，我们将了解如何使用Spring Boot客户端应用程序、授权服务器和用作资源服务器的REST API来配置这些功能。

请注意，对于此示例，我们只有一个使用SSO来演示云安全功能的客户端应用程序-但在典型情况下，我们至少有两个客户端应用程序来证明单点登录的必要性。

## 2. 云安全应用程序快速开始

让我们**从在Spring Boot应用程序中配置SSO开始**。

首先，我们需要添加[spring-cloud-starter-oauth2](https://search.maven.org/classic/#search|ga|1|a%3A"spring-cloud-starter-oauth2")依赖：

```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-oauth2</artifactId>
	<version>2.2.2.RELEASE</version>
</dependency>
```

这也将引入spring-cloud-starter-security依赖项。

我们可以将任何社交网站配置为我们网站的授权服务器，也可以使用我们自己的服务器。在我们的例子中，我们选择了后一个选项并配置了一个充当授权服务器的应用程序-该应用程序在http://localhost:7070/authserver本地部署。

我们的授权服务器使用JWT令牌。

此外，为了让任何客户端能够检索用户的凭据，我们需要配置我们的资源服务器，在端口9000上运行，并使用可以提供这些凭据的端点。

在这里，我们配置了一个位于http://localhost:9000/user的/user端点。

有关如何设置授权服务器和资源服务器的更多详细信息，请在[此处](https://www.baeldung.com/rest-api-spring-oauth2-angular)查看我们之前的文章。

现在，我们可以在客户端应用程序的配置类中添加注解：

```java
@Configuration
public class SiteSecurityConfigurer {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// ...   
		http.oauth2Login();
		// ... 
	}
}
```

**任何需要身份验证的请求都将被重定向到授权服务器**。为此，我们还必须定义服务器属性：

```yaml
spring:
    security:
        oauth2:
            client:
                registration:
                    baeldung:
                        client-id: authserver
                        client-secret: passwordforauthserver
                        authorization-grant-type: authorization_code
                        redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
                provider:
                    baeldung:
                        token-uri: http://localhost:7070/authserver/oauth/token
                        authorization-uri: http://localhost:7070/authserver/oauth/authorize
                        user-info-uri: http://localhost:9000/user
```

请注意，我们的类路径中需要有spring-boot-starter-security依赖才能找到上述配置。

## 3. 中继访问令牌

**在中继令牌时，OAuth2客户端将其接收到的OAuth2令牌转发到传出资源请求**。

Spring Security公开了一个OAuth2AuthorizedClientService，这对于创建RestTemplate拦截器很有用。基于此，我们可以在我们的客户端应用程序中创建我们自己的RestTemplate：

```java
@Bean
public RestOperations restTemplate(OAuth2AuthorizedClientService clientService) {
    return new RestTemplateBuilder().interceptors((ClientHttpRequestInterceptor) 
        (httpRequest, bytes, execution) -> {
        OAuth2AuthenticationToken token = 
        OAuth2AuthenticationToken.class.cast(SecurityContextHolder.getContext()
            .getAuthentication());
        OAuth2AuthorizedClient client = 
            clientService.loadAuthorizedClient(token.getAuthorizedClientRegistrationId(), 
            token.getName());
            httpRequest.getHeaders()
                .add(HttpHeaders.AUTHORIZATION, "Bearer " + client.getAccessToken()
                    .getTokenValue());
        return execution.execute(httpRequest, bytes);
    }).build();
}
```

一旦我们配置了bean，上下文就会将访问令牌转发给请求的服务，并且如果令牌过期也会刷新令牌。

## 4. 使用RestTemplate中继OAuth令牌

我们之前在客户端应用程序中定义了一个RestTemplate类型的restOperations bean。因此，**我们可以使用RestTemplate的getForObject()方法将带有必要令牌的请求从我们的客户端发送到受保护的资源服务器**。

首先，让我们在资源服务器中定义一个需要身份验证的端点：

```java
@GetMapping("/person")
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public @ResponseBody Person personInfo(){        
    return new Person("abir", "Dhaka", "Bangladesh", 29, "Male");       
}
```

这是一个简单的REST端点，它返回Person对象的JSON表示。

现在，**我们可以使用getForObject()方法从客户端应用程序发送请求，该方法会将令牌中继到资源服务器**：

```java
@Autowired
private RestOperations restOperations;

@GetMapping("/personInfo")
public ModelAndView person() { 
    ModelAndView mav = new ModelAndView("personinfo");
    String personResourceUrl = "http://localhost:9000/person";
    mav.addObject("person", restOperations.getForObject(personResourceUrl, String.class));       
    
    return mav;
}
```

## 5. 为令牌中继配置Zuul

如果我们想将令牌下游中继到代理服务，我们可以使用Spring Cloud Zuul嵌入式反向代理。

首先，我们需要添加Maven依赖项以使用Zuul：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
</dependency>
```

接下来，我们需要将@EnableZuulProxy注解添加到客户端应用程序的配置类中：

```java
@EnableZuulProxy
@Configuration
public class SiteSecurityConfigurer {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// ...   
		http.oauth2Login();
		// ... 
	}
}
```

剩下要做的就是将Zuul配置属性添加到我们的application.yml文件中：

```yaml
zuul:
    sensitiveHeaders: Cookie,Set-Cookie
    routes:
        resource:
            path: /api/**
            url: http://localhost:9000
        user:
            path: /user/**
            url: http://localhost:9000/user
```

任何到达客户端应用程序的/api端点的请求都将被重定向到资源服务器URL。我们还需要提供用户凭据端点的URL。

## 6. 总结

在这篇快速文章中，我们探讨了如何将Spring Cloud Security与OAuth2和Zuul结合使用来配置安全授权和资源服务器，以及如何使用RestTemplate和嵌入式Zuul代理在服务器之间中继OAuth2令牌。
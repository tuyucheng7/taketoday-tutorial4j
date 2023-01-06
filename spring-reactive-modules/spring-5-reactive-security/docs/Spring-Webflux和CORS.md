## 1. 概述

在[上一篇文章](https://www.baeldung.com/spring-cors)中，我们了解了跨源资源共享(CORS)规范以及如何在Spring中使用它。

在本快速教程中，我们将使用[Spring 5 WebFlux框架](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html)设置类似的CORS配置。

首先，我们将了解如何在基于注解的API上启用该机制。

然后，我们将分析如何将其作为全局配置在整个项目中启用，或者通过使用特殊的WebFilter来启用。

## 2. 在带注解的元素上启用CORS

Spring提供了@CrossOrigin注解以在控制器类和/或处理程序方法上启用CORS请求。

### 2.1 在请求处理程序方法上使用@CrossOrigin

让我们将此注解添加到我们映射的请求方法中：

```java
@CrossOrigin
@PutMapping("/cors-enabled-endpoint")
public Mono<String> corsEnabledEndpoint() {
    // ...
}
```

我们将使用WebTestClient(正如我们在[本文](https://www.baeldung.com/spring-5-functional-web)的“[4.测试]”部分中所解释的)来分析我们从该端点获得的响应：

```java
ResponseSpec response = webTestClient.put()
    .uri("/cors-enabled-endpoint")
    .header("Origin", "http://any-origin.com")
    .exchange();

response.expectHeader()
  		.valueEquals("Access-Control-Allow-Origin", "*");
```

此外，我们可以尝试预检请求以确保CORS配置按预期工作：

```java
ResponseSpec response = webTestClient.options()
    .uri("/cors-enabled-endpoint")
    .header("Origin", "http://any-origin.com")
    .header("Access-Control-Request-Method", "PUT")
    .exchange();

response.expectHeader()
  	.valueEquals("Access-Control-Allow-Origin", "*");
response.expectHeader()
  	.valueEquals("Access-Control-Allow-Methods", "PUT");
response.expectHeader()
  	.exists("Access-Control-Max-Age");
```

@CrossOrigin注解具有以下默认配置：

-   允许所有来源(解释响应标头中的“*”值)
-   允许所有标头
-   允许处理程序方法映射的所有HTTP方法
-   凭证未启用
-   'max-age'值为1800秒(30分钟)

但是，可以使用注解的参数覆盖这些值中的任何一个。

### 2.2 在控制器上使用@CrossOrigin

这个注解在类级别也受支持，它会影响它的所有方法。

如果类级配置不适合我们所有的方法，我们可以注解这两个元素以获得所需的结果：

```java
@CrossOrigin(value = { "http://allowed-origin.com" },
	allowedHeaders = { "Tuyucheng-Allowed" },
	maxAge = 900
)
@RestController
public class CorsOnClassController {

	@PutMapping("/cors-enabled-endpoint")
	public Mono<String> corsEnabledEndpoint() {
		// ...
	}

	@CrossOrigin({ "http://another-allowed-origin.com" })
	@PutMapping("/endpoint-with-extra-origin-allowed")
	public Mono<String> corsEnabledWithExtraAllowedOrigin() {
		// ...
	}

	// ...
}
```

## 3. 在全局配置上启用CORS

我们还可以通过覆盖WebFluxConfigurer实现的addCorsMappings()方法来定义全局CORS配置。

此外，实现需要@EnableWebFlux注解以在普通Spring应用程序中导入Spring WebFlux配置。如果我们使用的是Spring Boot，那么如果我们想要覆盖自动配置，则只需要这个注解：

```java
@Configuration
@EnableWebFlux
public class CorsGlobalConfiguration implements WebFluxConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry corsRegistry) {
		corsRegistry.addMapping("/**")
			.allowedOrigins("http://allowed-origin.com")
			.allowedMethods("PUT")
			.maxAge(3600);
	}
}
```

因此，我们正在为该特定路径模式启用跨源请求处理。

默认配置与@CrossOrigin类似，但只允许使用GET、HEAD和POST方法。

我们还可以将此配置与本地配置结合使用：

-   对于多值属性，生成的CORS配置将是每个规范的添加
-   另一方面，局部值将优先于单值值的全局值

但是，使用这种方法对函数式端点无效。

## 4. 使用WebFilter启用CORS

在函数式端点上启用CORS的最佳方法是使用WebFilter。

正如我们在[这篇文章](https://www.baeldung.com/spring-webflux-filters)中看到的，我们可以使用WebFilter修改请求和响应，同时保持端点的实现不变。

Spring提供了内置的CorsWebFilter来轻松应对跨域配置：

```java
@Bean
CorsWebFilter corsWebFilter() {
    CorsConfiguration corsConfig = new CorsConfiguration();
    corsConfig.setAllowedOrigins(Arrays.asList("http://allowed-origin.com"));
    corsConfig.setMaxAge(8000L);
    corsConfig.addAllowedMethod("PUT");
    corsConfig.addAllowedHeader("Tuyucheng-Allowed");

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfig);

    return new CorsWebFilter(source);
}
```

这对带注解的处理程序也有效，但它不能与更细粒度的@CrossOrigin配置结合使用。

我们必须记住，CorsConfiguration没有默认配置。

因此，除非我们指定所有相关属性，否则CORS实现将受到很大限制。

设置默认值的一种简单方法是在对象上使用applyPermitDefaultValues()方法。

## 5. 总结

总之，我们通过非常简短的示例了解了如何在我们基于Webflux的服务上启用CORS。

我们看到了不同的方法，因此我们现在要做的就是分析哪种方法最适合我们的要求。
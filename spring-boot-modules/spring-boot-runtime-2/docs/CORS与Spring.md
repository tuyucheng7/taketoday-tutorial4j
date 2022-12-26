## 1. 概述

在任何现代浏览器中，[跨源资源共享(CORS)]()都是随着通过REST API使用数据的HTML5和JS客户端的出现而出现的相关规范。

通常，为JS提供服务的主机(例如example.com)与为数据提供服务的主机(例如api.example.com)不同，在这种情况下，CORS可以实现跨域通信。

Spring为CORS提供了一流的[支持](https://docs.spring.io/spring/docs/current/spring-framework-reference/html/cors.html)，提供了一种在任何Spring或Spring Boot Web应用程序中配置它的简单而强大的方法。

## 2. 控制器方式CORS配置

**启用CORS很简单，只需添加注解@CrossOrigin即可**，我们可以通过几种不同的方式实现这一点。

### 2.1 在@RequestMapping处理程序方法上使用@CrossOrigin

```java
@RestController
@RequestMapping("/account")
public class AccountController {

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, path = "/{id}")
	public Account retrieve(@PathVariable Long id) {
		// ...
	}

	@RequestMapping(method = RequestMethod.DELETE, path = "/{id}")
	public void remove(@PathVariable Long id) {
		// ...
	}
}
```

在上面的示例中，我们只为retrieve()方法启用了CORS，我们可以看到我们没有为@CrossOrigin注解设置任何配置，因此它使用默认值：

-   允许所有来源。
-   允许的HTTP方法是那些在@RequestMapping注解中指定的方法(对于本例来说是GET)。
-   预检响应的缓存时间(maxAge)为30分钟。

### 2.2 在控制器上使用@CrossOrigin

```java
@CrossOrigin(origins = "http://example.com", maxAge = 3600)
@RestController
@RequestMapping("/account")
public class AccountController {

	@RequestMapping(method = RequestMethod.GET, path = "/{id}")
	public Account retrieve(@PathVariable Long id) {
		// ...
	}

	@RequestMapping(method = RequestMethod.DELETE, path = "/{id}")
	public void remove(@PathVariable Long id) {
		// ...
	}
}
```

这一次，我们在类级别添加了@CrossOrigin注解，因此retrieve()和remove()方法都启用了它。我们可以通过指定注解属性之一的值来自定义配置：origins、methods、allowedHeaders、exposedHeaders、allowCredentials或maxAge。

### 2.3 在控制器和处理程序方法上使用@CrossOrigin

```java
@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/account")
public class AccountController {

	@CrossOrigin("http://example.com")
	@RequestMapping(method = RequestMethod.GET, "/{id}")
	public Account retrieve(@PathVariable Long id) {
		// ...
	}

	@RequestMapping(method = RequestMethod.DELETE, path = "/{id}")
	public void remove(@PathVariable Long id) {
		// ...
	}
}
```

Spring将组合来自两个注解的属性来创建合并的CORS配置。

在这里，两种方法的maxAge均为3,600秒，remove()方法将允许所有来源，而retrieve()方法仅允许来自http://example.com的来源。

## 3. 全局CORS配置

作为细粒度的基于注解的配置的替代方案，Spring允许我们从我们的控制器中定义一个全局CORS配置，这类似于使用基于过滤器的解决方案，但可以在Spring MVC中声明并与细粒度的@CrossOrigin配置相结合。

默认情况下，允许所有来源和GET、HEAD和POST方法。

### 3.1 Java配置

```java
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**");
	}
}
```

上面的示例启用了从任何来源到应用程序中任何端点的CORS请求。

为了进一步锁定它，registry.addMapping方法返回一个CorsRegistration对象，我们可以使用它进行额外的配置。还有一个allowedOrigins方法可以让我们指定允许的来源数组，如果我们需要在运行时从外部源加载这个数组，这会很有用。

此外，还有allowedMethods、allowedHeaders、exposedHeaders、maxAge和allowCredentials可用于设置响应标头和自定义选项。

值得注意的是，从2.4.0版本开始，Spring Boot除了allowedOrigins之外，还引入了allowedOriginPatterns，这个新元素在定义模式时提供了更大的灵活性。此外，当allowCredentials为true时，allowedOrigins不能包含特殊值“*”，因为无法在Access-Control-Allow-Origin响应标头上设置该值。要解决此问题并允许凭证到一组来源，我们可以明确列出它们或考虑改用allowedOriginPatterns。

### 3.2 XML命名空间

这个最小的XML配置在/**路径模式上启用CORS，该模式具有与Java配置相同的默认属性：

```xml
<mvc:cors>
    <mvc:mapping path="/**" />
</mvc:cors>
```

也可以使用自定义属性声明多个CORS映射：

```xml
<mvc:cors>
    <mvc:mapping path="/api/**"
        allowed-origins="http://domain1.com, http://domain2.com"
        allowed-methods="GET, PUT"
        allowed-headers="header1, header2, header3"
        exposed-headers="header1, header2" allow-credentials="false"
        max-age="123" />

    <mvc:mapping path="/resources/**" allowed-origins="http://domain1.com" />
</mvc:cors>
```

## 4. CORS与SpringSecurity

如果我们在我们的项目中使用Spring Security，我们必须采取额外的步骤来确保它与CORS兼容。这是因为需要首先处理CORS，否则，Spring Security将在请求到达Spring MVC之前拒绝该请求。

幸运的是，Spring Security提供了一个开箱即用的解决方案：

```java
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and()...
    }
}
```

[本文]()对其进行了更详细的解释。

## 5. 工作原理

CORS请求会自动分派到各种已注册的HandlerMappings，他们使用[CorsProcessor](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/cors/CorsProcessor.html)实现(默认为DefaultCorsProcessor)处理CORS预检请求并拦截CORS简单和实际请求，以添加相关的CORS响应标头(例如Access-Control-Allow-Origin)。

[CorsConfiguration](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/cors/CorsConfiguration.html)允许我们指定应如何处理CORS请求，包括允许的来源、标头和方法等。我们可以通过多种方式提供它：

-   [AbstractHandlerMapping#setCorsConfiguration()](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/servlet/handler/AbstractHandlerMapping.html#setCorsConfiguration-java.util.Map-)允许我们指定一个Map，其中包含映射到路径模式(如/api/**)的多个CorsConfiguration。
-   子类可以通过覆盖AbstractHandlerMapping#getCorsConfiguration(Object, HttpServletRequest)方法来提供自己的CorsConfiguration。
-   处理程序可以实现[CorsConfigurationSource](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/cors/CorsConfigurationSource.html)接口(就像ResourceHttpRequestHandler现在所做的那样)来为每个请求提供CorsConfiguration。

## 6. 总结

在本文中，我们演示了Spring如何为在我们的应用程序中启用CORS提供支持。

我们从控制器的配置开始，我们看到我们只需要添加@CrossOrigin注解就可以为一个特定的方法或整个控制器启用CORS。此外，我们了解到，为了在控制器外部控制CORS配置，我们可以使用Java配置或XML在配置文件中顺利执行此操作。


## 1. 简介

在本快速教程中，我们将演示使用Spring的日志记录过滤器记录传入请求的基础知识，如果我们刚刚开始使用日志记录，可以查看这篇[日志记录介绍文章]()以及[SLF4J文章]()。

## 2. Maven依赖

日志依赖项与介绍文章中的依赖项相同；我们在这里简单地添加Spring：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-core</artifactId>
    <version>5.2.2.RELEASE</version>   
</dependency>
```

可以在此处找到[spring-core](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework" AND a%3A"spring-core")的最新版本。

## 3. 基本Web Controller

首先，我们将定义一个控制器以在我们的示例中使用：

```java
@RestController
public class TaxiFareController {

	@GetMapping("/taxifare/get/")
	public RateCard getTaxiFare() {
		return new RateCard();
	}

	@PostMapping("/taxifare/calculate/")
	public String calculateTaxiFare(@RequestBody @Valid TaxiRide taxiRide) {
		// return the calculated fare
	}
}
```

## 4. 自定义请求记录

Spring提供了一种机制，用于配置用户定义的拦截器以在Web请求之前和之后执行操作。

在Spring请求拦截器中，值得注意的接口之一是HandlerInterceptor，我们可以通过实现以下方法来记录传入的请求：

1.  preHandle()：我们在实际的控制器服务方法之前执行这个方法
2.  afterCompletion()：我们在控制器准备好发送响应后执行此方法

此外，Spring以HandlerInterceptorAdaptor类的形式提供了HandlerInterceptor接口的默认实现，用户可以对其进行扩展。

让我们通过扩展HandlerInterceptorAdaptor来创建我们自己的拦截器：

```java
@Component
public class TaxiFareRequestInterceptor
	extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(
		HttpServletRequest request,
		HttpServletResponse response,
		Object handler) {
		return true;
	}

	@Override
	public void afterCompletion(
		HttpServletRequest request,
		HttpServletResponse response,
		Object handler,
		Exception ex) {
		//
	}
}
```

最后，我们将在MVC生命周期中配置TaxiRideRequestInterceptor以捕获映射到TaxiFareController类中定义的路径/taxifare的控制器方法调用的预处理和后处理：

```java
@Configuration
public class TaxiFareMVCConfig implements WebMvcConfigurer {

	@Autowired
	private TaxiFareRequestInterceptor taxiFareRequestInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(taxiFareRequestInterceptor)
			.addPathPatterns("/taxifare/*/");
	}
}
```

总之，WebMvcConfigurer通过调用addInterceptors()方法在Spring MVC生命周期中添加了TaxiFareRequestInterceptor。

最大的挑战是获取用于日志记录的请求和响应有效载荷的副本，并且仍然将请求的有效载荷留给Servlet进行处理：

>   读取请求的主要问题是，一旦第一次读取输入流，它就会被标记为已消耗且无法再次读取。

应用程序将在读取请求流后引发异常：

```json
{
	"timestamp": 1500645243383,
	"status": 400,
	"error": "Bad Request",
	"exception": "org.springframework.http.converter.HttpMessageNotReadableException",
	"message": "Could not read document: Stream closed;nested exception is java.io.IOException: Stream closed",
	"path": "/rest-log/taxifare/calculate/"
}
```

为了克服这个问题，我们可以利用缓存来存储请求流并将其用于日志记录。

Spring提供了一些有用的类，例如[ContentCachingRequestWrapper](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/util/ContentCachingRequestWrapper.html)和[ContentCachingResponseWrapper](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/util/ContentCachingResponseWrapper.html)，它们可用于缓存请求数据以进行日志记录。

让我们调整TaxiRideRequestInterceptor类的preHandle()以使用ContentCachingRequestWrapper类缓存请求对象：

```java
@Override
public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    HttpServletRequest requestCacheWrapperObject = new ContentCachingRequestWrapper(request);
    requestCacheWrapperObject.getParameterMap();
    // Read inputStream from requestCacheWrapperObject and log it
    return true;
}
```

如我们所见，我们使用ContentCachingRequestWrapper类来缓存请求对象，我们可以使用它来读取有效负载数据以进行日志记录，而不会干扰实际的请求对象：

```java
requestCacheWrapperObject.getContentAsByteArray();
```

**局限性**

-   ContentCachingRequestWrapper类仅支持以下内容：

```http request
Content-Type:application/x-www-form-urlencoded
Method-Type:POST
```

-   我们必须调用以下方法来确保请求数据在使用之前缓存在ContentCachingRequestWrapper中：

```java
requestCacheWrapperObject.getParameterMap();
```

## 5. Spring内置请求日志

Spring提供了一个内置的解决方案来记录有效负载，我们可以通过使用配置插入Spring应用程序来使用现成的过滤器。

AbstractRequestLoggingFilter是一个提供日志记录基本功能的过滤器，子类应该覆盖beforeRequest()和afterRequest()方法来执行围绕请求的实际日志记录。

Spring框架提供了3个具体的实现类，我们可以使用它们来记录传入的请求。这3个类是分别是：

-   CommonsRequestLoggingFilter
-   Log4jNestedDiagnosticContextFilter(已弃用)
-   ServletContextRequestLoggingFilter

现在让我们转到CommonsRequestLoggingFilter，并将其配置为捕获用于日志记录的传入请求。

### 5.1 配置Spring Boot应用程序

我们可以通过添加bean定义来配置Spring Boot应用程序以启用请求日志记录：

```java
@Configuration
public class RequestLoggingFilterConfig {

	@Bean
	public CommonsRequestLoggingFilter logFilter() {
		CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
		filter.setIncludeQueryString(true);
		filter.setIncludePayload(true);
		filter.setMaxPayloadLength(10000);
		filter.setIncludeHeaders(false);
		filter.setAfterMessagePrefix("REQUEST DATA : ");
		return filter;
	}
}
```

此日志记录过滤器还要求我们将日志级别设置为DEBUG，我们可以通过在logback.xml中添加以下元素来启用DEBUG模式：

```xml
<logger name="org.springframework.web.filter.CommonsRequestLoggingFilter">
    <level value="DEBUG" />
</logger>
```

启用DEBUG级别日志的另一种方法是在application.properties中添加以下内容：

```properties
logging.level.org.springframework.web.filter.CommonsRequestLoggingFilter=DEBUG
```

### 5.2 配置传统Web应用程序

在标准的Spring Web应用程序中，我们可以通过XML配置或Java配置来设置Filter。因此，让我们使用传统的基于Java的配置来设置CommonsRequestLoggingFilter。

正如我们所知，CommonsRequestLoggingFilter的includePayload属性默认设置为false，在使用Java配置注入容器之前，我们需要一个自定义类来覆盖属性的值以启用includePayload：

```java
public class CustomeRequestLoggingFilter extends CommonsRequestLoggingFilter {

	public CustomeRequestLoggingFilter() {
		super.setIncludeQueryString(true);
		super.setIncludePayload(true);
		super.setMaxPayloadLength(10000);
	}
}
```

然后我们需要使用[基于Java的Web初始化程序]()注入CustomeRequestLoggingFilter：

```java
public class CustomWebAppInitializer implements WebApplicationInitializer {
	
	public void onStartup(ServletContext container) {
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.setConfigLocation("cn.tuyucheng.taketoday.runtime.web.log");
		container.addListener(new ContextLoaderListener(context));

		ServletRegistration.Dynamic dispatcher = container.addServlet("dispatcher", new DispatcherServlet(context));
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("/");

		container.addFilter("customRequestLoggingFilter", CustomeRequestLoggingFilter.class)
			.addMappingForServletNames(null, false, "dispatcher");
	}
}
```

## 6. 实践中的例子

最后，我们可以将Spring Boot与上下文连接起来，以实际查看传入请求的日志记录是否按预期工作：

```java
@Test
public void givenRequest_whenFetchTaxiFareRateCard_thanOK() {
    TestRestTemplate testRestTemplate = new TestRestTemplate();
    TaxiRide taxiRide = new TaxiRide(true, 10L);
    String fare = testRestTemplate.postForObject(URL + "calculate/", taxiRide, String.class);
 
    assertThat(fare, equalTo("200"));
}
```

## 7. 总结

在本文中，我们学习了如何使用拦截器实现基本的Web请求日志记录，我们还探讨了该解决方案的局限性和挑战。然后我们讨论了内置过滤器类，它提供了随时可用的简单日志记录机制。
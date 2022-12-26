## 1. 简介

一般来说，当我们使用微服务或Web服务时，了解我们的用户如何与我们的服务交互是非常有用的，这可以通过跟踪所有命中我们服务的请求并收集此信息以便稍后进行分析来实现。

有一些可用的系统可以帮助我们解决这个问题，并且可以像[Zipkin]()一样轻松地与Spring集成。但是，**Spring Boot Actuator内置了此功能**，可以通过跟踪所有HTTP请求的httpTrace端点使用。在本教程中，我们将展示如何使用它以及如何自定义它以更好地满足我们的要求。

## 2. HttpTrace端点设置

在本教程中，我们将使用[Maven Spring Boot项目]()。

我们需要做的第一件事是将[Spring Boot Actuator](https://search.maven.org/search?q=a:spring-boot-starter-actuator AND g:org.springframework.boot)依赖项添加到我们的项目中：

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

之后，我们必须在我们的应用程序中启用httpTrace端点。

为此，**我们只需要修改我们的application.properties文件以包含httpTrace端点**：

```properties
management.endpoints.web.exposure.include=httptrace
```

如果我们需要更多端点，我们可以将它们连接起来，用逗号分隔，或者我们可以通过使用通配符*包含所有这些端点。

现在，我们的httpTrace端点应该出现在我们应用程序的Actuator端点列表中：

```json
{
	"_links": {
		"self": {
			"href": "http://localhost:8080/actuator",
			"templated": false
		},
		"httptrace": {
			"href": "http://localhost:8080/actuator/httptrace",
			"templated": false
		}
	}
}
```

请注意，我们可以通过转到Web服务的/actuator端点来列出所有已启用的Actuator端点。

## 3. 分析痕迹

现在让我们分析httpTraceActuator端点返回的跟踪。

让我们向我们的服务发出一些请求，调用/actuator/httptrace端点并获取返回的跟踪之一：

```json
{
	"traces": [
		{
			"timestamp": "2022-12-26T14:28:36.353Z",
			"principal": null,
			"session": null,
			"request": {
				"method": "GET",
				"uri": "http://localhost:8080/echo?msg=test",
				"headers": {
					"accept-language": [
						"en-GB,en-US;q=0.9,en;q=0.8"
					],
					"upgrade-insecure-requests": [
						"1"
					],
					"host": [
						"localhost:8080"
					],
					"connection": [
						"keep-alive"
					],
					"accept-encoding": [
						"gzip, deflate, br"
					],
					"accept": [
						"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"
					],
					"user-agent": [
						"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36 OPR/62.0.3331.66"
					]
				},
				"remoteAddress": null
			},
			"response": {
				"status": 200,
				"headers": {
					"Content-Length": [
						"12"
					],
					"Date": [
						"Mon, 05 Aug 2022 14:28:36 GMT"
					],
					"Content-Type": [
						"text/html;charset=UTF-8"
					]
				}
			},
			"timeTaken": 82
		}
	]
}
```

正如我们所看到的，响应分为几个节点：

-   timestamp：收到请求的时间
-   principal：发出请求的经过身份验证的用户(如果适用)
-   session：与请求关联的任何会话
-   request：有关请求的信息，例如方法、完整URI或标头
-   response：有关响应的信息，例如状态或标头
-   timeTaken：处理请求所花费的时间

如果我们觉得这种响应太冗长，我们可以根据我们的需要调整此响应。**我们可以通过在application.properties的management.trace.http.include属性中指定它们来告诉Spring我们想要返回哪些字段**：

```properties
management.trace.http.include=RESPONSE_HEADERS
```

在这种情况下，我们指定我们只需要响应标头。因此，我们可以看到之前包含的字段(如请求标头或所用时间)现在不存在于响应中：

```json
{
	"traces": [
		{
			"timestamp": "2022-12-26T14:23:01.397Z",
			"principal": null,
			"session": null,
			"request": {
				"method": "GET",
				"uri": "http://localhost:8080/echo?msg=test",
				"headers": {},
				"remoteAddress": null
			},
			"response": {
				"status": 200,
				"headers": {
					"Content-Length": [
						"12"
					],
					"Date": [
						"Mon, 05 Aug 2022 14:23:01 GMT"
					],
					"Content-Type": [
						"text/html;charset=UTF-8"
					]
				}
			},
			"timeTaken": null
		}
	]
}
```

所有可以包含的可能值以及默认值都可以在[源代码]()中找到。

## 4. 自定义HttpTraceRepository

**默认情况下，httpTrace端点仅返回最后100个请求并将它们存储在内存中**，好消息是我们还可以通过创建我们自己的HttpTraceRepository来自定义它。

现在让我们创建我们的Repository，HttpTraceRepository接口非常简单，我们只需要实现两个方法：findAll()检索所有的踪迹；和add()将跟踪添加到Repository。

为简单起见，我们的Repository还将跟踪存储在内存中，并且我们将仅存储最后一次访问我们服务的GET请求：

```java
@Repository
public class CustomTraceRepository implements HttpTraceRepository {

	AtomicReference<HttpTrace> lastTrace = new AtomicReference<>();

	@Override
	public List<HttpTrace> findAll() {
		return Collections.singletonList(lastTrace.get());
	}

	@Override
	public void add(HttpTrace trace) {
		if ("GET".equals(trace.getRequest().getMethod())) {
			lastTrace.set(trace);
		}
	}
}
```

尽管这个简单的示例可能看起来不是很有用，但我们可以看到它有多么强大以及我们如何将日志存储在任何地方。

## 5. 过滤要跟踪的路径

我们要介绍的最后一件事是如何过滤我们想要跟踪的路径，这样我们就可以忽略一些我们不感兴趣的请求。

如果我们在向我们的服务发出一些请求后稍微使用httpTrace端点，我们可以看到我们也获得了Actuator请求的跟踪：

```json
{
	"traces": [
		{
			"timestamp": "2022-12-26T14:56:36.998Z",
			"principal": null,
			"session": null,
			"request": {
				"method": "GET",
				"uri": "http://localhost:8080/actuator/"
				// ...
			}
		}
	]
}
```

我们可能发现这些痕迹对我们没有用，我们更愿意排除它们。在这种情况下，我们只需要**创建自己的HttpTraceFilter并在shouldNotFilter方法中指定我们想要忽略的路径**：

```java
@Component
public class TraceRequestFilter extends HttpTraceFilter {

	public TraceRequestFilter(HttpTraceRepository repository, HttpExchangeTracer tracer) {
		super(repository, tracer);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		return request.getServletPath().contains("actuator");
	}
}
```

请注意，HttpTraceFilter只是一个常规的Spring过滤器，但具有一些特定于跟踪的功能。

## 6. 总结

在本教程中，我们介绍了httpTrace Spring Boot Actuator端点并演示了它的主要功能，我们还进行了更深入的研究，并解释了如何更改一些默认行为以更好地满足我们的特定需求。
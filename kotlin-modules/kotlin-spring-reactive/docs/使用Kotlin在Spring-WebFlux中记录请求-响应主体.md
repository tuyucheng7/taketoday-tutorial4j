## 一、简介

[本教程解释了在Spring WebFlux](https://www.baeldung.com/kotlin/spring-webflux-kotlin)中记录请求/响应主体的挑战，然后展示了如何使用自定义[WebFilter](https://www.baeldung.com/spring-webflux-filters)来实现目标。

## 2. 局限与挑战

Spring WebFlux 不提供任何开箱即用的日志实用程序来记录传入呼叫的主体。因此，我们必须创建自定义WebFilter来为请求和响应添加日志装饰。一旦我们读取请求或响应主体进行日志记录，输入流就会被消耗，因此控制器或客户端不会收到主体。

因此，解决方案是在装饰器中缓存请求和响应，或者将InputStream到新流并将其传递给记录器。但是，我们应该小心这种可能会增加内存使用量的重复，尤其是对于负载很重的传入调用。

## 3.用于日志记录的WebFilter

让我们从LoggingWebFilter开始，它将 ServerWebExchange的实例包装在我们为日志记录而增强的自定义ServerWebExchangeDecorator中：

```kotlin
@Component
class LoggingWebFilter : WebFilter {
    @Autowired
    lateinit var log: Logger

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain) = chain.filter(LoggingWebExchange(log, exchange))
}
```

然后我们有装饰请求和响应实例的LoggingWebExchange：

```kotlin
class LoggingWebExchange(log: Logger, delegate: ServerWebExchange) : ServerWebExchangeDecorator(delegate) {
    private val requestDecorator: LoggingRequestDecorator = LoggingRequestDecorator(log, delegate.request)
    private val responseDecorator: LoggingResponseDecorator = LoggingResponseDecorator(log, delegate.response)
    override fun getRequest(): ServerHttpRequest {
        return requestDecorator
    }

    override fun getResponse(): ServerHttpResponse {
        return responseDecorator
    }
}
```

LoggingRequestDecorator和LoggingResponseDecorator封装了日志的逻辑，我们将在后面的章节中相应地看到它们。

## 4.记录请求

我们可以从ServerHttpRequestDecorator扩展LoggingRequestDecorator，然后覆盖getBody方法来增强请求：

```kotlin
class LoggingRequestDecorator internal constructor(log: Logger, delegate: ServerHttpRequest) : ServerHttpRequestDecorator(delegate) {

    private val body: Flux<DataBuffer>?

    override fun getBody(): Flux<DataBuffer> {
        return body!!
    }

    init {
        if (log.isDebugEnabled) {
            val path = delegate.uri.path
            val query = delegate.uri.query
            val method = Optional.ofNullable(delegate.method).orElse(HttpMethod.GET).name
            val headers = delegate.headers.asString()
            log.debug(
                "{} {}n {}", method, path + (if (StringUtils.hasText(query)) "?$query" else ""), headers
            )
            body = super.getBody().doOnNext { buffer: DataBuffer ->
                    val bodyStream = ByteArrayOutputStream()
                    Channels.newChannel(bodyStream).write(buffer.asByteBuffer().asReadOnlyBuffer())
                    log.debug("{}: {}", "request", String(bodyStream.toByteArray()))
            }
        } else {
            body = super.getBody()
        }
    }
}
```

我们检查日志级别，如果日志级别匹配，我们将日志记录逻辑添加到getBody的onNext。对于日志记录，我们使用ByteBuffer#asReadOnlyBuffer来InputStream，并使用我们的Logger使用它。

## 5.记录响应

让我们继续我们的LoggingResponseDecorator类。它扩展了 ServerHttpResponseDecorator，然后覆盖了writeWith方法：

```kotlin
class LoggingResponseDecorator internal constructor(val log: Logger, delegate: ServerHttpResponse) : ServerHttpResponseDecorator(delegate) {

    override fun writeWith(body: Publisher<out DataBuffer>): Mono<Void> {
        return super.writeWith(Flux.from(body)
            .doOnNext { buffer: DataBuffer ->
                if (log.isDebugEnabled) {
                    val bodyStream = ByteArrayOutputStream()
                    Channels.newChannel(bodyStream).write(buffer.asByteBuffer().asReadOnlyBuffer())
                    log.debug("{}: {} - {} : {}", "response", String(bodyStream.toByteArray()), 
                      "header", delegate.headers.asString())
                }
            })
    }
}
```

如我们所见，响应的日志记录逻辑与记录请求的逻辑相同。我们获取 body publisher 的Flux实例，然后将其记录在onNext方法中。

## 六，总结

在本文中，我们提出了一个解决方案，用于在 Kotlin 中记录 Spring WebFlux 应用程序的请求和响应。我们首先创建了一个WebFilter，然后我们学习了如何修饰传入调用以能够使用我们首选的日志记录格式记录它们的请求和响应主体。
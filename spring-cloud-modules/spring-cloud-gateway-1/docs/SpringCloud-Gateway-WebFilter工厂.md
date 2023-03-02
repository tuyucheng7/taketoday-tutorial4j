## 1. 简介

[Spring Cloud Gateway](https://www.baeldung.com/spring-cloud-gateway)是微服务中经常使用的智能代理服务。它透明地将请求集中在单个入口点并将它们路由到适当的服务。它最有趣的特性之一是[过滤器](https://cloud.spring.io/spring-cloud-gateway/reference/html/#glossary)(WebFilter或GatewayFilter)的概念。

WebFilter与[Predicate工厂](https://www.baeldung.com/spring-cloud-gateway-routing-predicate-factories)一起，包含了完整的路由机制。**Spring Cloud Gateway提供了许多内置的WebFilter工厂，这些工厂允许在到达代理服务之前与HTTP请求交互，并在将结果交付给客户端之前与HTTP响应交互**。也可以实现[自定义过滤器](https://www.baeldung.com/spring-cloud-custom-gateway-filters)。

在本教程中，我们将重点介绍项目中包含的内置WebFilter工厂以及如何在高级用例中使用它们。

## 2. WebFilter工厂

WebFilter(或GatewayFilter)工厂允许修改入站HTTP请求和出站HTTP响应。从这个意义上讲，它提供了一组有趣的功能，可以在与下游服务交互之前和之后应用。

<img src="../assets/img_1.png">

处理程序映射(Handler Mapping)管理客户端的请求。它检查它是否与某个配置的路由匹配。然后，它将请求发送到Web Handler以执行该路由的特定过滤器链。虚线将逻辑分为前置和后置过滤器逻辑。入站过滤器在代理请求之前运行。出站过滤器在收到代理响应时开始运行。筛选器提供了修改两者之间的过程的机制。

## 3. 实现WebFilter工厂

让我们回顾一下Spring Cloud Gateway项目中最重要的WebFilter工厂。**有两种方法可以实现它们，使用YAML或[Java DSL](https://docs.spring.io/spring-integration/docs/5.1.0.M1/reference/html/java-dsl.html)**。我们将展示如何实现两者的示例。

### 3.1 HTTP请求

**内置的WebFilter工厂允许与HTTP请求的标头和参数进行交互**。我们可以[添加](https://cloud.spring.io/spring-cloud-gateway/reference/html/#the-addrequestheader-gatewayfilter-factory)(AddRequestHeader)、[映射](https://cloud.spring.io/spring-cloud-gateway/reference/html/#the-maprequestheader-gatewayfilter-factory)(MapRequestHeader)、[设置或替换](https://cloud.spring.io/spring-cloud-gateway/reference/html/#the-setrequestheader-gatewayfilter-factory)(SetRequestHeader)和[删除](https://cloud.spring.io/spring-cloud-gateway/reference/html/#the-removerequestheader-gatewayfilter-factory)(RemoveRequestHeader)标头值并将它们发送到代理服务。也可以保留原始主机标头(PreserveHostHeader)。

同理，我们可以[添加](https://cloud.spring.io/spring-cloud-gateway/reference/html/#the-addrequestparameter-gatewayfilter-factory)(AddRequestParameter)和[删除](https://cloud.spring.io/spring-cloud-gateway/reference/html/#the-removerequestparameter-gatewayfilter-factory)(RemoveRequestParameter)参数，供下游服务处理。让我们看看如何做到这一点：

```yaml
-   id: add_request_header_route
    uri: https://httpbin.org
    predicates:
        - Path=/get/**
    filters:
        - AddRequestHeader=My-Header-Good,Good
        - AddRequestHeader=My-Header-Remove,Remove
        - AddRequestParameter=var, good
        - AddRequestParameter=var2, remove
        - MapRequestHeader=My-Header-Good, My-Header-Bad
        - MapRequestHeader=My-Header-Set, My-Header-Bad
        - SetRequestHeader=My-Header-Set, Set
        - RemoveRequestHeader=My-Header-Remove
        - RemoveRequestParameter=var2
```

让我们检查一下是否一切都按预期工作。为此，我们将使用curl和公开可用的[httpbin.org](https://httpbin.org/)：

```shell
$ curl http://localhost:8080/get
{
    "args": {
        "var": "good"
    },
    "headers": {
        "Host": "localhost",
        "My-Header-Bad": "Good",
        "My-Header-Good": "Good",
        "My-Header-Set": "Set",
    },
    "origin": "127.0.0.1, 90.171.125.86",
    "url": "https://localhost:8080/get?var=good"
}
```

我们可以看到curl响应是配置请求过滤器的结果。他们将My-Header-Good添加为Good并将其内容映射到My-Header-Bad。他们删除My-Header-Remove并将新值设置为My-Header-Set。在args和url部分，我们可以看到添加了一个新参数var。此外，最后一个过滤器删除了var2参数。

**此外，我们可以在到达代理服务之前[修改请求体](https://cloud.spring.io/spring-cloud-gateway/reference/html/#modify-a-request-body-gatewayfilter-factory)**。此过滤器只能使用Java DSL表示法进行配置。下面的代码片段只是将响应正文的内容大写：

```java
@Bean
public RouteLocator routes(RouteLocatorBuilder builder) {
     return builder.routes()
        .route("modify_request_body", r -> r.path("/post/**")
            .filters(f -> f.modifyRequestBody(
                String.class, Hello.class, MediaType.APPLICATION_JSON_VALUE, 
                (exchange, s) -> Mono.just(new Hello(s.toUpperCase()))))
            .uri("https://httpbin.org"))
        .build();
}
```

为了测试代码片段，让我们使用-d选项执行curl以包含正文“Content”：

```shell
$ curl -X POST "http://localhost:8080/post" -i -d "Content"
"data": "{\"message\":\"CONTENT\"}",
"json": {
    "message": "CONTENT"
}
```

我们可以看到，由于过滤器的原因，正文的内容现在大写为CONTENT。

### 3.2 HTTP响应

同样，我们可以使用[添加](https://cloud.spring.io/spring-cloud-gateway/reference/html/#the-addresponseheader-gatewayfilter-factory)(AddResponseHeader)、[设置](https://cloud.spring.io/spring-cloud-gateway/reference/html/#the-setresponseheader-gatewayfilter-factory)或替换(SetResponseHeader)、[删除](https://cloud.spring.io/spring-cloud-gateway/reference/html/#removeresponseheader-gatewayfilter-factory)(RemoveResponseHeader)和[重写](https://cloud.spring.io/spring-cloud-gateway/reference/html/#the-rewriteresponseheader-gatewayfilter-factory)(RewriteResponseHeader)来**修改响应标头**。响应的另一个功能是[重复数据删除](https://cloud.spring.io/spring-cloud-gateway/reference/html/#the-deduperesponseheader-gatewayfilter-factory)(DedupeResponseHeader)以覆盖策略并避免重复它们。我们可以通过使用另一个内置工厂([RemoveLocationResponseHeader](https://cloud.spring.io/spring-cloud-gateway/reference/html/#rewritelocationresponseheader-gatewayfilter-factory))来摆脱有关版本、位置和主机的特定于后端的详细信息。

让我们看一个完整的例子：

```yaml
-   id: response_header_route
    uri: https://httpbin.org
    predicates:
        - Path=/header/post/**
    filters:
        - AddResponseHeader=My-Header-Good,Good
        - AddResponseHeader=My-Header-Set,Good
        - AddResponseHeader=My-Header-Rewrite, password=12345678
        - DedupeResponseHeader=Access-Control-Allow-Credentials Access-Control-Allow-Origin
        - AddResponseHeader=My-Header-Remove,Remove
        - SetResponseHeader=My-Header-Set, Set
        - RemoveResponseHeader=My-Header-Remove
        - RewriteResponseHeader=My-Header-Rewrite, password=[^&]+, password=***
        - RewriteLocationResponseHeader=AS_IN_REQUEST, Location, ,
```

让我们使用curl来显示响应标头：

```shell
$ curl -X POST "http://localhost:8080/header/post" -s -o /dev/null -D -
HTTP/1.1 200 OK
My-Header-Good: Good
Access-Control-Allow-Origin: *
Access-Control-Allow-Credentials: true
My-Header-Rewrite: password=***
My-Header-Set: Set
```

与HTTP请求类似，**我们可以[修改响应体](https://cloud.spring.io/spring-cloud-gateway/reference/html/#modify-a-response-body-gatewayfilter-factory)**。对于这个例子，我们覆盖PUT响应的主体：

```java
@Bean
public RouteLocator responseRoutes(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("modify_response_body", r -> r.path("/put/**")
            .filters(f -> f.modifyResponseBody(
                String.class, Hello.class, MediaType.APPLICATION_JSON_VALUE, 
                (exchange, s) -> Mono.just(new Hello("New Body"))))
            .uri("https://httpbin.org"))
        .build();
}
```

让我们使用PUT端点来测试功能：

```bash
$ curl -X PUT "http://localhost:8080/put" -i -d "CONTENT"
{"message":"New Body"}
```

### 3.3 Path

内置WebFilter工厂提供的功能之一是**与客户端配置的路径进行交互**。可以[设置不同的路径](https://cloud.spring.io/spring-cloud-gateway/reference/html/#the-setpath-gatewayfilter-factory)(SetPath)、[重写](https://cloud.spring.io/spring-cloud-gateway/reference/html/#the-rewritepath-gatewayfilter-factory)(RewritePath)、添加[前缀](https://cloud.spring.io/spring-cloud-gateway/reference/html/#the-prefixpath-gatewayfilter-factory)(PrefixPath)和[剥离](https://cloud.spring.io/spring-cloud-gateway/reference/html/#the-stripprefix-gatewayfilter-factory)(StripPrefix)以仅提取路径的一部分。请记住，过滤器是根据它们在YAML文件中的位置按顺序执行的。让我们看看如何配置路由：

```yaml
-   id: path_route
    uri: https://httpbin.org
    predicates:
        - Path=/new/post/**
    filters:
        - RewritePath=/new(?<segment>/?.*), $\{segment}
        - SetPath=/post
```

在到达代理服务之前，两个过滤器都删除了子路径/new。让我们执行curl：

```shell
$ curl -X POST "http://localhost:8080/new/post" -i
"X-Forwarded-Prefix": "/new"
"url": "https://localhost:8080/post"
```

我们还可以使用[StripPrefix](https://cloud.spring.io/spring-cloud-gateway/reference/html/#the-stripprefix-gatewayfilter-factory)工厂。使用StripPrefix=1，我们可以在联系下游服务时去掉第一个子路径。

### 3.4 与HTTP状态相关

[RedirectTo](https://cloud.spring.io/spring-cloud-gateway/reference/html/#the-redirectto-gatewayfilter-factory)有两个参数：status和URL。status必须是一系列300重定向HTTP代码，并且URL是有效的。[SetStatus](https://cloud.spring.io/spring-cloud-gateway/reference/html/#the-setstatus-gatewayfilter-factory)采用一个参数status，该status可以是HTTP代码或其字符串表示形式。让我们看几个例子：

```yaml
-   id: redirect_route
    uri: https://httpbin.org
    predicates:
        - Path=/fake/post/**
    filters:
        - RedirectTo=302, https://httpbin.org
-   id: status_route
    uri: https://httpbin.org
    predicates:
        - Path=/delete/**
    filters:
        - SetStatus=401
```

**第一个过滤器作用于/fake/post路径，客户端被重定向到HTTP状态302的https://httpbin.org**：

```shell
$ curl -X POST "http://localhost:8080/fake/post" -i
HTTP/1.1 302 Found
Location: https://httpbin.org
```

**第二个过滤器检测/delete路径，并设置HTTP状态401**：

```shell
$ curl -X DELETE "http://localhost:8080/delete" -i
HTTP/1.1 401 Unauthorized
```

### 3.5 请求大小限制

最后，我们可以[限制请求的大小限制](https://cloud.spring.io/spring-cloud-gateway/reference/html/#the-requestsize-gatewayfilter-factory)(RequestSize)。**如果请求大小超出限制，网关将拒绝对服务的访问**：

```yaml
-   id: size_route
    uri: https://httpbin.org
    predicates:
        - Path=/anything
    filters:
        -   name: RequestSize
            args:
                maxSize: 5000000
```

## 4. 高级用例

Spring Cloud Gateway提供了其他高级WebFilter工厂来支持微服务模式的基线功能。

### 4.1 断路器

**Spring Cloud Gateway有一个内置的WebFilter工厂，用于[断路器(Circuit Breaker)](https://www.baeldung.com/spring-cloud-circuit-breaker)功能**。工厂允许不同的回退策略和Java DSL路由配置。让我们看一个简单的例子：

```yaml
-   id: circuitbreaker_route
    uri: https://httpbin.org
    predicates:
        - Path=/status/504
    filters:
        -   name: CircuitBreaker
    args:
        name: myCircuitBreaker
        fallbackUri: forward:/anything
            - RewritePath=/status/504, /anything
```

对于断路器的配置，我们通过添加[spring-cloud-starter-circuitbreaker-reactor-resilience4j](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-starter-circuitbreaker-reactor-resilience4j/3.0.0)依赖项来使用[Resilience4J](https://cloud.spring.io/spring-cloud-circuitbreaker/reference/html/spring-cloud-circuitbreaker.html)：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-circuitbreaker-reactor-resilience4j</artifactId>
</dependency>
```

同样，我们可以使用curl测试功能：

```shell
$ curl http://localhost:8080/status/504 
"url": "https://localhost:8080/anything"
```

### 4.2 重试

**另一个高级功能允许客户端在代理服务出现问题时重试访问**。它需要几个参数，例如重试次数(retries)、HTTP状态代码(statuses)和应该重试的方法(methods)、series、exceptions和每次重试后等待的退避间隔。让我们看一下YAML配置：

```yaml
-   id: retry_test
    uri: https://httpbin.org
    predicates:
        - Path=/status/502
    filters:
        -   name: Retry
            args:
                retries: 3
                statuses: BAD_GATEWAY
                methods: GET,POST
                backoff:
                    firstBackoff: 10ms
                    maxBackoff: 50ms
                    factor: 2
                    basedOnPreviousValue: false
```

当客户端到达/status/502(Bad Gateway)时，过滤器会重试3次，等待每次执行后配置的退避间隔。让我们看看它是如何工作的：

```shell
$ curl http://localhost:8080/status/502
```

同时，我们需要查看服务器中的网关日志：

```shell
Mapping [Exchange: GET http://localhost:8080/status/502] to Route{id='retry_test', ...}
Handler is being applied: {uri=https://httpbin.org/status/502, method=GET}
Received last HTTP packet
Handler is being applied: {uri=https://httpbin.org/status/502, method=GET}
Received last HTTP packet
Handler is being applied: {uri=https://httpbin.org/status/502, method=GET}
Received last HTTP packet
```

当网关接收到状态502时，过滤器会针对GET和POST方法使用退避重试三次。

### 4.3 保存会话和安全标头

**[SecureHeader](https://cloud.spring.io/spring-cloud-gateway/reference/html/#the-secureheaders-gatewayfilter-factory)工厂将HTTP安全标头添加到响应中**。同样，[SaveSession](https://cloud.spring.io/spring-cloud-gateway/reference/html/#the-savesession-gatewayfilter-factory)在与Spring Session和Spring Security一起使用时特别重要：

```yaml
filters:
    - SaveSession
```

**此过滤器在进行转发调用之前存储会话状态**。

### 4.4 请求速率限制器

最后但同样重要的是，**[RequestRateLimiter](https://cloud.spring.io/spring-cloud-gateway/reference/html/#the-requestratelimiter-gatewayfilter-factory)工厂确定请求是否可以继续**。如果不是，它会返回HTTP代码状态429 – Too Many Requests。它使用不同的参数和解析器来指定速率限制器。

[RedisRateLimiter](https://cloud.spring.io/spring-cloud-gateway/reference/html/#the-redis-ratelimiter)使用众所周知的[Redis](https://www.baeldung.com/spring-data-redis-tutorial)数据库来检查存储桶可以保留的令牌数量。它需要以下依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
</dependency>
```

因此，它还需要Spring Redis的配置：

```yaml
spring:
    redis:
        host: localhost
        port: 6379
```

过滤器有一些属性。第一个参数replenishRate是每秒允许的请求数。第二个参数burstCapacity是一秒钟内的最大请求数。第三个参数requestedTokens是请求花费的令牌数。让我们看一个示例实现：

```yaml
-   id: request_rate_limiter
    uri: https://httpbin.org
    predicates:
        - Path=/redis/get/**
    filters:
        - StripPrefix=1
        -   name: RequestRateLimiter
            args:
                redis-rate-limiter.replenishRate: 10
                redis-rate-limiter.burstCapacity: 5
```

让我们使用curl来测试过滤器。事先，记得启动一个Redis实例，例如使用[Docker](https://hub.docker.com/_/redis/)：

```shell
$ curl "http://localhost:8080/redis/get" -i
HTTP/1.1 200 OK
X-RateLimit-Remaining: 4
X-RateLimit-Requested-Tokens: 1
X-RateLimit-Burst-Capacity: 5
X-RateLimit-Replenish-Rate: 10
```

一旦剩余速率限制达到0，网关就会引发HTTP代码429。为了测试行为，我们可以使用单元测试。我们启动一个[嵌入式Redis服务器](https://www.baeldung.com/spring-embedded-redis)并并行运行@RepeatedTests。一旦存储桶达到限制，将开始显示错误：

```shell
00:57:48.263 [main] INFO  c.t.t.s.w.RedisWebFilterFactoriesLiveTest - Received: status->200, reason->OK, remaining->[4]
00:57:48.394 [main] INFO  c.t.t.s.w.RedisWebFilterFactoriesLiveTest - Received: status->200, reason->OK, remaining->[3]
00:57:48.530 [main] INFO  c.t.t.s.w.RedisWebFilterFactoriesLiveTest - Received: status->200, reason->OK, remaining->[2]
00:57:48.667 [main] INFO  c.t.t.s.w.RedisWebFilterFactoriesLiveTest - Received: status->200, reason->OK, remaining->[1]
00:57:48.826 [main] INFO  c.t.t.s.w.RedisWebFilterFactoriesLiveTest - Received: status->200, reason->OK, remaining->[0]
00:57:48.851 [main] INFO  c.t.t.s.w.RedisWebFilterFactoriesLiveTest - Received: status->429, reason->Too Many Requests, remaining->[0]
00:57:48.894 [main] INFO  c.t.t.s.w.RedisWebFilterFactoriesLiveTest - Received: status->429, reason->Too Many Requests, remaining->[0]
00:57:49.135 [main] INFO  c.t.t.s.w.RedisWebFilterFactoriesLiveTest - Received: status->200, reason->OK, remaining->[4]
```

## 5. 总结

在本教程中，我们介绍了Spring Cloud Gateway的WebFilter工厂。我们展示了如何在执行代理服务之前和之后与来自客户端的请求和响应进行交互。
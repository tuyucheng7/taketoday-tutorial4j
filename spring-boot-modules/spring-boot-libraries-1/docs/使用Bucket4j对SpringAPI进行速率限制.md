## 1. 概述

在本教程中，我们将**重点介绍如何使用[Bucket4j](https://github.com/vladimir-bukhtoyarov/bucket4j)对Spring REST API进行速率限制**。

我们将探索API速率限制，了解Bucket4j，然后研究在Spring应用程序中限制REST API速率的几种方法。

## 2. API限速

速率限制是一种[限制对API访问](https://cloud.google.com/solutions/rate-limiting-strategies-techniques)的策略，它限制了客户端在特定时间范围内可以进行的API调用次数，这有助于保护API免受无意和恶意的过度使用。

速率限制通常通过跟踪IP地址或以更特定于业务的方式(例如API密钥或访问令牌)应用于API。作为API开发人员，当客户端达到限制时，我们有多种选择：

-   将请求排队，直到剩余时间段过去
-   立即允许请求，但为此请求收取额外费用
-   拒绝请求(HTTP 429请求过多)

## 3. Bucket4j限速库

### 3.1 什么是Bucket4j？

Bucket4j是一个基于[令牌桶](https://en.wikipedia.org/wiki/Token_bucket)算法的Java限速库，Bucket4j是一个线程安全的库，可以在独立的JVM应用程序或集群环境中使用，它还通过[JCache(JSR107)](https://www.jcp.org/en/jsr/detail?id=107)规范支持内存中或分布式缓存。

### 3.2 令牌桶算法

让我们在API速率限制的上下文中直观地看一下算法。

假设我们有一个存储桶，其容量定义为它可以容纳的令牌数量。**每当消费者想要访问API端点时，它都必须从存储桶中获取令牌**，如果令牌可用，我们会从桶中删除令牌并接受请求。相反，如果存储桶中没有任何令牌，我们将拒绝请求。

**由于请求正在消耗令牌，我们还以某种固定速率补充它们**，这样我们就不会超过存储桶的容量。

让我们考虑一个API，其速率限制为每分钟100个请求。我们可以创建一个容量为100的桶，每分钟填充100个令牌的速率。

如果我们收到70个请求，这少于给定分钟内的可用令牌，我们将在下一分钟开始时再添加30个令牌以使存储桶达到容量。另一方面，如果我们在40秒内用完所有令牌，我们将等待20秒来重新填充存储桶。

## 4. Bucket4j入门

### 4.1 Maven配置

首先我们将[bucket4j](https://search.maven.org/search?q=g:com.github.vladimir-bukhtoyarov AND a: bucket4j-core)依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.github.vladimir-bukhtoyarov</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>4.10.0</version>
</dependency>
```

### 4.2 术语

在了解如何使用Bucket4j之前，我们将简要讨论一些核心类，以及它们如何表示令牌桶算法的正式模型中的不同元素。

[Bucket](https://javadoc.io/doc/com.github.vladimir-bukhtoyarov/bucket4j-core/4.10.0/io/github/bucket4j/Bucket.html)接口表示具有最大容量的令牌桶，它提供了[tryConsume](https://javadoc.io/doc/com.github.vladimir-bukhtoyarov/bucket4j-core/4.10.0/io/github/bucket4j/Bucket.html#tryConsume-long-)和[tryConsumeAndReturnRemaining](https://javadoc.io/doc/com.github.vladimir-bukhtoyarov/bucket4j-core/4.10.0/io/github/bucket4j/Bucket.html#tryConsumeAndReturnRemaining-long-)等方法来消费令牌。如果请求符合限制并且令牌已被消费，则这些方法将消费结果返回为true。

[Bandwidth](https://javadoc.io/doc/com.github.vladimir-bukhtoyarov/bucket4j-core/4.10.0/io/github/bucket4j/Bandwidth.html)类是存储桶的关键构建块，因为它定义了存储桶的限制，我们使用带宽来配置桶的容量和重新填充的速率。

[Refill](https://javadoc.io/doc/com.github.vladimir-bukhtoyarov/bucket4j-core/4.10.0/io/github/bucket4j/Refill.html)类用于定义将令牌添加到桶中的固定速率，我们可以将速率配置为在给定时间段内添加的令牌数量。例如，每秒10个存储桶或每5分钟200个令牌，依此类推。

Bucket中的tryConsumeAndReturnRemaining方法返回[ConsumptionProbe](https://javadoc.io/doc/com.github.vladimir-bukhtoyarov/bucket4j-core/4.10.0/io/github/bucket4j/ConsumptionProbe.html)，ConsumptionProbe包含存储桶的状态，以及消费的结果，例如剩余的令牌，或者在请求的令牌再次在存储桶中可用之前剩余的时间。

### 4.3 基本用法

让我们测试一些基本的速率限制模式。

对于每分钟10个请求的速率限制，我们将创建一个容量为10且重新填充速率为每分钟10个令牌的存储桶：

```java
Refill refill = Refill.intervally(10, Duration.ofMinutes(1));
Bandwidth limit = Bandwidth.classic(10, refill);
Bucket bucket = Bucket4j.builder()
    .addLimit(limit)
    .build();

for (int i = 1; i <= 10; i++) {
    assertTrue(bucket.tryConsume(1));
}
assertFalse(bucket.tryConsume(1));
```

Refill.intervally在时间窗口开始时重新填充存储桶，在本例中为每分钟开始时的10个令牌。

接下来，让我们看看refill的作用。

**我们将设置每2秒1个令牌的重新填充率，并限制我们的请求以遵守速率限制**：

```java
Bandwidth limit = Bandwidth.classic(1, Refill.intervally(1, Duration.ofSeconds(2)));
Bucket bucket = Bucket4j.builder()
    .addLimit(limit)
    .build();
assertTrue(bucket.tryConsume(1));     // first request
Executors.newScheduledThreadPool(1)   // schedule another request for 2 seconds later
    .schedule(() -> assertTrue(bucket.tryConsume(1)), 2, TimeUnit.SECONDS);
```

假设我们的速率限制为每分钟10个请求，同时，**我们可能希望避免在前5秒内耗尽所有令牌的峰值**，Bucket4j允许我们在同一个存储桶上设置多个限制(带宽)。让我们添加另一个限制，在20秒的时间窗口内只允许5个请求：

```java
Bucket bucket = Bucket4j.builder()
    .addLimit(Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1))))
    .addLimit(Bandwidth.classic(5, Refill.intervally(5, Duration.ofSeconds(20))))
    .build();

for (int i = 1; i <= 5; i++) {
    assertTrue(bucket.tryConsume(1));
}
assertFalse(bucket.tryConsume(1));
```

## 5. 使用Bucket4j限制Spring API的速率

让我们使用Bucket4j在Spring REST API中应用速率限制。

### 5.1 面积计算器API

我们将实现一个简单但非常流行的面积计算器REST API，目前，它计算并返回给定尺寸的矩形面积：

```java
@RestController
class AreaCalculationController {

    @PostMapping(value = "/api/v1/area/rectangle")
    public ResponseEntity<AreaV1> rectangle(@RequestBody RectangleDimensionsV1 dimensions) {
        return ResponseEntity.ok(new AreaV1("rectangle", dimensions.getLength() * dimensions.getWidth()));
    }
}
```

让我们确保我们的API已启动并正在运行：

```shell
$ curl -X POST http://localhost:9001/api/v1/area/rectangle \
    -H "Content-Type: application/json" \
    -d '{ "length": 10, "width": 12 }'

{ "shape":"rectangle","area":120.0 }
```

### 5.2 应用速率限制

现在我们将引入一个简单的速率限制，允许API每分钟20个请求。换句话说，如果API在1分钟的时间窗口内收到了20个请求，它就会拒绝请求。

让我们修改我们的Controller以创建一个Bucket并添加限制(带宽)：

```java
@RestController
class AreaCalculationController {

    private final Bucket bucket;

    public AreaCalculationController() {
        Bandwidth limit = Bandwidth.classic(20, Refill.greedy(20, Duration.ofMinutes(1)));
        this.bucket = Bucket4j.builder()
              .addLimit(limit)
              .build();
    }
    //..
}
```

在这个API中，我们可以使用tryConsume方法从存储桶中消费令牌来检查请求是否被允许，如果我们达到了限制，我们可以通过响应HTTP 429 Too Many Requests状态来拒绝请求：

```java
public ResponseEntity<AreaV1> rectangle(@RequestBody RectangleDimensionsV1 dimensions) {
    if (bucket.tryConsume(1)) {
        return ResponseEntity.ok(new AreaV1("rectangle", dimensions.getLength()  dimensions.getWidth()));
    }

    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
}
```

```shell
# 21st request within 1 minute
$ curl -v -X POST http://localhost:9001/api/v1/area/rectangle \
    -H "Content-Type: application/json" \
    -d '{ "length": 10, "width": 12 }'

< HTTP/1.1 429
```

### 5.3 API客户端和定价计划

现在我们有了一个简单的速率限制，可以限制API请求。接下来，我们将介绍更多以业务为中心的速率限制的定价计划。

定价计划可帮助我们通过API获利，假设我们为API客户端制定了以下计划：

-   免费：每个API客户端每小时20个请求
-   基本：每个API客户端每小时40个请求
-   专业版：每个API客户端每小时100个请求

**每个API客户端都会获得一个唯一的API密钥，他们必须随每个请求一起发送该密钥**，这有助于我们确定与API客户端关联的定价计划。

让我们为每个定价计划定义速率限制(带宽)：

```java
enum PricingPlan {
    FREE {
        Bandwidth getLimit() {
            return Bandwidth.classic(20, Refill.intervally(20, Duration.ofHours(1)));
        }
    },
    BASIC {
        Bandwidth getLimit() {
            return Bandwidth.classic(40, Refill.intervally(40, Duration.ofHours(1)));
        }
    },
    PROFESSIONAL {
        Bandwidth getLimit() {
            return Bandwidth.classic(100, Refill.intervally(100, Duration.ofHours(1)));
        }
    }
    // ...
}
```

然后让我们添加一个方法来从给定的API密钥解析定价计划：

```java
enum PricingPlan {

    static PricingPlan resolvePlanFromApiKey(String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) {
            return FREE;
        } else if (apiKey.startsWith("PX001-")) {
            return PROFESSIONAL;
        } else if (apiKey.startsWith("BX001-")) {
            return BASIC;
        }
        return FREE;
    }
    // ...
}
```

接下来，我们需要为每个API密钥存储Bucket，并检索Bucket以进行限速：

```java
class PricingPlanService {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String apiKey) {
        return cache.computeIfAbsent(apiKey, this::newBucket);
    }

    private Bucket newBucket(String apiKey) {
        PricingPlan pricingPlan = PricingPlan.resolvePlanFromApiKey(apiKey);
        return Bucket4j.builder()
              .addLimit(pricingPlan.getLimit())
              .build();
    }
}
```

现在我们在内存中存储了每个API密钥的存储桶，让我们修改我们的Controller以使用PricingPlanService：

```java
@RestController
class AreaCalculationController {

    private PricingPlanService pricingPlanService;

    public ResponseEntity<AreaV1> rectangle(@RequestHeader(value = "X-api-key") String apiKey,
                                            @RequestBody RectangleDimensionsV1 dimensions) {

        Bucket bucket = pricingPlanService.resolveBucket(apiKey);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {
            return ResponseEntity.ok()
                  .header("X-Rate-Limit-Remaining", Long.toString(probe.getRemainingTokens()))
                  .body(new AreaV1("rectangle", dimensions.getLength()  dimensions.getWidth()));
        }

        long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
              .header("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill))
              .build();
    }
}
```

让我们来看看这些变化。API客户端发送带有X-api-key请求标头的API密钥，我们使用PricingPlanService获取此API密钥的存储桶，并通过使用存储桶中的令牌来检查请求是否被允许。

为了增强API的客户端体验，我们将使用以下额外的响应标头来发送有关速率限制的信息：

-   X-Rate-Limit-Remaining：当前时间窗口剩余的令牌数量
-   X-Rate-Limit-Retry-After-Seconds：存储桶重新填充之前的剩余时间(以秒为单位)

我们可以调用ConsumptionProbe的方法getRemainingTokens和getNanosToWaitForRefill来分别获取存储桶中剩余令牌的计数和距离下一次重新填充的剩余时间，如果我们能够成功使用令牌，则getNanosToWaitForRefill方法返回0。

让我们调用API：

```shell
## successful request
$ curl -v -X POST http://localhost:9001/api/v1/area/rectangle \
    -H "Content-Type: application/json" -H "X-api-key:FX001-99999" \
    -d '{ "length": 10, "width": 12 }'

< HTTP/1.1 200
< X-Rate-Limit-Remaining: 11
{"shape":"rectangle","area":120.0}

## rejected request
$ curl -v -X POST http://localhost:9001/api/v1/area/rectangle \
    -H "Content-Type: application/json" -H "X-api-key:FX001-99999" \
    -d '{ "length": 10, "width": 12 }'

< HTTP/1.1 429
< X-Rate-Limit-Retry-After-Seconds: 583
```

### 5.4 使用Spring MVC拦截器

假设我们现在必须添加一个新的API端点，该端点计算并返回给定高度和底边的三角形面积：

```java
@PostMapping(value = "/triangle")
public ResponseEntity<AreaV1> triangle(@RequestBody TriangleDimensionsV1 dimensions) {
    return ResponseEntity.ok(new AreaV1("triangle", 0.5d * dimensions.getHeight() * dimensions.getBase()));
}
```

事实证明，我们还需要对新端点进行速率限制，我们可以简单地从之前的端点复制并粘贴速率限制代码。或者，**我们可以使用Spring MVC的[HandlerInterceptor]()将限速代码与业务代码解耦**。

让我们创建一个RateLimitInterceptor并在preHandle方法中实现速率限制代码：

```java
public class RateLimitInterceptor implements HandlerInterceptor {

    private PricingPlanService pricingPlanService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String apiKey = request.getHeader("X-api-key");
        if (apiKey == null || apiKey.isEmpty()) {
            response.sendError(HttpStatus.BAD_REQUEST.value(), "Missing Header: X-api-key");
            return false;
        }

        Bucket tokenBucket = pricingPlanService.resolveBucket(apiKey);
        ConsumptionProbe probe = tokenBucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            return true;
        } else {
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "You have exhausted your API Request Quota");
            return false;
        }
    }
}
```

最后，我们必须将拦截器添加到InterceptorRegistry中：

```java
public class AppConfig implements WebMvcConfigurer {

    private RateLimitInterceptor interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor).addPathPatterns("/api/v1/area/");
    }
}
```

RateLimitInterceptor拦截对我们的面积计算API端点的每个请求。

下面访问试试我们的新端点：

```shell
## successful request
$ curl -v -X POST http://localhost:9001/api/v1/area/triangle \
    -H "Content-Type: application/json" -H "X-api-key:FX001-99999" \
    -d '{ "height": 15, "base": 8 }'

< HTTP/1.1 200
< X-Rate-Limit-Remaining: 9
{"shape":"triangle","area":60.0}

## rejected request
$ curl -v -X POST http://localhost:9001/api/v1/area/triangle \
    -H "Content-Type: application/json" -H "X-api-key:FX001-99999" \
    -d '{ "height": 15, "base": 8 }'

< HTTP/1.1 429
< X-Rate-Limit-Retry-After-Seconds: 299
{ "status": 429, "error": "Too Many Requests", "message": "You have exhausted your API Request Quota" }
```

看起来我们已经完成了，我们可以继续添加端点，拦截器将为每个请求应用速率限制。

## 6. Bucket4j Spring Boot Starter

让我们看看在Spring应用程序中使用Bucket4j的另一种方式，[Bucket4j Spring Boot Starter](https://github.com/MarcGiffing/bucket4j-spring-boot-starter)为Bucket4j提供自动配置，帮助我们通过Spring Boot应用程序属性或配置实现API速率限制。

**一旦我们将Bucket4j starter集成到我们的应用程序中，我们将拥有一个完全声明的API速率限制实现，而无需编写任何应用程序代码**。

### 6.1 速率限制过滤器

在我们的示例中，我们使用请求标头X-api-key的值作为识别和应用速率限制的键。

Bucket4j Spring Boot Starter提供了几个预定义的配置来定义我们的速率限制键：

-   一个天真的速率限制过滤器，这是默认的
-   按IP地址过滤
-   基于表达式的过滤器使用

基于表达式的过滤器使用[Spring表达式语言(SpEL)]()，SpEL提供对根对象的访问，例如HttpServletRequest，这些对象可用于在IP地址(getRemoteAddr())、请求标头(getHeader('X-api-key'))等上构建过滤器表达式。

该库还支持过滤器表达式中的自定义类，这在[文档](https://github.com/MarcGiffing/bucket4j-spring-boot-starter#filter-types-bad-name-should-be-renamed-in-the-feature)中进行了讨论。

### 6.2 Maven配置

首先我们将[bucket4j-spring-boot-starter](https://search.maven.org/search?q=g:com.giffing.bucket4j.spring.boot.starter AND a: bucket4j-spring-boot-starter)依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.giffing.bucket4j.spring.boot.starter</groupId>
    <artifactId>bucket4j-spring-boot-starter</artifactId>
    <version>0.2.0</version>
</dependency>
```

在我们早期的实现中，我们使用内存中的映射来存储每个API密钥(消费者)的存储桶，在这里，我们可以使用Spring的缓存抽象来配置内存存储，例如[Caffeine]()或[Guava]()。

让我们添加缓存依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
<dependency>
    <groupId>javax.cache</groupId>
    <artifactId>cache-api</artifactId>
</dependency>
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
    <version>2.8.2</version>
</dependency>
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>jcache</artifactId>
    <version>2.8.2</version>
</dependency>
```

注意：我们还添加了[jcache](https://search.maven.org/search?q=g:javax.cache AND a:cache-api)依赖项，以符合Bucket4j的缓存支持。

我们必须记住**通过将@EnableCaching注解添加到任何配置类上来启用缓存功能**。

### 6.3 应用配置

让我们将应用程序配置为使用Bucket4j Starter库，首先，我们将[配置](https://docs.spring.io/spring-boot/docs/2.1.6.RELEASE/reference/html/boot-features-caching.html#boot-features-caching-provider-caffeine)Caffeine缓存以将API密钥和Bucket存储在内存中：

```yaml
spring:
    cache:
        cache-names:
            - rate-limit-buckets
        caffeine:
            spec: maximumSize=100000,expireAfterAccess=3600s
```

接下来，让我们[配置](https://github.com/MarcGiffing/bucket4j-spring-boot-starter#bucket4j_complete_properties)Bucket4j：

```yaml
bucket4j:
    enabled: true
    filters:
        -   cache-name: rate-limit-buckets
            url: /api/v1/area.*
            strategy: first
            http-response-body: "{ \"status\": 429, \"error\": \"Too Many Requests\", \"message\": \"You have exhausted your API Request Quota\" }"
            rate-limits:
                -   expression: "getHeader('X-api-key')"
                    execute-condition: "getHeader('X-api-key').startsWith('PX001-')"
                    bandwidths:
                        -   capacity: 100
                            time: 1
                            unit: hours
                -   expression: "getHeader('X-api-key')"
                    execute-condition: "getHeader('X-api-key').startsWith('BX001-')"
                    bandwidths:
                        -   capacity: 40
                            time: 1
                            unit: hours
                -   expression: "getHeader('X-api-key')"
                    bandwidths:
                        -   capacity: 20
                            time: 1
                            unit: hours
```

以下对这些配置给出简要解释：

-   bucket4j.enabled=true：启用Bucket4j自动配置
-   bucket4j.filters.cache-name：从缓存中获取API密钥的Bucket
-   bucket4j.filters.url：指示用于应用速率限制的路径表达式
-   bucket4j.filters.strategy=first：在第一个匹配的速率限制配置处停止
-   bucket4j.filters.rate-limits.expression：使用Spring表达式语言(SpEL)检索密钥
-   bucket4j.filters.rate-limits.execute-condition：决定是否使用SpEL执行速率限制
-   bucket4j.filters.rate-limits.bandwidths：定义Bucket4j速率限制参数

我们将PricingPlanService和RateLimitInterceptor替换为按顺序评估的速率限制配置列表。

最后，请求我们的API：

```shell
## successful request
$ curl -v -X POST http://localhost:9000/api/v1/area/triangle \
    -H "Content-Type: application/json" -H "X-api-key:FX001-99999" \
    -d '{ "height": 20, "base": 7 }'

< HTTP/1.1 200
< X-Rate-Limit-Remaining: 7
{"shape":"triangle","area":70.0}

## rejected request
$ curl -v -X POST http://localhost:9000/api/v1/area/triangle \
    -H "Content-Type: application/json" -H "X-api-key:FX001-99999" \
    -d '{ "height": 7, "base": 20 }'

< HTTP/1.1 429
< X-Rate-Limit-Retry-After-Seconds: 212
{ "status": 429, "error": "Too Many Requests", "message": "You have exhausted your API Request Quota" }
```

## 7. 总结

在本文中，我们演示了几种使用Bucket4j对Spring API进行速率限制的不同方法，要了解更多信息，请务必查看[官方文档](https://github.com/vladimir-bukhtoyarov/bucket4j/blob/master/README.md)。
## 1. 简介

Spring Cloud Gateway的一个常见用例是充当一个或多个服务的门面，从而为客户端提供一种更简单的方式来使用它们。

在本教程中，我们将展示自定义公开API的不同方法，方法是在将请求发送到后端之前重写URL。

## 2. Spring Cloud Gateway快速回顾

[Spring Cloud Gateway](https://www.baeldung.com/spring-cloud-gateway)项目建立在流行的Spring Boot 2和[Project Reactor](https://www.baeldung.com/reactor-core)之上，因此它继承了它的主要优点：

-   由于其响应性，资源使用率低
-   支持来自Spring Cloud生态系统的所有好东西(服务发现、配置等)
-   使用标准的Spring模式易于扩展和/或定制

我们已经在之前的文章中介绍了它的主要功能，所以在这里我们只列出主要概念：

-   路由：匹配的传入请求在网关中经过的一组处理步骤
-   谓词 ：针对ServerWebExchange进行评估的Java 8 [Predicate](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/function/Predicate.html)
-   过滤器：可以检查和/或更改ServerWebExchange的GatewayFilter实例。网关支持全局过滤器和每路由(per-route)过滤器

简而言之，以下是传入请求所经历的处理顺序：

-   网关使用与每个路由相关联的谓词来查找哪个路由将处理请求
-   一旦找到路由，请求(一个ServerWebExchange实例)就会通过每个配置的过滤器，直到它最终被发送到后端
-   当后端发回响应或出现错误(例如超时或连接重置)时，过滤器在将响应发送回客户端之前再次获得处理响应的机会

## 3. 基于配置的URL重写

回到本文的主题，让我们看看如何定义一个在将传入URL发送到后端之前重写传入URL的路由。例如，假设给定一个/api/v1/customer/\*形式的传入请求，后端URL应该是http://v1.customers/api/*。在这里，我们使用“\*”来表示“超出这一点的任何内容”。

**要创建基于配置的重写，我们只需要向应用程序的配置添加一些属性**。在这里，为了清楚起见，我们将使用基于YAML的配置，但此信息可能来自任何受支持的PropertySource：

```yaml
spring:
    cloud:
        gateway:
            routes:
                -   id: rewrite_v1
                    uri: ${rewrite.backend.uri:http://example.com}
                    predicates:
                        - Path=/v1/customer/**
                    filters:
                        - RewritePath=/v1/customer/(?<segment>.*),/api/$\{segment}
```

让我们剖析这个配置。首先，我们有路由的id，这只是它的标识符。接下来，我们有uri属性给出的后端URI。**请注意，仅考虑主机名/端口，因为最终路径来自重写逻辑**。

predicates属性定义激活此路由必须满足的条件。在我们的例子中，我们使用Path谓词，它采用类似ant的路径表达式来匹配传入请求的路径。

最后，filters属性具有实际的重写逻辑。**RewritePath过滤器有两个参数：一个正则表达式和一个替换字符串**。过滤器的实现通过简单地在请求的URI上执行replaceAll()方法来工作，使用提供的参数作为参数。

Spring处理配置文件的方式的一个警告是我们不能使用标准的${group}替换表达式，因为Spring会认为它是一个属性引用并尝试替换它的值。为避免这种情况，我们需要在“$”和“{”字符之间添加一个反斜杠，在将其用作实际替换表达式之前，过滤器实现将删除该反斜杠。

## 4. 基于DSL的URL重写

虽然RewritePath非常强大且易于使用，但它在重写规则具有某些动态方面的场景中存在不足。根据具体情况，仍然可以使用谓词作为规则的每个分支的守卫来编写多个规则。

**但是，如果不是这种情况，我们可以使用基于DSL的方法创建路由**。我们需要做的就是创建一个实现路由逻辑的RouteLocator bean。例如，让我们创建一个简单的路由，它像以前一样使用正则表达式重写传入的URI。然而，这一次，替换字符串将根据每个请求动态生成：

```java
@Configuration
public class DynamicRewriteRoute {

    @Value("${rewrite.backend.uri}")
    private String backendUri;
    private static Random rnd = new Random();

    @Bean
    public RouteLocator dynamicZipCodeRoute(RouteLocatorBuilder builder) {
        return builder.routes()
              .route("dynamicRewrite", r ->
                    r.path("/v2/zip/**")
                          .filters(f -> f.filter((exchange, chain) -> {
                              ServerHttpRequest req = exchange.getRequest();
                              addOriginalRequestUrl(exchange, req.getURI());
                              String path = req.getURI().getRawPath();
                              String newPath = path.replaceAll(
                                    "/v2/zip/(?<zipcode>.*)",
                                    "/api/zip/${zipcode}-" + String.format("%03d", rnd.nextInt(1000)));
                              ServerHttpRequest request = req.mutate().path(newPath).build();
                              exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, request.getURI());
                              return chain.filter(exchange.mutate().request(request).build());
                          }))
                          .uri(backendUri))
              .build();
    }
}
```

在这里，动态部分只是附加到替换字符串的随机数。真实世界的应用程序可能具有更复杂的逻辑，但基本机制与所示相同。

关于这段代码所经历的步骤的一些说明：首先，它调用来自ServerWebExchangeUtils类的addOriginalRequestUrl()将原始URL存储在exchange的属性GATEWAY_ORIGINAL_REQUEST_URL_ATTR下。此属性的值是一个列表，我们将在进行任何修改之前将接收到的URL附加到该列表中，并由网关在内部用作X-Forwarded-For标头处理的一部分。

其次，一旦我们应用了重写逻辑，我们必须将修改后的URL保存在GATEWAY_REQUEST_URL_ATTR交换的属性中。**文档中没有直接提及此步骤，但可确保我们的自定义过滤器与其他可用过滤器很好地配合使用**。

## 5. 测试

为了测试我们的重写规则，我们将使用标准的[JUnit 5](https://www.baeldung.com/junit-5-test-annotation)类并稍加改动：我们将基于Java SDK的[com.sun.net.httpserver.HttpServer](https://docs.oracle.com/en/java/javase/11/docs/api/jdk.httpserver/com/sun/net/httpserver/HttpServer.html)类启动一个简单的服务器。服务器将在随机端口上启动，从而避免端口冲突。

**然而，这种方法的缺点是我们必须找出实际分配给服务器的端口并将其传递给Spring，以便我们可以使用它来设置路由的uri属性**。幸运的是，Spring为我们提供了解决这个问题的优雅方案：[@DynamicPropertySource](https://www.baeldung.com/spring-dynamicpropertysource)。在这里，我们将使用它来启动服务器并使用绑定端口的值注册一个属性：

```java
@DynamicPropertySource
static void registerBackendServer(DynamicPropertyRegistry registry) {
    registry.add("rewrite.backend.uri", () -> {
        HttpServer s = startTestServer();
        return "http://localhost:" + s.getAddress().getPort();
    });
}
```

[测试处理程序](https://github.com/eugenp/tutorials/blob/master/spring-cloud-modules/spring-cloud-gateway/src/test/java/com/baeldung/springcloudgateway/rewrite/URLRewriteGatewayApplicationLiveTest.java)只是在响应正文中回显接收到的URI。这使我们能够验证重写规则是否按预期工作。例如，这是：

```java
@Test
void testWhenApiCall_thenRewriteSuccess(@Autowired WebTestClient webClient) {
    webClient.get()
        .uri("http://localhost:" + localPort + "/v1/customer/customer1")
        .exchange()
        .expectBody()
        .consumeWith((result) -> {
            String body = new String(result.getResponseBody());
            assertEquals("/api/customer1", body);
        });
}
```

## 6. 总结

在本快速教程中，我们展示了使用Spring Cloud Gateway库重写URL的不同方法。
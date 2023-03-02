## 1. 简介

在本教程中，我们将了解如何使用Spring Cloud Gateway在将响应主体发送回客户端之前检查和/或修改响应主体。

## 2. Spring Cloud Gateway快速回顾

Spring Cloud Gateway，简称SCG，是Spring Cloud家族的一个子项目，它提供了一个构建在响应式Web堆栈之上的API网关。我们已经在[前面的教程](https://www.baeldung.com/?s="spring+cloud+gateway")中介绍了它的基本用法，因此我们不会在这里深入这些方面。

**相反，这次我们将重点介绍围绕API网关设计解决方案时不时出现的特定使用场景：如何在将后端响应有效负载发送回客户端之前对其进行处理**？

下面列出了我们可能使用此功能的一些情况：

-   保持与现有客户端的兼容性，同时允许后端发展
-   屏蔽某些字段遵守PCI或GDPR等法规的责任

更实际地说，满足这些要求意味着我们需要实现一个过滤器来处理后端响应。由于过滤器是SCG中的核心概念，因此我们支持响应处理所需要做的就是实现一个应用所需转换的自定义过滤器。

此外，一旦我们创建了过滤器组件，我们就可以将其应用于任何已声明的路由。

## 3. 实现数据清理过滤器

为了更好地说明响应主体操作的工作原理，让我们创建一个简单的过滤器来屏蔽基于JSON的响应中的值。例如，给定一个具有名为“ssn”的字段的JSON：

```json
{
    "name": "John Doe",
    "ssn": "123-45-9999",
    "account": "9999888877770000"
}
```

我们希望用固定值替换它们的值，从而防止数据泄漏：

```json
{
    "name" : "John Doe",
    "ssn" : "****",
    "account" : "9999888877770000"
}
```

### 3.1 实现GatewayFilterFactory

顾名思义，GatewayFilterFactory是给定时间过滤器的工厂。在启动时，Spring会查找任何实现此接口的带[@Component](https://www.baeldung.com/spring-component-annotation)注解的类。然后它构建一个可用过滤器的注册表，我们可以在声明路由时使用它：

```yaml
spring:
    cloud:
        gateway:
            routes:
                -   id: rewrite_with_scrub
                    uri: ${rewrite.backend.uri:http://example.com}
                    predicates:
                        - Path=/v1/customer/**
                    filters:
                        - RewritePath=/v1/customer/(?<segment>.*),/api/$\{segment}
                        - ScrubResponse=ssn,***
```

**请注意，当使用这种基于配置的方法来定义路由时，根据SCG的预期命名约定来命名我们的工厂很重要**：FilterNameGatewayFilterFactory。考虑到这一点，我们将我们的工厂命名为ScrubResponseGatewayFilterFactory。

SCG已经有几个实用类，我们可以使用它们来实现这个工厂。在这里，我们将使用开箱即用的过滤器常用的一个：AbstractGatewayFilterFactory<T\>，一个模板化的基类，其中T代表与我们的过滤器实例关联的配置类。在我们的例子中，我们只需要两个配置属性：

-   fields：用于匹配字段名称的正则表达式
-   replacement：将替换原始值的字符串

我们必须实现的关键方法是apply()。SCG为使用我们过滤器的每个路由定义调用此方法。例如，在上面的配置中，apply()只会被调用一次，因为只有一个路由定义。

在我们的例子中，实现很简单：

```java
@Override
public GatewayFilter apply(Config config) {
    return modifyResponseBodyFilterFactory
       .apply(c -> c.setRewriteFunction(JsonNode.class, JsonNode.class, new Scrubber(config)));
}
```

在这种情况下，它非常简单，因为我们使用了另一个内置过滤器ModifyResponseBodyGatewayFilterFactory，我们将与正文解析和类型转换相关的所有繁重工作委托给它。我们使用构造函数注入来获取这个工厂的实例，并在apply()中，我们将创建GatewayFilter实例的任务委托给它。

**这里的关键点是使用apply()方法变体，它不是接收配置对象，而是需要Consumer进行配置**。同样重要的是，此配置是ModifyResponseBodyGatewayFilterFactory之一。此配置对象提供我们在代码中调用的setRewriteFunction()方法。

### 3.2 使用setRewriteFunction()

现在，让我们更深入地了解setRewriteFunction()。

此方法接收三个参数：两个Class(输入和输出)和一个可以从传入类型转换为传出类型的函数。在我们的例子中，我们没有转换类型，所以输入和输出都使用相同的类：JsonNode。此类来自[Jackson](https://www.baeldung.com/jackson)库，位于用于表示JSON中不同节点类型(例如对象节点、数组节点等)的类层次结构的最顶端。使用JsonNode作为输入/输出类型允许我们处理任何有效的JSON负载，这正是我们在本例中想要的。

对于转换器类，我们传递了一个Scrubber实例，该实例在其apply()方法中实现了所需的RewriteFunction接口：

```java
public static class Scrubber implements RewriteFunction<JsonNode,JsonNode> {
    // ... fields and constructor omitted
    @Override
    public Publisher<JsonNode> apply(ServerWebExchange t, JsonNode u) {
        return Mono.just(scrubRecursively(u));
    }
    // ... scrub implementation omitted
}
```

传递给apply()的第一个参数是当前的ServerWebExchange，它使我们能够访问目前为止的请求处理上下文。我们不会在这里使用它，但很高兴知道我们有这个功能。下一个参数是接收到的正文，已经转换为适当的类。

预期的返回是被告知的外类实例的[Publisher](https://www.baeldung.com/reactor-core)。所以，只要我们不做任何类型的阻塞I/O操作，我们就可以在重写函数内部做一些复杂的工作。

### 3.3 Scrubber实现

因此，现在我们知道了重写函数的契约，让我们最终实现我们的Scrubber逻辑。**在这里，我们假设有效负载相对较小，因此我们不必担心存储接收到的对象的内存需求**。

它的实现只是递归遍历所有节点，查找与配置的模式匹配的属性并替换掩码的相应值：

```java
public static class Scrubber implements RewriteFunction<JsonNode, JsonNode> {
    // ... fields and constructor omitted
    private JsonNode scrubRecursively(JsonNode u) {
        if (!u.isContainerNode()) {
            return u;
        }

        if (u.isObject()) {
            ObjectNode node = (ObjectNode) u;
            node.fields().forEachRemaining((f) -> {
                if (fields.matcher(f.getKey()).matches() && f.getValue().isTextual()) {
                    f.setValue(TextNode.valueOf(replacement));
                } else {
                    f.setValue(scrubRecursively(f.getValue()));
                }
            });
        } else if (u.isArray()) {
            ArrayNode array = (ArrayNode) u;
            for (int i = 0; i < array.size(); i++) {
                array.set(i, scrubRecursively(array.get(i)));
            }
        }

        return u;
    }
}
```

## 4. 测试

我们在示例代码中包括了两个测试：一个简单的单元测试和一个集成测试。第一个只是一个常规的[JUnit](https://www.baeldung.com/junit-5)测试，用作Scrubber的健全性检查。**集成测试更有趣，因为它说明了SCG开发环境中的有用技术**。

首先，存在提供可以发送消息的实际后端的问题。一种可能性是使用外部工具，如Postman或等效工具，这会给典型的CI/CD场景带来一些问题。相反，我们将使用JDK鲜为人知的HttpServer类，它实现了一个简单的HTTP服务器。

```java
@Bean
public HttpServer mockServer() throws IOException {
    HttpServer server = HttpServer.create(new InetSocketAddress(0),0);
    server.createContext("/customer", (exchange) -> {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        
        byte[] response = JSON_WITH_FIELDS_TO_SCRUB.getBytes("UTF-8");
        exchange.sendResponseHeaders(200,response.length);
        exchange.getResponseBody().write(response);
    });
    
    server.setExecutor(null);
    server.start();
    return server;
}
```

该服务器将处理位于/customer的请求并返回我们测试中使用的固定JSON响应。请注意，返回的服务器已经启动，并将在随机端口上监听传入的请求。我们还指示服务器创建一个新的默认Executor来管理用于处理请求的线程

其次，我们以编程方式创建一个包含过滤器的路由@Bean。这相当于使用配置属性构建路由，但允许我们完全控制测试路由的所有方面：

```java
@Bean
public RouteLocator scrubSsnRoute(RouteLocatorBuilder builder, ScrubResponseGatewayFilterFactory scrubFilterFactory, SetPathGatewayFilterFactory pathFilterFactory, HttpServer server) {
    int mockServerPort = server.getAddress().getPort();
    ScrubResponseGatewayFilterFactory.Config config = new ScrubResponseGatewayFilterFactory.Config();
    config.setFields("ssn");
    config.setReplacement("*");
    
    SetPathGatewayFilterFactory.Config pathConfig = new SetPathGatewayFilterFactory.Config();
    pathConfig.setTemplate("/customer");
    
    return builder.routes()
        .route("scrub_ssn",
            r -> r.path("/scrub")
                .filters( 
                    f -> f
                        .filter(scrubFilterFactory.apply(config))
                        .filter(pathFilterFactory.apply(pathConfig)))
                .uri("http://localhost:" + mockServerPort ))
        .build();
}
```

最后，这些bean现在是@TestConfiguration的一部分，我们可以将它们与[WebTestClient](https://www.baeldung.com/spring-5-webclient#workingwebtestclient)一起注入到实际测试中。实际测试使用此WebTestClient来驱动旋转的SCG和后端：

```java
@Test
public void givenRequestToScrubRoute_thenResponseScrubbed() {
    client.get()
        .uri("/scrub")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().is2xxSuccessful()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody().json(JSON_WITH_SCRUBBED_FIELDS);
}
```

## 5. 总结

在本文中，我们展示了如何访问后端服务的响应主体并使用Spring Cloud Gateway库对其进行修改。
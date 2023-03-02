## 1. 概述

在本教程中，我们将学习如何编写自定义Spring Cloud Gateway过滤器。

我们在上一篇文章[探索新的Spring Cloud Gateway](https://www.baeldung.com/spring-cloud-gateway)中介绍了这个框架，我们在其中看到了许多内置过滤器。

**在这种情况下，我们将更深入并编写自定义过滤器以充分利用我们的API网关**。

首先，我们将了解如何创建全局过滤器来影响网关处理的每个请求。然后，我们将编写网关过滤器工厂，可以细粒度地应用于特定的路由和请求。

最后，我们将处理更高级的场景，学习如何修改请求或响应，甚至如何以响应方式将请求与对其他服务的调用链接起来。

## 2. 项目设置

我们将首先设置一个基本应用程序，将其用作API网关。

### 2.1 Maven配置

在使用Spring Cloud库时，设置依赖管理配置来为我们处理依赖始终是一个不错的选择：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>Hoxton.SR4</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

现在我们可以添加我们的Spring Cloud库而无需指定我们使用的实际版本：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
```

可以使用Maven Central搜索引擎找到最新的[Spring Cloud Release Train](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-dependencies/2022.0.1)版本。当然，我们应该始终检查该版本是否与我们在[Spring Cloud文档](https://spring.io/projects/spring-cloud)中使用的Spring Boot版本兼容。

### 2.2 API网关配置

我们假设有第二个应用程序在本地运行在端口8081上，它在点击/resource时公开资源(为简单起见，只是一个简单的字符串)。

考虑到这一点，我们将配置我们的网关来代理对此服务的请求。简而言之，当我们向URI路径中带有/service前缀的网关发送请求时，我们会将调用转发到该服务。

因此，当我们在网关中调用/service/resource时，我们应该会收到字符串响应。

为此，我们将使用应用程序属性配置此路由：

```yaml
spring:
    cloud:
        gateway:
            routes:
                -   id: service_route
                    uri: http://localhost:8081
                    predicates:
                        - Path=/service/**
                    filters:
                        - RewritePath=/service(?<segment>/?.*), $\{segment}
```

此外，为了能够正确跟踪网关进程，我们还将启用一些日志：

```yaml
logging:
    level:
        org.springframework.cloud.gateway: DEBUG
        reactor.netty.http.client: DEBUG
```

## 3. 创建全局过滤器

一旦网关处理程序确定请求与路由匹配，框架就会通过过滤器链传递请求。这些过滤器可以在发送请求之前或之后执行逻辑。

在本节中，我们将从编写简单的全局过滤器开始。这意味着，它会影响每一个请求。

首先，我们将了解如何在发送代理请求之前执行逻辑(也称为“前置(pre)”过滤器)

### 3.1 编写全局前置过滤器逻辑

正如我们所说，我们将在这一点上创建简单的过滤器，因为这里的主要目标只是查看过滤器是否在正确的时刻执行；只需记录一条简单的消息就可以解决问题。

**要创建自定义全局过滤器，我们所要做的就是实现Spring Cloud Gateway GlobalFilter接口，并将其作为bean添加到上下文中**：

```java
@Component
public class LoggingGlobalPreFilter implements GlobalFilter {

    final Logger logger = LoggerFactory.getLogger(LoggingGlobalPreFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        logger.info("Global Pre Filter executed");
        return chain.filter(exchange);
    }
}
```

我们可以很容易地看到这里发生了什么；一旦调用此过滤器，我们将记录一条消息，并继续执行过滤器链。

现在让我们定义一个“post(后置)”过滤器，如果我们不熟悉[Reactive编程模型和Spring Webflux API](https://www.baeldung.com/spring-webflux)，这可能会有点棘手。

### 3.2 编写全局后置过滤器逻辑

关于我们刚刚定义的全局过滤器，需要注意的另一件事是GlobalFilter接口只定义了一个方法。因此，它可以表示为[Lambda表达式](https://www.baeldung.com/java-8-lambda-expressions-tips)，使我们可以方便地定义过滤器。

例如，我们可以在配置类中定义我们的后置过滤器：

```java
@Configuration
public class LoggingGlobalFiltersConfigurations {

    final Logger logger = LoggerFactory.getLogger(LoggingGlobalFiltersConfigurations.class);

    @Bean
    public GlobalFilter postGlobalFilter() {
        return (exchange, chain) -> {
            return chain.filter(exchange)
                  .then(Mono.fromRunnable(() -> {
                      logger.info("Global Post Filter executed");
                  }));
        };
    }
}
```

简而言之，我们在链完成执行后运行一个新的Mono实例。

现在让我们通过在我们的网关服务中调用/service/resource URL并检查日志控制台来尝试一下：

```shell
DEBUG --- o.s.c.g.h.RoutePredicateHandlerMapping:
  Route matched: service_route
DEBUG --- o.s.c.g.h.RoutePredicateHandlerMapping:
  Mapping [Exchange: GET http://localhost/service/resource]
  to Route{id='service_route', uri=http://localhost:8081, order=0, predicate=Paths: [/service/**],
  match trailing slash: true, gatewayFilters=[[[RewritePath /service(?<segment>/?.*) = '${segment}'], order = 1]]}
INFO  --- c.b.s.c.f.global.LoggingGlobalPreFilter:
  Global Pre Filter executed
DEBUG --- r.netty.http.client.HttpClientConnect:
  [id: 0x58f7e075, L:/127.0.0.1:57215 - R:localhost/127.0.0.1:8081]
  Handler is being applied: {uri=http://localhost:8081/resource, method=GET}
DEBUG --- r.n.http.client.HttpClientOperations:
  [id: 0x58f7e075, L:/127.0.0.1:57215 - R:localhost/127.0.0.1:8081]
  Received response (auto-read:false) : [Content-Type=text/html;charset=UTF-8, Content-Length=16]
INFO  --- c.f.g.LoggingGlobalFiltersConfigurations:
  Global Post Filter executed
DEBUG --- r.n.http.client.HttpClientOperations:
  [id: 0x58f7e075, L:/127.0.0.1:57215 - R:localhost/127.0.0.1:8081] Received last HTTP packet
```

如我们所见，过滤器在网关将请求转发给服务之前和之后都有效地执行。

当然，我们可以在单个过滤器中组合“前置”和“后置”逻辑：

```java
@Component
public class FirstPreLastPostGlobalFilter implements GlobalFilter, Ordered {

    final Logger logger = LoggerFactory.getLogger(FirstPreLastPostGlobalFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        logger.info("First Pre Global Filter");
        return chain.filter(exchange)
              .then(Mono.fromRunnable(() -> {
                  logger.info("Last Post Global Filter");
              }));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
```

**请注意，如果我们关心过滤器在链中的位置，我们也可以实现Ordered接口**。

**由于过滤器链的性质，具有较低优先级(链中较低顺序)的过滤器将在较早阶段执行其“前置”逻辑，但其“后置”实现将在较晚阶段调用**：

<img src="../assets/img.png">

## 4. 创建GatewayFilters

全局过滤器非常有用，但我们经常需要执行仅适用于某些路由的细粒度自定义网关过滤器操作。

### 4.1 定义GatewayFilterFactory

**为了实现GatewayFilter，我们必须实现GatewayFilterFactory接口。Spring Cloud Gateway还提供了一个抽象类来简化这个过程，即AbstractGatewayFilterFactory类**：

```java
@Component
public class LoggingGatewayFilterFactory extends AbstractGatewayFilterFactory<LoggingGatewayFilterFactory.Config> {

    final Logger logger = LoggerFactory.getLogger(LoggingGatewayFilterFactory.class);

    public LoggingGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        // ...
    }

    public static class Config {
        // ...
    }
}
```

在这里，我们定义了GatewayFilterFactory的基本结构。**我们将在初始化过滤器时使用Config类来自定义过滤器**。

例如，在这种情况下，我们可以在配置中定义三个基本字段：

```java
public static class Config {
    private String baseMessage;
    private boolean preLogger;
    private boolean postLogger;

    // constructors, getters and setters ...
}
```

简单地说，这些字段是：

1.  将包含在日志条目中的自定义消息
2.  一个标志，指示过滤器是否应在转发请求之前记录
3.  一个标志，指示过滤器在收到代理服务的响应后是否应该记录

现在我们可以使用这些配置来检索GatewayFilter实例，同样可以用lambda函数表示：

```java
@Override
public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
        // Pre-processing
        if (config.isPreLogger()) {
            logger.info("Pre GatewayFilter logging: " + config.getBaseMessage());
        }
        return chain.filter(exchange)
          .then(Mono.fromRunnable(() -> {
              // Post-processing
              if (config.isPostLogger()) {
                  logger.info("Post GatewayFilter logging: " + config.getBaseMessage());
              }
          }));
    };
}
```

### 4.2 使用属性注册GatewayFilter

现在，我们可以轻松地将我们的过滤器注册到我们之前在应用程序属性中定义的路由：

```yaml
# ...
filters:
    - RewritePath=/service(?<segment>/?.*), $\{segment}
    -   name: Logging
        args:
            baseMessage: My Custom Message
            preLogger: true
            postLogger: true
```

我们只需指出配置参数。**这里很重要的一点是，我们需要在LoggingGatewayFilterFactory.Config类中配置一个无参数的构造函数和setter，以使这种方法正常工作**。

如果我们想使用紧凑的符号来配置过滤器，那么我们可以这样做：

```yaml
filters:
    - RewritePath=/service(?<segment>/?.*), $\{segment}
    - Logging=My Custom Message, true, true
```

我们需要对我们的工厂进行更多调整。简而言之，我们必须重写shortcutFieldOrder方法，以指示快捷方式属性将使用的顺序和参数数量：

```java
@Override
public List<String> shortcutFieldOrder() {
    return Arrays.asList("baseMessage", "preLogger", "postLogger");
}
```

### 4.3 排序GatewayFilter

**如果我们想配置过滤器在过滤器链中的位置，我们可以从AbstractGatewayFilterFactory#apply方法中检索一个OrderedGatewayFilter实例，而不是一个普通的lambda表达式**：

```java
@Override
public GatewayFilter apply(Config config) {
    return new OrderedGatewayFilter((exchange, chain) -> {
        // ...
    }, 1);
}
```

### 4.4 以编程方式注册GatewayFilter

此外，我们也可以通过编程方式注册我们的过滤器。让我们重新定义我们一直在使用的路由，这次通过设置RouteLocator bean：

```java
@Bean
public RouteLocator routes(RouteLocatorBuilder builder, LoggingGatewayFilterFactory loggingFactory) {
    return builder.routes()
        .route("service_route_java_config", r -> r.path("/service/**")
            .filters(f -> 
                f.rewritePath("/service(?<segment>/?.*)", "$\\{segment}")
                    .filter(loggingFactory.apply(
                    new Config("My Custom Message", true, true))))
                .uri("http://localhost:8081"))
        .build();
}
```

## 5. 高级场景

到目前为止，我们所做的只是在网关进程的不同阶段记录一条消息。

通常，我们需要过滤器来提供更高级的功能。例如，我们可能需要检查或操作我们收到的请求，修改我们正在检索的响应，甚至将响应流与对其他不同服务的调用链接起来。

接下来，我们将看到这些不同场景的示例。

### 5.1 检查和修改请求

让我们想象一个假设的场景。我们的服务过去常常根据locale设置查询参数来提供其内容。然后，我们将API更改为使用Accept-Language标头，但某些客户端仍在使用查询参数。

因此，我们要配置网关以遵循以下逻辑进行规范化：

1.  如果我们收到Accept-Language标头，我们希望保留它
2.  否则，使用locale查询参数值
3.  如果也不存在，请使用默认语言环境
4.  最后，我们要删除locale查询参数

注意：为了简单起见，我们将只关注过滤器逻辑；为了了解整个实现，我们将在教程末尾找到指向代码库的链接。

让我们将网关过滤器配置为“前置”过滤器：

```java
(exchange, chain) -> {
    if (exchange.getRequest()
        .getHeaders()
        .getAcceptLanguage()
        .isEmpty()) {
          // populate the Accept-Language header...
    }

    // remove the query param...
    return chain.filter(exchange);
};
```

在这里，我们处理逻辑的第一个方面。我们可以看到检查ServerHttpRequest对象非常简单。此时，我们只访问了它的标头，但正如我们接下来将看到的，我们可以同样轻松地获取其他属性：

```java
String queryParamLocale = exchange.getRequest()
    .getQueryParams()
    .getFirst("locale");

Locale requestLocale = Optional.ofNullable(queryParamLocale)
    .map(l -> Locale.forLanguageTag(l))
    .orElse(config.getDefaultLocale());
```

现在我们已经介绍了行为的下两点。但是我们还没有修改请求。为此，**我们必须使用mutate功能**。

这样，框架将创建实体的装饰器，保持原始对象不变。

修改标头很简单，因为我们可以获得对HttpHeaders映射对象的引用：

```java
exchange.getRequest()
    .mutate()
    .headers(h -> h.setAcceptLanguageAsLocales(Collections.singletonList(requestLocale)))
```

但是，另一方面，修改URI并非易事。

我们必须从原始exchange对象获取一个新的ServerWebExchange实例，修改原始ServerHttpRequest实例：

```java
ServerWebExchange modifiedExchange = exchange.mutate()
    // Here we'll modify the original request:
    .request(originalRequest -> originalRequest)
    .build();

return chain.filter(modifiedExchange);
```

现在是时候通过删除查询参数来更新原始请求URI了：

```java
originalRequest -> originalRequest.uri(
    UriComponentsBuilder.fromUri(exchange.getRequest()
        .getURI())
    .replaceQueryParams(new LinkedMultiValueMap<String, String>())
    .build()
    .toUri())
```

好了，我们现在可以尝试一下。在代码库中，我们在调用下一个链过滤器之前添加了日志条目，以准确查看请求中发送的内容。

### 5.2 修改响应

继续相同的案例场景，我们现在将定义一个“post”过滤器。我们的虚构服务用于检索自定义标头以指示它最终选择的语言，而不是使用传统的Content-Language标头。

因此，我们希望我们的新过滤器添加此响应标头，但前提是请求包含我们在上一节中介绍的locale标头。

```java
(exchange, chain) -> {
    return chain.filter(exchange)
        .then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
    
            Optional.ofNullable(exchange.getRequest()
                .getQueryParams()
                .getFirst("locale"))
                .ifPresent(qp -> {
                    String responseContentLanguage = response.getHeaders()
                        .getContentLanguage()
                        .getLanguage();
        
                    response.getHeaders()
                        .add("Bael-Custom-Language-Header", responseContentLanguage);
                    });
          }));
}
```

我们可以很容易地获得对响应对象的引用，并且我们不需要创建它的副本来修改它，就像请求一样。

这是链中过滤器顺序重要性的一个很好的例子；如果我们在上一节创建的过滤器之后配置此过滤器的执行，那么此处的exchange对象将包含对永远不会有任何查询参数的ServerHttpRequest的引用。

在执行所有“pre”过滤器后有效触发这一点甚至都没有关系，因为我们仍然有对原始请求的引用，这要归功于突变逻辑。

### 5.3 将请求链接到其他服务

我们假设场景的下一步是依赖第三个服务来指示我们应该使用哪个Accept-Language标头。

因此，我们将创建一个新的过滤器来调用此服务，并将其响应主体用作代理服务API的请求标头。

**在响应式环境中，这意味着链接请求以避免阻塞异步执行**。

在我们的过滤器中，我们首先向语言服务发出请求：

```java
(exchange, chain) -> {
    return WebClient.create().get()
        .uri(config.getLanguageEndpoint())
        .exchange()
        // ...
}
```

请注意，我们正在返回这个流式的操作，因为正如我们所说，我们将把调用的输出与我们的代理请求链接起来。

下一步将是提取语言-如果响应不成功，则从响应正文或从配置中提取并解析它：

```java
// ...
.flatMap(response -> {
    return (response.statusCode()
        .is2xxSuccessful()) ? response.bodyToMono(String.class) : Mono.just(config.getDefaultLanguage());
}).map(LanguageRange::parse)
// ...
```

最后，我们将像以前一样将LanguageRange值设置为请求标头，并继续过滤器链：

```java
.map(range -> {
    exchange.getRequest()
        .mutate()
        .headers(h -> h.setAcceptLanguage(range))
        .build();

    return exchange;
}).flatMap(chain::filter);
```

就是这样，现在交互将以非阻塞的方式进行。

## 6. 总结

现在我们已经学习了如何编写自定义Spring Cloud Gateway过滤器并了解了如何操作请求和响应实体，我们已经准备好充分利用这个框架。

与往常一样，所有完整的示例都可以在[GitHub上](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/spring-cloud-modules/spring-cloud-gateway-1)找到。请记住，为了测试它，我们需要通过[Maven](https://www.baeldung.com/maven)运行集成和实时测试。
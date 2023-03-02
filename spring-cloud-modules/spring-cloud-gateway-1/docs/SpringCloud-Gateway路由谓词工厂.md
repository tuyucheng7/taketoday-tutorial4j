## 1. 简介

在上一篇文章中，我们介绍了什么是[Spring Cloud Gateway](https://www.baeldung.com/spring-cloud-gateway)以及如何使用内置谓词来实现基本的路由规则。然而，有时这些内置谓词可能还不够。例如，出于某种原因，我们的路由逻辑可能需要查询数据库。

对于这些情况，**Spring Cloud Gateway允许我们定义自定义谓词**。一旦定义，我们就可以将它们用作任何其他谓词，这意味着我们可以使用流式的API和/或DSL来定义路由。

## 2. 谓词剖析

简而言之，Spring Cloud Gateway中的Predicate是一个对象，用于测试给定请求是否满足给定条件。对于每个路由，我们可以定义一个或多个谓词，如果满足这些谓词，将在应用任何过滤器后接受对配置后端的请求。

在编写我们的谓词之前，让我们看一下现有谓词的源代码，或者更准确地说，是现有PredicateFactory的代码。顾名思义，Spring Cloud Gateway使用流行的[工厂方法模式](https://en.wikipedia.org/wiki/Factory_method_pattern)作为支持以可扩展方式创建Predicate实例的机制。

我们可以选择任何一个内置谓词工厂，它们在[spring-cloud-gateway-core](https://github.com/spring-cloud/spring-cloud-gateway/tree/v2.1.3.RELEASE/spring-cloud-gateway-core)模块的[org.springframework.cloud.gateway.handler.predicate](https://github.com/spring-cloud/spring-cloud-gateway/tree/v2.1.3.RELEASE/spring-cloud-gateway-core/src/main/java/org/springframework/cloud/gateway/handler/predicate)包中可用。我们可以很容易地找出现有的，因为它们的名字都以RoutePredicateFactory结尾。[HeaderRouterPredicateFactory](https://github.com/spring-cloud/spring-cloud-gateway/blob/v2.1.3.RELEASE/spring-cloud-gateway-core/src/main/java/org/springframework/cloud/gateway/handler/predicate/HeaderRoutePredicateFactory.java)就是一个很好的例子：

```java
public class HeaderRoutePredicateFactory extends AbstractRoutePredicateFactory<HeaderRoutePredicateFactory.Config> {

    // ... setup code omitted
    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        return new GatewayPredicate() {
            @Override
            public boolean test(ServerWebExchange exchange) {
                // ... predicate logic omitted
            }
        };
    }

    @Validated
    public static class Config {
        public Config(boolean isGolden, String customerIdCookie ) {
            // ... constructor details omitted
        }
        // ... getters/setters omitted
    }
}
```

在实现中我们可以观察到几个关键点：

-   它扩展了AbstractRoutePredicateFactory<T\>，后者又实现了网关使用的RoutePredicateFactory接口
-   apply方法返回实际Predicate的实例-在本例中为GatewayPredicate
-   谓词定义了一个内部的Config类，用于存放测试逻辑使用的静态配置参数

如果我们看一下其他可用的PredicateFactory，我们会发现基本模式基本相同：

1.  定义一个Config类来保存配置参数
2.  扩展AbstractRoutePredicateFactory，使用配置类作为其模板参数
3.  覆盖apply方法，返回一个实现所需测试逻辑的Predicate

## 3. 实现自定义谓词工厂

对于我们的实现，让我们假设以下场景：对于给定的API，调用我们必须在两个可能的后端之间进行选择。“Golden”客户是我们最有价值的客户，他们应该被路由到功能强大的服务器，可以访问更多内存、更多CPU和快速磁盘。非Golden客户会使用功能较弱的服务器，这会导致响应时间变慢。

要确定请求是否来自黄金客户，我们需要调用一个服务，该服务获取与请求关联的customerId并返回其状态。至于customerId，在我们的简单场景中，我们假设它在cookie中可用。

有了所有这些信息，我们现在可以编写自定义谓词。我们将保留现有的命名约定并将我们的类命名为GoldenCustomerRoutePredicateFactory：

```java
public class GoldenCustomerRoutePredicateFactory extends AbstractRoutePredicateFactory<GoldenCustomerRoutePredicateFactory.Config> {

    private final GoldenCustomerService goldenCustomerService;

    // ... constructor omitted

    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        return (ServerWebExchange t) -> {
            List<HttpCookie> cookies = t.getRequest()
                  .getCookies()
                  .get(config.getCustomerIdCookie());

            boolean isGolden;
            if ( cookies == null || cookies.isEmpty()) {
                isGolden = false;
            } else {
                String customerId = cookies.get(0).getValue();
                isGolden = goldenCustomerService.isGoldenCustomer(customerId);
            }
            return config.isGolden() ? isGolden : !isGolden;
        };
    }

    @Validated
    public static class Config {
        boolean isGolden = true;
        @NotEmpty
        String customerIdCookie = "customerId";
        // ...constructors and mutators omitted   
    }
}
```

如我们所见，实现非常简单。我们的apply方法返回一个lambda，该lambda使用传递给它的ServerWebExchange实现所需的逻辑。首先，它检查customerId cookie是否存在。如果找不到，那么这是一个普通客户。否则，我们使用cookie值调用isGoldenCustomer服务方法。

接下来，我们结合客户端的类型和配置的isGolden参数来确定返回值。**这允许我们使用相同的谓词来创建前面描述的两个路由，只需更改isGolden参数的值即可**。

## 4. 注册自定义谓词工厂

一旦我们编写了自定义谓词工厂，我们就需要一种方法让Spring Cloud Gateway知道它。由于我们使用的是Spring，因此这是以通常的方式完成的：我们声明一个GoldenCustomerRoutePredicateFactory类型的bean。

由于我们的类型通过基类实现了RoutePredicateFactory，因此它将在上下文初始化时由Spring选择并提供给Spring Cloud Gateway。

在这里，我们将使用@Configuration类创建我们的bean：

```java
@Configuration
public class CustomPredicatesConfig {
    @Bean
    public GoldenCustomerRoutePredicateFactory goldenCustomer(GoldenCustomerService goldenCustomerService) {
        return new GoldenCustomerRoutePredicateFactory(goldenCustomerService);
    }
}
```

我们假设我们在Spring的上下文中有一个合适的GoldenCustomerService实现。在我们的例子中，我们只有一个虚拟实现，它将customerId值与固定值进行比较-虽然不现实，但对于演示目的很有用。

## 5. 使用自定义谓词

现在我们已经实现了“黄金客户”谓词并可用于Spring Cloud Gateway，我们可以开始使用它来定义路由。首先，我们将使用流式的API来定义路由，然后我们将使用YAML以声明方式执行此操作。

### 5.1 使用流式API定义路由

当我们必须以编程方式创建复杂对象时，[流式API](https://www.baeldung.com/mockito-fluent-apis)是一种流行的设计选择。在我们的例子中，我们在@Bean中定义路由，它使用RouteLocatorBuilder和我们的自定义谓词工厂创建RouteLocator对象：

```java
@Bean
public RouteLocator routes(RouteLocatorBuilder builder, GoldenCustomerRoutePredicateFactory gf ) {
    return builder.routes()
        .route("golden_route", r -> r.path("/api/**")
            .uri("https://fastserver")
            .predicate(gf.apply(new Config(true, "customerId"))))
        .route("common_route", r -> r.path("/api/**")
            .uri("https://slowserver")
            .predicate(gf.apply(new Config(false, "customerId"))))                
        .build();
}
```

请注意我们如何在每个路由中使用两个不同的Config配置。在第一种情况下，第一个参数为true，因此当我们收到来自黄金客户的请求时，谓词的计算结果也为true。至于第二个路由，我们在构造函数中传递false，因此我们的谓词将为非黄金客户返回true。

### 5.2 在YAML中定义路由

我们可以使用properties或yaml文件以声明方式实现与以前相同的结果。在这里，我们将使用yaml，因为它更容易阅读：

```yaml
spring:
    cloud:
        gateway:
            routes:
                -   id: golden_route
                    uri: https://fastserver
                    predicates:
                        - Path=/api/**
                        - GoldenCustomer=true
                -   id: common_route
                    uri: https://slowserver
                    predicates:
                        - Path=/api/**
                        -   name: GoldenCustomer
                            args:
                                golden: false
                                customerIdCookie: customerId

```

在这里，我们定义了与以前相同的路由，使用两个可用选项来定义谓词。第一个是golden_route，它使用形式为Predicate=[param[,param]+]的紧凑表示。这里的Predicate是谓词的名称，它是通过去除RoutePredicateFactory后缀从工厂类名称自动派生的。在“=”符号之后，我们有用于填充关联Config实例的参数。

当我们的谓词只需要简单的值时，这种紧凑的语法很好，但情况可能并非总是如此。对于这些场景，我们可以使用第二个路由中描述的长格式。在这种情况下，我们提供一个具有两个属性的对象：name和args。name包含谓词名称，args用于填充Config实例。由于这次args是一个对象，我们的配置可以根据需要进行复杂的配置。

## 6. 测试

现在，让我们使用curl来测试我们的网关，检查一切是否按预期工作。对于这些测试，我们已经像之前显示的那样设置了我们的路由，但我们将使用公开可用的[httpbin.org](https://httpbin.org/)服务作为我们的虚拟后端。这是一项非常有用的服务，我们可以使用它来快速检查我们的规则是否按预期工作，既可以在线使用，也可以作为我们可以在本地使用的Docker镜像使用。

我们的测试配置还包括标准的AddRequestHeader过滤器。我们使用它向请求添加一个自定义的Goldencustomer标头，其值对应于谓词结果。我们还添加了一个StripPrefix过滤器，因为我们希望在调用后端之前从请求URI中删除/api。

首先，让我们测试一下“普通客户端”场景。在我们的网关启动并运行后，我们使用curl调用httpbin的headers API，它将简单地回显所有接收到的标头：

```shell
$ curl http://localhost:8080/api/headers
{
    "headers": {
        "Accept": "*/*",
        "Forwarded": "proto=http;host=\"localhost:8080\";for=\"127.0.0.1:51547\"",
        "Goldencustomer": "false",
        "Host": "httpbin.org",
        "User-Agent": "curl/7.55.1",
        "X-Forwarded-Host": "localhost:8080",
        "X-Forwarded-Prefix": "/api"
    }
}
```

正如预期的那样，我们看到Goldencustomer标头是使用false值发送的。让我们现在试试“Golden”客户：

```shell
$ curl -b customerId=tuyucheng http://localhost:8080/api/headers
{
    "headers": {
        "Accept": "*/*",
        "Cookie": "customerId=tuyucheng",
        "Forwarded": "proto=http;host=\"localhost:8080\";for=\"127.0.0.1:51651\"",
        "Goldencustomer": "true",
        "Host": "httpbin.org",
        "User-Agent": "curl/7.55.1",
        "X-Forwarded-Host": "localhost:8080",
        "X-Forwarded-Prefix": "/api"
    }
}
```

这一次，Goldencustomer为true，因为我们发送了一个customerId cookie，其值被我们的虚拟服务识别为对黄金客户有效。

## 7. 总结

在本文中，我们介绍了如何将自定义谓词工厂添加到Spring Cloud Gateway并使用它们来定义使用任意逻辑的路由。
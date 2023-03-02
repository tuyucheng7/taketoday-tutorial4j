## 1. 简介

在本快速教程中，我们将了解如何根据[Spring Cloud Gateway](https://www.baeldung.com/spring-cloud-gateway)的客户端实际IP地址限制传入请求的速率。

简而言之，我们将在路由上设置RequestRateLimiter过滤器，然后**将网关配置为使用IP地址来限制唯一客户端的请求**。

## 2. 路由配置

首先，我们需要配置Spring Cloud Gateway以对特定路由进行速率限制。为此，我们将使用由[spring-boot-starter-data-redis-reactive](https://www.baeldung.com/spring-data-redis-reactive)实现的经典[令牌桶](https://en.wikipedia.org/wiki/Token_bucket)速率限制器。简而言之，**速率限制器创建一个存储桶，其中包含一个用于标识自身的关联键和一个固定的令牌初始容量，随着时间的推移会得到补充**。然后，对于每个请求，速率限制器会检查其相关存储桶并在可能的情况下减少令牌。否则，它拒绝传入的请求。

当我们使用分布式系统时，我们可能希望跟踪跨应用程序所有实例的所有传入请求。因此，拥有分布式缓存系统可以方便地存储桶的信息。在这种情况下，我们[预先配置了一个Redis实例](https://www.baeldung.com/spring-data-redis-properties)来模拟真实世界的应用程序。

接下来，我们将配置一个带有速率限制器的路由。我们将监听/example端点并将请求转发到http://example.org：

```java
@Bean
public RouteLocator myRoutes(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("requestratelimiter_route", p -> p
            .path("/example")
            .filters(f -> f.requestRateLimiter(r -> r.setRateLimiter(redisRateLimiter())))
            .uri("http://example.org"))
        .build();
}
```

上面，我们使用.setRateLimiter()方法使用RequestRateLimiter配置路由。特别是，我们通过方法redisRatelimiter()定义RedisRateLimiter bean来管理我们的速率限制器的状态：

```java
@Bean
public RedisRateLimiter redisRateLimiter() {
    return new RedisRateLimiter(1, 1, 1);
}
```

作为示例，我们将所有replenishRate、burstCapacity和requestedToken属性设置为1来配置速率限制。这使得多次调用/example端点并取回HTTP 429响应代码变得容易。

## 3. KeyResolver Bean

为了正常工作，**速率限制器必须识别每个通过密钥访问端点的客户端**。在下方，密钥标识了速率限制器将用于为每个请求消耗令牌的存储桶。因此，我们希望每个客户端的密钥都是唯一的。在这种情况下，我们将使用客户端的IP地址来监控他们的请求，并在他们发出过多请求时限制他们。

因此，我们之前配置的RequestRateLimiter将使用一个KeyResolver bean，该bean允许可插拔策略派生用于限制请求的密钥。**这意味着我们可以配置如何从每个请求中提取密钥**。

## 4. KeyResolver中客户端的IP地址

目前，这个接口没有默认实现，所以我们必须定义一个，请记住我们需要客户端的IP地址：

```java
@Component
public class SimpleClientAddressResolver implements KeyResolver {
    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        return Optional.ofNullable(exchange.getRequest().getRemoteAddress())
              .map(InetSocketAddress::getAddress)
              .map(InetAddress::getHostAddress)
              .map(Mono::just)
              .orElse(Mono.empty());
    }
}
```

我们正在使用ServerWebExchange对象来提取客户端的IP地址。如果我们无法获取IP地址，我们将返回Mono.empty()以向速率限制器发出信号并默认拒绝该请求。但是，我们可以通过将.setDenyEmptyKey()设置为false将速率限制器配置为在KeyResolver返回空密钥时允许请求。此外，我们还可以通过为.setKeyResolver()方法提供自定义KeyResolver实现来为每个不同的路由设置不同的KeyResolver：

```java
builder.routes()
    .route("ipaddress_route", p -> p
        .path("/example2")
        .filters(f -> f.requestRateLimiter(r -> r.setRateLimiter(redisRateLimiter())
            .setDenyEmptyKey(false)
            .setKeyResolver(new SimpleClientAddressResolver())))
        .uri("http://example.org"))
.build();
```

### 4.1 在代理后面时的原始IP地址

如果Spring Cloud Gateway直接监听客户端的请求，则先前定义的实现会起作用。但是，如果我们将应用程序部署在代理之后，所有主机地址都将相同。因此，速率限制器会将所有请求视为来自同一客户端，并限制它可以处理的请求数。

为了解决这个问题，**我们依靠X-Forwarded-For标头来识别通过代理服务器连接的客户端的原始IP地址**。例如，让我们配置KeyResolver以便它可以读取原始IP地址：

```java
@Primary
@Component
public class ProxiedClientAddressResolver implements KeyResolver {
    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        XForwardedRemoteAddressResolver resolver = XForwardedRemoteAddressResolver.maxTrustedIndex(1);
        InetSocketAddress inetSocketAddress = resolver.resolve(exchange);
        return Mono.just(inetSocketAddress.getAddress().getHostAddress());
    }
}
```

我们将值1传递给maxTrustedIndex()，假设我们只有一个代理服务器。否则，必须相应地设置该值。此外，我们使用[@Primary](https://www.baeldung.com/spring-primary)标注此KeyResolver以使其优先于先前的实现。

## 5. 总结

在本文中，我们根据客户端的IP地址配置了一个API速率限制器。首先，我们配置了一个带有令牌桶速率限制器的路由。然后，我们探讨了KeyResolver如何识别用于每个请求的存储桶。最后，我们探索了在直接访问我们的API或部署在代理后面时通过KeyResolver分配客户端IP地址的策略。
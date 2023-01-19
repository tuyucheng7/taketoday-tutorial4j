## 1. 概述

在本教程中，我们演示了如何使用 Kotlin 编程语言使用 Spring WebFlux 模块。

我们将说明如何使用基于注解和基于 lambda 的样式方法来定义端点。

## 2. Spring WebFlux 和 Kotlin

Spring 5 的发布引入了两大新特性，其中包括对反应式编程范式的原生支持和使用 Kotlin 编程语言的可能性。

在本教程中，我们假设我们已经配置了环境(请参阅[我们关于此问题的教程之一](https://www.baeldung.com/spring-boot-kotlin))并了解 Kotlin 语言语法(关于该主题的[另一篇教程)。](https://www.baeldung.com/kotlin)

## 3. 基于注解的方法

WebFlux 允许我们使用 SpringMVC 框架注解(如@RequestMapping或@PathVariable)或便利注解(如@RestController或@GetMapping)定义应以众所周知的方式处理传入请求的端点。

尽管注解名称相同，但 WebFlux 的注解名称 使方法成为 非阻塞的。

例如，此端点：

```java
@GetMapping(path = ["/numbers"],
  produces = [MediaType.APPLICATION_STREAM_JSON_VALUE])
@ResponseBody
fun getNumbers() = Flux.range(1, 100)

```

产生一些第一个整数的流。如果服务器在localhost:8080上运行，则通过以下命令连接到它：

```bash
curl localhost:8080/stream

```

将打印出请求的数字。

## 4. 基于 Lambda 的方法

一种定义端点的新方法是使用 lambda 表达式，自 Java 1.8 版以来就出现了。在 Kotlin 的帮助下，lambda 表达式甚至可以用于早期的 Java 版本。

在 WebFlux 中，路由器功能是由 RequestPredicate (换句话说，谁应该管理请求)和HandlerFunction(换句话说，应该如何详细说明请求)确定的功能。

处理函数接受一个 ServerRequest 实例并生成一个Mono<ServerResponse>实例。

在 Kotlin 中， 如果最后一个函数参数是一个 lambda 表达式，它可以放在括号外。

这样的语法允许我们突出请求的谓词和处理函数之间的分离

```java
router {
    GET("/route") { _ -> ServerResponse.ok().body(fromObject(arrayOf(1, 2, 3))) }
}

```

用于路由器功能。

该函数具有清晰的人类可读格式：一旦 GET 类型的请求到达 /route，然后构造一个响应(忽略请求的内容——因此符号下划线)，HTTP 状态为 OK，主体由给定的对象。

现在，为了让它在 WebFlux 中工作，我们应该将路由器功能放在一个类中：

```java
@Configuration
class SimpleRoute {
 
    @Bean
    fun route() = router {
        GET("/route") { _ -> ServerResponse.ok().body(fromObject(arrayOf(1, 2, 3))) }
    }
}

```

通常，我们的应用程序逻辑要求我们构建更复杂的路由器功能。

在 WebFlux 中，Kotlin 的路由函数 DSL通过扩展函数定义了多种函数，如accept、 and、 or、 nest、 invoke、 GET、 POST ，这些函数允许我们构建复合路由函数：

```java
router {
    accept(TEXT_HTML).nest {
        (GET("/device/") or GET("/devices/")).invoke(handler::getAllDevices)
    }
}

```

处理程序 变量应该是一个类的实例，该类实现了 具有标准HandlerFunction签名的方法getAllDevices() ：

```java
fun getAllDevices(request: ServerRequest): Mono<ServerResponse>

```

正如我们上面提到的。

为了保持适当的关注点分离，我们可以将不相关的路由器功能的定义放在单独的类中：

```java
@Configuration
class HomeSensorsRouters(private val handler: HomeSensorsHandler) {
    @Bean
    fun roomsRouter() = router {
        (accept(TEXT_HTML) and "/room").nest {
            GET("/light", handler::getLightReading)
            POST("/light", handler::setLight)
        }
    }
    // eventual other router function definitions
}

```

我们可以通过String值方法 pathVariable()访问路径变量：

```java
val id = request.pathVariable("id")
```

而对ServerRequest主体的访问是通过方法bodyToMono和bodyToFlux 实现的，即：

```java
val device: Mono<Device> = request
  .bodyToMono(Device::class.java)

```

## 5. 测试

为了测试路由器功能，我们应该构建一个WebTestClient实例，我们要测试的路由器SimpleRoute().route()：

```java
var client = WebTestClient.bindToRouterFunction(SimpleRoute().route()).build()

```

现在我们准备测试路由器的处理函数是否返回状态 OK：

```java
client.get()
  .uri("/route")
  .exchange()
  .expectStatus()
  .isOk

```

WebTestClient 接口定义了允许我们测试所有 HTTP 请求方法(例如GET、POST和PUT )的方法，即使不运行服务器。 

为了测试响应主体的内容，我们可能要使用json() 方法：

```java
client.get()
  .uri("/route")
  .exchange()
  .expectBody()
  .json("[1, 2, 3]")

```

## 六，总结

在本文中，我们演示了如何使用 Kotlin 使用 WebFlux 框架的基本功能。

我们在定义端点时简要提到了众所周知的基于注解的方法，并花更多时间来说明如何在基于 lambda 的样式方法中使用路由器函数来定义它们。
## 1. 概述

从[Spring 6和Spring Boot 3](https://www.baeldung.com/spring-boot-3-spring-6-new)开始，类似于[OpenFeign](https://github.com/OpenFeign/feign)和[Retrofit](https://howtodoinjava.com/retrofit2/retrofit2-beginner-tutorial/)等其他声明式客户端，**Spring框架支持创建RSocket服务作为Java接口，并带有用于[RSocket](https://howtodoinjava.com/spring-boot/rsocket-tutorial/)交换的注解方法**。

在本教程中，我们将使用@RSocketExchange为RSocket协议创建一个声明式请求客户端。

声明式HTTP接口是一个Java接口，有助于减少样板代码，生成实现此接口的代理，并在框架级别执行交换。

## 2. Maven依赖

我们需要包含最新版本的[spring-boot-starter-rsocket](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-rsocket)依赖项以包含所有必要的类和接口。

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-rsocket</artifactId>
</dependency>
```

## 3. 使用@RSocketExchange

@RSocketExchange注解用于将RSocket服务接口上的方法声明为RSocket端点。它接收定义端点路由的“value”参数。与用于HTTP传输的@RequestMapping类似，**@RSocketExchange可以在接口级别使用来表示由所有服务方法继承的公共路由**。

```java
public interface MessageService {
    @RSocketExchange("message")
    Mono<String> sendMessage(Mono<String> requestObject);
}
```

服务方法可以接受以下方法参数：

-   **@DestinationVariable**：添加路由变量以将模板占位符扩展到路由中。
-   **@Payload**：一个可选的注解，用于设置请求的输入负载。
-   **Object**：后跟MimeType：在输入负载及其媒体类型中发送额外的元数据条目。

```java
public interface MessageService {
    @RSocketExchange("greeting/{name}")
    Mono<String> sendMessage(@DestinationVariable("name") String name, @Payload Mono<String> greetingMono);
}
```

**在底层，Spring生成一个实现MessageService接口的代理，并使用底层的RSocketRequester执行交换**。

## 4. 生成服务代理

正如我们所知，Spring Boot自动配置会自动为我们配置RSocketRequester.Builder。我们可以使用构建器来创建RSocketRequester。

```java
@Autowired
RSocketRequester.Builder requesterBuilder;

// in any method
RSocketRequester rsocketRequester = requesterBuilder.tcp("localhost", 7000);
```

最后，我们可以使用RSocketRequester来初始化一个RSocketServiceProxyFactory，该工厂最终用于使用@RSocketExchange方法为任何RSocket服务接口创建客户端代理。

```java
RSocketServiceProxyFactory factory = RSocketServiceProxyFactory.builder(rsocketRequester).build();
MessageService service = factory.createClient(MessageService.class);
```

最后，我们可以使用创建的服务代理来调用交换方法。让我们看一个完整的例子：

```java
@SpringBootApplication
@Slf4j
public class App implements CommandLineRunner {
    
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
    
    @Autowired
    RSocketRequester.Builder requesterBuilder;
    
    @Override
    public void run(String... args) throws Exception {
        RSocketRequester rsocketRequester = requesterBuilder.tcp("localhost", 7000);
        RSocketServiceProxyFactory factory = RSocketServiceProxyFactory.builder(rsocketRequester).build();
        
        MessageService service = factory.createClient(MessageService.class);
        
        Mono<String> response = service.sendMessage("Tuyucheng", Mono.just("Hello there!"));
        response.subscribe(message -> log.info("RSocket response : {}", message));
    }
}
```

控制台中的程序输出：

```shell
2023-05-02T16:42:50.012+08:00  INFO 604 --- [ctor-http-nio-2] c.t.t.r.controller.MessageController     : Received a greeting from Tuyucheng : Hello there!
2023-05-02T16:42:50.017+08:00  INFO 604 --- [actor-tcp-nio-2] c.t.t.r.RSocketExchangeApp               : RSocket response : Hello Tuyucheng!
```

## 5. 总结

在这个简短的Spring教程中，我们了解了Spring 6中引入的一项新功能-即使用@RSocketExchange注解创建声明式RSocket客户端。

我们学习了如何创建和实例化服务代理，并使用它通过TCP协议连接到远程端点。
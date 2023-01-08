## 1. 什么是Micronaut

[Micronaut](http://micronaut.io/)是一个基于JVM的框架，用于构建轻量级、模块化的应用程序。由创建Grails的同一家公司OCI开发，**Micronaut是最新的框架，旨在使创建微服务变得快速和容易**。

虽然Micronaut包含一些类似于Spring等现有框架的功能，但它也有一些新功能使其与众不同。由于支持Java、Groovy和Kotlin，它提供了多种创建应用程序的方法。

## 2. 主要特点

Micronaut最令人兴奋的特性之一是它的编译时依赖注入机制，大多数框架使用反射和代理在运行时执行依赖注入。然而，**Micronaut在编译时构建其依赖注入数据，结果是更快的应用程序启动和更小的内存占用**。

**另一个特性是它对客户端和服务器的响应式编程的一流支持，由于同时支持RxJava和Project Reactor**，因此开发人员可以选择特定的响应式实现。

Micronaut还具有多项功能，使其成为开发云原生应用程序的优秀框架。**支持Eureka、Consul等多种服务发现工具，也支持Zipkin、Jaeger等不同的分布式追踪系统**。

它还支持创建AWS Lambda函数，从而可以轻松创建无服务器应用程序。

## 3. 入门

最简单的入门方法是使用[SDKMAN](https://sdkman.io/install)：

```bash
> sdk install micronaut 1.0.0.RC2
```

这将安装我们构建、测试和部署Micronaut应用程序所需的所有二进制文件。它还提供了Micronaut CLI工具，可以让我们轻松启动新项目。

二进制工件也可在[Sonatype](https://oss.sonatype.org/content/groups/public/io/micronaut/)和[GitHub上](https://github.com/micronaut-projects/micronaut-core/releases)找到。

在以下部分中，我们将了解该框架的一些功能。

## 4. 依赖注入

如前所述，Micronaut在编译时处理依赖注入，这与大多数IoC容器不同。

但是，它仍然**完全支持JSR-330注解**，因此使用bean类似于其他IoC框架。

为了自动将bean注入到我们的代码中，我们使用@Inject：

```java
@Inject
private EmployeeService service;
```

@Inject注解的工作方式与@Autowired类似，可以用在字段、方法、构造函数和参数上。

默认情况下，所有bean都被限定为原型，我们可以使用@Singleton快速创建单例bean。如果多个类实现相同的bean接口，则可以使用@Primary来消除它们的冲突：

```java
@Primary
@Singleton
public class BlueCar implements Car {}
```

当bean是可选的时，可以使用@Requires注解，或者仅在满足某些条件时才执行自动装配。

在这方面，它的行为很像Spring Boot的@Conditional注解：

```java
@Singleton
@Requires(beans = DataSource.class)
@Requires(property = "enabled")
@Requires(missingBeans = EmployeeService)
@Requires(sdk = Sdk.JAVA, value = "1.8")
public class JdbcEmployeeService implements EmployeeService {}
```

## 5. 构建HTTP服务器

现在，让我们看一下如何创建一个简单的HTTP服务器应用程序。首先，我们将使用SDKMAN创建一个项目：

```bash
> mn create-app hello-world-server -build maven
```

这将在名为hello-world-server的目录中使用Maven创建一个新的Java项目。在此目录中，我们将找到我们的主要应用程序源代码、Maven POM文件和项目的其他支持文件。

非常简单的默认应用程序：

```java
public class ServerApplication {
    public static void main(String[] args) {
        Micronaut.run(ServerApplication.class);
    }
}
```

### 5.1 阻塞HTTP

就其本身而言，此应用程序不会做太多事情。让我们添加一个具有两个端点的控制器，两者都将返回问候语，但一个将使用GET HTTP动词，另一个将使用POST：

```java
@Controller("/greet")
public class GreetController {

    @Inject
    private GreetingService greetingService;

    @Get("/{name}")
    public String greet(String name) {
        return greetingService.getGreeting() + name;
    }

    @Post(value = "/{name}", consumes = MediaType.TEXT_PLAIN)
    public String setGreeting(@Body String name) {
        return greetingService.getGreeting() + name;
    }
}
```

### 5.2 响应式IO

默认情况下，Micronaut将使用传统的阻塞I/O来实现这些端点。但是，**我们可以通过仅将返回类型更改为任何响应式非阻塞类型来快速实现非阻塞端点**。

例如，对于RxJava，我们可以使用Observable。同样，在使用Reactor时，我们可以返回Mono或Flux数据类型：

```java
@Get("/{name}")
public Mono<String> greet(String name) {
    return Mono.just(greetingService.getGreeting() + name);
}
```

对于阻塞端点和非阻塞端点，Netty是用于处理HTTP请求的底层服务器。

通常，请求在启动时创建的主I/O线程池中处理，使它们阻塞。

但是，当从控制器端点返回非阻塞数据类型时，Micronaut使用Netty事件循环线程，使整个请求成为非阻塞的。

## 6. 构建HTTP客户端

现在，让我们构建一个客户端来使用我们刚刚创建的端点。Micronaut提供了两种创建HTTP客户端的方法：

- 声明式HTTP客户端
- 编程式HTTP客户端

### 6.1 声明式HTTP客户端

第一种也是最快的创建方法是使用声明式方法：

```java
@Client("/greet")
public interface GreetingClient {
    @Get("/{name}")
    String greet(String name);
}
```

请注意，**我们没有实现任何代码来调用我们的服务**。相反，Micronaut了解如何从我们提供的方法签名和注解中调用服务。

为了测试这个客户端，我们可以创建一个JUnit测试，它使用EmbeddedServer API来运行我们服务器的嵌入式实例：

```java
public class GreetingClientTest {
    private EmbeddedServer server;
    private GreetingClient client;

    @Before
    public void setup() {
        server = ApplicationContext.run(EmbeddedServer.class);
        client = server.getApplicationContext().getBean(GreetingClient.class);
    }

    @After
    public void cleanup() {
        server.stop();
    }

    @Test
    public void testGreeting() {
        assertEquals(client.greet("Mike"), "Hello Mike");
    }
}
```

### 6.2 编程式HTTP客户端

如果我们需要更多地控制其行为和实现，我们还可以选择编写更传统的客户端：

```java
@Singleton
public class ConcreteGreetingClient {
    private RxHttpClient httpClient;

    public ConcreteGreetingClient(@Client("/") RxHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public String greet(String name) {
        HttpRequest<String> req = HttpRequest.GET("/greet/" + name);
        return httpClient.retrieve(req).blockingFirst();
    }

    public Single<String> greetAsync(String name) {
        HttpRequest<String> req = HttpRequest.GET("/async/greet/" + name);
        return httpClient.retrieve(req).first("An error as occurred");
    }
}
```

默认的HTTP客户端使用RxJava，因此可以轻松处理阻塞或非阻塞调用。

## 7. Micronaut CLI

当我们使用Micronaut CLI工具创建示例项目时，我们已经在上面看到了它的运行情况。

在我们的例子中，我们创建了一个独立的应用程序，但它还有其他一些功能。

### 7.1 联合项目

**在Micronaut中，联合只是一组位于同一目录下的独立应用程序**。通过使用联合，我们可以轻松地将它们一起管理，并确保它们获得相同的默认值和设置。

当我们使用CLI工具生成联合时，它采用与create-app命令相同的所有参数。它将创建一个顶级项目结构，并且每个独立应用程序都将在其子目录中从那里创建。

### 7.2 功能

**在创建独立应用程序或联合应用程序时，我们可以决定我们的应用程序需要哪些功能**，这有助于确保项目中包含最少的依赖项。

我们使用-features参数指定功能并提供以逗号分隔的功能名称列表。

我们可以通过运行以下命令找到可用功能的列表：

```bash
> mn profile-info service

Provided Features:
--------------------
* annotation-api - Adds Java annotation API
* config-consul - Adds support for Distributed Configuration with Consul
* discovery-consul - Adds support for Service Discovery with Consul
* discovery-eureka - Adds support for Service Discovery with Eureka
* groovy - Creates a Groovy application
[...] More features available
```

### 7.3 现有项目

**我们还可以使用CLI工具来修改现有项目**，使我们能够创建bean、客户端、控制器等。当我们从现有项目中运行mn命令时，我们将有一组新的命令可用：

```bash
> mn help
| Command Name         Command Description
-----------------------------------------------
create-bean            Creates a singleton bean
create-client          Creates a client interface
create-controller      Creates a controller and associated test
create-job             Creates a job with scheduled method
```

## 8. 总结

在对Micronaut的简要介绍中，我们已经看到构建阻塞和非阻塞HTTP服务器和客户端是多么容易。此外，我们还探讨了其CLI的一些功能。

但这只是它提供的功能的一小部分，还完全支持无服务器功能、服务发现、分布式跟踪、监控和指标、分布式配置等等。

虽然它的许多功能都源自现有的框架，例如Grails和Spring，但它也有许多独特的功能，可以帮助它脱颖而出。
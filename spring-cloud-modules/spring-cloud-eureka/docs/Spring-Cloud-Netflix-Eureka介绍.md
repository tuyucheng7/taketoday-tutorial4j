## 1. 概述

在本教程中，我们将通过“Spring Cloud Netflix Eureka”介绍客户端服务发现。

**客户端服务发现允许服务相互查找和通信，而无需对主机名和端口进行硬编码**。这种架构中唯一的“固定点”是服务注册中心，每个服务都必须注册到该服务注册中心。

一个缺点是所有客户端都必须实现特定的逻辑才能与这个固定点交互。这假设在实际请求之前有一个额外的网络往返。

使用Netflix Eureka，每个客户端都可以同时充当服务器，将其状态复制到连接的对等点。换句话说，客户端检索服务注册中心中所有已连接对等点的列表，并通过负载均衡算法向其他服务发出所有进一步请求。

要获知客户端的存在，他们必须向注册中心发送心跳信号。

为了实现本教程的目标，我们将实现三个微服务：

-   服务注册中心(**Eureka服务器**)
-   一个REST服务，它在注册中心中注册自己(**Eureka客户端**)
-   一个Web应用程序，它使用REST服务作为注册感知客户端(Spring Cloud Netflix **Feign Client**)

### 延伸阅读

### [Spring Cloud Netflix指南-Hystrix](https://www.baeldung.com/spring-cloud-netflix-hystrix)

本文展示了如何使用Spring Cloud Hystrix在应用程序逻辑中设置回退。

[阅读更多](https://www.baeldung.com/spring-cloud-netflix-hystrix)→

### [使用Zuul代理的Spring REST](https://www.baeldung.com/spring-rest-with-zuul-proxy)

探索将Zuul代理用于Spring REST API，解决CORS和浏览器的同源策略约束。

[阅读更多](https://www.baeldung.com/spring-rest-with-zuul-proxy)→

## 2. Eureka Server

为服务注册中心实现Eureka Server非常简单：

1.  将[spring-cloud-starter-netflix-eureka-server](https://search.maven.org/search?q=spring-cloud-starter-netflix-eureka-server)添加到依赖项中
2.  通过使用@EnableEurekaServer注解在[@SpringBootApplication](https://www.baeldung.com/spring-boot-application-configuration)中启用Eureka Server
3.  配置一些属性

让我们一步一步来。

首先，我们将创建一个新的Maven项目并将依赖项放入其中。请注意，我们将[spring-cloud-starter-parent](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-starter-parent/2022.0.1)导入到本教程中描述的所有项目中：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-parent</artifactId>
            <version>Greenwich.RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

我们可以在[Spring的项目文档](https://spring.io/projects/spring-cloud#learn)中查看最新的Spring Cloud版本。

然后我们将创建主应用程序类：

```java
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(EurekaServerApplication.class, args);
	}
}
```

最后，我们将以YAML格式配置属性，因此application.yml将是我们的配置文件：

```yaml
server:
    port: 8761
eureka:
    client:
        registerWithEureka: false
        fetchRegistry: false
```

**这里我们配置一个应用程序端口；Eureka服务器的默认值是8761**。我们告诉内置的Eureka客户端不要向自身注册，因为我们的应用程序应该充当服务器。

现在我们将浏览器指向[http://localhost:8761](http://localhost:8761/)以查看Eureka仪表板，稍后我们将在其中检查已注册的实例。

目前，我们可以看到基本的指标，比如状态和健康指标：

<img src="../assets/img.png">

## 3. Eureka Client

为了使@SpringBootApplication具有发现意识，我们必须在我们的类路径中包含一个Spring Discovery Client(例如，[spring-cloud-starter-netflix-eureka-client](https://search.maven.org/search?q=spring-cloud-starter-netflix-eureka-client))。

然后我们需要使用@EnableDiscoveryClient或@EnableEurekaClient标注@Configuration。请注意，如果我们在类路径上有spring-cloud-starter-netflix-eureka-client依赖项，则此注解是可选的。

后者明确告诉Spring Boot使用Spring Netflix Eureka进行服务发现。为了让我们的客户端应用程序充满活力，我们还将在pom.xml中包含[spring-boot-starter-web](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-web/3.0.3)依赖并实现一个REST控制器。

但首先，我们将添加依赖项。同样，我们可以将它留给spring-cloud-starter-parent依赖项来为我们找出工件版本：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-starter</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

在这里我们将实现主应用程序类：

```java
@SpringBootApplication
@RestController
public class EurekaClientApplication implements GreetingController {

	@Autowired
	@Lazy
	private EurekaClient eurekaClient;

	@Value("${spring.application.name}")
	private String appName;

	public static void main(String[] args) {
		SpringApplication.run(EurekaClientApplication.class, args);
	}

	@Override
	public String greeting() {
		return String.format(
			"Hello from '%s'!", eurekaClient.getApplication(appName).getName());
	}
}
```

和GreetingController接口：

```java
public interface GreetingController {
	@RequestMapping("/greeting")
	String greeting();
}
```

除了接口，我们还可以简单地在EurekaClientApplication类中声明映射。如果我们想在服务器和客户端之间共享该接口，则该接口可能会很有用。

接下来，我们必须使用已配置的Spring应用程序名称设置application.yml，以在已注册应用程序列表中唯一标识我们的客户端。

**我们可以让Spring Boot为我们选择一个随机端口，因为稍后我们将使用它的名称访问该服务**。 

最后，我们必须告诉我们的客户端它必须在哪里找到注册中心：

```yaml
spring:
    application:
        name: spring-cloud-eureka-client
server:
    port: 0
eureka:
    client:
        serviceUrl:
            defaultZone: ${EUREKA_URI:http://localhost:8761/eureka}
    instance:
        preferIpAddress: true
```

我们决定以这种方式设置我们的Eureka客户端，因为这种服务以后应该很容易扩展。

现在我们将运行客户端，并再次将浏览器指向[http://localhost:8761](https://localhost:8761/)以查看其在Eureka仪表板上的注册状态。通过使用仪表板，我们可以进行进一步的配置，例如将注册客户端的主页与仪表板链接起来以进行管理。但是，配置选项超出了本文的范围：

<img src="../assets/img_1.png">

## 4. Feign Client

为了使用三个相关的微服务完成我们的项目，我们现在将使用Spring Netflix Feign Client实现一个使用REST的Web应用程序。

**将Feign视为一个发现感知的[Spring RestTemplate](https://www.baeldung.com/rest-template)，它使用接口与端点进行通信。这些接口将在运行时自动实现，而不是service-urls，它使用service-names**。

如果没有Feign，我们将不得不将EurekaClient的一个实例自动注入到我们的控制器中，通过它我们可以通过service-name作为Application对象接收服务信息。

我们将使用此Application获取此服务的所有实例的列表，选择一个合适的实例，然后使用此InstanceInfo获取主机名和端口。有了这个，我们可以对任何http客户端执行标准请求：

```java
@Autowired
private EurekaClient eurekaClient;

@RequestMapping("/get-greeting-no-feign")
public String greeting(Model model) {

    InstanceInfo service = eurekaClient
      	.getApplication(spring-cloud-eureka-client)
      	.getInstances()
      	.get(0);

    String hostName = service.getHostName();
    int port = service.getPort();

    // ...
}
```

RestTemplate也可用于按名称访问Eureka客户端服务，但这个主题超出了本文的范围。

要设置我们的Feign Client项目，我们将向其pom.xml添加以下4个依赖项：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-feign</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

Feign客户端位于[spring-cloud-starter-feign](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-starter-feign/1.4.7.RELEASE)包中。要启用它，我们必须使用@EnableFeignClients标注@Configuration。要使用它，我们只需使用@FeignClient("service-name")标注一个接口并将其自动注入到控制器中。

创建此类Feign Client的一个好方法是使用[@RequestMapping](https://www.baeldung.com/spring-requestmapping)注解方法创建接口并将它们放入单独的模块中。这样它们就可以在服务器和客户端之间共享。在服务器端，我们可以将它们实现为@Controller，而在客户端，它们可以扩展并标注为@FeignClient。

此外，需要在项目中包含[spring-cloud-starter-eureka](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-starter-eureka/1.4.7.RELEASE)包，并通过使用@EnableEurekaClient标注主应用程序类来启用。

[spring-boot-starter-web](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-web/3.0.3)和[spring-boot-starter-thymeleaf](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-thymeleaf/3.0.3)依赖项用于呈现包含从我们的REST服务获取的数据的[视图](https://www.baeldung.com/spring-mvc-form-tutorial)。

这将是我们的Feign Client接口：

```java
@FeignClient("spring-cloud-eureka-client")
public interface GreetingClient {
	@RequestMapping("/greeting")
	String greeting();
}
```

在这里，我们将实现主应用程序类，它同时充当控制器：

```java
@SpringBootApplication
@EnableFeignClients
@Controller
public class FeignClientApplication {
	@Autowired
	private GreetingClient greetingClient;

	public static void main(String[] args) {
		SpringApplication.run(FeignClientApplication.class, args);
	}

	@RequestMapping("/get-greeting")
	public String greeting(Model model) {
		model.addAttribute("greeting", greetingClient.greeting());
		return "greeting-view";
	}
}
```

这将是我们视图的HTML模板：

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
	<title>Greeting Page</title>
</head>
<body>
<h2 th:text="${greeting}"/>
</body>
</html>
```

application.yml配置文件和上一步几乎一样：

```yaml
spring:
    application:
        name: spring-cloud-eureka-feign-client
server:
    port: 8080
eureka:
    client:
        serviceUrl:
            defaultZone: ${EUREKA_URI:http://localhost:8761/eureka}
```

现在我们可以构建并运行这个服务了。最后，我们将浏览器指向[http://localhost:8080/get-greeting](http://localhost:8080/get-greeting)，它应该显示如下内容：

<img src="../assets/img_2.png">

## 5. 'TransportException:Cannot Execute Request on Any Known Server'

在运行Eureka服务器时，我们经常会遇到如下异常：

```shell
com.netflix.discovery.shared.transport.TransportException: Cannot execute request on any known server
```

基本上，这是由于application.properties或application.yml中的配置错误造成的。Eureka为客户端提供了两个可配置的属性：

-   registerWithEureka：如果我们将此属性设置为true，那么当服务器启动时，内置客户端将尝试向Eureka服务器注册自己。
-   fetchRegistry：如果我们将此属性配置为true，则内置客户端将尝试获取Eureka注册中心。

**现在当我们启动Eureka服务器时，我们不想注册内置客户端以使用服务器配置自身**。

如果我们将上述属性标记为true(或者只是不配置它们，因为它们默认为true)，那么在启动服务器时，内置客户端会尝试向Eureka服务器注册自己并尝试获取注册中心，但尚不可用。结果，我们得到TransportException。

因此我们永远不应该在Eureka服务器应用程序中将这些属性配置为true。应该放在application.yml中的正确设置是：

```yaml
eureka:
    client:
        registerWithEureka: false
        fetchRegistry: false
```

## 6. 总结

在本文中，我们学习了如何使用Spring Netflix Eureka Server实现服务注册中心，并向其注册一些Eureka客户端。

由于我们在步骤3中的Eureka客户端监听随机选择的端口，因此如果没有来自注册中心的信息，它就不知道其位置。使用Feign Client和我们的注册中心，我们可以定位和使用REST服务，即使位置发生变化。

最后，我们看到了在微服务架构中使用服务发现的大局。
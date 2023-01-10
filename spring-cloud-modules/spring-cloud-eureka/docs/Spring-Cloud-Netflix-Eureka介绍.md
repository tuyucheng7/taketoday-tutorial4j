## 1. 概述

在本文中，我们将通过“Spring Cloud Netflix Eureka”介绍客户端的服务发现。

客户端服务发现允许服务相互查找和通信，而无需对主机名和端口进行硬编码。在这种架构中，唯一的“固定点”是服务注册中心，每个服务都必须向其注册。一个缺点是，所有客户端都必须实现一定的逻辑才能与这个固定点进行交互。这假设在实际请求之前有一个额外的网络往返。

使用Netflix Eureka，每个客户端都可以同时充当服务器，将其状态到连接的对等点。换句话说，客户端检索服务注册表中所有已连接的对等方的列表，并通过负载均衡算法向其他服务发出所有进一步的请求。

为了获知客户端的存在，他们必须向注册表发送心跳信号。

为了实现本教程的目标，我们将实现三个微服务：

+ 服务注册中心(Eureka Server)
+ REST服务，它在注册中心中注册自身(Eureka Client)
+ 一个Web应用程序，它将REST服务作为注册中心感知客户端(Spring Cloud Netflix Feign Client)使用

## 2. Eureka Server

使用Eureka Server实现服务注册非常简单：

1. 添加spring-cloud-starter-netflix-eureka-server依赖。
2. 通过在带有@SpringBootApplication注解的应用程序主类上使用@EnableEurekaServer注解来开启Eureka Server。
3. 在application.yaml中配置一些属性。

首先，我们将创建一个新的Maven项目并添加相关依赖。请注意，我们将spring-cloud-starter-parent依赖添加到本教程描述的所有模块中：

```xml
<dependencyManagement>
	<dependencies>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-dependencies</artifactId>
			<version>2021.0.0</version>
			<type>pom</type>
			<scope>import</scope>
		</dependency>
	</dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    </dependency>
</dependencies>
```

然后，我们创建一个主应用程序类：

```java
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaServerApplication.class, args);
	}
}
```

最后，我们在application.yaml文件中添加一些配置属性：

```yaml
server:
    port: 8761

eureka:
    client:
        registerWithEureka: false
        fetchRegistry: false
```

这里我们配置一个应用程序端口；Eureka Server的默认端口是8761。eureka.client.registerWithEureka告诉内置的Eureka客户端不要向自身注册自己，因为我们的应用程序应该充当服务器。

现在打开浏览器访问“http://localhost:8761”查看Eureka仪表盘，稍后我们会在其中观察到已注册的服务实例。

目前，我们可以看到服务的基本指标，如状态和健康指标：

<img src="../assets/img.png">

## 3. Eureka Client

为了让我们的客户端能够发现服务，我们必须在类路径中包含一个Spring Discovery Client(例如spring-cloud-starter-netflix-eureka-client)。然后我们需要使用@EnableDiscoveryClient或@EnableEurekaClient来标注带有@Configuration注解的类。请注意，如果我们在类路径上有spring-cloud-starter-netflix-eureka-client依赖项，那么这个注解是可选的；后者明确地告诉Spring Boot使用Spring Netflix Eureka进行服务发现。为了给我们的客户端应用程序添加一些示例，我们还将在pom.xml中包含spring-boot-starter-web依赖并实现一个REST控制器。

首先我们添加依赖项，同样，我们使用spring-cloud-starter-parent依赖来统一我们的版本：

```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

然后，我们需要实现我们的应用程序主类：

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
		return String.format("Hello from '%s'!", eurekaClient.getApplication(appName).getName());
	}
}
```

下面是GreetingController接口：

```java
public interface GreetingController {
	@RequestMapping("/greeting")
	String greeting();
}
```

除了接口，我们也可以简单地在EurekaClientApplication类中声明请求映射，如果我们想在服务器和客户端之间共享接口，那么这个接口可能很有用。

接下来，我们必须在application.yaml中配置spring.application.name，以便在服务注册中心中唯一标识我们注册的客户端。

我们可以让Spring Boot为我们选择一个随机端口，因为稍后我们只需要使用服务的名称即可访问该服务。

最后，我们必须告诉客户端它必须在哪里找到注册中心：

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

我们以这种方式配置我们的Eureka Client，因为这种服务以后应该很容易扩展。

现在我们运行客户端，并使用浏览器访问“http://localhost:8761”在Eureka仪表盘上查看客户端的注册状态。
通过使用仪表板，我们可以进行进一步的配置，例如将注册客户端的主页与仪表板链接以进行管理。但是，配置选项在本文中不做过多描述：

<img src="../assets/img_1.png">

## 4. Feign Client

为了用三个相关的微服务完成我们最终的项目，我们现在将使用Spring Netflix Feign Client实现一个使用REST的Web应用程序。

可以将Feign想象为一个支持服务发现的Spring RestTemplate，它使用接口与端点进行通信。这些接口将在运行时自动实现，并使用服务名称代替服务URL。

如果不使用Feign，我们就必须将一个EurekaClient的实例自动注入到我们的控制器中，我们可以通过服务名称获取Application对象接收服务信息。我们将使用Application获取此服务的所有实例的列表，选择一个合适的，然后使用InstanceInfo获取主机名和端口。这样，我们可以使用任何http客户端执行标准请求：

```java
@Controller
public class NoFeignClientController {

	private static final String SERVICE_NAME = "spring-cloud-eureka-client";

	@Autowired
	private EurekaClient eurekaClient;

	@RequestMapping("/get-greeting-no-feign")
	public String greeting(Model model) {

		InstanceInfo service = eurekaClient
				.getApplication(SERVICE_NAME)
				.getInstances()
				.get(0);

		String hostName = service.getHostName();
		int port = service.getPort();

		URI url = URI.create("http://" + hostName + ":" + port + "/greeting");

		ResponseEntity<String> response = new RestTemplate().getForEntity(url, String.class);

		model.addAttribute("greeting", response.getBody());

		return "greeting-view";
	}
}
```

RestTemplate也可以用于通过名称访问Eureka Client服务，本文不做介绍。

要构建我们的Feign Client模块，我们需要在pom.xml中添加以下四个依赖项：

```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-openfeign</artifactId>
	<version>3.1.3</version>
</dependency>
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
	<version>3.1.3</version>
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

Feign Client位于spring-cloud-starter-feign依赖中；要启用它，我们必须使用@EnableFeignClients注解标注带有@Configuration的类。要使用它，我们只需使用@FeignClient("service-name")标注服务接口并将其自动注入到控制器中。

创建此类Feign Clients的一个好方法是创建带有@RequestMapping注解的方法的接口，并将它们放在单独的模块中，通过这种方式，它们可以在服务器和客户端之间共享。在服务器端，我们可以将它们实现为@Controller，在客户端，它们可以被继承并使用@FeignClient标注。

此外，spring-cloud-starter-netflix-eureka-client依赖也需要包含在项目中，并通过使用@EnableEurekaClient标注主应用程序类来启用。

spring-boot-starter-web和spring-boot-starter-thymeleaf依赖用于呈现包含从我们的REST服务获取的数据的视图。

下面是我们的Feign Client接口：

```java
@FeignClient("spring-cloud-eureka-client")
public interface GreetingClient {
	@RequestMapping("/greeting")
	String greeting();
}
```

然后我们实现主应用程序类，它同时充当控制器：

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

下面是我们的呈现数据的视图页面：

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

application.yaml配置文件与Eureka Client模块几乎相同：

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

现在，我们可以运行该服务。最后，使用浏览器访问"http://localhost:8080/get-greeting"应显示如下内容：

> Hello from 'SPRING-CLOUD-EUREKA-CLIENT'!

## 5. TransportException: Cannot Execute Request on Any Known Server

在运行Eureka Server时，我们经常可能遇到以下异常：

```shell
com.netflix.discovery.shared.transport.TransportException: Cannot execute request on any known server
```

基本上，这是由于application.properties或application.yaml中的错误配置而导致的。Eureka为客户端提供了两个可配置的属性：

+ registerWithEureka：如果我们将此属性设置为true，那么当服务器启动时，内置客户端将尝试向Eureka服务器注册自己。
+ fetchRegistry：如果我们将此属性配置为true，则内置客户端将尝试获取Eureka注册表。

现在，当我们启动Eureka Server时，我们不想将内置客户端注册到服务器。如果我们将上述属性标记为true(或者只是不配置它们，因为它们默认情况下为true)，那么在启动服务器时，内置客户端会尝试将自己注册到Eureka Server并尝试获取注册表，但该注册表尚不可用，因此导致TransportException。

因此，我们绝不应该在Eureka Server应用程序中将这些属性配置为true。正确的application.yaml配置为：

```yaml
eureka:
    client:
        registerWithEureka: false
        fetchRegistry: false
```

## 6. 总结

在本文中，我们学习了如何使用Spring Netflix Eureka Server实现服务注册，并使用它注册一些Eureka客户端。

由于第3步中的Eureka Client模块监听随机选择的端口，因此如果没有来自注册表的信息，它就不知道自己的位置。使用Feign Client和我们的注册表，我们可以定位和使用REST服务，即使位置发生变化。

最后，我们看到了在微服务体系结构中使用服务发现的总体情况。
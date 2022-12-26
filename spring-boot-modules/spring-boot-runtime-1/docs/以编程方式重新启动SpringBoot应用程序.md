## 1. 概述

在本教程中，我们将演示如何**以编程方式重新启动Spring Boot应用程序**。

在某些情况下重新启动我们的应用程序可能会非常方便：

-   更改某些参数后重新加载配置文件
-   在运行时更改当前激活的Profile
-   出于任何原因重新初始化应用程序上下文

虽然本文介绍了重新启动Spring Boot应用程序的功能，但请注意，我们还有一个关于[关闭Spring Boot应用程序](关闭SpringBoot应用程序.md)的教程。

现在，让我们探索实现Spring Boot应用程序重新启动的不同方法。

## 2. 通过创建新上下文重新启动

我们可以通过关闭应用程序上下文并从头开始创建新的上下文来重新启动我们的应用程序，虽然这种方法非常简单，但我们必须注意一些微妙的细节才能使其发挥作用。

让我们看看如何在我们的Spring Boot应用程序的main方法中实现它：

```java
@SpringBootApplication
public class Application {

	private static ConfigurableApplicationContext context;

	public static void main(String[] args) {
		context = SpringApplication.run(Application.class, args);
	}

	public static void restart() {
		ApplicationArguments args = context.getBean(ApplicationArguments.class);

		Thread thread = new Thread(() -> {
			context.close();
			context = SpringApplication.run(Application.class, args.getSourceArgs());
		});

		thread.setDaemon(false);
		thread.start();
	}
}
```

正如我们在上面的例子中看到的，在一个单独的非守护线程中重新创建上下文很重要-这样我们就可以防止由close方法触发的JVM关机钩子关闭我们的应用程序。否则，我们的应用程序将停止，因为JVM在终止它们之前不会等待守护线程完成。

此外，让我们添加一个REST端点，通过它我们可以触发重启：

```java
@RestController
public class RestartController {

	@PostMapping("/restart")
	public void restart() {
		Application.restart();
	}
}
```

在这里，我们添加了一个控制器，其中包含一个调用restart方法的映射方法。

然后我们可以调用我们的新端点来重新启动应用程序：

```java
curl -X POST localhost:port/restart
```

**当然，如果我们在实际应用程序中添加这样的端点，我们也必须对其进行保护**。

## 3. Actuator的重启端点

重启应用程序的另一种方法是使用[Spring Boot Actuator]()中的内置RestartEndpoint。

首先，让我们添加所需的Maven依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-cloud-starter</artifactId>
</dependency>
```

接下来，我们必须在application.properties文件中启用内置地重启端点：

```properties
management.endpoint.restart.enabled=true
```

现在我们已经设置了所有内容，我们可以将RestartEndpoint注入到我们的服务中：

```java
@Service
public class RestartService {

	@Autowired
	private RestartEndpoint restartEndpoint;

	public void restartApp() {
		restartEndpoint.restart();
	}
}
```

在上面的代码中，我们使用RestartEndpoint bean来重新启动我们的应用程序，这是一种很好的重启方式，因为我们只需要调用一个方法即可完成所有工作。

如我们所见，使用RestartEndpoint是重新启动应用程序的一种简单方法。另一方面，这种方法有一个缺点，因为它需要我们添加提到的库。如果我们还没有使用它们，那么仅此功能的开销可能太大。在这种情况下，我们可以坚持使用上一节中的手动方法，因为它只需要多几行代码。

## 4. 刷新应用程序上下文

在某些情况下，我们可以通过调用其[refresh](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/ConfigurableApplicationContext.html#refresh--)方法来重新加载应用程序上下文。

尽管此方法听起来很有希望，但只有某些应用程序上下文类型支持刷新已初始化的上下文。例如，FileSystemXmlApplicationContext、GroovyWebApplicationContext和其他一些支持它。

不幸的是，如果我们在Spring Boot Web应用程序中尝试这样做，我们将得到以下错误：

```java
java.lang.IllegalStateException: GenericApplicationContext does not support multiple refresh attempts:
just call 'refresh' once
```

最后，虽然有些上下文类型支持多次刷新，但我们应该避免这样做，原因是refresh方法被设计为框架用来初始化应用程序上下文的内部方法。

## 5. 总结

在本文中，我们探讨了如何以编程方式重新启动Spring Boot应用程序的多种不同方式。
## 1. 概述

管理Spring Boot应用程序的生命周期对于生产就绪系统非常重要，Spring容器在ApplicationContext的帮助下处理所有Bean的创建、初始化和销毁。

**这篇文章的重点是生命周期的销毁阶段，更具体地说，我们将了解关闭Spring Boot应用程序的不同方法**。

要了解有关如何使用Spring Boot设置项目的更多信息，请查看[Spring Boot入门]()文章，或查看[Spring Boot配置]()。

## 2. 关机端点

默认情况下，除/shutdown之外的所有端点都在Spring Boot应用程序中启用；这自然是Actuator端点的一部分。

以下是用于设置这些内容的Maven依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

而且，如果我们还想设置安全支持，我们需要：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

最后，我们在application.properties文件中启用shutdown端点：

```properties
management.endpoints.web.exposure.include=*
management.endpoint.shutdown.enabled=true
endpoints.shutdown.enabled=true
```

请注意，我们还必须公开我们想要使用的任何Actuator端点。在上面的示例中，我们公开了所有Actuator端点，其中将包括/shutdown端点。

**要关闭Spring Boot应用程序，我们只需下面这样调用POST方法**：

```plaintext
curl -X POST localhost:port/actuator/shutdown
```

在此调用中，POST代表Actuator端口。

## 3. 关闭应用程序上下文

我们也可以直接使用应用程序上下文调用close()方法。

让我们从一个创建上下文并关闭它的示例开始：

```java
ConfigurableApplicationContext ctx = new SpringApplicationBuilder(Application.class).web(WebApplicationType.NONE).run();
System.out.println("Spring Boot application started");
ctx.getBean(TerminateBean.class);
ctx.close();
```

**这会销毁所有的bean，释放锁，然后关闭bean工厂**。为了验证应用程序关闭，我们使用带有@PreDestroy注解的Spring标准生命周期回调：

```java
public class TerminateBean {

	@PreDestroy
	public void onDestroy() throws Exception {
		System.out.println("Spring Container is destroyed!");
	}
}
```

我们还必须添加一个这种类型的bean：

```java
@Configuration
public class ShutdownConfig {

	@Bean
	public TerminateBean getTerminateBean() {
		return new TerminateBean();
	}
}
```

下面是运行此示例后的输出：

```shell
Spring Boot application started
Closing AnnotationConfigApplicationContext@39b43d60
DefaultLifecycleProcessor - Stopping beans in phase 0
Unregistering JMX-exposed beans on shutdown
Spring Container is destroyed!
```

这里要记住的重要一点是：**在关闭应用程序上下文时，父上下文不会因为单独的生命周期而受到影响**。

### 3.1 关闭当前应用程序上下文

在上面的示例中，我们创建了一个子应用程序上下文，然后使用close()方法将其销毁。

如果我们想关闭当前上下文，一种解决方案是简单地调用Actuator的/shutdown端点。

但是，我们也可以创建自己的自定义端点：

```java
@RestController
public class ShutdownController implements ApplicationContextAware {

	private ApplicationContext context;

	@PostMapping("/shutdownContext")
	public void shutdownContext() {
		((ConfigurableApplicationContext) context).close();
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		this.context = ctx;
	}
}
```

在这里，我们添加了一个控制器，该控制器实现了ApplicationContextAware接口并覆盖了setter方法以获取当前应用程序上下文。然后，在映射方法shutdownContext()中，我们只需调用close()方法。

然后我们可以调用我们的新端点来关闭当前上下文：

```bash
curl -X POST localhost:port/shutdownContext
```

**当然，如果你在实际应用程序中添加这样的端点，你也需要保护它**。

## 4. 退出SpringApplication

SpringApplication向JVM注册一个关闭挂钩以确保应用程序正确退出。

Bean可以实现ExitCodeGenerator接口以返回特定的错误代码：

```java
ConfigurableApplicationContext ctx = new SpringApplicationBuilder(Application.class)
	.web(WebApplicationType.NONE).run();

int exitCode = SpringApplication.exit(ctx, new ExitCodeGenerator() {
@Override
public int getExitCode() {
        // return the error code
        return 0;
    }
});

System.exit(exitCode);
```

与Java 8 lambda应用程序相同的代码：

```java
SpringApplication.exit(ctx, () -> 0);
```

**调用System.exit(exitCode)后，程序以0返回代码终止**：

```shell
Process finished with exit code 0
```

## 5. 杀死应用程序进程

最后，我们还可以使用bash脚本从应用程序外部关闭Spring Boot应用程序，我们对此选项的第一步是让应用程序上下文将其PID写入文件：

```java
SpringApplicationBuilder app = new SpringApplicationBuilder(Application.class)
	.web(WebApplicationType.NONE);
app.build().addListeners(new ApplicationPidFileWriter("./bin/shutdown.pid"));
app.run();
```

接下来，创建一个包含以下内容的shutdown.bat文件：

```plaintext
kill $(cat ./bin/shutdown.pid)
```

shutdown.bat的执行从shutdown.pid文件中提取进程ID，并使用kill命令终止Boot应用程序。

## 6. 总结

在这篇简短的文章中，我们介绍了一些可用于关闭正在运行的Spring Boot应用程序的简单方法，虽然由开发人员选择合适的方法；但所有这些方法都应该通过设计和有目的地使用。

例如，当我们需要将错误代码传递给另一个环境(例如JVM以进行进一步操作)时，首选.exit()。**使用应用程序PID提供了更大的灵活性，因为我们还可以使用bash脚本启动或重新启动应用程序**。

最后，/shutdown在这里可以通过HTTP从外部终止应用程序。对于所有其他情况，.close()将完美运行。
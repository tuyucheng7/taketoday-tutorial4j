## 1. 概述

在本文中，我们介绍了Spring Boot Actuator。我们将首先介绍基础知识，然后详细讨论Spring Boot 2.x与1.x中可用的内容。

我们将学习如何在Spring Boot 2.x和WebFlux中使用、配置和扩展此监控工具，从而利用反应式编程模型。然后我们将讨论如何使用Boot 1.x来做同样的事情。

Spring Boot Actuator自2014年4月起与第一个Spring Boot版本一起可用。

随着[Spring Boot 2的发布](https://www.baeldung.com/new-spring-boot-2)，Actuator已经过重新设计，并添加了新的令人兴奋的端点。

我们将本指南分为三个主要部分：

-   [什么是Actuator？](https://www.baeldung.com/spring-boot-actuators#understanding-actuator)
-   [Spring Boot 2.x Actuator](https://www.baeldung.com/spring-boot-actuators#boot-2x-actuator)
-   [Spring Boot 1.x Actuator](https://www.baeldung.com/spring-boot-actuators#boot-1x-actuator)

## 2. 什么是Actuator？

本质上，Actuator为我们的应用程序带来了生产就绪的特性。

有了这种依赖性，监控我们的应用程序、收集指标、了解流量或我们的数据库状态变得微不足道。

这个库的主要好处是我们可以获得生产级工具，而无需自己实际实现这些功能。

Actuator主要用于公开有关正在运行的应用程序的操作信息-健康、指标、信息、转储、环境等。它使用HTTP端点或JMX beans使我们能够与之交互。

一旦这种依赖性在类路径上，我们就可以使用几个开箱即用的端点。与大多数Spring模块一样，我们可以通过多种方式轻松配置或扩展它。

### 2.1 入门

要启用Spring Boot Actuator，我们只需要将spring-boot-actuator依赖项添加到我们的包管理器中。

在Maven中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

请注意，无论Boot版本如何，这仍然有效，因为版本在Spring Boot材料清单(BOM)中指定。

## 3. Spring Boot 2.x Actuator

在2.x中，Actuator保留了它的基本意图，但简化了它的模型，扩展了它的功能，并合并了更好的默认值。

首先，这个版本变得与技术无关。它还通过将其与应用程序合并来简化其安全模型。

在各种变化中，重要的是要记住，其中一些正在发生变化。这包括HTTP请求和响应以及Java API。

最后，最新版本现在支持CRUD模型，而不是旧的读/写模型。

### 3.1 技术支持

在其第二个主要版本中，Actuator现在与技术无关，而在1.x中它与MVC相关联，因此与Servlet API相关联。

在2.x中，Actuator将其模型定义为可插入和可扩展的，而无需为此依赖MVC。

因此，通过这种新模型，我们能够利用MVC和WebFlux作为底层Web技术。

此外，可以通过实施正确的适配器来添加即将推出的技术。

最后，JMX仍然支持在没有任何额外代码的情况下公开端点。

### 3.2 重要变化

与以前的版本不同，Actuator禁用了大多数端点。

因此，默认情况下仅有的两个可用的是/health和/info。

如果我们想启用所有这些，我们可以设置management.endpoints.web.exposure.include=*。或者，我们可以列出应该启用的端点。

Actuator现在与常规App安全规则共享安全配置，因此安全模型得到了极大的简化。

因此，为了调整Actuator安全规则，我们可以为/actuator/**添加一个条目：

```java
@Bean
public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    return http.authorizeExchange()
        .pathMatchers("/actuator/**").permitAll()
        .anyExchange().authenticated()
        .and().build();
}
```

我们可以在[全新的Actuator官方文档](https://docs.spring.io/spring-boot/docs/2.0.x/actuator-api/html/)中找到更多详细信息。

此外，默认情况下，所有Actuator端点现在都放在/actuator路径下。

与之前的版本一样，我们可以使用新的属性management.endpoints.web.base-path调整这条路径。

### 3.3 预定义端点

让我们看一下一些可用的端点，其中大部分已经在1.x中可用。

此外，还添加了一些端点，删除了一些端点，还对一些端点进行了重组：

-   /auditevents列出与安全审计相关的事件，例如用户登录/注销。此外，我们可以在其他字段中按主体或类型进行过滤。
-   /beans返回BeanFactory中所有可用的beans，与/auditevents不同，它不支持过滤。
-   /conditions，以前称为/autoconfig，围绕自动配置构建条件报告。
-   /configprops允许我们获取所有@ConfigurationProperties bean。
-   /env返回当前环境属性。此外，我们可以检索单个属性。
-   /flyway提供有关我们的Flyway数据库迁移的详细信息。
-   /health总结了我们应用程序的健康状况。
-   /heapdump从我们的应用程序使用的JVM构建并返回堆转储。
-   /info返回一般信息。它可能是自定义数据、构建信息或有关最新提交的详细信息。
-   /liquibase的行为类似于/flyway，但用于Liquibase。
-   /logfile返回普通的应用程序日志。
-   /loggers使我们能够查询和修改应用程序的日志记录级别。
-   /metrics详细说明我们应用程序的指标。这可能包括通用指标和自定义指标。
-   /prometheus返回与前一个类似的指标，但格式化为与Prometheus服务器一起使用。
-   /scheduledtasks提供有关我们应用程序中每个计划任务的详细信息。
-   /sessions列出我们正在使用Spring Session的HTTP会话。
-   /shutdown执行应用程序的正常关闭。
-   /threaddump转储底层JVM的线程信息。

### 3.4 Actuator端点的超媒体

Spring Boot添加了一个发现端点，该端点返回所有可用Actuator端点的链接。这将有助于发现Actuator端点及其相应的URL。

默认情况下，可以通过/actuator端点访问此发现端点。

因此，如果我们向此URL发送GET请求，它将返回各种端点的Actuator链接：

```json
{
	"_links": {
		"self": {
			"href": "http://localhost:8080/actuator",
			"templated": false
		},
		"features-arg0": {
			"href": "http://localhost:8080/actuator/features/{arg0}",
			"templated": true
		},
		"features": {
			"href": "http://localhost:8080/actuator/features",
			"templated": false
		},
		"beans": {
			"href": "http://localhost:8080/actuator/beans",
			"templated": false
		},
		"caches-cache": {
			"href": "http://localhost:8080/actuator/caches/{cache}",
			"templated": true
		}
		// truncated
	}
}
```

如上所示，/actuator端点在_links字段下报告所有可用的Actuator端点。

此外，如果我们配置自定义管理基本路径，那么我们应该使用该基本路径作为发现URL。

例如，如果我们将management.endpoints.web.base-path设置为/mgmt，那么我们应该向/mgmt端点发送请求以查看链接列表。

非常有趣的是，当管理基本路径设置为/时，发现端点被禁用以防止与其他映射发生冲突的可能性。

### 3.5 健康指标

就像在以前的版本中一样，我们可以轻松添加自定义指标。与其他API相反，用于创建自定义健康端点的抽象保持不变。但是，添加了一个新接口ReactiveHealthIndicator来实现响应式健康检查。

让我们看一下一个简单的自定义响应式健康检查：

```java
@Component
public class DownstreamServiceHealthIndicator implements ReactiveHealthIndicator {

	@Override
	public Mono<Health> health() {
		return checkDownstreamServiceHealth().onErrorResume(
			ex -> Mono.just(new Health.Builder().down(ex).build())
		);
	}

	private Mono<Health> checkDownstreamServiceHealth() {
		// we could use WebClient to check health reactively
		return Mono.just(new Health.Builder().up().build());
	}
}
```

健康指标的一个便利特性是我们可以将它们聚合为层次结构的一部分。

因此，按照前面的示例，我们可以将所有下游服务分组到下游服务类别下。只要每个嵌套服务都可以访问，这个类别就会是健康的。

查看我们关于[健康指标](https://www.baeldung.com/spring-boot-health-indicators)的文章以更深入地了解。

### 3.6 健康组

从Spring Boot 2.2开始，我们可以将健康指标组织成[组](https://github.com/spring-projects/spring-boot/blob/c3aa494ba32b8271ea19dd041327441b27ddc319/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/health/HealthEndpointGroups.java#L30)，并将相同的配置应用于所有组成员。

例如，我们可以 通过将其添加到我们的application.properties来创建一个名为custom的健康组：

```properties
management.endpoint.health.group.custom.include=diskSpace,ping
```

这样，自定义组就包含了diskSpace和ping健康指标。

现在，如果我们调用/actuator/health端点，它会在JSON响应中告诉我们新的健康组：

```json
{
	"status": "UP",
	"groups": [
		"custom"
	]
}
```

通过健康组，我们可以看到一些健康指标的汇总结果。

在这种情况下，如果我们向/actuator/health/custom发送请求，那么：

```json
{
	"status": "UP"
}
```

我们可以通过application.properties配置组以显示更多详细信息：

```properties
management.endpoint.health.group.custom.show-components=always
management.endpoint.health.group.custom.show-details=always
```

现在，如果我们向/actuator/health/custom发送相同的请求，我们将看到更多详细信息：

```json
{
	"status": "UP",
	"components": {
		"diskSpace": {
			"status": "UP",
			"details": {
				"total": 499963170816,
				"free": 91300069376,
				"threshold": 10485760
			}
		},
		"ping": {
			"status": "UP"
		}
	}
}
```

也可以仅为授权用户显示这些详细信息：

```properties
management.endpoint.health.group.custom.show-components=when_authorized
management.endpoint.health.group.custom.show-details=when_authorized
```

我们还可以有一个自定义状态映射。

例如，它可以返回207状态代码，而不是HTTP 200 OK响应：

```properties
management.endpoint.health.group.custom.status.http-mapping.up=207
```

在这里，我们告诉Spring Boot在自定义组状态为UP时返回207 HTTP状态代码。

### 3.7 Spring Boot 2中的指标

在Spring Boot 2.0中，内部指标被Micrometer支持取代，因此我们可以期待重大变化。如果我们的应用程序使用GaugeService或CounterService等指标服务，它们将不再可用。

相反，我们应该直接与[Micrometer](https://www.baeldung.com/micrometer)交互。在Spring Boot 2.0中，我们将获得一个自动配置的MeterRegistry类型的bean。

此外，Micrometer现在是Actuator依赖项的一部分，所以只要Actuator依赖项在类路径中，我们就可以开始了。

此外，我们将从/metrics端点获得全新的响应：

```json
{
	"names": [
		"jvm.gc.pause",
		"jvm.buffer.memory.used",
		"jvm.memory.used",
		"jvm.buffer.count"
		// ...
	]
}
```

如我们所见，没有我们在1.x中获得的实际指标。

要获取特定指标的实际值，我们现在可以导航到所需的指标，例如/actuator/metrics/jvm.gc.pause，并获得详细的响应：

```json
{
	"name": "jvm.gc.pause",
	"measurements": [
		{
			"statistic": "Count",
			"value": 3.0
		},
		{
			"statistic": "TotalTime",
			"value": 7.9E7
		},
		{
			"statistic": "Max",
			"value": 7.9E7
		}
	],
	"availableTags": [
		{
			"tag": "cause",
			"values": [
				"Metadata GC Threshold",
				"Allocation Failure"
			]
		},
		{
			"tag": "action",
			"values": [
				"end of minor GC",
				"end of major GC"
			]
		}
	]
}
```

现在指标更加全面，不仅包括不同的值，还包括一些关联的元数据。

### 3.8 自定义/info端点

/info端点保持不变。和以前一样，我们可以使用各自的Maven或Gradle依赖项添加git详细信息：

```xml
<dependency>
    <groupId>pl.project13.maven</groupId>
    <artifactId>git-commit-id-plugin</artifactId>
</dependency>
```

同样，我们还可以使用Maven或Gradle插件包含构建信息，包括名称、组和版本：

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <executions>
        <execution>
            <goals>
                <goal>build-info</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### 3.9 创建自定义端点

正如我们之前指出的，我们可以创建自定义端点。然而，Spring Boot 2重新设计了实现这一点的方式，以支持新的技术无关范式。

让我们创建一个Actuator端点来查询、启用和禁用我们应用程序中的功能标志：

```java
@Component
@Endpoint(id = "features")
public class FeaturesEndpoint {

	private Map<String, Feature> features = new ConcurrentHashMap<>();

	@ReadOperation
	public Map<String, Feature> features() {
		return features;
	}

	@ReadOperation
	public Feature feature(@Selector String name) {
		return features.get(name);
	}

	@WriteOperation
	public void configureFeature(@Selector String name, Feature feature) {
		features.put(name, feature);
	}

	@DeleteOperation
	public void deleteFeature(@Selector String name) {
		features.remove(name);
	}

	public static class Feature {
		private Boolean enabled;

		// [...] getters and setters 
	}
}
```

要获得端点，我们需要一个bean。在我们的例子中，我们为此使用@Component。此外，我们需要用@Endpoint装饰这个bean。

我们端点的路径由@Endpoint的id参数决定。在我们的例子中，它将请求路由到/actuator/features。

准备就绪后，我们可以开始使用以下方法定义操作：

-   @ReadOperation：它将映射到HTTP GET。
-   @WriteOperation：它将映射到HTTP POST。
-   @DeleteOperation：它将映射到HTTP DELETE。

当我们使用应用程序中的先前端点运行应用程序时，Spring Boot将注册它。

验证这一点的一种快速方法是检查日志：

```shell
[...].WebFluxEndpointHandlerMapping: Mapped "{[/actuator/features/{name}],
  methods=[GET],
  produces=[application/vnd.spring-boot.actuator.v2+json || application/json]}"
[...].WebFluxEndpointHandlerMapping : Mapped "{[/actuator/features],
  methods=[GET],
  produces=[application/vnd.spring-boot.actuator.v2+json || application/json]}"
[...].WebFluxEndpointHandlerMapping : Mapped "{[/actuator/features/{name}],
  methods=[POST],
  consumes=[application/vnd.spring-boot.actuator.v2+json || application/json]}"
[...].WebFluxEndpointHandlerMapping : Mapped "{[/actuator/features/{name}],
  methods=[DELETE]}"[...]
```

在之前的日志中，我们可以看到WebFlux如何公开我们的新端点。如果我们切换到MVC，它会简单地委托该技术，而无需更改任何代码。

此外，对于这种新方法，我们还有一些重要的注意事项需要牢记：

-   与MVC没有依赖关系。
-   之前作为方法存在的所有元数据(敏感的、启用的...)不再存在。但是，我们可以使用@Endpoint(id = "features", enableByDefault = false)启用或禁用端点。
-   与1.x不同，不再需要扩展给定的接口。
-   与旧的读/写模型相比，我们现在可以使用@DeleteOperation定义DELETE操作。

### 3.10 扩展现有端点

假设我们想要确保我们应用程序的生产实例永远不是SNAPSHOT版本。

我们决定通过更改返回此信息的Actuator端点的HTTP状态代码(即/info)来做到这一点。如果我们的应用程序碰巧是SNAPSHOT，我们将获得不同的HTTP状态代码。

我们可以使用@EndpointExtension注解或其更具体的专业化@EndpointWebExtension或@EndpointJmxExtension轻松扩展预定义端点的行为：

```java
@Component
@EndpointWebExtension(endpoint = InfoEndpoint.class)
public class InfoWebEndpointExtension {

	private InfoEndpoint delegate;

	// standard constructor

	@ReadOperation
	public WebEndpointResponse<Map> info() {
		Map<String, Object> info = this.delegate.info();
		Integer status = getStatus(info);
		return new WebEndpointResponse<>(info, status);
	}

	private Integer getStatus(Map<String, Object> info) {
		// return 5xx if this is a snapshot
		return 200;
	}
}
```

### 3.11 启用所有端点

为了使用HTTP访问Actuator端点，我们需要启用和公开它们。

默认情况下，启用除/shutdown之外的所有端点。默认情况下仅公开/health和/info端点。

我们需要添加以下配置来公开所有端点：

```properties
management.endpoints.web.exposure.include=*
```

要显式启用特定端点(例如，/shutdown)，我们使用：

```properties
management.endpoint.shutdown.enabled=true
```

要公开除一个(例如/loggers)之外的所有已启用的端点，我们使用：

```properties
management.endpoints.web.exposure.include=*
management.endpoints.web.exposure.exclude=loggers
```

## 4.Spring Boot 1.x Actuator

在1.x中，Actuator遵循读/写模型，这意味着我们可以从中读取或写入。

例如，我们可以检索指标或应用程序的运行状况。或者，我们可以优雅地终止我们的应用程序或更改我们的日志记录配置。

为了使其正常工作，Actuator需要Spring MVC通过HTTP公开其端点。不支持其他技术。

### 4.1 端点

在1.x中，Actuator带来了自己的安全模型。它利用了Spring Security结构，但需要独立于应用程序的其余部分进行配置。

此外，大多数端点都是敏感的——这意味着它们不是完全公开的，或者大多数信息将被省略——而少数则不是，例如/info。

以下是Boot开箱即用的一些最常见的端点：

-   /health显示应用程序健康信息(通过未经身份验证的连接访问时的简单状态 或经过身份验证时的完整消息详细信息)；它默认不敏感。
-   /info显示任意应用程序信息；它默认不敏感。
-   /metrics显示当前应用的指标信息；默认情况下它是敏感的。
-   /trace显示跟踪信息(默认为最后几个HTTP请求)。

我们可以在[官方文档](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready-endpoints)上找到现有端点的完整列表。

### 4.2 配置现有端点

我们可以使用格式endpoints.[endpoint name].[property to customize]自定义每个端点的属性。

三个属性可用：

-   id：将通过HTTP访问此端点
-   enabled：如果为true，则可以访问；否则不
-   sensitive：如果为真，则需要授权才能通过HTTP显示关键信息

例如，添加以下属性将自定义/beans端点：


```properties
endpoints.beans.id=springbeans
endpoints.beans.sensitive=false
endpoints.beans.enabled=true
```

### 4.3 /health端点

/health端点用于检查正在运行的应用程序的健康状况或状态。

如果正在运行的实例由于其他原因(例如，我们的数据库的连接问题、磁盘空间不足等)出现故障或变得不健康，通常会通过监控软件来提醒我们。

默认情况下，未经授权的用户只有在通过HTTP访问时才能看到状态信息：

```json
{
    "status" : "UP"
}
```

此健康信息是从所有实现在我们的应用程序上下文中配置的HealthIndicator接口的bean收集的。

HealthIndicator返回的一些信息本质上是敏感的，但我们可以配置endpoints.health.sensitive=false以公开更详细的信息，如磁盘空间、消息代理连接、自定义检查等。

请注意，这仅适用于1.5.0以下的Spring Boot版本。对于1.5.0及更高版本，我们还应该通过设置management.security.enabled=false来禁用安全性以防止未经授权的访问。

我们还可以实现我们自己的自定义健康指标，它可以收集特定于应用程序的任何类型的自定义健康数据，并通过/health端点自动公开它：

```java
@Component("myHealthCheck")
public class HealthCheck implements HealthIndicator {

	@Override
	public Health health() {
		int errorCode = check(); // perform some specific health check
		if (errorCode != 0) {
			return Health.down()
				.withDetail("Error Code", errorCode).build();
		}
		return Health.up().build();
	}

	public int check() {
		// Our logic to check health
		return 0;
	}
}
```

下面是输出的样子：

```json
{
	"status": "DOWN",
	"myHealthCheck": {
		"status": "DOWN",
		"Error Code": 1
	},
	"diskSpace": {
		"status": "UP",
		"free": 209047318528,
		"threshold": 10485760
	}
}
```

### 4.4 /info端点

我们还可以自定义/info端点显示的数据：

```properties
info.app.name=Spring Sample Application
info.app.description=This is my first spring boot application
info.app.version=1.0.0
```

样本输出：

```json
{
	"app": {
		"version": "1.0.0",
		"description": "This is my first spring boot application",
		"name": "Spring Sample Application"
	}
}
```

### 4.5 /metrics端点

指标端点发布有关操作系统和JVM的信息以及应用程序级指标。启用后，我们将获得内存、堆、处理器、线程、加载的类、卸载的类和线程池等信息以及一些HTTP指标。

这是开箱即用的此端点的输出：

```json
{
	"mem": 193024,
	"mem.free": 87693,
	"processors": 4,
	"instance.uptime": 305027,
	"uptime": 307077,
	"systemload.average": 0.11,
	"heap.committed": 193024,
	"heap.init": 124928,
	"heap.used": 105330,
	"heap": 1764352,
	"threads.peak": 22,
	"threads.daemon": 19,
	"threads": 22,
	"classes": 5819,
	"classes.loaded": 5819,
	"classes.unloaded": 0,
	"gc.ps_scavenge.count": 7,
	"gc.ps_scavenge.time": 54,
	"gc.ps_marksweep.count": 1,
	"gc.ps_marksweep.time": 44,
	"httpsessions.max": -1,
	"httpsessions.active": 0,
	"counter.status.200.root": 1,
	"gauge.response.root": 37.0
}
```

为了收集自定义指标，我们支持仪表(数据的单值快照)和计数器，即递增/递减指标。

让我们在/metrics端点中实现我们自己的自定义指标。

我们将自定义登录流程以记录成功和失败的登录尝试：

```java
@Service
public class LoginServiceImpl {

	private final CounterService counterService;

	public LoginServiceImpl(CounterService counterService) {
		this.counterService = counterService;
	}

	public boolean login(String userName, char[] password) {
		boolean success;
		if (userName.equals("admin") && "secret".toCharArray().equals(password)) {
			counterService.increment("counter.login.success");
			success = true;
		}
		else {
			counterService.increment("counter.login.failure");
			success = false;
		}
		return success;
	}
}
```

输出可能如下所示：

```json
{
	...
	"counter.login.success": 105,
	"counter.login.failure": 12,
	...
}
```

请注意，登录尝试和其他与安全相关的事件在Actuator中作为审计事件可用。

### 4.6 创建新端点

除了使用Spring Boot提供的现有端点之外，我们还可以创建一个全新的端点。

首先，我们需要让新端点实现Endpoint<T\>接口：

```java
@Component
public class CustomEndpoint implements Endpoint<List<String>> {

	@Override
	public String getId() {
		return "customEndpoint";
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isSensitive() {
		return true;
	}

	@Override
	public List<String> invoke() {
		// Custom logic to build the output
		List<String> messages = new ArrayList<String>();
		messages.add("This is message 1");
		messages.add("This is message 2");
		return messages;
	}
}
```

为了访问这个新端点，它的id用于映射它。换句话说，我们可以使用它来达到/customEndpoint。

输出：

```bash
[ "This is message 1", "This is message 2" ]
```

### 4.7 进一步定制

出于安全目的，我们可能会选择通过非标准端口公开Actuator端点-可以轻松使用management.port属性对其进行配置。

另外，正如我们已经提到的，在1.x中。Actuator基于Spring Security配置自己的安全模型，但独立于应用程序的其余部分。

因此，我们可以更改management.address属性以限制可以从网络访问端点的位置：

```properties
#port used to expose actuator
management.port=8081

#CIDR allowed to hit actuator
management.address=127.0.0.1

#Whether security should be enabled or disabled altogether
management.security.enabled=false
```

此外，默认情况下，除/info之外的所有内置端点都是敏感的。

如果应用程序使用Spring Security，我们可以通过在application.properties文件中定义默认安全属性(用户名、密码和角色)来保护这些端点：

```properties
security.user.name=admin
security.user.password=secret
management.security.role=SUPERUSER
```

## 5. 总结

在本文中，我们讨论了Spring Boot Actuator。我们首先定义Actuator的含义以及它对我们的作用。

接下来，我们重点介绍了当前Spring Boot 2.x版本的Actuator，讨论了如何使用、调整和扩展它。我们还讨论了我们可以在此新迭代中找到的重要安全更改。我们讨论了一些流行的端点以及它们是如何变化的。

然后我们讨论了更早的Spring Boot 1版本中的Actuator。

最后，我们演示了如何自定义和扩展Actuator。
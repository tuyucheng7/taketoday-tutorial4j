## 1. 概述

[Zuul](https://cloud.spring.io/spring-cloud-netflix/reference/html/#router-and-filter-zuul)是Netflix的边缘服务(或API网关)，可提供动态路由、监控、弹性和安全性。

在本教程中，我们将了解如何**使用回退配置Zuul路由**。

## 2. 初始设置

首先，我们将首先设置两个Spring Boot应用程序。在第一个应用程序中，我们将创建一个简单的REST服务。而在第二个应用程序中，我们将使用Zuul代理为第一个应用程序的REST服务创建路由。

### 2.1 一个简单的REST服务

假设我们的应用程序需要向用户显示今天的天气信息。因此，我们将使用[spring-boot-starter-web](https://search.maven.org/search?q=a:spring-boot-starter-web)创建一个基于Spring Boot的天气服务应用程序：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

现在，我们将为天气服务创建一个控制器：

```java
@RestController
@RequestMapping("/weather")
public class WeatherController {

	@GetMapping("/today")
	public String getMessage() {
		return "It's a bright sunny day today!";
	}
}
```

现在，让我们运行天气服务并检查天气服务API：

```shell
$ curl -s localhost:8080/weather/today
It's a bright sunny day today!
```

### 2.2 API网关应用程序

现在让我们创建我们的第二个Spring Boot应用程序，即API网关。在这个应用程序中，我们将为我们的天气服务创建一个Zuul路由。

由于我们的天气服务和Zuul都期望默认使用8080，因此我们将其配置为在不同的端口7070上运行。

所以，让我们首先在pom.xml中添加[spring-cloud-starter-netflix-zuul](https://search.maven.org/search?q=a:spring-cloud-starter-netflix-zuul)：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
</dependency>
```

接下来，我们将@EnableZuulProxy注解添加到我们的API网关应用程序类上：

```java
@SpringBootApplication
@EnableZuulProxy
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}
}
```

最后，我们将使用Ribbon在application.yml中为我们的天气服务API配置Zuul路由：

```yaml
spring:
    application:
        name: api-gateway
server:
    port: 7070

zuul:
    igoredServices: '*'
    routes:
        weather-service:
            path: /weather/**
            serviceId: weather-service
            strip-prefix: false

ribbon:
    eureka:
        enabled: false

weather-service:
    ribbon:
        listOfServers: localhost:8080
```

### 2.3 测试Zuul路由

此时，两个Spring Boot应用程序都已设置为使用Zuul代理公开天气服务API。

因此，让我们运行这两个应用程序并通过Zuul检查天气服务API：

```shell
$ curl -s localhost:7070/weather/today
It's a bright sunny day today!
```

### 2.4 在没有回退的情况下测试Zuul路由失败

现在，让我们停止天气服务应用程序并再次通过Zuul检查天气服务。结果，我们将在响应中看到一条错误消息：

```shell
$ curl -s localhost:7070/weather/today
{"timestamp":"2019-10-08T12:42:09.479+0000","status":500,
"error":"Internal Server Error","message":"GENERAL"}
```

显然，这不是用户希望看到的响应。因此，我们可以解决这个问题的方法之一是为天气服务Zuul路由创建一个回退。

## 3. 路由的Zuul回退

Zuul代理使用Ribbon进行负载均衡，请求在Hystrix命令中执行。因此，**Zuul路由中的失败出现在Hystrix矩阵中**。

因此，要为Zuul路由创建自定义回退，我们将创建一个[FallbackProvider](https://static.javadoc.io/org.springframework.cloud/spring-cloud-netflix-core/1.4.3.RELEASE/index.html?org/springframework/cloud/netflix/zuul/filters/route/FallbackProvider.html)类型的bean。

### 3.1 WeatherServiceFallback类

在此示例中，我们希望从回退响应中返回一条消息，而不是我们之前看到的默认错误消息。因此，让我们为天气服务路由创建一个简单的FallbackProvider实现：

```java
@Component
class WeatherServiceFallback implements FallbackProvider {

	private static final String DEFAULT_MESSAGE = "Weather information is not available.";

	@Override
	public String getRoute() {
		return "weather-service";
	}

	@Override
	public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
		if (cause instanceof HystrixTimeoutException) {
			return new GatewayClientResponse(HttpStatus.GATEWAY_TIMEOUT, DEFAULT_MESSAGE);
		} else {
			return new GatewayClientResponse(HttpStatus.INTERNAL_SERVER_ERROR, DEFAULT_MESSAGE);
		}
	}
}
```

正如我们所看到的，我们已经覆盖了方法getRoute和fallbackResponse。**getRoute方法返回我们必须为其创建回退的路由的Id**。而**fallbackResponse方法返回自定义回退响应，在我们的例子中是GatewayClientResponse类型的对象**。GatewayClientResponse是[ClientHttpResponse](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/http/client/ClientHttpResponse.html)的简单实现。

### 3.2 测试Zuul回退

现在让我们测试我们为天气服务创建的回退。因此，我们将运行API网关应用程序并确保天气服务应用程序已停止。

现在，让我们通过Zuul路由访问天气服务API并查看回退响应：

```shell
$ curl -s localhost:7070/weather/today
Weather information is not available.
```

## 4. 所有路由的回退

到目前为止，我们已经了解了如何使用其路由Id为Zuul路由创建回退。但是，假设我们还想**为我们应用程序中的所有其他路由创建一个通用回退**。我们可以通过创建FallbackProvider的另一个实现并**从getRoute方法返回*或null而不是路由Id**来实现这一点：

```java
@Override
public String getRoute() {
    return "*"; // or return null;
}
```

## 5. 总结

在本教程中，我们看到了为Zuul路由创建回退的示例。我们还看到了如何为所有Zuul路由创建通用回退。
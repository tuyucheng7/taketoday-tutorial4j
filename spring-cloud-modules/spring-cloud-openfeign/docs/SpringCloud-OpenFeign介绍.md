## 1. 概述

在本教程中，我们将描述[Spring Cloud OpenFeign](https://spring.io/projects/spring-cloud-openfeign)—一个用于Spring Boot应用程序的声明式REST客户端。

[Feign](https://www.baeldung.com/intro-to-feign)通过可插拔的注解支持(包括Feign注解和JAX-RS注解)使编写Web服务客户端变得更加容易。

此外，[Spring Cloud](https://www.baeldung.com/spring-cloud-series)添加了对[Spring MVC注解](https://www.baeldung.com/spring-mvc-annotations)的支持，并使用与Spring Web中使用的相同的[HttpMessageConverters](https://www.baeldung.com/spring-httpmessageconverter-rest)。

使用Feign的一个好处是我们不必编写任何代码来调用服务，除了接口定义。

## 2. 依赖关系

首先，我们将首先创建一个Spring Boot Web项目并将spring-cloud-starter-openfeign依赖项添加到我们的pom.xml文件中：

```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

此外，我们需要添加spring-cloud-dependencies：

```xml
<dependencyManagement>
	<dependencies>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-dependencies</artifactId>
			<version>${spring-cloud.version}</version>
			<type>pom</type>
			<scope>import</scope>
		</dependency>
	</dependencies>
</dependencyManagement>
```

我们可以在Maven Central上找到最新版本的[spring-cloud-starter-openfeign](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-starter-openfeign/4.0.1)和[spring-cloud-dependencies](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-dependencies/2022.0.1)。

## 3. Feign客户端

接下来，我们需要将@EnableFeignClients添加到我们的主类中：

```java
@SpringBootApplication
@EnableFeignClients
public class ExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExampleApplication.class, args);
	}
}
```

使用此注解，我们可以对声明为Feign客户端的接口启用组件扫描。

然后**我们使用@FeignClient注解声明一个Feign客户端**：

```java
@FeignClient(value = "jplaceholder", url = "https://jsonplaceholder.typicode.com/")
public interface JSONPlaceHolderClient {

	@RequestMapping(method = RequestMethod.GET, value = "/posts")
	List<Post> getPosts();

	@RequestMapping(method = RequestMethod.GET, value = "/posts/{postId}", produces = "application/json")
	Post getPostById(@PathVariable("postId") Long postId);
}
```

在此示例中，我们已将客户端配置为从[JSONPlaceholder API](https://jsonplaceholder.typicode.com/)中读取。

@FeignClient注解中传递的value参数是一个强制的、任意的客户端名称，而使用url参数，我们指定API基本URL。

此外，由于此接口是一个Feign客户端，因此我们可以使用Spring Web注解来声明我们想要访问的API。

## 4. 配置

现在，**了解每个Feign客户端都由一组可自定义的组件组成非常重要**。

Spring Cloud使用我们可以自定义的FeignClientsConfiguration类为每个命名客户端按需创建一个新的默认设置，如下一节所述。

上面的类包含这些bean：

-   Decoder：ResponseEntityDecoder，它包装了SpringDecoder，用于解码Response
-   Encoder：SpringEncoder用于对RequestBody进行编码
-   Logger：Slf4jLogger是Feign使用的默认记录器
-   Contract：SpringMvcContract，提供注解处理
-   Feign-Builder：HystrixFeign.Builder用于构建组件
-   Client：LoadBalancerFeignClient或默认的Feign客户端

### 4.1 自定义Bean配置

**如果我们想要自定义一个或多个这些bean，我们可以通过创建一个Configuration类来覆盖它们**，然后我们将其添加到FeignClient注解中：

```java
@FeignClient(value = "jplaceholder",
  	url = "https://jsonplaceholder.typicode.com/",
  	configuration = MyClientConfiguration.class)
```

```java
public class MyClientConfiguration {

	@Bean
	public OkHttpClient client() {
		return new OkHttpClient();
	}
}
```

在这个例子中，我们告诉Feign使用[OkHttpClient](https://www.baeldung.com/guide-to-okhttp)而不是默认的客户端来支持HTTP/2。

Feign支持针对不同用例的多个客户端，包括ApacheHttpClient，它随请求发送更多标头，例如Content-Length，这是某些服务器所期望的。

要使用这些客户端，请不要忘记将所需的依赖项添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-okhttp</artifactId>
</dependency>
<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-httpclient</artifactId>
</dependency>
```

我们可以在Maven Central上找到最新版本的[feign-okhttp](https://central.sonatype.com/artifact/io.github.openfeign/feign-okhttp/12.2)和[feign-httpclient](https://central.sonatype.com/artifact/io.github.openfeign/feign-httpclient/12.2)。

### 4.2 使用属性配置

我们**可以使用应用程序属性来配置Feign客户端，而不是使用配置类**，如application.yaml示例所示：

```yaml
feign:
    client:
        config:
            default:
                connectTimeout: 5000
                readTimeout: 5000
                loggerLevel: basic
```

使用此配置，我们将应用程序中每个声明的客户端的超时设置为5秒，将Logger级别设置为basic。

最后，我们可以创建以default作为客户端名称的配置来配置所有@FeignClient对象，或者我们可以为配置声明feign客户端名称：

```yaml
feign:
    client:
        config:
            jplaceholder:
```

如果我们同时拥有配置bean和配置属性，配置属性将覆盖配置bean的值。

## 5. 拦截器

**添加拦截器是Feign提供的另一个有用的功能**。

拦截器可以为每个HTTP请求/响应执行从身份验证到日志记录的各种隐式任务。

在本节中，我们将实现我们自己的拦截器，并使用Spring Cloud OpenFeign提供的开箱即用的拦截器。两者都会**为每个请求添加一个基本的身份验证标头**。

### 5.1 实现RequestInterceptor

让我们实现我们的自定义请求拦截器：

```java
@Bean
public RequestInterceptor requestInterceptor() {
  	return requestTemplate -> {
  	    requestTemplate.header("user", username);
  	    requestTemplate.header("password", password);
  	    requestTemplate.header("Accept", ContentType.APPLICATION_JSON.getMimeType());
  	};
}
```

此外，要将拦截器添加到请求链中，我们只需将此bean添加到我们的Configuration类中，或者如我们之前所见，在属性文件中声明它：

```yaml
feign:
    client:
        config:
            default:
                requestInterceptors:
                    cn.tuyucheng.taketoday.cloud.openfeign.JSONPlaceHolderInterceptor
```

### 5.2 使用BasicAuthRequestInterceptor

或者，我们可以使用Spring Cloud OpenFeign提供的BasicAuthRequestInterceptor类：

```java
@Bean
public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
    return new BasicAuthRequestInterceptor("username", "password");
}
```

就这么简单。现在所有请求都将包含基本身份验证标头。

## 6. Hystrix支持

**Feign支持[Hystrix](https://www.baeldung.com/spring-cloud-netflix-hystrix)，所以如果我们启用了它，我们就可以实现回退模式**。

使用回退模式，当远程服务调用失败时，服务消费者不会生成异常，而是会执行备用代码路径以尝试通过另一种方式执行操作。

为了实现这个目标，我们需要通过在属性文件中添加feign.hystrix.enabled=true来启用Hystrix。

这允许我们实现在服务失败时调用的回退方法：

```java
@Component
public class JSONPlaceHolderFallback implements JSONPlaceHolderClient {

	@Override
	public List<Post> getPosts() {
		return Collections.emptyList();
	}

	@Override
	public Post getPostById(Long postId) {
		return null;
	}
}
```

为了让Feign知道已经提供了回退方法，我们还需要在@FeignClient注解中设置我们的回退类：

```java
@FeignClient(value = "jplaceholder",
	url = "https://jsonplaceholder.typicode.com/",
	fallback = JSONPlaceHolderFallback.class)
public interface JSONPlaceHolderClient {
	// APIs
}
```

## 7. 日志记录

对于每个Feign客户端，默认情况下都会创建一个Logger。

要启用日志记录，我们应该使用客户端接口的包名称在application.propertie的文件中声明它：

```properties
logging.level.cn.tuyucheng.taketoday.cloud.openfeign.client=DEBUG
```

或者，如果我们只想为包中的一个特定客户端启用日志记录，我们可以使用完整的类名：

```properties
logging.level.cn.tuyucheng.taketoday.cloud.openfeign.client.JSONPlaceHolderClient=DEBUG
```

请注意，Feign日志记录仅响应DEBUG级别。

**我们可以为每个客户端配置的Logger.Level指示记录多少**：

```java
public class ClientConfiguration {

	@Bean
	Logger.Level feignLoggerLevel() {
		return Logger.Level.BASIC;
	}
}
```

有四种日志记录级别可供选择：

-   NONE：没有日志记录，这是默认设置
-   BASIC：只记录请求方法、URL和响应状态
-   HEADERS：记录基本信息以及请求和响应标头
-   FULL：记录请求和响应的正文、标头和元数据

## 8. 错误处理

Feign的默认错误处理程序ErrorDecoder.default总是抛出FeignException。

现在，这种行为并不总是最有用的。因此，**要自定义抛出的异常，我们可以使用CustomErrorDecoder**：

```java
public class CustomErrorDecoder implements ErrorDecoder {
	@Override
	public Exception decode(String methodKey, Response response) {

		switch (response.status()){
			case 400:
				return new BadRequestException();
			case 404:
				return new NotFoundException();
			default:
				return new Exception("Generic error");
		}
	}
}
```

然后，正如我们之前所做的那样，我们必须通过向Configuration类添加一个bean来替换默认的ErrorDecoder：

```java
public class ClientConfiguration {

	@Bean
	public ErrorDecoder errorDecoder() {
		return new CustomErrorDecoder();
	}
}
```

## 9. 总结

在本文中，我们讨论了Spring Cloud OpenFeign及其在一个简单示例应用程序中的实现。

我们还了解了如何配置客户端、向我们的请求添加拦截器以及使用Hystrix和ErrorDecoder处理错误。
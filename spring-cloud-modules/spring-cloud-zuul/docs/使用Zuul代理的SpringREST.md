## 1. 概述

在本文中，我们将探讨**单独部署的前端应用程序和REST API之间的通信**。

目标是解决浏览器的CORS和同源策略限制，并允许UI调用API，即使它们不共享相同的源。

我们基本上会创建两个独立的应用程序-一个UI应用程序和一个简单的REST API，我们将在UI应用程序中使用Zuul代理来代理对REST API的调用。

Zuul是Netflix的基于JVM的路由器和服务器端负载均衡器。Spring Cloud与嵌入式Zuul代理有很好的集成-这就是我们将在这里使用的。

### 延伸阅读

### [使用Zuul和Eureka进行负载均衡的示例](https://www.baeldung.com/zuul-load-balancing)

看看Netflix Zuul的负载均衡是什么样子的。

[阅读更多](https://www.baeldung.com/zuul-load-balancing)→

### [使用Springfox通过Spring REST API设置Swagger 2](https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api)

了解如何使用Swagger 2记录Spring REST API。

[阅读更多](https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api)→

### [Spring REST文档介绍](https://www.baeldung.com/spring-rest-docs)

本文介绍了Spring REST Docs，这是一种测试驱动的机制，可为RESTful服务生成既准确又可读的文档。

[阅读更多](https://www.baeldung.com/spring-rest-docs)→

## 2. Maven配置

首先，我们需要将Spring Cloud对Zuul支持的依赖添加到我们的UI应用程序的pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
    <version>2.2.0.RELEASE</version>
</dependency>
```

最新版本可以在[这里](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-starter-netflix-zuul/2.2.10.RELEASE)找到。

## 3. Zuul属性

接下来我们需要配置Zuul，由于我们使用的是Spring Boot，因此我们将在application.yml中进行配置：

```yaml
zuul:
    routes:
        foos:
            path: /foos/**
            url: http://localhost:8081/spring-zuul-foos-resource/foos
```

注意：

-   我们正在代理我们的资源服务器Foos。
-   来自UI的所有以“/foos/”开头的请求都将被路由到我们的Foos资源服务器，网址为http://loclahost:8081/spring-zuul-foos-resource/foos/

## 4. API

我们的API应用程序是一个简单的Spring Boot应用程序。

在本文中，我们将考虑部署在运行在**端口8081**上的服务器中的API。

让我们首先为我们将要使用的资源定义基本的DTO：

```java
public class Foo {
	private long id;
	private String name;

	// standard getters and setters
}
```

和一个简单的控制器：

```java
@RestController
public class FooController {

	@GetMapping("/foos/{id}")
	public Foo findById(
		@PathVariable long id, HttpServletRequest req, HttpServletResponse res) {
		return new Foo(Long.parseLong(randomNumeric(2)), randomAlphabetic(4));
	}
}
```

## 5. UI界面应用

我们的UI应用程序也是一个简单的Spring Boot应用程序。

在本文中，我们将考虑部署在运行在**端口8080**上的服务器中的API。

让我们从主要的index.html开始-使用一些AngularJS：

```html
<html>
<body ng-app="myApp" ng-controller="mainCtrl">
<script src="angular.min.js"></script>
<script src="angular-resource.min.js"></script>

<script>
	var app = angular.module('myApp', ["ngResource"]);

	app.controller('mainCtrl', function ($scope, $resource, $http) {
		$scope.foo = {id: 0, name: "sample foo"};
		$scope.foos = $resource("/foos/:fooId", {fooId: '@id'});

		$scope.getFoo = function () {
			$scope.foo = $scope.foos.get({fooId: $scope.foo.id});
		}
	});
</script>

<div>
	<h1>Foo Details</h1>
	<span>{{foo.id}}</span>
	<span>{{foo.name}}</span>
	<a href="#" ng-click="getFoo()">New Foo</a>
</div>
</body>
</html>
```

这里最重要的方面是我们如何**使用相对URL访问API**！

请记住，API应用程序未部署在与UI应用程序相同的服务器上，**因此相对URL不应该工作**，并且在没有代理的情况下将无法工作。

但是，使用代理，我们通过Zuul代理访问Foo资源，当然，该代理被配置为将这些请求路由到API实际部署的任何地方。

最后，实际启用Boot的应用程序：

```java
@EnableZuulProxy
@SpringBootApplication
public class UiApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(UiApplication.class, args);
	}
}
```

除了简单的Boot注解之外，请注意我们还为Zuul代理使用了enable风格注解，这非常酷、干净和简洁。

## 6. 测试路由

现在让我们测试我们的UI应用程序，如下所示：

```java
@Test
public void whenSendRequestToFooResource_thenOK() {
    Response response = RestAssured.get("http://localhost:8080/foos/1");
 
    assertEquals(200, response.getStatusCode());
}
```

## 7. 自定义Zuul过滤器

有多个Zuul过滤器可用，我们也可以创建自己的自定义过滤器：

```java
@Component
public class CustomZuulFilter extends ZuulFilter {

	@Override
	public Object run() {
		RequestContext ctx = RequestContext.getCurrentContext();
		ctx.addZuulRequestHeader("Test", "TestSample");
		return null;
	}

	@Override
	public boolean shouldFilter() {
		return true;
	}
	
	// ...
}
```

这个简单的过滤器只是向请求添加一个名为“Test”的标头-当然，我们可以根据需要在此处增加请求的复杂程度。

## 8. 测试自定义Zuul过滤器

最后，让我们测试以确保我们的自定义过滤器正常工作-首先我们将在Foos资源服务器上修改我们的FooController：

```java
@RestController
public class FooController {

	@GetMapping("/foos/{id}")
	public Foo findById(@PathVariable long id, HttpServletRequest req, HttpServletResponse res) {
		if (req.getHeader("Test") != null) {
			res.addHeader("Test", req.getHeader("Test"));
		}
		return new Foo(Long.parseLong(randomNumeric(2)), randomAlphabetic(4));
	}
}
```

现在，让我们测试一下：

```java
@Test
public void whenSendRequest_thenHeaderAdded() {
    Response response = RestAssured.get("http://localhost:8080/foos/1");
 
    assertEquals(200, response.getStatusCode());
    assertEquals("TestSample", response.getHeader("Test"));
}
```

## 9. 总结

在这篇文章中，我们重点介绍了使用Zuul将请求从UI应用程序路由到REST API。我们成功地解决了CORS和同源策略，并且我们还设法自定义和扩充了传输中的HTTP请求。
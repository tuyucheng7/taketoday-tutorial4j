## 1. 概述

在本文中，我们将探讨单独部署的前端应用程序和 REST API 之间的通信。

目标是解决浏览器的 CORS 和同源策略限制，并允许 UI 调用 API，即使它们不共享同源。

我们基本上会创建两个独立的应用程序——一个 UI 应用程序和一个简单的 REST API，我们将在 UI 应用程序中使用Zuul 代理来代理对 REST API 的调用。

Zuul 是 Netflix 的基于 JVM 的路由器和服务器端负载均衡器。Spring Cloud 与嵌入式 Zuul 代理有很好的集成——这就是我们将在这里使用的。

## 延伸阅读：

## [使用 Zuul 和 Eureka 进行负载均衡的示例](https://www.baeldung.com/zuul-load-balancing)

看看 Netflix Zuul 的负载均衡是什么样子的。

[阅读更多](https://www.baeldung.com/zuul-load-balancing)→

## [使用 Springfox 通过 Spring REST API 设置 Swagger 2](https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api)

了解如何使用 Swagger 2 记录 Spring REST API。

[阅读更多](https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api)→

## [Spring REST 文档介绍](https://www.baeldung.com/spring-rest-docs)

本文介绍了 Spring REST Docs，这是一种测试驱动的机制，可为 RESTful 服务生成既准确又可读的文档。

[阅读更多](https://www.baeldung.com/spring-rest-docs)→

## 2.Maven配置

首先，我们需要将 Spring Cloud 对 zuul 支持的依赖添加到我们的 UI 应用程序的pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
    <version>2.2.0.RELEASE</version>
</dependency>
```

最新版本可以在[这里](https://search.maven.org/search?q=g:org.springframework.cloud AND a:spring-cloud-starter-netflix-zuul)找到。

## 3. Zuul 属性

接下来——我们需要配置 Zuul，因为我们使用的是 Spring Boot，所以我们将在application.yml中进行配置：

```bash
zuul:
  routes:
    foos:
      path: /foos/
      url: http://localhost:8081/spring-zuul-foos-resource/foos
```

注意：

-   我们正在代理我们的资源服务器Foos。
-   来自 UI 的所有以“ /foos/ ”开头的请求都将被路由到我们的Foos资源服务器，网址为http://loclahost:8081/spring-zuul-foos-resource/foos/

## 4. API

我们的 API 应用程序是一个简单的Spring Boot应用程序。

在本文中，我们将考虑部署在运行在端口 8081 上的服务器中的 API。

让我们首先为我们将要使用的资源定义基本的 DTO：

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

## 5. 用户界面应用

我们的 UI 应用程序也是一个简单的Spring Boot应用程序。

在本文中，我们将考虑部署在运行在端口 8080 上的服务器中的 API。

让我们从主要的index.html开始——使用一点 AngularJS：

```html
<html>
<body ng-app="myApp" ng-controller="mainCtrl">
<script src="angular.min.js"></script>
<script src="angular-resource.min.js"></script>

<script>
var app = angular.module('myApp', ["ngResource"]);

app.controller('mainCtrl', function($scope,$resource,$http) {
    $scope.foo = {id:0 , name:"sample foo"};
    $scope.foos = $resource("/foos/:fooId",{fooId:'@id'});
    
    $scope.getFoo = function(){
        $scope.foo = $scope.foos.get({fooId:$scope.foo.id});
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

这里最重要的方面是我们如何使用相对 URL 访问 API！

请记住，API 应用程序未部署在与 UI 应用程序相同的服务器上，因此相对 URL 不应该工作，并且在没有代理的情况下将无法工作。

然而，使用代理，我们通过 Zuul 代理访问Foo资源，当然，它被配置为将这些请求路由到 API 实际部署的任何地方。

最后，实际启用 Boot 的应用程序：

```java
@EnableZuulProxy
@SpringBootApplication
public class UiApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(UiApplication.class, args);
    }
}
```

除了简单的 Boot 注解之外，请注意我们还为 Zuul 代理使用了 enable-style 注解，这非常酷、干净和简洁。

## 6.测试路由

现在 - 让我们测试我们的 UI 应用程序 - 如下所示：

```java
@Test
public void whenSendRequestToFooResource_thenOK() {
    Response response = RestAssured.get("http://localhost:8080/foos/1");
 
    assertEquals(200, response.getStatusCode());
}
```

## 7. 自定义 Zuul 过滤器

有多个 Zuul 过滤器可用，我们也可以创建自己的自定义过滤器：

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

这个简单的过滤器只是向请求添加一个名为“ Test ”的标头——当然，我们可以根据需要在此处增加请求的复杂程度。

## 8. 测试自定义 Zuul 过滤器

最后，让我们测试以确保我们的自定义过滤器正常工作——首先我们将在 Foos 资源服务器上修改我们的FooController ：

```java
@RestController
public class FooController {

    @GetMapping("/foos/{id}")
    public Foo findById(
      @PathVariable long id, HttpServletRequest req, HttpServletResponse res) {
        if (req.getHeader("Test") != null) {
            res.addHeader("Test", req.getHeader("Test"));
        }
        return new Foo(Long.parseLong(randomNumeric(2)), randomAlphabetic(4));
    }
}
```

现在 - 让我们测试一下：

```java
@Test
public void whenSendRequest_thenHeaderAdded() {
    Response response = RestAssured.get("http://localhost:8080/foos/1");
 
    assertEquals(200, response.getStatusCode());
    assertEquals("TestSample", response.getHeader("Test"));
}
```

## 9.总结

在这篇文章中，我们专注于使用 Zuul 将请求从 UI 应用程序路由到 REST API。我们成功地解决了 CORS 和同源策略，并且我们还设法自定义和扩充了传输中的 HTTP 请求。
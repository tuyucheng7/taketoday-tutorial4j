## 1. 简介

Spring 5引入了WebFlux，这是一个新的框架，可以让我们使用响应式编程模型构建Web应用程序。

在本教程中，我们将了解如何将此编程模型应用于Spring MVC中的函数式控制器。

## 2. Maven设置

我们将使用[Spring Boot](https://www.baeldung.com/spring-boot)来演示新的API。

该框架支持常见的基于注解的控制器定义方法，但它还添加了一种新的特定于域的语言，该语言提供了一种定义控制器的函数式方法。

**从Spring 5.2开始，函数式方法也将在Spring Web MVC框架中可用**。与WebFlux模块一样，RouterFunctions和RouterFunction是该API的主要抽象。

对于Maven，我们导入spring-boot-starter-web依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

## 3. RouterFunction与@Controller

**在函数式领域，Web服务被称为路由**，而传统的@Controller和@RequestMapping概念被RouterFunction所取代。

为了创建我们的第一个服务，让我们以一个基于注解的服务为例，看看如何将它转换成它的函数式等价物。

我们将使用返回产品目录中所有产品的服务示例：

```java
@RestController
public class ProductController {

    @RequestMapping("/product")
    public List<Product> productListing() {
        return ps.findAll();
    }
}
```

现在，让我们看看它的函数式端点的等效定义：

```java
@Bean
public RouterFunction<ServerResponse> productListing(ProductService ps) {
    return route().GET("/product", req -> ok().body(ps.findAll()))
        .build();
}
```

### 3.1 路由定义

我们应该注意到，在函数式方法中，productListing()方法返回的是RouterFunction而不是响应体。**这是路由的定义，而不是请求的执行**。

RouterFunction包括路径、请求标头、用于生成响应正文和响应头的处理程序函数，它可以包含单个或一组Web服务。

当我们查看嵌套路由时，我们将更详细地介绍Web服务组。

在这个例子中，**我们使用了RouterFunctions中的静态方法route()来创建一个RouterFunction**，可以使用此方法提供路由的所有请求和响应属性。

### 3.2 请求谓词

在我们的示例中，我们使用route()上的GET()方法来指定这是一个GET请求，路径以字符串形式提供。

**当我们想要指定请求的更多细节时，我们也可以使用RequestPredicate**。例如，上一个示例中的路径也可以使用RequestPredicate指定为：

```java
RequestPredicates.path("/product")
```

在这里，**我们使用RequestPredicates的静态方法path来创建RequestPredicate对象**。

### 3.3 响应

类似地，**ServerResponse包含用于创建响应对象的静态工具方法**。

在我们的示例中，我们使用ok()将HTTP状态码200添加到响应头，然后使用body()指定响应体。

此外，ServerResponse支持使用EntityResponse从自定义数据类型构建响应，我们也可以通过RenderingResponse使用Spring MVC的ModelAndView。

### 3.4 注册路由

接下来，我们使用@Bean注解注册该路由，将其添加到应用程序上下文中：

```java
@SpringBootApplication
public class SpringBootMvcFnApplication {

    @Bean
    RouterFunction<ServerResponse> productListing(ProductController pc, ProductService ps) {
        return pc.productListing(ps);
    }
}
```

## 4. 嵌套路由

在应用程序中包含一堆Web服务并根据功能或实体将它们划分为逻辑组是很常见的，例如，我们可能希望所有与产品相关的服务都以/product开头。

我们在现有路径/product中添加另一个路径，用于按名称查找产品：

```java
public RouterFunction<ServerResponse> productSearch(ProductService ps) {
    return route().nest(RequestPredicates.path("/product"), builder -> {
        builder.GET("/name/{name}", req -> ok().body(ps.findByName(req.pathVariable("name"))));
    }).build();
}
```

在传统方法中，我们可以通过将路径传递给@Controller来实现这一点，**而分组Web服务的等效函数式方法是route()上的nest()方法**。

在这里，我们首先提供要在其下对新路由进行分组的路径，即/product；接下来，我们使用builder对象添加路由，与前面的示例类似。

nest()方法负责将添加到builder对象的路由与主RouterFunction合并。

## 5. 错误处理

另一个常见的用例是使用自定义错误处理机制，**我们可以使用route()上的onError()方法来定义一个自定义异常处理程序**。

这相当于在基于注解的方法中使用@ExceptionHandler，但这种方法更加灵活，因为它可以用于为每组路由定义单独的异常处理程序。

我们在之前创建的产品搜索路径中添加一个异常处理程序，以处理在找不到产品时引发的自定义异常：

```java
public RouterFunction<ServerResponse> productSearch(ProductService ps) {
    return route()...
        .onError(ProductService.ItemNotFoundException.class,
                 (e, req) -> EntityResponse.fromObject(new Error(e.getMessage()))
                 .status(HttpStatus.NOT_FOUND)
                 .build())
        .build();
}
```

onError()方法接收Exception类对象，并期望函数实现提供ServerResponse。

我们使用了EntityResponse，它是ServerResponse的子类型，在这里从自定义数据类型Error构建响应对象；然后我们添加状态码并使用EntityResponse.build()返回一个ServerResponse对象。

## 6. 过滤器

实现身份验证以及管理横切关注点(例如日志记录和审计)的常用方法是使用过滤器，**过滤器用于决定是继续还是中止请求的处理**。

举个例子，我们需要一个将产品添加到目录的新路由：

```java
public RouterFunction<ServerResponse> adminFunctions(ProductService ps) {
    return route().POST("/product", req -> ok().body(ps.save(req.body(Product.class))))
        .onError(IllegalArgumentException.class, 
                 (e, req) -> EntityResponse.fromObject(new Error(e.getMessage()))
                 .status(HttpStatus.BAD_REQUEST)
                 .build())
        .build();
}
```

由于添加操作是一个管理员功能，因此我们需要对调用服务的用户进行身份验证。

**我们可以通过在route()上添加一个filter()方法来做到这一点**：

```java
public RouterFunction<ServerResponse> adminFunctions(ProductService ps) {
   return route().POST("/product", req -> ok().body(ps.save(req.body(Product.class))))
       .filter((req, next) -> authenticate(req) ? next.handle(req) : 
               status(HttpStatus.UNAUTHORIZED).build())
       ....;
}
```

在这里，由于filter()方法提供请求以及next处理程序，我们使用它进行简单的身份验证，如果成功则允许保存产品，或者在失败时向客户端返回UNAUTHORIZED错误。

## 7. 横切关注点

**有时，我们可能希望在请求之前、之后或前后执行一些操作**；例如，我们可能需要记录传入请求和返回响应的一些属性。

让我们在每次应用程序发现与传入请求匹配时记录一条语句，**我们将使用route()上的before()方法来完成此操作**：

```java
@Bean
RouterFunction<ServerResponse> allApplicationRoutes(ProductController pc, ProductService ps) {
    return route()...
        .before(req -> {
            LOG.info("Found a route which matches " + req.uri().getPath());
            return req;
        })
        .build();
}
```

类似地，**我们可以在处理请求后使用route()上的after()方法添加一个简单的日志语句**：

```java
@Bean
RouterFunction<ServerResponse> allApplicationRoutes(ProductController pc, ProductService ps) {
    return route()...
        .after((req, res) -> {
            if (res.statusCode() == HttpStatus.OK) {
                LOG.info("Finished processing request " + req.uri().getPath());
            } else {
                LOG.info("There was an error while processing request" + req.uri());
            }
            return res;
        })          
        .build();
}
```

## 8. 总结

在本教程中，我们首先简要介绍了定义控制器的函数式方法，然后，我们将Spring MVC注解与其函数式等价物进行了比较。

接下来，我们实现了一个简单的Web服务，它通过函数式控制器返回一个产品集合。

然后，我们着手实现Web服务控制器的一些常见用例，包括嵌套路由、错误处理、添加访问控制过滤器以及管理横切关注点(如日志记录)。
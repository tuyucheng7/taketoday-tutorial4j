## 1. 概述

在本教程中，我们将了解用于REST API开发的[JAX-RS]()和[Spring MVC]()之间的区别。

## 2. Jakarta RESTful Web服务

要成为[JAVA EE]()世界的一部分，功能部件必须具有规范、兼容的实现和[TCK](https://en.wikipedia.org/wiki/Technology_Compatibility_Kit)。相应地，**JAX-RS是一组用于构建REST服务的规范，其最著名的参考实现是**[RESTEasy]()和[Jersey]()。

现在，让我们通过实现一个简单的控制器来熟悉一下Jersey：

```java
@Path("/hello")
public class HelloController {

    @GET
    @Path("/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response hello(@PathParam("name") String name) {
        return Response.ok("Hello, " + name).build();
    }
}
```

上面，端点返回一个简单的“text/plain”响应，如注解@Produces所述。特别是，我们公开了一个hello HTTP资源，它接收一个名为name的参数，带有两个@Path注解。我们还需要指定它是一个GET请求，因此使用注解@GET。

## 3. REST与Spring MVC

**Spring MVC是Spring Framework的一个模块，用于创建Web应用程序，它为Spring Framework添加了REST功能**。

让我们使用Spring MVC创建与上面等效的GET请求示例：

```java
@RestController
@RequestMapping("/hello")
public class HelloController {

    @GetMapping(value = "/{name}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> hello(@PathVariable String name) {
        return new ResponseEntity<>("Hello, " + name, HttpStatus.OK);
    }
}
```

查看代码，@RequestMapping指出我们正在处理一个hello HTTP资源。特别是，通过@GetMapping注解，我们指定它是一个GET请求，它接收一个名为name的参数并返回一个“text/plain”响应。

## 4. 差异

JAX-RS依赖于提供一组Java注解并将它们应用于普通Java对象，事实上，这些注解帮助我们抽象出客户端-服务器通信的底层细节。为了简化它们的实现，它提供了注解来处理HTTP请求和响应并将它们绑定到代码中。**JAX-RS只是一个规范，它需要一个兼容的实现才能使用**。

另一方面，**Spring MVC是一个具有REST功能的完整框架**，与JAX-RS一样，它也为我们提供了有用的注解来从低级细节中抽象出来。它的主要优势是成为[Spring Framework]()生态系统的一部分，因此，它允许我们像使用任何其他Spring模块一样使用依赖注入。此外，它可以轻松地与其他组件集成，例如[Spring AOP]()、[Spring Data REST]()和[Spring Security]()。

## 5. 总结

在这篇简短的文章中，我们了解了JAX-RS和Spring MVC之间的主要区别。
## 1. 概述

在本教程中，我们将了解如何在属性文件中 设置[@RequestMapping值。](https://www.baeldung.com/spring-requestmapping)此外，我们将使用一个实际示例来解释所有必要的配置。

首先，让我们定义一个基本的@RequestMapping及其配置。

## 2.@ RequestMapping基础

首先，我们将创建WelcomeController类并 使用@RequestMapping对其进行注解以映射 Web 请求。此类将分配我们的处理程序方法getWelcomeMessage()。

那么，让我们定义它：

```java
@RestController
@RequestMapping("/welcome")
public class WelcomeController {

   @GetMapping
   public String getWelcomeMessage() {
       return "Welcome to Baeldung!";
   }
}
```

此外，值得注意的是，我们将使用@GetMapping 注解getWelcomeMessage () 以 仅映射 GET 请求。如我们所见，我们为路径使用了硬编码字符串，静态指示我们要访问的路径。有了这个配置，我们就可以完美的访问到我们感兴趣的资源了，如下图：

```bash
curl http://localhost:9006/welcome
> Welcome to Baeldung!
```

但是，如果我们想让路径依赖于配置参数怎么办？正如我们接下来要看到的，我们可以使用application.properties。

## 3.@ RequestMapping和属性文件

首先，正如我们在[文档](https://docs.spring.io/spring-framework/docs/3.2.16.RELEASE/spring-framework-reference/html/mvc.html)中看到的那样，@RequestMapping注解中的模式 支持针对本地属性和/或系统属性和环境变量的 ${...} 占位符。因此，通过这种方式，我们可以将我们的属性文件链接到我们的控制器。

一方面，我们需要自己创建属性文件。我们将把它放在资源文件夹中并将其命名为application.properties。然后，我们必须使用我们选择的名称创建属性。在我们的例子中，我们将设置名称welcome-controller.path 并将我们想要的值设置为请求的端点。现在，我们的 application.properties看起来像这样：

```properties
welcome-controller.path=welcome
```

另一方面，我们必须修改我们在@RequestMapping中静态建立的路径，以便它读取我们创建的新属性：

```java
@RestController
@RequestMapping("/${welcome-controller.path}")
public class WelcomeController {
    @GetMapping
    public String getWelcomeMessage() {
        return "Welcome to Baeldung!";
    }
}
```

因此，通过这种方式，Spring 将能够映射端点的值，并且当用户访问此 URL 时，此方法将负责处理它。我们可以在下面看到如何使用相同的请求显示相同的消息：

```bash
curl http://localhost:9006/welcome 
> Welcome to Baeldung!
```

## 4. 总结

在这篇简短的文章中，我们学习了如何在属性文件中设置@RequestMapping值。此外，我们还创建了一个功能齐全的示例，帮助我们理解所解释的概念。
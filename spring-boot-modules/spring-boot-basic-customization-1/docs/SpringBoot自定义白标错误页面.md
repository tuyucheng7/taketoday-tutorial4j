## 1. 概述

在本文中，我们将学习如何**禁用和自定义Spring Boot应用程序的默认错误页面**，因为正确的错误处理描述了专业和高质量的工作。

## 2. 禁用白标错误页面

首先，我们将了解如何通过将server.error.whitelabel.enabled属性设置为false来完全禁用白标错误页面：

```properties
server.error.whitelabel.enabled=false
```

将此属性添加到application.properties文件中将禁用错误页面，并显示源自底层应用程序容器(例如Tomcat)的简洁页面。

**我们可以通过排除ErrorMvcAutoConfiguration bean来实现相同的结果**。我们可以通过将此条目添加到属性文件来实现：

```properties
#for Spring Boot 1.0
#spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration

#for Spring Boot 2.0
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
```

或者我们可以在主类中添加这个注解：

```java
@EnableAutoConfiguration(exclude = {ErrorMvcAutoConfiguration.class})
```

上面提到的所有方法都会禁用白标错误页面，这给我们留下了一个问题，即谁来实际处理错误？

嗯，如上所述，它通常是底层应用程序容器。好消息是我们可以通过显示自定义错误页面而不是所有默认值来进一步自定义内容。这是下一节的重点。

## 3. 显示自定义错误页面

我们首先需要创建一个自定义HTML错误页面。

**我们将文件保存为error.html**，因为我们使用的是Thymeleaf模板引擎：

```html
<!DOCTYPE html>
<html lang="en">
<body>
<h1> Something went wrong! </h1>
<h2>Our Engineers are on it.</h2>
<p><a href="/">Go Home</a></p>
</body>
</html>
```

**如果我们将这个文件保存在resources/templates目录中，它会被默认的Spring Boot的BasicErrorController自动获取**。

这就是我们显示自定义错误页面所需的全部内容。通过一些样式，我们可以为我们的用户提供一个更好看的错误页面：

<img src="../assets/img.png">

我们还可以通过使用我们希望它使用的HTTP状态代码命名文件来更具体；例如，将文件保存为resources/templates/error中的404.html意味着它将明确用于404错误。

### 3.1 自定义ErrorController

到目前为止的限制是我们无法在发生错误时运行自定义逻辑。为了实现这一点，我们必须创建一个ErrorController bean来替换默认的bean。

为此，**我们必须创建一个实现ErrorController接口的类**，此外，我们需要设置server.error.path属性，以返回发生错误时要调用的自定义路径。

```java
@Controller
public class MyErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError() {
        //do something like logging
        return "error";
    }
}
```

在上面的代码片段中，我们还使用@Controller标注了类，并为指定为属性server.error.path的路径创建了一个映射：

```properties
server.error.path=/error
```

这样，控制器可以处理对/error路径的调用。

在handleError()中，我们将返回我们之前创建的自定义错误页面。如果我们现在触发404错误，将显示我们的自定义页面。

**让我们进一步增强handleError()以显示不同错误类型的特定错误页面**。

例如，我们可以专门针对404和500错误类型设计精美的页面，然后我们可以使用错误的HTTP状态码来确定一个合适的错误页面来显示：

```java
@Controller
public class MyErrorController implements ErrorController {

    @GetMapping(value = "/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "error-404";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "error-500";
            }
        }
        return "error";
    }
}
```

例如，对于404错误，我们将看到error-404.html页面：

<img src="../assets/img_1.png">

## 4. 总结

有了这些信息，我们现在可以更优雅地处理错误并向我们的用户展示一个美观的页面。
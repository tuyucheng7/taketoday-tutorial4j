## 1. 概述

任何 Web 应用程序中的一个常见要求是自定义错误页面。

例如，假设你正在 Tomcat 上运行一个普通的Spring MVC应用程序。用户在他的浏览器中输入了一个无效的URL，并显示了一个不太用户友好的蓝色和白色堆栈跟踪——不理想。

在本教程中，我们将为一些HTTP错误代码设置自定义错误页面。

工作假设是读者对使用Spring MVC相当满意；如果没有，[这是一个很好的开始方式](https://www.baeldung.com/spring-mvc-tutorial)。

本文重点介绍Spring MVC。我们的文章 [Customize Whitelabel Error Page](https://www.baeldung.com/spring-boot-custom-error-page) 描述了如何在Spring Boot中创建自定义错误页面。

## 2. 简单步骤

让我们从这里要遵循的简单步骤开始：

1.  在web.xml中指定一个URL/errors映射到一个方法，该方法将在生成错误时处理错误
2.  使用映射/errors创建一个名为ErrorController的控制器
3.  在运行时找出HTTP错误代码，并根据HTTP错误代码显示消息。例如，如果生成 404 错误，则用户应该看到类似“未找到资源”的消息，而对于 500 错误，用户应该看到类似“对不起！”的内容。我们这边产生了一个内部服务器错误'

## 3.web.xml _

我们首先将以下行添加到我们的web.xml中：

```xml
<error-page>
    <location>/errors</location>
</error-page>
```

请注意，此功能仅在大于 3.0 的 Servlet 版本中可用。

应用程序中生成的任何错误都与HTTP错误代码相关联。例如，假设用户在浏览器中输入一个URL/invalidUrl ，但是Spring 内部还没有定义这样的RequestMapping 。然后，由底层 Web 服务器生成 404 的HTTP代码。我们刚刚添加到web.xml 的行告诉Spring执行映射到URL/errors 的方法中编写的逻辑。


这里有一个简短的旁注——不幸的是，相应的JavaServlet 配置没有用于设置错误页面的 API——所以在这种情况下，我们实际上需要web.xml。

## 4.控制器

继续，我们现在创建我们的ErrorController。我们创建一个单一的统一方法来拦截错误并显示错误页面：

```java
@Controller
public class ErrorController {

    @RequestMapping(value = "errors", method = RequestMethod.GET)
    public ModelAndView renderErrorPage(HttpServletRequest httpRequest) {
        
        ModelAndView errorPage = new ModelAndView("errorPage");
        String errorMsg = "";
        int httpErrorCode = getErrorCode(httpRequest);

        switch (httpErrorCode) {
            case 400: {
                errorMsg = "Http Error Code: 400. Bad Request";
                break;
            }
            case 401: {
                errorMsg = "Http Error Code: 401. Unauthorized";
                break;
            }
            case 404: {
                errorMsg = "Http Error Code: 404. Resource not found";
                break;
            }
            case 500: {
                errorMsg = "Http Error Code: 500. Internal Server Error";
                break;
            }
        }
        errorPage.addObject("errorMsg", errorMsg);
        return errorPage;
    }
    
    private int getErrorCode(HttpServletRequest httpRequest) {
        return (Integer) httpRequest
          .getAttribute("javax.servlet.error.status_code");
    }
}

```

## 5.前端

出于演示目的，我们将保持我们的错误页面非常简单和紧凑。此页面将仅包含显示在白色屏幕上的消息。创建一个名为 errorPage.jsp 的jsp文件：

```html
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html>
<head>
    <title>Home</title>
</head>
<body>
    <h1>${errorMsg}</h1>
</body>
</html>
```

## 6. 测试

我们将模拟在任何应用程序中发生的两种最常见的错误：HTTP 404 错误和HTTP 500错误。

运行服务器并转到localhost:8080/spring-mvc-xml/invalidUrl。由于此URL不存在，我们希望看到带有消息“ Http 错误代码：404。未找到资源”的错误页面。

让我们看看当处理程序方法之一抛出NullPointerException 时会发生什么。我们在 ErrorController 中添加以下方法：

```java
@RequestMapping(value = "500Error", method = RequestMethod.GET)
public void throwRuntimeException() {
    throw new NullPointerException("Throwing a null pointer exception");
}
```

转到localhost:8080/spring-mvc-xml/500Error。你应该会看到一个白色屏幕，其中显示消息“Http 错误代码：500。内部服务器错误”。

## 七、总结

我们看到了如何使用Spring MVC为不同的HTTP代码设置错误页面。我们创建了一个错误页面，其中根据HTTP错误代码动态显示错误消息。
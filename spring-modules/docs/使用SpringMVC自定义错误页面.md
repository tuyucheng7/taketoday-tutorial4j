## **一、概述**

任何 Web 应用程序中的一个常见要求是自定义错误页面。

例如，假设您正在 Tomcat 上运行一个普通的 Spring MVC 应用程序。用户在他的浏览器中输入了一个无效的 URL，并显示了一个不太用户友好的蓝色和白色堆栈跟踪——不理想。

在本教程中，我们将为一些 HTTP 错误代码设置自定义错误页面。

工作假设是读者对使用 Spring MVC 相当满意；如果没有，[这是一个很好的开始方式](https://www.baeldung.com/spring-mvc-tutorial)。

本文重点介绍 Spring MVC。我们的文章 [Customize Whitelabel Error Page](https://www.baeldung.com/spring-boot-custom-error-page) 描述了如何在 Spring Boot 中创建自定义错误页面。

## **2. 简单步骤**

让我们从这里要遵循的简单步骤开始：

1.  在*web.xml*中指定一个 URL */errors*映射到一个方法，该方法将在生成错误时处理错误
2.  使用映射*/errors创建一个名为**ErrorController*的控制器
3.  在运行时找出 HTTP 错误代码，并根据 HTTP 错误代码显示消息*。**例如，如果生成 404 错误，则用户应该看到类似“未找到资源”的*消息，而对于 500 错误，用户应该看到类似*“对不起！”*的内容。*我们这边产生了一个内部服务器错误'*

## **3.web.xml \*_\***

我们首先将以下行添加到我们的*web.xml中**：***

```xml
<error-page>
    <location>/errors</location>
</error-page>复制
```

请注意，此功能仅在大于 3.0 的 Servlet 版本中可用。

应用程序中生成的任何错误都与 HTTP 错误代码相关联。例如，假设用户在浏览器中输入一个 URL */invalidUrl* ，但是Spring 内部还没有定义这样的*RequestMapping 。*然后，由底层 Web 服务器生成 404 的 HTTP 代码。*我们刚刚添加到web.xml 的*行告诉 Spring 执行映射到 URL /errors 的方法中编写的逻辑*。
*

这里有一个简短的旁注——不幸的是，相应的 Java Servlet 配置没有用于设置错误页面的 API——所以在这种情况下，我们实际上需要*web.xml*。

## **4.控制器**

继续，我们现在创建我们的*ErrorController*。我们创建一个单一的统一方法来拦截错误并显示错误页面：

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
复制
```

## **5.前端**

出于演示目的，我们将保持我们的错误页面非常简单和紧凑。此页面将仅包含显示在白色屏幕上的消息。创建一个名为 errorPage.jsp 的*jsp*文件*：*

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
</html>复制
```

## **6. 测试**

我们将模拟在任何应用程序中发生的两种最常见的错误：HTTP 404 错误和 HTTP 500 错误。

运行服务器并转到*localhost:8080/spring-mvc-xml/invalidUrl。*由于此 URL 不存在，我们希望看到带有消息“ *Http 错误代码：404。未找到资源”的错误页面。*

让我们看看当处理程序方法之一抛出*NullPointerException 时会发生什么。*我们在 ErrorController 中添加以下方法*：*

```java
@RequestMapping(value = "500Error", method = RequestMethod.GET)
public void throwRuntimeException() {
    throw new NullPointerException("Throwing a null pointer exception");
}复制
```

转到*localhost:8080/spring-mvc-xml/500Error。*您应该会看到一个白色屏幕，其中显示消息“Http 错误代码：500。内部服务器错误”。

## **七、结论**

我们看到了如何使用 Spring MVC 为不同的 HTTP 代码设置错误页面*。*我们创建了一个错误页面，其中根据 HTTP 错误代码动态显示错误消息。
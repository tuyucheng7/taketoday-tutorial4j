## 1. 概述

在本教程中，我们想看看如何从Spring MVC控制器返回HTML。

让我们来看看需要做什么。

## 2. Maven依赖

首先，我们必须为我们的MVC控制器添加[spring-boot-starter-web](https://search.maven.org/artifact/cn.org.faster/spring-boot-starter-web) Maven依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <versionId>1.3.7.RELEASE</versionId>
</dependency>
```

## 3. 控制器

接下来，让我们创建我们的控制器：

```java
@Controller
public class HtmlController {
    @GetMapping(value = "/welcome", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String welcomeAsHTML() {
        return "<html>\n" + "<header><title>Welcome</title></header>\n" +
              "<body>\n" + "Hello world\n" + "</body>\n" + "</html>";
    }
}
```

我们使用[@Controller](https://www.baeldung.com/spring-controllers)注解来告诉[DispatcherServlet](https://www.baeldung.com/spring-dispatcherservlet)这个类处理HTTP请求。

接下来，我们配置[@GetMapping](https://www.baeldung.com/spring-new-requestmapping-shortcuts)注解以生成MediaType.TEXT_HTML_VALUE输出。

最后，@ResponseBody注解告诉控制器返回的对象应该自动序列化为配置的媒体类型，即TEXT_HTML_VALUE或text/html。

如果没有这最后一个注解，我们将收到404错误，因为默认情况下String返回值引用视图名称。

有了那个控制器，我们就可以测试它了：

```bash
curl -v localhost:8081/welcome
```

输出将类似于：

```bash
> ... request ...
>
< HTTP/1.1 200
< Content-Type: text/html;charset=UTF-8
< ... other response headers ...
<

<html>
<header><title>Welcome</title></header>
<body>
Hello world
</body>
</html>
```

正如预期的那样，我们看到响应的Content-Type是text/html。此外，我们看到响应也有正确的HTML内容。

## 4. 总结

在本文中，我们研究了如何从Spring MVC控制器返回HTML。
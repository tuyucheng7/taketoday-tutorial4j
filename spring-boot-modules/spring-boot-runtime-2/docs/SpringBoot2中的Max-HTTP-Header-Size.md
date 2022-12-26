## 1. 概述

默认情况下，[Spring Boot]() Web应用程序包括一个预配置的嵌入式Web服务器。但是，在某些情况下，我们希望修改默认配置以满足自定义要求。

在本教程中，我们将了解如何在Spring Boot 2.x应用程序的application.properties文件中为请求标头设置和使用max-http-header-size属性。

## 2. Max-HTTP-Header-Size

Spring Boot支持[Tomcat]()、[Undertow]()和[Jetty]()作为嵌入式服务器。通常，我们在Spring Boot应用程序的application.properties文件或application.yaml文件中编写服务器配置。

大多数Web服务器对HTTP请求标头都有自己的一组大小限制，HTTP标头值受服务器实现的限制。在Spring Boot应用程序中，Max-HTTP-Header-Size使用server.max-http-header-size配置。

Tomcat和Jetty的实际默认值为8kB，而Undertow的默认值为1MB。

若要修改最大HTTP标头大小，我们会将下面属性添加到我们的application.properties文件中：

```properties
server.max-http-header-size=20000
```

同样，对于application.yaml格式：

```yaml
server:
    max-http-header-size: 20000
```

从Spring Boot 2.1开始，我们现在将使用DataSize可解析的值：

```properties
server.max-http-header-size=10KB
```

## 3. 请求头过大

假设发送的请求的总HTTP标头大小大于max-http-header-size值，服务器以“400 Bad request”错误拒绝请求。在下一个示例中，我们将在日志文件中看到此错误。

让我们创建一个控制器，它有一个名为token的标头属性：

```java
@RestController
@RequestMapping(value = "/request-header-test")
public class MaxHttpHeaderSizeController {
	@GetMapping
	public boolean testMaxHTTPHeaderSize(@RequestHeader(value = "token") String token) {
		return true;
	}
}
```

接下来，让我们向application.properties文件添加一些属性：

```properties
## Server connections configuration
server.tomcat.threads.max=200
server.connection-timeout=5s
server.max-http-header-size=8KB
server.tomcat.max-swallow-size=2MB
server.tomcat.max-http-post-size=2MB
```

当我们在token中传递一个大小大于8kb的字符串值时，我们将得到400错误，如下所示：

<img src="../assets/img.png">

在日志中，我们看到以下错误：

```shell
17:19:50.757 [http-nio-8080-exec-7] INFO  o.a.coyote.http11.Http11Processor - Error parsing HTTP request header
 Note: further occurrences of HTTP request parsing errors will be logged at DEBUG level.
java.lang.IllegalArgumentException: Request header is too large
...
```

## 4. 解决方案

我们可以根据我们的需要增加application.properties文件中max-http-header-size属性的值，在上面的程序中，我们可以将它的值从默认的8kb修改为40KB，这样就可以解决问题。

```properties
server.max-http-header-size=40KB
```

现在，服务器将处理请求并返回200响应，如下所示：

<img src="../assets/img_1.png">

因此，只要标头大小超过服务器列出的默认值，我们就会看到服务器返回一个400 Bad Request并显示错误“request header is too large”，我们必须覆盖应用程序配置文件中的max-http-header-size值以匹配请求标头长度，如我们在上面的示例中所见。

通常，请求标头可能会变得太大，例如，由于加密使用的令牌很长。

## 5. 总结

在本教程中，我们学习了如何在Spring Boot应用程序的应用程序配置文件中使用max-http-header-size属性。

然后，我们看到了当我们传递一个超过此大小的请求标头时会发生什么，以及如何在我们的application.properties中增加max-http-header-size的大小。
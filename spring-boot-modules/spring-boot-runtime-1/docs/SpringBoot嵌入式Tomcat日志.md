## 1. 简介

Spring Boot带有一个嵌入式Tomcat服务器，非常方便。但是，默认情况下我们看不到Tomcat的日志。

在本教程中，我们将学习**如何配置Spring Boot以通过玩具应用程序显示Tomcat的内部和访问日志**。

## 2. 示例应用

首先，让我们创建一个REST API，我们将定义一个GreetingsController来问候用户：

```java
@GetMapping("/greetings/{username}")
public String getGreetings(@PathVariable("username") String userName) {
    return "Hello " + userName + ", Good day...!!!";
}
```

## 3. Tomcat日志类型

嵌入式Tomcat存储两种类型的日志：

-   访问日志
-   内部服务器日志

访问日志保留应用程序处理的所有请求的记录，这些日志可用于**跟踪诸如页面点击次数和用户会话活动之类的事情**。相比之下，内部服务器日志将帮助我们解决正在运行的应用程序中的任何问题。

## 4. 访问日志

默认情况下，访问日志未启用。不过，我们可以通过向application.properties添加一个属性来轻松启用它们：

```properties
server.tomcat.accesslog.enabled=true
```

同样，我们可以使用VM参数来启用访问日志：

```shell
java -jar -Dserver.tomcat.basedir=tomcat -Dserver.tomcat.accesslog.enabled=true app.jar
```

**这些日志文件将创建在一个临时目录中**；例如，在Windows上，访问日志的目录将类似于AppData\Local\Temp\tomcat.2142886552084850151.40123\logs

### 4.1 格式

因此，启用此属性后，我们会在正在运行的应用程序中看到类似以下内容：

```shell
0:0:0:0:0:0:0:1 - - [13/May/2012:15:14:51 +0530] "GET /greetings/Harry HTTP/1.1" 200 27
0:0:0:0:0:0:0:1 - - [13/May/2012:15:17:23 +0530] "GET /greetings/Harry HTTP/1.1" 200 27
```

这些是访问日志，它们具有以下格式：

```shell
%h %l %u %t \"%r\" %>s %b
```

我们可以将其解释为：

%h：发送请求的客户端IP，在本例中为0:0:0:0:0:0:0:1
%l：用户的身份
%u：由HTTP身份验证确定的用户名
%t：收到请求的时间
%r：来自客户端的请求行，在本例中为GET /greetings/Harry HTTP/1.1
%>s：从服务器发送到客户端的状态代码，例如这里的200 
%b：对客户端的响应大小，对于这些请求为27 

由于此请求没有经过身份验证的用户，因此%l和%u打印破折号。

事实上，**如果缺少任何信息，Tomcat会为该插槽打印一个破折号**。

### 4.2 自定义访问日志

我们可以通过在application.properties中添加一些属性来覆盖默认的Spring Boot配置。

首先，要更改默认日志文件名，请执行以下操作：

```properties
server.tomcat.accesslog.suffix=.log
server.tomcat.accesslog.prefix=access_log
server.tomcat.accesslog.file-date-format=.yyyy-MM-dd
```

此外，我们可以更改日志文件的位置：

```properties
server.tomcat.basedir=tomcat
server.tomcat.accesslog.directory=logs
```

最后，我们可以覆盖日志在日志文件中的写入方式：

```properties
server.tomcat.accesslog.pattern=common
```

Spring Boot中还有一些[可配置的属性](https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-application-properties.html)。

## 5. 内部日志

Tomcat服务器的内部日志对于解决任何服务器端问题都非常有帮助，要查看这些日志，我们必须在application.properties中添加以下日志记录配置：

```properties
logging.level.org.apache.tomcat=DEBUG
logging.level.org.apache.catalina=DEBUG
```

然后我们会看到类似这样的东西：

```shell
2022-12-26 15:11:07.261 DEBUG 31160 --- [0124-Acceptor-0] o.apache.tomcat.util.threads.LimitLatch  : Counting up[http-nio-40124-Acceptor-0] latch=1
2022-12-26 15:11:07.262 DEBUG 31160 --- [0124-Acceptor-0] o.apache.tomcat.util.threads.LimitLatch  : Counting up[http-nio-40124-Acceptor-0] latch=2
2022-12-26 15:11:07.278 DEBUG 31160 --- [io-40124-exec-1] org.apache.tomcat.util.modeler.Registry  : Managed= Tomcat:type=RequestProcessor,worker="http-nio-40124",name=HttpRequest1
...
2022-12-26 15:11:07.279 DEBUG 31160 --- [io-40124-exec-1] m.m.MbeansDescriptorsIntrospectionSource : Introspected attribute virtualHost public java.lang.String org.apache.coyote.RequestInfo.getVirtualHost() null
...
2022-12-26 15:11:07.280 DEBUG 31160 --- [io-40124-exec-1] o.a.tomcat.util.modeler.BaseModelMBean   : preRegister org.apache.coyote.RequestInfo@1e6f89ad Tomcat:type=RequestProcessor,worker="http-nio-40124",name=HttpRequest1
2022-12-26 15:11:07.292 DEBUG 31160 --- [io-40124-exec-1] org.apache.tomcat.util.http.Parameters   : Set query string encoding to UTF-8
2022-12-26 15:11:07.294 DEBUG 31160 --- [io-40124-exec-1] o.a.t.util.http.Rfc6265CookieProcessor   : Cookies: Parsing b[]: jenkins-timestamper-offset=-19800000
2022-12-26 15:11:07.296 DEBUG 31160 --- [io-40124-exec-1] o.a.c.authenticator.AuthenticatorBase    : Security checking request GET /greetings/Harry
2022-12-26 15:11:07.296 DEBUG 31160 --- [io-40124-exec-1] org.apache.catalina.realm.RealmBase      :   No applicable constraints defined
```

## 6. 总结

在这篇简短的文章中，我们了解了Tomcat的内部日志和访问日志之间的区别。然后，我们看到了如何启用和自定义它们。
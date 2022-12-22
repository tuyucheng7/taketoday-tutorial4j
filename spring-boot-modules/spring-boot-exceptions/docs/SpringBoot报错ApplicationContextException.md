## 1. 概述

在这个快速教程中，我们将仔细研究[Spring Boot]()错误“ApplicationContextException：由于缺少ServletWebServerFactory bean，无法启动ServletWebServerApplicationContext”。

首先，我们将阐明导致此错误的主要原因。然后，我们将深入探讨如何使用实际示例重现它，最后探讨如何解决它。

## 2. 可能原因

首先，让我们尝试了解错误消息的含义。“由于缺少ServletWebServerFactory bean，无法启动ServletWebServerApplicationContext”说明了一切，它只是告诉我们在[ApplicationContext]()中没有配置的[ServletWebServerFactory](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/web/servlet/server/ServletWebServerFactory.html) bean。

该错误主要出现在Spring Boot无法启动[ServletWebServerApplicationContext](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/web/servlet/context/ServletWebServerApplicationContext.html)时。为什么？**因为ServletWebServerApplicationContext使用包含的ServletWebServerFactory bean来引导自身**。

通常，Spring Boot提供了[SpringApplication.run](https://docs.spring.io/spring-boot/docs/1.0.0.RC5/reference/html/boot-features-spring-application.html)方法来引导Spring应用程序。

SpringApplication类将尝试为我们创建正确的ApplicationContext，这具体**取决于我们是否正在开发Web应用程序**。

例如，用于确定Web应用程序是否来自某些依赖项(如spring-boot-starter-web)的算法。话虽如此，缺少这些依赖项可能是我们错误背后的原因之一。

另一个原因是在Spring Boot入口点类中缺少[@SpringBootApplication注解]()。

## 3. 重现错误

现在，让我们看一个可以产生Spring Boot错误的示例，**实现此目的的最简单方法是创建一个不带@SpringBootApplication注解的主类**。

首先，让我们创建一个入口点类，并故意忘记使用@SpringBootApplication对其进行标注：

```java
public class MainEntryPoint {

    public static void main(String[] args) {
        SpringApplication.run(MainEntryPoint.class, args);
    }
}
```

现在，运行我们的示例Spring Boot应用程序，看看会发生什么：

```shell
12:20:39.134 [main] ERROR o.s.boot.SpringApplication - Application run failed
org.springframework.context.ApplicationContextException: Unable to start web server; nested exception is org.springframework.context.ApplicationContextException: Unable to start ServletWebServerApplicationContext due to missing ServletWebServerFactory bean.
	...
	at cn.tuyucheng.taketoday.applicationcontextexception.MainEntryPoint.main(MainEntryPoint.java:10)
<strong>Caused by: org.springframework.context.ApplicationContextException: Unable to start ServletWebServerApplicationContext due to missing ServletWebServerFactory bean.</strong>
	at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.getWebServerFactory(ServletWebServerApplicationContext.java:209)
	at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.createWebServer(ServletWebServerApplicationContext.java:179)
	... 
```

如上所示，我们得到“ApplicationContextException: Unable to start ServletWebServerApplicationContext due to missing ServletWebServerFactory bean”错误。

## 4. 修复错误

修复错误的简单解决方案是使用@SpringBootApplication注解对我们的MainEntryPoint类进行标注。

通过使用这个注解，**我们告诉Spring Boot自动配置必要的bean并将它们注册到上下文中**。

同样，我们可以通过禁用Web环境来避免非Web应用程序的错误，为此，我们可以使用spring.main.web-application-type属性。

在application.properties中：

```properties
spring.main.web-application-type=none
```

同样，在我们的application.yml中：

```yaml
spring:
    main:
        web-application-type: none
```

**none表示应用程序不应作为Web应用程序运行，它用于禁用Web服务器**。

请记住，从[Spring Boot 2.0](https://spring.io/blog/2018/03/01/spring-boot-2-0-goes-ga)开始，我们还可以使用[SpringApplicationBuilder](https://docs.spring.io/spring-boot/docs/2.0.x/api/org/springframework/boot/builder/SpringApplicationBuilder.html#web-org.springframework.boot.WebApplicationType-)显式定义特定类型的Web应用程序：

```java
@SpringBootApplication
public class MainClass {

    public static void main(String[] args) {
        new SpringApplicationBuilder(MainClass.class)
              .web(WebApplicationType.NONE)
              .run(args);
    }
}
```

**对于[WebFlux项目]()，我们可以使用WebApplicationType.REACTIVE**，另一种解决方案可能是排除spring-webmvc依赖项。

类路径中此依赖项的存在告诉Spring Boot将该项目视为一个Servlet应用程序，而不是一个响应式Web应用程序。因此，Spring Boot无法启动ServletWebServerApplicationContext。

## 5. 总结

在这篇简短的文章中，我们详细讨论了导致Spring Boot在启动时失败并出现此错误的原因：“ApplicationContextException: Unable to start ServletWebServerApplicationContext due to missing ServletWebServerFactory bean”。

在此过程中，我们通过一个实际的例子解释了错误是如何产生的以及如何修复它。
## 1. 概述

本教程说明了如何使用 Spring 创建 Web 应用程序。

我们将研究用于构建应用程序的 Spring Boot 解决方案，还会看到一种非 Spring Boot 方法。

我们将主要使用 Java 配置，但也会查看它们等效的 XML 配置。

## 延伸阅读：

## [Spring Boot 教程——引导一个简单的应用程序](https://www.baeldung.com/spring-boot-start)

这就是你开始了解 Spring Boot 的方式。

[阅读更多](https://www.baeldung.com/spring-boot-start)→

## [配置 Spring Boot Web 应用程序](https://www.baeldung.com/spring-boot-application-configuration)

Spring Boot 应用程序的一些更有用的配置。

[阅读更多](https://www.baeldung.com/spring-boot-application-configuration)→

## [从 Spring 迁移到 Spring Boot](https://www.baeldung.com/spring-boot-migration)

查看如何正确地从 Spring 迁移到 Spring Boot。

[阅读更多](https://www.baeldung.com/spring-boot-migration)→

## 2. 使用 Spring Boot 进行设置

### 2.1. Maven 依赖

首先，我们需要[spring-boot-starter-web](https://search.maven.org/search?q=a:spring-boot-starter-web) 依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>2.7.2</version>
</dependency>
```

这个启动器包括：

-   我们的 Spring Web 应用程序所需的spring-web和spring-webmvc模块
-   一个 Tomcat 启动器，这样我们就可以直接运行我们的 Web 应用程序而无需显式安装任何服务器

### 2.2. 创建 Spring Boot 应用程序

开始使用 Spring Boot 最直接的方法是创建一个主类并用 @SpringBootApplication注解它：

```java
@SpringBootApplication
public class SpringBootRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootRestApplication.class, args);
    }
}
```

这个单一注解等同于使用@ Configuration 、 @ EnableAutoConfiguration和@ComponentScan。

默认情况下，它将扫描同一包或以下包中的所有组件。

接下来，对于基于 Java 的 Spring bean 配置，我们需要创建一个配置类并使用@Configuration注解对其进行注解：

```java
@Configuration
public class WebConfig {

}
```

此注解是基于 Java 的 Spring 配置使用的主要工件；它本身是用@Component进行元注解的，这使得带注解的类成为标准 bean，因此也是组件扫描的候选对象。

@Configuration类的主要目的是成为 Spring IoC 容器的 bean 定义的来源。更详细的描述参见[官方文档](http://static.springsource.org/spring/docs/current/spring-framework-reference/html/beans.html#beans-java)。

让我们也看看使用核心spring-webmvc库的解决方案。

## 3. 使用 spring-webmvc 设置

### 3.1. Maven 依赖项

首先，我们需要[spring-webmvc](https://search.maven.org/search?q=g:org.springframework AND a:spring-webmvc) 依赖：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>5.3.3</version>
</dependency>
```

### 3.2. 基于 Java 的 Web 配置

接下来，我们将添加具有@Configuration注解的配置类：

```java
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "cn.tuyucheng.taketoday.controller")
public class WebConfig {
   
}
```

在这里，与 Spring Boot 解决方案不同，我们必须显式定义@EnableWebMvc 以设置默认的 Spring MVC 配置和 @ComponentScan 以指定要扫描组件的包。

@EnableWebMvc注解 提供 Spring Web MVC 配置，例如设置调度程序 servlet、启用@Controller和@RequestMapping 注解以及设置其他默认值。

@ComponentScan配置组件扫描指令，指定要扫描的包。

### 3.3. 初始化器类

接下来，我们需要添加一个实现 WebApplicationInitializer接口的类：

```java
public class AppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext container) throws ServletException {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.scan("cn.tuyucheng.taketoday");
        container.addListener(new ContextLoaderListener(context));

        ServletRegistration.Dynamic dispatcher = 
          container.addServlet("mvc", new DispatcherServlet(context));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");   
    }
}
```

在这里，我们使用 AnnotationConfigWebApplicationContext类创建一个 Spring 上下文，这意味着我们仅使用基于注解的配置。然后，我们指定要扫描组件和配置类的包。

最后，我们定义了 Web 应用程序的入口点 – DispatcherServlet。

此类可以完全替换 <3.0 Servlet 版本的web.xml文件。

## 4. XML配置

让我们也快速浏览一下等效的 XML Web 配置：

```xml
<context:component-scan base-package="cn.tuyucheng.taketoday.controller" />
<mvc:annotation-driven />
```

我们可以用上面的WebConfig类替换这个 XML 文件。

要启动应用程序，我们可以使用加载 XML 配置或 web.xml 文件的 Initializer 类。有关这两种方法的更多详细信息，请查看[我们之前的文章](https://www.baeldung.com/spring-xml-vs-java-config)。

## 5.总结

在本文中，我们研究了两种用于引导 Spring Web 应用程序的流行解决方案，一种使用 Spring Boot Web 启动器，另一种使用核心 spring-webmvc 库。

在下[一篇关于 REST with Spring 的文章中](https://www.baeldung.com/building-a-restful-web-service-with-spring-and-java-based-configuration)，我将介绍在项目中设置 MVC、配置 HTTP 状态代码、有效负载编组和内容协商。
## 1. 概述

本教程说明了如何**使用Spring创建Web应用程序**。

我们将研究用于构建应用程序的Spring Boot解决方案，并介绍一种非Spring Boot方法。

我们将主要使用Java配置，但也会查看它们等效的XML配置。

## 延伸阅读

### [Spring Boot教程-引导一个简单的应用程序](https://www.baeldung.com/spring-boot-start)

这就是你开始了解Spring Boot的方式。

[阅读更多](https://www.baeldung.com/spring-boot-start)→

### [配置Spring Boot Web应用程序](https://www.baeldung.com/spring-boot-application-configuration)

Spring Boot应用程序的一些更有用的配置。

[阅读更多](https://www.baeldung.com/spring-boot-application-configuration)→

### [从Spring迁移到Spring Boot](https://www.baeldung.com/spring-boot-migration)

了解如何正确地从Spring迁移到Spring Boot。

[阅读更多](https://www.baeldung.com/spring-boot-migration)→

## 2. 使用Spring Boot进行设置

### 2.1 Maven依赖

首先，我们需要[spring-boot-starter-web](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-web/3.0.5)依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>2.7.2</version>
</dependency>
```

这个启动器包括：

-   我们的Spring Web应用程序所需的spring-web和spring-webmvc模块
-   一个Tomcat启动器，这样我们就可以直接运行我们的Web应用程序而无需显式安装任何服务器

### 2.2 创建Spring Boot应用程序

**开始使用Spring Boot最直接的方法是创建一个主类并使用@SpringBootApplication标注它**：

```java
@SpringBootApplication
public class SpringBootRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootRestApplication.class, args);
    }
}
```

这个单一注解等同于使用@Configuration、@EnableAutoConfiguration和@ComponentScan。

默认情况下，它将扫描同一包或以下包中的所有组件。

**接下来，对于基于Java的Spring bean配置，我们需要创建一个配置类并使用@Configuration注解对其进行标注**：

```java
@Configuration
public class WebConfig {
}
```

此注解是基于Java的Spring配置使用的主要工件；它本身是用@Component进行元注解的，这使得带注解的类成为标准bean，因此也是组件扫描的候选对象。

@Configuration类的主要目的是成为Spring IoC容器bean定义的来源。有关更详细的说明，请参阅[官方文档](http://static.springsource.org/spring/docs/current/spring-framework-reference/html/beans.html#beans-java)。

让我们也看一下使用核心spring-webmvc库的解决方案。

## 3. 使用spring-webmvc设置

### 3.1 Maven依赖项

首先，我们需要[spring-webmvc](https://central.sonatype.com/artifact/org.springframework/spring-webmvc/6.0.7)依赖：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>5.3.3</version>
</dependency>
```

### 3.2 基于Java的Web配置

接下来，我们将添加具有@Configuration注解的配置类：

```java
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "cn.tuyucheng.taketoday.controller")
public class WebConfig {
}
```

**在这里，与Spring Boot解决方案不同，我们必须显式定义@EnableWebMvc以设置默认的Spring MVC配置和@ComponentScan以指定要扫描组件的包**。

@EnableWebMvc注解提供Spring Web MVC配置，例如设置DispatcherServlet、启用@Controller和@RequestMapping注解以及设置其他默认值。

@ComponentScan配置组件扫描指令，指定要扫描的包。

### 3.3 初始化器类

接下来，我们需要**添加一个实现WebApplicationInitializer接口的类**：

```java
public class AppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext container) throws ServletException {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.scan("cn.tuyucheng.taketoday");
        container.addListener(new ContextLoaderListener(context));

        ServletRegistration.Dynamic dispatcher = container.addServlet("mvc", new DispatcherServlet(context));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }
}
```

在这里，我们使用AnnotationConfigWebApplicationContext类创建一个Spring上下文，这意味着我们仅使用基于注解的配置。然后，我们指定要扫描组件和配置类的包。

最后，我们定义了Web应用程序的入口点–DispatcherServlet。

此类可以完全替换小于Servlet 3.0版本的web.xml文件。

## 4. XML配置

让我们也快速浏览一下等效的XML Web配置：

```xml
<context:component-scan base-package="cn.tuyucheng.taketoday.controller" />
<mvc:annotation-driven />
```

我们可以用上面的WebConfig类替换这个XML文件。

要启动应用程序，我们可以使用加载XML配置或web.xml文件的AppInitializer类。有关这两种方法的更多详细信息，请查看我们[之前的文章](https://www.baeldung.com/spring-xml-vs-java-config)。

## 5. 总结

在本文中，我们研究了两种用于引导Spring Web应用程序的流行解决方案，一种使用Spring Boot Web启动器，另一种使用核心spring-webmvc库。

在下一篇[关于REST与Spring的文章](https://www.baeldung.com/building-a-restful-web-service-with-spring-and-java-based-configuration)中，我将介绍在项目中设置MVC、配置HTTP状态代码、有效负载编组和内容协商。
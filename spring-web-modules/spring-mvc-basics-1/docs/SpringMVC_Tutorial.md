## 1. 概述

这是一个简单的Spring MVC教程，演示了如何使用基于Java的配置以及XML配置来构建Spring MVC项目。

## 2. 什么是Spring MVC？

顾名思义，**它是Spring框架的一个模块，处理Model-View-Controller(MVC)模式**。它结合了MVC模式的所有优点和Spring的便利性。

Spring使用DispatcherServlet通过前端控制器模式实现MVC。

简而言之，DispatcherServlet充当主控制器，将请求路由到其预期目的地。模型只不过是我们应用程序的数据，视图由各种模板引擎表示。

稍后我们在案例中使用JSP作为模板引擎。

## 3. 使用Java配置的Spring MVC

要通过Java配置类启用Spring MVC支持，我们只需添加@EnableWebMvc注解：

```java

@EnableWebMvc
@Configuration
public class WebConfig {

    // ...
}
```

这为MVC项目设置我们所需的基本支持，例如注册控制器和映射、类型转换器、验证支持、消息转换器和异常处理。

**如果我们想自定义这个配置，我们需要实现WebMvcConfigurer接口**：

```java

@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
    }

    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver bean = new InternalResourceViewResolver();

        bean.setViewClass(JstlView.class);
        bean.setPrefix("/WEB-INF/view/");
        bean.setSuffix(".jsp");

        return bean;
    }
}
```

在这个例子中，我们注册了一个ViewResolver bean，它将从/WEB-INF/view目录返回.jsp视图。

这里非常重要的是，我们可以注册视图控制器，**使用ViewControllerRegistry在URL和视图名称之间创建直接映射**。这样，两者之间就不需要任何控制器了。

如果我们还想定义和扫描控制器类，我们可以添加@ComponentScan注解，并指定包含控制器类的包：

```java

@EnableWebMvc
@Configuration
@ComponentScan(basePackages = {"cn.tuyucheng.web.controller"})
public class WebConfig implements WebMvcConfigurer {
    // ...
}
```

要引导加载此配置的应用程序，我们还需要一个初始化类：

```java
public class MainWebAppInitializer implements WebApplicationInitializer {

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

请注意，对于Spring 5之前的版本，我们必须使用WebMvcConfigurerAdapter类。

## 4. 使用XML配置的Spring MVC

除了上面的Java配置，我们还可以使用纯XML配置：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc.xsd">

    <context:component-scan base-package="cn.tuyucheng.web.controller"/>
    <mvc:annotation-driven/>

    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="prefix" value="/WEB-INF/view/"/>
        <property name="suffix" value=".jsp"/>
    </bean>

    <mvc:view-controller path="/" view-name="index"/>
</beans>
```

如果我们想使用纯XML配置，我们还需要添加一个web.xml文件来引导应用程序。

## 5. 控制器和视图

让我们看一个基本控制器的示例：

```java

@Controller
public class SampleController {

    @GetMapping("/sample")
    public String showForm() {
        return "sample";
    }
}
```

而对应的JSP资源就是sample.jsp文件：

```html

<html>
<head></head>

<body>
<h1>This is the body of the sample view</h1>
</body>
</html>
```

基于JSP的视图文件位于项目的/WEB-INF文件夹下，因此它们只能由Spring访问，而不能通过直接URL访问。

## 6. Spring MVC与Spring Boot

Spring Boot是对Spring的一个扩展，它使得上手和创建独立的生产级应用程序变得非常容易。
**Boot并不是为了取代Spring，而是为了让使用它变得更快、更容易**。

### 6.1 Spring Boot Starters

新框架提供了方便的启动依赖项，这些依赖项可以为特定功能引入所有必要的技术。

它们的优点是我们不再需要为每个依赖项指定一个版本，而是允许启动器为我们管理依赖项。

最快的入门方法是添加spring-boot-starter-parent pom.xml：

```xml

<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.6.1</version>
</parent>
```

这将负责依赖管理。

### 6.2 Spring Boot入口点

使用Spring Boot构建的每个应用程序只需要定义主入口点。

这通常是一个带有main方法的Java类，使用@SpringBootApplication注解标注：

```java

@SpringBootApplication
public class MvcApplication {

    public static void main(String[] args) {
        SpringApplication.run(MvcApplication.class, args);
    }
}
```

此注解包含了以下其他注解：

+ @Configuration将类标记为bean定义的源。
+ @EnableAutoConfiguration告诉框架根据类路径上的依赖项自动添加bean。
+ @ComponentScan扫描与Application类同包或其子包下的其他配置和bean。

通过Spring Boot，我们可以使用Thymeleaf或JSP作为模板，而无需使用第3节中定义的ViewResolver。
通过向我们的pom.xml添加spring-boot-starter-thymeleaf依赖项，Thymeleaf被启用，并且不需要额外的配置。

## 7. 总结

在本文中，我们使用Java配置构建了一个简单的Spring MVC项目。

当项目在你的本地运行时，可以通过http://localhost:8080/spring-mvc-basics/sample访问sample.jsp。
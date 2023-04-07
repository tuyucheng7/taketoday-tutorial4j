## 1. 概述

在本教程中，我们将了解标准 Spring 框架和Spring Boot之间的区别。

我们将关注并讨论 Spring 的模块(如 MVC 和安全性)在核心 Spring 中使用时与在 Boot 中使用时有何不同。

## 延伸阅读：

## [配置Spring BootWeb 应用程序](https://www.baeldung.com/spring-boot-application-configuration)

Spring Boot 应用程序的一些更有用的配置。

[阅读更多](https://www.baeldung.com/spring-boot-application-configuration)→

## [从 Spring 迁移到 Spring Boot](https://www.baeldung.com/spring-boot-migration)

查看如何正确地从 Spring 迁移到 Spring Boot。

[阅读更多](https://www.baeldung.com/spring-boot-migration)→

## 2.什么是春天？

简单地说，Spring 框架为开发Java应用程序提供了完善的基础设施支持。

它包含一些不错的功能，例如依赖注入，以及开箱即用的模块，例如：

-   春季JDBC
-   春季MVC
-   弹簧安全
-   春季面向对象编程
-   弹簧ORM
-   春季测试

这些模块可以大大减少应用程序的开发时间。

例如，在早期的Java Web开发中，我们需要编写大量的样板代码来向数据源中插入一条记录。通过使用Spring JDBC模块的JDBCTemplate，我们可以将其简化为几行代码，只需很少的配置。

## 3.什么是Spring Boot？

Spring Boot 基本上是 Spring 框架的扩展，它消除了设置 Spring 应用程序所需的样板配置。

它采用了 Spring 平台的观点，为更快、更高效的开发生态系统铺平了道路。

以下是Spring Boot中的一些功能：

-   自以为是的“入门”依赖项，以简化构建和应用程序配置
-   嵌入式服务器以避免应用程序部署的复杂性
-   指标、健康检查和外部化配置
-   Spring 功能的自动配置——只要有可能

让我们逐步熟悉这两个框架。

## 4.Maven依赖

首先，让我们看一下使用 Spring 创建 Web 应用程序所需的最小依赖项：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-web</artifactId>
    <version>5.3.5</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>5.3.5</version>
</dependency>
```

与 Spring 不同，Spring Boot 只需要一个依赖项即可启动和运行 Web 应用程序：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>2.4.4</version>
</dependency>
```

在构建期间，所有其他依赖项都会自动添加到最终存档中。

另一个很好的例子是测试库。我们通常使用一组 Spring Test、JUnit、Hamcrest 和 Mockito 库。在 Spring 项目中，我们应该将所有这些库添加为依赖项。

或者，在Spring Boot中，我们只需要用于测试的启动器依赖项即可自动包含这些库。

Spring Boot 为不同的 Spring 模块提供了许多启动器依赖项。一些最常用的是：

-   spring-boot-starter-data-jpa
-   弹簧启动启动器安全
-   弹簧启动启动器测试
-   spring-boot-starter-web
-   spring-boot-starter-百里香叶

有关初学者的完整列表，另请查看[Spring 文档](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#using-boot-starter)。

## 5.MVC配置

让我们探讨使用 Spring 和Spring Boot创建 JSP Web 应用程序所需的配置。

Spring 需要定义调度程序 servlet、映射和其他支持配置。我们可以使用web.xml文件或Initializer类来做到这一点：

```java
public class MyWebAppInitializer implements WebApplicationInitializer {
 
    @Override
    public void onStartup(ServletContext container) {
        AnnotationConfigWebApplicationContext context
          = new AnnotationConfigWebApplicationContext();
        context.setConfigLocation("com.baeldung");
 
        container.addListener(new ContextLoaderListener(context));
 
        ServletRegistration.Dynamic dispatcher = container
          .addServlet("dispatcher", new DispatcherServlet(context));
         
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }
}
```

我们还需要将@EnableWebMvc注解添加到@Configuration类，并定义一个视图解析器来解析从控制器返回的视图：

```java
@EnableWebMvc
@Configuration
public class ClientWebConfig implements WebMvcConfigurer { 
   @Bean
   public ViewResolver viewResolver() {
      InternalResourceViewResolver bean
        = new InternalResourceViewResolver();
      bean.setViewClass(JstlView.class);
      bean.setPrefix("/WEB-INF/view/");
      bean.setSuffix(".jsp");
      return bean;
   }
}
```

相比之下，一旦我们添加了 web starter，Spring Boot 只需要几个属性就可以使事情正常进行：

```java
spring.mvc.view.prefix=/WEB-INF/jsp/
spring.mvc.view.suffix=.jsp
```

[上面所有的 Spring 配置都是通过一个名为auto-configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-auto-configuration.html)的过程添加 Boot web starter 来自动包含的。

这意味着Spring Boot将查看应用程序中存在的依赖项、属性和 bean，并基于这些启用配置。

当然，如果我们想添加自己的自定义配置，那么Spring Boot自动配置就会退却。

### 5.1. 配置模板引擎

现在让我们学习如何在 Spring 和Spring Boot中配置Thymeleaf模板引擎。

在 Spring 中，我们需要为视图解析器添加[thymeleaf-spring5依赖和一些配置：](https://mvnrepository.com/artifact/org.thymeleaf/thymeleaf-spring5)

```java
@Configuration
@EnableWebMvc
public class MvcWebConfig implements WebMvcConfigurer {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver templateResolver = 
          new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(applicationContext);
        templateResolver.setPrefix("/WEB-INF/views/");
        templateResolver.setSuffix(".html");
        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        templateEngine.setEnableSpringELCompiler(true);
        return templateEngine;
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine());
        registry.viewResolver(resolver);
    }
}
```

Spring Boot 1 只需要依赖spring-boot-starter-thymeleaf 即可在 Web 应用程序中启用Thymeleaf支持。由于Thymeleaf3.0 中的新特性， 我们还必须在Spring Boot 2 Web应用程序中添加thymeleaf-layout-dialect 作为依赖项。或者，我们可以选择添加一个spring-boot-starter-thymeleaf依赖项，它将为我们处理所有这些。

一旦依赖关系到位，我们就可以将模板添加到src/main/resources/templates文件夹中，Spring Boot 将自动显示它们。

## 6.Spring安全配置

为了简单起见，我们将了解如何使用这些框架启用默认的 HTTP 基本身份验证。

让我们首先查看使用 Spring 启用安全性所需的依赖项和配置。

Spring 需要标准的spring-security-web和spring-security-config 依赖项来在应用程序中设置安全性。

接下来我们需要添加一个类来创建SecurityFilterChain bean 并使用@EnableWebSecurity注解：

```java
@Configuration
@EnableWebSecurity
public class CustomWebSecurityConfigurerAdapter {
 
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
          .withUser("user1")
            .password(passwordEncoder()
            .encode("user1Pass"))
          .authorities("ROLE_USER");
    }
 
    @Bean
     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
          .anyRequest().authenticated()
          .and()
          .httpBasic();
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

这里我们使用inMemoryAuthentication来设置身份验证。

Spring Boot 也需要这些依赖项才能使其工作，但我们只需要定义spring-boot-starter-security的依赖项，因为这会自动将所有相关依赖项添加到类路径中。

Spring Boot中的安全配置和上面一样。

要了解如何在 Spring 和Spring Boot中实现 JPA 配置，我们可以查看我们的文章[A Guide to JPA with Spring](https://www.baeldung.com/the-persistence-layer-with-spring-and-jpa)。

## 7. 应用引导

在 Spring 和Spring Boot中引导应用程序的基本区别在于 servlet。Spring 使用 web.xml或 SpringServletContainerInitializer作为 其引导入口点。

另一方面，Spring Boot 仅使用 Servlet 3 功能来引导应用程序。让我们详细谈谈这个。

### 7.1. Spring Bootstrap如何？

Spring 既支持遗留的web.xml引导方式，也支持最新的 Servlet 3+ 方法。

让我们 逐步查看web.xml方法：

1.  Servlet 容器(服务器)读取web.xml。
2.  web.xml中定义的DispatcherServlet由容器实例化。
3.  DispatcherServlet通过读取WEB-INF/{servletName}-servlet.xml创建WebApplicationContext 。
4.  最后，DispatcherServlet注册在应用程序上下文中定义的 beans。

以下是 Spring 如何使用 Servlet 3+ 方法进行引导：

1.  容器搜索实现 ServletContainerInitializer的类并执行。
2.  SpringServletContainerInitializer找到所有实现WebApplicationInitializer的类。
3.  WebApplicationInitializer 使用XML或@Configuration类创建上下文。
4.  WebApplicationInitializer使用先前创建的上下文创建DispatcherServlet 。 

### 7.2.Spring Boot如何引导？

Spring Boot 应用程序的入口点是用@SpringBootApplication注解的类：

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

默认情况下，Spring Boot 使用嵌入式容器来运行应用程序。在这种情况下，Spring Boot 使用public static void main入口点来启动嵌入式 Web 服务器。

它还负责将Servlet、Filter和ServletContextInitializer bean 从应用程序上下文绑定到嵌入式 servlet 容器。

Spring Boot 的另一个特点是它会自动扫描同一个包中的所有类或 Main 类的子包中的组件。

此外，Spring Boot 提供了将其部署为外部容器中的 Web 存档的选项。在这种情况下，我们必须扩展SpringBootServletInitializer：

```java
@SpringBootApplication
public class Application extends SpringBootServletInitializer {
    // ...
}
```

此处外部 servlet 容器查找 Web 存档的 META-INF 文件中定义的主类，而 SpringBootServletInitializer将负责绑定Servlet、Filter和ServletContextInitializer。

## 8. 打包部署

最后，让我们看看如何打包和部署应用程序。这两个框架都支持常见的包管理技术，如 Maven 和 Gradle；但是，在部署方面，这些框架有很大不同。

例如，[Spring Boot Maven 插件](https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/htmlsingle/)在 Maven 中提供了Spring Boot支持。它还允许打包可执行 jar 或 war 存档并“就地”运行应用程序。

在部署上下文中，Spring Boot 优于 Spring 的一些优势包括：

-   提供嵌入式容器支持
-   准备使用命令java -jar独立运行 jar
-   在外部容器中部署时排除依赖项以避免潜在的 jar 冲突的选项
-   部署时指定活动配置文件的选项
-   用于集成测试的随机端口生成

## 9.总结

在本文中，我们了解了 Spring 和Spring Boot之间的区别。

简单来说，Spring Boot就是对Spring本身的扩展，让开发、测试、部署更加方便。
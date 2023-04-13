## 1.概述

在本教程中，我们将了解标准Spring框架和Spring Boot之间的区别。

我们将重点讨论Spring的模块(如MVC和Security)在核心Spring中使用时与在Boot中使用时有何不同。

## 延伸阅读

### [配置Spring Boot Web应用程序](https://www.baeldung.com/spring-boot-application-configuration)

Spring Boot应用程序的一些更有用的配置。

[阅读更多](https://www.baeldung.com/spring-boot-application-configuration)→

### [从Spring迁移到Spring Boot](https://www.baeldung.com/spring-boot-migration)

了解如何正确地从Spring迁移到Spring Boot。

[阅读更多](https://www.baeldung.com/spring-boot-migration)→

## 2. 什么是Spring？

**简单地说，Spring框架为开发Java应用程序提供了全面的基础设施支持**。

它包含一些不错的功能，例如依赖注入，以及开箱即用的模块，例如：

-   Spring JDBC
-   Spring MVC
-   Spring Security
-   Spring AOP
-   Spring ORM
-   Spring Test

这些模块可以大大减少应用程序的开发时间。

例如，在早期的Java Web开发中，我们需要编写大量的样板代码来向数据源中插入一条记录。通过使用Spring JDBC模块的JDBCTemplate，我们可以将其简化为几行代码，只需很少的配置。

## 3. 什么是Spring Boot？

Spring Boot基本上是Spring框架的扩展，它消除了设置Spring应用程序所需的样板配置。

**它采用了Spring平台的观点，为更快、更高效的开发生态系统铺平了道路**。

以下是Spring Boot中的一些功能：

-   固执己见的“启动器”依赖项，可简化构建和应用程序配置
-   嵌入式服务器，以避免应用程序部署的复杂性
-   指标、健康检查和外部化配置
-   自动配置Spring功能–只要有可能

让我们逐步熟悉这两个框架。

## 4. Maven依赖

首先，让我们看一下使用Spring创建Web应用程序所需的最小依赖项：

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

与Spring不同，Spring Boot只需要一个依赖项即可启动和运行Web应用程序：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>2.4.4</version>
</dependency>
```

在构建期间，所有其他依赖项都会自动添加到最终存档中。

另一个很好的例子是测试库。我们通常使用一组Spring Test、JUnit、Hamcrest和Mockito库。在Spring项目中，我们应该将所有这些库添加为依赖项。

或者，在Spring Boot中，我们只需要用于测试的启动器依赖项即可自动包含这些库。

**Spring Boot为不同的Spring模块提供了许多启动器依赖项**。一些最常用的是：

-  spring-boot-starter-data-jpa
-  spring-boot-starter-security
-  spring-boot-starter-test
-  spring-boot-starter-web
-  spring-boot-starter-thymeleaf

有关启动器的完整列表，另请查看[Spring文档](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#using-boot-starter)。

## 5. MVC配置

让我们探讨使用Spring和Spring Boot创建JSP Web应用程序所需的配置。

**Spring需要定义DispatcherServlet、映射和其他支持配置**。我们可以使用web.xml文件或WebApplicationInitializer类来做到这一点：

```java
public class MyWebAppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext container) {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setConfigLocation("cn.tuyucheng.taketoday");

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
        InternalResourceViewResolver bean = new InternalResourceViewResolver();
        bean.setViewClass(JstlView.class);
        bean.setPrefix("/WEB-INF/view/");
        bean.setSuffix(".jsp");
        return bean;
    }
}
```

相比之下，**一旦我们添加了web starter，Spring Boot只需要几个属性就可以让一切正常进行**：

```properties
spring.mvc.view.prefix=/WEB-INF/jsp/
spring.mvc.view.suffix=.jsp
```

**上面所有的Spring配置都是通过一个名为[自动配置](https://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-auto-configuration.html)的过程添加Boot web starter来自动包含的**。

这意味着Spring Boot将检查应用程序中存在的依赖项、属性和bean，并基于这些启用配置。

当然，如果我们想添加自己的自定义配置，那么Spring Boot自动配置就会退出(无效)。

### 5.1 配置模板引擎

现在让我们学习如何在Spring和Spring Boot中配置Thymeleaf模板引擎。

在Spring中，我们需要为视图解析器添加[thymeleaf-spring5](https://mvnrepository.com/artifact/org.thymeleaf/thymeleaf-spring5)依赖和一些配置：

```java
@Configuration
@EnableWebMvc
public class MvcWebConfig implements WebMvcConfigurer {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
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

Spring Boot 1只需要依赖spring-boot-starter-thymeleaf即可在Web应用程序中启用Thymeleaf支持。由于Thymeleaf 3.0中的新特性，我们还必须在Spring Boot 2 Web应用程序中添加thymeleaf-layout-dialect作为依赖项。或者，我们可以选择添加一个spring-boot-starter-thymeleaf依赖项，它将为我们处理所有这些。

一旦依赖添加到位，我们就可以将模板添加到src/main/resources/templates文件夹中，Spring Boot将自动显示它们。

## 6. Spring Security配置

为了简单起见，我们将了解如何使用这些框架启用默认的HTTP基本身份验证。

让我们首先看一下使用Spring启用安全性所需的依赖项和配置。

**Spring需要标准的spring-security-web和spring-security-config依赖项来在应用程序中设置安全性**。

接下来**我们需要添加一个类来创建SecurityFilterChain bean并使用@EnableWebSecurity标注**：

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

Spring Boot也需要这些依赖项才能使其工作，但我们**只需要定义spring-boot-starter-security的依赖项，因为这会自动将所有相关依赖项添加到类路径中**。

**Spring Boot中的安全配置和上面一样**。

要了解如何在Spring和Spring Boot中实现JPA配置，可以查看我们的文章[Spring与JPA指南](https://www.baeldung.com/the-persistence-layer-with-spring-and-jpa)。

## 7. Spring和Spring Boot的引导

在Spring和Spring Boot中引导应用程序的基本区别在于Servlet。Spring使用web.xml或SpringServletContainerInitializer作为其引导入口点。

另一方面，Spring Boot仅使用Servlet 3功能来引导应用程序。让我们详细谈谈这一点。

### 7.1 Spring如何引导？

Spring既支持遗留的web.xml引导方式，也支持最新的Servlet 3+方法。

让我们分步查看web.xml方法：

1.  Servlet容器(服务器)读取web.xml。
2.  web.xml中定义的DispatcherServlet由容器实例化。
3.  DispatcherServlet通过读取WEB-INF/{servletName}-servlet.xml来创建WebApplicationContext。
4.  最后，DispatcherServlet注册在应用程序上下文中定义的beans。

以下是Spring如何使用Servlet 3+方法进行引导：

1.  容器搜索实现ServletContainerInitializer的类并执行。
2.  SpringServletContainerInitializer查找所有实现WebApplicationInitializer的类。
3.  WebApplicationInitializer使用XML或@Configuration类创建上下文。
4.  WebApplicationInitializer使用先前创建的上下文创建DispatcherServlet。

### 7.2 Spring Boot如何引导？

**Spring Boot应用程序的入口点是用@SpringBootApplication标注的类**：

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

默认情况下，Spring Boot使用嵌入式容器来运行应用程序。在这种情况下，Spring Boot使用public static void main入口点来启动嵌入式Web服务器。

它还负责将Servlet、Filter和ServletContextInitializer bean从应用程序上下文绑定到嵌入式Servlet容器。

Spring Boot的另一个功能是它会自动扫描同一个包中的所有类或Main类的子包中的组件。

此外，Spring Boot提供了将其部署为外部容器中的Web存档的选项。在这种情况下，我们必须扩展SpringBootServletInitializer：

```java
@SpringBootApplication
public class Application extends SpringBootServletInitializer {
    // ...
}
```

在这里，外部Servlet容器查找Web存档的META-INF文件中定义的主类，而SpringBootServletInitializer将负责绑定Servlet、Filter和ServletContextInitializer。

## 8. 打包部署

最后，让我们看看如何打包和部署应用程序。这两个框架都支持常见的包管理技术，如Maven和Gradle；但是，在部署方面，这些框架有很大不同。

例如，[Spring Boot Maven插件](https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/htmlsingle/)在Maven中提供了Spring Boot支持。它还允许打包可执行jar或war存档并“就地”运行应用程序。

在部署上下文中，Spring Boot相对于Spring的一些优势包括：

-   提供嵌入式容器支持
-   使用命令java -jar独立运行jar的预配
-   在外部容器中部署时排除依赖项以避免潜在的jar冲突的选项
-   部署时指定激活Profile的选项
-   用于集成测试的随机端口生成

## 9. 总结

在本文中，我们了解了Spring和Spring Boot之间的区别。

简单来说，Spring Boot就是对Spring本身的扩展，让开发、测试、部署更加方便。
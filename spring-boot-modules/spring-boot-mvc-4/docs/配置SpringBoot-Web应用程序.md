## 一、概述

Spring Boot 可以做很多事情；在本教程中，我们将介绍 Boot 中一些更有趣的配置选项。

## 延伸阅读：

## [从 Spring 迁移到 Spring Boot](https://www.baeldung.com/spring-boot-migration)

查看如何正确地从 Spring 迁移到 Spring Boot。

[阅读更多](https://www.baeldung.com/spring-boot-migration)→

## [使用 Spring Boot 创建自定义 Starter](https://www.baeldung.com/spring-boot-custom-starter)

创建自定义 Spring Boot 启动器的快速实用指南。

[阅读更多](https://www.baeldung.com/spring-boot-custom-starter)→

## [在 Spring Boot 中测试](https://www.baeldung.com/spring-boot-testing)

了解 Spring Boot 如何支持测试，以高效地编写单元测试。

[阅读更多](https://www.baeldung.com/spring-boot-testing)→



## 2.端口号

在主要的独立应用程序中，主要的 HTTP 端口默认为 8080；我们可以轻松地将 Boot 配置为使用不同的端口：

```plaintext
server.port=8083
```

对于基于 YAML 的配置：

```plaintext
server:
    port: 8083
```

我们还可以通过编程方式自定义服务器端口：

```java
@Component
public class CustomizationBean implements
  WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
 
    @Override
    public void customize(ConfigurableServletWebServerFactory container) {
        container.setPort(8083);
    }
}
```

## 3.上下文路径

默认情况下，上下文路径为“/”。如果这不理想并且你需要将其更改为 / app_name之类的内容，这是通过属性进行更改的快速简单方法：

```java
server.servlet.contextPath=/springbootapp
```

对于基于 YAML 的配置：

```java
server:
    servlet:
        contextPath:/springbootapp
```

最后 - 更改也可以通过编程方式完成：

```java
@Component
public class CustomizationBean
  implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
 
    @Override
    public void customize(ConfigurableServletWebServerFactorycontainer) {
        container.setContextPath("/springbootapp");
    }
}
```

## 4. 白标错误页面

`BasicErrorController`如果你没有在配置中指定任何自定义实现，Spring Boot 会自动注册一个bean。

但是，当然可以配置此默认控制器：

```java
public class MyCustomErrorController implements ErrorController {
 
    private static final String PATH = "/error";
    
    @GetMapping(value=PATH)
    public String error() {
        return "Error haven";
    }
}
```

## 5.自定义错误信息

Boot 默认提供/error映射以合理的方式处理错误。

如果你想配置更具体的错误页面，可以使用统一的 Java DSL 来自定义错误处理：

```java
@Component
public class CustomizationBean
  implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
 
    @Override
    public void customize(ConfigurableServletWebServerFactorycontainer) {        
        container.addErrorPages(new ErrorPage(HttpStatus.BAD_REQUEST, "/400"));
        container.addErrorPages(new ErrorPage("/errorHaven"));
    }
}
```

在这里，我们专门处理Bad Request以匹配/400路径，并处理所有其他请求以匹配公共路径。

一个非常简单的/errorHaven实现：

```java
@GetMapping("/errorHaven")
String errorHeaven() {
    return "You have reached the haven of errors!!!";
}
```

输出：

```plaintext
You have reached the haven of errors!!!
```

## 6. 以编程方式关闭引导应用程序

你可以在SpringApplication的帮助下以编程方式关闭 Boot 应用程序。它有一个静态的exit()方法，它有两个参数：ApplicationContext和ExitCodeGenerator：

```java
@Autowired
public void shutDown(ExecutorServiceExitCodeGenerator exitCodeGenerator) {
    SpringApplication.exit(applicationContext, exitCodeGenerator);
}
```

通过这个实用方法，我们可以关闭应用程序。

## 7.配置日志记录级别

你可以轻松地调整引导应用程序中的日志记录级别；从 1.2.0 版本开始，你可以在主属性文件中配置日志级别：

```java
logging.level.org.springframework.web: DEBUG
logging.level.org.hibernate: ERROR
```

就像标准的 Spring 应用程序一样——你可以通过在类路径中添加自定义的 XML 或属性文件并在 pom.xml 中定义库来激活不同的日志记录系统，如Logback、log4j、log4j2等。

## 8. 注册一个新的 Servlet

如果你在嵌入式服务器的帮助下部署应用程序，则可以通过将新的 Servlet公开为传统配置中的 bean 来在 Boot 应用程序中注册它们：

```java
@Bean
public HelloWorldServlet helloWorld() {
    return new HelloWorldServlet();
}
```

或者，你可以使用ServletRegistrationBean ：


```java
@Bean
public SpringHelloServletRegistrationBean servletRegistrationBean() {
 
    SpringHelloServletRegistrationBean bean = new SpringHelloServletRegistrationBean(
      new SpringHelloWorldServlet(), "/springHelloWorld/");
    bean.setLoadOnStartup(1);
    bean.addInitParameter("message", "SpringHelloWorldServlet special message");
    return bean;
}
```

## 9. 在启动应用程序中配置 Jetty 或 Undertow

Spring Boot 启动器通常使用Tomcat 作为默认的嵌入式服务器。如果需要更改 - 你可以排除 Tomcat 依赖项并改为包含 Jetty 或 Undertow：

配置码头

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jetty</artifactId>
</dependency>
@Bean
public JettyEmbeddedServletContainerFactory  jettyEmbeddedServletContainerFactory() {
    JettyEmbeddedServletContainerFactory jettyContainer = 
      new JettyEmbeddedServletContainerFactory();
    
    jettyContainer.setPort(9000);
    jettyContainer.setContextPath("/springbootapp");
    return jettyContainer;
}
```

配置暗流

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-undertow</artifactId>
</dependency>
@Bean
public UndertowEmbeddedServletContainerFactory embeddedServletContainerFactory() {
    UndertowEmbeddedServletContainerFactory factory = 
      new UndertowEmbeddedServletContainerFactory();
    
    factory.addBuilderCustomizers(new UndertowBuilderCustomizer() {
        @Override
        public void customize(io.undertow.Undertow.Builder builder) {
            builder.addHttpListener(8080, "0.0.0.0");
        }
    });
    
    return factory;
}
```

## 10.总结

在这篇简短的文章中，我们介绍了一些更有趣和有用的 Spring Boot 配置选项。

当然，在参考文档中还有很多很多选项可以根据你的需要配置和调整 Boot 应用程序——这些只是我发现的一些更有用的选项。
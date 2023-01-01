## 一、概述

在本教程中，我们将快速介绍SpringBootServletInitializer。

这是WebApplicationInitializer的扩展，它从部署在 Web 容器上的传统 WAR 存档运行SpringApplication 。此类将Servlet、Filter和ServletContextInitializer bean 从应用程序上下文绑定到服务器。

扩展SpringBootServletInitializer类还允许我们在应用程序由 servlet 容器运行时通过覆盖configure()方法来配置我们的应用程序。

## 2. SpringBootServlet初始化器

为了更加实用，我们将展示一个扩展Initializer类的主类示例。

我们的@SpringBootApplication 类称为WarInitializerApplication扩展了SpringBootServletInitializer并覆盖了configure()方法。该方法使用SpringApplicationBuilder简单地将我们的类注册为应用程序的配置类：

```java
@SpringBootApplication
public class WarInitializerApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(
      SpringApplicationBuilder builder) {
        return builder.sources(WarInitializerApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication sa = new SpringApplication(
          WarInitializerApplication.class);
        sa.run(args);
    }

    @RestController
    public static class WarInitializerController {

        @GetMapping("/")
        public String handler() {
           // ...
        }
    }
}

```

现在，如果我们将应用程序打包为 WAR，我们将能够以传统方式将其部署到任何 Web 容器上，这也将执行我们在configure()方法中添加的逻辑。

如果我们想将它打包为 JAR 文件，那么我们需要将相同的逻辑添加到main()方法中，以便嵌入式容器也可以获取它。

## 3.总结

在本文中，我们介绍了SpringBootServletInitializer并演示了如何使用它从经典 WAR 存档运行 Spring Boot 应用程序。
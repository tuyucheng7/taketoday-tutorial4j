## 1. 概述

在本快速教程中，我们将探讨如何使用Spring Boot创建一个简单的基于控制台的应用程序。

## 2. Maven依赖

我们的项目依赖于spring-boot-parent：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.4.0</version>
</parent>
```

所需的初始依赖项是：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>
```

## 3. 控制台应用程序

我们的控制台应用程序由一个类SpringBootConsoleApplication.java组成，它是Spring Boot控制台应用程序的主类。我们在主类上**使用Spring的@SpringBootApplication注解**来启用自动配置。

这个类还实现了**Spring的CommandLineRunner接口**，CommandLineRunner是一个包含单个run方法的简单Spring Boot接口，Spring Boot会在应用程序上下文加载完成后自动调用所有实现该接口的bean的run方法。

下面是我们的控制台应用程序：

```java
@SpringBootApplication
public class SpringBootConsoleApplication implements CommandLineRunner {

    private static Logger LOG = LoggerFactory.getLogger(SpringBootConsoleApplication.class);

    public static void main(String[] args) {
        LOG.info("STARTING THE APPLICATION");
        SpringApplication.run(SpringBootConsoleApplication.class, args);
        LOG.info("APPLICATION FINISHED");
    }

    @Override
    public void run(String... args) {
        LOG.info("EXECUTING : command line runner");

        for (int i = 0; i < args.length; ++i) {
            LOG.info("args[{}]: {}", i, args[i]);
        }
    }
}
```

我们还应该指定spring.main.web-application-type=NONE [Spring属性]()，此属性将明确通知Spring这不是一个Web应用程序。

当我们执行SpringBootConsoleApplication时，我们可以看到记录了以下内容：

```shell
15:59:59.954 [main] INFO  c.t.t.s.SpringBootConsoleApplication - STARTING THE APPLICATION
15:59:59.955 [main] INFO  c.t.t.s.SpringBootConsoleApplication - No active profile set, falling back to default profiles: default
16:00:01.388 [main] INFO  o.s.c.a.AnnotationConfigApplicationContext 
  - Refreshing org.springframework.context.annotation.AnnotationConfigApplicationContext@6497b078: startup date [Sat Jun 16 00:48:52 IST 2018]; root of context hierarchy
16:00:01.407 [main] INFO  o.s.j.e.a.AnnotationMBeanExporter - Registering beans for JMX exposure on startup
16:00:02.047 [main] INFO  c.t.t.s.SpringBootConsoleApplication - EXECUTING : command line runner
16:00:02.056 [main] INFO  c.t.t.s.SpringBootConsoleApplication - args[0]: Hello World!
16:00:02.056 [main] INFO  c.t.t.s.SpringBootConsoleApplication - Started SpringBootConsoleApplication in 1.633 seconds (JVM running for 2.373)
16:00:02.056 [main] INFO  c.t.t.s.SpringBootConsoleApplication - APPLICATION FINISHED
16:00:02.161 [Thread-2] INFO  o.s.c.a.AnnotationConfigApplicationContext 
  - Closing org.springframework.context.annotation.AnnotationConfigApplicationContext@6497b078: startup date [Sat Jun 16 00:48:52 IST 2018]; root of context hierarchy
16:00:02.162 [Thread-2] INFO  o.s.j.e.a.AnnotationMBeanExporter - Unregistering JMX-exposed beans on shutdown
```

请注意，run方法是在加载应用程序上下文之后但在main方法执行完成之前调用的。

大多数控制台应用程序只有一个实现CommandLineRunner的类，如果我们的应用程序有多个实现CommandLineRunner的类，则可以使用Spring的[@Order注解]()指定执行顺序。

## 4. 总结

在这篇简短的文章中，我们学习了如何使用Spring Boot创建一个简单的基于控制台的应用程序。
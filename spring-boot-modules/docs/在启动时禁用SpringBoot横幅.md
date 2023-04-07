## 1. 概述

[Spring Boot](https://www.baeldung.com/spring-boot)是创建Java Web应用程序的好方法，但它的某些默认行为可能并不适合所有人。

一个特殊的功能是在启动时打印的Spring Boot横幅：

[![春季启动横幅 1](https://www.baeldung.com/wp-content/uploads/2019/11/spring-boot-banner-1.jpg)](https://www.baeldung.com/wp-content/uploads/2019/11/spring-boot-banner-1.jpg)

虽然此横幅通常无害，但在某些情况下可能需要禁用它。例如，防止自定义日志记录配置出错或使用远程日志聚合系统节省带宽。

在本教程中，我们将了解一些在启动时禁用Spring Boot横幅的不同方法。

## 2.使用配置

使用配置是禁用启动横幅最灵活的方法。它不需要更改代码，并且可以在需要时轻松恢复。

我们可以使用application.properties禁用启动横幅：

```plaintext
spring.main.banner-mode=off
```

或者如果我们使用application.yaml：

```plaintext
spring:
  main:
    banner-mode: "off"
```

最后，感谢Spring Boot的[外部化配置](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-external-config)支持，我们还可以通过设置环境变量来禁用它：

```plaintext
SPRING_MAIN_BANNER-MODE=off
```

## 3. 使用代码

除了配置之外，还有多种方法可以使用代码禁用Spring Boot横幅。使用代码的缺点是我们需要为每个应用程序执行此操作，并且需要更改代码才能恢复。

使用SpringApplicationBuilder时：

```java
new SpringApplicationBuilder(MyApplication.class)
    .bannerMode(Banner.Mode.OFF)
    .run(args)
```

使用SpringApplication时：

```java
SpringApplication app = new SpringApplication(MyApplication.class);
app.setBannerMode(Banner.Mode.OFF);
app.run(args);
```

## 4. 使用集成开发环境

大多数现代 IDE 都包含一种无需配置或代码即可禁用Spring Boot横幅的方法。

IntelliJ 为Spring Boot运行配置提供了一个复选框，该复选框将禁用横幅：

[![禁用 spring boot banner intellij](https://www.baeldung.com/wp-content/uploads/2019/11/disable-spring-boot-banner-intellij.jpg)](https://www.baeldung.com/wp-content/uploads/2019/11/disable-spring-boot-banner-intellij.jpg)

## 5.更改横幅文字

另一种禁用Spring Boot启动横幅的方法是将横幅文本更改为空文件。

我们首先在application.properties中指定一个自定义文件：

```plaintext
spring.banner.location=classpath:/banner.txt
```

或者，如果我们使用 YAML：

```plaintext
spring:
  banner:
    location: classpath:/banner.txt
```

然后我们在src/main/resources中创建一个名为banner.txt的新空文件。

## 六，总结

在本教程中，我们看到了使用配置或代码组合来禁用Spring Boot横幅的各种方法。
## 1. 概述

**默认情况下，Spring Boot在根上下文路径(“/”)上提供内容**。

虽然优先考虑约定而不是配置通常是个好主意，但在某些情况下我们确实希望拥有自定义路径。

在本快速教程中，我们将介绍配置它的不同方法。

## 2. 设置属性

就像许多其他配置选项一样，可以通过设置属性server.servlet.context-path来更改Spring Boot中的上下文路径。

请注意，这适用于Spring Boot 2.x。对于Spring Boot 1.x，属性是server.context-path。

### 2.1 使用application.properties/yml

更改上下文路径最直接的方法是在application.properties/yml文件中设置属性：

```properties
server.servlet.context-path=/tuyucheng
```

除了将属性文件放在src/main/resources中，我们还可以将其保存在当前工作目录中(在类路径之外)。

### 2.2 Java系统属性

我们还可以在初始化上下文之前将上下文路径设置为Java系统属性：

```java
public static void main(String[] args) {
    System.setProperty("server.servlet.context-path", "/tuyucheng");
    SpringApplication.run(Application.class, args);
}
```

### 2.3 操作系统环境变量

Spring Boot还可以依赖操作系统环境变量。在基于Unix的系统上，我们可以这样写：

```shell
$ export SERVER_SERVLET_CONTEXT_PATH=/tuyucheng
```

在Windows上，设置环境变量的命令是：

```bash
> set SERVER_SERVLET_CONTEXT_PATH=/tuyucheng
```

**上面的环境变量是针对Spring Boot 2.xx，如果我们使用1.xx，则变量是SERVER_CONTEXT_PATH**。

### 2.4 命令行参数

我们也可以通过命令行参数动态设置属性：

```shell
$ java -jar app.jar --server.servlet.context-path=/tuyucheng
```

## 3. 使用Java配置

现在让我们通过使用配置bean填充bean工厂来设置上下文路径。

在Spring Boot 2中，我们可以使用WebServerFactoryCustomizer：

```java
@Bean
public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> webServerFactoryCustomizer() {
    return factory -> factory.setContextPath("/tuyucheng");
}
```

使用Spring Boot 1，我们可以创建EmbeddedServletContainerCustomizer的实例：

```java
@Bean
public EmbeddedServletContainerCustomizer embeddedServletContainerCustomizer() {
    return container -> container.setContextPath("/tuyucheng");
}
```

## 4. 配置的优先级顺序

有了这么多选项，我们最终可能会为同一属性提供多个配置。

这是**降序的[优先级顺序](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html)**，Spring Boot使用它来选择有效的配置：

1.  Java配置
2.  命令行参数
3.  Java系统属性
4.  操作系统环境变量
5.  当前目录中的application.properties
6.  类路径中的application.properties(src/main/resources或打包的jar文件)

## 5. 总结

在这篇简短的文章中，我们介绍了在Spring Boot应用程序中设置上下文路径或任何其他配置属性的不同方法。
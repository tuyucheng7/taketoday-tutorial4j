## 1. 概述

默认情况下，Spring Boot 在根上下文路径(“/”)上提供内容。

虽然优先考虑约定而不是配置通常是个好主意，但在某些情况下我们确实希望拥有自定义路径。

在本快速教程中，我们将介绍配置它的不同方法。

## 2.设置属性

就像许多其他配置选项一样，可以通过设置属性server.servlet.context-path来更改Spring Boot中的上下文路径。

请注意，这适用于Spring Boot 2.x。对于 Boot 1.x，属性是 server.context-path。

有多种设置此属性的方法，让我们一一了解。

### 2.1. 使用 application.properties/yml _ _

更改上下文路径最直接的方法是在application.properties / yml文件中设置属性：

```javascript
server.servlet.context-path=/baeldung
```

除了将属性文件放在src/main/resources中，我们还可以将其保存在当前工作目录中(在类路径之外)。

### 2.2.Java系统属性

我们还可以在上下文初始化之前将上下文路径设置为Java系统属性：

```java
public static void main(String[] args) {
    System.setProperty("server.servlet.context-path", "/baeldung");
    SpringApplication.run(Application.class, args);
}
```

### 2.3. 操作系统环境变量

Spring Boot 还可以依赖操作系统环境变量。在基于 Unix 的系统上，我们可以这样写：

```bash
$ export SERVER_SERVLET_CONTEXT_PATH=/baeldung
```

在 Windows 上，设置环境变量的命令是：

```powershell
> set SERVER_SERVLET_CONTEXT_PATH=/baeldung
```

上面的环境变量是针对Spring Boot 2.xx 如果我们有 1.xx，变量是SERVER_CONTEXT_PATH。

### 2.4. 命令行参数

我们也可以通过命令行参数动态设置属性：

```bash
$ java -jar app.jar --server.servlet.context-path=/baeldung
```

## 3.使用Java配置

现在让我们通过使用配置 bean 填充 bean 工厂来设置上下文路径。

使用Spring Boot2，我们可以使用 WebServerFactoryCustomizer：

```java
@Bean
public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory>
  webServerFactoryCustomizer() {
    return factory -> factory.setContextPath("/baeldung");
}
```

使用Spring Boot1，我们可以创建EmbeddedServletContainerCustomizer的实例 ：

```java
@Bean
public EmbeddedServletContainerCustomizer
  embeddedServletContainerCustomizer() {
    return container -> container.setContextPath("/baeldung");
}
```

## 4.配置的优先顺序

有了这么多选项，我们最终可能会为同一属性拥有多个配置。

这是 降序的[优先级顺序](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html)，Spring Boot使用它来选择有效的配置：

1.  Java配置
2.  命令行参数
3. Java系统属性
4.  操作系统环境变量
5.  当前目录中的application.properties
6.  类路径中的application.properties ( src/main/resources或打包的 jar 文件)

## 5.总结

在这篇简短的文章中，我们介绍了在Spring Boot应用程序中设置上下文路径或任何其他配置属性的不同方法。
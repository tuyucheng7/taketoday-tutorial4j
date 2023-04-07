## 1. 概述

Spring Boot 是一个出色的框架，可用于为各种用例快速创建新的Java应用程序。最流行的用途之一是用作 Web 服务器，使用许多受支持的嵌入式 servlet 容器和模板引擎之一。

然而，Spring Boot 有许多不需要 Web 服务器的用途：[控制台应用程序](https://www.baeldung.com/spring-boot-console-app)、作业调度、[批处理](https://www.baeldung.com/introduction-to-spring-batch)或流处理、[无服务器](https://www.baeldung.com/spring-cloud-function)应用程序等等。

在本教程中，我们将了解几种在没有 Web 服务器的情况下使用Spring Boot的不同方法。

## 2.使用依赖

防止Spring Boot应用程序启动嵌入式 Web 服务器的最简单方法是不在我们的依赖项中包含 Web 服务器启动程序。

这意味着不在Maven POM 或 Gradle 构建文件中包含spring-boot-starter-web依赖项。相反，我们希望在其位置使用更基本的spring-boot-starter依赖项。

请记住，Tomcat 依赖项有可能作为传递依赖项包含在我们的应用程序中。在这种情况下，我们可能需要从包含它的任何依赖项中排除 Tomcat 库。

## 3.修改Spring应用

在Spring Boot中禁用嵌入式 Web 服务器的另一种方法是使用代码。我们可以使用SpringApplicationBuilder：

```java
new SpringApplicationBuilder(MainApplication.class)
  .web(WebApplicationType.NONE)
  .run(args);
```

或者我们可以使用对SpringApplication的引用：

```java
SpringApplication application = new SpringApplication(MainApplication.class);
application.setWebApplicationType(WebApplicationType.NONE);
application.run(args);
```

在任何一种情况下，我们都可以在 classpath 上保持 servlet 和容器 API 可用。这意味着我们仍然可以在不启动 Web 服务器的情况下使用 Web 服务器库。这可能很有用，例如，如果我们想使用它们来编写测试或在我们自己的代码中使用它们的 API。

## 4. 使用应用程序属性

使用代码禁用 Web 服务器是一个静态选项——无论我们将其部署在何处，它都会影响我们的应用程序。但是，如果我们想在特定情况下创建 Web 服务器怎么办？

在这种情况下，我们可以使用 Spring 应用程序属性：

```plaintext
spring.main.web-application-type=none
```

或者使用等效的 YAML：

```yaml
spring:
  main:
    web-application-type: none
```

这种方法的好处是我们可以有条件地启用 Web 服务器。使用[Spring 配置文件](https://www.baeldung.com/spring-profiles)或[条件](https://www.baeldung.com/spring-conditionalonproperty)，我们可以控制不同部署中的 Web 服务器行为。

例如，我们可以让 Web 服务器仅在开发中运行以公开指标或其他 Spring 端点，同时出于安全原因在生产中将其禁用。

请注意，某些早期版本的Spring Boot使用名为web-environment的布尔属性来启用和禁用 Web 服务器。随着Spring Boot中传统容器和反应容器的采用，该属性已重命名，现在使用 enum。

## 5.总结

在没有 Web 服务器的情况下创建Spring Boot应用程序的原因有很多。在本教程中，我们看到了执行此操作的多种方法。每个都有自己的优点和缺点，所以我们应该选择最能满足我们需求的方法。
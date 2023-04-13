## 1. 概述

自推出以来，Spring Boot一直是Spring生态系统中的关键参与者。该项目通过其自动配置功能使我们的生活更加轻松。

在本教程中，我们将介绍一些在求职面试中可能会出现的与Spring Boot相关的最常见问题。

## 延伸阅读

### [顶级Spring框架面试问题](https://www.baeldung.com/spring-interview-questions)

快速讨论在求职面试中可能出现的有关Spring框架的常见问题。

[阅读更多](https://www.baeldung.com/spring-interview-questions)→

### [Spring和Spring Boot的比较](https://www.baeldung.com/spring-vs-spring-boot)

了解Spring和Spring Boot之间的区别。

[阅读更多](https://www.baeldung.com/spring-vs-spring-boot)→

## 2. 问题

### Q1. 什么是Spring Boot，它的主要特点是什么？

Spring Boot本质上是一个构建在Spring框架之上的用于快速应用程序开发的框架。凭借其自动配置和嵌入式应用程序服务器支持，结合广泛的文档和社区支持，Spring Boot是迄今为止Java生态系统中最受欢迎的技术之一。

以下是一些显著的特点：

-   [Starters](https://www.baeldung.com/spring-boot-starters)：一组依赖描述符，用于一次性包含相关依赖项
-   [自动配置](https://www.baeldung.com/spring-boot-annotations#enable-autoconfiguration)：一种根据类路径上存在的依赖项自动配置应用程序的方法
-   [Actuator](https://www.baeldung.com/spring-boot-actuators)：获得生产就绪的功能，如监控
-   [安全](https://www.baeldung.com/security-spring)
-   [日志记录](https://www.baeldung.com/spring-boot-logging)

### Q2. Spring和Spring Boot有什么区别？

Spring框架提供了多种功能，使Web应用程序的开发更加容易。这些功能包括依赖注入、数据绑定、面向切面的编程、数据访问等等。

多年来，Spring变得越来越复杂，此类应用程序所需的配置量可能令人生畏。这就是Spring Boot派上用场的地方-它使配置Spring应用程序变得轻而易举。

从本质上讲，虽然Spring是独立的，**但Spring Boot对平台和库采取了固执己见的观点，让我们可以快速上手**。

以下是Spring Boot带来的两个最重要的好处：

-   根据在类路径上找到的工件自动配置应用程序
-   提供生产中应用程序通用的非功能特性，例如安全或健康检查

请查看我们的其他教程，了解[Spring和Spring Boot之间的详细比较](https://www.baeldung.com/spring-vs-spring-boot)。

### Q3. 我们如何使用Maven设置Spring Boot应用程序？

我们可以像对待任何其他库一样将Spring Boot包含在Maven项目中。但是，最好的方法是继承spring-boot-starter-parent项目并声明对[Spring Boot Starters](https://www.baeldung.com/spring-boot-starters)的依赖。这样做可以让我们的项目重用Spring Boot的默认设置。

继承spring-boot-starter-parent项目很简单-我们只需要在pom.xml中指定一个父元素：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.4.0.RELEASE</version>
</parent>
```

我们可以在[Maven Central](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-parent/3.0.5)上找到最新版本的spring-boot-starter-parent。

**使用starter-parent项目很方便，但并不总是可行**。例如，如果我们公司要求所有项目都继承自标准POM，我们仍然可以使用[自定义Parent](https://www.baeldung.com/spring-boot-dependency-management-custom-parent)从Spring Boot的依赖管理中获益。

### Q4. 什么是Spring Initializr？

Spring Initializr是创建Spring Boot项目的便捷方式。

我们可以到[Spring Initializr](https://start.spring.io/)站点，选择依赖管理工具(Maven或Gradle)、语言(Java、Kotlin或Groovy)、打包方案(Jar或War)、版本和依赖项，然后下载项目。

这为我们创建了一个框架项目并节省了设置时间，以便我们可以专注于添加业务逻辑。

即使当我们使用我们的IDE(例如STS或带有STS插件的Eclipse)新建项目向导来创建Spring Boot项目时，它也会在底层使用Spring Initializr。

### Q5. 有哪些Spring Boot Starters可用？

每个启动器都扮演着我们需要的所有Spring技术的一站式商店的角色。然后以一致的方式传递并管理其他所需的依赖项。

所有启动器都在org.springframework.boot组下，它们的名称以spring-boot-starter-开头。**这种命名模式使得查找启动器变得容易，特别是在使用支持按名称搜索依赖项的IDE时**。

在撰写本文时，我们可以使用50多个启动器。在这里，我们将列出最常见的：

-   spring-boot-starter：核心启动器，包括自动配置支持、日志记录和YAML
-   spring-boot-starter-aop：用于使用Spring AOP和AspectJ进行面向切面的编程
-   spring-boot-starter-data-jpa：用于将Spring Data JPA与Hibernate一起使用
-   spring-boot-starter-security：用于使用Spring Security
-   spring-boot-starter-test：用于测试Spring Boot应用程序
-   spring-boot-starter-web：用于构建Web，包括RESTful，使用Spring MVC的应用程序

有关启动器的完整列表，请参阅[此仓库](https://github.com/spring-projects/spring-boot/tree/master/spring-boot-project/spring-boot-starters)。

要查找有关Spring Boot Starters的更多信息，请查看[Spring Boot Starters简介](https://www.baeldung.com/spring-boot-starters)。

### Q6. 如何禁用特定的自动配置？

如果我们想禁用特定的自动配置，我们可以使用@EnableAutoConfiguration注解的exclude属性来指示它。

例如，此代码片段禁用了DataSourceAutoConfiguration：

```java
// other annotations
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
public class MyConfiguration { }
```

如果我们使用@SpringBootApplication注解启用自动配置-它具有@EnableAutoConfiguration作为元注解，我们也可以使用exclude属性禁用自动配置：

```java
// other annotations
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class MyConfiguration { }
```

我们还可以使用spring.autoconfigure.exclude环境属性禁用自动配置。application.properties文件中的此设置执行与以前相同的操作：

```properties
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
```

### Q7. 如何注册自定义自动配置？

要注册自动配置类，我们必须在META-INF/spring.factories文件中的EnableAutoConfiguration键下列出其完全限定名称：

```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=cn.tuyucheng.taketoday.autoconfigure.CustomAutoConfiguration
```

如果我们使用Maven构建一个项目，该文件应该放在resources/META-INF目录中，该目录将在打包阶段位于上述位置。

### Q8. 当Bean存在时，如何告诉自动配置退出？

要指示自动配置类在bean已存在时退出，我们可以使用@ConditionalOnMissingBean注解。

此注解最引人注目的属性是：

-   value：要检查的bean的类型
-   name：要检查的bean的名称

当放置在装饰有@Bean的方法上时，目标类型默认为方法的返回类型：

```java
@Configuration
public class CustomConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public CustomService service() {
        // ...
    }
}
```

### Q9. 如何将Spring Boot Web应用程序部署为Jar和War文件？

传统上，我们将Web应用程序打包为WAR文件，然后将其部署到外部服务器中。这样做可以让我们在同一台服务器上安排多个应用程序。当CPU和内存稀缺时，这是一种节省资源的好方法。

但情况发生了变化。现在计算机硬件相当便宜，注意力已经转向服务器配置。在部署过程中配置服务器的一个小错误可能会导致灾难性的后果。

**Spring通过提供一个插件来解决这个问题，即spring-boot-maven-plugin，将Web应用程序打包为可执行JAR**。

要包含此插件，只需将plugin元素添加到pom.xml：

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
</plugin>
```

有了这个插件，我们将在执行package阶段后得到一个fat JAR。这个JAR包含所有必要的依赖项，包括一个嵌入式服务器。因此，我们不再需要担心配置外部服务器。

然后，我们可以像运行普通的可执行JAR一样运行该应用程序。

请注意，pom.xml文件中的packaging元素必须设置为jar才能构建JAR文件：

```xml
<packaging>jar</packaging>
```

如果我们不包含此元素，它也默认为jar。

要构建WAR文件，我们将packaging元素更改为war：

```xml
<packaging>war</packaging>
```

并将容器依赖项保留在打包文件中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-tomcat</artifactId>
    <scope>provided</scope>
</dependency>
```

在执行Maven package阶段后，我们将得到一个可部署的WAR文件。

### Q10. 如何将Spring Boot用于命令行应用程序？

就像任何其他Java程序一样，Spring Boot命令行应用程序必须有一个main方法。

此方法用作入口点，它调用SpringApplication#run方法来引导应用程序：

```java
@SpringBootApplication
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class);
        // other statements
    }
}
```

然后SpringApplication类启动Spring容器并自动配置bean。

请注意，我们必须将配置类传递给run方法以用作主要配置源。按照惯例，这个参数是入口类本身。

调用run方法后，我们可以像在常规程序中一样执行其他语句。

### Q11. 外部配置的可能来源是什么？

Spring Boot提供了对外部配置的支持，允许我们在各种环境中运行同一个应用程序。**我们可以使用属性文件、YAML文件、环境变量、系统属性和命令行选项参数来指定配置属性**。

然后，我们可以使用@Value注解、通过[@ConfigurationProperties注解](https://www.baeldung.com/configuration-properties-in-spring-boot)绑定的对象或Environment抽象来访问这些属性。

### Q12. Spring Boot支持宽松绑定是什么意思？

Spring Boot中的宽松绑定适用于[配置属性的类型安全绑定](https://www.baeldung.com/configuration-properties-in-spring-boot)。

使用宽松绑定时，**属性的键不需要与属性名称完全匹配**。这样的环境属性可以用驼峰式、kebab-case、蛇形大小写或用下划线分隔的大写字母书写。

例如，如果带有@ConfigurationProperties注解的bean类中的一个属性名为myProp，它可以绑定到这些环境属性中的任何一个：myProp、my-prop、my_prop或MY_PROP。

### Q13. Spring Boot DevTools有什么用？

Spring Boot DevTools或DevTools是一组使开发过程更容易的工具。

要包含这些开发时功能，我们只需要向pom.xml文件添加依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
</dependency>
```

如果应用程序在生产环境中运行，则spring-boot-devtools模块会自动禁用。压缩包的重新打包也默认排除了这个模块。所以，它不会给我们的最终产品带来任何开销。

默认情况下，DevTools应用适合开发环境的属性。这些属性禁用模板缓存，为web组启用调试日志记录，等等。结果，我们在不设置任何属性的情况下获得了这个合理的开发时配置。

**只要类路径上的文件发生更改，使用DevTools的应用程序就会重新启动**。这是开发中非常有用的功能，因为它可以为修改提供快速反馈。

默认情况下，静态资源(包括视图模板)不会引发重启。相反，资源更改会触发浏览器刷新。请注意，只有在浏览器中安装了LiveReload扩展以与DevTools包含的嵌入式LiveReload服务器进行交互时，才会发生这种情况。

有关此主题的更多信息，请参阅[Spring Boot DevTools概述](https://www.baeldung.com/spring-boot-devtools)。

### Q14. 如何编写集成测试？

为Spring应用程序运行集成测试时，我们必须有一个ApplicationContext。

为了让我们的工作更轻松，Spring Boot提供了一个特殊的测试注解-@SpringBootTest。此注解从其classes属性指示的配置类创建ApplicationContext。

**如果未设置classes属性，Spring Boot会搜索主配置类**。搜索从包含测试的包开始，直到找到用@SpringBootApplication或@SpringBootConfiguration标注的类。

有关详细说明，请查看我们关于[在Spring Boot中进行测试](https://www.baeldung.com/spring-boot-testing)的教程。

### Q15. 什么是Spring Boot Actuator？

从本质上讲，Actuator通过启用生产就绪功能使Spring Boot应用程序栩栩如生。**这些功能使我们能够在应用程序在生产环境中运行时对其进行监控和管理**。

将Spring Boot Actuator集成到项目中非常简单。我们需要做的就是在pom.xml文件中包含spring-boot-starter-actuator启动器：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Spring Boot Actuator可以使用HTTP或JMX端点公开操作信息。但是大多数应用程序都使用HTTP，其中端点的标识和/actuator前缀构成了URL路径。

以下是Actuator提供的一些最常见的内置端点：

-   env公开环境属性
-   health显示应用程序运行状况信息
-   httptrace显示HTTP跟踪信息
-   info显示任意应用程序信息
-   metrics显示指标信息
-   loggers显示和修改应用程序中记录器的配置
-   mappings显示所有@RequestMapping路径的列表

请参阅我们的[Spring Boot Actuator教程](https://www.baeldung.com/spring-boot-actuators)了解详细信息。

### Q16. 配置Spring Boot项目哪个更好-Properties还是YAML？

与属性文件相比，YAML具有许多优势：

-   更清晰和更好的可读性
-   非常适合分层配置数据，这些数据也以更好、更易读的格式表示
-   支持Map、List和标量类型
-   可以在同一个文件中包含多个[配置文件](https://www.baeldung.com/spring-profiles)(自Spring Boot 2.4.0起，这也适用于属性文件)

但是，由于其缩进规则，编写它可能有点困难且容易出错。

有关详细信息和工作示例，请参阅我们的[Spring YAML与Properties](https://www.baeldung.com/spring-yaml-vs-properties)教程。

### Q17. Spring Boot提供了哪些基本注解？

Spring Boot提供的主要注解位于其org.springframework.boot.autoconfigure及其子包中。

这是几个基本的注解：

-   @EnableAutoConfiguration：使Spring Boot在其类路径中查找自动配置bean并自动应用它们
-   @SpringBootApplication：表示引导应用程序的主类。此注解将@Configuration、@EnableAutoConfiguration和@ComponentScan注解与其默认属性组合在一起。

[Spring Boot注解](https://www.baeldung.com/spring-boot-annotations)一文提供了对该主题的更多概述。

### Q18. 如何更改Spring Boot中的默认端口？

我们可以使用以下方式之一[更改嵌入在Spring Boot中的服务器的默认端口](https://www.baeldung.com/spring-boot-change-port)：

-   使用属性文件：我们可以使用属性server.port在application.properties(或application.yml)文件中定义它。

-   以编程方式：在我们的主@SpringBootApplication类中，我们可以在SpringApplication实例上设置server.port。

-   使用命令行：当应用程序作为jar文件运行时，我们可以将server.port设置为java命令参数：

    ```shell
    java -jar -Dserver.port=8081 myspringproject.jar
    ```

### Q19. Spring Boot支持哪些嵌入式服务器，如何修改默认值？

截至目前，**Spring MVC支持Tomcat、Jetty和Undertow**。Tomcat是Spring Boot的web starter支持的默认应用服务器。

**Spring WebFlux默认支持Reactor Netty、Tomcat、Jetty和Undertow**。

在Spring MVC中，要更改默认值，假设是Jetty，我们需要排除Tomcat并在依赖项中包含Jetty：

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
```

同样，要将WebFlux中的默认值更改为UnderTow，我们需要排除Reactor Netty并在依赖项中包含UnderTow。

[比较Spring Boot中的嵌入式Servlet容器](https://www.baeldung.com/spring-boot-servlet-containers)提供了有关我们可以与Spring MVC一起使用的不同嵌入式服务器的更多详细信息。

### Q20. 为什么我们需要Spring Profile？

在为企业开发应用程序时，我们通常会处理多个环境，例如Dev、QA和Prod。这些环境的配置属性不同。

例如，我们可能为Dev使用嵌入式H2数据库，但Prod可能拥有专有的Oracle或DB2。即使DBMS在不同环境中相同，URL也肯定会不同。

为了使这变得简单和干净，**Spring提供了Profile来帮助分离每个环境的配置**。属性可以保存在单独的文件中，例如application-dev.properties和application-prod.properties，而不是以编程方式维护它。默认的application.properties使用spring.profiles.active指向当前激活的Profile，以便选择正确的配置。

[Spring Profiles](https://www.baeldung.com/spring-profiles)给出了这个主题的全面介绍。

## 3. 总结

本文讨论了在技术面试中可能会出现的一些关于Spring Boot的最关键问题。
## 1. 概述

在本文中，我们将了解如何将现有的Spring应用程序迁移到Spring Boot应用程序。

**Spring Boot并不是为了取代Spring，而是为了让使用它更快、更容易**。因此，迁移应用程序所需的大部分更改都与配置相关。在大多数情况下，我们的自定义控制器和其他组件将保持不变。

使用Spring Boot进行开发具有以下几个优点：

-   更简单的依赖管理
-   默认自动配置
-   嵌入式Web服务器
-   应用程序指标和健康检查
-   高级外部化配置

## 2. Spring Boot启动器

首先，我们需要一组新的依赖项。**Spring Boot提供了方便的启动器依赖项，这些依赖项描述符可以为某些功能引入所有必要的技术**。

这些的优点是你不再需要为每个依赖项指定版本，而是让启动器为你管理依赖项。

最快的入门方法是添加[spring-boot-starter-parent](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-parent/3.0.5)：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>1.5.6.RELEASE</version>
</parent>
```

这将负责依赖管理。

我们将在接下来的部分中介绍更多的启动器，具体取决于我们将迁移的功能。作为参考，你可以在[此处](https://github.com/spring-projects/spring-boot/tree/master/spring-boot-project/spring-boot-starters)找到完整的启动器列表。

**作为更一般的说明，我们将要删除任何也由Spring Boot管理的显式定义的依赖项版本。否则，我们可能会遇到定义的版本与Boot使用的版本之间的不兼容问题**。

## 3. 应用程序入口

每个使用Spring Boot构建的应用程序都需要定义主入口点。这通常是一个带有main方法的Java类，用@SpringBootApplication标注：

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

@SpringBootApplication注解添加如下注解：

-   @Configuration：将类标记为bean定义的来源
-   @EnableAutoConfiguration：告诉框架根据类路径上的依赖自动添加bean
-   @ComponentScan：扫描Application类所在包和子包中的其他配置和bean

**默认情况下，@SpringBootApplication注解会扫描同一个包或子包中的所有类**。因此，一个方便的包结构可能如下所示：

<img src="../assets/img_12.png">

如果你的应用程序是创建ApplicationContext的非Web应用程序，则可以删除此代码并将其替换为上面的@SpringBootApplication类。

我们可能遇到的一个问题是多个配置类发生冲突。为了避免这种情况，我们可以过滤扫描的类：

```java
@SpringBootAppliaction
@ComponentScan(excludeFilters = {
      @ComponentScan.Filter(type = FilterType.REGEX, pattern = "cn.tuyucheng.taketoday.config.*")})
public class Application {
    // ...
}
```

## 4. 导入配置和组件

Spring Boot在很大程度上依赖于注解进行配置，但你仍然可以以注解和XML格式导入现有配置。

要获取现有的@Configuration或组件类，你有两个选择：

-   将现有类移动到与主应用程序类包相同或主应用程序类子包的包
-   显式导入类

**要显式导入类，你可以在主类上使用@ComponentScan或@Import注解**：

```java
@SpringBootApplication
@ComponentScan(basePackages="cn.tuyucheng.taketoday.config")
@Import(UserRepository.class)
public class Application {
    // ...
}
```

官方文档建议使用注解而不是XML配置。但是，如果你已经有XML文件并且不希望转换为Java配置，你仍然可以使用@ImportResource导入这些文件：

```java
@SpringBootApplication
@ImportResource("applicationContext.xml")
public class Application {
    // ...
}
```

## 5. 迁移应用程序资源

默认情况下，Spring Boot在以下位置之一查找资源文件：

-   /resources
-   /public
-   /static
-   /META-INF/resources

要迁移，我们可以将所有资源文件移动到这些位置之一，或者我们可以通过设置spring.resources.static-locations属性来自定义资源位置：

```properties
spring.resources.static-locations=classpath:/images/,classpath:/jsp/
```

## 6. 迁移应用程序属性

框架将自动加载位于以下位置之一的名为application.properties或application.yml的文件中定义的任何属性：

-   当前目录的/config子目录
-   当前目录
-   类路径上的/config目录
-   类路径根目录

为避免显式加载属性，我们可以将它们移动到这些位置之一的同名文件中。例如，移动到类路径中应该存在的/resources文件夹。

我们还可以从名为application-{profile}.properties的文件中自动加载特定于Profile的属性。

此外，还有大量[预定义的属性名称](https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-application-properties.html)可用于配置不同的应用程序行为。

你在应用程序中使用的每个Spring框架模块都需要稍作修改，主要与配置有关。让我们来看看一些最常用的功能。

## 7. 迁移Spring Web应用程序

### 7.1 Web启动器

Spring Boot为Web应用程序提供了一个启动器，它将引入所有必要的依赖项。这意味着我们可以从Spring框架中删除所有特定于Web的依赖项，并将它们替换为[spring-boot-starter-web](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-web/3.0.3)：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

由于Spring Boot会尽可能根据类路径自动配置应用程序，因此添加此依赖项将导致将@EnableWebMvc注解添加到主应用程序类，并设置DispatcherServlet bean。

如果你有一个设置DispatcherServlet的WebApplicationInitializer类，则这不再是必需的，@EnableWebMvc注解也不是必需的。

当然，如果我们想要自定义行为，我们可以定义我们的bean，在这种情况下，我们的bean将被使用。

**如果我们在@Configuration类上显式使用@EnableWebMvc注解，则MVC自动配置将不再启用**。

添加Web启动器还决定了以下bean的自动配置：

-   支持从类路径上名为/static、/public、/resources或/META-INF/resources的目录提供静态内容
-   用于JSON和XML等常见用例的HttpMessageConverter bean
-   处理所有错误的/error映射

### 7.2 视图技术

就构建网页而言，官方文档建议不要使用JSP文件，而是使用模板引擎。以下模板引擎包含自动配置：Thymeleaf、Groovy、FreeMarker、Mustache。要使用其中之一，我们需要做的就是添加特定的启动器：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

模板文件应放在/resources/templates文件夹中。

如果我们想继续使用JSP文件，我们需要配置应用程序以便它可以解析JSP。例如，如果我们的文件位于/webapp/WEB-INF/views中，那么我们需要设置以下属性：

```properties
spring.mvc.view.prefix=/WEB-INF/views/
spring.mvc.view.suffix=.jsp
```

### 7.3 嵌入式Web服务器

此外，我们还可以使用嵌入式Tomcat服务器运行我们的应用程序，该服务器将通过添加[spring-boot-starter-tomcat](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-tomcat/3.0.3)依赖项自动配置在端口8080上：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-tomcat</artifactId>
</dependency>
```

Spring Boot提供自动配置的其他Web服务器是Jetty和Undertow。

## 8. 迁移Spring Security应用程序

启用Spring Security的启动器是[spring-boot-starter-security](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-security/3.0.3)：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

默认情况下，这将创建一个名为“user”的用户，并在启动期间记录随机生成的密码，并使用基本身份验证保护所有端点。但是，我们通常希望添加与默认设置不同的安全配置。

出于这个原因，我们将使用@EnableWebSecurity标注我们现有的类，它创建一个SecurityFilterChain bean并定义一个自定义配置：

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // ...
    }
}
```

## 9. 迁移Spring Data应用程序

根据我们使用的Spring Data实现，我们需要添加相应的启动器。例如，对于JPA，我们可以添加[spring-boot-starter-data-jpa](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa/3.0.3)依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

如果我们想使用内存数据库，添加相应的依赖项可以为H2、Derby和HSQLDB类型的数据库启用自动配置。

例如，要使用H2内存数据库，我们只需要[h2](https://central.sonatype.com/artifact/com.h2database/h2/2.1.212)依赖项：

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
</dependency>
```

如果我们想要使用不同的数据库类型和配置，例如MySQL数据库，那么我们需要添加依赖项以及定义配置。

为此，我们可以保留我们的DataSource bean定义或使用预定义的属性：

```properties
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/myDb?createDatabaseIfNotExist=true
spring.datasource.username=user
spring.datasource.password=pass
```

Spring Boot会自动将Hibernate配置为默认的JPA提供程序，以及一个transactionManager bean。

## 10. 总结

在本文中，我们展示了将现有Spring应用程序迁移到较新的Spring Boot框架时遇到的一些常见场景。

总的来说，迁移时的体验当然在很大程度上取决于你构建的应用程序。
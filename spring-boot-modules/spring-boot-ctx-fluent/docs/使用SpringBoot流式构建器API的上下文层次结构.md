## 1. 概述

可以在Spring Boot中创建单独的上下文并将它们组织在一个层次结构中。

上下文层次结构可以在Spring Boot应用程序中以不同的方式定义。在本文中，我们将了解如何**使用流式的构建器API创建多个上下文**。

由于我们不会详细介绍如何设置Spring Boot应用程序，因此你可能需要查看这篇[文章]()。

## 2. 应用上下文层次结构

我们可以有多个共享[父子关系](https://www.baeldung.com/spring-boot-context-hierarchy)的应用程序上下文。

**上下文层次结构允许多个子上下文共享驻留在父上下文中的bean**，每个子上下文都可以覆盖从父上下文继承的配置。

此外，我们可以使用上下文来防止在一个上下文中注册的bean可以在另一个上下文中访问，这有助于创建松散耦合的模块。

这里值得注意的一点是，一个上下文只能有一个父上下文，而一个父上下文可以有多个子上下文。此外，子上下文可以访问父上下文中的bean，但反之则不行。

## 3. 使用SpringApplicationBuilder API

SpringApplicationBuilder类提供了一个流式的API，可以使用parent()、child()和sibling()方法在上下文之间创建父子关系。

为了举例说明上下文层次结构，**我们将设置一个具有2个子Web上下文的非Web父应用程序上下文**。

为了演示这一点，我们将启动两个嵌入式Tomcat实例，每个实例都有自己的Web应用程序上下文，并且都在单个JVM中运行。

### 3.1 父上下文

首先，让我们创建一个Service bean以及一个驻留在父包中的bean定义类，我们希望这个bean返回一条问候语，显示给我们的Web应用程序的客户端：

```java
@Service
public class HomeServiceImpl implements HomeService {

    public String getGreeting() {
        return "Welcome User";
    }
}
```

和bean定义类：

```java
@Configuration
@ComponentScan("cn.tuyucheng.taketoday.parent")
public class ServiceConfig {}
```

接下来，我们将为两个子上下文创建配置。

### 3.2 子上下文

由于所有上下文都是使用默认配置文件配置的，因此我们需要为不能在上下文之间共享的属性(例如服务器端口)提供单独的配置。

为了防止自动配置选择冲突的配置，我们还将这些类保存在单独的包中。

首先我们为第一个子上下文定义一个属性文件：

```properties
server.port=8074
server.servlet.context-path=/ctx1

spring.application.admin.enabled=false
spring.application.admin.jmx-name=org.springframework.boot:type=Ctx1Rest,name=Ctx1Application
```

请注意，我们已经配置了端口和上下文路径，以及JMX名称，因此应用程序名称不会发生冲突。

现在让我们为此上下文添加主配置类：

```java
@Configuration
@ComponentScan("cn.tuyucheng.taketoday.ctx1")
@EnableAutoConfiguration
public class Ctx1Config {

    @Bean
    public HomeService homeService() {
        return new GreetingService();
    }
}
```

此类为homeService bean提供了一个新定义，该定义将覆盖来自父级的bean。

让我们看看GreetingServiceImpl类的定义：

```java
@Service
public class GreetingServiceImpl implements HomeService {

    public String getGreeting() {
        return "Greetings for the day";
    }
}
```

最后，我们将为此Web上下文添加一个控制器，它使用homeService bean向用户显示消息：

```java
@RestController
public class Ctx1Controller {

    @Autowired
    private HomeService homeService;

    @GetMapping("/home")
    public String greeting() {
        return homeService.getGreeting();
    }
}
```

### 3.3 同级上下文

对于我们的第二个上下文，我们将创建一个控制器和配置类，它们与上一节中的非常相似。

这一次，我们不会创建homeService bean-因为我们将从父上下文访问它。

首先，让我们为此上下文添加一个属性文件：

```properties
server.port=8075
server.servlet.context-path=/ctx2

spring.application.admin.enabled=false
spring.application.admin.jmx-name=org.springframework.boot:type=WebAdmin,name=SpringWebApplication
```

以及同级应用程序的配置类：

```java
@Configuration
@ComponentScan("cn.tuyucheng.taketoday.ctx2")
@EnableAutoConfiguration
@PropertySource("classpath:ctx2.properties")
public class Ctx2Config {}
```

我们也添加一个控制器，它将HomeService作为依赖项：

```java
@RestController
public class Ctx2Controller {

    @Autowired
    private HomeService homeService;

    @GetMapping("/greeting")
    public String getGreeting() {
        return homeService.getGreeting();
    }
}
```

在这种情况下，我们的控制器应该从父上下文中获取homeService bean。

### 3.4 上下文层次结构

现在我们可以将所有内容放在一起并使用SpringApplicationBuilder定义上下文层次结构：

```java
public class App {
    public static void main(String[] args) {
        new SpringApplicationBuilder()
              .parent(ParentConfig.class).web(WebApplicationType.NONE)
              .child(WebConfig.class).web(WebApplicationType.SERVLET)
              .sibling(RestConfig.class).web(WebApplicationType.SERVLET)
              .run(args);
    }
}
```

最后，在运行Spring Boot应用程序时，我们可以使用localhost:8074/ctx1/home和localhost:8075/ctx2/greeting在各自的端口访问这两个应用程序。

## 4. 总结

使用SpringApplicationBuilder API，我们首先在应用程序的两个上下文之间创建父子关系。接下来，我们介绍了如何在子上下文中覆盖父配置。最后，我们添加了一个同级上下文来演示父上下文中的配置如何与其他子上下文共享。
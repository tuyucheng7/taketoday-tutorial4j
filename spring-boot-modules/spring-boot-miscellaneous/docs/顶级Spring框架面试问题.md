## 1. 概述

在本教程中，我们将探讨在求职面试中可能会出现的一些最常见的Spring相关问题。

## 延伸阅读：

## [Java面试题](https://www.baeldung.com/java-interview-questions)

了解常见Java面试问题的答案

[阅读更多](https://www.baeldung.com/java-interview-questions)→

## [Java 8面试问题(+答案)](https://www.baeldung.com/java-8-interview-questions)

一组流行的Java8 相关面试问题，当然还有答案。

[阅读更多](https://www.baeldung.com/java-8-interview-questions)→

## [Java 集合面试题](https://www.baeldung.com/java-collections-interview-questions)

一组实用的Collections相关的Java面试题

[阅读更多](https://www.baeldung.com/java-collections-interview-questions)→

## 2.Spring芯

### Q1。什么是Spring框架？

Spring 是用于开发Java企业版应用程序的最广泛使用的框架。此外，Spring 的核心特性可用于开发任何Java应用程序。

我们使用它的扩展在 Jakarta EE 平台之上构建各种 Web 应用程序。我们也可以在简单的独立应用程序中使用它的依赖注入条款。

### Q2。使用Spring有什么好处？

Spring 的目标是让 Jakarta EE 的开发更容易，所以让我们看看它的优势：

-   轻量级——在开发中使用框架的开销很小。
-   控制反转 (IoC) –Spring容器负责连接各种对象的依赖关系，而不是创建或查找依赖对象。
-   面向方面的编程 (AOP) –Spring支持 AOP 以将业务逻辑与系统服务分离。
-   IoC 容器——管理Spring Bean生命周期和项目特定的配置
-   MVC 框架 ——用于创建 Web 应用程序或RESTful Web服务，能够返回XML/JSON响应
-   事务管理——通过使用Java注解或Spring BeanXML配置文件，减少 JDBC 操作、文件上传等样板代码量
-   异常处理——Spring 提供了一个方便的 API，用于将特定于技术的异常转换为未经检查的异常。

### Q3. 你知道哪些Spring子项目？简要描述它们。

-   核心——提供框架基础部分的关键模块，例如 IoC 或 DI
-   JDBC – 启用 JDBC 抽象层，无需为特定供应商数据库进行 JDBC 编码
-   ORM 集成——为流行的对象关系映射API提供集成层，例如 JPA、JDO 和 Hibernate
-   Web——一个面向 web 的集成模块，提供多部分文件上传、Servlet 侦听器和面向 web 的应用程序上下文功能
-   MVC 框架——一个实现模型视图控制器设计模式的网络模块
-   AOP 模块——面向方面的编程实现，允许定义干净的方法拦截器和切入点

### Q4. 什么是依赖注入？

依赖注入是控制反转 (IoC) 的一个方面，是一个通用概念，说明我们不手动创建对象，而是描述应如何创建对象。然后 IoC 容器将在需要时实例化所需的类。

更多详情，请看[这里](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring)。

### Q5. 我们如何在Spring中注入 Bean？

为了注入Spring bean，存在一些不同的选项：

-   二传手注射
-   构造函数注入
-   现场注入

可以使用XML文件或注解来完成配置。

有关更多详细信息，请查看[这篇文章](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring)。

### Q6. 哪种是注入 Bean 的最佳方式？为什么？

推荐的方法是对强制依赖项使用构造函数参数，对可选依赖项使用设置器。这是因为构造函数注入允许将值注入不可变字段并使测试更容易。

### Q7. BeanFactory 和 ApplicationContext 有什么区别？

BeanFactory是表示提供和管理 bean 实例的容器的接口。默认实现在调用getBean()时延迟实例化 beans。

相反，ApplicationContext是一个接口，表示一个容器，其中包含应用程序中的所有信息、元数据和 bean。它还扩展了BeanFactory接口，但默认实现会在应用程序启动时立即实例化 beans。但是，可以为单个 bean 覆盖此行为。

对于所有差异，请参阅[文档](https://docs.spring.io/spring/docs/current/spring-framework-reference/html/beans.html)。

### Q8. 什么是Spring Bean？

Spring Bean是由Spring IoC容器初始化的Java对象。

### Q9.Spring框架中的默认 Bean 作用域是什么？

默认情况下，Spring Bean被初始化为singleton。

### Q10。Bean的作用域如何定义？

为了设置Spring Bean的作用域，我们可以在XML配置文件中使用@Scope注解或“scope”属性。请注意，有五个受支持的范围：

-   单例
-   原型
-   要求
-   会议
-   全局会话

有关差异，请查看[此处](https://docs.spring.io/spring/docs/3.0.0.M4/reference/html/ch03s05.html)。

### Q11. Singleton Bean 是线程安全的吗？

不，单例 bean 不是线程安全的，因为线程安全是关于执行的，而单例是一种专注于创建的设计模式。线程安全仅取决于 bean 实现本身。

### Q12.Spring Bean的生命周期是什么样的？

首先，需要基于Java或XMLbean 定义来实例化Spring bean。可能还需要执行一些初始化以使其进入可用状态。之后，当不再需要该 bean 时，它将从 IoC 容器中删除。

带有所有初始化方法的整个循环如图所示([来源](http://www.dineshonjava.com/2012/07/bean-lifecycle-and-callbacks.html))：

[![Spring Bean生命周期](https://www.baeldung.com/wp-content/uploads/2017/06/Spring-Bean-Life-Cycle.jpg)](https://www.baeldung.com/wp-content/uploads/2017/06/Spring-Bean-Life-Cycle.jpg)

### Q13. 什么是Spring基于Java的配置？

它是以类型安全的方式配置基于Spring的应用程序的方法之一。它是基于XML的配置的替代方法。

此外，要将项目从XML迁移到Java配置，请参阅[本文](https://www.baeldung.com/spring-xml-vs-java-config)。

### Q14. 一个项目可以有多个Spring配置文件吗？

是的，在大型项目中，建议使用多个Spring配置以增加可维护性和模块化。

我们可以加载多个基于Java的配置文件：

```java
@Configuration
@Import({MainConfig.class, SchedulerConfig.class})
public class AppConfig {
```

或者我们可以加载一个包含所有其他配置的XML文件：

```java
ApplicationContext context = new ClassPathXmlApplicationContext("spring-all.xml");
```

在这个XML文件中，我们将包含以下内容：

```xml
<import resource="main.xml"/>
<import resource="scheduler.xml"/>
```

### Q15. 什么是Spring安全？

Spring Security是Spring框架的一个独立模块，专注于在Java应用程序中提供身份验证和授权方法。它还处理大多数常见的安全漏洞，例如 CSRF 攻击。

要在 Web 应用程序中使用Spring Security，我们可以从简单的注解@EnableWebSecurity开始。

有关更多信息，我们有一系列与[安全](https://www.baeldung.com/security-spring)相关的文章。

### Q16. 什么是Spring靴？

Spring Boot是一个提供一组预配置框架以减少样板配置的项目。这样，我们就可以用最少的代码启动并运行Spring应用程序。

### Q17. 说出Spring框架中使用的一些设计模式？

-   单例模式 ——单例作用域的bean
-   工厂模式 ——Bean工厂类
-   原型模式——原型范围的bean
-   适配器模式 ——Spring Web 和Spring MVC
-   代理模式–Spring面向方面的编程支持
-   模板方法模式 ——JdbcTemplate 、HibernateTemplate等。
-   前端控制器 ——Spring MVCDispatcherServlet
-   数据访问对象 ——Spring DAO 支持
-   模型视图控制器——Spring MVC

### Q18. 作用域原型是如何工作的？

Scope原型意味着每次我们调用 Bean 的实例时，Spring 都会创建一个新实例并返回它。这与默认的单例作用域不同，在默认单例作用域中，每个Spring IoC容器实例化一次单个对象实例。

## 3. 春季网络MVC

### Q19. 如何在Spring Bean中获取ServletContext和ServletConfig对象？

我们可以通过实现Spring感知接口来实现。完整列表可[在此处](http://www.buggybread.com/2015/03/spring-framework-list-of-aware.html)获得。

我们还可以在这些 bean 上使用@Autowired注解：

```java
@Autowired
ServletContext servletContext;

@Autowired
ServletConfig servletConfig;
```

### Q20。什么是Spring MVC中的控制器？

简单地说， DispatcherServlet处理的所有请求都指向用@Controller注解的类。每个控制器类将一个或多个请求映射到使用提供的输入处理和执行请求的方法。

退后一步，我们建议看一下[典型Spring MVC架构中的前端控制器](https://www.baeldung.com/spring-controllers)的概念。

### Q21. @RequestMapping注解是如何工作的？

@RequestMapping注解用于将 Web 请求映射到SpringController 方法。除了简单的用例之外，我们还可以将其用于HTTP标头的映射、使用@PathVariable 绑定部分URI，以及使用URI参数和@RequestParam注解。

有关@RequestMapping的更多详细信息，请参见[此处](https://www.baeldung.com/spring-requestmapping)。

有关更多Spring MVC问题，请查看我们关于[Spring MVC面试问题](https://www.baeldung.com/spring-mvc-interview-questions)的文章。

## 4. 春季数据访问

### Q22. 什么是SpringJdbcTemplate类以及如何使用它？

Spring JDBC 模板是主要的 API，通过它我们可以访问我们感兴趣的数据库操作逻辑：

-   连接的创建和关闭
-   执行语句和存储过程调用
-   遍历ResultSet并返回结果

为了使用它，我们需要定义DataSource的简单配置：

```java
@Configuration
@ComponentScan("org.baeldung.jdbc")
public class SpringJdbcConfig {
    @Bean
    public DataSource mysqlDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/springjdbc");
        dataSource.setUsername("guest_user");
        dataSource.setPassword("guest_password");
 
        return dataSource;
    }
}
```

如需进一步说明，请查看[这篇快速文章](https://www.baeldung.com/spring-jdbc-jdbctemplate)。

### Q23. 如何在Spring中启用事务以及它们的好处是什么？

有两种不同的方法来配置事务——使用注解或使用面向方面的编程 (AOP)——每种方法各有优势。

[根据官方文档](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/transaction.html)，以下是使用SpringTransactions 的好处：

-   提供跨不同事务 API(如 JTA、JDBC、Hibernate、JPA 和 JDO)的一致编程模型
-   支持声明式事务管理
-   为程序化事务管理提供比一些复杂的事务 API(如 JTA)更简单的 API
-   与Spring的各种数据访问抽象很好地集成

### Q24. 什么是SpringDAO？

Spring 数据访问对象 (DAO) 是Spring提供的支持，用于以一致且简单的方式使用 JDBC、Hibernate 和 JPA 等数据访问技术。

有一个[完整的系列](https://www.baeldung.com/persistence-with-spring-series)讨论Spring中的持久性，提供了更深入的了解。

## 5.Spring面向切面编程

### Q25. 什么是面向方面的编程 (AOP)？

方面通过向现有代码添加额外行为而不修改受影响的类来实现横切关注点的模块化，例如跨越多种类型和对象的事务管理。

[这是基于方面的执行时间日志记录](https://www.baeldung.com/spring-aop-annotation)的示例。

### Q26. AOP中的Aspect、Advice、Pointcut和JoinPoint是什么？

-   Aspect—— 实现横切关注点的类，比如事务管理
-   建议——在应用程序中到达具有匹配切入点的特定JoinPoint 时执行的方法
-   Pointcut – 一组与JoinPoint 匹配的正则表达式，用于判断是否需要执行Advice
-   JoinPoint—— 程序执行过程中的一个点，例如方法的执行或异常的处理

### Q27. 什么是编织？

根据[官方文档](https://docs.spring.io/spring/docs/current/spring-framework-reference/html/aop.html)，编织是一个将方面与其他应用程序类型或对象链接起来以创建建议对象的过程。这可以在编译时、加载时或运行时完成。Spring AOP与其他纯JavaAOP 框架一样，在运行时执行织入。

## 6. 春天 5

### Q28. 什么是响应式编程？

响应式编程是关于非阻塞、事件驱动的应用程序，可通过少量线程进行扩展，背压是旨在确保生产者不会压倒消费者的关键因素。

这些是响应式编程的主要好处：

-   提高多核和多 CPU 硬件上计算资源的利用率
-   通过减少序列化提高性能

响应式编程通常是事件驱动的，而响应式系统是消息驱动的。所以，使用响应式编程并不意味着我们正在构建一个响应式系统，这是一种架构风格。

[但是，如果我们遵循Reactive Manifesto](https://www.reactivemanifesto.org/)，那么响应式编程可能会被用作实现响应式系统的一种手段 ，这对于理解是非常重要的。

基于此，响应式系统具有四个重要特征：

-   响应- 系统应及时响应。
-   弹性——万一系统面临任何故障，它应该保持响应。
-   弹性——响应式系统可以对变化做出响应，并在不同的工作负载下保持响应。
-   消息驱动——响应式系统需要依靠异步消息传递在组件之间建立边界。

### Q29. 什么是Spring WebFlux？

[Spring WebFlux](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#webflux) 是Spring的响应式堆栈 Web 框架，它是Spring MVC的替代品。

为了实现这种响应式模型并具有高度可扩展性，整个堆栈是非阻塞的。查看我们[关于Spring5 WebFlux 的教程](https://www.baeldung.com/spring-webflux)以了解更多详细信息。

### Q30。什么是 Mono 和 Flux 类型？

Spring Framework 5 中的 WebFlux 框架使用[Reactor](https://projectreactor.io/)作为其异步基础。

该项目提供两种核心类型： Mono表示单个异步值，Flux表示异步值流。它们都还实现了[Reactive Streams](http://www.reactive-streams.org/)规范中定义的 Publisher接口 。

Mono实现Publisher并返回 0 或 1 个元素：

```java
public abstract class Mono<T> implements Publisher<T> {...}
```

而 Flux实现Publisher并返回N 个元素：

```java
public abstract class Flux<T> implements Publisher<T> {...}
```

根据定义，这两种类型代表流，因此它们都是惰性的。这意味着在我们使用subscribe()方法使用流之前不会执行任何操作。[这两种类型也是不可变的，因此调用任何方法都将返回Flux或Mono](https://www.baeldung.com/reactor-core#streams)的新实例。

### Q31. WebClient和WebTestClient有什么用？

[WebClient](https://www.baeldung.com/spring-5-webclient)是新的 Web Reactive 框架中的一个组件，可以充当响应式客户端来执行非阻塞HTTP请求。由于它是响应式客户端，它可以处理带有背压的响应流，并且可以充分利用Java 8lambda。它还可以处理同步和异步场景。

另一方面， WebTestClient 是我们可以在测试中使用的类似类。基本上，它是 WebClient 周围的薄壳。它可以通过HTTP连接连接到任何服务器。它还可以使用模拟请求和响应对象直接绑定到 WebFlux 应用程序，而不需要HTTP服务器。

### Q32. 使用响应流的缺点是什么？

使用响应流有一些主要缺点：

-   对 Reactive 应用程序进行故障排除有点困难，因此请务必查看我们[关于调试 Reactive 流的教程](https://www.baeldung.com/spring-debugging-reactive-streams)，以获取一些方便的调试技巧。
-   由于传统的关系数据存储尚未接受响应式范例，因此对响应式数据存储的支持有限。
-   实施时有额外的学习曲线。

### Q33.Spring5 与旧版本的Java兼容吗？

为了利用Java 8的特性，对Spring代码库进行了改进。这意味着不能使用旧版本的Java。因此，该框架至少需要Java 8。

### Q34.Spring5如何与JDK 9模块化集成？

在Spring5 中，一切都已模块化。这样，我们就不会被迫导入可能没有我们正在寻找的功能的罐子。

请查看我们的 [Java 9 模块化指南，](https://www.baeldung.com/java-9-modularity) 以深入了解该技术的工作原理。

让我们看一个示例来了解Java 9中的新模块功能以及如何基于此概念组织Spring5 项目。

我们将首先创建一个新类，其中包含一个返回字符串“ HelloWorld”的方法。我们将把它放在一个新的Java项目中 — HelloWorldModule：

```java
package com.hello;
public class HelloWorld {
    public String sayHello(){
        return "HelloWorld";
    }
}
```

然后我们创建一个新模块：

```java
module com.hello {
    export com.hello;
}
```

现在让我们创建一个新的Java项目HelloWorldClient，通过定义一个模块来使用上面的模块：

```java
module com.hello.client {
    requires com.hello;
}
```

上述模块现在可用于测试：

```java
public class HelloWorldClient {
    public static void main(String[] args){
        HelloWorld helloWorld = new HelloWorld();
        log.info(helloWorld.sayHello());
    }
}
```

### Q35. 我们可以在同一个应用程序中同时使用 Web MVC 和 WebFlux 吗？

截至目前，Spring Boot将只允许Spring MVC或Spring WebFlux，因为Spring Boot会尝试根据其类路径中存在的依赖项自动配置上下文。

另外，Spring MVC不能在Netty上运行。此外，MVC 是一种阻塞范式，而 WebFlux 是一种非阻塞样式。因此，我们不应该将两者混合在一起，因为它们有不同的用途。

## 七、总结

在这篇内容广泛的文章中，我们探讨了一些关于Spring的技术面试最重要的问题。

我们希望这篇文章对即将到来的春季面试有所帮助。祝你好运！
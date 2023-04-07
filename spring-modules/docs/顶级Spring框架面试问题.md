## **一、概述**

在本教程中，我们将探讨在求职面试中可能会出现的一些最常见的 Spring 相关问题。

## 延伸阅读：

## [Java面试题](https://www.baeldung.com/java-interview-questions)

了解常见 Java 面试问题的答案

[阅读更多](https://www.baeldung.com/java-interview-questions)→

## [Java 8 面试问题（+答案）](https://www.baeldung.com/java-8-interview-questions)

一组流行的 Java8 相关面试问题，当然还有答案。

[阅读更多](https://www.baeldung.com/java-8-interview-questions)→

## [Java 集合面试题](https://www.baeldung.com/java-collections-interview-questions)

一组实用的Collections相关的Java面试题

[阅读更多](https://www.baeldung.com/java-collections-interview-questions)→

## 2.弹簧芯

### **Q1。什么是 Spring 框架？**

Spring 是用于开发 Java 企业版应用程序的最广泛使用的框架。此外，Spring 的核心特性可用于开发任何 Java 应用程序。

我们使用它的扩展在 Jakarta EE 平台之上构建各种 Web 应用程序。我们也可以在简单的独立应用程序中使用它的依赖注入条款。

### **Q2。使用 Spring 有什么好处？**

Spring 的目标是让 Jakarta EE 的开发更容易，所以让我们看看它的优势：

-   **轻量级**——在开发中使用框架的开销很小。
-   **控制反转 (IoC)** – Spring 容器负责连接各种对象的依赖关系，而不是创建或查找依赖对象。
-   **面向方面的编程 (AOP)** – Spring 支持 AOP 以将业务逻辑与系统服务分离。
-   **IoC 容器**——管理 Spring Bean 生命周期和项目特定的配置
-   **MVC 框架** ——用于创建 Web 应用程序或 RESTful Web 服务，能够返回 XML/JSON 响应
-   **事务管理**——通过使用 Java 注解或 Spring Bean XML 配置文件，减少 JDBC 操作、文件上传等样板代码量
-   **异常处理**——Spring 提供了一个方便的 API，用于将特定于技术的异常转换为未经检查的异常。

### **Q3. 你知道哪些 Spring 子项目？简要描述它们。**

-   **核心**——提供框架基础部分的关键模块，例如 IoC 或 DI
-   **JDBC** – 启用 JDBC 抽象层，无需为特定供应商数据库进行 JDBC 编码
-   **ORM 集成**——为流行的对象关系映射 API 提供集成层，例如 JPA、JDO 和 Hibernate
-   **Web——**一个面向 web 的集成模块，提供多部分文件上传、Servlet 侦听器和面向 web 的应用程序上下文功能
-   **MVC 框架**——一个实现模型视图控制器设计模式的网络模块
-   **AOP 模块**——面向方面的编程实现，允许定义干净的方法拦截器和切入点

### **Q4. 什么是依赖注入？**

依赖注入是控制反转 (IoC) 的一个方面，是一个通用概念，说明我们不手动创建对象，而是描述应如何创建对象。然后 IoC 容器将在需要时实例化所需的类。

更多详情，请看[这里](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring)。

### **Q5. 我们如何在 Spring 中注入 Bean？**

为了注入 Spring bean，存在一些不同的选项：

-   二传手注射
-   构造函数注入
-   现场注入

可以使用 XML 文件或注释来完成配置。

有关更多详细信息，请查看[这篇文章](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring)。

### **Q6. 哪种是注入 Bean 的最佳方式？为什么？**

推荐的方法是对强制依赖项使用构造函数参数，对可选依赖项使用设置器。这是因为构造函数注入允许将值注入不可变字段并使测试更容易。

### **Q7. BeanFactory 和 ApplicationContext 有什么区别？**

*BeanFactory*是表示提供和管理 bean 实例的容器的接口。默认实现在调用*getBean()时延迟实例化 beans。*

相反，*ApplicationContext*是一个接口，表示一个容器，其中包含应用程序中的所有信息、元数据和 bean。它还扩展了*BeanFactory*接口，但默认实现会在应用程序启动时立即实例化 beans。但是，可以为单个 bean 覆盖此行为。

对于所有差异，请参阅[文档](https://docs.spring.io/spring/docs/current/spring-framework-reference/html/beans.html)。

### **Q8. 什么是 Spring Bean？**

Spring Bean 是由 Spring IoC 容器初始化的 Java 对象。

### **Q9. Spring 框架中的默认 Bean 作用域是什么？**

默认情况下，Spring Bean 被初始化为*singleton*。

### **Q10。Bean的作用域如何定义？**

为了设置Spring Bean的作用域，我们可以在XML配置文件中使用*@Scope注解或“scope”属性。*请注意，有五个受支持的范围：

-   **单例**
-   **原型**
-   **要求**
-   **会议**
-   **全局会话**

有关差异，请查看[此处](https://docs.spring.io/spring/docs/3.0.0.M4/reference/html/ch03s05.html)。

### **Q11. Singleton Bean 是线程安全的吗？**

不，单例 bean 不是线程安全的，因为线程安全是关于执行的，而单例是一种专注于创建的设计模式。线程安全仅取决于 bean 实现本身。

### **Q12. Spring Bean 的生命周期是什么样的？**

首先，需要基于 Java 或 XML bean 定义来实例化 Spring bean。可能还需要执行一些初始化以使其进入可用状态。之后，当不再需要该 bean 时，它将从 IoC 容器中删除。

带有所有初始化方法的整个循环如图所示（[来源](http://www.dineshonjava.com/2012/07/bean-lifecycle-and-callbacks.html)）：

[![Spring Bean 生命周期](https://www.baeldung.com/wp-content/uploads/2017/06/Spring-Bean-Life-Cycle.jpg)](https://www.baeldung.com/wp-content/uploads/2017/06/Spring-Bean-Life-Cycle.jpg)

### **Q13. 什么是Spring基于Java的配置？**

它是以类型安全的方式配置基于 Spring 的应用程序的方法之一。它是基于 XML 的配置的替代方法。

此外，要将项目从 XML 迁移到 Java 配置，请参阅[本文](https://www.baeldung.com/spring-xml-vs-java-config)。

### **Q14. 一个项目可以有多个Spring配置文件吗？**

是的，在大型项目中，建议使用多个 Spring 配置以增加可维护性和模块化。

我们可以加载多个基于 Java 的配置文件：

```java
@Configuration
@Import({MainConfig.class, SchedulerConfig.class})
public class AppConfig {复制
```

或者我们可以加载一个包含所有其他配置的 XML 文件：

```java
ApplicationContext context = new ClassPathXmlApplicationContext("spring-all.xml");复制
```

在这个 XML 文件中，我们将包含以下内容：

```xml
<import resource="main.xml"/>
<import resource="scheduler.xml"/>复制
```

### **Q15. 什么是弹簧安全？**

Spring Security 是 Spring 框架的一个独立模块，专注于在 Java 应用程序中提供身份验证和授权方法。它还处理大多数常见的安全漏洞，例如 CSRF 攻击。

*要在 Web 应用程序中使用 Spring Security，我们可以从简单的注解@EnableWebSecurity*开始。

有关更多信息，我们有一系列与[安全](https://www.baeldung.com/security-spring)相关的文章。

### **Q16. 什么是弹簧靴？**

Spring Boot 是一个提供一组预配置框架以减少样板配置的项目。这样，我们就可以用最少的代码启动并运行 Spring 应用程序。

### **Q17. 说出 Spring 框架中使用的一些设计模式？**

-   **单例模式** ——单例作用域的bean
-   **工厂模式** ——Bean工厂类
-   **原型模式**——原型范围的bean
-   **适配器模式** ——Spring Web 和 Spring MVC
-   **代理模式**– Spring 面向方面的编程支持
-   **模板方法模式** ——JdbcTemplate *、**HibernateTemplate*等。
-   **前端控制器** ——Spring MVC *DispatcherServlet*
-   **数据访问对象** ——Spring DAO 支持
-   **模型视图控制器**——Spring MVC

### **Q18. 作用域原型是如何工作的？**

Scope*原型*意味着每次我们调用 Bean 的实例时，Spring 都会创建一个新实例并返回它。*这与默认的单例*作用域不同，在默认单例作用域中，每个 Spring IoC 容器实例化一次单个对象实例。

## **3. 春季网络MVC**

### **Q19. 如何在 Spring Bean 中获取\*ServletContext\*和\*ServletConfig对象？\***

我们可以通过实现 Spring 感知接口来实现。完整列表可[在此处](http://www.buggybread.com/2015/03/spring-framework-list-of-aware.html)获得。

我们还可以在这些 bean 上使用*@Autowired注解：*

```java
@Autowired
ServletContext servletContext;

@Autowired
ServletConfig servletConfig;复制
```

### **Q20。什么是 Spring MVC 中的控制器？**

*简单地说， DispatcherServlet*处理的所有请求都指向用*@Controller*注释的类。每个控制器类将一个或多个请求映射到使用提供的输入处理和执行请求的方法。

退后一步，我们建议看一下[典型 Spring MVC 架构中的前端控制器](https://www.baeldung.com/spring-controllers)的概念。

### **Q21. \*@RequestMapping\*注解是如何工作的？**

@RequestMapping注释用于将 Web 请求映射到 Spring Controller 方法*。*除了简单的用例之外，我们还可以将其用于 HTTP 标头的映射、使用*@PathVariable 绑定部分 URI，*以及使用 URI 参数和*@RequestParam*注释。

*有关@RequestMapping*的更多详细信息，请参见[此处](https://www.baeldung.com/spring-requestmapping)。

**有关更多 Spring MVC 问题，请查看我们关于[Spring MVC 面试问题](https://www.baeldung.com/spring-mvc-interview-questions)**的文章。

## **4. 春季数据访问**

### **Q22. 什么是 Spring \*JdbcTemplate\*类以及如何使用它？**

Spring JDBC 模板是主要的 API，通过它我们可以访问我们感兴趣的数据库操作逻辑：

-   连接的创建和关闭
-   执行语句和存储过程调用
-   遍历*ResultSet*并返回结果

为了使用它，我们需要定义*DataSource*的简单配置：

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
}复制
```

如需进一步说明，请查看[这篇快速文章](https://www.baeldung.com/spring-jdbc-jdbctemplate)。

### **Q23. 如何在 Spring 中启用事务以及它们的好处是什么？**

有两种不同的方法来配置*事务*——使用注释或使用面向方面的编程 (AOP)——每种方法各有优势。

[根据官方文档](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/transaction.html)，以下是使用 Spring Transactions 的好处：

-   提供跨不同事务 API（如 JTA、JDBC、Hibernate、JPA 和 JDO）的一致编程模型
-   支持声明式事务管理
-   为程序化事务管理提供比一些复杂的事务 API（如 JTA）更简单的 API
-   与 Spring 的各种数据访问抽象很好地集成

### **Q24. 什么是 Spring DAO？**

Spring 数据访问对象 (DAO) 是 Spring 提供的支持，用于以一致且简单的方式使用 JDBC、Hibernate 和 JPA 等数据访问技术。

有一个[完整的系列](https://www.baeldung.com/persistence-with-spring-series)讨论 Spring 中的持久性，提供了更深入的了解。

## **5. Spring 面向切面编程**

### **Q25. 什么是面向方面的编程 (AOP)？**

*方面*通过向现有代码添加额外行为而不修改受影响的类来实现横切关注点的模块化，例如跨越多种类型和对象的事务管理。

[这是基于方面的执行时间日志记录](https://www.baeldung.com/spring-aop-annotation)的示例。

### **Q26. AOP中的Aspect、Advice、Pointcut和JoinPoint是什么？**

-   ***Aspect——*** 实现横切关注点的类，比如事务管理
-   ***建议\***——在应用程序中到达具有匹配*切入点的特定**JoinPoint* 时执行的方法
-   ***Pointcut*** *– 一组与JoinPoint* 匹配的正则表达式，用于判断是否需要执行*Advice*
-   ***JoinPoint——*** 程序执行过程中的一个点，例如方法的执行或异常的处理

### **Q27. 什么是编织？**

根据[官方文档](https://docs.spring.io/spring/docs/current/spring-framework-reference/html/aop.html)，*编织*是一个将方面与其他应用程序类型或对象链接起来以创建建议对象的过程。这可以在编译时、加载时或运行时完成。Spring AOP 与其他纯 Java AOP 框架一样，在运行时执行*织入*。

## **6. 春天 5**

### **Q28. 什么是反应式编程？**

响应式编程是关于非阻塞、事件驱动的应用程序，可通过少量线程进行扩展，背压是旨在确保生产者不会压倒消费者的关键因素。

这些是反应式编程的主要好处：

-   提高多核和多 CPU 硬件上计算资源的利用率
-   通过减少序列化提高性能

响应式编程通常是事件驱动的，而响应式系统是消息驱动的。所以，使用响应式编程并不意味着我们正在构建一个响应式系统，这是一种架构风格。

[但是，如果我们遵循Reactive Manifesto](https://www.reactivemanifesto.org/)，那么反应式编程可能会被用作实现反应式系统的一种手段 ，这对于理解是非常重要的。

基于此，反应式系统具有四个重要特征：

-   **响应**- 系统应及时响应。
-   **弹性**——万一系统面临任何故障，它应该保持响应。
-   **弹性**——反应式系统可以对变化做出反应，并在不同的工作负载下保持响应。
-   **消息驱动**——反应式系统需要依靠异步消息传递在组件之间建立边界。

### **Q29. 什么是 Spring WebFlux？**

[Spring WebFlux](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#webflux) 是 Spring 的反应式堆栈 Web 框架，它是 Spring MVC 的替代品。

为了实现这种反应式模型并具有高度可扩展性，整个堆栈是非阻塞的。查看我们[关于 Spring 5 WebFlux 的教程](https://www.baeldung.com/spring-webflux)以了解更多详细信息。

### **Q30。什么是 Mono 和 Flux 类型？**

Spring Framework 5 中的 WebFlux 框架使用[Reactor](https://projectreactor.io/)作为其异步基础。

该项目提供两种核心类型： *Mono*表示单个异步值，*Flux*表示异步值流。它们都还实现了[Reactive Streams](http://www.reactive-streams.org/)规范中定义的 *Publisher*接口 。

*Mono*实现*Publisher*并返回 0 或 1 个元素：

```java
public abstract class Mono<T> implements Publisher<T> {...}复制
```

而 *Flux*实现*Publisher*并返回*N 个*元素：

```java
public abstract class Flux<T> implements Publisher<T> {...}复制
```

根据定义，这两种类型代表流，因此它们都是惰性的。*这意味着在我们使用subscribe()*方法使用流之前不会执行任何操作。[*这两种类型也是不可变的，因此调用任何方法都将返回Flux*或*Mono*](https://www.baeldung.com/reactor-core#streams)的新实例。

### **Q31. \*WebClient\*和\*WebTestClient\*有什么用？**

*[WebClient](https://www.baeldung.com/spring-5-webclient)*是新的 Web Reactive 框架中的一个组件，可以充当响应式客户端来执行非阻塞 HTTP 请求。由于它是反应式客户端，它可以处理带有背压的反应流，并且可以充分利用 Java 8 lambda。它还可以处理同步和异步场景。

另一方面， *WebTestClient* 是我们可以在测试中使用的类似类。基本上，它是 WebClient 周围的薄壳*。*它可以通过 HTTP 连接连接到任何服务器。它还可以使用模拟请求和响应对象直接绑定到 WebFlux 应用程序，而不需要 HTTP 服务器。

### **Q32. 使用反应流的缺点是什么？**

使用反应流有一些主要缺点：

-   对 Reactive 应用程序进行故障排除有点困难，因此请务必查看我们[关于调试 Reactive 流的教程](https://www.baeldung.com/spring-debugging-reactive-streams)，以获取一些方便的调试技巧。
-   由于传统的关系数据存储尚未接受反应式范例，因此对反应式数据存储的支持有限。
-   实施时有额外的学习曲线。

### **Q33. Spring 5 与旧版本的 Java 兼容吗？**

为了利用 Java 8 的特性，对 Spring 代码库进行了改进。这意味着不能使用旧版本的 Java。因此，该框架至少需要 Java 8。

### **Q34. Spring 5如何与JDK 9模块化集成？**

在 Spring 5 中，一切都已模块化。这样，我们就不会被迫导入可能没有我们正在寻找的功能的罐子。

请查看我们的 [Java 9 模块化指南，](https://www.baeldung.com/java-9-modularity) 以深入了解该技术的工作原理。

让我们看一个示例来了解 Java 9 中的新模块功能以及如何基于此概念组织 Spring 5 项目。

我们将首先创建一个新类，其中包含一个返回字符串*“* HelloWorld”的方法。我们将把它放在一个新的 Java 项目中 — *HelloWorldModule*：

```java
package com.hello;
public class HelloWorld {
    public String sayHello(){
        return "HelloWorld";
    }
}复制
```

然后我们创建一个新模块：

```java
module com.hello {
    export com.hello;
}复制
```

现在让我们创建一个新的 Java 项目*HelloWorldClient*，通过定义一个模块来使用上面的模块：

```java
module com.hello.client {
    requires com.hello;
}复制
```

上述模块现在可用于测试：

```java
public class HelloWorldClient {
    public static void main(String[] args){
        HelloWorld helloWorld = new HelloWorld();
        log.info(helloWorld.sayHello());
    }
}复制
```

### **Q35. 我们可以在同一个应用程序中同时使用 Web MVC 和 WebFlux 吗？**

截至目前，Spring Boot 将只允许 Spring MVC 或 Spring WebFlux，因为 Spring Boot 会尝试根据其类路径中存在的依赖项自动配置上下文。

另外，Spring MVC 不能在 Netty 上运行。此外，MVC 是一种阻塞范式，而 WebFlux 是一种非阻塞样式。因此，我们不应该将两者混合在一起，因为它们有不同的用途。

## **七、结论**

在这篇内容广泛的文章中，我们探讨了一些关于 Spring 的技术面试最重要的问题。

我们希望这篇文章对即将到来的春季面试有所帮助。祝你好运！
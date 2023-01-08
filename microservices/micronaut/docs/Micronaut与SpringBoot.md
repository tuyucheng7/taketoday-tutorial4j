## 1. 概述

在本教程中，我们将比较[Micronaut](https://micronaut.io/)和[Spring Boot](https://spring.io/projects/spring-boot)。[Spring Boot](https://www.baeldung.com/spring-boot)是流行的Spring框架的一部分，用于快速启动和运行Spring应用程序。[Micronaut](https://www.baeldung.com/micronaut)是一个基于JVM的框架，旨在解决Spring/Spring Boot的一些弱点。

我们将在几个方面比较这两个框架。首先，我们将比较创建新应用程序的难易程度、语言支持和其他配置选项。然后我们将查看两个简单的REST应用程序。最后，我们将比较代码并衡量性能差异。

## 2. 特性

在接下来的部分中，我们将分解这两个框架中的几个特性。

### 2.1 设置

首先，我们将比较在两个框架中启动和运行新应用程序的难易程度。

Micronaut和Spring Boot都提供了多种方便的方法来创建新的应用程序。例如，我们可以使用带有命令行界面的任一框架创建一个新应用程序。或者，我们可以为Spring Boot使用[Spring Initializr](https://start.spring.io/)或为Micronaut使用名为[Launch](https://micronaut.io/launch/)的类似工具。

在IDE支持方面，我们可以为大多数流行的IDE使用Spring Boot插件，包括它的Eclipse风格，即[Eclipse Spring Tools Suite](https://www.baeldung.com/eclipse-sts-spring)。如果我们使用IntelliJ，我们可以使用Micronaut插件。

### 2.2 语言支持

当我们转向语言支持时，我们会发现它对Spring Boot和Micronaut的支持几乎相同。对于这两个框架，我们可以在Java、[Groovy](https://www.baeldung.com/spring-boot-groovy-web-app)或[Kotlin](https://www.baeldung.com/kotlin/spring-boot-kotlin)之间进行选择。如果我们选择Java，这两个框架都支持Java 8、11和17。此外，我们可以将Gradle或Maven与这两个框架一起使用。

### 2.3 Servlet容器

使用Spring Boot，我们的应用程序将默认使用Tomcat。但是，我们也可以[将Spring Boot配置为使用Jetty或Undertow](https://www.baeldung.com/spring-boot-servlet-containers)。

默认情况下，我们的Micronaut应用程序将在基于Netty的HTTP服务器上运行。但是，我们可以选择将我们的应用程序切换为在Tomcat、Jetty或Undertow上运行。

### 2.4 属性配置

对于Spring Boot，我们可以在application.properties或application.yml中定义我们的[属性](https://www.baeldung.com/properties-with-spring)，我们可以使用application-{env}.properties约定为不同的环境提供不同的属性。此外，我们可以使用系统属性、环境变量或JNDI属性覆盖这些应用程序文件提供的属性。

我们可以为Micronaut中的属性文件使用application.properties、application.yml和application.json，我们还可以使用相同的约定来提供特定于环境的属性文件。如果我们需要覆盖任何属性，我们可以使用系统属性或环境变量。

### 2.5 消息系统支持

如果我们在Spring Boot中使用消息传递，我们可以使用[ActiveMQ](https://www.baeldung.com/spring-remoting-jms)、Artemis、[RabbitMQ](https://www.baeldung.com/spring-amqp)和[Apache Kafka](https://www.baeldung.com/spring-kafka)。

在Micronaut方面，我们有Apache Kafka、RabbitMQ和Nats.io作为选项。

### 2.6 安全

Spring Boot提供了五种授权策略：基本、表单登录、JWT、SAML和LDAP。如果我们使用的是Micronaut，除了SAML之外，我们还有相同的选项。

这两个框架都为我们提供了[OAuth2](https://www.baeldung.com/spring-security-oauth)支持。

在实际应用安全性方面，这两个框架都允许我们使用注解来保护方法。

### 2.7 管理和监控

这两个框架都为我们提供了监控应用程序中各种指标和统计数据的能力，我们可以在两个框架中定义自定义端点，我们还可以在两个框架中配置端点安全性。

但是，[Spring Boot Actuator](https://www.baeldung.com/spring-boot-actuators)提供了比Micronaut更多的内置端点。

### 2.8 模板语言

我们可以使用这两个框架创建完整的全栈应用程序，使用提供的模板语言来渲染前端。

对于Spring Boot，我们的选择是[Thymeleaf](https://www.baeldung.com/spring-boot-crud-thymeleaf)、[Apache Freemarker](https://www.baeldung.com/freemarker-in-spring-mvc-tutorial)、[Mustache](https://www.baeldung.com/spring-boot-mustache)和Groovy。我们也可以使用JSP，尽管不鼓励这种做法。

我们在Micronaut中提供了更多选项：Thymeleaf、Handlebars、Apache Velocity、Apache Freemarker、Rocker、Soy/Closure和Pebbles。

### 2.9 云支持

Spring Boot应用程序依赖第三方库来实现许多[特定于云](https://www.baeldung.com/spring-cloud-series)的功能。

Micronaut是为云微服务而设计的，Micronaut将原生为我们处理的云概念包括分布式配置、服务发现、客户端负载平衡、分布式跟踪和无服务器功能。

## 3. 守则

现在我们已经比较了两个框架中的一些基本特性，让我们创建并比较两个应用程序。为了简单起见，我们将创建一个简单的REST API来解决基本的算术问题。我们的服务层将包含一个实际为我们计算的类，我们的控制器类将包含一个用于加法、减法、乘法和除法的端点。

在我们深入研究代码之前，让我们考虑一下Spring Boot和Micronaut之间的显著差异。尽管这两个框架都提供依赖注入，但它们的处理方式不同。我们的Spring Boot应用程序使用反射和代理在运行时处理依赖注入。相比之下，我们的Micronaut应用程序在编译时会构建依赖注入数据。

### 3.1 Spring Boot应用程序

首先，让我们在我们的Spring Boot应用程序中定义一个名为ArithmeticService的类：

```java
@Service
public class ArithmeticService {
    public float add(float number1, float number2) {
        return number1 + number2;
    }

    public float subtract(float number1, float number2) {
        return number1 - number2;
    }

    public float multiply(float number1, float number2) {
        return number1 * number2;
    }

    public float divide(float number1, float number2) {
        if (number2 == 0) {
            throw new IllegalArgumentException("'number2' cannot be zero");
        }
        return number1 / number2;
    }
}
```

接下来，让我们创建我们的REST控制器：

```java
@RestController
@RequestMapping("/math")
public class ArithmeticController {
    @Autowired
    private ArithmeticService arithmeticService;

    @GetMapping("/sum/{number1}/{number2}")
    public float getSum(@PathVariable("number1") float number1, @PathVariable("number2") float number2) {
        return arithmeticService.add(number1, number2);
    }

    @GetMapping("/subtract/{number1}/{number2}")
    public float getDifference(@PathVariable("number1") float number1, @PathVariable("number2") float number2) {
        return arithmeticService.subtract(number1, number2);
    }

    @GetMapping("/multiply/{number1}/{number2}")
    public float getMultiplication(@PathVariable("number1") float number1, @PathVariable("number2") float number2) {
        return arithmeticService.multiply(number1, number2);
    }

    @GetMapping("/divide/{number1}/{number2}")
    public float getDivision(@PathVariable("number1") float number1, @PathVariable("number2") float number2) {
        return arithmeticService.divide(number1, number2);
    }
}
```

我们的控制器为四个算术函数中的每一个都有一个端点。

### 3.2 Micronaut应用程序

现在，让我们创建Micronaut应用程序的服务层：

```java
@Singleton
public class ArithmeticService {
    // implementation identical to the Spring Boot service layer
}
```

接下来，我们将使用与Spring Boot应用程序相同的四个端点编写REST控制器：

```java
@Controller("/math")
public class ArithmeticController {
    @Inject
    private ArithmeticService arithmeticService;

    @Get("/sum/{number1}/{number2}")
    public float getSum(float number1, float number2) {
        return arithmeticService.add(number1, number2);
    }

    @Get("/subtract/{number1}/{number2}")
    public float getDifference(float number1, float number2) {
        return arithmeticService.subtract(number1, number2);
    }

    @Get("/multiply/{number1}/{number2}")
    public float getMultiplication(float number1, float number2) {
        return arithmeticService.multiply(number1, number2);
    }

    @Get("/divide/{number1}/{number2}")
    public float getDivision(float number1, float number2) {
        return arithmeticService.divide(number1, number2);
    }
}
```

我们可以看到我们非常简单的示例应用程序之间有很多相似之处。就差异而言，我们看到Micronaut利用Java的注解进行注入，而Spring Boot有自己的注解。此外，我们的Micronaut REST端点不需要对传递给方法的路径变量进行任何特殊的注解标注。

### 3.3 基本性能比较

Micronaut宣传快速启动时间，所以让我们比较一下我们的两个应用程序。

首先，让我们启动Spring Boot应用程序，看看需要多长时间：

```bash
[main] INFO  c.t.t.m.v.s.CompareApplication - Started CompareApplication in 3.179 seconds (JVM running for 4.164)
```

接下来，让我们看看我们的Micronaut应用程序启动的速度有多快：

```bash
21:22:49.267 [main] INFO  io.micronaut.runtime.Micronaut - Startup completed in 1278ms. Server Running: http://localhost:57535
```

我们可以看到，我们的Spring Boot应用程序在3秒多一点的时间内启动，而在Micronaut中则只需要1秒多一点。

现在我们已经了解了启动时间，让我们稍微练习一下API，然后检查一些基本的内存统计信息。我们将在启动应用程序时使用默认内存设置。

我们将从Spring Boot应用程序开始，首先，让我们调用四个算术端点中的每一个，然后拉取我们的内存端点：

```bash
Initial: 0.25 GB 
Used: 0.02 GB 
Max: 4.00 GB 
Committed: 0.06 GB 
```

接下来，让我们对我们的Micronaut应用程序进行相同的过程：

```bash
Initial: 0.25 GB 
Used: 0.01 GB 
Max: 4.00 GB 
Committed: 0.03 GB
```

在这个有限的示例中，我们的两个应用程序使用的内存都很少，但Micronaut使用的内存大约是Spring Boot应用程序的一半。

## 4. 总结

在本文中，我们将Spring Boot与Micronaut进行了比较。首先，我们简要概述了这两个框架是什么。然后，我们浏览了几个功能并比较了选项。最后，我们将两个简单的示例应用程序相互比较。我们查看了这两个应用程序的代码，然后查看了启动时间和内存性能。
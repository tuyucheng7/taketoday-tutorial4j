## 1. 概述

在本文中，我们将学习如何使用 Spring Boot 3 配置可观察性。可观察性是仅通过其外部输出来衡量系统内部状态的能力。[（日志、指标和跟踪）我们可以在“分布式系统](https://www.baeldung.com/distributed-systems-observability)中的可观察性”中了解基础知识。此外，我们必须意识到 Spring Boot 2 和 3 之间存在重大变化。

我们只会捕获有关 Spring Boot 3 的详细信息。在从 Spring Boot 2 迁移的情况下，我们可以找到[详细说明](https://github.com/micrometer-metrics/micrometer/wiki/Migrating-to-new-1.10.0-Observation-API)。

## 2.千分尺观测API

[Micrometer](https://micrometer.io/)是一个提供供应商中立的应用程序指标外观的项目。它定义[了计量器、速率聚合、计数器、仪表和计时器等](https://micrometer.io/docs/concepts)概念，每个供应商都可以根据自己的概念和工具进行调整。一个核心部分是[Observation API](https://micrometer.io/docs/observation)，它允许对代码进行一次检测并具有多种好处。

它作为 Spring Framework 多个部分的依赖项包含在内，因此我们需要了解此 API 才能了解 Spring Boot 中的观察工作原理。我们可以用一个简单的例子来做到这一点。

### 2.1. 观察和观察登记处

来自[dictionary.com](https://www.dictionary.com/browse/observation)的观察是“为了某些科学或其他特殊目的而观察或记录事实或事件的行为或实例”。在我们的代码中，我们可以观察单个操作或完整的 HTTP 请求处理。在这些观察中，我们可以进行测量，为分布式跟踪创建跨度或只是注销其他信息。

要创建一个观察，我们需要一个ObservationRegistry。

```java
ObservationRegistry observationRegistry = ObservationRegistry.create();
Observation observation = Observation.createNotStarted("sample", observationRegistry);
```

Observations 的生命周期非常简单，如下图所示：

![观察状态图](https://www.baeldung.com/wp-content/uploads/2022/12/observation-statechart.png)

 

我们可以像这样使用Observation类型：

```java
observation.start();
try (Observation.Scope scope = observation.openScope()) {
    // ... the observed action
} catch (Exception e) {
    observation.error(e);
    // further exception handling
} finally {
    observation.stop();
}
```

或者干脆

```java
observation.observe(() -> {
    // ... the observed action
});
```

### 2.2. 观察处理器

数据收集代码作为ObservationHandler实现。此处理程序收到有关Observation的生命周期事件的通知，因此提供回调方法。可以这样实现一个只打印事件的简单处理程序：

```java
public class SimpleLoggingHandler implements ObservationHandler<Observation.Context> {

    private static final Logger log = LoggerFactory.getLogger(SimpleLoggingHandler.class);

    @Override
    public boolean supportsContext(Observation.Context context) {
        return true;
    }

    @Override
    public void onStart(Observation.Context context) {
        log.info("Starting");
    }

    @Override
    public void onScopeOpened(Observation.Context context) {
        log.info("Scope opened");
    }

    @Override
    public void onScopeClosed(Observation.Context context) {
        log.info("Scope closed");
    }

    @Override
    public void onStop(Observation.Context context) {
        log.info("Stopping");
    }

    @Override
    public void onError(Observation.Context context) {
        log.info("Error");
    }
}
```

然后我们在创建Observation之前在ObservationRegistry 注册ObservationHandler ：

```java
observationRegistry
  .observationConfig()
  .observationHandler(new SimpleLoggingHandler());
```

对于简单的日志记录，一个实现已经存在。例如，要简单地将事件记录到控制台，我们可以使用：

```java
observationRegistry
  .observationConfig()
  .observationHandler(new ObservationTextPublisher(System.out::println));
```

要使用定时器样本和计数器，我们可以这样配置：

```java
MeterRegistry meterRegistry = new SimpleMeterRegistry();
observationRegistry
  .observationConfig()
  .observationHandler(new DefaultMeterObservationHandler(meterRegistry));

// ... observe using Observation with name "sample"

// fetch maximum duration of the named observation
Optional<Double> maximumDuration = meterRegistry.getMeters().stream()
  .filter(m -> "sample".equals(m.getId().getName()))
  .flatMap(m -> StreamSupport.stream(m.measure().spliterator(), false))
  .filter(ms -> ms.getStatistic() == Statistic.MAX)
  .findFirst()
  .map(Measurement::getValue);

```

## 3. Spring集成

### 3.1. Actuator

[我们在具有Actuator](https://www.baeldung.com/spring-boot-actuators)依赖项的 Spring Boot 应用程序中获得了最佳集成：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

它包含一个ObservationAutoConfiguratio n，提供 ObservationRegistry 的可注入实例（如果它尚不存在）并配置ObservationHandlers以收集指标和跟踪。

例如，我们可以使用注册表在服务中创建自定义观察：

```java
@Service
public class GreetingService {

    private ObservationRegistry observationRegistry;

    // constructor

    public String sayHello() {
        return Observation
          .createNotStarted("greetingService", observationRegistry)
          .observe(this::sayHelloNoObs);
    }

    private String sayHelloNoObs() {
        return "Hello World!";
    }
}
```

此外，它在ObservationRegistry注册ObservationHandler bean。我们只需要提供beans：

```java
@Configuration
public class ObservationTextPublisherConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ObservationTextPublisherConfiguration.class);

    @Bean
    public ObservationHandler<Observation.Context> observationTextPublisher() {
        return new ObservationTextPublisher(log::info);
    }
}
```

### 3.2 Web

对于 MVC 和 WebFlux，有 Filter 可以用于 HTTP 服务器观察：

-   用于 Spring MVC 的org.springframework.web.filter.ServerHttpObservationFilter
-   用于 WebFlux 的org.springframework.web.filter.reactive.ServerHttpObservationFilter

当 Actuator 是我们应用程序的一部分时，这些过滤器已经注册并处于活动状态。如果没有，我们需要配置它们：

```java
@Configuration
public class ObservationFilterConfiguration {

    // if an ObservationRegistry is configured
    @ConditionalOnBean(ObservationRegistry.class)
    // if we do not use Actuator
    @ConditionalOnMissingBean(ServerHttpObservationFilter.class)
    @Bean
    public ServerHttpObservationFilter observationFilter(ObservationRegistry registry) {
        return new ServerHttpObservationFilter(registry);
    }
}
```

[我们可以在文档](https://github.com/spring-projects/spring-framework/wiki/What's-New-in-Spring-Framework-6.x#observability)中找到有关 Spring Web 中可观察性集成的更多详细信息。

### 3.3. 面向对象编程

Micrometer Observation API 还声明了一个带有基于 AspectJ 的方面实现的@Observed注释。为了完成这项工作，我们需要将 AOP 依赖项添加到我们的项目中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

然后，我们将方面实现注册为 Spring 管理的 bean：

```java
@Configuration
public class ObservedAspectConfiguration {

    @Bean
    public ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
        return new ObservedAspect(observationRegistry);
    }
}
```

现在，我们可以快速编写GreetingService，而不是在我们的代码中创建Observation：

```java
@Observed(name = "greetingService")
@Service
public class GreetingService {

    public String sayHello() {
        return "Hello World!";
    }
}

```

结合 Actuator，我们可以使用http://localhost:8080/actuator/metrics/greetingService读取预先配置的指标（在我们至少调用一次服务之后），并将得到如下结果：

```json
{
    "name": "greetingService",
    "baseUnit": "seconds",
    "measurements": [
        {
            "statistic": "COUNT",
            "value": 15
        },
        {
            "statistic": "TOTAL_TIME",
            "value": 0.0237577
        },
        {
            "statistic": "MAX",
            "value": 0.0035475
        }
    ],
    ...
}
```

## 4. 测试观察

Micrometer Observability API 提供了一个允许编写测试的模块。为此，我们需要添加以下依赖项：

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-observation-test</artifactId>
    <scope>test</scope>
</dependency>
```

micrometer-bom是 Spring Boot 托管依赖的一部分，所以我们不需要指定任何版本。

因为默认情况下整个可观察性自动配置对于测试是禁用的，所以我们需要使用@AutoConfigureObservability重新启用它，每当我们想要测试默认观察时。

### 4.1. 测试观察登记处

我们可以使用允许基于AssertJ的断言的 TestObservationRegistry。因此，我们必须用TestObservationRegistry实例替换已经在上下文中的ObservationRegistry 。

因此，例如，如果我们想测试对GreetingService的观察，我们可以使用这个测试设置：

```java
@ExtendWith(SpringExtension.class)
@ComponentScan(basePackageClasses = GreetingService.class)
@EnableAutoConfiguration
@Import(ObservedAspectConfiguration.class)
@AutoConfigureObservability
class GreetingServiceObservationTest {

    @Autowired
    GreetingService service;
    @Autowired
    TestObservationRegistry registry;

    @TestConfiguration
    static class ObservationTestConfiguration {

        @Bean
        TestObservationRegistry observationRegistry() {
            return TestObservationRegistry.create();
        }
    }

    // ...
}
```

我们还可以使用 JUnit 元注释来配置它：

```java
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({
  ObservedAspectConfiguration.class,
  EnableTestObservation.ObservationTestConfiguration.class
})
@AutoConfigureObservability
public @interface EnableTestObservation {

    @TestConfiguration
    class ObservationTestConfiguration {

        @Bean
        TestObservationRegistry observationRegistry() {
            return TestObservationRegistry.create();
        }
    }
}
```

然后，我们只需要将注释添加到我们的测试类：

```java
@ExtendWith(SpringExtension.class)
@ComponentScan(basePackageClasses = GreetingService.class)
@EnableAutoConfiguration
@EnableTestObservation
class GreetingServiceObservationTest {

    @Autowired
    GreetingService service;
    @Autowired
    TestObservationRegistry registry;

    // ...
}
```

然后，我们可以调用该服务并检查观察是否已完成：

```java
import static io.micrometer.observation.tck.TestObservationRegistryAssert.assertThat;

// ...

@Test
void testObservation() {
    // invoke service
    service.sayHello();
    assertThat(registry)
      .hasObservationWithNameEqualTo("greetingService")
      .that()
      .hasBeenStarted()
      .hasBeenStopped();
}
```

### 4.2. 观察处理器兼容性套件

为了测试我们的ObservationHandler实现，我们可以在测试中继承几个基类（所谓的Compatibility Kits ）：

-   NullContextObservationHandlerCompatibilityKit测试观察处理程序在空值参数的情况下是否正常工作。
-   AnyContextObservationHandlerCompatibilityKit测试观察处理程序在未指定测试上下文参数的情况下是否正常工作。这也包括NullContextObservationHandlerCompatibilityKit。
-   ConcreteContextObservationHandlerCompatibilityKit 测试观察处理程序在测试上下文的上下文类型的情况下是否正常工作。

实现很简单：

```java
public class SimpleLoggingHandlerTest
  extends AnyContextObservationHandlerCompatibilityKit {

    SimpleLoggingHandler handler = new SimpleLoggingHandler();

    @Override
    public ObservationHandler<Observation.Context> handler() {
        return handler;
    }
}

```

这将导致以下输出：

![使用兼容性套件的测试结果](https://www.baeldung.com/wp-content/uploads/2022/12/test-result.png)

## 5. 千分尺追踪

之前的 Spring Cloud Sleuth 项目已经迁移到 Micrometer，自 Spring Boot 3 以来，Micrometer Tracing 的核心是 Micrometer Tracing。我们可以在[文档](https://micrometer.io/docs/tracing)中找到 Micrometer Tracing 的定义：

>   Micrometer Tracing 为最流行的跟踪器库提供了一个简单的外观，让您可以在没有供应商锁定的情况下检测基于 JVM 的应用程序代码。它旨在为您的跟踪收集活动增加很少甚至没有开销，同时最大限度地提高跟踪工作的可移植性。

我们可以单独使用它，但它也通过提供ObservationHandler扩展与 Observation API 集成。

### 5.1. 集成到观察 API

要使用 Micrometer Tracing，我们需要在项目中添加以下依赖项——版本由 Spring Boot 管理：

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing</artifactId>
</dependency>
```

然后，我们需要一种[受支持](https://micrometer.io/docs/tracing#_supported_tracers)的跟踪器（目前[是 OpenZipkin Brave](https://github.com/openzipkin/brave)或[OpenTelemetry](https://opentelemetry.io/)）。然后，我们必须为特定于供应商的集成添加到 Micrometer Tracing 的依赖项：

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-brave</artifactId>
</dependency>
```

或者

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-otel</artifactId>
</dependency>
```

Spring Actuator 对两个跟踪器都有自动配置，即它注册特定于供应商的对象和 Micrometer Tracing ObservationHandler实现，将这些对象委托到应用程序上下文中。因此，不需要进一步的配置步骤。

### 5.2. 测试支持

出于测试目的，我们需要将以下依赖项添加到我们的项目中——版本由 Spring Boot 管理：

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-test</artifactId>
    <scope>test</scope>
</dependency>
```

然后，我们可以使用SimpleTracer类在测试期间收集和验证跟踪数据。为了让它工作，我们在应用程序上下文中用SimpleTracer替换了原始的、特定于供应商的Tracer 。我们还必须记住通过使用@AutoConfigureObservability来启用跟踪的自动配置。

因此，用于跟踪的最小测试配置可能如下所示：

```java
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@AutoConfigureObservability
public class GreetingServiceTracingTest {

    @TestConfiguration
    static class ObservationTestConfiguration {
        @Bean
        TestObservationRegistry observationRegistry() {
            return TestObservationRegistry.create();
        }
        @Bean
        SimpleTracer simpleTracer() {
            return new SimpleTracer();
        }
    }

    @Test
    void shouldTrace() {
        // test
    }
}
```

或者，在使用 JUnit 元注释的情况下：

```java
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@AutoConfigureObservability
@Import({
  ObservedAspectConfiguration.class,
  EnableTestObservation.ObservationTestConfiguration.class
})
public @interface EnableTestObservation {

    @TestConfiguration
    class ObservationTestConfiguration {

        @Bean
        TestObservationRegistry observationRegistry() {
            return TestObservationRegistry.create();
        }

        @Bean
        SimpleTracer simpleTracer() {
            return new SimpleTracer();
        }
    }
}
```

然后我们可以通过以下示例测试来测试我们的 GreetingService：

```java
import static io.micrometer.tracing.test.simple.TracerAssert.assertThat;

// ...

@Autowired
GreetingService service;
@Autowired
SimpleTracer tracer;

// ...

@Test
void testTracingForGreeting() {
    service.sayHello();
    assertThat(tracer)
      .onlySpan()
      .hasNameEqualTo("greeting-service#say-hello")
      .isEnded();
}
```

## 6. 总结

在本教程中，我们探讨了Micrometer Observation API以及与 Spring Boot 3 的集成。我们了解到Micrometer是一个用于独立于供应商的仪器的 API，而Micrometer Tracing是一个扩展。我们了解到Actuator有一组预配置的观察和跟踪，并且默认情况下禁用测试的可观察性自动配置。
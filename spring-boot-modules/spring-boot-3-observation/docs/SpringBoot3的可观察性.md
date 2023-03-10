## 1. 概述

在本文中，我们将学习如何使用Spring Boot 3配置可观察性。可观察性是仅通过其外部输出来衡量系统内部状态的能力(日志、指标和跟踪)。我们可以在[“分布式系统中的可观察性“](https://www.baeldung.com/distributed-systems-observability)中了解基础知识。

此外，我们必须意识到Spring Boot 2(Spring 5)和Spring Boot 3(Spring 6)之间存在重大变化。Spring 6引入了Spring Observability-一项建立在Micrometer和Micrometer Tracing(以前称为Spring Cloud Sleuth)之上的新计划。它更适合使用Micrometer高效记录应用程序指标，并通过OpenZipkin的Brave或OpenTelemetry等提供程序实施跟踪。**Spring可观察性优于基于代理的可观察性解决方案**，因为它在本机编译的Spring应用程序中无缝工作，并且更有效地提供更好的信息。

我们只会讨论有关Spring Boot 3的详细信息。在从Spring Boot 2迁移的情况下，我们可以找到[详细说明](https://github.com/micrometer-metrics/micrometer/wiki/Migrating-to-new-1.10.0-Observation-API)。

## 2. Micrometer Observation API

[Micrometer](https://micrometer.io/)是一个提供供应商中立的应用程序指标外观的项目。它定义了[计量器、速率聚合、计数器、仪表和计时器等](https://micrometer.io/docs/concepts)概念，每个供应商都可以根据自己的概念和工具进行调整。一个核心部分是[Observation API](https://micrometer.io/docs/observation)，它允许对代码进行一次检测并具有多种好处。

它作为Spring Framework多个部分的依赖项包含在内，因此我们需要了解此API才能了解Spring Boot中观察性的工作原理。我们可以通过一个简单的例子来做到这一点。

### 2.1 Observation和ObservationRegistry

来自[dictionary.com](https://www.dictionary.com/browse/observation)的观察是“为了某些科学或其他特殊目的而观察或记录事实或事件的行为或实例”。在我们的代码中，我们可以观察单个操作或完整的HTTP请求处理。在这些观察中，我们可以进行测量，为分布式跟踪创建跨度或只是记录其他信息。

要创建一个观察，我们需要一个ObservationRegistry。

```java
ObservationRegistry observationRegistry = ObservationRegistry.create();
Observation observation = Observation.createNotStarted("sample", observationRegistry);
```

Observations的生命周期非常简单，如下图所示：

<img src="../assets/img.png">

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

### 2.2 ObservationHandler

数据收集代码作为ObservationHandler实现。此处理程序接收有关Observation的生命周期事件的通知，因此提供回调方法。可以这样实现一个只打印事件的简单处理程序：

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

然后我们在创建Observation之前在ObservationRegistry中注册ObservationHandler：

```java
observationRegistry
    .observationConfig()
    .observationHandler(new SimpleLoggingHandler());
```

对于简单的日志记录，已存在现有实现。例如，要简单地将事件记录到控制台，我们可以使用：

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

### 3.1 Actuator

我们在具有[Actuator](https://www.baeldung.com/spring-boot-actuators)依赖项的Spring Boot应用程序中获得了最佳集成：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

它包含一个ObservationAutoConfiguration，提供ObservationRegistry的可注入实例(如果它尚不存在)并配置ObservationHandlers以收集指标和跟踪。

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

此外，它在ObservationRegistry上注册ObservationHandler bean。我们只需要提供beans：

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

对于MVC和WebFlux，有一些Filter可以用于HTTP服务器观察：

-   用于Spring MVC的org.springframework.web.filter.ServerHttpObservationFilter
-   用于WebFlux的org.springframework.web.filter.reactive.ServerHttpObservationFilter

当Actuator是我们应用程序的一部分时，这些过滤器已经注册并处于激活状态。如果没有，我们需要配置它们：

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

我们可以在[文档](https://github.com/spring-projects/spring-framework/wiki/What's-New-in-Spring-Framework-6.x#observability)中找到有关Spring Web中可观察性集成的更多详细信息。

### 3.3 AOP

Micrometer Observation API还声明了一个@Observed注解，其中包含基于AspectJ的切面实现。为了完成这项工作，我们需要将AOP依赖项添加到我们的项目中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

然后，我们将切面实现注册为Spring管理的bean：

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

结合Actuator，我们可以使用http://localhost:8080/actuator/metrics/greetingService读取预先配置的指标(在我们至少调用服务一次之后)，并将得到如下结果：

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

## 4. 测试Observation

Micrometer Observation API提供了一个允许编写测试的模块。为此，我们需要添加以下依赖项：

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-observation-test</artifactId>
    <scope>test</scope>
</dependency>
```

micrometer-bom是Spring Boot托管依赖的一部分，因此我们不需要指定任何版本。

因为默认情况下整个可观察性自动配置对于测试是禁用的，所以每当我们想要测试默认观察时我们都需要使用@AutoConfigureObservability重新启用它。

### 4.1 TestObservationRegistry

我们可以使用允许基于AssertJ的断言的TestObservationRegistry。因此，我们必须用TestObservationRegistry实例替换已经在上下文中的ObservationRegistry。

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

我们还可以使用JUnit元注解来配置它：

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

然后，我们只需要将注解添加到我们的测试类中：

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

### 4.2 ObservationHandler兼容性套件

为了测试我们的ObservationHandler实现，我们可以在测试中继承几个基类(所谓的兼容性工具包)：

-   NullContextObservationHandlerCompatibilityKit测试ObservationHandler在空值参数的情况下是否正常工作。
-   AnyContextObservationHandlerCompatibilityKit测试ObservationHandler在未指定测试上下文参数的情况下是否正常工作。这也包括NullContextObservationHandlerCompatibilityKit。
-   ConcreteContextObservationHandlerCompatibilityKit测试ObservationHandler在测试上下文的上下文类型的情况下是否正常工作。

实现很简单：

```java
public class SimpleLoggingHandlerTest extends AnyContextObservationHandlerCompatibilityKit {

    SimpleLoggingHandler handler = new SimpleLoggingHandler();

    @Override
    public ObservationHandler<Observation.Context> handler() {
        return handler;
    }
}
```

这将生成以下输出：

<img src="../assets/img_1.png">

## 5. Micrometer Tracing

以前的Spring Cloud Sleuth项目已经迁移到Micrometer，这是自Spring Boot 3以来Micrometer Tracing的核心。我们可以在[文档](https://micrometer.io/docs/tracing)中找到Micrometer Tracing的定义：

>   Micrometer Tracing为最流行的跟踪器库提供了一个简单的门面，使你可以在没有供应商锁定的情况下检测基于JVM的应用程序代码。它旨在为你的跟踪收集活动增加很少甚至没有开销，同时最大限度地提高跟踪工作的可移植性。

我们可以单独使用它，但它也通过提供ObservationHandler扩展与Observation API集成。

### 5.1 集成到Observation API

要使用Micrometer Tracing，我们需要在项目中添加以下依赖项(版本由Spring Boot管理)：

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing</artifactId>
</dependency>
```

然后，我们需要一种[受支持](https://micrometer.io/docs/tracing#_supported_tracers)的跟踪器(目前是[OpenZipkin Brave](https://github.com/openzipkin/brave)或[OpenTelemetry](https://opentelemetry.io/))。然后，我们必须为特定于供应商的Micrometer Tracing集成添加依赖项：

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

Spring Actuator对两个跟踪器都具有自动配置功能，即它注册特定于供应商的对象和Micrometer Tracing ObservationHandler实现，将这些对象委托到应用程序上下文中。因此，不需要进一步的配置步骤。

### 5.2 测试支持

出于测试目的，我们需要将以下依赖项添加到我们的项目中(版本由Spring Boot管理)：

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-test</artifactId>
    <scope>test</scope>
</dependency>
```

然后，我们可以使用SimpleTracer类在测试期间收集和验证跟踪数据。为了使它正常工作，我们在应用程序上下文中使用SimpleTracer替换了原始的、特定于供应商的Tracer。我们还必须记住通过使用@AutoConfigureObservability来启用跟踪的自动配置。

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

或者，在使用JUnit元注解的情况下：

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

然后我们可以通过以下示例测试来测试我们的GreetingService：

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

在本教程中，我们探讨了Micrometer Observation API以及与Spring Boot 3的集成。我们了解到Micrometer是一个用于独立于供应商的仪器的API，而Micrometer Tracing是一个扩展。我们了解到Actuator有一组预配置的观察和跟踪，并且默认情况下禁用测试的可观察性自动配置。
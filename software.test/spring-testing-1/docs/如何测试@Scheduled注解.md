## 1. 概述

[Spring框架](https://www.baeldung.com/spring-intro)中可用的注解之一是@Scheduled，我们可以使用这个注解来[定时执行任务](https://www.baeldung.com/spring-scheduled-tasks)。

在本教程中，我们将探讨如何测试@Scheduled注解。

## 2. 依赖

首先，让我们从[Spring Initializer](https://start.spring.io/)开始创建一个基于[Spring Boot](https://www.baeldung.com/spring-boot) Maven的应用程序：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.2</version>
    <relativePath/>
</parent>
```

我们还需要使用几个Spring Boot启动器：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

并且，让我们将[JUnit 5](https://www.baeldung.com/junit-5)的依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
</dependency>
```

我们可以在[Maven Central](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-parent/3.0.4)上找到最新版本的Spring Boot。

此外，为了在测试中使用[Awaitility](https://central.sonatype.com/artifact/org.awaitility/awaitility/4.2.0)，我们也需要添加它的依赖：

```xml
<dependency>
    <groupId>org.awaitility</groupId>
    <artifactId>awaitility</artifactId>
    <version>3.1.6</version>
    <scope>test</scope>
</dependency>
```

## 3. 简单的@Scheduled示例

让我们从创建一个简单的Counter类开始：

```java
@Component
public class Counter {
    private final AtomicInteger counter = new AtomicInteger(0);

    @Scheduled(fixedDelay = 5)
    public void scheduled() {
        this.counter.incrementAndGet();
    }

    public int getInvocationCount() {
        return this.counter.get();
    }
}
```

我们通过scheduled方法来自增counter变量。请注意，我们在该方法上添加了@Scheduled注解，以便在5毫秒的固定时间段内执行它。

此外，让我们创建一个ScheduledConfig类来使用@EnableScheduling注解启用任务调度：

```java
@Configuration
@EnableScheduling
@ComponentScan("cn.tuyucheng.taketoday.scheduled")
public class ScheduledConfig {
}
```

## 4. 使用集成测试

测试我们的类的替代方法之一是使用[集成测试](https://www.baeldung.com/integration-testing-in-spring)。为此，我们需要使用@SpringJUnitConfig注解在测试环境中启动应用程序上下文和我们的bean：

```java
@SpringJUnitConfig(ScheduledConfig.class)
class ScheduledIntegrationTest {

    @Autowired
    private Counter counter;

    @Test
    void givenSleepBy100Ms_whenGetInvocationCount_thenIsGreaterThanZero() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(100L);
        
        assertThat(counter.getInvocationCount()).isGreaterThan(0);
    }
}
```

在这种情况下，我们启动Counter bean并等待100毫秒来检查scheduled方法的调用次数。

## 5. 使用Awaitility

另一种测试调度任务的方法是使用[Awaitility](https://www.baeldung.com/awaitlity-testing)。我们可以使用Awaitility DSL使我们的测试更具声明性：

```java
@SpringJUnitConfig(ScheduledConfig.class)
class ScheduledAwaitilityIntegrationTest {

    @SpyBean
    private Counter counter;

    @Test
    void whenWaitOneSecond_thenScheduledIsCalledAtLeastTenTimes() {
        await().atMost(Duration.ONE_SECOND).untilAsserted(() -> verify(counter, atLeast(10)).scheduled());
    }
}
```

在这种情况下，我们使用[@SpyBean](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/test/mock/mockito/SpyBean.html)注解注入我们的bean，以检查scheduled方法在一秒内被调用的次数。

## 6. 总结

在本教程中，我们展示了一些使用集成测试和Awaitility库来测试调度任务的方法。

我们需要考虑到，虽然集成测试很好，但**通常最好将重点放在调度方法内部逻辑的单元测试上**。
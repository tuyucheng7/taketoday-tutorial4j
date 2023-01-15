## 1. 概述

@Scheduled是Spring框架中比较有用的注解之一，我们可以使用这个注解按调度的方式执行任务。在本教程中，我们介绍如何测试@Scheduled注解。

## 2. 依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>2.6.1</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <version>2.6.1</version>
    <scope>test</scope>
</dependency>
```

然后，我们将JUnit 5的依赖项添加到pom.xml中：

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
</dependency>
```

此外，为了在测试中使用Awaitility，我们也需要添加其依赖：

```xml
<dependency>
    <groupId>org.awaitility</groupId>
    <artifactId>awaitility</artifactId>
    <version>3.1.6</version>
    <scope>test</scope>
</dependency>
```

## 3. 简单的@Scheduled例子

首先，我们创建一个简单的Counter类：

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

我们通过scheduled方法来自增counter变量。请注意，我们在该方法上添加了@Scheduled注解，以便在5毫秒的固定周期内执行它。另外，让我们创建一个ScheduledConfig类来使用@EnableScheduling注解启用任务调度：

```java
@Configuration
@EnableScheduling
@ComponentScan("cn.tuyucheng.taketoday.scheduled")
public class ScheduledConfig {

}
```

## 4. 使用集成测试

测试我们的类的替代方法之一是使用集成测试。为此，我们需要使用@SpringJUnitConfig注解在测试环境中启动应用程序上下文和我们的bean：

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

另一种测试调度任务的方法是使用Awaitility。我们可以使用Awaitility DSL使我们的测试更具声明性：

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

在这种情况下，我们使用@SpyBean注解注入我们的bean，以检查在一秒钟内调用scheduled方法的次数。

## 6. 总结

在本教程中，我们演示了一些使用集成测试和Awaitility库来测试调度任务的方法。

我们需要考虑到，虽然集成测试很好，**但通常最好专注于调度方法内部逻辑的单元测试**。
## 1. 概述

在本教程中，我们将说明如何使用**Spring @Scheduled注解**来配置和安排任务。

使用@Scheduled注解方法需要遵循的简单规则是：

-   该方法通常应该有一个void返回类型(如果不是，返回值将被忽略)
-   该方法不应期望任何参数

### 延伸阅读：

### [如何在Spring中执行@Async](https://www.baeldung.com/spring-async)

如何在Spring中启用和使用@Async-从非常简单的配置和基本用法到更复杂的执行程序和异常处理策略。

[阅读更多](https://www.baeldung.com/spring-async)→

### [Spring任务调度器指南](https://www.baeldung.com/spring-task-scheduler)

使用Task Scheduler在Spring中进行调度的快速实用指南

[阅读更多](https://www.baeldung.com/spring-task-scheduler)→

### [使用Quartz在Spring中进行调度](https://www.baeldung.com/spring-quartz-schedule)

在Spring中使用Quartz的快速介绍。

[阅读更多](https://www.baeldung.com/spring-quartz-schedule)→

## 2. 启用调度支持

要在Spring中启用对调度任务和@Scheduled注解的支持，我们可以使用Java enable-style注解：

```java
@Configuration
@EnableScheduling
public class SpringConfig {
    // ...
}
```

同样，我们可以在XML中做同样的事情：

```xml
<task:annotation-driven>
```

## 3. 固定延迟安排任务

让我们首先配置一个任务在固定延迟后运行：

```java
@Scheduled(fixedDelay = 1000)
public void scheduleFixedDelayTask() {
    System.out.println("Fixed delay task - " + System.currentTimeMillis() / 1000);
}
```

在这种情况下，上一次执行结束和下一次执行开始之间的持续时间是固定的。任务总是等到前一个任务完成。

当必须在再次运行之前完成上一次执行时，应使用此选项。

## 4. 以固定速率安排任务

现在让我们以固定的时间间隔执行任务：

```java
@Scheduled(fixedRate = 1000)
public void scheduleFixedRateTask() {
    System.out.println("Fixed rate task - " + System.currentTimeMillis() / 1000);
}
```

当任务的每次执行都是独立的时，应使用此选项。

请注意，默认情况下计划任务不会并行运行。因此，即使我们使用fixedRate，下一个任务也不会在上一个任务完成之前被调用。

**如果我们想在计划任务中支持并行行为，我们需要添加@Async注解**：

```java
@EnableAsync
public class ScheduledFixedRateExample {
    @Async
    @Scheduled(fixedRate = 1000)
    public void scheduleFixedRateTaskAsync() throws InterruptedException {
        System.out.println("Fixed rate task async - " + System.currentTimeMillis() / 1000);
        Thread.sleep(2000);
    }
}
```

现在这个异步任务将每秒被调用一次，即使之前的任务没有完成。

## 5. 固定速率与固定延迟

我们可以使用Spring的@Scheduled注解运行计划任务，但基于fixedDelay和fixedRate属性，执行的性质会发生变化。

**fixedDelay属性确保任务执行的完成时间和任务下一次执行的开始时间之间有n毫秒的延迟**。

当我们需要确保只有一个任务实例始终运行时，此属性特别有用。对于依赖的工作，这很有帮助。

**fixedRate属性每n毫秒运行一次计划任务**。它不检查任务的任何先前执行。

当任务的所有执行都是独立的时，这很有用。如果我们不希望超过内存和线程池的大小，fixedRate应该非常好用。

虽然，如果传入的任务没有快速完成，它们可能会以“内存不足异常”结束。

## 6. 安排一个有初始延迟的任务

接下来，让我们安排一个延迟(以毫秒为单位)的任务：

```java
@Scheduled(fixedDelay = 1000, initialDelay = 1000)
public void scheduleFixedRateWithInitialDelayTask() {
 
    long now = System.currentTimeMillis() / 1000;
    System.out.println("Fixed rate task with one second initial delay - " + now);
}
```

注意我们在这个例子中是如何同时使用fixedDelay和initialDelay的。任务会在initialDelay值后第一次执行，之后会按照fixedDelay继续执行。

当任务具有需要完成的设置时，此选项很方便。

## 7. 使用Cron表达式安排任务

有时延迟和速率还不够，我们需要cron表达式的灵活性来控制我们任务的时间表：

```java
@Scheduled(cron = "0 15 10 15 * ?")
public void scheduleTaskUsingCronExpression() {
 
    long now = System.currentTimeMillis() / 1000;
    System.out.println("schedule tasks using cron jobs - " + now);
}
```

请注意，在此示例中，我们计划在每个月的第15天上午10:15执行任务。

默认情况下，Spring将为cron表达式使用服务器的本地时区。但是，**我们可以使用zone属性来更改此时区**：

```java
@Scheduled(cron = "0 15 10 15 * ?", zone = "Europe/Paris")
```

使用此配置，Spring将安排带注解的方法在巴黎时间每个月的第15天上午10:15运行。

## 8. 参数化时间表

硬编码这些时间表很简单，但我们通常需要能够控制时间表，而无需重新编译和重新部署整个应用程序。

我们将使用Spring表达式来外部化任务的配置，并将这些存储在属性文件中。

固定延迟任务：

```java
@Scheduled(fixedDelayString = "${fixedDelay.in.milliseconds}")
```

固定速率任务：

```java
@Scheduled(fixedRateString = "${fixedRate.in.milliseconds}")
```

基于cron表达式的任务：

```java
@Scheduled(cron = "${cron.expression}")
```

## 9. 使用XML配置计划任务

Spring还提供了一种配置计划任务的XML方式。这是设置这些的XML配置：

```xml
<!-- Configure the scheduler -->
<task:scheduler id="myScheduler" pool-size="10" />

<!-- Configure parameters -->
<task:scheduled-tasks scheduler="myScheduler">
    <task:scheduled ref="beanA" method="methodA" fixed-delay="5000" initial-delay="1000" />
    <task:scheduled ref="beanB" method="methodB" fixed-rate="5000" />
    <task:scheduled ref="beanC" method="methodC" cron="*/5 * * * * MON-FRI" />
</task:scheduled-tasks>
```

## 10. 在运行时动态设置延迟或速率

通常， @Scheduled注解的所有属性只在Spring上下文启动时解析和初始化一次。

因此，**当我们在Spring中使用@Scheduled注解时，无法在运行时更改fixedDelay或fixedRate值**。

但是，有一个解决方法。**使用Spring的SchedulingConfigurer提供了一种更可定制的方式，使我们有机会动态设置延迟或速率**。

让我们创建一个Spring配置DynamicSchedulingConfig并实现SchedulingConfigurer接口：

```java
@Configuration
@EnableScheduling
public class DynamicSchedulingConfig implements SchedulingConfigurer {

    @Autowired
    private TickService tickService;

    @Bean
    public Executor taskExecutor() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
        taskRegistrar.addTriggerTask(
              new Runnable() {
                  @Override
                  public void run() {
                      tickService.tick();
                  }
              },
              new Trigger() {
                  @Override
                  public Date nextExecutionTime(TriggerContext context) {
                      Optional<Date> lastCompletionTime = Optional.ofNullable(context.lastCompletionTime());
                      Instant nextExecutionTime =
                            lastCompletionTime.orElseGet(Date::new).toInstant()
                                  .plusMillis(tickService.getDelay());
                      return Date.from(nextExecutionTime);
                  }
              }
        );
    }
}
```

正如我们注意到的，借助ScheduledTaskRegistrar#addTriggerTask方法，我们可以添加一个Runnable任务和一个Trigger实现，以在每次执行结束后重新计算nextExecutionTime。

此外，我们用@EnableScheduling注解我们的DynamicSchedulingConfig以使调度工作。

因此，我们安排了TickService#tick方法在每次延迟后运行它，这是在运行时由getDelay方法动态确定的。

## 11. 并行运行任务

默认情况下，**Spring使用本地单线程调度程序来运行任务**。因此，即使我们有多个@Scheduled方法，它们每个都需要等待线程完成执行前一个任务。

如果我们的任务是真正独立的，那么并行运行它们会更方便。为此，我们需要提供一个更适合我们需求的[TaskScheduler](https://www.baeldung.com/spring-task-scheduler)：

```java
@Bean
public TaskScheduler  taskScheduler() {
    ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
    threadPoolTaskScheduler.setPoolSize(5);
    threadPoolTaskScheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
    return threadPoolTaskScheduler;
}
```

在上面的示例中，我们将TaskScheduler配置为池大小为5，但请记住，实际配置应根据个人的特定需求进行微调。

### 11.1 使用Spring Boot

如果我们使用Spring Boot，我们可以使用一种更方便的方法来增加调度程序的池大小。

设置spring.task.scheduling.pool.size属性就足够了：

```properties
spring.task.scheduling.pool.size=5
```

## 12. 总结

在本文中，我们讨论了配置和使用@Scheduled注解的方法。

我们介绍了启用调度的过程，以及配置调度任务模式的各种方法。我们还展示了一种动态配置延迟和速率的解决方法。
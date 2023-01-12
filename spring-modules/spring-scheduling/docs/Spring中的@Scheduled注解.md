## 1. 概述

在本教程中，我们将介绍Spring的@Scheduled注解如何用于配置和调度任务。

使用@Scheduled注解标注的方法需要遵循的简单规则是：

+ 该方法通常应该有一个void返回类型(如果不是，返回的值将被忽略)。
+ 该方法不应接收任何参数。

## 2. 启用对调度的支持

为了在Spring中启用对调度任务和@Scheduled注解的支持，我们可以使用@EnableScheduling注解：

```java

@Configuration
@EnableScheduling
public class SpringSchedulingConfig {

}
```

或者，如果我们使用的是XML：

```
<task:annotation-driven>
```

## 3. 以固定延迟调度任务

让我们首先将任务配置为在固定的延迟后运行：

```java

@Component("scheduledAnnotationExample")
public class ScheduledAnnotationExample {

  @Scheduled(fixedDelay = 1000)
  public void scheduleFixedDelayTask() {
    System.out.println("Fixed delay task - " + System.currentTimeMillis() / 1000);
  }
}
```

在这种情况下，上一次执行结束和下一次执行开始之间的持续时间是固定的。任务始终等待上一个任务完成。

当必须在再次运行之前完成先前的任务执行时，应使用此选项。

## 4. 以固定速率调度任务

现在让我们以固定的时间间隔执行一个任务：

```java
public class ScheduledAnnotationExample {

  @Scheduled(fixedRate = 1000)
  public void scheduleFixedRateTask() {
    System.out.println("Fixed rate task - " + System.currentTimeMillis() / 1000);
  }
}
```

当任务的每次执行都是独立的时，应使用此方法。

请注意，调度任务默认情况下不会并行运行。所以即使我们使用了fixedRate，在前一个任务完成之前，下一个任务也不会被调用。

如果我们想在调度任务中支持并行行为，我们需要添加@Async注解：

```java

@Component
@EnableAsync
public class ScheduledFixedRateExample {

  @Async
  @Scheduled(fixedRate = 1000)
  public void scheduleFixedRateTaskAsync() throws InterruptedException {
    System.out.println("Fixed rate task async - " + System.currentTimeMillis() / 1000);
    TimeUnit.MILLISECONDS.sleep(2000);
  }
}
```

现在这个异步任务每秒都会被调用，即使之前的任务没有完成。

## 5. 固定速率与固定延迟

我们可以使用Spring的@Scheduled注解来运行调度任务，但是基于属性fixedDelay和fixedRate，执行的性质会发生变化。

fixedDelay属性确保在任务执行的完成时间与下一次任务执行的开始时间之间存在n毫秒的延迟。

当我们需要确保只有一个任务实例始终运行时，此属性特别有用。对于互相依赖的任务，这很有帮助。

fixedRate属性每隔n毫秒运行一次任务。它不检查任务的任何先前执行。

当任务的所有执行都是独立的时，这很有用。如果我们不期望超过内存和线程池的大小，fixedRate应该会很方便。

虽然，如果传入的任务没有快速完成，它们可能会以“Out of Memory exception”告终。

## 6. 调度具有初始延迟的任务

接下来，让我们调度一个有延迟(以毫秒为单位)的任务：

```java
public class ScheduledAnnotationExample {

  @Scheduled(fixedDelay = 1000, initialDelay = 1000)
  public void scheduledFixedRateWithInitialDelayTask() {
    long now = System.currentTimeMillis() / 1000;
    System.out.println("Fixed rate task with one second initial delay - " + now);
  }
}
```

注意我们在这个例子中是如何同时使用fixedDelay和initialDelay的。
任务会在initialDelay值后第一次执行，并按照fixedDelay继续执行。

当任务有需要完成的设置时，此选项很方便。

## 7. 使用Cron表达式调度任务

有时延迟和速率是不够的，我们需要cron表达式的灵活性来控制我们任务的调度：

```java
public class ScheduledAnnotationExample {
  /
    任务在每月15日上午10:15执行
   /
  @Scheduled(cron = "0 15 10 15  ?")
  public void scheduleTaskUsingCronExpression() {
    long now = System.currentTimeMillis() / 1000;
    System.out.println("schedule tasks using cron jobs - " + now);
  }
}
```

请注意，在此示例中，我们将任务安排在每月15日上午10:15执行。

默认情况下，Spring将使用服务器的本地时区作为cron表达式。但是，我们可以使用zone属性来更改这个时区：

```
@Scheduled(cron = "0 15 10 15  ?", zone = "Asia/Shanghai")
```

使用此配置，Spring将安排带注解的方法在上海时间每月15日上午10:15运行。

## 8. 参数化调度器

硬编码这些时间表很简单，但我们通常需要能够在不重新编译和重新部署整个应用程序的情况下控制时间表。

我们将使用Spring表达式来外部化任务的配置，并将它们存储在属性文件中。

固定延迟任务：

```java
public class ScheduledAnnotationExample {

  @Scheduled(fixedDelayString = "${fixedDelay.in.milliseconds}")
  public void scheduleFixedDelayTaskUsingExpression() {
    System.out.println("Fixed delay task - " + System.currentTimeMillis() / 1000);
  }
}
```

固定速率任务：

```java
public class ScheduledAnnotationExample {

  @Scheduled(fixedRateString = "${fixedRate.in.milliseconds}")
  public void scheduleFixedRateTaskUsingExpression() {
    System.out.println("Fixed rate task - " + System.currentTimeMillis() / 1000);
  }
}
```

基于cron表达式的任务：

```java
public class ScheduledAnnotationExample {

  @Scheduled(cron = "${cron.expression}")
  public void scheduleTaskUsingExternalizedCronExpression() {
    System.out.println("schedule tasks using externalized cron expressions - " + System.currentTimeMillis() / 1000);
  }
}
```

以及上述包含任务配置的属性文件：

```properties
cron.expression=0 15 10 15  ?
fixedRate.in.milliseconds=1000
fixedDelay.in.milliseconds=1000
```

## 9. 使用XML配置任务调度

Spring还提供了一种配置任务的XML方式。以下是这些的XML配置：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:task="http://www.springframework.org/schema/task"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="
		http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.2.xsd
		http://www.springframework.org/schema/task http://www.springframework.org/schema/task/spring-task-4.2.xsd
		http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.2.xsd">

  <context:property-placeholder location="classpath:springScheduled.properties"/>

  <!-- 配置调度器 -->
  <task:scheduler id="myScheduler" pool-size="10"/>

  <bean id="myscheduler" class="org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler"/>

  <!-- 配置基于fixedDealy、fixedRate或cron的任务 -->
  <bean id="schedulingWithXmlConfig" class="cn.tuyucheng.taketoday.scheduling.SchedulingWithXmlConfig"/>

  <task:scheduled-tasks scheduler="myScheduler">
    <task:scheduled ref="schedulingWithXmlConfig" method="scheduleFixedDelayTask"
                    fixed-delay="${fixedDelay.in.milliseconds}" initial-delay="1000"/>
    <task:scheduled ref="schedulingWithXmlConfig" method="scheduleFixedRateTask"
                    fixed-rate="${fixedRate.in.milliseconds}"/>
    <task:scheduled ref="schedulingWithXmlConfig" method="scheduleTaskUsingCronExpression" cron="${cron.expression}"/>
  </task:scheduled-tasks>
</beans>
```

## 10. 在运行时动态设置延迟或速率

通常，@Scheduled注解的所有属性只在Spring上下文启动时解析和初始化一次。

因此，当我们在Spring中使用@Scheduled注解时，无法在运行时更改fixedDelay或fixedRate的值。

但是，有一种解决方法。使用Spring的SchedulingConfigurer提供了一种更可定制的方式，让我们有机会动态设置延迟或速率。

让我们创建一个Spring配置类DynamicSchedulingConfig，并实现SchedulingConfigurer接口：

```java

@Configuration
@ComponentScan("cn.tuyucheng.taketoday.scheduling.dynamic")
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
    taskRegistrar.addTriggerTask(() -> tickService.tick(), context -> {
      Optional<Date> lastCompletionTime = Optional.ofNullable(context.lastCompletionTime());
      Instant nextExecutionTime = lastCompletionTime.orElseGet(Date::new).toInstant().plusMillis(tickService.getDelay());
      return Date.from(nextExecutionTime);
    });
  }
}

@Service
public class TickService {
  private long delay = 0;

  public long getDelay() {
    this.delay += 1000;
    System.out.println("delaying " + this.delay + " milliseconds...");
    return this.delay;
  }

  public void tick() {
    final long now = System.currentTimeMillis() / 1000;
    System.out.println("schedule tasks with dynamic delay - " + now);
  }
}
```

我们注意到，使用ScheduledTaskRegistrar.addTriggerTask()方法，
我们可以添加一个Runnable任务和一个Trigger实现，以在每次执行结束后重新计算nextExecutionTime。

此外，我们使用@EnableScheduling标注我们的DynamicSchedulingConfig以开启调度功能。

因此，我们安排TickService.tick()方法在每个延迟量之后运行它，延迟量由getDelay方法在运行时动态确定。

## 11. 并行运行任务

默认情况下，Spring使用本地单线程调度程序来运行任务。
因此，即使我们有多个@Scheduled方法，它们每个都需要等待线程完成执行上一个任务。

如果我们的任务是真正独立的，并行运行它们会更方便。为此，我们需要提供一个更适合我们需求的TaskScheduler：

```java

@Configuration
@EnableScheduling
@ComponentScan("cn.tuyucheng.taketoday.scheduling")
@PropertySource("classpath:springScheduled.properties")
public class SpringSchedulingConfig {

  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }

  @Bean
  public TaskScheduler taskScheduler() {
    ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
    threadPoolTaskScheduler.setPoolSize(5);
    threadPoolTaskScheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
    return threadPoolTaskScheduler;
  }
}
```

在上面的示例中，我们将TaskScheduler配置为池的大小为5，但请记住，实际配置应根据具体需求进行微调。

如果我们使用Spring Boot，我们可以使用更方便的方法来增加调度程序的池大小。

设置spring.task.scheduling.pool.size属性就足够了：

```properties
spring.task.scheduling.pool.size=5
```

## 12. 总结

在本文中，我们介绍了配置和使用@Scheduled注解的方式。

我们介绍了启用调度的过程，以及配置调度任务模式的各种方法。我们还展示了一种动态配置延迟和速率的解决方法。
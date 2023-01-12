## 1. 简介

[Spring Scheduling](https://www.baeldung.com/spring-scheduled-tasks)库允许应用程序以特定的时间间隔执行代码。因为时间间隔是使用@Scheduled注解指定的，所以时间间隔通常是静态的，并且在应用程序的整个生命周期内不能更改。

在本教程中，我们将研究有条件地启用 Spring 计划作业的各种方法。

## 2. 使用布尔标志

有条件地启用 Spring 计划作业的最简单方法是使用我们在计划作业中检查的布尔变量。可以使用@Value注解该变量，以使其可使用普通的[Spring 配置机制](https://www.baeldung.com/properties-with-spring)进行配置：

```java
@Configuration
@EnableScheduling
public class ScheduledJobs {
  @Value("${jobs.enabled:true}")
  private boolean isEnabled;

  @Scheduled(fixedDelay = 60000)
  public void cleanTempDirectory() {
    if(isEnabled) {
      // do work here
    }
  }
}
```

缺点是计划的作业将始终由 Spring 执行，这在某些情况下可能并不理想。

## 3.使用 @ConditionalOnProperty

另一种选择是使用@ConditionalOnProperty注解。它采用 Spring 属性名称，并且仅在属性评估为true 时运行。

首先，我们新建一个类，封装了调度作业的代码，包括调度间隔：

```java
public class ScheduledJob {
    @Scheduled(fixedDelay = 60000)
    public void cleanTempDir() {
        // do work here
  }
}
```

然后我们有条件地创建该类型的 bean：

```java
@Configuration
@EnableScheduling
public class ScheduledJobs {
    @Bean
    @ConditionalOnProperty(value = "jobs.enabled", matchIfMissing = true, havingValue = "true")
    public ScheduledJob scheduledJob() {
        return new ScheduledJob();
    }
}
```

在这种情况下，如果属性jobs.enabled设置为true，或者它根本不存在，作业将运行 。缺点是此注解 仅在Spring Boot中可用。

## 4.使用弹簧配置文件

我们还可以根据应用程序运行时使用的[配置文件](https://www.baeldung.com/spring-profiles)有条件地启用 Spring 计划的作业。例如，当只应在生产环境中安排作业时，此方法很有用。

当计划在所有环境中都相同并且只需要在特定配置文件中禁用或启用时，这种方法很有效。

这与使用@ConditionalOnProperty类似，除了我们在 bean 方法上使用@Profile注解：

```java
@Profile("prod")
@Bean
public ScheduledJob scheduledJob() {
    return new ScheduledJob();
}
```

仅当prod配置文件处于活动状态时，这才会创建作业。此外，它为我们提供了@Profile注解附带的全套选项：匹配多个配置文件、复杂的 spring 表达式等。

使用这种方法需要注意的一件事是，如果根本没有指定配置文件，bean 方法将被执行。

## 5. Cron 表达式中的值占位符

使用 Spring 值占位符，我们不仅可以有条件地启用作业，还可以更改其调度：

```java
@Scheduled(cron = "${jobs.cronSchedule:-}")
public void cleanTempDirectory() {
    // do work here
}
```

在此示例中，默认情况下禁用作业(使用特殊的 Spring cron disable 表达式)。

如果我们想要启用该作业，我们所要做的就是为jobs.cronSchedule 提供一个有效的 cron 表达式。我们可以像任何其他 Spring 配置一样执行此操作：命令行参数、环境变量、属性文件等。

与 cron 表达式不同，无法设置禁用作业的固定延迟或固定速率值。因此这种方法只适用于 cron 计划的作业。

## 六. 总结

在本教程中，我们已经看到有几种不同的方法可以有条件地启用 Spring 计划的作业。有些方法比其他方法更简单，但可能有局限性。
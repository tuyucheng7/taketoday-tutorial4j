## 1. 简介

[Spring Scheduling](https://www.baeldung.com/spring-scheduled-tasks)库允许应用程序以特定的时间间隔执行代码。**由于时间间隔是使用@Scheduled注解指定的，因此时间间隔通常是静态的，并且在应用程序的整个生命周期内不能更改**。

在本教程中，我们将研究有条件地启用Spring计划作业的各种方法。

## 2. 使用布尔标志

**有条件地启用Spring计划作业的最简单方法是使用我们在计划作业中检查的布尔变量**。可以使用@Value标注该变量，以使其可使用普通的[Spring配置机制](https://www.baeldung.com/properties-with-spring)进行配置：

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

缺点是**计划的作业将始终由Spring执行**，这在某些情况下可能并不理想。

## 3. 使用@ConditionalOnProperty

另一种选择是使用@ConditionalOnProperty注解。它采用Spring属性名称，并且仅在属性评估为true时才运行。

首先，我们创建一个新类来封装调度的作业代码，包括计划间隔：

```java
public class ScheduledJob {
    @Scheduled(fixedDelay = 60000)
    public void cleanTempDir() {
        // do work here
    }
}
```

然后我们有条件地创建该类型的bean：

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

在这种情况下，如果属性jobs.enabled设置为true，或者它根本不存在，则作业将运行。缺点是**此注解仅在Spring Boot中可用**。

## 4. 使用Spring Profile

我们还可以根据应用程序运行时使用的[Profile](https://www.baeldung.com/spring-profiles)有条件地启用Spring计划的作业。例如，当只应在生产环境中调度作业时，此方法很有用。

**当计划在所有环境中都相同并且只需要在特定Profile中禁用或启用时，这种方法很有效**。

这与使用@ConditionalOnProperty类似，只是我们在bean方法上使用@Profile注解：

```java
@Profile("prod")
@Bean
public ScheduledJob scheduledJob() {
    return new ScheduledJob();
}
```

**仅当prod Profile处于激活状态时，这才会创建作业**。此外，它为我们提供了@Profile注解附带的全套选项：匹配多个Profile、复杂的Spring表达式等。

使用这种方法需要注意的一件事是，**如果根本没有指定Profile，则bean方法将被执行**。

## 5. Cron表达式中的值占位符

使用Spring值占位符，我们不仅可以有条件地启用作业，还可以更改其调度：

```java
@Scheduled(cron = "${jobs.cronSchedule:-}")
public void cleanTempDirectory() {
    // do work here
}
```

在此示例中，作业默认处于禁用状态(使用特殊的Spring cron禁用表达式)。

如果我们想要启用该作业，我们所要做的就是为jobs.cronSchedule提供一个有效的cron表达式。我们可以像任何其他Spring配置一样执行此操作：命令行参数、环境变量、属性文件等。

与cron表达式不同，无法设置禁用作业的固定延迟或固定速率值。因此**这种方法只适用于cron调度的作业**。

## 6. 总结

在本教程中，我们已经看到有几种不同的方法可以有条件地启用Spring调度的作业。有些方法比其他方法更简单，但可能有局限性。
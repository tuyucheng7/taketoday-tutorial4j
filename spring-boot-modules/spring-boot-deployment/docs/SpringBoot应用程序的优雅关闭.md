## 1. 概述

在关闭时，默认情况下，Spring的TaskExecutor只是中断所有正在运行的任务，但**让它等待所有正在运行的任务完成可能会更好，这为每个任务提供了采取措施以确保关闭安全的机会**。

在本快速教程中，我们将学习如何在涉及使用线程池执行的任务时更优雅地关闭Spring Boot应用程序。

## 2. 简单示例

让我们考虑一个简单的Spring Boot应用程序，我们将自动注入默认的TaskExecutor bean：

```java
@Autowired
private TaskExecutor taskExecutor;
```

在应用程序启动时，让我们使用线程池中的线程执行一个1分钟长的进程：

```java
taskExecutor.execute(() -> {
    Thread.sleep(60_000);
});
```

当启动[关闭]()时，例如启动后20秒，示例中的线程将被中断，应用程序立即关闭。

## 3. 等待任务完成

让我们通过创建自定义ThreadPoolTaskExecutor bean来更改任务执行器的默认行为。

此类提供标志setWaitForTasksToCompleteOnShutdown以防止中断正在运行的任务，让我们将其设置为true：

```java
@Bean
public TaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setCorePoolSize(2);
    taskExecutor.setMaxPoolSize(2);
    taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
    taskExecutor.initialize();
    return taskExecutor;
}
```

而且，我们可以重写之前的逻辑以创建3个线程，每个线程执行一个1分钟长的任务。

```java
@PostConstruct
public void runTaskOnStartup() {
    for (int i = 0; i < 3; i++) {
        taskExecutor.execute(() -> {
            Thread.sleep(60_000);
        });
    }
}
```

现在让我们在启动后的前60秒内启动关机。我们看到应用程序在启动后仅120秒就关闭了，池大小为2意味着只允许同时执行两个任务，因此第三个任务排队。

**设置标志可确保当前正在执行的任务和排队的任务都已完成**。

请注意，当收到关闭请求时，**任务执行器会关闭队列，这样就无法添加新任务**。

## 4. 终止前的最长等待时间

尽管我们已经配置为等待正在进行的和排队的任务完成，但**Spring会继续关闭容器的其余部分**，这可能会释放我们的任务执行器所需的资源并导致任务失败。

为了阻止容器其余部分的关闭，我们可以在ThreadPoolTaskExecutor上指定一个最长等待时间：

```java
taskExecutor.setAwaitTerminationSeconds(30);
```

这确保在指定的时间段内，**容器级别的关闭过程将被阻止**。

当我们将setWaitForTasksToCompleteOnShutdown标志设置为true时，我们需要指定一个明显更高的超时，以便队列中的所有剩余任务也都得到执行。

## 5. 总结

在这个快速教程中，我们了解了如何通过配置任务执行器bean来完成运行和提交的任务直到结束，从而安全地关闭Spring Boot应用程序，这保证了所有任务都有指定的时间来完成它们的工作。

一个明显的副作用是它还可能导致更长的关机阶段。因此，我们需要根据应用程序的性质决定是否使用它。
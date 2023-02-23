## 1. 概述

在本教程中，我们将讨论**Spring任务调度机制**、TaskScheduler及其预定义的实现。然后我们将探索要使用的不同触发器。要阅读有关Spring调度的更多信息，可以查看这些[@Async](https://www.baeldung.com/spring-async)和[@Scheduled](https://www.baeldung.com/spring-scheduled-tasks)文章。

Spring 3.0引入了TaskScheduler，它具有多种设计用于在未来某个时间点运行的方法。TaskScheduler还返回ScheduledFuture接口的表示对象，我们可以使用它来取消计划任务并检查它们是否完成。

我们需要做的就是选择一个可运行的任务进行调度，然后选择一个适当的调度策略。

## 2. 线程池任务调度器

ThreadPoolTaskScheduler对于内部线程管理很有用，因为它将任务委托给ScheduledExecutorService，并实现TaskExecutor接口。它的单个实例能够处理异步潜在执行，以及@Scheduled注解。

让我们在ThreadPoolTaskSchedulerConfig定义ThreadPoolTaskScheduler bean：

```java
@Configuration
@ComponentScan(
      basePackages="cn.tuyucheng.taketoday.taskscheduler",
      basePackageClasses={ThreadPoolTaskSchedulerExamples.class})
public class ThreadPoolTaskSchedulerConfig {

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler(){
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(5);
        threadPoolTaskScheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
        return threadPoolTaskScheduler;
    }
}
```

配置的bean threadPoolTaskScheduler可以根据配置的池大小5异步执行任务。

请注意，所有与ThreadPoolTaskScheduler相关的线程名称都将以ThreadPoolTaskScheduler为前缀。

让我们实现一个简单的任务，然后我们可以调度：

```java
class RunnableTask implements Runnable{
    private String message;

    public RunnableTask(String message){
        this.message = message;
    }

    @Override
    public void run() {
        System.out.println(new Date()+" Runnable Task with "+message +" on thread "+Thread.currentThread().getName());
    }
}
```

我们现在可以安排调度程序来执行此任务：

```java
taskScheduler.schedule(
    new Runnabletask("Specific time, 3 Seconds from now"),
    new Date(System.currentTimeMillis + 3000)
);
```

taskScheduler将在已知日期(正好比当前时间晚3秒)调度此可运行任务。

现在让我们更深入地了解ThreadPoolTaskScheduler调度机制。

## 3. 以固定延迟可运行任务

我们可以使用两种简单的机制来安排固定延迟：

### 3.1 在上次调度执行的固定延迟后进行调度

让我们配置一个任务在1000毫秒的固定延迟后运行：

```java
taskScheduler.scheduleWithFixedDelay(new RunnableTask("Fixed 1 second Delay"), 1000);
```

RunnableTask将始终在一次执行完成和下一次执行开始之间的1000毫秒后运行。

### 3.2 在特定日期的固定延迟后调度

让我们将任务配置为在给定开始时间的固定延迟后运行：

```java
taskScheduler.scheduleWithFixedDelay(
    new RunnableTask("Current Date Fixed 1 second Delay"),
    new Date(),
    1000);
```

RunnableTask将在指定的执行时间被调用，其中包括@PostConstruct方法开始的时间，随后有1000毫秒的延迟。

## 4. 固定速率调度

有两种简单的机制可以以固定速率调度可运行的任务。

### 4.1 以固定速率调度RunnableTask

让我们安排一个任务以**固定的毫秒速率**运行：

```java
taskScheduler.scheduleAtFixedRate(new RunnableTask("Fixed Rate of 2 seconds") , 2000);
```

**下一个RunnableTask将始终在2000毫秒后运行，而不管上次执行的状态如何，它可能仍在运行**。

### 4.2 从给定日期开始以固定速率安排RunnableTask

```java
taskScheduler.scheduleAtFixedRate(new RunnableTask("Fixed Rate of 2 seconds"), new Date(), 3000);
```

RunnableTask将在当前时间后运行3000毫秒。

## 5. 使用CronTrigger进行调度

我们使用CronTrigger来根据cron表达式安排任务：

```java
CronTrigger cronTrigger = new CronTrigger("10 * * * * ?");
```

我们可以使用提供的触发器根据特定的指定节奏或时间表运行任务：

```java
taskScheduler.schedule(new RunnableTask("Cron Trigger"), cronTrigger);
```

在这种情况下，RunnableTask将在每分钟的第10秒执行。

## 6. 使用PeriodicTrigger进行调度

让我们使用PeriodicTrigger来调度一个**固定延迟**为2000毫秒的任务：

```java
PeriodicTrigger periodicTrigger = new PeriodicTrigger(2000, TimeUnit.MICROSECONDS);
```

配置的PeriodicTrigger bean用于在2000毫秒的固定延迟后运行任务。

现在让我们使用PeriodicTrigger调度RunnableTask：

```java
taskScheduler.schedule(new RunnableTask("Periodic Trigger"), periodicTrigger);
```

我们还可以将PeriodicTrigger配置为以固定速率而不是固定延迟进行初始化。此外，我们可以为第一个计划任务设置一个给定毫秒的初始延迟。

我们需要做的就是在periodicTrigger bean的return语句之前添加两行代码：

```java
periodicTrigger.setFixedRate(true);
periodicTrigger.setInitialDelay(1000);
```

我们使用setFixedRate方法以固定速率而不是固定延迟来调度任务。然后我们使用setInitialDelay方法为第一个可运行任务运行设置初始延迟。

## 7. 总结

在这篇简短的文章中，我们学习了如何使用Spring对任务的支持来调度可运行的任务。

我们演示了以固定延迟、固定速率并根据指定触发器运行任务。
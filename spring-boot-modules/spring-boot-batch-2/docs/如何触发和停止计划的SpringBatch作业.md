## 1. 概述

在本教程中，我们将研究和比较不同的方法来**触发和停止任何所需业务案例的计划Spring Batch作业**。

如果你需要Spring Batch和Scheduler的介绍，请参考[Spring-Batch](https://www.baeldung.com/introduction-to-spring-batch)和[Spring-Scheduler](https://www.baeldung.com/spring-scheduled-tasks)的文章。

## 2. 触发预定的Spring Batch作业

首先，我们有一个类SpringBatchScheduler来配置调度和批处理作业。方法launchJob()将被注册为计划任务。

此外，为了以最直观的方式触发计划的Spring Batch作业，让我们添加一个条件标志，仅当标志设置为true时才触发作业：

```java
private AtomicBoolean enabled = new AtomicBoolean(true);

private AtomicInteger batchRunCounter = new AtomicInteger(0);

@Scheduled(fixedRate = 2000)
public void launchJob() throws Exception {
    if (enabled.get()) {
        Date date = new Date();
        JobExecution jobExecution = jobLauncher()
            .run(job(), new JobParametersBuilder()
                .addDate("launchDate", date)
                .toJobParameters());
        batchRunCounter.incrementAndGet();
    }
}

// stop, start functions (changing the flag of enabled)
```

变量batchRunCounter将用于集成测试以验证批处理作业是否已停止。

## 3. 停止计划的Spring Batch作业

有了上面的条件标志，我们就可以在计划任务处于活动状态的情况下触发计划的Spring Batch作业。

如果我们不需要恢复作业，那么我们实际上可以停止计划任务以节省资源。

让我们看看接下来两个小节中的两个选项。

### 3.1 使用调度程序后处理器

由于我们通过使用[@Scheduled](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/scheduling/annotation/Scheduled.html)注解来调度方法，因此将首先注册一个bean后处理器[ScheduledAnnotationBeanPostProcessor](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/scheduling/annotation/ScheduledAnnotationBeanPostProcessor.html)。

我们可以显式调用postProcessBeforeDestruction()来销毁给定的scheduled bean：

```java
@Test
public void stopJobSchedulerWhenSchedulerDestroyed() throws Exception {
    ScheduledAnnotationBeanPostProcessor bean = context.getBean(ScheduledAnnotationBeanPostProcessor.class);
    SpringBatchScheduler schedulerBean = context.getBean(SpringBatchScheduler.class);
    await().untilAsserted(() -> Assert.assertEquals(
        2, 
        schedulerBean.getBatchRunCounter().get()));
    bean.postProcessBeforeDestruction(schedulerBean, "SpringBatchScheduler");
    await().atLeast(3, SECONDS);

    Assert.assertEquals(2, schedulerBean.getBatchRunCounter().get());
}
```

考虑到多个调度器，最好将一个调度器保留在它自己的类中，这样我们就可以根据需要停止特定的调度器。

### 3.2 取消Future

另一种停止调度程序的方法是手动取消其Future。

下面是用于捕获Future Map的自定义任务调度程序：

```java
@Bean
public TaskScheduler poolScheduler() {
    return new CustomTaskScheduler();
}

private class CustomTaskScheduler extends ThreadPoolTaskScheduler {

    //

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long period) {
        ScheduledFuture<?> future = super.scheduleAtFixedRate(task, period);

        ScheduledMethodRunnable runnable = (ScheduledMethodRunnable) task;
        scheduledTasks.put(runnable.getTarget(), future);

        return future;
    }
}
```

然后我们迭代Future Map并为我们的批处理作业调度程序取消Future：

```java
public void cancelFutureSchedulerTasks() {
    scheduledTasks.forEach((k, v) -> {
        if (k instanceof SpringBatchScheduler) {
            v.cancel(false);
        }
    });
}
```

在有多个调度器任务的情况下，我们可以在自定义调度器池中维护Future Map，但根据调度器类取消相应的已调度Future。

## 4. 总结

在这篇快速文章中，我们尝试了三种不同的方式来触发或停止计划的Spring Batch作业。

当我们需要重新启动批处理作业时，使用条件标志来管理作业运行将是一种灵活的解决方案。否则，我们可以按照其他两个选项完全停止调度程序。
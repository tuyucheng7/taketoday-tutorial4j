## 1. 概述

在本文中，我们将介绍Spring任务调度机制-TaskScheduler以及它的预构建实现以及要使用的不同触发器。
如果你想了解更多关于Spring任务调度的信息，请查看[@Async]()和[@Scheduled](Spring_@Scheduled_Annotation.md)文章。

TaskScheduler是在Spring 3.0中引入的，有多种方法可以在未来的某个时间运行，
它还返回一个ScheduledFuture接口的表示对象，可以用来取消计划任务或检查它是否完成。

我们需要做的就是选择一个可运行的任务进行调度，然后选择一个合适的调度策略。

## 2. ThreadPoolTaskScheduler

ThreadPoolTaskScheduler非常适合内部线程管理，
因为它将任务委托给ScheduledExecutorService并实现了TaskExecutor接口-因此它的单个实例能够处理异步潜在执行以及@Scheduled注解。

现在让我们在ThreadPoolTaskSchedulerConfig中定义ThreadPoolTaskScheduler bean：


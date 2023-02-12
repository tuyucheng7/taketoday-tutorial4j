## 1. 简介

在多线程环境中，有时我们需要根据自定义条件而不仅仅是创建时间来安排任务。

让我们看看如何在Java中实现这一点-使用PriorityBlockingQueue。

## 2. 概述

假设我们想要根据其优先级执行作业：

```java
@Slf4j
public class Job implements Runnable {
    private final String jobName;
    private final JobPriority jobPriority;

    public Job(String jobName, JobPriority jobPriority) {
        this.jobName = jobName;
        this.jobPriority = jobPriority != null ? jobPriority : JobPriority.MEDIUM;
    }

    public JobPriority getJobPriority() {
        return jobPriority;
    }

    @Override
    public void run() {
        try {
            log.debug("Job:{} Priority:{}", jobName, jobPriority);
            Thread.sleep(1000); // to simulate actual execution time
        } catch (InterruptedException ignored) {
        }
    }
}
```

出于演示目的，我们在run()方法中只是打印任务名称和优先级。

我们还添加了sleep()调用以便模拟一个运行时间更长的任务；当任务正在执行时，优先级队列中会累积更多任务。

最后，JobPriority是一个简单的枚举：

```java
public enum JobPriority {
    HIGH, MEDIUM, LOW
}
```

## 3. 自定义比较器

我们需要编写一个比较器来定义我们的自定义标准；[在Java 8中，它很简单](https://www.baeldung.com/java-8-comparator-comparing)：

```java
Comparator.comparing(Job::getJobPriority);
```

## 4. 优先级任务调度器

完成所有设置后，现在让我们实现一个简单的任务调度器-它使用一个单线程执行器在PriorityBlockingQueue中查找任务并执行它们：

```java
public class PriorityJobScheduler {
    private final ExecutorService priorityJobPoolExecutor;
    private final ExecutorService priorityJobScheduler = Executors.newSingleThreadExecutor();
    private final PriorityBlockingQueue<Job> priorityQueue;

    public PriorityJobScheduler(Integer poolSize, Integer queueSize) {
        priorityJobPoolExecutor = Executors.newFixedThreadPool(poolSize);
        priorityQueue = new PriorityBlockingQueue<>(queueSize, Comparator.comparing(Job::jobPriority));

        priorityJobScheduler.execute(() -> {
            while (true) {
                try {
                    priorityJobPoolExecutor.execute(priorityQueue.take());
                } catch (InterruptedException e) {
                    // exception needs special handling
                    break;
                }
            }
        });
    }

    public void scheduleJob(Job job) {
        priorityQueue.add(job);
    }
}
```

**这里的关键是使用自定义比较器创建Job类型的PriorityBlockingQueue实例**。下一个要执行的作业是使用take()方法从队列中选取的，该方法检索并删除队列的头元素。

客户端代码现在只需要调用scheduleJob()-它将任务添加到队列中。priorityQueue.add()使用JobExecutionComparator将任务与队列中的现有任务相比较，并放置在队列中的适当位置。

请注意，实际任务是使用带有专用线程池的单独ExecutorService执行的。

## 5. 演示

最后，下面是调度程序的快速演示：

```java
class PriorityJobSchedulerUnitTest {
    private static final int POOL_SIZE = 1;
    private static final int QUEUE_SIZE = 10;

    @Test
    void whenMultiplePriorityJobsQueued_thenHighestPriorityJobIsPicked() {
        Job job1 = new Job("Job1", JobPriority.LOW);
        Job job2 = new Job("Job2", JobPriority.MEDIUM);
        Job job3 = new Job("Job3", JobPriority.HIGH);
        Job job4 = new Job("Job4", JobPriority.MEDIUM);
        Job job5 = new Job("Job5", JobPriority.LOW);
        Job job6 = new Job("Job6", JobPriority.HIGH);

        PriorityJobScheduler pjs = new PriorityJobScheduler(POOL_SIZE, QUEUE_SIZE);

        pjs.scheduleJob(job1);
        pjs.scheduleJob(job2);
        pjs.scheduleJob(job3);
        pjs.scheduleJob(job4);
        pjs.scheduleJob(job5);
        pjs.scheduleJob(job6);

        while (pjs.getQueuedTaskCount() != 0) ;

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        pjs.closeScheduler();
    }
}
```

为了演示任务是按优先级顺序执行的，我们将POOL_SIZE保持为1，并且QUEUE_SIZE为10。我们为调度程序提供具有不同优先级的作业。

这是我们为其中一次运行获得的示例输出：

```shell
18:48:19.766 [pool-2-thread-1] DEBUG [c.t.t.c.prioritytaskexecution.Job] >>> Job:Job3 Priority:HIGH 
18:48:20.785 [pool-2-thread-1] DEBUG [c.t.t.c.prioritytaskexecution.Job] >>> Job:Job6 Priority:HIGH 
18:48:21.796 [pool-2-thread-1] DEBUG [c.t.t.c.prioritytaskexecution.Job] >>> Job:Job4 Priority:MEDIUM 
18:48:22.803 [pool-2-thread-1] DEBUG [c.t.t.c.prioritytaskexecution.Job] >>> Job:Job2 Priority:MEDIUM 
18:48:23.811 [pool-2-thread-1] DEBUG [c.t.t.c.prioritytaskexecution.Job] >>> Job:Job1 Priority:LOW 
18:48:24.820 [pool-2-thread-1] DEBUG [c.t.t.c.prioritytaskexecution.Job] >>> Job:Job5 Priority:LOW
```

在每次运行中，输出结果可能会有所不同。但是输出的优先级一定是顺序的。

## 6. 总结

在这个快速教程中，我们了解了如何使用PriorityBlockingQueue以自定义优先级顺序执行任务。
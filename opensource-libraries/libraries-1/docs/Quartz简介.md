## 1. 概述

[Quartz](http://www.quartz-scheduler.org/) 是一个开源的作业调度框架，完全用Java编写，设计用于J2SE和J2EE应用程序。它在不牺牲简单性的情况下提供了极大的灵活性。

可以创建复杂的计划来执行任何作业。例如，每天、每隔一个星期五晚上 7:30 或仅在每个月的最后一天运行的任务。

在本文中，我们将了解使用 Quartz API 构建作业的元素。结合Spring的介绍，推荐[Scheduling in Spring with Quartz](https://www.baeldung.com/spring-quartz-schedule)。

## 2.Maven依赖

我们需要在pom.xml 中添加以下依赖项：

```xml
<dependency>
    <groupId>org.quartz-scheduler</groupId>
    <artifactId>quartz</artifactId>
    <version>2.3.0</version>
</dependency>
```

最新版本可以在[Maven 中央存储库](https://search.maven.org/classic/#search|gav|1|g%3A"org.quartz-scheduler" AND a%3A"quartz")中找到。

## 3.石英API

框架的核心是Scheduler。它负责管理我们应用程序的运行时环境。

为了确保可扩展性，Quartz 基于多线程架构。启动时，框架会初始化一组工作线程，调度程序使用这些线程来执行作业。

这就是框架可以同时运行许多作业的方式。它还依赖于一组松耦合的ThreadPool管理组件来管理线程环境。

API的关键接口是：

-   Scheduler——与框架的调度器交互的主要API
-   作业——一个由我们希望执行的组件实现的接口
-   JobDetail——用于定义Job的实例
-   触发器——确定执行给定作业的时间表的组件
-   JobBuilder——用于构建JobDetail实例，它定义了Jobs的实例
-   TriggerBuilder –用于构建Trigger实例

让我们来看看这些组件中的每一个。

## 4.调度器

在我们可以使用Scheduler之前，它需要被实例化。为此，我们可以使用工厂SchedulerFactory ：

```java
SchedulerFactory schedulerFactory = new StdSchedulerFactory();
Scheduler scheduler = schedulerFactory.getScheduler();
```

Scheduler的生命周期受其创建的限制，通过SchedulerFactory和对其shutdown()方法的调用。创建后，Scheduler接口可用于添加、删除和列出Jobs和Triggers，并执行其他与调度相关的操作(例如暂停触发器)。

但是，在使用start()方法启动之前，调度程序不会对任何触发器执行操作：

```java
scheduler.start();
```

## 5. 工作

Job是实现Job接口的类。它只有一个简单的方法：

```java
public class SimpleJob implements Job {
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        System.out.println("This is a quartz job!");
    }
}
```

当作业的触发器触发时，execute()方法会被调度程序的工作线程之一调用。

传递给此方法的JobExecutionContext对象提供作业实例，以及有关其运行时环境的信息、执行它的调度程序的句柄、触发执行的触发器的句柄、作业的JobDetail对象和一些其他项目.

JobDetail对象是在将Job添加到Scheduler时由 Quartz 客户端创建的。它本质上是作业实例的定义：

```java
JobDetail job = JobBuilder.newJob(SimpleJob.class)
  .withIdentity("myJob", "group1")
  .build();
```

该对象还可能包含Job的各种属性设置，以及JobDataMap，它可用于存储我们作业类的给定实例的状态信息。

### 5.1. 作业数据图

JobDataMap用于保存我们希望在作业实例执行时提供给它的任意数量的数据对象。JobDataMap是JavaMap接口的实现，并添加了一些方便的方法来存储和检索原始类型的数据。

这是在将作业添加到调度程序之前在构建JobDetail时将数据放入JobDataMap的示例：

```java
JobDetail job = newJob(SimpleJob.class)
  .withIdentity("myJob", "group1")
  .usingJobData("jobSays", "Hello World!")
  .usingJobData("myFloatValue", 3.141f)
  .build();
```

以下是如何在作业执行期间访问这些数据的示例：

```java
public class SimpleJob implements Job { 
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();

        String jobSays = dataMap.getString("jobSays");
        float myFloatValue = dataMap.getFloat("myFloatValue");

        System.out.println("Job says: " + jobSays + ", and val is: " + myFloatValue);
    } 
}
```

上面的示例将打印“Job says Hello World!, and val is 3.141”。

我们还可以将 setter 方法添加到与JobDataMap中的键名称相对应的作业类。

如果我们这样做，Quartz 的默认JobFactory实现会在作业实例化时自动调用这些 setter，从而避免需要在我们的 execute 方法中显式地从映射中获取值。

## 6.触发器

触发器对象用于触发Jobs的执行。

当我们希望调度一个Job时，我们需要实例化一个触发器并调整它的属性来配置我们的调度要求：

```java
Trigger trigger = TriggerBuilder.newTrigger()
  .withIdentity("myTrigger", "group1")
  .startNow()
  .withSchedule(SimpleScheduleBuilder.simpleSchedule()
    .withIntervalInSeconds(40)
    .repeatForever())
  .build();
```

Trigger也可能有一个与之关联的JobDataMap 。这对于将参数传递给特定于触发器执行的作业很有用。

针对不同的调度需求，有不同类型的触发器。每个人都有不同的TriggerKey属性来跟踪他们的身份。但是，其他一些属性对于所有触发器类型都是通用的：

-   jobKey属性指示触发器触发时应执行的作业的标识。
-   startTime属性指示触发器的计划何时首次生效。该值是一个java.util.Date对象，它定义给定日历日期的时刻。对于某些触发器类型，触发器会在给定的开始时间触发。对于其他人，它只是标记时间表应该开始的时间。
-   endTime属性指示何时应取消触发器的计划。

Quartz 附带了一些不同的触发器类型，但最常用的是SimpleTrigger和CronTrigger。

### 6.1. 优先

有时，当我们有很多触发器时，Quartz 可能没有足够的资源来立即触发所有计划同时触发的作业。在这种情况下，我们可能想要控制我们的哪些触发器首先可用。这正是触发器的优先级属性的用途。

例如，当同时触发十个触发器时，只有四个工作线程可用，则优先级最高的前四个触发器将首先执行。当我们没有为触发器设置优先级时，它使用默认优先级 5。任何整数值都可以作为优先级，无论是正数还是负数。

在下面的示例中，我们有两个具有不同优先级的触发器。如果没有足够的资源同时触发所有触发器，则triggerA将是第一个被触发的：

```java
Trigger triggerA = TriggerBuilder.newTrigger()
  .withIdentity("triggerA", "group1")
  .startNow()
  .withPriority(15)
  .withSchedule(SimpleScheduleBuilder.simpleSchedule()
    .withIntervalInSeconds(40)
    .repeatForever())
  .build();
            
Trigger triggerB = TriggerBuilder.newTrigger()
  .withIdentity("triggerB", "group1")
  .startNow()
  .withPriority(10)
  .withSchedule(SimpleScheduleBuilder.simpleSchedule()
    .withIntervalInSeconds(20)
    .repeatForever())
  .build();
```

### 6.2. 失火说明

如果持久触发器由于Scheduler被关闭而错过了它的触发时间，或者在 Quartz 的线程池中没有可用线程的情况下，就会发生失火。

不同的触发器类型有不同的失火指令可用。默认情况下，他们使用智能策略指令。当调度程序启动时，它会搜索任何未触发的持久触发器。之后，它会根据它们各自配置的失火指令更新它们中的每一个。

让我们看看下面的例子：

```java
Trigger misFiredTriggerA = TriggerBuilder.newTrigger()
  .startAt(DateUtils.addSeconds(new Date(), -10))
  .build();
            
Trigger misFiredTriggerB = TriggerBuilder.newTrigger()
  .startAt(DateUtils.addSeconds(new Date(), -10))
  .withSchedule(SimpleScheduleBuilder.simpleSchedule()
    .withMisfireHandlingInstructionFireNow())
  .build();
```

我们已将触发器安排在 10 秒前运行(因此它在创建时晚了 10 秒)以模拟失火，例如因为调度程序已关闭或没有足够数量的可用工作线程。当然，在现实世界中，我们永远不会这样安排触发器。

在第一个触发器 ( misFiredTriggerA ) 中没有设置失火处理指令。因此，在这种情况下使用称为智能策略，称为： withMisfireHandlingInstructionFireNow()。这意味着作业会在调度程序发现失火后立即执行。

第二个触发器明确定义了发生失火时我们期望的行为类型。在此示例中，它恰好是相同的智能策略。

### 6.3. 简单触发器

SimpleTrigger用于我们需要在特定时刻执行作业的场景。这可以恰好一次或以特定时间间隔重复。

一个例子可能是在 2018 年 1 月 13 日凌晨 12:20:00 恰好触发作业执行。同样，我们可以从那个时间开始，然后每十秒再执行五次。

在下面的代码中，日期myStartTime之前已定义并用于为一个特定时间戳构建触发器：

```java
SimpleTrigger trigger = (SimpleTrigger) TriggerBuilder.newTrigger()
  .withIdentity("trigger1", "group1")
  .startAt(myStartTime)
  .forJob("job1", "group1")
  .build();
```

接下来，让我们为特定时刻构建一个触发器，然后每十秒重复十次：

```java
SimpleTrigger trigger = (SimpleTrigger) TriggerBuilder.newTrigger()
  .withIdentity("trigger2", "group1")
  .startAt(myStartTime)
  .withSchedule(simpleSchedule()
    .withIntervalInSeconds(10)
    .withRepeatCount(10))
  .forJob("job1") 
  .build();
```

### 6.4. 定时触发器

当我们需要基于类似日历的语句的计划时，将使用CronTrigger 。例如，我们可以指定触发时间表，例如每周五中午或每个工作日上午 9:30。

Cron-Expressions 用于配置CronTrigger的实例。这些表达式由由七个子表达式组成的字符串组成。[我们可以在这里](https://docs.oracle.com/cd/E12058_01/doc/doc.1014/e12030/cron_expressions.htm)阅读更多关于 Cron-Expressions 的信息。

在下面的示例中，我们构建了一个触发器，每天上午 8 点到下午 5 点之间每隔一分钟触发一次：

```java
CronTrigger trigger = TriggerBuilder.newTrigger()
  .withIdentity("trigger3", "group1")
  .withSchedule(CronScheduleBuilder.cronSchedule("0 0/2 8-17   ?"))
  .forJob("myJob", "group1")
  .build();
```

## 七. 总结

在本文中，我们展示了如何构建一个Scheduler来触发一个Job。我们还看到了一些最常用的触发器选项：SimpleTrigger和CronTrigger。

Quartz 可用于创建简单或复杂的计划来执行数十个、数百个甚至更多作业。有关该框架的更多信息可以在主[网站](http://www.quartz-scheduler.org/)上找到。
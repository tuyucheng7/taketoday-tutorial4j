## 1. 概述

在本教程中，我们将使用 Quartz 在 Spring 中构建一个简单的调度程序。

我们将从一个简单的目标开始，即轻松配置新的预定作业。

### 1.1. Quartz API 的关键组件

Quartz 具有模块化架构。它由几个我们可以根据需要组合的基本组件组成。在本教程中，我们将重点关注每个作业共有的那些：Job、JobDetail、Trigger和Scheduler。

虽然我们将使用 Spring 来管理应用程序，但每个单独的组件都可以通过两种方式进行配置：Quartz方式或Spring方式(使用其便利类)。

为了完整起见，我们将尽可能涵盖这两个选项，但我们可能会采用其中一个。现在让我们开始构建，一次构建一个组件。

## 延伸阅读：

## [Spring 任务调度器指南](https://www.baeldung.com/spring-task-scheduler)

使用 Task Scheduler 在 Spring 中进行调度的快速实用指南

[阅读更多](https://www.baeldung.com/spring-task-scheduler)→

## [Jakarta EE 中的调度](https://www.baeldung.com/scheduling-in-java-enterprise-edition)

演示如何使用 @Schedule 注解和计时器服务在 Jakarta EE 中安排任务。

[阅读更多](https://www.baeldung.com/scheduling-in-java-enterprise-edition)→

## [流口水简介](https://www.baeldung.com/drools)

了解如何将 Drools 用作业务规则管理系统 (BRMS)。

[阅读更多](https://www.baeldung.com/drools)→

## 2.Job和JobDetail _

### 2.1. 工作

API 提供了一个Job接口，它只有一个方法，execute。它必须由包含要完成的实际工作(即任务)的类来实现。当作业的触发器触发时，调度程序调用execute方法，并向其传递一个JobExecutionContext对象。

JobExecutionContext为作业实例提供有关其运行时环境的信息，包括调度程序的句柄、触发器的句柄和作业的JobDetail对象。

在这个快速示例中，作业将任务委托给服务类：


```java
@Component
public class SampleJob implements Job {

    @Autowired
    private SampleJobService jobService;

    public void execute(JobExecutionContext context) throws JobExecutionException {
        jobService.executeSampleJob();
    }
}

```

### 2.2. 职位详情

虽然作业是主力，但 Quartz 不存储作业类的实际实例。相反，我们可以使用JobDetail类定义Job的实例。作业的类必须提供给JobDetail，以便它知道要执行的作业的类型。

### 2.3. 石英JobBuilder

Quartz JobBuilder为构建JobDetail实体提供了构建器风格的 API ：

```java
@Bean
public JobDetail jobDetail() {
    return JobBuilder.newJob().ofType(SampleJob.class)
      .storeDurably()
      .withIdentity("Qrtz_Job_Detail")  
      .withDescription("Invoke Sample Job service...")
      .build();
}
```

### 2.4. Spring JobDetailFactoryBean

Spring 的JobDetailFactoryBean提供了用于配置JobDetail实例的bean 样式用法。如果没有另外指定，它使用 Spring bean 名称作为作业名称：

```java
@Bean
public JobDetailFactoryBean jobDetail() {
    JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
    jobDetailFactory.setJobClass(SampleJob.class);
    jobDetailFactory.setDescription("Invoke Sample Job service...");
    jobDetailFactory.setDurability(true);
    return jobDetailFactory;
}
```

作业的每次执行都会创建一个新的JobDetail实例。JobDetail对象传达作业的详细属性。执行完成后，将删除对该实例的引用。

## 3.触发

触发器是调度作业的机制，即 触发器实例“触发”作业的执行。Job(任务的概念)和Trigger(调度机制)之间有明确的职责分离。

除了Job之外，trigger 还需要一个type，我们可以根据调度需求来选择。

假设我们想无限期地安排我们的任务每小时执行一次，那么我们可以使用 Quartz 的TriggerBuilder或 Spring 的SimpleTriggerFactoryBean来实现。

### 3.1. 石英触发器生成器

TriggerBuilder是用于构建Trigger实体的构建器风格的 API：

```java
@Bean
public Trigger trigger(JobDetail job) {
    return TriggerBuilder.newTrigger().forJob(job)
      .withIdentity("Qrtz_Trigger")
      .withDescription("Sample trigger")
      .withSchedule(simpleSchedule().repeatForever().withIntervalInHours(1))
      .build();
}
```

### 3.2. Spring SimpleTriggerFactoryBean

SimpleTriggerFactoryBean提供用于配置SimpleTrigger的 bean 样式用法。它使用 Spring bean 名称作为触发器名称，如果没有另外指定，则默认为无限重复：

```java
@Bean
public SimpleTriggerFactoryBean trigger(JobDetail job) {
    SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
    trigger.setJobDetail(job);
    trigger.setRepeatInterval(3600000);
    trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
    return trigger;
}
```

## 4.配置JobStore

JobStore提供了Job和Trigger 的存储机制。它还负责维护与作业调度程序相关的所有数据。API 支持内存中存储和持久存储。

### 4.1. 内存中的JobStore

对于我们的示例，我们将使用内存中的RAMJobStore，它通过quartz.properties提供超快的性能和简单的配置：

```plaintext
org.quartz.jobStore.class=org.quartz.simpl.RAMJobStore
```

RAMJobStore的明显缺点是它本质上是易变的。所有的调度信息在关闭之间丢失。如果我们需要在关闭之间保留作业定义和计划，我们可以改用持久性JDBCJobStore。

要在 Spring中启用内存中的JobStore ，我们将在application.properties中设置此属性：

```java
spring.quartz.job-store-type=memory
```

### 4.2. JDBC作业库

JDBCJobStore有两种类型：JobStoreTX和JobStoreCMT。它们都做同样的工作，将调度信息存储在数据库中。

两者之间的区别在于它们如何管理提交数据的事务。JobStoreCMT类型需要一个应用程序事务来存储数据，而JobStoreTX类型启动并管理它自己的事务。

有几个属性可以为JDBCJobStore设置。至少，我们必须指定JDBCJobStore的类型、数据源和数据库驱动程序类。大多数数据库都有驱动程序类，但StdJDBCDelegate涵盖了大多数情况：

```java
org.quartz.jobStore.class=org.quartz.impl.jdbcjobstore.JobStoreTX
org.quartz.jobStore.driverDelegateClass=org.quartz.impl.jdbcjobstore.StdJDBCDelegate
org.quartz.jobStore.dataSource=quartzDataSource
```

在 Spring 中设置 JDBC JobStore需要几个步骤。首先，我们将在application.properties中设置商店类型：

```java
spring.quartz.job-store-type=jdbc
```

然后我们需要启用自动配置并为 Spring 提供 Quartz 调度程序所需的数据源。@QuartzDataSource注解为我们完成了配置和初始化 Quartz 数据库的艰苦工作：

```java
@Configuration
@EnableAutoConfiguration
public class SpringQrtzScheduler {

    @Bean
    @QuartzDataSource
    public DataSource quartzDataSource() {
        return DataSourceBuilder.create().build();
    }
}
```

## 5.调度器

Scheduler接口是用于与作业调度程序交互的主要 API 。

Scheduler可以用SchedulerFactory 实例化。创建后，我们可以向其注册Job和Trigger。最初，调度器处于“待机”模式，我们必须调用它的启动方法来启动触发作业执行的线程。

### 5.1. Quartz StdSchedulerFactory

通过简单地调用StdSchedulerFactory上的getScheduler方法，我们可以实例化Scheduler，初始化它(使用配置的JobStore和ThreadPool)，并返回其 API 的句柄：

```java
@Bean
public Scheduler scheduler(Trigger trigger, JobDetail job, SchedulerFactoryBean factory) 
  throws SchedulerException {
    Scheduler scheduler = factory.getScheduler();
    scheduler.scheduleJob(job, trigger);
    scheduler.start();
    return scheduler;
}
```

### 5.2. Spring SchedulerFactoryBean

Spring 的SchedulerFactoryBean提供了 bean 样式的用法，用于配置Scheduler、在应用程序上下文中管理其生命周期，以及将Scheduler作为依赖注入的 bean 公开：

```java
@Bean
public SchedulerFactoryBean scheduler(Trigger trigger, JobDetail job, DataSource quartzDataSource) {
    SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
    schedulerFactory.setConfigLocation(new ClassPathResource("quartz.properties"));

    schedulerFactory.setJobFactory(springBeanJobFactory());
    schedulerFactory.setJobDetails(job);
    schedulerFactory.setTriggers(trigger);
    schedulerFactory.setDataSource(quartzDataSource);
    return schedulerFactory;
}
```

### 5.3. 配置SpringBeanJobFactory

SpringBeanJobFactory支持在创建实例时将调度程序上下文、作业数据映射和触发器数据条目作为属性注入到作业 bean 中。

但是，它不支持从应用程序上下文注入 bean 引用。感谢[这篇博](http://www.btmatthews.com/blog/2011/inject-application-context+dependencies-in-quartz-job-beans.html)文的作者，我们可以为SpringBeanJobFactory 添加自动装配支持：

```java
@Bean
public SpringBeanJobFactory springBeanJobFactory() {
    AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();
    jobFactory.setApplicationContext(applicationContext);
    return jobFactory;
}
```

## 六. 总结

在本文中，我们使用 Quartz API 以及 Spring 的便利类构建了我们的第一个基本调度程序。

关键要点是，我们只需几行代码就可以配置作业，而无需使用任何基于 XML 的配置。

[此 github 项目](https://github.com/eugenp/tutorials/tree/master/spring-quartz)中提供了该示例的完整源代码。这是一个 Maven 项目，所以我们可以导入它并按原样运行它。默认设置使用 Spring 的便利类，但我们可以使用运行时参数轻松地将其切换为 Quartz API(请参阅存储库中的 README.md)。
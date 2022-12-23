## 1. 概述

在本教程中，我们将研究使用JobRunr在Java中的分布式后台作业调度和处理，并将其与Spring集成。

## 2. 关于JobRunr

[JobRunr](https://www.jobrunr.io/en/)**是一个我们可以嵌入到我们的应用程序中的库，它允许我们使用Java 8 lambda来调度后台作业**。我们可以使用Spring服务的任何现有方法来创建作业，而无需实现接口。一个作业可以是一个短期或长期运行的进程，它会自动卸载到后台线程，这样当前的网络请求就不会被阻塞。

为了完成它的工作，JobRunr分析了Java 8 lambda，它将其序列化为JSON，并将其存储到关系型数据库或NoSQL数据存储中。

## 3. JobRunr功能

如果我们发现我们产生了太多的后台作业并且我们的服务器无法处理负载，我们可以通过添加额外的应用程序实例轻松地进行水平扩展，JobRunr将自动分担负载并将所有作业分布到我们应用程序的不同实例上。

**它还包含一个自动重试功能，该功能具有针对失败作业的指数退避策略**。还有一个**内置的仪表板**，使我们能够监控所有作业。JobRunr是自我维护的-**成功的作业将在一段可配置的时间后自动删除**，因此无需执行手动存储清理。

## 4. 设置

为了简单起见，我们将使用内存数据存储来存储所有与作业相关的信息。

### 4.1 Maven配置

在此之前，我们需要在pom.xml文件中声明以下[Maven依赖项](https://search.maven.org/search?q=g:org.jobrunr AND a:jobrunr-spring-boot-starter)：

```xml
<dependency>
    <groupId>org.jobrunr</groupId>
    <artifactId>jobrunr-spring-boot-starter</artifactId>
    <version>3.1.2</version>
</dependency>
```

### 4.2 Spring集成

在我们直接介绍如何创建后台作业之前，我们需要初始化JobRunr，当我们使用jobrunr-spring-boot-starter依赖项时，这很容易实现，我们只需要在application.properties中添加一些属性：

```properties
org.jobrunr.background-job-server.enabled=true
org.jobrunr.dashboard.enabled=true
```

第一个属性告诉JobRunr我们要启动一个负责处理作业的BackgroundJobServer实例；第二个属性告诉JobRunr启动嵌入式仪表板。

默认情况下，jobrunr-spring-boot-starter将尝试在关系型数据库的情况下使用你现有的DataSource来存储所有与作业相关的信息。

但是，由于我们将使用内存中的数据存储，因此我们需要提供一个StorageProvider bean：

```java
@Bean
public StorageProvider storageProvider(JobMapper jobMapper) {
    InMemoryStorageProvider storageProvider = new InMemoryStorageProvider();
    storageProvider.setJobMapper(jobMapper);
    return storageProvider;
}
```

## 5. 用法

现在，让我们了解如何使用JobRunr在Spring中创建和调度后台作业。

### 5.1 注入依赖

当我们想要创建作业时，我们需要注入[JobScheduler](https://www.javadoc.io/doc/org.jobrunr/jobrunr/latest/org/jobrunr/scheduling/JobScheduler.html)和我们现有的Spring服务，其中包含我们要为其创建作业的方法，在本例中为SampleJobService：

```java
@Inject
private JobScheduler jobScheduler;

@Inject
private SampleJobService sampleJobService;
```

来自JobRunr的JobScheduler类允许我们对新的后台作业进行排队或调度。

SampleJobService可以是我们现有的任何Spring服务，其中包含可能需要很长时间才能在Web请求中处理的方法。它也可以是调用一些其他外部服务的方法，我们希望在其中添加弹性，因为如果发生异常，JobRunr将重试该方法。

### 5.2 创建即发即弃的作业

现在我们有了依赖项，我们可以使用enqueue方法创建即发即弃的作业：

```java
jobScheduler.enqueue(() -> sampleJobService.executeSampleJob());
```

作业可以有参数，就像任何其他lambda一样：

```java
jobScheduler.enqueue(() -> sampleJobService.executeSampleJob("some string"));
```

这一行确保将lambda(包括类型、方法和参数)序列化为JSON到持久存储(RDBMS，如Oracle、Postgres、MySql和MariaDB或NoSQL数据库)。

然后，在所有不同的BackgroundJobServer中运行的专用工作线程池将以先进先出的方式尽快执行这些排队的后台作业，**JobRunr通过乐观锁定的方式保证由单个worker执行你的作业**。

### 5.3 安排未来的作业

我们还可以使用schedule方法安排未来的作业：

```java
jobScheduler.schedule(LocalDateTime.now().plusHours(5), () -> sampleJobService.executeSampleJob());
```

### 5.4 定期安排作业

如果我们想要经常性的作业，我们需要使用scheduleRecurrently方法：

```java
jobScheduler.scheduleRecurrently(Cron.hourly(), () -> sampleJobService.executeSampleJob());
```

### 5.5 使用@Job注解进行标注

为了控制作业的各个方面，我们可以使用@Job注解来标注我们的服务方法，这允许在仪表板中设置显示名称并配置作业失败时的重试次数。

```java
@Job(name = "The sample job with variable %0", retries = 2)
public void executeSampleJob(String variable) {
    // ...
}
```

我们甚至可以使用通过String.format()语法在显示名称中传递给我们的作业的变量。

如果我们有非常具体的用例，我们只想在出现特定异常时重试特定作业，我们可以编写自己的[ElectStateFilter](https://www.javadoc.io/doc/org.jobrunr/jobrunr/latest/org/jobrunr/jobs/filters/ElectStateFilter.html)，我们可以在其中访问作业并完全控制如何进行。

## 6. 仪表板

JobRunr带有一个内置的仪表板，可以让我们监控我们的作业。我们可以在[http://localhost:8000](http://localhost:8000/)找到它并检查所有作业，包括所有重复作业和估计处理完所有排队作业所需的时间：

<img src="../assets/img.png">

可能会发生不好的事情，例如，SSL证书已过期或磁盘已满。默认情况下，JobRunr将使用指数退避策略重新安排后台作业，如果后台作业连续失败十次，才会进入Failed状态。然后，你可以决定在解决根本原因后重新排队失败的作业。

所有这些都在仪表板中可见，包括每次重试时都带有确切的错误消息和作业失败原因的完整堆栈跟踪：

<img src="../assets/img_1.png">

## 7. 总结

在本文中，我们使用JobRunr和jobrunr-spring-boot-starter构建了我们的第一个基本调度程序，本教程的关键要点是，我们能够仅使用一行代码创建作业，而无需任何基于XML的配置或需要实现接口。
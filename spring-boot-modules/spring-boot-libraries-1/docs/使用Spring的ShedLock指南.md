## 1. 概述

Spring提供了一种简单的方法来实现用于调度作业的API，在我们部署应用程序的多个实例之前，它工作得很好。

默认情况下，Spring无法处理多个实例上的调度程序同步。相反，它在每个节点上同时执行作业。

在这个简短的教程中，我们将介绍ShedLock-一个Java库，它确保我们的计划任务只在同一时间运行一次，**并且是[Quartz]()的替代方案**。

## 2. Maven依赖

要将ShedLock与Spring一起使用，我们需要添加[shedlock-spring](https://search.maven.org/search?q=g:net.javacrumbs.shedlock AND a:shedlock-spring&core=gav)依赖项： 

```xml
<dependency>
    <groupId>net.javacrumbs.shedlock</groupId>
    <artifactId>shedlock-spring</artifactId>
    <version>2.2.0</version>
</dependency>
```

## 3. 配置

**请注意，通过声明适当的LockProvider，ShedLock仅适用于具有共享数据库的环境**。它在数据库中创建一个表或文档，在其中存储有关当前锁的信息。

目前，ShedLock支持Mongo、Redis、Hazelcast、ZooKeeper以及任何带有JDBC驱动程序的东西。

对于此示例，**我们将使用内存中的H2数据库**。

为了使其工作，我们需要提供[H2数据库](https://search.maven.org/search?q=g:com.h2database AND a:h2)和[ShedLock JDBC](https://search.maven.org/search?q=g:net.javacrumbs.shedlock AND a:shedlock-provider-jdbc-template&core=gav)依赖项：

```xml
<dependency>
    <groupId>net.javacrumbs.shedlock</groupId>
    <artifactId>shedlock-provider-jdbc-template</artifactId>
    <version>2.1.0</version>
</dependency>
<dependency>
     <groupId>com.h2database</groupId>
     <artifactId>h2</artifactId>
     <version>1.4.200</version>
</dependency>
```

接下来，我们需要为ShedLock创建一个数据库表，来保存有关调度程序锁的信息：

```sql
CREATE TABLE shedlock
(
    name       VARCHAR(64),
    lock_until TIMESTAMP(3) NULL,
    locked_at  TIMESTAMP(3) NULL,
    locked_by  VARCHAR(255),
    PRIMARY KEY (name)
)
```

我们应该在Spring Boot应用程序的属性文件中声明数据源，以便DataSource bean可以自动装配。

这里我们使用application.yml来定义H2数据库的数据源：

```yaml
spring:
    datasource:
        driverClassName: org.h2.Driver
        url: jdbc:h2:mem:shedlock_DB;INIT=CREATE SCHEMA IF NOT EXISTS shedlock;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
        username: sa
        password:
```

让我们使用上面的数据源配置来配置LockProvider。

Spring可以让它变得非常简单：

```java
@Configuration
public class SchedulerConfiguration {
    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(dataSource);
    }
}
```

我们必须提供的其他配置要求是我们的Spring配置类上的@EnableScheduling和@EnableSchedulerLock注解：

```java
@SpringBootApplication
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT30S")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringApplication.class, args);
    }
}
```

defaultLockAtMostFor参数指定在执行节点死亡的情况下应保留锁的默认时间量，它使用[ISO8601持续时间](https://en.wikipedia.org/wiki/ISO_8601#Durations)格式。

在下一节中，我们将看到如何覆盖此默认设置。

## 4. 创建任务

要创建由ShedLock处理的计划任务，我们只需将@Scheduled和@SchedulerLock注解放在一个方法上：

```java
@Component
class TuyuchengTaskScheduler {

    @Scheduled(cron = "0 0/15 * * * ?")
    @SchedulerLock(
          name = "TaskScheduler_scheduledTask", 
          lockAtLeastForString = "PT5M", 
          lockAtMostForString = "PT14M")
    public void scheduledTask() {
        // ...
    }
}
```

首先，让我们看看@Scheduled，它支持[cron格式](https://crontab.guru/)，这个表达式的意思是“每15分钟一次”。

接下来，看看@SchedulerLock，name参数必须是唯一的，而ClassName_methodName通常足以实现这一点。我们不希望同时运行多次此方法，ShedLock使用唯一名称来实现这一点。

我们还添加了几个可选参数。

首先，我们添加了lockAtLeastForString，以便我们可以在方法调用之间留出一些距离。使用“PT5M”意味着此方法将至少持有锁五分钟，换句话说，**这意味着ShedLock运行此方法的频率不超过每五分钟一次**。

接下来，我们添加了lockAtMostForString来指定在执行节点死亡的情况下锁应该保留多长时间，使用“PT14M”表示锁定不超过14分钟。

在正常情况下，ShedLock会在任务完成后直接释放锁。现在，我们不必这样做，**因为@EnableSchedulerLock中提供了默认值**，但我们选择在此处覆盖它。

## 5. 总结

在本文中，我们学习了如何使用ShedLock创建和同步计划任务。
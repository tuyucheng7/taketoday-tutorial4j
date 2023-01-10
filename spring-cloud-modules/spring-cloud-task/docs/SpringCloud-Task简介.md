## 1. 概述

Spring Cloud Task 的目标是提供为Spring Boot应用程序创建短期微服务的功能。

在 Spring Cloud Task 中，我们可以灵活地动态运行任何任务，按需分配资源并在任务完成后检索结果。

Tasks 是 Spring Cloud Data Flow 中的一个新原语，允许用户将几乎任何Spring Boot应用程序作为短期任务执行。

## 2. 开发一个简单的任务应用程序

### 2.1. 添加相关依赖

首先，我们可以使用spring-cloud-task-dependencies 添加依赖管理部分：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-task-dependencies</artifactId>
            <version>2.2.3.RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

此依赖项管理通过导入范围管理依赖项的版本。

我们需要添加以下依赖项：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-task</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-task-core</artifactId>
</dependency>
```

[这](https://search.maven.org/classic/#search|ga|1|a%3A"spring-cloud-task-core")是spring-cloud-task-core的 Maven Central 的链接。

现在，要启动我们的Spring Boot应用程序，我们需要具有相关父项的spring-boot-starter 。

我们将使用 Spring Data JPA 作为 ORM 工具，因此我们还需要为其添加依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
    <version>2.6.1</version>
</dependency>
```

[此处](https://www.baeldung.com/spring-boot-start)提供了使用 Spring Data JPA 引导简单Spring Boot应用程序的详细信息。

我们可以在 Maven Central 上查看最新版本的spring [- ](https://search.maven.org/classic/#search|ga|1|a%3A"spring-boot-starter-parent")boot-starter- parent 。

### 2.2. @EnableTask注解_

要引导 Spring Cloud Task 的功能，我们需要添加@EnableTask注解：

```java
@SpringBootApplication
@EnableTask
public class TaskDemo {
    // ...
}

```

注解在图片中引入了SimpleTaskConfiguration类，该类又注册了TaskRepository及其基础结构。默认情况下，内存映射用于存储TaskRepository的状态。

TaskRepository的主要信息在TaskExecution类中建模。此类的显着字段是taskName、startTime、endTime、exitMessage。exitMessage存储退出时的可用信息。

如果退出是由应用程序的任何事件失败引起的，完整的异常堆栈跟踪将存储在这里。

Spring Boot 提供了一个接口ExitCodeExceptionMapper，它将未捕获的异常映射到允许仔细调试的退出代码。Cloud Task 将信息存储在数据源中以供将来分析。

### 2.3. 为TaskRepository配置数据源

一旦任务结束，用于存储TaskRepository的内存映射将消失，我们将丢失与任务事件相关的数据。为了存储在永久存储中，我们将使用 MySQL 作为 Spring Data JPA 的数据源。

数据源在application.yml文件中配置。要配置 Spring Cloud Task 以使用提供的数据源作为TaskRepository的存储，我们需要创建一个扩展DefaultTaskConfigurer的类。

现在，我们可以将配置的数据源作为构造函数参数发送给超类的构造函数：

```java
@Autowired
private DataSource dataSource;

public class HelloWorldTaskConfigurer extends DefaultTaskConfigurer{
    public HelloWorldTaskConfigurer(DataSource dataSource){
        super(dataSource);
    }
}
```

要使上述配置生效，我们需要使用@Autowired注解来注解DataSource的实例，并将该实例作为上面定义的HelloWorldTaskConfigurer bean的构造函数参数注入：

```java
@Bean
public HelloWorldTaskConfigurer getTaskConfigurer() {
    return new HelloWorldTaskConfigurer(dataSource);
}
```

这样就完成了将TaskRepository存储到 MySQL 数据库的配置。

### 2.4. 执行

在Spring Boot中，我们可以在应用程序完成启动之前执行任何任务。我们可以使用ApplicationRunner或CommandLineRunner接口来创建一个简单的任务。

我们需要实现这些接口的run方法，并将实现类声明为bean：

```java
@Component
public static class HelloWorldApplicationRunner 
  implements ApplicationRunner {
 
    @Override
    public void run(ApplicationArguments arg0) throws Exception {
        System.out.println("Hello World from Spring Cloud Task!");
    }
}
```

现在，如果我们运行我们的应用程序，我们应该让我们的任务产生必要的输出，并在我们的 MySQL 数据库中创建所需的表来记录任务的事件数据。

## 3. Spring Cloud 任务的生命周期

一开始，我们在TaskRepository中创建一个条目。这表明所有 bean 都已准备好在 Application 中使用，并且 Runner 接口的run方法已准备好执行。

在run方法执行完成或ApplicationContext事件发生任何失败时， TaskRepository将更新为另一个条目。

在任务生命周期中，我们可以从TaskExecutionListener接口注册可用的监听器。我们需要一个实现接口的类，该接口具有三种方法—— onTaskEnd、onTaksFailed和onTaskStartup在任务的相应事件中触发。

我们需要在我们的TaskDemo类中声明实现类的 bean：

```java
@Bean
public TaskListener taskListener() {
    return new TaskListener();
}
```

## 4. 与 Spring Batch 集成

我们可以将 Spring Batch Job 作为任务执行，并使用 Spring Cloud Task 记录作业执行的事件。要启用此功能，我们需要添加与 Boot 和 Cloud 有关的 Batch 依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-batch</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-task-batch</artifactId>
</dependency>
```

[这](https://search.maven.org/classic/#search|ga|1|spring-cloud-task-batch)是spring-cloud-task-batch的 Maven Central 的链接。

要将作业配置为任务，我们需要在JobConfiguration类中注册 Job bean：

```java
@Bean
public Job job2() {
    return jobBuilderFactory.get("job2")
      .start(stepBuilderFactory.get("job2step1")
      .tasklet(new Tasklet(){
          @Override
          public RepeatStatus execute(
            StepContribution contribution,
            ChunkContext chunkContext) throws Exception {
            System.out.println("This job is from Baeldung");
                return RepeatStatus.FINISHED;
          }
    }).build()).build();
}
```

我们需要用@EnableBatchProcessing注解来装饰TaskDemo类：

```java
//..Other Annotation..
@EnableBatchProcessing
public class TaskDemo {
    // ...
}
```

@EnableBatchProcessing注解启用 Spring Batch 功能，具有设置批处理作业所需的基本配置。

现在，如果我们运行应用程序，@EnableBatchProcessing注解将触发 Spring Batch Job 执行，Spring Cloud Task 将记录所有批处理作业的执行事件以及在springcloud数据库中执行的其他任务。

## 5. 从 Stream 启动任务

我们可以从 Spring Cloud Stream 触发任务。为了达到这个目的，我们有@EnableTaskLaucnher注解。一次，我们使用Spring Boot应用程序添加注解，一个 TaskSink 将可用：

```java
@SpringBootApplication
@EnableTaskLauncher
public class StreamTaskSinkApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaskSinkApplication.class, args);
    }
}
```

TaskSink从包含GenericMessage的流接收消息，该GenericMessage将 TaskLaunchRequest作为有效负载。然后它会触发任务启动请求中提供的基于坐标的任务。

要使TaskSink起作用，我们需要配置一个实现TaskLauncher接口的 bean 。出于测试目的，我们在这里模拟实现：

```java
@Bean
public TaskLauncher taskLauncher() {
    return mock(TaskLauncher.class);
}
```

这里需要注意的是，TaskLauncher接口只有在添加[spring-cloud-deployer-local](https://search.maven.org/classic/#search|ga|1|a%3A"spring-cloud-deployer-local")依赖后才能使用：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-deployer-local</artifactId>
    <version>2.3.1.RELEASE</version>
</dependency>
```

我们可以通过调用Sink接口的input来测试Task是否启动：

```java
public class StreamTaskSinkApplicationTests {
   
    @Autowired
    private Sink sink; 
    
    //
}
```

现在，我们创建一个TaskLaunchRequest实例并将其作为GenericMessage< TaskLaunchRequest >对象的有效负载发送。然后我们可以调用Sink的输入通道，将GenericMessage对象保存在通道中。

## 六. 总结

在本教程中，我们探讨了 Spring Cloud Task 的执行方式以及如何配置它以将其事件记录在数据库中。我们还观察了 Spring Batch 作业是如何定义和存储在TaskRepository中的。最后，我们解释了如何从 Spring Cloud Stream 中触发任务。
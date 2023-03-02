## 1. 概述

在本文中，我们将介绍[Spring Cloud Sleuth](https://cloud.spring.io/spring-cloud-sleuth/)，这是一个强大的工具，用于增强任何应用程序中的日志，尤其是在由多个服务构建的系统中。

对于这篇文章，我们将重点介绍**如何在单体应用程序中使用Sleuth，而不是跨微服务**。

我们都有过尝试诊断计划任务、多线程操作或复杂Web请求问题的不幸经历。通常，即使有日志记录，也很难判断哪些操作需要关联在一起才能创建单个请求。

这会使**诊断复杂的操作变得非常困难甚至不可能**。通常会导致解决方案，例如将唯一ID传递给请求中的每个方法以识别日志。

因此Sleuth来了。该库可以识别与特定作业、线程或请求有关的日志。Sleuth毫不费力地与Logback和SLF4J等日志记录框架集成，以添加有助于使用日志跟踪和诊断问题的唯一标识符。

让我们来看看它是如何工作的。

## 2. 设置

我们将首先在我们最喜欢的IDE中创建一个Spring Boot Web项目，并将此依赖项添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-sleuth</artifactId>
</dependency>
```

我们的应用程序使用Spring Boot运行，父pom为每个依赖提供版本。可以在此处找到此依赖项的最新版本：[spring-cloud-starter-sleuth](https://repo.spring.io/libs-milestone-local/org/springframework/cloud/spring-cloud-sleuth/)。

此外，让我们添加一个应用程序名称以指示Sleuth识别此应用程序的日志。

在我们的application.properties文件中添加以下行：

```properties
spring.application.name=Tuyucheng Sleuth Tutorial
```

## 3. Sleuth配置

Sleuth能够在许多情况下增强日志。从2.0.0版开始，Spring Cloud Sleuth使用[Brave](https://github.com/openzipkin/brave)作为跟踪库，为进入我们应用程序的每个Web请求添加唯一ID。此外，Spring团队还增加了对跨线程边界共享这些ID的支持。

可以将跟踪视为在应用程序中触发的单个请求或作业。该请求中的所有不同步骤，甚至跨越应用程序和线程边界，都将具有相同的traceId。

另一方面，跨度可以被认为是工作或请求的一部分。单个跟踪可以由多个跨度组成，每个跨度与请求的特定步骤或部分相关。使用trace和span ids，我们可以准确地查明我们的应用程序在处理请求时的时间和位置。使阅读我们的日志变得更加容易。

在我们的示例中，我们将在单个应用程序中探索这些功能。

### 3.1 简单的Web请求

首先，让我们创建一个控制器类作为入口点：

```java
@RestController
public class SleuthController {

    @GetMapping("/")
    public String helloSleuth() {
        logger.info("Hello Sleuth");
        return "success";
    }
}
```

让我们运行我们的应用程序并导航到“http://localhost:8080”。查看日志以获取如下所示的输出：

```shell
2017-01-10 22:36:38.254  INFO 
  [Tuyucheng Sleuth Tutorial,4e30f7340b3fb631,4e30f7340b3fb631,false] 12516 
  --- [nio-8080-exec-1] c.t.t.spring.session.SleuthController : Hello Sleuth
```

这看起来像一个普通的日志，除了括号之间开头的部分。这是Spring Sleuth添加的核心信息。此数据遵循以下格式：

**[application name、traceId、spanId、export]**

-   **Application name**：这是我们在属性文件中设置的名称，可用于聚合来自同一应用程序的多个实例的日志。
-   **TraceId**：这是分配给单个请求、作业或操作的ID。像每个唯一用户发起的Web请求这样的东西都会有自己的traceId。
-   **SpanId**：跟踪一个工作单元。考虑一个包含多个步骤的请求。每个步骤都可以有自己的spanId并单独跟踪。默认情况下，任何应用程序流都将以相同的TraceId和SpanId开始。
-   **Export**：此属性是一个布尔值，指示此日志是否导出到Zipkin等聚合器。Zipkin超出了本文的范围，但在分析Sleuth创建的日志方面发挥着重要作用。

到目前为止，你应该对这个库的强大功能有所了解。让我们看另一个示例，以进一步说明该库与日志记录的集成程度。

### 3.2 具有服务访问权限的简单Web请求

让我们从使用单一方法创建服务开始：

```java
@Service
public class SleuthService {

    public void doSomeWorkSameSpan() {
        Thread.sleep(1000L);
        logger.info("Doing some work");
    }
}
```

现在让我们将我们的服务注入我们的控制器并添加一个访问它的请求映射方法：

```java
@Autowired
private SleuthService sleuthService;
    
    @GetMapping("/same-span")
    public String helloSleuthSameSpan() throws InterruptedException {
        logger.info("Same Span");
        sleuthService.doSomeWorkSameSpan();
        return "success";
}
```

最后，重新启动应用程序并导航到“http://localhost:8080/same-span” 。观察如下所示的日志输出：

```shell
2017-01-10 22:51:47.664  INFO 
  [Tuyucheng Sleuth Tutorial,b77a5ea79036d5b9,b77a5ea79036d5b9,false] 12516 
  --- [nio-8080-exec-3] c.t.t.spring.session.SleuthController      : Same Span
2017-01-10 22:51:48.664  INFO 
  [Tuyucheng Sleuth Tutorial,b77a5ea79036d5b9,b77a5ea79036d5b9,false] 12516 
  --- [nio-8080-exec-3] c.t.t.spring.session.SleuthService  : Doing some work
```

请注意，即使消息来自两个不同的类，trace和span id在两个日志之间也是相同的。这使得通过搜索该请求的traceId来识别请求期间的每个日志变得微不足道。

这是默认行为，一个请求获得单个traceId和spanId。但是我们可以根据需要手动添加跨度。让我们看一个使用此功能的示例。

### 3.3 手动添加Span

首先，让我们添加一个新控制器：

```java
@GetMapping("/new-span")
public String helloSleuthNewSpan() {
    logger.info("New Span");
    sleuthService.doSomeWorkNewSpan();
    return "success";
}
```

现在让我们在服务中添加新方法：

```java
@Autowired
private Tracer tracer;
// ...
public void doSomeWorkNewSpan() throws InterruptedException {
    logger.info("I'm in the original span");

    Span newSpan = tracer.nextSpan().name("newSpan").start();
    try (SpanInScope ws = tracer.withSpanInScope(newSpan.start())) {
        Thread.sleep(1000L);
        logger.info("I'm in the new span doing some cool work that needs its own span");
    } finally {
        newSpan.finish();
    }

    logger.info("I'm in the original span");
}
```

请注意，我们还添加了一个新对象Tracer。tracer实例由Spring Sleuth在启动期间创建，并通过依赖注入提供给我们的类。

必须手动启动和停止跟踪。为实现这一点，在手动创建的跨度中运行的代码被放置在一个try-finally块中，以确保无论操作是否成功，跨度都会关闭。另外，请注意新跨度必须放在作用域内。

重新启动应用程序并导航到“http://localhost:8080/new-span” 。观察如下所示的日志输出：

```shell
2017-01-11 21:07:54.924  
  INFO [Tuyucheng Sleuth Tutorial,9cdebbffe8bbbade,9cdebbffe8bbbade,false] 12516 
  --- [nio-8080-exec-6] c.t.t.spring.session.SleuthController      : New Span
2017-01-11 21:07:54.924  
  INFO [Tuyucheng Sleuth Tutorial,9cdebbffe8bbbade,9cdebbffe8bbbade,false] 12516 
  --- [nio-8080-exec-6] c.t.t.spring.session.SleuthService  : 
  I'm in the original span
2017-01-11 21:07:55.924  
  INFO [Tuyucheng Sleuth Tutorial,9cdebbffe8bbbade,1e706f252a0ee9c2,false] 12516 
  --- [nio-8080-exec-6] c.t.t.spring.session.SleuthService  : 
  I'm in the new span doing some cool work that needs its own span
2017-01-11 21:07:55.924  
  INFO [Tuyucheng Sleuth Tutorial,9cdebbffe8bbbade,9cdebbffe8bbbade,false] 12516 
  --- [nio-8080-exec-6] c.t.t.spring.session.SleuthService  : 
  I'm in the original span
```

我们可以看到第三条日志与其他日志共享traceId，但它具有唯一的spanId。这可用于定位单个请求中的不同部分以进行更细粒度的跟踪。

现在让我们看一下Sleuth对线程的支持。

### 3.4 跨越Runnable

为了演示Sleuth的线程功能，让我们首先添加一个配置类来设置线程池：

```java
@Configuration
public class ThreadConfig {

    @Autowired
    private BeanFactory beanFactory;

    @Bean
    public Executor executor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(1);
        threadPoolTaskExecutor.setMaxPoolSize(1);
        threadPoolTaskExecutor.initialize();

        return new LazyTraceExecutor(beanFactory, threadPoolTaskExecutor);
    }
}
```

重要的是要注意LazyTraceExecutor的使用。这个类来自Sleuth库，是一种特殊的执行器，它将我们的traceId传播到新线程并在进程中创建新的spanId。

现在让我们将这个执行器注入到我们的控制器中，并在新的请求映射方法中使用它：

```java
@Autowired
private Executor executor;
    
    @GetMapping("/new-thread")
    public String helloSleuthNewThread() {
        logger.info("New Thread");
        Runnable runnable = () -> {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.info("I'm inside the new thread - with a new span");
        };
        executor.execute(runnable);

        logger.info("I'm done - with the original span");
        return "success";
}
```

有了我们的runnable，让我们重新启动我们的应用程序并导航到“http://localhost:8080/new-thread” 。观察如下所示的日志输出：

```shell
2017-01-11 21:18:15.949  
  INFO [Tuyucheng Sleuth Tutorial,96076a78343c364d,96076a78343c364d,false] 12516 
  --- [nio-8080-exec-9] c.t.t.spring.session.SleuthController      : New Thread
2017-01-11 21:18:15.950  
  INFO [Tuyucheng Sleuth Tutorial,96076a78343c364d,96076a78343c364d,false] 12516 
  --- [nio-8080-exec-9] c.t.t.spring.session.SleuthController      : 
  I'm done - with the original span
2017-01-11 21:18:16.953  
  INFO [Tuyucheng Sleuth Tutorial,96076a78343c364d,e3b6a68013ddfeea,false] 12516 
  --- [lTaskExecutor-1] c.t.t.spring.session.SleuthController      : 
  I'm inside the new thread - with a new span
```

与前面的示例非常相似，我们可以看到所有日志共享相同的traceId。但是来自runnable的日志具有唯一的跨度，可以跟踪在该线程中完成的工作。请记住，发生这种情况是因为LazyTraceExecutor，如果我们使用普通的执行器，我们将继续看到在新线程中使用相同的spanId。

现在让我们看看Sleuth对@Async方法的支持。

### 3.5 @Async支持

要添加异步支持，让我们首先修改ThreadConfig类以启用此功能：

```java
@Configuration
@EnableAsync
public class ThreadConfig extends AsyncConfigurerSupport {

    //...
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(1);
        threadPoolTaskExecutor.setMaxPoolSize(1);
        threadPoolTaskExecutor.initialize();

        return new LazyTraceExecutor(beanFactory, threadPoolTaskExecutor);
    }
}
```

请注意，我们扩展了AsyncConfigurerSupport以指定我们的异步执行器并使用LazyTraceExecutor来确保正确传播traceIds和spanIds。我们还将@EnableAsync添加到类的上面。

现在让我们向我们的服务添加一个异步方法：

```java
@Async
public void asyncMethod() {
    logger.info("Start Async Method");
    Thread.sleep(1000L);
    logger.info("End Async Method");
}
```

现在让我们从我们的控制器调用这个方法：

```java
@GetMapping("/async")
public String helloSleuthAsync() {
    logger.info("Before Async Method Call");
    sleuthService.asyncMethod();
    logger.info("After Async Method Call");
    
    return "success";
}
```

最后，让我们重新启动我们的服务并导航到“http://localhost:8080/async”。 观察如下所示的日志输出：

```shell
2017-01-11 21:30:40.621  
  INFO [Tuyucheng Sleuth Tutorial,c187f81915377fff,c187f81915377fff,false] 10072 
  --- [nio-8080-exec-2] c.t.t.spring.session.SleuthController      : 
  Before Async Method Call
2017-01-11 21:30:40.622  
  INFO [Tuyucheng Sleuth Tutorial,c187f81915377fff,c187f81915377fff,false] 10072 
  --- [nio-8080-exec-2] c.t.t.spring.session.SleuthController      : 
  After Async Method Call
2017-01-11 21:30:40.622  
  INFO [Tuyucheng Sleuth Tutorial,c187f81915377fff,8a9f3f097dca6a9e,false] 10072 
  --- [lTaskExecutor-1] c.t.t.spring.session.SleuthService  : 
  Start Async Method
2017-01-11 21:30:41.622  
  INFO [Tuyucheng Sleuth Tutorial,c187f81915377fff,8a9f3f097dca6a9e,false] 10072 
  --- [lTaskExecutor-1] c.t.t.spring.session.SleuthService  : 
  End Async Method
```

我们可以在这里看到，与我们的Runnable示例非常相似，Sleuth将traceId传播到异步方法中并添加了一个唯一的spanId。

现在让我们来看一个使用Spring支持调度任务的示例。

### 3.6 @Scheduled支持

最后，让我们看看Sleuth如何使用@Scheduled方法。为此，让我们更新ThreadConfig类以启用调度：

```java
@Configuration
@EnableAsync
@EnableScheduling
public class ThreadConfig extends AsyncConfigurerSupport implements SchedulingConfigurer {

    //...

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.setScheduler(schedulingExecutor());
    }

    @Bean(destroyMethod = "shutdown")
    public Executor schedulingExecutor() {
        return Executors.newScheduledThreadPool(1);
    }
}
```

请注意，我们已经实现了SchedulingConfigurer接口并覆盖了它的configureTasks方法。我们还在类的上面添加了@EnableScheduling。

接下来，让我们为我们的调度任务添加一个服务：

```java
@Service
public class SchedulingService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SleuthService sleuthService;

    @Scheduled(fixedDelay = 30000)
    public void scheduledWork() throws InterruptedException {
        logger.info("Start some work from the scheduled task");
        sleuthService.asyncMethod();
        logger.info("End work from scheduled task");
    }
}
```

在本类中，我们创建了一个固定延迟为30秒的调度任务。

现在让我们重新启动我们的应用程序并等待我们的任务被执行。观察控制台输出如下：

```shell
2017-01-11 21:30:58.866  
  INFO [Tuyucheng Sleuth Tutorial,3605f5deaea28df2,3605f5deaea28df2,false] 10072 
  --- [pool-1-thread-1] c.t.t.spring.session.SchedulingService     : 
  Start some work from the scheduled task
2017-01-11 21:30:58.866  
  INFO [Tuyucheng Sleuth Tutorial,3605f5deaea28df2,3605f5deaea28df2,false] 10072 
  --- [pool-1-thread-1] c.t.t.spring.session.SchedulingService     : 
  End work from scheduled task
```

我们可以在这里看到Sleuth为我们的任务创建了新的trace和span id。默认情况下，任务的每个实例都将获得自己的跟踪和跨度。

## 4. 总结

总之，我们已经了解了如何在单个Web应用程序中的各种情况下使用Spring Sleuth。我们可以使用这项技术轻松关联来自单个请求的日志，即使该请求跨越多个线程。

到目前为止，我们可以看到Spring Cloud Sleuth如何帮助我们在调试多线程环境时保持理智。通过识别traceId中的每个操作和spanId中的每个步骤，我们可以真正开始分解我们对日志中复杂作业的分析。

即使我们不使用云，Spring Sleuth也可能是几乎所有项目中的关键依赖项；它可以无缝集成并且是一个巨大的附加值。

从这里你可能想研究Sleuth的其他功能。它可以支持使用RestTemplate在分布式系统中进行跟踪，跨RabbitMQ和Redis使用的消息传递协议，以及通过像Zuul这样的网关。
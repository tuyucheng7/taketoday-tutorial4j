## 1. 简介

在这个简短的教程中，我们将了解如何在Spring Boot应用程序中利用虚拟线程的强大功能。

虚拟线程是Java 19的[预览功能](https://openjdk.org/jeps/425)，这意味着它们将在未来12个月内包含在正式的JDK版本中。最初由Project Loom引入，[Spring 6版本](https://spring.io/blog/2022/10/11/embracing-virtual-threads)为开发人员提供了开始试验这一出色功能的选项。

首先，我们将了解“平台线程”和“虚拟线程”之间的主要区别。接下来，我们将使用虚拟线程从头开始构建一个Spring Boot应用程序。最后，我们将构建一个小型测试套件，以查看简单Web应用程序吞吐量的最终改进。

## 2. 虚拟线程与平台线程

主要区别在于[虚拟线程](https://www.baeldung.com/java-virtual-thread-vs-thread)在其运行周期中不依赖操作系统线程：它们与硬件分离，因此称为“虚拟”。这种分离是由JVM提供的抽象层授予的。

出于本教程的目的，重要的是要了解虚拟线程的运行成本远低于平台线程，它们消耗的内存量更少。这就是为什么可以创建数百万个虚拟线程而不会出现内存不足问题，而不是我们可以使用标准平台(或内核)线程创建的几百个。

从理论上讲，这赋予了开发人员一种超能力：在不依赖异步代码的情况下管理高度可扩展的应用程序。

## 3. 在Spring 6中使用虚拟线程

从Spring Framework 6(和Spring Boot 3)开始，虚拟线程功能正式全面可用，但虚拟线程是Java 19的[预览功能](https://www.baeldung.com/java-preview-features)。这意味着我们需要告诉JVM我们要在应用程序中启用它们。由于我们使用Maven来构建我们的应用程序，因此我们要确保在pom.xml中包含以下代码：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <source>19</source>
                <target>19</target>
                <compilerArgs>
                    --enable-preview
                </compilerArgs>
            </configuration>
        </plugin>
    </plugins>
</build>
```

从Java的角度来看，要使用Apache Tomcat和虚拟线程，我们只需要一个包含几个bean的简单配置类：

```java
@EnableAsync
@Configuration
@ConditionalOnProperty(
        value = "spring.thread-executor",
        havingValue = "virtual"
)
public class ThreadConfig {
    @Bean
    public AsyncTaskExecutor applicationTaskExecutor() {
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }

    @Bean
    public TomcatProtocolHandlerCustomizer<?> protocolHandlerVirtualThreadExecutorCustomizer() {
        return protocolHandler -> {
            protocolHandler.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        };
    }
}
```

第一个名为ApplicationTaskExecutor的Spring bean将取代标准的[ApplicationTaskExecutor](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/autoconfigure/task/TaskExecutionAutoConfiguration.html)，提供一个为每个任务启动新虚拟线程的Executor。第二个bean名为ProtocolHandlerVirtualThreadExecutorCustomizer，将以相同的方式自定义标准的[TomcatProtocolHandler](https://tomcat.apache.org/tomcat-8.5-doc/api/org/apache/coyote/ProtocolHandler.html)。我们还添加了注解[@ConditionalOnProperty](https://www.baeldung.com/spring-conditionalonproperty)以通过切换application.yaml文件中配置属性的值来按需启用虚拟线程：

```yaml
spring:
    thread-executor: virtual
    # ...
```

现在让我们测试Spring Boot应用程序是否使用虚拟线程来处理Web请求调用。为此，我们需要构建一个简单的控制器来返回所需的信息：

```java
@RestController
@RequestMapping("/thread")
public class ThreadController {
    @GetMapping("/name")
    public String getThreadName() {
        return Thread.currentThread().toString();
    }
}
```

[Thread](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/lang/Thread.html)对象的toString()方法将返回我们需要的所有信息：线程ID、线程名称、线程组和优先级。让我们用[curl](https://www.baeldung.com/curl-rest)请求访问这个端点：

```shell
$ curl -s http://localhost:8080/thread/name
$ VirtualThread[#171]/runnable@ForkJoinPool-1-worker-4
```

如我们所见，响应明确表示我们正在使用虚拟线程来处理此Web请求。换句话说，Thread.currentThread()调用返回虚拟线程类的一个实例。现在让我们通过一个简单但有效的负载测试来了解虚拟线程的有效性。

## 4. 性能比较

对于此负载测试，我们将使用[JMeter](https://www.baeldung.com/jmeter)。这不是虚拟线程和标准线程之间的完整性能比较，而是我们可以从中构建具有不同参数的其他测试的起点。

在这种特殊情况下，我们将在RestController中调用一个端点，该端点简单地将执行置于睡眠状态一秒钟，模拟一个复杂的异步任务：

```java
@RestController
@RequestMapping("/load")
public class LoadTestController {

    private static final Logger LOG = LoggerFactory.getLogger(LoadTestController.class);

    @GetMapping
    public void doSomething() throws InterruptedException {
        LOG.info("hey, I'm doing something");
        Thread.sleep(1000);
    }
}
```

请记住，由于@ConditionalOnProperty注解，我们可以通过仅更改application.yaml中的变量值来在虚拟线程和标准线程之间切换。

JMeter测试将只包含一个线程组，模拟1000个并发用户命中/load端点100秒：

<img src="../assets/img.png">

在这种情况下，采用此新功能带来的性能提升是显而易见的。让我们比较一下不同实现的“Response Time Graph”。这是标准线程的响应图。正如我们所看到的，完成调用所需的时间立即达到5000毫秒：

<img src="../assets/img_1.png">

发生这种情况是因为平台线程是一种有限的资源，当所有调度线程和池线程都繁忙时，Spring应用程序除了暂停请求直到一个线程空闲之外，没有什么可做的。

让我们看看虚拟线程会发生什么：

<img src="../assets/img_2.png">

正如我们所见，响应在1000毫秒时稳定下来。虚拟线程在请求后立即创建和使用，因为从资源的角度来看它们非常便宜。

这种性能提升之所以成为可能，是因为该场景过于简单，并且没有考虑Spring Boot应用程序可以做的所有事情。从底层操作系统基础设施中采用这种抽象可能会带来好处，但并非在所有情况下都如此。

## 5. 总结

在本文中，我们了解了如何在基于Spring 6的应用程序中使用虚拟线程。
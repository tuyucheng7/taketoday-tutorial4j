## 一、概述

典型的分布式系统由许多协作在一起的服务组成。

这些服务容易出现故障或响应延迟。如果一个服务失败，它可能会影响其他服务，影响性能，并可能使应用程序的其他部分无法访问，或者在最坏的情况下导致整个应用程序崩溃。

当然，有一些可用的解决方案可以帮助使应用程序具有弹性和容错能力——Hystrix 就是这样一种框架。

Hystrix 框架库通过提供容错和延迟容忍来帮助控制服务之间的交互。它通过隔离故障服务并阻止故障的级联效应来提高系统的整体弹性。

在本系列文章中，我们将首先了解 Hystrix 如何在服务或系统出现故障时进行救援，以及 Hystrix 在这些情况下可以完成什么。

## 2. 简单例子

Hystrix 提供容错和延迟容忍的方式是隔离和包装对远程服务的调用。

在这个简单的示例中，我们在HystrixCommand的run()方法中包装了一个调用：

```java
class CommandHelloWorld extends HystrixCommand<String> {

    private String name;

    CommandHelloWorld(String name) {
        super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
        this.name = name;
    }

    @Override
    protected String run() {
        return "Hello " + name + "!";
    }
}
```

我们按如下方式执行调用：

```java
@Test
public void givenInputBobAndDefaultSettings_whenCommandExecuted_thenReturnHelloBob(){
    assertThat(new CommandHelloWorld("Bob").execute(), equalTo("Hello Bob!"));
}
```

## 3. 行家设置

要在 Maven 项目中使用 Hystrix，我们需要在项目pom.xml中拥有来自 Netflix 的hystrix-core和rxjava-core依赖项：

```java
<dependency>
    <groupId>com.netflix.hystrix</groupId>
    <artifactId>hystrix-core</artifactId>
    <version>1.5.4</version>
</dependency>

```

总能在[这里](https://search.maven.org/classic/#search|ga|1|a%3A"hystrix-core")找到最新版本。

```java
<dependency>
    <groupId>com.netflix.rxjava</groupId>
    <artifactId>rxjava-core</artifactId>
    <version>0.20.7</version>
</dependency>
```

这个库的最新版本总是可以在[这里](https://search.maven.org/classic/#search|ga|1|a%3A"rxjava-core")找到。

## 4.设置远程服务

让我们从模拟一个真实世界的例子开始。

在下面的示例中，类RemoteServiceTestSimulator表示远程服务器上的服务。它有一个在给定时间段后用消息响应的方法。我们可以想象，这个等待是对远程系统中一个耗时过程的模拟，导致对调用服务的响应延迟：

```java
class RemoteServiceTestSimulator {

    private long wait;

    RemoteServiceTestSimulator(long wait) throws InterruptedException {
        this.wait = wait;
    }

    String execute() throws InterruptedException {
        Thread.sleep(wait);
        return "Success";
    }
}
```

这是我们调用RemoteServiceTestSimulator的示例客户端。

对服务的调用被隔离并包装在HystrixCommand的run()方法中。正是这种包装提供了我们上面提到的弹性：

```java
class RemoteServiceTestCommand extends HystrixCommand<String> {

    private RemoteServiceTestSimulator remoteService;

    RemoteServiceTestCommand(Setter config, RemoteServiceTestSimulator remoteService) {
        super(config);
        this.remoteService = remoteService;
    }

    @Override
    protected String run() throws Exception {
        return remoteService.execute();
    }
}
```

该调用是通过调用RemoteServiceTestCommand对象实例上的execute()方法来执行的。

以下测试演示了这是如何完成的：

```java
@Test
public void givenSvcTimeoutOf100AndDefaultSettings_whenRemoteSvcExecuted_thenReturnSuccess()
  throws InterruptedException {

    HystrixCommand.Setter config = HystrixCommand
      .Setter
      .withGroupKey(HystrixCommandGroupKey.Factory.asKey("RemoteServiceGroup2"));
    
    assertThat(new RemoteServiceTestCommand(config, new RemoteServiceTestSimulator(100)).execute(),
      equalTo("Success"));
}
```

到目前为止，我们已经了解了如何将远程服务调用包装在HystrixCommand对象中。在下面的部分中，让我们看看如何处理远程服务开始恶化的情况。

## 5. 使用远程服务和防御性编程

### 5.1. 带超时的防御性编程

为调用远程服务设置超时是一般的编程习惯。

让我们首先看看如何在HystrixCommand上设置超时以及它如何通过短路来提供帮助：

```java
@Test
public void givenSvcTimeoutOf5000AndExecTimeoutOf10000_whenRemoteSvcExecuted_thenReturnSuccess()
  throws InterruptedException {

    HystrixCommand.Setter config = HystrixCommand
      .Setter
      .withGroupKey(HystrixCommandGroupKey.Factory.asKey("RemoteServiceGroupTest4"));

    HystrixCommandProperties.Setter commandProperties = HystrixCommandProperties.Setter();
    commandProperties.withExecutionTimeoutInMilliseconds(10_000);
    config.andCommandPropertiesDefaults(commandProperties);

    assertThat(new RemoteServiceTestCommand(config, new RemoteServiceTestSimulator(500)).execute(),
      equalTo("Success"));
}
```

在上面的测试中，我们通过将超时设置为 500 毫秒来延迟服务的响应。我们还将HystrixCommand的执行超时设置为 10,000 毫秒，从而为远程服务留出足够的时间进行响应。

现在让我们看看当执行超时小于服务超时调用时会发生什么：

```java
@Test(expected = HystrixRuntimeException.class)
public void givenSvcTimeoutOf15000AndExecTimeoutOf5000_whenRemoteSvcExecuted_thenExpectHre()
  throws InterruptedException {

    HystrixCommand.Setter config = HystrixCommand
      .Setter
      .withGroupKey(HystrixCommandGroupKey.Factory.asKey("RemoteServiceGroupTest5"));

    HystrixCommandProperties.Setter commandProperties = HystrixCommandProperties.Setter();
    commandProperties.withExecutionTimeoutInMilliseconds(5_000);
    config.andCommandPropertiesDefaults(commandProperties);

    new RemoteServiceTestCommand(config, new RemoteServiceTestSimulator(15_000)).execute();
}
```

请注意我们是如何降低标准并将执行超时设置为 5,000 毫秒的。

我们期望服务在 5,000 毫秒内响应，而我们已将服务设置为在 15,000 毫秒后响应。如果在执行测试时注意到，测试将在 5,000 毫秒后退出，而不是等待 15,000 毫秒，并将抛出HystrixRuntimeException。

这演示了 Hystrix 如何不会等待超过配置的响应超时时间。这有助于使受 Hystrix 保护的系统响应更快。

在下面的部分中，我们将研究设置线程池大小以防止线程耗尽，我们将讨论它的好处。

### 5.2. 有限线程池的防御性编程

为服务调用设置超时并不能解决与远程服务相关的所有问题。

当远程服务开始响应缓慢时，典型的应用程序将继续调用该远程服务。

应用程序不知道远程服务是否健康，每次收到请求时都会生成新线程。这将导致使用已经苦苦挣扎的服务器上的线程。

我们不希望发生这种情况，因为我们需要这些线程来执行服务器上运行的其他远程调用或进程，并且我们还希望避免 CPU 利用率激增。

让我们看看如何在HystrixCommand中设置线程池大小：

```java
@Test
public void givenSvcTimeoutOf500AndExecTimeoutOf10000AndThreadPool_whenRemoteSvcExecuted
  _thenReturnSuccess() throws InterruptedException {

    HystrixCommand.Setter config = HystrixCommand
      .Setter
      .withGroupKey(HystrixCommandGroupKey.Factory.asKey("RemoteServiceGroupThreadPool"));

    HystrixCommandProperties.Setter commandProperties = HystrixCommandProperties.Setter();
    commandProperties.withExecutionTimeoutInMilliseconds(10_000);
    config.andCommandPropertiesDefaults(commandProperties);
    config.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
      .withMaxQueueSize(10)
      .withCoreSize(3)
      .withQueueSizeRejectionThreshold(10));

    assertThat(new RemoteServiceTestCommand(config, new RemoteServiceTestSimulator(500)).execute(),
      equalTo("Success"));
}
```

在上面的测试中，我们设置了最大队列大小、核心队列大小和队列拒绝大小。当最大线程数达到 10 并且任务队列的大小达到 10 时，Hystrix将开始拒绝请求。

核心大小是线程池中始终保持活动状态的线程数。

### 5.3. 具有短路断路器模式的防御性编程

但是，我们仍然可以对远程服务调用进行改进。

让我们考虑远程服务开始失败的情况。

我们不想继续向它发出请求并浪费资源。理想情况下，我们希望在一定时间内停止发出请求，以便在恢复请求之前为服务提供恢复时间。这就是所谓的短路断路器模式。

让我们看看 Hystrix 是如何实现这种模式的：

```java
@Test
public void givenCircuitBreakerSetup_whenRemoteSvcCmdExecuted_thenReturnSuccess()
  throws InterruptedException {

    HystrixCommand.Setter config = HystrixCommand
      .Setter
      .withGroupKey(HystrixCommandGroupKey.Factory.asKey("RemoteServiceGroupCircuitBreaker"));

    HystrixCommandProperties.Setter properties = HystrixCommandProperties.Setter();
    properties.withExecutionTimeoutInMilliseconds(1000);
    properties.withCircuitBreakerSleepWindowInMilliseconds(4000);
    properties.withExecutionIsolationStrategy
     (HystrixCommandProperties.ExecutionIsolationStrategy.THREAD);
    properties.withCircuitBreakerEnabled(true);
    properties.withCircuitBreakerRequestVolumeThreshold(1);

    config.andCommandPropertiesDefaults(properties);
    config.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
      .withMaxQueueSize(1)
      .withCoreSize(1)
      .withQueueSizeRejectionThreshold(1));

    assertThat(this.invokeRemoteService(config, 10_000), equalTo(null));
    assertThat(this.invokeRemoteService(config, 10_000), equalTo(null));
    assertThat(this.invokeRemoteService(config, 10_000), equalTo(null));

    Thread.sleep(5000);

    assertThat(new RemoteServiceTestCommand(config, new RemoteServiceTestSimulator(500)).execute(),
      equalTo("Success"));

    assertThat(new RemoteServiceTestCommand(config, new RemoteServiceTestSimulator(500)).execute(),
      equalTo("Success"));

    assertThat(new RemoteServiceTestCommand(config, new RemoteServiceTestSimulator(500)).execute(),
      equalTo("Success"));
}
public String invokeRemoteService(HystrixCommand.Setter config, int timeout)
  throws InterruptedException {

    String response = null;

    try {
        response = new RemoteServiceTestCommand(config,
          new RemoteServiceTestSimulator(timeout)).execute();
    } catch (HystrixRuntimeException ex) {
        System.out.println("ex = " + ex);
    }

    return response;
}
```

在上面的测试中，我们设置了不同的断路器属性。最重要的是：

-   CircuitBreakerSleepWindow设置为 4,000 毫秒。这配置了断路器窗口并定义了恢复对远程服务的请求的时间间隔
-   CircuitBreakerRequestVolumeThreshold设置为 1 并定义在考虑故障率之前所需的最小请求数

通过上述设置，我们的HystrixCommand现在将在两次失败请求后跳闸。即使我们将服务延迟设置为 500 毫秒，第三个请求甚至不会命中远程服务，Hystrix将短路并且我们的方法将返回null作为响应。

我们随后将添加一个Thread.sleep(5000)以跨越我们设置的睡眠窗口的限制。这将导致Hystrix关闭电路，后续请求将成功流过。

## 6.总结

总之，Hystrix 旨在：

1.  对通常通过网络访问的服务的故障和延迟提供保护和控制
2.  停止因某些服务关闭而导致的级联故障
3.  快速失败并快速恢复
4.  尽可能优雅地降级
5.  指挥中心故障实时监控告警

在下一篇文章中，我们将看到如何将 Hystrix 的优势与 Spring 框架结合起来。
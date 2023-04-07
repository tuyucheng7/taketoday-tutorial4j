## 1. 概述

结构化并发特性([JEP-428](https://openjdk.org/jeps/428))旨在通过将在不同线程(从同一父线程派生)中运行的多个任务视为单个工作单元来简化Java并发程序。将所有此类子线程视为一个单元将有助于将所有线程作为一个单元进行管理；因此，可以更可靠地完成取消和错误处理。

错误处理和任务取消的可靠性将消除线程泄漏和取消延迟等常见风险。

## 2. 传统并发的问题

### 2.1 线程泄漏

在传统的[多线程编程](https://howtodoinjava.com/series/java-concurrency/)(**非结构化并发**)中，如果一个应用程序必须执行一个复杂的任务，它会将程序分解成多个更小且独立的子任务单元。然后应用程序将所有任务提交给[ThreadPoolExecutor](https://howtodoinjava.com/java/multi-threading/java-thread-pool-executor-example/)，通常使用运行所有任务和子任务的[ExecutorService](https://howtodoinjava.com/java/multi-threading/executor-service-example/)。

在这样的编程模型中，所有子任务并发运行，因此每个子任务都可以独立地成功或失败。如果其中一个子任务失败，API中不支持取消所有相关子任务。应用程序无法控制子任务，必须等待所有子任务完成才能返回父任务的结果。这种等待是一种资源浪费，会降低应用程序的性能。

例如，如果一个任务必须获取帐户的详细信息，并且它需要从多个来源(例如帐户详细信息、关联帐户、用户的人口统计数据等)获取详细信息，那么并发请求处理的伪代码将如下所示：

```java
Response fetch(Long id) throws ExecutionException, InterruptedException {
    Future<AccountDetails>  accountDetailsFuture  = es.submit(() -> getAccountDetails(id));
    Future<LinkedAccounts> linkedAccountsFuture = es.submit(() -> fetchLinkedAccounts(id));
    Future<DemographicData> userDetailsFuture = es.submit(() -> fetchUserDetails(id));
    
    AccountDetails accountDetails  = accountDetailsFuture.get();
    LinkedAccounts linkedAccounts  = linkedAccountsFuture.get();
    DemographicData userDetails    = userDetailsFuture.get();
    
    return new Response(accountDetails, linkedAccounts, userDetails);
}
```

在上面的示例中，所有三个线程都独立执行。

-   假设如果在获取关联帐户时出错，则fetch()将返回错误响应，但其他两个线程将继续在后台运行。这是线程泄漏的情况。
-   同样，如果用户从前端取消请求，fetch()被中断，那么三个线程都会继续在后台运行。

尽管可以通过编程方式[取消子任务](https://howtodoinjava.com/java/multi-threading/executor-service-cancel-task/)，但没有直接的方法可以做到这一点，并且存在出错的可能性。

### 2.2 不相关的线程转储和诊断

在前面的示例中，如果fetch() API中存在错误，则很难[分析线程转储](https://howtodoinjava.com/java/how-to-get-thread-dump-in-linux-using-jstack/)，因为它们在3个不同的线程中运行。在3个线程中的信息之间建立关系非常困难，因为在API级别这些线程之间没有关系。

当调用堆栈定义了任务-子任务层次结构时，例如在顺序方法执行中，我们得到了父子关系，它流入错误传播。

理想情况下，任务关系应该反映在API级别，以便在必要时控制子线程的执行和调试。这将允许子任务仅向其父任务(拥有所有子任务的唯一任务)报告结果或异常，然后可以隐式取消剩余的子任务。

## 3. 结构化并发

### 3.1 基本概念

在结构化的多线程代码中，如果一个任务拆分成并发的子任务，它们都会返回到同一个地方，即任务的代码块。这样，并发子任务的生命周期就被限制在该句法块中。

在这种方法中，子任务代表等待其结果并监视其失败的任务。在运行时，结构化并发构建了一个树形的任务层次结构，同级子任务由同一个父任务拥有。该树可以看作是具有多个方法调用的单个线程的调用堆栈的并发对应物。

### 3.2 使用StructuredTaskScope实现

StructuredTaskScope是用于结构化并发的基本API，它支持任务拆分为多个并发子任务并在它们自己的线程中执行的情况。

它强制子任务必须先完成，然后主任务才能继续。它确保并发操作的生命周期受语法块的限制。

让我们使用StructuredTaskScope API重写前面的例子。请注意，fork()方法启动一个[虚拟线程](https://howtodoinjava.com/java/multi-threading/virtual-threads/)来执行任务，join()方法等待所有线程完成，而close()方法关闭任务作用域。

StructuredTaskScope类实现了[AutoCloseable](https://howtodoinjava.com/java/basics/java-cleaners/)接口，因此如果我们使用[try-with-resources](https://howtodoinjava.com/java/try-with-resources/)块，那么在父线程完成执行后将自动调用close()。

```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()()) {
    
    Future<AccountDetails> accountDetailsFuture = scope.fork(() -> getAccountDetails(id));
    Future<LinkedAccounts> linkedAccountsFuture = scope.fork(() -> fetchLinkedAccounts(id));
    Future<DemographicData> userDetailsFuture = scope.fork(() -> fetchUserDetails(id));
    
    scope.join();	// join all subtasks
    scope.throwIfFailed(e -> new WebApplicationException(e));
    
    // the subtasks have completed by now so process the result
    return new Response(accountDetailsFuture.resultNow(),
	    linkedAccountsFuture.resultNow(),
	    userDetailsFuture.resultNow());
}
```

如第一部分所述，该解决方案解决了非结构化并发的所有问题。

## 4. 结构化并发和虚拟线程

虚拟线程是JVM管理的轻量级线程，用于编写高吞吐量并发应用程序。由于与传统操作系统线程相比，虚拟线程成本低廉，因此结构化并发利用它们来fork所有新线程。

除了数量丰富之外，虚拟线程的成本也足够低，可以表示任何并发的行为单元，甚至是涉及I/O的行为。在幕后，任务与子任务的关系是通过将每个虚拟线程与其唯一的所有者相关联来维护的，因此它知道其层次结构，类似于调用堆栈中的帧如何知道其唯一的调用者。

## 5. 总结

当与虚拟线程结合使用时，结构化并发为Java提供了期待已久且急需的功能，这些功能已经存在于其他编程语言中(例如，Go中的goroutines和Erlang中的process)。它将有助于编写具有出色可靠性和更少线程泄漏的更复杂和并发的应用程序。

当出现错误时，此类应用程序将更易于调试和分析。
## 1. 概述

我们通常会在同一个机器集群上部署各种应用程序。例如，如今 在同一个集群中拥有像[Apache Spark](https://www.baeldung.com/apache-spark)或[Apache Flink这样的分布式处理引擎和像](https://www.baeldung.com/apache-flink)[Apache Cassandra](https://www.baeldung.com/cassandra-with-java)这样的分布式数据库是很常见的。

Apache Mesos 是一个允许在此类应用程序之间进行有效资源共享的平台。

在本文中，我们将首先讨论部署在同一集群上的应用程序内部的资源分配问题。稍后，我们将了解 Apache Mesos 如何在应用程序之间提供更好的资源利用率。

## 2.共享集群

许多应用程序需要共享一个集群。总的来说，有两种常见的方法：

-   对集群进行静态分区并在每个分区上运行一个应用程序
-   为一个应用程序分配一组机器

尽管这些方法允许应用程序彼此独立运行，但并没有实现高资源利用率。

例如，考虑一个只运行很短时间的应用程序，然后有一段时间不活动。现在，由于我们已经为这个应用程序分配了静态机器或分区，所以在非活动期间我们有未使用的资源。

我们可以通过将非活动期间的空闲资源重新分配给其他应用程序来优化资源利用率。

Apache Mesos 有助于在应用程序之间进行动态资源分配。

## 3.阿帕奇月

通过我们上面讨论的两种集群共享方法，应用程序只知道它们正在运行的特定分区或机器的资源。但是，Apache Mesos 为应用程序提供了集群中所有资源的抽象视图。

我们很快就会看到，Mesos 充当机器和应用程序之间的接口。它为应用程序提供集群中所有机器上的可用资源。它经常更新此信息以包括已达到完成状态的应用程序释放的资源。这允许应用程序做出关于在哪台机器上执行哪个任务的最佳决定。

为了理解 Mesos 是如何工作的，让我们看一下它的[架构](https://mesos.apache.org/documentation/latest/architecture/)：

[![月拱](https://www.baeldung.com/wp-content/uploads/2019/07/Mesos-arch.jpg)](https://www.baeldung.com/wp-content/uploads/2019/07/Mesos-arch.jpg)

此图像是 Mesos 官方文档的一部分([来源](https://mesos.apache.org/assets/img/documentation/architecture3.jpg))。在这里，Hadoop和MPI是共享集群的两个应用程序。

我们将在接下来的几节中讨论此处显示的每个组件。

### 3.1. 月大师

Master 是此设置中的核心组件，用于存储集群中资源的当前状态。此外，它通过传递有关资源和任务等信息，充当 代理和应用程序之间的协调器。

由于 master 中的任何故障都会导致资源和任务的状态丢失，因此我们将其部署在高可用性配置中。从上图中可以看出，Mesos 部署了备用主守护进程和一个领导者。这些守护进程依靠 Zookeeper 在发生故障时恢复状态。

### 3.2. 月代理

Mesos 集群必须在每台机器上运行一个代理。这些代理定期向主机报告它们的资源，然后接收应用程序计划运行的任务。在计划任务完成或丢失后重复此循环。

在接下来的部分中，我们将看到应用程序如何在这些代理上安排和执行任务。

### 3.3. 几个月的框架

Mesos 允许应用程序实现一个抽象组件，该组件与 Master 交互以接收集群中的可用资源，并根据它们做出调度决策。这些组件称为框架。

Mesos 框架由两个子组件组成：

-   调度程序——使应用程序能够根据所有代理上的可用资源来安排任务
-   执行器——在所有代理上运行并包含在该代理上执行任何计划任务所需的所有信息

此流程描述了整个过程：

 

[![钱币流](https://www.baeldung.com/wp-content/uploads/2019/07/Mesos-flow.jpg)](https://www.baeldung.com/wp-content/uploads/2019/07/Mesos-flow.jpg)

首先，代理向主报告他们的资源。此时，master 将这些资源提供给所有已注册的调度程序。此过程称为资源提议，我们将在下一节中详细讨论。

调度器然后选择最好的代理并通过 Master 在其上执行各种任务。一旦执行者完成分配的任务，代理就会将它们的资源重新发布给主控。Master 对集群中的所有框架重复这个资源共享的过程。

Mesos 允许应用程序以各种编程语言实现其自定义调度程序和执行程序。调度程序的Java实现必须实现 Scheduler 接口：

```java
public class HelloWorldScheduler implements Scheduler {
 
    @Override
    public void registered(SchedulerDriver schedulerDriver, Protos.FrameworkID frameworkID, 
      Protos.MasterInfo masterInfo) {
    }
 
    @Override
    public void reregistered(SchedulerDriver schedulerDriver, Protos.MasterInfo masterInfo) {
    }
 
    @Override
    public void resourceOffers(SchedulerDriver schedulerDriver, List<Offer> list) {
    }
 
    @Override
    public void offerRescinded(SchedulerDriver schedulerDriver, OfferID offerID) {
    }
 
    @Override
    public void statusUpdate(SchedulerDriver schedulerDriver, Protos.TaskStatus taskStatus) {
    }
 
    @Override
    public void frameworkMessage(SchedulerDriver schedulerDriver, Protos.ExecutorID executorID, 
      Protos.SlaveID slaveID, byte[] bytes) {
    }
 
    @Override
    public void disconnected(SchedulerDriver schedulerDriver) {
    }
 
    @Override
    public void slaveLost(SchedulerDriver schedulerDriver, Protos.SlaveID slaveID) {
    }
 
    @Override
    public void executorLost(SchedulerDriver schedulerDriver, Protos.ExecutorID executorID, 
      Protos.SlaveID slaveID, int i) {
    }
 
    @Override
    public void error(SchedulerDriver schedulerDriver, String s) {
    }
}
```

可以看出，它主要由各种回调方法组成，特别是用于与 master 通信。

同样，执行器的实现必须实现Executor 接口：

```java
public class HelloWorldExecutor implements Executor {
    @Override
    public void registered(ExecutorDriver driver, Protos.ExecutorInfo executorInfo, 
      Protos.FrameworkInfo frameworkInfo, Protos.SlaveInfo slaveInfo) {
    }
  
    @Override
    public void reregistered(ExecutorDriver driver, Protos.SlaveInfo slaveInfo) {
    }
  
    @Override
    public void disconnected(ExecutorDriver driver) {
    }
  
    @Override
    public void launchTask(ExecutorDriver driver, Protos.TaskInfo task) {
    }
  
    @Override
    public void killTask(ExecutorDriver driver, Protos.TaskID taskId) {
    }
  
    @Override
    public void frameworkMessage(ExecutorDriver driver, byte[] data) {
    }
  
    @Override
    public void shutdown(ExecutorDriver driver) {
    }
}
```

我们将在后面的部分中看到调度程序和执行程序的操作版本。

## 4. 资源管理

### 4.1. 资源优惠

正如我们之前讨论的，代理向主服务器发布它们的资源信息。反过来，master 将这些资源提供给集群中运行的框架。此过程称为资源报价。

资源报价由两部分组成——资源和属性。

资源用于发布代理机器的内存、CPU、磁盘等硬件信息。

每个代理都有五种预定义资源：

-   中央处理器
-   显卡
-   内存
-   磁盘
-   端口

这些资源的值可以定义为以下三种类型之一：

-   标量——用来表示数值信息，使用浮点数允许小数值，比如1.5G的内存
-   范围——用于表示标量值的范围——例如，端口范围
-   Set——用于表示多个文本值

默认情况下，Mesos 代理会尝试从机器上检测这些资源。

但是，在某些情况下，我们可以在代理上配置自定义资源。此类自定义资源的值也应为上述任何一种类型。

例如，我们可以使用这些资源启动我们的代理：

```plaintext
--resources='cpus:24;gpus:2;mem:24576;disk:409600;ports:[21000-24000,30000-34000];bugs(debug_role):{a,b,c}'
```

可以看出，我们为代理配置了少量预定义资源和一个名为bugs 的自定义资源，该资源属于 集合 类型。

除了资源之外，代理还可以向主服务器发布键值属性。这些属性充当代理的附加元数据并帮助框架进行调度决策。

一个有用的例子是将代理添加到不同的机架或区域，然后在同一机架或区域上安排各种任务以实现数据局部性：

```plaintext
--attributes='rack:abc;zone:west;os:centos5;level:10;keys:[1000-1500]'
```

与资源类似，属性的值可以是标量、范围或文本类型。

### 4.2. 资源角色

许多现代操作系统都支持多用户。同样，Mesos 也支持同一集群中的多个用户。这些用户称为角色。我们可以将每个角色视为集群中的资源消费者。

正因如此，Mesos 代理可以根据不同的分配策略将资源划分到不同的角色下。此外，框架可以在集群内订阅这些角色，并对不同角色下的资源进行细粒度控制。

例如，考虑一个集群托管为组织中的不同用户提供服务的应用程序。因此，通过将资源划分为角色，每个应用程序都可以彼此隔离地工作。

此外，框架可以使用这些角色来实现数据局部性。

例如，假设我们在集群中有两个名为生产者 和 消费者的应用程序。 在这里， 生产者 将数据写入 消费者 可以随后读取的持久卷。我们可以 通过与生产者共享卷 来优化消费者 应用程序。

由于 Mesos 允许多个应用程序订阅同一个角色，我们可以将持久卷与资源角色相关联。此外，生产者 和 消费者 的框架都将订阅相同的资源角色。因此，消费者 应用程序现在可以在与生产者 应用程序相同的卷上启动数据读取任务。

### 4.3. 资源预留

现在可能会出现这样的问题，即 Mesos 如何将集群资源分配给不同的角色。Mesos 通过预留分配资源。

有两种类型的预订：

-   静态预订
-   动态预订

静态预留类似于我们在前面部分讨论的代理启动时的资源分配：

```plaintext
 --resources="cpus:4;mem:2048;cpus(baeldung):8;mem(baeldung):4096"
```

这里唯一的不同是，现在 Mesos 代理为名为baeldung的角色保留了 8 个 CPU 和 4096m 内存。

与静态预留不同，动态预留允许我们重新调整角色内的资源。Mesos 允许框架和集群操作员通过框架消息动态更改资源分配，作为对资源提供的响应或通过[HTTP 端点](https://mesos.apache.org/documentation/latest/reservation/#examples)。

Mesos 将没有任何角色的所有资源分配到名为 () 的默认角色中。Master 向所有框架提供此类资源，无论他们是否订阅了它。

### 4.4. 资源权重和配额

通常，Mesos 管理节点使用公平策略提供资源。它使用加权主导资源公平 (wDRF) 来识别缺少资源的角色。然后，master 向订阅了这些角色的框架提供更多资源。

虽然在应用程序之间公平共享资源是 Mesos 的一个重要特性，但它并不总是必要的。假设集群托管具有低资源足迹的应用程序以及具有高资源需求的应用程序。在此类部署中，我们希望根据应用程序的性质分配资源。

Mesos 允许框架通过订阅角色并为该角色添加更高的权重值来请求更多资源。因此，如果有两个角色，一个是权重 1，另一个是权重 2，Mesos 将分配两倍的公平份额资源给第二个角色。

与资源类似，我们可以通过[HTTP 端点](https://mesos.apache.org/documentation/latest/weights/#operator-http-endpoint)配置权重。

Mesos 除了确保为具有权重的角色公平分配资源外，Mesos 还确保为角色分配最少的资源。

Mesos 允许我们为资源角色添加配额。配额指定角色保证接收的最小资源量。

## 5. 实施框架

正如我们在前面的部分中讨论的那样，Mesos 允许应用程序以他们选择的语言提供框架实现。在Java中，框架是使用主类(作为框架进程的入口点)以及前面讨论的调度程序 和 执行 程序的实现来实现的。

### 5.1. 框架主类

在我们实现调度器和执行器之前，我们将首先实现我们框架的入口点：

-   向主人注册自己
-   向代理提供执行程序运行时信息
-   启动调度程序

我们将首先为 Mesos添加一个[Maven 依赖项：](https://search.maven.org/search?q=g:org.apache.mesos AND a:mesos&core=gav)

```xml
<dependency>
    <groupId>org.apache.mesos</groupId>
    <artifactId>mesos</artifactId>
    <version>0.28.3</version>
</dependency>
```

接下来，我们将为我们的框架实现 HelloWorldMain 。我们要做的第一件事就是在 Mesos 代理上启动执行程序进程：

```java
public static void main(String[] args) {
  
    String path = System.getProperty("user.dir")
      + "/target/libraries2-1.0.0-SNAPSHOT.jar";
  
    CommandInfo.URI uri = CommandInfo.URI.newBuilder().setValue(path).setExtract(false).build();
  
    String helloWorldCommand = "java -cp libraries2-1.0.0-SNAPSHOT.jar com.baeldung.mesos.executors.HelloWorldExecutor";
    CommandInfo commandInfoHelloWorld = CommandInfo.newBuilder()
      .setValue(helloWorldCommand)
      .addUris(uri)
      .build();
  
    ExecutorInfo executorHelloWorld = ExecutorInfo.newBuilder()
      .setExecutorId(Protos.ExecutorID.newBuilder()
      .setValue("HelloWorldExecutor"))
      .setCommand(commandInfoHelloWorld)
      .setName("Hello World (Java)")
      .setSource("java")
      .build();
}
```

在这里，我们首先配置了执行程序二进制位置。Mesos 代理将在框架注册时下载此二进制文件。接下来，代理将运行给定的命令来启动执行程序进程。

接下来，我们将初始化我们的框架并启动调度程序：

```java
FrameworkInfo.Builder frameworkBuilder = FrameworkInfo.newBuilder()
  .setFailoverTimeout(120000)
  .setUser("")
  .setName("Hello World Framework (Java)");
 
frameworkBuilder.setPrincipal("test-framework-java");
 
MesosSchedulerDriver driver = new MesosSchedulerDriver(new HelloWorldScheduler(),
  frameworkBuilder.build(), args[0]);
```

最后，我们将启动向 Master 注册自身的MesosSchedulerDriver。为了成功注册，我们必须将 Master 的 IP 作为程序参数 args[0]传递给这个主类：

```java
int status = driver.run() == Protos.Status.DRIVER_STOPPED ? 0 : 1;

driver.stop();

System.exit(status);
```

在上面显示的类中， CommandInfo、ExecutorInfo 和FrameworkInfo都是master 和框架之间的[protobuf 消息的Java表示。](https://www.baeldung.com/google-protocol-buffer)

### 5.2. 实施调度器

从 Mesos 1.0 开始，我们可以从任何Java应用程序调用[HTTP 端点](https://mesos.apache.org/documentation/latest/scheduler-http-api/)来向 Mesos master 发送和接收消息。其中一些消息包括，例如，框架注册、资源提议和提议拒绝。

对于Mesos 0.28 或更早版本，我们需要实现Scheduler接口：

在大多数情况下，我们将只关注 调度程序的resourceOffers方法 。让我们看看调度程序如何接收资源并根据它们初始化任务。

首先，我们将看到调度程序如何为任务分配资源：

```java
@Override
public void resourceOffers(SchedulerDriver schedulerDriver, List<Offer> list) {

    for (Offer offer : list) {
        List<TaskInfo> tasks = new ArrayList<TaskInfo>();
        Protos.TaskID taskId = Protos.TaskID.newBuilder()
          .setValue(Integer.toString(launchedTasks++)).build();

        System.out.println("Launching printHelloWorld " + taskId.getValue() + " Hello World Java");

        Protos.Resource.Builder cpus = Protos.Resource.newBuilder()
          .setName("cpus")
          .setType(Protos.Value.Type.SCALAR)
          .setScalar(Protos.Value.Scalar.newBuilder()
            .setValue(1));

        Protos.Resource.Builder mem = Protos.Resource.newBuilder()
          .setName("mem")
          .setType(Protos.Value.Type.SCALAR)
          .setScalar(Protos.Value.Scalar.newBuilder()
            .setValue(128));
```

在这里，我们为我们的任务分配了 1 个 CPU 和 128M 内存。接下来，我们将使用SchedulerDriver 在代理上启动任务：

```java
        TaskInfo printHelloWorld = TaskInfo.newBuilder()
          .setName("printHelloWorld " + taskId.getValue())
          .setTaskId(taskId)
          .setSlaveId(offer.getSlaveId())
          .addResources(cpus)
          .addResources(mem)
          .setExecutor(ExecutorInfo.newBuilder(helloWorldExecutor))
          .build();

        List<OfferID> offerIDS = new ArrayList<>();
        offerIDS.add(offer.getId());

        tasks.add(printHelloWorld);

        schedulerDriver.launchTasks(offerIDS, tasks);
    }
}
```

或者， 调度程序经常发现需要拒绝资源提供。例如，如果 调度程序 由于资源不足而无法在代理上启动任务，则它必须立即拒绝该提议：

```java
schedulerDriver.declineOffer(offer.getId());
```

### 5.3. 实施执行者

正如我们之前讨论的，框架的执行器组件负责在 Mesos 代理上执行应用程序任务。

我们使用 HTTP 端点在 Mesos 1.0中实现调度器。同样，我们可以为执行程序使用[HTTP 端点](https://mesos.apache.org/documentation/latest/executor-http-api/)。

在前面的部分中，我们讨论了框架如何配置代理以启动执行程序进程：

```plaintext
java -cp libraries2-1.0.0-SNAPSHOT.jar com.baeldung.mesos.executors.HelloWorldExecutor
```

值得注意的是，此命令将 HelloWorldExecutor 视为主类。我们将实现这个主要方法来初始化与 Mesos 代理连接的MesosExecutorDriver 以接收任务并共享其他信息，如任务状态：

```java
public class HelloWorldExecutor implements Executor {
    public static void main(String[] args) {
        MesosExecutorDriver driver = new MesosExecutorDriver(new HelloWorldExecutor());
        System.exit(driver.run() == Protos.Status.DRIVER_STOPPED ? 0 : 1);
    }
}
```

现在要做的最后一件事是接受来自框架的任务并在代理上启动它们。启动任何任务的信息都包含在HelloWorldExecutor 中：

```java
public void launchTask(ExecutorDriver driver, TaskInfo task) {
 
    Protos.TaskStatus status = Protos.TaskStatus.newBuilder()
      .setTaskId(task.getTaskId())
      .setState(Protos.TaskState.TASK_RUNNING)
      .build();
    driver.sendStatusUpdate(status);
 
    System.out.println("Execute Task!!!");
 
    status = Protos.TaskStatus.newBuilder()
      .setTaskId(task.getTaskId())
      .setState(Protos.TaskState.TASK_FINISHED)
      .build();
    driver.sendStatusUpdate(status);
}
```

当然，这只是一个简单的实现，但它解释了执行者如何在每个阶段与主控共享任务状态，然后在发送完成状态之前执行任务。

在某些情况下，执行器还可以将数据发送回调度器：

```java
String myStatus = "Hello Framework";
driver.sendFrameworkMessage(myStatus.getBytes());
```

## 六. 总结

在本文中，我们简要讨论了在同一集群中运行的应用程序之间的资源共享。我们还讨论了 Apache Mesos 如何通过 CPU 和内存等集群资源的抽象视图帮助应用程序实现最大利用率。

稍后，我们讨论了基于各种公平策略和角色在应用程序之间动态分配资源。Mesos 允许应用程序根据集群中 Mesos 代理提供的资源做出调度决策。

最后，我们看到了 Mesos 框架在Java中的实现。
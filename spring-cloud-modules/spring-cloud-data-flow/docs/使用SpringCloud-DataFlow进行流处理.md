## 1. 简介

Spring Cloud Data Flow是一种用于可组合数据微服务的云原生编程和操作模型。

借助[Spring Cloud Data Flow](https://cloud.spring.io/spring-cloud-dataflow/)，开发人员可以为常见用例(例如数据摄取、实时分析和数据导入/导出)创建和编排数据管道。

这种数据管道有两种形式，流式处理管道和批处理数据管道。

在第一种情况下，无限量的数据通过消息传递中间件被消费或产生。而在第二种情况下，短期任务处理一组有限的数据，然后终止。

本文将重点介绍流式处理。

## 2. 架构概述

这些类型的架构的关键组件是应用程序、数据流服务器和目标运行时。

此外，除了这些关键组件之外，我们通常在体系结构中还有一个Data Flow Shell和一个消息代理。

让我们更详细地了解所有这些组件。

### 2.1 应用程序

通常，流数据管道包括消费来自外部系统的事件、数据处理和多语言持久性。这些阶段在Spring Cloud术语中通常称为Source、Processor和Sink：

-   **Source**：是消费事件的应用程序
-   **Processor**：消费来自Source的数据，对其进行一些处理，并将处理后的数据发送到管道中的下一个应用程序
-   **Sink**：从Source或Processor消费并将数据写入所需的持久层

这些应用程序可以通过两种方式打包：

-   托管在Maven仓库、文件、http或任何其他Spring资源实现中的Spring Boot uber-jar(本文将使用此方法)
-   Docker

Spring Cloud Data Flow团队已经提供了许多用于常见用例(例如jdbc、hdfs、http、router)的源、处理器和接收器应用程序，并可供使用。

### 2.2 运行时

此外，这些应用程序的执行需要运行时。支持的运行时包括：

-   Cloud Foundry
-   Apache YARN
-   Kubernetes
-   Apache Mesos
-   用于开发Local Server(将在本文中使用)

### 2.3 Data Flow Server

负责将应用程序部署到运行时的组件是数据流服务器。为每个目标运行时提供了一个数据流服务器可执行jar。

数据流服务器负责解释：

-   描述通过多个应用程序的数据逻辑流的流DSL
-   描述应用程序到运行时的映射的部署清单

### 2.4 Data Flow Shell

Data Flow Shell是Data Flow Server的客户端。shell允许我们执行与服务器交互所需的DSL命令。

例如，描述从http源到jdbc接收器的数据流的DSL将写为“http | jdbc”。DSL中的这些名称在数据流服务器中注册，并映射到可以托管在Maven或Docker仓库中的应用程序工件。

Spring还提供了一个名为Flo的图形界面，用于创建和监控流数据管道。但是，它的使用不在本文的讨论范围之内。

### 2.5 消息代理

正如我们在上一节的示例中看到的，我们在数据流的定义中使用了管道符号。管道符号表示两个应用程序之间通过消息传递中间件进行的通信。

这意味着我们需要一个在目标环境中启动并运行的消息代理。

支持的两个消息传递中间件代理是：

-   Apache Kafka
-   RabbitMQ

因此，现在我们已经对架构组件有了一个概述-**是时候构建我们的第一个流处理管道了**。

## 3. 安装消息代理

正如我们所看到的，管道中的应用程序需要一个消息传递中间件来进行通信。出于本文的目的，我们将使用RabbitMQ。

有关安装的完整详细信息，你可以按照[官方网站](https://www.rabbitmq.com/download.html)上的说明进行操作。

## 4. 本地数据流服务器

为了加快生成应用程序的过程，我们将使用[Spring Initializr](https://start.spring.io/)；在它的帮助下，我们可以在几分钟内获得我们的Spring Boot应用程序。

到达网站后，只需选择一个Group和一个Artifact名称。

完成此操作后，单击“Generate Project”按钮开始下载Maven工件。

<img src="../assets/img.png">

下载完成后，解压项目并将其作为Maven项目导入到你选择的IDE中。

让我们向项目添加一个Maven依赖项。由于我们需要数据流本地服务器库，因此让我们添加[spring-cloud-starter-dataflow-server-local](https://search.maven.org/search?q=spring-cloud-starter-dataflow-server-local)依赖项：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-dataflow-server-local</artifactId>
</dependency>
```

现在我们需要用@EnableDataFlowServer注解来标注Spring Boot主类：

```java
@EnableDataFlowServer
@SpringBootApplication
public class SpringDataFlowServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringDataFlowServerApplication.class, args);
    }
}
```

就这样。我们的本地数据流服务器已准备好执行：

```shell
mvn spring-boot:run
```

该应用程序将在端口9393上启动。

## 5. Data Flow Shell

再次，转到Spring Initializr并选择一个Group和Artifact名称。

下载并导入项目后，让我们添加一个[spring-cloud-dataflow-shell](https://search.maven.org/search?q=a:spring-cloud-dataflow-shell)依赖项：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-dataflow-shell</artifactId>
</dependency>
```

现在我们需要在Spring Boot主类中添加@EnableDataFlowShell注解：

```java
@EnableDataFlowShell
@SpringBootApplication
public class SpringDataFlowShellApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringDataFlowShellApplication.class, args);
    }
}
```

我们现在可以运行shell：

```shell
mvn spring-boot:run
```

shell运行后，我们可以在提示符中键入help命令以查看我们可以执行的命令的完整列表。

## 6. 源应用程序

同样，在Spring Initializr上，我们现在将创建一个简单的应用程序并添加一个名为[spring-cloud-starter-stream-rabbit](https://search.maven.org/search?q=a:spring-cloud-starter-stream-rabbit)的Stream Rabbit依赖项：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-stream-rabbit</artifactId>
</dependency>
```

然后我们将@EnableBinding(Source.class)注解添加到Spring Boot主类：

```java
@EnableBinding(Source.class)
@SpringBootApplication
public class SpringDataFlowTimeSourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringDataFlowTimeSourceApplication.class, args);
    }
}
```

现在我们需要定义必须处理的数据源。此源可能是任何潜在的无穷无尽的工作负载(物联网传感器数据、24/7事件处理、在线交易数据摄取)。

**在我们的示例应用程序中，我们使用Poller每10秒生成一个事件(为简单起见，使用new的时间戳)**。

@InboundChannelAdapter注解将消息发送到源的输出通道，使用返回值作为消息的有效负载：

```java
@Bean
@InboundChannelAdapter(
    value = Source.OUTPUT, 
    poller = @Poller(fixedDelay = "10000", maxMessagesPerPoll = "1")
)
public MessageSource<Long> timeMessageSource() {
    return () -> MessageBuilder.withPayload(new Date().getTime()).build();
}
```

我们的数据源已准备就绪。

## 7. 处理器应用程序

接下来，我们将创建一个应用程序并添加Stream Rabbit依赖项。

然后我们将@EnableBinding(Processor.class)注解添加到Spring Boot主类：

```java
@EnableBinding(Processor.class)
@SpringBootApplication
public class SpringDataFlowTimeProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringDataFlowTimeProcessorApplication.class, args);
    }
}
```

接下来，我们需要定义一个方法来处理来自源应用程序的数据。

要定义一个转换器，我们需要用@Transformer注解来标注这个方法：

```java
@Transformer(inputChannel = Processor.INPUT, 
    outputChannel = Processor.OUTPUT)
public Object transform(Long timestamp) {

    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:yy");
    String date = dateFormat.format(timestamp);
    return date;
}
```

它将时间戳从“输入”通道转换为格式化日期，该日期将发送到“输出”通道。

## 8. 接收器应用程序

**最后一个要创建的应用程序是接收器应用程序**。

再次，转到Spring Initializr并选择一个Group，一个Artifact名称。下载项目后，让我们添加Stream Rabbit依赖项。

然后在Spring Boot主类中添加@EnableBinding(Sink.class)注解：

```java
@EnableBinding(Sink.class)
@SpringBootApplication
public class SpringDataFlowLoggingSinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringDataFlowLoggingSinkApplication.class, args);
    }
}
```

现在我们需要一种方法来拦截来自处理器应用程序的消息。

为此，我们需要将@StreamListener(Sink.INPUT)注解添加到我们的方法中：

```java
@StreamListener(Sink.INPUT)
public void loggerSink(String date) {
    logger.info("Received: " + date);
}
```

该方法只是将以格式化日期形式转换的时间戳打印到日志文件中。

## 9. 注册一个Stream App

Spring Cloud Data Flow Shell允许我们使用app register命令向App Registry注册一个Stream App。

我们必须提供一个唯一的名称、应用程序类型和一个可以解析为应用工件的URI。对于类型，指定“source”、“processor ”或“sink”。

使用Maven方案提供URI时，格式应符合以下内容：

```shell
maven://<groupId>:<artifactId>[:<extension>[:<classifier>]]:<version>
```

要注册之前创建的Source、Processor和Sink应用程序，请转到Spring Cloud Data Flow Shell并从提示符执行以下命令：

```shell
app register --name time-source --type source 
  --uri maven://com.baeldung.spring.cloud:spring-data-flow-time-source:jar:1.0.0

app register --name time-processor --type processor 
  --uri maven://com.baeldung.spring.cloud:spring-data-flow-time-processor:jar:1.0.0

app register --name logging-sink --type sink 
  --uri maven://com.baeldung.spring.cloud:spring-data-flow-logging-sink:jar:1.0.0
```

## 10. 创建和部署流

要创建新的流定义，请转到Spring Cloud Data Flow Shell并执行以下shell命令：

```shell
stream create --name time-to-log 
  --definition 'time-source | time-processor | logging-sink'
```

这根据DSL表达式'time-source | time-processor | logging-sink'定义了一个名为time-to-log的流。

然后，要部署流，请执行以下shell命令：

```shell
stream deploy --name time-to-log
```

数据流服务器将time-source、time-processor和logging-sink解析为Maven坐标，并使用这些坐标来启动流的time-source、time-processor和logging-sink应用程序。

如果流已正确部署，你将在数据流服务器日志中看到模块已启动并绑定在一起：

```shell
2016-08-24 12:29:10.516  INFO 8096 --- [io-9393-exec-10] o.s.c.d.spi.local.LocalAppDeployer: deploying app time-to-log.logging-sink instance 0
   Logs will be in PATH_TO_LOG/spring-cloud-dataflow-1276836171391672089/time-to-log-1472034549734/time-to-log.logging-sink
2016-08-24 12:29:17.600  INFO 8096 --- [io-9393-exec-10] o.s.c.d.spi.local.LocalAppDeployer       : deploying app time-to-log.time-processor instance 0
   Logs will be in PATH_TO_LOG/spring-cloud-dataflow-1276836171391672089/time-to-log-1472034556862/time-to-log.time-processor
2016-08-24 12:29:23.280  INFO 8096 --- [io-9393-exec-10] o.s.c.d.spi.local.LocalAppDeployer       : deploying app time-to-log.time-source instance 0
   Logs will be in PATH_TO_LOG/spring-cloud-dataflow-1276836171391672089/time-to-log-1472034562861/time-to-log.time-source
```

## 11. 查看结果

在此示例中，源只是每秒将当前时间戳作为消息发送，处理器对其进行格式化，日志接收器使用日志记录框架输出格式化的时间戳。

日志文件位于数据流服务器日志输出中显示的目录中，如上所示。要查看结果，我们可以跟踪日志：

```shell
tail -f PATH_TO_LOG/spring-cloud-dataflow-1276836171391672089/time-to-log-1472034549734/time-to-log.logging-sink/stdout_0.log
2016-08-24 12:40:42.029  INFO 9488 --- [r.time-to-log-1] s.c.SpringDataFlowLoggingSinkApplication : Received: 2016/08/24 11:40:01
2016-08-24 12:40:52.035  INFO 9488 --- [r.time-to-log-1] s.c.SpringDataFlowLoggingSinkApplication : Received: 2016/08/24 11:40:11
2016-08-24 12:41:02.030  INFO 9488 --- [r.time-to-log-1] s.c.SpringDataFlowLoggingSinkApplication : Received: 2016/08/24 11:40:21
```

## 12. 总结

在本文中，我们了解了如何通过使用Spring Cloud Data Flow构建用于流处理的数据管道。

此外，我们还看到了源、处理器和接收器应用程序在流中的作用，以及如何通过使用Data Flow Shell将此模块插入和绑定到数据流服务器中。
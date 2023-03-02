## 1. 简介

本文将主要通过一些实际的例子来**介绍Spring Integration的核心概念**。

Spring Integration提供了许多强大的组件，可以极大地增强企业架构内系统和进程的互连性。

它体现了一些最好和最流行的设计模式，帮助开发人员避免自己动手。

我们将看看这个库满足企业应用程序的具体需求，以及为什么它比它的一些替代品更可取。我们还将研究一些可用的工具，以进一步简化基于Spring Integration的应用程序的开发。

## 2. 设置

```xml
<dependency>
    <groupId>org.springframework.integration</groupId>
    <artifactId>spring-integration-core</artifactId>
    <version>4.3.5.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.springframework.integration</groupId>
    <artifactId>spring-integration-file</artifactId>
    <version>4.3.5.RELEASE</version>
</dependency>
```

你可以从Maven Central下载最新版本的[Spring Integration Core](https://central.sonatype.com/artifact/org.springframework.integration/spring-integration-core/6.0.3)和[Spring Integration File Support](https://central.sonatype.com/artifact/org.springframework.integration/spring-integration-file/6.0.3)。

## 3. 消息传递模式

该库中的基本模式之一是消息传递。该模式以消息为中心-离散的数据有效负载通过预定义的通道从原始系统或进程移动到一个或多个系统或进程。

从历史上看，该模式作为集成多个不同系统的最灵活方式出现，其方式如下：

-   几乎完全解耦了集成中涉及的系统
-   允许集成中的参与者系统完全不知道彼此的底层协议、格式或其他实现细节
-   鼓励集成中涉及的组件的开发和重用

## 4. 消息集成实战

让我们考虑一个将MPEG视频文件从指定文件夹复制到另一个配置文件夹的基本示例：

```java
@Configuration
@EnableIntegration
public class BasicIntegrationConfig{
    public String INPUT_DIR = "the_source_dir";
    public String OUTPUT_DIR = "the_dest_dir";
    public String FILE_PATTERN = "*.mpeg";

    @Bean
    public MessageChannel fileChannel() {
        return new DirectChannel();
    }

    @Bean
    @InboundChannelAdapter(value = "fileChannel", poller = @Poller(fixedDelay = "1000"))
    public MessageSource<File> fileReadingMessageSource() {
        FileReadingMessageSource sourceReader= new FileReadingMessageSource();
        sourceReader.setDirectory(new File(INPUT_DIR));
        sourceReader.setFilter(new SimplePatternFileListFilter(FILE_PATTERN));
        return sourceReader;
    }

    @Bean
    @ServiceActivator(inputChannel= "fileChannel")
    public MessageHandler fileWritingMessageHandler() {
        FileWritingMessageHandler handler = new FileWritingMessageHandler(new File(OUTPUT_DIR));
        handler.setFileExistsMode(FileExistsMode.REPLACE);
        handler.setExpectReply(false);
        return handler;
    }
}
```

上面的代码配置了一个服务激活器、一个集成通道和一个入站通道适配器。

稍后我们将更详细地研究这些组件类型中的每一种。@EnableIntegration注解将此类指定为Spring Integration配置。

让我们开始我们的Spring Integration应用程序上下文：

```java
public static void main(String... args) {
    AbstractApplicationContext context = new AnnotationConfigApplicationContext(BasicIntegrationConfig.class);
    context.registerShutdownHook();
    
    Scanner scanner = new Scanner(System.in);
    System.out.print("Please enter q and press <enter> to exit the program: ");
    
    while (true) {
        String input = scanner.nextLine();
        if("q".equals(input.trim())) {
            break;
        }
    }
    System.exit(0);
}
```

上面的main方法启动集成上下文；它还接收从命令行输入的“q”字符以退出程序。让我们更详细地检查组件。

## 5. Spring Integration组件

### 5.1 Message

org.springframework.integration.Message接口定义了Spring消息：Spring Integration上下文中的数据传输单元。

```java
public interface Message<T> {
    T getPayload();

    MessageHeaders getHeaders();
}
```

它定义了两个关键元素的访问器：

-   消息标头，本质上是一个键值容器，可以用来传输元数据，定义在org.springframework.integration.MessageHeaders类中
-   消息有效负载，这是要传输的有价值的实际数据-在我们的用例中，视频文件是有效负载

### 5.2 Channel

Spring Integration(实际上，EAI)中的通道是集成架构中的基本管道。它是将消息从一个系统中继到另一个系统的管道。

你可以将其视为文字管道，集成系统或进程可以通过它向其他系统推送消息(或从其他系统接收消息)。

Spring Integration中的通道有多种形式，具体取决于你的需要。它们在很大程度上是可配置的，开箱即用，无需任何自定义代码，但如果你有自定义需求，可以使用强大的框架。

**点对点(P2P)**通道用于在系统或组件之间建立一对一的通信线路。一个组件向通道发布一条消息，以便另一个组件可以接收它。通道的每一端只能有一个组件。

正如我们所看到的，配置通道就像返回DirectChannel的实例一样简单：

```java
@Bean
public MessageChannel fileChannel1() {
    return new DirectChannel();
}

@Bean
public MessageChannel fileChannel2() {
    return new DirectChannel();
}

@Bean
public MessageChannel fileChannel3() {
    return new DirectChannel();
}
```

在这里，我们定义了三个独立的通道，所有通道都由各自的getter方法的名称标识。

**发布-订阅(Pub-Sub)**通道用于在系统或组件之间建立一对多的通信线路。这将使我们能够发布到我们之前创建的所有3个直接通道。

因此，按照我们的示例，我们可以用发布-订阅通道替换P2P通道：

```java
@Bean
public MessageChannel pubSubFileChannel() {
    return new PublishSubscribeChannel();
}

@Bean
@InboundChannelAdapter(value = "pubSubFileChannel", poller = @Poller(fixedDelay = "1000"))
public MessageSource<File> fileReadingMessageSource() {
    FileReadingMessageSource sourceReader = new FileReadingMessageSource();
    sourceReader.setDirectory(new File(INPUT_DIR));
    sourceReader.setFilter(new SimplePatternFileListFilter(FILE_PATTERN));
    return sourceReader;
}
```

我们现在已经将入站通道适配器转换为发布到Pub-Sub通道。这将允许我们将正在从源文件夹中读取的文件发送到多个目的地。

### 5.3 Bridge

Spring Integration中的桥用于连接两个消息通道或适配器，如果它们由于任何原因无法直接连接。

在我们的例子中，我们可以使用桥将我们的Pub-Sub通道连接到三个不同的P2P通道(因为P2P和Pub-Sub通道不能直接连接)：

```java
@Bean
@BridgeFrom(value = "pubSubFileChannel")
public MessageChannel fileChannel1() {
    return new DirectChannel();
}

@Bean
@BridgeFrom(value = "pubSubFileChannel")
public MessageChannel fileChannel2() {
    return new DirectChannel();
}

@Bean
@BridgeFrom(value = "pubSubFileChannel")
public MessageChannel fileChannel3() {
    return new DirectChannel();
}
```

上面的bean配置现在将pubSubFileChannel桥接到三个P2P通道。@BridgeFrom注解定义了一个桥，可以应用于需要订阅Pub-Sub通道的任意数量的通道。

我们可以将上面的代码理解为“创建一个从pubSubFileChannel到fileChannel1、fileChannel2 和fileChannel3的桥，以便来自pubSubFileChannel的消息可以同时馈送到所有三个通道。”

### 5.4 Service Activator

Service Activator是在给定方法上定义@ServiceActivator注解的任何POJO。这允许我们在从入站通道接收到消息时在我们的POJO上执行任何方法，并且它允许我们将消息写入外向通道。

在我们的示例中，我们的服务激活器从配置的输入通道接收文件并将其写入配置的文件夹。

### 5.5 Adapter

Adapter是一种基于企业集成模式的组件，允许“插入”到系统或数据源。正如我们所知，它几乎就是一个适配器，可以插入墙上的插座或电子设备。

它允许与其他“黑盒”系统(如数据库、FTP服务器和消息传递系统(如JMS、AMQP)和社交网络(如Twitter))的可重用连接。连接到这些系统的需求无处不在，这意味着适配器非常便携且可重复使用(事实上，有一个小的[适配器目录](https://docs.spring.io/spring-integration/docs/5.1.0.M1/reference/html/endpoint-summary.html)，免费提供，任何人都可以使用)。

**适配器分为两大类-入站和出站**。

让我们在示例场景中使用的适配器的上下文中检查这些类别：

正如我们所看到的，入站适配器用于从外部系统(在本例中为文件系统目录)引入消息。

我们的入站适配器配置包括：

-   将bean配置标记为适配器的@InboundChannelAdapter注解-我们配置适配器将其消息(在我们的例子中是一个MPEG文件)馈送到的通道和一个poller，一个帮助适配器轮询配置文件夹的组件指定间隔
-   返回FileReadingMessageSource的标准Spring Java配置类，处理文件系统轮询的Spring Integration类实现

出站适配器用于向外发送消息。Spring Integration支持适用于各种常见用例的各种开箱即用的适配器。

## 6. 总结

我们已经检查了Spring Integration的一个基本用例，它演示了库的基于Java的配置和可用组件的可重用性。

Spring Integration代码可以作为独立项目部署在Java SE中，也可以作为Jakarta EE环境中更大项目的一部分进行部署。虽然它不直接与其他以EAI为中心的产品和模式(如企业服务总线(ESB))竞争，但它是解决许多ESB旨在解决的相同问题的可行、轻量级替代方案。
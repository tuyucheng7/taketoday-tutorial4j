## 1. 概述

本文将主要通过实用的例子来介绍Spring Integration的核心概念。

Spring Integration提供了许多强大的组件，可以极大地增强企业架构中系统和流程的互连性。

它体现了一些最好和最流行的设计模式，帮助开发人员避免自己动手。

我们将看看这个库满足企业应用程序的特定需求，以及为什么它比它的一些替代品更可取。
我们还将研究一些可用的工具，以进一步简化基于Spring Integration的应用程序的开发。

## 2. 依赖

```
<dependency>
  <groupId>org.springframework.integration</groupId>
  <artifactId>spring-integration-core</artifactId>
  <version>5.0.13.RELEASE</version>
</dependency>
<dependency>
  <groupId>org.springframework.integration</groupId>
  <artifactId>spring-integration-file</artifactId>
  <version>5.0.13.RELEASE</version>
</dependency>
```

## 3. 消息模式

这个库中的基础模式之一是消息传递。该模式以消息为中心-离散的数据有效负载通过预定义的通道从一个原始系统或进程移动到一个或多个系统或进程。

从历史上看，该模式是集成多个不同系统的最灵活方式，其方式如下：

+ 几乎完全解耦了集成中涉及的系统。
+ 允许集成中的参与者系统完全不知道彼此的底层协议、格式或其他实现细节。
+ 鼓励集成中涉及的组件的开发和重用。

## 4. 消息传递集成实践

让我们考虑一个将JPG图片文件从指定文件夹到另一个配置文件夹的基本示例：

```java

@Configuration
@EnableIntegration
public class FileCopyConfig {
  public final String INPUT_DIR = "source";
  public final String OUTPUT_DIR = "target";
  public final String FILE_PATTERN = ".jpg";

  @Bean
  public MessageChannel fileChannel() {
    return new DirectChannel();
  }

  @Bean
  @InboundChannelAdapter(value = "fileChannel", poller = @Poller(fixedDelay = "10000"))
  public MessageSource<File> fileReadingMessageSource() {
    FileReadingMessageSource sourceReading = new FileReadingMessageSource();
    sourceReading.setDirectory(new File(INPUT_DIR));
    sourceReading.setFilter(new SimplePatternFileListFilter(FILE_PATTERN));
    return sourceReading;
  }

  @Bean
  @ServiceActivator(inputChannel = "fileChannel")
  public MessageHandler fileWritingMessageHandler() {
    FileWritingMessageHandler handler = new FileWritingMessageHandler(new File(OUTPUT_DIR));
    handler.setFileExistsMode(FileExistsMode.REPLACE);
    handler.setExpectReply(false);
    return handler;
  }
}
```

上面的代码配置了一个服务激活器、一个集成通道和一个入站通道适配器。

稍后我们会更详细地介绍这些组件类型中的每一个。@EnableIntegration注解将此类指定为Spring Integration配置。

让我们开始我们的Spring Integration应用程序上下文：

```java
public class FileCopyConfig {

  public static void main(String[] args) {
    final AbstractApplicationContext context = new AnnotationConfigApplicationContext(FileCopyConfig.class);
    context.registerShutdownHook();
    final Scanner scanner = new Scanner(System.in);
    System.out.println("Please enter a string and press <enter>: ");
    while (true) {
      final String input = scanner.nextLine();
      if ("q".equals(input.trim())) {
        context.close();
        scanner.close();
        break;
      }
    }
    System.exit(0);
  }
}
```

上面的main方法启动了集成上下文；它还接受从命令行输入的“q”字符以退出程序。让我们更详细地检查这些组件。

## 5. Spring集成组件

### 5.1 Message

org.springframework.integration.Message接口定义了spring消息：Spring集成上下文中的数据传输单元。

```java
public interface Message<T> {
  T getPayload();

  MessageHeaders getHeaders();
}
```

它定义了两个关键元素的访问器：

+ 消息头，本质上是一个键值容器，可用于传输元数据，定义在org.springframework.integration.MessageHeaders类中。
+ 消息有效负载，这是要传输的有价值的实际数据-在我们的用例中，图片文件是有效负载。

### 5.2 Channel

Spring Integration(实际上是EAI)中的通道是集成架构中的基本管道。它是将消息从一个系统中继到另一个系统的管道。

你可以将其视为文字管道，集成系统或进程可以通过该管道将消息推送到其他系统(或从其他系统接收消息)。

Spring Integration中的通道有多种风格，具体取决于你的需要。
它们在很大程度上是开箱即用的可配置和可用的，无需任何自定义代码，但如果你有自定义需求，可以使用强大的框架。

点对点(P2P)通道用于在系统或组件之间建立一对一的通信线路。
一个组件向通道发布消息，以便另一个组件可以接收它。通道的每一端只能有一个组件。

正如我们所见，配置通道就像返回一个DirectChannel实例一样简单：

```java

@Configuration
@EnableIntegration
public class FileCopyConfig {

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
}
```

在这里，我们定义了三个单独的通道，它们都由它们各自的getter方法的名称标识。

发布-订阅(Pub-Sub)通道用于在系统或组件之间建立一对多的通信线路。这将允许我们发布到我们之前创建的所有3个直接通道。

因此，按照我们的示例，我们可以将P2P通道替换为pub-sub通道：

```java

@Configuration
@EnableIntegration
public class FileCopyConfig {

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
}
```

我们现在已将入站通道适配器转换为发布到Pub-Sub通道。这允许我们将从源文件夹中读取的文件发送到多个目的地。

### 5.3 Bridge

Spring Integration中的桥用于连接两个消息通道或适配器，如果由于某种原因它们无法直接连接。

在我们的例子中，我们可以使用桥接器将我们的Pub-Sub通道连接到三个不同的P2P通道(因为P2P和Pub-Sub通道不能直接连接)：

```java

@Configuration
@EnableIntegration
public class FileCopyConfig {

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
}
```

上面的bean配置现在将pubSubFileChannel桥接到三个P2P通道。
@BridgeFrom注解定义了一个桥梁，可以应用于需要订阅Pub-Sub通道的任意数量的通道。

我们可以将上面的代码视为“创建一个从pubSubFileChannel到fileChannel1、fileChannel2和fileChannel3的桥，
以便来自pubSubFileChannel的消息可以同时发送到所有三个通道。”

### 5.4 Service Activator

Service Activator是在给定方法上定义@ServiceActivator注解的任何POJO。
这允许我们在从入站通道接收到消息时在POJO上执行任何方法，并允许我们将消息写入出站通道。

在我们的示例中，我们的服务激活器从配置的输入通道接收文件并将其写入配置的文件夹。

### 5.5 Adapter

适配器是一种基于企业集成模式的组件，它允许“插入”到系统或数据源。正如我们从插入墙上插座或电子设备中所知道的那样，它几乎就是一个适配器。

它允许与其他“黑盒”系统(如数据库、FTP服务器和消息系统(如JMS、AMQP)和社交网络(如Twitter))进行可重复使用的连接。
连接到这些系统的需求无处不在，这意味着适配器非常便携和可重复使用(事实上，有一小部分适配器，免费提供，任何人都可以使用)。

适配器分为两大类-inbound和outbound。

让我们在示例场景中使用的适配器的上下文中检查这些类别：

正如我们所见，Inbound adapters用于从外部系统(在本例中为文件系统目录)引入消息。

我们的Inbound适配器配置包括：

+ 一个@InboundChannelAdapter注解，将bean配置标记为适配器-我们配置适配器将向其提供消息的通道(在我们的例子中，一个JPG文件)和一个poller，
  一个帮助适配器轮询配置文件夹的组件指定间隔。
+ 一个标准的Spring java配置类，它返回一个FileReadingMessageSource，处理文件系统轮询的Spring Integration类实现。

Outbound adapters用于向外发送消息。Spring Integration支持多种开箱即用的适配器，适用于各种常见用例。

## 6. 总结

我们介绍了Spring Integration的一个基本用例，它演示了基于java的配置和可用组件的可重用性。

Spring Integration代码可作为JavaSE中的独立项目部署，也可作为Jakarta EE环境中更大项目的一部分进行部署。
虽然它不直接与其他以EAI为中心的产品和模式(如企业服务总线(ESB))竞争，但它是解决许多ESB旨在解决的相同问题的可行的轻量级替代方案。
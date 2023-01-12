## 1. 概述

在本教程中，我们将介绍用于创建应用程序集成的Spring IntegrationJavaDSL。

我们将采用我们在Spring Integration简介中构建的文件移动集成，并改用DSL。

## 2. 依赖

Spring IntegrationJavaDSL是Spring Integration Core的一部分。

因此，我们可以添加该依赖项：

```
<dependency>
  <groupId>org.springframework.integration</groupId>
  <artifactId>spring-integration-core</artifactId>
  <version>5.0.13.RELEASE</version>
</dependency>
```

为了处理我们的文件移动应用程序，我们还需要Spring Integration File：

```
<dependency>
  <groupId>org.springframework.integration</groupId>
  <artifactId>spring-integration-file</artifactId>
  <version>5.0.13.RELEASE</version>
</dependency>
```

## 3. Spring IntegrationJavaDSL

在Java DSL之前，用户使用XML配置Spring Integration组件。

DSL引入了一些流式的构建器，我们可以从中轻松地创建一个完整的纯Java的Spring Integration管道。

所以，假设我们想创建一个通道，将通过管道的任何数据大写。

过去，我们可能需要这么做：

```
<int:channel id="input"/>

<int:transformer input-channel="input" expression="payload.toUpperCase()" />
```

现在我们可以改为：

```java

@Configuration
@EnableIntegration
@IntegrationComponentScan
public class JavaDSLFileCopyConfig {

  @Bean
  public IntegrationFlow upcaseFlow() {
    return IntegrationFlows.from("input")
        .transform(String::toUpperCase)
        .get();
  }
}
```

## 4. 文件移动应用程序

要开始我们的文件移动集成，我们需要一些简单的构建块。

### 4.1 集成流

我们需要的第一个构建块是集成流，我们可以从IntegrationFlows构建器中获得它：

```
IntegrationFlows.from(...)
```

from可以有多种类型，但在本教程中，我们只关注三种：

+ MessageSources
+ MessageChannels
+ Strings

在我们调用from之后，我们现在可以使用一些自定义方法：

```
IntegrationFlow flow = IntegrationFlows.from(sourceDirectory())
  .filter(onlyJpgs())
  .handle(targetDirectory())
  // 添加更多组件
  .get();
```

最终，IntegrationFlows将始终生成一个IntegrationFlow实例，它是任何Spring Integration应用程序的最终产品。

这种接收输入、执行适当的转换并发出结果的模式是所有Spring Integration应用程序的基础。

### 4.2 描述输入源

首先，要移动文件，我们需要向集成流指明它应该在哪里寻找它们，为此，我们需要一个MessageSource：

```
@Bean
public MessageSource<File> sourceDirectory() {
  // 创建消息源...
}
```

简单地说，MessageSource是应用程序外部消息可以来自的地方。

更具体地说，我们需要一些可以将该外部源适配到Spring消息传递表示中的东西。而且由于这种适配侧重于输入，因此这些通常称为输入通道适配器。

spring-integration-file依赖项为我们提供了一个非常适合我们用例的输入通道适配器：FileReadingMessageSource：

```java
public class JavaDSLFileCopyConfig {
  public static final String INPUT_DIR = "D:java-workspaceintellij-workspacespring-examplesspring-integrationsrcmainresourcessource";
  public static final String OUTPUT_DIR = "D:java-workspaceintellij-workspacespring-examplesspring-integrationsrcmainresourcestarget";
  public static final String OUTPUT_DIR2 = "D:java-workspaceintellij-workspacespring-examplesspring-integrationsrcmainresourcestarget2";

  @Bean
  public MessageSource<File> sourceDirectory() {
    FileReadingMessageSource messageSource = new FileReadingMessageSource();
    messageSource.setDirectory(new File(INPUT_DIR));
    return messageSource;
  }
}
```

在这里，我们的FileReadingMessageSource将读取INPUT_DIR给定的目录，并从中创建一个MessageSource。

让我们在IntegrationFlows.from调用中将其指定为源：

```
IntegrationFlows.from(sourceDirectory());
```

### 4.3 配置输入源

现在，如果我们将其视为一个长期存在的应用程序，我们可能希望能够在文件进入时注意到它们，而不仅仅是在启动时移动已经存在的文件。

为了方便这一点，from还可以使用额外的配置器作为输入源的进一步定制：

```
IntegrationFlows.from(sourceDirectory(), configurer -> configurer.poller(Pollers.fixedDelay(10000)));
```

在这种情况下，我们可以通过告诉Spring Integration每10秒轮询一次该源(在这种情况下是我们的文件系统)来使我们的输入源更具弹性。

当然，这不仅仅适用于我们的文件输入源，我们可以将此轮询器添加到任何MessageSource。

### 4.4 从输入源过滤消息

接下来，假设我们希望我们的文件移动应用程序仅移动特定文件，例如具有jpg扩展名的图像文件。

为此，我们可以使用GenericSelector：

```java
public class JavaDSLFileCopyConfig {

  @Bean
  public GenericSelector<File> onlyJpgs() {
    return source -> source.getName().endsWith(".jpg");
  }
}
```

所以，让我们再次更新我们的集成流程：

```
IntegrationFlows.from(sourceDirectory()).filter(onlyJpgs());
```

或者，因为这个过滤器非常简单，我们可以使用lambda来定义它：

```
IntegrationFlows.from(sourceDirectory()).filter(source -> ((File) source).getName().endsWith(".jpg"));
```

### 4.5 使用服务激活器处理消息

现在我们有一个过滤的文件列表，我们需要将它们写入一个新位置。

当我们在Spring Integration中考虑输出时，我们会使用服务激活器。

让我们使用spring-integration-file中的FileWritingMessageHandler服务激活器：

```java
public class JavaDSLFileCopyConfig {

  @Bean
  public MessageHandler targetDirectory() {
    FileWritingMessageHandler handler = new FileWritingMessageHandler(new File(OUTPUT_DIR));
    handler.setFileExistsMode(FileExistsMode.REPLACE);
    handler.setExpectReply(false);
    return handler;
  }
}
```

在这里，我们的FileWritingMessageHandler会将它接收到的每个消息负载写入OUTPUT_DIR。

再次，让我们更新：

```
IntegrationFlows.from(sourceDirectory())
  .filter(onlyJpgs())
  .handle(targetDirectory());
```

顺便注意一下setExpectReply的用法。因为集成流可以是双向的，所以这个调用表明这个特定的管道是单向方式。

### 4.6 激活我们的集成流程

添加完所有组件后，我们需要将IntegrationFlow注册为bean以激活它：

```java
public class JavaDSLFileCopyConfig {

  @Bean
  public IntegrationFlow fileMover() {
    return IntegrationFlows.from(sourceDirectory(), c -> c.poller(Pollers.fixedDelay(10000)))
        .filter(onlyJpgs())
        .handle(targetDirectory())
        .get();
  }
}
```

get方法提取我们需要注册为Spring Bean的IntegrationFlow实例。

一旦我们的应用程序上下文加载，我们的IntegrationFlow中包含的所有组件都会被激活。

现在，我们的应用程序会开始将文件从源目录移动到目标目录。

## 5. 其他组件

在我们基于DSL的文件移动应用程序中，我们创建了一个入站通道适配器、一个消息过滤器和一个服务激活器。

让我们看看其他一些常见的Spring Integration组件，看看我们如何使用它们。

### 5.1 Message Channels

如前所述，消息通道是另一种初始化流的方式：

```
IntegrationFlows.from("anyChannel")
```

我们可以将其解读为“请找到或创建一个名为anyChannel的通道bean。然后，读取从其他流馈送到anyChannel的任何数据。”

但是，实际上它比这更通用。

简单地说，通道将生产者与消费者抽象出来，我们可以将其视为Java队列。可以在流中的任何点插入通道。

例如，假设我们希望在文件从一个目录移动到下一个目录时对其进行优先级排序：

```java
public class JavaDSLFileCopyConfig {

  @Bean
  public PriorityChannel alphabetically() {
    return new PriorityChannel(1000, (left, right) ->
        ((File) left.getPayload()).getName().compareTo(
            ((File) right.getPayload()).getName()));
  }
}
```

然后，我们可以在我们的流程之间插入对通道的调用：

```java
public class JavaDSLFileCopyConfig {

  @Bean
  public IntegrationFlow fileMover() {
    return IntegrationFlows.from(sourceDirectory())
        .filter(onlyJpgs())
        .channel("alphabetically")
        .handle(targetDirectory())
        .get();
  }
}
```

有几十个通道可供选择，其中一些更方便的通道用于并发、审计或中间持久性(想想Kafka或JMS缓冲区)。

此外，与Bridges结合使用时，channel会非常强大。

### 5.2 Bridge

当我们想组合两个通道时，我们使用桥接器。

让我们想象一下，我们没有直接写入输出目录，而是让文件移动应用程序写入另一个通道：

```java
public class JavaDSLFileCopyConfig {

  @Bean
  public IntegrationFlow fileReader() {
    return IntegrationFlows.from(sourceDirectory())
        .filter(onlyJpgs())
        .channel("holdingTank")
        .get();
  }
}
```

现在，因为我们只是将它写入一个通道，我们可以从那里连接到其他流。

让我们创建一个桥，轮询我们的容器以获取消息并将它们写入目的地：

```java
public class JavaDSLFileCopyConfig {
  
  @Bean
  public IntegrationFlow fileWriter() {
    return IntegrationFlows.from("holdingTank")
        .bridge(e -> e.poller(Pollers.fixedRate(1, TimeUnit.SECONDS, 20)))
        .handle(targetDirectory())
        .get();
  }
}
```

同样，因为我们写入了一个中间通道，所以现在我们可以添加另一个流来获取这些相同的文件并以不同的速率写入它们：

```java
public class JavaDSLFileCopyConfig {
  
  @Bean
  public IntegrationFlow anotherFileWriter() {
    return IntegrationFlows.from("holdingTank")
        .bridge(e -> e.poller(Pollers.fixedRate(2, TimeUnit.SECONDS, 10)))
        .handle(anotherTargetDirectory())
        .get();
  }
}
```

正如我们所见，各个网桥可以控制不同处理程序的轮询配置。

一旦我们的应用程序上下文被加载，我们现在就有一个更复杂的应用程序在运行，它将开始将文件从源目录移动到两个目标目录。

## 6. 总结

在本文中，我们看到了使用Spring IntegrationJavaDSL构建不同集成管道的各种方法。

本质上，我们能够从之前的教程中重新创建文件移动应用程序，这次使用纯java。

此外，我们还查看了其他一些组件，例如通道和桥接器。
## 1. 简介 

在本教程中，我们将了解用于创建应用程序集成的Spring Integration Java DSL。

我们将采用我们在[Spring Integration简介](https://www.baeldung.com/spring-integration)中构建的文件移动集成，并改用DSL。

## 2. 依赖

Spring Integration Java DSL是[Spring Integration Core](https://central.sonatype.com/artifact/org.springframework.integration/spring-integration-core/6.0.3)的一部分。

因此，我们可以添加该依赖项：

```xml
<dependency>
    <groupId>org.springframework.integration</groupId>
    <artifactId>spring-integration-core</artifactId>
    <version>5.0.6.RELEASE</version>
</dependency>
```

为了处理我们的文件移动应用程序，我们还需要[Spring Integration File](https://central.sonatype.com/artifact/org.springframework.integration/spring-integration-file/6.0.3)：

```xml
<dependency>
    <groupId>org.springframework.integration</groupId>
    <artifactId>spring-integration-file</artifactId>
    <version>5.0.6.RELEASE</version>
</dependency>
```

## 3. Spring Integration Java DSL

在Java DSL出现之前，用户会在XML中配置Spring Integration组件。

**DSL引入了一些流式的构建器，我们可以从中轻松地创建一个完整的纯Java的Spring Integration管道**。 

所以，假设我们想创建一个通道，将通过管道的任何数据大写。

在过去，我们可能会这样做：

```xml
<int:channel id="input"/>

<int:transformer input-channel="input" expression="payload.toUpperCase()" />
```

现在我们可以改为：

```java
@Bean
public IntegrationFlow upcaseFlow() {
    return IntegrationFlows.from("input")
        .transform(String::toUpperCase)
        .get();
}
```

## 4. 文件移动应用程序

要开始我们的文件移动集成，我们需要一些简单的构建块。

### 4.1 集成流

我们需要的第一个构建块是集成流，我们可以从IntegrationFlows构建器中获取它：

```java
IntegrationFlows.from(...)
```

from可以采用多种类型，但在本教程中，我们只会看三种：

-   MessageSources
-   MessageChannel
-   Strings

我们将很快讨论这三个方面。

在我们调用from之后，我们现在可以使用一些自定义方法：

```java
IntegrationFlow flow = IntegrationFlows.from(sourceDirectory())
    .filter(onlyJpgs())
    .handle(targetDirectory())
    // add more components
    .get();
```

最终，IntegrationFlows将始终生成IntegrationFlow的实例，**这是任何Spring Integration应用程序的最终产品**。

**这种获取输入、执行适当转换并发出结果的模式是所有Spring Integration应用程序的基础**。

### 4.2 描述输入源

首先，要移动文件，我们需要向我们的集成流指示它应该在哪里寻找它们，为此，我们需要一个MessageSource：

```java
@Bean
public MessageSource<File> sourceDirectory() {
  // ... create a message source
}
```

简而言之，MessageSource是[应用程序外部的消息可以来自](http://joshlong.com/jl/blogPost/spring_integration_adapters_gateways_and_channels.html)的地方。

更具体地说，我们需要能够将该外部源调整为Spring消息传递表示的东西。由于这种适配侧重于输入，因此通常称为输入通道适配器。

spring-integration-file依赖项为我们提供了一个非常适合我们用例的输入通道适配器FileReadingMessageSource：

```java
@Bean
public MessageSource<File> sourceDirectory() {
    FileReadingMessageSource messageSource = new FileReadingMessageSource();
    messageSource.setDirectory(new File(INPUT_DIR));
    return messageSource;
}
```

在这里，我们的FileReadingMessageSource将读取INPUT_DIR给定的目录，并从中创建一个MessageSource。

让我们在IntegrationFlows.from调用中将其指定为我们的源：

```java
IntegrationFlows.from(sourceDirectory());
```

### 4.3 配置输入源

现在，如果我们将其视为一个长期存在的应用程序，**我们可能希望能够在文件进入时注意到它们**，而不仅仅是移动启动时已经存在的文件。

为了促进这一点，from还可以接收额外的配置器作为输入源的进一步自定义：

```java
IntegrationFlows.from(sourceDirectory(), configurer -> configurer.poller(Pollers.fixedDelay(10000)));
```

在这种情况下，我们可以通过让Spring Integration每10秒轮询一次该源(在本例中为我们的文件系统)来使我们的输入源更具弹性。

而且，当然，这不仅仅适用于我们的文件输入源，我们可以将此轮询器添加到任何MessageSource。

### 4.4 过滤来自输入源的消息

接下来，假设我们希望我们的文件移动应用程序仅移动特定文件，比如具有jpg扩展名的图像文件。

为此，我们可以使用GenericSelector：

```java
@Bean
public GenericSelector<File> onlyJpgs() {
    return new GenericSelector<File>() {

        @Override
        public boolean accept(File source) {
            return source.getName().endsWith(".jpg");
        }
    };
}
```

那么，让我们再次更新我们的集成流程：

```java
IntegrationFlows.from(sourceDirectory())
    .filter(onlyJpgs());
```

或者，**因为这个过滤器非常简单，我们可以使用lambda来定义它**：

```java
IntegrationFlows.from(sourceDirectory())
    .filter(source -> ((File) source).getName().endsWith(".jpg"));
```

### 4.5 使用ServiceActivators处理消息

现在我们有了过滤后的文件列表，我们需要将它们写入新位置。

当我们考虑Spring Integration中的输出时，我们会转向服务激活器。 

让我们使用spring-integration-file中的FileWritingMessageHandler服务激活器：

```java
@Bean
public MessageHandler targetDirectory() {
    FileWritingMessageHandler handler = new FileWritingMessageHandler(new File(OUTPUT_DIR));
    handler.setFileExistsMode(FileExistsMode.REPLACE);
    handler.setExpectReply(false);
    return handler;
}
```

在这里，我们的FileWritingMessageHandler会将它接收到的每个消息有效负载写入OUTPUT_DIR。

再次，让我们更新：

```java
IntegrationFlows.from(sourceDirectory())
    .filter(onlyJpgs())
    .handle(targetDirectory());
```

顺便说一下，请注意setExpectReply的用法。**因为集成流可以是双向的，所以这个调用表明这个特定的管道是单向的**。

### 4.6 激活我们的集成流程

添加完所有组件后，我们需要**将IntegrationFlow注册为bean以激活它**：

```java
@Bean
public IntegrationFlow fileMover() {
    return IntegrationFlows.from(sourceDirectory(), c -> c.poller(Pollers.fixedDelay(10000)))
        .filter(onlyJpgs())
        .handle(targetDirectory())
        .get();
}
```

get方法提取我们需要注册为Spring Bean的IntegrationFlow实例。 

一旦我们的应用程序上下文加载，我们IntegrationFlow中包含的所有组件都会被激活。

现在，我们的应用程序将开始将文件从源目录移动到目标目录。

## 5. 附加组件

在我们基于DSL的文件移动应用程序中，我们创建了一个入站通道适配器、一个消息过滤器和一个服务激活器。

让我们看看其他一些常见的Spring Integration组件，看看我们如何使用它们。

### 5.1 消息通道

如前所述，消息通道是另一种初始化流的方法：

```java
IntegrationFlows.from("anyChannel")
```

我们可以将其理解为“请找到或创建一个名为anyChannel的通道bean。然后，读取从其他流馈送到anyChannel的任何数据。”

但是，实际上它比这更通用。

简单来说，通道将生产者从消费者中抽象出来。我们可以把它看成是一个Java Queue，**可以在流中的任意点插入通道**。

例如，假设我们希望在文件从一个目录移动到下一个目录时确定文件的优先级：

```java
@Bean
public PriorityChannel alphabetically() {
    return new PriorityChannel(1000, (left, right) -> ((File)left.getPayload()).getName().compareTo(
        ((File)right.getPayload()).getName()));
}
```

然后，我们可以在流之间插入对通道的调用：

```java
@Bean
public IntegrationFlow fileMover() {
    return IntegrationFlows.from(sourceDirectory())
        .filter(onlyJpgs())
        .channel("alphabetically")
        .handle(targetDirectory())
        .get();
}
```

有许多通道可供选择，**其中一些比较方便的通道用于并发、审计或中间持久性(想想Kafka或JMS缓冲区)**。

此外，当与桥结合使用时，通道会变得非常强大。

### 5.2 桥

当我们想**合并两个通道**时，我们使用桥接器。

让我们想象一下，我们不是直接写入输出目录，而是让我们的文件移动应用程序写入另一个通道：

```java
@Bean
public IntegrationFlow fileReader() {
    return IntegrationFlows.from(sourceDirectory())
        .filter(onlyJpgs())
        .channel("holdingTank")
        .get();
}
```

现在，因为我们只是将它写入一个通道，所以**我们可以从那里桥接到其他流**。

让我们创建一个桥接器来轮询我们的存储站以获取消息并将它们写入目的地：

```java
@Bean
public IntegrationFlow fileWriter() {
    return IntegrationFlows.from("holdingTank")
       .bridge(e -> e.poller(Pollers.fixedRate(1, TimeUnit.SECONDS, 20)))
       .handle(targetDirectory())
       .get();
}
```

同样，因为我们写入了一个中间通道，**现在我们可以添加另一个流来获取这些相同的文件并以不同的速率写入它们**：

```java
@Bean
public IntegrationFlow anotherFileWriter() {
    return IntegrationFlows.from("holdingTank")
        .bridge(e -> e.poller(Pollers.fixedRate(2, TimeUnit.SECONDS, 10)))
        .handle(anotherTargetDirectory())
        .get();
}
```

如我们所见，各个桥可以控制不同处理程序的轮询配置。

一旦我们的应用程序上下文被加载，我们现在就有一个更复杂的应用程序在运行，它将开始将文件从源目录移动到两个目标目录。

## 6. 总结

在本文中，我们看到了使用Spring Integration Java DSL构建不同集成管道的各种方法。

本质上，我们能够从之前的教程中重新创建文件移动应用程序，这次使用纯Java。

此外，我们还查看了一些其他组件，例如通道和桥接器。
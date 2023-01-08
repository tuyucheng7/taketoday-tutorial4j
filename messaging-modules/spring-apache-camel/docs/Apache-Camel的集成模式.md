## 1. 概述

本文将介绍 Apache Camel 支持的一些基本企业集成模式 (EIP)。集成模式通过为集成系统的标准化方式提供解决方案来提供帮助。

如果你需要先了解 Apache Camel 的基础知识，请务必访问[本文](https://www.baeldung.com/apache-camel-intro)以复习基础知识。

## 2. 关于EIP

企业集成模式是旨在为集成挑战提供解决方案的设计模式。Camel 为其中的许多模式提供了实现。要查看受支持模式的完整列表，请访问[此链接](https://camel.apache.org/enterprise-integration-patterns.html)。

在本文中，我们将介绍基于内容的路由器、消息翻译器、多播、拆分器和死信通道集成模式。

## 2. 基于内容的路由器

基于内容的路由器是一种消息路由器，它根据消息头、部分有效负载或基本上我们认为是内容的消息交换中的任何内容将消息路由到其目的地。

它以choice() DSL 语句开始，后跟一个或多个when() DSL 语句。每个when()包含一个谓词表达式，如果满足该表达式，将导致执行包含的处理步骤。

让我们通过定义一个路由来说明这个 EIP，该路由使用一个文件夹中的文件并根据文件扩展名将它们移动到两个不同的文件夹中。我们的路由在 Spring XML 文件中引用，使用 Camel 的自定义 XML 语法：

```xml

<bean id="contentBasedFileRouter"
      class="cn.tuyucheng.taketoday.camel.file.ContentBasedFileRouter"/>

<camelContext xmlns="http://camel.apache.org/schema/spring">
<routeBuilder ref="contentBasedFileRouter"/>
</camelContext>
```

路由定义包含在ContentBasedFileRouter类中，其中文件根据其扩展名从源文件夹路由到两个不同的目标文件夹。

或者，我们可以在这里使用 SpringJava配置方法而不是使用 Spring XML 文件。为此，我们需要向我们的项目添加一个额外的依赖项：

```xml
<dependency>
    <groupId>org.apache.camel</groupId>
    <artifactId>camel-spring-javaconfig</artifactId>
    <version>2.18.1</version>
</dependency>
```

可以在[此处](https://search.maven.org/classic/#search|ga|1|a%3A"camel-spring-javaconfig")找到该工件的最新版本。

之后，我们需要扩展CamelConfiguration类并覆盖将引用ContentBasedFileRouter的routes()方法：

```java
@Configuration
public class ContentBasedFileRouterConfig extends CamelConfiguration {

    @Bean
    ContentBasedFileRouter getContentBasedFileRouter() {
        return new ContentBasedFileRouter();
    }

    @Override
    public List<RouteBuilder> routes() {
        return Arrays.asList(getContentBasedFileRouter());
    }
}
```

扩展使用[简单表达式语言](https://camel.apache.org/simple.html)通过简单的() DSL 语句进行评估，该语句旨在用于评估表达式和谓词：

```java
public class ContentBasedFileRouter extends RouteBuilder {

    private static final String SOURCE_FOLDER 
      = "src/test/source-folder";
    private static final String DESTINATION_FOLDER_TXT 
      = "src/test/destination-folder-txt";
    private static final String DESTINATION_FOLDER_OTHER 
      = "src/test/destination-folder-other";

    @Override
    public void configure() throws Exception {
        from("file://" + SOURCE_FOLDER + "?delete=true").choice()
          .when(simple("${file:ext} == 'txt'"))
          .to("file://" + DESTINATION_FOLDER_TXT).otherwise()
          .to("file://" + DESTINATION_FOLDER_OTHER);
    }
}
```

在这里，我们还使用otherwise() DSL 语句来路由所有不满足when()语句给定谓词的消息。

## 3.消息翻译器

由于每个系统都使用自己的数据格式，因此经常需要将来自另一个系统的消息转换为目标系统支持的数据格式。

Camel 支持MessageTranslator路由器，它允许我们使用路由逻辑中的自定义处理器、使用特定的 bean 来执行转换或使用transform() DSL 语句来转换消息。

[在上一篇文章](https://www.baeldung.com/apache-camel-intro)中可以找到使用自定义处理器的示例，我们在其中定义了一个处理器，该处理器为每个传入文件的文件名添加时间戳。

现在让我们演示如何使用transform()语句使用 Message Translator：

```java
public class MessageTranslatorFileRouter extends RouteBuilder {
    private static final String SOURCE_FOLDER 
      = "src/test/source-folder";
    private static final String DESTINATION_FOLDER 
      = "src/test/destination-folder";

    @Override
    public void configure() throws Exception {
        from("file://" + SOURCE_FOLDER + "?delete=true")
          .transform(body().append(header(Exchange.FILE_NAME)))
          .to("file://" + DESTINATION_FOLDER);
    }
}
```

在此示例中，我们通过transform()语句为源文件夹中的每个文件将文件名附加到文件内容，并将转换后的文件移动到目标文件夹。

## 4.多播

多播允许我们将相同的消息路由到一组不同的端点并以不同的方式处理它们。

这可以通过使用multicast() DSL 语句然后列出端点和其中的处理步骤来实现。

默认情况下，不同端点上的处理不是并行完成的，但这可以通过使用parallelProcessing() DSL 语句来更改。

默认情况下，Camel 将使用最后的回复作为多播后的传出消息。但是，可以定义不同的聚合策略以用于组装来自多播的回复。

让我们看看多播 EIP 在示例中的样子。我们会将源文件夹中的文件多播到两个不同的路由，我们将在其中转换它们的内容并将它们发送到不同的目标文件夹。这里我们使用[direct: component](https://camel.apache.org/direct.html)，它允许我们将两条路由链接在一起：

```java
public class MulticastFileRouter extends RouteBuilder {
    private static final String SOURCE_FOLDER 
      = "src/test/source-folder";
    private static final String DESTINATION_FOLDER_WORLD 
      = "src/test/destination-folder-world";
    private static final String DESTINATION_FOLDER_HELLO 
      = "src/test/destination-folder-hello";

    @Override
    public void configure() throws Exception {
        from("file://" + SOURCE_FOLDER + "?delete=true")
          .multicast()
          .to("direct:append", "direct:prepend").end();

        from("direct:append")
          .transform(body().append("World"))
          .to("file://" + DESTINATION_FOLDER_WORLD);

        from("direct:prepend")
           .transform(body().prepend("Hello"))
           .to("file://" + DESTINATION_FOLDER_HELLO);
    }
}
```

## 5.分离器

拆分器允许我们将传入的消息拆分为多个部分并单独处理每个部分。这可以通过使用split() DSL 语句来实现。

与 Multicast 不同，Splitter 将更改传入消息，而 Multicast 将保持原样。

为了在示例中演示这一点，我们将定义一个路径，其中文件中的每一行都被拆分并转换为一个单独的文件，然后该文件被移动到不同的目标文件夹。将创建每个新文件，文件名等于文件内容：

```java
public class SplitterFileRouter extends RouteBuilder {
    private static final String SOURCE_FOLDER 
      = "src/test/source-folder";
    private static final String DESTINATION_FOLDER  
      = "src/test/destination-folder";

    @Override
    public void configure() throws Exception {
        from("file://" + SOURCE_FOLDER + "?delete=true")
          .split(body().convertToString().tokenize("n"))
          .setHeader(Exchange.FILE_NAME, body())
          .to("file://" + DESTINATION_FOLDER);
    }
}
```

## 6. 死信频道

这很常见，并且应该预料到有时会发生问题，例如，数据库死锁，这可能导致消息无法按预期传递。但是，在某些情况下，以一定的延迟重试会有所帮助，消息将得到处理。

Dead Letter Channel 允许我们控制消息在传递失败后会发生什么。使用死信通道，我们可以指定是否将抛出的异常传播给调用者以及将失败的 Exchange 路由到何处。

当消息传递失败时，死信通道(如果使用)会将消息移动到死信端点。

让我们通过在路由上抛出异常来演示这一点：

```java
public class DeadLetterChannelFileRouter extends RouteBuilder {
    private static final String SOURCE_FOLDER 
      = "src/test/source-folder";

    @Override
    public void configure() throws Exception {
        errorHandler(deadLetterChannel("log:dead?level=ERROR")
          .maximumRedeliveries(3).redeliveryDelay(1000)
          .retryAttemptedLogLevel(LoggingLevel.ERROR));

        from("file://" + SOURCE_FOLDER + "?delete=true")
          .process(exchange -> {
            throw new IllegalArgumentException("Exception thrown!");
        });
    }
}
```

在这里，我们定义了一个errorHandler，它记录失败的交付并定义重新交付策略。通过设置retryAttemptedLogLevel()，每次重新投递尝试都将以指定的日志级别记录。

为了使其功能齐全，我们还需要配置一个记录器。

运行此测试后，控制台中会显示以下日志语句：

```java
ERROR DeadLetterChannel:156 - Failed delivery for 
(MessageId: ID-ZAG0025-50922-1481340325657-0-1 on 
ExchangeId: ID-ZAG0025-50922-1481340325657-0-2). 
On delivery attempt: 0 caught: java.lang.IllegalArgumentException: 
Exception thrown!
ERROR DeadLetterChannel:156 - Failed delivery for 
(MessageId: ID-ZAG0025-50922-1481340325657-0-1 on 
ExchangeId: ID-ZAG0025-50922-1481340325657-0-2). 
On delivery attempt: 1 caught: java.lang.IllegalArgumentException: 
Exception thrown!
ERROR DeadLetterChannel:156 - Failed delivery for 
(MessageId: ID-ZAG0025-50922-1481340325657-0-1 on 
ExchangeId: ID-ZAG0025-50922-1481340325657-0-2). 
On delivery attempt: 2 caught: java.lang.IllegalArgumentException: 
Exception thrown!
ERROR DeadLetterChannel:156 - Failed delivery for 
(MessageId: ID-ZAG0025-50922-1481340325657-0-1 on 
ExchangeId: ID-ZAG0025-50922-1481340325657-0-2). 
On delivery attempt: 3 caught: java.lang.IllegalArgumentException: 
Exception thrown!
ERROR dead:156 - Exchange[ExchangePattern: InOnly, 
BodyType: org.apache.camel.component.file.GenericFile, 
Body: [Body is file based: GenericFile[File.txt]]]
```

正如你所注意到的，每次重新传递尝试都会被记录下来，显示传递不成功的 Exchange。

## 七. 总结

在本文中，我们介绍了使用 Apache Camel 的集成模式，并通过几个示例对其进行了演示。

我们演示了如何使用这些集成模式以及为什么它们有助于解决集成挑战。
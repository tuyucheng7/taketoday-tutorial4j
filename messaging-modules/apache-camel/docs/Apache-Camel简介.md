## 1. 概述

在本文中，我们将介绍 Camel 并探索其核心概念之一——消息路由。

我们将从介绍这些基本概念和术语开始，然后介绍定义路由的两个主要选项——Java DSL 和 Spring DSL。

我们还将在一个示例中演示这些——通过定义一个路由，该路由使用一个文件夹中的文件并将它们移动到另一个文件夹，同时为每个文件名添加一个时间戳。

## 2.关于阿帕奇骆驼

[Apache Camel](https://camel.apache.org/)是一个开源集成框架，旨在使集成系统简单易行。

它允许最终用户使用相同的 API 集成各种系统，提供对多种协议和数据类型的支持，同时具有可扩展性并允许引入自定义协议。

## 3.Maven依赖

为了使用 Camel，我们需要先添加 Maven 依赖：

```xml
<dependency>
    <groupId>org.apache.camel</groupId>
    <artifactId>camel-core</artifactId>
    <version>2.18.0</version>
</dependency>
```

最新版本的 Camel 神器可以在[这里](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.camel")找到。

## 3.领域特定语言

路由和路由引擎是Camel的核心部分。路由包含了不同系统之间集成的流程和逻辑。

为了更简单、更清晰地定义路由，Camel 为Java或 Groovy 等编程语言提供了几种不同的领域特定语言 (DSL)。另一方面，它还提供了使用 Spring DSL 在 XML 中定义路由。

使用JavaDSL 或 Spring DSL 主要是用户偏好，因为大多数功能在两者中都可用。

Java DSL 提供了一些 Spring DSL 不支持的特性。然而，Spring DSL 有时更有利，因为无需重新编译代码即可更改 XML。

## 4. 术语和架构

现在让我们讨论基本的 Camel 术语和架构。

首先，我们将在这里看一下 Camel 的核心概念：

-   消息包含正在传输到路由的数据。每封邮件都有一个唯一的标识符，它由正文、标题和附件构成
-   Exchange是消息的容器，它是在路由过程中消费者收到消息时创建的。Exchange 允许系统之间进行不同类型的交互——它可以定义单向消息或请求-响应消息
-   端点是系统可以接收或发送消息的通道。它可以引用 Web 服务 URI、队列 URI、文件、电子邮件地址等
-   组件充当端点工厂。简而言之，组件使用相同的方法和语法为不同的技术提供接口。Camel 已经在其 DSL 中为几乎所有可能的技术支持[大量组件](https://camel.apache.org/components.html)，但它也提供了编写自定义组件的能力
-   Processor是一个简单的Java接口，用于向路由添加自定义集成逻辑。它包含一个单一的流程方法，用于对消费者收到的消息执行自定义业务逻辑

在高层次上，Camel 的架构很简单。CamelContext代表 Camel 运行时系统，它连接了不同的概念，例如路由、组件或端点。

在此之下，处理器处理端点之间的路由和转换，而端点则集成不同的系统。

## 5. 定义路线

可以使用JavaDSL 或 Spring DSL 定义路由。

我们将通过定义一个路由来说明这两种样式，该路由使用一个文件夹中的文件并将它们移动到另一个文件夹，同时为每个文件名添加时间戳。

### 5.1. 使用JavaDSL 进行路由

要使用JavaDSL 定义路由，我们首先需要创建一个DefaultCamelContext实例。之后，我们需要扩展RouteBuilder类并实现包含路由流的配置方法：

```java
private static final long DURATION_MILIS = 10000;
private static final String SOURCE_FOLDER = "src/test/source-folder";
private static final String DESTINATION_FOLDER 
  = "src/test/destination-folder";

@Test
public void moveFolderContentJavaDSLTest() throws Exception {
    CamelContext camelContext = new DefaultCamelContext();
    camelContext.addRoutes(new RouteBuilder() {
      @Override
      public void configure() throws Exception {
        from("file://" + SOURCE_FOLDER + "?delete=true").process(
          new FileProcessor()).to("file://" + DESTINATION_FOLDER);
      }
    });
    camelContext.start();
    Thread.sleep(DURATION_MILIS);
    camelContext.stop();
}
```

配置方法可以这样理解：从源文件夹中读取文件，使用 FileProcessor 处理它们并将结果发送到目标文件夹。设置delete=true意味着文件将在成功处理后从源文件夹中删除。

为了启动 Camel，我们需要调用CamelContext的start方法。调用Thread.sleep是为了让 Camel 有必要的时间将文件从一个文件夹移动到另一个文件夹。

FileProcessor实现Processor接口并包含单个处理方法，该方法包含修改文件名的逻辑：

```java
public class FileProcessor implements Processor {
    public void process(Exchange exchange) throws Exception {
        String originalFileName = (String) exchange.getIn().getHeader(
          Exchange.FILE_NAME, String.class);

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
          "yyyy-MM-dd HH-mm-ss");
        String changedFileName = dateFormat.format(date) + originalFileName;
        exchange.getIn().setHeader(Exchange.FILE_NAME, changedFileName);
    }
}
```

为了检索文件名，我们必须从交换器检索传入消息并访问其标头。与此类似，要修改文件名，我们必须更新消息头。

### 5.2. 使用 Spring DSL 进行路由

当使用 Spring DSL 定义路由时，我们使用 XML 文件来设置我们的路由和处理器。这允许我们使用 Spring 不使用代码配置路由，并最终为我们带来完全控制反转的好处。

这已在[现有文章](https://www.baeldung.com/spring-apache-camel-tutorial)中介绍，因此我们将重点关注使用 Spring DSL 和JavaDSL，这通常是定义路由的首选方式。

在这种安排中，CamelContext 是在 Spring XML 文件中使用 Camel 的自定义 XML 语法定义的，但没有像使用 XML 的“纯”Spring DSL 那样的路由定义：

```xml

<bean id="fileRouter" class="cn.tuyucheng.taketoday.camel.file.FileRouter"/>
<bean id="fileProcessor"
      class="cn.tuyucheng.taketoday.camel.file.FileProcessor"/>

<camelContext xmlns="http://camel.apache.org/schema/spring">
<routeBuilder ref="fileRouter"/>
</camelContext>

```

通过这种方式，我们告诉 Camel 使用FileRouter类，它在JavaDSL 中保存我们的路由定义：

```java
public class FileRouter extends RouteBuilder {

    private static final String SOURCE_FOLDER = 
      "src/test/source-folder";
    private static final String DESTINATION_FOLDER = 
      "src/test/destination-folder";

    @Override
    public void configure() throws Exception {
        from("file://" + SOURCE_FOLDER + "?delete=true").process(
          new FileProcessor()).to("file://" + DESTINATION_FOLDER);
    }
}
```

为了对此进行测试，我们必须创建一个ClassPathXmlApplicationContext实例，它将在 Spring 中加载我们的CamelContext：

```java
@Test
public void moveFolderContentSpringDSLTest() throws InterruptedException {
    ClassPathXmlApplicationContext applicationContext = 
      new ClassPathXmlApplicationContext("camel-context.xml");
    Thread.sleep(DURATION_MILIS);
    applicationContext.close();
}
```

通过使用这种方法，我们获得了 Spring 提供的额外灵活性和好处，以及通过使用JavaDSL 获得Java语言的所有可能性。

## 六. 总结

在这篇简短的文章中，我们介绍了 Apache Camel，并展示了使用 Camel 执行集成任务的好处，例如将文件从一个文件夹路由到另一个文件夹。

在我们的示例中，我们看到 Camel 让你专注于业务逻辑并减少样板代码的数量。
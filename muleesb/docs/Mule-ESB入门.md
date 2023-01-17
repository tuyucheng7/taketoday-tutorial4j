## 1. 概述

[Mule ESB](https://www.mulesoft.com/platform/soa/mule-esb-open-source-esb)是一种基于Java的轻量级企业服务总线。它允许开发人员通过以不同格式交换数据来将多个应用程序连接在一起。它以消息的形式携带数据。

ESB 通过提供多种服务来提供强大的功能，例如：

-   服务创建和托管
-   服务调解
-   消息路由
-   数据转换

如果我们需要将多个应用程序集成在一起，或者如果我们有在未来添加更多应用程序的想法，我们会发现 ESB 很有用。

ESB 还用于处理不止一种类型的通信协议以及需要消息路由功能的情况。

[让我们使用可在此处](https://www.mulesoft.com/lp/dl/studio)下载的AnyPoint Studio在第 5 节中创建示例项目。

## 2.骡子消息结构

简而言之，ESB 的主要目的是在服务之间进行调解并将消息路由到各种端点。所以它需要处理不同类型的内容或负载。

消息结构分为两部分：

-   标头，其中 包含消息元数据
-   有效负载，其中包含特定于业务的数据

消息嵌入在消息对象中。我们可以从上下文中检索消息对象。我们可以使用 Mule 流中的自定义Java组件和转换器来更改其属性和有效负载。

每个应用程序由一个或多个流组成。

在流中，我们可以使用组件来访问、过滤或更改消息及其不同属性。

例如，我们可以使用Java组件获取消息的实例。这个组件类实现了org.mule.api.lifecycle包中的Callable接口：

```java
public Object onCall(MuleEventContext eventContext) throws Exception {
    MuleMessage message = eventContext.getMessage();
    message.setPayload("Message payload is changed here.");
    return message;
}
```

## 3. 属性和变量

消息元数据由属性组成。变量表示有关消息的数据。如何在消息的生命周期中应用属性和变量由它们的范围定义。根据其范围，属性可以分为两种类型：入站和出站。

入站属性包含元数据，可防止消息在跨流时变得杂乱无章。入站属性是不可变的，用户不能更改。它们仅在流的持续时间内存在——一旦消息退出流，入站属性就不再存在。

出站属性可以由 Mule 自动设置，或者用户可以通过流配置来设置它们。这些属性是可变的。当消息在跨越传输障碍后进入另一个流时，它们成为入站属性。

我们可以通过在各自范围内调用关联的 setter 和 getter 方法来分别设置和获取出站和入站属性：

```java
message.setProperty(
  "outboundKey", "outboundpropertyvalue", PropertyScope.OUTBOUND);
String inboundProp = (String) message.getInboundProperty("outboundKey");
```

有两种类型的变量可用于在应用程序中声明。

一个是流变量，它是 Mule 流的本地变量，可以跨流、子流和私有流使用。

会话变量一旦声明就可以在整个应用程序中使用。

## 4. 运输障碍和流量参考

传输障碍是 HTTP 连接器、VM、JMS 或类似的连接器，它们需要路径或端点来路由消息。流变量不可跨传输障碍使用，但会话变量可在整个项目的所有流中使用。

当我们需要创建子流或私有流时，我们可以使用flow-ref组件从父流或另一个流中引用流。流变量和会话变量在使用flow-ref 引用的子流和私有流中可用。

## 5. 示例项目

让我们在 Anypoint Studio 中创建一个包含多个流的应用程序，这些流通过入站和出站连接器在它们之间进行通信。

让我们看一下第一个流程：

[![流动](https://www.baeldung.com/wp-content/uploads/2017/12/Flow-300x90.jpg)](https://www.baeldung.com/wp-content/uploads/2017/12/Flow.jpg)

 

我们可以将 HTTP 侦听器配置为：

```xml
<http:listener-config name="HTTP_Listener_Configuration"
  host="localhost" port="8081" doc:name="HTTP Listener Configuration"/>
```

流组件必须在<flow>标记内。因此，具有多个组件的示例流程是：

```xml
<flow name="Flow">
    <http:listener 
      config-ref="HTTP_Listener_Configuration" 
      path="/" doc:name="HTTP" 
      allowedMethods="POST"/>
    <logger message="Original 
      paylaod: #[payload]" 
      level="INFO" doc:name="Logger"/>
    <custom-transformer 
      class="com.baeldung.transformer.InitializationTransformer" 
      doc:name="Java"/>
    <logger message="Payload After Initialization: #[payload]" 
      level="INFO" doc:name="Logger"/>
    <set-variable variableName="f1" 
      value="#['Flow Variable 1']" doc:name="F1"/>
    <set-session-variable variableName="s1" 
      value="#['Session variable 1']" doc:name="S1"/>
    <vm:outbound-endpoint exchange-pattern="request-response" 
      path="test" doc:name="VM"/>
</flow>
```

在流程中，我们提供了对配置的 HTTP 侦听器的引用。然后我们保留一个记录器来记录 HTTP 侦听器通过 POST 方法接收的有效负载。

之后，放置一个自定义Java转换器类，在收到消息后转换有效负载：

```java
public Object transformMessage(
  MuleMessage message, 
  String outputEncoding) throws TransformerException {
 
    message.setPayload("Payload is transferred here.");
    message.setProperty(
      "outboundKey", "outboundpropertyvalue", PropertyScope.OUTBOUND);
    return message;
}
```

转换器类必须扩展AbstractMessageTransformer。我们还在类中设置了一个出站属性。

现在，我们已经在消息对象中转换了有效负载，并使用 logger.log 将其记录在控制台中。我们正在设置一个流变量和一个会话变量。

最后，我们通过出站 VM 连接器发送我们的有效负载。VM 连接器中的路径确定接收端点：

[![流程 1](https://www.baeldung.com/wp-content/uploads/2017/12/Flow1-300x91.jpg)](https://www.baeldung.com/wp-content/uploads/2017/12/Flow1.jpg)

初始流携带和转换的消息通过入站 VM 端点到达Flow1 。

Java 组件检索由第一个流设置的出站属性并返回成为消息负载的对象。

此任务的transformMessage()方法：

```java
public Object transformMessage(
  MuleMessage message, 
  String outputEncoding) throws TransformerException {

    return (String) message.getInboundProperty("outboundKey");
}
```

然后，流和会话变量被设置为第二个流。之后，我们使用flow-ref组件获得了对Flow2的引用。

[![流程 2](https://www.baeldung.com/wp-content/uploads/2017/12/Flow2-300x125.jpg)](https://www.baeldung.com/wp-content/uploads/2017/12/Flow2.jpg)

在Flow2 中，我们使用Java组件类转换消息并将其记录在控制台中。我们还设置了一个流变量F3。

使用flow-ref调用Flow2后，Flow1将等待 Flow2 处理消息。

Flow1和Flow2中设置的任何流变量都将在两个流中可用，因为这些流没有被任何传输障碍分隔。

最后，消息通过 VM 发送回 HTTP 请求方。我们将所有 VM 配置为请求-响应。

我们可以通过在主体中发布任何 JSON 数据来从任何 REST 客户端调用此应用程序。URL 将是在 HTTP 侦听器中配置的localhost:8081 。

## 6. Maven原型

我们可以使用 Mulesoft 的 Maven 原型构建一个 Mule ESB 项目。

在 Maven 的settings.xml文件中，我们首先需要添加org.mule.tools插件组：

```xml
<pluginGroups>
    <pluginGroup>org.mule.tools</pluginGroup>
</pluginGroups>
```

然后，我们需要添加一个配置文件标签，说明 Maven 应该在哪里寻找 Mulesoft 工件：

```xml
<profile>
    <id>Mule Org</id>
    <activation>
        <activeByDefault>true</activeByDefault>
    </activation>
    <repositories>
        <repository>
            <id>mulesoft-releases</id>
            <name>MuleSoft Repository</name>
            <url>https://repository.mulesoft.org/releases/</url>
            <layout>default</layout>
        </repository>
    </repositories>
</profile>
```

最后，我们可以使用mule-project-archetype:create 创建项目：

```shell
mvn mule-project-archetype:create -DartifactId=muleesb -DmuleVersion=3.9.0
```

配置我们的项目后，我们可以使用mvn package创建一个可部署的存档。

之后，我们将存档部署到任何独立 Mule 服务器的应用程序文件夹中。

## 7. 通过 MuleSoft 的 Maven 存储库的独立 Mule 服务器

如前所述，我们刚刚创建的项目需要[一个独立的 Mule 服务器](https://repository.mulesoft.org/nexus/content/repositories/releases/org/mule/distributions/mule-standalone/)。

如果我们还没有，我们可以编辑pom.xml [以从 MuleSoft 的 Maven 存储库中提取一个](https://repository.mulesoft.org/nexus/content/repositories/releases/org/mule/tools/maven/mule-maven-plugin/)：

```xml
<plugin>
    <groupId>org.mule.tools.maven</groupId>
    <artifactId>mule-maven-plugin</artifactId>
    <version>2.2.1</version>
    <configuration>
        <deploymentType>standalone</deploymentType>
        <muleVersion>3.9.0</muleVersion>
    </configuration>
    <executions>
        <execution>
            <id>deploy</id>
            <phase>deploy</phase>
            <goals>
                <goal>deploy</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## 八. 总结

在本文中，我们介绍了在 Mule 中构建 ESB 应用程序的不同必要概念。我们创建了一个示例项目来说明所有描述的概念。

我们现在可以开始使用 Anypoint Studio 创建 ESB 应用程序来满足我们的各种需求。
## 1. 概述

记录 API 是构建应用程序的重要组成部分。这是我们与客户签订的共享合同。此外，它详细记录了我们的集成点如何工作。该文档应该易于访问、理解和实施。

在本教程中，我们将了解[Springwolf](https://www.springwolf.dev/)用于记录事件驱动的Spring Boot服务。Springwolf 实现了[AsyncAPI](https://www.asyncapi.com/)规范，这是针对事件驱动 API 的[OpenAPI](https://www.openapis.org/)规范的改编。Springwolf 与协议无关，涵盖[Spring Kafka](https://www.baeldung.com/spring-kafka)、Spring RabbitMQ 和 Spring CloudStream 实现。

使用 Spring Kafka 作为我们的事件驱动系统，Springwolf 从代码中为我们生成一个 AsyncAPI 文档。一些消费者是被自动检测到的。其他信息由我们提供。

## 2. 设置Springwolf

为了开始使用 Springwolf，我们添加依赖项并配置它。

### 2.1. 添加依赖项

假设我们有一个正在运行的带有 Spring Kafka 的 Spring 应用程序，我们将springwolf-kafka作为pom.xml 文件中的依赖项添加到我们的 Maven 项目中：

```xml
<dependency>
    <groupId>io.github.springwolf</groupId>
    <artifactId>springwolf-kafka</artifactId>
    <version>0.14.0</version>
</dependency>

```

[最新版本可以在Maven Central](https://mvnrepository.com/artifact/io.github.springwolf/springwolf-kafka)上找到，并且该项目的网站上提到了对除 Spring Kafka 之外的其他绑定的支持。

### 2.2. application.properties配置

在最基本的形式中，我们将以下 Springwolf 配置添加到application.properties中：

```properties
# Springwolf Configuration
springwolf.docket.base-package=com.baeldung.boot.documentation.springwolf.adapter
springwolf.docket.info.title=${spring.application.name}
springwolf.docket.info.version=1.0.0
springwolf.docket.info.description=Baeldung Tutorial Application to Demonstrate AsyncAPI Documentation using Springwolf

# Springwolf Kafka Configuration
springwolf.docket.servers.kafka.protocol=kafka
springwolf.docket.servers.kafka.url=localhost:9092
```

第一个块设置一般 Springwolf 配置。这包括基础包，Springwolf 使用它来自动检测侦听器。此外，我们在 AsyncAPI 文档中显示的文档配置键下设置一般信息。

然后，我们设置springwolf-kafka的具体配置。同样，这出现在 AsyncAPI 文档中。

### 2.3. 确认

现在，我们已准备好运行我们的应用程序。应用程序启动后，AsyncAPI 文档默认位于/springwolf/docs路径：

```bash
http://localhost:8080/springwolf/docs
```

## 3.AsyncAPI文档

AsyncAPI 文档遵循与 OpenAPI 文档[类似的结构。](https://www.asyncapi.com/docs/tutorials/getting-started/coming-from-openapi)首先，我们只看关键部分。该[规范](https://www.asyncapi.com/docs/reference/specification/latest)可在 AsyncAPI 网站上找到。为简洁起见，我们将仅查看属性的子集。

在以下小节中，我们将逐步查看 JSON 格式的 AsyncAPI 文档。我们从以下结构开始：

```json
{
    "asyncapi": "2.6.0",
    "info": { ... },
    "servers": { ... },
    "channels": { ... },
    "components": { ... }
}
```

### 3.1. 信息部分

文档的信息部分包含有关应用程序本身的信息。这至少包括以下字段：标题、应用程序版本和描述。

根据我们添加到配置中的信息，创建以下结构：

```json
"info": {
    "title": "Baeldung Tutorial Springwolf Application",
    "version": "1.0.0",
    "description": "Baeldung Tutorial Application to Demonstrate AsyncAPI Documentation using Springwolf"
}
```

### 3.2. 服务器部分

同样，服务器部分包含有关我们的 Kafka 代理的信息，并且基于上面的application.properties配置：

```json
"servers": {
    "kafka": {
        "url": "localhost:9092",
        "protocol": "kafka"
    }
}
```

### 3.3. 渠道部分

此时此部分为空，因为我们尚未在应用程序中配置任何消费者或生产者。在后面的部分中配置它们之后，我们将看到以下结构：

```json
"channels": {
    "my-topic-name": {
        "publish": {
             "message": {
                "title": "IncomingPayloadDto",
                "payload": {
                    "$ref": "#/components/schemas/IncomingPayloadDto"
                }
            }
        }
    }
}
```

通用术语通道指的是Kafka 术语中的主题。

每个主题可以提供两种操作：发布和/或订阅。值得注意的是，从应用程序的角度来看，语义可能看起来很混乱：

- 将消息发布到此通道，以便我们的应用程序可以使用它们。
- 订阅此频道以接收来自我们应用程序的消息。

操作对象本身包含描述和消息等信息。消息对象包含标题和有效负载等信息。

为了避免在多个主题和操作中重复相同的负载信息，AsyncAPI 使用$ref表示法来指示AsyncAPI 文档的组件部分中的引用。

### 3.4. 组件部分

同样，此部分此时为空，但将具有以下结构：

```json
"components": {
    "schemas": {
        "IncomingPayloadDto": {
            "type": "object",
             "properties": {
                ...
                "someString": {
                    "type": "string"
                }
            },
            "example": {
                "someEnum": "FOO2",
                "someLong": 1,
                "someString": "string"
            }
        }
    }
}
```

Components 部分包含$ref引用的所有详细信息，包括#/components/schemas/IncomingPayloadDto。除了数据类型和有效负载的属性之外，模式还可以包含有效负载的示例(JSON)。

## 4. 记录消费者

Springwolf 自动检测所有@KafkaListener注解，如下所示。此外，我们使用@AsyncListener注释来手动提供更多详细信息。

### 4.1. 自动检测@KafkaListener注解

通过在方法上使用 Spring-Kafka 的@KafkaListener注释，Springwolf 自动在基础包中找到消费者：

```java
@KafkaListener(topics = TOPIC_NAME)
public void consume(IncomingPayloadDto payload) {
    // ...
}
```

现在，AsyncAPI 文档确实包含通道TOPIC_NAME以及发布 操作和IncomingPayloadDto架构，如我们之前所见。

### 4.2. 通过@AsyncListener注释手动记录消费者

将自动检测和@AsyncListener一起使用可能会导致重复。为了能够手动添加更多信息，我们完全禁用@KafkaListener自动检测并将以下行添加到application.properties文件中：

```properties
springwolf.plugin.kafka.scanner.kafka-listener.enabled=false
```

接下来，我们将 Springwolf @AsyncListener 注解添加到同一方法中，并为 AsyncAPI 文档提供附加信息：

```java
@KafkaListener(topics = TOPIC_NAME)
@AsyncListener(
    operation = @AsyncOperation(
        channelName = TOPIC_NAME,
        description = "More details for the incoming topic"
    )
)
@KafkaAsyncOperationBinding
public void consume(IncomingPayloadDto payload) {
    // ...
}
```

此外，我们添加了@KafkaAsyncOperationBinding注释，以将通用@AsyncOperation注释与服务器部分中的 Kafka 代理连接起来。Kafka 协议特定信息也是使用此注释设置的。

更改后，AsyncAPI 文档包含更新的文档。

## 5. 记录生产者

使用 Springwolf @AsyncPublisher注释手动记录生产者。

### 5.1. 通过@AsyncPublisher注释手动记录生产者

与@AsyncListener注释类似，我们将@AsyncPublisher注释添加到publisher方法中，并添加@KafkaAsyncOperationBinding注释：

```java
@AsyncPublisher(
    operation = @AsyncOperation(
        channelName = TOPIC_NAME,
        description = "More details for the outgoing topic"
    )
)
@KafkaAsyncOperationBinding
public void publish(OutgoingPayloadDto payload) {
    kafkaTemplate.send(TOPIC_NAME, payload);
}
```

基于此，Springwolf使用上面提供的信息为TOPIC_NAME频道添加了对频道部分的订阅操作。有效负载类型是从方法签名中提取的，其方式与@AsyncListener的提取方式相同。

## 6. 加强文档记录

AsyncAPI 规范涵盖的功能比我们上面介绍的还要多。接下来，我们记录默认的 Spring Kafka 标头__TypeId__并改进有效负载的文档。

### 6.1. 添加 Kafka 标头

当运行本机 Spring Kafka 应用程序时，Spring Kafka 会自动添加标头__TypeId__以协助消费者中有效负载的反序列化。

我们通过在@AsyncListener(或@AsyncPublisher )注释的@AsyncOperation上设置headers字段，将__TypeId__标头添加到文档中：

```java
@AsyncListener(
    operation = @AsyncOperation(
        ...,
        headers = @AsyncOperation.Headers(
            schemaName = "SpringKafkaDefaultHeadersIncomingPayloadDto",
            values = {
                // this header is generated by Spring by default
                @AsyncOperation.Headers.Header(
                    name = DEFAULT_CLASSID_FIELD_NAME,
                    description = "Spring Type Id Header",
                    value = "com.baeldung.boot.documentation.springwolf.dto.IncomingPayloadDto"
                ),
            }
        )
    )
)
```

现在，AsyncAPI 文档包含一个新的字段标头作为消息对象的一部分。

### 6.2. 添加有效负载详细信息

我们使用 Swagger [@Schema](https://javadoc.io/doc/io.swagger.core.v3/swagger-annotations/latest/io/swagger/v3/oas/annotations/media/Schema.html)注释来提供有关有效负载的附加信息。在下面的代码片段中，我们设置了描述、示例值以及该字段是否为必填：

```java
@Schema(description = "Outgoing payload model")
public class OutgoingPayloadDto {
    @Schema(description = "Foo field", example = "bar", requiredMode = NOT_REQUIRED)
    private String foo;

    @Schema(description = "IncomingPayload field", requiredMode = REQUIRED)
    private IncomingPayloadDto incomingWrapped;
}
```

基于此，我们在AsyncAPI文档中看到了丰富的OutgoingPayloadDto模式：

```json
"OutgoingPayloadDto": {
    "type": "object",
    "description": "Outgoing payload model",
    "properties": {
        "incomingWrapped": {
            "$ref": "#/components/schemas/IncomingPayloadDto"
        },
        "foo": {
            "type": "string",
            "description": "Foo field",
            "example": "bar"
        }
    },
    "required": [
        "incomingWrapped"
    ],
    "example": {
        "incomingWrapped": {
            "someEnum": "FOO2",
            "someLong": 5,
            "someString": "some string value"
         },
         "foo": "bar"
    }
}
```

我们的应用程序的完整 AsyncAPI 文档可在[链接的示例项目](https://github.com/eugenp/tutorials/blob/master/spring-boot-modules/spring-boot-documentation/src/test/resources/asyncapi.json)中找到。

## 7. 使用Springwolf UI

Springwolf 有自己的 UI，但可以使用任何符合 AsyncAPI 的文档渲染器。

### 7.1. 添加springwolf-ui依赖项

要使用springwolf-ui，我们将[依赖项](https://mvnrepository.com/artifact/io.github.springwolf/springwolf-ui)添加到pom.xml，重建并重新启动我们的应用程序：

```xml
<dependency>
    <groupId>io.github.springwolf</groupId> 
    <artifactId>springwolf-ui</artifactId
    <version>0.8.0</version>
</dependency>
```

### 7.2. 查看AsyncAPI文档

现在，我们通过访问http://localhost:8080/springwolf/asyncapi-ui.html在浏览器中打开文档 。

与 AsyncAPI 文档相比，该网页具有类似的结构，并显示有关应用程序的信息、有关服务器、通道和架构的详细信息：

[![Springwolf用户界面](https://www.baeldung.com/wp-content/uploads/2023/07/springwolf-ui.jpg)](https://www.baeldung.com/wp-content/uploads/2023/07/springwolf-ui.jpg)

### 7.3. 发布消息

Springwolf允许我们直接从浏览器发布消息。展开通道后，单击“发布”按钮会直接将消息发送到 Kafka 上。消息绑定(包括Kafka Message Key)、标头和消息可根据需要进行调整：

[![Springwolf UI 发布](https://www.baeldung.com/wp-content/uploads/2023/07/springwolf-ui-publish-1.jpg)](https://www.baeldung.com/wp-content/uploads/2023/07/springwolf-ui-publish-1.jpg)

出于安全考虑，该功能默认处于禁用状态。为了启用发布，我们将以下行添加到application.properties 文件中：

```properties
springwolf.plugin.kafka.publishing.enabled=true
```

## 八、总结

在本文中，我们在现有的Spring BootKafka 应用程序中设置了 Springwolf。

使用消费者自动检测，自动生成符合 AsyncAPI 的文档。我们通过手动配置进一步增强了文档。

除了通过提供的 REST 端点下载 AsyncAPI 文档之外，我们还使用springwolf-ui在浏览器中查看文档。
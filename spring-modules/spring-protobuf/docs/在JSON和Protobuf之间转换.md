## 1. 概述

在本教程中，我们将演示如何从[JSON](https://www.baeldung.com/java-json)转换为[Protobuf](https://www.baeldung.com/google-protocol-buffer)以及如何从 Protobuf 转换为 JSON。

Protobuf 是一种免费开源的跨平台数据格式，用于[序列化](https://en.wikipedia.org/wiki/Serialization)结构化数据。

## 2.Maven依赖

首先，让我们通过包含[protobuf-java-util](https://search.maven.org/artifact/com.google.protobuf/protobuf-java-util)依赖项来创建一个[Spring Boot](https://www.baeldung.com/spring-boot)项目：

```java
<dependency>
    <groupId>com.google.protobuf</groupId>
    <artifactId>protobuf-java-util</artifactId>
    <version>3.21.5</version>
</dependency>
```

## 3.将JSON转换为Protobuf

我们可以使用[JsonFormat](https://developers.google.com/protocol-buffers/docs/reference/java/com/google/protobuf/util/JsonFormat)将 JSON 转换为 protobuf 消息。 JsonFormat是一个实用程序类，用于将 protobuf 消息与 JSON 格式相互转换。JsonFormat 的 parser()创建一个[Parser](https://developers.google.com/protocol-buffers/docs/reference/java/com/google/protobuf/util/JsonFormat.Parser.html)，它使用 merge()方法将 JSON 解析为 protobuf 消息。

让我们创建一个接受 JSON 并生成 protobuf 消息的方法：

```java
public static Message fromJson(String json) throws IOException {
    Builder structBuilder = Struct.newBuilder();
    JsonFormat.parser().ignoringUnknownFields().merge(json, structBuilder);
    return structBuilder.build();
}
```

让我们使用以下示例 JSON：

```json
{
    "boolean": true,
    "color": "gold",
    "object": {
      "a": "b",
      "c": "d"
    },
    "string": "Hello World"
}
```

现在，让我们编写一个简单的测试来验证从 JSON 到 protobuf 消息的转换：

```java
@Test
public void givenJson_convertToProtobuf() throws IOException {
    Message protobuf = ProtobufUtil.fromJson(jsonStr);
    Assert.assertTrue(protobuf.toString().contains("key: "boolean""));
    Assert.assertTrue(protobuf.toString().contains("string_value: "Hello World""));
}
```

## 4.将Protobuf转换为 JSON

我们可以使用JsonFormat的printer()方法将 protobuf 消息转换为 JSON，该方法接受 protobuf 作为MessageOrBuilder：

```java
public static String toJson(MessageOrBuilder messageOrBuilder) throws IOException {
    return JsonFormat.printer().print(messageOrBuilder);
}
```

让我们编写一个简单的测试来验证从 protobuf 到 JSON 消息的转换：

```java
@Test
public void givenProtobuf_convertToJson() throws IOException {
    Message protobuf = ProtobufUtil.fromJson(jsonStr);
    String json = ProtobufUtil.toJson(protobuf);
    Assert.assertTrue(json.contains(""boolean": true"));
    Assert.assertTrue(json.contains(""string": "Hello World""));
    Assert.assertTrue(json.contains(""color": "gold""));
}
```

## 5.总结

在本文中，我们演示了如何将 JSON 转换为 protobuf，反之亦然。
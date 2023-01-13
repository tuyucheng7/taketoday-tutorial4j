## 1. 概述

当使用 Gson 库在Java中处理 JSON 时，我们有多种选择可以将原始 JSON 转换为我们可以更轻松地处理的其他类或数据结构。

例如，我们可以[将 JSON 字符串转换为Map](https://www.baeldung.com/gson-json-to-map)或[使用映射创建自定义类](https://www.baeldung.com/gson-deserialization-guide)。但是，有时将我们的 JSON 转换为通用对象会很方便。

在本教程中，我们将了解[Gson](https://github.com/google/gson)如何从字符串中为我们提供JsonObject 。

## 2.Maven依赖

首先，我们需要在pom.xml中包含gson 依赖 项：

```xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.8.5</version>
</dependency>
```

我们可以 在 [Maven Central上找到最新版本的](https://search.maven.org/search?q=g:com.google.code.gson AND a:gson&core=gav)gson。

## 3. 使用JsonParser

我们将检查将 JSON String转换为JsonObject的第一种方法是使用JsonParser类的两步过程。

第一步，我们需要解析我们原来的String。

Gson 为我们提供了一个名为JsonParser 的解析器，它将指定的 JSON String解析成JsonElements的解析树：

```java
public JsonElement parse(String json) throws JsonSyntaxException
```

在JsonElement树中解析String后，我们 将使用 getAsJsonObject()方法，该方法将返回所需的结果。

让我们看看我们如何获得最终的JsonObject：

```java
String json = "{ "name": "Baeldung", "java": true }";
JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();

Assert.assertTrue(jsonObject.isJsonObject());
Assert.assertTrue(jsonObject.get("name").getAsString().equals("Baeldung"));
Assert.assertTrue(jsonObject.get("java").getAsBoolean() == true);
```

## 4.使用fromJson 

在我们的第二种方法中，我们将看到如何创建一个Gson实例并使用 fromJson方法。此方法将指定的 JSON String反序列化为指定类的对象：

```java
public <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException
```

让我们看看如何使用此方法来解析我们的 JSON String，将 JsonObject类作为第二个参数传递：

```java
String json = "{ "name": "Baeldung", "java": true }";
JsonObject convertedObject = new Gson().fromJson(json, JsonObject.class);

Assert.assertTrue(convertedObject.isJsonObject());
Assert.assertTrue(convertedObject.get("name").getAsString().equals("Baeldung"));
Assert.assertTrue(convertedObject.get("java").getAsBoolean() == true);
```

## 5.总结

在这篇简短的文章中，我们学习了两种不同的方法来使用 Gson 库从Java 中的 JSON 格式的字符串中获取JsonObject 。我们应该使用更适合我们的中间 JSON 操作的那个。
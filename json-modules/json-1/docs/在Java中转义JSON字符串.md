## 1. 概述

在这个简短的教程中，我们将展示一些在Java中转义 JSON 字符串的方法。

我们将快速浏览最流行的 JSON 处理库，以及它们如何使转义成为一项简单的任务。

## 2. 会出什么问题？

让我们考虑一个简单但常见的用例，即向 Web 服务发送用户指定的消息。天真地，我们可能会尝试：

```java
String payload = "{"message":"" + message + ""}";
sendMessage(payload);
```

但是，实际上，这会带来很多问题。

最简单的是如果消息包含引号：

```json
{ "message" : "My "message" breaks json" }
```

更糟糕的是用户可以故意破坏请求的语义。如果他发送：

```javascript
Hello", "role" : "admin
```

然后消息变成：

```java
{ "message" : "Hello", "role" : "admin" }
```

最简单的方法是用适当的转义序列替换引号：

```javascript
String payload = "{"message":"" + message.replace(""", """) + ""}";
```

但是，这种方法非常脆弱：

-   它需要为每个连接的值完成，我们需要始终牢记我们已经转义了哪些字符串
-   此外，随着消息结构随着时间的推移而变化，这可能会成为一个令人头疼的维护问题
-   而且很难阅读，更容易出错

简而言之，我们需要采用更通用的方法。遗憾的是，原生 JSON 处理功能仍处于[JEP 阶段](https://openjdk.java.net/jeps/198)，因此我们不得不将目光转向各种开源 JSON 库。

幸运的是，有[几个](https://json.org/) JSON 处理库。让我们快速浏览一下最受欢迎的三个。

## 3. JSON-java库

在我们的评论中，最简单和最小的库是[JSON-java](https://www.baeldung.com/java-org-json)，也称为org.json。

要构造一个 JSON 对象，我们只需创建一个JSONObject 实例并将其视为一个Map：

```java
JSONObject jsonObject = new JSONObject();
jsonObject.put("message", "Hello "World"");
String payload = jsonObject.toString();
```

这将采用“World”周围的引号并转义它们：

```javascript
{
   "message" : "Hello "World""
}
```

## 4. 杰克逊图书馆

用于 JSON 处理的最流行和最通用的Java库之一是[Jackson](https://www.baeldung.com/jackson)。

乍一看，Jackson 的行为类似于 org.json：

```java
Map<String, Object> params = new HashMap<>();
params.put("message", "Hello "World"");
String payload = new ObjectMapper().writeValueAsString(params);
```

然而，Jackson 也可以支持序列化Java对象。

因此，让我们通过将消息包装在自定义类中来稍微增强我们的示例：

```java
class Payload {
    Payload(String message) {
        this.message = message;
    }

    String message;
    
    // getters and setters
}

```

然后，我们需要一个 ObjectMapper实例，我们可以将我们的对象实例传递给它：

```java
String payload = new ObjectMapper().writeValueAsString(new Payload("Hello "World""));

```

在这两种情况下，我们都会得到与之前相同的结果：

```javascript
{
   "message" : "Hello "World""
}
```

如果我们有一个已经转义的属性并且需要在没有任何进一步转义的情况下对其进行序列化，我们可能希望在该字段上使用 Jackson 的@JsonRawValue注解。

## 5.Gson库

[Gson](https://www.baeldung.com/gson-deserialization-guide) 是 Google 的一个库，经常[与 Jackson 正面交锋](https://www.baeldung.com/jackson-vs-gson)。

当然，我们可以再次像对org.json 那样做：

```java
JsonObject json = new JsonObject();
json.addProperty("message", "Hello "World"");
String payload = new Gson().toJson(gsonObject);
```

或者我们可以使用自定义对象，比如 Jackson：

```java
String payload = new Gson().toJson(new Payload("Hello "World""));
```

我们将再次得到相同的结果。

## 六. 总结

在这篇简短的文章中，我们了解了如何使用不同的开源库在Java中转义 JSON 字符串。
## 1. 概述

JSON 是数据的字符串表示。我们可能希望在我们的算法或测试中比较这些数据。尽管可以比较包含 JSON 的字符串，但字符串比较对表示差异敏感，而不是内容差异。

为了克服这个问题并在语义上比较 JSON 数据，我们需要将数据加载到内存中的一个结构中，该结构不受空格或对象键顺序的影响。

在这个简短的教程中，我们将使用[Gson](https://github.com/google/gson)解决这个问题，这是一个 JSON 序列化/反序列化库，可以在 JSON 对象之间进行深入比较。

## 2. 不同字符串中语义相同的 JSON

让我们仔细看看我们要解决的问题。

假设我们有两个字符串，代表相同的 JSON 数据，但其中一个字符串的末尾有一些额外的空格：

```java
String string1 = "{"fullName": "Emily Jenkins", "age": 27    }";
String string2 = "{"fullName": "Emily Jenkins", "age": 27}";
```

虽然 JSON 对象的内容是相同的，但将上面的内容作为字符串进行比较会显示出差异：

```java
assertNotEquals(string1, string2);
```

如果对象中键的顺序不同，也会发生同样的情况，即使 JSON 通常对此不敏感：

```java
String string1 = "{"fullName": "Emily Jenkins", "age": 27}";
String string2 = "{"age": 27, "fullName": "Emily Jenkins"}";
assertNotEquals(string1, string2);
```

这就是为什么我们会受益于使用 JSON 处理库来比较 JSON 数据。

## 3.Maven依赖

要使用 Gson，让我们首先添加[Gson Maven 依赖](https://search.maven.org/artifact/com.google.code.gson/gson)项：

```xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.8.6</version>
</dependency>
```

## 4. 将JSON解析成Gson对象

在深入比较对象之前，让我们看一下 Gson 如何在Java中表示 JSON 数据。

在Java中使用 JSON 时，我们首先需要将 JSON String 转换为Java对象。Gson 提供[JsonParser](https://www.javadoc.io/doc/com.google.code.gson/gson/2.6.2/com/google/gson/JsonParser.html)将源 JSON 解析为[JsonElement](https://www.javadoc.io/doc/com.google.code.gson/gson/2.8.5/com/google/gson/JsonElement.html) 树：

```java
JsonParser parser = new JsonParser();
String objectString = "{"customer": {"fullName": "Emily Jenkins", "age": 27 }}";
String arrayString = "[10, 20, 30]";

JsonElement json1 = parser.parse(objectString);
JsonElement json2 = parser.parse(arrayString);
```

JsonElement 是一个抽象类，代表 JSON 的一个元素。parse方法返回JsonElement的实现；JsonObject 、JsonArray、JsonPrimitive或JsonNull：

```java
assertTrue(json1.isJsonObject());
assertTrue(json2.isJsonArray());
```

这些子类(JsonObject、JsonArray等)中的每一个都覆盖了Object.equals方法，提供了有效的深度 JSON 比较。

## 5. Gson 比较用例

### 5.1. 比较两个简单的 JSON 对象

假设我们有两个字符串，代表简单的 JSON 对象，其中键的顺序不同：

第一个对象的fullName早于age：

```javascript
{
    "customer": {
        "id": 44521,
        "fullName": "Emily Jenkins",
        "age": 27
    }
}
```

第二个颠倒顺序：

```javascript
{
    "customer": {
        "id": 44521,
        "age": 27,
        "fullName": "Emily Jenkins"
    }
}
```

我们可以简单地解析和比较它们：

```java
assertEquals(parser.parse(string1), parser.parse(string2));
```

在这种情况下，JsonParser返回一个JsonObject，其equals实现不 区分顺序。

### 5.2. 比较两个 JSON 数组

对于 JSON 数组，JsonParser将返回一个JsonArray。

如果我们在一个订单中有一个数组：

```java
[10, 20, 30]
assertTrue(parser.parse(string1).isJsonArray());
```

我们可以按不同的顺序将它与另一个进行比较：

```java
[20, 10, 30]
```

与JsonObject不同，JsonArray的equals方法是顺序敏感的，因此这些数组不相等，这在语义上是正确的：

```java
assertNotEquals(parser.parse(string1), parser.parse(string2));
```

### 5.3. 比较两个嵌套的 JSON 对象

正如我们之前看到的，JsonParser可以解析 JSON 的树状结构。每个JsonObject和JsonArray 都包含其他JsonElement对象，这些对象本身可以是JsonObject或JsonArray类型。

当我们使用equals时，它递归地比较所有成员，这意味着嵌套对象也是可比较的：

如果这是string1：

```javascript
{
  "customer": {
    "id": "44521",
    "fullName": "Emily Jenkins",
    "age": 27,
    "consumption_info": {
      "fav_product": "Coke",
      "last_buy": "2012-04-23"
    }
  }
}
```

这个 JSON 是string2：

```javascript
{
  "customer": {
    "fullName": "Emily Jenkins",
    "id": "44521",
    "age": 27,
    "consumption_info": {
      "last_buy": "2012-04-23",
      "fav_product": "Coke"
   }
  }
}
```

然后我们仍然可以使用equals方法来比较它们：

```java
assertEquals(parser.parse(string1), parser.parse(string2));
```

## 六. 总结

在这篇简短的文章中，我们研究了将 JSON 作为String进行比较的挑战。我们已经看到 Gson 如何允许我们将这些字符串解析为支持比较的对象结构。
## 1. 概述

在Java中处理原始 JSON 值时，有时需要检查它是否有效。有几个库可以帮助我们解决这个问题：[Gson](https://www.baeldung.com/gson-deserialization-guide)、[JSON API](https://www.baeldung.com/java-org-json)和[Jackson](https://www.baeldung.com/jackson)。每个工具都有自己的优点和局限性。

在本教程中，我们将使用它们中的每一个来实现 JSON字符串验证，并通过实际示例仔细研究这些方法之间的主要区别。

## 2. 使用 JSON API 验证

最轻量级和最简单的库是 JSON API。

检查String是否为有效 JSON 的常用方法是异常处理。因此，我们委托 JSON 解析并在值不正确的情况下处理特定类型的错误，或者如果没有发生异常则假设值是正确的。

### 2.1. Maven 依赖

首先，我们需要在pom.xml中包含[json](https://mvnrepository.com/artifact/org.json/json) 依赖 项：

```xml
<dependency>
    <groupId>org.json</groupId>
    <artifactId>json</artifactId>
    <version>20211205</version>
</dependency>
```

### 2.2. 使用JSONObject验证

首先，要检查字符串是否为 JSON，我们将尝试创建一个JSONObject。此外，如果值无效，我们将得到一个JSONException：

```java
public boolean isValid(String json) {
    try {
        new JSONObject(json);
    } catch (JSONException e) {
        return false;
    }
    return true;
}
```

让我们用一个简单的例子来尝试一下：

```java
String json = "{"email": "example@com", "name": "John"}";
assertTrue(validator.isValid(json));
String json = "Invalid_Json"; 
assertFalse(validator.isValid(json));
```

但是，这种方法的缺点是 String只能是一个对象，不能是使用JSONObject的数组。

例如，让我们看看它如何处理数组：

```java
String json = "[{"email": "example@com", "name": "John"}]";
assertFalse(validator.isValid(json));
```

### 2.3. 使用JSONArray验证

无论String是对象还是数组，为了验证，如果JSONObject创建失败，我们需要添加一个额外的条件。同样，如果String也不适合 JSON 数组，则JSONArray将抛出 JSONException：

```java
public boolean isValid(String json) {
    try {
        new JSONObject(json);
    } catch (JSONException e) {
        try {
            new JSONArray(json);
        } catch (JSONException ne) {
            return false;
        }
    }
    return true;
}
```

因此，我们可以验证任何值：

```java
String json = "[{"email": "example@com", "name": "John"}]";
assertTrue(validator.isValid(json));
```

## 3. 杰克逊验证

同样，Jackson 库提供了一种基于异常处理来验证 JSON 的方法。它是一个更复杂的工具，具有多种类型的解析策略。但是，它更容易使用。

### 3.1. Maven 依赖

让我们添加[jackson-databind](https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind) Maven 依赖项：

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.13.0</version>
</dependency>
```

### 3.2. 使用ObjectMapper验证

我们使用readTree()方法读取整个 JSON 并在语法不正确时获取JacksonException 。

换句话说，我们不需要提供额外的检查。它适用于对象和数组：

```java
ObjectMapper mapper = new ObjectMapper();

public boolean isValid(String json) {
    try {
        mapper.readTree(json);
    } catch (JacksonException e) {
        return false;
    }
    return true;
}
```

让我们看看如何通过示例使用它：

```java
String json = "{"email": "example@com", "name": "John"}";
assertTrue(validator.isValid(json));

String json = "[{"email": "example@com", "name": "John"}]";
assertTrue(validator.isValid(json));

String json = "Invalid_Json";
assertFalse(validator.isValid(json));
```

## 4. 使用 Gson 验证

Gson 是另一个通用库，它允许我们使用相同的方法验证原始 JSON 值。它是一个复杂的工具，用于将Java对象映射到不同类型的 JSON 处理。

### 4.1. Maven 依赖

让我们添加[gson](https://mvnrepository.com/artifact/com.google.code.gson/gson) Maven 依赖项：

```xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.8.5</version>
</dependency>
```

### 4.2. 非严格验证

Gson 提供JsonParser以将指定的 JSON 读取到JsonElement的树中。因此，如果在读取时出现错误，它可以保证我们得到JsonSyntaxException 。

因此，我们可以使用parse()方法来计算String并在出现格式错误的 JSON 值时处理异常：

```java
public boolean isValid(String json) {
    try {
        JsonParser.parseString(json);
    } catch (JsonSyntaxException e) {
        return false;
    }
    return true;
}
```

让我们编写一些测试来检查主要情况：

```java
String json = "{"email": "example@com", "name": "John"}";
assertTrue(validator.isValid(json));

String json = "[{"email": "example@com", "name": "John"}]";
assertTrue(validator.isValid(json));
```

这种方法的主要区别在于 Gson 默认策略将单独的字符串和数值视为有效的JsonElement节点的一部分。换句话说，它也将单个字符串或数字视为有效。

例如，让我们看看它如何处理单个字符串：

```java
String json = "Invalid_Json";
assertTrue(validator.isValid(json));
```

但是，如果我们想将此类值视为格式错误，则需要对JsonParser执行严格的类型策略。

### 4.3. 严格验证

为了实施严格的类型策略，我们创建了一个TypeAdapter并将JsonElement类定义为必需的类型匹配。因此，如果类型不是 JSON 对象或数组， JsonParser 将抛出JsonSyntaxException 。

我们可以使用特定的TypeAdapter调用fromJson()方法来读取原始 JSON ：

```java
final TypeAdapter<JsonElement> strictAdapter = new Gson().getAdapter(JsonElement.class);

public boolean isValid(String json) {
    try {
        strictAdapter.fromJson(json);
    } catch (JsonSyntaxException | IOException e) {
        return false;
    }
    return true;
}
```

最后，我们可以检查 JSON 是否有效：

```java
String json = "Invalid_Json";
assertFalse(validator.isValid(json));
```

## 5.总结

在本文中，我们看到了检查字符串是否为有效 JSON 的不同方法。

每种方法都有其优点和局限性。虽然 JSON API 可用于简单的对象验证，但作为 JSON 对象的一部分，Gson 可以更可扩展地用于原始值验证。但是，Jackson 更易于使用。因此，我们应该使用更适合的那个。

此外，我们应该检查一些库是否已经在使用或是否也适用于其余目标。
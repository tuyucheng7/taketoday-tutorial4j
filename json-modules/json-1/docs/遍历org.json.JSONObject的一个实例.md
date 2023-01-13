## 1. 简介

在本教程中，我们将了解几种迭代 [JSONObject](https://www.baeldung.com/java-org-json)的方法，JSONObject是Java的简单 JSON 表示形式。

我们将从一个简单的解决方案开始，然后看看更稳健的解决方案。

## 2. 遍历JSONObject

让我们从迭代名称-值对的 JSON 的简单案例开始：

```plaintext
{
  "name": "Cake",
  "cakeId": "0001",
  "cakeShape": "Heart"
}
```

为此，我们可以使用keys() 方法简单地遍历键 ：

```java
void handleJSONObject(JSONObject jsonObject) {
    jsonObject.keys().forEachRemaining(key -> {
        Object value = jsonObject.get(key);
        logger.info("Key: {0}tValue: {1}", key, value);
    }
}
```

我们的输出将是：

```plaintext
Key: name      Value: Cake
Key: cakeId    Value: 0001
Key: cakeShape Value: Heart
```

## 3.遍历一个JSONObject

但是假设我们有一个更复杂的结构：

```plaintext
{
  "batters": [
    {
      "type": "Regular",
      "id": "1001"
    },
    {
      "type": "Chocolate",
      "id": "1002"
    },
    {
      "type": "BlueBerry",
      "id": "1003"
    }
  ],
  "name": "Cake",
  "cakeId": "0001"
}
```

在这种情况下，遍历键意味着什么？

让我们来看看我们朴素的 keys() 方法会给我们带来什么：

```java
Key: batters    Value: [{"type":"Regular","id":"1001"},{"type":"Chocolate","id":"1002"},
  {"type":"BlueBerry","id":"1003"}]
Key: name       Value: Cake
Key: cakeId     Value: 0001
```

这也许不是很有帮助。在这种情况下，我们想要的似乎不是迭代，而是遍历。

遍历JSONObject不同于遍历 JSONObject的键集。

为此，我们实际上也需要检查值类型。想象一下我们用一个单独的方法来做到这一点：

```java
void handleValue(Object value) {
    if (value instanceof JSONObject) {
        handleJSONObject((JSONObject) value);
    } else if (value instanceof JSONArray) {
        handleJSONArray((JSONArray) value);
    } else {
        logger.info("Value: {0}", value);
    }
}
```

然后，我们的方法仍然非常相似：

```java
void handleJSONObject(JSONObject jsonObject) {
    jsonObject.keys().forEachRemaining(key -> {
        Object value = jsonObject.get(key);
        logger.info("Key: {0}", key);
        handleValue(value);
    });
}
```

唯一的问题是我们需要考虑如何处理数组。

## 4. 遍历JSONArray

让我们尝试并保持使用迭代器的类似方法。不过，我们 不调用keys() ，而是调用iterator()：

```java
void handleJSONArray(JSONArray jsonArray) {
    jsonArray.iterator().forEachRemaining(element -> {
        handleValue(element)
    });
}
```

现在，这个解决方案是有限的，因为我们将遍历与我们想要采取的行动结合起来。将两者分开的常用方法是使用 [访问者模式](https://www.baeldung.com/java-visitor-pattern)。

## 5.总结

在本文中，我们看到了一种为简单的名称-值对迭代JSONObject的方法、与复杂结构相关的问题以及解决它的遍历技术。

当然，这是一种深度优先的遍历方法，但是我们可以用类似的方式进行广度优先。
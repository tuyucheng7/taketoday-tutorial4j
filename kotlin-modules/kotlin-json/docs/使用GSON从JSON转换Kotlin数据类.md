## 1. 概述

在这个简短的教程中，我们将讨论如何使用 Gson Java 库将 Kotlin 中的数据类转换 为 JSON 字符串，反之亦然。

## 2.Maven依赖

在我们开始之前，让我们将[Gson](https://search.maven.org/search?q=g:com.google.code.gson AND a:gson)添加到我们的pom.xml 中：

```xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.8.5</version>
</dependency>
```

## 3. Kotlin数据类

首先，让我们创建一个数据类，我们将在本文的后面部分将其转换为 JSON 字符串：

```java
data class TestModel(
    val id: Int,
    val description: String
)
```

TestModel 类由 2 个属性组成 ：id和name。因此，我们期望从 Gson 获得的 JSON 字符串如下所示：

```java
{"id":1,"description":"Test"}
```

## 4. 从数据类转换为 JSON 字符串

现在，我们可以使用 Gson将TestModel类的对象转换为 JSON：

```java
var gson = Gson()
var jsonString = gson.toJson(TestModel(1,"Test"))
Assert.assertEquals(jsonString, """{"id":1,"description":"Test"}""")
```

在此示例中，我们使用Assert检查 Gson 的输出是否符合我们的预期值。

## 5. 从 JSON 字符串转换为数据类

当然，有时我们需要将 JSON 转换为数据对象：

```java
var jsonString = """{"id":1,"description":"Test"}""";
var testModel = gson.fromJson(jsonString, TestModel::class.java)
Assert.assertEquals(testModel.id, 1)
Assert.assertEquals(testModel.description, "Test")
```

在这里，我们通过告诉 Gson 使用TestModel::class.java 将 JSON 字符串转换为TestModel对象，因为Gson 是一个 Java 库并且只接受 Java 类。

最后，我们测试结果对象是否包含原始字符串中的正确值。

## 六，总结

在这篇快速文章中，我们讨论了如何在 Kotlin 中使用 Gson 将 Kotlin数据类转换为 JSON 字符串，反之亦然。
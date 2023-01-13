## 1. 概述

[Gson](https://github.com/google/gson)是一个Java库，它允许我们将Java对象转换为 JSON 表示。我们也可以反过来使用它，将 JSON 字符串转换为等效的Java对象。

在本快速教程中，我们将了解如何将各种Java数据类型保存为文件中的 JSON。

## 2.Maven依赖

首先，我们需要在pom.xml中添加 Gson 依赖。这在[Maven Central](https://search.maven.org/classic/#search|gav|1|g%3A"com.google.code.gson" AND a%3A"gson")中可用：

```xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.8.5</version>
</dependency>
```

## 3. 将数据保存到 JSON 文件

我们将使用 [Gson类中的](https://static.javadoc.io/com.google.code.gson/gson/2.8.5/com/google/gson/Gson.html)[toJson(Object src, Appendable writer)](https://static.javadoc.io/com.google.code.gson/gson/2.8.5/com/google/gson/Gson.html#toJson-java.lang.Object-java.lang.Appendable-) 方法 将Java数据类型转换为 JSON 并将其存储在文件中。Gson()构造函数使用默认配置创建一个Gson对象：

```java
Gson gson = new Gson();
```

现在，我们可以调用toJson () 来转换和存储Java对象。

让我们探索Java中不同数据类型的一些示例。

### 3.1. 基元

使用 GSON 将原语保存到 JSON 文件非常简单：

```java
gson.toJson(123.45, new FileWriter(filePath));
```

此处， filePath表示文件的位置。文件输出将只包含原始值：

```xml
123.45
```

### 3.2. 自定义对象

同样，我们可以将对象存储为 JSON。

首先，我们将创建一个简单的用户类：

```java
public class User {
    private int id;
    private String name;
    private transient String nationality;

    public User(int id, String name, String nationality) {
        this.id = id;
        this.name = name;
        this.nationality = nationality;
    }
    
    public User(int id, String name) {
        this(id, name, null);
    }
}
```

现在，我们将User对象存储为 JSON：

```java
User user = new User(1, "Tom Smith", "American");
gson.toJson(user, new FileWriter(filePath));
```

文件输出将是：

```xml
{"id":1,"name":"Tom"}
```

如果一个字段被标记为transient，它默认被忽略并且不包含在 JSON 序列化或反序列化中。因此，国籍字段不存在于 JSON 输出中。

同样默认情况下，Gson 在序列化过程中会忽略空字段。所以如果我们考虑这个例子：

```java
gson.toJson(new User(1, null, "Unknown"), new FileWriter(filePath));
```

文件输出将是：

```xml
{"id":1}
```

稍后我们将看到如何在序列化中包含空字段。

### 3.3. 收藏品

我们可以用类似的方式存储一组对象：

```java
User[] users = new User[] { new User(1, "Mike"), new User(2, "Tom") };
gson.toJson(users, new FileWriter(filePath));
```

在这种情况下，文件输出将是一个用户对象数组：

```xml
[{"id":1,"name":"Mike"},{"id":2,"name":"Tom"}]
```

## 4. 使用GsonBuilder

为了调整默认的 Gson 配置设置，我们可以使用[GsonBuilder](https://static.javadoc.io/com.google.code.gson/gson/2.8.5/com/google/gson/GsonBuilder.html)类。

此类遵循构建器模式，通常通过首先调用各种配置方法来设置所需选项，最后调用 create()方法来使用它：

```java
Gson gson = new GsonBuilder()
  .setPrettyPrinting()
  .create();
```

在这里，我们将漂亮的打印选项设置为默认设置为 false。同样，要在序列化中包含空值，我们可以调用 serializeNulls()。[此处](https://static.javadoc.io/com.google.code.gson/gson/2.8.5/com/google/gson/Gson.html#method.detail)列出了可用的选项。

## 5.总结

在这篇快速文章中，我们了解了如何将各种Java数据类型序列化为 JSON 文件。要探索有关 JSON 的各种文章，请查看我们 关于该主题[的其他教程。](https://www.baeldung.com/category/json/)
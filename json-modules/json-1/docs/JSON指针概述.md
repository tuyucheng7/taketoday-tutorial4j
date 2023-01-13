## 1. 概述

在本教程中，我们将展示如何使用 JSON 指针从 JSON 数据中导航和获取信息。

我们还将展示如何执行插入新数据或更新现有键值等操作。

## 2.依赖设置

首先，我们需要向我们的pom.xml添加一些依赖项：

```xml
<dependency>
    <groupId>org.glassfish</groupId>
    <artifactId>javax.json</artifactId>
    <version>1.1.2</version>
</dependency>
```

## 3. JSON 指针

[JSON](https://www.json.org/) (“JavaScript Object Notation”)是一种用于在系统之间交换数据的轻量级格式，最初由 Douglas Crockford 指定。

尽管它使用JavaScript语法，但它与语言无关，因为结果是纯文本。

JSON 指针 ( [RFC 6901](https://tools.ietf.org/html/rfc6901) ) 是 JSON Processing 1.1 API ( [JSR 374](https://jcp.org/en/jsr/detail?id=374) ) 的一项功能。它定义了一个可用于访问 JSON 文档中的值的字符串。它可能与 XPath 对 XML 文档的作用有关。

通过使用 JSON 指针，我们可以从 JSON 文件中获取数据和更改数据。

## 4. 访问数据

我们将看到一些示例，说明如何通过实现名为JsonPointerCrud的类来执行操作。

假设我们有一个名为books.json的 JSON 文件，其内容为：

```javascript
{
    "library": "My Personal Library",
    "books": [
        { "title":"Title 1", "author":"Jane Doe" },
        { "title":"Title 2", "author":"John Doe" }
    ]
}
```

要访问该文件中的数据，我们需要读取它并将其解析为JsonStructure。我们可以使用Json.createReader()方法来实现它，该方法接受 InputStream或FileReader。

我们可以这样做：

```java
JsonReader reader = Json.createReader(new FileReader("books.json"));
JsonStructure jsonStructure = reader.read();
reader.close();
```

内容将存储在JsonStructure对象中。这是我们将用来执行下一个操作的对象。

### 4.1. 从文件中获取数据

为了获取单个值，我们创建了一个JsonPointer，通知我们要从哪个标签获取值：

```java
JsonPointer jsonPointer = Json.createPointer("/library");
JsonString jsonString = (JsonString) jsonPointer.getValue(jsonStructure);
System.out.println(jsonString.getString());
```

请注意，此字符串的第一个字符是“ /” ——这是句法要求。

这段代码的结果是：

```plaintext
My Personal Library
```

要从列表中获取值，我们需要指定其索引(第一个索引为 0)：

```java
JsonPointer jsonPointer = Json.createPointer("/books/1");
JsonObject jsonObject = (JsonObject) jsonPointer.getValue(jsonStructure);
System.out.println(jsonObject.toString());
```

这输出：

```plaintext
"title":"Title 2", "author":"John Doe"
```

### 4.2. 检查文件中是否存在密钥

通过containsValue方法，我们可以检查用于创建指针的值是否存在于 JSON 文件中：

```java
JsonPointer jsonPointer = Json.createPointer("/library");
boolean found = jsonPointer.containsValue(jsonStructure);
System.out.println(found);

```

该片段的结果是：

```plaintext
true
```

### 4.3. 插入新键值

如果我们需要向 JSON 添加新值，createValue就是处理它的方法。方法 createValue被重载以接受String、int、long、double、BigDecimal和BigInteger ：

```java
JsonPointer jsonPointer = Json.createPointer("/total");
JsonNumber jsonNumber = Json.createValue(2);
jsonStructure = jsonPointer.add(jsonStructure, jsonNumber);
System.out.println(jsonStructure);
```

同样，我们的输出是：

```javascript
{
    "library": "My Personal Library",
    "total": 2,
    "books": [
        { "title":"Title 1", "author":"Jane Doe" },
        { "title":"Title 2", "author":"John Doe" }
    ]
}
```

### 4.4. 更新键值

要更新一个值，我们需要先创建新值。创建值后，我们使用使用 key 参数创建的指针的replace方法：

```java
JsonPointer jsonPointer = Json.createPointer("/total");
JsonNumber jsonNumberNewValue = Json.createValue(5);
jsonStructure = jsonPointer.replace(jsonStructure, jsonNumberNewValue);
System.out.println(jsonStructure);
```

输出：

```javascript
{
    "library": "My Personal Library",
    "total": 5,
    "books": [
        { "title":"Title 1", "author":"Jane Doe" },
        { "title":"Title 2", "author":"John Doe" }
    ]
}
```

### 4.5. 删除密钥

要删除一个键，我们首先创建一个指向该键的指针。然后我们使用 remove 方法：

```java
JsonPointer jsonPointer = Json.createPointer("/library");
jsonPointer.getValue(jsonStructure);
jsonStructure = jsonPointer.remove(jsonStructure);
System.out.println(jsonStructure);
```

导致：

```javascript
{
    "total": 5,
    "books": [
        { "title":"Title 1", "author":"Jane Doe" },
        { "title":"Title 2", "author":"John Doe" }
    ]
}
```

### 4.6. 显示文件的全部内容

如果指针是用空String创建的，则会检索整个内容：

```java
JsonPointer jsonPointer = Json.createPointer("");
JsonObject jsonObject = (JsonObject) jsonPointer.getValue(jsonStructure);
System.out.println(jsonObject.toString());
```

此代码示例将输出jsonStructure的全部内容。

## 5.总结

在这篇快速文章中，我们介绍了如何使用 JSON Pointer 对 JSON 数据执行各种操作。
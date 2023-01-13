## 1. 简介

近年来，JavaScript Object Notation 或 JSON 作为一种数据交换格式获得了广泛的欢迎。[Jsoniter](https://jsoniter.com/)是一个新的 JSON 解析库，旨在提供比其他可用解析器更灵活、性能更高的 JSON 解析。

在本教程中，我们将了解如何使用Java的 Jsoniter 库解析 JSON 对象。

## 2.依赖关系

最新版本的[Jsoniter](https://search.maven.org/search?q=a:jsoniter AND g:com.jsoniter)可以从 Maven Central 存储库中找到。

让我们从将依赖项添加到pom.xml开始：

```java
<dependency>
    <groupId>com.jsoniter<groupId> 
    <artifactId>jsoniter</artifactId>
    <version>0.9.23</version>
</dependency>

```

同样，我们可以将依赖项添加到我们的build.gradle文件中：

```java
compile group: 'com.jsoniter', name: 'jsoniter', version: '0.9.23'

```

## 3.使用Jsoniter解析JSON

Jsoniter 提供了 3 个 API 来解析 JSON 文档：

-   绑定接口
-   任何API
-   迭代器API

让我们看看上面的每个 API。

### 3.1. 使用 Bind API 进行 JSON 解析

绑定 API 使用传统方式将 JSON 文档绑定到Java类。

让我们考虑包含学生详细信息的 JSON 文档：

```java
{"id":1,"name":{"firstName":"Joe","surname":"Blogg"}}
```

现在让我们定义Student和Name模式类来表示上述 JSON：

```java
public class Student {
    private int id;
    private Name name;
    
    // standard setters and getters
}
public class Name {
    private String firstName;
    private String surname;
    
    // standard setters and getters
}
```

使用绑定 API 将 JSON 反序列化为Java对象非常简单。我们使用JsonIterator的反序列化方法：

```java
@Test
public void whenParsedUsingBindAPI_thenConvertedToJavaObjectCorrectly() {
    String input = "{"id":1,"name":{"firstName":"Joe","surname":"Blogg"}}";
    
    Student student = JsonIterator.deserialize(input, Student.class);

    assertThat(student.getId()).isEqualTo(1);
    assertThat(student.getName().getFirstName()).isEqualTo("Joe");
    assertThat(student.getName().getSurname()).isEqualTo("Blogg");
}
```

Student 模式类将id声明为int数据类型。但是，如果我们收到的 JSON 包含id的字符串值 而不是数字怎么办？例如：

```java
{"id":"1","name":{"firstName":"Joe","surname":"Blogg"}}
```

请注意这次 JSON 中的id是一个字符串值“1”。Jsoniter 提供了Maybe解码器来处理这种情况。

### 3.2. 也许解码器

当 JSON 元素的数据类型为 fuzzy 时， Jsoniter 的Maybe解码器会派上用场。student.id字段的数据类型是模糊的——它可以是String或int。为了处理这个问题，我们需要使用MaybeStringIntDecoder在我们的模式类中注解id字段：

```java
public class Student {
    @JsonProperty(decoder = MaybeStringIntDecoder.class)
    private int id;
    private Name name;
    
    // standard setters and getters
}

```

我们现在可以解析 JSON，即使id值是一个String：

```java
@Test
public void givenTypeInJsonFuzzy_whenFieldIsMaybeDecoded_thenFieldParsedCorrectly() {
    String input = "{"id":"1","name":{"firstName":"Joe","surname":"Blogg"}}";
    
    Student student = JsonIterator.deserialize(input, Student.class);

    assertThat(student.getId()).isEqualTo(1); 
}
```

同样，Jsoniter 提供其他解码器，例如MaybeStringLongDecoder和MaybeEmptyArrayDecoder。

现在让我们想象一下，我们希望收到一个包含学生详细信息的 JSON 文档，但我们收到的却是以下文档：

```java
{"error":404,"description":"Student record not found"}

```

这里发生了什么？我们期待对学生数据的成功响应，但我们收到了错误响应。这是一个非常常见的场景，但我们将如何处理呢？

一种方法是在提取学生数据之前执行空检查以查看我们是否收到错误响应。然而，空值检查会导致一些难以阅读的代码，如果我们有一个多层嵌套的 JSON，问题会变得更糟。

Jsoniter 使用Any API 的解析来解决这个问题。

### 3.3. 使用 Any API 进行 JSON 解析

当 JSON 结构本身是动态的时，我们可以使用 Jsoniter 的Any API，它提供了 JSON 的无模式解析。这类似于将 JSON 解析为Map<String, Object>。

让我们像以前一样解析Student JSON，但这次使用Any API：

```java
@Test
public void whenParsedUsingAnyAPI_thenFieldValueCanBeExtractedUsingTheFieldName() {
    String input = "{"id":1,"name":{"firstName":"Joe","surname":"Blogg"}}";
    
    Any any = JsonIterator.deserialize(input);

    assertThat(any.toInt("id")).isEqualTo(1);
    assertThat(any.toString("name", "firstName")).isEqualTo("Joe");
    assertThat(any.toString("name", "surname")).isEqualTo("Blogg"); 
}
```

让我们理解这个例子。首先，我们使用JsonIterator.deserialize(..)来解析 JSON。但是，我们在这个实例中没有指定模式类。结果是Any 类型。

接下来，我们使用字段名称读取字段值。我们使用Any.toInt方法读取“id”字段值。toInt方法将“id”值转换为整数。同样，我们使用toString方法将“name.firstName”和“name.surname”字段值读取为字符串值。

使用Any API，我们还可以检查 JSON 中是否存在元素。我们可以通过查找元素然后检查查找结果的valueType来做到这一点。当JSON 中不存在该元素时，valueType将为INVALID 。

例如：

```java
@Test
public void whenParsedUsingAnyAPI_thenFieldValueTypeIsCorrect() {
    String input = "{"id":1,"name":{"firstName":"Joe","surname":"Blogg"}}";
    
    Any any = JsonIterator.deserialize(input);

    assertThat(any.get("id").valueType()).isEqualTo(ValueType.NUMBER);
    assertThat(any.get("name").valueType()).isEqualTo(ValueType.OBJECT);
    assertThat(any.get("error").valueType()).isEqualTo(ValueType.INVALID);
}
```

“id”和“name”字段存在于 JSON 中，因此它们的valueType分别为NUMBER和OBJECT。但是，JSON 输入没有名为“error”的元素，因此valueType为INVALID。

回到上一节末尾提到的场景，我们需要检测我们收到的 JSON 输入是成功响应还是错误响应。我们可以通过检查“error”元素的valueType来检查我们是否收到了错误响应：

```java
String input = "{"error":404,"description":"Student record not found"}";
Any response = JsonIterator.deserialize(input);

if (response.get("error").valueType() != ValueType.INVALID) {
    return "Error!! Error code is " + response.toInt("error");
}
return "Success!! Student id is " + response.toInt("id");
```

运行时，上面的代码会返回“Error!! 错误代码是 404”。

接下来，我们将看看使用 Iterator API 来解析 JSON 文档。

### 3.4. 使用 Iterator API 进行 JSON 解析

如果我们希望手动执行绑定，我们可以使用 Jsoniter 的Iterator API。让我们考虑一下 JSON：

```java
{"firstName":"Joe","surname":"Blogg"}
```

我们将使用我们之前使用的Name模式类来使用Iterator API 解析 JSON：

```java
@Test
public void whenParsedUsingIteratorAPI_thenFieldValuesExtractedCorrectly() throws Exception {
    Name name = new Name();    
    String input = "{"firstName":"Joe","surname":"Blogg"}";
    JsonIterator iterator = JsonIterator.parse(input);

    for (String field = iterator.readObject(); field != null; field = iterator.readObject()) {
        switch (field) {
            case "firstName":
                if (iterator.whatIsNext() == ValueType.STRING) {
                    name.setFirstName(iterator.readString());
                }
                continue;
            case "surname":
                if (iterator.whatIsNext() == ValueType.STRING) {
                    name.setSurname(iterator.readString());
                }
                continue;
            default:
                iterator.skip();
        }
    }

    assertThat(name.getFirstName()).isEqualTo("Joe");
    assertThat(name.getSurname()).isEqualTo("Blogg");
}
```

让我们理解上面的例子。首先，我们将 JSON 文档解析为迭代器。我们使用生成的JsonIterator实例来迭代 JSON 元素：

1.  我们首先调用readObject方法，该方法返回下一个字段名称(如果已到达文档末尾，则为null )。
2.  如果我们对字段名称不感兴趣，我们可以使用skip方法跳过 JSON 元素。否则，我们使用whatIsNext方法检查元素的数据类型。调用whatIsNext方法不是强制性的，但在我们不知道字段的数据类型时很有用。
3.  最后，我们使用readString方法提取 JSON 元素的值。

## 4. 总结

在本文中，我们讨论了 Jsoniter 提供的用于将 JSON 文档解析为Java对象的各种方法。

首先，我们查看了使用模式类解析 JSON 文档的标准方法。

接下来，我们研究了在分别使用Maybe解码器和Any数据类型解析 JSON 文档时处理模糊数据类型和动态结构。

最后，我们查看了用于将 JSON 手动绑定到Java对象的Iterator API。
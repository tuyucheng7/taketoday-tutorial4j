## 1. 简介

使用所有字段的精确一对一表示将我们的完整数据结构序列化为 JSON 有时可能不合适，或者根本不是我们想要的。相反，我们可能希望创建数据的扩展或简化视图。这就是自定义 Jackson 序列化程序发挥作用的地方。

但是，实现自定义序列化程序可能很乏味，尤其是当我们的模型对象有很多字段、集合或嵌套对象时。幸运的是，[Jackson 图书馆](https://www.baeldung.com/jackson)有几项规定可以使这项工作简单很多。

在这个简短的教程中，我们将了解自定义 Jackson 序列化器并展示如何在自定义序列化器中访问默认序列化器。

## 2. 样本数据模型

在深入了解 Jackson 的自定义之前，让我们看一下我们要序列化的示例Folder类：

```java
public class Folder {
    private Long id;
    private String name;
    private String owner;
    private Date created;
    private Date modified;
    private Date lastAccess;
    private List<File> files = new ArrayList<>();

    // standard getters and setters
}

```

和File类，它在我们的Folder类中被定义为一个List：

```java
public class File {
    private Long id;
    private String name;

    // standard getters and setters
}

```

## 3. Jackson 中的自定义序列化程序

使用自定义序列化器的主要优点是我们不必修改我们的类结构。另外，我们可以轻松地将预期行为与类本身分离。

所以，假设我们想要Folder类的简化视图：

```javascript
{
    "name": "Root Folder",
    "files": [
        {"id": 1, "name": "File 1"},
        {"id": 2, "name": "File 2"}
    ]
}

```

正如我们将在下一节中看到的那样，有几种方法可以在 Jackson 中实现我们想要的输出。

### 3.1. 蛮力方法

首先，在不使用 Jackson 的默认序列化器的情况下，我们可以创建一个自定义序列化器，在其中我们自己完成所有繁重的工作。

让我们为Folder类创建一个自定义序列化程序来实现这一点：

```java
public class FolderJsonSerializer extends StdSerializer<Folder> {

    public FolderJsonSerializer() {
        super(Folder.class);
    }

    @Override
    public void serialize(Folder value, JsonGenerator gen, SerializerProvider provider)
      throws IOException {
        gen.writeStartObject();
        gen.writeStringField("name", value.getName());

        gen.writeArrayFieldStart("files");
        for (File file : value.getFiles()) {
            gen.writeStartObject();
            gen.writeNumberField("id", file.getId());
            gen.writeStringField("name", file.getName());
            gen.writeEndObject();
        }
        gen.writeEndArray();

        gen.writeEndObject();
    }
}
```

因此，我们可以将Folder类序列化为只包含我们想要的字段的简化视图。

### 3.2. 使用内部ObjectMapper

尽管自定义序列化器为我们提供了详细更改每个属性的灵活性，但我们可以通过重用 Jackson 的默认序列化器来简化我们的工作。

使用默认序列化程序的一种方法是访问内部ObjectMapper类：

```java
@Override
public void serialize(Folder value, JsonGenerator gen, SerializerProvider provider) throws IOException {
    gen.writeStartObject();
    gen.writeStringField("name", value.getName());

    ObjectMapper mapper = (ObjectMapper) gen.getCodec();
    gen.writeFieldName("files");
    String stringValue = mapper.writeValueAsString(value.getFiles());
    gen.writeRawValue(stringValue);

    gen.writeEndObject();
}

```

因此，Jackson 只是通过序列化File对象列表来处理繁重的工作，然后我们的输出将是相同的。

### 3.3. 使用SerializerProvider

调用默认序列化程序的另一种方法是使用SerializerProvider。因此，我们将该过程委托给File类型的默认序列化程序。

现在，让我们在SerializerProvider的帮助下稍微简化我们的代码：

```java
@Override
public void serialize(Folder value, JsonGenerator gen, SerializerProvider provider) throws IOException {
    gen.writeStartObject();
    gen.writeStringField("name", value.getName());

    provider.defaultSerializeField("files", value.getFiles(), gen);

    gen.writeEndObject();
}

```

而且，就像以前一样，我们得到相同的输出。

## 4.一个可能的递归问题

根据用例，我们可能需要通过包含Folder的更多详细信息来扩展我们的序列化数据。这可能是为了集成我们没有机会修改的遗留系统或外部应用程序。

让我们更改我们的序列化程序，为我们的序列化数据创建一个详细信息字段，以简单地公开Folder类的所有字段：

```java
@Override
public void serialize(Folder value, JsonGenerator gen, SerializerProvider provider) throws IOException {
    gen.writeStartObject();
    gen.writeStringField("name", value.getName());

    provider.defaultSerializeField("files", value.getFiles(), gen);

    // this line causes exception
    provider.defaultSerializeField("details", value, gen);

    gen.writeEndObject();
}

```

这次我们得到一个StackOverflowError异常。

当我们定义自定义序列化程序时，Jackson 在内部覆盖为Folder类型创建的原始BeanSerializer实例。因此，我们的SerializerProvider每次都会找到自定义的序列化器，而不是默认的序列化器，这会导致无限循环。

那么，我们如何解决这个问题呢？我们将在下一节中看到针对此场景的一个可用解决方案。

## 5.使用BeanSerializerModifier

一种可能的解决方法是使用BeanSerializerModifier 在 Jackson 内部覆盖它之前存储类型Folder的默认序列化程序。

让我们修改我们的序列化器并添加一个额外的字段 — defaultSerializer：

```java
private final JsonSerializer<Object> defaultSerializer;

public FolderJsonSerializer(JsonSerializer<Object> defaultSerializer) {
    super(Folder.class);
    this.defaultSerializer = defaultSerializer;
}

```

接下来，我们将创建BeanSerializerModifier的实现来传递默认序列化程序：

```java
public class FolderBeanSerializerModifier extends BeanSerializerModifier {

    @Override
    public JsonSerializer<?> modifySerializer(
      SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {

        if (beanDesc.getBeanClass().equals(Folder.class)) {
            return new FolderJsonSerializer((JsonSerializer<Object>) serializer);
        }

        return serializer;
    }
}

```

现在，我们需要将BeanSerializerModifier注册为模块以使其工作：

```java
ObjectMapper mapper = new ObjectMapper();

SimpleModule module = new SimpleModule();
module.setSerializerModifier(new FolderBeanSerializerModifier());

mapper.registerModule(module);

```

然后，我们将defaultSerializer用于details字段：

```java
@Override
public void serialize(Folder value, JsonGenerator gen, SerializerProvider provider) throws IOException {
    gen.writeStartObject();
    gen.writeStringField("name", value.getName());

    provider.defaultSerializeField("files", value.getFiles(), gen);

    gen.writeFieldName("details");
    defaultSerializer.serialize(value, gen, provider);

    gen.writeEndObject();
}

```

最后，我们可能希望从详细信息中删除文件字段，因为我们已经将它单独写入序列化数据中。

因此，我们只需忽略Folder类中的文件字段：

```java
@JsonIgnore
private List<File> files = new ArrayList<>();

```

最后，问题解决了，我们也得到了预期的输出：

```javascript
{
    "name": "Root Folder",
    "files": [
        {"id": 1, "name": "File 1"},
        {"id": 2, "name": "File 2"}
    ],
    "details": {
        "id":1,
        "name": "Root Folder",
        "owner": "root",
        "created": 1565203657164,
        "modified": 1565203657164,
        "lastAccess": 1565203657164
    }
}

```

## 六. 总结

在本教程中，我们学习了如何在 Jackson 库中的自定义序列化程序中调用默认序列化程序。
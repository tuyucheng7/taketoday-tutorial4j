## 1. 概述

序列化是将对象转换为字节流的过程。然后可以将该对象保存到数据库或通过网络传输。相反的操作，从一系列字节中提取一个对象，是反序列化。它们的主要目的是保存对象的状态，以便我们可以在需要时重新创建它。

在本教程中，我们将探索Java 对象的不同序列化方法。

首先，我们将讨论用于序列化的Java本机 API。接下来，我们将探索支持 JSON 和 YAML 格式的库来执行相同的操作。最后，我们将看一下一些跨语言协议。

## 2. 示例实体类

让我们首先介绍一个我们将在整个教程中使用的简单实体：

```java
public class User {
    private int id;
    private String name;
    
    //getters and setters
}

```

在接下来的部分中，我们将介绍最广泛使用的序列化协议。通过示例，我们将学习它们每个的基本用法。

## 3. Java的原生序列化

[Java 中的序列化](https://www.baeldung.com/java-serialization)有助于实现多个系统之间的有效和迅速的通信。Java 指定了序列化对象的默认方式。Java 类可以覆盖此默认序列化并定义其自己的序列化对象的方式。

Java原生序列化的优点是：

-   这是一个简单但可扩展的机制
-   它以序列化形式维护对象类型和安全属性
-   可扩展以支持远程对象所需的编组和解组
-   这是原生Java解决方案，因此不需要任何外部库

### 3.1. 默认机制

根据[Java 对象序列化规范](https://docs.oracle.com/en/java/javase/11/docs/specs/serialization/index.html)，我们可以使用ObjectOutputStream类中的writeObject()方法来序列化对象。另一方面，我们可以使用属于ObjectInputStream类的readObject()方法来执行反序列化。

我们将用我们的User类来说明基本过程。

首先，我们的类需要实现[Serializable](https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/io/Serializable.html)接口：

```java
public class User implements Serializable {
    //fields and methods
}
```

接下来，我们需要添加[serialVersionU ID属性](https://www.baeldung.com/java-serial-version-uid)：

```java
private static final long serialVersionUID = 1L;
```

现在，让我们创建一个用户对象：

```java
User user = new User();
user.setId(1);
user.setName("Mark");
```

我们需要提供一个文件路径来保存我们的数据：

```java
String filePath = "src/test/resources/protocols/user.txt";

```

现在，是时候将我们的User对象序列化为一个文件了：

```java
FileOutputStream fileOutputStream = new FileOutputStream(filePath);
ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
objectOutputStream.writeObject(user);
```

在这里，我们使用ObjectOutputStream将User对象的状态保存到“user.txt”文件中。

另一方面，我们可以从同一个文件中读取User对象并反序列化它：

```java
FileInputStream fileInputStream = new FileInputStream(filePath);
ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
User deserializedUser = (User) objectInputStream.readObject();
```

最后，我们可以测试加载对象的状态：

```java
assertEquals(1, deserializedUser.getId());
assertEquals("Mark", deserializedUser.getName());
```

这是序列化Java对象的默认方式。在下一节中，我们将看到执行相同操作的自定义方法。

### 3.2. 使用Externalizable接口自定义序列化

尝试序列化具有某些不可序列化属性的对象时，自定义序列化特别有用。这可以通过实现[Externalizable](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/Externalizable.html) 接口来完成，它有两个方法：

```java
public void writeExternal(ObjectOutput out) throws IOException;

public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException;
```

我们可以在我们想要序列化的类中实现这两个方法。可以在我们关于[Externalizable Interface](https://www.baeldung.com/java-externalizable)的文章中找到详细示例。

### 3.3.Java序列化注意事项

Java 中的本机序列化有一些注意事项：

-   只有标记为Serializable的对象才能被持久化。Object类没有实现Serializable，因此Java中并不是所有的对象都可以自动持久化
-   当一个类实现了Serializable接口时，它的所有子类也都是可序列化的。但是，当一个对象引用了另一个对象时，这些对象必须分别实现 Serializable 接口，否则 会抛出NotSerializableException
-   如果我们想要控制版本控制，我们需要提供serialVersionUID属性。此属性用于验证保存和加载的对象是否兼容。因此，我们需要确保它始终相同，否则将抛出InvalidClassException
-  Java序列化大量使用 I/O 流。我们需要在读取或写入操作后立即关闭流，因为如果我们忘记关闭流，我们将以[资源泄漏](https://en.wikipedia.org/wiki/Resource_leak)告终。为防止此类资源泄漏，我们可以使用[try-with-resources](https://www.baeldung.com/java-try-with-resources)习惯用法 

## 4.Gson库

Google 的[Gson](https://sites.google.com/site/gson/)是一个Java库，用于将Java对象序列化和反序列化为 JSON 表示形式。

[Gson 是托管在GitHub](https://github.com/google/gson)中的开源项目。一般来说，它提供了toJson()和fromJson()方法来将Java对象转换为 JSON，反之亦然。

### 4.1. Maven 依赖

让我们添加对[Gson 库](https://search.maven.org/classic/#search|gav|1|g%3A"com.google.code.gson" AND a%3A"gson")的依赖：

```xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.8.7</version>
</dependency>
```

### 4.2. Gson序列化

首先，让我们创建一个用户对象：

```java
User user = new User();
user.setId(1);
user.setName("Mark");

```

接下来，我们需要提供一个文件路径来保存我们的 JSON 数据：

```java
String filePath = "src/test/resources/protocols/gson_user.json";
```

现在，让我们使用Gson类中的toJson()方法将User对象序列化到“ gson_user.json”文件中：

```java
Writer writer = new FileWriter(filePath);
Gson gson = new GsonBuilder().setPrettyPrinting().create();
gson.toJson(user, writer);

```

### 4.3. Gson反序列化

我们可以使用Gson类中的fromJson()方法来反序列化 JSON 数据。

让我们读取 JSON 文件并将数据反序列化为User对象：

```java
Gson gson = new GsonBuilder().setPrettyPrinting().create();
User deserializedUser = gson.fromJson(new FileReader(filePath), User.class);
```

最后，我们可以测试反序列化的数据：

```java
assertEquals(1, deserializedUser.getId());
assertEquals("Mark", deserializedUser.getName());

```

### 4.4. Gson 特性

Gson 有很多重要的特性，包括：

-   它可以处理集合、泛型类型和嵌套类
-   使用 Gson，我们还可以编写自定义序列化器和/或反序列化器，以便我们可以控制整个过程
-   最重要的是，它允许反序列化无法访问其源代码的类的实例
-   此外，我们可以使用版本控制功能，以防我们的类文件在不同版本中被修改。我们可以在新添加的字段上使用@Since注解，然后我们可以使用GsonBuilder中的setVersion()方法

有关更多示例，请查看我们的[Gson 序列化](https://www.baeldung.com/gson-serialization-guide)和[Gson 反](https://www.baeldung.com/gson-deserialization-guide)序列化食谱。

在本节中，我们使用 Gson API 将数据序列化为 JSON 格式。在下一节中，我们将使用 Jackson API 来做同样的事情。

## 5.杰克逊API

[Jackson](https://github.com/FasterXML/jackson)也被称为“Java JSON 库”或“最好的JavaJSON 解析器”。它提供了多种处理 JSON 数据的方法。

要从总体上了解 Jackson 库，我们的[Jackson 教程](https://www.baeldung.com/jackson) 是一个很好的起点。

### 5.1. Maven 依赖

让我们为 [杰克逊图书馆](https://search.maven.org/classic/#search|ga|1|g%3A"com.fasterxml.jackson.core"):

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-core</artifactId>
    <version>2.12.4</version>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-annotations</artifactId>
    <version>2.12.4</version>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
     <version>2.12.4</version>
</dependency>

```

### 5.2.Java对象到 JSON

我们可以使用 属于ObjectMapper类的writeValue()方法将任何Java对象序列化为 JSON 输出。

让我们从创建一个User对象开始：

```java
User user = new User();
user.setId(1);
user.setName("Mark Jonson");

```

之后，让我们提供一个文件路径来存储我们的 JSON 数据：

```java
String filePath = "src/test/resources/protocols/jackson_user.json";
```

现在，我们可以使用ObjectMapper类将User对象存储到 JSON 文件中：

```java
File file = new File(filePath);
ObjectMapper mapper = new ObjectMapper();
mapper.writeValue(file, user);

```

此代码会将我们的数据写入“jackson_user.json”文件。

### 5.3. JSON 到Java对象

ObjectMapper的简单 readValue()方法 是一个很好的入口点。我们可以使用它将 JSON 内容反序列化为Java对象。

让我们从 JSON 文件中读取用户对象：

```java
User deserializedUser = mapper.readValue(new File(filePath), User.class);
```

我们总是可以测试加载的数据：

```java
assertEquals(1, deserializedUser.getId());
assertEquals("Mark Jonson", deserializedUser.getName());

```

### 5.4. 杰克逊特色

-   Jackson 是一个可靠且成熟的JavaJSON 序列化库
-   ObjectMapper类是序列化过程的入口点，提供了一种直接的方式来解析和生成具有很大灵活性的 JSON对象
-   Jackson 库的最大优势之一是高度可定制的[序列化](https://www.baeldung.com/jackson-custom-serialization)和[反序列](https://www.baeldung.com/jackson-deserialization)化过程

到目前为止，我们看到的是 JSON 格式的数据序列化。在下一节中，我们将探讨使用 YAML 的序列化。

## 6. YAML

[YAML](http://yaml.org/)代表“YAML 不是标记语言”。它是一种人类可读的数据序列化语言。我们可以将 YAML 用于配置文件，以及我们想要存储或传输数据的应用程序。

在上一节中，我们看到了 Jackson API 处理 JSON 文件。我们还可以使用 Jackson API 来处理 YAML 文件。可以在我们关于[使用 Jackson 解析 YAML](https://www.baeldung.com/jackson-yaml)的文章中找到详细示例。

现在，让我们看看其他库。

### 6.1. YAML 豆类

[YAML Beans](https://github.com/EsotericSoftware/yamlbeans)使将Java对象图序列化和反序列化到 YAML 变得容易。

YamlWriter类用于将Java对象序列化为 YAML 。write()方法通过识别公共字段和 bean 的 getter 方法自动处理这个问题。

相反，我们可以使用YamlReader类将 YAML 反序列化为Java对象。read()方法读取 YAML 文档并将其反序列化为所需的对象。

首先，让我们添加对[YAML Beans](https://search.maven.org/classic/#search|ga|1|g%3A"com.esotericsoftware.yamlbeans" AND a%3A"yamlbeans")的依赖：

```xml
<dependency>
    <groupId>com.esotericsoftware.yamlbeans</groupId>
    <artifactId>yamlbeans</artifactId>
    <version>1.15</version>
</dependency>
```

现在。让我们创建一个User对象的映射：

```java
private Map<String, User> populateUserMap() {
    User user1 = new User();
    user1.setId(1);
    user1.setName("Mark Jonson");
    //.. more user objects
    
    Map<String, User> users = new LinkedHashMap<>();
    users.put("User1", user1);
    // add more user objects to map
    
    return users;
}
```

之后，我们需要提供一个文件路径来存储我们的数据：

```java
String filePath = "src/test/resources/protocols/yamlbeans_users.yaml";
```

现在，我们可以使用YamlWriter类将映射序列化为 YAML 文件：

```java
YamlWriter writer = new YamlWriter(new FileWriter(filePath));
writer.write(populateUserMap());
writer.close();

```

另一方面，我们可以使用YamlReader类来反序列化地图：

```java
YamlReader reader = new YamlReader(new FileReader(filePath));
Object object = reader.read();
assertTrue(object instanceof Map); 
```

最后，我们可以测试加载的地图：

```java
Map<String, User> deserializedUsers = (Map<String, User>) object;
assertEquals(4, deserializedUsers.size());
assertEquals("Mark Jonson", (deserializedUsers.get("User1").getName()));
assertEquals(1, (deserializedUsers.get("User1").getId()));
```

### 6.2. 蛇YAML

[SnakeYAML](https://bitbucket.org/asomov/snakeyaml/src/master/)提供了一个高级 API 来将Java对象序列化为 YAML 文档，反之亦然。最新版本 1.2 可以与 JDK 1.8 或更高版本的Java一起使用。它可以解析String、List和Map等Java结构。

SnakeYAML 的入口点是Yaml类，它包含几个有助于序列化和反序列化的方法。

要将 YAML 输入反序列化为Java对象，我们可以使用load()方法加载单个文档，并使用loadAll()方法加载多个文档。这些方法接受InputStream以及String对象。

换个方向，我们可以使用dump()方法将Java对象序列化为 YAML 文档。

可以在我们关于[使用 SnakeYAML 解析 YAML](https://www.baeldung.com/java-snake-yaml)的文章中找到详细示例。

当然，SnakeYAML 可以很好地与JavaMap一起使用，但是，它也可以与自定义Java对象一起使用。

在本节中，我们看到了将数据序列化为 YAML 格式的不同库。在下一节中，我们将讨论跨平台协议。

## 7.阿帕奇节俭

[Apache Thrift](https://thrift.apache.org/)最初由 Facebook 开发，目前由 Apache 维护。

使用 Thrift 的最大好处是它以较低的开销支持跨语言序列化。此外，许多序列化框架只支持一种序列化格式，但是 Apache Thrift 允许我们从多种格式中进行选择。

### 7.1. 节俭的特点

Thrift 提供称为协议的可插入序列化程序。这些协议提供了使用多种序列化格式中的任何一种进行数据交换的灵活性。支持的协议的一些示例包括：

-   TBinaryProtocol使用二进制格式，因此比文本协议处理速度更快
-   TCompactProtocol是一种更紧凑的二进制格式，因此处理起来也更高效
-   TJSONProtocol使用 JSON 来编码数据

Thrift 还支持容器类型的序列化——列表、集合和映射。

### 7.2. Maven 依赖

要在我们的应用程序中使用 Apache Thrift 框架，让我们添加[Thrift 库](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.thrift" AND a%3A"libthrift")：

```xml
<dependency>
    <groupId>org.apache.thrift</groupId>
    <artifactId>libthrift</artifactId>
    <version>0.14.2</version>
</dependency>
```

### 7.3. 节俭数据序列化

Apache Thrift 协议和传输旨在作为分层堆栈协同工作。协议将数据序列化为字节流，传输读取和写入字节。

如前所述，Thrift 提供了许多协议。我们将使用二进制协议说明 thrift 序列化。

首先，我们需要一个User对象：

```java
User user = new User();
user.setId(2);
user.setName("Greg");
```

下一步是创建一个二进制协议：

```java
TMemoryBuffer trans = new TMemoryBuffer(4096);
TProtocol proto = new TBinaryProtocol(trans);
```

现在，让我们序列化我们的数据。我们可以使用写入API来做到这一点：

```java
proto.writeI32(user.getId());
proto.writeString(user.getName());
```

### 7.4. Thrift 数据反序列化

让我们使用读取 API 来反序列化数据：

```java
int userId = proto.readI32();
String userName = proto.readString();
```

最后，我们可以测试加载的数据：

```java
assertEquals(2, userId);
assertEquals("Greg", userName);
```

更多示例可以在我们关于[Apache Thrift](https://www.baeldung.com/apache-thrift)的文章中找到。

## 8. 谷歌协议缓冲区

我们将在本教程中介绍的最后一种方法是[Google Protocol Buffers](https://developers.google.com/protocol-buffers/docs/javatutorial) (protobuf)。它是一种众所周知的二进制数据格式。

### 8.1. 协议缓冲区的好处

Protocol buffers 提供了几个好处，包括：

-   它与语言和平台无关
-   它是一种二进制传输格式，这意味着数据以二进制形式传输。这提高了传输速度，因为它占用更少的空间和带宽
-   支持向后和向前兼容，以便新版本可以读取旧数据，反之亦然

### 8.2. Maven 依赖

让我们从添加[Google protocol buffer 库](https://search.maven.org/classic/#search|gav|1|g%3A"com.google.protobuf" AND a%3A"protobuf-java")的依赖开始：

```xml
<dependency>
    <groupId>com.google.protobuf</groupId>
    <artifactId>protobuf-java</artifactId>
    <version>3.17.3</version>
</dependency>
```

### 8.3. 定义协议

解决了我们的依赖关系后，我们现在可以定义消息格式：

```java
syntax = "proto3";
package protobuf;
option java_package = "com.baeldung.serialization.protocols";
option java_outer_classname = "UserProtos";
message User {
    int32 id = 1;
    string name = 2;
}
```

这是一个User类型的简单消息协议，它有两个字段—— id和name ，分别是integer和string类型。请注意，我们将其保存为“user.proto”文件。

### 8.4. 从 Protobuf 文件生成Java代码

一旦我们有了一个 protobuf 文件，我们就可以使用protoc编译器从中生成代码：

```bash
protoc -I=. --java_out=. user.proto
```

因此，此命令将生成一个UserProtos.java文件。

之后，我们可以创建UserProtos类的实例：

```java
UserProtos.User user = UserProtos.User.newBuilder().setId(1234).setName("John Doe").build();
```

### 8.5. 序列化和反序列化 Protobuf

首先，我们需要提供一个文件路径来存储我们的数据：

```java
String filePath = "src/test/resources/protocols/usersproto";
```

现在，让我们将数据保存到文件中。我们可以使用UserProtos类中的writeTo()方法——一个我们从 protobuf 文件生成的类：

```java
FileOutputStream fos = new FileOutputStream(filePath);
user.writeTo(fos);
```

执行这段代码后，我们的对象将被序列化为二进制格式并保存到“ usersproto ”文件中。

相反，我们可以使用mergeFrom() 方法从文件中加载数据并将其反序列化回User对象：

```java
UserProtos.User deserializedUser = UserProtos.User.newBuilder().mergeFrom(new FileInputStream(filePath)).build();

```

最后，我们可以测试加载的数据：

```java
assertEquals(1234, deserializedUser.getId());
assertEquals("John Doe", deserializedUser.getName());
```

## 9.总结

在本教程中，我们探讨了一些广泛使用的Java对象序列化协议。应用程序数据序列化格式的选择取决于各种因素，例如数据复杂性、人类可读性需求和速度。

Java 支持易于使用的内置序列化。

由于可读性和无模式， JSON更可取。因此，Gson 和 Jackson 都是序列化 JSON 数据的不错选择。它们使用简单并且有据可查。对于编辑数据，YAML非常适合。

另一方面，二进制格式比文本格式更快。当速度对我们的应用程序很重要时，Apache Thrift 和 Google Protocol Buffers 是序列化数据的理想选择。两者都比 XML 或 JSON 格式更紧凑、更快。

总而言之，便利性和性能之间通常需要权衡，序列化也证明没有什么不同。当然，还有许多其他[格式可用于数据序列化](https://en.wikipedia.org/wiki/Comparison_of_data-serialization_formats#Syntax_comparison_of_human-readable_formats)。
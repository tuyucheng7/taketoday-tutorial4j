## 1. 概述

数据序列化是一种将数据转换为二进制或文本格式的技术。有多种系统可用于此目的。[Apache Avro](https://avro.apache.org/)是这些数据序列化系统之一。

Avro 是一个独立于语言、基于模式的数据序列化库。它使用模式来执行序列化和反序列化。此外，Avro 使用 JSON 格式来指定数据结构，这使得它更加强大。

在本教程中，我们将探索更多关于 Avro 设置、执行序列化的JavaAPI 以及 Avro 与其他数据序列化系统的比较。

我们将主要关注作为整个系统基础的模式创建。

## 2.阿帕奇阿夫罗

Avro 是一个独立于语言的序列化库。为此，Avro 使用了一个模式，它是核心组件之一。它将模式存储在一个文件中以供进一步的数据处理。

Avro 最适合大数据处理。它以其更快的处理速度在 Hadoop 和 Kafka 世界中非常受欢迎。

Avro 创建了一个数据文件，它在元数据部分中保存数据和模式。最重要的是，它提供了丰富的数据结构，使其比其他类似解决方案更受欢迎。

要使用 Avro 进行序列化，我们需要按照下面提到的步骤进行。

## 3.问题陈述

让我们从定义一个名为 AvroHttRequest的类开始 ，我们将在示例中使用该类。该类包含原始类型和复杂类型属性：

```java
class AvroHttpRequest {
    
    private long requestTime;
    private ClientIdentifier clientIdentifier;
    private List<String> employeeNames;
    private Active active;
}

```

这里，requestTime是一个原始值。ClientIdentifier是另一个表示复杂类型的类。我们还有employeeName，它也是一个复杂类型。Active是一个枚举，用于描述给定的员工列表是否处于活动状态。

我们的目标是使用 Apache Avro序列化和反序列化 AvroHttRequest类。

## 4. Avro 数据类型

在继续之前，让我们讨论一下 Avro 支持的数据类型。

Avro 支持两种类型的数据：

-   原始类型：Avro 支持所有原始类型。我们使用原始类型名称来定义给定字段的类型。例如，一个包含字符串的值在 Schema 中应该声明为 {“type”: “string”}
-   
-   复杂类型：Avro 支持六种复杂类型：记录、枚举、数组、映射、联合和固定

例如，在我们的问题陈述中， ClientIdentifier是一条记录。

在这种情况下， ClientIdentifier的架构应如下所示：

```json
{
   "type":"record",
   "name":"ClientIdentifier",
   "namespace":"com.baeldung.avro",
   "fields":[
      {
         "name":"hostName",
         "type":"string"
      },
      {
         "name":"ipAddress",
         "type":"string"
      }
   ]
}
```

## 5. 使用 Avro

首先，让我们将需要的 Maven 依赖项添加到我们的pom.xml文件中。

我们应该包括以下依赖项：

-   Apache Avro——核心组件
-   编译器——用于 Avro IDL 和 Avro 特定JavaAPIT 的 Apache Avro 编译器
-   工具——包括 Apache Avro 命令行工具和实用程序
-   用于 Maven 项目的 Apache Avro Maven 插件

我们在本教程中使用 1.8.2 版。

但是，始终建议在[Maven Central](https://search.maven.org/classic/#search|ga|1|a%3A"avro" AND g%3A"org.apache.avro")上查找最新版本：

```xml
<dependency>
    <groupId>org.apache.avro</groupId>
    <artifactId>avro-compiler</artifactId>
    <version>1.8.2</version>
</dependency>
<dependency>
    <groupId>org.apache.avro</groupId>
    <artifactId>avro-maven-plugin</artifactId>
    <version>1.8.2</version>
</dependency>
```

添加 maven 依赖后，接下来的步骤将是：

-   架构创建
-   读取我们程序中的模式
-   使用 Avro 序列化我们的数据
-   最后，反序列化数据

## 6. 模式创建

Avro 使用 JSON 格式描述其 Schema。给定的 Avro Schema 主要有四个属性：

-   Type-描述 Schema 的类型，无论是复杂类型还是原始值
-   命名空间- 描述给定架构所属的命名空间
-   名称- 模式的名称
-   字段 -说明与给定模式关联的字段。字段可以是原始类型，也可以是复杂类型。

创建模式的一种方法是编写 JSON 表示，正如我们在前面部分中看到的那样。

我们还可以使用SchemaBuilder创建模式，这无疑是一种更好、更有效的创建方式。

### 6.1. SchemaBuilder实用程序

类org.apache.avro.SchemaBuilder对于创建 Schema 很有用。

首先，让我们为ClientIdentifier 创建架构：

```java
Schema clientIdentifier = SchemaBuilder.record("ClientIdentifier")
  .namespace("com.baeldung.avro")
  .fields().requiredString("hostName").requiredString("ipAddress")
  .endRecord();
```

现在，让我们用它来创建一个 avroHttpRequest模式：

```java
Schema avroHttpRequest = SchemaBuilder.record("AvroHttpRequest")
  .namespace("com.baeldung.avro")
  .fields().requiredLong("requestTime")
  .name("clientIdentifier")
    .type(clientIdentifier)
    .noDefault()
  .name("employeeNames")
    .type()
    .array()
    .items()
    .stringType()
    .arrayDefault(null)
  .name("active")
    .type()
    .enumeration("Active")
    .symbols("YES","NO")
    .noDefault()
  .endRecord();
```

重要的是要注意，我们已将 clientIdentifier 指定为clientIdentifier 字段的 类型。在这种情况下，用于定义类型的clientIdentifier与我们之前创建的模式相同。

稍后我们可以应用 toString方法来获取Schema的JSON结构。

架构文件使用 .avsc 扩展名保存。让我们将生成的模式保存到 “src/main/resources/avroHttpRequest-schema.avsc”文件中。

## 7.阅读模式

读取模式或多或少就是为给定模式创建 Avro 类。一旦创建了 Avro 类，我们就可以使用它们来序列化和反序列化对象。

有两种创建 Avro 类的方法：

-   以编程方式生成 Avro 类：可以使用SchemaCompiler生成类。我们可以使用几个 API 来生成Java类。我们可以在 GitHub 上找到生成类的代码。
-   使用 Maven 生成类

我们确实有一个 maven 插件可以很好地完成这项工作。我们需要包含插件并运行mvn clean install。

让我们将插件添加到我们的pom.xml文件中：

```xml
<plugin>
    <groupId>org.apache.avro</groupId>
    <artifactId>avro-maven-plugin</artifactId>
    <version>${avro.version}</version>
        <executions>
            <execution>
                <id>schemas</id>
                <phase>generate-sources</phase>
                <goals>
                    <goal>schema</goal>
                    <goal>protocol</goal>
                    <goal>idl-protocol</goal>
                </goals>
                <configuration>
                    <sourceDirectory>${project.basedir}/src/main/resources/</sourceDirectory>
                    <outputDirectory>${project.basedir}/src/main/java/</outputDirectory>
                </configuration>
            </execution>
        </executions>
</plugin>

```

## 8. 使用 Avro 进行序列化和反序列化

当我们完成生成架构后，让我们继续探索序列化部分。

Avro 支持两种数据序列化格式：JSON 格式和二进制格式。

首先，我们将重点关注 JSON 格式，然后我们将讨论二进制格式。

在继续之前，我们应该通过几个关键接口。我们可以使用下面的接口和类进行序列化：

DatumWriter<T>：我们应该使用它在给定的 Schema 上写入数据。我们将在我们的示例中使用 SpecificDatumWriter实现，但是，DatumWriter也有其他实现。其他实现有 GenericDatumWriter、Json.Writer、ProtobufDatumWriter、ReflectDatumWriter、ThriftDatumWriter。

编码器：使用编码器或定义格式，如前所述。EncoderFactory提供了两种类型的编码器，二进制编码器和 JSON 编码器。

DatumReader<D>：用于反序列化的单一接口。同样，它有多个实现，但我们将在示例中使用SpecificDatumReader。其他实现是GenericDatumReader、Json.ObjectReader、Json.Reader、ProtobufDatumReader、ReflectDatumReader、ThriftDatumReader。

解码器：在反序列化数据时使用解码器。Decoderfactory提供了两种类型的解码器：二进制解码器和 JSON 解码器。

接下来，让我们看看序列化和反序列化在 Avro 中是如何发生的。

### 8.1. 连载

我们将以AvroHttpRequest类为例，并尝试使用 Avro 对其进行序列化。

首先，让我们将其序列化为 JSON 格式：

```java
public byte[] serealizeAvroHttpRequestJSON(
  AvroHttpRequest request) {
 
    DatumWriter<AvroHttpRequest> writer = new SpecificDatumWriter<>(
      AvroHttpRequest.class);
    byte[] data = new byte[0];
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    Encoder jsonEncoder = null;
    try {
        jsonEncoder = EncoderFactory.get().jsonEncoder(
          AvroHttpRequest.getClassSchema(), stream);
        writer.write(request, jsonEncoder);
        jsonEncoder.flush();
        data = stream.toByteArray();
    } catch (IOException e) {
        logger.error("Serialization error:" + e.getMessage());
    }
    return data;
}

```

让我们看一下此方法的测试用例：

```java
@Test
public void whenSerialized_UsingJSONEncoder_ObjectGetsSerialized(){
    byte[] data = serealizer.serealizeAvroHttpRequestJSON(request);
    assertTrue(Objects.nonNull(data));
    assertTrue(data.length > 0);
}
```

在这里，我们使用了 jsonEncoder 方法并将模式传递给它。

如果我们想使用二进制编码器，我们需要用 binaryEncoder() 替换 jsonEncoder() 方法：

```java
Encoder jsonEncoder = EncoderFactory.get().binaryEncoder(stream,null);
```

### 8.2. 反序列化

为此，我们将使用上述DatumReader和Decoder接口。

正如我们使用EncoderFactory获取 编码器一样，我们将类似地使用DecoderFactory获取 解码器对象。

让我们使用 JSON 格式反序列化数据：

```java
public AvroHttpRequest deSerealizeAvroHttpRequestJSON(byte[] data) {
    DatumReader<AvroHttpRequest> reader
     = new SpecificDatumReader<>(AvroHttpRequest.class);
    Decoder decoder = null;
    try {
        decoder = DecoderFactory.get().jsonDecoder(
          AvroHttpRequest.getClassSchema(), new String(data));
        return reader.read(null, decoder);
    } catch (IOException e) {
        logger.error("Deserialization error:" + e.getMessage());
    }
}

```

让我们看看测试用例：

```java
@Test
public void whenDeserializeUsingJSONDecoder_thenActualAndExpectedObjectsAreEqual(){
    byte[] data = serealizer.serealizeAvroHttpRequestJSON(request);
    AvroHttpRequest actualRequest = deSerealizer
      .deSerealizeAvroHttpRequestJSON(data);
    assertEquals(actualRequest,request);
    assertTrue(actualRequest.getRequestTime()
      .equals(request.getRequestTime()));
}
```

同样，我们可以使用二进制解码器：

```java
Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);
```

## 9.总结

Apache Avro 在处理大数据时特别有用。它提供二进制和 JSON 格式的数据序列化，可以根据用例使用。

Avro 序列化过程更快，而且节省空间。Avro 不保留每个字段的字段类型信息；相反，它在模式中创建元数据。

最后但并非最不重要的一点是，Avro 与各种编程语言有着很好的结合，这赋予了它优势。
## 1. 概述

在本快速教程中，我们将了解使用[Jackson](https://github.com/FasterXML/jackson)对Java映射进行序列化和反序列化。

我们将说明如何将Map<String, String>、Map<Object, String>和Map<Object, Object>与 JSON 格式的字符串序列化和反序列化。

## 延伸阅读：

## [Jackson – 使用地图和空值](https://www.baeldung.com/jackson-map-null-values-or-null-key)

如何使用 Jackson 序列化具有空键或空值的地图。

[阅读更多](https://www.baeldung.com/jackson-map-null-values-or-null-key)→

## [如何使用 Jackson 序列化和反序列化枚举](https://www.baeldung.com/jackson-serialize-enums)

如何使用 Jackson 2 将枚举序列化和反序列化为 JSON 对象。

[阅读更多](https://www.baeldung.com/jackson-serialize-enums)→

## [使用 Jackson 进行 XML 序列化和反序列化](https://www.baeldung.com/jackson-xml-serialization-and-deserialization)

这个简短的教程展示了如何使用 Jackson 库将Java对象序列化为 XML 并将它们反序列化回对象。

[阅读更多](https://www.baeldung.com/jackson-xml-serialization-and-deserialization)→

## 2.Maven配置

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.13.3</version>
</dependency>
```

我们可以在[这里](https://search.maven.org/classic/#search|gav|1|g%3A"com.fasterxml.jackson.core" AND a%3A"jackson-databind")获得最新版本的 Jackson 。

## 3.连载

序列化将Java对象转换为字节流，可以根据需要持久化或共享。Java映射是将键对象映射到值对象的集合，并且通常是要序列化的最不直观的对象。

### 3.1. Map<String, String>序列化

对于一个简单的案例，让我们创建一个Map<String, String>并将其序列化为 JSON：

```java
Map<String, String> map = new HashMap<>();
map.put("key", "value");

ObjectMapper mapper = new ObjectMapper();
String jsonResult = mapper.writerWithDefaultPrettyPrinter()
  .writeValueAsString(map);
```

ObjectMapper是 Jackson 的序列化映射器。它允许我们序列化我们的地图，并使用String 中的toString()方法将其写成漂亮的 JSON字符串：

```plaintext
{
  "key" : "value"
}
```

### 3.2. Map<Object, String>序列化

通过一些额外的步骤，我们还可以序列化包含自定义Java类的地图。让我们创建一个MyPair类来表示一对相关的String对象。

注意：getters/setters 应该是公开的，我们用@JsonValue注解toString()以确保 Jackson 在序列化时使用这个自定义的toString()：

```java
public class MyPair {

    private String first;
    private String second;
    
    @Override
    @JsonValue
    public String toString() {
        return first + " and " + second;
    }
 
    // standard getter, setters, equals, hashCode, constructors
}
```

然后我们将告诉 Jackson 如何通过扩展 Jackson 的JsonSerializer来序列化MyPair：

```java
public class MyPairSerializer extends JsonSerializer<MyPair> {

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void serialize(MyPair value, 
      JsonGenerator gen,
      SerializerProvider serializers) 
      throws IOException, JsonProcessingException {
 
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, value);
        gen.writeFieldName(writer.toString());
    }
}
```

JsonSerializer，顾名思义，使用MyPair的toString()方法将 MyPair 序列化为JSON。此外，Jackson 提供了许多[Serializer 类](https://github.com/FasterXML/jackson-databind/blob/master/docs/javadoc/2.3/com/fasterxml/jackson/databind/ser/std/package-summary.html)来满足我们的序列化要求。

接下来，我们使用 @JsonSerialize 注解将MyPairSerializer应用于我们的Map<MyPair, String>。请注意，我们只告诉 Jackson 如何序列化MyPair，因为它已经知道如何序列化String：

```java
@JsonSerialize(keyUsing = MyPairSerializer.class) 
Map<MyPair, String> map;
```

然后让我们测试我们的地图序列化：

```java
map = new HashMap<>();
MyPair key = new MyPair("Abbott", "Costello");
map.put(key, "Comedy");

String jsonResult = mapper.writerWithDefaultPrettyPrinter()
  .writeValueAsString(map);
```

序列化的 JSON 输出是：

```plaintext
{
  "Abbott and Costello" : "Comedy"
}
```

### 3.3. Map<Object, Object>序列化

最复杂的情况是序列化Map<Object, Object>，但大部分工作已经完成。让我们将 Jackson 的MapSerializer用于我们的地图，并将上一节中的MyPairSerializer用于地图的键和值类型：

```java
@JsonSerialize(keyUsing = MapSerializer.class)
Map<MyPair, MyPair> map;
	
@JsonSerialize(keyUsing = MyPairSerializer.class)
MyPair mapKey;

@JsonSerialize(keyUsing = MyPairSerializer.class)
MyPair mapValue;
```

然后让我们测试序列化我们的Map<MyPair, MyPair>：

```java
mapKey = new MyPair("Abbott", "Costello");
mapValue = new MyPair("Comedy", "1940s");
map.put(mapKey, mapValue);

String jsonResult = mapper.writerWithDefaultPrettyPrinter()
  .writeValueAsString(map);
```

使用MyPair的toString()方法的序列化 JSON 输出是：

```plaintext
{
  "Abbott and Costello" : "Comedy and 1940s"
}
```

## 4.反序列化

反序列化将字节流转换为我们可以在代码中使用的Java对象。在本节中，我们会将 JSON 输入反序列化为具有不同签名的Map 。

### 4.1. Map<String, String>反序列化

对于一个简单的案例，让我们采用 JSON 格式的输入字符串并将其转换为Map<String, String>Java集合：

```java
String jsonInput = "{"key": "value"}";
TypeReference<HashMap<String, String>> typeRef 
  = new TypeReference<HashMap<String, String>>() {};
Map<String, String> map = mapper.readValue(jsonInput, typeRef);
```

我们使用 Jackson 的ObjectMapper，就像我们对序列化所做的那样，使用readValue()来处理输入。另外，请注意我们对 Jackson 的TypeReference的使用，我们将在所有反序列化示例中使用它来描述目标Map的类型。这是我们地图的toString()表示：

```plaintext
{key=value}
```

### 4.2. Map<Object, String>反序列化

现在让我们将输入 JSON 和目的地的TypeReference更改为Map<MyPair, String>：

```java
String jsonInput = "{"Abbott and Costello" : "Comedy"}";

TypeReference<HashMap<MyPair, String>> typeRef 
  = new TypeReference<HashMap<MyPair, String>>() {};
Map<MyPair,String> map = mapper.readValue(jsonInput, typeRef);
```

我们需要为MyPair创建一个构造函数，它接受一个包含两个元素的字符串并将它们解析为MyPair元素：

```java
public MyPair(String both) {
    String[] pairs = both.split("and");
    this.first = pairs[0].trim();
    this.second = pairs[1].trim();
}
```

我们的Map<MyPair,String>对象的toString()是：

```plaintext
{Abbott and Costello=Comedy}
```

当我们反序列化为包含Map 的Java类时，还有另一种选择；我们可以使用 Jackson 的KeyDeserializer类，这是 Jackson 提供的众多[反序列化类之一。](https://github.com/FasterXML/jackson-databind/blob/master/docs/javadoc/2.3/com/fasterxml/jackson/databind/deser/package-summary.html)让我们用@JsonCreator、@JsonProperty和@JsonDeserialize 注解我们的ClassWithAMap ：

```java
public class ClassWithAMap {

  @JsonProperty("map")
  @JsonDeserialize(keyUsing = MyPairDeserializer.class)
  private Map<MyPair, String> map;

  @JsonCreator
  public ClassWithAMap(Map<MyPair, String> map) {
    this.map = map;
  }
 
  // public getters/setters omitted
}
```

在这里，我们告诉 Jackson 反序列化ClassWithAMap中包含的Map<MyPair, String> ，因此我们需要扩展KeyDeserializer来描述如何从输入String反序列化地图的键( MyPair对象) ：

```java
public class MyPairDeserializer extends KeyDeserializer {

  @Override
  public MyPair deserializeKey(
    String key, 
    DeserializationContext ctxt) throws IOException, 
    JsonProcessingException {
      
      return new MyPair(key);
    }
}
```

然后我们可以使用readValue测试反序列化：

```java
String jsonInput = "{"Abbott and Costello":"Comedy"}";

ClassWithAMap classWithMap = mapper.readValue(jsonInput,
  ClassWithAMap.class);
```

同样，我们ClassWithAMap映射的toString()方法为我们提供了我们期望的输出：

```plaintext
{Abbott and Costello=Comedy}
```

### 4.3. Map<Object,Object>反序列化

最后，让我们将输入 JSON 和目的地的TypeReference更改为Map<MyPair, MyPair>：

```java
String jsonInput = "{"Abbott and Costello" : "Comedy and 1940s"}";
TypeReference<HashMap<MyPair, MyPair>> typeRef 
  = new TypeReference<HashMap<MyPair, MyPair>>() {};
Map<MyPair,MyPair> map = mapper.readValue(jsonInput, typeRef);
```

我们的Map<MyPair, MyPair>对象的toString()是：

```plaintext
{Abbott and Costello=Comedy and 1940s}
```

## 5.总结

在这篇简短的文章中，我们学习了如何将 Java映射与 JSON 格式的字符串序列化和反序列化。
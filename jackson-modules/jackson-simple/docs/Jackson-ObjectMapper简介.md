## 1. 概述

本教程重点了解 Jackson ObjectMapper类以及如何将Java对象序列化为 JSON 以及如何将 JSON 字符串反序列化为Java对象。

要从总体上了解更多关于 Jackson 库的信息，[Jackson 教程](https://www.baeldung.com/jackson)是一个很好的起点。

## 延伸阅读：

## [与杰克逊的继承](https://www.baeldung.com/jackson-inheritance)

本教程将演示如何使用 Jackson 处理包含子类型元数据和忽略从超类继承的属性。

[阅读更多](https://www.baeldung.com/jackson-inheritance)→

## [杰克逊 JSON 视图](https://www.baeldung.com/jackson-json-view-annotation)

如何在 Jackson 中使用 @JsonView 注解来完美控制对象的序列化(不使用和使用 Spring)。

[阅读更多](https://www.baeldung.com/jackson-json-view-annotation)→

## [Jackson——自定义序列化器](https://www.baeldung.com/jackson-custom-serialization)

通过使用自定义序列化器控制 Jackson 2 的 JSON 输出。

[阅读更多](https://www.baeldung.com/jackson-custom-serialization)→

## 2.依赖关系

让我们首先将以下依赖项添加到pom.xml：

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.13.3</version>
</dependency>

```

此依赖项还将传递性地将以下库添加到类路径中：

1.  杰克逊注解
2.  杰克逊核心

[始终为jackson-databind](https://search.maven.org/classic/#search|gav|1|g%3A"com.fasterxml.jackson.core" AND a%3A"jackson-databind")使用 Maven 中央存储库中的最新版本。

## 3.使用ObjectMapper读写

让我们从基本的读写操作开始。

ObjectMapper的简单readValue API是一个很好的切入点。我们可以使用它来将 JSON 内容解析或反序列化为Java对象。

此外，在写入方面，我们可以使用writeValue API 将任何Java对象序列化为 JSON 输出。

在本文中，我们将使用以下带有两个字段的Car类作为序列化或反序列化的对象：

```java
public class Car {

    private String color;
    private String type;

    // standard getters setters
}
```

### 3.1.Java对象到 JSON

让我们看一下使用ObjectMapper类的writeValue方法将Java 对象序列化为 JSON 的第一个示例：

```java
ObjectMapper objectMapper = new ObjectMapper();
Car car = new Car("yellow", "renault");
objectMapper.writeValue(new File("target/car.json"), car);

```

文件中上述内容的输出将是：

```javascript
{"color":"yellow","type":"renault"}

```

ObjectMapper类的writeValueAsString和writeValueAsBytes方法从Java对象生成 JSON，并将生成的 JSON 作为字符串或字节数组返回：

```java
String carAsString = objectMapper.writeValueAsString(car);

```

### 3.2. JSON 到Java对象

下面是一个使用ObjectMapper类将 JSON 字符串转换为Java对象的简单示例：

```java
String json = "{ "color" : "Black", "type" : "BMW" }";
Car car = objectMapper.readValue(json, Car.class);	

```

readValue()函数还接受其他形式的输入，例如包含 JSON 字符串的文件：

```java
Car car = objectMapper.readValue(new File("src/test/resources/json_car.json"), Car.class);
```

或网址：

```java
Car car = 
  objectMapper.readValue(new URL("file:src/test/resources/json_car.json"), Car.class);
```

### 3.3. JSON 到 Jackson JsonNode

或者，可以将 JSON 解析为JsonNode对象并用于从特定节点检索数据：

```java
String json = "{ "color" : "Black", "type" : "FIAT" }";
JsonNode jsonNode = objectMapper.readTree(json);
String color = jsonNode.get("color").asText();
// Output: color -> Black

```

### 3.4. 从 JSON 数组字符串创建Java列表

我们可以使用TypeReference将数组形式的 JSON 解析为Java对象列表：

```java
String jsonCarArray = 
  "[{ "color" : "Black", "type" : "BMW" }, { "color" : "Red", "type" : "FIAT" }]";
List<Car> listCar = objectMapper.readValue(jsonCarArray, new TypeReference<List<Car>>(){});

```

### 3.5. 从 JSON 字符串创建Java地图

同样，我们可以将 JSON 解析为JavaMap：

```java
String json = "{ "color" : "Black", "type" : "BMW" }";
Map<String, Object> map 
  = objectMapper.readValue(json, new TypeReference<Map<String,Object>>(){});

```

## 4.高级功能

Jackson 库的最大优势之一是高度可定制的序列化和反序列化过程。

在本节中，我们将介绍一些高级功能，其中输入或输出 JSON 响应可能不同于生成或使用响应的对象。

### 4.1. 配置序列化或反序列化功能

在将 JSON 对象转换为Java类时，如果 JSON 字符串有一些新的字段，默认过程会导致异常：

```java
String jsonString 
  = "{ "color" : "Black", "type" : "Fiat", "year" : "1970" }";

```

上面例子中的JSON字符串在默认解析到Class Car的Java对象的过程中会导致UnrecognizedPropertyException异常。

通过configure方法，我们可以扩展默认流程以忽略新字段：

```java
objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
Car car = objectMapper.readValue(jsonString, Car.class);

JsonNode jsonNodeRoot = objectMapper.readTree(jsonString);
JsonNode jsonNodeYear = jsonNodeRoot.get("year");
String year = jsonNodeYear.asText();

```

另一个选项基于FAIL_ON_NULL_FOR_PRIMITIVES，它定义是否允许原始值的空值：

```java
objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);

```

同样，FAIL_ON_NUMBERS_FOR_ENUM控制是否允许将枚举值序列化/反序列化为数字：

```java
objectMapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false);
```

你可以在[官方网站](https://github.com/FasterXML/jackson-databind/wiki/Serialization-Features)上找到完整的序列化和反序列化功能列表。

### 4.2. 创建自定义序列化器或反序列化器

ObjectMapper类的另一个重要特性是能够注册自定义[序列化](https://www.baeldung.com/jackson-custom-serialization)器和[反](https://www.baeldung.com/jackson-deserialization)序列化器。

自定义序列化器和反序列化器在输入或输出 JSON 响应的结构与必须序列化或反序列化到的Java类不同的情况下非常有用。

下面是自定义 JSON 序列化程序的示例：

```java
public class CustomCarSerializer extends StdSerializer<Car> {
    
    public CustomCarSerializer() {
        this(null);
    }

    public CustomCarSerializer(Class<Car> t) {
        super(t);
    }

    @Override
    public void serialize(
      Car car, JsonGenerator jsonGenerator, SerializerProvider serializer) {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("car_brand", car.getType());
        jsonGenerator.writeEndObject();
    }
}

```

可以像这样调用此自定义序列化程序：

```java
ObjectMapper mapper = new ObjectMapper();
SimpleModule module = 
  new SimpleModule("CustomCarSerializer", new Version(1, 0, 0, null, null, null));
module.addSerializer(Car.class, new CustomCarSerializer());
mapper.registerModule(module);
Car car = new Car("yellow", "renault");
String carJson = mapper.writeValueAsString(car);

```

这是Car在客户端的样子(作为 JSON 输出)：

```javascript
var carJson = {"car_brand":"renault"}

```

这是自定义 JSON 反序列化器的示例：

```java
public class CustomCarDeserializer extends StdDeserializer<Car> {
    
    public CustomCarDeserializer() {
        this(null);
    }

    public CustomCarDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Car deserialize(JsonParser parser, DeserializationContext deserializer) {
        Car car = new Car();
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);
        
        // try catch block
        JsonNode colorNode = node.get("color");
        String color = colorNode.asText();
        car.setColor(color);
        return car;
    }
}

```

可以通过以下方式调用此自定义反序列化器：

```java
String json = "{ "color" : "Black", "type" : "BMW" }";
ObjectMapper mapper = new ObjectMapper();
SimpleModule module =
  new SimpleModule("CustomCarDeserializer", new Version(1, 0, 0, null, null, null));
module.addDeserializer(Car.class, new CustomCarDeserializer());
mapper.registerModule(module);
Car car = mapper.readValue(json, Car.class);

```

### 4.3. 处理日期格式

java.util.Date的默认序列化会产生一个数字，即纪元时间戳(自 1970 年 1 月 1 日以来的毫秒数，UTC)。但这不是人类可读的，需要进一步转换才能以人类可读的格式显示。

让我们用datePurchased属性包装我们到目前为止在Request类中使用的Car实例：

```java
public class Request 
{
    private Car car;
    private Date datePurchased;

    // standard getters setters
}

```

要控制日期的字符串格式并将其设置为例如yyyy-MM-dd HH:mm az，请考虑以下代码段：

```java
ObjectMapper objectMapper = new ObjectMapper();
DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm a z");
objectMapper.setDateFormat(df);
String carAsString = objectMapper.writeValueAsString(request);
// output: {"car":{"color":"yellow","type":"renault"},"datePurchased":"2016-07-03 11:43 AM CEST"}

```

要了解有关使用 Jackson 序列化日期的更多信息，请阅读[我们更深入的](https://www.baeldung.com/jackson-serialize-dates)文章。

### 4.4. 处理集合

DeserializationFeature类提供的另一个小但有用的功能是能够从 JSON 数组响应生成我们想要的集合类型。

例如，我们可以将结果生成为数组：

```java
String jsonCarArray = 
  "[{ "color" : "Black", "type" : "BMW" }, { "color" : "Red", "type" : "FIAT" }]";
ObjectMapper objectMapper = new ObjectMapper();
objectMapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
Car[] cars = objectMapper.readValue(jsonCarArray, Car[].class);
// print cars
```

或者作为一个列表：

```java
String jsonCarArray = 
  "[{ "color" : "Black", "type" : "BMW" }, { "color" : "Red", "type" : "FIAT" }]";
ObjectMapper objectMapper = new ObjectMapper();
List<Car> listCar = objectMapper.readValue(jsonCarArray, new TypeReference<List<Car>>(){});
// print cars
```

有关使用 Jackson 处理集合的更多信息，请参见[此处](https://www.baeldung.com/jackson-collection-array)。

## 5.总结

Jackson 是一个可靠且成熟的JavaJSON 序列化/反序列化库。ObjectMapper API 提供了一种直接的方式来解析和生成具有很大灵活性的 JSON 响应对象。本文讨论了使该库如此受欢迎的主要特性。
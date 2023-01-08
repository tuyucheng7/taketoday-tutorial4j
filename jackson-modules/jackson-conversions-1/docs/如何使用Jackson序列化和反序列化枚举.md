## 1. 概述

在本快速教程中，我们将学习如何使用 Jackson 2 控制Java枚举序列化和反序列化的方式。

要更深入地挖掘并了解我们可以使用 Jackson 2 做的其他很酷的事情，请转到[主要的 Jackson 教程](https://www.baeldung.com/jackson)。

## 2. 控制枚举表示

让我们定义以下枚举：

```java
public enum Distance {
    KILOMETER("km", 1000), 
    MILE("miles", 1609.34),
    METER("meters", 1), 
    INCH("inches", 0.0254),
    CENTIMETER("cm", 0.01), 
    MILLIMETER("mm", 0.001);

    private String unit;
    private final double meters;

    private Distance(String unit, double meters) {
        this.unit = unit;
        this.meters = meters;
    }

    // standard getters and setters
}
```

## 3. 将枚举序列化为 JSON

### 3.1. 默认枚举表示

默认情况下，Jackson 会将Java枚举表示为一个简单的字符串。例如：

```java
new ObjectMapper().writeValueAsString(Distance.MILE);
```

将导致：

```plaintext
"MILE"
```

然而，当将此Enum 编组为 JSON 对象时， 我们希望得到如下内容：

```javascript
{"unit":"miles","meters":1609.34}

```

### 3.2. 枚举作为 JSON 对象

从 Jackson 2.1.2 开始，现在有一个配置选项可以处理这种表示。这可以通过类级别的@JsonFormat注解来完成：

```java
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Distance { ... }
```

当为Distance序列化此枚举时，这将导致所需的结果。英里：

```javascript
{"unit":"miles","meters":1609.34}
```

### 3.3. 枚举和@JsonValue

控制枚举的封送处理输出的另一种简单方法是在 getter 上使用@JsonValue注解：

```java
public enum Distance { 
    ...
 
    @JsonValue
    public String getMeters() {
        return meters;
    }
}
```

我们在这里表达的是getMeters()是这个枚举的实际表示。所以序列化的结果将是：

```javascript
1609.34
```

### 3.4. 枚举的自定义序列化程序

如果我们使用的 Jackson 版本早于 2.1.2，或者枚举需要更多自定义，我们可以使用自定义 Jackson 序列化程序。首先，我们需要定义它：

```java
public class DistanceSerializer extends StdSerializer {
    
    public DistanceSerializer() {
        super(Distance.class);
    }

    public DistanceSerializer(Class t) {
        super(t);
    }

    public void serialize(
      Distance distance, JsonGenerator generator, SerializerProvider provider) 
      throws IOException, JsonProcessingException {
        generator.writeStartObject();
        generator.writeFieldName("name");
        generator.writeString(distance.name());
        generator.writeFieldName("unit");
        generator.writeString(distance.getUnit());
        generator.writeFieldName("meters");
        generator.writeNumber(distance.getMeters());
        generator.writeEndObject();
    }
}
```

然后我们可以将序列化程序应用于将被序列化的类：

```java
@JsonSerialize(using = DistanceSerializer.class)
public enum TypeEnum { ... }
```

这导致：

```javascript
{"name":"MILE","unit":"miles","meters":1609.34}
```

## 4. JSON 反序列化为 Enum

首先，让我们定义一个具有Distance成员的City类：

```java
public class City {
    
    private Distance distance;
    ...    
}
```

然后我们将讨论将 JSON 字符串反序列化为枚举的不同方法。

### 4.1. 默认行为

默认情况下，Jackson 将使用 Enum 名称从 JSON 反序列化。

例如，它将反序列化 JSON：

```javascript
{"distance":"KILOMETER"}
```

对于Distance.KILOMETER对象：

```java
City city = new ObjectMapper().readValue(json, City.class);
assertEquals(Distance.KILOMETER, city.getDistance());
```

如果我们希望 Jackson 通过 Enum 名称不区分大小写地反序列化 JSON，我们需要[自定义ObjectMapper](https://www.baeldung.com/spring-boot-customize-jackson-objectmapper)以启用ACCEPT_CASE_INSENSITIVE_ENUMS功能。

假设我们有另一个 JSON：

```json
{"distance":"KiLoMeTeR"}
```

现在，让我们进行不区分大小写的反序列化：

```java
ObjectMapper objectMapper = JsonMapper.builder()
  .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
  .build();
City city = objectMapper.readValue(json, City.class);
                                                     
assertEquals(Distance.KILOMETER, city.getDistance());
```

如上面的测试所示，我们使用JsonMapper构建器启用ACCEPT_CASE_INSENSITIVE_ENUMS功能。

### 4.2. 使用@JsonValue

我们已经学习了如何使用@JsonValue来序列化枚举。我们也可以使用相同的注解进行反序列化。这是可能的，因为枚举值是常量。

首先，让我们将@JsonValue与其中一种 getter 方法getMeters() 结合使用：

```java
public enum Distance {
    ...

    @JsonValue
    public double getMeters() {
        return meters;
    }
}
```

getMeters()方法的返回值表示 Enum 对象。因此，反序列化示例 JSON 时：

```javascript
{"distance":"0.0254"}
```

Jackson 将查找具有getMeters()返回值 0.0254 的 Enum 对象。在这种情况下，对象是距离。英寸：

```java
assertEquals(Distance.INCH, city.getDistance());

```

### 4.3. 使用@JsonProperty

@JsonProperty注解用于枚举实例：

```java
public enum Distance {
    @JsonProperty("distance-in-km")
    KILOMETER("km", 1000), 
    @JsonProperty("distance-in-miles")
    MILE("miles", 1609.34);
 
    ...
}
```

通过使用这个注解，我们只是告诉 Jackson 将@JsonProperty的值映射到用这个 value 注解的对象。

作为上述声明的结果，示例 JSON 字符串：

```javascript
{"distance": "distance-in-km"}
```

将映射到Distance.KILOMETER对象：

```java
assertEquals(Distance.KILOMETER, city.getDistance());
```

### 4.4. 使用@JsonCreator

Jackson 调用用@JsonCreator注解的方法来获取封闭类的实例。

考虑 JSON 表示：

```javascript
{
    "distance": {
        "unit":"miles", 
        "meters":1609.34
    }
}
```

然后我们将使用@JsonCreator注解定义forValues()工厂方法：

```java
public enum Distance {
   
    @JsonCreator
    public static Distance forValues(@JsonProperty("unit") String unit,
      @JsonProperty("meters") double meters) {
        for (Distance distance : Distance.values()) {
            if (
              distance.unit.equals(unit) && Double.compare(distance.meters, meters) == 0) {
                return distance;
            }
        }

        return null;
    }

    ...
}
```

请注意使用@JsonProperty注解将 JSON 字段与方法参数绑定。

然后，当我们反序列化 JSON 样本时，我们将得到结果：

```java
assertEquals(Distance.MILE, city.getDistance());
```

### 4.5. 使用自定义反序列化器

如果所描述的技术都不可用，我们可以使用自定义反序列化器。例如，我们可能无法访问 Enum 源代码，或者我们可能 正在使用不支持到目前为止所涵盖的一个或多个注解的旧 Jackson 版本。

根据[我们的自定义反序列化文章](https://www.baeldung.com/jackson-deserialization)，为了反序列化上一节中提供的 JSON，我们将从创建反序列化类开始：

```java
public class CustomEnumDeserializer extends StdDeserializer<Distance> {

    @Override
    public Distance deserialize(JsonParser jsonParser, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        String unit = node.get("unit").asText();
        double meters = node.get("meters").asDouble();

        for (Distance distance : Distance.values()) {
           
            if (distance.getUnit().equals(unit) && Double.compare(
              distance.getMeters(), meters) == 0) {
                return distance;
            }
        }

        return null;
    }
}

```

然后我们将在 Enum 上使用@JsonDeserialize注解来指定我们的自定义反序列化器：

```java
@JsonDeserialize(using = CustomEnumDeserializer.class)
public enum Distance {
   ...
}
```

而我们的结果是：

```java
assertEquals(Distance.MILE, city.getDistance());
```

## 5.总结

本文说明了如何更好地控制Java 枚举的序列化和反序列化过程以及格式。
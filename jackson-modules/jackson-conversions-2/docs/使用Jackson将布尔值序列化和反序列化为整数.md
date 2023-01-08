## 1. 简介

在处理 JSON 方面，[Jackson 库](https://www.baeldung.com/jackson-object-mapper-tutorial)是Java世界中事实上的标准。尽管 Jackson 有明确定义的默认值，但为了将Boolean值映射到Integer，我们仍然需要进行手动配置。

当然，一些开发人员想知道如何以最好的方式和最少的努力实现这一目标。

在本文中，我们将解释如何在 Jackson 中将Boolean值序列化为Integer s — 加上数字字符串 — 反之亦然。

## 2.连载

最初，我们将研究序列化部分。为了测试Boolean到Integer的序列化，让我们定义我们的模型Game：

```java
public class Game {

    private Long id;
    private String name;
    private Boolean paused;
    private Boolean over;

    // constructors, getters and setters
}
```

像往常一样，Game对象的默认序列化将使用 Jackson 的ObjectMapper：

```java
ObjectMapper mapper = new ObjectMapper();
Game game = new Game(1L, "My Game");
game.setPaused(true);
game.setOver(false);
String json = mapper.writeValueAsString(game);
```

毫不奇怪，布尔字段的输出将是默认值—— true或false：

```java
{"id":1, "name":"My Game", "paused":true, "over":false}
```

然而，我们的目标是最终从我们的Game对象中获得以下 JSON 输出：

```java
{"id":1, "name":"My Game", "paused":1, "over":0}
```

### 2.1. 字段级配置

序列化为Integer的一种非常直接的方法是使用@JsonFormat注解我们的布尔字段并为其设置Shape.NUMBER：

```java
@JsonFormat(shape = Shape.NUMBER)
private Boolean paused;

@JsonFormat(shape = Shape.NUMBER)
private Boolean over;
```

然后，让我们在测试方法中尝试我们的序列化：

```java
ObjectMapper mapper = new ObjectMapper();
Game game = new Game(1L, "My Game");
game.setPaused(true);
game.setOver(false);
String json = mapper.writeValueAsString(game);

assertThat(json)
  .isEqualTo("{"id":1,"name":"My Game","paused":1,"over":0}");
```

正如我们在 JSON 输出中注意到的那样，我们的布尔字段——暂停和结束——形成了数字1和0。我们可以看到这些值是整数格式，因为它们没有被引号括起来。

### 2.2. 全局配置

有时，注解每个字段是不切实际的。例如，根据要求，我们可能需要全局配置我们的布尔值到整数序列化。

幸运的是，Jackson 允许我们通过覆盖ObjectMapper中的默认值来全局配置@JsonFormat：

```java
ObjectMapper mapper = new ObjectMapper();
mapper.configOverride(Boolean.class)
  .setFormat(JsonFormat.Value.forShape(Shape.NUMBER));

Game game = new Game(1L, "My Game");
game.setPaused(true);
game.setOver(false);
String json = mapper.writeValueAsString(game);

assertThat(json)
  .isEqualTo("{"id":1,"name":"My Game","paused":1,"over":0}");
```

## 3.反序列化

同样，我们可能还希望在将 JSON 字符串反序列化到我们的模型中时从数字中获取布尔值。

幸运的是，默认情况下，Jackson 可以将数字(仅1和0)解析为布尔值。因此，我们不需要使用@JsonFormat注解或任何其他配置。

因此，在没有配置的情况下，让我们在另一种测试方法的帮助下查看此行为：

```java
ObjectMapper mapper = new ObjectMapper();
String json = "{"id":1,"name":"My Game","paused":1,"over":0}";
Game game = mapper.readValue(json, Game.class);

assertThat(game.isPaused()).isEqualTo(true);
assertThat(game.isOver()).isEqualTo(false);
```

因此，Jackson 开箱即用地支持整数到布尔值的反序列化。

## 4. 数字字符串而不是整数

另一个用例是使用数字字符串—— “1”和“0” ——而不是整数。在这种情况下，将布尔值序列化为数字字符串或将它们反序列化回布尔值需要更多的努力。

### 4.1. 序列化为数字字符串

要将布尔值序列化为数字字符串等价物，我们需要定义一个自定义序列化程序。

因此，让我们通过扩展 Jackson 的JsonSerializer来创建我们的NumericBooleanSerializer：

```java
public class NumericBooleanSerializer extends JsonSerializer<Boolean> {

    @Override
    public void serialize(Boolean value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
        gen.writeString(value ? "1" : "0");
    }
}
```

作为旁注，通常情况下，布尔类型可以为null。但是，Jackson 会在内部处理这个问题，并且当value字段为null时不会考虑我们的自定义序列化程序。因此，我们在这里很安全。

接下来，我们将注册我们的自定义序列化程序，以便 Jackson 识别和使用它。

如果我们只需要对有限数量的字段进行这种行为，我们可以选择带有@JsonSerialize 注解的字段级配置。

因此，让我们注解我们的布尔字段，paused和over：

```java
@JsonSerialize(using = NumericBooleanSerializer.class)
private Boolean paused;

@JsonSerialize(using = NumericBooleanSerializer.class)
private Boolean over;
```

然后，同样地，我们在测试方法中尝试序列化：

```java
ObjectMapper mapper = new ObjectMapper();
Game game = new Game(1L, "My Game");
game.setPaused(true);
game.setOver(false);
String json = mapper.writeValueAsString(game);

assertThat(json)
  .isEqualTo("{"id":1,"name":"My Game","paused":"1","over":"0"}");
```

尽管测试方法的实现与之前的几乎相同，但我们应该注意数字值周围的引号—— “暂停”：“1”，“结束”：“0” 。当然，这表明这些值是包含数字内容的实际字符串。

最后但同样重要的是，如果我们需要在任何地方执行此自定义序列化， Jackson 支持通过 Jackson 模块将它们添加到ObjectMapper来全局配置序列化程序：

```java
ObjectMapper mapper = new ObjectMapper();
SimpleModule module = new SimpleModule();
module.addSerializer(Boolean.class, new NumericBooleanSerializer());
mapper.registerModule(module);

Game game = new Game(1L, "My Game");
game.setPaused(true);
game.setOver(false);
String json = mapper.writeValueAsString(game);

assertThat(json)
  .isEqualTo("{"id":1,"name":"My Game","paused":"1","over":"0"}");
```

因此，只要我们使用相同的ObjectMapper实例，Jackson 就会将所有布尔类型的字段序列化为数字字符串。

### 4.2. 从数字字符串反序列化

与序列化类似，这次我们将定义一个自定义反序列化器来将数字字符串解析为布尔值。

让我们通过扩展JsonDeserializer来创建我们的类NumericBooleanDeserializer：

```java
public class NumericBooleanDeserializer extends JsonDeserializer<Boolean> {

    @Override
    public Boolean deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException {
        if ("1".equals(p.getText())) {
            return Boolean.TRUE;
        }
        if ("0".equals(p.getText())) {
            return Boolean.FALSE;
        }
        return null;
    }

}
```

接下来，我们再次注解我们的布尔字段，但这次使用@JsonDeserialize：

```java
@JsonSerialize(using = NumericBooleanSerializer.class)
@JsonDeserialize(using = NumericBooleanDeserializer.class)
private Boolean paused;

@JsonSerialize(using = NumericBooleanSerializer.class)
@JsonDeserialize(using = NumericBooleanDeserializer.class)
private Boolean over;
```

因此，让我们编写另一个测试方法来查看我们的NumericBooleanDeserializer的运行情况：

```java
ObjectMapper mapper = new ObjectMapper();
String json = "{"id":1,"name":"My Game","paused":"1","over":"0"}";
Game game = mapper.readValue(json, Game.class);

assertThat(game.isPaused()).isEqualTo(true);
assertThat(game.isOver()).isEqualTo(false);
```

或者，也可以通过 Jackson 模块对我们的自定义解串器进行全局配置：

```java
ObjectMapper mapper = new ObjectMapper();
SimpleModule module = new SimpleModule();
module.addDeserializer(Boolean.class, new NumericBooleanDeserializer());
mapper.registerModule(module);

String json = "{"id":1,"name":"My Game","paused":"1","over":"0"}";
Game game = mapper.readValue(json, Game.class);

assertThat(game.isPaused()).isEqualTo(true);
assertThat(game.isOver()).isEqualTo(false);
```

## 5.总结

在本文中，我们描述了如何将布尔值序列化为整数和数字字符串，以及如何将它们反序列化。
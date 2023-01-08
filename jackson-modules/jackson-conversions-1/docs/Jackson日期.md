## 1. 概述

在本教程中，我们将使用 Jackson 序列化日期。我们将从序列化一个简单的 java.util 开始。Date，然后是 Joda-Time，最后是Java8 DateTime。

## 2.将日期序列化为时间戳

首先，让我们看看如何使用 Jackson序列化一个简单的java.util.Date。

在下面的示例中，我们将序列化“事件”的一个实例，它具有日期字段“ eventDate ”：

```java
@Test
public void whenSerializingDateWithJackson_thenSerializedToTimestamp()
  throws JsonProcessingException, ParseException {
 
    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm");
    df.setTimeZone(TimeZone.getTimeZone("UTC"));

    Date date = df.parse("01-01-1970 01:00");
    Event event = new Event("party", date);

    ObjectMapper mapper = new ObjectMapper();
    mapper.writeValueAsString(event);
}
```

需要注意的是，Jackson 默认会将 Date 序列化为时间戳格式(自 1970 年 1 月 1 日以来的毫秒数，UTC)。

“事件”序列化的实际输出是：

```bash
{
   "name":"party",
   "eventDate":3600000
}
```

## 3. 将日期序列化为 ISO-8601

序列化为这种简洁的时间戳格式并不是最优的。相反，让我们将 Date 序列化为ISO - 8601格式：

```java
@Test
public void whenSerializingDateToISO8601_thenSerializedToText()
  throws JsonProcessingException, ParseException {
 
    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm");
    df.setTimeZone(TimeZone.getTimeZone("UTC"));

    String toParse = "01-01-1970 02:30";
    Date date = df.parse(toParse);
    Event event = new Event("party", date);

    ObjectMapper mapper = new ObjectMapper();
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    // StdDateFormat is ISO8601 since jackson 2.9
    mapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true));
    String result = mapper.writeValueAsString(event);
    assertThat(result, containsString("1970-01-01T02:30:00.000+00:00"));
}
```

我们可以看到日期的表示现在更具可读性。

## 4.配置ObjectMapper DateFormat

以前的解决方案仍然缺乏选择确切格式来表示java.util.Date实例的充分灵活性。

相反，让我们看一下允许我们设置表示日期的格式的配置：

```java
@Test
public void whenSettingObjectMapperDateFormat_thenCorrect()
  throws JsonProcessingException, ParseException {
 
    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm");

    String toParse = "20-12-2014 02:30";
    Date date = df.parse(toParse);
    Event event = new Event("party", date);

    ObjectMapper mapper = new ObjectMapper();
    mapper.setDateFormat(df);

    String result = mapper.writeValueAsString(event);
    assertThat(result, containsString(toParse));
}
```

请注意，尽管我们现在在日期格式方面更加灵活，但我们仍在整个ObjectMapper级别使用全局配置。

## 5.使用@JsonFormat格式化日期

接下来让我们看一下@JsonFormat注解来控制单个类的日期格式，而不是全局地控制整个应用程序的日期格式：

```java
public class Event {
    public String name;

    @JsonFormat
      (shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    public Date eventDate;
}
```

现在让我们测试一下：

```java
@Test
public void whenUsingJsonFormatAnnotationToFormatDate_thenCorrect()
  throws JsonProcessingException, ParseException {
 
    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
    df.setTimeZone(TimeZone.getTimeZone("UTC"));

    String toParse = "20-12-2014 02:30:00";
    Date date = df.parse(toParse);
    Event event = new Event("party", date);

    ObjectMapper mapper = new ObjectMapper();
    String result = mapper.writeValueAsString(event);
    assertThat(result, containsString(toParse));
}
```

## 6.自定义日期序列化器

接下来，为了完全控制输出，我们将利用日期的自定义序列化程序：

```java
public class CustomDateSerializer extends StdSerializer<Date> {
 
    private SimpleDateFormat formatter 
      = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

    public CustomDateSerializer() {
        this(null);
    }

    public CustomDateSerializer(Class t) {
        super(t);
    }
    
    @Override
    public void serialize (Date value, JsonGenerator gen, SerializerProvider arg2)
      throws IOException, JsonProcessingException {
        gen.writeString(formatter.format(value));
    }
}
```

现在我们将把它用作我们的“ eventDate ”字段的序列化器：

```java
public class Event {
    public String name;

    @JsonSerialize(using = CustomDateSerializer.class)
    public Date eventDate;
}
```

最后我们来测试一下：

```java
@Test
public void whenUsingCustomDateSerializer_thenCorrect()
  throws JsonProcessingException, ParseException {
 
    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

    String toParse = "20-12-2014 02:30:00";
    Date date = df.parse(toParse);
    Event event = new Event("party", date);

    ObjectMapper mapper = new ObjectMapper();
    String result = mapper.writeValueAsString(event);
    assertThat(result, containsString(toParse));
}
```

## 延伸阅读：

## [如何使用 Jackson 序列化和反序列化枚举](https://www.baeldung.com/jackson-serialize-enums)

如何使用 Jackson 2 将枚举序列化和反序列化为 JSON 对象。

[阅读更多](https://www.baeldung.com/jackson-serialize-enums)→

## [Jackson——自定义序列化器](https://www.baeldung.com/jackson-custom-serialization)

通过使用自定义序列化器控制 Jackson 2 的 JSON 输出。

[阅读更多](https://www.baeldung.com/jackson-custom-serialization)→

## [Jackson 中的自定义反序列化入门](https://www.baeldung.com/jackson-deserialization)

使用 Jackson 将自定义 JSON 映射到任何 java 实体图，并完全控制反序列化过程。

[阅读更多](https://www.baeldung.com/jackson-deserialization)→

## 7. 与 Jackson 连载 Joda-Time

日期并不总是java.util.Date 的实例。事实上，越来越多的日期由其他类表示，一个常见的类是 Joda-Time 库中的DateTime实现。

让我们看看如何使用 Jackson序列化DateTime。

我们将使用[jackson-datatype-joda](https://search.maven.org/artifact/com.fasterxml.jackson.datatype/jackson-datatype-joda)模块来提供开箱即用的 Joda-Time 支持：

```xml
<dependency>
  <groupId>com.fasterxml.jackson.datatype</groupId>
  <artifactId>jackson-datatype-joda</artifactId>
  <version>2.9.7</version>
</dependency>
```

然后我们可以简单地注册JodaModule并完成：

```java
@Test
public void whenSerializingJodaTime_thenCorrect() 
  throws JsonProcessingException {
    DateTime date = new DateTime(2014, 12, 20, 2, 30, 
      DateTimeZone.forID("Europe/London"));

    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JodaModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    String result = mapper.writeValueAsString(date);
    assertThat(result, containsString("2014-12-20T02:30:00.000Z"));
}
```

## 8.使用自定义序列化程序序列化 Joda DateTime

如果我们不想要额外的 Joda-Time Jackson 依赖项，我们还可以使用自定义序列化程序(类似于前面的示例)来干净地序列化DateTime实例：

```java
public class CustomDateTimeSerializer extends StdSerializer<DateTime> {

    private static DateTimeFormatter formatter = 
      DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");

    public CustomDateTimeSerializer() {
        this(null);
    }

     public CustomDateTimeSerializer(Class<DateTime> t) {
         super(t);
     }
    
    @Override
    public void serialize
      (DateTime value, JsonGenerator gen, SerializerProvider arg2)
      throws IOException, JsonProcessingException {
        gen.writeString(formatter.print(value));
    }
}
```

然后我们可以将它用作我们的属性“ eventDate ”序列化程序：

```java
public class Event {
    public String name;

    @JsonSerialize(using = CustomDateTimeSerializer.class)
    public DateTime eventDate;
}
```

最后，我们可以将所有内容放在一起并进行测试：

```java
@Test
public void whenSerializingJodaTimeWithJackson_thenCorrect() 
  throws JsonProcessingException {
 
    DateTime date = new DateTime(2014, 12, 20, 2, 30);
    Event event = new Event("party", date);

    ObjectMapper mapper = new ObjectMapper();
    String result = mapper.writeValueAsString(event);
    assertThat(result, containsString("2014-12-20 02:30"));
}
```

## 9.使用 Jackson序列化Java8 Date

现在让我们看看如何使用 Jackson序列化Java8 DateTime，在此示例中为LocalDateTime。我们可以使用[jackson-datatype-jsr310](https://search.maven.org/artifact/com.fasterxml.jackson.datatype/jackson-datatype-jsr310)模块：

```xml
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
    <version>2.9.7</version>
</dependency>
```

然后我们需要做的就是注册JavaTimeModule(不推荐使用JSR310Module)，Jackson 会处理剩下的事情：

```java
@Test
public void whenSerializingJava8Date_thenCorrect()
  throws JsonProcessingException {
    LocalDateTime date = LocalDateTime.of(2014, 12, 20, 2, 30);

    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    String result = mapper.writeValueAsString(date);
    assertThat(result, containsString("2014-12-20T02:30"));
}
```

## 10. 序列化Java8 Date没有任何额外的依赖

如果我们不想要额外的依赖，我们总是可以使用自定义序列化程序将Java8 DateTime写出到 JSON：

```java
public class CustomLocalDateTimeSerializer 
  extends StdSerializer<LocalDateTime> {

    private static DateTimeFormatter formatter = 
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public CustomLocalDateTimeSerializer() {
        this(null);
    }
 
    public CustomLocalDateTimeSerializer(Class<LocalDateTime> t) {
        super(t);
    }
    
    @Override
    public void serialize(
      LocalDateTime value,
      JsonGenerator gen,
      SerializerProvider arg2)
      throws IOException, JsonProcessingException {
 
        gen.writeString(formatter.format(value));
    }
}
```

然后我们将为我们的“ eventDate ”字段使用序列化程序：

```java
public class Event {
    public String name;

    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    public LocalDateTime eventDate;
}
```

最后我们来测试一下：

```java
@Test
public void whenSerializingJava8DateWithCustomSerializer_thenCorrect()
  throws JsonProcessingException {
 
    LocalDateTime date = LocalDateTime.of(2014, 12, 20, 2, 30);
    Event event = new Event("party", date);

    ObjectMapper mapper = new ObjectMapper();
    String result = mapper.writeValueAsString(event);
    assertThat(result, containsString("2014-12-20 02:30"));
}
```

## 11.反序列化日期

现在让我们看看如何反序列化Date with Jackson。在以下示例中，我们将反序列化包含日期的“ Event ”实例：

```java
@Test
public void whenDeserializingDateWithJackson_thenCorrect()
  throws JsonProcessingException, IOException {
 
    String json = "{"name":"party","eventDate":"20-12-2014 02:30:00"}";

    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
    ObjectMapper mapper = new ObjectMapper();
    mapper.setDateFormat(df);

    Event event = mapper.readerFor(Event.class).readValue(json);
    assertEquals("20-12-2014 02:30:00", df.format(event.eventDate));
}
```

## 12.反序列化保留时区的Joda ZonedDateTime

在其默认配置中，Jackson 将 Joda ZonedDateTime的时区调整为本地上下文的时区。由于默认没有设置本地上下文的时区，需要手动配置，Jackson将时区调整为GMT：

```java
@Test
public void whenDeserialisingZonedDateTimeWithDefaults_thenNotCorrect()
  throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Berlin"));
    String converted = objectMapper.writeValueAsString(now);

    ZonedDateTime restored = objectMapper.readValue(converted, ZonedDateTime.class);
    System.out.println("serialized: " + now);
    System.out.println("restored: " + restored);
    assertThat(now, is(restored));
}
```

测试用例将失败并输出：

```java
serialized: 2017-08-14T13:52:22.071+02:00[Europe/Berlin]
restored: 2017-08-14T11:52:22.071Z[UTC]
```

幸运的是，对于这种奇怪的默认行为，有一个快速简单的修复方法；我们只需要告诉杰克逊不要调整时区。

这可以通过将下面的代码行添加到上面的测试用例来完成：

```java
objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
```

请注意，为了保留时区，我们还必须禁用将日期序列化为时间戳的默认行为。

## 13.自定义日期反序列化器

我们还可以使用自定义Date反序列化器。我们将为属性“ eventDate ”编写自定义反序列化程序：

```java
public class CustomDateDeserializer extends StdDeserializer<Date> {

    private SimpleDateFormat formatter = 
      new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

    public CustomDateDeserializer() {
        this(null);
    }

    public CustomDateDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Date deserialize(JsonParser jsonparser, DeserializationContext context)
      throws IOException, JsonProcessingException {
        String date = jsonparser.getText();
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
```

接下来我们将其用作“ eventDate ”反序列化器：

```java
public class Event {
    public String name;

    @JsonDeserialize(using = CustomDateDeserializer.class)
    public Date eventDate;
}
```

最后我们来测试一下：

```java
@Test
public void whenDeserializingDateUsingCustomDeserializer_thenCorrect()
  throws JsonProcessingException, IOException {
 
    String json = "{"name":"party","eventDate":"20-12-2014 02:30:00"}";

    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
    ObjectMapper mapper = new ObjectMapper();

    Event event = mapper.readerFor(Event.class).readValue(json);
    assertEquals("20-12-2014 02:30:00", df.format(event.eventDate));
}
```

## 14. 修复InvalidDefinition异常

在创建LocalDate实例时，我们可能会遇到一个异常：

```java
com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Cannot construct instance
of `java.time.LocalDate`(no Creators, like default construct, exist): no String-argument
constructor/factory method to deserialize from String value ('2014-12-20') at [Source:
(String)"2014-12-20"; line: 1, column: 1]
```

出现此问题是因为 JSON 本身没有日期格式，因此它将日期表示为String。

日期的String表示与内存中LocalDate类型的对象不同，因此我们需要一个外部反序列化器从String读取该字段，并需要一个序列化器将日期呈现为String格式。

这些方法也适用于LocalDateTime，唯一的变化是对LocalDateTime使用等效类。

### 14.1. 杰克逊依赖

Jackson 允许我们通过几种方式解决这个问题。首先，我们必须确保[jsr310依赖](https://search.maven.org/artifact/com.fasterxml.jackson.datatype/jackson-datatype-jsr310)项在我们的pom.xml中：

```xml
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
    <version>2.11.0</version>
</dependency>
```

### 14.2. 序列化为单个日期对象

为了能够处理LocalDate，我们需要 用我们的ObjectMapper注册JavaTimeModule。

我们还需要禁用ObjectMapper中的WRITE_DATES_AS_TIMESTAMPS 功能，以防止 Jackson 在 JSON 输出中添加时间数字：

```java
@Test
public void whenSerializingJava8DateAndReadingValue_thenCorrect() throws IOException {
    String stringDate = ""2014-12-20"";

    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    LocalDate result = mapper.readValue(stringDate, LocalDate.class);
    assertThat(result.toString(), containsString("2014-12-20"));
}
```

在这里，我们使用了 Jackson 对日期序列化和反序列化的原生支持。

### 14.3. POJO 中的注解

处理该问题的另一种方法是在实体级别使用LocalDateDeserializer 和JsonFormat注解：

```java
public class EventWithLocalDate {

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    public LocalDate eventDate;
}
```

@JsonDeserialize注解用于指定自定义反序列化器以解组 JSON 对象。同样，@JsonSerialize指示在封送实体时使用的自定义序列化程序。

此外，注解 @JsonFormat允许我们指定序列化日期值的格式。因此，这个 POJO 可以用来读写 JSON：

```java
@Test
public void whenSerializingJava8DateAndReadingFromEntity_thenCorrect() throws IOException {
    String json = "{"name":"party","eventDate":"20-12-2014"}";

    ObjectMapper mapper = new ObjectMapper();

    EventWithLocalDate result = mapper.readValue(json, EventWithLocalDate.class);
    assertThat(result.getEventDate().toString(), containsString("2014-12-20"));
}
```

虽然这种方法比使用 JavaTimeModule默认值需要更多的工作，但它的可定制性要高得多。

## 15.总结

在这篇广泛的日期文章中，我们研究了Jackson 可以使用我们可以控制的合理格式帮助将日期编组和解组为 JSON的几种方法。
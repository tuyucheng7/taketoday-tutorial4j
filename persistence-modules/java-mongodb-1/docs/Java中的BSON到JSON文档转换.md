## 1. 概述

在上一篇文章中，我们了解了如何从 MongoDB中[检索 BSON 文档作为Java对象。](https://www.baeldung.com/mongodb-bson)

这是开发 REST API 的一种非常常见的方法，因为我们可能希望在将这些对象转换为 JSON 之前修改它们(例如使用[Jackson )。](https://www.baeldung.com/jackson)

但是，我们可能不想更改文档的任何内容。为了省去编码冗长的Java对象映射的麻烦，我们可以使用直接 BSON 到 JSON 文档转换。

让我们看看[MongoDB BSON API](https://search.maven.org/artifact/org.mongodb/bson)如何处理这个用例。

## 2. 使用 Morphia 在 MongoDB 中创建 BSON 文档

首先，让我们按照[本文所述](https://www.baeldung.com/mongodb-morphia)使用 Morphia 设置我们的依赖项。

这是我们的示例 实体，其中包括各种属性类型：

```java
@Entity("Books")
public class Book {
    @Id
    private String isbn;

    @Embedded
    private Publisher publisher;

    @Property("price")
    private double cost;

    @Property
    private LocalDateTime publishDate;

    // Getters and setters ...
}
```

然后让我们为我们的测试创建一个新的 BSON 实体并将其保存到 MongoDB：

```java
public class BsonToJsonIntegrationTest {
    
    private static final String DB_NAME = "library";
    private static Datastore datastore;

    @BeforeClass
    public static void setUp() {
        Morphia morphia = new Morphia();
        morphia.mapPackage("com.baeldung.morphia");
        datastore = morphia.createDatastore(new MongoClient(), DB_NAME);
        datastore.ensureIndexes();
        
        datastore.save(new Book()
          .setIsbn("isbn")
          .setCost(3.95)
          .setPublisher(new Publisher(new ObjectId("fffffffffffffffffffffffa"),"publisher"))
          .setPublishDate(LocalDateTime.parse("2020-01-01T18:13:32Z", DateTimeFormatter.ISO_DATE_TIME)));
    }
}
```

## 3.默认BSON到JSON文档转换

现在让我们测试非常简单的默认转换：只需从 BSON Document类调用toJson方法： 

```java
@Test
public void givenBsonDocument_whenUsingStandardJsonTransformation_thenJsonDateIsObjectEpochTime() {
     String json = null;
     try (MongoClient mongoClient = new MongoClient()) {
         MongoDatabase mongoDatabase = mongoClient.getDatabase(DB_NAME);
         Document bson = mongoDatabase.getCollection("Books").find().first();
         assertEquals(expectedJson, bson.toJson());
     }
}
```

expectedJson值为：

```javascript
{
    "_id": "isbn",
    "className": "com.baeldung.morphia.domain.Book",
    "publisher": {
        "_id": {
            "$oid": "fffffffffffffffffffffffa"
        },
        "name": "publisher"
    },
    "price": 3.95,
    "publishDate": {
        "$date": 1577898812000
    }
}
```

这似乎对应于标准的 JSON 映射。

但是，我们可以看到默认情况下将日期转换为具有[纪元时间](https://en.wikipedia.org/wiki/Unix_time)格式的$date字段的对象。现在让我们看看如何更改此日期格式。

## 4. 轻松的 BSON 到 JSON 日期转换

例如，如果我们想要更经典的 ISO 日期表示(例如用于 JavaScript 客户端)，我们可以使用JsonWriterSettings.builder将宽松的JSON 模式传递给toJson方法：

```java
bson.toJson(JsonWriterSettings
  .builder()
  .outputMode(JsonMode.RELAXED)
  .build());
```

结果，我们可以看到publishDate字段的“宽松”转换：

```javascript
{
    ...
    "publishDate": {
        "$date": "2020-01-01T17:13:32Z"
    }
    ...
}
```

这种格式似乎是正确的，但我们仍然有$date字段——让我们看看如何使用自定义转换器摆脱它。

## 5. 自定义 BSON 到 JSON 日期转换

首先，我们必须为Long类型实现BSON Converter接口，因为日期值是从纪元时间开始以毫秒为单位表示的。我们正在使用DateTimeFormatter.ISO_INSTANT来获取预期的输出格式：

```java
public class JsonDateTimeConverter implements Converter<Long> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonDateTimeConverter.class);
    static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_INSTANT
        .withZone(ZoneId.of("UTC"));

    @Override
    public void convert(Long value, StrictJsonWriter writer) {
        try {
            Instant instant = new Date(value).toInstant();
            String s = DATE_TIME_FORMATTER.format(instant);
            writer.writeString(s);
        } catch (Exception e) {
            LOGGER.error(String.format("Fail to convert offset %d to JSON date", value), e);
        }
    }
}
```

然后，我们可以将此类的实例作为 DateTime 转换器传递给JsonWriterSettings构建器：

```java
bson.toJson(JsonWriterSettings
  .builder()
  .dateTimeConverter(new JsonDateTimeConverter())
  .build());
```

最后，我们得到一个普通的 JSON ISO 日期格式：

```javascript
{
    ...
    "publishDate": "2020-01-01T17:13:32Z"
    ...
}
```

## 六. 总结

在本文中，我们了解了 BSON 到 JSON 文档转换的默认行为。

我们强调了如何使用 BSON Converter自定义日期格式，这是一个常见问题。

当然，我们可以用同样的方法转换其他数据类型：例如数字、布尔值、空值或对象 ID。
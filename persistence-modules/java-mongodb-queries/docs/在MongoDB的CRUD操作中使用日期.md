## 1. 概述

在本教程中，我们将使用[MongoDB](https://www.baeldung.com/java-mongodb)JavaDriver 执行与日期相关的 CRUD 操作，例如创建和更新带有日期字段的文档，以及查询、更新和删除日期字段在给定范围内的文档。

## 2.设置

在深入实施之前，让我们设置我们的工作环境。

### 2.1. Maven 依赖

首先，你应该安装 MongoDB。如果你不这样做，你可以按照官方的 MongoDB 安装[指南](https://www.mongodb.com/docs/manual/administration/install-community/)来安装。

接下来，让我们将[MongoDBJava驱动程序](https://mvnrepository.com/artifact/org.mongodb/mongodb-driver-sync)作为依赖项添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>org.mongodb</groupId>
    <artifactId>mongodb-driver-sync</artifactId>
    <version>4.6.0</version>
</dependency>
```

### 2.2. POJO数据模型

让我们定义一个[POJO](https://www.baeldung.com/java-pojo-class)来表示我们数据库中包含的文档：

```java
public class Event {
    private String title;
    private String location;
    private LocalDateTime dateTime;

    public Event() {}
    public Event(String title, String location, LocalDateTime dateTime) {
        this.title = title;
        this.location = location;
        this.dateTime = dateTime;
    }
    
    // standard setters and getters
}
```

请注意，我们已经声明了两个构造函数。[MongoDB默认](https://www.mongodb.com/docs/drivers/java/sync/current/fundamentals/data-formats/pojo-customization/#pojos-without-no-argument-constructors)使用无参数构造函数。另一个构造函数在本教程中供我们自己使用。

我们还要注意，虽然dateTime可能是一个String变量，但最佳实践是对日期字段使用特定于日期/时间的 JDK 类。使用字符串字段来表示日期需要额外的努力来确保值的格式正确。

我们现在准备将客户端连接到我们的数据库。

### 2.3. MongoDB 客户端

为了让 MongoDB 序列化/反序列化我们的Event POJO，我们需要向MongoDB 的CodecRegistry注册PojoCodecProvider：

```java
CodecProvider codecProvider = PojoCodecProvider.builder().automatic(true).build();
CodecRegistry codecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(codecProvider));
```

让我们创建一个数据库、集合和客户端，它们将使用我们注册的PojoCodecProvider ：

```java
MongoClient mongoClient = MongoClients.create(uri);
MongoDatabase db = mongoClient.getDatabase("calendar").withCodecRegistry(codecRegistry);
MongoCollection<Event> collection = db.getCollection("my_events", Event.class);
```

我们现在已准备好创建文档并执行与日期相关的 CRUD 操作。

## 3. 创建带有日期字段的文档

在我们的 POJO 中，我们使用[LocalDateTime](https://www.baeldung.com/java-8-date-time-intro)而不是String以便更轻松地处理日期值。让我们现在通过使用LocalDateTime的便捷 API构造Event对象来利用它：

```java
Event pianoLessonsEvent = new Event("Piano lessons", "Foo Blvd",
  LocalDateTime.of(2022, 6, 4, 11, 0, 0));
Event soccerGameEvent = new Event("Soccer game", "Bar Avenue",
  LocalDateTime.of(2022, 6, 10, 17, 0, 0));
```

我们可以将新的Event插入到我们的数据库中，如下所示：

```java
InsertOneResult pianoLessonsInsertResult = collection.insertOne(pianoLessonsEvent);
InsertOneResult soccerGameInsertResult = collection.insertOne(soccerGameEvent);
```

让我们通过检查插入文档的 id 来验证插入是否成功：

```java
assertNotNull(pianoLessonsInsertResult.getInsertedId());
assertNotNull(soccerGameInsertResult.getInsertedId());
```

## 4.查询单据匹配日期条件

现在我们的数据库中有Event s，让我们根据它们的日期字段检索它们。

我们可以使用相等过滤器 ( eq ) 来检索与特定日期和时间匹配的文档：

```java
LocalDateTime dateTime = LocalDateTime.of(2022, 6, 10, 17, 0, 0);
Event event = collection.find(eq("dateTime", dateTime)).first();
```

让我们检查结果Event的各个字段：

```java
assertEquals("Soccer game", event.title);
assertEquals("Bar Avenue", event.location);
assertEquals(dateTime, event.dateTime);
```

我们还可以使用 MongoDB BasicDBObject类以及gte和lte运算符来使用日期范围构建更复杂的查询：

```java
LocalDateTime from = LocalDateTime.of(2022, 06, 04, 12, 0, 0);
LocalDateTime to = LocalDateTime.of(2022, 06, 10, 17, 0, 0);
BasicDBObject object = new BasicDBObject();
object.put("dateTime", BasicDBObjectBuilder.start("$gte", from).add("$lte", to).get());
List list = new ArrayList(collection.find(object).into(new ArrayList()));
```

Since the soccer game is the only Event within the date range of our query, we should only see one Event object in the list, with the piano lesson excluded:

```plaintext
assertEquals(1, events.size());
assertEquals("Soccer game", events.get(0).title);
assertEquals("Bar Avenue", events.get(0).location);
assertEquals(dateTime, events.get(0).dateTime);Copy
```

## 5. Updating Documents

Let's explore two use cases for updating documents based on their date fields. First, we'll update the date field of a single document, and then we'll update multiple documents matching a date range.

### 5.1. Updating a Document's Date Field

To update a MongoDB document, we can use the updateOne() method. Let's also use the currentDate() method to set the dateTime field of our piano lesson event:

```java
Document document = new Document().append("title", "Piano lessons");
Bson update = Updates.currentDate("dateTime");
UpdateOptions options = new UpdateOptions().upsert(false);
UpdateResult result = collection.updateOne(document, update, options);Copy
```

请注意，updateOne()的第一个参数是一个Document对象，MongoDB 将使用它来匹配我们数据库中的单个条目。如果多个文档匹配，MongoDB 将只更新它遇到的第一个文档。还要注意，我们将false传递给了upsert()方法。如果我们改为传入true，如果现有文档均不匹配，MongoDB 将插入一个新文档。

我们可以通过检查修改了多少文档来确认操作是否成功：

```java
assertEquals(1, result.getModifiedCount());
```

### 5.2. 更新文件匹配日期标准

为了更新多个文档，MongoDB 提供了updateMany方法。在此示例中，我们将更新与查询中的日期范围相匹配的多个事件。

与updateOne()不同，updateMany()方法需要第二个Bson对象来封装查询条件，该条件将定义我们要更新的文档。在这种情况下，我们将通过引入lt字段运算符来指定涵盖 2022 年所有事件的日期范围：

```java
LocalDate updateManyFrom = LocalDate.of(2022, 1, 1);
LocalDate updateManyTo = LocalDate.of(2023, 1, 1);
Bson query = and(gte("dateTime", from), lt("dateTime", to));
Bson updates = Updates.currentDate("dateTime");
UpdateResult result = collection.updateMany(query, updates);
```

就像updateOne()一样，我们可以通过检查结果对象的更新计数来确认此操作更新了多个事件：

```java
assertEquals(2, result.getModifiedCount());
```

## 6. 删除符合日期条件的文件

与更新一样，我们可以一次从数据库中删除一个或多个文档。假设我们需要删除 2022 年的所有事件。让我们使用Bson日期范围查询和 deleteMany()方法来执行此操作：

```java
LocalDate deleteFrom = LocalDate.of(2022, 1, 1);
LocalDate deleteTo = LocalDate.of(2023, 1, 1);
Bson query = and(gte("dateTime", deleteFrom), lt("dateTime", deleteTo));
DeleteResult result = collection.deleteMany(query);

```

由于我们在本教程中创建的所有事件都有一个 2022日期时间字段值，因此deleteMany ()将它们全部从我们的集合中删除。我们可以通过检查删除计数来确认这一点：

```java
assertEquals(2, result.getDeletedCount());
```

## 7. 使用时区

MongoDB 以 UTC 格式存储日期，并且无法更改。因此，如果我们希望我们的日期字段特定于某个时区，我们可以将时区偏移量存储在一个单独的字段中并自己进行转换。让我们将该字段添加为String：

```java
public String timeZoneOffset;
```

我们需要调整我们的构造函数，以便我们可以在创建事件时设置新字段：

```arduino
public Event(String title, String location, LocalDateTime dateTime, String timeZoneOffset) {
    this.title = title;
    this.location = location;
    this.dateTime = dateTime;
    this.timeZoneOffset = timeZoneOffset;
}
```

我们现在可以为特定时区创建事件并将其插入到我们的数据库中。让我们使用ZoneOffset类来避免手动格式化时区偏移量字符串：

```java
LocalDateTime utcDateTime = LocalDateTime.of(2022, 6, 20, 11, 0, 0);
Event pianoLessonsTZ = new Event("Piano lessons", "Baz Bvld", utcDateTime, ZoneOffset.ofHours(2).toString());
InsertOneResult pianoLessonsTZInsertResult = collection.insertOne(pianoLessonsTZ);
assertNotNull(pianoLessonsTZInsertResult.getInsertedId());
```

注意，由于偏移量是相对于 UTC 的，所以 dateTime 成员变量必须代表 UTC 时间，以便我们稍后可以正确转换。从集合中检索文档后，我们可以使用偏移量字段和[OffsetDateTime](https://www.baeldung.com/java-8-date-time-intro)类进行转换：

```java
OffsetDateTime dateTimeWithOffset = OffsetDateTime.of(pianoLessonsTZ.dateTime, ZoneOffset.of(pianoLessonsTZ.timeZoneOffset));
```

## 八. 总结

在本文中，我们学习了如何使用Java和 MongoDB 数据库执行与日期相关的 CRUD 操作。

我们使用日期值来创建、检索、更新或删除数据库中的文档。在我们的整个示例中，我们涵盖了各种帮助程序类并介绍了在处理日期时很有用的 MongoDB 运算符。最后，为了解决 MongoDB 如何仅以 UTC 格式存储日期，我们学习了如何处理需要特定于时区的日期/时间值。
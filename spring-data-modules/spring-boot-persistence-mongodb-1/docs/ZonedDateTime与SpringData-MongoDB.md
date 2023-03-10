## 1. 概述

[Spring Data MongoDB](https://spring.io/projects/spring-data-mongodb)模块提高了在Spring项目中与MongoDB数据库交互时的可读性和可用性。

**在本教程中，我们将重点介绍在读取和写入MongoDB数据库时如何处理ZonedDateTime Java对象**。

## 2. 项目构建

要使用Spring Data MongoDB模块，我们需要添加以下依赖项：

```xml
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-mongodb</artifactId>
    <version>3.0.3.RELEASE</version>
</dependency>
```

可以在[此处](https://central.sonatype.com/artifact/org.springframework.data/spring-data-mongodb/4.0.3)找到该库的最新版本。

让我们定义一个名为Action的模型类(具有ZonedDateTime属性)：

```java
@Document
public class Action {
    @Id
    private String id;

    private String description;
    private ZonedDateTime time;

    // constructor, getters and setters 
}
```

为了与MongoDB交互，我们还将创建一个扩展MongoRepository的接口：

```java
public interface ActionRepository extends MongoRepository<Action, String> {
}
```

现在我们将定义一个测试，它将一个Action对象插入MongoDB并断言它以正确的时间存储。在断言语句中，我们将删除纳秒信息，因为MongoDB Date类型的精度为毫秒：

```java
@Test
void givenSavedAction_TimeIsRetrievedCorrectly() {
	String id = "testId";
	ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
    
	actionRepository.save(new Action(id, "click-action", now));
	Action savedAction = actionRepository.findById(id).get();
    
	assertEquals(now.withNano(0), savedAction.getTime().withNano(0));
}
```

此时，我们在运行测试时会收到以下错误：

```shell
org.bson.codecs.configuration.CodecConfigurationException: Can't find a codec for class java.time.ZonedDateTime
```

**这是因为Spring Data MongoDB没有定义ZonedDateTime转换器**，接下来我们看看如何配置它们。

## 3. MongoDB转换器

我们可以通过定义一个用于从MongoDB读取和写入数据的转换器来处理ZonedDateTime对象(跨所有模型)。

为了便于阅读，我们将Date对象转换为ZonedDateTime对象。在下一个示例中，我们使用ZoneOffset.UTC，因为Date对象不存储区域信息：

```java
public class ZonedDateTimeReadConverter implements Converter<Date, ZonedDateTime> {
    @Override
    public ZonedDateTime convert(Date date) {
        return date.toInstant().atZone(ZoneOffset.UTC);
    }
}
```

然后，我们将ZonedDateTime对象转换为Date对象。如果需要，我们可以将区域信息添加到另一个字段：

```java
public class ZonedDateTimeWriteConverter implements Converter<ZonedDateTime, Date> {
    @Override
    public Date convert(ZonedDateTime zonedDateTime) {
        return Date.from(zonedDateTime.toInstant());
    }
}
```

由于Date对象不存储时区偏移量，因此我们在示例中使用UTC。将ZonedDateTimeReadConverter和ZonedDateTimeWriteConverter添加到MongoCustomConversions后，我们的测试现在将通过。

存储对象的简单打印如下所示：

```shell
Action{id='testId', description='click', time=2018-11-08T08:03:11.257Z}
```

要了解有关如何注册MongoDB转换器的更多信息，我们可以参考[本教程](https://www.baeldung.com/spring-data-mongodb-index-annotations-converter)。

## 4. 总结

在这篇快速文章中，我们了解了如何创建MongoDB转换器来处理Java ZonedDateTime对象。
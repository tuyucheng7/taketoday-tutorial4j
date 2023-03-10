## 1. 概述

本教程将探讨**Spring Data MongoDB的核心功能之一：与GridFS交互**。

GridFS存储规范主要用于处理超过BSON文档大小限制16MB的文件。Spring Data提供了一个GridFsOperations接口及其实现GridFsTemplate，以轻松地与这个文件系统交互。

## 2. 配置

### 2.1 XML配置

让我们从GridFsTemplate的简单XML配置开始：

```xml
<bean id="gridFsTemplate" class="org.springframework.data.mongodb.gridfs.GridFsTemplate">
    <constructor-arg ref="mongoDbFactory"/>
    <constructor-arg ref="mongoConverter"/>
</bean>
```

GridFsTemplate的构造函数参数包括对mongoDbFactory的bean引用(它创建一个Mongo数据库)，以及mongoConverter(在Java和MongoDB类型之间进行转换)。它们的bean定义如下。

```xml
<mongo:db-factory id="mongoDbFactory" dbname="test" mongo-client-ref="mongoClient" />

<mongo:mapping-converter id="mongoConverter" base-package="cn.tuyucheng.taketoday.zoneddatetime.converter">
    <mongo:custom-converters base-package="cn.tuyucheng.taketoday.zoneddatetime.converter"/>
</mongo:mapping-converter>
```

### 2.2 Java配置

让我们创建一个类似的配置，只使用Java：

```java
@Configuration
@EnableMongoRepositories(basePackages = "cn.tuyucheng.taketoday")
public class MongoConfig extends AbstractMongoClientConfiguration {
    @Autowired
    private MappingMongoConverter mongoConverter;

    @Bean
    public GridFsTemplate gridFsTemplate() throws Exception {
        return new GridFsTemplate(mongoDbFactory(), mongoConverter);
    }

    // ...
}
```

对于此配置，我们使用mongoDbFactory()方法并自动注入了在父类AbstractMongoClientConfiguration中定义的MappingMongoConverter。

## 3. GridFsTemplate核心方法

### 3.1 store

store方法将文件保存到MongoDB中。

假设我们有一个空数据库并希望在其中存储一个文件：

```java
InputStream inputStream = new FileInputStream("src/main/resources/test.png"); 
gridFsTemplate.store(inputStream, "test.png", "image/png", metaData).toString();
```

请注意，我们可以通过将DBObject传递给store方法来将其他元数据与文件一起保存。对于我们的示例，DBObject可能看起来像这样：

```java
DBObject metaData = new BasicDBObject();
metaData.put("user", "alex");
```

GridFS使用两个集合来存储文件元数据及其内容。文件的元数据存储在files集合中，文件的内容存储在chunks集合中。这两个集合都以fs为前缀。

如果我们执行MongoDB命令db\['fs.files'\].find()，我们将看到fs.files集合：

```json
{
    "_id": ObjectId("5602de6e5d8bba0d6f2e45e4"),
    "metadata": {
        "user": "alex"
    },
    "filename": "test.png",
    "aliases": null,
    "chunkSize": NumberLong(261120),
    "uploadDate": ISODate("2015-09-23T17:16:30.781Z"),
    "length": NumberLong(855),
    "contentType": "image/png",
    "md5": "27c915db9aa031f1b27bb05021b695c6"
}
```

命令db\['fs.chunks'\].find()检索文件的内容：

```json
{
    "_id" : ObjectId("5602de6e5d8bba0d6f2e45e4"),
    "files_id" : ObjectId("5602de6e5d8bba0d6f2e45e4"),
    "n" : 0,
    "data" : 
    { 
        "$binary" : "/9j/4AAQSkZJRgABAQAAAQABAAD/4QAqRXhpZgAASUkqAAgAAAABADEBAgAHAAAAGgAAAAAAAABHb29nbGUAAP/bAIQAAwICAwICAwMDAwQDAwQFCAUFBAQFCgcHBggM
          CgwMCwoLCw0OEhANDhEOCwsQFhARExQVFRUMDxcYFhQYEhQVFAEDBAQGBQUJBgYKDw4MDhQUFA8RDQwMEA0QDA8VDA0NDw0MDw4MDA0ODxAMDQ0MDAwODA8MDQ4NDA0NDAwNDAwQ/8AA
          EQgAHAAcAwERAAIRAQMRAf/EABgAAQEBAQEAAAAAAAAAAAAAAAgGBwUE/8QALBAAAgEDAgQFAwUAAAAAAAAAAQIDBAURBiEABwgSIjFBYXEyUYETFEKhw
          f/EABoBAAIDAQEAAAAAAAAAAAAAAAMEAQIFBgD/xAAiEQACAgEDBAMAAAAAAAAAAAAAAQIRAyIx8BJRYYETIUH/2gAMAwEAAhEDEQA/AHDyq1Bb6GjFPMAszLkZHHCTi1I6O
          cXOFRZ1ZqoX6aqzRClkhb9MGVh2SsNyVI/hjG5389tuGcUaLK1GmFfpn5r3rnXpfV82pGtS3a0XmaGOO3zguKV1SWDwBQDH2uUWTOWMZzuM8bS0VQtJKRb2li9LL3l+4VNQPEfQTOB/WO
          G1K0JtUzwad1eZaYBiqzL4S2N8cZUsa7DqlRGdWvMq5aX6b9Tvb5pIZbggt7VcU3YacSkDbfuLNuu3lkk+98GNfIrLt2gK9K/NWl5Z87Ldebj3R0NTa2tVVKhOI0KoQ5AG4DRqSPk+gHGn
          khUPYNOx92vW9PcrdDW0FUJqOp7po5ETIYMxOdyOAK0qAvcgKPWa0oMTo7SEYDKPp98/5wPoJsx3rZ1wLhojS9iinLD9w9W47iSwVe0Z3wfrPoce2eC4I6rCX9MxrpUpWqudNunUosNLR1EkiyIGDqUKF
          fyZB+AeG80riueQdVfObC/tN1pLdaLfSxMiRQ08aIg2CjtGAB9uEyCSqSWujICUXwghT57A5+ePEoMvUdc5a3XlSsgUhZGjGM/TGAqjz+SfuT7DDmGC6WzzeyOv0+2amOrr3KylzTUwjjDeWGbJJ9/COI
          yvRFFv1iRsVGDaqYGWVsIoBZydsDhQGf/Z", 
        "$type" : "00" 
    }
}
```

### 3.2 findOne

findOne只返回一个满足指定查询条件的文档。

```java
String id = "5602de6e5d8bba0d6f2e45e4";
GridFSFile gridFsFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));
```

上面的代码将返回在上面的示例中添加的结果记录。如果数据库包含多个与查询匹配的记录，则它将只返回一个文档。返回的具体记录将根据自然顺序(文档在数据库中存储的顺序)进行选择。

### 3.3 find

find从集合中选择文档并将光标返回到所选文档。

假设我们有以下数据库，包含2条记录：

```json
[
    {
        "_id" : ObjectId("5602de6e5d8bba0d6f2e45e4"),
        "metadata" : {
            "user" : "alex"
        },
        "filename" : "test.png",
        "aliases" : null,
        "chunkSize" : NumberLong(261120),
        "uploadDate" : ISODate("2015-09-23T17:16:30.781Z"),
        "length" : NumberLong(855),
        "contentType" : "image/png",
        "md5" : "27c915db9aa031f1b27bb05021b695c6"
    },
    {
        "_id" : ObjectId("5702deyu6d8bba0d6f2e45e4"),
        "metadata" : {
            "user" : "david"
        },
        "filename" : "test.png",
        "aliases" : null,
        "chunkSize" : NumberLong(261120),
        "uploadDate" : ISODate("2015-09-23T17:16:30.781Z"),
        "length" : NumberLong(855),
        "contentType" : "image/png",
        "md5" : "27c915db9aa031f1b27bb05021b695c6"
    }
]
```

如果我们使用GridFsTemplate执行以下查询：

```java
List<GridFSFile> fileList = new ArrayList<GridFSFile>();
gridFsTemplate.find(new Query()).into(fileList);
```

由于我们没有提供任何条件，因此生成的列表应包含两条记录。

当然，我们可以为find方法提供一些条件。例如，如果我们想要获取其元数据(metadata)包含名为alex的用户的文件，则代码为：

```java
List<GridFSFile> gridFSFiles = new ArrayList<GridFSFile>();
gridFsTemplate.find(new Query(Criteria.where("metadata.user").is("alex"))).into(gridFSFiles);
```

结果列表将只包含一条记录。

### 3.4 delete

delete从集合中删除文档。

使用上一个示例中的数据库，假设我们有以下代码：

```java
String id = "5702deyu6d8bba0d6f2e45e4";
gridFsTemplate.delete(new Query(Criteria.where("_id").is(id)));
```

执行delete后，数据库中只剩下一条记录：

```json
{
    "_id" : ObjectId("5702deyu6d8bba0d6f2e45e4"),
    "metadata" : {
        "user" : "alex"
    },
    "filename" : "test.png",
    "aliases" : null,
    "chunkSize" : NumberLong(261120),
    "uploadDate" : ISODate("2015-09-23T17:16:30.781Z"),
    "length" : NumberLong(855),
    "contentType" : "image/png",
    "md5" : "27c915db9aa031f1b27bb05021b695c6"
}
```

以及chunks：

```json
{
    "_id" : ObjectId("5702deyu6d8bba0d6f2e45e4"),
    "files_id" : ObjectId("5702deyu6d8bba0d6f2e45e4"),
    "n" : 0,
    "data" : 
    { 
        "$binary" : "/9j/4AAQSkZJRgABAQAAAQABAAD/4QAqRXhpZgAASUkqAAgAAAABADEBAgAHAAAAGgAAAAAAAABHb29nbGUAAP/bAIQAAwICAwICAwMDAwQDAwQFCAUFBAQFCgcHBggM
          CgwMCwoLCw0OEhANDhEOCwsQFhARExQVFRUMDxcYFhQYEhQVFAEDBAQGBQUJBgYKDw4MDhQUFA8RDQwMEA0QDA8VDA0NDw0MDw4MDA0ODxAMDQ0MDAwODA8MDQ4NDA0NDAwNDAwQ/8AA
          EQgAHAAcAwERAAIRAQMRAf/EABgAAQEBAQEAAAAAAAAAAAAAAAgGBwUE/8QALBAAAgEDAgQFAwUAAAAAAAAAAQIDBAURBiEABwgSIjFBYXEyUYETFEKhw
          f/EABoBAAIDAQEAAAAAAAAAAAAAAAMEAQIFBgD/xAAiEQACAgEDBAMAAAAAAAAAAAAAAQIRAyIx8BJRYYETIUH/2gAMAwEAAhEDEQA/AHDyq1Bb6GjFPMAszLkZHHCTi1I6O
          cXOFRZ1ZqoX6aqzRClkhb9MGVh2SsNyVI/hjG5389tuGcUaLK1GmFfpn5r3rnXpfV82pGtS3a0XmaGOO3zguKV1SWDwBQDH2uUWTOWMZzuM8bS0VQtJKRb2li9LL3l+4VNQPEfQTOB/WO
          G1K0JtUzwad1eZaYBiqzL4S2N8cZUsa7DqlRGdWvMq5aX6b9Tvb5pIZbggt7VcU3YacSkDbfuLNuu3lkk+98GNfIrLt2gK9K/NWl5Z87Ldebj3R0NTa2tVVKhOI0KoQ5AG4DRqSPk+gHGn
          khUPYNOx92vW9PcrdDW0FUJqOp7po5ETIYMxOdyOAK0qAvcgKPWa0oMTo7SEYDKPp98/5wPoJsx3rZ1wLhojS9iinLD9w9W47iSwVe0Z3wfrPoce2eC4I6rCX9MxrpUpWqudNunUosNLR1EkiyIGDqUKF
          fyZB+AeG80riueQdVfObC/tN1pLdaLfSxMiRQ08aIg2CjtGAB9uEyCSqSWujICUXwghT57A5+ePEoMvUdc5a3XlSsgUhZGjGM/TGAqjz+SfuT7DDmGC6WzzeyOv0+2amOrr3KylzTUwjjDeWGbJJ9/COI
          yvRFFv1iRsVGDaqYGWVsIoBZydsDhQGf/Z", 
        "$type" : "00" 
    }
}
```

### 3.5 getResources

getResources返回所有具有给定文件名模式的GridFsResource。

假设我们在数据库中有如下记录：

```json
[
    {
        "_id" : ObjectId("5602de6e5d8bba0d6f2e45e4"),
        "metadata" : {
            "user" : "alex"
        },
        "filename" : "test.png",
        "aliases" : null,
        "chunkSize" : NumberLong(261120),
        "uploadDate" : ISODate("2015-09-23T17:16:30.781Z"),
        "length" : NumberLong(855),
        "contentType" : "image/png",
        "md5" : "27c915db9aa031f1b27bb05021b695c6"
    },
    {
        "_id" : ObjectId("5505de6e5d8bba0d6f8e4574"),
        "metadata" : {
            "user" : "david"
        },
        "filename" : "test.png",
        "aliases" : null,
        "chunkSize" : NumberLong(261120),
        "uploadDate" : ISODate("2015-09-23T17:16:30.781Z"),
        "length" : NumberLong(855),
        "contentType" : "image/png",
        "md5" : "27c915db9aa031f1b27bb05021b695c6"
    }, 
    {
        "_id" : ObjectId("5777de6e5d8bba0d6f8e4574"),
        "metadata" : {
            "user" : "tuyucheng"
        },
        "filename" : "tuyucheng.png",
        "aliases" : null,
        "chunkSize" : NumberLong(261120),
        "uploadDate" : ISODate("2015-09-23T17:16:30.781Z"),
        "length" : NumberLong(855),
        "contentType" : "image/png",
        "md5" : "27c915db9aa031f1b27bb05021b695c6"
    }
]
```

现在让我们使用文件模式执行getResources：

```java
GridFsResource[] gridFsResource = gridFsTemplate.getResources("test*");
```

这将返回文件名以“test”开头的两条记录(在本例中，它们都被命名为test.png)。

## 4. GridFSFile核心方法

GridFSFile API也非常简单：

-   getFilename：获取文件的文件名
-   getMetaData：获取给定文件的元数据
-   containsField：确定文档是否包含具有给定名称的字段
-   get：按名称从对象中获取一个字段
-   getId：获取文件的ObjectID
-   keySet：获取对象的字段名称

## 5. 总结

在本文中，我们了解了MongoDB的GridFS功能，以及如何使用Spring Data MongoDB与它们进行交互。
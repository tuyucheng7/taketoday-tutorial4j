## 1. 概述

在本教程中，我们将着眼于使用[MongoDB](https://www.baeldung.com/java-mongodb)中的文档 ID 执行查询操作。MongoDB 提供了一个 [查找](https://www.baeldung.com/mongodb-find)运算符来查询集合中的文档。

在本教程中，我们将首先了解在 MongoDB Shell 查询中使用文档 ID 查询文档，然后使用Java驱动程序代码。

## 2. MongoDB文档的文档ID是什么？

与任何其他数据库管理系统一样，MongoDB 要求存储在集合中的每个文档都有一个唯一标识符。此唯一标识符充当集合的主键。

在 MongoDB 中，ID 由 12 个字节组成：

-   一个 4 字节的时间戳，表示自 Unix 纪元以来以秒为单位的 ID 的创建
-   机器和进程唯一的 5 字节随机生成值
-   一个 3 字节的递增计数器

在 MongoDB 中，ID 存储在名为_id的字段中，由客户端生成。因此，应该在将文档发送到数据库之前生成 ID。在客户端，我们可以使用驱动程序生成的 ID 或生成自定义 ID。

唯一标识符存储在[ObjectId](https://www.mongodb.com/docs/manual/reference/method/ObjectId/)类中。此类提供方便的方法来获取存储在 ID 中的数据，而无需实际解析它。如果在插入文档时未指定_id ， MongoDB 将添加_id字段并为文档分配一个唯一的ObjectId 。

## 3.数据库初始化

首先，让我们建立一个新的数据库baeldung和一个样本集合vehicle：

```rust
use baeldung;
db.createCollection("vehicle");
```

此外，让我们使用insertMany 方法将一些文档添加到集合中：

```json
db.vehicle.insertMany([
{
    "companyName":"Skoda", 
    "modelName":"Octavia",
    "launchYear":2016,
    "type":"Sports",
    "registeredNo":"SKO 1134"
},
{ 
    "companyName":"BMW",
    "modelName":"X5",
    "launchYear":2020,
    "type":"SUV",
    "registeredNo":"BMW 3325"
},
{
    "companyName":"Mercedes",
    "modelName":"Maybach",
    "launchYear":2021,
    "type":"Luxury",
    "registeredNo":"MER 9754"
}]);
```

如果插入成功，上述命令将打印类似于下图的 JSON：

```json
{
    "acknowledged" : true,
    "insertedIds" : [
        ObjectId("62d01d17cdd1b7c8a5f945b9"),
        ObjectId("62d01d17cdd1b7c8a5f945ba"),
        ObjectId("62d01d17cdd1b7c8a5f945bb")
    ]
}
```

我们已经成功地建立了数据库和集合。我们将为所有示例使用此数据库和集合。

## 4. 使用 MongoDB 外壳

我们将使用db.collection.find(query, projection)方法从 MongoDB 查询文档。

首先，让我们编写一个返回所有车辆集合文档的查询：

```matlab
db.vehicle.find({});
```

上面的查询返回所有文档：

```json
{ "_id" : ObjectId("62d01d17cdd1b7c8a5f945b9"), "companyName" : "Skoda",
    "modelName" : "Octavia", "launchYear" : 2016, "type" : "Sports", "registeredNo" : "SKO 1134" }
{ "_id" : ObjectId("62d01d17cdd1b7c8a5f945ba"), "companyName" : "BMW",
    "modelName" : "X5", "launchYear" : 2020, "type" : "SUV", "registeredNo" : "BMW 3325" }
{ "_id" : ObjectId("62d01d17cdd1b7c8a5f945bb"), "companyName" : "Mercedes",
    "modelName" : "Maybach", "launchYear" : 2021, "type" : "Luxury", "registeredNo" : "MER 9754" }
```

此外，让我们编写一个查询，使用上面结果中返回的 ID 来获取车辆集合文档：

```matlab
db.vehicle.find(
{
    "_id": ObjectId("62d01d17cdd1b7c8a5f945b9")
});
```

上面的查询返回_id等于ObjectId(“62d01d17cdd1b7c8a5f945b9”)的车辆集合文档：

```json
{ "_id" : ObjectId("62d01d17cdd1b7c8a5f945b9"), "companyName" : "Skoda",
    "modelName" : "Octavia", "launchYear" : 2016, "type" : "Sports", "registeredNo" : "SKO 1134" }
```

此外，我们可以使用带有in查询运算符的 ID 检索多个车辆集合文档：

```matlab
db.vehicle.find(
{
    "_id": {
        $in: [
            ObjectId("62d01d17cdd1b7c8a5f945b9"),
            ObjectId("62d01d17cdd1b7c8a5f945ba"),
            ObjectId("62d01d17cdd1b7c8a5f945bb")
        ]
    }
});
```

上述查询返回in运算符中查询 ID 的所有车辆集合文档：

```json
{ "_id" : ObjectId("62d01d17cdd1b7c8a5f945b9"), "companyName" : "Skoda",
    "modelName" : "Octavia", "launchYear" : 2016, "type" : "Sports", "registeredNo" : "SKO 1134" }
{ "_id" : ObjectId("62d01d17cdd1b7c8a5f945ba"), "companyName" : "BMW",
    "modelName" : "X5", "launchYear" : 2020, "type" : "SUV", "registeredNo" : "BMW 3325" }
{ "_id" : ObjectId("62d01d17cdd1b7c8a5f945bb"), "companyName" : "Mercedes",
    "modelName" : "Maybach", "launchYear" : 2021, "type" : "Luxury", "registeredNo" : "MER 9754" }
```

同样，任何[查询运算符](https://www.mongodb.com/docs/manual/reference/operator/query/)都可以用作具有要查询 ID的find()方法的过滤器。

此外，请务必注意，在使用_id字段查询文档时，文档 ID 字符串值应指定为ObjectId()而不是String。

让我们尝试查询一个 ID 为字符串值的现有文档：

```matlab
db.vehicle.find(
{
    "_id": "62d01d17cdd1b7c8a5f945b9"
});
```

不幸的是，上述查询不会返回任何文档，因为不存在 ID 为字符串值62d01d17cdd1b7c8a5f945b9的任何文档。

## 5. 使用Java驱动程序

到目前为止，我们已经学习了如何在 MongoDB Shell 中使用 ID 查询文档。现在让我们使用 MongoDBJava驱动程序实现相同的功能。

在执行更新操作之前，让我们先连接到 baeldung数据库中的车辆 集合 ：

```java
MongoClient mongoClient = new MongoClient("localhost", 27017);
MongoDatabase database = mongoClient.getDatabase("baeldung");
MongoCollection<Document> collection = database.getCollection("vehicle");
```

在这种情况下，我们连接到 MongoDB，它在本地主机上的默认端口 27017 上运行。

首先，让我们编写代码来使用ID查询文档：

```java
Bson filter = Filters.eq("_id", new ObjectId("62d01d17cdd1b7c8a5f945b9"));
FindIterable<Document> documents = collection.find(filter);

MongoCursor<Document> cursor = documents.iterator();
while (cursor.hasNext()) {
    System.out.println(cursor.next());
}
```

在这里，我们将一个Bson过滤器作为参数传递给带有_id字段的find()方法进行查询。上面的代码片段将返回车辆集合文档，其中_id等于ObjectId(“62d01d17cdd1b7c8a5f945b9”)。

此外，让我们编写一个片段来查询具有多个 ID 的文档：

```java
Bson filter = Filters.in("_id", new ObjectId("62d01d17cdd1b7c8a5f945b9"),
  new ObjectId("62d01d17cdd1b7c8a5f945ba"));
FindIterable<Document> documents = collection.find(filter);

MongoCursor<Document> cursor = documents.iterator();
while (cursor.hasNext()) {
    System.out.println(cursor.next());
}
```

上面的查询返回查询 ID 的所有车辆集合文档。

最后，让我们尝试使用驾驶员生成的 ID查询车辆集合：

```java
Bson filter = Filters.eq("_id", new ObjectId());
FindIterable<Document> documents = collection.find(filter);

MongoCursor<Document> cursor = documents.iterator();
while (cursor.hasNext()) {
    System.out.println(cursor.next());
}
```

上述查询不会返回任何文档，因为车辆集合中不存在具有新生成的 ID 的文档。

## 六. 总结

在本文中，我们学习了在 MongoDB 中使用文档 ID 查询文档。首先，我们在 MongoDB Shell 查询中研究了这些用例，然后我们讨论了相应的Java驱动程序代码。
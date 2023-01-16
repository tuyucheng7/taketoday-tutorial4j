## 1. 概述

在本教程中，我们将学习如何通过键名从[MongoDB中检索值。](https://www.mongodb.com/)我们将探索 MongoDB 的各种方法，以根据应用的过滤器获取文档的关键字段名称。首先，我们将使用[find](https://www.mongodb.com/docs/manual/reference/method/db.collection.find/)或findone方法来获取所需的数据，然后使用[聚合](https://www.mongodb.com/docs/manual/aggregation/)方法。在这里，我们将在 MongoDB shell 查询和Java驱动程序代码中编写查询。

让我们看一下通过字段名在 MongoDB 中检索值的不同方法。

## 2.数据库初始化

首先，我们需要建立一个新的数据库baeldung和一个新的集合travel：

```json
use baeldung;
db.createCollection(travel);
```

现在让我们使用 MongoDB 的insertMany方法将一些虚拟数据添加到集合中：

```json
db.travel.insertMany([
{ 
    "passengerId":145,
    "passengerName":"Nathan Green",
    "passengerAge":25,
    "sourceStation":"London",
    "destinationStation":"Birmingham",
    "seatType":"Slepper",
    "emailAddress":"nathongreen12@gmail.com"
},
{ 
    "passengerId":148,
    "passengerName":"Kevin Joseph",
    "passengerAge":28,
    "sourceStation":"Manchester",
    "destinationStation":"London",
    "seatType":"Slepper",
    "emailAddress":"kevin13@gmail.com"
},
{ 
    "passengerId":154,
    "passengerName":"Sheldon burns",
    "passengerAge":26,
    "sourceStation":"Cambridge",
    "destinationStation":"Leeds",
    "seatType":"Slepper",
    "emailAddress":"sheldonnn160@gmail.com"
},
{ 
    "passengerId":168,
    "passengerName":"Jack Ferguson",
    "passengerAge":24,
    "sourceStation":"Cardiff",
    "destinationStation":"Coventry",
    "seatType":"Slepper",
    "emailAddress":"jackfergusion9890@gmail.com"
}
]);
```

上面的insertMany查询将返回以下 JSON：

```json
{
    "acknowledged" : true,
    "insertedIds" : [
        ObjectId("623d7f079d55d4e137e47825"),
	ObjectId("623d7f079d55d4e137e47826"),
	ObjectId("623d7f079d55d4e137e47827"),
        ObjectId("623d7f079d55d4e137e47828")
    ]
}
```

到现在为止，我们已经将虚拟数据插入到 collection travel中。

## 3.使用查找方法

find方法查找并返回与集合中指定查询条件匹配的文档。如果有多个文档符合条件，那么它将根据文档在磁盘上的顺序返回所有文档。此外，在 MongoDB 中，find方法支持查询中的参数投影。如果我们在find方法中指定投影参数，它将返回所有仅包含投影字段的文档。

需要注意的一个关键点是，除非明确删除，否则_id字段始终包含在响应中。

为了演示，让我们看一下 shell 查询以投影一个关键字段：

```bash
db.travel.find({},{"passengerId":1}).pretty();
```

对上述查询的响应将是：

```json
{ "_id" : ObjectId("623d7f079d55d4e137e47825"), "passengerId" : 145 }
{ "_id" : ObjectId("623d7f079d55d4e137e47826"), "passengerId" : 148 }
{ "_id" : ObjectId("623d7f079d55d4e137e47827"), "passengerId" : 154 }
{ "_id" : ObjectId("623d7f079d55d4e137e47828"), "passengerId" : 168 }
```

在这里，在此查询中，我们只是投影了passengerId。现在让我们看看排除_id的关键字段：

```bash
db.travel.find({},{"passengerId":1,"_id":0}).pretty();
```

上面的查询将有以下响应：

```bash
{ "passengerId" : 145 }
{ "passengerId" : 148 }
{ "passengerId" : 154 }
{ "passengerId" : 168 }
```

在这里，在此查询中，我们从响应投影中排除了_id字段。让我们看看上述查询的Java驱动程序代码：

```java
MongoClient mongoClient = new MongoClient("localhost", 27017);
DB database = mongoClient.getDB("baeldung");
DBCollection collection = database.getCollection("travel");
BasicDBObject queryFilter = new BasicDBObject();
BasicDBObject projection = new BasicDBObject();
projection.put("passengerId", 1);
projection.put("_id", 0);
DBCursor dbCursor = collection.find(queryFilter, projection);
while (dbCursor.hasNext()) {
    System.out.println(dbCursor.next());
}
```

在上面的代码中，首先，我们创建了一个MongoClient与运行在端口27017上的本地 mongo 服务器的连接。接下来，我们使用了find方法，它有两个参数，queryFilter和 projection。查询DBObject包含我们需要获取数据的过滤器。在这里，我们使用DBCursor打印旅行证件的所有预计字段。

## 4.使用 聚合方法

MongoDB 中的聚合操作处理数据记录和文档并返回计算结果。它从各种文档中收集值并将它们组合在一起，然后对分组数据执行不同类型的操作，例如求和、平均值、最小值、最大值等。

当我们需要做更复杂的聚合时，我们可以使用 MongoDB 聚合管道。聚合管道是阶段的集合，结合 MongoDB 查询语法以产生聚合结果。

让我们看看聚合查询以通过键名检索值：

```json
db.travel.aggregate([
{
    "$project":{
        "passengerId":1
    }
}
]).pretty();
```

对上述聚合查询的响应将是：

```json
{ "_id" : ObjectId("623d7f079d55d4e137e47825"), "passengerId" : 145 }
{ "_id" : ObjectId("623d7f079d55d4e137e47826"), "passengerId" : 148 }
{ "_id" : ObjectId("623d7f079d55d4e137e47827"), "passengerId" : 154 }
{ "_id" : ObjectId("623d7f079d55d4e137e47828"), "passengerId" : 168 }
```

在本例中，我们使用了聚合管道的$project阶段。$project指定要包含或排除的字段。在我们的查询中，我们只将 passengerId 传递到投影阶段。

让我们看一下上述查询的Java驱动程序代码：

```java
ArrayList<Document> response = new ArrayList<>();
ArrayList<Document> pipeline = new ArrayList<>(Arrays.asList(new Document("$project", new Document("passengerId", 1L))));
database = mongoClient.getDatabase("baeldung");
database.getCollection("travel").aggregate(pipeline).allowDiskUse(true).into(response);
System.out.println("response:- " + response);
```

我们也可以这样写聚合管道：

```java
ArrayList<Document> response = new ArrayList<>();
ArrayList<Bson> pipeline = new ArrayList<>(Arrays.asList(
  project(fields(Projections.exclude("_id"), Projections.include("passengerId")))));
MongoDatabase database = mongoClient.getDatabase("baeldung");
database.getCollection("travel").aggregate(pipeline).allowDiskUse(true).into(response);
System.out.println("response:- "+response);
```

我们使用 java 驱动程序代码创建了一个聚合管道，并将项目阶段设置为仅包含passengerId字段。最后，我们将聚合管道传递给聚合方法以检索数据。

## 5.总结

在本文中，我们学习了在 MongoDB 中通过键名检索值。我们探索了 MongoDB 获取数据的不同方法。首先，我们使用find方法检索数据，然后使用aggregate方法检索数据。我们研究了 MongoDB 中字段投影的用例。简而言之，我们使用 Mongo shell 查询和Java驱动程序代码实现了字段的投影。
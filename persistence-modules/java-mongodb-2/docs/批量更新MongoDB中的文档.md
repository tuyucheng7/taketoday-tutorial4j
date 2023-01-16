## 1. 概述

在本教程中，我们将着眼于在[MongoDB](https://www.mongodb.com/)中执行批量更新和插入操作。此外，MongoDB 提供 API 调用，允许在单个操作中插入或检索多个文档。MongoDB 使用Array或Batch接口，通过减少客户端和数据库之间的调用次数，大大提高了数据库性能。

在本教程中，我们将研究使用 MongoDB Shell 和Java驱动程序代码的解决方案。

让我们深入了解如何在 MongoDB 中实现文档的批量更新。

## 2.数据库初始化

首先，我们需要连接到 mongo shell：

```json
mongo --host localhost --port 27017
```

现在，建立一个数据库baeldung和一个样本集合populations：

```json
use baeldung;
db.createCollection(populations);
```

让我们使用insertMany方法将一些示例数据添加到集合中：

```json
db.populations.insertMany([
{
    "cityId":1124,
    "cityName":"New York",
    "countryName":"United States",
    "continentName":"North America",
    "population":22
},
{
    "cityId":1125,
    "cityName":"Mexico City",
    "countryName":"Mexico",
    "continentName":"North America",
    "population":25
},
{
    "cityId":1126,
    "cityName":"New Delhi",
    "countryName":"India",
    "continentName":"Asia",
    "population":45
},
{
    "cityId":1134,
    "cityName":"London",
    "countryName":"England",
    "continentName":"Europe",
    "population":32
}]);
```

上面的insertMany查询将返回以下文档：

```json
{
    "acknowledged" : true,
    "insertedIds" : [
        ObjectId("623575049d55d4e137e477f6"),
        ObjectId("623575049d55d4e137e477f7"),
        ObjectId("623575049d55d4e137e477f8"),
        ObjectId("623575049d55d4e137e477f9")
    ]
}
```

在这里，我们在上述查询中插入了四个文档，以在 MongoDB 中执行所有类型的写入批量操作。

数据库baeldung已成功创建，所有需要的数据也已插入到集合populations中，因此我们已准备好执行批量更新。

## 3.使用MongoDB Shell查询

MongoDB 的批量操作构建器用于为单个集合批量构建写入操作列表。我们可以用两种不同的方式初始化批量操作。initializeOrderedBulkOp方法用于在写操作的有序列表中执行批量操作。initializeOrderedBulkOp的缺点之一是如果在处理任何写操作时发生错误，MongoDB 将返回而不处理列表中剩余的写操作。

我们可以使用插入、更新、替换和删除方法在单个数据库调用中执行不同类型的操作。作为说明，让我们看看使用 MongoDB shell 的批量写入操作查询：

```json
db.populations.bulkWrite([
    { 
        insertOne :
            { 
                "document" :
                    {
                        "cityId":1128,
                        "cityName":"Kathmandu",
                        "countryName":"Nepal",
                        "continentName":"Asia",
                        "population":12
                    }
            }
    },
    { 
        insertOne :
            { 
                "document" :
                    {
                        "cityId":1130,
                        "cityName":"Mumbai",
                        "countryName":"India",
                        "continentName":"Asia",
                        "population":55
                    }
            }
    },
    { 
        updateOne :
            { 
                "filter" : 
                     { 
                         "cityName": "New Delhi"
                     },
                 "update" : 
                     { 
                         $set : 
                         { 
                             "status" : "High Population"
                         } 
                     }
            }
    },
    { 
        updateMany :
            { 
                "filter" : 
                     { 
                         "cityName": "London"
                     },
                 "update" : 
                     { 
                         $set : 
                         { 
                             "status" : "Low Population"
                         } 
                     }
            }
    },
    { 
        deleteOne :
            { 
                "filter" : 
                    { 
                        "cityName":"Mexico City"
                    } 
            }
    },
    { 
        replaceOne :
            {
                "filter" : 
                    { 
                        "cityName":"New York"
                    },
                 "replacement" : 
                    {
                        "cityId":1124,
                        "cityName":"New York",
                        "countryName":"United States",
                        "continentName":"North America",
                        "population":28
                    }
             }
    }
]);
```

上面的bulkWrite查询将返回以下文档：

```json
{
    "acknowledged" : true,
    "deletedCount" : 1,
    "insertedCount" : 2,
    "matchedCount" : 3,
    "upsertedCount" : 0,
    "insertedIds" : 
        {
            "0" : ObjectId("623575f89d55d4e137e477f9"),
            "1" : ObjectId("623575f89d55d4e137e477fa")
        },
    "upsertedIds" : {}
}
```

在这里，在上面的查询中，我们执行了所有类型的写操作，即insertOne、updateOne、deleteOne、replaceOne。

首先，我们使用insertOne方法向集合中插入一个新文档。其次，我们使用updateOne更新城市名称“New Delhi”的文档。后来，我们使用deleteOne方法根据过滤器从集合中删除了一个文档。最后，我们使用replaceOne 替换了带有过滤器cityName “New York” 的完整文档。

## 4.使用Java驱动程序

我们已经讨论了执行批量写入操作的 MongoDB shell 查询。在创建批量写入操作之前，让我们首先创建一个与数据库baeldung的集合populations的MongoClient连接：

```java
MongoClient mongoClient = new MongoClient("localhost", 27017);
MongoDatabase database = mongoClient.getDatabase("baeldung");
MongoCollection<Document> collection = database.getCollection("populations");
```

在这里，我们创建了与运行在默认端口 27017 上的 MongoDB 服务器的连接。现在让我们使用Java代码实现相同的批量操作：

```java
List<WriteModel<Document>> writeOperations = new ArrayList<WriteModel<Document>>();
writeOperations.add(new InsertOneModel<Document>(new Document("cityId", 1128)
  .append("cityName", "Kathmandu")
  .append("countryName", "Nepal")
  .append("continentName", "Asia")
  .append("population", 12)));
writeOperations.add(new InsertOneModel<Document>(new Document("cityId", 1130)
  .append("cityName", "Mumbai")
  .append("countryName", "India")
  .append("continentName", "Asia")
  .append("population", 55)));
writeOperations.add(new UpdateOneModel<Document>(new Document("cityName", "New Delhi"),
  new Document("$set", new Document("status", "High Population"))
));
writeOperations.add(new UpdateManyModel<Document>(new Document("cityName", "London"),
  new Document("$set", new Document("status", "Low Population"))
));
writeOperations.add(new DeleteOneModel<Document>(new Document("cityName", "Mexico City")));
writeOperations.add(new ReplaceOneModel<Document>(new Document("cityId", 1124), 
  new Document("cityName", "New York").append("cityName", "United States")
    .append("continentName", "North America")
    .append("population", 28)));
BulkWriteResult bulkWriteResult = collection.bulkWrite(writeOperations);
System.out.println("bulkWriteResult:- " + bulkWriteResult);
```

在这里，我们首先创建了一个writeModel列表，将所有不同类型的写操作添加到一个更新列表中。此外，我们在查询中使用了InsertOneModel、UpdateOneModel、UpdateManyModel、DeleteOneModel和ReplaceOneModel。最后，bulkWrite方法一次执行了所有操作。

## 5.总结

在本文中，我们学习了使用不同类型的写操作在 MongoDB 中执行批量操作。我们在单个数据库查询中执行了文档的插入、更新、删除和替换。此外，我们了解了initializeOrderedBulkOp 在 MongoDB 中批量更新的用例。

首先，我们研究了 MongoDB shell 查询中批量操作的用例，然后我们讨论了相应的Java驱动程序代码。
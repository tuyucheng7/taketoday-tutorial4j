## 1. 概述

在本教程中，我们将介绍如何将文档插入到[MongoDB](https://www.mongodb.com/)中的数组中。此外，我们将看到$push 和 $addToset运算符的各种应用，以将值添加到数组中。

首先，我们将创建一个示例数据库、一个集合，并将虚拟数据插入其中。此外，我们将研究一些使用$push运算符更新文档的基本示例。稍后，我们还将讨论$push和$addtoSet运算符的各种用例。

让我们深入探讨在 MongoDB 中将文档插入数组的各种方法。

## 2.数据库初始化

首先，让我们设置一个新的数据库baeldung和一个示例集合orders：

```json
use baeldung;
db.createCollection(orders);
```

现在让我们使用insertMany方法将一些文档添加到集合中：

```json
db.orders.insertMany([
    {
        "customerId": 1023,
        "orderTimestamp": NumberLong("1646460073000"),
        "shippingDestination": "336, Street No.1 Pawai Mumbai",
        "purchaseOrder": 1000,
        "contactNumber":"9898987676",
        "items": [ 
            {
                "itemName": "BERGER",
                "quantity": 1,
                "price": 500
            },
            {
                "itemName": "VEG PIZZA",
                "quantity": 1,
                "price": 800
            } 
          ]
    },
    {
        "customerId": 1027,
        "orderTimestamp": NumberLong("1646460087000"),
        "shippingDestination": "445, Street No.2 Pawai Mumbai",
        "purchaseOrder": 2000,
        "contactNumber":"9898987676",
        "items": [ 
            {
               "itemName": "BERGER",
               "quantity": 1,
               "price": 500
            },
            {
               "itemName": "NON-VEG PIZZA",
               "quantity": 1,
               "price": 1200
            } 
          ]
    }
]);
```

如果插入成功，上述命令将打印类似于下图的 JSON：

```json
{
    "acknowledged" : true,
    "insertedIds" : [
        ObjectId("622300cc85e943405d04b567"),
	ObjectId("622300cc85e943405d04b568")
    ]
}
```

到现在为止，我们已经成功地建立了数据库和集合。我们将为所有示例使用此数据库和集合。

## 3.使用Mongo Query进行推送操作

MongoDB 提供了多种类型的数组运算符来更新 MongoDB 文档中的数组。 MongoDB 中的[$push](https://docs.mongodb.com/manual/reference/operator/update/push/)运算符将值附加到数组的末尾。根据查询的类型，我们可以将$push运算符与updateOne、updateMany、findAndModify等方法一起使用。

现在让我们看看使用$push运算符的 shell 查询：

```json
db.orders.updateOne(
    {
        "customerId": 1023
    },
    {
        $push: {
            "items":{
                "itemName": "PIZZA MANIA",
                "quantity": 1,
                "price": 800
            }
        }
    });
```

上面的查询将返回以下文档：

```json
{
    "acknowledged":true,
    "matchedCount":1,
    "modifiedCount":1
}
```

现在让我们检查customerId 为1023 的文档。在这里，我们可以看到新项目被插入到列表“ items ”的末尾：

```json
{
    "customerId" : 1023,
    "orderTimestamp" : NumberLong("1646460073000"),
    "shippingDestination" : "336, Street No.1 Pawai Mumbai",
    "purchaseOrder" : 1000,
    "contactNumber" : "9898987676",
    "items" : [
        {
            "itemName" : "BERGER",
            "quantity" : 1,
	    "price" : 500
        },
	{
            "itemName" : "VEG PIZZA",
	    "quantity" : 1,
	    "price" : 800
	},
	{
	    "itemName" : "PIZZA MANIA",
	    "quantity" : 1,
	    "price" : 800
        }
    ]
}
```

## 4.使用Java驱动代码进行推送操作

到目前为止，我们已经讨论了将文档推送到数组中的 MongoDB shell 查询。现在让我们使用Java代码实现推送更新查询。

在执行更新操作之前，让我们先连接到 baeldung数据库中的订单集合：

```java
mongoClient = new MongoClient("localhost", 27017);
MongoDatabase database = mongoClient.getDatabase("baeldung");
MongoCollection<Document> collection = database.getCollection("orders");
```

在这种情况下，我们连接到在本地主机上的默认端口 27017 上运行的 MongoDB。

### 4.1. 使用 数据库对象

[MongoDBJava驱动程序](https://www.baeldung.com/java-mongodb)提供了对DBObject和BSON文档的支持。在这里，DBObject是 MongoDB 遗留驱动程序的一部分，但在新版本的 MongoDB 中已弃用。

现在让我们看一下Java驱动程序代码，以将新值插入到数组中：

```java
DBObject listItem = new BasicDBObject("items", new BasicDBObject("itemName", "PIZZA MANIA")
  .append("quantity", 1)
  .append("price", 800));
BasicDBObject searchFilter = new BasicDBObject("customerId", 1023);
BasicDBObject updateQuery = new BasicDBObject();
updateQuery.append("$push", listItem);
UpdateResult updateResult = collection.updateOne(searchFilter, updateQuery);
```

在上面的查询中，我们首先使用BasicDBObject创建了项目文档。在searchQuery的基础上，对集合的文档进行过滤，将值压入数组。

### 4.2. 使用 BSON文档

[BSON](https://www.baeldung.com/mongodb-bson)文档是访问Java中使用较新的客户端堆栈构建的 MongoDB 文档的新方法。org.bson.Document类不 那么复杂也更容易使用。

让我们使用org.bson.Document 类将值推送到数组“ items”中：

```java
Document item = new Document()
  .append("itemName1", "PIZZA MANIA")
  .append("quantity", 1).append("price", 800);
UpdateResult updateResult = collection.updateOne(Filters.eq("customerId", 1023), Updates.push("items", item));
```

在这种情况下，BSON的实现类似于使用DBObject 运行的代码，更新也会相同。在这里，我们使用updateOne方法仅更新单个文档。

## 5. 使用 addToSet操作符

[$addToSet](https://docs.mongodb.com/manual/reference/operator/update/addToSet/)运算符也可用于将值推送到数组中。仅当数组中不存在该值时，此运算符才添加值。否则，它只会忽略它。而 push 运算符会将值作为条件推送到 filter 中以获取匹配项。

需要注意的一个关键点是$addToSet运算符不会在重复项的情况下推动值工作。另一方面，$push 运算符只是将值推入数组，而不管任何其他条件。

### 5.1. 使用addToSet运算符的 Shell 查询 

$addToSet运算符的 mongo shell 查询类似于$push运算符，但$addToSet不会在数组中插入重复值。

现在让我们检查 MongoDB 查询以使用$addToset将值推送到数组中：

```json
db.orders.updateOne(
    {
        "customerId": 1023
    },
    {
        $addToSet: {
            "items":{
                "itemName": "PASTA",
                "quantity": 1,
                "price": 1000
            }
        }
    });
```

在这种情况下，输出将如下所示：

```json
{
    "acknowledged":true,
    "matchedCount":1,
    "modifiedCount":1
}

```

在这种情况下，我们使用了$addToSet运算符，只有当文档是唯一的时，它才会被推送到数组“items”。

### 5.2. 使用addToSet运算符的Java驱动程序 

与 push 运算符相比， $ addToSet运算符提供了一种不同类型的数组更新操作：

```java
Document item = new Document()
  .append("itemName1", "PIZZA MANIA")
  .append("quantity", 1).append("price", 800);
UpdateResult updateResult = collection
  .updateOne(Filters.eq("customerId", 1023), Updates.addToSet("items", item));
System.out.println("updateResult:- " + updateResult);
```

在上面的代码中，首先，我们创建了文档“ item ”，在customerId过滤器的基础上，updateOne方法会尝试将文档“ item ”压入数组“ items ”。

## 六. 总结

在本文中，我们学习了使用$push 和 $addToSet运算符将新值推送到数组中。首先，我们研究了$push运算符在 MongoDB shell 查询中的使用，然后我们讨论了相应的Java驱动程序代码。
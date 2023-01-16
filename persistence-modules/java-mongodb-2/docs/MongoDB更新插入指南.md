## 1. 概述

Upsert 是插入和更新的组合(inSERT + UPdate = upsert)。我们可以将upsert与不同的更新方法一起使用，即update、findAndModify和replaceOne。

在[MongoDB 中，](https://www.mongodb.com/) upsert 选项是一个布尔值。假设值为true并且文档匹配指定的查询过滤器。在这种情况下，应用的更新操作将更新文档。如果值为true并且没有文档匹配条件，则此选项会将新文档插入集合中。新文档将包含基于过滤器和应用操作的字段。

在本教程中，我们将首先了解 MongoDB Shell 查询中的upsert，然后使用Java驱动程序代码。

## 2.数据库初始化

在我们继续执行更新插入操作之前，首先我们需要设置一个新的数据库baeldung 和一个样本集合 vehicle：

```json
db.vehicle.insertMany([
{
    "companyName":"Nissan", 
    "modelName":"GTR",
    "launchYear":2016,
    "type":"Sports",
    "registeredNo":"EPS 5561"
},
{ 
    "companyName":"BMW",
    "modelName":"X5",
    "launchYear":2020,
    "type":"SUV",
    "registeredNo":"LLS 6899"
},
{
    "companyName":"Honda",
    "modelName":"Gold Wing",
    "launchYear":2018,
    "type":"Bike",
    "registeredNo":"LKS 2477"
}]);
```

如果插入成功，上述命令将打印类似于下图的 JSON：

```json
{
    "acknowledged" : true, 
    "insertedIds" : [
        ObjectId("623c1db39d55d4e137e4781b"),
	ObjectId("623c1db39d55d4e137e4781c"),
	ObjectId("623c1db39d55d4e137e4781d")
    ]
}
```

我们已经成功地将虚拟数据添加到收集工具中。

## 3.使用 更新方法

在本节中，我们将学习将upsert选项与更新方法一起使用。upsert选项的主要目的是根据应用的过滤器更新现有文档，或者在过滤器未匹配时插入新文档。

作为示例，我们将使用带有upsert选项的$setOnInsert运算符以获得将新字段插入文档的额外优势。

让我们检查一个查询，其中过滤条件与集合的现有文档相匹配：

```json
db.vehicle.update(
{
    "modelName":"X5"
},
{
    "$set":{
        "companyName":"Hero Honda"
    }
},
{
    "upsert":true
});
```

上面的查询将返回以下文档：

```json
{ 
    "nMatched" : 1, 
    "nUpserted" : 0,
    "nModified" : 1 
}
```

在这里，我们将看到对应于上述 mongo shell 查询的Java驱动程序代码：

```java
UpdateOptions options = new UpdateOptions().upsert(true);
UpdateResult updateResult = collection.updateOne(Filters.eq("modelName", "X5"), 
  Updates.combine(Updates.set("companyName", "Hero Honda")), options);
System.out.println("updateResult:- " + updateResult);
```

在上面的查询中，集合中已经存在字段modelName “X5”，因此该文档的字段companyName将更新为“Hero Honda”。

现在让我们检查一个使用 $ setOnInsert运算符的upsert选项的示例。它仅适用于添加新文档的情况：

```json
db.vehicle.update(
{
    "modelName":"GTPR"
},
{
    "$set":{
        "companyName":"Hero Honda"
    },
    "$setOnInsert":{
        "launchYear" : 2022,
	"type" : "Bike",
	"registeredNo" : "EPS 5562"
    },  
},
{
    "upsert":true
});
```

上面的查询将返回以下文档：

```json
{
    "nMatched" : 0,
    "nUpserted" : 1,
    "nModified" : 0,
    "_id" : ObjectId("623b378ed648af670fe50e7f")
}
```

上面带有$setOnInsert选项的更新查询的Java驱动程序代码将是：

```java
UpdateResult updateSetOnInsertResult = collection.updateOne(Filters.eq("modelName", "GTPR"),
  Updates.combine(Updates.set("companyName", "Hero Honda"),
  Updates.setOnInsert("launchYear", 2022),
  Updates.setOnInsert("type", "Bike"),
  Updates.setOnInsert("registeredNo", "EPS 5562")), options);
System.out.println("updateSetOnInsertResult:- " + updateSetOnInsertResult);
```

这里，在上面的查询中，字段modelName “GTPR” 的过滤条件没有匹配到任何集合文档，所以我们将添加一个新文档到集合中。需要注意的关键点是$setOnInsert将所有字段添加到新文档中。

## 4. 使用findAndModify方法

我们还可以使用findAndModify方法使用upsert选项 。对于此方法，upsert选项的默认值为false。如果我们将upsert选项设置为true，它将执行与更新方法完全相同的操作。

让我们检查一下findAndModify方法的用例，其中更新插入选项为true：

```json
db.vehicle.findAndModify(
{
    query:{
        "modelName":"X7"
    },
    update: {
        "$set":{
            "companyName":"Hero Honda"
        }
    },
    "upsert":true,
    "new":true
});
```

在这种情况下，上述查询将返回新创建的文档。让我们检查一下上述查询的Java驱动程序代码：

```java
FindOneAndUpdateOptions upsertOptions = new FindOneAndUpdateOptions();
  upsertOptions.returnDocument(ReturnDocument.AFTER);
  upsertOptions.upsert(true);
Document resultDocument = collection.findOneAndUpdate(Filters.eq("modelName", "X7"),
  Updates.set("companyName", "Hero Honda"), upsertOptions);
System.out.println("resultDocument:- " + resultDocument);
```

在这里，我们首先创建了过滤条件，然后我们将更新现有文档或将新文档添加到集合vehicle中。

## 5. 使用replaceOne方法

让我们使用replaceOne方法执行更新插入操作。如果条件匹配，MongoDB的replaceOne方法只是替换集合中的单个文档。

首先，让我们看一下 replace 方法的 Mongo shell 查询：

```json
db.vehicle.replaceOne(
{
    "modelName":"GTPR"
},
{
    "modelName" : "GTPR",
    "companyName" : "Hero Honda",
    "launchYear" : 2022,
    "type" : "Bike",
    "registeredNo" : "EPS 5562"
},
{
    "upsert":true
});
```

上面的查询将返回以下响应：

```json
{ 
    "acknowledged" : true, 
    "matchedCount" : 1,
    "modifiedCount" : 1 
}
```

现在，让我们使用Java驱动程序代码编写上述查询：

```java
Document replaceDocument = new Document();
replaceDocument.append("modelName", "GTPP")
  .append("companyName", "Hero Honda")
  .append("launchYear", 2022)
  .append("type", "Bike")
  .append("registeredNo", "EPS 5562");
UpdateResult updateReplaceResult = collection.replaceOne(Filters.eq("modelName", "GTPP"), replaceDocument, options);
System.out.println("updateReplaceResult:- " + updateReplaceResult);
```

在这种情况下，我们首先需要创建一个新文档来替换现有文档，并且使用upsert选项true，只有在条件匹配时我们才会替换文档。

## 六. 总结

在本文中，我们了解了如何使用 MongoDB 的各种更新方法执行更新插入操作。首先，我们学习了使用update和findAndModify方法执行更新插入，然后使用 replaceOne方法。简而言之，我们使用 Mongo shell 查询和Java驱动程序代码实现了 upsert 操作。
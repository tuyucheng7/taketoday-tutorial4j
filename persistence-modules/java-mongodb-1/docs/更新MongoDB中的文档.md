## 1. 概述

[MongoDB](https://www.mongodb.com/) 是一个跨平台、面向文档的开源 NoSQL 数据库，用 C++ 编写。此外，MongoDB 还提供高性能、高可用性和自动缩放。

为了更新 MongoDB 中的文档，我们可以使用不同的方法，如 updateOne、findOneAndUpdate等。此外，MongoDB 为更新方法提供了各种运算符。

在本教程中，我们将讨论在 MongoDB 中执行更新操作的不同方法。对于每种方法，我们将首先讨论 mongo shell 查询，然后讨论它在Java中的实现。

## 2.数据库设置

在我们继续更新查询之前，让我们首先创建一个数据库baeldung和一个示例集合student：

```bash
use baeldung;
db.createCollection(student);
```

作为示例，让我们使用insertMany查询将一些文档添加到集合student中：

```json
db.student.insertMany([
    {
        "student_id": 8764,
        "student_name": "Paul Starc",
        "address": "Hostel 1",
        "age": 16,
        "roll_no":199406
    },
    {
        "student_id": 8765,
        "student_name": "Andrew Boult",
        "address": "Hostel 2",
        "age": 18,
        "roll_no":199408
    }
]);
```

成功插入后，我们将获得带有acknowledge:true的 JSON ：

```json
{
    "acknowledged" : true,
    "insertedIds" : [
        ObjectId("621b078485e943405d04b557"),
	ObjectId("621b078485e943405d04b558")
    ]
}
```

现在让我们深入探讨更新 MongoDB 中文档的不同方法。

## 3.使用updateOne方法

MongoDB 中的更新操作可以通过添加新字段、删除字段或更新现有字段来完成。[updateOne](https://docs.mongodb.com/manual/reference/method/db.collection.updateOne/) 方法根据 应用的查询过滤器更新集合中的单个文档。它首先找到与过滤器匹配的文档，然后更新指定的字段。

此外，我们可以在更新方法中使用不同的运算符，例如$set、$unset、$inc等。

为了演示，让我们看一下更新集合中单个文档的查询：

```json
db.student.updateOne(
    { 
        "student_name" : "Paul Starc"
    },
    { 
        $set: {
            "address" : "Hostel 2"
        }
    }
 );
```

我们将得到类似于下图所示的输出：

```json
{
    "acknowledged":true,
    "matchedCount":1,
    "modifiedCount":1
}
```

现在让我们检查一下上述updateOne查询的Java驱动程序代码：

```java
UpdateResult updateResult = collection.updateOne(Filters.eq("student_name", "Paul Starc"),
Updates.set("address", "Hostel 2"));
```

在这里，我们首先使用student_name字段来过滤文档。然后我们用student_name “Paul Starc”更新文档的地址。

## 4. 使用updateMany方法

[updateMany](https://docs.mongodb.com/manual/reference/method/db.collection.updateMany/)方法更新 MongoDB 集合中与给定过滤器匹配的所有文档。 使用updateMany的好处之一是我们可以更新多个文档而不会丢失旧文档的字段。

让我们看看使用updateMany方法的 MongoDB shell 查询：

```json
db.student.updateMany(
    { 
        age: { 
            $lt: 20
         } 
    },
    { 
        $set:{ 
            "Review" : true 
        }
    }
);
```

上面的命令将返回以下输出：

```json
{
    "acknowledged":true,
    "matchedCount":2,
    "modifiedCount":2
}
```

这里，matchedCount包含匹配文档的数量，而modifiedCount包含修改后的文档数。

现在让我们使用updateMany方法查看Java驱动程序代码：

```java
UpdateResult updateResult = collection.updateMany(Filters.lt("age", 20), Updates.set("Review", true));
```

在这里，将过滤所有age小于 20的文档，并将Review字段设置为true。

## 5. 使用replaceOne方法

MongoDB的[replaceOne](https://docs.mongodb.com/manual/reference/method/db.collection.replaceOne/)方法替换整个文档。 replaceOne的缺点之一是所有旧字段将被新字段替换，并且旧字段也会丢失：

```json
db.student.replaceOne(
    { 
        "student_id": 8764
    },
    {
        "student_id": 8764,
        "student_name": "Paul Starc",
        "address": "Hostel 2",
        "age": 18,
        "roll_no":199406
    }
);
```

在这种情况下，我们将得到以下输出：

```json
{
    "acknowledged":true,
    "matchedCount":1,
    "modifiedCount":1
}
```

如果未找到匹配项，则该操作将matchedCount返回为 0：

```json
{
    "acknowledged":true,
    "matchedCount":0,
    "modifiedCount":0
}
```

下面使用replaceOne方法编写相应的Java驱动代码：

```java
Document replaceDocument = new Document();
replaceDocument
  .append("student_id", 8764)
  .append("student_name", "Paul Starc")
  .append("address", "Hostel 2")
  .append("age",18)
  .append("roll_no", 199406);
UpdateResult updateResult = collection.replaceOne(Filters.eq("student_id", 8764), replaceDocument);
```

在上面的代码中，我们创建了一个文档，旧文档将被替换。student_id为 8764的文档将被替换为新创建的文档。

## 6. 使用findOneAndReplace方法

findOneAndReplace方法是 MongoDB 提供的高级更新方法之一，它根据给定的选择条件替换第一个匹配的文档[。](https://docs.mongodb.com/manual/reference/method/db.collection.findOneAndReplace/)默认情况下，此方法返回原始文档。如果需要，我们可以使用findOneAndReplace的不同选项来排序和投影文档。

简而言之，findOneAndReplace根据应用的过滤器替换集合中第一个匹配的文档：

```json
db.student.findOneAndReplace(
    { 
        "student_id" : { 
            $eq : 8764 
        }
    },
    { 
        "student_id" : 8764,
        "student_name" : "Paul Starc",
        "address": "Hostel 2",
        "age": 18,
        "roll_no":199406 
    },
    {
        returnNewDocument: false
    }
);
```

此查询将返回以下文档：

```json
{
    "student_id":8764,
    "student_name":"Paul Starc",
    "address":"Hostel 1",
    "age":16,
    "roll_no":199406
}
```

如果我们将 returnNewDocument设置 为 true，则该操作将返回替换后的文档：

```json
{
    "student_id":8764,
    "student_name":"Paul Starc",
    "address":"Hostel 2",
    "age":18,
    "roll_no":199406
}
```

现在让我们使用findOneAndReplace方法投影返回文档中的student_id和age字段：

```json
db.student.findOneAndReplace(
    { 
        "student_id" : {
        $eq : 8764 
        } 
    },
    { 
        "student_id" : 8764, 
        "student_name" : "Paul Starc",
        "address": "Hostel 2",
        "age": 18,
        "roll_no":199406 
    },
    { 
        projection: { 
            "_id" : 0,
            "student_id":1,
            "age" : 1 
        } 
    }
);
```

上述查询的输出将仅包含投影字段：

```json
{
    "student_id":"8764",
    "age":16
}
```

上述查询的Java驱动程序代码带有findOneAndReplace 的各种选项：

```java
Document replaceDocument = new Document();
replaceDocument
  .append("student_id", 8764)
  .append("student_name", "Paul Starc")
  .append("address", "Hostel 2")
  .append("age", 18)
  .append("roll_no", 199406);
Document sort = new Document("roll_no", 1);
Document projection = new Document("_id", 0).append("student_id", 1).append("address", 1);
Document resultDocument = collection.findOneAndReplace(
  Filters.eq("student_id", 8764), 
  replaceDocument,
  new FindOneAndReplaceOptions().upsert(true).sort(sort).projection(projection).returnDocument(ReturnDocument.AFTER));

```

在上面的查询中，findOneAndReplace方法会先将文档按照roll_no升序排序，新创建的文档替换student_id为“8764”的文档。

## 7. 使用findOneAndUpdate 方法

findOneAndUpdate方法更新[集合](https://docs.mongodb.com/manual/reference/method/db.collection.findOneAndUpdate/)中第一个匹配的文档。如果有多个文档符合选择条件，则它只更新第一个匹配的文档。当我们更新文档时，_id字段的值保持不变：

```json
db.student.findOneAndUpdate(
    { 
        "student_id" : 8764
    },
    { 
        $inc : { 
            "roll_no" : 5
        } 
    },
    { 
        sort: { 
            "roll_no" : 1 
        }, 
        projection: { 
            "_id" : 0,
            "student_id":1,
            "address" : 1
        }
    }
);

```

查询的输出将只包含旧文档的studentId和地址：

```java
{
    "student_id":8764,
    "address":"Hostel 1"
}

```

上述查询的Java驱动程序代码，使用findOneAndUpdate的不同选项如下：

```java
Document sort = new Document("roll_no", 1);
Document projection = new Document("_id", 0).append("student_id", 1).append("address", 1);
Document resultDocument = collection.findOneAndUpdate(
  Filters.eq("student_id", 8764),
  Updates.inc("roll_no", 5), 
  new FindOneAndUpdateOptions().sort(sort).projection(projection).returnDocument(ReturnDocument.BEFORE));
```

在这种情况下，findOneAndUpdate方法将首先根据roll_no对文档进行升序排序。上面的查询将roll_no 递增5，然后返回student_id和address字段。

## 八. 总结

在本文中，我们了解了更新 MongoDB 中文档的各种方法。首先，我们研究了 MongoDB shell 查询，然后我们讨论了相应的Java驱动程序代码。
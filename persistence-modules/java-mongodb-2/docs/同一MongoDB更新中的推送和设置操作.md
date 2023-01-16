## 1. 概述

$push是 MongoDB 中的一个更新运算符，用于将值添加到数组中。相反，$set运算符用于更新文档中现有字段的值。

在这个简短的教程中，我们将介绍如何在单个更新查询中同时执行$push和$set操作。

## 2.数据库初始化

在我们继续执行[多个更新操作](https://www.baeldung.com/mongodb-update-multiple-fields)之前，我们首先需要设置一个数据库baeldung和样本收集标记：

```json
use baeldung;
db.createCollection(marks);
```

让我们使用 MongoDB 的insertMany方法将一些文档插入到集合标记中：

```json
db.marks.insertMany([
    {
        "studentId": 1023,
        "studentName":"James Broad",
        "joiningYear":"2018",
        "totalMarks":100,
        "subjectDetails":[
            {
                "subjectId":123,
                "subjectName":"Operating Systems Concepts",
                "marks":40
            },
            {
                "subjectId":124,
                "subjectName":"Numerical Analysis",
                "marks":60
            }
        ]
    },
    {
        "studentId": 1024,
        "studentName":"Chris Overton",
        "joiningYear":"2018",
        "totalMarks":110,
        "subjectDetails":[
            {
                "subjectId":123,
                "subjectName":"Operating Systems Concepts",
                "marks":50
            },
            {
                "subjectId":124,
                "subjectName":"Numerical Analysis",
                "marks":60
            }
        ]
    }
]);
```

成功插入后，上述查询将返回以下响应：

```json
{
    "acknowledged" : true,
    "insertedIds" : [
        ObjectId("622300cc85e943405d04b567"),
        ObjectId("622300cc85e943405d04b568")
    ]
}
```

至此，我们已经成功将几个示例文档插入到集合标记中。

## 3.理解问题

为了理解这个问题，让我们先了解一下我们刚刚插入的文档。它包括学生的详细信息以及他们在不同科目中获得的分数。totalMarks 是在不同科目中获得的分数的总和。

让我们考虑一种情况，我们希望在subjectDetails数组中添加一个新主题。为了使数据保持一致，我们还需要更新totalMarks字段。

在 MongoDB 中，首先，我们将使用$push运算符将新主题添加到数组中。然后我们将使用$set运算符将totalMarks 字段设置为特定值。

这两个操作都可以分别使用$push和$set运算符单独执行。但是我们可以编写 MongoDB 查询来同时执行这两个操作。

## 4. 使用 MongoDB Shell 查询

在 MongoDB 中，我们可以使用不同的更新运算符更新文档的多个字段。在这里，我们将在updateOne查询中同时使用$push和$set运算符。

让我们检查同时包含 $push和$set运算符的示例：

```json
db.marks.updateOne(
    {
        "studentId": 1023
    },
    {
        $set: {
            totalMarks: 170
        },
        $push: {
            "subjectDetails":{
                "subjectId": 126,
                "subjectName": "Java Programming",
                "marks": 70
            }
        }
    }
);
```

在这里，在上面的查询中，我们添加了基于studentId 的筛选查询。一旦我们得到过滤后的文档，我们就会使用 $set 运算符更新totalMarks 。除此之外，我们使用$push运算符将新的主题数据插入到subjectDetails数组中。

结果，上述查询将返回以下输出：

```json
{
    "acknowledged":true,
    "matchedCount":1,
    "modifiedCount":1
}
```

此处， matchedCount包含与过滤器匹配的文档计数，而modifiedCount包含已修改文档的数量。

## 5.Java驱动程序代码

到目前为止，我们讨论了一起使用$push和$set运算符的 mongo shell 查询。在这里，我们将学习使用Java驱动程序代码实现相同的功能。

在我们继续之前，让我们首先连接到数据库和所需的集合：

```java
MongoClient mongoClient = new MongoClient(new MongoClientURI("localhost", 27017);
MongoDatabase database = mongoClient.getDatabase("baeldung");
MongoCollection<Document> collection = database.getCollection("marks");
```

在这里，我们连接到 MongoDB，它在本地主机上的默认端口 27017 上运行。

现在让我们看看Java驱动程序代码：

```java
Document subjectData = new Document()
  .append("subjectId", 126)
  .append("subjectName", "Java Programming")
  .append("marks", 70); 
UpdateResult updateQueryResult = collection.updateOne(Filters.eq("studentId", 1023), 
  Updates.combine(Updates.set("totalMarks", 170), 
  Updates.push("subjectDetails", subjectData)));
```

在此代码片段中，我们使用了updateOne方法，该方法仅根据应用的过滤器studentId 1023更新单个文档。然后我们使用Updates.combine在一次调用中执行多个操作。字段totalMarks将更新为 170，新文档subjectData 将被推送到数组字段“subjectDetails”。

## 六. 总结

在本文中，我们了解了在单个 MongoDB 查询中一起应用多个操作的用例。此外，我们使用 MongoDB shell 查询和Java驱动程序代码执行相同的操作。
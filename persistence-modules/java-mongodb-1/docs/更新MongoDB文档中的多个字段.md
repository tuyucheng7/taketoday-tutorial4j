## 1. 概述

[MongoDB](https://www.mongodb.com/)是一个公开可用的面向文档的 NoSQL 数据库。我们可以使用更新、替换和保存等各种方法更新集合中的文档。为了更改文档的特定字段，我们将使用不同的运算符，如$set、$inc 等。

在本教程中，我们将学习使用更新和替换查询修改文档的多个字段。出于演示目的，我们将首先讨论 mongo shell 查询，然后讨论它在Java中的相应实现。

现在让我们研究实现该目的的各种方法。

## 2. Shell查询更新不同字段

在开始之前，让我们先创建一个新数据库baeldung和一个示例集合employee。我们将在所有示例中使用此集合：

```sql
use baeldung;
db.createCollection(employee);
```

现在让我们使用insertMany查询向这个集合中添加一些文档：

```bash
db.employee.insertMany([
    {
        "employee_id": 794875,
        "employee_name": "David Smith",
        "job": "Sales Representative",
        "department_id": 2,
        "salary": 20000,
        "hire_date": NumberLong("1643969311817")
    },
    {
        "employee_id": 794876,
        "employee_name": "Joe Butler",
        "job": "Sales Manager",
        "department_id": 3,
        "salary": 30000,
        "hire_date": NumberLong("1645338658000")
    }
]);
```

结果，我们将为这两个文档获得一个带有 ObjectId 的 JSON，如下所示：

```bash
{
    "acknowledged": true,
    "insertedIds": [
        ObjectId("6211e034b76b996845f3193d"),
        ObjectId("6211e034b76b996845f3193e")
        ]
}
```

至此，我们已经搭建好了所需的环境。现在让我们更新刚刚插入的文档。

### 2.1. 更新单个文档的多个字段

我们可以使用$set和$inc运算符来更新 MongoDB 中的任何字段。$set运算符将设置新指定的值，而$inc运算符会将值增加指定值。

让我们首先查看 MongoDB 查询，使用$set运算符更新员工集合的两个字段：

```json
db.employee.updateOne(
    {
        "employee_id": 794875,
        "employee_name": "David Smith"
    },
    {
        $set:{
            department_id:3,
            job:"Sales Manager"
        }
    }
);
```

在上面的查询中，employee_id 和employee_name 字段用于过滤文档，$set运算符用于更新job和department_id字段。

我们还可以在单个更新查询中同时使用$set和$inc运算符：

```json
db.employee.updateOne(
    {
        "employee_id": 794875
    },
    {
        $inc: {
            department_id: 1
        },
        $set: {
            job: "Sales Manager"
        }
    }
);
```

这会将工作字段更新为销售经理并将department_id增加1。

### 2.2. 更新多个文档的多个字段

此外，我们还可以更新MongoDB中多个文档的多个字段。我们只需要包含选项multi:true来修改所有符合过滤查询条件的文档：

```json
db.employee.update(
    {
        "job": "Sales Representative"
    },
    {
        $inc: { 
            salary: 10000
        }, 
        $set: { 
            department_id: 5
        }
    },
    {
        multi: true 
    }
);
```

或者，我们将使用updateMany查询获得相同的结果：

```json
db.employee.updateMany(
    {
        "job": "Sales Representative"
    },
    {
        $inc: {
            salary: 10000
        },
        $set: {
            department_id: 5
        }
    }
);
```

在上面的查询中，我们使用了updateMany方法来更新集合中的多于 1 个文档。

### 2.3. 更新多个字段时的常见问题

到目前为止，我们已经学习了通过提供两个不同的运算符或在多个字段上使用单个运算符来使用更新查询来更新多个字段。

现在，如果我们在单个查询中对不同的字段多次使用一个运算符，MongoDB 将只更新更新查询的最后一条语句而忽略其余部分：

```json
db.employee.updateMany(
    {
        "employee_id": 794875
    },
    {
        $set: {
            department_id: 3
        },
        $set: {
            job:"Sales Manager"
        }
    }
);
```

上面的查询将返回类似的输出：

```bash
{
    "acknowledged":true,
    "matchedCount":1,
    "modifiedCount":1
}
```

在这种情况下，唯一的 工作将更新为“销售经理”。department_id值不会更新为 3 。

## 3. 使用Java驱动程序更新字段

到目前为止，我们已经讨论了原始 MongoDB 查询。[现在让我们使用 Java](https://www.baeldung.com/queries-in-spring-data-mongodb)执行相同的操作。MongoDBJava驱动程序支持两个类来表示 MongoDB 文档，com.mongodb.BasicDBObject和org.bson.Document。我们将研究这两种更新文档中字段的方法。

在我们继续之前，让我们首先连接到baeldung数据库中的员工集合：

```java
MongoClient mongoClient = new MongoClient(new MongoClientURI("localhost", 27017);
MongoDatabase database = mongoClient.getDatabase("baeldung");
MongoCollection<Document> collection = database.getCollection("employee");
```

这里我们假设 MongoDB 在默认端口 27017 本地运行。

### 3.1. 使用数据库对象

为了在 MongoDB 中创建文档，我们将使用com.mongodb。DBObject 接口及其实现类com.mongodb.BasicDBObject。

DBObject的实现基于键值对。BasicDBObject继承自util包中的LinkedHashMap类。

现在让我们使用com.mongodb.BasicDBObject 对多个字段执行更新操作：

```bash
BasicDBObject searchQuery = new BasicDBObject("employee_id", 794875);
BasicDBObject updateFields = new BasicDBObject();
updateFields.append("department_id", 3);
updateFields.append("job", "Sales Manager");
BasicDBObject setQuery = new BasicDBObject();
setQuery.append("$set", updateFields);
UpdateResult updateResult = collection.updateMany(searchQuery, setQuery);

```

在这里，首先，我们根据employee_id 创建了一个过滤器查询。此操作将返回一组文档。此外，我们根据设置的查询更新了department_id和job的值。

### 3.2. 使用bson文档

[我们可以使用bson](https://baeldung.com/mongodb-bson)文档执行所有 MongoDB 操作。为此，首先，我们需要集合对象，然后使用带有过滤器和设置函数的updateMany方法执行更新操作。

```bash
UpdateResult updateQueryResult = collection.updateMany(Filters.eq("employee_id", 794875),
Updates.combine(Updates.set("department_id", 3), Updates.set("job", "Sales Manager")));
```

在这里，我们将查询过滤器传递给updateMany方法。eq过滤器将employee_id与完全匹配的文本“794875”相匹配。然后，我们使用set运算符更新department_id和作业。

## 4.使用替换查询

更新文档的多个字段的天真方法是用具有更新值的新文档替换它。

例如，如果我们希望用employee_id 794875 替换文档，我们可以执行以下查询：

```json
db.employee.replaceOne(
    {
        "employee_id": 794875
    },
    {
        "employee_id": 794875,
        "employee_name": "David Smith",
        "job": "Sales Manager",
        "department_id": 3,
        "salary": 30000,
        "hire_date": NumberLong("1643969311817")
    }
);
```

上面的命令将在输出中打印确认 JSON：

```json
{
    "acknowledged":true,
    "matchedCount":1,
    "modifiedCount":1
}
```

此处，employee_id 字段用于过滤文档。更新查询的第二个参数表示将替换现有文档的文档。

在上面的查询中，我们正在执行replaceOne，因此，它将仅用该过滤器替换单个文档。或者，如果我们想用该过滤器查询替换所有文档，则需要使用updateMany方法。

## 5.总结

在本文中，我们探讨了在 MongoDB 中更新文档的多个字段的各种方法。我们广泛讨论了两种实现，使用 MongoDB shell 和使用Java驱动程序。

有多种选项可以更新文档的多个字段，包括$inc 和$set 运算符。
## 1. 概述

在本教程中，我们将着眼于执行搜索操作以检索[MongoDB](https://www.mongodb.com/)中的文档。MongoDB 提供了一个查找运算符来查询集合中的文档。find运算符的主要目的是根据查询条件从集合中选择文档，并返回一个游标到选择的文档。

在本教程中，我们将首先了解 MongoDB Shell 查询中的查找运算符，然后使用Java驱动程序代码。

## 2.数据库初始化

在我们继续执行查找操作之前，我们首先需要设置一个数据库baeldung和一个样本收集员工：

```json
db.employee.insertMany([
{
    "employeeId":"EMP1",
    "name":"Sam", 
    "age":23,
    "type":"Full Time",
    "department":"Engineering"
},
{ 
    "employeeId":"EMP2",
    "name":"Tony",
    "age":31,
    "type":"Full Time",
    "department":"Admin"
},
{
    "employeeId":"EMP3",
    "name":"Lisa",
    "age":42,
    "type":"Part Time",
    "department":"Engineering"
}]);
```

成功插入后，上述查询将返回类似于下图所示的 JSON 结果：

```json
{
    "acknowledged" : true,
    "insertedIds" : [
        ObjectId("62a88223ff0a77909323a7fa"),
        ObjectId("62a88223ff0a77909323a7fb"),
        ObjectId("62a88223ff0a77909323a7fc")
    ]
}
```

此时，我们已将一些文档插入到我们的集合中以执行各种类型的查找操作。

## 3. 使用 MongoDB 外壳

要从 MongoDB 集合中查询文档，我们使用db.collection.find(query, projection)方法。该方法接受两个可选参数——查询和投影——作为 MongoDB [BSON](https://www.baeldung.com/mongodb-bson)文档。

查询参数接受带有查询运算符的选择过滤器。要从 MongoDB 集合中检索所有文档，我们可以省略此参数或传递空白文档。

接下来，投影参数用于指定要从匹配文档返回的字段。要返回匹配文档中的所有字段，我们可以省略此参数。

此外，让我们从一个返回所有集合文档的基本查找查询开始：

```json
db.employee.find({});
```

上面的查询将返回员工集合中的所有文档：

```json
{ "_id" : ObjectId("62a88223ff0a77909323a7fa"), "employeeId" : "1", "name" : "Sam", "age" : 23, "type" : "Full Time", "department" : "Engineering" }
{ "_id" : ObjectId("62a88223ff0a77909323a7fb"), "employeeId" : "2", "name" : "Tony", "age" : 31, "type" : "Full Time", "department" : "Admin" }
{ "_id" : ObjectId("62a88223ff0a77909323a7fc"), "employeeId" : "3", "name" : "Ray", "age" : 42, "type" : "Part Time", "department" : "Engineering" }
```

接下来，让我们编写一个查询以返回属于“工程” 部门的所有员工：

```matlab
db.employee.find(
{
    "department":"Engineering"
});
```

上面的查询返回部门等于“工程”的所有员工集合文档：

```css
{ "_id" : ObjectId("62a88223ff0a77909323a7fa"), "employeeId" : "1", "name" : "Sam", "age" : 23, "type" : "Full Time", "department" : "Engineering" }
{ "_id" : ObjectId("62a88223ff0a77909323a7fc"), "employeeId" : "3", "name" : "Ray", "age" : 42, "type" : "Part Time", "department" : "Engineering" }
```

最后，让我们编写一个查询来获取属于“工程”部门的所有员工的姓名和年龄：

```matlab
db.employee.find(
{
    "department":"Engineering"
},
{
    "name":1,
    "age":1
});
```

上面的查询只返回符合查询条件的文档的name和age字段：

```css
{ "_id" : ObjectId("62a88223ff0a77909323a7fa"), "name" : "Sam", "age" : 23 }
{ "_id" : ObjectId("62a88223ff0a77909323a7fc"), "name" : "Ray", "age" : 42 }
```

请注意，除非明确排除，否则_id字段在所有文档中默认返回。

另外，重要的是要注意find运算符将游标返回到与查询过滤器匹配的文档。MongoDB Shell 自动迭代游标以显示最多 20 个文档。

此外，MongoDB Shell 提供了一个findOne()方法，它只返回一个满足上述查询条件的文档。如果有多个文档匹配，则按照文档在磁盘上的自然顺序返回第一个文档：

```scss
db.employee.findOne();
```

与find()不同，上面的查询将只返回一个文档而不是游标：

```json
{
    "_id" : ObjectId("62a99e22a849e1472c440bbf"),
    "employeeId" : "EMP1",
    "name" : "Sam",
    "age" : 23,
    "type" : "Full Time",
    "department" : "Engineering"
}
```

## 4. 使用Java驱动程序

到目前为止，我们已经了解了如何使用 MongoDB Shell 执行查找操作。接下来，让我们使用 MongoDBJava驱动程序实现相同的功能。在我们开始之前，让我们首先创建一个到员工集合的MongoClient连接：

```java
MongoClient mongoClient = new MongoClient("localhost", 27017);
MongoDatabase database = mongoClient.getDatabase("baeldung");
MongoCollection<Document> collection = database.getCollection("employee");
```

在这里，我们创建了一个到在默认端口 27017 上运行的 MongoDB 服务器的连接。接下来，我们从连接创建的MongoDatabase实例中获取了MongoCollection实例。

首先，为了执行查找操作，我们在MongoCollection的实例上调用find()方法。让我们检查代码以从集合中检索所有文档：

```java
FindIterable<Document> documents = collection.find();
MongoCursor<Document> cursor = documents.iterator();
while (cursor.hasNext()) {
    System.out.println(cursor.next());
}
```

请注意，find()方法返回FindIterable<Document>的一个实例。然后我们通过调用 FindIterable 的iterator()方法获得MongoCursor的实例。最后，我们遍历游标以检索每个文档。

接下来，让我们添加查询运算符来过滤从查找操作返回的文档：

```java
Bson filter = Filters.eq("department", "Engineering");
FindIterable<Document> documents = collection.find(filter);

MongoCursor<Document> cursor = documents.iterator();
while (cursor.hasNext()) {
    System.out.println(cursor.next());
}
```

在这里，我们将Bson过滤器作为参数传递给find()方法。我们可以使用[查询运算符](https://www.mongodb.com/docs/manual/reference/operator/query/)的任意组合作为find()方法的过滤器。上面的代码片段将返回部门等于“工程”的所有文档。

此外，让我们编写一个片段，仅返回符合选择条件的文档中的姓名和年龄字段：

```java
Bson filter = Filters.eq("department", "Engineering");
Bson projection = Projections.fields(Projections.include("name", "age"));
FindIterable<Document> documents = collection.find(filter)
  .projection(projection);

MongoCursor<Document> cursor = documents.iterator();
while (cursor.hasNext()) {
    System.out.println(cursor.next());
}
```

在这里，我们在FindIterable实例上调用projection()方法。我们将Bson过滤器作为参数传递给projection()方法。我们可以使用投影操作在最终结果中包含或排除任何字段。作为直接使用 MongoDB 驱动程序和Bson的替代方法，请查看我们[的 Spring Data MongoDB 投影指南](https://www.baeldung.com/mongodb-return-specific-fields)。

最后，我们可以使用FindIterable实例上的first()方法检索结果的第一个文档。这将返回单个文档而不是MongoCursor实例：

```java
FindIterable<Document> documents = collection.find();
Document document = documents.first();
```

## 5.总结

在本文中，我们学习了使用各种方法在 MongoDB 中执行查找操作。我们执行查找以使用查询运算符检索与选择条件匹配的特定文档。此外，我们还学习了执行投影以确定匹配文档中返回哪些字段。

首先，我们研究了 MongoDB Shell 查询中查找操作的用例，然后讨论了相应的Java驱动程序代码。
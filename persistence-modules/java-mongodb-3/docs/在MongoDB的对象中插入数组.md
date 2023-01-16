## 1. 概述

[MongoDB](https://www.mongodb.com/)是最流行的开源和分布式面向文档的 NoSQL 数据库。MongoDB 中的文档是一种数据结构，具有类似 JSON 的对象，这些对象具有字段和值对。

为了将文档插入 MongoDB 集合，我们可以使用不同的方法，例如[insert()](https://www.mongodb.com/docs/manual/reference/method/db.collection.insert/)、[insertOne()](https://www.mongodb.com/docs/manual/reference/method/db.collection.insertOne/)和[insertMany()](https://www.mongodb.com/docs/manual/reference/method/db.collection.insertMany/)。

[在本教程中，我们将讨论如何在MongoDB](https://www.baeldung.com/java-mongodb)文档中插入数组。首先，我们将了解如何使用 MongoDB Shell 查询将数组插入到文档中。然后我们将使用[MongoDBJava驱动程序](https://www.baeldung.com/java-mongodb)代码。

## 2.数据库初始化

在我们继续插入查询之前，让我们先创建一个数据库。让我们称之为baeldung。 我们还将创建一个名为student 的示例集合：

```rust
use baeldung;
db.createCollection(student);
```

使用此命令，我们的示例baeldung数据库和学生集合已成功设置。我们将使用这些在所有示例中进行演示。

## 3. 使用 MongoDB 外壳

要使用 MongoDB Shell 将数组插入到集合中，我们可以简单地将数组作为 JSON 数组类型传递给 shell：

```lua
db.student.insert({
    "studentId" : "STU1",
    "name" : "Avin",
    "Age" : 12,
    "courses" : ["Science", "Math"]
});
```

上面的查询在学生集合中插入一个带有数组的文档。我们可以通过使用[查找](https://www.baeldung.com/mongodb-find)运算符查询学生集合的文档来验证结果：

```matlab
db.student.find();
```

上面的查询返回插入的学生集合文档：

```css
{
    "_id" : ObjectId("631da4197581ba6bc1d2524d"),
    "studentId" : "STU1",
    "name" : "Avin",
    "Age" : 12,
    "courses" : [ "Science", "Math" ]
}
```

## 4.插入操作使用Java驱动代码

MongoDBJavaDriver 提供了各种方便的方法来帮助我们将文档插入到集合中：

-   insert() – 将单个文档或多个文档插入集合
-   insertOne() – 将单个文档插入集合
-   insertMany() – 将多个文档插入到一个集合中

上面的任何一种方法都可以用来对MongoDB集合进行插入操作。

接下来，让我们深入了解使用JavaMongoDB Driver 实现数组插入操作。MongoDBJava驱动程序支持DBObject 和 BSON文档。

## 5. 使用数据库对象

此处，DBObject是 MongoDB 遗留驱动程序的一部分，但在较新版本的 MongoDB 中已弃用。

让我们将一个带有数组的DBObject文档插入到学生集合中：

```java
BasicDBList coursesList = new BasicDBList();
coursesList.add("Chemistry");
coursesList.add("Science");

DBObject student = new BasicDBObject().append("studentId", "STU1")
  .append("name", "Jim")
  .append("age", 13)
  .append("courses", coursesList);

dbCollection.insert(student);
```

上面的查询将带有数组的单个DBObject文档插入到学生集合中。

## 6. 使用BSON文档

[BSON](https://www.baeldung.com/mongodb-bson)文档是用Java访问 MongoDB 文档的新方法，它是用更新的客户端堆栈构建的。 幸运的是，它也更易于使用。

 

Java 驱动程序提供了一个org.bson.Document 类来将一个带有数组的 Bson 文档对象插入到学生集合中。

### 6.1. 插入带有数组的单个文档

首先，让我们使用insertOne()方法将带有数组的单个文档插入到集合中：

```java
List coursesList = new ArrayList<>();
coursesList.add("Science");
coursesList.add("Geography");

Document student = new Document().append("studentId", "STU2")
  .append("name", "Sam")
  .append("age", 13)
  .append("courses", coursesList);

collection.insertOne(student);
```

上面的查询将带有数组的单个文档插入到学生集合中。重要的是要注意Document类的append(String, Object)方法接受一个Object作为值。我们可以将任何对象类型的列表作为值传递，以将其作为数组插入到文档中。

### 6.2. 使用数组插入多个文档

让我们使用insertMany()方法将带有数组的多个文档插入到集合中：

```java
List coursesList1 = new ArrayList<>();
coursesList1.add("Chemistry");
coursesList1.add("Geography");

Document student1 = new Document().append("studentId", "STU3")
  .append("name", "Sarah")
  .append("age", 12)
  .append("courses", coursesList1);

List coursesList2 = new ArrayList<>();
coursesList2.add("Math");
coursesList2.add("History");

Document student2 = new Document().append("studentId", "STU4")
  .append("name", "Tom")
  .append("age", 13)
  .append("courses", coursesList2);

List<Document> students = new ArrayList<>();
students.add(student1);
students.add(student2);

collection.insertMany(students);
```

上面的查询将带有数组的多个文档插入到学生集合中。

### 6.3. 插入对象数组

最后，让我们将一个Object数组类型的文档插入到 MongoDB 集合中：

```java
Document course1 = new Document().append("name", "C1")
  .append("points", 5);

Document course2 = new Document().append("name", "C2")
  .append("points", 7);

List<Document> coursesList = new ArrayList<>();
coursesList.add(course1);
coursesList.add(course2);

Document student = new Document().append("studentId", "STU5")
  .append("name", "Sam")
  .append("age", 13)
  .append("courses", coursesList);

collection.insertOne(student);
```

上面的查询将带有Object数组的多个文档插入到学生集合中。在这里，我们插入了一个文档，其中包含一个文档列表作为一个数组到集合中。同样，我们可以构造任何复杂的数组对象并将其插入到 MongoDB 集合中。

## 七. 总结

在本文中，我们看到了将带有数组对象的文档插入MongoDB 集合的各种方法。我们使用 MongoDB Shell 查询以及相应的Java驱动程序代码实现来讨论这些用例。

对于Java驱动代码，我们首先查看了使用已弃用的DBObject类的实现。然后，我们学习了使用新的BSON Document 类来实现相同的功能。
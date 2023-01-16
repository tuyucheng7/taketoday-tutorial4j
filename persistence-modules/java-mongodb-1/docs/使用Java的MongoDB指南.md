## 1. 概述

在本文中，我们将了解如何将非常流行的 NoSQL 开源数据库[MongoDB与独立的Java客户端集成。](https://www.mongodb.com/)

MongoDB 是用 C++ 编写的，具有相当多的可靠特性，例如 map-reduce、自动分片、、高可用性等。

## 2. MongoDB

让我们从关于 MongoDB 本身的几个关键点开始：

-   将数据存储在[类似 JSON](https://www.w3schools.com/js/js_json_intro.asp)的文档中，这些文档可以具有各种结构
-   使用动态模式，这意味着我们可以在不预先定义任何东西的情况下创建记录
-   记录的结构可以简单地通过添加新字段或删除现有字段来更改

上述数据模型使我们能够轻松地表示层次关系、存储数组和其他更复杂的结构。

## 3.术语

如果我们可以将它们与关系数据库结构进行比较，那么理解 MongoDB 中的概念就会变得更加容易。

让我们看看 Mongo 和传统 MySQL 系统之间的类比：

-   MySQL 中的表成为Mongo中的集合
-   行成为文档
-   列成为一个字段
-   联接被定义为链接和嵌入文档

当然，这是查看 MongoDB 核心概念的一种简单方法，但仍然很有用。

现在，让我们深入实施以了解这个强大的数据库。

## 4.Maven依赖

我们需要首先为 MongoDB 定义Java驱动程序的依赖项：

```xml
<dependency>
    <groupId>org.mongodb</groupId>
    <artifactId>mongo-java-driver</artifactId>
    <version>3.4.1</version>
</dependency>

```

要检查是否已发布任何新版本的库 -[在此处跟踪发布](https://search.maven.org/classic/#search|gav|1|g%3A"org.mongodb" AND a%3A"mongo-java-driver")。

## 5.使用MongoDB

现在，让我们开始使用Java实现 Mongo 查询。我们将遵循基本的 CRUD 操作，因为它们是最好的开始。

### 5.1. 与MongoClient建立连接

首先，让我们连接到 MongoDB 服务器。对于 >= 2.10.0 的版本，我们将使用MongoClient：

```java
MongoClient mongoClient = new MongoClient("localhost", 27017);
```

对于旧版本，使用Mongo类：

```java
Mongo mongo = new Mongo("localhost", 27017);
```

### 5.2. 连接到数据库

现在，让我们连接到我们的数据库。有趣的是，我们不需要创建一个。当 Mongo 发现数据库不存在时，它会为我们创建它：

```java
DB database = mongoClient.getDB("myMongoDb");
```

有时，默认情况下，MongoDB 以身份验证模式运行。在这种情况下，我们需要在连接到数据库时进行身份验证。

我们可以按如下所示进行操作：

```java
MongoClient mongoClient = new MongoClient();
DB database = mongoClient.getDB("myMongoDb");
boolean auth = database.authenticate("username", "pwd".toCharArray());
```

### 5.3. 显示现有数据库

让我们显示所有现有的数据库。当我们想使用命令行时，显示数据库的语法类似于MySQL：

```c
show databases;
```

在Java中，我们使用以下代码片段显示数据库：

```java
mongoClient.getDatabaseNames().forEach(System.out::println);
```

输出将是：

```java
local      0.000GB
myMongoDb  0.000GB
```

上面，local是默认的 Mongo 数据库。

### 5.4. 创建一个集合

让我们从为我们的数据库创建一个集合(相当于 MongoDB 的表)开始。一旦我们连接到我们的数据库，我们就可以创建一个集合：

```java
database.createCollection("customers", null);
```

现在，让我们显示当前数据库的所有现有集合：

```java
database.getCollectionNames().forEach(System.out::println);
```

输出将是：

```java
customers
```

### 5.5. 保存 - 插入

保存操作具有保存或更新语义：如果存在id，则执行更新，如果不存在，则执行插入。

当我们保存一个新客户时：

```java
DBCollection collection = database.getCollection("customers");
BasicDBObject document = new BasicDBObject();
document.put("name", "Shubham");
document.put("company", "Baeldung");
collection.insert(document);
```

该实体将被插入到数据库中：

```java
{
    "_id" : ObjectId("33a52bb7830b8c9b233b4fe6"),
    "name" : "Shubham",
    "company" : "Baeldung"
}
```

接下来，我们将查看具有更新语义的相同操作——保存。

### 5.6. 保存 - 更新

现在让我们看看save with update语义，对现有客户进行操作：

```java
{
    "_id" : ObjectId("33a52bb7830b8c9b233b4fe6"),
    "name" : "Shubham",
    "company" : "Baeldung"
}
```

现在，当我们保存现有客户时——我们将更新它：

```java
BasicDBObject query = new BasicDBObject();
query.put("name", "Shubham");

BasicDBObject newDocument = new BasicDBObject();
newDocument.put("name", "John");

BasicDBObject updateObject = new BasicDBObject();
updateObject.put("$set", newDocument);

collection.update(query, updateObject);
```

数据库将如下所示：

```java
{
    "_id" : ObjectId("33a52bb7830b8c9b233b4fe6"),
    "name" : "John",
    "company" : "Baeldung"
}
```

如你所见，在这个特定示例中，save使用update的语义，因为我们使用具有给定_id的对象。

### 5.7. 从集合中读取文档

让我们通过查询在集合中搜索文档：

```java
BasicDBObject searchQuery = new BasicDBObject();
searchQuery.put("name", "John");
DBCursor cursor = collection.find(searchQuery);

while (cursor.hasNext()) {
    System.out.println(cursor.next());
}
```

它将显示我们收藏中目前唯一的文档：

```java
[
    {
      "_id" : ObjectId("33a52bb7830b8c9b233b4fe6"),
      "name" : "John",
      "company" : "Baeldung"
    }
]
```

### 5.8. 删除文档_

让我们继续我们的最后一个 CRUD 操作，删除：

```java
BasicDBObject searchQuery = new BasicDBObject();
searchQuery.put("name", "John");

collection.remove(searchQuery);
```

执行上述命令后，我们唯一的Document将从Collection中删除。

## 六. 总结

本文是对从Java使用 MongoDB 的快速介绍。
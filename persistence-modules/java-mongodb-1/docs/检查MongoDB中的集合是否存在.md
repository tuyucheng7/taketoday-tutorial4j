## 1. 概述

MongoDB 是一个 NoSQL 数据库，它将数据记录作为[BSON](https://www.baeldung.com/mongodb-bson)文档存储到一个集合中。我们可以有多个数据库，每个数据库可以有一个或多个文档集合。

与关系数据库不同，MongoDB 使用插入的文档创建集合而不需要任何结构定义。在本教程中，我们将学习检查集合是否存在的各种方法。我们将使用collectionExists、createCollection、listCollectionNames和count方法来检查集合是否存在。

## 2. 数据库连接

为了访问集合的任何数据，我们首先需要建立与数据库的连接。让我们连接到在我们机器上本地运行的 MongoDB 数据库。

### 2.1. 使用MongoClient创建连接

[MongoClient](https://docs.mongodb.com/drivers/java-drivers/) 是一个Java类，用于与 MongoDB 实例建立连接：

```java
MongoClient mongoClient = new MongoClient("localhost", 27017);
```

在这里，我们连接到在本地主机上的默认端口 27017 上运行的 MongoDB。

### 2.2. 连接到数据库

现在，让我们使用MongoClient 对象访问数据库。使用MongoClient访问数据库有两种方法。

首先，我们将使用getDatabase方法访问baeldung数据库：

```java
MongoDatabase database = mongoClient.getDatabase("baeldung");
```

我们也可以使用Mongo Java驱动的getDB方法连接数据库：

```java
DB db = mongoClient.getDB("baeldung");
```

getDB方法已 弃用，因此不建议使用。

到目前为止，我们已经使用 MongoClient 建立了与 MongoDB 的连接，并进一步连接到baeldung数据库。

让我们深入探讨检查 MongoDB 中集合是否存在的不同方法。

## 3. 使用 数据库类

MongoDBJava驱动程序提供同步和异步方法调用。为了连接到数据库，我们只需要指定数据库名称。如果数据库不存在，MongoDB 将自动创建一个。

collectionExists方法可用于检查集合是否存在：

```java
MongoClient mongoClient = new MongoClient("localhost", 27017);
DB db = mongoClient.getDB("baeldung");
String testCollectionName = "student";
System.out.println("Collection Name " + testCollectionName + " " + db.collectionExists(testCollectionName));
```

在这里，如果集合存在，collectionExists方法将返回true ，否则返回 false。

MongoDBJava驱动程序的com.mongodb.DB API 从版本 3.x 开始弃用，但仍然可以访问。因此，不建议将DB类用于新项目。

## 4. 使用 MongoDatabase类

com.mongodb.client.MongoDatabase是 Mongo 3.x 及更高版本的更新 API 。与 DB 类不同，MongoDatabase 类不提供任何特定方法来检查集合是否存在。但是，我们可以使用多种方法来获得所需的结果。

### 4.1. 使用 createCollection方法

createCollection方法在 MongoDB中 创建一个新集合。但我们也可以用它来检查集合是否存在：

```java
String databaseName="baeldung";
MongoDatabase database = mongoClient.getDatabase(databaseName);
String testCollectionName = "student";
try {
    database.createCollection(testCollectionName);
} catch (Exception exception) {
    System.err.println("Collection:- "+testCollectionName +" already Exists");
}
```

上面的代码将创建一个新的集合“ student” ，如果它不存在于数据库中的话。 如果集合已经存在，createCollection方法将抛出异常。

不推荐使用这种方法，因为它会在数据库中创建一个新集合。

### 4.2. 使用 listCollectionNames方法

listCollectionNames方法列出数据库中的所有集合名称。因此，我们可以用这种方法来解决集合存在的问题。

现在让我们看一下使用Java驱动程序代码的listCollectionNames方法的示例代码：

```java
String databaseName="baeldung";
MongoDatabase database = mongoClient.getDatabase(databaseName);
String testCollectionName = "student";
boolean collectionExists = database.listCollectionNames()
  .into(new ArrayList()).contains(testCollectionName);
System.out.println("collectionExists:- " + collectionExists);
```

在这里，我们迭代了数据库baeldung 中所有集合名称的列表。 对于每次出现，我们将集合字符串名称与testCollectionName进行匹配。它将在成功匹配时返回true ，否则返回false。

### 4.3. 使用 计数方法

MongoCollection的count方法计算集合中存在的文档数。

作为解决方法，我们可以使用此方法检查集合是否存在。这是相同的Java代码片段：

```java
String databaseName="baeldung";
MongoDatabase database = mongoClient.getDatabase(databaseName);
String testCollectionName = "student";
MongoCollection<Document> collection = database.getCollection(testCollectionName);
Boolean collectionExists = collection.count() > 0 ? true : false;
System.out.println("collectionExists:- " + collectionExists);
Boolean expectedStatus = false;
assertEquals(expectedStatus, collectionExists);
```

如果集合存在但没有任何数据，则此方法不起作用，在这种情况下，它将返回 0，但集合存在且数据为空。

## 5.总结

在本文中，我们探讨了使用 MongoDatabase和DB类方法检查集合是否存在的各种方法。

简而言之，建议使用 com.mongodb.DB 类的collectionExists方法和com.mongodb.client.MongoDatabase类的listCollectionNames方法来检查集合是否存在。
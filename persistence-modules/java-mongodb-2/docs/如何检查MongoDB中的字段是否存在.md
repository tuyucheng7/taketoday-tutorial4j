## 1. 概述

在这个简短的教程中，我们将了解如何检查[MongoDB](https://www.baeldung.com/java-mongodb)中的字段是否存在。 

首先，我们将创建一个简单的 Mongo 数据库和示例集合。然后，我们将在其中放入虚拟数据，以便稍后在我们的示例中使用。之后，我们将展示如何在本机 Mongo 查询和Java中检查该字段是否存在。

## 2. 示例配置

在我们开始检查字段是否存在之前，我们需要一个现有的数据库、集合和虚拟数据以备后用。为此，我们将使用 Mongo shell。

首先，让我们将 Mongo shell 上下文切换到现有数据库：

```bash
use existence
```

值得指出的是，MongoDB 仅在你首次将数据存储在该数据库中时才创建数据库。我们将向用户集合中插入一个用户：

```bash
db.users.insert({name: "Ben", surname: "Big" })
```

现在我们有了需要检查的一切，无论该字段是否存在。

## 3. 在 Mongo Shell 中检查字段是否存在

有时我们需要使用基本查询来检查特定字段是否存在，例如，在 Mongo Shell 或任何其他数据库控制台中。幸运的是，Mongo为此目的提供了一个特殊的查询运算符$exists ：

```bash
db.users.find({ 'name' : { '$exists' : true }})
```

我们使用标准的查找Mongo 方法，在该方法中我们指定要查找的字段并使用$exists查询运算符。如果用户集合中存在name字段，则将返回包含该字段的所有行：

```bash
[
  {
    "_id": {"$oid": "6115ad91c4999031f8e6f582"},
    "name": "Ben",
    "surname": "Big"
  }
]
```

如果缺少该字段，我们将得到一个空结果。

## 4. 在Java中检查字段是否存在

在我们研究在Java中检查字段是否存在的可能方法之前，让我们将必要的[Mongo 依赖项添加到我们的项目](https://search.maven.org/search?q=a:mongo-java-driver AND g:org.mongodb)中。这是 Maven 依赖项：

```xml
<dependency>
    <groupId>org.mongodb</groupId>
    <artifactId>mongo-java-driver</artifactId>
    <version>3.12.10</version>
</dependency>
```

这是 Gradle 版本：

```bash
implementation group: 'org.mongodb', name: 'mongo-java-driver', version: '3.12.10'
```

最后，让我们连接到现有数据库和用户集合：

```java
MongoClient mongoClient = new MongoClient();
MongoDatabase db = mongoClient.getDatabase("existence");
MongoCollection<Document> collection = db.getCollection("users");
```

### 4.1. 使用过滤器

com.mongodb.client.model.Filters是来自 Mongo 依赖项的一个实用 类，它包含许多有用的方法。我们将在示例中使用exists()方法：

```java
Document nameDoc = collection.find(Filters.exists("name")).first();
assertNotNull(nameDoc);
assertFalse(nameDoc.isEmpty());
```

首先，我们尝试从users集合中查找元素并获取第一个找到的元素。如果指定的字段存在，我们会得到一个nameDoc文档作为响应。它不是 null 也不是空的。

现在，让我们看看当我们试图找到一个不存在的字段时会发生什么：

```java
Document nameDoc = collection.find(Filters.exists("non_existing")).first();
assertNull(nameDoc);
```

如果没有找到元素，我们将得到一个空文档作为响应。

### 4.2. 使用文档查询

com.mongodb.client.model.Filters类不是检查字段是否存在的唯一方法。我们可以使用com.mongodb.BasicDBObject 的实例：

```java
Document query = new Document("name", new BasicDBObject("$exists", true));
Document doc = collection.find(query).first();
assertNotNull(doc);
assertFalse(doc.isEmpty());
```

该行为与前面的示例相同。如果找到该元素，我们会收到一个 not null Document，它是空的。

当我们试图找到一个不存在的字段时，代码的行为也是一样的：

```java
Document query = new Document("non_existing", new BasicDBObject("$exists", true));
Document doc = collection.find(query).first();
assertNull(doc);
```

如果没有找到元素，我们将得到一个空 文档作为响应。

## 5.总结

在本文中，我们讨论了如何检查 MongoDB 中的字段是否存在。首先，我们展示了如何创建 Mongo 数据库、集合以及如何插入虚拟数据。然后，我们解释了如何使用基本查询在 Mongo shell 中检查字段是否存在。最后，我们解释了如何使用 com.mongodb.client.model.Filters和文档查询方法检查字段是否存在。
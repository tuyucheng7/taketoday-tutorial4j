## 1. 概述

默认情况下，[MongoDB](https://www.baeldung.com/java-mongodb)引擎在对提取的数据进行排序时会考虑字符大小写。可以通过指定Aggregations或Collations来执行不区分大小写的排序查询。

在这个简短的教程中，我们将研究使用 MongoDB Shell 和Java的两种解决方案。

## 2.搭建环境

首先，我们需要运行一个 MongoDB 服务器。让我们使用 Docker 镜像：

```shell
$ docker run -d -p 27017:27017 --name example-mongo mongo:latest
```

这将创建一个名为“ example-mongo ”的新临时 Docker 容器，并公开端口27017。现在，我们需要使用测试解决方案所需的数据创建一个基本的 Mongo 数据库。

首先，让我们在容器中打开一个 Mongo Shell：

```shell
$ docker exec -it example-mongo mongosh
```

进入 shell 后，让我们切换上下文并输入名为“ sorting ”的数据库：

```shell
> use sorting
```

最后，让我们插入一些数据来尝试我们的排序操作：

```shell
> db.users.insertMany([
  {name: "ben", surname: "ThisField" },
  {name: "aen", surname: "Does" },
  {name: "Aen", surname: "Not" },
  {name: "Ben", surname: "Matter" },
])
```

我们在一些文档的名称字段中插入了类似的值。唯一的区别是第一个字母的大小写。此时，数据库已创建并已适当插入数据，因此我们可以开始操作了。

## 3.默认排序

让我们运行没有自定义的标准查询：

```shell
> db.getCollection('users').find({}).sort({name:1})
```

返回的数据将根据情况进行排序。这意味着，例如，大写字符“ B”将在小写字符“ a”之前被考虑：

```shell
[
  {
    _id: ..., name: 'Aen', surname: 'Not'
  },
  {
    _id: ..., name: 'Ben', surname: 'Matter'
  },
  {
    _id: ..., name: 'aen', surname: 'Does'
  },
  {
    _id: ..., name: 'ben', surname: 'ThisField'
  }
]
```

现在让我们看看如何使我们的排序不区分大小写，以便Ben和 ben一起出现。

## 4. Mongo Shell 中不区分大小写的排序

### 4.1. 使用排序规则排序

让我们尝试使用[MongoDB Collation](https://docs.mongodb.com/manual/reference/collation/)。仅在 MongoDB 3.4 及后续版本中可用，它启用特定于语言的字符串比较规则。

Collation ICU locale参数驱动数据库如何进行排序。让我们使用“en”(英语)语言环境：

```shell
> db.getCollection('users').find({}).collation({locale: "en"}).sort({name:1})
```

这会产生名称按字母聚集的输出：

```shell
[
  {
    _id: ..., name: 'aen', surname: 'Does'
  },
  {
    _id: ..., name: 'Aen', surname: 'Not'
  },
  {
    _id: ..., name: 'ben', surname: 'ThisField'
  },
  {
    _id: ..., name: 'Ben', surname: 'Matter'
  }
]
```

### 4.2. 使用聚合排序

现在让我们使用聚合函数：

```shell
> db.getCollection('users').aggregate([{
        "$project": {
            "name": 1,
            "surname": 1,
            "lowerName": {
                "$toLower": "$name"
            }
        }
    },
    {
        "$sort": {
            "lowerName": 1
        }
    }
])
```

使用[$project](https://docs.mongodb.com/manual/reference/operator/aggregation/project/)功能，我们添加一个lowerName字段作为名称字段的小写版本。这允许我们使用该字段进行排序。它将按照所需的排序顺序为我们提供一个带有附加字段的结果对象：

```shell
[
  {
    _id: ..., name: 'aen', surname: 'Does', lowerName: 'aen'
  },
  {
    _id: ..., name: 'Aen', surname: 'Not', lowerName: 'aen'
  },
  {
    _id: ..., name: 'ben', surname: 'ThisField', lowerName: 'ben'
  },
  {
    _id: ..., name: 'Ben', surname: 'Matter', lowerName: 'ben'
  }
]
```

## 5. 使用Java进行不区分大小写的排序

让我们尝试在Java中实现相同的方法。

### 5.1. 配置样板代码

让我们首先添加[mongo-java-driver](https://search.maven.org/artifact/org.mongodb/mongo-java-driver)依赖：

```xml
<dependency>
    <groupId>org.mongodb</groupId>
    <artifactId>mongo-java-driver</artifactId>
    <version>3.12.10</version>
</dependency>
```

然后，让我们使用MongoClient进行连接：

```java
MongoClient mongoClient = new MongoClient();
MongoDatabase db = mongoClient.getDatabase("sorting");
MongoCollection<Document> collection = db.getCollection("users");
```

### 5.2. 在Java中使用排序规则进行排序

让我们看看如何在 Java中实现“排序规则”解决方案：

```java
FindIterable<Document> nameDoc = collection.find().sort(ascending("name"))
  .collation(Collation.builder().locale("en").build());
```

在这里，我们使用“en”语言环境构建了排序规则。然后，我们将创建的 Collation 对象传递[给 FindIterable](https://mongodb.github.io/mongo-java-driver/3.6/javadoc/com/mongodb/client/FindIterable.html)对象[的](https://mongodb.github.io/mongo-java-driver/3.6/javadoc/com/mongodb/client/model/Collation.html)collat ion方法。

接下来，让我们使用[MongoCursor](https://mongodb.github.io/mongo-java-driver/3.6/javadoc/index.html?com/mongodb/client/MongoCursor.html)一个一个地读取结果：

```java
MongoCursor cursor = nameDoc.cursor();
List expectedNamesOrdering = Arrays.asList("aen", "Aen", "ben", "Ben", "cen", "Cen");
List actualNamesOrdering = new ArrayList<>();
while (cursor.hasNext()) {
    Document document = cursor.next();
    actualNamesOrdering.add(document.get("name").toString());
}
assertEquals(expectedNamesOrdering, actualNamesOrdering);
```

### 5.3. 在Java中使用聚合进行排序

我们还可以使用聚合对集合进行排序。让我们使用JavaAPI 重新创建我们的命令行版本。

首先，我们依赖[project](https://mongodb.github.io/mongo-java-driver/3.6/javadoc/com/mongodb/client/model/Aggregates.html#project-org.bson.conversions.Bson-)方法创建一个[Bson](https://mongodb.github.io/mongo-java-driver/3.4/bson/)对象。该对象还将包含lowerName字段，该字段通过使用[Projections](https://mongodb.github.io/mongo-java-driver/3.6/javadoc/com/mongodb/client/model/Projections.html)类将名称的每个字符转换为小写来计算：

```java
Bson projectBson = project(
  Projections.fields(
    Projections.include("name","surname"),
    Projections.computed("lowerName", Projections.computed("$toLower", "$name"))));

```

接下来，我们向聚合方法提供一个包含前一个片段的[Bson](https://mongodb.github.io/mongo-java-driver/3.4/bson/)和排序 方法的列表：

```java
AggregateIterable<Document> nameDoc = collection.aggregate(
  Arrays.asList(projectBson,
  sort(Sorts.ascending("lowerName"))));

```

在这种情况下，与前一种情况一样，我们可以使用[MongoCursor](https://mongodb.github.io/mongo-java-driver/3.6/javadoc/index.html?com/mongodb/client/MongoCursor.html)轻松读取结果。

## 六. 总结

在本文中，我们了解了如何对 MongoDB 集合执行简单的不区分大小写的排序。

我们在 MongoDB shell 中使用了聚合和整理方法。最后，我们翻译了这些查询并使用mongo-java-driver库提供了一个简单的Java实现。
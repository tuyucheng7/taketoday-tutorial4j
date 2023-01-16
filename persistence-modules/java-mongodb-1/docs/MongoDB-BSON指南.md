## 1. 简介

在本教程中，我们将研究[BSON](http://bsonspec.org/)以及如何使用它与[MongoDB](https://www.mongodb.com/)进行交互。

现在，对 MongoDB 及其所有功能的深入描述超出了本文的范围。但是，了解一些关键概念会很有用。

MongoDB 是一个分布式的 NoSQL 文档存储引擎。文档存储为 BSON 数据，并分组到集合中。集合中的文档类似于关系数据库表中的行。

要更深入地了解，请查看[介绍性 MongoDB 文章](https://www.baeldung.com/java-mongodb)。

## 2.什么是BSON？

BSON 代表二进制 JSON。它是一种用于对类似 JSON 的数据进行二进制序列化的协议。

JSON 是一种在现代 Web 服务中流行的数据交换格式。它提供了一种灵活的方式来表示复杂的数据结构。

与使用常规 JSON 相比，BSON 具有以下几个优势：

-   紧凑：在大多数情况下，存储BSON 结构需要的空间少于其等效的 JSON
-   数据类型：BSON 提供了常规 JSON 中没有的其他数据类型，例如Date和BinData

使用 BSON 的主要好处之一是易于遍历。BSON 文档包含额外的元数据，允许轻松操作文档的字段，而无需阅读整个文档本身。

## 3. MongoDB 驱动程序

现在我们对 BSON 和 MongoDB 有了基本的了解，让我们看看如何一起使用它们。我们将重点关注 CRUD 首字母缩略词(C reate、R ead、Update、D elete)中的主要操作。

MongoDB为大多数现代编程语言提供[软件驱动程序。](https://docs.mongodb.com/ecosystem/drivers/)驱动程序构建在 BSON 库之上，这意味着我们在构建查询时将直接使用 BSON API。有关详细信息，请参阅我们[的 MongoDB 查询语言指南](https://www.baeldung.com/queries-in-spring-data-mongodb)。

在本节中，我们将了解如何使用驱动程序连接到集群，并使用 BSON API 执行不同类型的查询。注意，MongoDB 驱动提供了一个[Filters类](https://mongodb.github.io/mongo-java-driver/3.10/javadoc/?com/mongodb/client/model/Filters.html)，可以帮助我们编写更紧凑的代码。然而，对于本教程，我们将只专注于使用核心 BSON API。

作为直接使用 MongoDB 驱动程序和 BSON 的替代方法，请查看我们的[Spring Data MongoDB 指南](https://www.baeldung.com/spring-data-mongodb-guide)。

### 3.1. 连接中

首先，我们首先将[MongoDB 驱动程序](https://search.maven.org/search?q=g:org.mongodb AND a:mongodb-driver-sync)作为依赖项添加到我们的应用程序中：

```xml
<dependency>
    <groupId>org.mongodb</groupId>
    <artifactId>mongodb-driver-sync</artifactId>
    <version>3.10.1</version>
</dependency>
```

然后我们创建一个到 MongoDB 数据库和集合的连接：

```java
MongoClient mongoClient = MongoClients.create();
MongoDatabase database = mongoClient.getDatabase("myDB");
MongoCollection<Document> collection = database.getCollection("employees");
```

其余部分将着眼于使用集合引用创建查询。

### 3.2. 插入

假设我们有以下 JSON，我们希望将其作为新文档插入到employees集合中：

```plaintext
{
  "first_name" : "Joe",
  "last_name" : "Smith",
  "title" : "Java Developer",
  "years_of_service" : 3,
  "skills" : ["java","spring","mongodb"],
  "manager" : {
     "first_name" : "Sally",
     "last_name" : "Johanson"
  }
}
```

这个 JSON 示例显示了我们在处理 MongoDB 文档时会遇到的最常见的数据类型：文本、数字、数组和嵌入式文档。

要使用 BSON 插入它，我们将使用 MongoDB 的文档API：

```java
Document employee = new Document()
    .append("first_name", "Joe")
    .append("last_name", "Smith")
    .append("title", "Java Developer")
    .append("years_of_service", 3)
    .append("skills", Arrays.asList("java", "spring", "mongodb"))
    .append("manager", new Document()
                           .append("first_name", "Sally")
                           .append("last_name", "Johanson"));
collection.insertOne(employee);

```

Document类是 BSON 中使用的主要 API 。它扩展了JavaMap接口并包含几个重载方法。这使得使用本机类型以及常见对象(如对象 ID、日期和列表)变得容易。

### 3.3. 寻找

为了在 MongoDB 中查找文档，我们提供了一个搜索文档来指定要查询的字段。例如，要查找姓氏为“Smith”的所有文档，我们将使用以下 JSON 文档：

```plaintext
{  
  "last_name": "Smith"
}
```

用 BSON 写成：

```plaintext
Document query = new Document("last_name", "Smith");
List results = new ArrayList<>();
collection.find(query).into(results);
```

“查找”查询可以接受多个字段，默认行为是使用逻辑和运算符将它们组合起来。这意味着只返回匹配所有字段的文档。

为了解决这个问题，MongoDB 提供了or查询运算符：

```plaintext
{
  "$or": [
    { "first_name": "Joe" },
    { "last_name":"Smith" }
  ]
}
```

这将找到名字为“Joe”或姓氏为“Smith”的所有文档。要将其写为 BSON，我们将使用嵌套文档，就像上面的插入查询一样：

```java
Document query = 
  new Document("$or", Arrays.asList(
      new Document("last_name", "Smith"),
      new Document("first_name", "Joe")));
List results = new ArrayList<>();
collection.find(query).into(results);
```

### 3.4. 更新

更新查询在 MongoDB 中略有不同，因为它们需要两个文档：

1.  查找一个或多个文档的过滤条件
2.  指定要修改哪些字段的更新文档

例如，假设我们要为每个已经拥有“spring”技能的员工添加“安全”技能。第一个文档将找到所有具有“spring”技能的员工，第二个文档将在他们的技能数组中添加一个新的“security”条目。

在 JSON 中，这两个查询看起来像：

```plaintext
{
  "skills": { 
    $elemMatch:  { 
      "$eq": "spring"
    }
  }
}

{
  "$push": { 
    "skills": "security"
  }
}
```

在 BSON 中，它们将是：

```java
Document query = new Document(
  "skills",
  new Document(
    "$elemMatch",
    new Document("$eq", "spring")));
Document update = new Document(
  "$push",
  new Document("skills", "security"));
collection.updateMany(query, update);

```

### 3.5. 删除

MongoDB 中的删除查询使用与查找查询相同的语法。我们只是提供一个文档，指定一个或多个要匹配的条件。

例如，假设我们在我们的员工数据库中发现了一个错误，并且不小心创建了一个服务年限为负值的员工。要找到所有这些，我们将使用以下 JSON：

```plaintext
{
  "years_of_service" : { 
    "$lt" : 0
  }
}
```

等效的 BSON 文档将是：

```java
Document query = new Document(
  "years_of_service", 
  new Document("$lt", 0));
collection.deleteMany(query);
```

## 4. 总结

在本教程中，我们看到了使用 BSON 库构建 MongoDB 查询的基本介绍。仅使用 BSON API，我们为 MongoDB 集合实现了基本的 CRUD 操作。

我们没有涵盖的是更高级的主题，例如投影、聚合、地理空间查询、批量操作等。所有这些都可以仅使用 BSON 库。我们在这里看到的示例构成了我们将用来实现这些更高级操作的构建块。
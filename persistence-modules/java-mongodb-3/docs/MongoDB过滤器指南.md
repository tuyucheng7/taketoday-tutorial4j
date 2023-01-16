## 1. 概述

在本教程中，我们将学习如何使用[过滤器](https://www.mongodb.com/docs/drivers/java/sync/current/fundamentals/builders/filters/)[构建器为MongoDB](https://www.baeldung.com/java-mongodb)中的查询指定过滤器。

Filters类是一种帮助我们构建查询过滤器的构建器。过滤器是 MongoDB 用来根据特定条件限制结果的操作。

## 2. 建造者的种类

Java MongoDB Driver 提供了各种类型的[构建器](https://www.mongodb.com/docs/drivers/java/sync/current/fundamentals/builders/)来帮助我们构建[BSON](https://www.baeldung.com/mongodb-bson)文档。构建器提供了一个方便的 API 来简化执行各种 CRUD 和聚合操作的过程。

让我们回顾一下可用的不同类型的构建器：

-   用于构建查询过滤器的过滤器
-   用于构建字段投影的投影，指定要包含和排除哪些字段
-   用于构建排序标准的排序
-   建筑更新操作的更新
-   用于构建聚合管道的聚合
-   用于构建索引键的索引

现在让我们深入探讨在 MongoDB中使用过滤器的不同方式。

## 3.数据库初始化

首先，为了演示各种过滤操作，让我们设置一个数据库baeldung和一个示例集合，用户：

```plaintext
use baeldung;
db.createCollection("user");
```

此外，让我们将一些文档填充到用户集合中：

```plaintext
db.user.insertMany([
{
    "userId":"123",
    "userName":"Jack",
    "age":23,
    "role":"Admin"
},
{
    "userId":"456",
    "userName":"Lisa",
    "age":27,
    "role":"Admin",
    "type":"Web"
},
{
    "userId":"789",
    "userName":"Tim",
    "age":31,
    "role":"Analyst"
}]);
```

成功插入后，上述查询将返回确认结果的响应：

```css
{
    "acknowledged" : true,
    "insertedIds" : [
        ObjectId("6357c4736c9084bcac72eced"),
        ObjectId("6357c4736c9084bcac72ecee"),
        ObjectId("6357c4736c9084bcac72ecef")
    ]
}
```

我们将使用此集合作为我们所有过滤器操作示例的样本。

## 4. 使用过滤器类

如前所述，过滤器是 MongoDB 用来将结果限制为我们希望看到的内容的操作。Filters类为不同类型的 MongoDB 操作提供了各种静态工厂方法。每个方法都返回一个 BSON 类型，然后可以将其传递给任何需要查询过滤器的方法。

当前 MongoDBJavaDriver API 中的Filters类替换了 Legacy API 中的[QueryBuilder](https://mongodb.github.io/mongo-java-driver/4.7/apidocs/mongodb-driver-legacy/com/mongodb/QueryBuilder.html)。

过滤器根据操作类型进行分类：条件、逻辑、数组、元素、评估、按位和地理空间。

接下来，让我们看看一些最常用的Filters方法。

### 4.1. eq()方法

Filters.eq ()方法创建一个过滤器，匹配指定字段的值等于指定值的所有文档。

首先，让我们看一下 MongoDB Shell 查询以过滤用户名等于“Jack”的用户集合文档：

```matlab
db.getCollection('user').find({"userName":"Jack"})
```

上面的查询返回用户集合中的单个文档：

```css
{
    "_id" : ObjectId("6357c4736c9084bcac72eced"),
    "userId" : "123",
    "userName" : "Jack",
    "age" : 23.0,
    "role" : "Admin"
}
```

现在，让我们看一下相应的 MongoDBJavaDriver 代码：

```java
Bson filter = Filters.eq("userName", "Jack");
FindIterable<Document> documents = collection.find(filter);

MongoCursor<Document> cursor = documents.iterator();
while (cursor.hasNext()) {
    System.out.println(cursor.next());
}
```

我们可以注意到Filters.eq()方法返回一个 BSON 类型，然后我们将其作为过滤器传递给[find()方法。](https://www.baeldung.com/mongodb-find)

此外，Filters.ne()与Filters.eq()方法正好相反——它匹配指定字段的值不等于指定值的所有文档：

```java
Bson filter = Filters.ne("userName", "Jack");
FindIterable<Document> documents = collection.find(filter);

MongoCursor<Document> cursor = documents.iterator();
while (cursor.hasNext()) {
    System.out.println(cursor.next());
}
```

### 4.2. gt()方法

Filters.gt ()方法创建一个过滤器，匹配指定字段的值大于指定值的所有文档。

让我们看一个例子：

```java
Bson filter = Filters.gt("age", 25);
FindIterable<Document> documents = collection.find(filter);

MongoCursor<Document> cursor = documents.iterator();
while (cursor.hasNext()) {
    System.out.println(cursor.next());
}
```

上面的代码片段获取所有年龄大于25岁的用户集合文档。就像Filters.gt()方法一样，还有一个Filters.lt()方法匹配所有 named 字段值小于的文档比指定值：

```java
Bson filter = Filters.lt("age", 25);
FindIterable<Document> documents = collection.find(filter);

MongoCursor<Document> cursor = documents.iterator();
while (cursor.hasNext()) {
    System.out.println(cursor.next());
}
```

此外，还有Filters.gte()和Filters.lte()方法分别匹配大于或等于和小于或等于指定值的值。

### 4.3. in()方法

Filters.in ()方法创建一个过滤器，匹配所有文档，其中指定字段的值等于指定值列表中的任何值。

让我们看看它的实际效果：

```java
Bson filter = Filters.in("userName", "Jack", "Lisa");
FindIterable<Document> documents = collection.find(filter);

MongoCursor<Document> cursor = documents.iterator();
while (cursor.hasNext()) {
    System.out.println(cursor.next());
}
```

此代码片段获取用户名等于“Jack”或“Lisa”的所有用户集合文档。

就像Filters.in()方法一样，有一个Filters.nin()方法匹配指定字段的值不等于指定值列表中的任何值的所有文档：

```java
Bson filter = Filters.nin("userName", "Jack", "Lisa");
FindIterable<Document> documents = collection.find(filter);

MongoCursor<Document> cursor = documents.iterator();
while (cursor.hasNext()) {
    System.out.println(cursor.next());
}
```

### 4.4. and()方法

Filters.and()方法创建一个过滤器，该过滤器对提供的过滤器列表执行逻辑 AND 操作。

让我们找到年龄大于25且角色等于“Admin”的所有用户集合文档：

```java
Bson filter = Filters.and(Filters.gt("age", 25), Filters.eq("role", "Admin"));
FindIterable<Document> documents = collection.find(filter);

MongoCursor<Document> cursor = documents.iterator();
while (cursor.hasNext()) {
    System.out.println(cursor.next());
}
```

### 4.5. or()方法

如我们所料，Filters.or()方法创建一个过滤器，该过滤器对提供的过滤器列表执行逻辑 OR 操作。

让我们编写一个代码片段，返回所有年龄大于30或角色等于“Admin”的用户集合文档：

```java
Bson filter = Filters.or(Filters.gt("age", 30), Filters.eq("role", "Admin"));
FindIterable<Document> documents = collection.find(filter);

MongoCursor<Document> cursor = documents.iterator();
while (cursor.hasNext()) {
    System.out.println(cursor.next());
}
```

### 4.6. exists()方法

此外，Filters.exists()方法创建一个过滤器，匹配包含给定字段的所有文档：

```java
Bson filter = Filters.exists("type");
FindIterable<Document> documents = collection.find(filter);

MongoCursor<Document> cursor = documents.iterator();
while (cursor.hasNext()) {
    System.out.println(cursor.next());
}
```

上面的代码返回所有具有类型字段的用户集合文档。

### 4.7. regex()方法

最后，Filters.regex()方法创建一个过滤器来匹配文档，其中指定字段的值与给定的正则表达式模式匹配：

```java
Bson filter = Filters.regex("userName", "a");
FindIterable<Document> documents = collection.find(filter);

MongoCursor<Document> cursor = documents.iterator();
while (cursor.hasNext()) {
    System.out.println(cursor.next());
}
```

在这里，我们获取了所有用户名与正则表达式“a”匹配的用户集合文档。

到目前为止，我们已经讨论了一些最常用的过滤器运算符。我们可以使用查询[过滤器运算符](https://www.mongodb.com/docs/manual/reference/operator/query/)的任意组合作为find()方法的过滤器。

此外，过滤器还可以用在其他各种地方，例如聚合的匹配阶段、deleteOne()方法和updateOne()方法等。

## 5.总结

在本文中，我们首先讨论了如何使用过滤器构建器对 MongoDB 集合执行过滤器操作。然后我们看到了如何使用 MongoDBJavaDriver API 实现一些最常用的过滤器操作。
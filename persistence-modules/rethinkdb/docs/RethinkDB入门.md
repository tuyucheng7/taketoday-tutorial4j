## **一、简介**

**在本文中，我们将了解[RethinkDB](https://rethinkdb.com/)。**这是一个开源的 NoSQL 数据库，专为实时应用程序而设计。我们将看到它为我们的应用程序带来了哪些特性，我们可以用它做什么，以及如何与它交互。

## **2. 什么是 RethinkDB？**

RethinkDB 是一个强调可伸缩性和高可用性的开源 NoSQL 数据库。它允许我们存储稍后可以查询的 JSON 文档。我们还能够在我们的数据库中执行跨多个表的连接，并对我们的数据执行[map-reduce](https://www.baeldung.com/cs/mapreduce-algorithm)函数。

然而，**让 RethinkDB 脱颖而出的是它的实时流功能**。我们可以对我们的数据库执行查询，以便对结果集的更改不断地流回客户端，从而使我们能够实时更新我们的数据。这意味着我们的应用程序可以在任何变化时立即向我们的用户提供更新。

## **3. 运行和使用 RethinkDB**

RethinkDB 是一个用 C++ 编写的本地应用程序。大多数平台都可以使用[预构建的包。](https://rethinkdb.com/docs/install/)还有一个[官方 Docker 镜像](https://registry.hub.docker.com/_/rethinkdb/)。

安装后，我们可以通过简单地运行可执行文件来启动数据库。如有必要，我们可以告诉它存储数据文件的位置，但如果不需要，这将有一个合理的默认值。我们还可以配置它侦听的端口，甚至可以在集群配置中运行多个服务器以实现扩展和可用性。所有这些以及更多内容都可以在[官方文档](https://rethinkdb.com/docs/start-a-server/)中看到。

然后我们需要实际使用我们应用程序中的数据库。这将要求我们使用适当的[客户端](https://rethinkdb.com/docs/install-drivers/)连接到它——具有多种语言的选项。但是，对于本文，我们将使用[Java 客户端](https://rethinkdb.com/docs/install-drivers/java/)。

将客户端添加到我们的应用程序就像添加一个依赖项一样简单：

```xml
<dependency>
    <groupId>com.rethinkdb</groupId>
    <artifactId>rethinkdb-driver</artifactId>
    <version>2.4.4</version>
</dependency>复制
```

接下来，我们实际上需要连接到数据库。为此，我们需要一个*连接*：

```java
Connection conn = RethinkDB.r.connection()
  .hostname("localhost")
  .port(28015)
  .connect();复制
```

## **4. 与 RethinkDB 交互**

现在我们已经连接到 RethinkDB，我们需要知道如何使用它。**在数据库的基础层面，这意味着我们需要能够创建、操作和检索数据。**

与 RethinkDB 的所有交互都是通过编程接口完成的。我们不是使用自定义查询语言编写查询，而是使用更丰富的类型模型在标准 Java 中编写它们。这给了我们让编译器确保我们的查询有效而不是在运行时发现我们有问题的优势。

### **4.1. 使用表格**

**RethinkDB 实例公开了多个数据库，每个数据库都将数据存储在表中。**这些在概念上类似于 SQL 数据库中的表。但是，RethinkDB 不会在我们的表上强制执行模式，而是将其留给应用程序。

我们可以使用我们的连接创建一个新表：

```java
r.db("test").tableCreate(tableName).run(conn);复制
```

同样，我们可以使用 Db.tableDrop() 删除表*。*我们还可以使用*Db.tableList()*列出所有已知的表：

```java
r.db(dbName).tableCreate(tableName).run(conn);
List<String> tables = r.db(dbName).tableList().run(conn, List.class).first();
assertTrue(tables.contains(tableName));
复制
```

### **4.2. 插入数据**

一旦我们有了要使用的表，我们就需要能够填充它们。**我们可以通过使用\*Table.insert()\*并为它提供数据来做到这一点。**

让我们通过提供由 RethinkDB API 本身构造的对象来将一些数据插入到我们的表中：

```java
r.db(DB_NAME).table(tableName)
  .insert(r.hashMap().with("name", "Baeldung"))
  .run(conn);
复制
```

或者，我们可以提供标准的 Java 集合：

```java
r.db(DB_NAME).table(tableName)
  .insert(Map.of("name", "Baeldung"))
  .run(conn);
复制
```

**我们插入的数据可以像单个键/值对一样简单，也可以像需要的那样复杂。**这可以包括嵌套结构、数组或任何需要的东西：

```java
r.db(DB_NAME).table(tableName)
  .insert(
    r.hashMap()
      .with("name", "Baeldung")
      .with("articles", r.array(
        r.hashMap()
          .with("id", "article1")
          .with("name", "String Interpolation in Java")
          .with("url", "https://www.baeldung.com/java-string-interpolation"),
        r.hashMap()
          .with("id", "article2")
          .with("name", "Access HTTPS REST Service Using Spring RestTemplate")
          .with("url", "https://www.baeldung.com/spring-resttemplate-secure-https-service"))
      )
).run(conn);
复制
```

插入的每条记录都有一个唯一的 ID——可以是我们在记录中作为“id”字段提供的 ID，也可以是从数据库中随机生成的 ID。

### **4.3. 检索数据**

**现在我们有了一个包含一些数据的数据库，我们需要能够再次将其取出。**与所有数据库一样，我们通过查询数据库来完成此操作。

我们可以做的最简单的事情就是查询一个表而不需要任何额外的东西：

```java
Result<Map> results = r.db(DB_NAME).table(tableName).run(conn, Map.class);
复制
```

我们的结果对象为我们提供了多种访问结果的方法，包括能够将其直接视为迭代器：

```java
for (Map result : results) {
    // Process result
}复制
```

我们还可以将结果转换为*List*或*Stream——*包括并行流——如果我们随后想将结果视为普通的 Java 集合。

**如果我们只想检索结果的一个子集，我们可以在运行查询时应用过滤器。**这是通过提供 Java [lambda](https://www.baeldung.com/java-8-lambda-expressions-tips)来执行查询来完成的：

```java
Result<Map> results = r.db(DB_NAME)
  .table(tableName)
  .filter(r -> r.g("name").eq("String Interpolation in Java"))
  .run(conn, Map.class);
复制
```

我们的过滤器根据表中的行进行评估，只有匹配的那些才会返回到我们的结果集中。

**如果我们知道 ID 值，我们也可以直接转到单行：**

```java
Result<Map> results = r.db(DB_NAME).table(tableName).get(id).run(conn, Map.class);
复制
```

### **4.4. 更新和删除数据**

一个一旦插入就不能改变数据的数据库只有有限的用途，那么我们如何更新我们的数据呢？**RethinkDB API 为我们提供了一个\*update()\*方法，我们可以将其链接到查询语句的末尾** ，以便将这些更新应用于与查询匹配的每条记录。

**这些更新是补丁，而不是完全替换**，所以我们只指定我们想要进行的更改：

```java
r.db(DB_NAME).table(tableName).update(r.hashMap().with("site", "Baeldung")).run(conn);
复制
```

**与查询一样，我们可以使用过滤器准确选择要更新的记录。** 这些需要在指定更新之前完成。这是因为过滤器实际上应用于选择要更新的记录的查询，然后更新应用于匹配的所有内容：

```java
r.db(DB_NAME).table(tableName)
  .filter(r -> r.g("name").eq("String Interpolation in Java"))
  .update(r.hashMap().with("category", "java"))
  .run(conn);复制
```

我们还可以通过在查询结束时使用***delete()\*****调用**而不是*update()***以类似的方式删除记录：**

```java
r.db(DB_NAME).table(tableName)
  .filter(r -> r.g("name").eq("String Interpolation in Java"))
  .delete()
  .run(conn);复制
```

## **5.实时更新**

到目前为止，我们已经看到了一些如何与 RethinkDB 交互的示例，但是这些都没有什么特别之处。我们所看到的一切也可以通过大多数其他数据库系统实现。

**RethinkDB 的特别之处在于能够在我们的应用程序无需轮询数据的情况下实时更新我们的数据。**相反，我们可以以游标保持打开的方式执行查询，并且数据库会在发生任何更改时将其推送给我们：

```java
Result<Map> cursor = r.db(DB_NAME).table(tableName).changes().run(conn, Map.class);
cursor.stream().forEach(record -> System.out.println("Record: " + record));复制
```

这在编写我们想要立即更新的实时应用程序时非常强大——例如，显示实时股票价格、游戏比分或许多其他内容。

当我们执行这样的查询时，我们会像以前一样得到一个游标。但是，添加*changes()*意味着我们不会查询已经存在的记录。相反，**游标将为我们提供与查询匹配的记录发生的变化的无限集合**。这包括插入、更新和删除。

游标是无界的这一事实意味着我们对其执行的任何迭代，无论是使用普通的*for*循环还是流，都将根据需要持续进行。我们不能安全地做的是收集到一个列表，因为列表没有尽头。

我们在游标中返回的记录包括更改记录的新值和旧值。然后我们可以确定更改是否是插入（因为没有旧值）、删除（因为没有新值）或更新（因为同时存在旧值和新值）。我们还可以在更新中看到旧值和新值之间的差异并做出相应的反应。

与所有查询一样，我们也可以在获取记录更改时应用过滤器。**这将导致我们的游标仅包含与此过滤器匹配的记录。**这甚至适用于插入，其中记录在执行时不存在：

```java
Result<Map> cursor = r.db(DB_NAME).table(tableName)
  .filter(r -> r.g("index").eq(5))
  .changes()
  .run(conn, Map.class);复制
```

## **六，结论**

**我们在这里看到了对 RethinkDB 数据库引擎的非常简短的介绍**，展示了我们如何将它用于我们所有的传统数据库任务，以及如何利用其自动将更改推送到我们的客户端的独特功能。这只是一个快速浏览，这个系统还有很多，为什么不自己试试呢？
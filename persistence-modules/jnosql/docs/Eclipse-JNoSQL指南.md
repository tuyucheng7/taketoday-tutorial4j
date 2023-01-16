## 1. 概述

Eclipse [JNoSQL](https://projects.eclipse.org/projects/technology.jnosql)是一组 API 和实现，可简化Java应用程序与 NoSQL 数据库的交互。

在本文中，我们将学习如何设置和配置 JNoSQL 以与 NoSQL 数据库交互。我们将同时使用通信层和映射层。

## 2. Eclipse JNoSQL 通信层

从技术上讲，通信层由两个模块组成：Diana API 和驱动程序。

虽然 API 定义了对 NoSQL 数据库类型的抽象，但驱动程序提供了大多数已知数据库的实现。

我们可以将其与关系数据库中的 JDBC API 和 JDBC 驱动程序进行比较。

### 2.1. Eclipse JNoSQL 戴安娜 API

简单来说，NoSQL 数据库有四种基本类型：Key-Value、Column、Document 和 Graph。

Eclipse JNoSQL Diana API 定义了三个模块：

1.  戴安娜键值
2.  戴安娜专栏
3.  戴安娜文件

API 未涵盖 NoSQL 图类型，因为它已被 [Apache ThinkerPop](https://tinkerpop.apache.org/)涵盖。

API 基于核心模块 diana-core，并定义了对常见概念的抽象，例如 Configuration、Factory、Manager、Entity 和 Value。

要使用 API，我们需要提供相应模块对我们的 NoSQL 数据库类型的依赖性。

因此，对于面向文档的数据库，我们需要 diana-document依赖：

```xml
<dependency>
    <groupId>org.jnosql.diana</groupId>
    <artifactId>diana-document</artifactId>
    <version>0.0.6</version>
</dependency>
```

同样，如果工作的 NoSQL 数据库是面向键值的，我们应该使用diana-key-value模块：

```xml
<dependency>
    <groupId>org.jnosql.diana</groupId>
    <artifactId>diana-key-value</artifactId>
    <version>0.0.6</version>
</dependency>
```

最后，diana-column模块，如果它是面向列的：

```xml
<dependency>
    <groupId>org.jnosql.diana</groupId>
    <artifactId>diana-column</artifactId>
    <version>0.0.6</version>
</dependency>
```

最新版本可以在 [Maven Central](https://search.maven.org/classic/#search|ga|1|org.jnosql.diana)上找到。

### 2.2. Eclipse JNoSQL 戴安娜驱动程序

驱动程序是最常见的 NoSQL 数据库的一组 API 实现。

每个 NoSQL 数据库有一个实现。如果数据库是多模型的，驱动程序应该实现所有支持的 API。

例如，couchbase-driver同时实现了diana-document和diana-key-value，因为 [Couchbase](https://www.couchbase.com/)是面向文档和键值的。

与关系数据库不同，驱动程序通常由数据库供应商提供，这里的驱动程序由 Eclipse JNoSQL 提供。在大多数情况下，此驱动程序是官方供应商库的包装器。

要开始使用驱动程序，我们应该包括 API 和所选 NoSQL 数据库的相应实现。

例如，对于 MongoDB，我们需要包含以下依赖项：

```xml
<dependency>
    <groupId>org.jnosql.diana</groupId>
    <artifactId>diana-document</artifactId>
    <version>0.0.6</version>
</dependency>
<dependency>
    <groupId>org.jnosql.diana</groupId>
    <artifactId>mongodb-driver</artifactId>
    <version>0.0.6</version>
</dependency>
```

使用驱动程序背后的过程很简单。

首先，我们需要一个配置 bean。通过从类路径或硬编码值读取配置文件， 配置能够创建工厂。 然后我们用它来创建一个管理器。

最后，Manager负责向 NoSQL 数据库推送和检索Entity。

在接下来的小节中，我们将针对每种 NoSQL 数据库类型解释此过程。

### 2.3. 使用面向文档的数据库

在此示例中，我们将使用嵌入式 MongoDB，因为它易于上手且不需要安装。它是面向文档的，以下说明适用于任何其他面向文档的 NoSQL 数据库。

在一开始，我们应该提供应用程序正确与数据库交互所需的所有必要设置。 在最基本的形式中，我们应该提供 MongoDB 运行实例的主机和端口。

我们可以在位于类路径的mongodb-driver.properties中提供这些设置：

```plaintext
#Define Host and Port
mongodb-server-host-1=localhost:27017
```

或者作为硬编码值：

```java
Map<String, Object> map = new HashMap<>();
map.put("mongodb-server-host-1", "localhost:27017");
```

接下来，我们为文档类型创建配置bean：

```java
DocumentConfiguration configuration = new MongoDBDocumentConfiguration();
```

从这个Configuration bean，我们能够创建一个ManagerFactory：

```java
DocumentCollectionManagerFactory managerFactory = configuration.get();
```

Configuration bean 的get() 方法隐含地使用属性文件中的设置。我们也可以从硬编码值中获取这个工厂：

```java
DocumentCollectionManagerFactory managerFactory 
  = configuration.get(Settings.of(map));
```

ManagerFactory有一个简单的方法get()，它将数据库名称作为参数，并创建Manager：

```java
DocumentCollectionManager manager = managerFactory.get("my-db");
```

最后，我们准备好了。Manager 提供了所有必要的 方法来通过DocumentEntity 与底层 NoSQL 数据库进行交互。

因此，例如，我们可以插入一个文档：

```java
DocumentEntity documentEntity = DocumentEntity.of("books");
documentEntity.add(Document.of("_id", "100"));
documentEntity.add(Document.of("name", "JNoSQL in Action"));
documentEntity.add(Document.of("pages", "620"));
DocumentEntity saved = manager.insert(documentEntity);
```

我们还可以搜索文档：

```java
DocumentQuery query = select().from("books").where("_id").eq(100).build();
List<DocumentEntity> entities = manager.select(query);
```

以类似的方式，我们可以更新现有文档：

```java
saved.add(Document.of("author", "baeldung"));
DocumentEntity updated = manager.update(saved);
```

最后，我们可以删除存储的文档：

```java
DocumentDeleteQuery deleteQuery = delete().from("books").where("_id").eq("100").build();
manager.delete(deleteQuery);
```

要运行示例，我们只需要访问jnosql-diana模块并运行DocumentApp应用程序。

我们应该在控制台中看到输出：

```bash
DefaultDocumentEntity{documents={pages=620, name=JNoSQL in Action, _id=100}, name='books'}
DefaultDocumentEntity{documents={pages=620, author=baeldung, name=JNoSQL in Action, _id=100}, name='books'}
[]
```

### 2.4. 使用面向列的数据库

出于本节的目的，我们将使用嵌入式版本的 Cassandra 数据库，因此无需安装。

使用面向列的数据库的过程非常相似。首先，我们将 Cassandra 驱动程序和列 API 添加到 pom 中：

```xml
<dependency>
    <groupId>org.jnosql.diana</groupId>
    <artifactId>diana-column</artifactId>
    <version>0.0.6</version>
</dependency>
<dependency>
    <groupId>org.jnosql.diana</groupId>
    <artifactId>cassandra-driver</artifactId>
    <version>0.0.6</version>
</dependency>
```

接下来，我们需要在类路径上的配置文件diana-cassandra.properties 中指定的配置设置 。或者，我们也可以使用硬编码的配置值。

然后，使用类似的方法，我们将创建一个ColumnFamilyManager并开始操作 ColumnEntity：

```java
ColumnConfiguration configuration = new CassandraConfiguration();
ColumnFamilyManagerFactory managerFactory = configuration.get();
ColumnFamilyManager entityManager = managerFactory.get("my-keySpace");
```

因此，要创建一个新实体，让我们调用insert()方法：

```java
ColumnEntity columnEntity = ColumnEntity.of("books");
Column key = Columns.of("id", 10L);
Column name = Columns.of("name", "JNoSQL in Action");
columnEntity.add(key);
columnEntity.add(name);
ColumnEntity saved = entityManager.insert(columnEntity);
```

要运行示例并在控制台中查看输出，请运行ColumnFamilyApp应用程序。

### 2.5. 使用面向键值的数据库

在本节中，我们将使用 Hazelcast。Hazelcast 是一个面向键值的 NoSQL 数据库。有关 Hazelcast 数据库的更多信息，你可以查看此[链接](https://www.baeldung.com/java-hazelcast)。

使用面向键值类型的过程也类似。我们首先将这些依赖项添加到 pom：

```xml
<dependency>
    <groupId>org.jnosql.diana</groupId>
    <artifactId>diana-key-value</artifactId>
    <version>0.0.6</version>
</dependency>
<dependency>
    <groupId>org.jnosql.diana</groupId>
    <artifactId>hazelcast-driver</artifactId>
    <version>0.0.6</version>
</dependency>
```

然后我们需要提供配置设置。接下来，我们可以获得一个BucketManager，然后操作 KeyValueEntity：

```java
KeyValueConfiguration configuration = new HazelcastKeyValueConfiguration();
BucketManagerFactory managerFactory = configuration.get();
BucketManager entityManager = managerFactory.getBucketManager("books");

```

假设我们要保存以下Book模型：

```java
public class Book implements Serializable {

    private String isbn;
    private String name;
    private String author;
    private int pages;

    // standard constructor
    // standard getters and setters
}
```

所以我们创建一个Book实例，然后我们通过调用put()方法保存它；

```java
Book book = new Book(
  "12345", "JNoSQL in Action", 
  "baeldung", 420);
KeyValueEntity keyValueEntity = KeyValueEntity.of(
  book.getIsbn(), book);
entityManager.put(keyValueEntity);
```

然后检索保存的Book实例：

```java
Optional<Value> optionalValue = manager.get("12345");
Value value = optionalValue.get(); // or any other adequate Optional handling
Book savedBook = value.get(Book.class);
```

要运行示例并在控制台中查看输出，请运行KeyValueApp应用程序。

## 3. Eclipse JNoSQL 映射层

映射层 Artemis API 是一组 API，可帮助将 java 注解对象映射到 NoSQL 数据库。它基于 Diana API 和 CDI(上下文和依赖注入)。

我们可以将此 API 视为 NoSQL 世界中的 JPA 或 ORM。该层还为每种 NoSQL 类型提供了一个 API，并为通用功能提供了一个核心 API。

在本节中，我们将使用 MongoDB 面向文档的数据库。

### 3.1. 必需的依赖项

要在应用程序中启用 Artemis，我们需要添加[artemis-configuration](https://search.maven.org/classic/#search|ga|1|artemis-configuration) 依赖项。由于MongoDB是面向文档的，所以 还需要依赖[artemis-document 。](https://search.maven.org/classic/#search|ga|1|artemis-document)

对于其他类型的 NoSQL 数据库，我们将使用artemis-column、artemis-key-value和artemis-graph。

还需要 MongoDB 的 Diana 驱动程序：

```xml
<dependency>
    <groupId>org.jnosql.artemis</groupId>
    <artifactId>artemis-configuration</artifactId>
    <version>0.0.6</version>
</dependency>
<dependency>
    <groupId>org.jnosql.artemis</groupId>
    <artifactId>artemis-document</artifactId>
    <version>0.0.6</version>
</dependency>
<dependency>
    <groupId>org.jnosql.diana</groupId>
    <artifactId>mongodb-driver</artifactId>
    <version>0.0.6</version>
</dependency>
```

Artemis 基于 CDI，所以我们还需要提供这个 Maven 依赖：

```xml
<dependency>
    <groupId>javax</groupId>
    <artifactId>javaee-web-api</artifactId>
    <version>8.0</version>
    <scope>provided</scope>
</dependency>
```

### 3.2. 文档配置文件

配置是给定数据库的一组属性，让我们可以在代码之外提供设置。默认情况下，我们需要 在 META-INF 资源下提供jnosql.json文件。

这是配置文件的示例：

```javascript
[
    {
        "description": "The mongodb document configuration",
        "name": "document",
        "provider": "org.jnosql.diana.mongodb.document.MongoDBDocumentConfiguration",
        "settings": {
            "mongodb-server-host-1":"localhost:27019"
        }
    }
]
```

我们需要通过在 ConfigurationUnit 中设置 name 属性来指定上面 的配置 名称。如果配置在不同的文件中，可以使用fileName属性指定。

鉴于此配置，我们创建一个工厂：

```java
@Inject
@ConfigurationUnit(name = "document")
private DocumentCollectionManagerFactory<MongoDBDocumentCollectionManager> managerFactory;
```

从这个工厂，我们可以创建一个DocumentCollectionManager：

```java
@Produces
public MongoDBDocumentCollectionManager getEntityManager() {
    return managerFactory.get("todos");
}
```

DocumentCollectionManager是一个支持 CDI的bean，它在模板 和 存储库中使用。

### 3.3. 测绘

映射是一个注解驱动的过程，实体模型通过该过程转换为 Diana EntityValue。

让我们从定义一个Todo模型开始：

```java
@Entity
public class Todo implements Serializable {

    @Id("id")
    public long id;

    @Column
    public String name;

    @Column
    public String description;

    // standard constructor
    // standard getters and setters
}
```

如上所示，我们有基本的映射注解： @Entity、@Id和@Column。

现在要操作这个模型，我们需要一个Template类或一个Repository接口。

### 3.4. 使用模板

模板是 实体模型和 Diana API 之间的桥梁。对于面向文档的数据库，我们首先注入DocumentTemplate bean：

```java
@Inject
DocumentTemplate documentTemplate;
```

然后，我们可以操作Todo实体。例如，我们可以创建一个Todo：

```java
public Todo add(Todo todo) {
    return documentTemplate.insert(todo);
}
```

或者我们可以通过id检索Todo：

```java
public Todo get(String id) {
    Optional<Todo> todo = documentTemplate
      .find(Todo.class, id);
    return todo.get(); // or any other proper Optional handling
}
```

要选择所有实体，我们构建一个DocumentQuery然后调用select()方法：

```java
public List<Todo> getAll() {
    DocumentQuery query = select().from("Todo").build();
    return documentTemplate.select(query);
}
```

最后我们可以通过id删除Todo 实体：

```java
public void delete(String id) {
    documentTemplate.delete(Todo.class, id);
}
```

### 3.5. 使用存储库

除了Template类之外，我们还可以通过Repository接口管理实体，该接口具有创建、更新、删除和检索信息的方法。

要使用Repository接口，我们只需提供Repository 的子接口：

```java
public interface TodoRepository extends Repository<Todo, String> {
    List<Todo> findByName(String name);
    List<Todo> findAll();
}
```

通过以下方法和参数命名约定，此接口的实现在运行时作为 CDI bean 提供。

在此示例中，所有具有匹配 名称的Todo实体 都由findByName()方法检索。

我们现在可以使用它：

```java
@Inject
TodoRepository todoRepository;
```

数据库限定符让我们可以在同一个应用程序中使用多个 NoSQL 数据库。它带有两个属性，类型和提供者。

如果数据库是多模型的，那么我们需要指定我们正在使用的模型：

```java
@Inject
@Database(value = DatabaseType.DOCUMENT)
TodoRepository todoRepository;
```

此外，如果我们有多个相同型号的数据库，我们需要指定提供者：

```java
@Inject
@Database(value = DatabaseType.DOCUMENT, provider="org.jnosql.diana.mongodb.document.MongoDBDocumentConfiguration")
TodoRepository todoRepository;
```

要运行示例，只需访问 jnosql-artemis 模块并调用此命令：

```bash
mvn package liberty:run
```

[借助 liberty-maven-plugin](https://github.com/WASdev/ci.maven#liberty-maven-plugin)，此命令构建、部署和启动[Open Liberty](https://www.openliberty.io/)服务器 。

### 3.6. 测试应用程序

由于应用程序公开了一个 REST 端点，我们可以使用任何 REST 客户端进行测试。这里我们使用了卷曲工具。

所以要保存一个 Todo 类：

```bash
curl -d '{"id":"120", "name":"task120", "description":"Description 120"}' -H "Content-Type: application/json" -X POST http://localhost:9080/jnosql-artemis/todos
```

并获取所有待办事项：

```bash
curl -H "Accept: application/json" -X GET http://localhost:9080/jnosql-artemis/todos
```

或者只获取一个 Todo：

```bash
curl -H "Accept: application/json" -X GET http://localhost:9080/jnosql-artemis/todos/120
```

## 4. 总结

在本教程中，我们探讨了 JNoSQL 如何能够抽象与 NoSQL 数据库的交互。

首先，我们使用 JNoSQL Diana API 通过低级代码与数据库进行交互。然后，我们使用 JNoSQL Artemis API 来处理友好的Java注解模型。
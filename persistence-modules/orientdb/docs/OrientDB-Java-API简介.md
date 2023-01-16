## 1. 概述

[OrientDB](https://orientdb.com/)是一种开源多模型 NoSQL 数据库技术，旨在与[Graph](https://en.wikipedia.org/wiki/Graph_database)、[Document](https://en.wikipedia.org/wiki/Document-oriented_database)、[Key-Value](https://en.wikipedia.org/wiki/Key-value_database)、[GeoSpatial](https://orientdb.com/docs/2.2.x/Spatial-Index.html)和[Reactive模型一起使用，同时使用](https://orientdb.com/docs/2.2.x/Live-Query.html)[SQL](https://orientdb.com/docs/2.2.x/SQL.html)语法管理查询。

在本文中，我们将介绍 OrientDBJavaAPI 的设置和使用。

## 2. 安装

首先，我们需要安装二进制包。

[让我们下载OrientDB](https://orientdb.com/download-previous/)的最新稳定版本(在撰写本文时为[2.2.x )。](https://orientdb.com/download-previous/orientdb-neo4j-importer-2.2.31.tar.gz)

其次，我们需要解压缩它并将其内容移动到一个方便的目录(使用ORIENTDB_HOME)。请确保将bin文件夹添加到环境变量中，以便于命令行使用。

最后，我们需要编辑位于$ORIENTDB_HOME/bin中的orientdb.sh文件，方法是在ORIENTDB_DIR位置填充OrientDB 目录的位置 ( ORIENTDB_HOME )以及我们要使用的系统用户而不是USER_YOU_WANT_ORIENTDB_RUN_WITH。

现在我们有了一个完全可用的 OrientDB。我们可以使用带有选项的orientdb.sh <option>脚本：

-   开始：启动服务器
-   状态：检查状态
-   停止：停止服务器

请注意，启动和停止操作都需要用户密码(我们在orientdb.sh文件中设置的密码)。

服务器启动后，它将占用端口 2480。因此我们可以使用此[URL](http://localhost:2480/studio/index.html)在本地访问它：

[![orientdb运行](https://www.baeldung.com/wp-content/uploads/2017/12/orientdb-running.png)](https://www.baeldung.com/wp-content/uploads/2017/12/orientdb-running.png)

可以在[此处找到有关手动安装的更多详细信息。](https://orientdb.com/docs/2.2.x/Tutorial-Installation.html)

注意：OrientDB 需要[Java](https://www.java.com/en/download/) 1.7 或更高版本。

以前的版本可[在此处](http://orientdb.com/download-previous/)获得。

## 3. OrientDBJavaAPI 设置

OrientDB 允许Java开发人员使用三种不同的 API，例如：

-   Graph API——图形数据库
-   文档 API – 面向文档的数据库
-   对象 API – 直接绑定到 OrientDB 文档的对象

我们可以通过集成和使用 OrientDB 在单个代码库中使用所有这些类型。

让我们看一下可以包含在项目类路径中的一些可用 jar：

-   orientdb-core-.jar：带来核心库
-   blueprints-core-.jar：带来适配器核心组件
-   orientdb-graphdb-.jar：提供图形数据库 API
-   orientdb-object-.jar：提供对象数据库 API
-   orientdb-distributed-.jar：提供分布式数据库插件以与服务器集群一起工作
-   orientdb-tools-.jar : 交出控制台命令
-   orientdb-client-.jar : 提供远程客户端
-   orientdb-enterprise-.jar：启用客户端和服务器共享的协议和网络类

仅当我们在远程服务器上管理数据时才需要最后两个。

让我们从一个 Maven 项目开始，并使用以下依赖项：

```xml
<dependency>
    <groupId>com.orientechnologies</groupId>
    <artifactId>orientdb-core</artifactId>
    <version>2.2.31</version>
</dependency>
<dependency>
    <groupId>com.orientechnologies</groupId>
    <artifactId>orientdb-graphdb</artifactId>
    <version>2.2.31</version>
</dependency>
<dependency>
    <groupId>com.orientechnologies</groupId>
    <artifactId>orientdb-object</artifactId>
    <version>2.2.31</version>
</dependency>
<dependency>
    <groupId>com.tinkerpop.blueprints</groupId>
    <artifactId>blueprints-core</artifactId>
    <version>2.6.0</version>
</dependency>
```

请检查 Maven Central 存储库以获取最新版本的 OrientDB 的[Core](http://www.maven.org/#search|gav|1|g%3A"com.orientechnologies" AND a%3A"orientdb-core")、[GraphDB](http://www.maven.org/#search|gav|1|g%3A"com.orientechnologies" AND a%3A"orientdb-graphdb")、[Object](http://www.maven.org/#search|gav|1|g%3A"com.orientechnologies" AND a%3A"orientdb-object") APIs 和[Blueprints-Core](http://www.maven.org/#search|gav|1|g%3A"com.tinkerpop.blueprints" AND a%3A"blueprints-core")。

## 4.用法

OrientDB 使用[TinkerPop Blueprints](https://tinkerpop.apache.org/)实现来处理图形。

[TinkerPop](https://tinkerpop.apache.org/)是一个图形计算框架，提供多种构建图形数据库的方法，其中每种方法都有其实现：

-   [蓝图](https://github.com/tinkerpop/blueprints/wiki)
-   [管道](https://github.com/tinkerpop/pipes/wiki)
-   [格林姆林](https://github.com/tinkerpop/gremlin/wiki)
-   [雷克斯特](https://github.com/tinkerpop/rexster/wiki)
-   [航海互补](https://github.com/tinkerpop/blueprints/wiki/Sail-Ouplementation)

此外，无论 API 的类型如何，OrientDB 都允许使用三种[模式：](https://orientdb.com/docs/last/java/Graph-Schema.html)

-   Schema-Full – 启用严格模式，因此在创建类时指定所有字段
-   Schema-Less——创建的类没有特定的属性，因此我们可以根据需要添加它们；这是默认模式
-   模式混合——它是全模式和无模式的混合，我们可以在其中创建一个具有预定义字段的类，但让记录定义其他自定义字段

### 4.1. 图形接口

由于这是一个基于图形的数据库，因此数据表示为包含由[边](https://orientdb.com/docs/2.2.x/Graph-VE.html#edges)(弧)互连的[顶点](https://orientdb.com/docs/2.2.x/Graph-VE.html#vertices)(节点)的网络。

第一步，让我们使用 UI 创建一个名为BaeldungDB的图形数据库，用户名为admin，密码为admin。

正如我们在下图中看到的那样，该图已被选为数据库类型，因此可以在GRAPH 选项卡中访问其数据：

[![创建图形数据库](https://www.baeldung.com/wp-content/uploads/2017/12/create-graph-db.png)](https://www.baeldung.com/wp-content/uploads/2017/12/create-graph-db.png)

现在让我们连接到所需的数据库，知道ORIENTDB_HOME是一个环境变量，对应于OrientDB的安装文件夹：

```java
@BeforeClass
public static void setup() {
    String orientDBFolder = System.getenv("ORIENTDB_HOME");
    graph = new OrientGraphNoTx("plocal:" + orientDBFolder + 
      "/databases/BaeldungDB", "admin", "admin");
}
```

让我们启动Article、Author和Editor类——同时展示如何向它们的字段添加验证：

```java
@BeforeClass
public static void init() {
    graph.createVertexType("Article");

    OrientVertexType writerType
      = graph.createVertexType("Writer");
    writerType.setStrictMode(true);
    writerType.createProperty("firstName", OType.STRING);
    // ...

    OrientVertexType authorType 
      = graph.createVertexType("Author", "Writer");
    authorType.createProperty("level", OType.INTEGER).setMax("3");

    OrientVertexType editorType
      = graph.createVertexType("Editor", "Writer");
    editorType.createProperty("level", OType.INTEGER).setMin("3");

    Vertex vEditor = graph.addVertex("class:Editor");
    vEditor.setProperty("firstName", "Maxim");
    // ...

    Vertex vAuthor = graph.addVertex("class:Author");
    vAuthor.setProperty("firstName", "Jerome");
    // ...

    Vertex vArticle = graph.addVertex("class:Article");
    vArticle.setProperty("title", "Introduction to ...");
    // ...

    graph.addEdge(null, vAuthor, vEditor, "has");
    graph.addEdge(null, vAuthor, vArticle, "wrote");
}
```

在上面的代码片段中，我们简单地表示了我们的简单数据库，其中：

-   Article是包含文章的无模式类
-   Writer是一个包含必要的 writer 信息的全模式超类
-   Writer是Author的子类型，包含其详细信息
-   Editor是Writer的无模式子类型，包含编辑器详细信息
-   已保存的作者lastName字段未填写但仍出现在下图中
-   我们在所有类之间有一个关系：作者可以写文章并且需要有一个编辑器
-   Vertex代表一个有一些字段的实体
-   边是连接两个顶点的实体

请注意，通过尝试向完整类的对象添加另一个属性，我们将以[OValidationException](https://orientdb.com/javadoc/latest/com/orientechnologies/orient/core/exception/OValidationException.html)结束。

[使用OrientDB studio](https://orientdb.com/docs/2.2.x/Tutorial-Run-the-studio.html)连接到我们的数据库后，我们将看到数据的图形表示：

[![orientdb图](https://www.baeldung.com/wp-content/uploads/2017/12/orientdb-graph.png)](https://www.baeldung.com/wp-content/uploads/2017/12/orientdb-graph.png)

让我们看看如何获得数据库的所有记录(顶点)的数量：

```java
long size = graph.countVertices();
```

现在，让我们只显示Writer(作者和编辑)对象的数量：

```java
@Test
public void givenBaeldungDB_checkWeHaveTwoWriters() {
    long size = graph.countVertices("Writer");

    assertEquals(2, size);
}
```

在下一步中，我们可以使用以下语句找到所有Writer的数据：

```java
Iterable<Vertex> writers = graph.getVerticesOfClass("Writer");
```

最后，让我们查询所有级别为 7的Editor；这里我们只有一个匹配：

```java
@Test
public void givenBaeldungDB_getEditorWithLevelSeven() {
    String onlyEditor = "";
    for(Vertex v : graph.getVertices("Editor.level", 7)) {
        onlyEditor = v.getProperty("firstName").toString();
    }

    assertEquals("Maxim", onlyEditor);
}
```

请求时始终指定类名以查找特定的顶点。我们可以[在这里](https://orientdb.com/docs/last/java/Graph-Database-Tinkerpop.html)找到更多详细信息。

### 4.2. 文档API

下一个选项是使用 OrientDB 的文档模型。这通过简单的记录公开数据操作，信息存储在字段中，类型可以是文本、图片或二进制形式。

让我们再次使用 UI 创建一个名为BaeldungDBTwo的数据库，但现在使用[文档](https://orientdb.com/javadoc/latest/com/orientechnologies/orient/core/record/impl/ODocument.html)作为类型：

[![创建文档数据库](https://www.baeldung.com/wp-content/uploads/2017/12/create-document-db.png)](https://www.baeldung.com/wp-content/uploads/2017/12/create-document-db.png)

注意：同样，此 API 也可以在全模式、无模式或混合模式模式下使用。

数据库连接仍然很简单，因为我们只需要实例化一个ODatabaseDocumentTx对象，提供数据库 URL 和数据库用户的凭据：

```java
@BeforeClass
public static void setup() {
    String orientDBFolder = System.getenv("ORIENTDB_HOME");
    db = new ODatabaseDocumentTx("plocal:" 
      + orientDBFolder + "/databases/BaeldungDBTwo")
      .open("admin", "admin");
}
```

让我们从保存一个包含作者信息的简单文档开始。

在这里我们可以看到类已经自动创建了：

```java
@Test
public void givenDB_whenSavingDocument_thenClassIsAutoCreated() {
    ODocument doc = new ODocument("Author");
    doc.field("name", "Paul");
    doc.save();

    assertEquals("Author", doc.getSchemaClass().getName());
}
```

因此，要计算Authors的数量，我们可以使用：

```java
long size = db.countClass("Author");
```

让我们再次使用字段值查询文档，以搜索Author的级别为7的对象：

```java
@Test
public void givenDB_whenSavingAuthors_thenWeGetOnesWithLevelSeven() {
    for (ODocument author : db.browseClass("Author")) author.delete();

    ODocument authorOne = new ODocument("Author");
    authorOne.field("firstName", "Leo");
    authorOne.field("level", 7);
    authorOne.save();

    ODocument authorTwo = new ODocument("Author");
    authorTwo.field("firstName", "Lucien");
    authorTwo.field("level", 9);
    authorTwo.save();

    List<ODocument> result = db.query(
      new OSQLSynchQuery<ODocument>("select  from Author where level = 7"));

    assertEquals(1, result.size());
}
```

同样，要删除Author类的所有记录，我们可以使用：

```java
for (ODocument author : db.browseClass("Author")) {
    author.delete();
}
```

在 OrientDB studio 的BROWSE 选项卡上，我们可以查询以获取作者的所有对象：

[![orientdb文件](https://www.baeldung.com/wp-content/uploads/2017/12/orientdb-document.png)](https://www.baeldung.com/wp-content/uploads/2017/12/orientdb-document.png)

### 4.3. 对象接口

OrientDB 没有数据库的对象类型。因此，对象 API 依赖于文档数据库。

在 Object API 类型中，所有其他概念保持不变，只增加了一个——[绑定](https://orientdb.com/docs/2.2.x/Object-2-Record-Java-Binding.html)到 POJO。

让我们从使用OObjectDatabaseTx类连接到BaeldungDBThree开始：

```java
@BeforeClass
public static void setup() {
    String orientDBFolder = System.getenv("ORIENTDB_HOME");
    db = new OObjectDatabaseTx("plocal:" 
      + orientDBFolder + "/databases/BaeldungDBThree")
      .open("admin", "admin");
}
```

接下来，假设Author是用来保存Author数据的 POJO，我们需要注册它：

```java
db.getEntityManager().registerEntityClass(Author.class);
```

作者有以下字段的 getter 和 setter：

-   名
-   姓
-   等级

如果我们承认一个无参数的构造函数，让我们用多行指令创建一个Author ：

```java
Author author = db.newInstance(Author.class);
author.setFirstName("Luke");
author.setLastName("Sky");
author.setLevel(9);
db.save(author);
```

另一方面，如果我们有另一个构造函数分别采用作者的firstName、lastName和level，则实例化只是一行：

```java
Author author = db.newInstance(Author.class, "Luke", "Sky", 9);
db.save(author);
```

以下行用于浏览和删除 Author 类的所有记录：

```java
for (Author author : db.browseClass(Author.class)) {
    db.delete(author);
}
```

要计算所有作者，我们只需提供类和数据库实例，而无需编写 SQL 查询：

```java
long authorsCount = db.countClass(Author.class);
```

同样，我们像这样查询级别为7 的作者：

```java
@Test
public void givenDB_whenSavingAuthors_thenWeGetOnesWithLevelSeven() {
    for (Author author : db.browseClass(Author.class)) {
        db.delete(author);
    }

    Author authorOne 
      = db.newInstance(Author.class, "Leo", "Marta", 7);
    db.save(authorOne);

    Author authorTwo
      = db.newInstance(Author.class, "Lucien", "Aurelien", 9);
    db.save(authorTwo);

    List<Author> result
      = db.query(new OSQLSynchQuery<Author>(
      "select  from Author where level = 7"));

    assertEquals(1, result.size());
}
```

最后，这是介绍一些高级 Object API 使用的[官方指南。](https://orientdb.com/docs/2.2.x/Object-Database.html)

## 5.总结

在本文中，我们了解了如何使用 OrientDB 作为具有JavaAPI 的数据库管理系统。我们还学习了如何在字段上添加验证并编写一些简单的查询。
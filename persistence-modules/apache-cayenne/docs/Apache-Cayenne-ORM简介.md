## 1. 概述

[Apache Cayenne](https://cayenne.apache.org/)是一个开源库，在 Apache 许可下分发，提供建模工具、对象关系映射(又名 ORM)等功能，用于本地持久性操作和远程服务。

在以下部分中，我们将了解如何使用 Apache Cayenne ORM 与 MySQL 数据库进行交互。

## 2.Maven依赖

首先，我们只需要添加以下依赖项来启动 Apache Cayenne 和 MySQL 连接器 JDBC 驱动程序一起访问我们的intro_cayenne数据库：

```xml
<dependency>
    <groupId>org.apache.cayenne</groupId>
    <artifactId>cayenne-server</artifactId>
    <version>4.0.M5</version>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>5.1.44</version>
    <scope>runtime</scope>
</dependency>
```

让我们配置 Cayenne 建模器插件，该插件将用于设计或设置我们的映射文件，该文件充当数据库模式和Java对象之间的桥梁：

```xml
<plugin>
    <groupId>org.apache.cayenne.plugins</groupId>
    <artifactId>maven-cayenne-modeler-plugin</artifactId>
    <version>4.0.M5</version>
</plugin>
```

建议使用建模器，而不是手动构建 XML 映射文件(很少创建)，这是 Cayenne 发行版附带的一个非常高级的工具。

[根据你的操作系统，它可以从这个存档](https://cayenne.apache.org/download.html)中下载，或者只使用作为 Maven 插件包含在其中的跨平台版本 (JAR)。

Maven 中央存储库托管最新版本的[Apache Cayenne](https://search.maven.org/classic/#search|ga|1|a%3A"cayenne-server")、[他的建模器](https://search.maven.org/classic/#search|ga|1|a%3A"cayenne-modeler-maven-plugin")和[MySQL 连接器](https://search.maven.org/classic/#search|gav|1|g%3A"mysql" AND a%3A"mysql-connector-java")。

接下来，让我们使用mvn install构建我们的项目，并使用命令mvn cayenne-modeler:run启动建模器 GUI以获取此屏幕的输出：

[![屏幕1](https://www.baeldung.com/wp-content/uploads/2017/09/Screen1.png)](https://www.baeldung.com/wp-content/uploads/2017/09/Screen1.png)

## 3.设置

为了让 Apache Cayenne 查找正确的本地数据库，我们只需要在资源目录中的文件cayenne-project.xml中用正确的驱动程序、URL 和用户填充他的配置文件：

```xml
<?xml version="1.0" encoding="utf-8"?>
<domain project-version="9">
    <node name="datanode"
          factory
      ="org.apache.cayenne.configuration.server.XMLPoolingDataSourceFactory"
          schema-update-strategy
      ="org.apache.cayenne.access.dbsync.CreateIfNoSchemaStrategy">
        <data-source>
            <driver value="com.mysql.jdbc.Driver"/>
            <url value
              ="jdbc:mysql://localhost:3306/intro_cayenne;create=true"/>
            <connectionPool min="1" max="1"/>
            <login userName="root" password="root"/>
        </data-source>
    </node>
</domain>
```

在这里我们可以看到：

-   本地数据库名为intro_cayenne
-   如果尚未创建，Cayenne 会为我们创建
-   我们将使用用户名root和密码root进行连接(根据在你的数据库管理系统中注册的用户进行更改)

在内部，它是XMLPoolingDataSourceFactory负责从与DataNodeDescriptor关联的 XML 资源加载 JDBC 连接信息。

请注意，这些参数与数据库管理系统和 JDBC 驱动程序相关，因为该库可以支持许多不同的数据库。

[在这个详细列表](https://cayenne.apache.org/database-support.html)中，他们每个人都有一个可用的适配器。请注意，4.0 版的完整文档尚不可用，因此我们在这里参考以前的版本。

## 4. 映射和数据库设计

### 4.1. 造型

现在让我们点击“打开项目”，导航到项目的资源文件夹并选择文件cayenne-project.xml，建模器将显示：

[![截屏](https://www.baeldung.com/wp-content/uploads/2017/09/Capture-d%E2%80%99%C3%A9cran-2017-09-26-%C3%A0-02.45.04.png)](https://www.baeldung.com/wp-content/uploads/2017/09/Capture-d’écran-2017-09-26-à-02.45.04.png)

在这里，我们可以选择从现有数据库创建映射结构 或手动进行。本文将处理使用建模器和现有数据库进入 Cayenne 并快速了解其工作原理的问题。

让我们看一下我们的intro_cayenne数据库，它具有跨两个表的一对多关系，因为作者可以发表或拥有多篇文章：

-   作者：id(PK)和名字
-   文章：id (PK)、title、content和author_id(FK)

现在让我们转到“工具 > 重新设计数据库架构”，我们将自动填充所有映射配置。在提示屏幕上，只需填写cayenne-project.xml文件中可用的数据源配置，然后点击继续：

[![截屏](https://www.baeldung.com/wp-content/uploads/2017/09/Capture-d%E2%80%99%C3%A9cran-2017-09-26-%C3%A0-02.55.12.png)](https://www.baeldung.com/wp-content/uploads/2017/09/Capture-d’écran-2017-09-26-à-02.55.12.png)

在下一个屏幕上，我们需要选中“UseJavaprimitive types”，如下所示：

[![截屏](https://www.baeldung.com/wp-content/uploads/2017/09/Capture-d%E2%80%99%C3%A9cran-2017-09-24-%C3%A0-14.02.18.png)](https://www.baeldung.com/wp-content/uploads/2017/09/Capture-d’écran-2017-09-24-à-14.02.18.png)

我们还需要确保将com.baeldung.apachecayenne.persistent作为Java包并保存；我们将看到 XML 配置文件的defaultPackage属性已更新，以匹配Java包：

[![截屏](https://www.baeldung.com/wp-content/uploads/2017/09/Capture-d%E2%80%99%C3%A9cran-2017-09-26-%C3%A0-02.58.41.png)](https://www.baeldung.com/wp-content/uploads/2017/09/Capture-d’écran-2017-09-26-à-02.58.41.png)

在每个ObjEntity 中，我们必须指定子类的包，如下图所示，然后再次单击“保存”图标：

[![画面5](https://www.baeldung.com/wp-content/uploads/2017/09/Screen5.png)](https://www.baeldung.com/wp-content/uploads/2017/09/Screen5.png)

现在在“Tools > Generate Classes”菜单上，选择“ Standard Persistent Objects ”作为类型；在“类”选项卡上检查所有类并点击“生成”。

我们回到源码看看我们的持久化对象已经生成成功了，说说_Article.java和_Author.java。

请注意，所有这些配置都保存在文件datamap.map.xml中，该文件也位于资源文件夹中。

### 4.2. 映射结构

资源文件夹中生成的 XML 映射文件使用了一些与 Apache Cayenne 相关的独特标签：

-   DataNode(<node>) – 数据库模型，其内容是连接到数据库所需的所有信息(数据库名称、驱动程序和用户凭据)
-   DataMap(<data-map>) – 它是持久实体及其关系的容器
-   DbAttribute(<db-attribute>) – 表示数据库表中的列
-   DbEntity(<db-entity>) – 单个数据库表或视图的模型，它可以有 DbAttributes 和关系
-   ObjEntity(<obj-entity>) – 单个持久化 java 类的模型；由对应于实体类属性的 ObjAttributes 和具有另一个实体类型的属性的 ObjRelationships 组成
-   Embeddable(<embeddable>) –Java类的模型，充当 ObjEntity 的属性，但对应于数据库中的多个列
-   Procedure(<procedure>) – 在数据库中注册存储过程
-   Query(<query>) – 查询的模型，用于在配置文件中映射查询，不要忘记我们也可以在代码中进行

这是完整的[细节](https://cayenne.apache.org/docs/3.1/cayenne-guide/cayenne-mapping-structure.html)。

## 5.卡宴API

剩下的唯一步骤是使用 Cayenne API 使用生成的类执行我们的数据库操作，知道子类化我们的持久类只是用于稍后自定义模型的最佳实践。

### 5.1. 创建对象

在这里，我们只是保存了一个Author对象，稍后查看数据库中是否只有一条该类型的记录：

```java
@Test
public void whenInsert_thenWeGetOneRecordInTheDatabase() {
    Author author = context.newObject(Author.class);
    author.setName("Paul");

    context.commitChanges();

    long records = ObjectSelect.dataRowQuery(Author.class)
      .selectCount(context);
 
    assertEquals(1, records);
}
```

### 5.2. 读取对象

保存Author后，我们只需通过特定属性的简单查询从其他人中挑选它：

```java
@Test
public void whenInsert_andQueryByFirstName_thenWeGetTheAuthor() {
    Author author = context.newObject(Author.class);
    author.setName("Paul");

    context.commitChanges();

    Author expectedAuthor = ObjectSelect.query(Author.class)
      .where(Author.NAME.eq("Paul"))
      .selectOne(context);

    assertEquals("Paul", expectedAuthor.getName());
}
```

### 5.3. 检索一个类的所有记录

我们将保存两个作者并检索作者对象的集合以检查是否只保存了这两个：

```java
@Test
public void whenInsert_andQueryAll_thenWeGetTwoAuthors() {
    Author firstAuthor = context.newObject(Author.class);
    firstAuthor.setName("Paul");

    Author secondAuthor = context.newObject(Author.class);
    secondAuthor.setName("Ludovic");

    context.commitChanges();

    List<Author> authors = ObjectSelect
      .query(Author.class)
      .select(context);
 
    assertEquals(2, authors.size());
}
```

### 5.4. 更新对象

更新过程也很简单，但在修改其属性并将其应用到数据库之前，我们首先需要拥有所需的对象：

```java
@Test
public void whenUpdating_thenWeGetAnUpatedeAuthor() {
    Author author = context.newObject(Author.class);
    author.setName("Paul");
    context.commitChanges();

    Author expectedAuthor = ObjectSelect.query(Author.class)
      .where(Author.NAME.eq("Paul"))
      .selectOne(context);
    expectedAuthor.setName("Garcia");
    context.commitChanges();

    assertEquals(author.getName(), expectedAuthor.getName());
}
```

### 5.5. 附加对象

我们可以将一篇文章分配给作者：

```java
@Test
public void whenAttachingToArticle_thenTheRelationIsMade() {
    Author author = context.newObject(Author.class);
    author.setName("Paul");

    Article article = context.newObject(Article.class);
    article.setTitle("My post title");
    article.setContent("The content");
    article.setAuthor(author);

    context.commitChanges();

    Author expectedAuthor = ObjectSelect.query(Author.class)
      .where(Author.NAME.eq("Smith"))
      .selectOne(context);

    Article expectedArticle = (expectedAuthor.getArticles()).get(0);
 
    assertEquals(article.getTitle(), expectedArticle.getTitle());
}
```

### 5.6. 删除对象

删除保存的对象会将其从数据库中完全删除，此后我们将看到null作为查询结果：

```java
@Test
public void whenDeleting_thenWeLostHisDetails() {
    Author author = context.newObject(Author.class);
    author.setName("Paul");
    context.commitChanges();

    Author savedAuthor = ObjectSelect.query(Author.class)
      .where(Author.NAME.eq("Paul"))
      .selectOne(context);
    if(savedAuthor != null) {
        context.deleteObjects(author);
        context.commitChanges();
    }

    Author expectedAuthor = ObjectSelect.query(Author.class)
      .where(Author.NAME.eq("Paul"))
      .selectOne(context);
 
    assertNull(expectedAuthor);
}
```

### 5.7. 删除一个班级的所有记录

也可以使用SQLTemplate删除表的所有记录，这里我们在每个测试方法之后执行此操作，以便在每次测试启动之前始终有一个空数据库：

```java
@After
public void deleteAllRecords() {
    SQLTemplate deleteArticles = new SQLTemplate(
      Article.class, "delete from article");
    SQLTemplate deleteAuthors = new SQLTemplate(
      Author.class, "delete from author");

    context.performGenericQuery(deleteArticles);
    context.performGenericQuery(deleteAuthors);
}
```

## 六. 总结

在本教程中，我们专注于使用 Apache Cayenne ORM 轻松演示如何使用一对多关系进行 CRUD 操作。
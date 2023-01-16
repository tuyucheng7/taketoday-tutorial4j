## 1. 简介

在本教程中，我们将了解[Sql2o](https://www.sql2o.org/)，这是一个使用惯用Java进行关系数据库访问的小型快速库。

值得一提的是，尽管 Sql2o 通过将查询结果映射到 POJO(普通的旧Java对象)来工作，但它并不是一个完整的 ORM 解决方案，例如 Hibernate。

## 2.sql2o设置

Sql2o 是一个单独的 jar 文件，我们可以轻松地将其添加到项目的依赖项中：

```xml
<dependency>
    <groupId>org.sql2o</groupId>
    <artifactId>sql2o</artifactId>
    <version>1.6.0</version>
</dependency>
```

在我们的示例中，我们还将使用嵌入式数据库 HSQL；为了跟进，我们也可以包括它：

```xml
<dependency>
    <groupId>org.hsqldb</groupId>
    <artifactId>hsqldb</artifactId>
    <version>2.4.0</version>
    <scope>test</scope>
</dependency>
```

Maven Central 托管最新版本的[sql2o](https://search.maven.org/search?q=g:org.sql2o AND a:sql2o&core=gav)和[HSQLDB](https://search.maven.org/classic/#search|gav|1|g%3A"org.hsqldb" AND a%3A"hsqldb")。

## 3. 连接数据库

要建立连接，我们从Sql2o类的一个实例开始：

```java
Sql2o sql2o = new Sql2o("jdbc:hsqldb:mem:testDB", "sa", "");
```

在这里，我们将连接 URL、用户名和密码指定为构造函数参数。

Sql2o对象是线程安全的，我们可以在整个应用程序中共享它。

### 3.1. 使用数据源

在大多数应用程序中，我们希望使用DataSource 而不是原始DriverManager连接，也许是为了利用连接池，或者指定其他连接参数。别担心，Sql2o 已经帮我们解决了：

```java
Sql2o sql2o = new Sql2o(datasource);
```

### 3.2. 使用连接

仅仅实例化一个Sql2o对象并不会建立与数据库的任何连接。

相反，我们使用open方法来获取Connection对象(注意它不是 JDBC Connection)。由于 Connection是可自动关闭的，我们可以将其包装在一个[try-with-resources](https://www.baeldung.com/java-try-with-resources)块中：

```java
try (Connection connection = sql2o.open()) {
    // use the connection
}
```

## 4.插入和更新语句

现在让我们创建一个数据库并将一些数据放入其中。在整个教程中，我们将使用一个名为project 的简单表：

```java
connection.createQuery(
    "create table project "
    + "(id integer identity, name varchar(50), url varchar(100))").executeUpdate();
```

executeUpdate返回Connection对象，以便我们可以链接多个调用。然后，如果我们想知道受影响的行数，我们使用getResult：

```java
assertEquals(0, connection.getResult());
```

我们将为所有 DDL、INSERT 和 UPDATE 语句应用我们刚刚看到的模式——createQuery和executeUpdate 。

### 4.1. 获取生成的键值

但是，在某些情况下，我们可能希望取回生成的键值。这些是自动计算的键列的值(例如在某些数据库上使用自动增量时)。

我们分两步进行。首先，用一个额外的参数来创建查询：

```java
Query query = connection.createQuery(
    "insert into project (name, url) "
    + "values ('tutorials', 'github.com/eugenp/tutorials')", true);
```

然后，在连接上调用getKey：

```java
assertEquals(0, query.executeUpdate().getKey());
```

如果键不止一个，我们使用getKeys代替，它返回一个数组：

```java
assertEquals(1, query.executeUpdate().getKeys()[0]);
```

## 5. 从数据库中提取数据

现在让我们进入问题的核心：SELECT查询和结果集到Java对象的映射。

首先，我们必须定义一个带有 getter 和 setter 的 POJO 类来表示我们的项目表：

```java
public class Project {
    long id;
    private String name;
    private String url;
    //Standard getters and setters
}
```

然后，和以前一样，我们将编写我们的查询：

```java
Query query = connection.createQuery("select  from project order by id");
```

然而，这次我们将使用一个新方法，executeAndFetch：

```java
List<Project> list = query.executeAndFetch(Project.class);
```

如我们所见，该方法将结果类作为参数，Sql2o 会将来自数据库的原始结果集的行映射到该参数。

### 5.1. 列映射

Sql2o 按名称将列映射到 JavaBean 属性，不区分大小写。

但是，Java 和关系数据库之间的命名约定不同。假设我们向我们的项目添加一个创建日期属性：

```java
public class Project {
    long id;
    private String name;
    private String url;
    private Date creationDate;
    //Standard getters and setters
}
```

在数据库模式中，我们很可能会调用相同的属性creation_date。

当然，我们可以在查询中为其添加别名：

```java
Query query = connection.createQuery(
    "select name, url, creation_date as creationDate from project");
```

然而，这很乏味，我们失去了使用select  的可能性。

另一种选择是指示 Sql2o 将 creation_date 映射到creationDate 。也就是说，我们可以告诉查询有关映射的信息：

```java
connection.createQuery("select  from project")
    .addColumnMapping("creation_date", "creationDate");
```

如果我们在少量查询中谨慎使用creationDate ，这很好；然而，当在更大的项目中广泛使用时，一遍又一遍地讲述同一个事实会变得乏味且容易出错。

幸运的是，我们还可以全局指定映射：

```java
Map<String, String> mappings = new HashMap<>();
mappings.put("CREATION_DATE", "creationDate");
sql2o.setDefaultColumnMappings(mappings);
```

当然，这将导致creation_date的每个实例都映射到creationDate，因此这是努力在我们的数据定义中保持名称一致的另一个原因。

### 5.2. 标量结果

有时，我们希望从查询中提取单个标量结果。比如我们需要统计记录条数的时候。

在那些情况下，定义一个类并遍历我们知道包含单个元素的列表是多余的。因此，Sql2o 包括executeScalar方法：

```java
Query query = connection.createQuery(
    "select count() from project");
assertEquals(2, query.executeScalar(Integer.class));
```

在这里，我们将返回类型指定为Integer。但是，这是可选的，我们可以让底层 JDBC 驱动程序决定。

### 5.3. 复杂的结果

相反，有时复杂的查询(例如报告查询)可能无法轻松映射到Java对象。我们也可能决定不希望将Java类编码为仅在单个查询中使用。

因此，Sql2o 还允许较低级别的动态映射到表格数据结构。我们使用executeAndFetchTable方法访问它：

```java
Query query = connection.createQuery(
    "select  from project order by id");
Table table = query.executeAndFetchTable();
```

然后，我们可以提取地图列表：

```java
List<Map<String, Object>> list = table.asList();
assertEquals("tutorials", list.get(0).get("name"));
```

或者，我们可以将数据映射到Row对象的列表，这些对象是从列名到值的映射，类似于ResultSet：

```java
List<Row> rows = table.rows();
assertEquals("tutorials", rows.get(0).getString("name"));
```

## 6.绑定查询参数

许多 SQL 查询都具有固定结构和一些参数化部分。我们可能天真地用字符串连接编写那些部分动态的查询。

但是，Sql2o 允许参数化查询，因此：

-   我们避免[SQL 注入攻击](https://www.baeldung.com/sql-injection)
-   我们允许数据库缓存常用查询并提高性能
-   最后，我们无需对日期和时间等复杂类型进行编码

因此，我们可以在 Sql2o 中使用命名参数来实现上述所有功能。我们用冒号引入参数，并用addParameter方法绑定它们：

```java
Query query = connection.createQuery(
    "insert into project (name, url) values (:name, :url)")
    .addParameter("name", "REST with Spring")
    .addParameter("url", "github.com/eugenp/REST-With-Spring");
assertEquals(1, query.executeUpdate().getResult());
```

### 6.1. 从 POJO 绑定

Sql2o 提供了另一种绑定参数的方法：即使用 POJO作为源。当一个查询有很多参数并且它们都引用同一个实体时，这种技术特别适用。那么，我们来介绍一下bind方法：

```java
Project project = new Project();
project.setName("REST with Spring");
project.setUrl("github.com/eugenp/REST-With-Spring");
connection.createQuery(
    "insert into project (name, url) values (:name, :url)")
    .bind(project)
    .executeUpdate();
assertEquals(1, connection.getResult());
```

## 7.交易和批量查询

通过事务，我们可以将多个 SQL 语句作为一个原子操作发出。也就是说，它要么成功，要么批量失败，没有中间结果。事实上，事务是关系数据库的关键特性之一。

为了打开一个事务，我们使用beginTransaction方法而不是我们目前使用的open方法：

```java
try (Connection connection = sql2o.beginTransaction()) {
    // here, the transaction is active
}
```

当执行离开块时，Sql2o 会自动回滚事务(如果它仍然处于活动状态)。

### 7.1. 手动提交和回滚

但是，我们可以使用适当的方法显式提交或回滚事务：

```java
try (Connection connection = sql2o.beginTransaction()) {
    boolean transactionSuccessful = false;
    // perform some operations
    if(transactionSuccessful) {
        connection.commit();
    } else {
        connection.rollback();
    }
}
```

请注意，提交和回滚都会结束事务。随后的语句将在没有事务的情况下运行，因此它们不会在块的末尾自动回滚。

但是，我们可以在不结束事务的情况下提交或回滚事务：

```java
try (Connection connection = sql2o.beginTransaction()) {
    List list = connection.createQuery("select  from project")
        .executeAndFetchTable()
        .asList();
    assertEquals(0, list.size());
    // insert or update some data
    connection.rollback(false);
    // perform some other insert or update queries
}
// implicit rollback
try (Connection connection = sql2o.beginTransaction()) {
    List list = connection.createQuery("select  from project")
        .executeAndFetchTable()
        .asList();
    assertEquals(0, list.size());
}
```

### 7.2. 批量操作

当我们需要使用不同的参数多次发出相同的语句时，批量运行它们可以提供很大的性能优势。

幸运的是，通过结合我们目前描述的两种技术——参数化查询和事务——可以很容易地批量运行它们：

-   首先，我们只创建一次查询
-   然后，我们绑定参数并为查询的每个实例调用addToBatch
-   最后，我们调用executeBatch：

```java
try (Connection connection = sql2o.beginTransaction()) {
    Query query = connection.createQuery(
        "insert into project (name, url) " +
        "values (:name, :url)");
    for (int i = 0; i < 1000; i++) {
        query.addParameter("name", "tutorials" + i);
        query.addParameter("url", "https://github.com/eugenp/tutorials" + i);
        query.addToBatch();
    }
    query.executeBatch();
    connection.commit();
}
try (Connection connection = sql2o.beginTransaction()) {
    assertEquals(
        1000L,
        connection.createQuery("select count() from project").executeScalar());
}
```

### 7.3. 延迟获取

相反，当单个查询返回大量结果时，将它们全部转换并存储在列表中会占用大量内存。

因此，Sql2o 支持惰性模式，其中一次返回并映射一个行：

```java
Query query = connection.createQuery("select  from project");
try (ResultSetIterable<Project> projects = query.executeAndFetchLazy(Project.class)) {
    for(Project p : projects) {
        // do something with the project
    }
}
```

请注意，ResultSetIterable是AutoCloseable并且旨在与try-with-resources一起使用以在完成时关闭底层ResultSet。

## 八. 总结

在本教程中，我们概述了 Sql2o 库及其最常见的使用模式。更多信息可以[在 GitHub 上的 Sql20 wiki 中找到。](https://github.com/aaberg/sql2o/wiki)
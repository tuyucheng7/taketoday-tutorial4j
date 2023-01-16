## 1. 简介

本教程是将 CockroachDB 与Java结合使用的入门指南。

我们将解释关键特性、如何配置本地集群以及如何监控它，以及关于如何使用Java连接服务器并与之交互的实用指南。

让我们首先定义它是什么。

## 2.蟑螂数据库

CockroachDB 是一个分布式 SQL 数据库，建立在一个事务性的和一致的键值存储之上。

用 Go 编写并完全开源，其主要设计目标是支持 ACID 事务、水平可伸缩性和生存能力。有了这些设计目标，它旨在以最小的延迟中断和无需人工干预的方式容忍从单个磁盘故障到整个数据中心崩溃的一切。

因此，对于无论规模如何都需要可靠、可用和正确数据的应用程序，CockroachDB 可以被认为是一个非常适合的解决方案。但是，当非常低的延迟读取和写入至关重要时，它不是首选。

### 2.1. 主要特征

让我们继续探索 CockroachDB 的一些关键方面：

-   SQL API 和 PostgreSQL 兼容性——用于结构化、操作和查询数据
-   ACID事务——支持分布式事务，提供强一致性
-   云就绪——设计用于在云端或本地解决方案上运行，提供不同云提供商之间的轻松迁移，而不会造成任何服务中断
-   水平扩展——增加容量就像将新节点指向正在运行的集群一样简单，操作员开销最小
-   ——数据以获得可用性并保证副本之间的一致性
-   自动修复——只要大多数副本对短期故障仍然可用，就会无缝地继续，而对于长期故障，使用未受影响的副本作为源，自动重新平衡丢失节点的副本

## 3.配置蟑螂数据库

在我们[安装 CockroachDB](https://www.cockroachlabs.com/docs/stable/install-cockroachdb.html)之后，我们可以启动本地集群的第一个节点：

```shell
cockroach start --insecure --host=localhost;
```

出于演示目的，我们使用不安全属性，使通信未加密，无需指定证书位置。

此时，我们的本地集群已启动并运行。只有一个节点，我们已经可以连接到它并进行操作，但为了更好地利用 CockroachDB 的自动、重新平衡和容错功能，我们将再添加两个节点：

```shell
cockroach start --insecure --store=node2 
  --host=localhost --port=26258 --http-port=8081 
  --join=localhost:26257;

cockroach start --insecure --store=node3 
  --host=localhost --port=26259 --http-port=8082 
  --join=localhost:26257;
```

对于另外两个节点，我们使用加入标志将新节点连接到集群，指定第一个节点的地址和端口，在我们的例子中是 localhost:26257。本地集群上的每个节点都需要唯一的store、port和http-port值。

当配置 CockroachDB 的分布式集群时，每个节点将在不同的机器上，因此可以避免指定port、store和http-port ，因为默认值就足够了。此外，将其他节点加入集群时，应使用第一个节点的实际 IP。

### 3.1. 配置数据库和用户

一旦我们的集群启动并运行，通过 CockroachDB 提供的 SQL 控制台，我们需要创建我们的数据库和用户。

首先，让我们启动 SQL 控制台：

```shell
cockroach sql --insecure;
```

现在，让我们创建我们的testdb数据库，创建一个用户并向该用户添加授权以便能够执行 CRUD 操作：

```sql
CREATE DATABASE testdb;
CREATE USER user17 with password 'qwerty';
GRANT ALL ON DATABASE testdb TO user17;
```

如果我们要验证数据库是否正确创建，我们可以列出当前节点中创建的所有数据库：

```sql
SHOW DATABASES;
```

最后，如果我们想验证 CockroachDB 的自动功能，我们可以检查其他两个节点之一是否正确创建了数据库。为此，我们必须在使用 SQL 控制台时表达端口标志：

```shell
cockroach sql --insecure --port=26258;
```

## 4.监控CockroachDB

现在我们已经启动了本地集群并创建了数据库，我们可以使用 CockroachDB Admin UI 监控它们：

[![蟑螂数据库监控](https://www.baeldung.com/wp-content/uploads/2018/01/CockroachDB_Monitoring-1024x487-1024x487.png)](https://www.baeldung.com/wp-content/uploads/2018/01/CockroachDB_Monitoring-1024x487.png)

一旦集群启动并运行，就可以通过http://localhost:8080访问这个与 CockroachDB 捆绑在一起的管理 UI 。特别是，它提供了有关集群和数据库配置的详细信息，并通过监控以下指标帮助我们优化集群性能：

-   集群健康——关于集群健康的基本指标
-   运行时指标——关于节点数、CPU 时间和内存使用的指标
-   SQL 性能——关于 SQL 连接、查询和事务的指标
-   详细信息——关于如何在集群中数据的指标
-   节点详细信息——活动、死亡和退役节点的详细信息
-   数据库详细信息——有关集群中系统和用户数据库的详细信息

## 5.项目设置

考虑到我们正在运行的本地 CockroachDB 集群，为了能够连接到它，我们必须向我们的pom.xml添加一个[额外的依赖项：](https://search.maven.org/classic/#search|ga|1|g%3A"org.postgresql" AND a%3A"postgresql")

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.1.4</version>
</dependency>
```

或者，对于 Gradle 项目：

```plaintext
compile 'org.postgresql:postgresql:42.1.4'
```

## 6. 使用蟑螂数据库

现在很清楚我们正在使用什么并且一切都已正确设置，让我们开始使用它。

由于 PostgreSQL 的兼容性，可以直接连接 JDBC 或使用 Hibernate 等 ORM(在撰写本文时(2018 年 1 月)，这两个驱动程序都已经过足够的测试，可以根据开发人员的说法获得Beta 级支持) . 在我们的例子中，我们将使用 JDBC 与数据库交互。

为简单起见，我们将遵循基本的 CRUD 操作，因为它们是最好的开始。

让我们从连接到数据库开始。

### 6.1. 连接到蟑螂数据库

要打开与数据库的连接，我们可以使用DriverManager类的getConnection()方法。此方法需要连接 URL字符串参数、用户名和密码：

```java
Connection con = DriverManager.getConnection(
  "jdbc:postgresql://localhost:26257/testdb", "user17", "qwerty"
);
```

### 6.2. 创建表

通过工作连接，我们可以开始创建我们将用于所有 CRUD 操作的文章表：

```java
String TABLE_NAME = "articles";
StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ")
  .append(TABLE_NAME)
  .append("(id uuid PRIMARY KEY, ")
  .append("title string,")
  .append("author string)");

String query = sb.toString();
Statement stmt = connection.createStatement();
stmt.execute(query);
```

如果我们想验证表是否正确创建，我们可以使用SHOW TABLES命令：

```java
PreparedStatement preparedStatement = con.prepareStatement("SHOW TABLES");
ResultSet resultSet = preparedStatement.executeQuery();
List tables = new ArrayList<>();
while (resultSet.next()) {
    tables.add(resultSet.getString("Table"));
}

assertTrue(tables.stream().anyMatch(t -> t.equals(TABLE_NAME)));
```

让我们看看如何修改刚刚创建的表。

### 6.3. 改变表

如果我们在创建表的过程中遗漏了一些列，或者因为我们稍后需要它们，我们可以轻松地添加它们：

```java
StringBuilder sb = new StringBuilder("ALTER TABLE ").append(TABLE_NAME)
  .append(" ADD ")
  .append(columnName)
  .append(" ")
  .append(columnType);

String query = sb.toString();
Statement stmt = connection.createStatement();
stmt.execute(query);
```

更改表后，我们可以使用SHOW COLUMNS FROM命令验证是否添加了新列：

```java
String query = "SHOW COLUMNS FROM " + TABLE_NAME;
PreparedStatement preparedStatement = con.prepareStatement(query);
ResultSet resultSet = preparedStatement.executeQuery();
List<String> columns = new ArrayList<>();
while (resultSet.next()) {
    columns.add(resultSet.getString("Field"));
}

assertTrue(columns.stream().anyMatch(c -> c.equals(columnName)));
```

### 6.4. 删除表

在处理表格时，有时我们需要删除它们，这可以通过几行代码轻松实现：

```java
StringBuilder sb = new StringBuilder("DROP TABLE IF EXISTS ")
  .append(TABLE_NAME);

String query = sb.toString();
Statement stmt = connection.createStatement();
stmt.execute(query);
```

### 6.5. 插入数据

一旦我们清楚了可以在表上执行的操作，我们现在就可以开始处理数据了。我们可以开始定义Article类：

```java
public class Article {

    private UUID id;
    private String title;
    private String author;

    // standard constructor/getters/setters
}
```

现在我们可以看到如何将文章添加到我们的文章表中：

```java
StringBuilder sb = new StringBuilder("INSERT INTO ").append(TABLE_NAME)
  .append("(id, title, author) ")
  .append("VALUES (?,?,?)");

String query = sb.toString();
PreparedStatement preparedStatement = connection.prepareStatement(query);
preparedStatement.setString(1, article.getId().toString());
preparedStatement.setString(2, article.getTitle());
preparedStatement.setString(3, article.getAuthor());
preparedStatement.execute();
```

### 6.6. 读取数据

一旦数据存储在表中，我们想要读取这些数据，这很容易实现：

```java
StringBuilder sb = new StringBuilder("SELECT  FROM ")
  .append(TABLE_NAME);

String query = sb.toString();
PreparedStatement preparedStatement = connection.prepareStatement(query);
ResultSet rs = preparedStatement.executeQuery();
```

但是，如果我们不想读取articles表中的所有数据而只想读取一个Article，我们可以简单地更改构建PreparedStatement的方式：

```java
StringBuilder sb = new StringBuilder("SELECT  FROM ").append(TABLE_NAME)
  .append(" WHERE title = ?");

String query = sb.toString();
PreparedStatement preparedStatement = connection.prepareStatement(query);
preparedStatement.setString(1, title);
ResultSet rs = preparedStatement.executeQuery();
```

### 6.7. 删除数据

最后但同样重要的是，如果我们想从表中删除数据，我们可以使用标准的DELETE FROM命令删除一组有限的记录：

```java
StringBuilder sb = new StringBuilder("DELETE FROM ").append(TABLE_NAME)
  .append(" WHERE title = ?");

String query = sb.toString();
PreparedStatement preparedStatement = connection.prepareStatement(query);
preparedStatement.setString(1, title);
preparedStatement.execute();
```

或者我们可以使用TRUNCATE函数删除表中的所有记录：

```java
StringBuilder sb = new StringBuilder("TRUNCATE TABLE ")
  .append(TABLE_NAME);

String query = sb.toString();
Statement stmt = connection.createStatement();
stmt.execute(query);
```

### 6.8. 处理交易

一旦连接到数据库，默认情况下，每个单独的 SQL 语句都被视为一个事务，并在其执行完成后立即自动提交。

但是，如果我们希望允许两个或多个 SQL 语句组合到一个事务中，我们必须以编程方式控制事务。

首先，我们需要通过将Connection的autoCommit属性设置为false来禁用自动提交模式，然后使用commit()和rollback()方法来控制事务。

让我们看看在进行多次插入时如何实现数据一致性：

```java
try {
    con.setAutoCommit(false);

    UUID articleId = UUID.randomUUID();

    Article article = new Article(
      articleId, "Guide to CockroachDB in Java", "baeldung"
    );
    articleRepository.insertArticle(article);

    article = new Article(
      articleId, "A Guide to MongoDB with Java", "baeldung"
    );
    articleRepository.insertArticle(article); // Exception

    con.commit();
} catch (Exception e) {
    con.rollback();
} finally {
    con.setAutoCommit(true);
}
```

在这种情况下，在第二次插入时抛出异常，因为违反了主键约束，因此没有文章被插入到articles表中。

## 七. 总结

在本文中，我们解释了 CockroachDB 是什么，如何设置一个简单的本地集群，以及我们如何从Java与它交互。
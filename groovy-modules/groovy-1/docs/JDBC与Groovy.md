## 1. 简介

在本文中，我们将了解如何使用惯用的Groovy通过[JDBC查询关系数据库。](https://www.baeldung.com/java-jdbc)

JDBC 虽然相对较低级别，但却是JVM上大多数ORM和其他高级数据访问库的基础。当然，我们可以直接在Groovy中使用JDBC；但是，它有一个相当繁琐的API。

对我们来说幸运的是，Groovy标准库构建在JDBC之上，提供了一个干净、简单但功能强大的接口。因此，我们将探索GroovySQL模块。

我们将在普通Groovy中查看JDBC，而不考虑任何框架，例如我们有[其他指南](https://www.baeldung.com/spring-jdbc-jdbctemplate)的Spring。

## 2. JDBC和Groovy设置

我们必须在依赖项中包含groovy-sql模块：

```xml
<dependency>
    <groupId>org.codehaus.groovy</groupId>
    <artifactId>groovy</artifactId>
    <version>2.4.13</version>
</dependency>
<dependency>
    <groupId>org.codehaus.groovy</groupId>
    <artifactId>groovy-sql</artifactId>
    <version>2.4.13</version>
</dependency>
```

如果我们使用 groovy-all，则没有必要明确列出它：

```xml
<dependency>
    <groupId>org.codehaus.groovy</groupId>
    <artifactId>groovy-all</artifactId>
    <version>2.4.13</version>
</dependency>
```

我们可以在 Maven Central 上找到最新版本的[groovy](https://search.maven.org/classic/#search|gav|1|g%3A"org.codehaus.groovy" AND a%3A"groovy")、[groovy-sql](https://search.maven.org/classic/#search|gav|1|g%3A"org.codehaus.groovy" AND a%3A"groovy-sql") 和[groovy-all 。](https://search.maven.org/classic/#search|gav|1|g%3A"org.codehaus.groovy" AND a%3A"groovy-all")

## 3. 连接数据库

为了使用数据库，我们必须做的第一件事就是连接到它。

让我们介绍一下groovy.sql.Sql类，我们将使用它来通过GroovySQL 模块对数据库进行所有操作。

Sql的实例代表我们要操作的数据库。

但是，Sql实例 不是单个数据库连接。我们稍后会讨论连接，我们现在不要担心它们；让我们假设一切都神奇地起作用。

### 3.1. 指定连接参数

在整篇文章中，我们将使用 HSQL 数据库，这是一种主要用于测试的轻量级关系数据库。

数据库连接需要 URL、驱动程序和访问凭证：

```java
Map dbConnParams = [
  url: 'jdbc:hsqldb:mem:testDB',
  user: 'sa',
  password: '',
  driver: 'org.hsqldb.jdbc.JDBCDriver']
```

在这里，我们选择使用Map指定那些，尽管这不是唯一可能的选择。

然后我们可以从Sql类中获取一个连接：

```groovy
def sql = Sql.newInstance(dbConnParams)
```

我们将在以下部分中看到如何使用它。

完成后，我们应该始终释放任何相关资源：

```groovy
sql.close()
```

### 3.2. 使用数据源

使用数据源连接到数据库是很常见的，尤其是在应用程序服务器内运行的程序中。

此外，当我们想要汇集连接或使用 JNDI 时，数据源是最自然的选择。

Groovy 的Sql类可以很好地接受数据源：

```groovy
def sql = Sql.newInstance(datasource)
```

### 3.3. 自动资源管理

当我们完成一个Sql实例时记住调用close()是乏味的；毕竟，机器比我们记住的东西要好得多。

使用Sql，我们可以将我们的代码包装在一个闭包中，并在控制离开时让Groovy自动调用close()，即使在出现异常的情况下也是如此：

```groovy
Sql.withInstance(dbConnParams) {
    Sql sql -> haveFunWith(sql)
}
```

## 4.针对数据库发布语句

现在，我们可以继续讨论有趣的事情了。

对数据库发出语句的最简单和非专业化的方法是execute方法：

```groovy
sql.execute "create table PROJECT (id integer not null, name varchar(50), url varchar(100))"
```

理论上，它既适用于 DDL/DML 语句，也适用于查询；然而，上面的简单形式并没有提供返回查询结果的方法。我们将在以后进行查询。

execute方法有几个重载版本，但是，我们将在后面的部分再次查看此方法和其他方法的更高级用例。

### 4.1. 插入数据

对于插入少量数据和简单场景，前面讨论的execute方法非常好。

然而，对于我们已经生成列(例如，使用序列或自动递增)并且我们想知道生成的值的情况，存在一个专用方法：executeInsert 。

至于execute，我们现在将看看可用的最简单的方法重载，将更复杂的变体留到后面的部分。

所以，假设我们有一个带有自动递增主键的表(HSQLDB 用语中的标识)：

```groovy
sql.execute "create table PROJECT (ID IDENTITY, NAME VARCHAR (50), URL VARCHAR (100))"
```

让我们在表中插入一行并将结果保存在一个变量中：

```groovy
def ids = sql.executeInsert """
  INSERT INTO PROJECT (NAME, URL) VALUES ('tutorials', 'github.com/eugenp/tutorials')
"""
```

executeInsert的行为与execute完全一样，但它返回什么？

事实证明，返回值是一个矩阵：它的行是插入的行(请记住，一条语句可以导致插入多行)，它的列是生成的值。

这听起来很复杂，但在我们的例子中，这是迄今为止最常见的例子，只有一行和一个生成的值：

```groovy
assertEquals(0, ids[0][0])
```

随后的插入将返回生成的值 1：

```groovy
ids = sql.executeInsert """
  INSERT INTO PROJECT (NAME, URL)
  VALUES ('REST with Spring', 'github.com/eugenp/REST-With-Spring')
"""

assertEquals(1, ids[0][0])
```

### 4.2. 更新和删除数据

同样，存在一个专门用于数据修改和删除的方法：executeUpdate。

同样，这与execute only 的区别在于它的返回值，我们将只看它的最简单形式。

在这种情况下，返回值是一个整数，即受影响的行数：

```groovy
def count = sql.executeUpdate("UPDATE PROJECT SET URL = 'https://' + URL")

assertEquals(2, count)
```

## 5.查询数据库

当我们查询数据库时，事情开始变得 Groovy。

处理 JDBC ResultSet类并不十分有趣。对我们来说幸运的是，Groovy 提供了对所有这些的很好的抽象。

### 5.1. 遍历查询结果

虽然循环是如此古老的风格……但现在我们都喜欢闭包。

Groovy 是为了迎合我们的口味：

```groovy
sql.eachRow("SELECT  FROM PROJECT") { GroovyResultSet rs ->
    haveFunWith(rs)
}
```

eachRow方法发出我们对数据库的查询并对每一行调用一个闭包。

正如我们所见，一行由GroovyResultSet的实例表示，它是普通旧ResultSet的扩展，并添加了一些好东西。继续阅读以了解更多信息。

### 5.2. 访问结果集

除了所有的ResultSet方法之外，GroovyResultSet还提供了一些方便的实用程序。

主要是，它公开了与列名匹配的命名属性：

```groovy
sql.eachRow("SELECT  FROM PROJECT") { rs ->
    assertNotNull(rs.name)
    assertNotNull(rs.URL)
}
```

请注意属性名称是如何区分大小写的。

GroovyResultSet还使用从零开始的索引提供对列的访问：

```groovy
sql.eachRow("SELECT  FROM PROJECT") { rs ->
    assertNotNull(rs[0])
    assertNotNull(rs[1])
    assertNotNull(rs[2])
}
```

### 5.3. 分页

我们可以轻松地对结果进行分页，即只加载从某个偏移量开始到某个最大行数的子集。例如，这是 Web 应用程序中的一个常见问题。

eachRow和相关方法具有接受偏移量和最大返回行数的重载：

```groovy
def offset = 1
def maxResults = 1
def rows = sql.rows('SELECT  FROM PROJECT ORDER BY NAME', offset, maxResults)

assertEquals(1, rows.size())
assertEquals('REST with Spring', rows[0].name)
```

在这里，rows方法返回一个行列表，而不是像eachRow那样遍历它们。

## 6. 参数化查询和语句

通常情况下，查询和语句在编译时并不完全固定；它们通常以参数的形式具有静态部分和动态部分。

如果你正在考虑字符串连接，现在停下来阅读有关 SQL 注入的内容！

我们之前提到过，我们在前面的部分中看到的方法有许多针对各种场景的重载。

让我们介绍那些处理 SQL 查询和语句中的参数的重载。

### 6.1. 带占位符的字符串

在类似于普通 JDBC 的风格中，我们可以使用位置参数：

```groovy
sql.execute(
    'INSERT INTO PROJECT (NAME, URL) VALUES (?, ?)',
    'tutorials', 'github.com/eugenp/tutorials')
```

或者我们可以使用带有映射的命名参数：

```groovy
sql.execute(
    'INSERT INTO PROJECT (NAME, URL) VALUES (:name, :url)',
    [name: 'REST with Spring', url: 'github.com/eugenp/REST-With-Spring'])
```

这适用于execute、executeUpdate、rows和eachRow。executeInsert也支持参数，但它的签名有点不同且更棘手。

### 6.2.Groovy字符串

我们还可以选择使用带有占位符的 GString 的 Groovier 风格。

我们看到的所有方法都不会以通常的方式替换 GString 中的占位符；相反，他们将它们作为 JDBC 参数插入，确保正确保留 SQL 语法，无需引用或转义任何内容，因此没有注入风险。

这是非常好的、安全的和Groovy的：

```groovy
def name = 'REST with Spring'
def url = 'github.com/eugenp/REST-With-Spring'
sql.execute "INSERT INTO PROJECT (NAME, URL) VALUES (${name}, ${url})"
```

## 7. 交易与联系

到目前为止，我们已经跳过了一个非常重要的问题：事务。

事实上，我们也根本没有讨论Groovy的Sql如何管理连接。

### 7.1. 短暂的连接

在目前给出的示例中，每个查询或语句都使用新的专用连接发送到数据库。Sql在操作终止后立即关闭连接。

当然，如果我们使用连接池，对性能的影响可能很小。

不过，如果我们想将多个 DML 语句和查询作为单个原子操作发出，我们需要一个事务。

此外，为了让交易成为可能，我们需要一个跨越多个语句和查询的连接。

### 7.2. 具有缓存连接的事务

Groovy SQL 不允许我们显式地创建或访问事务。

相反，我们使用带有闭包的withTransaction方法：

```groovy
sql.withTransaction {
    sql.execute """
        INSERT INTO PROJECT (NAME, URL)
        VALUES ('tutorials', 'github.com/eugenp/tutorials')
    """
    sql.execute """
        INSERT INTO PROJECT (NAME, URL)
        VALUES ('REST with Spring', 'github.com/eugenp/REST-With-Spring')
    """
}
```

在闭包内部，单个数据库连接用于所有查询和语句。

此外，事务在闭包终止时自动提交，除非它由于异常提前退出。

但是，我们也可以通过Sql类中的方法手动提交或回滚当前事务：

```groovy
sql.withTransaction {
    sql.execute """
        INSERT INTO PROJECT (NAME, URL)
        VALUES ('tutorials', 'github.com/eugenp/tutorials')
    """
    sql.commit()
    sql.execute """
        INSERT INTO PROJECT (NAME, URL)
        VALUES ('REST with Spring', 'github.com/eugenp/REST-With-Spring')
    """
    sql.rollback()
}
```

### 7.3. 没有事务的缓存连接

最后，要在没有上述事务语义的情况下重用数据库连接，我们使用cacheConnection：

```groovy
sql.cacheConnection {
    sql.execute """
        INSERT INTO PROJECT (NAME, URL)
        VALUES ('tutorials', 'github.com/eugenp/tutorials')
    """
    throw new Exception('This does not roll back')
}
```

## 8. 总结和延伸阅读

在本文中，我们了解了GroovySQL 模块以及它如何使用闭包和Groovy字符串增强和简化 JDBC。

然后我们可以安全地得出总结，普通的旧 JDBC 看起来更现代一点，带有一点 Groovy！

我们还没有讨论GroovySQL 的每一个特性；例如，我们省略了[批处理](https://www.baeldung.com/jdbc-batch-processing)、存储过程、元数据和其他内容。

有关详细信息，请参阅[Groovy 文档](http://docs.groovy-lang.org/latest/html/api/groovy/sql/Sql.html)。
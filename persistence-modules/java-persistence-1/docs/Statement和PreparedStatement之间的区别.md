## 1. 概述

[在本教程中，我们将探讨JDBC](https://www.baeldung.com/java-jdbc)的Statement和PreparedStatement接口之间的差异。我们不会介绍CallableStatement，这是一个用于执行存储过程的JDBC API 接口。

## 2.JDBC API接口

 Statement和PreparedStatement都可以用来执行 SQL 查询。这些界面看起来非常相似。但是，它们在功能和性能上彼此显着不同： 

-   语句——用于执行基于字符串的 SQL查询
-   PreparedStatement –用于执行参数化 SQL 查询

为了能够在我们的示例中使用Statement和PreparedStatement ，我们将在pom.xml文件 中将h2 JDBC 连接器声明为依赖项：

```xml
<dependency>
  <groupId>com.h2database</groupId>
  <artifactId>h2</artifactId>
  <version>1.4.200</version>
</dependency>
```

让我们定义一个我们将在整篇文章中使用的实体：

```java
public class PersonEntity {
    private int id;
    private String name;

    // standard setters and getters
}
```

## 三、声明

首先，Statement接口接受字符串作为 SQL 查询。因此，当我们连接 SQL字符串时，代码的可读性会降低：

```java
public void insert(PersonEntity personEntity) {
    String query = "INSERT INTO persons(id, name) VALUES(" + personEntity.getId() + ", '"
      + personEntity.getName() + "')";

    Statement statement = connection.createStatement();
    statement.executeUpdate(query);
}
```

其次，易受[SQL注入](https://www.baeldung.com/cs/sql-injection)攻击。下面的例子说明了这个弱点。

在第一行，update 会将所有行的“ name ”列设置为“ hacker ”，因为“—”之后的任何内容在 SQL 中都被解释为注解，更新语句的条件将被忽略。在第二行中，插入将失败，因为“ name ”列上的引号没有被转义：

```java
dao.update(new PersonEntity(1, "hacker' --"));
dao.insert(new PersonEntity(1, "O'Brien"))
```

第三，JDBC 将带有内联值的查询传递给数据库。因此，没有查询优化，最重要的是，数据库引擎必须确保所有检查。此外，查询不会像数据库一样出现，并且会阻止缓存使用。同样，批量更新需要单独执行：

```java
public void insert(List<PersonEntity> personEntities) {
    for (PersonEntity personEntity: personEntities) {
        insert(personEntity);
    }
}
```

第四，Statement接口适用于CREATE、ALTER和DROP等DDL查询 ：

```java
public void createTables() {
    String query = "create table if not exists PERSONS (ID INT, NAME VARCHAR(45))";
    connection.createStatement().executeUpdate(query);
}
```

最后，Statement 接口不能用于存储和检索文件和数组。

## 4.准备好的语句

首先，PreparedStatement扩展了Statement接口。它具有绑定各种对象类型的方法，包括文件和数组。因此，代码变得 容易理解：

```java
public void insert(PersonEntity personEntity) {
    String query = "INSERT INTO persons(id, name) VALUES( ?, ?)";

    PreparedStatement preparedStatement = connection.prepareStatement(query);
    preparedStatement.setInt(1, personEntity.getId());
    preparedStatement.setString(2, personEntity.getName());
    preparedStatement.executeUpdate();
}
```

其次，它通过转义提供的所有参数值的文本来防止 SQL 注入：

```java
@Test 
void whenInsertAPersonWithQuoteInText_thenItNeverThrowsAnException() {
    assertDoesNotThrow(() -> dao.insert(new PersonEntity(1, "O'Brien")));
}

@Test 
void whenAHackerUpdateAPerson_thenItUpdatesTheTargetedPerson() throws SQLException {

    dao.insert(Arrays.asList(new PersonEntity(1, "john"), new PersonEntity(2, "skeet")));
    dao.update(new PersonEntity(1, "hacker' --"));

    List<PersonEntity> result = dao.getAll();
    assertEquals(Arrays.asList(
      new PersonEntity(1, "hacker' --"), 
      new PersonEntity(2, "skeet")), result);
}
```

第三，PreparedStatement使用预编译。一旦数据库得到一个查询，它就会在预编译查询之前检查缓存。因此，如果它没有被缓存，数据库引擎将保存它以备下次使用。

此外，此功能通过非 SQL 二进制协议加速了数据库与 JVM 之间的通信。也就是说，数据包中的数据较少，因此服务器之间的通信速度更快。

第四，PreparedStatement提供了在单个数据库连接期间的[批处理](https://www.baeldung.com/jdbc-batch-processing)。让我们看看实际效果：

```java
public void insert(List<PersonEntity> personEntities) throws SQLException {
    String query = "INSERT INTO persons(id, name) VALUES( ?, ?)";
    PreparedStatement preparedStatement = connection.prepareStatement(query);
    for (PersonEntity personEntity: personEntities) {
        preparedStatement.setInt(1, personEntity.getId());
        preparedStatement.setString(2, personEntity.getName());
        preparedStatement.addBatch();
    }
    preparedStatement.executeBatch();
}
```

接下来，PreparedStatement提供了一种使用BLOB和 CLOB数据类型存储和检索文件的简单方法。同样，它通过将java.sql.Array转换为 SQL 数组来帮助存储列表。

最后，PreparedStatement实现getMetadata()等方法，其中包含有关返回结果的信息。

## 5.总结

在本教程中，我们介绍了PreparedStatement和Statement之间的主要区别。这两个接口都提供了执行 SQL 查询的方法，但是更适合使用Statement进行 DDL 查询，使用PreparedStatement进行 DML 查询。
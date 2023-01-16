## 1. 概述

在本快速教程中，我们将了解如何使用纯 JDBC 获取最后自动生成的密钥。

## 2.设置

为了能够执行 SQL 查询，我们将使用内存中的[H2](https://search.maven.org/artifact/com.h2database/h2)数据库。

那么，对于我们的第一步，让我们添加它的 Maven 依赖项：

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>1.4.200</version>
</dependency>
```

此外，我们将使用一个只有两列的非常简单的表：

```java
public class JdbcInsertIdIntegrationTest {

    private static Connection connection;

    @BeforeClass
    public static void setUp() throws Exception {
        connection = DriverManager.getConnection("jdbc:h2:mem:generated-keys", "sa", "");
        connection
          .createStatement()
          .execute("create table persons(id bigint auto_increment, name varchar(255))");
    }

    @AfterClass
    public static void tearDown() throws SQLException {
        connection
          .createStatement()
          .execute("drop table persons");
        connection.close();
    }

    // omitted
}
```

在这里，我们连接到 生成的密钥 内存数据库并在其中创建一个名为 persons的表。

## 3.返回生成的密钥标志

在自动生成后获取密钥的一种方法是将[Statement.RETURN_GENERATED_KEYS](https://docs.oracle.com/en/java/javase/11/docs/api/java.sql/java/sql/Statement.html#RETURN_GENERATED_KEYS) 传递给 [prepareStatement()](https://docs.oracle.com/en/java/javase/11/docs/api/java.sql/java/sql/Connection.html#prepareStatement(java.lang.String,int)) 方法：

```java
String QUERY = "insert into persons (name) values (?)";
try (PreparedStatement statement = connection.prepareStatement(QUERY, Statement.RETURN_GENERATED_KEYS)) {
    statement.setString(1, "Foo");
    int affectedRows = statement.executeUpdate();
    assertThat(affectedRows).isPositive();

    // omitted
} catch (SQLException e) {
    // handle the database related exception appropriately
}
```

准备并执行查询后，我们可以调用 PreparedStatement 上的[getGeneratedKeys()](https://docs.oracle.com/en/java/javase/11/docs/api/java.sql/java/sql/Statement.html#getGeneratedKeys()) 方法 来获取 id：

```java
try (ResultSet keys = statement.getGeneratedKeys()) {
    assertThat(keys.next()).isTrue();
    assertThat(keys.getLong(1)).isGreaterThanOrEqualTo(1);
}
```

如上所示，我们首先调用 next() 方法来移动结果游标。然后我们使用 getLong() 方法获取第一列，同时将其转换为 long 。

此外，也可以对普通 的Statement使用相同的技术：

```java
try (Statement statement = connection.createStatement()) {
    String query = "insert into persons (name) values ('Foo')";
    int affectedRows = statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
    assertThat(affectedRows).isPositive();

    try (ResultSet keys = statement.getGeneratedKeys()) {
        assertThat(keys.next()).isTrue();
        assertThat(keys.getLong(1)).isGreaterThanOrEqualTo(1);
    }
}
```

此外，值得一提的是，我们广泛使用[try-with-resources](https://www.baeldung.com/java-try-with-resources) 让编译器在我们之后进行清理。

## 4.返回列

事实证明，我们还可以要求 JDBC 在发出查询后返回特定的列。为此，我们只需要传递一个列名数组：

```java
try (PreparedStatement statement = connection.prepareStatement(QUERY, new String[] { "id" })) {
    statement.setString(1, "Foo");
    int affectedRows = statement.executeUpdate();
    assertThat(affectedRows).isPositive();

    // omitted
}
```

如上所示，我们告诉 JDBC 在执行给定查询后返回id 列的值。与前面的示例类似，我们可以在之后获取 id ：

```java
try (ResultSet keys = statement.getGeneratedKeys()) {
    assertThat(keys.next()).isTrue();
    assertThat(keys.getLong(1)).isGreaterThanOrEqualTo(1);
}
```

我们也可以对简单的Statement使用相同的方法 ：

```java
try (Statement statement = connection.createStatement()) {
    int affectedRows = statement.executeUpdate("insert into persons (name) values ('Foo')", 
      new String[] { "id" });
    assertThat(affectedRows).isPositive();

    try (ResultSet keys = statement.getGeneratedKeys()) {
        assertThat(keys.next()).isTrue();
        assertThat(keys.getLong(1)).isGreaterThanOrEqualTo(1);
    }
}
```

## 5.总结

在本快速教程中，我们了解了如何在使用纯 JDBC 执行查询后获取生成的键。
## 1. 概述

在本快速教程中，我们将讨论如何从 JDBC Connection对象获取数据库 URL。

## 2.示例类

为了演示这一点，我们将创建一个 带有方法getConnection的DBConfiguration类：

```java
public class DBConfiguration {

    public static Connection getConnection() throws Exception {
        Class.forName("org.h2.Driver");
        String url = "jdbc:h2:mem:testdb";
        return DriverManager.getConnection(url, "user", "password");
    }
}
```

## 3. DatabaseMetaData#getURL方法

我们可以使用[DatabaseMetaData#getURL](https://docs.oracle.com/en/java/javase/11/docs/api/java.sql/java/sql/DatabaseMetaData.html#getURL())方法获取数据库 URL：

```java
@Test
void givenConnectionObject_whenExtractMetaData_thenGetDbURL() throws Exception {
    Connection connection = DBConfiguration.getConnection();
    String dbUrl = connection.getMetaData().getURL();
    assertEquals("jdbc:h2:mem:testdb", dbUrl);
}
```

在上面的例子中，我们首先获取了Connection实例。

然后，我们调用Connection上的getMetaData方法来获取[DatabaseMetaData](https://www.baeldung.com/jdbc-database-metadata#databasemetadata-interface)。

最后，我们在[DatabaseMetaData](https://www.baeldung.com/jdbc-database-metadata#databasemetadata-interface)实例上调用getURL方法。如我们所料，它返回我们数据库的 URL。

## 4. 总结

在本教程中，我们了解了如何从 JDBC Connection对象获取数据库 URL。
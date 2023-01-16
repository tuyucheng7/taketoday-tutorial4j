## 1. 简介

在本教程中，我们将了解如何使用 JDBC 和纯 SQL 检查数据库中是否存在表。

## 2. 使用数据库元数据

[JDBC](https://www.baeldung.com/java-jdbc)为我们提供了读取和写入数据库数据的工具。除了存储在表中的实际数据外，我们还可以读取描述数据库的元数据。为此，我们将使用可以从 JDBC 连接获取的[DatabaseMetaData对象：](https://www.baeldung.com/jdbc-database-metadata)

```java
DatabaseMetaData databaseMetaData = connection.getMetaData();
```

DatabaseMetaData提供了很多信息丰富的方法，但我们只需要一个：getTables。让我们用它来打印所有可用的表：

```java
ResultSet resultSet = databaseMetaData.getTables(null, null, null, new String[] {"TABLE"});

while (resultSet.next()) {
    String name = resultSet.getString("TABLE_NAME");
    String schema = resultSet.getString("TABLE_SCHEM");
    System.out.println(name + " on schema " + schema);
}
```

因为我们没有提供前三个参数，所以我们得到了所有目录和模式中的所有表。我们还可以将查询范围缩小到，例如，只有一个模式：

```java
ResultSet resultSet = databaseMetaData.getTables(null, "PUBLIC", null, new String[] {"TABLE"});
```

## 3.使用DatabaseMetaData检查表是否存在

如果我们想检查一个表是否存在，我们不需要遍历结果集。我们只需要检查结果集是否为空。让我们首先创建一个“EMPLOYEE”表：

```java
connection.createStatement().executeUpdate("create table EMPLOYEE (id int primary key auto_increment, name VARCHAR(255))");
```

现在我们可以使用元数据对象来断言我们刚刚创建的表确实存在：

```java
boolean tableExists(Connection connection, String tableName) throws SQLException {
    DatabaseMetaData meta = connection.getMetaData();
    ResultSet resultSet = meta.getTables(null, null, tableName, new String[] {"TABLE"});

    return resultSet.next();
}
```

请注意，虽然 SQL 不区分大小写，但getTables方法的实现是。即使我们用小写字母定义一个表，它也会以大写字母存储。因此，getTables方法将对大写的表名进行操作，因此我们需要使用“EMPLOYEE”而不是“employee”。

## 4. SQL查询表是否存在

虽然DatabaseMetaData 很方便，但我们可能需要使用纯 SQL 来实现相同的目标。为此，我们需要查看位于模式“ information_schema ”中的“ tables ”表。它是[SQL-92](https://en.wikipedia.org/wiki/SQL-92)标准的一部分，大多数主要数据库引擎都实现了它(Oracle 除外)。

让我们查询“ tables ”表并计算获取了多少结果。如果表存在，我们期望一个，如果不存在，我们期望为零：

```sql
SELECT count() FROM information_schema.tables
WHERE table_name = 'EMPLOYEE' 
LIMIT 1;
```

将它与 JDBC 一起使用是创建一个简单的[准备语句](https://www.baeldung.com/java-statement-preparedstatement)然后检查结果计数是否不等于零的问题：

```java
static boolean tableExistsSQL(Connection connection, String tableName) throws SQLException {
    PreparedStatement preparedStatement = connection.prepareStatement("SELECT count() "
      + "FROM information_schema.tables "
      + "WHERE table_name = ?"
      + "LIMIT 1;");
    preparedStatement.setString(1, tableName);

    ResultSet resultSet = preparedStatement.executeQuery();
    resultSet.next();
    return resultSet.getInt(1) != 0;
}
```

## 5.总结

在本教程中，我们学习了如何查找有关数据库中表存在的信息。我们同时使用了 JDBC 的DatabaseMetaData和纯 SQL。
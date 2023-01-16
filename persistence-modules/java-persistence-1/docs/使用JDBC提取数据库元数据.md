## 1. 概述

[JDBC](https://www.baeldung.com/java-jdbc)提供了一个JavaAPI 来读取存储在数据库表中的实际数据。除此之外，相同的 API 也可用于读取有关数据库的元数据。元数据是指有关数据的数据，例如表名、列名和列类型。

在本教程中，我们将学习如何使用DatabaseMetaData接口提取不同类型的元数据。

## 2.数据库元数据接口

[DatabaseMetaData](https://docs.oracle.com/en/java/javase/11/docs/api/java.sql/java/sql/DatabaseMetaData.html)是一个接口，提供了多种获取数据库综合信息的方法。此信息对于创建允许用户探索不同数据库结构的数据库工具很有用。当我们想要检查底层数据库是否支持某些功能时，它也很有帮助。

我们需要一个DatabaseMetaData实例来获取此信息。那么，让我们在代码中看看如何从Connection对象中获取它：

```java
DatabaseMetaData databaseMetaData = connection.getMetaData();
```

此处，连接是JdbcConnection的一个实例。因此，getMetaData()方法返回一个JdbcDatabaseMetaData对象，它实现了DatabaseMetaData接口。

在接下来的几节中，我们将使用此对象来获取不同类型的元数据。之后，我们还将学习如何检查数据库是否支持特定功能。

## 3. 表元数据

有时，我们想知道所有用户定义的表、系统表或视图的名称。此外，我们可能想知道对表格的一些解释性评论。所有这些都可以通过使用DatabaseMetaData对象的getTables()方法来完成。

首先，让我们看看如何提取所有现有用户定义表的名称：

```java
try(ResultSet resultSet = databaseMetaData.getTables(null, null, null, new String[]{"TABLE"})){ 
  while(resultSet.next()) { 
    String tableName = resultSet.getString("TABLE_NAME"); 
    String remarks = resultSet.getString("REMARKS"); 
  }
}
```

在这里，前两个参数是catalog和schema。第三个参数采用表名模式。例如，如果我们提供“CUST%”，这将包括名称以“CUST”开头的所有表。最后一个参数采用包含表类型的字符串数组。将TABLE用于用户定义的表。

接下来，如果我们要查找系统定义的表，我们所要做的就是将表类型替换为“ SYSTEM TABLE ”：

```java
try(ResultSet resultSet = databaseMetaData.getTables(null, null, null, new String[]{"SYSTEM TABLE"})){
 while(resultSet.next()) { 
    String systemTableName = resultSet.getString("TABLE_NAME"); 
 }
}
```

最后，要找出所有现有视图，我们只需将类型更改为“ VIEW ”。

## 4.列元数据

我们还可以使用相同的DatabaseMetaData对象提取特定表的列。让我们看看实际效果：

```java
try(ResultSet columns = databaseMetaData.getColumns(null,null, "CUSTOMER_ADDRESS", null)){
  while(columns.next()) {
    String columnName = columns.getString("COLUMN_NAME");
    String columnSize = columns.getString("COLUMN_SIZE");
    String datatype = columns.getString("DATA_TYPE");
    String isNullable = columns.getString("IS_NULLABLE");
    String isAutoIncrement = columns.getString("IS_AUTOINCREMENT");
  }
}
```

在这里，getColumns()调用返回一个ResultSet，我们可以迭代它以找到每一列的描述。每个描述都包含许多有用的列，例如COLUMN_NAME、COLUMN_SIZE和DATA_TYPE。

除了常规列，我们还可以找出特定表的主键列：

```java
try(ResultSet primaryKeys = databaseMetaData.getPrimaryKeys(null, null, "CUSTOMER_ADDRESS")){ 
 while(primaryKeys.next()){ 
    String primaryKeyColumnName = primaryKeys.getString("COLUMN_NAME"); 
    String primaryKeyName = primaryKeys.getString("PK_NAME"); 
 }
}
```

同样，我们可以检索外键列的描述以及给定表引用的主键列。让我们看一个例子：

```java
try(ResultSet foreignKeys = databaseMetaData.getImportedKeys(null, null, "CUSTOMER_ADDRESS")){
 while(foreignKeys.next()){
    String pkTableName = foreignKeys.getString("PKTABLE_NAME");
    String fkTableName = foreignKeys.getString("FKTABLE_NAME");
    String pkColumnName = foreignKeys.getString("PKCOLUMN_NAME");
    String fkColumnName = foreignKeys.getString("FKCOLUMN_NAME");
 }
}
```

在这里，CUSTOMER_ADDRESS表有一个外键列CUST_ID，它引用了CUSTOMER表的ID列。上面的代码片段将生成“CUSTOMER”作为主表，“CUSTOMER_ADDRESS”作为外部表。

在下一节中，我们将了解如何获取有关用户名和可用架构名称的信息。

## 5. 用户名和模式元数据

我们还可以获得在获取数据库连接时使用其凭据的用户的名称：

```java
String userName = databaseMetaData.getUserName();
```

同样，我们可以使用方法getSchemas()来检索数据库中可用模式的名称：

```java
try(ResultSet schemas = databaseMetaData.getSchemas()){
 while (schemas.next()){
    String table_schem = schemas.getString("TABLE_SCHEM");
    String table_catalog = schemas.getString("TABLE_CATALOG");
 }
}
```

在下一节中，我们将了解如何获取有关数据库的其他一些有用信息。

## 6. 数据库级元数据

现在，让我们看看如何使用相同的DatabaseMetaData对象获取数据库级别的信息。

例如，我们可以获取数据库产品的名称和版本、JDBC 驱动程序的名称、JDBC 驱动程序的版本号等。现在让我们看一下代码片段：

```java
String productName = databaseMetaData.getDatabaseProductName();
String productVersion = databaseMetaData.getDatabaseProductVersion();
String driverName = databaseMetaData.getDriverName();
String driverVersion = databaseMetaData.getDriverVersion();
```

了解此信息有时会很有用，尤其是当应用程序针对多个数据库产品和版本运行时。例如，某个版本或产品可能缺少特定功能或包含应用程序需要实施某种变通方法的错误。

接下来，我们将了解如何了解数据库是否缺少或支持特定功能。

## 7. 支持的数据库功能元数据

不同的数据库支持不同的特性。例如，H2 不支持完全外连接，而 MySQL 支持。

那么，我们怎样才能知道我们使用的数据库是否支持某个特性呢？让我们看一些例子：

```java
boolean supportsFullOuterJoins = databaseMetaData.supportsFullOuterJoins();
boolean supportsStoredProcedures = databaseMetaData.supportsStoredProcedures();
boolean supportsTransactions = databaseMetaData.supportsTransactions();
boolean supportsBatchUpdates = databaseMetaData.supportsBatchUpdates();
```

[此外，可以在官方Java文档](https://docs.oracle.com/en/java/javase/11/docs/api/java.sql/java/sql/DatabaseMetaData.html)中找到可以查询的完整功能列表。

## 八. 总结

在本文中，我们了解了如何使用DatabaseMetaData接口来检索元数据和数据库支持的功能。
## 1. 概述

[Java 数据库连接 (JDBC) API](https://www.baeldung.com/java-jdbc)提供从Java应用程序访问数据库的功能。只要支持的 JDBC 驱动程序可用，我们就可以使用 JDBC 连接到任何数据库。

ResultSet是通过执行数据库查询生成的数据表。在本教程中，我们将深入了解[ResultSet API](https://docs.oracle.com/en/java/javase/11/docs/api/java.sql/java/sql/ResultSet.html)。

## 2.生成ResultSet

 首先，我们通过在任何实现 Statement接口的对象上 调用 executeQuery()来检索ResultSet 。PreparedStatement和 CallableStatement都是Statement的子接口 ：

```java
PreparedStatement pstmt = dbConnection.prepareStatement("select  from employees");
ResultSet rs = pstmt.executeQuery();
```

ResultSet对象维护一个指向结果集当前行的游标。我们将在ResultSet上使用next()来遍历记录。

接下来，我们将 在遍历结果时使用getX()方法从数据库列中获取值，其中X是列的数据类型。事实上，我们将为getX()方法提供数据库列名：

```java
while(rs.next()) {
    String name = rs.getString("name");
    Integer empId = rs.getInt("emp_id");
    Double salary = rs.getDouble("salary");
    String position = rs.getString("position");
}

```

同样， 列的索引号可以与 getX()方法一起使用， 而不是列名。索引号是 SQL select 语句中列的顺序。

如果 select 语句没有列出列名，索引号就是表中列的序列。列索引编号从一开始：

```java
Integer empId = rs.getInt(1);
String name = rs.getString(2);
String position = rs.getString(3);
Double salary = rs.getDouble(4);

```

## 3. 从结果集中检索元数据

在本节中，我们将了解如何在ResultSet中检索有关列属性和类型的信息。

首先，让 我们在ResultSet上使用getMetaData()方法来获取 [ResultSetMetaData](https://docs.oracle.com/en/java/javase/11/docs/api/java.sql/java/sql/ResultSetMetaData.html)：

```java
ResultSetMetaData metaData = rs.getMetaData();
```

接下来，让我们获取 ResultSet中的列数：

```java
Integer columnCount = metaData.getColumnCount();
```

此外，我们可以在元数据对象上使用以下任何方法来检索每一列的属性：

-   getColumnName(int columnNumber) –获取列的名称
-   getColumnLabel(int columnNumber) –访问列的标签，在 SQL 查询中指定在AS之后
-   getTableName(int columnNumber) –获取该列所属的表名
-   getColumnClassName(int columnNumber) –获取列的Java数据类型
-   getColumnTypeName(int columnNumber) –获取数据库中列的数据类型
-   getColumnType(int columnNumber) –获取列的 SQL 数据类型
-   isAutoIncrement(int columnNumber) –表示列是否自增
-   isCaseSensitive(int columnNumber) –指定列大小写是否重要
-   isSearchable(int columnNumber) –建议我们是否可以在 SQL 查询的where子句中使用该列
-   isCurrency(int columnNumber) – 表示该列是否包含现金值
-   isNullable(int columnNumber) –如果列不能为 null 则返回0 ，如果列可以包含 null 值则返回1 ，如果列的可空性未知则返回2
-   isSigned(int columnNumber) –如果列中的值是有符号的，则返回true，否则返回false

让我们遍历列以获取它们的属性：

```java
for (int columnNumber = 1; columnNumber <= columnCount; columnNumber++) {
    String catalogName = metaData.getCatalogName(columnNumber);
    String className = metaData.getColumnClassName(columnNumber);
    String label = metaData.getColumnLabel(columnNumber);
    String name = metaData.getColumnName(columnNumber);
    String typeName = metaData.getColumnTypeName(columnNumber);
    int type = metaData.getColumnType(columnNumber);
    String tableName = metaData.getTableName(columnNumber);
    String schemaName = metaData.getSchemaName(columnNumber);
    boolean isAutoIncrement = metaData.isAutoIncrement(columnNumber);
    boolean isCaseSensitive = metaData.isCaseSensitive(columnNumber);
    boolean isCurrency = metaData.isCurrency(columnNumber);
    boolean isDefiniteWritable = metaData.isDefinitelyWritable(columnNumber);
    boolean isReadOnly = metaData.isReadOnly(columnNumber);
    boolean isSearchable = metaData.isSearchable(columnNumber);
    boolean isReadable = metaData.isReadOnly(columnNumber);
    boolean isSigned = metaData.isSigned(columnNumber);
    boolean isWritable = metaData.isWritable(columnNumber);
    int nullable = metaData.isNullable(columnNumber);
}
```

## 4. 浏览结果集

当我们得到一个 ResultSet时，游标的位置在第一行之前。此外，默认情况下，ResultSet仅向前移动。但是，我们可以为其他导航选项使用可滚动的结果集。

在本节中，我们将讨论各种导航选项。

### 4.1. 结果集 类型

ResultSet 类型指示我们将如何引导数据集：

-   TYPE_FORWARD_ONLY – 默认选项，其中光标从头移动到尾
-   TYPE_SCROLL_INSENSITIVE—— 我们的光标可以前后移动穿过数据集；如果在数据集中移动时底层数据发生了变化，它们将被忽略；数据集包含数据库查询返回结果时的数据
-   TYPE_SCROLL_SENSITIVE –类似于滚动不敏感类型，但是对于这种类型，数据集会立即反映对基础数据的任何更改

并非所有数据库都支持所有ResultSet类型。因此，让我们使用DatabaseMetaData对象上的supportsResultSetType来检查该类型是否受 支持：

```java
DatabaseMetaData dbmd = dbConnection.getMetaData();
boolean isSupported = dbmd.supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE);
```

### 4.2. 可滚动的结果集

要获得可滚动的 ResultSet，我们需要在准备Statement时传递一些额外的参数 。

例如，我们将通过使用TYPE_SCROLL_INSENSITIVE或 TYPE_SCROLL_SENSITIVE作为ResultSet类型来获得可滚动的ResultSet ：

```java
PreparedStatement pstmt = dbConnection.prepareStatement(
  "select  from employees",
  ResultSet.TYPE_SCROLL_INSENSITIVE,
  ResultSet.CONCUR_UPDATABLE); 
ResultSet rs = pstmt.executeQuery();

```

### 4.3. 导航选项

我们可以在可滚动的ResultSet上使用以下任何选项：

-   next() – 从当前位置前进到下一行
-   previous() – 遍历到上一行
-   first() – 导航到结果集的第一行
-   last() –跳转到最后一行
-   beforeFirst() –移动到开始； 调用此方法后在我们的ResultSet上调用next()返回我们ResultSet的第一行
-   afterLast()——跳到最后；执行此方法后在我们的 ResultSet 上调用previous() 返回我们ResultSet的最后一行
-   relative(int numOfRows) – 从当前位置前进或后退numOfRows
-   absolute(int rowNumber) –跳转 到指定的行号

让我们看一些例子：

```java
PreparedStatement pstmt = dbConnection.prepareStatement(
  "select  from employees",
  ResultSet.TYPE_SCROLL_SENSITIVE,
  ResultSet.CONCUR_UPDATABLE);
ResultSet rs = pstmt.executeQuery();

while (rs.next()) {
    // iterate through the results from first to last
}
rs.beforeFirst(); // jumps back to the starting point, before the first row
rs.afterLast(); // jumps to the end of resultset

rs.first(); // navigates to the first row
rs.last(); // goes to the last row

rs.absolute(2); //jumps to 2nd row

rs.relative(-1); // jumps to the previous row
rs.relative(2); // jumps forward two rows

while (rs.previous()) {
    // iterates from current row to the first row in backward direction
}

```

### 4.4. 结果集行数

让我们使用getRow()获取ResultSet 的当前行号。

首先，我们将导航到ResultSet的最后一行，然后使用getRow()获取记录数：

```java
rs.last();
int rowCount = rs.getRow();
```

## 5. 更新结果集中的数据

默认情况下，ResultSet是只读的。但是，我们可以使用可更新的ResultSet来插入、更新和删除行。

### 5.1. 结果集 并发

并发模式表明我们的ResultSet是否可以更新数据。

CONCUR_READ_ONLY选项是默认选项，如果我们不需要使用ResultSet更新数据，则应使用该选项。

但是，如果我们需要更新 ResultSet中的数据，则应使用CONCUR_UPDATABLE选项。

并非所有数据库都支持所有ResultSet类型的所有并发模式。因此，我们需要使用supportsResultSetConcurrency()方法检查我们想要的类型和并发模式是否受支持：

```java
DatabaseMetaData dbmd = dbConnection.getMetaData(); 
boolean isSupported = dbmd.supportsResultSetConcurrency(
  ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

```

### 5.2. 获取可更新的结果集

为了获得可更新的ResultSet，我们需要在准备 Statement时传递一个额外的参数。为此，让我们在创建语句时使用CONCUR_UPDATABLE作为第三个参数：

```html
PreparedStatement pstmt = dbConnection.prepareStatement(
  "select  from employees",
  ResultSet.TYPE_SCROLL_SENSITIVE,
  ResultSet.CONCUR_UPDATABLE);
ResultSet rs = pstmt.executeQuery();
```

### 5.3. 更新一行

在本节中，我们将使用在上一节中创建的可更新结果集更新一行。

我们可以通过调用updateX()方法来连续更新数据，将列名和值传递给更新。我们可以在updateX()方法中使用任何支持的数据类型代替X。

让我们更新double类型的“salary”列：

```java
rs.updateDouble("salary", 1100.0);
```

请注意，这只会更新ResultSet中的数据，但修改尚未保存回数据库。

最后，让我们调用 updateRow() 将更新保存到数据库中：

```java
rs.updateRow();

```

我们可以将列索引传递给updateX()方法，而不是列名。这类似于使用列索引通过getX()方法获取值。将列名或索引传递给updateX()方法会产生相同的结果：

```java
rs.updateDouble(4, 1100.0);
rs.updateRow();

```

### 5.4. 插入一行

现在，让我们使用可更新的ResultSet插入一个新行。

首先，我们将使用moveToInsertRow() 移动光标以插入新行：

```java
rs.moveToInsertRow();
```

接下来，我们必须调用updateX()方法将信息添加到行中。我们需要向数据库表中的所有列提供数据。如果我们不向每一列提供数据，则使用默认列值：

```java
rs.updateString("name", "Venkat"); 
rs.updateString("position", "DBA"); 
rs.updateDouble("salary", 925.0);
```

然后，让我们调用insertRow()向数据库中插入一个新行：

```java
rs.insertRow();
```

最后，让我们使用 moveToCurrentRow()。这将使光标位置回到我们开始使用moveToInsertRow()方法插入新行之前所在的行 ：

```java
rs.moveToCurrentRow();
```

### 5.5. 删除一行

在本节中，我们将使用可更新的ResultSet删除一行。

首先，我们将导航到要删除的行。然后，我们将调用deleteRow() 方法来删除当前行：

```java
rs.absolute(2);
rs.deleteRow();
```

## 6. 持握力

可保持性决定了我们的ResultSet在数据库事务结束时是打开还是关闭。

### 6.1. 持仓类型

 如果提交事务后不需要ResultSet ，则使用 CLOSE_CURSORS_AT_COMMIT 。

使用HOLD_CURSORS_OVER_COMMIT创建可持有的ResultSet。即使在提交数据库事务后，可持有的ResultSet也不会关闭。

并非所有数据库都支持所有可持有性类型。

因此，让我们使用 DatabaseMetaData对象上的 supportsResultSetHoldability()检查是否支持可持有性类型。然后，我们将使用getResultSetHoldability()获取数据库的默认可持有性：

```java
boolean isCloseCursorSupported
  = dbmd.supportsResultSetHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT);
boolean isOpenCursorSupported
  = dbmd.supportsResultSetHoldability(ResultSet.HOLD_CURSORS_OVER_COMMIT);
boolean defaultHoldability
  = dbmd.getResultSetHoldability();
```

### 6.2. 可持有的结果集

要创建可持有的ResultSet，我们需要在创建 Statement 时将可持有性类型指定为最后一个参数。该参数在并发模式之后指定。

请注意，如果我们使用 Microsoft SQL Server (MSSQL)，我们必须在数据库连接上设置可保持性，而不是在ResultSet上：

```java
dbConnection.setHoldability(ResultSet.HOLD_CURSORS_OVER_COMMIT);
```

让我们看看实际效果。首先，让我们创建一个Statement，将 holdability 设置为HOLD_CURSORS_OVER_COMMIT：

```java
Statement pstmt = dbConnection.createStatement(
  ResultSet.TYPE_SCROLL_SENSITIVE, 
  ResultSet.CONCUR_UPDATABLE, 
  ResultSet.HOLD_CURSORS_OVER_COMMIT)
```

现在，让我们在检索数据时更新一行。这类似于我们之前讨论的更新示例，不同之处在于我们将在将更新事务提交到数据库后继续遍历ResultSet 。这适用于 MySQL 和 MSSQL 数据库：

```java
dbConnection.setAutoCommit(false);
ResultSet rs = pstmt.executeQuery("select  from employees");
while (rs.next()) {
    if(rs.getString("name").equalsIgnoreCase("john")) {
        rs.updateString("name", "John Doe");
        rs.updateRow();
        dbConnection.commit();
    }                
}
rs.last();

```

值得注意的是，MySQL 仅支持HOLD_CURSORS_OVER_COMMIT。所以，即使我们使用CLOSE_CURSORS_AT_COMMIT，它也会被忽略。

MSSQL 数据库支持CLOSE_CURSORS_AT_COMMIT。这意味着当我们提交事务时， ResultSet将被关闭。因此，在提交事务后尝试访问ResultSet会导致“游标未打开错误”。因此，我们无法从ResultSet中检索更多记录。

## 7.获取大小

通常，在将数据加载到ResultSet时，数据库驱动程序决定从数据库中获取的行数。例如，在 MySQL 数据库中，ResultSet通常一次将所有记录加载到内存中。

然而，有时我们可能需要处理大量不适合我们的 JVM 内存的记录。在这种情况下，我们可以在Statement或ResultSet对象上使用 fetch size 属性来限制最初返回的记录数。

每当需要额外的结果时，ResultSet从数据库中获取另一批记录。使用获取大小属性，我们可以向数据库驱动程序提供有关每次数据库访问要获取的行数的建议。我们指定的提取大小将应用于后续的数据库行程。

如果我们没有为ResultSet指定获取大小，则使用Statement的获取大小。如果我们没有为Statement或ResultSet指定提取大小，则使用数据库默认值。

### 7.1. 在语句上使用 Fetch Size

现在，让我们看看Statement上的提取大小。我们将语句的提取大小设置为 10 条记录。如果我们的查询返回 100 条记录，那么就会有 10 次数据库往返，每次加载 10 条记录：

```java
PreparedStatement pstmt = dbConnection.prepareStatement(
  "select  from employees", 
  ResultSet.TYPE_SCROLL_SENSITIVE, 
  ResultSet.CONCUR_READ_ONLY);
pstmt.setFetchSize(10);

ResultSet rs = pstmt.executeQuery();

while (rs.next()) {
    // iterate through the resultset
}
```

### 7.2. 在ResultSet上使用 Fetch Size

现在，让我们使用ResultSet更改前面示例中的提取大小。

首先，我们将在Statement上使用获取大小。这允许我们的ResultSet在执行查询后最初加载 10 条记录。

然后，我们将修改ResultSet上的提取大小。这将覆盖我们之前在Statement中指定的获取大小。因此，所有后续行程将加载 20 条记录，直到加载完所有记录。

因此，只有 6 次数据库访问才能加载所有记录：

```java
PreparedStatement pstmt = dbConnection.prepareStatement(
  "select  from employees", 
  ResultSet.TYPE_SCROLL_SENSITIVE, 
  ResultSet.CONCUR_READ_ONLY);
pstmt.setFetchSize(10);

ResultSet rs = pstmt.executeQuery();
 
rs.setFetchSize(20); 

while (rs.next()) { 
    // iterate through the resultset 
}
```

最后，我们将了解如何在迭代结果时修改ResultSet的获取大小。

与前面的示例类似，我们将首先在Statement上将提取大小设置为 10 。因此，我们的前 3 次数据库旅行每次都会加载 10 条记录。

然后，我们将在读取第 30 条记录时将ResultSet上的提取大小修改为 20。因此，接下来的 4 次行程每次都会加载 20 条记录。

因此，我们需要 7 次数据库访问才能加载所有 100 条记录：

```java
PreparedStatement pstmt = dbConnection.prepareStatement(
  "select  from employees", 
  ResultSet.TYPE_SCROLL_SENSITIVE, 
  ResultSet.CONCUR_READ_ONLY);
pstmt.setFetchSize(10);

ResultSet rs = pstmt.executeQuery();

int rowCount = 0;

while (rs.next()) { 
    // iterate through the resultset 
    if (rowCount == 30) {
        rs.setFetchSize(20); 
    }
    rowCount++;
}
```

## 八. 总结

在本文中，我们了解了如何使用ResultSet API 从数据库中检索和更新数据。我们讨论的一些高级功能取决于我们使用的数据库。因此，我们需要在使用它们之前检查对这些功能的支持。
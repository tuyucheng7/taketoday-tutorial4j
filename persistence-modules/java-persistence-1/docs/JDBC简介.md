## 1. 概述

在本文中，我们将了解 JDBC(Java 数据库连接)，它是一种用于连接和执行数据库查询的 API。

只要提供适当的驱动程序，JDBC 就可以与任何数据库一起工作。

## 2. JDBC 驱动程序

JDBC 驱动程序是用于连接特定类型数据库的 JDBC API 实现。有几种类型的 JDBC 驱动程序：

-   类型 1 – 包含到另一个数据访问 API 的映射；JDBC-ODBC 驱动程序就是一个例子
-   类型 2 – 是使用目标数据库的客户端库的实现；也称为本机 API 驱动程序
-   类型 3——使用中间件将 JDBC 调用转换为特定于数据库的调用；也称为网络协议驱动程序
-   类型 4 – 通过将 JDBC 调用转换为特定于数据库的调用来直接连接到数据库；称为数据库协议驱动程序或瘦驱动程序，

最常用的类型是类型 4，因为它具有平台无关的优点。与其他类型相比，直接连接到数据库服务器可提供更好的性能。这种类型的驱动程序的缺点是它是特定于数据库的——因为每个数据库都有自己特定的协议。

## 3.连接到数据库

要连接到数据库，我们只需初始化驱动程序并打开数据库连接。

### 3.1. 注册驱动程序

对于我们的示例，我们将使用类型 4 数据库协议驱动程序。

由于我们使用的是 MySQL 数据库，因此我们需要[mysql-connector-java](https://search.maven.org/classic/#search|ga|1|a%3A"mysql-connector-java" AND g%3A"mysql")依赖项：

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>6.0.6</version>
</dependency>
```

接下来，让我们使用Class.forName()方法注册驱动程序，该方法动态加载驱动程序类：

```java
Class.forName("com.mysql.cj.jdbc.Driver");
```

在旧版本的 JDBC 中，在获得连接之前，我们首先必须通过调用Class.forName 方法来初始化 JDBC 驱动程序。[从 JDBC 4.0](https://www.baeldung.com/java-jdbc-loading-drivers)开始，在类路径中找到的所有驱动程序都会自动加载。因此，在现代环境中我们不需要这个 Class.forName 部分。

### 3.2. 创建连接

要打开连接，我们可以使用DriverManager类的getConnection()方法。此方法需要一个连接 URL字符串参数：

```java
try (Connection con = DriverManager
  .getConnection("jdbc:mysql://localhost:3306/myDb", "user1", "pass")) {
    // use con here
}
```

由于 Connection 是一个 [AutoCloseable](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/AutoCloseable.html) 资源，我们应该在 [try-with-resources](https://www.baeldung.com/java-try-with-resources) 块中使用它。

连接 URL 的语法取决于所使用的数据库类型。让我们看几个例子：

```plaintext
jdbc:mysql://localhost:3306/myDb?user=user1&password=pass
jdbc:postgresql://localhost/myDb
jdbc:hsqldb:mem:myDb
```

要连接到指定的myDb数据库，我们必须创建数据库和用户，并添加授予必要的访问权限：

```sql
CREATE DATABASE myDb;
CREATE USER 'user1' IDENTIFIED BY 'pass';
GRANT ALL on myDb. TO 'user1';
```

## 4.执行SQL语句

向数据库发送 SQL 指令，我们可以使用Statement、PreparedStatement或CallableStatement 类型的实例，我们可以使用Connection对象获取这些实例。

### 4.1. 陈述

Statement接口包含执行 SQL 命令的基本功能。

首先，让我们创建一个Statement对象：

```java
try (Statement stmt = con.createStatement()) {
    // use stmt here
}
```

同样，我们应该 在 try-with-resources 块中使用Statement以进行自动资源管理。

不管怎样，执行SQL指令可以通过三种方法来完成：

-   用于 SELECT 指令的executeQuery()
-   executeUpdate()用于更新数据或数据库结构
-   当结果未知时， execute()可用于上述两种情况

让我们使用execute()方法将学生表添加到我们的数据库中：

```java
String tableSql = "CREATE TABLE IF NOT EXISTS employees" 
  + "(emp_id int PRIMARY KEY AUTO_INCREMENT, name varchar(30),"
  + "position varchar(30), salary double)";
stmt.execute(tableSql);
```

当使用execute()方法更新数据时，stmt.getUpdateCount()方法返回受影响的行数。

如果结果为 0，那么要么没有行受到影响，要么是数据库结构更新命令。

如果值为 -1，则该命令是一个 SELECT 查询；然后我们可以使用stmt.getResultSet()获取结果。

接下来，让我们使用executeUpdate()方法向表中添加一条记录：

```java
String insertSql = "INSERT INTO employees(name, position, salary)"
  + " VALUES('john', 'developer', 2000)";
stmt.executeUpdate(insertSql);
```

该方法为更新行的命令返回受影响的行数，或为更新数据库结构的命令返回 0。

我们可以使用executeQuery()方法从表中检索记录，该方法返回一个ResultSet类型的对象：

```java
String selectSql = "SELECT  FROM employees"; 
try (ResultSet resultSet = stmt.executeQuery(selectSql)) {
    // use resultSet here
}
```

我们应该确保在使用后关闭ResultSet 实例。否则，我们可能会使底层游标保持打开状态的时间比预期的要长得多。为此，建议使用try-with-resources 块，如我们上面的示例所示。

### 4.2. PreparedStatement

PreparedStatement对象包含预编译的 SQL 序列。它们可以有一个或多个用问号表示的参数。

让我们创建一个PreparedStatement ，它根据给定的参数更新employees表中的记录：

```java
String updatePositionSql = "UPDATE employees SET position=? WHERE emp_id=?";
try (PreparedStatement pstmt = con.prepareStatement(updatePositionSql)) {
    // use pstmt here
}
```

要向PreparedStatement添加参数，我们可以使用简单的设置器——setX() ——其中 X 是参数的类型，方法参数是参数的顺序和值：

```java
pstmt.setString(1, "lead developer");
pstmt.setInt(2, 1);
```

该语句使用与之前描述的相同的三种方法之一执行：executeQuery()、executeUpdate()、execute()，不带 SQL String参数：

```java
int rowsAffected = pstmt.executeUpdate();
```

### 4.3. 可调用语句

CallableStatement接口允许调用存储过程。

要创建CallableStatement对象，我们可以使用Connection的prepareCall()方法：

```java
String preparedSql = "{call insertEmployee(?,?,?,?)}";
try (CallableStatement cstmt = con.prepareCall(preparedSql)) {
    // use cstmt here
}
```

为存储过程设置输入参数值就像在PreparedStatement接口中一样，使用setX()方法：

```java
cstmt.setString(2, "ana");
cstmt.setString(3, "tester");
cstmt.setDouble(4, 2000);
```

如果存储过程有输出参数，我们需要使用registerOutParameter()方法添加它们：

```java
cstmt.registerOutParameter(1, Types.INTEGER);
```

然后让我们执行语句并使用相应的getX()方法检索返回值：

```java
cstmt.execute();
int new_id = cstmt.getInt(1);
```

例如，为了工作，我们需要在 MySql 数据库中创建存储过程：

```sql
delimiter //
CREATE PROCEDURE insertEmployee(OUT emp_id int, 
  IN emp_name varchar(30), IN position varchar(30), IN salary double) 
BEGIN
INSERT INTO employees(name, position,salary) VALUES (emp_name,position,salary);
SET emp_id = LAST_INSERT_ID();
END //
delimiter ;
```

上面的insertEmployee过程将使用给定的参数将新记录插入employees表，并在emp_id输出参数中返回新记录的 ID。

为了能够从Java运行存储过程，连接用户需要能够访问存储过程的元数据。这可以通过向用户授予对所有数据库中所有存储过程的权限来实现：

```sql
GRANT ALL ON mysql.proc TO 'user1';
```

或者，我们可以打开连接并将属性noAccessToProcedureBodies设置为true：

```java
con = DriverManager.getConnection(
  "jdbc:mysql://localhost:3306/myDb?noAccessToProcedureBodies=true", 
  "user1", "pass");
```

这将通知 JDBC API 用户无权读取过程元数据，因此它将创建所有参数作为 INOUT String参数。

## 5. 解析查询结果

执行查询后，结果由ResultSet对象表示，该对象具有类似于表的结构，有行和列。

### 5.1. 结果集接口

ResultSet使用next()方法移动到下一行。

让我们首先创建一个Employee类来存储我们检索到的记录：

```java
public class Employee {
    private int id;
    private String name;
    private String position;
    private double salary;
 
    // standard constructor, getters, setters
}
```

接下来，让我们遍历ResultSet并为每条记录创建一个Employee对象：

```java
String selectSql = "SELECT  FROM employees"; 
try (ResultSet resultSet = stmt.executeQuery(selectSql)) {
    List<Employee> employees = new ArrayList<>(); 
    while (resultSet.next()) { 
        Employee emp = new Employee(); 
        emp.setId(resultSet.getInt("emp_id")); 
        emp.setName(resultSet.getString("name")); 
        emp.setPosition(resultSet.getString("position")); 
        emp.setSalary(resultSet.getDouble("salary")); 
        employees.add(emp); 
    }
}
```

可以使用类型为getX( ) 的方法来检索每个表格单元格的值，其中 X 表示单元格数据的类型。

getX()方法可以与表示单元格顺序的 int 参数或表示列名称的String参数一起使用。如果我们更改查询中列的顺序，则后一种选择更可取。

### 5.2. 可更新的结果集

隐含地，一个ResultSet对象只能向前遍历，不能被修改。

如果我们想使用ResultSet更新数据并双向遍历它，我们需要创建带有附加参数的Statement对象：

```java
stmt = con.createStatement(
  ResultSet.TYPE_SCROLL_INSENSITIVE, 
  ResultSet.CONCUR_UPDATABLE
);
```

要导航这种类型的ResultSet，我们可以使用以下方法之一：

-   first(), last(), beforeFirst(), beforeLast() – 移至结果集的第一行或最后一行或这些行之前的行
-   next(), previous() – 在ResultSet中向前和向后导航
-   getRow() –获取当前行号
-   moveToInsertRow(), moveToCurrentRow() – 移动到一个新的空行以插入并返回到当前行(如果在新行上)
-   absolute(int row) –移动到指定行
-   relative(int nrRows) – 将光标移动给定的行数

可以使用格式为 updateX()的方法更新ResultSet，其中 X 是单元格数据的类型。这些方法只更新ResultSet对象而不是数据库表。

要将ResultSet更改持久化到数据库，我们必须进一步使用其中一种方法：

-   updateRow() – 将对当前行的更改保存到数据库
-   insertRow(), deleteRow() – 添加新行或从数据库中删除当前行
-   refreshRow() –使用数据库中的任何更改刷新ResultSet
-   cancelRowUpdates() – 取消对当前行所做的更改

让我们看一个通过更新员工表中的数据来使用其中一些方法的示例：

```java
try (Statement updatableStmt = con.createStatement(
  ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
    try (ResultSet updatableResultSet = updatableStmt.executeQuery(selectSql)) {
        updatableResultSet.moveToInsertRow();
        updatableResultSet.updateString("name", "mark");
        updatableResultSet.updateString("position", "analyst");
        updatableResultSet.updateDouble("salary", 2000);
        updatableResultSet.insertRow();
    }
}
```

## 6.解析元数据

JDBC API 允许查找有关数据库的信息，称为元数据。

### 6.1. 数据库元数据

DatabaseMetadata接口可用于获取有关数据库的一般信息，例如表、存储过程或 SQL 方言。

让我们快速看一下如何检索数据库表中的信息：

```java
DatabaseMetaData dbmd = con.getMetaData();
ResultSet tablesResultSet = dbmd.getTables(null, null, "%", null);
while (tablesResultSet.next()) {
    LOG.info(tablesResultSet.getString("TABLE_NAME"));
}
```

### 6.2. 结果集元数据

该接口可用于查找有关某个ResultSet的信息，例如其列的数量和名称：

```java
ResultSetMetaData rsmd = rs.getMetaData();
int nrColumns = rsmd.getColumnCount();

IntStream.range(1, nrColumns).forEach(i -> {
    try {
        LOG.info(rsmd.getColumnName(i));
    } catch (SQLException e) {
        e.printStackTrace();
    }
});
```

## 七、处理交易

默认情况下，每个 SQL 语句在完成后立即提交。但是，也可以通过编程方式控制事务。

在我们想要保持数据一致性的情况下，这可能是必要的，例如，当我们只想在前一个事务成功完成时提交一个事务。

首先，我们需要将Connection的autoCommit属性设置为false，然后使用commit()和rollback()方法来控制事务。

让我们在员工职位列更新后为薪水列添加第二个更新语句，并将它们都包装在一个事务中。这样，只有在成功更新职位后才会更新薪水：

```java
String updatePositionSql = "UPDATE employees SET position=? WHERE emp_id=?";
PreparedStatement pstmt = con.prepareStatement(updatePositionSql);
pstmt.setString(1, "lead developer");
pstmt.setInt(2, 1);

String updateSalarySql = "UPDATE employees SET salary=? WHERE emp_id=?";
PreparedStatement pstmt2 = con.prepareStatement(updateSalarySql);
pstmt.setDouble(1, 3000);
pstmt.setInt(2, 1);

boolean autoCommit = con.getAutoCommit();
try {
    con.setAutoCommit(false);
    pstmt.executeUpdate();
    pstmt2.executeUpdate();
    con.commit();
} catch (SQLException exc) {
    con.rollback();
} finally {
    con.setAutoCommit(autoCommit);
}
```

为了简洁起见，我们在这里省略了 try-with-resources 块。

## 8.关闭资源

当我们不再使用它时，我们需要关闭连接来释放数据库资源。

我们可以使用close() API 来做到这一点：

```java
con.close();
```

但是，如果我们在try-with-resources块中使用资源，则不需要显式调用close() 方法，因为 try-with-resources 块会自动为我们执行此操作。

Statement s、 PreparedStatement s、 CallableStatement s 和 ResultSet s也是如此 。

## 9.总结

在本教程中，我们了解了使用 JDBC API 的基础知识。
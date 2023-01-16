## 1. 简介

Java 数据库连接 (JDBC) 是一种用于与数据库交互的JavaAPI。批处理将多个查询分组为一个单元，并通过一次网络传输将其传递到数据库。

在本文中，我们将了解如何将 JDBC 用于 SQL 查询的批处理。

有关 JDBC 的更多信息，你可以[在此处](https://www.baeldung.com/java-jdbc)查看我们的介绍文章。

## 2. 为什么要批处理？

性能和数据一致性是进行批处理的主要动机。

### 2.1. 改进的性能

一些用例需要将大量数据插入到数据库表中。在使用 JDBC 时，无需批处理即可实现此目的的方法之一是顺序执行多个查询。

让我们看一个发送到数据库的顺序查询的例子：

```java
statement.execute("INSERT INTO EMPLOYEE(ID, NAME, DESIGNATION) "
 + "VALUES ('1','EmployeeName1','Designation1')"); 
statement.execute("INSERT INTO EMPLOYEE(ID, NAME, DESIGNATION) "
 + "VALUES ('2','EmployeeName2','Designation2')");
```

这些顺序调用会增加数据库的网络访问次数，从而导致性能不佳。

通过使用批处理，可以在一次调用中将这些查询发送到数据库，从而提高性能。

### 2.2. 数据一致性

在某些情况下，需要将数据推送到多个表中。这会导致一个相互关联的事务，其中推送的查询顺序很重要。

执行期间发生的任何错误都应导致回滚先前查询推送的数据(如果有)。

让我们看一个向多个表添加数据的示例：

```java
statement.execute("INSERT INTO EMPLOYEE(ID, NAME, DESIGNATION) "
 + "VALUES ('1','EmployeeName1','Designation1')"); 
statement.execute("INSERT INTO EMP_ADDRESS(ID, EMP_ID, ADDRESS) "
 + "VALUES ('10','1','Address')");

```

当第一个语句成功而第二个语句失败时，会出现上述方法中的一个典型问题。这种情况下第一条语句插入的数据没有回滚，导致数据不一致。

我们可以通过跨越多个插入/更新的事务然后在最后提交事务或在出现异常时执行回滚来实现数据一致性，但在这种情况下，我们仍然会为每个语句重复访问数据库。

## 3.如何进行批处理

JDBC 提供了两个类，Statement和PreparedStatement来执行对数据库的查询。这两个类都有自己的addBatch()和executeBatch()方法实现，它们为我们提供了批处理功能。

### 3.1. 批处理使用语句

使用 JDBC，对数据库执行查询的最简单方法是通过Statement对象。

首先，使用addBatch()我们可以将所有 SQL 查询添加到一个批处理中，然后使用executeBatch()执行这些 SQL 查询。

executeBatch()的返回类型是一个int数组，表示每条 SQL 语句的执行影响了多少条记录。

让我们看一个使用 Statement 创建和执行批处理的示例：

```java
Statement statement = connection.createStatement();
statement.addBatch("INSERT INTO EMPLOYEE(ID, NAME, DESIGNATION) "
 + "VALUES ('1','EmployeeName','Designation')");
statement.addBatch("INSERT INTO EMP_ADDRESS(ID, EMP_ID, ADDRESS) "
 + "VALUES ('10','1','Address')");
statement.executeBatch();

```

在上面的示例中，我们尝试使用Statement将记录插入到EMPLOYEE和EMP_ADDRESS表中。我们可以看到如何将 SQL 查询添加到要执行的批处理中。

### 3.2. 使用PreparedStatement 进行批处理

PreparedStatement是另一个用于执行 SQL 查询的类。它允许重用 SQL 语句，并要求我们为每个更新/插入设置新参数。

让我们看一个使用PreparedStatement 的例子。首先，我们使用编码为字符串的 SQL 查询设置语句：

```java
String[] EMPLOYEES = new String[]{"Zuck","Mike","Larry","Musk","Steve"};
String[] DESIGNATIONS = new String[]{"CFO","CSO","CTO","CEO","CMO"};

String insertEmployeeSQL = "INSERT INTO EMPLOYEE(ID, NAME, DESIGNATION) "
 + "VALUES (?,?,?)";
PreparedStatement employeeStmt = connection.prepareStatement(insertEmployeeSQL);
```

接下来，我们遍历一个字符串值数组，并将新配置的查询添加到批处理中。

循环完成后，我们执行批处理：

```java
for(int i = 0; i < EMPLOYEES.length; i++){
    String employeeId = UUID.randomUUID().toString();
    employeeStmt.setString(1,employeeId);
    employeeStmt.setString(2,EMPLOYEES[i]);
    employeeStmt.setString(3,DESIGNATIONS[i]);
    employeeStmt.addBatch();
}
employeeStmt.executeBatch();

```

在上面显示的示例中，我们使用PreparedStatement将记录插入到EMPLOYEE表中。我们可以看到要插入的值是如何在查询中设置的，然后添加到要执行的批处理中。

## 4. 总结

在本文中，我们了解了在使用 JDBC 与数据库交互时 SQL 查询的批处理是多么重要。
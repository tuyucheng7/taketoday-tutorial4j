## 1. 概述

Apache Commons DbUtils 是一个小型库，它使使用 JDBC 变得更加容易。

在本文中，我们将实施示例来展示其特性和功能。

## 2.设置

### 2.1. Maven 依赖项

首先，我们需要将commons-dbutils和h2依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>commons-dbutils</groupId>
    <artifactId>commons-dbutils</artifactId>
    <version>1.7</version>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>1.4.196</version>
</dependency>
```

可以在 Maven Central 上找到最新版本的[commons-dbutils](https://search.maven.org/classic/#search|gav|1|g%3A"commons-dbutils" AND a%3A"commons-dbutils")和[h2 。](https://search.maven.org/classic/#search|gav|1|g%3A"com.h2database" AND a%3A"h2")

### 2.2. 测试数据库

有了我们的依赖关系，让我们创建一个脚本来创建我们将使用的表和记录：

```sql
CREATE TABLE employee(
    id int NOT NULL PRIMARY KEY auto_increment,
    firstname varchar(255),
    lastname varchar(255),
    salary double,
    hireddate date,
);

CREATE TABLE email(
    id int NOT NULL PRIMARY KEY auto_increment,
    employeeid int,
    address varchar(255)
);

INSERT INTO employee (firstname,lastname,salary,hireddate)
  VALUES ('John', 'Doe', 10000.10, to_date('01-01-2001','dd-mm-yyyy'));
// ...
INSERT INTO email (employeeid,address)
  VALUES (1, 'john@baeldung.com');
// ...
```

本文中的所有示例测试用例都将使用新创建的 H2 内存数据库连接：

```java
public class DbUtilsUnitTest {
    private Connection connection;

    @Before
    public void setupDB() throws Exception {
        Class.forName("org.h2.Driver");
        String db
          = "jdbc:h2:mem:;INIT=runscript from 'classpath:/employees.sql'";
        connection = DriverManager.getConnection(db);
    }

    @After
    public void closeBD() {
        DbUtils.closeQuietly(connection);
    }
    // ...
}
```

### 2.3. POJO

最后，我们需要两个简单的类：

```java
public class Employee {
    private Integer id;
    private String firstName;
    private String lastName;
    private Double salary;
    private Date hiredDate;

    // standard constructors, getters, and setters
}

public class Email {
    private Integer id;
    private Integer employeeId;
    private String address;

    // standard constructors, getters, and setters
}
```

## 3.简介

DbUtils 库提供QueryRunner类作为大多数可用功能的主要入口点。

此类通过接收与数据库的连接、要执行的 SQL 语句以及为查询的占位符提供值的可选参数列表来工作。

正如我们稍后将看到的，一些方法还接收一个ResultSetHandler实现——它负责将ResultSet实例转换为我们的应用程序期望的对象。

当然，该库已经提供了几个处理最常见转换的实现，例如列表、映射和 JavaBeans。

## 4.查询数据

现在我们了解了基础知识，我们已经准备好查询我们的数据库了。

让我们从一个使用MapListHandler将数据库中的所有记录作为地图列表获取的快速示例开始：

```java
@Test
public void givenResultHandler_whenExecutingQuery_thenExpectedList()
  throws SQLException {
    MapListHandler beanListHandler = new MapListHandler();

    QueryRunner runner = new QueryRunner();
    List<Map<String, Object>> list
      = runner.query(connection, "SELECT  FROM employee", beanListHandler);

    assertEquals(list.size(), 5);
    assertEquals(list.get(0).get("firstname"), "John");
    assertEquals(list.get(4).get("firstname"), "Christian");
}
```

接下来，这是一个使用BeanListHandler将结果转换为Employee实例的示例：

```java
@Test
public void givenResultHandler_whenExecutingQuery_thenEmployeeList()
  throws SQLException {
    BeanListHandler<Employee> beanListHandler
      = new BeanListHandler<>(Employee.class);

    QueryRunner runner = new QueryRunner();
    List<Employee> employeeList
      = runner.query(connection, "SELECT  FROM employee", beanListHandler);

    assertEquals(employeeList.size(), 5);
    assertEquals(employeeList.get(0).getFirstName(), "John");
    assertEquals(employeeList.get(4).getFirstName(), "Christian");
}
```

对于返回单个值的查询，我们可以使用ScalarHandler：

```java
@Test
public void givenResultHandler_whenExecutingQuery_thenExpectedScalar()
  throws SQLException {
    ScalarHandler<Long> scalarHandler = new ScalarHandler<>();

    QueryRunner runner = new QueryRunner();
    String query = "SELECT COUNT() FROM employee";
    long count
      = runner.query(connection, query, scalarHandler);

    assertEquals(count, 5);
}
```

要了解所有ResultSerHandler实现，可以参考[ResultSetHandler文档](https://commons.apache.org/proper/commons-dbutils/apidocs/org/apache/commons/dbutils/ResultSetHandler.html)。

### 4.1. 自定义处理程序

当我们需要更多地控制如何将结果转换为对象时，我们还可以创建自定义处理程序以传递给QueryRunner的方法。

这可以通过实现ResultSetHandler接口或扩展库提供的现有实现之一来完成。

让我们看看第二种方法是怎样的。首先，让我们在Employee类中添加另一个字段：

```java
public class Employee {
    private List<Email> emails;
    // ...
}
```

现在，让我们创建一个类来扩展BeanListHandler类型并为每个员工设置电子邮件列表：

```java
public class EmployeeHandler extends BeanListHandler<Employee> {

    private Connection connection;

    public EmployeeHandler(Connection con) {
        super(Employee.class);
        this.connection = con;
    }

    @Override
    public List<Employee> handle(ResultSet rs) throws SQLException {
        List<Employee> employees = super.handle(rs);

        QueryRunner runner = new QueryRunner();
        BeanListHandler<Email> handler = new BeanListHandler<>(Email.class);
        String query = "SELECT  FROM email WHERE employeeid = ?";

        for (Employee employee : employees) {
            List<Email> emails
              = runner.query(connection, query, handler, employee.getId());
            employee.setEmails(emails);
        }
        return employees;
    }
}
```

请注意，我们期望构造函数中有一个Connection对象，以便我们可以执行查询以获取电子邮件。

最后，让我们测试一下我们的代码，看看是否一切都按预期工作：

```java
@Test
public void
  givenResultHandler_whenExecutingQuery_thenEmailsSetted()
    throws SQLException {
    EmployeeHandler employeeHandler = new EmployeeHandler(connection);

    QueryRunner runner = new QueryRunner();
    List<Employee> employees
      = runner.query(connection, "SELECT  FROM employee", employeeHandler);

    assertEquals(employees.get(0).getEmails().size(), 2);
    assertEquals(employees.get(2).getEmails().size(), 3);
}
```

### 4.2. 自定义行处理器

在我们的示例中，employee表的列名与我们的Employee类的字段名匹配(匹配不区分大小写)。然而，情况并非总是如此——例如，当列名使用下划线分隔复合词时。

在这些情况下，我们可以利用RowProcessor接口及其实现将列名映射到类中的适当字段。

让我们看看这是什么样子的。首先，让我们创建另一个表并向其中插入一些记录：

```sql
CREATE TABLE employee_legacy (
    id int NOT NULL PRIMARY KEY auto_increment,
    first_name varchar(255),
    last_name varchar(255),
    salary double,
    hired_date date,
);

INSERT INTO employee_legacy (first_name,last_name,salary,hired_date)
  VALUES ('John', 'Doe', 10000.10, to_date('01-01-2001','dd-mm-yyyy'));
// ...
```

现在，让我们修改我们的EmployeeHandler类：

```java
public class EmployeeHandler extends BeanListHandler<Employee> {
    // ...
    public EmployeeHandler(Connection con) {
        super(Employee.class,
          new BasicRowProcessor(new BeanProcessor(getColumnsToFieldsMap())));
        // ...
    }
    public static Map<String, String> getColumnsToFieldsMap() {
        Map<String, String> columnsToFieldsMap = new HashMap<>();
        columnsToFieldsMap.put("FIRST_NAME", "firstName");
        columnsToFieldsMap.put("LAST_NAME", "lastName");
        columnsToFieldsMap.put("HIRED_DATE", "hiredDate");
        return columnsToFieldsMap;
    }
    // ...
}
```

请注意，我们正在使用BeanProcessor将列实际映射到字段，并且仅用于那些需要处理的字段。

最后，让我们测试一切正常：

```java
@Test
public void
  givenResultHandler_whenExecutingQuery_thenAllPropertiesSetted()
    throws SQLException {
    EmployeeHandler employeeHandler = new EmployeeHandler(connection);

    QueryRunner runner = new QueryRunner();
    String query = "SELECT  FROM employee_legacy";
    List<Employee> employees
      = runner.query(connection, query, employeeHandler);

    assertEquals((int) employees.get(0).getId(), 1);
    assertEquals(employees.get(0).getFirstName(), "John");
}
```

## 5.插入记录

QueryRunner类提供了两种在数据库中创建记录的方法。

第一个是使用update()方法并传递 SQL 语句和可选的替换参数列表。该方法返回插入的记录数：

```java
@Test
public void whenInserting_thenInserted() throws SQLException {
    QueryRunner runner = new QueryRunner();
    String insertSQL
      = "INSERT INTO employee (firstname,lastname,salary, hireddate) "
        + "VALUES (?, ?, ?, ?)";

    int numRowsInserted
      = runner.update(
        connection, insertSQL, "Leia", "Kane", 60000.60, new Date());

    assertEquals(numRowsInserted, 1);
}
```

第二种是使用insert()方法，除了 SQL 语句和替换参数外，还需要一个ResultSetHandler来转换结果自动生成的键。返回值将是处理程序返回的内容：

```java
@Test
public void
  givenHandler_whenInserting_thenExpectedId() throws SQLException {
    ScalarHandler<Integer> scalarHandler = new ScalarHandler<>();

    QueryRunner runner = new QueryRunner();
    String insertSQL
      = "INSERT INTO employee (firstname,lastname,salary, hireddate) "
        + "VALUES (?, ?, ?, ?)";

    int newId
      = runner.insert(
        connection, insertSQL, scalarHandler,
        "Jenny", "Medici", 60000.60, new Date());

    assertEquals(newId, 6);
}
```

## 6.更新和删除

QueryRunner类的update()方法也可用于修改和删除数据库中的记录。

它的用法很简单。以下是如何更新员工工资的示例：

```java
@Test
public void givenSalary_whenUpdating_thenUpdated()
 throws SQLException {
    double salary = 35000;

    QueryRunner runner = new QueryRunner();
    String updateSQL
      = "UPDATE employee SET salary = salary  1.1 WHERE salary <= ?";
    int numRowsUpdated = runner.update(connection, updateSQL, salary);

    assertEquals(numRowsUpdated, 3);
}
```

这是另一个删除具有给定 ID 的员工的方法：

```java
@Test
public void whenDeletingRecord_thenDeleted() throws SQLException {
    QueryRunner runner = new QueryRunner();
    String deleteSQL = "DELETE FROM employee WHERE id = ?";
    int numRowsDeleted = runner.update(connection, deleteSQL, 3);

    assertEquals(numRowsDeleted, 1);
}
```

## 7.异步操作

DbUtils 提供了AsyncQueryRunner类来异步执行操作。该类的方法与QueryRunner类的方法一一对应，只是返回的是Future实例。

下面是获取数据库中所有员工的示例，最多等待 10 秒才能得到结果：

```java
@Test
public void
  givenAsyncRunner_whenExecutingQuery_thenExpectedList() throws Exception {
    AsyncQueryRunner runner
      = new AsyncQueryRunner(Executors.newCachedThreadPool());

    EmployeeHandler employeeHandler = new EmployeeHandler(connection);
    String query = "SELECT  FROM employee";
    Future<List<Employee>> future
      = runner.query(connection, query, employeeHandler);
    List<Employee> employeeList = future.get(10, TimeUnit.SECONDS);

    assertEquals(employeeList.size(), 5);
}
```

## 八. 总结

在本教程中，我们探索了 Apache Commons DbUtils 库最显着的特性。

我们查询数据并将其转换为不同的对象类型，插入记录获取生成的主键，并根据给定的标准更新和删除数据。我们还利用AsyncQueryRunner类异步执行查询操作。
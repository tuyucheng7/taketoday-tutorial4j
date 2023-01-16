## 1. 简介

[使用JDBC](https://www.baeldung.com/java-jdbc) API创建的数据库[连接](https://docs.oracle.com/en/java/javase/11/docs/api/java.sql/java/sql/Connection.html)具有称为[自动提交模式](https://www.ibm.com/docs/en/i/7.4?topic=transactions-jdbc-auto-commit-mode)的功能。

打开此模式有助于消除管理事务所需的样板代码。然而，尽管如此，它的目的以及它在执行 SQL[语句](https://docs.oracle.com/en/java/javase/11/docs/api/java.sql/java/sql/Statement.html)时如何影响事务处理有时仍不清楚。

在本文中，我们将讨论什么是自动提交模式以及如何将其正确地用于自动和显式事务管理。我们还将介绍在自动提交打开或关闭时要避免的各种问题。

## 2、什么是JDBC自动提交模式？

开发人员在使用 JDBC 时不一定了解如何有效地管理数据库事务。因此，如果手动处理事务，开发人员可能不会在适当的地方启动它们或根本不会启动它们。同样的问题适用于在必要时发布[提交](https://docs.oracle.com/en/java/javase/11/docs/api/java.sql/java/sql/Connection.html#commit())或[回滚。](https://docs.oracle.com/en/java/javase/11/docs/api/java.sql/java/sql/Connection.html#rollback())

为了解决这个问题，JDBC 中的自动提交模式提供了一种通过[JDBC 驱动程序](https://www.baeldung.com/java-jdbc-loading-drivers)自动处理事务管理来执行 SQL 语句的方法。

因此，自动提交模式的目的是减轻开发人员必须自己管理事务的负担。这样，开启它可以更方便地使用 JDBC API 开发应用程序。当然，这仅在允许在每个 SQL 语句完成后立即保留数据更新的情况下有所帮助。

## 3. 自动提交为真时的自动事务管理

JDBC 驱动程序默认为新的数据库连接打开自动提交模式。当它打开时，它们会自动在其自己的事务中运行每个单独的 SQL 语句。

除了使用此默认设置外，我们还可以通过将true传递给连接的[setAutoCommit](https://docs.oracle.com/en/java/javase/11/docs/api/java.sql/java/sql/Connection.html#setAutoCommit(boolean))方法来手动打开自动提交：

```java
connection.setAutoCommit(true);
```

这种我们自己打开它的方式在我们之前关闭它时很有用，但后来我们需要恢复自动事务管理。

既然我们已经介绍了如何确保自动提交处于启用状态，我们将演示当这样设置时，JDBC 驱动程序会在它们自己的事务中运行 SQL 语句。也就是说，它们会立即自动将每个语句的任何数据更新提交给数据库。

### 3.1. 设置示例代码

在这个例子中，我们将使用[H2 内存数据库](https://www.h2database.com/)来存储我们的数据。要使用它，我们首先需要定义 Maven[依赖](https://search.maven.org/classic/#search|ga|1|g%3A"com.h2database" AND a%3A"h2")项：

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>1.4.200</version>
</dependency>
```

首先，让我们创建一个数据库表来保存有关人员的详细信息：

```sql
CREATE TABLE Person (
    id INTEGER not null,
    name VARCHAR(50),
    lastName VARCHAR(50),
    age INTEGER,PRIMARY KEY (id)
)
```

接下来，我们将创建两个到数据库的连接。我们将使用第一个在表上运行 SQL 查询和更新。我们将使用第二个连接来测试是否对该表进行了更新：

```java
Connection connection1 = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
Connection connection2 = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
```

请注意，我们需要使用单独的连接来测试提交的数据。这是因为如果我们在第一个连接上运行任何选择查询，那么它们将看到尚未提交的更新。

现在我们将创建一个 POJO 来表示保存有关人员信息的数据库记录：

```java
public class Person {

    private Integer id;
    private String name;
    private String lastName;
    private Integer age;

    // standard constructor, getters, and setters
}
```

要在我们的表中插入一条记录，让我们创建一个名为insertPerson的方法：

```java
private static int insertPerson(Connection connection, Person person) throws SQLException {    
    try (PreparedStatement preparedStatement = connection.prepareStatement(
      "INSERT INTO Person VALUES (?,?,?,?)")) {
        
        preparedStatement.setInt(1, person.getId());
        preparedStatement.setString(2, person.getName());
        preparedStatement.setString(3, person.getLastName());
        preparedStatement.setInt(4, person.getAge());
        
        return preparedStatement.executeUpdate();
    }        
}     

```

然后我们将添加一个updatePersonAgeById方法来更新表中的特定记录：

```java
private static void updatePersonAgeById(Connection connection, int id, int newAge) throws SQLException {
    try (PreparedStatement preparedStatement = connection.prepareStatement(
      "UPDATE Person SET age = ? WHERE id = ?")) {
        preparedStatement.setInt(1, newAge);
        preparedStatement.setInt(2, id);
        
        preparedStatement.executeUpdate();
    }
}

```

最后，让我们添加一个selectAllPeople方法来从表中选择所有记录。我们将使用它来检查 SQL插入 和更新语句的结果：

```java
private static List selectAllPeople(Connection connection) throws SQLException {
    
    List people = null;
    
    try (Statement statement = connection.createStatement()) {
        people = new ArrayList();
        ResultSet resultSet = statement.executeQuery("SELECT  FROM Person");

        while (resultSet.next()) {
            Person person = new Person();
            person.setId(resultSet.getInt("id"));
            person.setName(resultSet.getString("name"));
            person.setLastName(resultSet.getString("lastName"));
            person.setAge(resultSet.getInt("age"));
            
            people.add(person);
        }
    }
    
    return people;
}

```

现在有了这些实用方法，我们将测试打开自动提交的效果。

### 3.2. 运行测试

为了测试我们的示例代码，让我们首先将一个人插入表中。然后，从不同的连接，我们将检查数据库是否已更新，而无需我们发出提交：

```java
Person person = new Person(1, "John", "Doe", 45);
insertPerson(connection1, person);

List people = selectAllPeople(connection2);
assertThat("person record inserted OK into empty table", people.size(), is(equalTo(1)));
Person personInserted = people.iterator().next();
assertThat("id correct", personInserted.getId(), is(equalTo(1)));

```

然后，将这条新记录插入表中，让我们更新此人的年龄。此后，我们将从第二个连接检查更改是否已保存到数据库，而无需调用commit：

```java
updatePersonAgeById(connection1, 1, 65);

people = selectAllPeople(connection2);
Person personUpdated = people.iterator().next();
assertThat("updated age correct", personUpdated.getAge(), is(equalTo(65)));

```

因此，我们已经通过测试验证了当自动提交模式打开时，JDBC 驱动程序隐式地运行它自己的事务中的每个 SQL 语句。因此，我们不需要自己调用commit来持久化对数据库的更新。

## 4. 自动提交为假时的显式事务管理

当我们想自己处理事务，将多条SQL语句归为一个事务时，就需要关闭自动提交模式。

我们通过将false 传递给连接的setAutoCommit方法来做到这一点：

```java
connection.setAutoCommit(false);
```

当自动提交模式关闭时，我们需要通过在连接上调用提交或回滚来手动标记每个事务的结束。

但是需要注意的是，即使关闭了自动提交，JDBC 驱动程序仍然会在需要时自动为我们启动一个事务。例如，这发生在我们运行第一个 SQL 语句之前，也发生在每次提交或回滚之后。

让我们演示一下，当我们在关闭自动提交的情况下执行多个 SQL 语句时，结果更新只会在我们调用commit时保存到数据库中。

### 4.1. 运行测试

首先，让我们使用第一个连接插入人员记录。然后在不调用commit的情况下，我们将断言我们无法从其他连接看到数据库中插入的记录：

```java
Person person = new Person(1, "John", "Doe", 45);
insertPerson(connection1, person);

List<Person> people = selectAllPeople(connection2);
assertThat("No people have been inserted into database yet", people.size(), is(equalTo(0)));

```

接下来，我们将更新该人在该记录上的年龄。和以前一样，我们将在不调用 commit 的情况下断言，我们仍然无法使用第二个连接从数据库中选择记录：

```java
updatePersonAgeById(connection1, 1, 65);

people = selectAllPeople(connection2);
assertThat("No people have been inserted into database yet", people.size(), is(equalTo(0)));

```

为了完成我们的测试，我们将调用commit然后断言我们现在可以使用第二个连接查看数据库中的所有更新：

```java
connection1.commit();

people = selectAllPeople(connection2);
Person personUpdated = people.iterator().next();
assertThat("person's age updated to 65", personUpdated.getAge(), is(equalTo(65)));

```

正如我们在上面的测试中验证的那样，当自动提交模式关闭时，我们需要手动调用提交以将我们的更改持久保存到数据库中。这样做将保存自当前事务开始以来我们执行的所有 SQL 语句的任何更新。如果这是我们的第一个事务，那是因为我们打开了连接，或者是在我们最后一次提交或回滚之后。

## 5.注意事项和潜在问题

对于比较琐碎的应用程序，我们可以方便地运行开启自动提交的 SQL 语句。也就是说，不需要手动事务控制。然而，在更复杂的情况下，我们应该考虑到当 JDBC 驱动程序自动处理事务时，这有时可能会导致不需要的副作用或问题。

我们需要考虑的一件事是，当自动提交开启时，它可能会浪费大量的处理时间和资源。这是因为它会导致驱动程序在其自己的事务中运行每个 SQL 语句，无论是否需要。

例如，如果我们使用不同的值多次执行同一条语句，则每次调用都包含在它自己的事务中。因此，这会导致不必要的执行和资源管理开销。

因此在这种情况下，我们通常最好关闭自动提交并显式地将对同一 SQL 语句的多次调用分批处理到一个事务中。这样做，我们很可能会看到应用程序性能的显着提高。

另外需要注意的是，我们不建议在打开的事务期间重新打开自动提交。这是因为在事务中打开自动提交模式会将所有待处理的更新提交到数据库，无论当前事务是否完成。因此，我们应该避免这样做，因为它可能会导致数据不一致。

## 六. 总结

在本文中，我们讨论了 JDBC API 中自动提交模式的用途。我们还介绍了如何通过分别打开或关闭来启用隐式和显式事务管理。最后，我们谈到了使用它时要考虑的各种问题。
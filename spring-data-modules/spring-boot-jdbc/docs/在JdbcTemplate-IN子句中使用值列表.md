## 1. 简介

在 SQL 语句中，我们可以使用 IN 运算符来测试表达式是否匹配列表中的任何值。因此，我们可以使用 IN 运算符而不是多个 OR 条件。

在本教程中，我们将学习如何将值列表传递到[Spring JDBC 模板](https://www.baeldung.com/spring-jdbc-jdbctemplate) 查询的 IN 子句中。

## 2. 将列表参数传递给IN子句

IN 运算符允许我们在 WHERE 子句中指定多个值。例如，我们可以使用它来查找 id 在指定 id 列表中的所有员工：

```sql
SELECT  FROM EMPLOYEE WHERE id IN (1, 2, 3)
```

通常，IN 子句中值的总数是可变的。因此，我们需要创建一个可以支持动态值列表的占位符。

### 2.1. 使用JdbcTemplate

使用[JdbcTemplate](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/core/JdbcTemplate.html)，我们可以使用 '?' 字符作为值列表的占位符。的数量 '？' 字符将与列表的大小相同：

```java
List<Employee> getEmployeesFromIdList(List<Integer> ids) {
    String inSql = String.join(",", Collections.nCopies(ids.size(), "?"));
 
    List<Employee> employees = jdbcTemplate.query(
      String.format("SELECT  FROM EMPLOYEE WHERE id IN (%s)", inSql), 
      ids.toArray(), 
      (rs, rowNum) -> new Employee(rs.getInt("id"), rs.getString("first_name"), 
        rs.getString("last_name")));

    return employees;
}

```

在这个方法中，我们首先生成一个占位符字符串，其中包含ids.size() ['?' 以逗号分隔的字符](https://www.baeldung.com/java-strings-concatenation)。然后我们将这个字符串放入 SQL 语句的 IN 子句中。例如，如果我们在ids列表中有三个数字，则 SQL 语句为：

```sql
SELECT  FROM EMPLOYEE WHERE id IN (?,?,?)
```

在查询方法中，我们将ids列表作为参数传递，以匹配 IN 子句中的占位符。这样，我们就可以根据输入的值列表执行动态 SQL 语句。

### 2.2. 带命名参数的JdbcTemplate

处理动态值列表的另一种方法是使用[NamedParameterJdbcTemplate](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/jdbc/core/namedparam/NamedParameterJdbcTemplate.html)。例如，我们可以直接为输入列表创建一个命名参数：

```java
List<Employee> getEmployeesFromIdListNamed(List<Integer> ids) {
    SqlParameterSource parameters = new MapSqlParameterSource("ids", ids);
 
    List<Employee> employees = namedJdbcTemplate.query(
      "SELECT  FROM EMPLOYEE WHERE id IN (:ids)", 
      parameters, 
      (rs, rowNum) -> new Employee(rs.getInt("id"), rs.getString("first_name"),
        rs.getString("last_name")));

    return employees;
}
```

在此方法中，我们首先构造一个包含输入 id 列表的[MapSqlParameterSource对象。](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/jdbc/core/namedparam/MapSqlParameterSource.html)然后我们只使用一个命名参数来表示值的动态列表。

在幕后，NamedParameterJdbcTemplate将命名参数替换为“？” 占位符，并使用JdbcTemplate来执行查询。

## 3.处理大列表

当列表中有大量值时，我们应该考虑将它们传递到JdbcTemplate查询的替代方法。

例如，Oracle 数据库不支持 IN 子句中超过 1,000 个文字。

一种方法是为列表创建一个临时表。但是，不同的数据库可以有不同的方法来创建临时表。例如，我们可以使用[CREATE GLOBAL TEMPORARY TABLE](https://docs.oracle.com/cd/B28359_01/server.111/b28310/tables003.htm#ADMIN11633)语句在 Oracle 数据库中创建一个临时表。

让我们为 H2 数据库创建一个临时表：

```java
List<Employee> getEmployeesFromLargeIdList(List<Integer> ids) {
    jdbcTemplate.execute("CREATE TEMPORARY TABLE IF NOT EXISTS employee_tmp (id INT NOT NULL)");

    List<Object[]> employeeIds = new ArrayList<>();
    for (Integer id : ids) {
        employeeIds.add(new Object[] { id });
    }
    jdbcTemplate.batchUpdate("INSERT INTO employee_tmp VALUES(?)", employeeIds);

    List<Employee> employees = jdbcTemplate.query(
      "SELECT  FROM EMPLOYEE WHERE id IN (SELECT id FROM employee_tmp)", 
      (rs, rowNum) -> new Employee(rs.getInt("id"), rs.getString("first_name"),
      rs.getString("last_name")));

    jdbcTemplate.update("DELETE FROM employee_tmp");
 
    return employees;
}
```

在这里，我们首先创建一个临时表来保存输入列表的所有值。然后我们将输入列表的值插入表中。

在我们生成的 SQL 语句中，IN 子句中的值来自临时表，我们避免构造带有大量占位符的 IN 子句。

最后，当我们完成查询后，我们可以清理临时表以备将来使用。

## 4. 总结

在本文中，我们演示了如何使用JdbcTemplate和NamedParameterJdbcTemplate为 SQL 查询的 IN 子句传递值列表。我们还提供了另一种方法来使用临时表来处理大量列表值。
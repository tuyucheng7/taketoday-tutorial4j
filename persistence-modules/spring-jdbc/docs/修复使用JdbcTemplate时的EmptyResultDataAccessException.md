## 一、概述

在这个简短的教程中，我们将查看[Spring *JdbcTemplate*](https://www.baeldung.com/spring-jdbc-jdbctemplate) “ *EmptyResultDataAccessException：结果大小不正确：预期为 1，实际为 0* ”异常。

首先，我们将详细讨论此异常的根本原因。然后，我们将看到如何使用实际示例重现它，最后学习如何修复它。

## 2. 原因

Spring *JdbcTemplate*类提供了执行 SQL 查询和检索结果的便捷方式。它在底层使用[JDBC API 。](https://www.baeldung.com/java-jdbc)

通常，**当预期结果至少有一行但实际返回零行时，*****JdbcTemplate\*会抛出\*EmptyResultDataAccessException\***。

现在我们知道了异常的含义，让我们看看如何使用实际示例重现和修复它。

## 3.重现异常

例如，让我们考虑*Employee*类：

```java
public class Employee {

    private int id;
    private String firstName;
    private String lastName;

    public Employee(int id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // standard getters and setters

}复制
```

接下来，让我们创建一个使用*JdbcTemplate*处理 SQL 查询的[数据访问对象(DAO) 类：](https://www.baeldung.com/java-dao-pattern)

```java
public class EmployeeDAO {
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
	
}复制
```

值得注意的是，由于*JdbcTemplate*需要一个*DataSource*对象，所以我们将其注入到 setter 方法中。

现在，我们将添加一个通过*id*检索*Employee*对象的方法。为此，让我们使用*JdbcTemplate*类提供的*queryForObject()*方法：

```java
public Employee getEmployeeById(int id) {
    RowMapper<Employee> employeeRowMapper = (rs, rowNum) -> new Employee(rs.getInt("ID"), rs.getString("FIRST_NAME"), rs.getString("LAST_NAME"));

    return jdbcTemplate.queryForObject("SELECT * FROM EMPLOYEE WHERE id=?", employeeRowMapper, id);
}复制
```

如我们所见，**第一个参数表示 SQL 查询。第二个参数表示用于将\*ResultSet\*映射到\*Employee\*对象的[\*RowMapper\*](https://www.baeldung.com/spring-jdbc-jdbctemplate#3-mapping-query-results-to-java-object)**。

事实上，*queryForObject()*期望只返回一行。因此，如果我们传递一个不存在的*id ，它只会抛出**EmptyResultDataAccessException。*

让我们使用测试来确认这一点：

```java
@Test(expected = EmptyResultDataAccessException.class)
public void whenIdNotExist_thenThrowEmptyResultDataAccessException() {
    EmployeeDAO employeeDAO = new EmployeeDAO();
    ReflectionTestUtils.setField(employeeDAO, "jdbcTemplate", jdbcTemplate);
    Mockito.when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<RowMapper<Employee>> any(), anyInt()))
        .thenThrow(EmptyResultDataAccessException.class);

    employeeDAO.getEmployeeById(1);
}复制
```

**由于没有员工具有给定的\*id\*，因此指定的查询返回零行。结果，\*JdbcTemplate\*失败并出现\*EmptyResultDataAccessException\***。

## 4.修复异常

最简单的解决方案是捕获异常然后返回*null*：

```java
public Employee getEmployeeByIdV2(int id) {
    RowMapper<Employee> employeeRowMapper = (rs, rowNum) -> new Employee(rs.getInt("ID"), rs.getString("FIRST_NAME"), rs.getString("LAST_NAME"));

    try {
        return jdbcTemplate.queryForObject("SELECT * FROM EMPLOYEE WHERE id=?", employeeRowMapper, id);
    } catch (EmptyResultDataAccessException e) {
        return null;
    }
}复制
```

**这样，我们确保在 SQL 查询的结果为空时返回\*null\***。

现在，让我们添加另一个测试用例来确认一切都按预期工作：

```java
@Test
public void whenIdNotExist_thenReturnNull() {
    EmployeeDAO employeeDAO = new EmployeeDAO();
    ReflectionTestUtils.setField(employeeDAO, "jdbcTemplate", jdbcTemplate);
    Mockito.when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<RowMapper<Employee>> any(), anyInt()))
        .thenReturn(null);

    assertNull(employeeDAO.getEmployeeByIdV2(1));
}复制
```

## 5.结论

在这个简短的教程中，我们详细讨论了导致*JdbcTemplate*抛出异常*“EmptyResultDataAccessException：结果大小不正确：预期为 1，实际为 0”的原因。*

在此过程中，我们看到了如何产生异常以及如何使用实际示例修复它。
## 1. 概述

[Spring JdbcTemplate](https://www.baeldung.com/spring-jdbc-jdbctemplate)是开发人员专注于编写 SQL 查询和提取结果的强大工具。它连接到后端数据库并直接执行 SQL 查询。

因此，我们可以使用集成测试来确保我们可以正确地从数据库中拉取数据。此外，我们可以编写单元测试来检查相关功能的正确性。

在本教程中，我们将展示如何对JdbcTemplate代码进行单元测试。

## 2. JdbcTemplate 和运行查询

首先，让我们从使用JdbcTemplate的数据访问对象 (DAO) 类开始：

```java
public class EmployeeDAO {
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int getCountOfEmployees() {
        return jdbcTemplate.queryForObject("SELECT COUNT() FROM EMPLOYEE", Integer.class);
    }
}
```

我们将一个DataSource对象依赖注入到EmployeeDAO类中。然后，我们在 setter 方法中创建JdbcTemplate对象。此外，我们在示例方法getCountOfEmployees()中使用JdbcTemplate 。

有两种方法可以对使用JdbcTemplate的方法进行单元测试。

我们可以使用内存数据库如[H2数据库](https://www.baeldung.com/spring-boot-h2-database)作为测试的数据源。然而，在实际应用中，SQL 查询可能有复杂的关系，我们需要创建复杂的设置脚本来测试 SQL 语句。

或者，我们也可以模拟JdbcTemplate 对象来测试方法功能。

## 3. H2数据库的单元测试

我们可以创建一个连接到 H2 数据库的数据源并将其注入到EmployeeDAO类中：

```java
@Test
public void whenInjectInMemoryDataSource_thenReturnCorrectEmployeeCount() {
    DataSource dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
      .addScript("classpath:jdbc/schema.sql")
      .addScript("classpath:jdbc/test-data.sql")
      .build();

    EmployeeDAO employeeDAO = new EmployeeDAO();
    employeeDAO.setDataSource(dataSource);

    assertEquals(4, employeeDAO.getCountOfEmployees());
}
```

在本次测试中，我们首先在H2数据库上构建数据源。在构建过程中，我们执行schema.sql来创建EMPLOYEE表：

```sql
CREATE TABLE EMPLOYEE
(
    ID int NOT NULL PRIMARY KEY,
    FIRST_NAME varchar(255),
    LAST_NAME varchar(255),
    ADDRESS varchar(255)
);
```

此外，我们运行test-data.sql将测试数据添加到表中：

```sql
INSERT INTO EMPLOYEE VALUES (1, 'James', 'Gosling', 'Canada');
INSERT INTO EMPLOYEE VALUES (2, 'Donald', 'Knuth', 'USA');
INSERT INTO EMPLOYEE VALUES (3, 'Linus', 'Torvalds', 'Finland');
INSERT INTO EMPLOYEE VALUES (4, 'Dennis', 'Ritchie', 'USA');
```

然后，我们可以将此数据源注入EmployeeDAO类，并在内存中的 H2 数据库上测试getCountOfEmployees方法。

## 4. 模拟对象的单元测试

我们可以模拟JdbcTemplate对象，这样我们就不需要在数据库上运行 SQL 语句：

```java
public class EmployeeDAOUnitTest {
    @Mock
    JdbcTemplate jdbcTemplate;

    @Test
    public void whenMockJdbcTemplate_thenReturnCorrectEmployeeCount() {
        EmployeeDAO employeeDAO = new EmployeeDAO();
        ReflectionTestUtils.setField(employeeDAO, "jdbcTemplate", jdbcTemplate);
        Mockito.when(jdbcTemplate.queryForObject("SELECT COUNT() FROM EMPLOYEE", Integer.class))
          .thenReturn(4);

        assertEquals(4, employeeDAO.getCountOfEmployees());
    }
}
```

在这个单元测试中，我们首先声明一个带有@Mock注解的mock JdbcTemplate对象。[然后我们使用ReflectionTestUtils](https://www.baeldung.com/spring-reflection-test-utils)将它注入到EmployeeDAO对象。 此外，我们使用[Mockito](https://www.baeldung.com/mockito-mock-methods)实用程序来模拟JdbcTemplate查询的返回结果。这使我们能够在不连接到数据库的情况下测试getCountOfEmployees方法的功能。

当我们模拟JdbcTemplate查询时，我们在 SQL 语句字符串上使用精确匹配。在实际应用中，我们可能会创建复杂的 SQL 字符串，并且很难做到精确匹配。因此，我们也可以使用[anyString()](https://www.baeldung.com/mockito-argument-matchers)方法来绕过字符串检查：

```java
Mockito.when(jdbcTemplate.queryForObject(Mockito.anyString(), Mockito.eq(Integer.class)))
  .thenReturn(3);
assertEquals(3, employeeDAO.getCountOfEmployees());
```

## 5. 春季启动@JdbcTest

最后，如果我们使用的是 Spring Boot，则可以使用一个注解来使用 H2 数据库和JdbcTemplate bean 引导测试：@JdbcTest。

让我们用这个注解创建一个测试类：

```java
@JdbcTest
@Sql({"schema.sql", "test-data.sql"})
class EmployeeDAOIntegrationTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void whenInjectInMemoryDataSource_thenReturnCorrectEmployeeCount() {
        EmployeeDAO employeeDAO = new EmployeeDAO();
        employeeDAO.setJdbcTemplate(jdbcTemplate);

        assertEquals(4, employeeDAO.getCountOfEmployees());
    }
}
```

我们还可以注意到@Sql注解的存在，它允许我们[指定要在测试之前运行的 SQL 文件](https://www.baeldung.com/spring-boot-data-sql-and-schema-sql)。

## 六. 总结

在本教程中，我们展示了多种对JdbcTemplate 进行单元测试的方法。
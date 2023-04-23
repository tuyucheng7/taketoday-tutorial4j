## 1. 概述

在本教程中，我们将介绍 Spring JDBC 模块的实际用例。

Spring JDBC 中的所有类都分为四个独立的包：

-   核心——JDBC 的核心功能。这个包下的一些重要类包括JdbcTemplate、 SimpleJdbcInsert、 SimpleJdbcCall和NamedParameterJdbcTemplate。

-   数据源——访问数据源的实用类。它还具有用于在 Jakarta EE 容器外部测试 JDBC 代码的各种数据源实现。

-   object — 以面向对象的方式访问数据库。它允许运行查询并将结果作为业务对象返回。它还在业务对象的列和属性之间映射查询结果。

-   support

    

     —核心

    和

    对象

    包下类的支持类，例如，提供

    SQLException

    翻译功能

    ## 延伸阅读：

    ## [Spring Security：探索 JDBC 身份验证](https://www.baeldung.com/spring-security-jdbc-authentication)

    探索 Spring 提供的使用现有数据源配置执行 JDBC 身份验证的功能。

    [阅读更多](https://www.baeldung.com/spring-security-jdbc-authentication)→

    ## [Spring Data JPA 简介](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)

    Spring Data JPA 与 Spring 4 简介 - Spring 配置、DAO、手动和生成的查询以及事务管理。

    [阅读更多](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)→

## 2.配置

让我们从数据源的一些简单配置开始。

我们将使用 MySQL 数据库：

```java
@Configuration
@ComponentScan("com.baeldung.jdbc")
public class SpringJdbcConfig {
    @Bean
    public DataSource mysqlDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/springjdbc");
        dataSource.setUsername("guest_user");
        dataSource.setPassword("guest_password");

        return dataSource;
    }
}
```

或者，我们也可以利用嵌入式数据库进行开发或测试。

这是一个快速配置，它创建一个 H2 嵌入式数据库实例并使用简单的 SQL 脚本预填充它：

```java
@Bean
public DataSource dataSource() {
    return new EmbeddedDatabaseBuilder()
      .setType(EmbeddedDatabaseType.H2)
      .addScript("classpath:jdbc/schema.sql")
      .addScript("classpath:jdbc/test-data.sql").build();
}

```

最后，可以使用 XML 配置数据源来完成相同的操作：

```java
<bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource" 
  destroy-method="close">
    <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
    <property name="url" value="jdbc:mysql://localhost:3306/springjdbc"/>
    <property name="username" value="guest_user"/>
    <property name="password" value="guest_password"/>
</bean>
```

## 3. JdbcTemplate和运行查询

### 3.1. 基本查询

JDBC 模板是主要的 API，我们将通过它访问我们感兴趣的大部分功能：

-   创建和关闭连接
-   运行语句和存储过程调用
-   遍历ResultSet并返回结果

首先，让我们从一个简单的例子开始，看看JdbcTemplate可以做什么：

```java
int result = jdbcTemplate.queryForObject(
    "SELECT COUNT() FROM EMPLOYEE", Integer.class);

```

这是一个简单的插入：

```java
public int addEmplyee(int id) {
    return jdbcTemplate.update(
      "INSERT INTO EMPLOYEE VALUES (?, ?, ?, ?)", id, "Bill", "Gates", "USA");
}
```

请注意使用?提供参数的标准语法。特点。

接下来，让我们看看这种语法的替代方法。

### 3.2. 带命名参数的查询

为了获得对命名参数的支持，我们将使用框架提供的另一个 JDBC 模板 — NamedParameterJdbcTemplate。

此外，这包装了JbdcTemplate并提供了使用 ? 的传统语法的替代方法。指定参数。

在幕后，它将命名参数替换为 JDBC ? 占位符并委托给包装的JDCTemplate来运行查询：

```java
SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", 1);
return namedParameterJdbcTemplate.queryForObject(
  "SELECT FIRST_NAME FROM EMPLOYEE WHERE ID = :id", namedParameters, String.class);
```

请注意我们如何使用MapSqlParameterSource为命名参数提供值。

让我们看看使用 bean 的属性来确定命名参数：

```java
Employee employee = new Employee();
employee.setFirstName("James");

String SELECT_BY_ID = "SELECT COUNT() FROM EMPLOYEE WHERE FIRST_NAME = :firstName";

SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(employee);
return namedParameterJdbcTemplate.queryForObject(
  SELECT_BY_ID, namedParameters, Integer.class);
```

请注意我们现在如何使用BeanPropertySqlParameterSource实现而不是像以前那样手动指定命名参数。

### 3.3. 将查询结果映射到Java对象

另一个非常有用的特性是能够通过实现RowMapper接口将查询结果映射到Java对象。

例如，对于查询返回的每一行，Spring 使用行映射器来填充 java bean：

```java
public class EmployeeRowMapper implements RowMapper<Employee> {
    @Override
    public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
        Employee employee = new Employee();

        employee.setId(rs.getInt("ID"));
        employee.setFirstName(rs.getString("FIRST_NAME"));
        employee.setLastName(rs.getString("LAST_NAME"));
        employee.setAddress(rs.getString("ADDRESS"));

        return employee;
    }
}
```

随后，我们现在可以将行映射器传递给查询 API 并获取完全填充的Java对象：

```java
String query = "SELECT  FROM EMPLOYEE WHERE ID = ?";
Employee employee = jdbcTemplate.queryForObject(
  query, new Object[] { id }, new EmployeeRowMapper());
```

## 4.异常翻译

Spring 附带了自己开箱即用的数据异常层次结构——DataAccessException作为根异常——并将所有底层原始异常转换为它。

因此，我们通过不处理低级持久性异常来保持理智。我们还受益于 Spring 将低级异常包装在DataAccessException或其子类之一中这一事实。

这也使异常处理机制独立于我们正在使用的底层数据库。

除了默认的SQLErrorCodeSQLExceptionTranslator之外，我们还可以提供自己的SQLExceptionTranslator实现。

下面是自定义实现的一个简单示例 — 在存在重复键违规时自定义错误消息，这会在使用 H2 时导致[错误代码 23505 ：](https://www.h2database.com/javadoc/org/h2/api/ErrorCode.html#c23505)

```java
public class CustomSQLErrorCodeTranslator extends SQLErrorCodeSQLExceptionTranslator {
    @Override
    protected DataAccessException
      customTranslate(String task, String sql, SQLException sqlException) {
        if (sqlException.getErrorCode() == 23505) {
          return new DuplicateKeyException(
            "Custom Exception translator - Integrity constraint violation.", sqlException);
        }
        return null;
    }
}
```

要使用此自定义异常转换器，我们需要通过调用setExceptionTranslator()方法将其传递给JdbcTemplate ：

```java
CustomSQLErrorCodeTranslator customSQLErrorCodeTranslator = 
  new CustomSQLErrorCodeTranslator();
jdbcTemplate.setExceptionTranslator(customSQLErrorCodeTranslator);
```

## 5. 使用 SimpleJdbc 类的 JDBC 操作

SimpleJdbc类提供了一种配置和运行 SQL 语句的简单方法。这些类使用数据库元数据来构建基本查询。因此，SimpleJdbcInsert和SimpleJdbcCall类提供了一种更简单的方法来运行插入和存储过程调用。

## 5.1. 简单的JdbcInsert

让我们看一下以最少的配置运行简单的插入语句。

INSERT 语句是根据SimpleJdbcInsert的配置生成的。我们只需要提供表名、列名和值。

首先，让我们创建一个 SimpleJdbcInsert：

```java
SimpleJdbcInsert simpleJdbcInsert = 
  new SimpleJdbcInsert(dataSource).withTableName("EMPLOYEE");
```

接下来，让我们提供列名称和值，并运行操作：

```java
public int addEmplyee(Employee emp) {
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("ID", emp.getId());
    parameters.put("FIRST_NAME", emp.getFirstName());
    parameters.put("LAST_NAME", emp.getLastName());
    parameters.put("ADDRESS", emp.getAddress());

    return simpleJdbcInsert.execute(parameters);
}
```

此外，我们可以使用executeAndReturnKey() API 让数据库生成主键。我们还需要配置实际的自动生成列：

```java
SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                                        .withTableName("EMPLOYEE")
                                        .usingGeneratedKeyColumns("ID");

Number id = simpleJdbcInsert.executeAndReturnKey(parameters);
System.out.println("Generated id - " + id.longValue());
```

最后，我们还可以使用BeanPropertySqlParameterSource和MapSqlParameterSource传入这些数据。

## 5.2. 使用SimpleJdbcCall 的存储过程

我们还看一下运行存储过程。

我们将使用SimpleJdbcCall抽象：

```java
SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(dataSource)
		                     .withProcedureName("READ_EMPLOYEE");

public Employee getEmployeeUsingSimpleJdbcCall(int id) {
    SqlParameterSource in = new MapSqlParameterSource().addValue("in_id", id);
    Map<String, Object> out = simpleJdbcCall.execute(in);

    Employee emp = new Employee();
    emp.setFirstName((String) out.get("FIRST_NAME"));
    emp.setLastName((String) out.get("LAST_NAME"));

    return emp;
}
```

## 6.批量操作

另一个简单的用例是将多个操作一起批处理。

### 6.1. 使用JdbcTemplate 的基本批处理操作

使用JdbcTemplate，可以通过batchUpdate() API运行批处理操作。

这里有趣的部分是简洁但非常有用的BatchPreparedStatementSetter实现：

```java
public int[] batchUpdateUsingJdbcTemplate(List<Employee> employees) {
    return jdbcTemplate.batchUpdate("INSERT INTO EMPLOYEE VALUES (?, ?, ?, ?)",
        new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, employees.get(i).getId());
                ps.setString(2, employees.get(i).getFirstName());
                ps.setString(3, employees.get(i).getLastName());
                ps.setString(4, employees.get(i).getAddress();
            }
            @Override
            public int getBatchSize() {
                return 50;
            }
        });
}
```

### 6.2. 使用NamedParameterJdbcTemplate 进行批量操作

我们还可以选择使用NamedParameterJdbcTemplate – batchUpdate() API 进行批处理操作。

这个 API 比上一个更简单。因此，不需要实现任何额外的接口来设置参数，因为它有一个内部准备语句设置器来设置参数值。

相反，参数值可以作为SqlParameterSource的数组传递给batchUpdate()方法。

```java
SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(employees.toArray());
int[] updateCounts = namedParameterJdbcTemplate.batchUpdate(
    "INSERT INTO EMPLOYEE VALUES (:id, :firstName, :lastName, :address)", batch);
return updateCounts;
```

## 7. 使用Spring Boot的 Spring JDBC

Spring Boot 提供了一个 starter spring-boot-starter-jdbc用于将 JDBC 与关系数据库一起使用。

与每个Spring Boot启动器一样，这个启动器可以帮助我们快速启动和运行应用程序。

### 7.1. Maven 依赖

我们需要 [spring-boot-starter-jdbc](https://search.maven.org/search?q=a:spring-boot-starter-jdbc)依赖作为主要依赖。我们还需要我们将使用的数据库的依赖项。在我们的例子中，这是MySQL：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <scope>runtime</scope>
</dependency>
```

### 7.2. 配置

Spring Boot 会自动为我们配置数据源。我们只需要在属性文件中提供属性：

```java
spring.datasource.url=jdbc:mysql://localhost:3306/springjdbc
spring.datasource.username=guest_user
spring.datasource.password=guest_password
```

就是这样。我们的应用程序仅通过进行这些配置即可启动并运行。我们现在可以将它用于其他数据库操作。

我们在上一节中看到的标准 Spring 应用程序的显式配置现在作为Spring Boot自动配置的一部分包含在内。

## 八. 总结

在本文中，我们研究了 Spring Framework 中的 JDBC 抽象。我们通过实际示例介绍了 Spring JDBC 提供的各种功能。

我们还研究了如何使用Spring BootJDBC starter 快速开始使用 Spring JDBC。
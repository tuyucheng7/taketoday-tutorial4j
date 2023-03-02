## 1. 概述

在本快速教程中，我们将介绍最重要的常见NonTransientDataAccessException类型，并通过示例对其进行说明。

## 2.基础异常类

此主要异常类的子类表示与数据访问相关的异常，这些异常被认为是非暂时性或永久性的。

简而言之，这意味着——直到根本原因被修复——所有未来对导致异常的方法的尝试都将失败。

## 3.数据完整性违规异常

当尝试修改数据导致违反完整性约束时，将抛出此NonTransientDataAccessException子类型。

在我们的Foo类示例中，名称列被定义为不允许空值：

```java
@Column(nullable = false)
private String name;
```

如果我们试图在不为名称设置值的情况下保存实例，我们可能会抛出DataIntegrityViolationException：

```java
@Test(expected = DataIntegrityViolationException.class)
public void whenSavingNullValue_thenDataIntegrityException() {
    Foo fooEntity = new Foo();
    fooService.create(fooEntity);
}
```

### 3.1 重复键异常

DataIntegrityViolationException的子类之一是DuplicateKeyException，当尝试保存具有已存在的主键的记录或已存在于具有唯一约束的列中的值时抛出，例如尝试插入两行在相同id为1的foo表中：

```java
@Test(expected = DuplicateKeyException.class)
public void whenSavingDuplicateKeyValues_thenDuplicateKeyException() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(restDataSource);
    jdbcTemplate.execute("insert into foo(id,name) values (1,'a')");
    jdbcTemplate.execute("insert into foo(id,name) values (1,'b')");
}
```

## 4. 数据检索失败异常

当检索数据过程中出现问题时抛出此异常，例如查找具有数据库中不存在的标识符的对象。

例如，我们将使用JdbcTemplate类，它有一个抛出此异常的方法：

```java
@Test(expected = DataRetrievalFailureException.class)
public void whenRetrievingNonExistentValue_thenDataRetrievalException() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(restDataSource);
    
    jdbcTemplate.queryForObject("select  from foo where id = 3", Integer.class);
}
```

### 4.1 不正确的ResultSetColumnCountException

在未创建正确的RowMapper的情况下尝试从表中检索多个列时抛出此异常子类：

```java
@Test(expected = IncorrectResultSetColumnCountException.class)
public void whenRetrievingMultipleColumns_thenIncorrectResultSetColumnCountException() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(restDataSource);

    jdbcTemplate.execute("insert into foo(id,name) values (1,'a')");
    jdbcTemplate.queryForList("select id,name from foo where id=1", Foo.class);
}
```

### 4.2 不正确的结果大小数据访问异常

当许多检索到的记录与预期记录不同时会抛出此异常，例如，当期望单个Integer值时，但为查询检索了两行：

```java
@Test(expected = IncorrectResultSizeDataAccessException.class)
public void whenRetrievingMultipleValues_thenIncorrectResultSizeException() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(restDataSource);

    jdbcTemplate.execute("insert into foo(name) values ('a')");
    jdbcTemplate.execute("insert into foo(name) values ('a')");

    jdbcTemplate.queryForObject("select id from foo where name='a'", Integer.class);
}
```

## 5. 数据源查找失败异常

当无法获取到指定的数据源时抛出该异常。例如，我们将使用JndiDataSourceLookup类来查找不存在的数据源：

```java
@Test(expected = DataSourceLookupFailureException.class)
public void whenLookupNonExistentDataSource_thenDataSourceLookupFailureException() {
    JndiDataSourceLookup dsLookup = new JndiDataSourceLookup();
    dsLookup.setResourceRef(true);
    DataSource dataSource = dsLookup.getDataSource("java:comp/env/jdbc/example_db");
}
```

## 6. 无效的数据访问资源使用异常

当资源访问不正确时会抛出此异常，例如当用户缺少SELECT权限时。

为了测试这个异常，我们需要撤销用户的SELECT权限，然后运行SELECT查询：

```java
@Test(expected = InvalidDataAccessResourceUsageException.class)
public void whenRetrievingDataUserNoSelectRights_thenInvalidResourceUsageException() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(restDataSource);
    jdbcTemplate.execute("revoke select from tutorialuser");

    try {
        fooService.findAll();
    } finally {
        jdbcTemplate.execute("grant select to tutorialuser");
    }
}
```

请注意，我们正在finally块中恢复对用户的权限。

### 6.1 BadSqlGrammarException

InvalidDataAccessResourceUsageException的一个非常常见的子类型是BadSqlGrammarException，它在尝试使用无效SQL运行查询时抛出：

```java
@Test(expected = BadSqlGrammarException.class)
public void whenIncorrectSql_thenBadSqlGrammarException() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(restDataSource);
    jdbcTemplate.queryForObject("select  fro foo where id=3", Integer.class);
}
```

当然要注意来回——这是查询的无效方面。

## 7. 无法获取JdbcConnectionException

当通过JDBC的连接尝试失败时抛出此异常，例如当数据库url不正确时。如果我们这样写url：

```properties
jdbc.url=jdbc:mysql:3306://localhost/spring_hibernate4_exceptions?createDatabaseIfNotExist=true
```

然后在尝试执行语句时将抛出CannotGetJdbcConnectionException：

```java
@Test(expected = CannotGetJdbcConnectionException.class)
public void whenJdbcUrlIncorrect_thenCannotGetJdbcConnectionException() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(restDataSource);
    jdbcTemplate.execute("select  from foo");
}
```

## 8. 总结

在这个重点教程中，我们了解了NonTransientDataAccessException类的一些最常见的子类型。

所有示例的实现都可以在[GitHub项目](https://github.com/eugenp/tutorials/tree/master/spring-exceptions)中找到。当然，所有示例都使用内存数据库，因此你无需进行任何设置即可轻松运行它们。
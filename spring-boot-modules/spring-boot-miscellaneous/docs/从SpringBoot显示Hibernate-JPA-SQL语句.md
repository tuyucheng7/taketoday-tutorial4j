## 1. 概述

[Spring JDBC](https://www.baeldung.com/spring-jdbc-jdbctemplate)和[JPA](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)提供了对原生JDBC API的抽象，允许开发人员摆脱原生SQL查询。但是，出于调试目的，我们经常需要查看那些自动生成的SQL查询以及它们的执行顺序。

在这个快速教程中，我们将了解在Spring Boot中记录这些SQL查询的不同方式。

## 延伸阅读

### [Spring JDBC](https://www.baeldung.com/spring-jdbc-jdbctemplate)

Spring JDBC抽象简介，以及如何使用JdbcTemplate和NamedParameterJdbcTemplate API的示例。

[阅读更多](https://www.baeldung.com/spring-jdbc-jdbctemplate)→

### [Spring Data JPA简介](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)

Spring Data JPA与Spring 4简介-Spring配置、DAO、手动和生成的查询以及事务管理。

[阅读更多](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)→

### [Hibernate拦截器](https://www.baeldung.com/hibernate-interceptor)

创建Hibernate拦截器的快速实用指南。

[阅读更多](https://www.baeldung.com/hibernate-interceptor)→

## 2. 记录JPA查询

### 2.1 到标准输出

将查询转储到标准输出的最简单方法是将以下内容添加到application.properties：

```properties
spring.jpa.show-sql=true
```

为了美化或漂亮地打印SQL，我们可以添加：

```properties
spring.jpa.properties.hibernate.format_sql=true
```

虽然这非常简单，**但不推荐这样做**，因为它直接将所有内容卸载到标准输出，而没有对日志框架进行任何优化。

此外，**它不会记录预准备语句的参数**。

### 2.2 通过记录器

现在让我们看看如何通过在属性文件中配置记录器来记录SQL语句：

```properties
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

第一行记录SQL查询，第二行记录预准备语句参数。

format_sql属性也适用于此配置。

通过设置这些属性，**日志将被发送到配置的appender**。默认情况下，Spring Boot使用带标准输出appender的logback。

## 3. 记录JdbcTemplate查询

要在使用JdbcTemplate时配置语句日志记录，我们需要以下属性：

```properties
logging.level.org.springframework.jdbc.core.JdbcTemplate=DEBUG
logging.level.org.springframework.jdbc.core.StatementCreatorUtils=TRACE
```

与JPA日志记录配置类似，第一行用于记录语句，第二行用于记录预准备语句的参数。

## 4. 它是如何工作的？

**生成SQL语句并设置参数的Spring/Hibernate类已经包含用于记录它们的代码**。

但是，这些日志语句的级别分别设置为DEBUG和TRACE，低于Spring Boot中的默认级别—INFO。

通过添加这些属性，我们只是将这些记录器设置为所需的级别。

## 5. 总结

在这篇简短的文章中，我们了解了在Spring Boot中记录SQL查询的方法。

如果我们选择[配置多个appender](https://logback.qos.ch/manual/appenders.html)，我们也可以将SQL语句和其他日志语句分开到不同的日志文件中，以保持清晰。
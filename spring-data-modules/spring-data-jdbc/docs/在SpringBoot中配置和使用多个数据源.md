## 1. 概述

Spring Boot应用程序的典型场景是将数据存储在单个关系数据库中。但是我们有时需要访问多个数据库。

在本教程中，我们将学习如何使用Spring Boot配置和使用多个数据源。

要了解如何处理单个数据源，请查看我们对[Spring Data JPA的介绍](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)。

## 2. 默认行为

让我们记住在application.yml中声明Spring Boot中的数据源是什么样的：

```yaml
spring:
    datasource:
        url: ...
        username: ...
        password: ...
        driverClassname: ...
```

在内部，Spring将这些设置映射到org.springframework.boot.autoconfigure.jdbc.DataSourceProperties的实例。

让我们看一下实现：

```java
@ConfigurationProperties(prefix = "spring.datasource")
public class DataSourceProperties implements BeanClassLoaderAware, InitializingBean {

    // ...

    /**
     * Fully qualified name of the JDBC driver. Auto-detected based on the URL by default.
     */
    private String driverClassName;

    /**
     * JDBC URL of the database.
     */
    private String url;

    /**
     * Login username of the database.
     */
    private String username;

    /**
     * Login password of the database.
     */
    private String password;

    // ...
}
```

我们应该指出自动将配置属性映射到Java对象的@ConfigurationProperties注解。

## 3. 扩展默认值

因此，要使用多个数据源，我们需要在Spring的应用程序上下文中声明多个具有不同映射的beans。

我们可以通过使用配置类来做到这一点：

```java
@Configuration
public class TodoDatasourceConfiguration {

    @Bean
    @ConfigurationProperties("spring.datasource.todos")
    public DataSourceProperties todosDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.topics")
    public DataSourceProperties topicsDataSourceProperties() {
        return new DataSourceProperties();
    }
}
```

数据源的配置必须如下所示：

```yaml
spring:
    datasource:
        todos:
            url: ...
            username: ...
            password: ...
            driverClassName: ...
        topics:
            url: ...
            username: ...
            password: ...
            driverClassName: ...
```

然后我们可以使用DataSourceProperties对象创建数据源：

```java
@Bean
public DataSource todosDataSource() {
    return todosDataSourceProperties()
        .initializeDataSourceBuilder()
        .build();
}

@Bean
public DataSource topicsDataSource() {
    return topicsDataSourceProperties()
        .initializeDataSourceBuilder()
        .build();
}
```

## 4. Spring Data JDBC

在使用Spring Data JDBC时，我们还需要为每个DataSource配置一个JdbcTemplate实例：

```java
@Bean
public JdbcTemplate todosJdbcTemplate(@Qualifier("todosDataSource") DataSource dataSource) {
    return new JdbcTemplate(dataSource);
}

@Bean
public JdbcTemplate topicsJdbcTemplate(@Qualifier("topicsDataSource") DataSource dataSource) {
    return new JdbcTemplate(dataSource);
}
```

然后我们也可以通过指定@Qualifier来使用它们：

```java
@Autowired
@Qualifier("topicsJdbcTemplate")
JdbcTemplate jdbcTemplate;
```

## 5. Spring Data JPA

当使用Spring Data JPA时，我们希望使用如下所示的Repository，其中Todo是实体：

```java
public interface TodoRepository extends JpaRepository<Todo, Long> {
}
```

因此，我们需要为每个数据源声明EntityManager工厂：

```java
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
      basePackageClasses = Todo.class,
      entityManagerFactoryRef = "todosEntityManagerFactory",
      transactionManagerRef = "todosTransactionManager"
)
public class TodoJpaConfiguration {

    @Bean
    public LocalContainerEntityManagerFactoryBean todosEntityManagerFactory(
          Qualifier("todosDataSource") DataSource dataSource,
    EntityManagerFactoryBuilder builder) {
        return builder
              .dataSource(todosDataSource())
              .packages(Todo.class)
              .build();
    }

    @Bean
    public PlatformTransactionManager todosTransactionManager(
          @Qualifier("todosEntityManagerFactory") LocalContainerEntityManagerFactoryBean todosEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(todosEntityManagerFactory.getObject()));
    }
}
```

**让我们看一下我们应该注意的一些限制**。

我们需要对包进行拆分，以便为每个数据源提供一个@EnableJpaRepositories。

不幸的是，要注入EntityManagerFactoryBuilder，我们需要将其中一个数据源声明为@Primary。

这是因为EntityManagerFactoryBuilder是在org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration中声明的，并且这个类需要注入单个数据源。通常，框架的某些部分可能不需要配置多个数据源。

## 6. 配置Hikari连接池

如果我们想配置[Hikari](https://www.baeldung.com/spring-boot-hikari)，只需要在数据源定义中添加一个@ConfigurationProperties：

```java
@Bean
@ConfigurationProperties("spring.datasource.todos.hikari")
public DataSource todosDataSource() {
    return todosDataSourceProperties()
        .initializeDataSourceBuilder()
        .build();
}
```

然后我们可以将以下内容添加到application.properties文件中：

```properties
spring.datasource.todos.hikari.connectionTimeout=30000
spring.datasource.todos.hikari.idleTimeout=600000
spring.datasource.todos.hikari.maxLifetime=1800000
```

## 7. 总结

在本文中，我们学习了如何使用Spring Boot配置多个数据源。

我们看到我们需要一些配置，并且在偏离标准时可能存在陷阱，但最终是可能的。
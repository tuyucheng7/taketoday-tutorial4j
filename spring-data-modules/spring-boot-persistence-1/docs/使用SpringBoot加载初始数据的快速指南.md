## 一、概述

Spring Boot 使管理我们的数据库更改变得非常容易。如果我们保留默认配置，它将在我们的包中搜索实体并自动创建相应的表。

但有时我们需要对数据库更改进行更细粒度的控制。这就是我们可以在 Spring 中使用data.sql 和 schema.sql 文件的时候。

## 延伸阅读：

## [带有 H2 数据库的 Spring Boot](https://www.baeldung.com/spring-boot-h2-database)

了解如何配置以及如何将 H2 数据库与 Spring Boot 一起使用。

[阅读更多](https://www.baeldung.com/spring-boot-h2-database)→

## [使用 Flyway 进行数据库迁移](https://www.baeldung.com/database-migrations-with-flyway)

本文介绍了 Flyway 的关键概念，以及我们如何使用该框架可靠、轻松地持续重构应用程序的数据库模式。

[阅读更多](https://www.baeldung.com/database-migrations-with-flyway)→

## [使用 Spring Data JPA 生成数据库模式](https://www.baeldung.com/spring-data-jpa-generate-db-schema)

JPA 提供了从我们的实体模型生成 DDL 的标准。在这里，我们探讨如何在 Spring Data 中执行此操作并将其与本机 Hibernate 进行比较。

[阅读更多](https://www.baeldung.com/spring-data-jpa-generate-db-schema)→

## 2.data.sql 文件_ 

我们还假设我们正在使用 JPA 并在我们的项目中定义一个简单的Country 实体：

```java
@Entity
public class Country {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private String name;

    //...
}
```

如果我们运行我们的应用程序， Spring Boot 将为我们创建一个空表，但不会用任何东西填充它。

一个简单的方法是创建一个名为 data.sql的文件：

```sql
INSERT INTO country (name) VALUES ('India');
INSERT INTO country (name) VALUES ('Brazil');
INSERT INTO country (name) VALUES ('USA');
INSERT INTO country (name) VALUES ('Italy');
```

当我们在类路径上运行带有此文件的项目时，Spring 将选取它并将其用于填充数据库。

## 3. schema.sql 文件

有时，我们不想依赖默认的模式创建机制。

在这种情况下，我们可以创建一个自定义的 schema.sql 文件：

```sql
CREATE TABLE country (
    id   INTEGER      NOT NULL AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL,
    PRIMARY KEY (id)
);
```

Spring 将拾取此文件并将其用于创建模式。

请注意，基于脚本的初始化，即通过schema.sql和data.sql以及 Hibernate 一起初始化可能会导致一些问题。

要么我们禁用 Hibernate 自动模式创建：

```
spring.jpa.hibernate.ddl-auto=none
```

这将确保直接使用 schema.sql 和 data.sql 执行基于脚本的初始化。

如果我们仍然希望将 Hibernate 自动模式生成与基于脚本的模式创建和数据填充相结合，我们将不得不使用：

```properties
spring.jpa.defer-datasource-initialization=true
```

这将确保在执行 Hibernate 模式创建之后，还会读取schema.sql以获取任何其他模式更改，并执行data.sql以填充数据库。 

此外，默认情况下，基于脚本的初始化仅针对嵌入式数据库执行，要始终使用脚本初始化数据库，我们必须使用：

```properties
spring.sql.init.mode=always
```

请参考 Spring 官方文档中关于[使用 SQL 脚本初始化数据库的](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.using-basic-sql-scripts)内容。

## 4. 使用 Hibernate 控制数据库创建

Spring 提供了一个特定于 JPA 的 属性，Hibernate 使用它来生成 DDL：spring.jpa.hibernate.ddl-auto。

标准的 Hibernate 属性值为create、 update、 create-drop、 validate 和 none：

-   创建——Hibernate 首先删除现有表，然后创建新表。
-   更新——将基于映射(注解或 XML)创建的对象模型与现有模式进行比较，然后 Hibernate 根据差异更新模式。它永远不会删除现有的表或列，即使应用程序不再需要它们也是如此。
-   create-drop – 类似于 create，除了 Hibernate 将在所有操作完成后删除数据库；通常用于单元测试
-   validate ——Hibernate 只验证表和列是否存在；否则，它会抛出异常。
-   none – 该值有效地关闭了 DDL 生成。

 如果没有检测到模式管理器， Spring Boot 在内部默认此参数值为 create-drop ，否则所有其他情况下都没有 。

我们必须仔细设置值或使用其他机制之一来初始化数据库。

## 5.自定义数据库模式创建

默认情况下，Spring Boot 会自动创建嵌入式 DataSource的模式。

如果我们需要控制或自定义此行为，我们可以使用属性[spring.sql.init.mode](https://docs.spring.io/spring-boot/docs/current-SNAPSHOT/api/org/springframework/boot/sql/init/DatabaseInitializationMode.html)。此属性采用以下三个值之一：

-   always—— 始终初始化数据库
-   嵌入式——如果嵌入式数据库正在使用中，则始终进行初始化。如果未指定属性值，则这是默认值。
-   never—— 从不初始化数据库

值得注意的是，如果我们使用的是非嵌入式数据库，比如 MySQL 或 PostGreSQL，并且想要初始化其架构，则必须将此属性设置为always。

该属性在 Spring Boot 2.5.0 中引入；如果我们使用以前版本的 Spring Boot，则需要使用spring.datasource.initialization-mode 。

## 6. @SQL

Spring 还提供了 @Sql注解——一种初始化和填充我们的测试模式的声明方式。

让我们看看如何使用 @Sql 注解创建一个新表，并为我们的集成测试加载带有初始数据的表：

```java
@Sql({"/employees_schema.sql", "/import_employees.sql"})
public class SpringBootInitialLoadIntegrationTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    public void testLoadDataForTestClass() {
        assertEquals(3, employeeRepository.findAll().size());
    }
}
```

以下是@Sql注解的属性：

-   config – SQL 脚本的本地配置。我们将在下一节中对此进行详细描述。
-   executionPhase – 我们还可以指定何时执行脚本，BEFORE_TEST_METHOD 或 AFTER_TEST_METHOD。
-   语句——我们可以声明要执行的内联 SQL 语句。
-   脚本——我们可以声明要执行的 SQL 脚本文件的路径。这是 value 属性的别名。

@Sql注解 可以 在类级别或方法级别使用。

我们将通过注解该方法来加载特定测试用例所需的额外数据：

```java
@Test
@Sql({"/import_senior_employees.sql"})
public void testLoadDataForTestCase() {
    assertEquals(5, employeeRepository.findAll().size());
}
```

## 7. @SqlConfig 

我们可以 使用 @SqlConfig 注解来配置解析和运行 SQL 脚本的方式。

@SqlConfig可以在类级别声明，它用作全局配置。或者我们可以使用它来配置特定的@Sql 注解。

让我们看一个示例，其中我们指定了 SQL 脚本的编码以及执行脚本的事务模式：

```java
@Test
@Sql(scripts = {"/import_senior_employees.sql"}, 
  config = @SqlConfig(encoding = "utf-8", transactionMode = TransactionMode.ISOLATED))
public void testLoadDataForTestCase() {
    assertEquals(5, employeeRepository.findAll().size());
}
```

让我们看看 @SqlConfig的各种属性：

-   blockCommentStartDelimiter – 用于标识 SQL 脚本文件中块注解开始的分隔符
-   blockCommentEndDelimiter – 用于表示 SQL 脚本文件中块注解结束的分隔符
-   commentPrefix – 用于标识 SQL 脚本文件中的单行注解的前缀
-   dataSource – 脚本和语句将针对其运行的javax.sql.DataSource bean 的名称
-   编码——SQL 脚本文件的编码；默认是平台编码
-   errorMode – 运行脚本时遇到错误时使用的模式
-   分隔符——用于分隔单个语句的字符串；默认为“-”
-   transactionManager –将用于事务的PlatformTransactionManager 的 bean 名称 
-   transactionMode—— 在事务中执行脚本时将使用的模式

## 8. @SqlGroup 

Java 8 及以上版本允许使用重复注解。我们也可以将此功能用于@Sql注解。对于 Java 7 及以下版本，有一个容器注解 — @SqlGroup。

使用 @SqlGroup注解，我们将声明多个@Sql 注解：

```java
@SqlGroup({
  @Sql(scripts = "/employees_schema.sql", 
    config = @SqlConfig(transactionMode = TransactionMode.ISOLATED)),
  @Sql("/import_employees.sql")})
public class SpringBootSqlGroupAnnotationIntegrationTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    public void testLoadDataForTestCase() {
        assertEquals(3, employeeRepository.findAll().size());
    }
}
```

## 9.总结

在这篇快速文章中，我们了解了如何利用 schema.sql 和 data.sql文件来设置初始模式并用数据填充它。

我们还研究了如何使用@Sql、@SqlConfig 和 @SqlGroup 注解来加载测试数据以进行测试。

请记住，这种方法更适合基本和简单的场景，任何高级数据库处理都需要更高级和更精细的工具，如[Liquibase](https://www.baeldung.com/liquibase-refactor-schema-of-java-app) 或 [Flyway](https://www.baeldung.com/database-migrations-with-flyway)。
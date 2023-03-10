##  1. 概述

Spring Boot使管理我们的数据库更改变得非常容易。如果我们保留默认配置，它将在我们的包中搜索实体并自动创建相应的表。

但有时我们需要对数据库更改进行更细粒度的控制。这时我们就可以在Spring中使用data.sql和schema.sql文件。

## 延伸阅读

### [使用H2数据库的Spring Boot](https://www.baeldung.com/spring-boot-h2-database)

了解如何配置以及如何将H2数据库与Spring Boot一起使用。

[阅读更多](https://www.baeldung.com/spring-boot-h2-database)→

### [使用Flyway进行数据库迁移](https://www.baeldung.com/database-migrations-with-flyway)

本文介绍了Flyway的关键概念，以及我们如何使用该框架可靠、轻松地持续重构应用程序的数据库模式。

[阅读更多](https://www.baeldung.com/database-migrations-with-flyway)→

### [使用Spring Data JPA生成数据库模式](https://www.baeldung.com/spring-data-jpa-generate-db-schema)

JPA提供了从我们的实体模型生成DDL的标准。在这里，我们探讨如何在Spring Data中执行此操作并将其与原生Hibernate进行比较。
    
[阅读更多](https://www.baeldung.com/spring-data-jpa-generate-db-schema)→

## 2. data.sql文件

在这里也假设我们正在使用JPA并在我们的项目中定义一个简单的Country实体：

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

如果我们运行我们的应用程序，**Spring Boot将为我们创建一个空表，但不会用任何东西填充它**。

执行此操作的一种简单方法是创建一个名为data.sql的文件：

```sql
INSERT INTO country (name) VALUES ('India');
INSERT INTO country (name) VALUES ('Brazil');
INSERT INTO country (name) VALUES ('USA');
INSERT INTO country (name) VALUES ('Italy');
```

当我们在类路径上运行带有此文件的项目时，Spring将选取它并将其用于填充数据库。

## 3. schema.sql文件

有时，我们不想依赖默认的模式创建机制。

在这种情况下，我们可以创建一个自定义的schema.sql文件：

```sql
CREATE TABLE country
(
    id   INTEGER      NOT NULL AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL,
    PRIMARY KEY (id)
);
```

Spring将选取此文件并将其用于创建模式。

**请注意，基于脚本的初始化，即通过schema.sql和data.sql以及Hibernate一起初始化可能会导致一些问题**。

要么我们禁用Hibernate自动模式创建：

```properties
spring.jpa.hibernate.ddl-auto=none
```

这将确保直接使用schema.sql和data.sql执行基于脚本的初始化。

如果我们仍然希望将Hibernate自动模式生成与基于脚本的模式创建和数据填充相结合，我们必须使用：

```properties
spring.jpa.defer-datasource-initialization=true
```

**这将确保在执行Hibernate模式创建之后，还会读取schema.sql以获取任何其他模式更改，并执行data.sql以填充数据库**。

此外，默认情况下，基于脚本的初始化仅针对嵌入式数据库执行，要始终使用脚本初始化数据库，我们必须使用：

```properties
spring.sql.init.mode=always
```

请参考Spring官方文档中关于[使用SQL脚本初始化数据库](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.using-basic-sql-scripts)内容的。

## 4. 使用Hibernate控制数据库创建

**Spring提供了一个特定于JPA的属性，Hibernate使用它来生成DDL：spring.jpa.hibernate.ddl-auto**。

标准的Hibernate属性值为create、update、create-drop、validate和none：

- create：Hibernate首先删除现有表，然后创建新表。
- update：将基于映射(注解或XML)创建的对象模型与现有模式进行比较，然后Hibernate根据差异更新模式。它永远不会删除现有的表或列，即使应用程序不再需要它们也是如此。
- create-drop：类似于create，除了Hibernate将在所有操作完成后删除数据库；通常用于单元测试
- validate：Hibernate只验证表和列是否存在；否则，它会抛出异常。
- none：该值有效地关闭了DDL生成。

如果没有检测到模式管理器，Spring Boot在内部默认此参数值为create-drop，否则对于所有其他情况则为none。

**我们必须仔细设置值或使用其他机制之一来初始化数据库**。

## 5. 自定义数据库模式创建

默认情况下，Spring Boot会自动创建嵌入式DataSource的模式。

如果我们需要控制或自定义此行为，我们可以使用属性[spring.sql.init.mode](https://docs.spring.io/spring-boot/docs/current-SNAPSHOT/api/org/springframework/boot/sql/init/DatabaseInitializationMode.html)。此属性采用以下三个值之一：

- always：始终初始化数据库
- embedded：如果正在使用嵌入式数据库，则始终进行初始化。如果未指定属性值，则这是默认值
- never：从不初始化数据库

值得注意的是，如果我们使用的是非嵌入式数据库，比如MySQL或PostGreSQL，并且想要初始化其模式，则必须将此属性设置为always。

该属性在Spring Boot 2.5.0中引入；如果我们使用以前版本的Spring Boot，则需要使用spring.datasource.initialization-mode。

## 6. @SQL

Spring还提供了@Sql注解-一种初始化和填充我们的测试模式的声明方式。

让我们看看如何使用@Sql注解创建一个新表，并为我们的集成测试加载带有初始数据的表：

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

- config：SQL脚本的本地配置。我们将在下一节中对此进行详细描述。
- executionPhase：我们还可以指定何时执行脚本，BEFORE_TEST_METHOD或AFTER_TEST_METHOD。
- statements：我们可以声明要执行的内联SQL语句。
- scripts：我们可以声明要执行的SQL脚本文件的路径。这是value属性的别名。

**@Sql注解可以在类级别或方法级别使用**。

我们将通过注解该方法来加载特定测试用例所需的额外数据：

```java
@Test
@Sql({"/import_senior_employees.sql"})
public void testLoadDataForTestCase() {
    assertEquals(5, employeeRepository.findAll().size());
}
```

## 7. @SqlConfig

**我们可以使用@SqlConfig注解来配置解析和运行SQL脚本的方式**。

@SqlConfig可以在类级别声明，它用作全局配置。或者我们可以使用它来配置特定的@Sql注解。

让我们看一个示例，其中我们指定了SQL脚本的编码以及执行脚本的事务模式：

```java
@Test
@Sql(scripts = {"/import_senior_employees.sql"}, 
    config = @SqlConfig(encoding = "utf-8", transactionMode = TransactionMode.ISOLATED))
public void testLoadDataForTestCase() {
    assertEquals(5, employeeRepository.findAll().size());
}
```

让我们看看@SqlConfig的各种属性：

- blockCommentStartDelimiter：用于标识SQL脚本文件中块注释开始的分隔符
- blockCommentEndDelimiter：用于表示SQL脚本文件中块注释结束的分隔符
- commentPrefix：用于标识SQL脚本文件中的单行注释的前缀
- dataSource：脚本和语句将针对其运行的javax.sql.DataSource bean的名称
- encoding：SQL脚本文件的编码；默认是平台编码
- errorMode：运行脚本时遇到错误时使用的模式
- separator：用于分隔单个语句的字符串；默认为“-”
- transactionManager：将用于事务的PlatformTransactionManager的bean名称
- transactionMode：在事务中执行脚本时将使用的模式

## 8. @SqlGroup

Java 8及以上版本允许使用重复注解。我们也可以将此功能用于@Sql注解。对于Java 7及以下版本，有一个容器注解@SqlGroup。

**使用@SqlGroup注解，我们将声明多个@Sql注解**：

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

## 9. 总结

在这篇简短的文章中，我们了解了如何利用schema.sql和data.sql文件来设置初始模式并用数据填充它。

我们还研究了如何使用@Sql、@SqlConfig和@SqlGroup注解来加载测试数据以进行测试。

请记住，这种方法更适合基本和简单的场景，任何高级数据库处理都需要更高级和更精细的工具，如[Liquibase](https://www.baeldung.com/liquibase-refactor-schema-of-java-app)或[Flyway](https://www.baeldung.com/database-migrations-with-flyway)。
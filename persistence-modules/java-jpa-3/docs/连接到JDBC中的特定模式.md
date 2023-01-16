## 1. 简介

在本文中，我们将介绍数据库模式的基础知识、我们为什么需要它们以及它们的用途。之后，我们将重点介绍以 PostgreSQL 作为数据库在 JDBC 中设置模式的实际示例。

## 2. 什么是数据库模式

通常，数据库模式是一组规范数据库的规则。它是围绕数据库的附加抽象层。有两种模式：

1.  逻辑数据库模式定义适用于存储在数据库中的数据的规则。
2.  物理数据库模式定义了数据如何物理存储在存储系统上的规则。

在 PostgreSQL 中，模式指的是第一种。Schema是一个逻辑命名空间，包含表、视图、索引等数据库对象。每个schema都属于一个数据库，每个数据库至少有一个schema。如果没有另外指定，PostgreSQL 中的默认模式是公共的。我们创建的每个数据库对象都属于公共模式，没有指定模式。

PostgreSQL 中的模式允许我们将表和视图组织成组并使它们更易于管理。这样，我们就可以在更细粒度的级别上为我们的数据库对象设置权限。此外，模式允许我们让多个用户同时使用相同的数据库而不会相互干扰。

## 3. 如何在 PostgreSQL 中使用模式

要访问数据库模式的对象，我们必须在要使用的给定数据库对象的名称之前指定模式的名称。例如，要查询模式存储中的表产品，我们需要使用表的限定名称：

```sql
SELECT  FROM store.product;
```

建议避免硬编码模式名称，以防止将具体模式耦合到我们的应用程序。这意味着我们直接使用数据库对象名称，让数据库系统决定使用哪个模式。PostgreSQL 通过遵循搜索路径确定在何处搜索给定表。

### 3.1. PostgreSQL搜索路径

搜索路径是一个有序的模式列表，它定义了数据库系统对给定数据库对象的搜索。如果对象存在于任何(或多个)模式中，我们将找到第一个出现的地方。否则，我们会得到一个错误。搜索路径中的第一个模式也称为当前模式。要预览搜索路径上的模式，我们可以使用查询：

```sql
SHOW search_path;
```

默认的 PostgreSQL 配置将返回$user和 public 模式。我们已经提到的公共模式，即$user模式，是一个以当前用户命名的模式，它可能不存在。在这种情况下，数据库会忽略该模式。

要将存储模式添加到搜索路径，我们可以执行查询：

```sql
SET search_path TO store,public;
```

在此之后，我们可以在不指定模式的情况下查询产品表。此外，我们可以从搜索路径中删除公共架构。

我们上面描述的设置搜索路径是ROLE级别的配置。我们可以通过更改postgresql.conf文件并重新加载数据库实例来更改整个数据库的搜索路径。

### 3.2. JDBC 网址

我们可以使用[JDBC](https://www.baeldung.com/java-jdbc) URL 在连接设置期间指定各种参数。通常的参数是数据库类型、地址、端口、数据库名称等。从[Postgres 版本 9.4 开始。](https://jdbc.postgresql.org/documentation/94/connect.html#connection-parameters)添加了对使用 URL 指定当前模式的支持。

在将这个概念付诸实践之前，让我们先搭建一个测试环境。为此，我们将使用[testcontainers](https://www.baeldung.com/spring-boot-testcontainers-integration-test)库并创建以下测试设置：

```java
@ClassRule
public static PostgresqlTestContainer container = PostgresqlTestContainer.getInstance();

@BeforeClass
public static void setup() throws Exception {
    Properties properties = new Properties();
    properties.setProperty("user", container.getUsername());
    properties.setProperty("password", container.getPassword());
    Connection connection = DriverManager.getConnection(container.getJdbcUrl(), properties);
    connection.createStatement().execute("CREATE SCHEMA store");
    connection.createStatement().execute("CREATE TABLE store.product(id SERIAL PRIMARY KEY, name VARCHAR(20))");
    connection.createStatement().execute("INSERT INTO store.product VALUES(1, 'test product')");
}
```

使用[@ClassRule，](https://www.baeldung.com/junit-4-rules)我们创建了一个 PostgreSQL 数据库容器的实例。接下来，在设置方法中，创建到该数据库的连接并创建所需的对象。

现在设置数据库后，让我们使用 JDBC URL连接到存储模式：

```java
@Test
public void settingUpSchemaUsingJdbcURL() throws Exception {
    Properties properties = new Properties();
    properties.setProperty("user", container.getUsername());
    properties.setProperty("password", container.getPassword());
    Connection connection = DriverManager.getConnection(container.getJdbcUrl().concat("&" + "currentSchema=store"), properties);

    ResultSet resultSet = connection.createStatement().executeQuery("SELECT  FROM product");
    resultSet.next();

    assertThat(resultSet.getInt(1), equalTo(1));
    assertThat(resultSet.getString(2), equalTo("test product"));
}
```

要更改默认模式，我们需要指定currentSchema参数。如果我们输入一个不存在的模式，则在选择查询期间抛出PSQLException，表示缺少数据库对象。

### 3.3. PGSimpleDataSource

要连接到数据库，我们可以使用名为PGSimpleDataSource的 PostgreSQL 驱动程序库中的javax.sql.DataSource实现。这个具体实现支持建立模式：

```java
@Test
public void settingUpSchemaUsingPGSimpleDataSource() throws Exception {
    int port = //extracting port from container.getJdbcUrl()
    PGSimpleDataSource ds = new PGSimpleDataSource();
    ds.setServerNames(new String[]{container.getHost()});
    ds.setPortNumbers(new int[]{port});
    ds.setUser(container.getUsername());
    ds.setPassword(container.getPassword());
    ds.setDatabaseName("test");
    ds.setCurrentSchema("store");

    ResultSet resultSet = ds.getConnection().createStatement().executeQuery("SELECT  FROM product");
    resultSet.next();

    assertThat(resultSet.getInt(1), equalTo(1));
    assertThat(resultSet.getString(2), equalTo("test product"));
}
```

在使用PGSimpleDataSource时，如果我们不设置模式，驱动程序默认使用公共模式。

### 3.4. 来自 javax.persistence 包的 @Table 注解

如果我们在项目中使用 JPA，我们可以使用[@Table](https://www.baeldung.com/jpa-entities)注解在实体级别指定模式。此注解可以保存模式的值或默认为空字符串。让我们将产品表映射到产品实体：

```java
@Entity
@Table(name = "product", schema = "store")
public class Product {

    @Id
    private int id;
    private String name;
    
    // getters and setters
}
```

为了验证这种行为，我们设置[EntityManager](https://www.baeldung.com/hibernate-entitymanager) 实例来查询产品表：

```java
@Test
public void settingUpSchemaUsingTableAnnotation(){
    Map<String,String> props = new HashMap<>();
    props.put("hibernate.connection.url", container.getJdbcUrl());
    props.put("hibernate.connection.user", container.getUsername());
    props.put("hibernate.connection.password", container.getPassword());
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("postgresql_schema_unit", props);
    EntityManager entityManager = emf.createEntityManager();

    Product product = entityManager.find(Product.class, 1);

    assertThat(product.getName(), equalTo("test product"));
}
```

正如我们之前在第 3 节中提到的，出于各种原因最好避免将模式与代码耦合。因此，此功能经常被忽视，但在访问多个模式时可能很有用。

## 4. 总结

在本教程中，首先，我们介绍了有关数据库模式的基本理论。之后，我们描述了使用不同方法和技术设置数据库模式的多种方法。
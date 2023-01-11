## 1. 概述

在本快速教程中，我们逐步介绍如何在支持JPA的Spring Boot应用程序中使用[SQLite](https://sqlite.org/index.html)数据库。

Spring Boot开箱即用地[支持一些现成的内存数据库]()，但SQLite对我们的要求更多。

## 2. 项目设置

在pom中，我们需要添加[sqllite-jdbc](https://search.maven.org/classic/#search|ga|1|a%3A"sqlite-jdbc" AND g%3A"org.xerial")依赖：

```xml
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.25.2</version>
</dependency>
```

该依赖项为我们提供了使用[JDBC](https://static.javadoc.io/org.xerial/sqlite-jdbc/3.25.2/org/sqlite/JDBC.html)与SQLite通信所需的条件。但是，如果我们要使用ORM，还远远不够。

## 3. SQLite方言

Hibernate没有为SQLite提供[方言](https://docs.jboss.org/hibernate/orm/4.3/javadocs/org/hibernate/dialect/Dialect.html)，我们需要自己创造一个。

### 3.1 扩展Dialect

第一步是继承org.hibernate.dialect.Dialect类来注册SQLite提供的[数据类型](https://www.sqlite.org/datatype3.html)：

```java
public class SQLiteDialect extends Dialect {

    public SQLiteDialect() {
        registerColumnType(Types.BIT, "integer");
        registerColumnType(Types.TINYINT, "tinyint");
        registerColumnType(Types.SMALLINT, "smallint");
        registerColumnType(Types.INTEGER, "integer");
        // other data types
    }
}
```

上面给出的代码只列出了一部分数据类型，完整的实现可以查看仓库源代码。接下来，我们需要重写一些默认的方言行为。

### 3.2 标识列支持

例如，**我们需要告诉Hibernate SQLite如何处理@Id列**，这可以通过自定义IdentityColumnSupport实现来做到这一点：

```java
public class SQLiteIdentityColumnSupport extends IdentityColumnSupportImpl {

    @Override
    public boolean supportsIdentityColumns() {
        return true;
    }

    @Override
    public String getIdentitySelectString(String table, String column, int type) throws MappingException {
        return "select last_insert_rowid()";
    }

    @Override
    public String getIdentityColumnString(int type) throws MappingException {
        return "integer";
    }
}
```

为了简单起见，我们将标识列类型仅保持为Integer。为了获得下一个可用的标识值，我们将指定适当的机制。然后，我们简单地重写SQLiteDialect类中的相应方法：

```java
@Override
public IdentityColumnSupport getIdentityColumnSupport() {
    return new SQLiteIdentityColumnSupport();
}
```

### 3.3 禁用约束处理

而且，**SQLite不支持数据库约束，因此我们需要通过再次重写主键和外键的适当方法来禁用这些约束**：

```java
@Override
public boolean hasAlterTable() {
    return false;
}

@Override
public boolean dropConstraints() {
    return false;
}

@Override
public String getDropForeignKeyString() {
    return "";
}

@Override
public String getAddForeignKeyConstraintString(String cn,String[] fk, String t, String[] pk, boolean rpk) {
    return "";
}

@Override
public String getAddPrimaryKeyConstraintString(String constraintName) {
    return "";
}
```

稍后，我们就能够在我们的Spring Boot配置中引用这个新方言。

## 4. 数据源配置

此外，**由于Spring Boot没有为SQLite数据库提供开箱即用的配置支持，我们还需要公开我们自己的DataSource bean**：

```java
@Autowired Environment env;

@Bean
public DataSource dataSource() {
    final DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(env.getProperty("driverClassName"));
    dataSource.setUrl(env.getProperty("url"));
    dataSource.setUsername(env.getProperty("user"));
    dataSource.setPassword(env.getProperty("password"));
    return dataSource;
}
```

最后，我们需要在persistence.properties文件中配置以下属性：

```properties
driverClassName=org.sqlite.JDBC
url=jdbc:sqlite:memory:myDb?cache=shared
username=sa
password=sa
hibernate.dialect=cn.tuyucheng.taketoday.books.dialect.SQLiteDialect
hibernate.hbm2ddl.auto=create-drop
hibernate.show_sql=true
```

请注意，我们需要将缓存保持为共享状态，以便在多个数据库连接中保持数据库更新可见。

**因此，通过上述配置，应用程序将启动并启动一个名为myDb的内存数据库**，其余的[Spring Data Rest](https://www.baeldung.com/spring-data-rest-intro)配置可以使用该数据库。

## 5. 总结

在本文中，我们构建了一个示例Spring Data Rest应用程序，并使用SQLite数据库。然而，要做到这一点，我们必须创建一个自定义的Hibernate方言。

请务必在[Github]()上查看该应用程序，然后运行mvn -Dspring.profiles.active=sqlite spring-boot:run并访问[http://localhost:8080](http://localhost:8080/)。
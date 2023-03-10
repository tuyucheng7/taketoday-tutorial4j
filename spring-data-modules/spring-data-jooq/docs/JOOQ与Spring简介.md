## 1. 概述

本文将介绍Jooq面向对象查询-Jooq，以及与Spring框架协作设置它的简单方法。

大多数Java应用程序都具有某种SQL持久层，并在更高级别的工具(如JPA)的帮助下访问该层。虽然这很有用，但在某些情况下，你确实需要一个更精细、更细致的工具来获取你的数据或实际利用底层数据库必须提供的所有功能。

Jooq避免了一些典型的ORM模式，并生成允许我们构建类型安全查询的代码，并通过干净而强大的流式API完全控制生成的SQL。

本文重点介绍Spring MVC。我们的文章[jOOQ的Spring Boot支持](https://www.baeldung.com/spring-boot-support-for-jooq)描述了如何在Spring Boot中使用jOOQ。

## 2. Maven依赖

以下依赖项是运行本教程中的代码所必需的。

### 2.1 jOOQ

```xml
<dependency>
    <groupId>org.jooq</groupId>
    <artifactId>jooq</artifactId>
    <version>3.14.15</version>
</dependency>
```

### 2.2 Spring

我们的示例需要几个Spring依赖项；但是，为了简单起见，我们只需要在POM文件中显式包含其中两个：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>5.2.2.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-jdbc</artifactId>
    <version>5.2.2.RELEASE</version>
</dependency>
```

### 2.3 数据库

为了简化我们的示例，我们将使用H2嵌入式数据库：

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>1.4.191</version>
</dependency>
```

## 3. 代码生成

### 3.1 数据库结构

让我们介绍一下我们将在整篇文章中使用的数据库结构。假设我们需要为出版商创建一个数据库来存储他们管理的书籍和作者的信息，其中一个作者可能写了很多书，而一本书可能是由许多作者合著的。

为简单起见，我们将只生成三个表：书籍的book，作者的author，以及另一个名为author_book的表，用于表示作者和书籍之间的多对多关系。author表包含三列：id、first_name和last_name。book表只包含一个title列和id主键。

以下SQL查询存储在intro_schema.sql资源文件中，将针对我们之前设置的数据库执行，以创建必要的表并用示例数据填充它们：

```sql
DROP TABLE IF EXISTS author_book, author, book;

CREATE TABLE author
(
    id         INT         NOT NULL PRIMARY KEY,
    first_name VARCHAR(50),
    last_name  VARCHAR(50) NOT NULL
);

CREATE TABLE book
(
    id    INT          NOT NULL PRIMARY KEY,
    title VARCHAR(100) NOT NULL
);

CREATE TABLE author_book
(
    author_id INT NOT NULL,
    book_id   INT NOT NULL,

    PRIMARY KEY (author_id, book_id),
    CONSTRAINT fk_ab_author FOREIGN KEY (author_id) REFERENCES author (id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_ab_book FOREIGN KEY (book_id) REFERENCES book (id)
);

INSERT INTO author
VALUES (1, 'Kathy', 'Sierra'),
       (2, 'Bert', 'Bates'),
       (3, 'Bryan', 'Basham');

INSERT INTO book
VALUES (1, 'Head First Java'),
       (2, 'Head First Servlets and JSP'),
       (3, 'OCA/OCP Java SE 7 Programmer');

INSERT INTO author_book
VALUES (1, 1),
       (1, 3),
       (2, 1);
```

### 3.2 Properties Maven插件

我们将使用三个不同的Maven插件来生成Jooq代码。其中第一个是Properties Maven插件。

该插件用于从资源文件中读取配置数据。这不是必需的，因为数据可以直接添加到POM，但在外部管理属性是个好主意。

在本节中，我们将在名为intro_config.properties的文件中定义数据库连接的属性，包括JDBC驱动程序类、数据库URL、用户名和密码。外部化这些属性使得切换数据库或仅更改配置数据变得容易。

此插件的read-project-properties目标应绑定到早期阶段，以便可以准备配置数据以供其他插件使用。在这种情况下，它绑定到initialize阶段：

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>properties-maven-plugin</artifactId>
    <version>1.0.0</version>
    <executions>
        <execution>
            <phase>initialize</phase>
            <goals>
                <goal>read-project-properties</goal>
            </goals>
            <configuration>
                <files>
                    <file>src/main/resources/intro_config.properties</file>
                </files>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### 3.3 SQL Maven插件

SQL Maven插件用于执行SQL语句来创建和填充数据库表。它将利用Properties Maven插件从intro_config.properties文件中提取的属性，并从intro_schema.sql资源中获取SQL语句。

SQL Maven插件配置如下：

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>sql-maven-plugin</artifactId>
    <version>1.5</version>
    <executions>
        <execution>
            <phase>initialize</phase>
            <goals>
                <goal>execute</goal>
            </goals>
            <configuration>
                <driver>${db.driver}</driver>
                <url>${db.url}</url>
                <username>${db.username}</username>
                <password>${db.password}</password>
                <srcFiles>
                    <srcFile>src/main/resources/intro_schema.sql</srcFile>
                </srcFiles>
            </configuration>
        </execution>
    </executions>
    <dependencies>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <version>1.4.191</version>
        </dependency>
    </dependencies>
</plugin>
```

请注意，此插件必须放置在POM文件中的Properties Maven插件之后，因为它们的execute目标都绑定到同一阶段，并且Maven将按照它们列出的顺序执行它们。

### 3.4 jOOQ代码生成插件

Jooq Codegen插件从数据库表结构生成Java代码。它的generate目标应该绑定到generate-sources阶段，以确保正确的执行顺序。插件元数据如下所示：

```xml
<plugin>
    <groupId>org.jooq</groupId>
    <artifactId>jooq-codegen-maven</artifactId>
    <version>${org.jooq.version}</version>
    <executions>
        <execution>
            <phase>generate-sources</phase>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <jdbc>
                    <driver>${db.driver}</driver>
                    <url>${db.url}</url>
                    <user>${db.username}</user>
                    <password>${db.password}</password>
                </jdbc>
                <generator>
                    <target>
                        <packageName>cn.tuyucheng.taketoday.jooq.introduction.db</packageName>
                        <directory>src/main/java</directory>
                    </target>
                </generator>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### 3.5 生成代码

要完成源代码生成过程，我们需要运行Maven generate-sources阶段。在Eclipse中，我们可以通过右键单击项目并选择RunAs –> Maven generate-sources来完成此操作。命令完成后，将生成与author、book、author_book表(以及其他几个支持类)对应的源文件。

让我们深入研究表类，看看Jooq生成了什么。每个类都有一个与类同名的静态字段，只是名称中的所有字母都大写。以下是从生成的类的定义中提取的代码片段：

Author类：

```java
public class Author extends TableImpl<AuthorRecord> {
    public static final Author AUTHOR = new Author();

    // other class members
}
```

Book类：

```java
public class Book extends TableImpl<BookRecord> {
    public static final Book BOOK = new Book();

    // other class members
}
```

AuthorBook类：

```java
public class AuthorBook extends TableImpl<AuthorBookRecord> {
    public static final AuthorBook AUTHOR_BOOK = new AuthorBook();

    // other class members
}
```

这些静态字段引用的实例将用作数据访问对象，以在与项目中的其他层一起工作时表示相应的表。

## 4. Spring配置

### 4.1 将jOOQ异常转换为Spring

为了使Jooq执行抛出的异常与Spring对数据库访问的支持一致，我们需要将它们转换成DataAccessException类的子类型。

让我们定义一个ExecuteListener接口的实现来转换异常：

```java
public class ExceptionTranslator extends DefaultExecuteListener {
    public void exception(ExecuteContext context) {
        SQLDialect dialect = context.configuration().dialect();
        SQLExceptionTranslator translator = new SQLErrorCodeSQLExceptionTranslator(dialect.name());
        context.exception(translator
              .translate("Access database using Jooq", context.sql(), context.sqlException()));
    }
}
```

此类将由Spring应用程序上下文使用。

### 4.2 配置Spring

本节将介绍定义PersistenceContext的步骤，该上下文包含要在Spring应用程序上下文中使用的元数据和bean。

让我们从对类应用必要的注解开始：

- @Configuration：使类被识别为bean的容器
- @ComponentScan：配置扫描指令，包括用于声明包名称数组以搜索组件的value参数。在本教程中，要扫描的包是由Jooq Codegen Maven插件生成的包
- @EnableTransactionManagement：启用Spring管理的事务
- @PropertySource：表示要加载的属性文件的位置。本文中的值指向包含配置数据和数据库方言的文件，恰好是3.1小节中提到的同一个文件。

```java
@Configuration
@ComponentScan({"cn.tuyucheng.taketoday.Jooq.introduction.db.public_.tables"})
@EnableTransactionManagement
@PropertySource("classpath:intro_config.properties")
public class PersistenceContext {
    // Other declarations
}
```

接下来，使用Environment对象获取配置数据，然后使用这些数据配置DataSource bean：

```java
@Autowired
private Environment environment;

@Bean
public DataSource dataSource() {
    JdbcDataSource dataSource = new JdbcDataSource();

    dataSource.setUrl(environment.getRequiredProperty("db.url"));
    dataSource.setUser(environment.getRequiredProperty("db.username"));
    dataSource.setPassword(environment.getRequiredProperty("db.password"));
    return dataSource; 
}
```

现在我们定义几个bean来处理数据库访问操作：

```java
@Bean
public TransactionAwareDataSourceProxy transactionAwareDataSource() {
    return new TransactionAwareDataSourceProxy(dataSource());
}

@Bean
public DataSourceTransactionManager transactionManager() {
    return new DataSourceTransactionManager(dataSource());
}

@Bean
public DataSourceConnectionProvider connectionProvider() {
    return new DataSourceConnectionProvider(transactionAwareDataSource());
}

@Bean
public ExceptionTranslator exceptionTransformer() {
    return new ExceptionTranslator();
}
    
@Bean
public DefaultDSLContext dsl() {
    return new DefaultDSLContext(configuration());
}
```

最后，我们提供一个Jooq Configuration实现并将其声明为一个Spring bean以供DSLContext类使用：

```java
@Bean
public DefaultConfiguration configuration() {
    DefaultConfiguration JooqConfiguration = new DefaultConfiguration();
    jooqConfiguration.set(connectionProvider());
    jooqConfiguration.set(new DefaultExecuteListenerProvider(exceptionTransformer()));

    String sqlDialectName = environment.getRequiredProperty("jooq.sql.dialect");
    SQLDialect dialect = SQLDialect.valueOf(sqlDialectName);
    jooqConfiguration.set(dialect);

    return jooqConfiguration;
}
```

## 5. 将jOOQ与Spring结合使用

本节演示Jooq在常见的数据库访问查询中的使用。对于每种类型的“写”操作，包括插入、更新和删除数据，有两种测试，一种用于提交，一种用于回滚。在选择数据以验证“写”查询时，说明了“读”操作的使用。

我们将首先声明一个自动注入的DSLContext对象和Jooq生成的类的实例，以供所有测试方法使用：

```java
@Autowired
private DSLContext dsl;

Author author = Author.AUTHOR;
Book book = Book.BOOK;
AuthorBook authorBook = AuthorBook.AUTHOR_BOOK;
```

### 5.1 插入数据

第一步是向表中插入数据：

```java
dsl.insertInto(author)
    .set(author.ID, 4)
    .set(author.FIRST_NAME, "Herbert")
    .set(author.LAST_NAME, "Schildt")
    .execute();
dsl.insertInto(book)
    .set(book.ID, 4)
    .set(book.TITLE, "A Beginner's Guide")
    .execute();
dsl.insertInto(authorBook)
    .set(authorBook.AUTHOR_ID, 4)
    .set(authorBook.BOOK_ID, 4)
    .execute();
```

用于提取数据的SELECT查询：

```java
Result<Record3<Integer, String, Integer>> result = dsl
    .select(author.ID, author.LAST_NAME, DSL.count())
    .from(author)
    .join(authorBook)
    .on(author.ID.equal(authorBook.AUTHOR_ID))
    .join(book)
    .on(authorBook.BOOK_ID.equal(book.ID))
    .groupBy(author.LAST_NAME)
    .fetch();
```

上面的查询产生以下输出：

```shell
+----+---------+-----+
|  ID|LAST_NAME|count|
+----+---------+-----+
|   1|Sierra   |    2|
|   2|Bates    |    1|
|   4|Schildt  |    1|
+----+---------+-----+
```

结果由Assert API确认：

```java
assertEquals(3, result.size());
assertEquals("Sierra", result.getValue(0, author.LAST_NAME));
assertEquals(Integer.valueOf(2), result.getValue(0, DSL.count()));
assertEquals("Schildt", result.getValue(2, author.LAST_NAME));
assertEquals(Integer.valueOf(1), result.getValue(2, DSL.count()));
```

当由于无效查询而发生故障时，将抛出异常并回滚事务。在以下示例中，INSERT查询违反了外键约束，导致出现异常：

```java
@Test(expected = DataAccessException.class)
public void givenInvalidData_whenInserting_thenFail() {
    dsl.insertInto(authorBook)
        .set(authorBook.AUTHOR_ID, 4)
        .set(authorBook.BOOK_ID, 5)
        .execute();
}
```

### 5.2 更新数据

现在让我们更新现有数据：

```java
dsl.update(author)
    .set(author.LAST_NAME, "Baeldung")
    .where(author.ID.equal(3))
    .execute();
dsl.update(book)
    .set(book.TITLE, "Building your REST API with Spring")
    .where(book.ID.equal(3))
    .execute();
dsl.insertInto(authorBook)
    .set(authorBook.AUTHOR_ID, 3)
    .set(authorBook.BOOK_ID, 3)
    .execute();
```

获取必要的数据：

```java
Result<Record3<Integer, String, String>> result = dsl
    .select(author.ID, author.LAST_NAME, book.TITLE)
    .from(author)
    .join(authorBook)
    .on(author.ID.equal(authorBook.AUTHOR_ID))
    .join(book)
    .on(authorBook.BOOK_ID.equal(book.ID))
    .where(author.ID.equal(3))
    .fetch();
```

输出应该是：

```shell
+----+---------+----------------------------------+
|  ID|LAST_NAME|TITLE                             |
+----+---------+----------------------------------+
|   3|Baeldung |Building your REST API with Spring|
+----+---------+----------------------------------+
```

以下测试将验证Jooq是否按预期工作：

```java
assertEquals(1, result.size());
assertEquals(Integer.valueOf(3), result.getValue(0, author.ID));
assertEquals("Baeldung", result.getValue(0, author.LAST_NAME));
assertEquals("Building your REST API with Spring", result.getValue(0, book.TITLE));
```

如果失败，将抛出异常并回滚事务，我们通过测试确认这一点：

```java
@Test(expected = DataAccessException.class)
public void givenInvalidData_whenUpdating_thenFail() {
    dsl.update(authorBook)
        .set(authorBook.AUTHOR_ID, 4)
        .set(authorBook.BOOK_ID, 5)
        .execute();
}
```

### 5.3 删除数据

以下方法删除一些数据：

```java
dsl.delete(author)
    .where(author.ID.lt(3))
    .execute();
```

这是读取受影响表的查询：

```java
Result<Record3<Integer, String, String>> result = dsl
    .select(author.ID, author.FIRST_NAME, author.LAST_NAME)
    .from(author)
    .fetch();
```

查询输出：

```shell
+----+----------+---------+
|  ID|FIRST_NAME|LAST_NAME|
+----+----------+---------+
|   3|Bryan     |Basham   |
+----+----------+---------+
```

以下测试验证删除：

```java
assertEquals(1, result.size());
assertEquals("Bryan", result.getValue(0, author.FIRST_NAME));
assertEquals("Basham", result.getValue(0, author.LAST_NAME));
```

另一方面，如果查询无效，它将抛出异常并且事务回滚。下面的测试用于证明：

```java
@Test(expected = DataAccessException.class)
public void givenInvalidData_whenDeleting_thenFail() {
    dsl.delete(book)
        .where(book.ID.equal(1))
        .execute();
}
```

## 6. 总结

本教程介绍了Jooq的基础知识，这是一个用于处理数据库的Java库。它涵盖了从数据库结构生成源代码的步骤以及如何使用新创建的类与该数据库进行交互。
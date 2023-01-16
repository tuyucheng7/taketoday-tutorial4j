## 1. 简介

在本教程中，我们将快速浏览一下使用[jOOQ](https://www.jooq.org/)(Java 面向对象查询)运行应用程序。该库基于数据库表生成Java类，并让我们通过其流畅的 API 创建类型安全的 SQL 查询。

我们将介绍整个设置、PostgreSQL 数据库连接和几个 CRUD 操作示例。

## 2.Maven依赖

对于 jOOQ 库，我们需要以下[三个 jOOQ 依赖项](https://search.maven.org/classic/#search|ga|1|g%3A"org.jooq" AND (a%3A"jooq" OR a%3A"jooq-meta" OR a%3A"jooq-codegen"))：

```xml
<dependency>
    <groupId>org.jooq</groupId>
    <artifactId>jooq</artifactId>
    <version>3.13.4</version>
</dependency>
<dependency>
    <groupId>org.jooq</groupId>
    <artifactId>jooq-meta</artifactId>
    <version>3.13.4</version>
</dependency>
<dependency>
    <groupId>org.jooq</groupId>
    <artifactId>jooq-codegen</artifactId>
    <version>3.13.4</version>
</dependency>
```

我们还需要[PostgreSQL 驱动程序](https://search.maven.org/classic/#search|ga|1|g%3A"org.postgresql" AND a%3A"postgresql")的一个依赖项：

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.2.16</version>
</dependency>
```

## 3. 数据库结构

在开始之前，让我们为示例创建一个简单的数据库模式。我们将使用简单的Author和Article关系：

```sql
create table AUTHOR
(
    ID         integer PRIMARY KEY,
    FIRST_NAME varchar(255),
    LAST_NAME  varchar(255),
    AGE        integer
);

create table ARTICLE
(
    ID          integer PRIMARY KEY,
    TITLE       varchar(255) not null,
    DESCRIPTION varchar(255),
    AUTHOR_ID   integer
        CONSTRAINT fk_author_id REFERENCES AUTHOR
);
```

## 4.数据库连接

现在，让我们来看看我们将如何[连接到我们的数据库](https://www.baeldung.com/java-jdbc)。

首先，我们需要提供用户、密码和数据库的完整 URL。我们将使用这些属性通过DriverManager及其getConnection方法创建一个Connection对象：

```java
String userName = "user";
String password = "pass";
String url = "jdbc:postgresql://db_host:5432/baeldung";
Connection conn = DriverManager.getConnection(url, userName, password);

```

接下来，我们需要创建一个[DSLContext](https://www.jooq.org/javadoc/latest/org.jooq/org/jooq/DSLContext.html)实例。这个对象将是我们 jOOQ 接口的入口点：

```java
DSLContext context = DSL.using(conn, SQLDialect.POSTGRES);
```

在我们的例子中，我们传递的是POSTGRES方言，但几乎没有其他可用的方言，如 H2、MySQL、SQLite 等。

## 5.代码生成

要为我们的数据库表生成Java类，我们需要以下jooq-config.xml文件：

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<configuration xmlns="http://www.jooq.org/xsd/jooq-codegen-3.13.0.xsd">
    
    <jdbc>
        <driver>org.postgresql.Driver</driver>
        <url>jdbc:postgresql://db_url:5432/baeldung_database</url>
        <user>username</user>
        <password>password</password>
    </jdbc>

    <generator>
        <name>org.jooq.codegen.JavaGenerator</name>

        <database>
            <name>org.jooq.meta.postgres.PostgresDatabase</name>
            <inputSchema>public</inputSchema>
            <includes>.</includes>
            <excludes></excludes>
        </database>

        <target>
            <packageName>com.baeldung.jooq.model</packageName>
            <directory>C:/projects/baeldung/tutorials/jooq-examples/src/main/java</directory>
        </target>
    </generator>
</configuration>
```

自定义配置需要更改我们放置数据库凭据的<jdbc>部分和我们为将生成的类配置包名称和位置目录的<target>部分。

要执行 jOOQ 代码生成工具，我们需要运行以下代码：

```java
GenerationTool.generate(
  Files.readString(
    Path.of("jooq-config.xml")
  )    
);
```

生成完成后，我们会得到下面两个类，每个类对应它的数据库表：

```java
com.baeldung.model.generated.tables.Article;
com.baeldung.model.generated.tables.Author;
```

## 6.增删改查操作

现在，让我们看一下我们可以使用 jOOQ 库执行的一些基本 CRUD 操作。

### 6.1. 创造

首先，让我们创建一个新的文章记录。为此，我们需要使用适当的表引用作为参数调用newRecord方法：

```java
ArticleRecord article = context.newRecord(Article.ARTICLE);
```

Article.ARTICLE变量是 ARTICLE 数据库表的引用实例。它是在代码生成期间由 jOOQ 自动创建的。

接下来，我们可以为所有需要的属性设置值：

```java
article.setId(2);
article.setTitle("jOOQ examples");
article.setDescription("A few examples of jOOQ CRUD operations");
article.setAuthorId(1);
```

最后，我们需要调用记录上的store方法将其保存在数据库中：

```java
article.store();
```

### 6.2. 阅读

现在，让我们看看如何从数据库中读取值。例如，让我们选择所有作者：

```java
Result<Record> authors = context.select()
  .from(Author.AUTHOR)
  .fetch();
```

在这里，我们使用select方法结合from子句来指示我们要从哪个表中读取。调用fetch方法执行 SQL 查询并返回生成的结果。

Result对象实现了Iterable接口，因此很容易遍历每个元素。在访问单个记录的同时，我们可以通过使用具有适当字段引用的getValue方法来获取其参数：

```java
authors.forEach(author -> {
    Integer id = author.getValue(Author.AUTHOR.ID);
    String firstName = author.getValue(Author.AUTHOR.FIRST_NAME);
    String lastName = author.getValue(Author.AUTHOR.LAST_NAME);
    Integer age = author.getValue(Author.AUTHOR.AGE);

    System.out.printf("Author %s %s has id: %d and age: %d%n", firstName, lastName, id, age);
});
```

我们可以将选择查询限制为一组特定字段。让我们只获取文章 ID 和标题：

```java
Result<Record2<Integer, String>> articles = context.select(Article.ARTICLE.ID, Article.ARTICLE.TITLE)
  .from(Author.AUTHOR)
  .fetch();
```

我们还可以使用fetchOne方法选择单个对象。这个参数是表引用和匹配正确记录的条件。

在我们的例子中，让我们选择一个 ID 等于 1的作者：

```java
AuthorRecord author = context.fetchOne(Author.AUTHOR, Author.AUTHOR.ID.eq(1))
```

如果没有记录符合条件，则fetchOne方法将返回null。

### 6.3. 更新中

要更新给定的记录，我们可以使用DSLContext对象中的更新方法，并为我们需要更改的每个字段调用set方法。此语句后应跟一个具有适当匹配条件的where子句：

```java
context.update(Author.AUTHOR)
  .set(Author.AUTHOR.FIRST_NAME, "David")
  .set(Author.AUTHOR.LAST_NAME, "Brown")
  .where(Author.AUTHOR.ID.eq(1))
  .execute();
```

更新查询只会在我们调用execute方法后运行。作为返回值，我们将得到一个等于已更新记录数的整数。

也可以通过执行其存储方法来更新已获取的记录：

```java
ArticleRecord article = context.fetchOne(Article.ARTICLE, Article.ARTICLE.ID.eq(1));
article.setTitle("A New Article Title");
article.store();
```

如果操作成功，store方法将返回1 ，如果不需要更新，则返回0 。例如，没有任何内容符合条件。

### 6.4. 删除

要删除给定的记录，我们可以使用DSLContext对象的delete方法。删除条件应作为以下where子句中的参数传递：

```java
context.delete(Article.ARTICLE)
  .where(Article.ARTICLE.ID.eq(1))
  .execute();
```

删除查询只会在我们调用execute方法后运行。作为返回值，我们将得到一个等于已删除记录数的整数。

也可以通过执行删除方法来删除已经获取的记录：

```java
ArticleRecord articleRecord = context.fetchOne(Article.ARTICLE, Article.ARTICLE.ID.eq(1));
articleRecord.delete();
```

如果操作成功，delete方法将返回1 ，如果不需要删除，则返回0 。例如，当条件不匹配时。

## 七. 总结

在本文中，我们学习了如何使用 jOOQ 框架配置和创建一个简单的 CRUD 应用程序。